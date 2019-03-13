package com.orientechnologies.orient.graph;


import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.storage.impl.local.OStorageRecoverEventListener;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OGraphRepair;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import org.junit.Assert;
import org.junit.Test;


public class GraphRecoveringTest {
    private class TestListener implements OStorageRecoverEventListener {
        public long scannedEdges = 0;

        public long removedEdges = 0;

        public long scannedVertices = 0;

        public long scannedLinks = 0;

        public long removedLinks = 0;

        public long repairedVertices = 0;

        @Override
        public void onScannedEdge(ODocument edge) {
            (scannedEdges)++;
        }

        @Override
        public void onRemovedEdge(ODocument edge) {
            (removedEdges)++;
        }

        @Override
        public void onScannedVertex(ODocument vertex) {
            (scannedVertices)++;
        }

        @Override
        public void onScannedLink(OIdentifiable link) {
            (scannedLinks)++;
        }

        @Override
        public void onRemovedLink(OIdentifiable link) {
            (removedLinks)++;
        }

        @Override
        public void onRepairedVertex(ODocument vertex) {
            (repairedVertices)++;
        }
    }

    @Test
    public void testRecoverPerfectGraphNonLW() {
        final OrientBaseGraph g = new OrientGraphNoTx("memory:testRecoverPerfectGraphNonLW");
        try {
            init(g, false);
            final GraphRecoveringTest.TestListener eventListener = new GraphRecoveringTest.TestListener();
            new OGraphRepair().setEventListener(eventListener).repair(g, null, null);
            Assert.assertEquals(eventListener.scannedEdges, 3);
            Assert.assertEquals(eventListener.removedEdges, 0);
            Assert.assertEquals(eventListener.scannedVertices, 3);
            Assert.assertEquals(eventListener.scannedLinks, 6);
            Assert.assertEquals(eventListener.removedLinks, 0);
            Assert.assertEquals(eventListener.repairedVertices, 0);
        } finally {
            g.shutdown();
        }
    }

    @Test
    public void testRecoverPerfectGraphLW() {
        final OrientBaseGraph g = new OrientGraphNoTx("memory:testRecoverPerfectGraphLW");
        try {
            init(g, true);
            final GraphRecoveringTest.TestListener eventListener = new GraphRecoveringTest.TestListener();
            new OGraphRepair().setEventListener(eventListener).repair(g, null, null);
            Assert.assertEquals(eventListener.scannedEdges, 0);
            Assert.assertEquals(eventListener.removedEdges, 0);
            Assert.assertEquals(eventListener.scannedVertices, 3);
            Assert.assertEquals(eventListener.scannedLinks, 6);
            Assert.assertEquals(eventListener.removedLinks, 0);
            Assert.assertEquals(eventListener.repairedVertices, 0);
        } finally {
            g.shutdown();
        }
    }

    @Test
    public void testRecoverBrokenGraphAllEdges() {
        final OrientBaseGraph g = new OrientGraphNoTx("memory:testRecoverBrokenGraphAllEdges");
        try {
            init(g, false);
            for (Edge e : g.getEdges()) {
                getRecord().removeField("out");
                getRecord().save();
            }
            final GraphRecoveringTest.TestListener eventListener = new GraphRecoveringTest.TestListener();
            new OGraphRepair().setEventListener(eventListener).repair(g, null, null);
            Assert.assertEquals(eventListener.scannedEdges, 3);
            Assert.assertEquals(eventListener.removedEdges, 3);
            Assert.assertEquals(eventListener.scannedVertices, 3);
            Assert.assertEquals(eventListener.scannedLinks, 6);
            Assert.assertEquals(eventListener.removedLinks, 6);
            Assert.assertEquals(eventListener.repairedVertices, 3);
        } finally {
            g.shutdown();
        }
    }

    @Test
    public void testRecoverBrokenGraphLinksInVerticesNonLW() {
        final OrientBaseGraph g = new OrientGraphNoTx("memory:testRecoverBrokenGraphLinksInVerticesNonLW");
        try {
            init(g, false);
            for (Vertex v : g.getVertices()) {
                for (String f : getRecord().fieldNames()) {
                    if (f.startsWith("out_"))
                        getRecord().removeField(f);

                }
            }
            final GraphRecoveringTest.TestListener eventListener = new GraphRecoveringTest.TestListener();
            new OGraphRepair().setEventListener(eventListener).repair(g, null, null);
            Assert.assertEquals(eventListener.scannedEdges, 3);
            Assert.assertEquals(eventListener.removedEdges, 3);
            Assert.assertEquals(eventListener.scannedVertices, 3);
            Assert.assertEquals(eventListener.scannedLinks, 3);
            Assert.assertEquals(eventListener.removedLinks, 3);
            Assert.assertEquals(eventListener.repairedVertices, 3);
        } finally {
            g.shutdown();
        }
    }

    @Test
    public void testRecoverBrokenGraphLinksInVerticesLW() {
        final OrientBaseGraph g = new OrientGraphNoTx("memory:testRecoverBrokenGraphLinksInVerticesLW");
        try {
            init(g, true);
            for (Vertex v : g.getVertices()) {
                final ODocument record = ((com.tinkerpop.blueprints.impls.orient.OrientVertex) (v)).getRecord();
                int key = v.getProperty("key");
                if (key == 0)
                    record.field("out_", record);
                else
                    if (key == 1)
                        record.field("in_E1", new ORecordId(100, 200));
                    else
                        if (key == 2)
                            record.field("out_E2", record);



                record.save();
            }
            final GraphRecoveringTest.TestListener eventListener = new GraphRecoveringTest.TestListener();
            new OGraphRepair().setEventListener(eventListener).repair(g, null, null);
            Assert.assertEquals(eventListener.scannedEdges, 0);
            Assert.assertEquals(eventListener.removedEdges, 0);
            Assert.assertEquals(eventListener.scannedVertices, 3);
            Assert.assertEquals(eventListener.scannedLinks, 7);
            Assert.assertEquals(eventListener.removedLinks, 5);
            Assert.assertEquals(eventListener.repairedVertices, 3);
        } finally {
            g.shutdown();
        }
    }
}

