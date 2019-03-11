package io.github.hidroh.materialistic.data;


import ItemManager.MODE_DEFAULT;
import dagger.Module;
import dagger.Provides;
import io.github.hidroh.materialistic.ActivityModule;
import io.github.hidroh.materialistic.DataModule;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;


@RunWith(Parameterized.class)
public class AlgoliaPopularClientTest {
    private final String range;

    @Inject
    RestServiceFactory factory;

    @Inject
    @Named(ActivityModule.HN)
    ItemManager hackerNewsClient;

    private AlgoliaPopularClient client;

    public AlgoliaPopularClientTest(String range) {
        this.range = range;
    }

    @Test
    public void testGetStories() {
        Mockito.when(TestRestServiceFactory.algoliaRestService.searchByMinTimestamp(ArgumentMatchers.any())).thenReturn(Observable.error(new IOException()));
        client.getStories(range, MODE_DEFAULT, Mockito.mock(ResponseListener.class));
        Mockito.verify(TestRestServiceFactory.algoliaRestService).searchByMinTimestamp(ArgumentMatchers.contains("created_at_i>"));
    }

    @Module(injects = { AlgoliaPopularClientTest.class, AlgoliaPopularClient.class }, overrides = true)
    static class TestModule {
        @Provides
        @Singleton
        @Named(ActivityModule.HN)
        public ItemManager provideHackerNewsClient() {
            return Mockito.mock(ItemManager.class);
        }

        @Provides
        @Singleton
        @Named(DataModule.MAIN_THREAD)
        public Scheduler provideMainThreadScheduler() {
            return Schedulers.immediate();
        }

        @Provides
        @Singleton
        public RestServiceFactory provideRestServiceFactory() {
            return new TestRestServiceFactory();
        }
    }
}

