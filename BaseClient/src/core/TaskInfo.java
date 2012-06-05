package core;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 4/22/12
 * Time: 2:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class TaskInfo {
    private long preprocessedLogLines = 0;

    public long getPreprocessedLogLines() {
        return preprocessedLogLines;
    }

    public void setPreprocessedLogLines(long preprocessedLogLines) {
        this.preprocessedLogLines = preprocessedLogLines;
    }
}
