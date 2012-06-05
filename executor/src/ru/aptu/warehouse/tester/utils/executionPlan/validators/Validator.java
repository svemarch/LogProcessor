package ru.aptu.warehouse.tester.utils.executionPlan.validators;

import ru.aptu.warehouse.tester.utils.executionPlan.exceptions.TestPlanException;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/27/12
 * Time: 3:48 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Validator {
    public abstract void validate() throws TestPlanException;
}
