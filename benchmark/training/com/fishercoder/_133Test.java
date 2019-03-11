package com.fishercoder;


import _133.Solution1;
import com.fishercoder.common.classes.UndirectedGraphNode;
import junit.framework.Assert;
import org.junit.Test;


public class _133Test {
    private static Solution1 solution1;

    private static UndirectedGraphNode expected;

    private static UndirectedGraphNode actual;

    @Test
    public void test1() {
        UndirectedGraphNode node0 = new UndirectedGraphNode(0);
        UndirectedGraphNode node1 = new UndirectedGraphNode(1);
        UndirectedGraphNode node2 = new UndirectedGraphNode(2);
        node0.neighbors.add(node1);
        node0.neighbors.add(node2);
        node1.neighbors.add(node2);
        _133Test.expected = node0;
        _133Test.actual = _133Test.solution1.cloneGraph(_133Test.expected);
        Assert.assertEquals(_133Test.expected, _133Test.actual);
    }
}

