package core;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 4/22/12
 * Time: 1:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResultFlusher {
    private BufferedWriter writer;
    private ConcurrentLinkedQueue<String> values;
    
    private static final int  DEFAULT_CACHE_SIZE = 1000;

    public ResultFlusher(String resultFile) throws IOException {
        this.writer = new BufferedWriter(new FileWriter(resultFile));
        values = new ConcurrentLinkedQueue<String>();
    }
    
    public void write(String line) throws IOException {
        values.add(line);
        if (values.size() >= DEFAULT_CACHE_SIZE) flush();
    }
    
    public void write(Collection<String> lines) throws IOException {
        values.addAll(lines);
        if (values.size() >= DEFAULT_CACHE_SIZE) flush();
    }

    public void flush() throws IOException {
        for (String line: values) {
            writer.write(line+"\n");
        }
        values.clear();
    }

    public void close() throws IOException {
        writer.close();
    }
}
