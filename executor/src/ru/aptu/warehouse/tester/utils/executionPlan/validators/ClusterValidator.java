package ru.aptu.warehouse.tester.utils.executionPlan.validators;

import ru.aptu.warehouse.tester.core.Cluster;
import ru.aptu.warehouse.tester.utils.executionPlan.exceptions.NullPlanElement;
import ru.aptu.warehouse.tester.utils.executionPlan.exceptions.TestPlanException;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/27/12
 * Time: 3:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClusterValidator extends Validator{
    private Cluster cluster;

    public ClusterValidator(Cluster cluster) {
        this.cluster = cluster;
    }

    @Override
    public void validate() throws TestPlanException {
        if (cluster.getMode() == null || cluster.getMode().isEmpty()) {
            throw new NullPlanElement("cluster", "mode");
        }
        if (cluster.getNodesCount() == 0) {
            throw new NullPlanElement("cluster", "nodesCount");
        }
    }
}
