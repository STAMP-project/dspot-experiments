package com.nytimes.android.external.fs3;


import RecordState.FRESH;
import RecordState.MISSING;
import RecordState.STALE;
import com.nytimes.android.external.store3.base.Fetcher;
import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.Store;
import io.reactivex.Maybe;
import io.reactivex.Single;
import okio.BufferedSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class StoreNetworkBeforeStaleTest {
    Exception sorry = new Exception("sorry");

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
    public void networkBeforeDiskWhenStale() {
        Mockito.when(fetcher.fetch(barCode)).thenReturn(Single.<BufferedSource>error(new Exception()));
        Mockito.when(persister.read(barCode)).thenReturn(Maybe.just(disk1));// get should return from disk

        Mockito.when(persister.getRecordState(barCode)).thenReturn(STALE);
        Mockito.when(persister.write(barCode, network1)).thenReturn(Single.just(true));
        store.get(barCode).test().awaitTerminalEvent();
        InOrder inOrder = Mockito.inOrder(fetcher, persister);
        inOrder.verify(fetcher, Mockito.times(1)).fetch(barCode);
        inOrder.verify(persister, Mockito.times(1)).read(barCode);
        Mockito.verify(persister, Mockito.never()).write(barCode, network1);
    }

    @Test
    public void noNetworkBeforeStaleWhenMissingRecord() {
        Mockito.when(fetcher.fetch(barCode)).thenReturn(Single.just(network1));
        Mockito.when(persister.read(barCode)).thenReturn(Maybe.<BufferedSource>empty(), Maybe.just(disk1));// first call should return

        // empty, second call after network should return the network value
        Mockito.when(persister.getRecordState(barCode)).thenReturn(MISSING);
        Mockito.when(persister.write(barCode, network1)).thenReturn(Single.just(true));
        store.get(barCode).test().awaitTerminalEvent();
        InOrder inOrder = Mockito.inOrder(fetcher, persister);
        inOrder.verify(persister, Mockito.times(1)).read(barCode);
        inOrder.verify(fetcher, Mockito.times(1)).fetch(barCode);
        inOrder.verify(persister, Mockito.times(1)).write(barCode, network1);
        inOrder.verify(persister, Mockito.times(1)).read(barCode);
    }

    @Test
    public void noNetworkBeforeStaleWhenFreshRecord() {
        Mockito.when(persister.read(barCode)).thenReturn(Maybe.just(disk1));// get should return from disk

        Mockito.when(persister.getRecordState(barCode)).thenReturn(FRESH);
        store.get(barCode).test().awaitTerminalEvent();
        Mockito.verify(fetcher, Mockito.never()).fetch(barCode);
        Mockito.verify(persister, Mockito.never()).write(barCode, network1);
        Mockito.verify(persister, Mockito.times(1)).read(barCode);
    }

    @Test
    public void networkBeforeStaleNoNetworkResponse() {
        Single<BufferedSource> singleError = Single.error(sorry);
        Maybe<BufferedSource> maybeError = Maybe.error(sorry);
        Mockito.when(fetcher.fetch(barCode)).thenReturn(singleError);
        Mockito.when(persister.read(barCode)).thenReturn(maybeError, maybeError);// first call should return

        // empty, second call after network should return the network value
        Mockito.when(persister.getRecordState(barCode)).thenReturn(MISSING);
        Mockito.when(persister.write(barCode, network1)).thenReturn(Single.just(true));
        store.get(barCode).test().assertError(sorry);
        InOrder inOrder = Mockito.inOrder(fetcher, persister);
        inOrder.verify(persister, Mockito.times(1)).read(barCode);
        inOrder.verify(fetcher, Mockito.times(1)).fetch(barCode);
        inOrder.verify(persister, Mockito.times(1)).read(barCode);
    }
}

