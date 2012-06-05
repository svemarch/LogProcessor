package core.processors;

import core.ChunkFunction;
import core.ChunkProcessor;
import core.LineSplitter;
import core.ResultFlusher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 4/23/12
 * Time: 4:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class Counter extends ChunkFunction {
    @Override
    protected List<LineSplitter.ResultLine> processChunk(List<String> linesChunk) throws IOException {
        List<LineSplitter.ResultLine> resultLines = new ArrayList<LineSplitter.ResultLine>();
        String result = null;
        result = linesChunk.get(0);
        result = result.concat("\t"+linesChunk.size());
        LineSplitter.ResultLine line = new LineSplitter.ResultLine();
        line.setHashKey(1);
        line.setLine(result);
        resultLines.add(line);
        return resultLines;
    }
}
