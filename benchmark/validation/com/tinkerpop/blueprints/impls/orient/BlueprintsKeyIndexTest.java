package com.tinkerpop.blueprints.impls.orient;


import com.tinkerpop.blueprints.Vertex;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class BlueprintsKeyIndexTest {
    private static final String ROOT_NODE_NAME = "rootNode";

    private static final String KEY_NAME = "name";

    @Test
    public void test_with_createKeyIndex() throws Exception {
        final OrientGraph graph = new OrientGraph(("memory:" + (BlueprintsKeyIndexTest.class.getSimpleName())));
        graph.setWarnOnForceClosingTx(false);
        try {
            /* create key index */
            graph.createKeyIndex(BlueprintsKeyIndexTest.KEY_NAME, Vertex.class);
            {
                final Vertex v = graph.addVertex(null);
                v.setProperty(BlueprintsKeyIndexTest.KEY_NAME, BlueprintsKeyIndexTest.ROOT_NODE_NAME);/* as key index */

                graph.commit();
                final Object rootVertexId = v.getId();
                Assert.assertNotNull(rootVertexId);
            }
            /* get rootNode */
            final List<Vertex> rootNodes = BlueprintsKeyIndexTest.toArrayList(graph.getVertices(BlueprintsKeyIndexTest.KEY_NAME, BlueprintsKeyIndexTest.ROOT_NODE_NAME));
            Assert.assertEquals(1, rootNodes.size());// ##########

            // java.lang.AssertionError:
            // expected:<1> but was:<0>
        } finally {
            graph.drop();
        }
    }

    @Test
    public void test_without_createKeyIndex() throws Exception {
        final OrientGraph graph = new OrientGraph(("memory:" + (BlueprintsKeyIndexTest.class.getSimpleName())));
        graph.setWarnOnForceClosingTx(false);
        try {
            /* create key index */
            // graph.createKeyIndex("name", Vertex.class);
            {
                final Vertex v = graph.addVertex(null);
                v.setProperty(BlueprintsKeyIndexTest.KEY_NAME, BlueprintsKeyIndexTest.ROOT_NODE_NAME);/* as key index */

                graph.commit();
                final Object rootVertexId = v.getId();
                Assert.assertNotNull(rootVertexId);
            }
            /* get rootNode */
            final List<Vertex> rootNodes = BlueprintsKeyIndexTest.toArrayList(graph.getVertices(BlueprintsKeyIndexTest.KEY_NAME, BlueprintsKeyIndexTest.ROOT_NODE_NAME));
            Assert.assertEquals(1, rootNodes.size());// ########## no problem

        } finally {
            graph.drop();
        }
    }

    @Test
    public void test_without_createKeyIndexVertexType() throws Exception {
        final OrientGraph graph = new OrientGraph(("memory:" + (BlueprintsKeyIndexTest.class.getSimpleName())));
        graph.setWarnOnForceClosingTx(false);
        graph.createVertexType("Test");
        graph.createVertexType("Test1");
        try {
            /* create key index */
            // graph.createKeyIndex("name", Vertex.class);
            {
                Vertex v = graph.addVertex("class:Test");
                v.setProperty(BlueprintsKeyIndexTest.KEY_NAME, BlueprintsKeyIndexTest.ROOT_NODE_NAME);/* as key index */

                v = graph.addVertex("class:Test1");
                v.setProperty(BlueprintsKeyIndexTest.KEY_NAME, BlueprintsKeyIndexTest.ROOT_NODE_NAME);
                v = graph.addVertex("class:Test1");
                v.setProperty(BlueprintsKeyIndexTest.KEY_NAME, "Fail");
                graph.commit();
                final Object rootVertexId = v.getId();
                Assert.assertNotNull(rootVertexId);
            }
            /* get rootNode */
            final List<Vertex> rootNodes = BlueprintsKeyIndexTest.toArrayList(graph.getVertices(("Test." + (BlueprintsKeyIndexTest.KEY_NAME)), BlueprintsKeyIndexTest.ROOT_NODE_NAME));
            Assert.assertEquals(1, rootNodes.size());// ########## no problem

        } finally {
            graph.drop();
        }
    }
}

