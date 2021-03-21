package csc1009assignment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;


public class chartOne extends JFrame {
	//Initialize Connection from sql api
	static Connection connection = null;
	
	//Initialize String array variable with the values "Negative, "Neutral" and "Positive"
	final static String[] all = { "Negative", "Neutral", "Positive" };
	private static final long serialVersionUID = 1L;

	/* chartOne is a constructor to create a 3D stack bar chart based on the queryTerm and crawlerType that we get
	 * from DBViewer
	 * <p>
	 * Initialize Constructor chartOne with the queryTerm and crawlerType strings as parameter
	 * Retrieve the crawler type and query term by calling the get() method
	 * Create Constant variable with values "Negative", "Neutral" and "Positive"
	 * Call the DefaultCategoryDataset object to set the values of it with a for loop
	 * Call the JFreeChart api to create a 3D stacked bar chart for the purpose of displaying the data from SQLite database.
	 * Finally, name the chart and set its visibility and size
	 *
	 * @param	queryTerm				queryTerm from DBViewer
	 * @param	crawlerType				crawlerType from DBViewer
	 * @see		3D stacked bar chart	
	 */
	public chartOne(String queryTerm, String crawlerType) {
		super(crawlerType);
		int[] results = get(crawlerType, queryTerm);
		
		final String[] all = {"Negative", "Neutral", "Positive"};
		
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = 0; i<3;i++) {
			dataset.setValue(results[i], all[i], all[i]);
		}
		
		JFreeChart chart = ChartFactory.createStackedBarChart3D(crawlerType, "polarity", "count", dataset, PlotOrientation.HORIZONTAL, true, true, true);
        ChartFrame frame1 = new ChartFrame("Bar Chart", chart);
        frame1.setVisible(true);
        frame1.setSize(400, 350);
        
  }
  
	/* The purpose of get method is to return the sentimental results in an integer array for chartOne's use to display the data 
	 * <p>
	 * Initialize a method called get with a String variable crawlerType and String variable queryTerm as parameter 
	 * Calling the dbConnector method from sqliteConnector class and initialize a int array to store the return results later
	 * Use try catch statement to prepare the query for the database and fetch the sentiment result of the case-sensitive 
	 * query term and crawler type using while-loop
	 * Close the connection to SQLite and return the results
	 *
	 * @param	crawlerType		Taken from chartOne 
	 * @param	queryTerm		Taken from chartOne
	 * @return	ret				Sentimental Results in an integer array 
	 */
	public static int[] get(String crawlerType, String queryTerm) {
		connection = sqliteConnector.dbConnector();
		int[] ret = {0, 0, 0};
	
		try {
			String querys = "SELECT SentimentalResult FROM CrawlerDB WHERE lower(QueryTerm) = '" + queryTerm.toLowerCase() + "' AND CrawlerType='" + crawlerType + "'";
			
			PreparedStatement psts = connection.prepareStatement(querys);
			ResultSet result = psts.executeQuery();
			
			while(result.next()) {
				String current = result.getString("SentimentalResult");
				System.out.println (current);
				for (int i = 0; i<3; i++) {
					if(current.equals(all[i])) {
						ret[i] += 1;
					}
				}
			}
			psts.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;  
	}

	//Used to test the functionality of the chart with sample data
	public static void main(String[] args) {
       new chartOne("sad","Reddit");
    }
}
