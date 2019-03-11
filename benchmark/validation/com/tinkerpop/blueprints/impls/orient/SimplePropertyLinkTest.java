package com.tinkerpop.blueprints.impls.orient;


import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tglman on 28/04/16.
 */
public class SimplePropertyLinkTest {
    private OrientGraph graph;

    @Test
    public void testSimplePropertyLink() {
        OrientVertex v1 = graph.addVertex(null);
        OrientVertex v2 = graph.addVertex(null);
        v1.setProperty("link", v2);
        v1.save();
        v2.save();
        graph.commit();
        graph.getRawGraph().getLocalCache().clear();
        Assert.assertTrue(getIdentity().isPersistent());
    }
}

