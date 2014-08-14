package me.tracker;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.tracker.boot.Log;

import org.slf4j.Logger;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;

public class WebScaper {
	
	@Log
	private Logger logger;
	
	public String[][] get(URL url, Pattern[] regEx) {
		try {
			URLConnection conn = url.openConnection();
			
			final InputStream is = conn.getInputStream();
			
			String content = CharStreams.toString(CharStreams.newReaderSupplier(new InputSupplier<InputStream>(){
				@Override
				public InputStream getInput() throws IOException { return is; }
			}, Charsets.UTF_8));
			
			String[][] groups = new String[regEx.length][];
			int start = 0;
			for(int j = 0; j< regEx.length; j++) {
				Matcher matcher = regEx[j].matcher(content);
				if(matcher.find(start)) {
					groups[j] = new String[matcher.groupCount()+1];
					for(int i=0;i<=matcher.groupCount();i++) {
						groups[j][i] = matcher.group(i);
					}
					start = matcher.end();
				}
			}
			return groups;
		} catch (IOException e) {
			logger.error("error occurred", e);
			return null;
		}
	}
	
	
}
