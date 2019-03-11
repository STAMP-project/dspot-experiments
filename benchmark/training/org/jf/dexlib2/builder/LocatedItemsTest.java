package org.jf.dexlib2.builder;


import org.junit.Test;


public class LocatedItemsTest {
    @Test
    public void testMergeIntoKeepsOrderOfDebugItems() {
        doTestMergeIntoKeepsOrderOfDebugItems(2, 2);
        doTestMergeIntoKeepsOrderOfDebugItems(0, 0);
        doTestMergeIntoKeepsOrderOfDebugItems(0, 2);
        doTestMergeIntoKeepsOrderOfDebugItems(2, 0);
    }
}

