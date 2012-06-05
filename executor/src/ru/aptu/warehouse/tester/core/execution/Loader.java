package ru.aptu.warehouse.tester.core.execution;

import ru.aptu.warehouse.tester.execution.LoadExecutor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/23/12
 * Time: 3:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class Loader {

    public static class InitProperties {
        boolean needCreate;
        boolean isIterative;

        public boolean isNeedCreate() {
            return needCreate;
        }

        public void setNeedCreate(boolean needCreate) {
            this.needCreate = needCreate;
        }

        public boolean isIterative() {
            return isIterative;
        }

        public void setIterative(boolean iterative) {
            isIterative = iterative;
        }
    }

    private String mode;
    private InitProperties initProperties = new InitProperties();
    private List<List<String>> args = new ArrayList<List<String>>();
    private LoadExecutor executor;


    public Loader() {

    }

    public Loader(Loader loader) {
        this.mode = loader.mode;
        this.initProperties = loader.initProperties;
        this.args = new ArrayList<List<String>>(loader.args);
        this.executor = loader.executor;
    }


    public InitProperties getInitProperties() {
        return initProperties;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setInitProperties(InitProperties initProperties) {
        this.initProperties = initProperties;
    }

    public void setInitProperties(boolean need, boolean iterative) {
        this.initProperties.needCreate = need;
        this.initProperties.isIterative = iterative;
    }

    public List<List<String>> getArgs() {
        return args;
    }

    public void setArgs(List<List<String>> args) {
        this.args = args;
    }

    public LoadExecutor getExecutor() {
        return executor;
    }

    public void setExecutor(LoadExecutor executor) {
        this.executor = executor;
    }
}
