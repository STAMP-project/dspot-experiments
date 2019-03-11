package com.nytimes.android.external.store3;


import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.Store;
import io.reactivex.Maybe;
import io.reactivex.Single;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ClearStoreTest {
    @Mock
    GetRefreshingTest.ClearingPersister persister;

    AtomicInteger networkCalls;

    private Store<Integer, BarCode> store;

    @Test
    public void testClearSingleBarCode() {
        // one request should produce one call
        BarCode barcode = new BarCode("type", "key");
        // read from disk after clearing
        // read from disk after fetching from network
        // read from disk on get
        Mockito.when(persister.read(barcode)).thenReturn(Maybe.<Integer>empty()).thenReturn(Maybe.just(1)).thenReturn(Maybe.<Integer>empty()).thenReturn(Maybe.just(1));// read from disk after making additional network call

        Mockito.when(persister.write(barcode, 1)).thenReturn(Single.just(true));
        Mockito.when(persister.write(barcode, 2)).thenReturn(Single.just(true));
        store.get(barcode).test().awaitTerminalEvent();
        assertThat(networkCalls.intValue()).isEqualTo(1);
        // after clearing the memory another call should be made
        store.clear(barcode);
        store.get(barcode).test().awaitTerminalEvent();
        Mockito.verify(persister).clear(barcode);
        assertThat(networkCalls.intValue()).isEqualTo(2);
    }

    @Test
    public void testClearAllBarCodes() {
        BarCode barcode1 = new BarCode("type1", "key1");
        BarCode barcode2 = new BarCode("type2", "key2");
        // read from disk after clearing disk cache
        // read from disk after fetching from network
        // read from disk
        Mockito.when(persister.read(barcode1)).thenReturn(Maybe.<Integer>empty()).thenReturn(Maybe.just(1)).thenReturn(Maybe.<Integer>empty()).thenReturn(Maybe.just(1));// read from disk after making additional network call

        Mockito.when(persister.write(barcode1, 1)).thenReturn(Single.just(true));
        Mockito.when(persister.write(barcode1, 2)).thenReturn(Single.just(true));
        // read from disk after clearing disk cache
        // read from disk after fetching from network
        // read from disk
        Mockito.when(persister.read(barcode2)).thenReturn(Maybe.<Integer>empty()).thenReturn(Maybe.just(1)).thenReturn(Maybe.<Integer>empty()).thenReturn(Maybe.just(1));// read from disk after making additional network call

        Mockito.when(persister.write(barcode2, 1)).thenReturn(Single.just(true));
        Mockito.when(persister.write(barcode2, 2)).thenReturn(Single.just(true));
        // each request should produce one call
        store.get(barcode1).test().awaitTerminalEvent();
        store.get(barcode2).test().awaitTerminalEvent();
        assertThat(networkCalls.intValue()).isEqualTo(2);
        store.clear();
        // after everything is cleared each request should produce another 2 calls
        store.get(barcode1).test().awaitTerminalEvent();
        store.get(barcode2).test().awaitTerminalEvent();
        assertThat(networkCalls.intValue()).isEqualTo(4);
    }
}

