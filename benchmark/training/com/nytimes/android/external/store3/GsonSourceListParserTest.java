package com.nytimes.android.external.store3;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nytimes.android.external.store3.base.Fetcher;
import com.nytimes.android.external.store3.base.Parser;
import com.nytimes.android.external.store3.base.Persister;
import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.Store;
import com.nytimes.android.external.store3.middleware.GsonParserFactory;
import io.reactivex.Maybe;
import io.reactivex.Single;
import java.util.Arrays;
import java.util.List;
import okio.BufferedSource;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


public class GsonSourceListParserTest {
    public static final String KEY = "key";

    @Mock
    Fetcher<BufferedSource, BarCode> fetcher;

    @Mock
    Persister<BufferedSource, BarCode> persister;

    private final BarCode barCode = new BarCode("value", GsonSourceListParserTest.KEY);

    @Test
    public void testSimple() {
        MockitoAnnotations.initMocks(this);
        Parser<BufferedSource, List<GsonSourceListParserTest.Foo>> parser = GsonParserFactory.createSourceParser(new Gson(), new TypeToken<List<GsonSourceListParserTest.Foo>>() {}.getType());
        Store<List<GsonSourceListParserTest.Foo>, BarCode> simpleStore = com.nytimes.android.external.store3.base.impl.StoreBuilder.<BarCode, BufferedSource, List<GsonSourceListParserTest.Foo>>parsedWithKey().persister(persister).fetcher(fetcher).parser(parser).open();
        GsonSourceListParserTest.Foo foo = new GsonSourceListParserTest.Foo("a");
        GsonSourceListParserTest.Foo foo2 = new GsonSourceListParserTest.Foo("b");
        GsonSourceListParserTest.Foo foo3 = new GsonSourceListParserTest.Foo("c");
        List<GsonSourceListParserTest.Foo> data = Arrays.asList(foo, foo2, foo3);
        String sourceData = new Gson().toJson(data);
        BufferedSource source = GsonSourceListParserTest.source(sourceData);
        Single<BufferedSource> value = Single.just(source);
        Mockito.when(fetcher.fetch(barCode)).thenReturn(value);
        Mockito.when(persister.read(barCode)).thenReturn(Maybe.<BufferedSource>empty()).thenReturn(value.toMaybe());
        Mockito.when(persister.write(barCode, source)).thenReturn(Single.just(true));
        List<GsonSourceListParserTest.Foo> result = simpleStore.get(barCode).blockingGet();
        assertThat(result.get(0).value).isEqualTo("a");
        assertThat(result.get(1).value).isEqualTo("b");
        assertThat(result.get(2).value).isEqualTo("c");
        Mockito.verify(fetcher, Mockito.times(1)).fetch(barCode);
    }

    private static class Foo {
        String value;

        Foo(String value) {
            this.value = value;
        }
    }
}

