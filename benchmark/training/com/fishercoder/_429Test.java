package com.fishercoder;


import _429.Solution1;
import com.fishercoder.common.classes.Node;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class _429Test {
    private static Solution1 solution1;

    private static Node root;

    private static List<List<Integer>> expected;

    @Test
    public void test1() {
        _429Test.root = new Node(1);
        Node node3 = new Node(3);
        Node node2 = new Node(2);
        Node node4 = new Node(4);
        _429Test.root.children = Arrays.asList(node3, node2, node4);
        Node node5 = new Node(5);
        Node node6 = new Node(6);
        node3.children = Arrays.asList(node5, node6);
        _429Test.expected = new ArrayList<>();
        _429Test.expected.add(Arrays.asList(1));
        _429Test.expected.add(Arrays.asList(3, 2, 4));
        _429Test.expected.add(Arrays.asList(5, 6));
        Assert.assertEquals(_429Test.expected, _429Test.solution1.levelOrder(_429Test.root));
    }

    @Test
    public void test2() {
        _429Test.root = null;
        _429Test.expected = new ArrayList<>();
        Assert.assertEquals(_429Test.expected, _429Test.solution1.levelOrder(_429Test.root));
    }
}

