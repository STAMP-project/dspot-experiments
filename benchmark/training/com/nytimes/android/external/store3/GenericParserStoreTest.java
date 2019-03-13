package com.nytimes.android.external.store3;


import com.google.gson.Gson;
import com.nytimes.android.external.store3.base.Fetcher;
import com.nytimes.android.external.store3.base.Parser;
import com.nytimes.android.external.store3.base.Persister;
import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.Store;
import com.nytimes.android.external.store3.middleware.GsonParserFactory;
import io.reactivex.Maybe;
import io.reactivex.Single;
import okio.BufferedSource;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


public class GenericParserStoreTest {
    public static final String KEY = "key";

    @Mock
    Fetcher<BufferedSource, BarCode> fetcher;

    @Mock
    Persister<BufferedSource, BarCode> persister;

    private final BarCode barCode = new BarCode("value", GenericParserStoreTest.KEY);

    @Test
    public void testSimple() {
        MockitoAnnotations.initMocks(this);
        Parser<BufferedSource, GenericParserStoreTest.Foo> parser = GsonParserFactory.createSourceParser(new Gson(), GenericParserStoreTest.Foo.class);
        Store<GenericParserStoreTest.Foo, BarCode> simpleStore = com.nytimes.android.external.store3.base.impl.StoreBuilder.<BarCode, BufferedSource, GenericParserStoreTest.Foo>parsedWithKey().persister(persister).fetcher(fetcher).parser(parser).open();
        GenericParserStoreTest.Foo foo = new GenericParserStoreTest.Foo();
        foo.bar = barCode.getKey();
        String sourceData = new Gson().toJson(foo);
        BufferedSource source = GenericParserStoreTest.source(sourceData);
        Single<BufferedSource> value = Single.just(source);
        Mockito.when(fetcher.fetch(barCode)).thenReturn(value);
        Mockito.when(persister.read(barCode)).thenReturn(Maybe.<BufferedSource>empty()).thenReturn(value.toMaybe());
        Mockito.when(persister.write(barCode, source)).thenReturn(Single.just(true));
        GenericParserStoreTest.Foo result = simpleStore.get(barCode).blockingGet();
        assertThat(result.bar).isEqualTo(GenericParserStoreTest.KEY);
        result = simpleStore.get(barCode).blockingGet();
        assertThat(result.bar).isEqualTo(GenericParserStoreTest.KEY);
        Mockito.verify(fetcher, Mockito.times(1)).fetch(barCode);
    }

    private static class Foo {
        String bar;

        Foo() {
        }
    }
}

