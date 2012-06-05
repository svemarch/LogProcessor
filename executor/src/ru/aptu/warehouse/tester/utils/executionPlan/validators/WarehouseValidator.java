package ru.aptu.warehouse.tester.utils.executionPlan.validators;

import ru.aptu.warehouse.tester.core.Warehouse;
import ru.aptu.warehouse.tester.utils.executionPlan.exceptions.NullPlanElement;
import ru.aptu.warehouse.tester.utils.executionPlan.exceptions.TestPlanException;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/27/12
 * Time: 3:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class WarehouseValidator extends Validator{
    private Warehouse warehouse;

    public WarehouseValidator(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    @Override
    public void validate() throws TestPlanException {
        if (warehouse.getName() == null || warehouse.getName().isEmpty()) {
            throw new NullPlanElement("warehouse", "name");
        }
        if (warehouse.getType() == null || warehouse.getType().isEmpty()) {
            throw new NullPlanElement("warehouse", "type");
        }
    }
}
