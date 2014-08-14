package me.tracker;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.inject.Inject;

import me.tracker.boot.LoggerBeanPostProcessor;
import me.tracker.db.jpa.entities.Price;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class StockContentAnalyzerTest {
	
	@Inject
	StockContentAnalyzer analyzer;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAnalyze() throws Exception {
		String[][] results =new String[][] 
				{
					{"<span class=\"time_rtq_ticker\"><span>19.1</span></span>","19.1"},
					{"<span class=\"time_rtq\"> <span><span>03月11日, 星期一, 15:59</span></span></span>","03月11日, 星期一, 15:59"},
					{"買入價:</th><td class=\"yfnc_tabledata1\">1.21</td>","1.21"},
					{"賣出價:</th><td class=\"yfnc_tabledata1\">1.22</td>","1.22"},
					{"成交量:</th><td class=\"yfnc_tabledata1\"><span>2,447,000</span>","2,447,000"}
				};
		
		Price p = analyzer.analyze(results);
		assertEquals(new BigDecimal("1.22"), p.getAsk());
		assertEquals(new BigDecimal("1.21"), p.getBid());
		assertEquals(new BigDecimal("2447000"), p.getVol());
		assertEquals(new SimpleDateFormat("yyyyMMddHHmm").parse(Calendar.getInstance().get(Calendar.YEAR) + "03111559"), p.getTime());
		
	}

	@Configuration
	public static class Config {
		@Bean LoggerBeanPostProcessor loggerBeanPostProcessor() {
			return new LoggerBeanPostProcessor();
		}
		
	    @Bean
	    public StockContentAnalyzer stockContentAnalyzer() {
	    	return new StockContentAnalyzer();
	    }

	}
}
