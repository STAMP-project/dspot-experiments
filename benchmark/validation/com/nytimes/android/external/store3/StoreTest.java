package com.nytimes.android.external.store3;


import Result.Source.CACHE;
import Result.Source.NETWORK;
import com.nytimes.android.external.cache3.Cache;
import com.nytimes.android.external.cache3.CacheBuilder;
import com.nytimes.android.external.store.util.Result;
import com.nytimes.android.external.store3.base.Fetcher;
import com.nytimes.android.external.store3.base.Persister;
import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.RealStore;
import com.nytimes.android.external.store3.base.impl.Store;
import com.nytimes.android.external.store3.util.NoopPersister;
import io.reactivex.Maybe;
import io.reactivex.Single;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class StoreTest {
    private static final String DISK = "disk";

    private static final String NETWORK = "fetch";

    private static final String MEMORY = "memory";

    final AtomicInteger counter = new AtomicInteger(0);

    @Mock
    Fetcher<String, BarCode> fetcher;

    @Mock
    Persister<String, BarCode> persister;

    private final BarCode barCode = new BarCode("key", "value");

    @Test
    public void testSimple() {
        Store<String, BarCode> simpleStore = com.nytimes.android.external.store3.base.impl.StoreBuilder.<String>barcode().persister(persister).fetcher(fetcher).open();
        Mockito.when(fetcher.fetch(barCode)).thenReturn(Single.just(StoreTest.NETWORK));
        Mockito.when(persister.read(barCode)).thenReturn(Maybe.<String>empty()).thenReturn(Maybe.just(StoreTest.DISK));
        Mockito.when(persister.write(barCode, StoreTest.NETWORK)).thenReturn(Single.just(true));
        String value = simpleStore.get(barCode).blockingGet();
        assertThat(value).isEqualTo(StoreTest.DISK);
        value = simpleStore.get(barCode).blockingGet();
        assertThat(value).isEqualTo(StoreTest.DISK);
        Mockito.verify(fetcher, Mockito.times(1)).fetch(barCode);
    }

    @Test
    public void testSimpleWithResult() {
        Store<String, BarCode> simpleStore = com.nytimes.android.external.store3.base.impl.StoreBuilder.<String>barcode().persister(persister).fetcher(fetcher).open();
        Mockito.when(fetcher.fetch(barCode)).thenReturn(Single.just(StoreTest.NETWORK));
        Mockito.when(persister.read(barCode)).thenReturn(Maybe.<String>empty()).thenReturn(Maybe.just(StoreTest.DISK));
        Mockito.when(persister.write(barCode, StoreTest.NETWORK)).thenReturn(Single.just(true));
        Result<String> result = simpleStore.getWithResult(barCode).blockingGet();
        assertThat(result.source()).isEqualTo(Result.Source.NETWORK);
        assertThat(result.value()).isEqualTo(StoreTest.DISK);
        result = simpleStore.getWithResult(barCode).blockingGet();
        assertThat(result.source()).isEqualTo(CACHE);
        assertThat(result.value()).isEqualTo(StoreTest.DISK);
        Mockito.verify(fetcher, Mockito.times(1)).fetch(barCode);
    }

    @Test
    public void testDoubleTap() {
        Store<String, BarCode> simpleStore = com.nytimes.android.external.store3.base.impl.StoreBuilder.<String>barcode().persister(persister).fetcher(fetcher).open();
        Single<String> networkSingle = Single.create(( emitter) -> {
            if ((counter.incrementAndGet()) == 1) {
                emitter.onSuccess(NETWORK);
            } else {
                emitter.onError(new RuntimeException("Yo Dawg your inflight is broken"));
            }
        });
        Mockito.when(fetcher.fetch(barCode)).thenReturn(networkSingle);
        Mockito.when(persister.read(barCode)).thenReturn(Maybe.<String>empty()).thenReturn(Maybe.just(StoreTest.DISK));
        Mockito.when(persister.write(barCode, StoreTest.NETWORK)).thenReturn(Single.just(true));
        String response = simpleStore.get(barCode).zipWith(simpleStore.get(barCode), ( s, s2) -> "hello").blockingGet();
        assertThat(response).isEqualTo("hello");
        Mockito.verify(fetcher, Mockito.times(1)).fetch(barCode);
    }

    @Test
    public void testDoubleTapWithResult() {
        Store<String, BarCode> simpleStore = com.nytimes.android.external.store3.base.impl.StoreBuilder.<String>barcode().persister(persister).fetcher(fetcher).open();
        Single<String> networkSingle = Single.create(( emitter) -> {
            if ((counter.incrementAndGet()) == 1) {
                emitter.onSuccess(NETWORK);
            } else {
                emitter.onError(new RuntimeException("Yo Dawg your inflight is broken"));
            }
        });
        Mockito.when(fetcher.fetch(barCode)).thenReturn(networkSingle);
        Mockito.when(persister.read(barCode)).thenReturn(Maybe.<String>empty()).thenReturn(Maybe.just(StoreTest.DISK));
        Mockito.when(persister.write(barCode, StoreTest.NETWORK)).thenReturn(Single.just(true));
        Result<String> response = simpleStore.getWithResult(barCode).zipWith(simpleStore.getWithResult(barCode), ( s, s2) -> Result.createFromNetwork("hello")).blockingGet();
        assertThat(response.source()).isEqualTo(Result.Source.NETWORK);
        assertThat(response.value()).isEqualTo("hello");
        Mockito.verify(fetcher, Mockito.times(1)).fetch(barCode);
    }

    @Test
    public void testSubclass() {
        RealStore<String, BarCode> simpleStore = new SampleStore(fetcher, persister);
        simpleStore.clear();
        Mockito.when(fetcher.fetch(barCode)).thenReturn(Single.just(StoreTest.NETWORK));
        Mockito.when(persister.read(barCode)).thenReturn(Maybe.<String>empty()).thenReturn(Maybe.just(StoreTest.DISK));
        Mockito.when(persister.write(barCode, StoreTest.NETWORK)).thenReturn(Single.just(true));
        String value = simpleStore.get(barCode).blockingGet();
        assertThat(value).isEqualTo(StoreTest.DISK);
        value = simpleStore.get(barCode).blockingGet();
        assertThat(value).isEqualTo(StoreTest.DISK);
        Mockito.verify(fetcher, Mockito.times(1)).fetch(barCode);
    }

    @Test
    public void testSubclassWithResult() {
        RealStore<String, BarCode> simpleStore = new SampleStore(fetcher, persister);
        simpleStore.clear();
        Mockito.when(fetcher.fetch(barCode)).thenReturn(Single.just(StoreTest.NETWORK));
        Mockito.when(persister.read(barCode)).thenReturn(Maybe.<String>empty()).thenReturn(Maybe.just(StoreTest.DISK));
        Mockito.when(persister.write(barCode, StoreTest.NETWORK)).thenReturn(Single.just(true));
        Result<String> result = simpleStore.getWithResult(barCode).blockingGet();
        assertThat(result.source()).isEqualTo(Result.Source.NETWORK);
        assertThat(result.value()).isEqualTo(StoreTest.DISK);
        result = simpleStore.getWithResult(barCode).blockingGet();
        assertThat(result.source()).isEqualTo(CACHE);
        assertThat(result.value()).isEqualTo(StoreTest.DISK);
        Mockito.verify(fetcher, Mockito.times(1)).fetch(barCode);
    }

    @Test
    public void testNoopAndDefault() {
        Persister<String, BarCode> persister = Mockito.spy(NoopPersister.<String, BarCode>create());
        RealStore<String, BarCode> simpleStore = new SampleStore(fetcher, persister);
        Mockito.when(fetcher.fetch(barCode)).thenReturn(Single.just(StoreTest.NETWORK));
        String value = simpleStore.get(barCode).blockingGet();
        Mockito.verify(fetcher, Mockito.times(1)).fetch(barCode);
        Mockito.verify(persister, Mockito.times(1)).write(barCode, StoreTest.NETWORK);
        Mockito.verify(persister, Mockito.times(2)).read(barCode);
        assertThat(value).isEqualTo(StoreTest.NETWORK);
        value = simpleStore.get(barCode).blockingGet();
        Mockito.verify(persister, Mockito.times(2)).read(barCode);
        Mockito.verify(persister, Mockito.times(1)).write(barCode, StoreTest.NETWORK);
        Mockito.verify(fetcher, Mockito.times(1)).fetch(barCode);
        assertThat(value).isEqualTo(StoreTest.NETWORK);
    }

    @Test
    public void testNoopAndDefaultWithResult() {
        Persister<String, BarCode> persister = Mockito.spy(NoopPersister.<String, BarCode>create());
        RealStore<String, BarCode> simpleStore = new SampleStore(fetcher, persister);
        Mockito.when(fetcher.fetch(barCode)).thenReturn(Single.just(StoreTest.NETWORK));
        Result<String> value = simpleStore.getWithResult(barCode).blockingGet();
        Mockito.verify(fetcher, Mockito.times(1)).fetch(barCode);
        Mockito.verify(persister, Mockito.times(1)).write(barCode, StoreTest.NETWORK);
        Mockito.verify(persister, Mockito.times(2)).read(barCode);
        assertThat(value.source()).isEqualTo(Result.Source.NETWORK);
        assertThat(value.value()).isEqualTo(StoreTest.NETWORK);
        value = simpleStore.getWithResult(barCode).blockingGet();
        Mockito.verify(persister, Mockito.times(2)).read(barCode);
        Mockito.verify(persister, Mockito.times(1)).write(barCode, StoreTest.NETWORK);
        Mockito.verify(fetcher, Mockito.times(1)).fetch(barCode);
        assertThat(value.source()).isEqualTo(CACHE);
        assertThat(value.value()).isEqualTo(StoreTest.NETWORK);
    }

    @Test
    public void testEquivalence() {
        Cache<BarCode, String> cache = CacheBuilder.newBuilder().maximumSize(1).expireAfterAccess(Long.MAX_VALUE, TimeUnit.SECONDS).build();
        cache.put(barCode, StoreTest.MEMORY);
        String value = cache.getIfPresent(barCode);
        assertThat(value).isEqualTo(StoreTest.MEMORY);
        value = cache.getIfPresent(new BarCode(barCode.getType(), barCode.getKey()));
        assertThat(value).isEqualTo(StoreTest.MEMORY);
    }
}

