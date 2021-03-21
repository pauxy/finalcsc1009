package csc1009assignment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chuny
 *
 */
public class redditComment extends crawlComment {
	private List<redditComment> children = new ArrayList<redditComment>();
	private String link;
	private String subreddit;

	/**
	 * returns title of a comment
	 * 
	 * @return title title of comment
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * sets title of a comment
	 * 
	 * @param title title of comment
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * returns subreddit for the current comment
	 * 
	 * @return subreddit subreddit of current comment
	 */
	public String getSubreddit() {
		return subreddit;
	}

	/**
	 * sets the subreddit of current
	 * 
	 * @param subreddit subreddit for current comment
	 */
	public void setSubreddit(String subreddit) {
		this.subreddit = subreddit;
	}

	/**
	 * returns any links in the current comment
	 * 
	 * @return link link in current comment
	 */
	public String getLink() {
		return link;
	}

	/**
	 * sets link for current comment
	 * 
	 * @param link link in current comment
	 */
	public void setLink(String link) {
		this.link = link;
	}

	/**
	 * returns sentiment type of current comment Very positive,positive,
	 * neutral,negative,verynegative.
	 * 
	 * @return sr.sentimentType the sentiment type
	 */
	public String getSr() {
		return sr.getSentimentType();
	}

	/**
	 * sets sr given current text in comment by referencing sentiment analyzer.
	 * 
	 * @param curr the text in comment
	 */
	public void setSr(String curr) {
		while (true) {

			try {
				SentimentResult a = SentimentAnalyzer.getSentimentResult(curr);
				sr = a;
				return;
			} catch (Exception e) {
				SentimentAnalyzer.initialize();
			}
		}
	}

	/**
	 * gets id of current comment
	 *
	 * @return id the current id
	 */
	public String getId() {
		return id;
	}

	/**
	 * sets id of current comment
	 *
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * gets the children comments in a comment
	 * 
	 * @return children the children comments
	 */
	public List<redditComment> getChildren() {
		return children;
	}

	/**
	 * sets the children comments in a comment
	 * 
	 * @param children the children comments
	 */
	public void setChildren(List<redditComment> children) {
		this.children = children;
	}

	/**
	 * sets text of current comment and sets the sentiment result as well
	 *
	 * @param text the text part of a comment.
	 */
	public void setText(String text) {
		super.text = text;
		setSr(text);
	}

}
