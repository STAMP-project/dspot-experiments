package com.tinkerpop.gremlin.java;


import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraphFactory;
import junit.framework.TestCase;


public class GremlinPipelineTest extends TestCase {
    public void testCasting() {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        GremlinPipeline<Vertex, Vertex> pipeline = new GremlinPipeline<Vertex, Vertex>(graph.getVertices());
        TestCase.assertTrue((pipeline == (pipeline.cast(Vertex.class))));
        // This should compile without warnings
        pipeline.as("person").out("knows").has("name", "josh").back("person").cast(Vertex.class).filter(new com.tinkerpop.pipes.PipeFunction<Vertex, Boolean>() {
            @Override
            public Boolean compute(Vertex argument) {
                return true;
            }
        });
    }
}

