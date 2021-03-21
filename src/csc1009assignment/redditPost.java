package csc1009assignment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chuny
 *
 */
public class redditPost implements Runnable {
	String thumbNail = "error";
	String title = "error";
	String id;
	String subReddit = "error";
	String externalLink = "error";
	SentimentResult sr;
	String searchterm;
	String postText;
	List<redditComment> comments = new ArrayList<redditComment>();
	String date="";

	/**
	 * gets the comments of this comment
	 * 
	 * @return comments subcomments of the comment
	 */
	public List<redditComment> getComments() {
		return comments;
	}

	/**
	 * sets the comments of this comment
	 * 
	 * @param comments subcomments of the comment
	 */
	public void setComments(List<redditComment> comments) {
		this.comments = comments;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public SentimentResult getSr() {
		return sr;
	}

	public void setSr(SentimentResult sr) {
		this.sr = sr;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * gets the text of this comment
	 * 
	 * @return postText text of the comment
	 */
	public String getPostText() {
		return postText;
	}

	/**
	 * sets the text of this comment
	 * 
	 * @param postText textof the comment
	 */
	public void setPostText(String postText) {
		this.postText = postText;
	}

	/**
	 * gets the searchterm of this comment
	 * 
	 * @return searchterm searchterm of the comment
	 */
	public String getSearchterm() {
		return searchterm;
	}

	/**
	 * sets the searchtermof this comment
	 * 
	 * @param searchterm searchterm of the comment
	 */
	public void setSearchterm(String searchterm) {
		this.searchterm = searchterm;
	}

	/**
	 * gets the thumbnail of this comment
	 * 
	 * @return thumbnail thumbnail of the comment
	 */
	public String getThumbNail() {
		return thumbNail;
	}

	/**
	 * sets the thumbnail of this comment
	 * 
	 * @param thumbnail thumbnail of the comment
	 */
	public void setThumbNail(String thumbNail) {
		this.thumbNail = thumbNail;
	}

	/**
	 * sets the date of this comment
	 * 
	 * @param date date of the comment
	 */
	public void setDate(int date) {
		this.date =Integer.toString(date);
	}

	/**
	 * gets the date of this comment
	 * 
	 * @return date date of the comment
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * sets the tite of this comment
	 * 
	 * @param title title of the comment
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * gets the subreddit of this comment
	 * 
	 * @return subreddit subreddit of the comment
	 */
	public String getSubReddit() {
		return subReddit;
	}

	/**
	 * sets the subreddit of this comment
	 * 
	 * @param subreddit subreddit of the comment
	 */
	public void setSubReddit(String subReddit) {
		this.subReddit = subReddit;
	}

	/**
	 * gets the link of this comment
	 * 
	 * @return externalLink link of the comment
	 */
	public String getExternalLink() {
		return externalLink;
	}

	/**
	 * sets the link of this comment
	 * 
	 * @param externalLink link of the comment
	 */
	public void setExternalLink(String externalLink) {
		this.externalLink = externalLink;
	}

	/**
	 * used for threading comments, this is what each thread will run upon run,
	 * getting all the comments on their own post.
	 *
	 */
	@Override
	public void run() {
		if (this.subReddit.contains("r/")) {// makes sure this is from a subreddit, ensuring it is apost and not a user
											// data.
			List<redditComment> rc = redditUtils.getComments(this);// gets comments of current post.
			this.comments = rc;
		}

	}

}
