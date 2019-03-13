package org.bukkit.metadata;


import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.TestPlugin;
import org.junit.Assert;
import org.junit.Test;


public class MetadataValueAdapterTest {
    private TestPlugin plugin = new TestPlugin("x");

    @Test
    public void testAdapterBasics() {
        MetadataValueAdapterTest.IncrementingMetaValue mv = new MetadataValueAdapterTest.IncrementingMetaValue(plugin);
        // check getOwningPlugin
        Assert.assertEquals(getOwningPlugin(), this.plugin);
        // Check value-getting and invalidation.
        Assert.assertEquals(new Integer(1), mv.value());
        Assert.assertEquals(new Integer(2), mv.value());
        mv.invalidate();
        Assert.assertEquals(new Integer(1), mv.value());
    }

    @Test
    public void testAdapterConversions() {
        MetadataValueAdapterTest.IncrementingMetaValue mv = new MetadataValueAdapterTest.IncrementingMetaValue(plugin);
        Assert.assertEquals(1, asInt());
        Assert.assertEquals(2L, asLong());
        Assert.assertEquals(3.0, asFloat(), 0.001);
        Assert.assertEquals(4, asByte());
        Assert.assertEquals(5.0, asDouble(), 0.001);
        Assert.assertEquals(6, asShort());
        Assert.assertEquals("7", asString());
    }

    /**
     * Boolean conversion is non-trivial, we want to test it thoroughly.
     */
    @Test
    public void testBooleanConversion() {
        // null is False.
        Assert.assertEquals(false, simpleValue(null).asBoolean());
        // String to boolean.
        Assert.assertEquals(true, simpleValue("True").asBoolean());
        Assert.assertEquals(true, simpleValue("TRUE").asBoolean());
        Assert.assertEquals(false, simpleValue("false").asBoolean());
        // Number to boolean.
        Assert.assertEquals(true, simpleValue(1).asBoolean());
        Assert.assertEquals(true, simpleValue(5.0).asBoolean());
        Assert.assertEquals(false, simpleValue(0).asBoolean());
        Assert.assertEquals(false, simpleValue(0.1).asBoolean());
        // Boolean as boolean, of course.
        Assert.assertEquals(true, simpleValue(Boolean.TRUE).asBoolean());
        Assert.assertEquals(false, simpleValue(Boolean.FALSE).asBoolean());
        // any object that is not null and not a Boolean, String, or Number is true.
        Assert.assertEquals(true, simpleValue(new Object()).asBoolean());
    }

    /**
     * Test String conversions return an empty string when given null.
     */
    @Test
    public void testStringConversionNull() {
        Assert.assertEquals("", simpleValue(null).asString());
    }

    /**
     * A sample non-trivial MetadataValueAdapter implementation.
     *
     * The rationale for implementing an incrementing value is to have a value
     * which changes with every call to value(). This is important for testing
     * because we want to make sure all the tested conversions are calling the
     * value() method exactly once and no caching is going on.
     */
    class IncrementingMetaValue extends MetadataValueAdapter {
        private int internalValue = 0;

        protected IncrementingMetaValue(Plugin owningPlugin) {
            super(owningPlugin);
        }

        public Object value() {
            return ++(internalValue);
        }

        public void invalidate() {
            internalValue = 0;
        }
    }
}

