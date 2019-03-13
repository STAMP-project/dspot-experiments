package com.fishercoder;


import _559.Solution1;
import com.fishercoder.common.classes.Node;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class _559Test {
    private static Solution1 solution1;

    private static Node root;

    @Test
    public void test1() {
        _559Test.root = new Node(1);
        Node node3 = new Node(3);
        Node node2 = new Node(2);
        Node node4 = new Node(4);
        _559Test.root.children = Arrays.asList(node3, node2, node4);
        Node node5 = new Node(5);
        Node node6 = new Node(6);
        node3.children = Arrays.asList(node5, node6);
        Assert.assertEquals(3, _559Test.solution1.maxDepth(_559Test.root));
    }
}

