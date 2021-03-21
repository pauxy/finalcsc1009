package csc1009assignment;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class chartThree extends JFrame {
	private static final long serialVersionUID = 1L;
	
	/* Start of chartThree javadoc
	 * 
	 * Initialize and show a 3D Stacked Bar Chart that can turn data into visualization with JFreeChart when called
	 * <p>
	 * Initialize queryTerm as a super to refer to DBViewer's queryTerm from the display's text field
	 * Initialize the type and all string array for the use of displaying dataset
	 * Call the DefaultCategoryDataset object to set the values of it with a for loop
	 * Call the JFreeChart api to create a 3D stacked bar chart for the purpose of displaying the data from SQLite database.
	 * Finally, name the chart and set its visibility and size
	 * 
	 * @param	queryTerm				Taken from DBViewer
	 * @see		3D Stacked Bar Chart  
	 */
	public chartThree(String queryTerm) {
		super(queryTerm);
	    
		final String[] type = {"Youtube","Reddit","Twitter"};
		final String[] all = {"Negative", "Neutral", "Positive"};        
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = 0; i<3;i++) { 
			int[] curr = chartOne.get(type[i],queryTerm);
			dataset.setValue(curr[0], type[i], all[0]);
			dataset.setValue(curr[1], type[i], all[1]);
			dataset.setValue(curr[2], type[i], all[2]);
	      	}


	      	JFreeChart chart = ChartFactory.createStackedBarChart3D("All", "polarity", "count", dataset, PlotOrientation.HORIZONTAL, true, true, true);
	      	ChartFrame frame1=new ChartFrame("Bar Chart",chart);
	      	frame1.setVisible(true);
	      	frame1.setSize(400,350);
	  }
}