package ru.aptu.warehouse.tester.utils.executionPlan.exceptions;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/27/12
 * Time: 5:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class InvalidValue extends TestPlanException{
    private String objectName;
    private String parameterName;
    private String invalidValue;
    private String allowedValues;

    public InvalidValue(String objectName, String parameterName, String invalidValue, String allowedValues) {
        super("Error: element "+ objectName+ " couldn`t have parameter "+ parameterName+
            " with value "+invalidValue+
            ". Allowed values: "+allowedValues
        );
        this.objectName = objectName;
        this.parameterName = parameterName;
        this.invalidValue = invalidValue;
        this.allowedValues = allowedValues;
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

    public String getInvalidValue() {
        return invalidValue;
    }

    public void setInvalidValue(String invalidValue) {
        this.invalidValue = invalidValue;
    }

    public String getAllowedValues() {
        return allowedValues;
    }

    public void setAllowedValues(String allowedValues) {
        this.allowedValues = allowedValues;
    }
}
