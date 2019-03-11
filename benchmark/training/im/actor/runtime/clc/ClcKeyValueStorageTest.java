package im.actor.runtime.clc;


import im.actor.runtime.StorageRuntimeProvider;
import im.actor.runtime.storage.KeyValueRecord;
import im.actor.runtime.storage.KeyValueStorage;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ClcKeyValueStorageTest {
    private StorageRuntimeProvider srp;

    private KeyValueStorage kvs;

    @Test
    public void loadItem() {
        Assert.assertNull(kvs.loadItem(0));
    }

    @Test
    public void loadItemWithContext() {
        kvs.addOrUpdateItem(1, "value1".getBytes());
        kvs.addOrUpdateItem(2, "value2".getBytes());
        Assert.assertEquals(new String(kvs.loadItem(1)), "value1");
        Assert.assertEquals(new String(kvs.loadItem(2)), "value2");
        srp.setContext("935");
        kvs = srp.createKeyValue("test");
        kvs.addOrUpdateItem(3, "value3".getBytes());
        kvs.addOrUpdateItem(4, "value4".getBytes());
        Assert.assertEquals(new String(kvs.loadItem(3)), "value3");
        Assert.assertNull(kvs.loadItem(1));
        Assert.assertNull(kvs.loadItem(2));
        srp.setContext(null);
        kvs = srp.createKeyValue("test");
        Assert.assertEquals(new String(kvs.loadItem(1)), "value1");
        Assert.assertEquals(new String(kvs.loadItem(2)), "value2");
    }

    @Test
    public void addOrUpdateItem() {
        // insert
        kvs.addOrUpdateItem(1, "value1".getBytes());
        Assert.assertEquals(new String(kvs.loadItem(1)), "value1");
        // update
        kvs.addOrUpdateItem(1, "value2".getBytes());
        Assert.assertEquals(new String(kvs.loadItem(1)), "value2");
    }

    @Test
    public void addOrUpdateItems() {
        // insert
        List keyValues = Arrays.asList(new KeyValueRecord(1, "value1".getBytes()), new KeyValueRecord(2, "value2".getBytes()));
        kvs.addOrUpdateItems(keyValues);
        Assert.assertEquals(new String(kvs.loadItem(1)), "value1");
        Assert.assertEquals(new String(kvs.loadItem(2)), "value2");
        // update
        keyValues = Arrays.asList(new KeyValueRecord(1, "value3".getBytes()), new KeyValueRecord(2, "value4".getBytes()));
        kvs.addOrUpdateItems(keyValues);
        Assert.assertEquals(new String(kvs.loadItem(1)), "value3");
        Assert.assertEquals(new String(kvs.loadItem(2)), "value4");
    }

    @Test
    public void removeItem() {
        kvs.addOrUpdateItem(1, "value1".getBytes());
        Assert.assertEquals(new String(kvs.loadItem(1)), "value1");
        kvs.removeItem(1);
        Assert.assertNull(kvs.loadItem(1));
    }

    @Test
    public void removeItemWithContext() {
        kvs.addOrUpdateItem(1, "value1".getBytes());
        Assert.assertEquals(new String(kvs.loadItem(1)), "value1");
        srp.setContext("935");
        kvs = srp.createKeyValue("test");
        kvs.addOrUpdateItem(1, "value2".getBytes());
        kvs.removeItem(1);
        Assert.assertNull(kvs.loadItem(1));
        srp.setContext(null);
        kvs = srp.createKeyValue("test");
        Assert.assertEquals(new String(kvs.loadItem(1)), "value1");
    }

    @Test
    public void removeItems() {
        List keyValues = Arrays.asList(new KeyValueRecord(1, "value1".getBytes()), new KeyValueRecord(2, "value2".getBytes()));
        kvs.addOrUpdateItems(keyValues);
        Assert.assertEquals(new String(kvs.loadItem(1)), "value1");
        Assert.assertEquals(new String(kvs.loadItem(2)), "value2");
        kvs.removeItems(new long[]{ 1, 2 });
        Assert.assertNull(kvs.loadItem(1));
        Assert.assertNull(kvs.loadItem(2));
    }

    @Test
    public void clear() {
        List keyValues = Arrays.asList(new KeyValueRecord(1, "value1".getBytes()), new KeyValueRecord(2, "value2".getBytes()));
        kvs.addOrUpdateItems(keyValues);
        Assert.assertEquals(new String(kvs.loadItem(1)), "value1");
        Assert.assertEquals(new String(kvs.loadItem(2)), "value2");
        kvs.clear();
        Assert.assertNull(kvs.loadItem(1));
        Assert.assertNull(kvs.loadItem(2));
    }

    @Test
    public void clearWithContext() {
        List keyValues = Arrays.asList(new KeyValueRecord(1, "value1".getBytes()), new KeyValueRecord(2, "value2".getBytes()));
        kvs.addOrUpdateItems(keyValues);
        Assert.assertEquals(new String(kvs.loadItem(1)), "value1");
        Assert.assertEquals(new String(kvs.loadItem(2)), "value2");
        srp.setContext("935");
        kvs = srp.createKeyValue("test");
        Assert.assertNull(kvs.loadItem(1));
        Assert.assertNull(kvs.loadItem(2));
        List keyValues2 = Arrays.asList(new KeyValueRecord(3, "value3".getBytes()), new KeyValueRecord(4, "value4".getBytes()));
        kvs.addOrUpdateItems(keyValues2);
        Assert.assertEquals(new String(kvs.loadItem(3)), "value3");
        Assert.assertEquals(new String(kvs.loadItem(4)), "value4");
        kvs.clear();
        Assert.assertNull(kvs.loadItem(3));
        Assert.assertNull(kvs.loadItem(4));
        srp.setContext(null);
        kvs = srp.createKeyValue("test");
        Assert.assertEquals(new String(kvs.loadItem(1)), "value1");
        Assert.assertEquals(new String(kvs.loadItem(2)), "value2");
    }
}

