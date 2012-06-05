package ru.aptu.warehouse.tester.utils.executionPlan.readers;

import org.w3c.dom.Element;
import ru.aptu.warehouse.tester.core.Cluster;

/**
 * Created by IntelliJ IDEA.
 * User: svemarch
 * Date: 5/22/12
 * Time: 9:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClusterReader {
    public static Cluster readCluster(Element element) {
        return updateCluster(new Cluster(), element);
    }
    
    public static Cluster updateCluster(final Cluster old, Element element) {
        Cluster updated = new Cluster(old);
        String mode = PlanReader.tryReadTagByName(element, "mode");
        if (mode != null) updated.setMode(mode);
        
        String description = PlanReader.tryReadTagByName(element, "description");
        if (description != null) updated.setDescription(description);

        String value = PlanReader.tryReadTagByName(element, "nodesCount");
        Integer nodesCount = value != null ? Integer.valueOf(value) : null;
        if (nodesCount != null) updated.setNodesCount(nodesCount);
        return updated;
    }
}
