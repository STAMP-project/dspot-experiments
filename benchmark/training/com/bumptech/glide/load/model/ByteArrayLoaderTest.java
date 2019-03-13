package com.bumptech.glide.load.model;


import Priority.HIGH;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.util.Preconditions;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public class ByteArrayLoaderTest {
    @Mock
    private ByteArrayLoader.Converter<Object> converter;

    @Mock
    private DataFetcher.DataCallback<Object> callback;

    private ByteArrayLoader<Object> loader;

    private Options options;

    @Test
    public void testCanHandleByteArray() {
        byte[] data = new byte[10];
        DataFetcher<Object> fetcher = Preconditions.checkNotNull(loader.buildLoadData(data, (-1), (-1), options)).fetcher;
        Assert.assertNotNull(fetcher);
    }

    @Test
    public void testFetcherReturnsObjectReceivedFromConverter() throws IOException {
        byte[] data = "fake".getBytes("UTF-8");
        Object expected = new Object();
        Mockito.when(converter.convert(ArgumentMatchers.eq(data))).thenReturn(expected);
        Preconditions.checkNotNull(loader.buildLoadData(data, 10, 10, options)).fetcher.loadData(HIGH, callback);
        Mockito.verify(callback).onDataReady(ArgumentMatchers.eq(expected));
    }

    @Test
    public void testFetcherReturnsDataClassFromConverter() {
        Mockito.when(converter.getDataClass()).thenReturn(Object.class);
        Assert.assertEquals(Object.class, Preconditions.checkNotNull(loader.buildLoadData(new byte[10], 10, 10, options)).fetcher.getDataClass());
    }
}

