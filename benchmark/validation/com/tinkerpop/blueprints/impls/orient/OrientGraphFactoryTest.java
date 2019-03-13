package com.tinkerpop.blueprints.impls.orient;


import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 *
 *
 * @author Luca Garulli (l.garulli--(at)--orientdb.com) (http://orientdb.com)
 */
@RunWith(JUnit4.class)
public class OrientGraphFactoryTest {
    @Test
    public void createTxPoolNestedCreations() {
        OrientGraph graph = new OrientGraph("memory:testPool");
        graph.shutdown();
        OrientGraphFactory factory = new OrientGraphFactory("memory:testPool");
        factory.setupPool(5, 10);
        OrientBaseGraph g = factory.getTx();
        Assert.assertEquals(g.getClass(), OrientGraph.class);
        Assert.assertSame(g, OrientBaseGraph.getActiveGraph());
        OrientBaseGraph g1 = factory.getTx();
        Assert.assertSame(g1, OrientBaseGraph.getActiveGraph());
        g1.shutdown();
        Assert.assertSame(g, OrientBaseGraph.getActiveGraph());
        g.shutdown();
        Assert.assertNull(OrientBaseGraph.getActiveGraph());
        factory.close();
    }

    @Test
    public void createNoTx() {
        OrientGraphFactory factory = new OrientGraphFactory("memory:testPool");
        OrientBaseGraph g = factory.getNoTx();
        Assert.assertSame(g, OrientBaseGraph.getActiveGraph());
        Assert.assertEquals(g.getClass(), OrientGraphNoTx.class);
        Assert.assertEquals(g.getRawGraph().getClass().getSuperclass(), ODatabaseDocumentTx.class);
        g.shutdown();
        Assert.assertNull(OrientBaseGraph.getActiveGraph());
        factory.close();
    }

    @Test
    public void createNoTxPool() {
        OrientGraph graph = new OrientGraph("memory:testPool");
        graph.shutdown();
        OrientGraphFactory factory = new OrientGraphFactory("memory:testPool");
        factory.setupPool(5, 10);
        OrientBaseGraph g = factory.getNoTx();
        Assert.assertSame(g, OrientBaseGraph.getActiveGraph());
        Assert.assertEquals(g.getClass(), OrientGraphNoTx.class);
        g.shutdown();
        Assert.assertNull(OrientBaseGraph.getActiveGraph());
        factory.close();
    }

    @Test
    public void dynamicType() {
        OrientGraphFactory factory = new OrientGraphFactory("memory:testPool");
        OrientBaseGraph g = factory.getTx();
        Assert.assertEquals(g.getClass(), OrientGraph.class);
        Assert.assertEquals(g.getRawGraph().getClass().getSuperclass(), ODatabaseDocumentTx.class);
        g.shutdown();
        g = factory.getNoTx();
        Assert.assertEquals(g.getClass(), OrientGraphNoTx.class);
        Assert.assertEquals(g.getRawGraph().getClass().getSuperclass(), ODatabaseDocumentTx.class);
        g.shutdown();
        factory.close();
    }

    @Test
    public void releaseThreadLocal() {
        OrientGraphFactory factory = new OrientGraphFactory("memory:testPool");
        OrientBaseGraph createGraph = factory.getTx();
        createGraph.shutdown();
        factory.setupPool(10, 20);
        OrientBaseGraph g = factory.getTx();
        g.addVertex(null);
        g.commit();
        g.shutdown();
        Assert.assertNull(ODatabaseRecordThreadLocal.instance().getIfDefined());
        factory.close();
    }

    @Test
    public void textReqTx() {
        final OrientGraphFactory gfactory = new OrientGraphFactory("memory:testPool");
        gfactory.setRequireTransaction(false);
        gfactory.declareIntent(new OIntentMassiveInsert());
        OrientGraph g = gfactory.getTx();
        OrientVertex v1 = g.addVertex(null);
        OrientVertex v2 = g.addVertex(null);
        v1.addEdge("E", v2);
        g.shutdown();
        gfactory.close();
    }

    @Test
    public void testCreateGraphByOrientGraphFactory() {
        OrientGraphFactory factory = new OrientGraphFactory("memory:test01").setupPool(1, 10);
        OrientGraph graph = factory.getTx();
        Assert.assertNotNull(graph);
        graph.shutdown();
    }
}

