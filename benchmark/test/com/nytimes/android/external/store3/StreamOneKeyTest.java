package com.nytimes.android.external.store3;


import com.nytimes.android.external.store3.base.Fetcher;
import com.nytimes.android.external.store3.base.Persister;
import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.Store;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class StreamOneKeyTest {
    private static final String TEST_ITEM = "test";

    private static final String TEST_ITEM2 = "test2";

    @Mock
    Fetcher<String, BarCode> fetcher;

    @Mock
    Persister<String, BarCode> persister;

    private final BarCode barCode = new BarCode("key", "value");

    private final BarCode barCode2 = new BarCode("key2", "value2");

    private Store<String, BarCode> store;

    @Test
    public void testStream() {
        TestObserver<String> streamObservable = store.stream(barCode).test();
        // first time we subscribe to stream it will fail getting from memory & disk and instead
        // fetch from network, write to disk and notifiy subscribers
        streamObservable.assertValueCount(1);
        store.clear();
        // fetch should notify subscribers again
        store.fetch(barCode).test().awaitCount(1);
        streamObservable.assertValues(StreamOneKeyTest.TEST_ITEM, StreamOneKeyTest.TEST_ITEM2);
        // get for another barcode should not trigger a stream for barcode1
        Mockito.when(fetcher.fetch(barCode2)).thenReturn(Single.just(StreamOneKeyTest.TEST_ITEM));
        Mockito.when(persister.read(barCode2)).thenReturn(Maybe.empty()).thenReturn(Maybe.just(StreamOneKeyTest.TEST_ITEM));
        Mockito.when(persister.write(barCode2, StreamOneKeyTest.TEST_ITEM)).thenReturn(Single.just(true));
        store.get(barCode2).test().awaitCount(1);
        streamObservable.assertValueCount(2);
    }
}

