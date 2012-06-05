package ru.aptu.warehouse.tester.core.execution;

import org.apache.avro.generic.GenericData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/22/12
 * Time: 8:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class Scenario {
    private String preScript;
    private String postScript;
    private Loader loader = new Loader();
    private List<Test> tests = new ArrayList<Test>();
    private List<List<String>> testsArgs = new ArrayList<List<String>>();

    public Scenario() {}

    public Scenario(Scenario scenario) {
        this.preScript = scenario.preScript;
        this.postScript = scenario.postScript;
        this.loader = new Loader(scenario.getLoader());
        this.tests = new ArrayList<Test>();
        for (Test test: scenario.getTests()) {
            this.tests.add(new Test(test));
        }
    }

    public List<List<String>> getTestsArgs() {
        return testsArgs;
    }

    public void setTestsArgs(List<List<String>> testsArgs) {
        this.testsArgs = testsArgs;
    }

    public String getPreScript() {
        return preScript;
    }

    public void setPreScript(String preScript) {
        this.preScript = preScript;
    }

    public String getPostScript() {
        return postScript;
    }

    public void setPostScript(String postScript) {
        this.postScript = postScript;
    }

    public Loader getLoader() {
        return loader;
    }

    public void setLoader(Loader loader) {
        this.loader = loader;
    }

    public List<Test> getTests() {
        return tests;
    }

    public void setTests(List<Test> tests) {
        this.tests = tests;
    }
}
