package com.nytimes.android.external.fs3;


import RecordState.FRESH;
import RecordState.STALE;
import com.nytimes.android.external.store3.base.Fetcher;
import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.Store;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import okio.BufferedSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class StoreRefreshWhenStaleTest {
    @Mock
    Fetcher<BufferedSource, BarCode> fetcher;

    @Mock
    RecordPersister persister;

    @Mock
    BufferedSource network1;

    @Mock
    BufferedSource network2;

    @Mock
    BufferedSource disk1;

    @Mock
    BufferedSource disk2;

    private final BarCode barCode = new BarCode("key", "value");

    private Store<BufferedSource, BarCode> store;

    @Test
    public void diskWasRefreshedWhenStaleRecord() {
        Mockito.when(fetcher.fetch(barCode)).thenReturn(Single.just(network1));
        Mockito.when(persister.read(barCode)).thenReturn(Maybe.just(disk1));// get should return from disk

        Mockito.when(persister.getRecordState(barCode)).thenReturn(STALE);
        Mockito.when(persister.write(barCode, network1)).thenReturn(Single.just(true));
        store.get(barCode).test().awaitTerminalEvent();
        Mockito.verify(fetcher, Mockito.times(1)).fetch(barCode);
        Mockito.verify(persister, Mockito.times(2)).getRecordState(barCode);
        Mockito.verify(persister, Mockito.times(1)).write(barCode, network1);
        Mockito.verify(persister, Mockito.times(2)).read(barCode);// reads from disk a second time when backfilling

    }

    @Test
    public void diskWasNotRefreshedWhenFreshRecord() {
        Mockito.when(fetcher.fetch(barCode)).thenReturn(Single.just(network1));
        // get should return from disk
        Mockito.when(persister.read(barCode)).thenReturn(Maybe.just(disk1)).thenReturn(Maybe.just(disk2));// backfill should read from disk again

        Mockito.when(persister.getRecordState(barCode)).thenReturn(FRESH);
        Mockito.when(persister.write(barCode, network1)).thenReturn(Single.just(true));
        TestObserver testObserver = store.get(barCode).test();
        testObserver.awaitTerminalEvent();
        testObserver.assertNoErrors();
        testObserver.assertResult(disk1);
        Mockito.verify(fetcher, Mockito.times(0)).fetch(barCode);
        Mockito.verify(persister, Mockito.times(1)).getRecordState(barCode);
        store.clear(barCode);
        testObserver = store.get(barCode).test();
        testObserver.awaitTerminalEvent();
        testObserver.assertResult(disk2);
        Mockito.verify(fetcher, Mockito.times(0)).fetch(barCode);
        Mockito.verify(persister, Mockito.times(2)).getRecordState(barCode);
    }
}

