package csc1009assignment;
import java.sql.*;
import javax.swing.JOptionPane;


/**
 * 
 * @author arico
 *
 */

public class sqliteConnector {

Connection conn=null;
	
	public static Connection dbConnector()
	{
		/**
		 * This class serves as the connection between the database and the program where the program
		 * will perform all functions that requires access to the database.
		 * 
		 * @return conn to return the connection details to the program to execute its statements
		 */
		
		try {
			Class.forName("org.sqlite.JDBC");
			//Change the connection address to your computer one -->
			Connection conn  = DriverManager.getConnection("jdbc:sqlite:/Users/TTH/Documents/GitHub/1009assignment/src/csc1009assignment/CrawlerDatabase.db");
			
			
			return conn;
		}catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, e);
			return null;
		}

	}
	
}
