package com.nytimes.android.external.fs3;


import com.google.gson.Gson;
import com.nytimes.android.external.store3.base.Fetcher;
import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.Store;
import com.nytimes.android.external.store3.middleware.GsonSourceParser;
import io.reactivex.Maybe;
import io.reactivex.Single;
import okio.BufferedSource;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


public class SourceFilerReaderWriterStoreTest {
    public static final String KEY = "key";

    @Mock
    Fetcher<BufferedSource, BarCode> fetcher;

    @Mock
    SourceFileReader fileReader;

    @Mock
    SourceFileWriter fileWriter;

    private final BarCode barCode = new BarCode("value", SourceFilerReaderWriterStoreTest.KEY);

    @Test
    public void testSimple() {
        MockitoAnnotations.initMocks(this);
        GsonSourceParser<SourceFilerReaderWriterStoreTest.Foo> parser = new GsonSourceParser(new Gson(), SourceFilerReaderWriterStoreTest.Foo.class);
        Store<SourceFilerReaderWriterStoreTest.Foo, BarCode> simpleStore = com.nytimes.android.external.store3.base.impl.StoreBuilder.<BarCode, BufferedSource, SourceFilerReaderWriterStoreTest.Foo>parsedWithKey().persister(fileReader, fileWriter).fetcher(fetcher).parser(parser).open();
        SourceFilerReaderWriterStoreTest.Foo foo = new SourceFilerReaderWriterStoreTest.Foo();
        foo.bar = barCode.getKey();
        String sourceData = new Gson().toJson(foo);
        BufferedSource source = SourceFilerReaderWriterStoreTest.source(sourceData);
        Single<BufferedSource> value = Single.just(source);
        Mockito.when(fetcher.fetch(barCode)).thenReturn(value);
        Mockito.when(fileReader.read(barCode)).thenReturn(Maybe.<BufferedSource>empty()).thenReturn(value.toMaybe());
        Mockito.when(fileWriter.write(barCode, source)).thenReturn(Single.just(true));
        SourceFilerReaderWriterStoreTest.Foo result = simpleStore.get(barCode).blockingGet();
        assertThat(result.bar).isEqualTo(SourceFilerReaderWriterStoreTest.KEY);
        result = simpleStore.get(barCode).blockingGet();
        assertThat(result.bar).isEqualTo(SourceFilerReaderWriterStoreTest.KEY);
        Mockito.verify(fetcher, Mockito.times(1)).fetch(barCode);
    }

    private static class Foo {
        String bar;

        Foo() {
        }
    }
}

