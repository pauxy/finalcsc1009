package csc1009assignment;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

class crawlTest {
	
	static Connection connection = null;
	String search = "happy";
	String[] accepted = {"Very negative","Negative", "Neutral", "Positive", "Very positive"};

	int Search(String crawlerType, String query) throws Exception {
		connection = sqliteConnector.dbConnector();
		String querys = "SELECT SentimentalResult FROM CrawlerDB WHERE  CrawlerType='" + crawlerType + "' AND QueryTerm ='" + query + "'";
		PreparedStatement psts = connection.prepareStatement(querys);
	      ResultSet result = psts.executeQuery();
	      int a = getRowCount(result);
	      
		return a;
		
	}


	@Test
	void testTwitterInput() {
		try {
			int start = Search("Twitter",search);
			TwitterCrawl tc = new TwitterCrawl();
			tc.crawler(search);
			Thread.sleep(2000);
			int end = Search("Twitter", search);
			
			assertTrue(end>start, "end more than start" );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			assertTrue(false,"error occured");
		}
	}
	
	@Test
	void testRedditInput() {
		try {
			int start = Search("Reddit",search);
			redditUtils ru = new redditUtils();
			ru.crawler(search);
			Thread.sleep(2000);
			int end = Search("Reddit", search);
			
			assertTrue(end>start, "end more than start" );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			assertTrue(false,"error occured");
		}
	}
	
	@Test
	void testYoutubeInput() {
		try {
			int start = Search("YouTube",search);
			YTVideoCrawler y = new YTVideoCrawler();
			y.crawler(search);
			Thread.sleep(2000);
			int end = Search("YouTube", search);
			assertTrue(end>start, "end more than start" );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			assertTrue(false,"error occured");
		}
	}
	
	@Test
	void testThesaurus() {
		ArrayList<String> word = thesaurus.getSimilar(search, 3);
		assertTrue(word.size()==3,"Thesaurus gets 3 terms");
	}
	
	@Test
	void testSentimentAnalysis() {
		SentimentResult sr = new SentimentResult();
		
		sr = SentimentAnalyzer.getSentimentResult("this is the post");
		if(Arrays.asList(accepted).contains(sr.getSentimentType())) {
			assertTrue(true, "expected output for sentiment type");
		}
		else {
			assertTrue(false,"unexpected output for sentiment type");
		}
		
	}
	
	@Test
	void testThesaurusDuplicate() {
		ArrayList<String> word = thesaurus.getSimilar(search, 3);
		for(int i =0; i < word.size(); i++) {
			  if (word.lastIndexOf(word.get(i)) != i)  {
			     assertTrue(false,word.get(i)+" is duplicated");
			  }  
			}
			 assertTrue(true,"no duplicates");
			
	}
	
	private int getRowCount(ResultSet resultSet) {
	    if (resultSet == null) {
	        return 0;
	    }

	    try {
	        resultSet.last();
	        return resultSet.getRow();
	    } catch (SQLException exp) {
	        exp.printStackTrace();
	    } finally {
	        try {
	            resultSet.beforeFirst();
	        } catch (SQLException exp) {
	            exp.printStackTrace();
	        }
	    }

	    return 0;
	}
	

}
