package ru.aptu.warehouse.tester.utils.executionPlan.readers;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ru.aptu.warehouse.tester.core.Task;
import ru.aptu.warehouse.tester.core.execution.Loader;
import ru.aptu.warehouse.tester.core.execution.Scenario;
import ru.aptu.warehouse.tester.core.execution.TaskSolution;
import ru.aptu.warehouse.tester.core.execution.Test;
import ru.aptu.warehouse.tester.execution.SimpleTestRunner;
import ru.aptu.warehouse.tester.execution.TaskExecutor;
import ru.aptu.warehouse.tester.execution.TestRunner;
import ru.aptu.warehouse.tester.utils.executionPlan.exceptions.NoExecutorFound;
import ru.aptu.warehouse.tester.utils.executionPlan.exceptions.TestPlanException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/22/12
 * Time: 9:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class ScenarioReader {
    public static Scenario read(Element element) throws TestPlanException {
        Scenario scenario = new Scenario();

        Element scriptsElem = (Element)element.getElementsByTagName("scripts").item(0);
        String pre = PlanReader.tryReadTagByName(scriptsElem, "pre");
        String post = PlanReader.tryReadTagByName(scriptsElem, "post");
        scenario.setPreScript(pre);
        scenario.setPostScript(post);

        Element loaderElem = (Element) element.getElementsByTagName("loader").item(0);
        scenario.setLoader(loaderElem == null ? null: readLoader(loaderElem));

        Element testsElem = (Element) element.getElementsByTagName("tests").item(0);
        scenario.setTests(readTests(testsElem));
        
        Element iterationsElem = (Element) testsElem.getElementsByTagName("iterations").item(0);
        if (iterationsElem != null) {
            scenario.setTestsArgs(readIterations(iterationsElem));
        }
        return scenario;
    }
    
    private static Loader readLoader(Element element) throws TestPlanException {
        return  LoaderReader.read(element);
    }
    
    private static List<List<String>> readIterations(Element element) throws TestPlanException {
        String iterationsFile = null;
        if ((iterationsFile = PlanReader.tryReadTagByName(element, "file")) != null
                && !iterationsFile.equals("") ) {
            Document tasksDoc = null;
            try {
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                documentBuilderFactory.setIgnoringElementContentWhitespace(true);
                tasksDoc = documentBuilderFactory.newDocumentBuilder().parse("file:"+iterationsFile);
            } catch (SAXException e) {
                throw new TestPlanException("Error: SAX parsing of the file with loader properties failed. \n"
                        +e.getMessage());
            } catch (IOException e) {
                throw new TestPlanException("Error: cannot open to read the file with loader properties.\n"
                        +e.getMessage());
            } catch (ParserConfigurationException e) {
                throw new TestPlanException("Error: cannot parse the file with loader properties by xml format.\n"
                        +e.getMessage());
            }
            element = tasksDoc.getDocumentElement();
        }
        return readArgumentsForIterations(element);
    }
    
    private static List<Test> readTests(Element element) throws TestPlanException {
                   
        NodeList testElements = element.getElementsByTagName("test");
        List<Test> testList = new ArrayList<Test>();
        for (int i  = 0; i < testElements.getLength(); ++i) {
            Element testElem = (Element)testElements.item(i);
            testList.add(readTest(testElem));
        }
        return testList;
    }
    
    private static Test readTest(Element element) throws TestPlanException {
        Test test = new Test();
        
        String iterative = PlanReader.tryReadTagByName(element, "iterative");
        if (iterative != null) test.setIterative(Boolean.valueOf(iterative));
        
        test.setMode(PlanReader.tryReadTagByName(element, "mode"));
        test.setDescription(PlanReader.tryReadTagByName(element, "description"));

        NodeList tasksElements = ((Element)element.getElementsByTagName("tasks").item(0))
                .getElementsByTagName("task");
        List<TaskSolution> tasks = new ArrayList<TaskSolution>();
        for (int i =0; i<tasksElements.getLength(); ++i) {
            tasks.add(readTask((Element)tasksElements.item(i)));
        }
        test.setTasks(tasks);

        String executorName = PlanReader.tryReadTagByName(element, "runner");
        if (executorName != null && !executorName.equals("")) {
            Object executorObject = null;
            try {
                executorObject = Class.forName(executorName).newInstance();
            } catch (ClassNotFoundException e) {
                throw new NoExecutorFound(executorName, "test.runner");
            } catch (InstantiationException e) {
                throw new TestPlanException("Error: cannot instantiate an object of TestRunner class"+e.getMessage());
            } catch (IllegalAccessException e) {
                throw new TestPlanException("Error: catch IllegalAccessException while instantiating an object of " +
                        "TestRunner"+e.getMessage());
            }

            if (executorObject instanceof TestRunner) test.setRunner((TestRunner) executorObject);
        } else {
            test.setRunner(new SimpleTestRunner(test));
        }
        return test;
    }

    public static List<List<String>> readArgumentsForIterations(Element element) {
        NodeList iterations = element.getElementsByTagName("args");
        List<List<String>> result = new ArrayList<List<String>>();
        for (int i = 0; i < iterations.getLength(); ++i) {
            Element agrsElem = (Element)iterations.item(i);
            NodeList args = agrsElem.getElementsByTagName("arg");
            List<String> argList = new ArrayList<String>();
            for (int j = 0; j < args.getLength(); ++j) {
                Element arg = (Element)args.item(j);
                argList.add(arg.getAttribute("value"));
            }
            result.add(argList);
        }
        return result;
    }
    
    private static TaskSolution readTask(Element element) throws TestPlanException {
        TaskSolution taskSolution = new TaskSolution();
        taskSolution.setMode(PlanReader.tryReadTagByName(element, "mode"));
        Task info = new Task();
        info.setName(PlanReader.tryReadTagByName(element, "name"));
        taskSolution.setTask(info);
        String executorName = PlanReader.tryReadTagByName(element, "executor"); 
        if (executorName != null && !executorName.equals("")) {
            Object executorObject = null;
            try {
                executorObject = Class.forName(executorName).newInstance();
            } catch (ClassNotFoundException e) {
                throw new NoExecutorFound(executorName, "task.executor");
            } catch (InstantiationException e) {
                throw new TestPlanException("Error: cannot instantiate an object of TaskExecutor class"+e.getMessage());
            } catch (IllegalAccessException e) {
                throw new TestPlanException("Error: catch IllegalAccessException while instantiating an object of " +
                        "TaskExecutor"+e.getMessage());
            }
            if (executorObject instanceof TaskExecutor) taskSolution.setExecutor((TaskExecutor)executorObject);
        }
        return taskSolution;
    }
}
