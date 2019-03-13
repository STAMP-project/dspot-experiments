package com.tinkerpop.blueprints.impls.orient;


import com.orientechnologies.orient.server.OServer;
import com.tinkerpop.blueprints.GraphQueryTestSuite;
import com.tinkerpop.blueprints.KeyIndexableGraphTestSuite;
import com.tinkerpop.blueprints.VertexQueryTestSuite;
import com.tinkerpop.blueprints.impls.GraphTest;
import com.tinkerpop.blueprints.util.io.gml.GMLReaderTestSuite;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLReaderTestSuite;
import com.tinkerpop.blueprints.util.io.graphson.GraphSONReaderTestSuite;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 *
 *
 * @author Andrey Lomakin (a.lomakin-at-orientdb.com)
 * @since 2/6/14
 */
@RunWith(JUnit4.class)
public class OrientGraphNoTxRemoteTest extends GraphTest {
    private static final String serverPort = System.getProperty("orient.server.port", "3080");

    private static OServer server;

    private static String oldOrientDBHome;

    private static String serverHome;

    private Map<String, OrientGraphNoTx> currentGraphs = new HashMap<String, OrientGraphNoTx>();

    private Map<String, OrientGraphFactory> graphFactories = new HashMap<String, OrientGraphFactory>();

    // testing only those suites that are read-only
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
    public void testKeyIndexableGraphTestSuite() throws Exception {
        stopWatch();
        doTestSuite(new KeyIndexableGraphTestSuite(this));
        printTestPerformance("KeyIndexableGraphTestSuite", stopWatch());
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
}

