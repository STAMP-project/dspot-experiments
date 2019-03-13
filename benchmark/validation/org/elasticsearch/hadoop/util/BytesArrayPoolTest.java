package org.elasticsearch.hadoop.util;


import org.junit.Assert;
import org.junit.Test;


public class BytesArrayPoolTest {
    @Test
    public void testAddAndReset() throws Exception {
        BytesArrayPool pool = new BytesArrayPool();
        pool.get().bytes("Test");
        pool.get().bytes("Data");
        pool.get().bytes("Rules");
        BytesRef ref = new BytesRef();
        Assert.assertEquals(13, pool.length());
        ref.add(pool);
        Assert.assertEquals("TestDataRules", ref.toString());
        BytesRef ref2 = new BytesRef();
        pool.reset();
        pool.get().bytes("New");
        pool.get().bytes("Info");
        Assert.assertEquals(7, pool.length());
        ref2.add(pool);
        Assert.assertEquals("NewInfo", ref2.toString());
    }
}

