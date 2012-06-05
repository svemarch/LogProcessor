package ru.aptu.warehouse.tester.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/23/12
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class Table {
    int id;
    String name;
    String description;
    List<TableState> states = new ArrayList<TableState>();

    public Table() {
        states.add(new TableState(0, 0));
    }

    public Table(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Table(Table table) {
        this.name = table.name;
        this.description = table.description;
        this.states = new ArrayList<TableState>();
        for (TableState state: table.getStates()) {
            this.states.add(new TableState(state));
        }
    }

    public TableState getCurrentState() {
        return states.get(states.size()-1);
    }
    
    public long getLinesCount() {
        return getCurrentState().getLinesCount();
    }
    
    public long getBytesCount() {
        return getCurrentState().getBytesCount();
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

    public List<TableState> getStates() {
        return states;
    }

    public void setStates(List<TableState> states) {
        this.states = states;
    }

    public void addState(long bytesCount, long linesCount) {
        this.states.add(new TableState(bytesCount, linesCount));
    }
}
