package csc1009assignment;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.CommentSnippet;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zenttan
 *
 */
public class YTCommentCrawler {
	Connection connection = null;
	private static final String DEVELOPER_KEY = "AIzaSyAomqNsYSaPsN7hSkAB2lRPwtSyyMJlyVY";
	private static final String APPLICATION_NAME = "YTCrawlCrawl";
	private static ArrayList<String> showdown = new ArrayList<String>();

	/**
	 * This crawler serves the purpose to crawl YouTube Video code, so to pass
	 * individual video code into myYoutubeCrawl() later part. This part allows
	 * users to search each videos and concatenate all the comments into an
	 * arraylist.
	 * 
	 * @param tempcc     contains all the video codes for the search term
	 * @param searchterm the searchterm associated with the arraylist of codes.
	 * @return allcomm all the comment objects for the list of video codes.
	 * @throws GeneralSecurityException
	 * @throws IOException
	 * @throws GoogleJsonResponseException
	 */
	public static ArrayList<crawlComment> myYouTubeCrawl(ArrayList<String> tempcc, String searchterm)
			throws GeneralSecurityException, IOException, GoogleJsonResponseException {
		YouTube youtubeService = getService();// get youtube object
		// Define and execute the API request
		ArrayList<crawlComment> allcomm = new ArrayList<crawlComment>();
		SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer();
		sentimentAnalyzer.initialize();
		// Initalize YoutubeObject
		for (String i : tempcc) {
			try {
				YouTube.CommentThreads.List request = youtubeService.commentThreads().list("id,snippet");
				CommentThreadListResponse response = request.setKey(DEVELOPER_KEY).setMaxResults(2L)// <--- limit set to
																									// 100
						.setVideoId(i)// this is the video code <----
						.execute();
				List<CommentThread> channelComments = response.getItems();// response of search
				CommentSnippet snippet;

				if (channelComments.isEmpty()) {// in case no comments
					System.out.println("Can't get channel comments.");
				} else {

					for (CommentThread channelComment : channelComments) {// loop through each comment for current video

						crawlComment ytoo = new crawlComment();// new comment
						snippet = channelComment.getSnippet().getTopLevelComment().getSnippet();

						long date = snippet.getPublishedAt().getValue();
						SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
						String stringDate = dateFormat.format(date);

						SentimentResult sentimentResult = sentimentAnalyzer
								.getSentimentResult(snippet.getTextDisplay());
						ytoo.setDate(stringDate);
						ytoo.setId(i);
						ytoo.setSearch(searchterm);
						ytoo.setSource("Youtube");
						ytoo.setSr(sentimentResult);
						ytoo.setText(snippet.getTextDisplay());
						ytoo.setTitle("Video Comments");
						ytoo.setUser(snippet.getAuthorDisplayName());
						allcomm.add(ytoo);

					}
// 	
				}
			} catch (Exception err) {

			}

		}
		return allcomm;
	}

	/**
	 * gets the Youtube Object for the program to use when searching the platform
	 * 
	 * @return youtube youtube object to search youtube.
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	private static YouTube getService() throws GeneralSecurityException, IOException {

		List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube.force-ssl");
		Credential credential = Auth.authorize(scopes, "commentthreads");

		return new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME).build();

	}

}