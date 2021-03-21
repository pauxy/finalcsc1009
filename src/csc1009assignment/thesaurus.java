package csc1009assignment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author chuny
 *
 */
public class thesaurus {
	private static String url = "https://api.datamuse.com/words?rel_syn=";

	/**
	 * Users can now get related words to the original words they were searching
	 * through an API's usage, which will return words with similar meaning. We have
	 * implemented a method that will call the API and get back an ArrayList of
	 * strings of similar words and return to the caller. The returned ArrayList
	 * from the thesaurus will then be looped through in each web crawler class.
	 * Users who may be interested in the results of words with similar meaning will
	 * potentially find this helpful.
	 * 
	 * @param original original word to get all the similar words from
	 * @param limit    limit of number of words to get in thesaurus.
	 * @return similar arraylist of the similar words from thesaurus
	 */
	public static ArrayList<String> getSimilar(String original, int limit) {
		ArrayList<String> similar = new ArrayList<String>();
		JSONArray ja = (JSONArray) redditUtils.getSearch(url + original);// returns JSONArray of search from thesaurus
																			// api

		if (limit > ja.length()) {// ensure no null value when looping
			limit = ja.length();
		}
		limit = limit - 1;
		similar.add(original);// adds original no matter what. this is to make sure at least what they
								// searched for will still be searched
		for (int i = 0; i < limit; i++) {// limits the amt of strings
			JSONObject jo = ja.getJSONObject(i);// gets each word and stores it, replacing spaces with html encoding.
			similar.add(jo.getString("word").replace(" ", "%20"));
		}

		return similar;
	}

	static Connection connection = sqliteConnector.dbConnector();

	/**
	 * Inserts a single crawlComment into the database using prepared statements.
	 * 
	 * @param curr curr is the current crawlComment to enter
	 */
	public static void insertcrawl(crawlComment curr) {
		try {

			String querys = "insert into CrawlerDB (Username,CrawlerType,PostID,Title,Comments,Date,QueryTerm,SentimentalResult) values (?,?,?,?,?,?,?,?)";//prepared statement
			PreparedStatement pst = connection.prepareStatement(querys);//setting prepared statement vv
			pst.setString(1, curr.getUser());
			pst.setString(2, curr.getSource());
			pst.setString(3, curr.getId());
			pst.setString(4, curr.getTitle());
			pst.setString(5, curr.getText());
			pst.setString(6, curr.getDate());
			pst.setString(7, curr.getSearch());
			pst.setString(8, curr.getSr());
			pst.execute();

			pst.close();
		}

		catch (Exception err) {
			err.printStackTrace();
		}
	}

}
