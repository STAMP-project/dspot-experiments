package com.orientechnologies.orient.graph.blueprints;


import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import org.junit.Assert;
import org.junit.Test;


public class GraphFunctionsTest {
    private static String DB_URL = "memory:" + (GraphFunctionsTest.class.getSimpleName());

    private static OrientGraph graph;

    private static Vertex v1;

    private static Vertex v2;

    private static Vertex v3;

    private static Edge e1;

    private static Edge e2;

    public GraphFunctionsTest() {
    }

    @Test
    public void testOut() {
        int found;
        // V1
        found = 0;
        for (Vertex v : ((Iterable<Vertex>) (GraphFunctionsTest.graph.command(new com.orientechnologies.orient.core.sql.OCommandSQL(("select expand( out() ) from " + (GraphFunctionsTest.v1.getId())))).execute())))
            found++;

        Assert.assertEquals(found, 2);
        found = 0;
        for (Vertex v : ((Iterable<Vertex>) (GraphFunctionsTest.graph.command(new com.orientechnologies.orient.core.sql.OCommandSQL(("select expand( out('SubEdge') ) from " + (GraphFunctionsTest.v1.getId())))).execute())))
            found++;

        Assert.assertEquals(found, 1);
        found = 0;
        for (Vertex v : ((Iterable<Vertex>) (GraphFunctionsTest.graph.command(new com.orientechnologies.orient.core.sql.OCommandSQL(("select expand( out('dddd') ) from " + (GraphFunctionsTest.v1.getId())))).execute())))
            found++;

        Assert.assertEquals(found, 0);
        // V2
        found = 0;
        for (Vertex v : ((Iterable<Vertex>) (GraphFunctionsTest.graph.command(new com.orientechnologies.orient.core.sql.OCommandSQL(("select expand( out() ) from " + (GraphFunctionsTest.v2.getId())))).execute())))
            found++;

        Assert.assertEquals(found, 0);
        // V3
        found = 0;
        for (Vertex v : ((Iterable<Vertex>) (GraphFunctionsTest.graph.command(new com.orientechnologies.orient.core.sql.OCommandSQL(("select expand( out() ) from " + (GraphFunctionsTest.v3.getId())))).execute())))
            found++;

        Assert.assertEquals(found, 0);
    }

    @Test
    public void testIn() {
        int found;
        // V1
        found = 0;
        for (Vertex v : ((Iterable<Vertex>) (GraphFunctionsTest.graph.command(new com.orientechnologies.orient.core.sql.OCommandSQL(("select expand( in() ) from " + (GraphFunctionsTest.v1.getId())))).execute())))
            found++;

        Assert.assertEquals(found, 0);
        // V2
        found = 0;
        for (Vertex v : ((Iterable<Vertex>) (GraphFunctionsTest.graph.command(new com.orientechnologies.orient.core.sql.OCommandSQL(("select expand( in() ) from " + (GraphFunctionsTest.v2.getId())))).execute())))
            found++;

        Assert.assertEquals(found, 1);
        found = 0;
        for (Vertex v : ((Iterable<Vertex>) (GraphFunctionsTest.graph.command(new com.orientechnologies.orient.core.sql.OCommandSQL(("select expand( in('SubEdge') ) from " + (GraphFunctionsTest.v2.getId())))).execute())))
            found++;

        Assert.assertEquals(found, 1);
        found = 0;
        for (Vertex v : ((Iterable<Vertex>) (GraphFunctionsTest.graph.command(new com.orientechnologies.orient.core.sql.OCommandSQL(("select expand( in('dddd') ) from " + (GraphFunctionsTest.v2.getId())))).execute())))
            found++;

        Assert.assertEquals(found, 0);
        // V3
        found = 0;
        for (Vertex v : ((Iterable<Vertex>) (GraphFunctionsTest.graph.command(new com.orientechnologies.orient.core.sql.OCommandSQL(("select expand( in() ) from " + (GraphFunctionsTest.v3.getId())))).execute())))
            found++;

        Assert.assertEquals(found, 1);
    }

    @Test
    public void testOutE() {
        int found;
        // V1
        found = 0;
        for (Edge v : ((Iterable<Edge>) (GraphFunctionsTest.graph.command(new com.orientechnologies.orient.core.sql.OCommandSQL(("select expand( outE() ) from " + (GraphFunctionsTest.v1.getId())))).execute())))
            found++;

        Assert.assertEquals(found, 2);
        found = 0;
        for (Edge v : ((Iterable<Edge>) (GraphFunctionsTest.graph.command(new com.orientechnologies.orient.core.sql.OCommandSQL(("select expand( outE('SubEdge') ) from " + (GraphFunctionsTest.v1.getId())))).execute())))
            found++;

        Assert.assertEquals(found, 1);
        found = 0;
        for (Edge v : ((Iterable<Edge>) (GraphFunctionsTest.graph.command(new com.orientechnologies.orient.core.sql.OCommandSQL(("select expand( outE('dddd') ) from " + (GraphFunctionsTest.v1.getId())))).execute())))
            found++;

        Assert.assertEquals(found, 0);
        // V2
        found = 0;
        for (Edge v : ((Iterable<Edge>) (GraphFunctionsTest.graph.command(new com.orientechnologies.orient.core.sql.OCommandSQL(("select expand( outE() ) from " + (GraphFunctionsTest.v2.getId())))).execute())))
            found++;

        Assert.assertEquals(found, 0);
        // V3
        found = 0;
        for (Edge v : ((Iterable<Edge>) (GraphFunctionsTest.graph.command(new com.orientechnologies.orient.core.sql.OCommandSQL(("select expand( outE() ) from " + (GraphFunctionsTest.v3.getId())))).execute())))
            found++;

        Assert.assertEquals(found, 0);
    }

    @Test
    public void testInE() {
        int found;
        // V1
        found = 0;
        for (Edge v : ((Iterable<Edge>) (GraphFunctionsTest.graph.command(new com.orientechnologies.orient.core.sql.OCommandSQL(("select expand( inE() ) from " + (GraphFunctionsTest.v1.getId())))).execute())))
            found++;

        Assert.assertEquals(found, 0);
        // V2
        found = 0;
        for (Edge v : ((Iterable<Edge>) (GraphFunctionsTest.graph.command(new com.orientechnologies.orient.core.sql.OCommandSQL(("select expand( inE() ) from " + (GraphFunctionsTest.v2.getId())))).execute())))
            found++;

        Assert.assertEquals(found, 1);
        found = 0;
        for (Edge v : ((Iterable<Edge>) (GraphFunctionsTest.graph.command(new com.orientechnologies.orient.core.sql.OCommandSQL(("select expand( inE('SubEdge') ) from " + (GraphFunctionsTest.v2.getId())))).execute())))
            found++;

        Assert.assertEquals(found, 1);
        found = 0;
        for (Edge v : ((Iterable<Edge>) (GraphFunctionsTest.graph.command(new com.orientechnologies.orient.core.sql.OCommandSQL(("select expand( inE('dddd') ) from " + (GraphFunctionsTest.v2.getId())))).execute())))
            found++;

        Assert.assertEquals(found, 0);
        // V3
        found = 0;
        for (Edge v : ((Iterable<Edge>) (GraphFunctionsTest.graph.command(new com.orientechnologies.orient.core.sql.OCommandSQL(("select expand( inE() ) from " + (GraphFunctionsTest.v3.getId())))).execute())))
            found++;

        Assert.assertEquals(found, 1);
    }

    @Test
    public void testOutV() {
        Iterable<Vertex> vertices;
        // V1
        vertices = ((Iterable<Vertex>) (GraphFunctionsTest.graph.command(new com.orientechnologies.orient.core.sql.OCommandSQL(("select expand( outE().outV() ) from " + (GraphFunctionsTest.v1.getId())))).execute()));
        Assert.assertEquals(vertices.iterator().next(), GraphFunctionsTest.v1);
        vertices = ((Iterable<Vertex>) (GraphFunctionsTest.graph.command(new com.orientechnologies.orient.core.sql.OCommandSQL(("select expand( outE().inV() ) from " + (GraphFunctionsTest.v1.getId())))).execute()));
        Assert.assertEquals(vertices.iterator().next(), GraphFunctionsTest.v2);
        // V2
        vertices = ((Iterable<Vertex>) (GraphFunctionsTest.graph.command(new com.orientechnologies.orient.core.sql.OCommandSQL(("select expand( inE().inV() ) from " + (GraphFunctionsTest.v2.getId())))).execute()));
        Assert.assertEquals(vertices.iterator().next(), GraphFunctionsTest.v2);
        vertices = ((Iterable<Vertex>) (GraphFunctionsTest.graph.command(new com.orientechnologies.orient.core.sql.OCommandSQL(("select expand( inE().outV() ) from " + (GraphFunctionsTest.v2.getId())))).execute()));
        Assert.assertEquals(vertices.iterator().next(), GraphFunctionsTest.v1);
    }

    @Test
    public void testOutEPolymorphic() {
        int found;
        // V1
        found = 0;
        for (Edge v : ((Iterable<Edge>) (GraphFunctionsTest.graph.command(new com.orientechnologies.orient.core.sql.OCommandSQL(("select expand( outE('E') ) from " + (GraphFunctionsTest.v1.getId())))).execute())))
            found++;

        Assert.assertEquals(found, 2);
    }
}

