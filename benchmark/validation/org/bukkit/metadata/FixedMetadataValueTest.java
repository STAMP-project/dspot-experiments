package org.bukkit.metadata;


import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.TestPlugin;
import org.junit.Assert;
import org.junit.Test;


public class FixedMetadataValueTest {
    private Plugin plugin = new TestPlugin("X");

    private FixedMetadataValue subject;

    @Test
    public void testBasic() {
        subject = new FixedMetadataValue(plugin, new Integer(50));
        Assert.assertSame(plugin, subject.getOwningPlugin());
        Assert.assertEquals(new Integer(50), subject.value());
    }

    @Test
    public void testNumberTypes() {
        subject = new FixedMetadataValue(plugin, new Integer(5));
        Assert.assertEquals(new Integer(5), subject.value());
        Assert.assertEquals(5, subject.asInt());
        Assert.assertEquals(true, subject.asBoolean());
        Assert.assertEquals(5, subject.asByte());
        Assert.assertEquals(5.0, subject.asFloat(), 1.0E-9);
        Assert.assertEquals(5.0, subject.asDouble(), 1.0E-9);
        Assert.assertEquals(5L, subject.asLong());
        Assert.assertEquals(5, subject.asShort());
        Assert.assertEquals("5", subject.asString());
    }

    @Test
    public void testInvalidateDoesNothing() {
        Object o = new Object();
        subject = new FixedMetadataValue(plugin, o);
        subject.invalidate();
        Assert.assertSame(o, subject.value());
    }
}

