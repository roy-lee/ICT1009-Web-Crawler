package Project;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

public class ChartServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //set document type as image
		response.setContentType("image/png");
        //get additional info after url, pnn values
        String query = request.getQueryString();
        String[] parts = query.split("&");
        int positive = Integer.parseInt(parts[0].split("=")[1]);
        int neutral = Integer.parseInt(parts[1].split("=")[1]);
        int negative = Integer.parseInt(parts[2].split("=")[1]);

		OutputStream outputStream = response.getOutputStream();

		JFreeChart chart = getChart(positive, neutral, negative); //call method to create pie chart
		int width = 500;
		int height = 350;
		ChartUtilities.writeChartAsPNG(outputStream, chart, width, height);

	}

	public JFreeChart getChart(int positive, int neutral, int negative) {
		DefaultPieDataset dataset = new DefaultPieDataset();
		dataset.setValue("Positive", positive);
		dataset.setValue("Neutral", neutral);
		dataset.setValue("Negative", negative);

		boolean legend = true;
		boolean tooltips = true;
		boolean urls = false;

		JFreeChart chart = ChartFactory.createPieChart("Sentiments", dataset, legend, tooltips, urls);

		chart.setBorderPaint(Color.BLACK);
		chart.setBorderStroke(new BasicStroke(5.0f));
		chart.setBorderVisible(true);

		return chart;
	}

}