package com.tinkerpop.blueprints.impls.orient;


import com.tinkerpop.blueprints.impls.GraphTest;
import com.tinkerpop.blueprints.util.io.gml.GMLReaderTestSuite;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLReaderTestSuite;
import com.tinkerpop.blueprints.util.io.graphson.GraphSONReaderTestSuite;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;


/**
 * Test suite for OrientDB graph implementation.
 *
 * @author Luca Garulli (l.garulli--(at)--orientdb.com) (http://orientdb.com)
 */
public abstract class OrientGraphTest extends GraphTest {
    protected Map<String, OrientGraph> currentGraphs = new HashMap<String, OrientGraph>();

    @Test
    public void testVertexTestSuite() throws Exception {
        stopWatch();
        doTestSuite(new VertexTestSuite(this));
        printTestPerformance("VertexTestSuite", stopWatch());
    }

    @Test
    public void testEdgeTestSuite() throws Exception {
        stopWatch();
        doTestSuite(new EdgeTestSuite(this));
        printTestPerformance("EdgeTestSuite", stopWatch());
    }

    @Test
    public void testGraphTestSuite() throws Exception {
        stopWatch();
        doTestSuite(new GraphTestSuite(this));
        printTestPerformance("GraphTestSuite", stopWatch());
    }

    @Test
    public void testVertexQueryTestSuite() throws Exception {
        stopWatch();
        doTestSuite(new VertexQueryTestSuite(this));
        printTestPerformance("VertexQueryTestSuite", stopWatch());
    }

    @Test
    public void testGraphQueryTestSuite() throws Exception {
        stopWatch();
        doTestSuite(new GraphQueryTestSuite(this));
        printTestPerformance("GraphQueryTestSuite", stopWatch());
    }

    @Test
    public void testIndexableGraphTestSuite() throws Exception {
        stopWatch();
        doTestSuite(new IndexableGraphTestSuite(this));
        printTestPerformance("IndexableGraphTestSuite", stopWatch());
    }

    @Test
    public void testIndexTestSuite() throws Exception {
        stopWatch();
        doTestSuite(new IndexTestSuite(this));
        printTestPerformance("IndexTestSuite", stopWatch());
    }

    @Test
    public void testKeyIndexableGraphTestSuite() throws Exception {
        stopWatch();
        doTestSuite(new KeyIndexableGraphTestSuite(this));
        printTestPerformance("KeyIndexableGraphTestSuite", stopWatch());
    }

    @Test
    public void testTransactionalGraphTestSuite() throws Exception {
        try {
            stopWatch();
            doTestSuite(new TransactionalGraphTestSuite(this));
            printTestPerformance("TransactionGraphTestSuite", stopWatch());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    public void testGraphMLReaderTestSuite() throws Exception {
        stopWatch();
        doTestSuite(new GraphMLReaderTestSuite(this));
        printTestPerformance("GraphMLReaderTestSuite", stopWatch());
    }

    @Test
    public void testGraphSONReaderTestSuite() throws Exception {
        stopWatch();
        doTestSuite(new GraphSONReaderTestSuite(this));
        printTestPerformance("GraphSONReaderTestSuite", stopWatch());
    }

    @Test
    public void testGMLReaderTestSuite() throws Exception {
        stopWatch();
        doTestSuite(new GMLReaderTestSuite(this));
        printTestPerformance("GMLReaderTestSuite", stopWatch());
    }

    // 
    // orientdb specific test
    // 
    @Test
    public void testOrientGraphSpecificTestSuite() throws Exception {
        stopWatch();
        doTestSuite(new OrientGraphSpecificTestSuite(this));
        printTestPerformance("OrientGraphSpecificTestSuite", stopWatch());
    }

    public static enum ENV {

        DEV,
        RELEASE,
        CI;}
}

