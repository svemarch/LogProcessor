package core;

import core.ChunkTaskDistributor;
import core.LogPreprocessor;
import core.ResultFlusher;
import core.TaskInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 4/4/12
 * Time: 1:20 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class TaskManager {
    public static class Interval{
        private int leftBound;
        private int rightBound;

        public Interval(int leftBound, int rightBound) {
            this.leftBound = leftBound;
            this.rightBound = rightBound;
        }
        
        public boolean contains(int value) {
            return (leftBound <= value && value <= rightBound);
        }
    }

    protected TaskInfo taskInfo;
    protected List<String> sourceFiles = new ArrayList<String>();
    protected String resultFile;
    protected Map<Interval, ResultFlusher> keyHashFileMap;
    protected int threadCount;
    protected ExecutorService executor;
    protected String tmpDirectory = "./tmp";
    
    private static final int THREAD_COUNT_DEFAULT = 70;
    private static final int OUTPUT_FILES_COUNT_DEFAULT = 50;
    
    public TaskManager(List<String> sourceFiles, String resultFile) throws IOException {
        this.taskInfo =  new TaskInfo();
        this.sourceFiles = sourceFiles;
        this.resultFile = resultFile;
        this.threadCount = THREAD_COUNT_DEFAULT;

    }

    protected void defineKeyHashResultMap() throws IOException {
        ResultFlusher flusher = new ResultFlusher(this.resultFile);
        Interval anyValues = new Interval(Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.keyHashFileMap = new LinkedHashMap<Interval, ResultFlusher>();
        keyHashFileMap.put(anyValues, flusher);
    }

    protected void defineKeyHashFileMap() throws IOException {
        defineKeyHashFileMap(tmpDirectory);
    }

    protected void defineKeyHashFileMap(String directory) throws IOException {
        File tmpDirFile = new File(directory);
        if (tmpDirFile.exists() && tmpDirFile.isDirectory() && tmpDirFile.list().length > 0) {
            for (File file: tmpDirFile.listFiles()) {
                file.delete();
            }
        }
        if (!tmpDirFile.exists()) tmpDirFile.mkdir();
        this.keyHashFileMap = new LinkedHashMap<Interval, ResultFlusher>();
        int left = Integer.MIN_VALUE;
        int intervalLength = Integer.MAX_VALUE/OUTPUT_FILES_COUNT_DEFAULT + 1;
        intervalLength = intervalLength*2 - 1;
        int counter = 0;
        while (counter < OUTPUT_FILES_COUNT_DEFAULT) {
            int right = left + intervalLength;
            if (left >= 0 && right < 0) right = Integer.MAX_VALUE;
            Interval interval = new Interval(left, right);
            keyHashFileMap.put(interval, new ResultFlusher(directory.concat("/" + counter)));
            ++counter;
            left = right + 1;
        }
    }

    abstract public void execute() throws ExecutionException, InterruptedException, IOException;

    protected static <T> boolean allTaskDone(final List<Future<T>> states) {
        for (Future<T> state: states) {
            if (!state.isDone()) return false;
        }
        return true;
    }
    
    protected void closeFlushers() throws IOException {
        for (ResultFlusher flusher: keyHashFileMap.values()) {
            flusher.flush();
            flusher.close();
        }
    }
}
