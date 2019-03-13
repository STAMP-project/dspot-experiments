package com.crossoverjie.algorithm;


import org.junit.Test;


public class BinaryNodeTest {
    @Test
    public void test1() {
        BinaryNode node = new BinaryNode();
        // ?????
        node = node.createNode();
        System.out.println(node);
        // ???????
        node.levelIterator(node);
    }
}

