package tasks.simple;

import core.ChunkTaskDistributor;
import core.LogPreprocessor;
import core.ResultFlusher;
import core.TaskManager;
import core.processors.Counter;
import ru.aptu.warehouse.tester.services.Logger;
import tasks.ResultLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 4/23/12
 * Time: 4:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleTaskManager extends TaskManager {
    private Logger logger;
    private ResultLoader loader;

    public SimpleTaskManager(List<String> sourceFiles, String resultFile, Logger logger) throws IOException {
        super(sourceFiles, resultFile);
        this.logger = logger;
    }

    public ResultLoader getLoader() {
        return loader;
    }

    public void setLoader(ResultLoader loader) {
        this.loader = loader;
    }

    @Override
    public void execute() throws ExecutionException, InterruptedException, IOException {
        long time = System.currentTimeMillis();
        executor = Executors.newFixedThreadPool(this.threadCount);
        processLogs();
        executeTask();
        loader.loadResult(resultFile, "simple_task");
        executor.shutdown();
        long diff = System.currentTimeMillis() - time;
        logger.addMeasureResult("whole_task", "simple_task", taskInfo.getPreprocessedLogLines(), diff);
    }

    private void executeTask() throws IOException {
        long time = System.currentTimeMillis();
        ResultFlusher resultFlusher = new ResultFlusher(resultFile);
        File splitterOutput[] = new File(tmpDirectory).listFiles();
        List<Future<Integer>> states = new ArrayList<Future<Integer>>();
        Map<Interval, ResultFlusher> resultFlusherMap = new LinkedHashMap<Interval, ResultFlusher>();
        resultFlusherMap.put(new Interval(Integer.MIN_VALUE, Integer.MAX_VALUE), resultFlusher);
        for (File file: splitterOutput) {
            if (file.length() > 0) {
                states.add(executor.submit(
                        new ChunkTaskDistributor(file.getAbsolutePath(), resultFlusherMap, executor, new Counter())));
            }
        }
        while(!allTaskDone(states)) {}
        resultFlusher.flush();
        resultFlusher.close();
        long diff = System.currentTimeMillis() - time;
        logger.addMeasureResult("aggregation", "simple_task", taskInfo.getPreprocessedLogLines(), diff);
    }


    private void processLogs() throws ExecutionException, InterruptedException, IOException {
        defineKeyHashFileMap();
        List<Future<Long>> readerStates = new ArrayList<Future<Long>>(sourceFiles.size());
        long time = System.currentTimeMillis();
        for (String log: sourceFiles) {
            readerStates.add(executor.submit(new LogPreprocessor(logger, log, keyHashFileMap, executor, new ShowLogSplitter())));
        }
        while (!allTaskDone(readerStates)) {}
        long diff = System.currentTimeMillis() - time;

        for (Future<Long> readerResult: readerStates) {
            long cnt = taskInfo.getPreprocessedLogLines();
            taskInfo.setPreprocessedLogLines(cnt + readerResult.get());
        }
        //logger.addMeasureResult("whole_task", "load_task", taskInfo.getPreprocessedLogLines(), diff);
        for (ResultFlusher flusher: keyHashFileMap.values()) {
            flusher.flush();
            flusher.close();
        }
    }
}
