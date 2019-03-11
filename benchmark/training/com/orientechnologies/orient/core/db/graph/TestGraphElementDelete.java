package com.orientechnologies.orient.core.db.graph;


import ODirection.OUT;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.OEdge;
import com.orientechnologies.orient.core.record.OVertex;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tglman on 20/02/17.
 */
public class TestGraphElementDelete {
    private OrientDB orientDB;

    private ODatabaseDocument database;

    @Test
    public void testDeleteVertex() {
        OVertex vertex = database.newVertex("V");
        OVertex vertex1 = database.newVertex("V");
        OEdge edge = vertex.addEdge(vertex1, "E");
        database.save(edge);
        database.delete(vertex);
        Assert.assertNull(database.load(edge.getIdentity()));
    }

    @Test
    public void testDeleteEdge() {
        OVertex vertex = database.newVertex("V");
        OVertex vertex1 = database.newVertex("V");
        OEdge edge = vertex.addEdge(vertex1, "E");
        database.save(edge);
        database.delete(edge);
        Assert.assertFalse(vertex.getEdges(OUT, "E").iterator().hasNext());
    }
}

