package ru.aptu.warehouse.tester.services;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import ru.aptu.warehouse.tester.core.Launch;
import ru.aptu.warehouse.tester.core.Task;
import ru.aptu.warehouse.tester.core.execution.TaskSolution;
import ru.aptu.warehouse.tester.core.execution.Test;
import ru.aptu.warehouse.tester.core.report.GraphData;
import ru.aptu.warehouse.tester.core.report.Report;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 6/2/12
 * Time: 12:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class LaunchReporter implements Reporter{
    private DbService dbService;
    private Launch launch;

    public LaunchReporter(DbService dbService, Launch launch) {
        this.dbService = dbService;
        this.launch = launch;
    }

    @Override
    public void makeReport(Report reportInfo) {
        extractReportData(reportInfo);
        JFreeChart chart = createChart(reportInfo);
        try {
            saveToFile(chart, reportInfo.getPathToSave());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    @Override
    public List<GraphData> extractReportData(Report reportInfo) {
        Task task = launch.getTasks().findByName(reportInfo.getTaskName());
        int taskId = task.getId();
        int taskMeasureId = task.getMeasureByName(reportInfo.getMeasureName()).getId();
        Map<Integer, GraphData> result;
        if (reportInfo.getLevel() == Report.Level.TEST) {
            result = dbService.getTestData(reportInfo.getTestId(), taskId, taskMeasureId);
        } else {
            result = dbService.getLaunchData(reportInfo.getLaunchId(), taskId, taskMeasureId);
        }
        if (reportInfo.getReportData() == null) reportInfo.setReportData(new ArrayList<GraphData>());
        for (Integer testTaskId: result.keySet()) {
            String mode =  dbService.getTestTaskMode(testTaskId);
            if (mode == null) mode = getDefinedModeByTaskSolutionId(testTaskId);
            result.get(testTaskId).setDescription(mode);
            reportInfo.getReportData().add(result.get(testTaskId));
        }
        return reportInfo.getReportData();
    }
    
    private String getDefinedModeByTaskSolutionId(int taskSolutionId) {
        for (Test test: launch.getScenario().getTests()) {
            for (TaskSolution taskSolution: test.getTasks()) {
                if (taskSolution.getId() == taskSolutionId)
                    return test.getMode();
            }
        }
        return launch.getMode();
    }

    private XYDataset createDataset(List<GraphData> reportData) {
        final XYSeriesCollection dataset = new XYSeriesCollection();
        for (GraphData graphData: reportData) {
            final XYSeries series = new XYSeries(graphData.getDescription());
            for (GraphData.MeasurePoint point: graphData.getPoints()) {
                series.add(point.getDataSize(), point.getMillis());
            }
            dataset.addSeries(series);
        }
        return dataset;
    }

    private JFreeChart createChart(Report reportInfo) {
        final JFreeChart chart = ChartFactory.createXYLineChart(
            reportInfo.getTitle(),
                "lines",
                "millis",
                createDataset(reportInfo.getReportData()),
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        final XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.LIGHT_GRAY);
        plot.setDomainGridlinePaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.WHITE);

        final XYSplineRenderer renderer = new XYSplineRenderer();
        for (int i =0; i < reportInfo.getReportData().size(); ++i) {
            renderer.setSeriesShapesVisible(i, false);
        }
        plot.setRenderer(renderer);

        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        return chart;
    }

    private BufferedImage createImage(JFreeChart chart) {
        BufferedImage result = new BufferedImage(800, 640, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = result.createGraphics();
        chart.draw(graphics2D, new Rectangle2D.Double(0, 0, 800, 640));
        graphics2D.dispose();
        return result;
    }

    private void saveToFile(JFreeChart chart, String file) throws IOException {
        BufferedImage image = createImage(chart);
        FileOutputStream fos = null;
        String directoryName = file.substring(0, file.lastIndexOf("/"));
        File directory = new File(directoryName);
        if (!directory.exists()) {
            directory.mkdirs();
            File fileForImage = new File(file);
            fileForImage.createNewFile();
        }
        fos = new FileOutputStream(file);
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(fos);
        encoder.encode(image);
        fos.close();
    }
}
