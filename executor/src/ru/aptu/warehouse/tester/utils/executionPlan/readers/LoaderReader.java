package ru.aptu.warehouse.tester.utils.executionPlan.readers;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ru.aptu.warehouse.tester.core.execution.Loader;
import ru.aptu.warehouse.tester.execution.LoadExecutor;
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
 * Date: 5/23/12
 * Time: 4:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoaderReader {
    public static Loader read(Element element) throws TestPlanException {
        Loader loader = new Loader();

        Element initElem = (Element) element.getElementsByTagName("init").item(0);
        Boolean need = Boolean.valueOf(PlanReader.tryReadTagByName(initElem, "need"));
        Boolean iterative = Boolean.valueOf(PlanReader.tryReadTagByName(initElem, "iterative"));
        loader.setInitProperties(need, iterative);

        loader.setMode(PlanReader.tryReadTagByName(element, "mode"));
        
        Element iterationsElem = (Element) element.getElementsByTagName("iterations").item(0);
        String iterationsFile = null;
        if ((iterationsFile = PlanReader.tryReadTagByName(iterationsElem, "file")) != null 
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
            iterationsElem = tasksDoc.getDocumentElement();
        }
        loader.setArgs(ScenarioReader.readArgumentsForIterations(iterationsElem));
        
        String executor = PlanReader.tryReadTagByName(element, "executor");
        Object executorObject = null;
        try {
            executorObject = Class.forName(executor).newInstance();
        } catch (ClassNotFoundException e) {
            throw new NoExecutorFound(executor, "loader");
        } catch (InstantiationException e) {
            throw new TestPlanException("Error: cannot instantiate an object of LoaderExecutor class"+e.getMessage());
        } catch (IllegalAccessException e) {
            throw new TestPlanException("Error: catch IllegalAccessException while instantiating an object of " +
                    "LoaderExecutor"+e.getMessage());
        }
        if (executorObject instanceof LoadExecutor) {
            loader.setExecutor((LoadExecutor) executorObject);
        }
        return loader;
    } 
    

}
