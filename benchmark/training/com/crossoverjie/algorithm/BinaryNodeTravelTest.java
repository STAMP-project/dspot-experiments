package com.crossoverjie.algorithm;


import org.junit.Test;


public class BinaryNodeTravelTest {
    @Test
    public void levelIterator() throws Exception {
        BinaryNodeTravel node = new BinaryNodeTravel();
        // ?????
        node = node.createNode();
        // ???????
        BinaryNodeTravel binaryNodeTravel = node.levelIterator(node);
        while (binaryNodeTravel != null) {
            System.out.print(((binaryNodeTravel.getData()) + "--->"));
            binaryNodeTravel = binaryNodeTravel.next;
        } 
    }
}

