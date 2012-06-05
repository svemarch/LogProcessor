package ru.aptu.warehouse.tester.core;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/22/12
 * Time: 8:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class Cluster {
    private int id;
    private String mode;
    private String description;
    private int nodesCount = 0;

    public Cluster() {
    }

    public Cluster(Cluster lft) {
        this.mode = lft.mode;
        this.description = lft.description;
        this.nodesCount = lft.nodesCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNodesCount() {
        return nodesCount;
    }

    public void setNodesCount(int nodesCount) {
        this.nodesCount = nodesCount;
    }
}
