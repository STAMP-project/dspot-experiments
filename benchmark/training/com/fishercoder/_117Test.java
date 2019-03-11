package com.fishercoder;


import _117.Solution1;
import com.fishercoder.common.classes.TreeLinkNode;
import org.junit.Test;


public class _117Test {
    private static Solution1 solution1;

    private static TreeLinkNode root;

    @Test
    public void test1() {
        _117Test.root = new TreeLinkNode(1);
        _117Test.root.left = new TreeLinkNode(2);
        _117Test.root.right = new TreeLinkNode(3);
        _117Test.root.left.left = new TreeLinkNode(4);
        _117Test.root.left.right = new TreeLinkNode(5);
        _117Test.root.right.right = new TreeLinkNode(7);
        _117Test.solution1.connect(_117Test.root);
    }
}

