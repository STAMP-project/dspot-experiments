package com.nytimes.android.external.store3.middleware.jackson;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nytimes.android.external.store3.base.Fetcher;
import com.nytimes.android.external.store3.base.Parser;
import com.nytimes.android.external.store3.base.Persister;
import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.Store;
import com.nytimes.android.external.store3.middleware.jackson.data.Foo;
import okio.BufferedSource;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;


public class JacksonSourceParserStoreTest {
    private static final String KEY = "key";

    private static final String sourceString = "{\"number\":123,\"string\":\"abc\",\"bars\":[{\"string\":\"def\"},{\"string\":\"ghi\"}]}";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    Fetcher<BufferedSource, BarCode> fetcher;

    @Mock
    Persister<BufferedSource, BarCode> persister;

    private final BarCode barCode = new BarCode("value", JacksonSourceParserStoreTest.KEY);

    @Test
    public void testDefaultJacksonSourceParser() {
        Parser<BufferedSource, Foo> parser = JacksonParserFactory.createSourceParser(Foo.class);
        Store<Foo, BarCode> store = com.nytimes.android.external.store3.base.impl.StoreBuilder.<BarCode, BufferedSource, Foo>parsedWithKey().persister(persister).fetcher(fetcher).parser(parser).open();
        Foo result = store.get(barCode).blockingGet();
        validateFoo(result);
        Mockito.verify(fetcher, Mockito.times(1)).fetch(barCode);
    }

    @Test
    public void testCustomJsonFactorySourceParser() {
        JsonFactory jsonFactory = new JsonFactory();
        Parser<BufferedSource, Foo> parser = JacksonParserFactory.createSourceParser(jsonFactory, Foo.class);
        Store<Foo, BarCode> store = com.nytimes.android.external.store3.base.impl.StoreBuilder.<BarCode, BufferedSource, Foo>parsedWithKey().persister(persister).fetcher(fetcher).parser(parser).open();
        Foo result = store.get(barCode).blockingGet();
        validateFoo(result);
        Mockito.verify(fetcher, Mockito.times(1)).fetch(barCode);
    }

    @Test
    public void testNullJsonFactory() {
        expectedException.expect(NullPointerException.class);
        JacksonParserFactory.createStringParser(((JsonFactory) (null)), Foo.class);
    }

    @Test
    public void testNullTypeWithValidJsonFactory() {
        expectedException.expect(NullPointerException.class);
        JacksonParserFactory.createStringParser(new JsonFactory(), null);
    }

    @Test
    public void testNullObjectMapper() {
        expectedException.expect(NullPointerException.class);
        JacksonParserFactory.createStringParser(((ObjectMapper) (null)), Foo.class);
    }

    @Test
    public void testNullType() {
        expectedException.expect(NullPointerException.class);
        JacksonParserFactory.createStringParser(null);
    }
}

