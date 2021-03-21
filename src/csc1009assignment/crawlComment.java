package csc1009assignment;
interface crawlinter{
	void crawler(String searchterm);
}
/**
 * @author chuny
 *
 */
public class crawlComment {
	String user;
	String text;
	SentimentResult sr;
	String date;
	String source;
	String id;
	String title;
	String search;

	/**
	 * gets the source of this comment
	 * 
	 * @return source source of the comment
	 */
	public String getSource() {
		return source;
	}

	/**
	 * sets the source of this comment
	 * 
	 * @param source source of the comment
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * gets the id of this comment
	 * 
	 * @return id id of the comment
	 */
	public String getId() {
		return id;
	}

	/**
	 * sets the id of this comment
	 * 
	 * @param id id of the comment
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * gets the title of this comment
	 * 
	 * @return title title of the comment
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * sets the title of this comment
	 * 
	 * @param title title of the comment
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * gets the search of this comment
	 * 
	 * @return search search of the comment
	 */
	public String getSearch() {
		return search;
	}

	/**
	 * sets the search of this comment
	 * 
	 * @param search search of the comment
	 */
	public void setSearch(String search) {
		this.search = search;
	}

	/**
	 * gets the user of this comment
	 * 
	 * @return user user of the comment
	 */
	public String getUser() {
		return user;
	}

	/**
	 * sets the user of this comment
	 * 
	 * @param user user of the comment
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * gets the text of this comment
	 * 
	 * @return text text of the comment
	 */
	public String getText() {
		return text;
	}

	/**
	 * sets the text of this comment
	 * 
	 * @param text text of the comment
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * gets the date of this comment
	 * 
	 * @return date date of the comment
	 */
	public String getDate() {
		return date;
	}

	/**
	 * sets the date of this comment
	 * 
	 * @param date date of the comment
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * gets the sr.sentimentType of this comment which is the readable format of the
	 * emotion of the text
	 * 
	 * @return sr sr.sentimentType of the comment
	 */
	public String getSr() {
		return sr.sentimentType;
	}

	/**
	 * sets the sentiment result of this comment
	 * 
	 * @param sr sentimentResult of the comment
	 */
	public void setSr(SentimentResult sr) {
		this.sr = sr;
	}
}
