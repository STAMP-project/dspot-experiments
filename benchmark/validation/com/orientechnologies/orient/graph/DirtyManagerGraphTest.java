package com.orientechnologies.orient.graph;


import OGlobalConfiguration.RID_BAG_EMBEDDED_TO_SBTREEBONSAI_THRESHOLD;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.junit.Assert;
import org.junit.Test;


public class DirtyManagerGraphTest {
    @Test
    public void testLoopOfNew() {
        OrientGraph graph = new OrientGraph(("memory:" + (DirtyManagerGraphTest.class.getSimpleName())));
        try {
            graph.createEdgeType("next");
            OrientVertex vertex = graph.addVertex(null);
            OrientVertex vertex1 = graph.addVertex(null);
            OrientVertex vertex2 = graph.addVertex(null);
            OrientVertex vertex3 = graph.addVertex(null);
            OrientEdge edge1 = ((OrientEdge) (vertex.addEdge("next", vertex1)));
            OrientEdge edge2 = ((OrientEdge) (vertex1.addEdge("next", vertex2)));
            OrientEdge edge3 = ((OrientEdge) (vertex2.addEdge("next", vertex3)));
            OrientEdge edge4 = ((OrientEdge) (vertex3.addEdge("next", vertex)));
            graph.commit();
            Assert.assertTrue(vertex.getIdentity().isPersistent());
            Assert.assertTrue(vertex1.getIdentity().isPersistent());
            Assert.assertTrue(vertex2.getIdentity().isPersistent());
            Assert.assertTrue(vertex3.getIdentity().isPersistent());
            Assert.assertTrue(edge1.getIdentity().isPersistent());
            Assert.assertTrue(edge2.getIdentity().isPersistent());
            Assert.assertTrue(edge3.getIdentity().isPersistent());
            Assert.assertTrue(edge4.getIdentity().isPersistent());
        } finally {
            graph.drop();
        }
    }

    @Test
    public void testLoopOfNewTree() {
        OrientGraph graph = new OrientGraph(("memory:" + (DirtyManagerGraphTest.class.getSimpleName())));
        Object prev = RID_BAG_EMBEDDED_TO_SBTREEBONSAI_THRESHOLD.getValue();
        RID_BAG_EMBEDDED_TO_SBTREEBONSAI_THRESHOLD.setValue((-1));
        try {
            graph.createEdgeType("next");
            OrientVertex vertex = graph.addVertex(null);
            OrientVertex vertex1 = graph.addVertex(null);
            OrientVertex vertex2 = graph.addVertex(null);
            OrientVertex vertex3 = graph.addVertex(null);
            OrientEdge edge1 = ((OrientEdge) (vertex.addEdge("next", vertex1)));
            OrientEdge edge2 = ((OrientEdge) (vertex1.addEdge("next", vertex2)));
            OrientEdge edge3 = ((OrientEdge) (vertex2.addEdge("next", vertex3)));
            OrientEdge edge4 = ((OrientEdge) (vertex3.addEdge("next", vertex)));
            graph.commit();
            Assert.assertTrue(vertex.getIdentity().isPersistent());
            Assert.assertTrue(vertex1.getIdentity().isPersistent());
            Assert.assertTrue(vertex2.getIdentity().isPersistent());
            Assert.assertTrue(vertex3.getIdentity().isPersistent());
            Assert.assertTrue(edge1.getIdentity().isPersistent());
            Assert.assertTrue(edge2.getIdentity().isPersistent());
            Assert.assertTrue(edge3.getIdentity().isPersistent());
            Assert.assertTrue(edge4.getIdentity().isPersistent());
        } finally {
            RID_BAG_EMBEDDED_TO_SBTREEBONSAI_THRESHOLD.setValue(prev);
            graph.drop();
        }
    }
}

