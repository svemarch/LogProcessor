package ru.aptu.warehouse.tester.utils.executionPlan.readers;

import org.xml.sax.SAXException;
import ru.aptu.warehouse.tester.core.*;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import ru.aptu.warehouse.tester.core.execution.Scenario;
import ru.aptu.warehouse.tester.utils.executionPlan.exceptions.TestPlanException;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/22/12
 * Time: 7:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlanReader {
    public static Launch read(String filePath) throws TestPlanException {
        Launch launch = new Launch();
        Document planDoc = null;
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setIgnoringElementContentWhitespace(true);
            planDoc = documentBuilderFactory.newDocumentBuilder().parse("file:"+filePath);
        } catch (SAXException e) {
            throw new TestPlanException("Error: SAX parsing of the file with test plan failed. \n"
                    +e.getMessage());
        } catch (IOException e) {
            throw new TestPlanException("Error: cannot open to read the file with test plan .\n"
                    +e.getMessage());
        } catch (ParserConfigurationException e) {
            throw new TestPlanException("Error: cannot parse the file with test plan by xml format.\n"
                    +e.getMessage());
        }
        Element launchElem = planDoc.getDocumentElement();

        launch.setMode(tryReadTagByName(launchElem, "mode"));  //not null
        launch.setDescription(tryReadTagByName(launchElem, "description"));
        launch.setCluster(readClusterInfo(
                (Element) launchElem.getElementsByTagName("cluster").item(0)));
        launch.setWarehouse(readWarehouse(
                (Element)launchElem.getElementsByTagName("warehouse").item(0))); //not null
        launch.setTasks(readTasks(
                (Element) launchElem.getElementsByTagName("tasks").item(0)));  //not null
        launch.setScenario(readScenario(
                (Element)launchElem.getElementsByTagName("scenario").item(0)));
        launch.setReports(ReportsReader.readAll(
                (Element)launchElem.getElementsByTagName("scenario").item(0))
        );
        return launch;
    }
    
    static String tryReadTagByName(Element element, String tagName) {
        NodeList tagList = element.getElementsByTagName(tagName);
        if (tagList == null || tagList.getLength() == 0
                || tagList.item(0).getFirstChild() == null) return null;
        String tagValue = tagList.item(0)
                .getFirstChild().getNodeValue();
        return tagValue;
    }

    public static Cluster readClusterInfo(Element element) throws TestPlanException {
        Cluster result = new Cluster();
        String file = tryReadTagByName(element, "file");
        if (file != null && !file.equals("")) {
            Document clusterDoc = null;
            try {
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                documentBuilderFactory.setIgnoringElementContentWhitespace(true);
                clusterDoc = documentBuilderFactory.newDocumentBuilder().parse("file:"+file);
            } catch (SAXException e) {
                throw new TestPlanException("Error: SAX parsing of the file with cluster properties failed. \n"
                        +e.getMessage());
            } catch (IOException e) {
                throw new TestPlanException("Error: cannot open to read the file with cluster properties.\n"
                        +e.getMessage());
            } catch (ParserConfigurationException e) {
                throw new TestPlanException("Error: cannot parse the file with cluster properties by xml format.\n"
                        +e.getMessage());
            }
            Element clusterElement = clusterDoc.getDocumentElement();
            result = ClusterReader.readCluster(clusterElement);
        }
        return ClusterReader.updateCluster(result, element);
    }
    
    public static LaunchTaskList readTasks(Element element) throws TestPlanException {
        List<Task> tasks = new ArrayList<Task>();
        String file = tryReadTagByName(element, "file");
        if (file != null && !file.equals("")) {
            Document tasksDoc = null;
            try {
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                documentBuilderFactory.setIgnoringElementContentWhitespace(true);
                tasksDoc = documentBuilderFactory.newDocumentBuilder().parse("file:"+file);
            } catch (SAXException e) {
                throw new TestPlanException("Error: SAX parsing of the file with tasks description failed. \n"
                        +e.getMessage());
            } catch (IOException e) {
                throw new TestPlanException("Error: cannot open to read the file with tasks description.\n"
                        +e.getMessage());
            } catch (ParserConfigurationException e) {
                throw new TestPlanException("Error: cannot parse the file with tasks description by xml format.\n"
                        +e.getMessage());
            }
            Element tasksElement = tasksDoc.getDocumentElement();
            tasks = TaskReader.readAllTasks(tasksElement);
        }
        return TaskReader.updateAllTasks(tasks, element);
    }

    public static Warehouse readWarehouse(Element element) {
        Warehouse warehouse = new Warehouse();
        warehouse.setType(tryReadTagByName(element, "type"));
        warehouse.setName(tryReadTagByName(element, "name"));
        warehouse.setDescription(tryReadTagByName(element, "description"));
        return warehouse;
    }

    public static Scenario readScenario(Element element) throws TestPlanException {
        return ScenarioReader.read(element);
    }
}
