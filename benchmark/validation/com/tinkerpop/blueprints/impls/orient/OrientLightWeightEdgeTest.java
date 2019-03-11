package com.tinkerpop.blueprints.impls.orient;


import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.Edge;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;


public class OrientLightWeightEdgeTest {
    private OrientGraph graph;

    @Test
    public void testLiteweightToEavyMigration() throws Exception {
        graph.setUseLightweightEdges(true);
        graph.setAutoScaleEdgeType(true);
        OrientVertex vertex = graph.addVertex(null);
        OrientVertex vertex2 = graph.addVertex(null);
        vertex.addEdge("friend", vertex2);
        Object id = vertex.getId();
        graph.commit();
        // This shouldn't be done, i do it for ensure that the data is reloaded from the db, because the issue was after reload.
        graph.getRawGraph().getLocalCache().clear();
        OrientVertex vertexPrev = graph.getVertex(id);
        OrientVertex vertex3 = graph.addVertex(null);
        vertexPrev.addEdge("friend", vertex3);
        graph.commit();
    }

    @Test
    public void testDeleteVertex() throws Exception {
        graph.setUseLightweightEdges(true);
        OrientVertex vertex = graph.addVertex(null);
        OrientVertex vertex2 = graph.addVertex(null);
        Edge edge = vertex.addEdge("testDeleteVertex", vertex2);
        graph.commit();
        ORID vertexId = vertex.getIdentity();
        vertex = null;
        Iterable result = graph.command(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery(("SELECT FROM " + vertexId))).execute();
        Iterator iterator = result.iterator();
        Assert.assertTrue(iterator.hasNext());
        OrientVertex item = ((OrientVertex) (iterator.next()));
        ODocument doc = ((ODocument) (item.rawElement));
        Object fieldVal = doc.rawField("out_testDeleteVertex");
        Assert.assertTrue((fieldVal instanceof Iterable));
        Assert.assertTrue(((Iterable) (fieldVal)).iterator().hasNext());
        graph.commit();
        graph.command(new com.orientechnologies.orient.core.sql.OCommandSQL(("DELETE VERTEX " + (vertex2.getIdentity())))).execute();
        graph.commit();
        result = graph.command(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery(("SELECT FROM " + vertexId))).execute();
        iterator = result.iterator();
        Assert.assertTrue(iterator.hasNext());
        item = ((OrientVertex) (iterator.next()));
        doc = ((ODocument) (item.rawElement));
        fieldVal = doc.rawField("out_testDeleteVertex");
        Assert.assertTrue((fieldVal instanceof Iterable));
        Assert.assertFalse(((Iterable) (fieldVal)).iterator().hasNext());
    }
}

