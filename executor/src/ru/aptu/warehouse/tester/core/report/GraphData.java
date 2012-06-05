package ru.aptu.warehouse.tester.core.report;

import org.datanucleus.sco.simple.ArrayList;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 6/1/12
 * Time: 3:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class GraphData {
    public static class MeasurePoint {
        private long dataSize;
        private long millis;

        public MeasurePoint() {
        }

        public MeasurePoint(long dataSize, long millis) {
            this.dataSize = dataSize;
            this.millis = millis;
        }

        public long getDataSize() {
            return dataSize;
        }

        public void setDataSize(long dataSize) {
            this.dataSize = dataSize;
        }

        public long getMillis() {
            return millis;
        }

        public void setMillis(long millis) {
            this.millis = millis;
        }
    }
    
    private List<MeasurePoint> points = new java.util.ArrayList<MeasurePoint>();
    private String description;

    public GraphData() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public GraphData(List<MeasurePoint> points) {
        this.points = points;
    }

    public List<MeasurePoint> getPoints() {
        return points;
    }

    public void setPoints(List<MeasurePoint> points) {
        this.points = points;
    }

    public void addPoint(MeasurePoint point) {
        this.points.add(point);
    }
}
