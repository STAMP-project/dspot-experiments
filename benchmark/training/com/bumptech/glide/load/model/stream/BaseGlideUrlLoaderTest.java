package com.bumptech.glide.load.model.stream;


import android.support.annotation.NonNull;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelCache;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.util.Preconditions;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class BaseGlideUrlLoaderTest {
    @Mock
    private ModelCache<Object, GlideUrl> modelCache;

    @Mock
    private ModelLoader<GlideUrl, InputStream> wrapped;

    @Mock
    private DataFetcher<InputStream> fetcher;

    private BaseGlideUrlLoaderTest.TestLoader urlLoader;

    private Options options;

    @Test
    public void testReturnsNullIfUrlIsNull() {
        urlLoader.resultUrl = null;
        Assert.assertNull(urlLoader.buildLoadData(new Object(), 100, 100, options));
    }

    @Test
    public void testReturnsNullIfUrlIsEmpty() {
        urlLoader.resultUrl = "    ";
        Assert.assertNull(urlLoader.buildLoadData(new Object(), 100, 100, options));
    }

    @Test
    public void testReturnsUrlFromCacheIfPresent() {
        Object model = new Object();
        int width = 100;
        int height = 200;
        GlideUrl expectedUrl = Mockito.mock(GlideUrl.class);
        Mockito.when(modelCache.get(ArgumentMatchers.eq(model), ArgumentMatchers.eq(width), ArgumentMatchers.eq(height))).thenReturn(expectedUrl);
        Mockito.when(wrapped.buildLoadData(ArgumentMatchers.eq(expectedUrl), ArgumentMatchers.eq(width), ArgumentMatchers.eq(height), ArgumentMatchers.eq(options))).thenReturn(new ModelLoader.LoadData<>(mock(.class), fetcher));
        Assert.assertEquals(fetcher, Preconditions.checkNotNull(urlLoader.buildLoadData(model, width, height, options)).fetcher);
    }

    @Test
    public void testBuildsNewUrlIfNotPresentInCache() {
        int width = 10;
        int height = 11;
        urlLoader.resultUrl = "fakeUrl";
        Mockito.when(wrapped.buildLoadData(ArgumentMatchers.any(GlideUrl.class), ArgumentMatchers.eq(width), ArgumentMatchers.eq(height), ArgumentMatchers.eq(options))).thenAnswer(new org.mockito.stubbing.Answer<ModelLoader.LoadData<InputStream>>() {
            @Override
            public ModelLoader.LoadData<InputStream> answer(InvocationOnMock invocationOnMock) {
                GlideUrl glideUrl = ((GlideUrl) (invocationOnMock.getArguments()[0]));
                assertEquals(urlLoader.resultUrl, glideUrl.toStringUrl());
                return new ModelLoader.LoadData<>(mock(.class), fetcher);
            }
        });
        Assert.assertEquals(fetcher, Preconditions.checkNotNull(urlLoader.buildLoadData(new GlideUrl(urlLoader.resultUrl), width, height, options)).fetcher);
    }

    @Test
    public void testAddsNewUrlToCacheIfNotPresentInCache() {
        urlLoader.resultUrl = "fakeUrl";
        Object model = new Object();
        int width = 400;
        int height = 500;
        Mockito.doAnswer(new org.mockito.stubbing.Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) {
                GlideUrl glideUrl = ((GlideUrl) (invocationOnMock.getArguments()[3]));
                Assert.assertEquals(urlLoader.resultUrl, glideUrl.toStringUrl());
                return null;
            }
        }).when(modelCache).put(ArgumentMatchers.eq(model), ArgumentMatchers.eq(width), ArgumentMatchers.eq(height), ArgumentMatchers.any(GlideUrl.class));
        urlLoader.buildLoadData(model, width, height, options);
        Mockito.verify(modelCache).put(ArgumentMatchers.eq(model), ArgumentMatchers.eq(width), ArgumentMatchers.eq(height), ArgumentMatchers.any(GlideUrl.class));
    }

    @Test
    public void testDoesNotInteractWithModelCacheIfNull() {
        BaseGlideUrlLoaderTest.TestLoader urlLoader = new BaseGlideUrlLoaderTest.TestLoader(wrapped, null);
        urlLoader.resultUrl = "fakeUrl";
        int width = 456;
        int height = 789;
        Mockito.when(wrapped.buildLoadData(ArgumentMatchers.any(GlideUrl.class), ArgumentMatchers.eq(width), ArgumentMatchers.eq(height), ArgumentMatchers.eq(options))).thenReturn(new ModelLoader.LoadData<>(mock(.class), fetcher));
        Assert.assertEquals(fetcher, Preconditions.checkNotNull(urlLoader.buildLoadData(new Object(), width, height, options)).fetcher);
    }

    private static final class TestLoader extends BaseGlideUrlLoader<Object> {
        String resultUrl;

        TestLoader(ModelLoader<GlideUrl, InputStream> concreteLoader, ModelCache<Object, GlideUrl> modelCache) {
            super(concreteLoader, modelCache);
        }

        @Override
        protected String getUrl(Object model, int width, int height, Options options) {
            return resultUrl;
        }

        @Override
        public boolean handles(@NonNull
        Object model) {
            return true;
        }
    }
}

