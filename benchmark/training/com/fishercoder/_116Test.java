package com.fishercoder;


import _116.Solution1;
import com.fishercoder.common.classes.TreeLinkNode;
import org.junit.Test;


public class _116Test {
    private static Solution1 solution1;

    private static TreeLinkNode root;

    @Test
    public void test1() {
        _116Test.root = new TreeLinkNode(1);
        _116Test.root.left = new TreeLinkNode(2);
        _116Test.root.right = new TreeLinkNode(3);
        _116Test.root.left.left = new TreeLinkNode(4);
        _116Test.root.left.right = new TreeLinkNode(5);
        _116Test.root.right.right = new TreeLinkNode(7);
        _116Test.solution1.connect(_116Test.root);
    }
}

