package com.nytimes.android.sample;


import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.Store;
import junit.framework.Assert;
import org.junit.Test;


public class StoreIntegrationTest {
    private Store<String, BarCode> testStore;

    @Test
    public void testRepeatedGet() throws Exception {
        String first = testStore.get(BarCode.empty()).blockingGet();
        Assert.assertEquals(first, "hello");
    }
}

