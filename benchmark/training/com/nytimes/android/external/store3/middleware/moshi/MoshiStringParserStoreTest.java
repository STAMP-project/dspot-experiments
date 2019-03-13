package com.nytimes.android.external.store3.middleware.moshi;


import com.nytimes.android.external.store3.base.Fetcher;
import com.nytimes.android.external.store3.base.Persister;
import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.Store;
import com.nytimes.android.external.store3.middleware.moshi.data.Bar;
import com.nytimes.android.external.store3.middleware.moshi.data.Foo;
import com.squareup.moshi.Moshi;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;


public class MoshiStringParserStoreTest {
    private static final String KEY = "key";

    private static final String source = "{\"number\":123,\"string\":\"abc\",\"bars\":[{\"string\":\"def\"},{\"string\":\"ghi\"}]}";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    Fetcher<String, BarCode> fetcher;

    @Mock
    Persister<String, BarCode> persister;

    private final BarCode barCode = new BarCode("value", MoshiStringParserStoreTest.KEY);

    @Test
    public void testMoshiString() {
        Store<Foo, BarCode> store = com.nytimes.android.external.store3.base.impl.ParsingStoreBuilder.<String, Foo>builder().persister(persister).fetcher(fetcher).parser(MoshiParserFactory.createStringParser(Foo.class)).open();
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
        MoshiParserFactory.createStringParser(null, Foo.class);
    }

    @Test
    public void testNullType() {
        expectedException.expect(NullPointerException.class);
        MoshiParserFactory.createStringParser(new Moshi.Builder().build(), null);
    }
}

