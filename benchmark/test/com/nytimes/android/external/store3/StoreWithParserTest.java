package com.nytimes.android.external.store3;


import Result.Source.CACHE;
import Result.Source.NETWORK;
import com.nytimes.android.external.store.util.Result;
import com.nytimes.android.external.store3.base.Fetcher;
import com.nytimes.android.external.store3.base.Parser;
import com.nytimes.android.external.store3.base.Persister;
import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.Store;
import io.reactivex.Maybe;
import io.reactivex.Single;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


public class StoreWithParserTest {
    private static final String DISK = "persister";

    private static final String NETWORK = "fetch";

    @Mock
    Fetcher<String, BarCode> fetcher;

    @Mock
    Persister<String, BarCode> persister;

    @Mock
    Parser<String, String> parser;

    private final BarCode barCode = new BarCode("key", "value");

    @Test
    public void testSimple() throws Exception {
        MockitoAnnotations.initMocks(this);
        Store<String, BarCode> simpleStore = com.nytimes.android.external.store3.base.impl.ParsingStoreBuilder.<String, String>builder().persister(persister).fetcher(fetcher).parser(parser).open();
        Mockito.when(fetcher.fetch(barCode)).thenReturn(Single.just(StoreWithParserTest.NETWORK));
        Mockito.when(persister.read(barCode)).thenReturn(Maybe.<String>empty()).thenReturn(Maybe.just(StoreWithParserTest.DISK));
        Mockito.when(persister.write(barCode, StoreWithParserTest.NETWORK)).thenReturn(Single.just(true));
        Mockito.when(parser.apply(StoreWithParserTest.DISK)).thenReturn(barCode.getKey());
        String value = simpleStore.get(barCode).blockingGet();
        assertThat(value).isEqualTo(barCode.getKey());
        value = simpleStore.get(barCode).blockingGet();
        assertThat(value).isEqualTo(barCode.getKey());
        Mockito.verify(fetcher, Mockito.times(1)).fetch(barCode);
    }

    @Test
    public void testSimpleWithResult() throws Exception {
        MockitoAnnotations.initMocks(this);
        Store<String, BarCode> simpleStore = com.nytimes.android.external.store3.base.impl.ParsingStoreBuilder.<String, String>builder().persister(persister).fetcher(fetcher).parser(parser).open();
        Mockito.when(fetcher.fetch(barCode)).thenReturn(Single.just(StoreWithParserTest.NETWORK));
        Mockito.when(persister.read(barCode)).thenReturn(Maybe.<String>empty()).thenReturn(Maybe.just(StoreWithParserTest.DISK));
        Mockito.when(persister.write(barCode, StoreWithParserTest.NETWORK)).thenReturn(Single.just(true));
        Mockito.when(parser.apply(StoreWithParserTest.DISK)).thenReturn(barCode.getKey());
        Result<String> result = simpleStore.getWithResult(barCode).blockingGet();
        assertThat(result.source()).isEqualTo(Result.Source.NETWORK);
        assertThat(result.value()).isEqualTo(barCode.getKey());
        result = simpleStore.getWithResult(barCode).blockingGet();
        assertThat(result.source()).isEqualTo(CACHE);
        assertThat(result.value()).isEqualTo(barCode.getKey());
        Mockito.verify(fetcher, Mockito.times(1)).fetch(barCode);
    }

    @Test
    public void testSubclass() throws Exception {
        MockitoAnnotations.initMocks(this);
        Store<String, BarCode> simpleStore = new SampleParsingStore(fetcher, persister, parser);
        Mockito.when(fetcher.fetch(barCode)).thenReturn(Single.just(StoreWithParserTest.NETWORK));
        Mockito.when(persister.read(barCode)).thenReturn(Maybe.<String>empty()).thenReturn(Maybe.just(StoreWithParserTest.DISK));
        Mockito.when(persister.write(barCode, StoreWithParserTest.NETWORK)).thenReturn(Single.just(true));
        Mockito.when(parser.apply(StoreWithParserTest.DISK)).thenReturn(barCode.getKey());
        String value = simpleStore.get(barCode).blockingGet();
        assertThat(value).isEqualTo(barCode.getKey());
        value = simpleStore.get(barCode).blockingGet();
        assertThat(value).isEqualTo(barCode.getKey());
        Mockito.verify(fetcher, Mockito.times(1)).fetch(barCode);
    }

    @Test
    public void testSubclassWithResult() throws Exception {
        MockitoAnnotations.initMocks(this);
        Store<String, BarCode> simpleStore = new SampleParsingStore(fetcher, persister, parser);
        Mockito.when(fetcher.fetch(barCode)).thenReturn(Single.just(StoreWithParserTest.NETWORK));
        Mockito.when(persister.read(barCode)).thenReturn(Maybe.<String>empty()).thenReturn(Maybe.just(StoreWithParserTest.DISK));
        Mockito.when(persister.write(barCode, StoreWithParserTest.NETWORK)).thenReturn(Single.just(true));
        Mockito.when(parser.apply(StoreWithParserTest.DISK)).thenReturn(barCode.getKey());
        Result<String> result = simpleStore.getWithResult(barCode).blockingGet();
        assertThat(result.source()).isEqualTo(Result.Source.NETWORK);
        assertThat(result.value()).isEqualTo(barCode.getKey());
        result = simpleStore.getWithResult(barCode).blockingGet();
        assertThat(result.source()).isEqualTo(CACHE);
        assertThat(result.value()).isEqualTo(barCode.getKey());
        Mockito.verify(fetcher, Mockito.times(1)).fetch(barCode);
    }
}

