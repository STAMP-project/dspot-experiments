package com.nytimes.android.external.store3;


import com.nytimes.android.external.store3.base.Clearable;
import com.nytimes.android.external.store3.base.Persister;
import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.Store;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nonnull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class GetRefreshingTest {
    @Mock
    GetRefreshingTest.ClearingPersister persister;

    AtomicInteger networkCalls;

    private Store<Integer, BarCode> store;

    @Test
    public void testRefreshOnClear() {
        BarCode barcode = new BarCode("type", "key");
        // read from disk after clearing disk cache
        // read from disk after fetching from network
        // read from disk
        Mockito.when(persister.read(barcode)).thenReturn(Maybe.<Integer>empty()).thenReturn(Maybe.just(1)).thenReturn(Maybe.<Integer>empty()).thenReturn(Maybe.just(1));// read from disk after making additional network call

        Mockito.when(persister.write(barcode, 1)).thenReturn(Single.just(true));
        Mockito.when(persister.write(barcode, 2)).thenReturn(Single.just(true));
        TestObserver<Integer> refreshingObservable = store.getRefreshing(barcode).test();
        refreshingObservable.assertValueCount(1);
        assertThat(networkCalls.intValue()).isEqualTo(1);
        // clearing the store should produce another network call
        store.clear(barcode);
        refreshingObservable.assertValueCount(2);
        assertThat(networkCalls.intValue()).isEqualTo(2);
        store.get(barcode).test().awaitTerminalEvent();
        refreshingObservable.assertValueCount(2);
        assertThat(networkCalls.intValue()).isEqualTo(2);
    }

    @Test
    public void testRefreshOnClearAll() {
        BarCode barcode1 = new BarCode("type", "key");
        BarCode barcode2 = new BarCode("type", "key2");
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
        TestObserver<Integer> testObservable1 = store.getRefreshing(barcode1).test();
        TestObserver<Integer> testObservable2 = store.getRefreshing(barcode2).test();
        testObservable1.assertValueCount(1);
        testObservable2.assertValueCount(1);
        assertThat(networkCalls.intValue()).isEqualTo(2);
        store.clear();
        assertThat(networkCalls.intValue()).isEqualTo(4);
    }

    // everything will be mocked
    static class ClearingPersister implements Clearable<BarCode> , Persister<Integer, BarCode> {
        @Override
        public void clear(@Nonnull
        BarCode key) {
            throw new RuntimeException();
        }

        @Nonnull
        @Override
        public Maybe<Integer> read(@Nonnull
        BarCode barCode) {
            throw new RuntimeException();
        }

        @Nonnull
        @Override
        public Single<Boolean> write(@Nonnull
        BarCode barCode, @Nonnull
        Integer integer) {
            throw new RuntimeException();
        }
    }
}

