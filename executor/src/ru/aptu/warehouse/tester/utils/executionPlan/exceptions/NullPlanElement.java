package ru.aptu.warehouse.tester.utils.executionPlan.exceptions;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/27/12
 * Time: 3:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class NullPlanElement extends TestPlanException {
    private String objectName;
    private String parameterName;

    public NullPlanElement(String objectName, String parameterName) {
        super("Error: for element "+objectName+" parameter "+parameterName+" should be defined.");
        this.objectName = objectName;
        this.parameterName = parameterName;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }
}
