package com.orientechnologies.orient.graph.blueprints;


import com.orientechnologies.orient.core.exception.OConcurrentModificationException;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.junit.Assert;
import org.junit.Test;


public class OrientTestsManualTxTest {
    private static final String STORAGE_ENGINE = "memory";

    private static final String DATABASE_URL = ((OrientTestsManualTxTest.STORAGE_ENGINE) + ":") + (OrientTestsManualTxTest.class.getSimpleName());

    private static final String PROPERTY_NAME = "pn";

    OrientGraphFactory graphFactory;

    OrientGraph graph;

    @Test
    public void vertexObjectsAreInSyncWithMultipleVertexObjects() {
        final int firstValue = 0;
        final int secondValue = 1;
        OrientGraph graph = graphFactory.getTx();
        graph.begin();
        OrientVertex firstVertexHandle = graph.addVertex(null, OrientTestsManualTxTest.PROPERTY_NAME, firstValue);
        graph.commit();
        triggerException(graph);
        Object recordId = firstVertexHandle.getId();
        graph.begin();
        Vertex secondVertexHandle = graph.getVertex(recordId);
        secondVertexHandle.setProperty(OrientTestsManualTxTest.PROPERTY_NAME, secondValue);
        graph.commit();
        Assert.assertEquals(("Both queries should return " + secondValue), ((Integer) (secondVertexHandle.getProperty(OrientTestsManualTxTest.PROPERTY_NAME))), firstVertexHandle.getProperty(OrientTestsManualTxTest.PROPERTY_NAME));
    }

    @Test
    public void noOConcurrentModificationExceptionWithMultipleVertexObjects() {
        final int firstValue = 0;
        final int secondValue = 1;
        final int thirdValue = 2;
        graph.begin();
        OrientVertex firstVertexHandle = graph.addVertex(null, OrientTestsManualTxTest.PROPERTY_NAME, firstValue);
        graph.commit();
        triggerException(graph);
        Object recordId = firstVertexHandle.getId();
        graph.begin();
        Vertex secondVertexHandle = graph.getVertex(recordId);
        secondVertexHandle.setProperty(OrientTestsManualTxTest.PROPERTY_NAME, secondValue);
        graph.commit();
        try {
            firstVertexHandle.setProperty(OrientTestsManualTxTest.PROPERTY_NAME, thirdValue);
        } catch (OConcurrentModificationException o) {
            Assert.fail("OConcurrentModificationException was thrown");
        }
    }

    @Test
    public void noOConcurrentModificationExceptionSettingAFixedValueWithMultipleVertexObjects() {
        final int fixedValue = 113;
        graph.begin();
        OrientVertex firstVertexHandle = graph.addVertex(null, OrientTestsManualTxTest.PROPERTY_NAME, fixedValue);
        graph.commit();
        triggerException(graph);
        Object recordId = firstVertexHandle.getId();
        Vertex secondVertexHandle = graph.getVertex(recordId);
        secondVertexHandle.setProperty(OrientTestsManualTxTest.PROPERTY_NAME, fixedValue);
        try {
            firstVertexHandle.setProperty(OrientTestsManualTxTest.PROPERTY_NAME, fixedValue);
        } catch (OConcurrentModificationException o) {
            Assert.fail("OConcurrentModificationException was thrown");
        }
    }
}

