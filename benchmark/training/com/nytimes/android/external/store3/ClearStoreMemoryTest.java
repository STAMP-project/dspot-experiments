package com.nytimes.android.external.store3;


import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.Store;
import org.junit.Test;


public class ClearStoreMemoryTest {
    int networkCalls = 0;

    private Store<Integer, BarCode> store;

    @Test
    public void testClearSingleBarCode() {
        // one request should produce one call
        BarCode barcode = new BarCode("type", "key");
        store.get(barcode).test().awaitTerminalEvent();
        assertThat(networkCalls).isEqualTo(1);
        // after clearing the memory another call should be made
        store.clearMemory(barcode);
        store.get(barcode).test().awaitTerminalEvent();
        assertThat(networkCalls).isEqualTo(2);
    }

    @Test
    public void testClearAllBarCodes() {
        BarCode b1 = new BarCode("type1", "key1");
        BarCode b2 = new BarCode("type2", "key2");
        // each request should produce one call
        store.get(b1).test().awaitTerminalEvent();
        store.get(b2).test().awaitTerminalEvent();
        assertThat(networkCalls).isEqualTo(2);
        store.clearMemory();
        // after everything is cleared each request should produce another 2 calls
        store.get(b1).test().awaitTerminalEvent();
        store.get(b2).test().awaitTerminalEvent();
        assertThat(networkCalls).isEqualTo(4);
    }
}

