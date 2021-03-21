package csc1009assignment;

//import csc1009assignment.thesaurus_test;
import java.util.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.*;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class TwitterCrawl implements crawlinter {
	static Connection connection = null;
	// access and consumer key cannot be altered
	static final String CONSUMER_KEY = "XrR0t3DrkuGJRrPAzDmKwWobI";
	static final String CONSUMER_SECRET = "abpFw9ee9BeV9HVEzQ0NwALasiMjSEtPwXsnYWuuVpByPgxoxw";
	static final String ACCESS_TOKEN = "266135434-mr0MVwOniZDOGY4bxdMYwFloitsfaworfOMYjPDF";
	static final String ACCESS_TOKEN_SECRET = "thNWbd2tV9reR9bGsR9UseqKd61MSIGe4B7On6fRCzhB2";

	public static ArrayList<crawlComment> Crawl(String tweets) throws TwitterException {

		SentimentAnalyzer sentimentanalyzer = new SentimentAnalyzer();
		sentimentanalyzer.initialize();

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(CONSUMER_KEY).setOAuthConsumerSecret(CONSUMER_SECRET)
				.setOAuthAccessToken(ACCESS_TOKEN).setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);

		/**
		 * Initializing the twitter API
		 */
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		/**
		 * This will generate the similar search term ie: if i search for "happy" it
		 * will return 3 other word with similar meaning
		 */
		ArrayList<crawlComment> tweetList = new ArrayList<crawlComment>();

		for (String i : thesaurus.getSimilar(tweets, 3)) {
			System.out.println("Whats the search: " + i);
			Query query = new Query(i);
			query.setLang("en");

			QueryResult result = twitter.search(query);

			for (Status status : result.getTweets()) {
				crawlComment curr = new crawlComment();
				String TweetStatus;
				String url = "https://twitter.com/" + status.getUser().getScreenName() + "/status/" + status.getId();

				// Date cur = status.getCreatedAt();

				java.util.Date cur = status.getCreatedAt();
				SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
				String stringDate = dateFormat.format(cur);

				long id = status.getInReplyToStatusId();
				curr.setText(status.getText());
				curr.setUser(status.getUser().getScreenName());
				// curr.setDate(status.getCreatedAt());
				SentimentResult sentimentresult = SentimentAnalyzer.getSentimentResult(status.getText());
				curr.setSr(sentimentresult);
				curr.setDate(stringDate);
				curr.setId(Long.toString(status.getId()));
				curr.setSearch(i);
				curr.setSource("Twitter");
				curr.setTitle(i);

//				System.out.println("========= START TWEET STATUS =========");
//				System.out.println("Tweet URL:" + url);
//				System.out.println("Tweet Username: " + curr.getUser());
//				System.out.println("Tweet Context: " + curr.getText());
//				System.out.println("Tweet Date: " + stringDate);
//				System.out.println("Tweet Sentimental Result: " + curr.getSr());
//				System.out.println("Id: " + id);
//				System.out.println("========= END TWEET STATUS =========");
				// Sqlite Database codes

				if (id == -1) { // Set Title as Original Tweet Else set as Replied Tweets
					String regex = "^RT @.+:";
					Pattern pattern = Pattern.compile(regex);
					Matcher matcher = pattern.matcher(curr.getText());

					TweetStatus = "Original Tweet";

					while (matcher.find()) {
						TweetStatus = "Retweet";
					}

				}

				else {
					TweetStatus = "Replied Tweet";
				}
				curr.setTitle(i);
				 thesaurus.insertcrawl(curr);
				tweetList.add(curr);

			}

		}
		return tweetList;
	}

	@Override
	public void crawler(String searchterm) {// twit
		try {
			ArrayList<crawlComment> curr = Crawl(searchterm);
			
		} catch (TwitterException err) {
			err.printStackTrace();
		}
	}

}
