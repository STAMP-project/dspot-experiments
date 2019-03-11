package com.thinkaurelius.titan.graphdb.serializer;


import Multiplicity.MANY2ONE;
import com.thinkaurelius.titan.StorageSetup;
import com.thinkaurelius.titan.core.EdgeLabel;
import com.thinkaurelius.titan.core.TitanEdge;
import com.thinkaurelius.titan.core.TitanVertex;
import com.thinkaurelius.titan.core.schema.TitanManagement;
import com.thinkaurelius.titan.graphdb.database.StandardTitanGraph;
import org.junit.Test;


/**
 *
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class EdgeSerializerTest {
    @Test
    public void testValueOrdering() {
        StandardTitanGraph graph = ((StandardTitanGraph) (StorageSetup.getInMemoryGraph()));
        TitanManagement mgmt = graph.openManagement();
        EdgeLabel father = mgmt.makeEdgeLabel("father").multiplicity(MANY2ONE).make();
        for (int i = 1; i <= 5; i++)
            mgmt.makePropertyKey(("key" + i)).dataType(Integer.class).make();

        mgmt.commit();
        TitanVertex v1 = graph.addVertex();
        TitanVertex v2 = graph.addVertex();
        TitanEdge e1 = v1.addEdge("father", v2);
        for (int i = 1; i <= 5; i++)
            e1.property(("key" + i), i);

        graph.tx().commit();
        e1.remove();
        graph.tx().commit();
    }
}

