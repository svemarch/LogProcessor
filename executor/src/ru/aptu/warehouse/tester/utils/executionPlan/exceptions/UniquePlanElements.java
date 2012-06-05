package ru.aptu.warehouse.tester.utils.executionPlan.exceptions;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/27/12
 * Time: 4:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class UniquePlanElements extends TestPlanException{
    private String objectSet;
    private String uniqueObjectParameter;
    private String repeatedParameter;

    public UniquePlanElements(String objectSet, String uniqueObjectParameter, String repeatedParameter) {
        super("Error: all objects in " + objectSet +
                " should be unique by parameter " + uniqueObjectParameter +
                ". In the test plan objects with the same value " + repeatedParameter +
                " of parameter " + uniqueObjectParameter + " were found.");
        this.objectSet = objectSet;
        this.uniqueObjectParameter = uniqueObjectParameter;
        this.repeatedParameter = repeatedParameter;
    }

    public String getObjectSet() {
        return objectSet;
    }

    public void setObjectSet(String objectSet) {
        this.objectSet = objectSet;
    }

    public String getUniqueObjectParameter() {
        return uniqueObjectParameter;
    }

    public void setUniqueObjectParameter(String uniqueObjectParameter) {
        this.uniqueObjectParameter = uniqueObjectParameter;
    }

    public String getRepeatedParameter() {
        return repeatedParameter;
    }

    public void setRepeatedParameter(String repeatedParameter) {
        this.repeatedParameter = repeatedParameter;
    }
}
