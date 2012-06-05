package ru.aptu.warehouse.tester.utils.executionPlan.readers;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import ru.aptu.warehouse.tester.core.LaunchTaskList;
import ru.aptu.warehouse.tester.core.Measure;
import ru.aptu.warehouse.tester.core.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/22/12
 * Time: 9:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class TaskReader {
    public static Task readTask(Element element) {
        Task task = new Task();
        task.setName(PlanReader.tryReadTagByName(element, "name"));
        task.setDescription(PlanReader.tryReadTagByName(element, "description"));
        task.setType(PlanReader.tryReadTagByName(element, "type"));
        Element measures = (Element)element.getElementsByTagName("measures").item(0);
        if (measures != null) {
            NodeList measureNodes = measures.getElementsByTagName("measure");
            for (int i  = 0; i < measureNodes.getLength(); ++i) {
                Element measureElem = (Element) measureNodes.item(i);
                Measure measure = readMeasure(measureElem);
                task.addMeasure(measure);
            }
        }
        return task;
    }

    public static Measure readMeasure(Element element) {
        String measureName = PlanReader.tryReadTagByName(element, "name");
        String measureDescription = PlanReader.tryReadTagByName(element, "description");
        Measure measure = new Measure(measureName, measureDescription);
        return measure;
    }

    public static Task updateTask(final Task task, Element element) {
        Task updated = new Task(task);
        String name = PlanReader.tryReadTagByName(element, "name");
        if (!task.getName().equals(name)) return updated;
        
        String description = PlanReader.tryReadTagByName(element, "description");
        if (description != null) updated.setDescription(description);
        
        String type = PlanReader.tryReadTagByName(element, "type");
        if (type != null) updated.setType(type);
        
        Element measures = (Element)element.getElementsByTagName("measures");
        if (measures != null) {
            NodeList measureNodes = measures.getElementsByTagName("measure");
            for (int i  = 0; i < measureNodes.getLength(); ++i) {
                Element measureElem = (Element) measureNodes.item(i);
                Measure measure = readMeasure(measureElem);
                updated.updateMeasure(measure);    
            }
        }
        return updated;
    }

    public static List<Task> readAllTasks(Element element) {
        List<Task> taskList = new ArrayList<Task>();
        NodeList taskElements = element.getElementsByTagName("task");
        for (int i = 0; i < taskElements.getLength(); ++i) {
            Element taskElement = (Element)taskElements.item(i);
            Task task = readTask(taskElement);
            taskList.add(task);
        }
        return taskList;
    }

    public static LaunchTaskList updateAllTasks(final List<Task> tasks, Element element) {
        LaunchTaskList updatedList = new LaunchTaskList();
        updatedList.setTasks(tasks);
        NodeList nodes = element.getElementsByTagName("task");
        for (int i = 0; i < nodes.getLength(); ++i) {
            Element taskElement = (Element) nodes.item(i);
            String name = PlanReader.tryReadTagByName(taskElement, "name");
            if (name != null) {
                Task existed = updatedList.findByName(name);
                Task updatedTask = readTask(taskElement);
                if (existed != null)
                    updatedTask = updateTask(existed, taskElement);
                updatedList.replace(updatedTask);
            }
        }
        return updatedList;
    }
}
