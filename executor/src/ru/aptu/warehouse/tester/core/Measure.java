package ru.aptu.warehouse.tester.core;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/22/12
 * Time: 8:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class Measure {
    private int id;
    private String name;
    private String description;

    public Measure() {
    }

    public Measure(final Measure measure) {
        this.name = measure.name;
        this.description = measure.description;
    }

    public Measure(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
