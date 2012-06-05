package ru.aptu.warehouse.tester.utils.executionPlan.exceptions;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/27/12
 * Time: 3:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestPlanException extends Exception {
    private String message;

    public TestPlanException() {
    }

    public TestPlanException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
