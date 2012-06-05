package tasks.simple;

import core.LineSplitter;
import core.ResultFlusher;
import core.SplitterFunction;
import core.TaskManager;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 4/23/12
 * Time: 4:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class ShowLogSplitter extends SplitterFunction {

    @Override
    protected LineSplitter.ResultLine process(String line) {
        LineSplitter.ResultLine result = new LineSplitter.ResultLine();
        String values[] = line.split("\t");
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(values[0]+"\t");    //add date
        keyBuilder.append(values[5]+"\t");    //add region
        keyBuilder.append(values[3]+"\t");    //add company_id
        keyBuilder.append(values[4]);         //add base
        int hashKey = keyBuilder.toString().hashCode();
        result.setHashKey(hashKey);
        StringBuilder lineBuilder = new StringBuilder();
        lineBuilder.append(hashKey+"\t");
        lineBuilder.append(keyBuilder.toString());
        result.setLine(lineBuilder.toString());
        return result;
    }

}
