package core;

import core.ChunkProcessor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
 * Time: 1:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChunkTaskDistributor implements Callable<Integer> {
    private String fileName;
    private Map<TaskManager.Interval, ResultFlusher> keyHashFileMap;
    private ExecutorService executor;
    private ChunkFunction function;

    public ChunkTaskDistributor(String fileName, Map<TaskManager.Interval, ResultFlusher> keyHashFileMap, ExecutorService executorService, ChunkFunction function) {
        this.fileName = fileName;
        this.keyHashFileMap = keyHashFileMap;
        this.executor = executorService;
        this.function = function;
    }


    private Map<String, List<String>> makeChunks() throws IOException {
        Map<String, List<String>> chunks = new LinkedHashMap<String, List<String>>();
        BufferedReader reader =  new BufferedReader(new FileReader(fileName));
        String line = null;
        while ((line = reader.readLine()) != null) {
            String values[] = line.split("\t");
            String keyHash = values[0];
            if (!chunks.containsKey(keyHash)) {
                chunks.put(keyHash, new ArrayList<String>());        
            }
            chunks.get(keyHash).add(line.substring(keyHash.concat("\t").length()));
        
        }
        reader.close();
        return chunks;
    }

    private List<Future<Integer>> distributeTasks(Map<String, List<String>> chunks) throws IOException {
        List<Future<Integer>> taskStates = new ArrayList<Future<Integer>>();
        for (String key: chunks.keySet()) {
            taskStates.add(executor.submit(new ChunkProcessor(chunks.get(key), keyHashFileMap, function)));
        }
        return taskStates;
    }

    private static <T> boolean allTaskDone(final List<Future<T>> states) {
        for (Future<T> state: states) {
            if (!state.isDone()) return false;
        }
        return true;
    }

    @Override
    public Integer call() throws Exception {
        try {
            List<Future<Integer>> states = distributeTasks(makeChunks());
            while (!allTaskDone(states)) {}
            return states.size();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}