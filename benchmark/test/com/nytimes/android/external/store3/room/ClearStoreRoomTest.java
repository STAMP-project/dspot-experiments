package com.nytimes.android.external.store3.room;


import com.nytimes.android.external.store3.base.Clearable;
import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.room.StoreRoom;
import com.nytimes.android.external.store3.base.room.RoomPersister;
import io.reactivex.Observable;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nonnull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ClearStoreRoomTest {
    @Mock
    ClearStoreRoomTest.RoomClearingPersister persister;

    private AtomicInteger networkCalls;

    private StoreRoom<Integer, BarCode> store;

    @Test
    public void testClearSingleBarCode() {
        // one request should produce one call
        BarCode barcode = new BarCode("type", "key");
        // read from disk after clearing
        // read from disk after fetching from network
        // read from disk on get
        Mockito.when(persister.read(barcode)).thenReturn(Observable.empty()).thenReturn(Observable.just(1)).thenReturn(Observable.empty()).thenReturn(Observable.just(1));// read from disk after making additional network call

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
        Mockito.when(persister.read(barcode1)).thenReturn(Observable.empty()).thenReturn(Observable.just(1)).thenReturn(Observable.empty()).thenReturn(Observable.just(1));// read from disk after making additional network call

        // read from disk after clearing disk cache
        // read from disk after fetching from network
        // read from disk
        Mockito.when(persister.read(barcode2)).thenReturn(Observable.empty()).thenReturn(Observable.just(1)).thenReturn(Observable.empty()).thenReturn(Observable.just(1));// read from disk after making additional network call

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

    // everything will be mocked
    static class RoomClearingPersister implements Clearable<BarCode> , RoomPersister<Integer, Integer, BarCode> {
        @Override
        public void clear(@Nonnull
        BarCode key) {
            throw new RuntimeException();
        }

        @Nonnull
        @Override
        public Observable<Integer> read(@Nonnull
        BarCode barCode) {
            throw new RuntimeException();
        }

        @Override
        public void write(@Nonnull
        BarCode barCode, @Nonnull
        Integer integer) {
            // noop
        }
    }
}

