package csc1009assignment;
import java.awt.EventQueue;
import java.sql.*;
import java.util.ArrayList;

import javax.swing.*;

import net.proteanit.sql.DbUtils;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.Font;
import java.awt.Color;

/**
 * 
 * @author Arico
 *
 */

// GUI for Emotionanalyst
public class DBViewer {

	/** 
	*	This class serves as the main class for the GUI. It initializes the crawler to crawl the data
	*	on behalf of the user and display the database data for user viewing.
	*/
	
	
	JFrame frmDBViewer;
	private JTextField txtSearchChart;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					DBViewer window = new DBViewer();
					window.frmDBViewer.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	Connection connection = null;
	private JTable tabDatabaseData;
	private JTextField cSearch;
	
	/**
	 * Create the application.
	 */
	public DBViewer() {
		initialize();
	}


	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		connection = sqliteConnector.dbConnector();
		frmDBViewer = new JFrame();
		frmDBViewer.getContentPane().setBackground(new Color(0, 204, 204));
		frmDBViewer.getContentPane().setForeground(new Color(0, 0, 0));
		frmDBViewer.setTitle("Database Preview");
		frmDBViewer.setBounds(100, 100, 679, 481);
		frmDBViewer.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		
		

		JScrollPane scrollDatabaseData = new JScrollPane();

		tabDatabaseData = new JTable();
		scrollDatabaseData.setViewportView(tabDatabaseData);
		
		JComboBox cbCrawlerType = new JComboBox();
		cbCrawlerType.setModel(new DefaultComboBoxModel(new String[] {"All", "Reddit", "Twitter", "Youtube"}));
		

		JLabel lblCrawlerType = new JLabel("Crawler Type");
		
		JLabel lblSentimentResult = new JLabel("Sentiment Result");
		lblSentimentResult.setHorizontalAlignment(SwingConstants.CENTER);
		
		JComboBox cbSentimentResult = new JComboBox();
		cbSentimentResult.setModel(new DefaultComboBoxModel(new String[] {"All", "Positive", "Negative", "Neutral", "N.A"}));
		


		cSearch = new JTextField();
		cSearch.setColumns(10);
		
		/**
		 * The following action is only executed when the user pressed the "Load Data" button
		 * It loads the data that was stored in the database onto a scrollpane for viewing purpose.
		 * Multiple checks were performed to see whether additional SQL terms is required to filter the results
		 * The query will then be executed after acquiring the database connection and get its results
		 */
		
		JButton btnLoadData = new JButton("Load Data");
		btnLoadData.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				
				//Calling the data onto the database and viewing it on screen
				String cSearchterm = cSearch.getText();
				
				try {

					String querys = "SELECT * FROM CrawlerDB";
					// Add SQL statement if Sentiment Result selected result is not equals to "All"
                    if(cbSentimentResult.getSelectedItem()!="All") {
                    	querys += " WHERE SentimentalResult='"+cbSentimentResult.getSelectedItem()+"'";
                    }
                    
                   // Add SQL statement if CrawlerType selected result is not equals to "All"
                    if(cbCrawlerType.getSelectedItem()!="All") {
                    	querys = addand(querys);
                    	querys += "CrawlerType='"+cbCrawlerType.getSelectedItem()+"'";
                    }
                    // Add SQL statement if QueryTerm is not equals to null or empty
                    if(!cSearchterm.equals("")) {
                    	querys = addand(querys);
                    	querys += "lower(QueryTerm) = '"+ cSearchterm.toLowerCase()+"'";
                    }
                    System.out.println(querys);
					
                    //Execute the SQL statement and display the result onto a scrollpane
					PreparedStatement psts = connection.prepareStatement(querys);
					ResultSet rss = psts.executeQuery();
					tabDatabaseData.setModel(DbUtils.resultSetToTableModel(rss));
				} catch (Exception err) {
					err.printStackTrace();
				} 
			}
		});
		
		/**
		 * The following action is only executed when the user pressed the "Crawl Data" button
		 * It will check for additional terms (e.g QueryTerm,CrawlerType and SentimentalResult if it's present) before inserting into the database
		 * Once the checking is done, it will run the program according based on the crawlerType using SwitchCase.
		 */
		
		// Web Crawler Button
		JButton btnCrawl = new JButton("Crawl");
		btnCrawl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String search = cSearch.getText();
				String type = cbCrawlerType.getSelectedItem().toString();
				boolean i = true;
				
				// Crawls the data from the respective web crawler that user selects under cbCrawlerType
				if(!search.equals("")) {
					
				switch(type) {
				  case "All":
				    i=false;
				  case "Youtube":
					  YTVideoCrawler y= new YTVideoCrawler();
					  y.crawler(search);
					  JOptionPane.showMessageDialog(null, "YouTube crawled");
					if(i) break;
				 
				  case "Twitter":
					  TwitterCrawl tc = new TwitterCrawl();
					  tc.crawler(search);
					  JOptionPane.showMessageDialog(null, "Twitter crawled");
					    if(i) break;
					    
				  case "Reddit":
					  search = search.replace(" ", "%20");
					  redditUtils ru = new redditUtils();
					  ru.crawler(search);
					  JOptionPane.showMessageDialog(null, "Reddit crawled");
					    if(i) break;
					    
				}}else {
					JOptionPane.showMessageDialog(null, "Invalid, Enter something");
				}
				JOptionPane.showMessageDialog(null, "All media crawled successfully");
				
				
			}
		});
		
		/**
		 * The following action is only executed when the user pressed the "Display Charts" button
		 * It requires the cSearch (Searchterm) and the cbCrawlerType (cbCrawlerType) values in order to search and display the data out.
		 * If the crawlerType is selected at a specific type, it will only chart data from that specific type.
		 * Else it will attempt to chart data from all 3 crawlers using the Searchterm
		 */
		
	    //Charts Display Button
		JButton btnDisplayChart = new JButton("Display Charts");
		btnDisplayChart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {

					String cSearchterm = cSearch.getText();
					
					// If cbCrawlerType selects "All", it displays all 3 web crawler results of the searchterm and chart it on a bar graphs
					if (cbCrawlerType.getSelectedItem()=="All")
					{
						new chartThree(cSearchterm);
					}
					
					// Else, it will display only 1 crawlerType that user selected under cbCrawlerType and charts the data on a bar graph
					else 
					{
						new chartOne(cSearchterm,cbCrawlerType.getSelectedItem().toString());
					}
					
					//chartLoad.setVisible(true);
				}catch (Exception err){
					err.printStackTrace();
				}
				
			}
		});
		
		
		JLabel lblNewLabel = new JLabel("Search:");
		
		JLabel lblTitle = new JLabel("Emotionalyst");
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setForeground(new Color(0, 0, 0));
		lblTitle.setFont(new Font(".AppleSystemUIFont", Font.BOLD, 30));
		
		/**
		 * The following action is only executed when the user pressed the "Display Terms" button
		 * It searches for unique past entries of the queryTerm inside the database.
		 * Database connection is required in order to access the database
		 * The result will be displayed on a table model, displaying all unique queryTerms.
		 */
		
		// Displays Previous Searchs Button
		JButton btnDisTerm = new JButton("Display Term");
		btnDisTerm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String querys = "SELECT DISTINCT QueryTerm FROM CrawlerDB";
				PreparedStatement psts;
				try {
					psts = connection.prepareStatement(querys);
					ResultSet rss = psts.executeQuery();
					tabDatabaseData.setModel(DbUtils.resultSetToTableModel(rss));
					
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
	
		
		GroupLayout groupLayout = new GroupLayout(frmDBViewer.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(17)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblCrawlerType, GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
							.addGap(543))
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(lblSentimentResult)
								.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
									.addComponent(cbSentimentResult, Alignment.LEADING, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(cbCrawlerType, Alignment.LEADING, 0, 209, Short.MAX_VALUE))
								.addGroup(groupLayout.createSequentialGroup()
									.addGap(6)
									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
										.addComponent(btnDisplayChart, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(btnLoadData, GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
										.addComponent(btnCrawl, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(btnDisTerm, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
								.addComponent(lblNewLabel)
								.addComponent(cSearch, GroupLayout.PREFERRED_SIZE, 203, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(lblTitle, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addGap(222))
								.addComponent(scrollDatabaseData, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
							.addContainerGap())))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(74)
					.addComponent(lblCrawlerType)
					.addGap(12)
					.addComponent(cbCrawlerType, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblSentimentResult, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(cbSentimentResult, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(lblNewLabel)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(cSearch, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(9)
					.addComponent(btnLoadData)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnDisplayChart)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnCrawl)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnDisTerm)
					.addContainerGap(53, Short.MAX_VALUE))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(25)
					.addComponent(lblTitle)
					.addGap(29)
					.addComponent(scrollDatabaseData, GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
					.addGap(42))
		);
		frmDBViewer.getContentPane().setLayout(groupLayout);
		

		
	}
	private String addand(String check) {
    	if(check!="SELECT * FROM CrawlerDB") {
    		check+=" AND ";
    	}if(!check.contains("WHERE")) {
    		check +=" WHERE ";
    	}
    	return check;
    }
}
		
