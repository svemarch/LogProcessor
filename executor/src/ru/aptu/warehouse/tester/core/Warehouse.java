package ru.aptu.warehouse.tester.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/22/12
 * Time: 8:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class Warehouse {
    private int id;
    private String type;
    private String name;
    private String description;
    private List<Table> tables = new ArrayList<Table>();

    public Warehouse() {}

    public Warehouse(Warehouse warehouse) {
        this.type = warehouse.type;
        this.name = warehouse.name;
        this.description = warehouse.description;
        this.tables = new ArrayList<Table>();
        for (Table table: warehouse.getTables()) {
            this.tables.add(new Table(table));
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

    public List<Table> getTables() {
        return tables;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }
    
    public Table getTableByName(String tableName) {
        for (Table table: this.tables) {
            if (table.getName().equals(tableName)) return table;
        }
        return null;
    }
    
}
