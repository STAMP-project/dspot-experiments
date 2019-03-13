package org.bukkit.metadata;


import java.util.List;
import java.util.concurrent.Callable;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.TestPlugin;
import org.junit.Assert;
import org.junit.Test;


public class MetadataStoreTest {
    private Plugin pluginX = new TestPlugin("x");

    private Plugin pluginY = new TestPlugin("y");

    MetadataStoreTest.StringMetadataStore subject = new MetadataStoreTest.StringMetadataStore();

    @Test
    public void testMetadataStore() {
        setMetadata("subject", "key", new FixedMetadataValue(pluginX, 10));
        Assert.assertTrue(hasMetadata("subject", "key"));
        List<MetadataValue> values = getMetadata("subject", "key");
        Assert.assertEquals(10, values.get(0).value());
    }

    @Test
    public void testMetadataNotPresent() {
        Assert.assertFalse(hasMetadata("subject", "key"));
        List<MetadataValue> values = getMetadata("subject", "key");
        Assert.assertTrue(values.isEmpty());
    }

    @Test
    public void testInvalidateAll() {
        final MetadataStoreTest.Counter counter = new MetadataStoreTest.Counter();
        subject.setMetadata("subject", "key", new LazyMetadataValue(pluginX, new Callable<Object>() {
            public Object call() throws Exception {
                counter.increment();
                return 10;
            }
        }));
        Assert.assertTrue(hasMetadata("subject", "key"));
        subject.getMetadata("subject", "key").get(0).value();
        subject.invalidateAll(pluginX);
        subject.getMetadata("subject", "key").get(0).value();
        Assert.assertEquals(2, counter.value());
    }

    @Test
    public void testInvalidateAllButActuallyNothing() {
        final MetadataStoreTest.Counter counter = new MetadataStoreTest.Counter();
        subject.setMetadata("subject", "key", new LazyMetadataValue(pluginX, new Callable<Object>() {
            public Object call() throws Exception {
                counter.increment();
                return 10;
            }
        }));
        Assert.assertTrue(hasMetadata("subject", "key"));
        subject.getMetadata("subject", "key").get(0).value();
        subject.invalidateAll(pluginY);
        subject.getMetadata("subject", "key").get(0).value();
        Assert.assertEquals(1, counter.value());
    }

    @Test
    public void testMetadataReplace() {
        setMetadata("subject", "key", new FixedMetadataValue(pluginX, 10));
        setMetadata("subject", "key", new FixedMetadataValue(pluginY, 10));
        setMetadata("subject", "key", new FixedMetadataValue(pluginX, 20));
        for (MetadataValue mv : subject.getMetadata("subject", "key")) {
            if (mv.getOwningPlugin().equals(pluginX)) {
                Assert.assertEquals(20, mv.value());
            }
            if (mv.getOwningPlugin().equals(pluginY)) {
                Assert.assertEquals(10, mv.value());
            }
        }
    }

    @Test
    public void testMetadataRemove() {
        setMetadata("subject", "key", new FixedMetadataValue(pluginX, 10));
        setMetadata("subject", "key", new FixedMetadataValue(pluginY, 20));
        subject.removeMetadata("subject", "key", pluginX);
        Assert.assertTrue(hasMetadata("subject", "key"));
        Assert.assertEquals(1, subject.getMetadata("subject", "key").size());
        Assert.assertEquals(20, subject.getMetadata("subject", "key").get(0).value());
    }

    @Test
    public void testMetadataRemoveLast() {
        setMetadata("subject", "key", new FixedMetadataValue(pluginX, 10));
        subject.removeMetadata("subject", "key", pluginX);
        Assert.assertFalse(hasMetadata("subject", "key"));
        Assert.assertEquals(0, subject.getMetadata("subject", "key").size());
    }

    @Test
    public void testMetadataRemoveForNonExistingPlugin() {
        setMetadata("subject", "key", new FixedMetadataValue(pluginX, 10));
        subject.removeMetadata("subject", "key", pluginY);
        Assert.assertTrue(hasMetadata("subject", "key"));
        Assert.assertEquals(1, subject.getMetadata("subject", "key").size());
        Assert.assertEquals(10, subject.getMetadata("subject", "key").get(0).value());
    }

    @Test
    public void testHasMetadata() {
        setMetadata("subject", "key", new FixedMetadataValue(pluginX, 10));
        Assert.assertTrue(hasMetadata("subject", "key"));
        Assert.assertFalse(hasMetadata("subject", "otherKey"));
    }

    private class StringMetadataStore extends MetadataStoreBase<String> implements MetadataStore<String> {
        @Override
        protected String disambiguate(String subject, String metadataKey) {
            return (subject + ":") + metadataKey;
        }
    }

    private class Counter {
        int c = 0;

        public void increment() {
            (c)++;
        }

        public int value() {
            return c;
        }
    }
}

