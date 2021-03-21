package csc1009assignment;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.Search;
import com.google.api.services.youtube.model.CommentSnippet;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import com.google.api.services.youtubeAnalytics.model.GroupItem.Resource;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONObject;

public class YTVideoCrawler implements crawlinter {
	// You need to set this value for your code to compile.
	// For example: ... DEVELOPER_KEY = "YOUR ACTUAL KEY";
	private final String DEVELOPER_KEY = "AIzaSyAomqNsYSaPsN7hSkAB2lRPwtSyyMJlyVY";

	private final String APPLICATION_NAME = "YTcrawlcrawl";
	private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private ArrayList<String> temp = new ArrayList<String>();

	// public YTVideoCrawler() {
	// }
	

	public ArrayList<String> getTemp() {
		return temp;
	}

	/**
	 * Call function to create API service object. Define and execute API request.
	 * Print API response.
	 *
	 * @throws GeneralSecurityException, IOException, GoogleJsonResponseException
	 */
	private void myYTvidCrawl(String a)
			throws GeneralSecurityException, IOException, GoogleJsonResponseException {

		temp = new ArrayList<String>();
		YouTube youtubeService = getService();
		// Define and execute the API request
		YouTube.Search.List request = youtubeService.search().list("snippet");
		SearchListResponse response = request.setKey(DEVELOPER_KEY).setMaxResults(3L).setQ(a)
				.setFields("items(id/videoId)").execute();
		System.out.println(response);
		List<SearchResult> searchresults = response.getItems();
		ResourceId snippet;
		if (searchresults.isEmpty()) {
			System.out.println("Can't get Video ID.");
		} else {
			for (SearchResult searchresult : searchresults) {

				snippet = searchresult.getId();
				if (snippet.getVideoId() != null) {
					temp.add(snippet.getVideoId());
				}
			}
		}

	}

	/**
	 * Build and return an authorized API client service.
	 *
	 * @return an authorized API client service
	 * @throws GeneralSecurityException, IOException
	 */
	private YouTube getService() throws GeneralSecurityException, IOException {
		final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		return new YouTube.Builder(httpTransport, JSON_FACTORY, null).setApplicationName(APPLICATION_NAME).build();
	}

	@Override
	public void crawler(String searchterm) {
		try {
			myYTvidCrawl(searchterm);
			ArrayList<crawlComment> cc =YTCommentCrawler.myYouTubeCrawl(temp, searchterm);
		for(crawlComment c: cc) {
			thesaurus.insertcrawl(c);
		}
		} catch (Exception yterr) {
			yterr.printStackTrace();
		}
	}
}