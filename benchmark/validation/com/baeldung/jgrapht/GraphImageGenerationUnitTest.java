package com.baeldung.jgrapht;


import com.mxgraph.layout.mxIGraphLayout;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.Assert;
import org.junit.Test;


public class GraphImageGenerationUnitTest {
    static DefaultDirectedGraph<String, DefaultEdge> g;

    @Test
    public void givenAdaptedGraph_whenWriteBufferedImage_ThenFileShouldExist() throws IOException {
        JGraphXAdapter<String, DefaultEdge> graphAdapter = new JGraphXAdapter<String, DefaultEdge>(GraphImageGenerationUnitTest.g);
        mxIGraphLayout layout = new com.mxgraph.layout.mxCircleLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());
        File imgFile = new File("src/test/resources/graph.png");
        BufferedImage image = mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
        ImageIO.write(image, "PNG", imgFile);
        Assert.assertTrue(imgFile.exists());
    }
}

