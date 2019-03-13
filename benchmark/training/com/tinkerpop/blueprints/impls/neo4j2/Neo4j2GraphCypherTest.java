package com.tinkerpop.blueprints.impls.neo4j2;


import org.junit.Test;


/**
 *
 *
 * @author mh
 * @since 08.01.14
 */
public class Neo4j2GraphCypherTest {
    private Neo4j2Graph graph;

    @Test
    public void testVertexLabels() throws Exception {
        Neo4j2Vertex vertex = graph.addVertex(null);
        vertex.addLabel("Label");
        vertex.setProperty("key", "value");
        queryAndAssert(vertex);
        graph.commit();
        graph.query("create index on :Label(key)", null);
        queryAndAssert(vertex);
    }
}

