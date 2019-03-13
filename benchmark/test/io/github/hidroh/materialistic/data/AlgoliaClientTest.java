package io.github.hidroh.materialistic.data;


import AlgoliaClient.AlgoliaHits;
import ItemManager.MODE_DEFAULT;
import com.google.gson.GsonBuilder;
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
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;


@RunWith(JUnit4.class)
public class AlgoliaClientTest {
    @Inject
    RestServiceFactory factory;

    @Inject
    @Named(ActivityModule.HN)
    ItemManager hackerNewsClient;

    @Captor
    ArgumentCaptor<Item[]> getStoriesResponse;

    private AlgoliaClient client;

    @Mock
    ResponseListener<Item> itemListener;

    @Mock
    ResponseListener<Item[]> storiesListener;

    @Test
    public void testGetItem() {
        client.getItem("1", MODE_DEFAULT, itemListener);
        Mockito.verify(hackerNewsClient).getItem(ArgumentMatchers.eq("1"), ArgumentMatchers.eq(MODE_DEFAULT), ArgumentMatchers.eq(itemListener));
    }

    @Test
    public void testGetStoriesNoListener() {
        client.getStories("filter", MODE_DEFAULT, null);
        Mockito.verify(TestRestServiceFactory.algoliaRestService, Mockito.never()).searchByDate(ArgumentMatchers.eq("filter"));
    }

    @Test
    public void testGetStoriesSuccess() {
        AlgoliaClient.AlgoliaHits hits = new GsonBuilder().create().fromJson("{\"hits\":[{\"objectID\":\"1\"}]}", AlgoliaHits.class);
        Mockito.when(TestRestServiceFactory.algoliaRestService.searchByDate(ArgumentMatchers.eq("filter"))).thenReturn(Observable.just(hits));
        client.getStories("filter", MODE_DEFAULT, storiesListener);
        Mockito.verify(TestRestServiceFactory.algoliaRestService).searchByDate(ArgumentMatchers.eq("filter"));
        Mockito.verify(storiesListener).onResponse(getStoriesResponse.capture());
        assertThat(getStoriesResponse.getValue()).hasSize(1);
    }

    @Test
    public void testGetStoriesSuccessSortByPopularity() {
        Mockito.when(TestRestServiceFactory.algoliaRestService.search(ArgumentMatchers.eq("filter"))).thenReturn(Observable.error(new IOException()));
        client.sSortByTime = false;
        client.getStories("filter", MODE_DEFAULT, storiesListener);
        Mockito.verify(TestRestServiceFactory.algoliaRestService).search(ArgumentMatchers.eq("filter"));
    }

    @Test
    public void testGetStoriesEmpty() {
        AlgoliaClient.AlgoliaHits hits = new GsonBuilder().create().fromJson("{\"hits\":[]}", AlgoliaHits.class);
        Mockito.when(TestRestServiceFactory.algoliaRestService.searchByDate(ArgumentMatchers.eq("filter"))).thenReturn(Observable.just(hits));
        client.getStories("filter", MODE_DEFAULT, storiesListener);
        Mockito.verify(TestRestServiceFactory.algoliaRestService).searchByDate(ArgumentMatchers.eq("filter"));
        Mockito.verify(storiesListener).onResponse(getStoriesResponse.capture());
        assertThat(getStoriesResponse.getValue()).isEmpty();
    }

    @Test
    public void testGetStoriesFailure() {
        Mockito.when(TestRestServiceFactory.algoliaRestService.searchByDate(ArgumentMatchers.eq("filter"))).thenReturn(Observable.error(new Throwable("message")));
        client.getStories("filter", MODE_DEFAULT, storiesListener);
        Mockito.verify(TestRestServiceFactory.algoliaRestService).searchByDate(ArgumentMatchers.eq("filter"));
        Mockito.verify(storiesListener).onError(ArgumentMatchers.eq("message"));
    }

    @Module(injects = { AlgoliaClientTest.class, AlgoliaClient.class }, overrides = true)
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

