package me.tracker;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import javax.inject.Inject;

import me.tracker.boot.Log;
import me.tracker.db.jpa.entities.Price;
import me.tracker.repositories.PriceRepository;
import me.tracker.web.PriceMessage;

import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

public class PriceUpdateJob implements Job {
	
	@Log
	private Logger logger;
	
	@Inject
	private WebScaper webScaper;
	
	@Inject
	private StockContentAnalyzer analyzer;
	
	@Inject
	private PriceRepository priceRepo;
	
	@Inject
	private PriceBroadcaster priceBroadcaster;
	
	public PriceUpdateJob() {
	}
	
	final static Pattern pattern1 = Pattern.compile("<span class=\"time_rtq_ticker\"><span>([0-9.]+)</span></span>");
	final static Pattern pattern2 = Pattern.compile("<span class=\"time_rtq\">\\s*<span><span>(.[^<]+)</span></span></span>");
	final static Pattern pattern3 = Pattern.compile("買入價\\:</th><td class=\"yfnc_tabledata1\">([^<]+)</td>");
	final static Pattern pattern4 = Pattern.compile("賣出價\\:</th><td class=\"yfnc_tabledata1\">([^<]+)</td>");
	final static Pattern pattern5 = Pattern.compile("成交量\\:</th><td class=\"yfnc_tabledata1\"><span>([0-9,\\.]+)</span>");
	final static String baseURL = "http://hk.finance.yahoo.com/q?s=" ;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("Running update job for: " + context.getJobDetail().getKey());
        
        
        String stockCode = (String) context.getJobDetail().getJobDataMap().get("stockCode");
        
        Price price = null;
        if(stockCode.startsWith("TEST")) {
			List<Price> prices = priceRepo.findlatestPriceBySymbol(stockCode);
			Price curPrice = prices.isEmpty() ? null : prices.get(0);
			price = new Price();
			
			double bid;
			double mu = 0.01;
			double sigma = 0.01;
			if(curPrice == null) {
				bid = new Random().nextDouble()*100;
			} else {
				bid = curPrice.getBid().doubleValue() + curPrice.getBid().doubleValue()*(mu* + new Random().nextGaussian()*sigma); 
			}
			double spread = Math.exp(new Random().nextGaussian())/100;
			double mid = bid + spread/2;
			double vol;
			if(curPrice == null) {
				vol = 0;
			} else {
				vol = curPrice.getVol().intValue() + Math.round(new Random().nextDouble()*100);
			}
			
        	price.setMid(BigDecimal.valueOf(mid).setScale(4, BigDecimal.ROUND_HALF_UP));
        	price.setBid(BigDecimal.valueOf(bid).setScale(4, BigDecimal.ROUND_HALF_UP));
        	price.setAsk(BigDecimal.valueOf(bid+spread).setScale(4, BigDecimal.ROUND_HALF_UP));
        	price.setSymbol(stockCode);
        	price.setTime(new Date());
        	price.setVol(BigDecimal.valueOf(vol));
        } else {
	        URL url;
			try {
				url = new URL(baseURL + stockCode);
			} catch (MalformedURLException e) {
				throw new JobExecutionException(e);
			}
	
			String[][] results = webScaper.get(url, new Pattern[]{pattern1,pattern2,pattern3,pattern4,pattern5});
	        
			price = analyzer.analyze(results);
			price.setSymbol(stockCode);
        }
		
		logger.info("Persist: " + price);
		if(((price.getAsk() != null && price.getBid()!=null) || price.getMid()!=null) && price.getVol() !=null && price.getTime()!=null) {
			priceRepo.save(price);
			PriceMessage msg = new PriceMessage();
			msg.setPrice(price);
			PriceRange range = priceRepo.findPriceVolRangeSince(stockCode, new DateTime().minusMinutes(5).toDate());
			logger.info("Price ranges:" + range);
			
			BigDecimal upMid = range.getMinMid() != null ? price.getMid().subtract(range.getMinMid()) : BigDecimal.ZERO;
			BigDecimal downMid = range.getMaxMid() != null ?  price.getMid().subtract(range.getMaxMid()) : BigDecimal.ZERO;
			BigDecimal upVol = range.getMinMid() !=null ? price.getVol().subtract(range.getMinVol()) : BigDecimal.ZERO;
//			BigDecimal downVol = price.getVol().subtract(range.getMaxVol());
			
			if(upMid.compareTo(downMid.abs()) > 0) {
				if(range.getMinMid().compareTo(BigDecimal.ZERO) != 0)
					msg.setPriceChange(upMid.divide(range.getMinMid(), 4, BigDecimal.ROUND_HALF_UP));
			}
			else {
				if(range.getMaxMid().compareTo(BigDecimal.ZERO) != 0)
					msg.setPriceChange(downMid.divide(range.getMaxMid(), 4, BigDecimal.ROUND_HALF_UP));
			}
//			if(upVol.compareTo(downVol.abs()) > 0) {
			if(range.getMinVol().compareTo(BigDecimal.ZERO) != 0)
				msg.setVolChange(upVol.divide(range.getMinVol(), 4, BigDecimal.ROUND_HALF_UP));
//			}
//			else {
//				if(range.getMaxVol().compareTo(BigDecimal.ZERO) != 0)
//					msg.setVolChange(downVol.divide(range.getMaxVol(), BigDecimal.ROUND_HALF_UP));
//			}

			publishUpdate(msg);
		} else {
			logger.warn("some fields are missing, skip saving");
		}
	}

	private void publishUpdate(PriceMessage price) {
		priceBroadcaster.broadcast(price);
	}

}
