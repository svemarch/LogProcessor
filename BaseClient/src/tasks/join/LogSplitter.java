package tasks.join;

import core.LineSplitter;
import core.SplitterFunction;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 4/23/12
 * Time: 5:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class LogSplitter extends SplitterFunction{
    /*
    * input:
    *   dt req_id pos comp_id base region - from show log
    *   dt req_id pos path                - from click log
    * output:
    *   hash [line] (click|show)
    * */
    @Override
    protected LineSplitter.ResultLine process(String line) {
        LineSplitter.ResultLine resultLine = new LineSplitter.ResultLine();
        String values[] = line.split("\t");
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(values[0]+"\t");    //add date
        keyBuilder.append(values[1]+"\t");    //add request id
        keyBuilder.append(values[2]);         //add pos
        int hashKey = keyBuilder.toString().hashCode();
        resultLine.setHashKey(hashKey);
        StringBuilder lineBuilder = new StringBuilder();
        lineBuilder.append(hashKey+"\t");
        lineBuilder.append(line+"\t");
        if (values.length == 4) {
            lineBuilder.append("click");
        } else {
            lineBuilder.append("show");
        }
        resultLine.setLine(lineBuilder.toString());
        return resultLine;
    }
}
