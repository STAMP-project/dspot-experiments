package com.orientechnologies.orient.graph.blueprints;


import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Sergey Sitnikov
 * @since 16/03/16
 */
public class NestedTxTest {
    @Test
    public void testNestedTx() throws InterruptedException, ExecutionException {
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        final OrientGraphFactory factory = new OrientGraphFactory("memory:NestedTxTest.testNestedTx", "admin", "admin").setupPool(2, 10);
        factory.setAutoStartTx(false);
        final OrientGraph graph = factory.getTx();
        graph.createVertexType("A");
        graph.createVertexType("B");
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                final OrientGraph graph = NestedTxTest.getGraph(factory);
                graph.begin();
                graph.addVertex("class:A");
            }
        }).get();
        Assert.assertTrue("vertex A should not exist before top-level commit", (!(NestedTxTest.vertexExists(graph, "A"))));
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                final OrientGraph graph = NestedTxTest.getGraph(factory);
                graph.begin();
                graph.addVertex("class:B");
            }
        }).get();
        Assert.assertTrue("vertex B should not exist before top-level commit", (!(NestedTxTest.vertexExists(graph, "B"))));
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                final OrientGraph graph = NestedTxTest.getGraph(factory);
                graph.commit();
            }
        }).get();
        Assert.assertTrue("vertex A should not exist before top-level commit", (!(NestedTxTest.vertexExists(graph, "A"))));
        Assert.assertTrue("vertex B should not exist before top-level commit", (!(NestedTxTest.vertexExists(graph, "B"))));
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                final OrientGraph graph = NestedTxTest.getGraph(factory);
                graph.commit();
            }
        }).get();
        Assert.assertTrue("vertex A should exist after top-level commit", NestedTxTest.vertexExists(graph, "A"));
        Assert.assertTrue("vertex B should exist after top-level commit", NestedTxTest.vertexExists(graph, "B"));
    }
}

