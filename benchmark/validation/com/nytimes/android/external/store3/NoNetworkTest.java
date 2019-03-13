package com.nytimes.android.external.store3;


import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.Store;
import org.junit.Test;


/**
 * Created by 206847 on 5/3/17.
 */
public class NoNetworkTest {
    private static final RuntimeException EXCEPTION = new RuntimeException();

    private Store<Object, BarCode> store;

    @Test
    public void testNoNetwork() throws Exception {
        store.get(new BarCode("test", "test")).test().assertError(NoNetworkTest.EXCEPTION);
    }

    @Test
    public void testNoNetworkWithResult() throws Exception {
        store.getWithResult(new BarCode("test", "test")).test().assertError(NoNetworkTest.EXCEPTION);
    }
}

