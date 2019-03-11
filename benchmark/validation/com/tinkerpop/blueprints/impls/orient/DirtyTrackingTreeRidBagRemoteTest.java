package com.tinkerpop.blueprints.impls.orient;


import OGlobalConfiguration.RID_BAG_EMBEDDED_TO_SBTREEBONSAI_THRESHOLD;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.orientechnologies.orient.server.OServer;
import com.tinkerpop.blueprints.Vertex;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tglman on 04/05/16.
 */
public class DirtyTrackingTreeRidBagRemoteTest {
    private OServer server;

    private String serverHome;

    private String oldOrientDBHome;

    @Test
    public void test() {
        RID_BAG_EMBEDDED_TO_SBTREEBONSAI_THRESHOLD.setValue(RID_BAG_EMBEDDED_TO_SBTREEBONSAI_THRESHOLD.getDefValue());
        final int max = (RID_BAG_EMBEDDED_TO_SBTREEBONSAI_THRESHOLD.getValueAsInteger()) * 2;
        OrientGraph graph = new OrientGraph(("remote:localhost:3064/" + (DirtyTrackingTreeRidBagRemoteTest.class.getSimpleName())), "root", "root");
        try {
            graph.getRawGraph().declareIntent(new OIntentMassiveInsert());
            graph.createEdgeType("Edge");
            OIdentifiable oneVertex = null;
            Map<Object, Vertex> vertices = new HashMap<Object, Vertex>();
            for (int i = 0; i < max; i++) {
                Vertex v = graph.addVertex("class:V");
                v.setProperty("key", ("foo" + i));
                graph.commit();
                vertices.put(v.getProperty("key"), v);
                if (i == ((max / 2) + 1))
                    oneVertex = getIdentity();

            }
            graph.commit();
            // Add the edges
            for (int i = 0; i < max; i++) {
                String codeUCD1 = "foo" + i;
                // Take the first vertex
                Vertex med1 = ((Vertex) (vertices.get(codeUCD1)));
                // For the 2nd term
                for (int j = 0; j < max; j++) {
                    String key = "foo" + j;
                    // Take the second vertex
                    Vertex med2 = ((Vertex) (vertices.get(key)));
                    // ((OrientVertex)med2).getRecord().reload();
                    OrientEdge eInteraction = graph.addEdge(null, med1, med2, "Edge");
                    Assert.assertNotNull(graph.getRawGraph().getTransaction().getRecordEntry(getIdentity()));
                }
                // COMMIT
                graph.commit();
            }
            graph.getRawGraph().getLocalCache().clear();
            OrientVertex vertex = graph.getVertex(oneVertex);
            Assert.assertEquals(new com.tinkerpop.gremlin.java.GremlinPipeline<Vertex, Long>().start(vertex).in("Edge").count(), max);
        } finally {
            graph.shutdown();
        }
    }
}

