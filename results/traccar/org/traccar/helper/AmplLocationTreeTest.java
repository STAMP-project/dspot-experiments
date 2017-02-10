

package org.traccar.helper;


public class AmplLocationTreeTest {
    @org.junit.Test
    public void testLocationTree() {
        java.util.List<org.traccar.helper.LocationTree.Item> items = new java.util.ArrayList<>();
        items.add(new org.traccar.helper.LocationTree.Item(1, 1, "a"));
        items.add(new org.traccar.helper.LocationTree.Item(3, 2, "b"));
        items.add(new org.traccar.helper.LocationTree.Item(1, 3, "c"));
        items.add(new org.traccar.helper.LocationTree.Item(4, 3, "d"));
        org.traccar.helper.LocationTree tree = new org.traccar.helper.LocationTree(items);
        org.junit.Assert.assertEquals("a", tree.findNearest(new org.traccar.helper.LocationTree.Item(1.0F, 1.0F)).getData());
        org.junit.Assert.assertEquals("d", tree.findNearest(new org.traccar.helper.LocationTree.Item(10.0F, 10.0F)).getData());
        org.junit.Assert.assertEquals("c", tree.findNearest(new org.traccar.helper.LocationTree.Item(1.0F, 2.5F)).getData());
        org.junit.Assert.assertEquals("a", tree.findNearest(new org.traccar.helper.LocationTree.Item(1.5F, 1.5F)).getData());
    }

    /* amplification of org.traccar.helper.LocationTreeTest#testLocationTree */
    @org.junit.Test
    public void testLocationTree_literalMutation16() {
        java.util.List<org.traccar.helper.LocationTree.Item> items = new java.util.ArrayList<>();
        items.add(new org.traccar.helper.LocationTree.Item(1, 1, "a"));
        // AssertGenerator replace invocation
        boolean o_testLocationTree_literalMutation16__5 = items.add(new org.traccar.helper.LocationTree.Item(1, 2, "b"));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testLocationTree_literalMutation16__5);
        items.add(new org.traccar.helper.LocationTree.Item(1, 3, "c"));
        items.add(new org.traccar.helper.LocationTree.Item(4, 3, "d"));
        org.traccar.helper.LocationTree tree = new org.traccar.helper.LocationTree(items);
        org.junit.Assert.assertEquals("a", tree.findNearest(new org.traccar.helper.LocationTree.Item(1.0F, 1.0F)).getData());
        org.junit.Assert.assertEquals("d", tree.findNearest(new org.traccar.helper.LocationTree.Item(10.0F, 10.0F)).getData());
        org.junit.Assert.assertEquals("c", tree.findNearest(new org.traccar.helper.LocationTree.Item(1.0F, 2.5F)).getData());
        org.junit.Assert.assertEquals("a", tree.findNearest(new org.traccar.helper.LocationTree.Item(1.5F, 1.5F)).getData());
    }

    /* amplification of org.traccar.helper.LocationTreeTest#testLocationTree */
    @org.junit.Test
    public void testLocationTree_literalMutation28() {
        java.util.List<org.traccar.helper.LocationTree.Item> items = new java.util.ArrayList<>();
        items.add(new org.traccar.helper.LocationTree.Item(1, 1, "a"));
        items.add(new org.traccar.helper.LocationTree.Item(3, 2, "b"));
        // AssertGenerator replace invocation
        boolean o_testLocationTree_literalMutation28__7 = items.add(new org.traccar.helper.LocationTree.Item(1, 2, "c"));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testLocationTree_literalMutation28__7);
        items.add(new org.traccar.helper.LocationTree.Item(4, 3, "d"));
        org.traccar.helper.LocationTree tree = new org.traccar.helper.LocationTree(items);
        org.junit.Assert.assertEquals("a", tree.findNearest(new org.traccar.helper.LocationTree.Item(1.0F, 1.0F)).getData());
        org.junit.Assert.assertEquals("d", tree.findNearest(new org.traccar.helper.LocationTree.Item(10.0F, 10.0F)).getData());
        org.junit.Assert.assertEquals("c", tree.findNearest(new org.traccar.helper.LocationTree.Item(1.0F, 2.5F)).getData());
        org.junit.Assert.assertEquals("a", tree.findNearest(new org.traccar.helper.LocationTree.Item(1.5F, 1.5F)).getData());
    }

    /* amplification of org.traccar.helper.LocationTreeTest#testLocationTree */
    @org.junit.Test
    public void testLocationTree_literalMutation14() {
        java.util.List<org.traccar.helper.LocationTree.Item> items = new java.util.ArrayList<>();
        items.add(new org.traccar.helper.LocationTree.Item(1, 1, "a"));
        // AssertGenerator replace invocation
        boolean o_testLocationTree_literalMutation14__5 = items.add(new org.traccar.helper.LocationTree.Item(2, 2, "b"));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testLocationTree_literalMutation14__5);
        items.add(new org.traccar.helper.LocationTree.Item(1, 3, "c"));
        items.add(new org.traccar.helper.LocationTree.Item(4, 3, "d"));
        org.traccar.helper.LocationTree tree = new org.traccar.helper.LocationTree(items);
        org.junit.Assert.assertEquals("a", tree.findNearest(new org.traccar.helper.LocationTree.Item(1.0F, 1.0F)).getData());
        org.junit.Assert.assertEquals("d", tree.findNearest(new org.traccar.helper.LocationTree.Item(10.0F, 10.0F)).getData());
        org.junit.Assert.assertEquals("c", tree.findNearest(new org.traccar.helper.LocationTree.Item(1.0F, 2.5F)).getData());
        org.junit.Assert.assertEquals("a", tree.findNearest(new org.traccar.helper.LocationTree.Item(1.5F, 1.5F)).getData());
    }

    /* amplification of org.traccar.helper.LocationTreeTest#testLocationTree */
    @org.junit.Test
    public void testLocationTree_literalMutation15() {
        java.util.List<org.traccar.helper.LocationTree.Item> items = new java.util.ArrayList<>();
        items.add(new org.traccar.helper.LocationTree.Item(1, 1, "a"));
        // AssertGenerator replace invocation
        boolean o_testLocationTree_literalMutation15__5 = items.add(new org.traccar.helper.LocationTree.Item(0, 2, "b"));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testLocationTree_literalMutation15__5);
        items.add(new org.traccar.helper.LocationTree.Item(1, 3, "c"));
        items.add(new org.traccar.helper.LocationTree.Item(4, 3, "d"));
        org.traccar.helper.LocationTree tree = new org.traccar.helper.LocationTree(items);
        org.junit.Assert.assertEquals("a", tree.findNearest(new org.traccar.helper.LocationTree.Item(1.0F, 1.0F)).getData());
        org.junit.Assert.assertEquals("d", tree.findNearest(new org.traccar.helper.LocationTree.Item(10.0F, 10.0F)).getData());
        org.junit.Assert.assertEquals("c", tree.findNearest(new org.traccar.helper.LocationTree.Item(1.0F, 2.5F)).getData());
        org.junit.Assert.assertEquals("a", tree.findNearest(new org.traccar.helper.LocationTree.Item(1.5F, 1.5F)).getData());
    }

    /* amplification of org.traccar.helper.LocationTreeTest#testLocationTree */
    @org.junit.Test
    public void testLocationTree_literalMutation28_literalMutation2356() {
        java.util.List<org.traccar.helper.LocationTree.Item> items = new java.util.ArrayList<>();
        items.add(new org.traccar.helper.LocationTree.Item(1, 1, "a"));
        // AssertGenerator replace invocation
        boolean o_testLocationTree_literalMutation28_literalMutation2356__5 = items.add(new org.traccar.helper.LocationTree.Item(3, 0, "b"));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testLocationTree_literalMutation28_literalMutation2356__5);
        // AssertGenerator replace invocation
        boolean o_testLocationTree_literalMutation28__7 = items.add(new org.traccar.helper.LocationTree.Item(1, 2, "c"));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testLocationTree_literalMutation28__7);
        items.add(new org.traccar.helper.LocationTree.Item(4, 3, "d"));
        org.traccar.helper.LocationTree tree = new org.traccar.helper.LocationTree(items);
        org.junit.Assert.assertEquals("a", tree.findNearest(new org.traccar.helper.LocationTree.Item(1.0F, 1.0F)).getData());
        org.junit.Assert.assertEquals("d", tree.findNearest(new org.traccar.helper.LocationTree.Item(10.0F, 10.0F)).getData());
        org.junit.Assert.assertEquals("c", tree.findNearest(new org.traccar.helper.LocationTree.Item(1.0F, 2.5F)).getData());
        org.junit.Assert.assertEquals("a", tree.findNearest(new org.traccar.helper.LocationTree.Item(1.5F, 1.5F)).getData());
    }

    /* amplification of org.traccar.helper.LocationTreeTest#testLocationTree */
    @org.junit.Test
    public void testLocationTree_literalMutation35_literalMutation2904() {
        java.util.List<org.traccar.helper.LocationTree.Item> items = new java.util.ArrayList<>();
        items.add(new org.traccar.helper.LocationTree.Item(1, 1, "a"));
        // AssertGenerator replace invocation
        boolean o_testLocationTree_literalMutation35_literalMutation2904__5 = items.add(new org.traccar.helper.LocationTree.Item(1, 2, "b"));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testLocationTree_literalMutation35_literalMutation2904__5);
        items.add(new org.traccar.helper.LocationTree.Item(1, 3, "c"));
        // AssertGenerator replace invocation
        boolean o_testLocationTree_literalMutation35__9 = items.add(new org.traccar.helper.LocationTree.Item(8, 3, "d"));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testLocationTree_literalMutation35__9);
        org.traccar.helper.LocationTree tree = new org.traccar.helper.LocationTree(items);
        org.junit.Assert.assertEquals("a", tree.findNearest(new org.traccar.helper.LocationTree.Item(1.0F, 1.0F)).getData());
        org.junit.Assert.assertEquals("d", tree.findNearest(new org.traccar.helper.LocationTree.Item(10.0F, 10.0F)).getData());
        org.junit.Assert.assertEquals("c", tree.findNearest(new org.traccar.helper.LocationTree.Item(1.0F, 2.5F)).getData());
        org.junit.Assert.assertEquals("a", tree.findNearest(new org.traccar.helper.LocationTree.Item(1.5F, 1.5F)).getData());
    }

    /* amplification of org.traccar.helper.LocationTreeTest#testLocationTree */
    @org.junit.Test
    public void testLocationTree_literalMutation39_literalMutation3231_literalMutation8249() {
        java.util.List<org.traccar.helper.LocationTree.Item> items = new java.util.ArrayList<>();
        // AssertGenerator replace invocation
        boolean o_testLocationTree_literalMutation39_literalMutation3231_literalMutation8249__3 = items.add(new org.traccar.helper.LocationTree.Item(0, 1, "a"));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testLocationTree_literalMutation39_literalMutation3231_literalMutation8249__3);
        items.add(new org.traccar.helper.LocationTree.Item(3, 2, "b"));
        // AssertGenerator replace invocation
        boolean o_testLocationTree_literalMutation39_literalMutation3231__7 = items.add(new org.traccar.helper.LocationTree.Item(0, 3, "c"));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testLocationTree_literalMutation39_literalMutation3231__7);
        // AssertGenerator replace invocation
        boolean o_testLocationTree_literalMutation39__9 = items.add(new org.traccar.helper.LocationTree.Item(4, 4, "d"));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testLocationTree_literalMutation39__9);
        org.traccar.helper.LocationTree tree = new org.traccar.helper.LocationTree(items);
        org.junit.Assert.assertEquals("a", tree.findNearest(new org.traccar.helper.LocationTree.Item(1.0F, 1.0F)).getData());
        org.junit.Assert.assertEquals("d", tree.findNearest(new org.traccar.helper.LocationTree.Item(10.0F, 10.0F)).getData());
        org.junit.Assert.assertEquals("c", tree.findNearest(new org.traccar.helper.LocationTree.Item(1.0F, 2.5F)).getData());
        org.junit.Assert.assertEquals("a", tree.findNearest(new org.traccar.helper.LocationTree.Item(1.5F, 1.5F)).getData());
    }

    /* amplification of org.traccar.helper.LocationTreeTest#testLocationTree */
    @org.junit.Test
    public void testLocationTree_literalMutation38_literalMutation3154_literalMutation15513() {
        java.util.List<org.traccar.helper.LocationTree.Item> items = new java.util.ArrayList<>();
        items.add(new org.traccar.helper.LocationTree.Item(1, 1, "a"));
        // AssertGenerator replace invocation
        boolean o_testLocationTree_literalMutation38_literalMutation3154_literalMutation15513__5 = items.add(new org.traccar.helper.LocationTree.Item(3, 2, ""));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testLocationTree_literalMutation38_literalMutation3154_literalMutation15513__5);
        // AssertGenerator replace invocation
        boolean o_testLocationTree_literalMutation38_literalMutation3154__7 = items.add(new org.traccar.helper.LocationTree.Item(1, 2, "c"));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testLocationTree_literalMutation38_literalMutation3154__7);
        // AssertGenerator replace invocation
        boolean o_testLocationTree_literalMutation38__9 = items.add(new org.traccar.helper.LocationTree.Item(3, 3, "d"));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testLocationTree_literalMutation38__9);
        org.traccar.helper.LocationTree tree = new org.traccar.helper.LocationTree(items);
        org.junit.Assert.assertEquals("a", tree.findNearest(new org.traccar.helper.LocationTree.Item(1.0F, 1.0F)).getData());
        org.junit.Assert.assertEquals("d", tree.findNearest(new org.traccar.helper.LocationTree.Item(10.0F, 10.0F)).getData());
        org.junit.Assert.assertEquals("c", tree.findNearest(new org.traccar.helper.LocationTree.Item(1.0F, 2.5F)).getData());
        org.junit.Assert.assertEquals("a", tree.findNearest(new org.traccar.helper.LocationTree.Item(1.5F, 1.5F)).getData());
    }
}

