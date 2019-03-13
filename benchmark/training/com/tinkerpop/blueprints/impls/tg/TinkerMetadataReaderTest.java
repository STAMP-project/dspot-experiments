package com.tinkerpop.blueprints.impls.tg;


import com.tinkerpop.blueprints.CloseableIterable;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Index;
import com.tinkerpop.blueprints.Vertex;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Victor Su
 */
public class TinkerMetadataReaderTest {
    private TinkerGraph graph;

    @Test
    public void exampleMetadataGetsCorrectCurrentId() throws IOException {
        TinkerMetadataReader.load(this.graph, TinkerMetadataReaderTest.class.getResourceAsStream("example-tinkergraph-metadata.dat"));
        Assert.assertEquals(((long) (this.graph.currentId)), 0L);
    }

    @Test
    public void exampleMetadataGetsCorrectIndices() throws IOException {
        TinkerMetadataReader.load(this.graph, TinkerMetadataReaderTest.class.getResourceAsStream("example-tinkergraph-metadata.dat"));
        Assert.assertEquals(2, this.graph.indices.size());
        Index idxAge = this.graph.getIndex("age", Vertex.class);
        CloseableIterable<Vertex> vertices = idxAge.get("age", 27);
        Assert.assertEquals(1, getIterableCount(vertices));
        vertices.close();
        Index idxWeight = this.graph.getIndex("weight", Edge.class);
        CloseableIterable<Edge> edges = idxWeight.get("weight", 0.5F);
        Assert.assertEquals(1, getIterableCount(edges));
        edges.close();
    }

    @Test
    public void exampleMetadataGetsCorrectVertexKeyIndices() throws IOException {
        TinkerMetadataReader.load(this.graph, TinkerMetadataReaderTest.class.getResourceAsStream("example-tinkergraph-metadata.dat"));
        Assert.assertEquals(1, this.graph.vertexKeyIndex.index.size());
        Assert.assertEquals(1, getIterableCount(graph.getVertices("age", 27)));
    }

    @Test
    public void exampleMetadataGetsCorrectEdgeKeyIndices() throws IOException {
        TinkerMetadataReader.load(this.graph, TinkerMetadataReaderTest.class.getResourceAsStream("example-tinkergraph-metadata.dat"));
        Assert.assertEquals(1, this.graph.edgeKeyIndex.index.size());
        Assert.assertEquals(1, getIterableCount(graph.getEdges("weight", 0.5F)));
    }
}

