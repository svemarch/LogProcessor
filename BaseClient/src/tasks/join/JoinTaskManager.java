package tasks.join;

import core.ChunkTaskDistributor;
import core.LogPreprocessor;
import core.TaskManager;
import core.processors.Counter;
import ru.aptu.warehouse.tester.services.Logger;
import tasks.ResultLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 4/23/12
 * Time: 5:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class JoinTaskManager extends TaskManager{
    private Logger logger;
    private ResultLoader loader;
    
    public JoinTaskManager(List<String> sourceFiles, String resultFile, Logger logger) throws IOException {
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
        long time= System.currentTimeMillis();
        executor = Executors.newFixedThreadPool(this.threadCount);
        processLogs();
        executeMergeTask();
        executeCountTask();
        loader.loadResult(resultFile, "join_task");
        executor.shutdown();
        long diff = System.currentTimeMillis() - time;
        logger.addMeasureResult("whole_task", "join_task", taskInfo.getPreprocessedLogLines(), diff);
    }

    private void processLogs() throws ExecutionException, InterruptedException, IOException {
        defineKeyHashFileMap();
        List<Future<Long>> readerStates = new ArrayList<Future<Long>>(sourceFiles.size());
        long time = System.currentTimeMillis();
        for (String log: sourceFiles) {
            readerStates.add(executor.submit(new LogPreprocessor(logger, log, keyHashFileMap, executor, new LogSplitter())));
        }
        while (!allTaskDone(readerStates)) {}
        long diff = System.currentTimeMillis() - time;
        for (Future<Long> readerResult: readerStates) {
            long cnt = taskInfo.getPreprocessedLogLines();
            taskInfo.setPreprocessedLogLines(cnt + readerResult.get());
        }
        logger.addMeasureResult("whole_task", "load_task", taskInfo.getPreprocessedLogLines(), diff);
        closeFlushers();
    }

    private void executeMergeTask() throws IOException {
        long time = System.currentTimeMillis();

        defineKeyHashFileMap(tmpDirectory+"1");
        File splitterOutput[] = new File(tmpDirectory).listFiles();
        List<Future<Integer>> states = new ArrayList<Future<Integer>>();
        for (File file: splitterOutput) {
            if (file.length() > 0) {
                states.add(executor.submit(
                        new ChunkTaskDistributor(file.getAbsolutePath(), keyHashFileMap, executor, new MergeClicks())));
            }

        }
        while(!allTaskDone(states)) {}
        closeFlushers();

        long diff = System.currentTimeMillis() - time;
        String sourceTableName = this.sourceFiles.get(0);
        sourceTableName = sourceTableName.substring(sourceTableName.lastIndexOf("/"));
        logger.addMeasureResult("join", "join_task", taskInfo.getPreprocessedLogLines(), diff);
     }

    private void executeCountTask() throws IOException {
        long time = System.currentTimeMillis();
        defineKeyHashResultMap();
        File splitterOutput[] = new File(tmpDirectory+"1").listFiles();
        List<Future<Integer>> states = new ArrayList<Future<Integer>>();
        for (File file: splitterOutput) {
            if (file.length() > 0) {
                states.add(executor.submit(
                        new ChunkTaskDistributor(file.getAbsolutePath(), keyHashFileMap, executor, new Counter())));
            }
        }
        while(!allTaskDone(states)) {}
        closeFlushers();

        long diff = System.currentTimeMillis() - time;
        logger.addMeasureResult("aggregation", "join_task", taskInfo.getPreprocessedLogLines(), diff);
    }
}
