package ru.aptu.warehouse.tester.core;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/23/12
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class TableState {
    int id;
    long bytesCount = 0;
    long linesCount = 0;

    public TableState() {}

    public TableState(long bytesCount, long linesCount) {
        this.bytesCount = bytesCount;
        this.linesCount = linesCount;
    }

    public TableState(TableState state) {
        this.bytesCount = state.bytesCount;
        this.linesCount = state.linesCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getBytesCount() {
        return bytesCount;
    }

    public void setBytesCount(long bytesCount) {
        this.bytesCount = bytesCount;
    }

    public long getLinesCount() {
        return linesCount;
    }

    public void setLinesCount(long linesCount) {
        this.linesCount = linesCount;
    }
}
