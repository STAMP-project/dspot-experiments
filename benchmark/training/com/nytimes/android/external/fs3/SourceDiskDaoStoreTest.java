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


public class SourceDiskDaoStoreTest {
    public static final String KEY = "key";

    @Mock
    Fetcher<BufferedSource, BarCode> fetcher;

    @Mock
    SourcePersister diskDAO;

    private final BarCode barCode = new BarCode("value", SourceDiskDaoStoreTest.KEY);

    @Test
    public void testSimple() {
        MockitoAnnotations.initMocks(this);
        GsonSourceParser<SourceDiskDaoStoreTest.Foo> parser = new GsonSourceParser(new Gson(), SourceDiskDaoStoreTest.Foo.class);
        Store<SourceDiskDaoStoreTest.Foo, BarCode> store = com.nytimes.android.external.store3.base.impl.StoreBuilder.<BarCode, BufferedSource, SourceDiskDaoStoreTest.Foo>parsedWithKey().persister(diskDAO).fetcher(fetcher).parser(parser).open();
        SourceDiskDaoStoreTest.Foo foo = new SourceDiskDaoStoreTest.Foo();
        foo.bar = barCode.getKey();
        String sourceData = new Gson().toJson(foo);
        BufferedSource source = SourceDiskDaoStoreTest.source(sourceData);
        Single<BufferedSource> value = Single.just(source);
        Mockito.when(fetcher.fetch(barCode)).thenReturn(value);
        Mockito.when(diskDAO.read(barCode)).thenReturn(Maybe.<BufferedSource>empty()).thenReturn(value.toMaybe());
        Mockito.when(diskDAO.write(barCode, source)).thenReturn(Single.just(true));
        SourceDiskDaoStoreTest.Foo result = store.get(barCode).blockingGet();
        assertThat(result.bar).isEqualTo(SourceDiskDaoStoreTest.KEY);
        result = store.get(barCode).blockingGet();
        assertThat(result.bar).isEqualTo(SourceDiskDaoStoreTest.KEY);
        Mockito.verify(fetcher, Mockito.times(1)).fetch(barCode);
    }

    private static class Foo {
        String bar;

        Foo() {
        }
    }
}

