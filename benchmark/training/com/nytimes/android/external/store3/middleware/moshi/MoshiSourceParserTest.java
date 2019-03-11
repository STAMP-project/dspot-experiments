package com.nytimes.android.external.store3.middleware.moshi;


import com.nytimes.android.external.store3.base.Fetcher;
import com.nytimes.android.external.store3.base.Parser;
import com.nytimes.android.external.store3.base.Persister;
import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.Store;
import com.nytimes.android.external.store3.middleware.moshi.data.Bar;
import com.nytimes.android.external.store3.middleware.moshi.data.Foo;
import okio.BufferedSource;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;


public class MoshiSourceParserTest {
    private static final String KEY = "key";

    private static final String sourceString = "{\"number\":123,\"string\":\"abc\",\"bars\":[{\"string\":\"def\"},{\"string\":\"ghi\"}]}";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    Fetcher<BufferedSource, BarCode> fetcher;

    @Mock
    Persister<BufferedSource, BarCode> persister;

    private final BarCode barCode = new BarCode("value", MoshiSourceParserTest.KEY);

    @Test
    public void testSourceParser() throws Exception {
        Parser<BufferedSource, Foo> parser = MoshiParserFactory.createSourceParser(Foo.class);
        Store<Foo, BarCode> store = com.nytimes.android.external.store3.base.impl.ParsingStoreBuilder.<BufferedSource, Foo>builder().persister(persister).fetcher(fetcher).parser(parser).open();
        Foo result = store.get(barCode).blockingGet();
        Assert.assertEquals(result.number, 123);
        Assert.assertEquals(result.string, "abc");
        Assert.assertEquals(result.bars.size(), 2);
        Assert.assertEquals(result.bars.get(0).string, "def");
        Assert.assertEquals(result.bars.get(1).string, "ghi");
        Mockito.verify(fetcher, Mockito.times(1)).fetch(barCode);
    }

    @Test
    public void testNullMoshi() {
        expectedException.expect(NullPointerException.class);
        MoshiParserFactory.createSourceParser(null, Foo.class);
    }

    @Test
    public void testNullType() {
        expectedException.expect(NullPointerException.class);
        MoshiParserFactory.createSourceParser(null, Foo.class);
    }
}

