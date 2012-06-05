package tasks.join;

import core.ChunkFunction;
import core.LineSplitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 4/23/12
 * Time: 6:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class MergeClicks extends ChunkFunction{
    @Override
    protected List<LineSplitter.ResultLine> processChunk(List<String> linesChunk) throws IOException {
        List<LineSplitter.ResultLine> resultLines = new ArrayList<LineSplitter.ResultLine>();
        String showLine = seekShowLine(linesChunk);
        if (showLine != null) {
            String showInfo = pullShowInfo(showLine);
            for (String line: linesChunk) {
                if (line.endsWith("click")) {
                    StringBuilder key = new StringBuilder(showInfo);
                    key.append(line.split("\t")[3]);        //add path
                    int hash = key.toString().hashCode();
                    StringBuilder lineBuilder = new StringBuilder();
                    lineBuilder.append(hash+"\t");
                    lineBuilder.append(key.toString());
                    LineSplitter.ResultLine resultLine = new LineSplitter.ResultLine();
                    resultLine.setHashKey(hash);
                    resultLine.setLine(lineBuilder.toString());
                    resultLines.add(resultLine);
                }
            }    
        }
        return resultLines;
    }
    
    private String seekShowLine (List<String> lines) {
        String showLine = null;
        for (String line: lines) {
            if (line.endsWith("show")) return line;
        }
        return showLine;
    }
    
    private String pullShowInfo (String showLine) {
        String values[] = showLine.split("\t");
        StringBuilder infoBuilder = new StringBuilder();
        infoBuilder.append(values[0]+"\t");         //add dt
        infoBuilder.append(values[3]+"\t");         //add company id
        infoBuilder.append(values[4]+"\t");         //add base
        infoBuilder.append(values[5]+"\t");         //add region
        return infoBuilder.toString();
    }
}
