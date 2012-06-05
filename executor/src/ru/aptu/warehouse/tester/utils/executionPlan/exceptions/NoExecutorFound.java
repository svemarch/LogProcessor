package ru.aptu.warehouse.tester.utils.executionPlan.exceptions;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/27/12
 * Time: 4:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class NoExecutorFound extends TestPlanException{
    private String absenteeClass;
    private String containedElement;

    public NoExecutorFound(String absenteeClass, String containedElement) {
        super("Error: no class " + absenteeClass +
                " was found for "+containedElement+" executor.");
        this.absenteeClass = absenteeClass;
        this.containedElement = containedElement;
    }

    public String getAbsenteeClass() {
        return absenteeClass;
    }

    public void setAbsenteeClass(String absenteeClass) {
        this.absenteeClass = absenteeClass;
    }

    public String getContainedElement() {
        return containedElement;
    }

    public void setContainedElement(String containedElement) {
        this.containedElement = containedElement;
    }
}
