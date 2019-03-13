package com.thinkaurelius.titan.hadoop;


import Cardinality.LIST;
import com.thinkaurelius.titan.example.GraphOfTheGodsFactory;
import com.thinkaurelius.titan.graphdb.TitanGraphBaseTest;
import java.util.List;
import java.util.Map;
import org.apache.tinkerpop.gremlin.hadoop.process.computer.spark.SparkGraphComputer;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory;
import org.junit.Assert;
import org.junit.Test;


public class CassandraInputFormatIT extends TitanGraphBaseTest {
    @Test
    public void testReadGraphOfTheGods() {
        GraphOfTheGodsFactory.load(graph, null, true);
        Assert.assertEquals(12L, ((long) (graph.traversal().V().count().next())));
        Graph g = GraphFactory.open("target/test-classes/cassandra-read.properties");
        GraphTraversalSource t = g.traversal(GraphTraversalSource.computer(SparkGraphComputer.class));
        Assert.assertEquals(12L, ((long) (t.V().count().next())));
    }

    @Test
    public void testReadWideVertexWithManyProperties() {
        int numProps = 1 << 16;
        long numV = 1;
        mgmt.makePropertyKey("p").cardinality(LIST).dataType(Integer.class).make();
        mgmt.commit();
        finishSchema();
        for (int j = 0; j < numV; j++) {
            Vertex v = graph.addVertex();
            for (int i = 0; i < numProps; i++) {
                v.property("p", i);
            }
        }
        graph.tx().commit();
        Assert.assertEquals(numV, ((long) (graph.traversal().V().count().next())));
        Map<String, Object> propertiesOnVertex = graph.traversal().V().valueMap().next();
        List<?> valuesOnP = ((List) (propertiesOnVertex.values().iterator().next()));
        Assert.assertEquals(numProps, valuesOnP.size());
        Graph g = GraphFactory.open("target/test-classes/cassandra-read.properties");
        GraphTraversalSource t = g.traversal(GraphTraversalSource.computer(SparkGraphComputer.class));
        Assert.assertEquals(numV, ((long) (t.V().count().next())));
        propertiesOnVertex = t.V().valueMap().next();
        valuesOnP = ((List) (propertiesOnVertex.values().iterator().next()));
        Assert.assertEquals(numProps, valuesOnP.size());
    }
}

