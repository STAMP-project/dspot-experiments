package com.nytimes.android.external.store3;


import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.Store;
import org.junit.Test;


public class DontCacheErrorsTest {
    boolean shouldThrow;

    private Store<Integer, BarCode> store;

    @Test
    public void testStoreDoesntCacheErrors() throws InterruptedException {
        BarCode barcode = new BarCode("bar", "code");
        shouldThrow = true;
        store.get(barcode).test().assertTerminated().assertError(Exception.class).awaitTerminalEvent();
        shouldThrow = false;
        store.get(barcode).test().assertNoErrors().awaitTerminalEvent();
    }

    @Test
    public void testStoreDoesntCacheErrorsWithResult() throws InterruptedException {
        BarCode barcode = new BarCode("bar", "code");
        shouldThrow = true;
        store.getWithResult(barcode).test().assertTerminated().assertError(Exception.class).awaitTerminalEvent();
        shouldThrow = false;
        store.get(barcode).test().assertNoErrors().awaitTerminalEvent();
    }
}

