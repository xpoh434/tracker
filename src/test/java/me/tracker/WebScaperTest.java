package me.tracker;

import java.net.URL;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.StringUtils;

public class WebScaperTest {

	WebScaper webScaper;
	Pattern pattern1 = Pattern.compile("<span class=\"time_rtq_ticker\"><span>([0-9.]+)</span></span>");
	Pattern pattern2 = Pattern.compile("<span class=\"time_rtq\"> <span><span>(.[^<]+)</span></span></span>"); 
	Pattern pattern3 = Pattern.compile("買入價\\:</th><td class=\"yfnc_tabledata1\">([^<]+)</td>");
	Pattern pattern4 = Pattern.compile("賣出價\\:</th><td class=\"yfnc_tabledata1\">([^<]+)</td>");
	Pattern pattern5 = Pattern.compile("成交量\\:</th><td class=\"yfnc_tabledata1\"><span>([0-9,\\.]+)</span>");
	
	@Before
	public void setUp() throws Exception {
		webScaper = new WebScaper();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGet() throws Exception {
		String[][] s = webScaper.get(new URL("http://hk.finance.yahoo.com/q?s=0023.HK"), new Pattern[]{ pattern1, pattern2,pattern3, pattern4, pattern5});
		for(String[] t :s)
			System.out.println(StringUtils.arrayToCommaDelimitedString(t));
	}

}
