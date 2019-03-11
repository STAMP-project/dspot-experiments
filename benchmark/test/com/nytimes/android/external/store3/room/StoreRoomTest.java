package com.nytimes.android.external.store3.room;


import StalePolicy.UNSPECIFIED;
import com.nytimes.android.external.store3.base.Fetcher;
import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.room.StoreRoom;
import com.nytimes.android.external.store3.base.room.RoomPersister;
import io.reactivex.Observable;
import io.reactivex.Single;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class StoreRoomTest {
    private static final String DISK = "disk";

    private static final String NETWORK = "fetch";

    final AtomicInteger counter = new AtomicInteger(0);

    @Mock
    Fetcher<String, BarCode> fetcher;

    @Mock
    RoomPersister<String, String, BarCode> persister;

    private final BarCode barCode = new BarCode("key", "value");

    @Test
    public void testSimple() {
        StoreRoom<String, BarCode> simpleStore = StoreRoom.from(fetcher, persister, UNSPECIFIED);
        Mockito.when(fetcher.fetch(barCode)).thenReturn(Single.just(StoreRoomTest.NETWORK));
        Mockito.when(persister.read(barCode)).thenReturn(Observable.<String>empty()).thenReturn(Observable.just(StoreRoomTest.DISK));
        String value = simpleStore.get(barCode).blockingFirst();
        assertThat(value).isEqualTo(StoreRoomTest.DISK);
        value = simpleStore.get(barCode).blockingFirst();
        assertThat(value).isEqualTo(StoreRoomTest.DISK);
        Mockito.verify(fetcher, Mockito.times(1)).fetch(barCode);
    }

    @Test
    public void testDoubleTap() {
        StoreRoom<String, BarCode> simpleStore = StoreRoom.from(fetcher, persister, UNSPECIFIED);
        Single<String> networkSingle = Single.create(( emitter) -> {
            if ((counter.incrementAndGet()) == 1) {
                emitter.onSuccess(NETWORK);
            } else {
                emitter.onError(new RuntimeException("Yo Dawg your inflight is broken"));
            }
        });
        Mockito.when(fetcher.fetch(barCode)).thenReturn(networkSingle);
        Mockito.when(persister.read(barCode)).thenReturn(Observable.empty()).thenReturn(Observable.just(StoreRoomTest.DISK));
        String response = simpleStore.get(barCode).zipWith(simpleStore.get(barCode), ( s, s2) -> "hello").blockingFirst();
        assertThat(response).isEqualTo("hello");
        Mockito.verify(fetcher, Mockito.times(1)).fetch(barCode);
    }
}

