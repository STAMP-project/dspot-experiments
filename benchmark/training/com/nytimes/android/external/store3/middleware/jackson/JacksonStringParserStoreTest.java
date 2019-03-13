package com.nytimes.android.external.store3.middleware.jackson;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nytimes.android.external.store3.base.Fetcher;
import com.nytimes.android.external.store3.base.Parser;
import com.nytimes.android.external.store3.base.Persister;
import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.Store;
import com.nytimes.android.external.store3.middleware.jackson.data.Foo;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;


public class JacksonStringParserStoreTest {
    private static final String KEY = "key";

    private static final String source = "{\"number\":123,\"string\":\"abc\",\"bars\":[{\"string\":\"def\"},{\"string\":\"ghi\"}]}";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    Fetcher<String, BarCode> fetcher;

    @Mock
    Persister<String, BarCode> persister;

    private final BarCode barCode = new BarCode("value", JacksonStringParserStoreTest.KEY);

    @Test
    public void testDefaultJacksonStringParser() {
        Store<Foo, BarCode> store = com.nytimes.android.external.store3.base.impl.StoreBuilder.<BarCode, String, Foo>parsedWithKey().persister(persister).fetcher(fetcher).parser(JacksonParserFactory.createStringParser(Foo.class)).open();
        Foo result = store.get(barCode).blockingGet();
        validateFoo(result);
        Mockito.verify(fetcher, Mockito.times(1)).fetch(barCode);
    }

    @Test
    public void testCustomJsonFactoryStringParser() {
        JsonFactory jsonFactory = new JsonFactory();
        Parser<String, Foo> parser = JacksonParserFactory.createStringParser(jsonFactory, Foo.class);
        Store<Foo, BarCode> store = com.nytimes.android.external.store3.base.impl.StoreBuilder.<BarCode, String, Foo>parsedWithKey().persister(persister).fetcher(fetcher).parser(parser).open();
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

