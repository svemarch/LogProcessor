package ru.aptu.warehouse.tester.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/22/12
 * Time: 8:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class Task {

    public enum Type {LOAD, PROCESS}

    private int id;
    private String name;
    private String description;
    private String type;
    private List<Measure> measures = new ArrayList<Measure>();

    public Task() {
    }
    
    public Task(final Task task) {
        this.name = task.name;
        this.description = task.description;
        this.type = task.type;
        this.measures = new ArrayList<Measure>();
        for (Measure measure: task.getMeasures()) {
            this.measures.add(new Measure(measure));
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Measure> getMeasures() {
        return measures;
    }

    public void setMeasures(List<Measure> measures) {
        this.measures = measures;
    }
    
    public boolean addMeasure(Measure measure) {
        if (getMeasureByName(measure.getName())!= null) return false;
        measures.add(measure);
        return true;
    }
    
    public Measure getMeasureByName(String name) {
        for (Measure measure: measures) {
            if (measure.getName().equals(name)) return measure;
        }
        return null;
    }

    public void updateMeasure(Measure measure) {
        Measure old = getMeasureByName(measure.getName());
        if (old != null) this.measures.remove(old);
        addMeasure(measure);
    }
}
