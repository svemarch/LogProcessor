package core;

import java.io.IOException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 4/23/12
 * Time: 4:46 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ChunkFunction  {
    abstract protected List<LineSplitter.ResultLine> processChunk(List<String> linesChunk) throws IOException;
}
