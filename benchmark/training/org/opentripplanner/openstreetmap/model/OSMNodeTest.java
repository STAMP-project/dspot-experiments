package org.opentripplanner.openstreetmap.model;


import org.junit.Assert;
import org.junit.Test;


public class OSMNodeTest {
    @Test
    public void testIsMultiLevel() {
        OSMNode node = new OSMNode();
        Assert.assertFalse(node.isMultiLevel());
        node.addTag("highway", "var");
        Assert.assertFalse(node.isMultiLevel());
        node.addTag("highway", "elevator");
        Assert.assertTrue(node.isMultiLevel());
    }

    @Test
    public void testGetCapacity() {
        OSMNode node = new OSMNode();
        Assert.assertFalse(node.hasTag("capacity"));
        Assert.assertEquals(0, node.getCapacity());
        try {
            node.addTag("capacity", "foobie");
            node.getCapacity();
            // Above should fail.
            Assert.assertFalse(true);
        } catch (NumberFormatException e) {
        }
        node.addTag("capacity", "10");
        Assert.assertTrue(node.hasTag("capacity"));
        Assert.assertEquals(10, node.getCapacity());
    }
}

