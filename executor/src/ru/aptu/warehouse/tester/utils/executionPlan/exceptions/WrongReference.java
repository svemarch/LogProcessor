package ru.aptu.warehouse.tester.utils.executionPlan.exceptions;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/27/12
 * Time: 4:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class WrongReference extends TestPlanException{
    private String sourceElementsSet;
    private String referrerElement;
    private String referencedParameter;
    private String absenteeValue;

    public WrongReference(String sourceElementsSet, String referrerElement, String referencedParameter, String absenteeValue) {
        super("Error: "+ referrerElement +
                " should refer to values from " + sourceElementsSet +
                " element by parameter "+referencedParameter+
                ". No objects with value "+absenteeValue+" of parameter "+referencedParameter +
                " were found."
        );
        this.sourceElementsSet = sourceElementsSet;
        this.referrerElement = referrerElement;
        this.referencedParameter = referencedParameter;
        this.absenteeValue = absenteeValue;
    }

    public String getSourceElementsSet() {
        return sourceElementsSet;
    }

    public void setSourceElementsSet(String sourceElementsSet) {
        this.sourceElementsSet = sourceElementsSet;
    }

    public String getReferrerElement() {
        return referrerElement;
    }

    public void setReferrerElement(String referrerElement) {
        this.referrerElement = referrerElement;
    }

    public String getReferencedParameter() {
        return referencedParameter;
    }

    public void setReferencedParameter(String referencedParameter) {
        this.referencedParameter = referencedParameter;
    }

    public String getAbsenteeValue() {
        return absenteeValue;
    }

    public void setAbsenteeValue(String absenteeValue) {
        this.absenteeValue = absenteeValue;
    }
}
