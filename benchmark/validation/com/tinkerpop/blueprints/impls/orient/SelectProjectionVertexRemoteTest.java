package com.tinkerpop.blueprints.impls.orient;


import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.server.OServer;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;


public class SelectProjectionVertexRemoteTest {
    private OServer server;

    @Test
    public void test() {
        OrientGraph graph = new OrientGraph(("remote:localhost:3064/" + (SelectProjectionVertexRemoteTest.class.getSimpleName())));
        try {
            graph.createVertexType("VertA");
            graph.createVertexType("VertB");
            graph.createEdgeType("AtoB");
            OrientVertex root = graph.addVertex("class:VertA");
            graph.commit();
            for (int i = 0; i < 2; i++) {
                OrientVertex v = graph.addVertex("class:VertB");
                root.addEdge("AtoB", v);
            }
            graph.commit();
            String query = "SELECT $res as val LET $res = (SELECT @rid AS refId, out('AtoB') AS vertices FROM VertA) FETCHPLAN val:2";
            Iterable<OrientVertex> results = graph.command(new OCommandSQL(query)).execute();
            final Iterator<OrientVertex> iterator = results.iterator();
            Assert.assertTrue(iterator.hasNext());
            OrientVertex result = iterator.next();
            Iterable<OrientVertex> vertices = result.getProperty("val");
            for (OrientVertex vertex : vertices) {
                Assert.assertEquals(getIdentity(), root.getIdentity());
            }
        } finally {
            graph.shutdown();
        }
    }
}

