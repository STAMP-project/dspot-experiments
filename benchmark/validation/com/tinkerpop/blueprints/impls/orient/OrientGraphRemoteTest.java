package com.tinkerpop.blueprints.impls.orient;


import Direction.IN;
import Direction.OUT;
import com.orientechnologies.orient.server.OServer;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrey Lomakin (a.lomakin-at-orientdb.com)
 * @since 2/6/14
 */
public abstract class OrientGraphRemoteTest extends OrientGraphTest {
    private static final String serverPort = System.getProperty("orient.server.port", "3080");

    private static OServer server;

    private static String oldOrientDBHome;

    private static String serverHome;

    private Map<String, OrientGraphFactory> graphFactories = new HashMap<String, OrientGraphFactory>();

    @Test
    public void testDeleteAndAddNewEdge() {
        OrientGraph graph = ((OrientGraph) (generateGraph()));
        try {
            OrientVertex v1 = graph.addTemporaryVertex("Test1V");
            v1.getRecord().field("name", "v1");
            v1.save();
            OrientVertex v2 = graph.addTemporaryVertex("Test2V");
            v2.getRecord().field("name", "v2");
            v2.save();
            graph.commit();
            Assert.assertTrue(v1.getIdentity().isPersistent());
            Assert.assertTrue(v2.getIdentity().isPersistent());
            for (int i = 0; i < 5; i++) {
                System.out.println(i);
                // Remove all edges
                for (Edge edge : v1.getEdges(OUT, "TestE")) {
                    edge.remove();
                }
                // Add new edge
                v1.addEdge("TestE", v2);
                graph.commit();
                Assert.assertEquals(v2.getId(), v1.getVertices(OUT, "TestE").iterator().next().getId());
                Assert.assertEquals(v1.getId(), v2.getVertices(IN, "TestE").iterator().next().getId());
            }
            graph.shutdown();
        } finally {
            dropGraph("graph");
        }
    }
}

