package org.nd4j.imports;


import lombok.val;
import org.junit.Assert;
import org.junit.Test;


public class OnnxImportTest {
    @Test
    public void testOnnxImportEmbedding() throws Exception {
        /**
         *
         */
        val importGraph = org.nd4j.imports.graphmapper.onnx.OnnxGraphMapper.getInstance().importGraph(new org.nd4j.linalg.io.ClassPathResource("onnx_graphs/embedding_only.onnx").getInputStream());
        val embeddingMatrix = importGraph.getVariable("2");
        Assert.assertArrayEquals(new long[]{ 100, 300 }, embeddingMatrix.getShape());
        /* val onlyOp = importGraph.getFunctionForVertexId(importGraph.getVariable("3").getVertexId());
        assertNotNull(onlyOp);
        assertTrue(onlyOp instanceof Gather);
         */
    }

    @Test
    public void testOnnxImportCnn() throws Exception {
        /* val importGraph = OnnxGraphMapper.getInstance().importGraph(new ClassPathResource("onnx_graphs/sm_cnn.onnx").getFile());
        assertEquals(20,importGraph.graph().numVertices());
        val outputTanhOutput = importGraph.getFunctionForVertexId(15);
        assertNotNull(outputTanhOutput);
        assertTrue(outputTanhOutput instanceof Tanh);

        val pooling = importGraph.getFunctionForVertexId(16);
        assertTrue(pooling instanceof MaxPooling2D);

        val poolingCast = (MaxPooling2D) pooling;
        assertEquals(24,poolingCast.getConfig().getKh());
        assertEquals(24,poolingCast.getConfig().getKw());
         */
    }
}

