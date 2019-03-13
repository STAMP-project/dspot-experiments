package com.orientechnologies.orient.graph.sql;


import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import org.junit.Assert;
import org.junit.Test;


public class OutInChainTest {
    @Test
    public void t() {
        OrientGraph graph = new OrientGraph(("memory:" + (OutInChainTest.class.getSimpleName())), "admin", "admin");
        Vertex vUser = graph.addVertex("class:User");
        Vertex vCar = graph.addVertex("class:Car");
        graph.addEdge("class:Owns", vUser, vCar, null);
        graph.commit();
        Iterable<Vertex> res = graph.command(new OCommandSQL("select expand( out('Owns') ) from User")).execute();
        Assert.assertTrue(res.iterator().hasNext());
        Assert.assertEquals("Car", res.iterator().next().getProperty("@class").toString());
        Iterable<Vertex> resEdge = graph.command(new OCommandSQL("select expand( inE('Owns') ) from Car")).execute();
        Assert.assertTrue(resEdge.iterator().hasNext());
        // when out('Owns') is executed we have Car vertex (see above)
        // after that inE('Owns') should return Owns edge (see above)
        // but test fails
        resEdge = graph.command(new OCommandSQL("select expand( out('Owns').inE('Owns') ) from User")).execute();
        Assert.assertTrue(resEdge.iterator().hasNext());// assertion error here

        graph.shutdown();
    }

    @Test
    public void testMultipleLabels() {
        // issue #5359
        OrientGraph graph = new OrientGraph(("memory:" + (OutInChainTest.class.getSimpleName())), "admin", "admin");
        graph.command(new OCommandSQL("create vertex V1 set name = '1'")).execute();
        graph.command(new OCommandSQL("create vertex V1 set name = '2'")).execute();
        graph.command(new OCommandSQL("create vertex V1 set name = '3'")).execute();
        graph.command(new OCommandSQL("create edge E1 from (select from V1 where name = '1') to (select from V1 where name = '2')")).execute();
        graph.command(new OCommandSQL("create edge E2 from (select from V1 where name = '1') to (select from V1 where name = '3')")).execute();
        Iterable result = graph.command(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<com.tinkerpop.blueprints.impls.orient.OrientEdge>("select expand(outE('E1').out.outE('E1', 'E2')) from V1 where name = '1'")).execute();
        int count = 0;
        for (Object e : result) {
            count++;
        }
        Assert.assertEquals(2, count);
        // result = graph.command(new OSQLSynchQuery<OrientEdge>("select outE('E1').out.outE('E1', 'E2') from V1 where name = '1'"))
        // .execute();
        // iterator = result.iterator();
        // 
        // assertTrue(iterator.hasNext());
        // iterator.next();
        // assertTrue(iterator.hasNext());
    }
}

