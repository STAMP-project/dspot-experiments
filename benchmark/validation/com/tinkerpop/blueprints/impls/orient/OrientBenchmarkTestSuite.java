package com.tinkerpop.blueprints.impls.orient;


import com.tinkerpop.blueprints.impls.GraphTest;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLReader;
import java.io.FileInputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 *
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
@RunWith(JUnit4.class)
public class OrientBenchmarkTestSuite extends TestSuite {
    private static final int TOTAL_RUNS = 1;

    public OrientBenchmarkTestSuite() {
    }

    public OrientBenchmarkTestSuite(final GraphTest graphTest) {
        super(graphTest);
    }

    @Test
    public void testOrientGraph() throws Exception {
        double totalTime = 0.0;
        if ((graphTest) == null)
            return;

        Graph graph = graphTest.generateGraph();
        // graph = new OrientBatchGraph((ODatabaseDocumentTx) ((OrientGraph) graph).getRawGraph());
        // GraphMLReader.inputGraph(graph, GraphMLReader.class.getResourceAsStream("graph-example-2.xml"));
        GraphMLReader.inputGraph(graph, new FileInputStream("/Users/luca/Downloads/graph-example-2.xml"));
        System.out.println(((("V: " + (getRawGraph().countClass("V"))) + " E: ") + (getRawGraph().countClass("E"))));
        graph.shutdown();
        for (int i = 0; i < (OrientBenchmarkTestSuite.TOTAL_RUNS); i++) {
            graph = graphTest.generateGraph();
            int counter = execute(graph);
            double currentTime = stopWatch();
            totalTime = totalTime + currentTime;
            BaseTest.printPerformance(graph.toString(), counter, "OrientGraph elements touched", currentTime);
            graph.shutdown();
        }
        BaseTest.printPerformance("OrientGraph", 1, "OrientGraph experiment average", (totalTime / ((double) (OrientBenchmarkTestSuite.TOTAL_RUNS))));
    }
}

