package core;

import ru.aptu.warehouse.tester.services.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 4/22/12
 * Time: 1:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class LogPreprocessor implements Callable<Long>{
    private String logFile;
    private Map<TaskManager.Interval, ResultFlusher> outputMap;
    private ExecutorService outputSubmitter;
    private SplitterFunction function;
    private Logger logger;

    private static final int DEFAULT_TASK_SIZE = 10000;

    public LogPreprocessor(Logger logger, String logFile, Map<TaskManager.Interval, ResultFlusher> outputMap, ExecutorService executor, SplitterFunction function) {
        this.logFile = logFile;
        this.outputMap = outputMap;
        this.outputSubmitter = executor;
        this.function = function;
        this.logger = logger;
    }

    @Override
    public Long call() throws Exception {
        long time = System.currentTimeMillis();
        
        BufferedReader reader = new BufferedReader(new FileReader(logFile));
        String line = null;
        List<String> taskChunk = new ArrayList<String>(DEFAULT_TASK_SIZE);
        List<Future<Object>> splitterStates = new ArrayList<Future<Object>>();
        while ((line = reader.readLine()) != null) {
            taskChunk.add(line);
            if (taskChunk.size() == DEFAULT_TASK_SIZE) {
                splitterStates.add(outputSubmitter.submit(Executors.callable(new LineSplitter(outputMap, taskChunk, function))));
                taskChunk = new ArrayList<String>();
            }
        }
        Long preprocessedLines = (long)DEFAULT_TASK_SIZE * splitterStates.size();
        if (taskChunk.size() > 0) {
            splitterStates.add(outputSubmitter.submit(Executors.callable(new LineSplitter(outputMap, taskChunk, function))));
            preprocessedLines += taskChunk.size();
        }
        reader.close();
        while (!allTaskDone(splitterStates)) {}

        long diff = System.currentTimeMillis() - time;
        String[] logPathTokens = logFile.split("/");
        String logName = logPathTokens[logPathTokens.length-1];
        logger.createTable(logName, "");
        logger.setTableState(logName, 0, preprocessedLines);
        //logger.addMeasureResult("whole_task", "load_task", preprocessedLines, diff);
        
        return preprocessedLines;
    }

    private static <T> boolean allTaskDone(final List<Future<T>> states) {
        for (Future<T> state: states) {
            if (!state.isDone()) return false;
        }
        return true;
    }
}
