package core;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 4/23/12
 * Time: 4:38 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class SplitterFunction {
    abstract protected LineSplitter.ResultLine process(final String line);
}
