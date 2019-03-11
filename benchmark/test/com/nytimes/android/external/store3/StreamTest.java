package com.nytimes.android.external.store3;


import com.nytimes.android.external.store3.base.Fetcher;
import com.nytimes.android.external.store3.base.Persister;
import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.Store;
import io.reactivex.observers.TestObserver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class StreamTest {
    private static final String TEST_ITEM = "test";

    @Mock
    Fetcher<String, BarCode> fetcher;

    @Mock
    Persister<String, BarCode> persister;

    private final BarCode barCode = new BarCode("key", "value");

    private Store<String, BarCode> store;

    @Test
    public void testStream() {
        TestObserver<String> streamObservable = store.stream().test();
        streamObservable.assertValueCount(0);
        store.get(barCode).subscribe();
        streamObservable.assertValueCount(1);
    }

    @Test
    public void testStreamEmitsOnlyFreshData() {
        store.get(barCode).subscribe();
        TestObserver<String> streamObservable = store.stream().test();
        streamObservable.assertValueCount(0);
    }
}

