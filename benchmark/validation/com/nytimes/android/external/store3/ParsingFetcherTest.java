package com.nytimes.android.external.store3;


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


public class ParsingFetcherTest {
    static final String RAW_DATA = "Test data.";

    static final String PARSED = "DATA PARSED";

    @Mock
    Fetcher<String, BarCode> fetcher;

    @Mock
    Parser<String, String> parser;

    @Mock
    Persister<String, BarCode> persister;

    private final BarCode barCode = new BarCode("key", "value");

    @Test
    public void testPersistFetcher() {
        Store<String, BarCode> simpleStore = com.nytimes.android.external.store3.base.impl.StoreBuilder.<String>barcode().fetcher(com.nytimes.android.external.store3.base.impl.ParsingFetcher.from(fetcher, parser)).persister(persister).open();
        Mockito.when(fetcher.fetch(barCode)).thenReturn(Single.just(ParsingFetcherTest.RAW_DATA));
        Mockito.when(parser.apply(ParsingFetcherTest.RAW_DATA)).thenReturn(ParsingFetcherTest.PARSED);
        Mockito.when(persister.read(barCode)).thenReturn(Maybe.just(ParsingFetcherTest.PARSED));
        Mockito.when(persister.write(barCode, ParsingFetcherTest.PARSED)).thenReturn(Single.just(true));
        String value = simpleStore.fetch(barCode).blockingGet();
        assertThat(value).isEqualTo(ParsingFetcherTest.PARSED);
        Mockito.verify(fetcher, Mockito.times(1)).fetch(barCode);
        Mockito.verify(parser, Mockito.times(1)).apply(ParsingFetcherTest.RAW_DATA);
        Mockito.verify(persister, Mockito.times(1)).write(barCode, ParsingFetcherTest.PARSED);
    }
}

