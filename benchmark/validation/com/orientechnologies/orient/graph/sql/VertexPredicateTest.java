package com.orientechnologies.orient.graph.sql;


import com.orientechnologies.orient.core.sql.filter.OSQLPredicate;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class VertexPredicateTest {
    private static OrientGraph graph;

    private static OrientVertex luca;

    private static OrientVertex bill;

    private static OrientVertex jay;

    private static OrientVertex steve;

    @Test
    public void testPredicate() throws Exception {
        Iterable<OrientVertex> p1 = ((Iterable<OrientVertex>) (VertexPredicateTest.luca.execute(new OSQLPredicate("out()"))));
        Assert.assertTrue(p1.iterator().hasNext());
        Assert.assertEquals(p1.iterator().next(), VertexPredicateTest.bill);
        Iterable<OrientVertex> p2 = ((Iterable<OrientVertex>) (VertexPredicateTest.luca.execute(new OSQLPredicate("out().out('Friend')"))));
        Assert.assertTrue(p2.iterator().hasNext());
        Assert.assertEquals(p2.iterator().next(), VertexPredicateTest.jay);
        Iterable<OrientVertex> p3 = ((Iterable<OrientVertex>) (VertexPredicateTest.luca.execute(new OSQLPredicate("out().out('Friend').out('Friend')"))));
        Assert.assertTrue(p3.iterator().hasNext());
        Assert.assertEquals(p3.iterator().next(), VertexPredicateTest.steve);
    }
}

