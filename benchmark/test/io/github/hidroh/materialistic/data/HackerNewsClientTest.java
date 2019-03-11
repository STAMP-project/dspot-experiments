package io.github.hidroh.materialistic.data;


import ItemManager.ASK_FETCH_MODE;
import ItemManager.JOBS_FETCH_MODE;
import ItemManager.MODE_CACHE;
import ItemManager.MODE_DEFAULT;
import ItemManager.MODE_NETWORK;
import ItemManager.NEW_FETCH_MODE;
import ItemManager.SHOW_FETCH_MODE;
import ItemManager.TOP_FETCH_MODE;
import dagger.Module;
import dagger.Provides;
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
public class HackerNewsClientTest {
    @Inject
    RestServiceFactory factory;

    @Inject
    SessionManager sessionManager;

    @Inject
    FavoriteManager favoriteManager;

    private HackerNewsClient client;

    @Captor
    ArgumentCaptor<Item[]> getStoriesResponse;

    @Mock
    ResponseListener<Item> itemListener;

    @Mock
    ResponseListener<UserManager.User> userListener;

    @Mock
    ResponseListener<Item[]> storiesListener;

    @Test
    public void testGetItemNoListener() {
        client.getItem("1", MODE_DEFAULT, null);
        Mockito.verify(TestRestServiceFactory.hnRestService, Mockito.never()).itemRx(ArgumentMatchers.any());
    }

    @Test
    public void testGetItemSuccess() {
        HackerNewsItem hnItem = Mockito.mock(HackerNewsItem.class);
        Mockito.when(TestRestServiceFactory.hnRestService.itemRx(ArgumentMatchers.eq("1"))).thenReturn(Observable.just(hnItem));
        Mockito.when(sessionManager.isViewed(ArgumentMatchers.eq("1"))).thenReturn(Observable.just(false));
        Mockito.when(favoriteManager.check(ArgumentMatchers.eq("1"))).thenReturn(Observable.just(false));
        client.getItem("1", MODE_DEFAULT, itemListener);
        Mockito.verify(TestRestServiceFactory.hnRestService).itemRx(ArgumentMatchers.eq("1"));
        Mockito.verify(sessionManager).isViewed(ArgumentMatchers.eq("1"));
        Mockito.verify(favoriteManager).check(ArgumentMatchers.eq("1"));
        Mockito.verify(itemListener).onResponse(ArgumentMatchers.eq(hnItem));
    }

    @Test
    public void testGetItemForceNetwork() {
        client.getItem("1", MODE_NETWORK, itemListener);
        Mockito.verify(TestRestServiceFactory.hnRestService).networkItemRx(ArgumentMatchers.eq("1"));
    }

    @Test
    public void testGetItemForceCache() {
        HackerNewsItem hnItem = Mockito.mock(HackerNewsItem.class);
        Mockito.when(TestRestServiceFactory.hnRestService.cachedItemRx(ArgumentMatchers.eq("1"))).thenReturn(Observable.just(hnItem));
        Mockito.when(sessionManager.isViewed(ArgumentMatchers.eq("1"))).thenReturn(Observable.just(false));
        Mockito.when(favoriteManager.check(ArgumentMatchers.eq("1"))).thenReturn(Observable.just(false));
        client.getItem("1", MODE_CACHE, itemListener);
        Mockito.verify(TestRestServiceFactory.hnRestService).cachedItemRx(ArgumentMatchers.eq("1"));
        Mockito.verify(sessionManager).isViewed(ArgumentMatchers.eq("1"));
        Mockito.verify(favoriteManager).check(ArgumentMatchers.eq("1"));
        Mockito.verify(itemListener).onResponse(ArgumentMatchers.eq(hnItem));
    }

    @Test
    public void testGetItemForceCacheUnsatisfiable() {
        Mockito.when(TestRestServiceFactory.hnRestService.cachedItemRx(ArgumentMatchers.eq("1"))).thenReturn(Observable.error(new IOException()));
        Mockito.when(TestRestServiceFactory.hnRestService.itemRx(ArgumentMatchers.eq("1"))).thenReturn(Observable.just(Mockito.mock(HackerNewsItem.class)));
        client.getItem("1", MODE_CACHE, itemListener);
        Mockito.verify(TestRestServiceFactory.hnRestService).cachedItemRx(ArgumentMatchers.eq("1"));
        Mockito.verify(TestRestServiceFactory.hnRestService).itemRx(ArgumentMatchers.eq("1"));
    }

    @Test
    public void testGetItemFailure() {
        Mockito.when(TestRestServiceFactory.hnRestService.itemRx(ArgumentMatchers.eq("1"))).thenReturn(Observable.error(new Throwable("message")));
        Mockito.when(sessionManager.isViewed(ArgumentMatchers.eq("1"))).thenReturn(Observable.just(true));
        Mockito.when(favoriteManager.check(ArgumentMatchers.eq("1"))).thenReturn(Observable.just(true));
        client.getItem("1", MODE_DEFAULT, itemListener);
        Mockito.verify(favoriteManager).check(ArgumentMatchers.eq("1"));
        Mockito.verify(sessionManager).isViewed(ArgumentMatchers.eq("1"));
        Mockito.verify(TestRestServiceFactory.hnRestService).itemRx(ArgumentMatchers.eq("1"));
        Mockito.verify(itemListener).onError(ArgumentMatchers.eq("message"));
    }

    @Test
    public void testGetItemFailureNoMessage() {
        Mockito.when(TestRestServiceFactory.hnRestService.itemRx(ArgumentMatchers.eq("1"))).thenReturn(Observable.error(new Throwable("")));
        Mockito.when(sessionManager.isViewed(ArgumentMatchers.eq("1"))).thenReturn(Observable.just(true));
        Mockito.when(favoriteManager.check(ArgumentMatchers.eq("1"))).thenReturn(Observable.just(true));
        client.getItem("1", MODE_DEFAULT, itemListener);
        Mockito.verify(sessionManager).isViewed(ArgumentMatchers.eq("1"));
        Mockito.verify(favoriteManager).check(ArgumentMatchers.eq("1"));
        Mockito.verify(TestRestServiceFactory.hnRestService).itemRx(ArgumentMatchers.eq("1"));
        Mockito.verify(itemListener).onError(ArgumentMatchers.eq(""));
    }

    @Test
    public void testGetStoriesNoListener() {
        client.getStories(TOP_FETCH_MODE, MODE_DEFAULT, null);
        Mockito.verify(TestRestServiceFactory.hnRestService, Mockito.never()).topStoriesRx();
    }

    @Test
    public void testGetTopStoriesSuccess() {
        Mockito.when(TestRestServiceFactory.hnRestService.topStoriesRx()).thenReturn(Observable.just(new int[]{ 1, 2 }));
        client.getStories(TOP_FETCH_MODE, MODE_DEFAULT, storiesListener);
        Mockito.verify(TestRestServiceFactory.hnRestService).topStoriesRx();
        Mockito.verify(storiesListener).onResponse(getStoriesResponse.capture());
        assertThat(getStoriesResponse.getValue()).hasSize(2);
    }

    @Test
    public void testGetNewStoriesNull() {
        Mockito.when(TestRestServiceFactory.hnRestService.newStoriesRx()).thenReturn(Observable.just(null));
        client.getStories(NEW_FETCH_MODE, MODE_DEFAULT, storiesListener);
        Mockito.verify(TestRestServiceFactory.hnRestService).newStoriesRx();
        Mockito.verify(storiesListener).onResponse(getStoriesResponse.capture());
        assertThat(getStoriesResponse.getValue()).isNullOrEmpty();
    }

    @Test
    public void testGetAskEmpty() {
        Mockito.when(TestRestServiceFactory.hnRestService.askStoriesRx()).thenReturn(Observable.just(new int[0]));
        client.getStories(ASK_FETCH_MODE, MODE_DEFAULT, storiesListener);
        Mockito.verify(TestRestServiceFactory.hnRestService).askStoriesRx();
        Mockito.verify(storiesListener).onResponse(getStoriesResponse.capture());
        assertThat(getStoriesResponse.getValue()).isEmpty();
    }

    @Test
    public void testGetShowFailure() {
        Mockito.when(TestRestServiceFactory.hnRestService.showStoriesRx()).thenReturn(Observable.error(new Throwable("message")));
        client.getStories(SHOW_FETCH_MODE, MODE_DEFAULT, storiesListener);
        Mockito.verify(TestRestServiceFactory.hnRestService).showStoriesRx();
        Mockito.verify(storiesListener).onError(ArgumentMatchers.eq("message"));
    }

    @Test
    public void testGetStoriesForceNetwork() {
        Mockito.when(TestRestServiceFactory.hnRestService.networkTopStoriesRx()).thenReturn(Observable.just(null));
        client.getStories(TOP_FETCH_MODE, MODE_NETWORK, storiesListener);
        Mockito.verify(TestRestServiceFactory.hnRestService).networkTopStoriesRx();
        Mockito.when(TestRestServiceFactory.hnRestService.networkNewStoriesRx()).thenReturn(Observable.just(null));
        client.getStories(NEW_FETCH_MODE, MODE_NETWORK, storiesListener);
        Mockito.verify(TestRestServiceFactory.hnRestService).networkNewStoriesRx();
        Mockito.when(TestRestServiceFactory.hnRestService.networkAskStoriesRx()).thenReturn(Observable.just(null));
        client.getStories(ASK_FETCH_MODE, MODE_NETWORK, storiesListener);
        Mockito.verify(TestRestServiceFactory.hnRestService).networkAskStoriesRx();
        Mockito.when(TestRestServiceFactory.hnRestService.networkJobStoriesRx()).thenReturn(Observable.just(null));
        client.getStories(JOBS_FETCH_MODE, MODE_NETWORK, storiesListener);
        Mockito.verify(TestRestServiceFactory.hnRestService).networkJobStoriesRx();
        Mockito.when(TestRestServiceFactory.hnRestService.networkShowStoriesRx()).thenReturn(Observable.just(null));
        client.getStories(SHOW_FETCH_MODE, MODE_NETWORK, storiesListener);
        Mockito.verify(TestRestServiceFactory.hnRestService).networkShowStoriesRx();
    }

    @Test
    public void testGetUserNoListener() {
        client.getUser("username", null);
        Mockito.verify(TestRestServiceFactory.hnRestService, Mockito.never()).userRx(ArgumentMatchers.any());
    }

    @Test
    public void testGetUserSuccess() {
        UserItem hnUser = Mockito.mock(UserItem.class);
        Mockito.when(TestRestServiceFactory.hnRestService.userRx(ArgumentMatchers.eq("username"))).thenReturn(Observable.just(hnUser));
        client.getUser("username", userListener);
        Mockito.verify(TestRestServiceFactory.hnRestService).userRx(ArgumentMatchers.eq("username"));
        Mockito.verify(userListener).onResponse(ArgumentMatchers.eq(hnUser));
    }

    @Test
    public void testGetUserNull() {
        Mockito.when(TestRestServiceFactory.hnRestService.userRx(ArgumentMatchers.eq("username"))).thenReturn(Observable.just(null));
        client.getUser("username", userListener);
        Mockito.verify(TestRestServiceFactory.hnRestService).userRx(ArgumentMatchers.eq("username"));
        Mockito.verify(userListener).onResponse(ArgumentMatchers.isNull());
    }

    @Test
    public void testGetUserFailure() {
        Mockito.when(TestRestServiceFactory.hnRestService.userRx(ArgumentMatchers.eq("username"))).thenReturn(Observable.error(new Throwable("message")));
        client.getUser("username", userListener);
        Mockito.verify(TestRestServiceFactory.hnRestService).userRx(ArgumentMatchers.eq("username"));
        Mockito.verify(userListener).onError(ArgumentMatchers.eq("message"));
    }

    @Module(injects = { HackerNewsClientTest.class, HackerNewsClient.class }, overrides = true)
    static class TestModule {
        @Provides
        @Singleton
        public RestServiceFactory provideRestServiceFactory() {
            return new TestRestServiceFactory();
        }

        @Provides
        @Singleton
        public SessionManager provideSessionManager() {
            return Mockito.mock(SessionManager.class);
        }

        @Provides
        @Singleton
        public FavoriteManager provideFavoriteManager() {
            return Mockito.mock(FavoriteManager.class);
        }

        @Provides
        @Singleton
        @Named(DataModule.IO_THREAD)
        public Scheduler provideIoScheduler() {
            return Schedulers.immediate();
        }

        @Provides
        @Singleton
        @Named(DataModule.MAIN_THREAD)
        public Scheduler provideMainThreadScheduler() {
            return Schedulers.immediate();
        }
    }
}

