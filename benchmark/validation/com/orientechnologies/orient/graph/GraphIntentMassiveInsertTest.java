package com.orientechnologies.orient.graph;


import Direction.BOTH;
import Direction.IN;
import OType.INTEGER;
import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tglman on 08/07/16.
 */
public class GraphIntentMassiveInsertTest {
    @Test
    public void testIntent() {
        final OrientGraph graph = new OrientGraph("memory:default", false);
        graph.setUseLightweightEdges(true);
        graph.getRawGraph().declareIntent(new OIntentMassiveInsert());
        final OrientVertexType c1 = graph.createVertexType("C1");
        c1.createProperty("p1", INTEGER);
        graph.createEdgeType("parent");
        graph.begin();
        final OrientVertex first = graph.addVertex("class:C1");
        first.setProperty("p1", (-1));
        for (int i = 0; i < 10; i++) {
            final OrientVertex v = graph.addVertex("class:C1");
            v.setProperty("p1", i);
            first.addEdge("parent", v);
            // this search fills _source
            graph.command(new OSQLSynchQuery((("SELECT from V where p1='" + i) + "'"))).execute();
        }
        graph.commit();
        // here NPE will be thrown
        final Iterable<Edge> edges = first.getEdges(BOTH);
        Iterator<Edge> ter = edges.iterator();
        for (int i = 0; i < 10; i++) {
            Assert.assertTrue(ter.hasNext());
            Assert.assertEquals(ter.next().getVertex(IN).getProperty("p1"), ((Integer) (i)));
        }
    }
}

