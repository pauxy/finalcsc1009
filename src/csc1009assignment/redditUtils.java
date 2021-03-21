package csc1009assignment;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import com.monkeylearn.MonkeyLearnException;

import java.io.IOException;
import java.net.URI;

/**
 * @author chuny
 *
 */
public class redditUtils implements crawlinter {
	static Connection connection = null;

	/**
	 * This class is the “main'' class to return all the reddit posts and storing
	 * all the comments for each post appropriately. This function allows users to
	 * search for a term in a subreddit or on the whole of Reddit. The user can then
	 * choose the way to sort the data, be it, Relevance, hot, top or new. It can
	 * then instruct the method on how much posts to limit it at. We have also
	 * implemented a thesaurus function that will return similar words. The method
	 * will then loop through the thesaurus list then concatenate a working URL with
	 * the given search term and get the JSON file. This is done by using the
	 * function getSearch to get a JSON object, It will then start a new thread for
	 * each redditpost to get their respective posts and parse their comments
	 * recursively using getComments
	 * 
	 * @param search    Term to search in reddit
	 * @param subreddit subreddit to search, if "", implies search entire reddit
	 * @param sort      type to sort by,eg hot latest etc.
	 * @param limit     limit for amt of posts to return
	 * @return a list of redditpost objects after searching reddit.
	 */
	public static List<redditPost> searchReddit(String search, String subreddit, String sort, int limit) {
		ArrayList<String> example = thesaurus.getSimilar(search, 3); // limit to 3 thesaurus values.
		if (!subreddit.equals("")) { // check if subreddit is needed, append as needed
			subreddit = "r/" + subreddit + "/";
		}
		ArrayList<redditPost> allrp = new ArrayList<>();
		for (String sea : example) { // loop for each word returned by thesaurus.
			String url = "https://www.reddit.com/" + subreddit + "search.json?q=" + sea + "&sort=" + sort + "&limit="
					+ limit; // to concatenate the site to get the respective search.
			if (subreddit != "") { //only applicable for when searchinf in a subreddit, makes sure it only searches in the subreddit itself.
				url = url + "&restrict_sr=on";
			}
			System.out.println(url);
			Object jo = getSearch(url);// get the JSON object for the url, this is dont by curling the site and then processing it(done in getSearch() method.
			List<redditPost> rp = new ArrayList<redditPost>();
			rp = parseSearch((JSONObject) jo, (String) sea);// parses returned JSONOBject into an arraylist of redditPost.
			for (redditPost i : rp) {//for each redditpost, start a new thread to get the comments in each post.
				new Thread(i).start();
			}
			allrp.addAll(rp);//append all redditPost from each thesaurus values into one main arraylist to return to caller.
		}
		return allrp;

	}

	/**
	 * This class will get in a string search that holds the URL of the JSON site to
	 * curl. The class will then get the site and parse it to either a JSON object
	 * or a JSON array accordingly. Returning it as an Object for the caller to
	 * handle.
	 * 
	 * @param search - search term to search reddit in a concatenated URL.
	 * @return either a JSONObject or a JSONArray, depending on what is returned by
	 *         the reddit site.
	 */
	public static Object getSearch(String search) {
		Object jo = null;// to handle when the resulting JSON returns either a Array or an Object
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(search)).header("User-Agent",
				"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
				.build();//initialise and make a request
		HttpResponse<String> response = null;//response, initialised 
		try {
			response = client.send(request, HttpResponse.BodyHandlers.ofString());//get response when you send out request.
		} catch (IOException e) {//catch any errors in IO
			e.printStackTrace();
		} catch (InterruptedException e) {//if connection is interrupted
			e.printStackTrace();
		}
		String results = response.body();//data on the website
		if (results.startsWith("302 Found")) {//checks if it is an error, 302Found implies wrong subreddit entered.
			System.out.println("in");
			return 1;
		}
		try {
			char first = results.charAt(0);//check if firstcharacter is { or [ if it is { it means it is a JSONObject else, an array.
			if (first == '{') {
				jo = new JSONObject(results);
			} else if (first == '[') {
				jo = new JSONArray(results);
			}
		} catch (Exception err) {
			System.out.println(err);//if it is faces an error when parsing to a JSONObject or JSONArray.
		}

		return jo;
	}

	/**
	 * This class will get the array in the JSON object and loop through the array,
	 * storing each one as a redditPost, appending all of the results into an
	 * ArrayList and returning the ArrayList of posts back to the caller
	 * 
	 * @param jo  a JSONObject containing one reddit post
	 * @param sea searchterm used to search reddit to obtain this reddit post.
	 * @return
	 */
	public static List<redditPost> parseSearch(JSONObject jo, String sea) {
		JSONArray ja = jo.getJSONObject("data").getJSONArray("children");//for each JSONObject in JSONArray it contains a redditPost.
		List<redditPost> rp = new ArrayList<redditPost>();//append all the redditposts to this arraylist for returning in future.
		for (int i = 0; i < ja.length(); i++) {//loop through and then store them each as a redditpost
			redditPost ph = new redditPost();
			jo = ja.getJSONObject(i).getJSONObject("data");
			ph.setSubReddit(jo.getString("subreddit_name_prefixed"));
			ph.setThumbNail(jo.getString("thumbnail"));
			ph.setExternalLink(jo.getString("url"));
			ph.setTitle(jo.getString("title"));
			ph.setDate(jo.getInt("created_utc"));
			ph.setPostText(jo.getString("selftext"));
			ph.setSearchterm(sea.replace("%20", " "));
			ph.setId(jo.getString("id"));
			if (ph.getPostText().trim().isEmpty()) {
				ph.setPostText("N.A");
			}
			rp.add(ph);
		}
		return rp;

	}

	/**
	 * Given a redditpost object, it will then proceed to go to the site holding the
	 * post and then retrieve and store all the comments using the method
	 * recurseComments to recursively get the sub comments for each comment and
	 * storing them in an ArrayList of comments for each comment.
	 * 
	 * @param rp redditpost to retrieve the comments from.
	 * @return
	 */
	public static List<redditComment> getComments(redditPost rp) {

		List<redditComment> rc = new ArrayList<redditComment>();
		String url = "https://www.reddit.com/" + rp.getSubReddit() + "/comments/" + rp.getId() + ".json";//concatenates and generates the URL that the post is residing at.
		System.out.println(url);
		JSONArray ja = null;//all comments stored in a JSONArray obj, this is here as a placeholder and initialiser.
		try {
			ja = (JSONArray) getSearch(url);//uses getSearch to get the JSON object in the url, which is in this case, supposed to be a JSONArray.
		} catch (Exception e) {// in event that it cant be parsed, handle error.
			e.printStackTrace();
		}
		JSONArray comments = ja.getJSONObject(1).getJSONObject("data").getJSONArray("children");//gets amt of "parent" comments aka the amount of comments which are not replies or subcomments to anyother comments
		/*
		 * For unlimited comments for (int i = 0; i < comments.length(); i++) {
		 */
		// Replace this line if you plan to limit number of comments
		int lengths = 3;//for limiting search :/ database cant handle that much search :(
		if (comments.length() < 3) {//makes sure if the amount of parent comments if lesser than 3,is stored, to ensure no getting of non existant values
			lengths = comments.length();
		}
		for (int i = 0; i < lengths; i++) {//loops through parent comments and pases the JSONObject of each parent comment to recursecomment for it to recursively build the comment tree.
			JSONObject jo = comments.getJSONObject(i).getJSONObject("data");
			List<redditComment> ph = recurseComment(jo, rp);
			try {
				rc.add(setComment(jo, ph, rp));// adds subcomments to the comment object.
			} catch (Exception e) {
			}
		}
		return rc;
	}

	/**
	 * Given a JSONObject it will get the relevant data and put it into a new
	 * redditcomment object and stores the array list of redditcomments, which are
	 * the replies to the current comments, as the comment to the object.
	 * 
	 * @param jo JSONObject containing comment
	 * @param rc list of children comment under current comment
	 * @param rp redditpost the comments are from
	 * @return
	 */
	public static redditComment setComment(JSONObject jo, List<redditComment> rc, redditPost rp) {//sets a comment object and inserts into database
		String datea = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss")//formats dates to a format for sql
				.format(new java.util.Date(jo.getLong("created_utc") * 1000));
		redditComment curr = new redditComment();//initalises and creates a new redditComment
		curr.setSearch(rp.getSearchterm());
		curr.setText(jo.getString("body"));
		curr.setId(jo.getString("id"));
		curr.setUser(jo.getString("author_fullname").substring(3));
		curr.setDate(datea);
		curr.setLink(jo.getString("link_id").substring(3));
		curr.setSubreddit(jo.getString("subreddit_name_prefixed"));
		curr.setUser(jo.getString("author"));
		curr.setTitle("comment");
		curr.setChildren(rc);// sets subcomments.
		curr.setSource("Reddit");
		thesaurus.insertcrawl(curr);
		return curr;
	}

	/**
	 * Recursively parses and sets the comments with the help of setComment. Reddit
	 * comments are stored such that a comment can have several comments under it,
	 * creating a tree structure of sorts. The JSON data is also represented in this
	 * tree structure and it has variable depth depending on the number of replies a
	 * comment may have. A sub comment may also branch out to have more unrelated
	 * sub comments, making the collection of comments especially tedious especially
	 * in the case of parsing and cleaning the data.
	 * 
	 * @param jo JSONObject containing all the comments that need to be recursively
	 *           parsed
	 * @param rp redditpost the comments are from
	 * @return list of comments that are subcomments to the parent classes.
	 */
	public static List<redditComment> recurseComment(JSONObject jo, redditPost rp) {
		List<redditComment> children = new ArrayList<redditComment>();// holds the children of the comment prev
		List<redditComment> prev = new ArrayList<redditComment>();//holds the comment deeper in
		try {
			JSONArray phja = jo.getJSONObject("replies").getJSONObject("data").getJSONArray("children");//gets all the replies
			for (int j = 0; j < phja.length(); j++) {
				jo = phja.getJSONObject(j).getJSONObject("data");//get the comment text of the subcomment
				prev = recurseComment(jo, rp);// get the subcomments of the subcomment
				children.add(setComment(jo, prev, rp));//stores the subcomments to their parent comment.
			}
		} catch (JSONException e) {//recurse until error occurs. this is to accomodate for bigger datasets from reddit :) hopefully.
		}
		return children;
	}

	/**
	 * From the crawliner interface, this method will search for a term in
	 * respective crawlers then store them all into the database.
	 *
	 * @param searchterm search term to search reddit with
	 */
	@Override
	public void crawler(String searchterm) {
		try {
			List<redditPost> n = searchReddit(searchterm, "", "hot", 1);//calls the searchReddit method to return a list of redditPosts.
		} catch (Exception rerr) {// in case of error, print error.
			rerr.printStackTrace();
		}
	}

}