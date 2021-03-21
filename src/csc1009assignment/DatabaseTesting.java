package csc1009assignment;
import java.awt.EventQueue;
import java.sql.*;
import javax.swing.*;

import net.proteanit.sql.DbUtils;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


// Testing DatabaseCodes
public class DatabaseTesting {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					DatabaseTesting window = new DatabaseTesting();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	Connection connection = null;
	private JTable tabDatabaseData;

	/**
	 * Create the application.
	 */
	public DatabaseTesting() {
		initialize();
	}


	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		connection = sqliteConnector.dbConnector();
		frame = new JFrame();
		frame.setBounds(100, 100, 679, 481);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JButton btnLoadData = new JButton("Load Data");
		btnLoadData.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				try {
					String query = "insert into CrawlerDB (UserID,Username,Subreddit,Title) values (?,?,?,?)";
					PreparedStatement pst = connection.prepareStatement(query);
					pst.setString(1, "100001");
					pst.setString(2, "testings");
					pst.setString(3, "tests");
					pst.setString(4, "testsss");
					
					pst.execute();
					JOptionPane.showMessageDialog(null, "Data saved");
					
					String querys = "select * from CrawlerDB";
					PreparedStatement psts = connection.prepareStatement(querys);
					ResultSet rss = psts.executeQuery();
					tabDatabaseData.setModel(DbUtils.resultSetToTableModel(rss));
				} catch (Exception err) {
					err.printStackTrace();
				} 
			}
		});

				
				
		btnLoadData.setBounds(392, 48, 89, 23);
		frame.getContentPane().add(btnLoadData);

		JScrollPane scrollDatabaseData = new JScrollPane();
		scrollDatabaseData.setBounds(204, 93, 449, 327);
		frame.getContentPane().add(scrollDatabaseData);

		tabDatabaseData = new JTable();
		scrollDatabaseData.setViewportView(tabDatabaseData);
	}
}
		
