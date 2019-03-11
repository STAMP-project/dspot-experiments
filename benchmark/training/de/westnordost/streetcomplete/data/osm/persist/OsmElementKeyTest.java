package de.westnordost.streetcomplete.data.osm.persist;


import Element.Type;
import org.junit.Assert;
import org.junit.Test;


public class OsmElementKeyTest {
    @Test
    public void almostEqual() {
        OsmElementKey a = new OsmElementKey(Type.NODE, 1);
        OsmElementKey b = new OsmElementKey(Type.WAY, 1);
        OsmElementKey c = new OsmElementKey(Type.NODE, 2);
        Assert.assertNotEquals(a, b);
        Assert.assertNotEquals(a, c);
        Assert.assertNotEquals(a.hashCode(), b.hashCode());
        Assert.assertNotEquals(a.hashCode(), c.hashCode());
    }

    @Test
    public void equal() {
        OsmElementKey a = new OsmElementKey(Type.NODE, 1);
        OsmElementKey b = new OsmElementKey(Type.NODE, 1);
        Assert.assertEquals(a, b);
        Assert.assertEquals(a.hashCode(), b.hashCode());
    }
}

