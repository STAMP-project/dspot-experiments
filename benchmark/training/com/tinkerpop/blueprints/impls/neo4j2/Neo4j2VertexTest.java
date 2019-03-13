package com.tinkerpop.blueprints.impls.neo4j2;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author mh
 * @since 08.01.14
 */
public class Neo4j2VertexTest {
    private Neo4j2Graph graph;

    @Test
    public void testVertexLabels() throws Exception {
        Neo4j2Vertex vertex = graph.addVertex(null);
        vertex.addLabel("Label");
        Assert.assertEquals(Arrays.asList("Label"), vertex.getLabels());
        vertex.addLabel("Label2");
        Assert.assertEquals(Arrays.asList("Label", "Label2"), vertex.getLabels());
        vertex.removeLabel("Label2");
        Assert.assertEquals(Arrays.asList("Label"), vertex.getLabels());
        vertex.removeLabel("Label");
        Assert.assertEquals(Arrays.<String>asList(), vertex.getLabels());
    }
}

