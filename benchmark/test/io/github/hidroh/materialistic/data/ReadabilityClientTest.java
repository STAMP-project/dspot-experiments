package io.github.hidroh.materialistic.data;


import ReadabilityClient.Callback;
import ReadabilityClient.Impl.Readable;
import com.google.gson.GsonBuilder;
import dagger.Module;
import dagger.Provides;
import io.github.hidroh.materialistic.DataModule;
import io.github.hidroh.materialistic.test.InMemoryCache;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;


@RunWith(JUnit4.class)
public class ReadabilityClientTest {
    @Inject
    RestServiceFactory factory;

    @Inject
    LocalCache cache;

    private ReadabilityClient client;

    private Callback callback;

    @Test
    public void testWithContent() {
        ReadabilityClient.Impl.Readable readable = new GsonBuilder().create().fromJson("{\"content\":\"<div>content</div>\"}", Readable.class);
        Mockito.when(TestRestServiceFactory.mercuryService.parse(ArgumentMatchers.any())).thenReturn(Observable.just(readable));
        client.parse("1", "http://example.com/article.html", callback);
        Mockito.verify(TestRestServiceFactory.mercuryService).parse(ArgumentMatchers.any());
        Mockito.verify(callback).onResponse(ArgumentMatchers.eq("<div>content</div>"));
    }

    @Test
    public void testEmptyContent() {
        ReadabilityClient.Impl.Readable readable = new GsonBuilder().create().fromJson("{\"content\":\"<div></div>\"}", Readable.class);
        Mockito.when(TestRestServiceFactory.mercuryService.parse(ArgumentMatchers.any())).thenReturn(Observable.just(readable));
        client.parse("1", "http://example.com/article.html", callback);
        Mockito.verify(TestRestServiceFactory.mercuryService).parse(ArgumentMatchers.any());
        Mockito.verify(callback).onResponse(ArgumentMatchers.isNull());
    }

    @Test
    public void testError() {
        Mockito.when(TestRestServiceFactory.mercuryService.parse(ArgumentMatchers.any())).thenReturn(Observable.error(new IOException()));
        client.parse("1", "http://example.com/article.html", callback);
        Mockito.verify(TestRestServiceFactory.mercuryService).parse(ArgumentMatchers.any());
        Mockito.verify(callback).onResponse(ArgumentMatchers.isNull());
    }

    @Test
    public void testCachedContent() {
        cache.putReadability("1", "<div>content</div>");
        client.parse("1", "http://example.com/article.html", callback);
        Mockito.verify(TestRestServiceFactory.mercuryService, Mockito.never()).parse(ArgumentMatchers.any());
        Mockito.verify(callback).onResponse(ArgumentMatchers.eq("<div>content</div>"));
    }

    @Test
    public void testEmptyCachedContent() {
        cache.putReadability("1", "<div></div>");
        client.parse("1", "http://example.com/article.html", callback);
        Mockito.verify(TestRestServiceFactory.mercuryService, Mockito.never()).parse(ArgumentMatchers.any());
        Mockito.verify(callback).onResponse(ArgumentMatchers.isNull());
    }

    @Module(injects = { ReadabilityClientTest.class, ReadabilityClient.Impl.class }, overrides = true)
    static class TestModule {
        @Provides
        @Singleton
        public RestServiceFactory provideRestServiceFactory() {
            return new TestRestServiceFactory();
        }

        @Provides
        @Singleton
        @Named(DataModule.MAIN_THREAD)
        public Scheduler provideMainThreadScheduler() {
            return Schedulers.immediate();
        }

        @Provides
        @Singleton
        @Named(DataModule.IO_THREAD)
        public Scheduler provideIoThreadScheduler() {
            return Schedulers.immediate();
        }

        @Provides
        @Singleton
        public LocalCache provideLocalCache() {
            return new InMemoryCache();
        }
    }
}

