package core;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 4/22/12
 * Time: 1:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChunkProcessor implements Callable<Integer> {
    private Map<TaskManager.Interval, ResultFlusher> keyHashOutputMap;
    private List<String> linesChunk;
    private  ChunkFunction function;

    public ChunkProcessor(List<String> lines, Map<TaskManager.Interval, ResultFlusher> keyHashOutputMap, ChunkFunction function) {
        this.keyHashOutputMap = keyHashOutputMap;
        this.linesChunk = lines;
        this.function = function;
    }

    /*@Override
    public void run() {
        try {
            List<LineSplitter.ResultLine> results = function.processChunk(linesChunk);
            for (LineSplitter.ResultLine result: results) {
                defineOutputFlusher(result.getHashKey()).write(result.getLine());
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }*/

    private ResultFlusher defineOutputFlusher(int hashKey) {
        for (TaskManager.Interval interval: keyHashOutputMap.keySet()) {
            if (interval.contains(hashKey)) return keyHashOutputMap.get(interval);
        }
        return null;
    }

    @Override
    public Integer call() throws Exception {
        try {
            List<LineSplitter.ResultLine> results = function.processChunk(linesChunk);
            for (LineSplitter.ResultLine result: results) {
                defineOutputFlusher(result.getHashKey()).write(result.getLine());
            }
            return results.size();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return 0;
    }
}
