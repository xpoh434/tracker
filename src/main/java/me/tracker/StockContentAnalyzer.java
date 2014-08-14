package me.tracker;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import me.tracker.boot.Log;
import me.tracker.db.jpa.entities.Price;

import org.slf4j.Logger;

public class StockContentAnalyzer {
	
	@Log
	Logger logger;
	
	public Price analyze(String[][] tokens) {
		Price price = new Price();
		
		BigDecimal bid=null,ask =null, vol=null, mid=null;		
		if(tokens[0]!=null)
			mid = toBigDecimal(tokens[0][1]);
		if(tokens[2]!=null && !"不適用".equals(tokens[2][1]))
			bid = toBigDecimal(tokens[2][1]);
		if(tokens[3]!=null && !"不適用".equals(tokens[3][1]))
			ask = toBigDecimal(tokens[3][1]);
		if(tokens[4]!=null)
			vol = toBigDecimal(tokens[4][1]);
		
		String dateString = null;
		if(tokens[1]!=null) {
			String[] dateTokens = tokens[1][1].split(",");
			if(dateTokens.length == 3) {
				dateString = Calendar.getInstance().get(Calendar.YEAR) + " " + dateTokens[0].trim() + dateTokens[2].trim() + " +0800";
			}
		}
		
		Date date;
		try {
			date = new SimpleDateFormat("yyyy MM月dd日HH:mm Z").parse(dateString);
		} catch (Exception e) {
			logger.warn("unparseable date, fallback to current time: " + dateString, e);
			date = new Date();
		}
		
		price.setBid(bid);
		price.setAsk(ask);
		price.setVol(vol);
		price.setTime(date);
		price.setMid(mid);
		return price;
	}
	
	private BigDecimal toBigDecimal(String s) {
		try {
			return new BigDecimal(NumberFormat.getInstance().parse(s).toString());
		}
		catch (NumberFormatException | ParseException e) {
			logger.warn("cannot convert to number: " + s, e);
			return null;
		}
	}
	
}
