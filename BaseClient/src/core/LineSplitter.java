package core;

import core.ResultFlusher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 4/22/12
 * Time: 1:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class LineSplitter implements Runnable{
    @Override
    public void run() {
        for (String line: lines) {
            ResultLine processedLine = processFunction.process(line);
            pushProcessedLine(processedLine);
        }
        try {
            writeResult();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Cannot write result");
        }
    }

    public static class ResultLine{
        private String line;
        private int hashKey;

        public String getLine() {
            return line;
        }

        public void setLine(String line) {
            this.line = line;
        }

        public int getHashKey() {
            return hashKey;
        }

        public void setHashKey(int hashKey) {
            this.hashKey = hashKey;
        }
    }
    
    private Map<TaskManager.Interval, ResultFlusher> keyHashOutputMap;
    protected List<String> lines;
    private Map<ResultFlusher, List<String>> processedLines = new LinkedHashMap<ResultFlusher, List<String>>();
    private SplitterFunction processFunction;

    public LineSplitter(Map<TaskManager.Interval, ResultFlusher> keyHashOutputMap, List<String> lines, SplitterFunction func) {
        this.keyHashOutputMap = keyHashOutputMap;
        this.lines = lines;
        this.processFunction = func;
    }

    protected void pushProcessedLine(ResultLine resultLine) {
        ResultFlusher outputFlusher = defineOutputFlusher(resultLine.getHashKey());
        if (!processedLines.containsKey(outputFlusher)) {
            processedLines.put(outputFlusher, new ArrayList<String>());
        }
        processedLines.get(outputFlusher).add(resultLine.getLine());
    } 
    
    private ResultFlusher defineOutputFlusher(int hashKey) {
        for (TaskManager.Interval interval: keyHashOutputMap.keySet()) {
            if (interval.contains(hashKey)) return keyHashOutputMap.get(interval);
        }    
        return null;
    }
    
    protected void writeResult() throws IOException {
        for (ResultFlusher outputFlusher: processedLines.keySet()) {
            outputFlusher.write(processedLines.get(outputFlusher));
        }
    }
}
