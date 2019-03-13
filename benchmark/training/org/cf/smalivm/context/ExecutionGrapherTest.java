package org.cf.smalivm.context;


import java.util.LinkedList;
import java.util.List;
import org.cf.smalivm.type.VirtualMethod;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ExecutionGrapherTest {
    private static final int CHILD_ADDRESS = 2;

    private static final String CHILD_NODE_STR = "child node";

    private static final String CHILD_STATE_STR = "child state";

    private static final String METHOD_SIGNATURE = "Lclass;->method()V";

    private static final int ROOT_ADDRESS = 1;

    private static final String ROOT_NODE_STR = "root node";

    private static final String ROOT_STATE_STR = "root state";

    @Test
    public void testHasExpectedGraph() {
        ExecutionNode child = ExecutionGrapherTest.buildNode(ExecutionGrapherTest.CHILD_ADDRESS, ExecutionGrapherTest.CHILD_NODE_STR, ExecutionGrapherTest.CHILD_STATE_STR);
        Mockito.when(child.getChildren()).thenReturn(new LinkedList<ExecutionNode>());
        ExecutionNode root = ExecutionGrapherTest.buildNode(ExecutionGrapherTest.ROOT_ADDRESS, ExecutionGrapherTest.ROOT_NODE_STR, ExecutionGrapherTest.ROOT_STATE_STR);
        List<ExecutionNode> children = new LinkedList<ExecutionNode>();
        children.add(child);
        Mockito.when(root.getChildren()).thenReturn(children);
        VirtualMethod localMethod = Mockito.mock(VirtualMethod.class);
        Mockito.when(localMethod.toString()).thenReturn(ExecutionGrapherTest.METHOD_SIGNATURE);
        ExecutionGraph graph = Mockito.mock(ExecutionGraph.class);
        Mockito.when(graph.getRoot()).thenReturn(root);
        Mockito.when(graph.getMethod()).thenReturn(localMethod);
        Mockito.when(graph.getNodeIndex(root)).thenReturn(0);
        Mockito.when(graph.getNodeIndex(child)).thenReturn(0);
        String digraph = ExecutionGrapher.graph(graph);
        StringBuilder sb = new StringBuilder("digraph {\n");
        sb.append("\"@").append(ExecutionGrapherTest.ROOT_ADDRESS).append(".0 :: ").append(ExecutionGrapherTest.ROOT_NODE_STR).append('\n');
        sb.append(ExecutionGrapherTest.ROOT_STATE_STR).append("\" -> ");
        sb.append("\"@").append(ExecutionGrapherTest.CHILD_ADDRESS).append(".0 :: ").append(ExecutionGrapherTest.CHILD_NODE_STR).append('\n');
        sb.append(ExecutionGrapherTest.CHILD_STATE_STR).append("\"\n");
        sb.append("labelloc=\"t\"\n");
        sb.append("label=\"").append(ExecutionGrapherTest.METHOD_SIGNATURE).append("\";");
        sb.append("\n}");
        String expected = sb.toString();
        Assert.assertEquals(expected, digraph);
    }
}

