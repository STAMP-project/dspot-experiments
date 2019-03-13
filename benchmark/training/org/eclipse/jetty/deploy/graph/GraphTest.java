/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.deploy.graph;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class GraphTest {
    final Node nodeA = new Node("A");

    final Node nodeB = new Node("B");

    final Node nodeC = new Node("C");

    final Node nodeD = new Node("D");

    final Node nodeE = new Node("E");

    @Test
    public void testPath() {
        Path path = new Path();
        Assertions.assertEquals(0, path.nodes());
        Assertions.assertEquals(null, path.firstNode());
        Assertions.assertEquals(null, path.lastNode());
        path.add(new Edge(nodeA, nodeB));
        Assertions.assertEquals(2, path.nodes());
        Assertions.assertEquals(nodeA, path.firstNode());
        Assertions.assertEquals(nodeB, path.lastNode());
        path.add(new Edge(nodeB, nodeC));
        Assertions.assertEquals(3, path.nodes());
        Assertions.assertEquals(nodeA, path.firstNode());
        Assertions.assertEquals(nodeC, path.lastNode());
    }

    @Test
    public void testPoint() {
        Graph graph = new Graph();
        graph.addNode(nodeA);
        Assertions.assertEquals(1, graph.getNodes().size());
        Assertions.assertEquals(0, graph.getEdges().size());
        Path path = graph.getPath(nodeA, nodeA);
        Assertions.assertEquals(0, path.nodes());
    }

    @Test
    public void testLine() {
        Graph graph = new Graph();
        graph.addEdge(new Edge(nodeA, nodeB));
        Assertions.assertEquals(2, graph.getNodes().size());
        Assertions.assertEquals(1, graph.getEdges().size());
        Path path = graph.getPath(nodeA, nodeB);
        Assertions.assertEquals(2, path.nodes());
    }

    @Test
    public void testTriangleDirected() {
        Graph graph = new Graph();
        graph.addEdge(new Edge(nodeA, nodeB));
        graph.addEdge(new Edge(nodeA, nodeC));
        graph.addEdge(new Edge(nodeB, nodeC));
        Assertions.assertEquals(3, graph.getNodes().size());
        Assertions.assertEquals(3, graph.getEdges().size());
        Path path = graph.getPath(nodeA, nodeB);
        Assertions.assertEquals(2, path.nodes());
        path = graph.getPath(nodeA, nodeC);
        Assertions.assertEquals(2, path.nodes());
        path = graph.getPath(nodeB, nodeC);
        Assertions.assertEquals(2, path.nodes());
    }

    @Test
    public void testSquareDirected() {
        Graph graph = new Graph();
        graph.addEdge(new Edge(nodeA, nodeB));
        graph.addEdge(new Edge(nodeB, nodeC));
        graph.addEdge(new Edge(nodeA, nodeD));
        graph.addEdge(new Edge(nodeD, nodeC));
        Assertions.assertEquals(4, graph.getNodes().size());
        Assertions.assertEquals(4, graph.getEdges().size());
        Path path = graph.getPath(nodeA, nodeC);
        Assertions.assertEquals(3, path.nodes());
        path = graph.getPath(nodeC, nodeA);
        Assertions.assertEquals(null, path);
    }

    @Test
    public void testSquareCyclic() {
        Graph graph = new Graph();
        graph.addEdge(new Edge(nodeA, nodeB));
        graph.addEdge(new Edge(nodeB, nodeC));
        graph.addEdge(new Edge(nodeC, nodeD));
        graph.addEdge(new Edge(nodeD, nodeA));
        Assertions.assertEquals(4, graph.getNodes().size());
        Assertions.assertEquals(4, graph.getEdges().size());
        Path path = graph.getPath(nodeA, nodeB);
        Assertions.assertEquals(2, path.nodes());
        path = graph.getPath(nodeA, nodeC);
        Assertions.assertEquals(3, path.nodes());
        path = graph.getPath(nodeA, nodeD);
        Assertions.assertEquals(4, path.nodes());
        graph.addNode(nodeE);
        path = graph.getPath(nodeA, nodeE);
        Assertions.assertEquals(null, path);
    }
}

