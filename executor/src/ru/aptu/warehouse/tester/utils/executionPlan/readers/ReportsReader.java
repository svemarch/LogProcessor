package ru.aptu.warehouse.tester.utils.executionPlan.readers;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import ru.aptu.warehouse.tester.core.report.Report;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 6/1/12
 * Time: 3:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class ReportsReader {
    public static List<Report> readAll(Element element) {
        List<Report> reports = new ArrayList<Report>();
        Element launchReportsElement  = null;
        if (element.getElementsByTagName("reports").getLength() > 0) 
            launchReportsElement = (Element) element.getElementsByTagName("reports").item(0);
        if (launchReportsElement != null) reports.addAll(read(launchReportsElement));
        NodeList tests = ((Element)element.getElementsByTagName("tests").item(0)).getElementsByTagName("test");
        for (int i = 0; i < tests.getLength(); ++i) {
            Element testElem = (Element)tests.item(i);
            if (testElem.getElementsByTagName("reports").getLength() > 0)  {
                Element testReportsElem = (Element) testElem.getElementsByTagName("reports").item(0);
                List<Report> testReports = read(testReportsElem);
                for (Report report: testReports) {
                    report.setTestId(i);
                    reports.add(report);
                }
            }
        }
        return reports;
    }
    
    public static List<Report> read(Element element) {
        List<Report> reports = new ArrayList<Report>();
        String parentName = element.getParentNode().getNodeName();
        Report.Level level = Report.Level.LAUNCH;
        if (parentName.equals("test")) level = Report.Level.TEST;
        String pathToSave = PlanReader.tryReadTagByName(element, "path");
        NodeList measures = element.getElementsByTagName("measure");
        for (int i = 0; i < measures.getLength(); ++i) {
            Element measureElem = (Element) measures.item(i);
            String taskName = measureElem.getAttribute("task");
            String measureName = measureElem.getFirstChild().getNodeValue();
            Report report = new Report(level, taskName, measureName);
            if (pathToSave != null) report.setPathToSave(pathToSave);
            reports.add(report);
        }
        return reports;
    } 
}
