/**
 * Copyright (c) 2016 Ha Duy Trung
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.hidroh.materialistic.data;


import ConnectivityManager.TYPE_MOBILE;
import ConnectivityManager.TYPE_WIFI;
import Context.NOTIFICATION_SERVICE;
import MaterialisticDatabase.SavedStoriesDao;
import R.string.offline_data_default;
import R.string.pref_offline_article;
import R.string.pref_offline_comments;
import R.string.pref_offline_data;
import R.string.pref_offline_notification;
import R.string.pref_offline_readability;
import ShadowContentResolver.Status;
import SyncContentProvider.PROVIDER_AUTHORITY;
import android.accounts.Account;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import io.github.hidroh.materialistic.BuildConfig;
import io.github.hidroh.materialistic.test.InMemoryCache;
import io.github.hidroh.materialistic.test.TestRunner;
import io.github.hidroh.materialistic.test.shadow.ShadowWebView;
import java.io.IOException;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ServiceController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContentResolver;
import org.robolectric.shadows.ShadowNotification;
import org.robolectric.shadows.ShadowNotificationManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.schedulers.Schedulers;


@SuppressWarnings("unchecked")
@Config(shadows = { ShadowWebView.class }, sdk = 18)
@RunWith(TestRunner.class)
public class ItemSyncAdapterTest {
    private ItemSyncAdapterTest.TestItemSyncAdapter adapter;

    private SharedPreferences syncPreferences;

    @Captor
    private ArgumentCaptor<Callback<HackerNewsItem>> callbackCapture;

    private ReadabilityClient readabilityClient = Mockito.mock(ReadabilityClient.class);

    private ServiceController<ItemSyncService> serviceController;

    private ItemSyncService service;

    @Captor
    private ArgumentCaptor<ReadabilityClient.Callback> readabilityCallbackCaptor;

    private SyncScheduler syncScheduler;

    @Test
    public void testSyncDisabled() {
        PreferenceManager.getDefaultSharedPreferences(service).edit().clear().apply();
        syncScheduler.scheduleSync(service, "1");
        Assert.assertNull(ShadowContentResolver.getStatus(createSyncAccount(), PROVIDER_AUTHORITY));
    }

    @Test
    public void testSyncEnabledCached() throws IOException {
        HackerNewsItem hnItem = Mockito.mock(HackerNewsItem.class);
        Call<HackerNewsItem> call = Mockito.mock(Call.class);
        Mockito.when(call.execute()).thenReturn(Response.success(hnItem));
        Mockito.when(TestRestServiceFactory.hnRestService.cachedItem(ArgumentMatchers.any())).thenReturn(call);
        syncScheduler.scheduleSync(service, "1");
        adapter.onPerformSync(Mockito.mock(Account.class), getLastSyncExtras(), null, null, null);
        // cache hit, should not try network or defer
        Mockito.verify(TestRestServiceFactory.hnRestService).cachedItem(ArgumentMatchers.any());
        Mockito.verify(TestRestServiceFactory.hnRestService, Mockito.never()).networkItem(ArgumentMatchers.any());
        assertThat(syncPreferences.getAll()).isEmpty();
    }

    @Test
    public void testSyncEnabledNonWifi() throws IOException {
        Call<HackerNewsItem> call = Mockito.mock(Call.class);
        Mockito.when(call.execute()).thenThrow(IOException.class);
        Mockito.when(TestRestServiceFactory.hnRestService.cachedItem(ArgumentMatchers.any())).thenReturn(call);
        setNetworkType(TYPE_MOBILE);
        syncScheduler.scheduleSync(service, "1");
        adapter.onPerformSync(Mockito.mock(Account.class), getLastSyncExtras(), null, null, null);
        // should defer
        assertThat(syncPreferences.getAll()).isNotEmpty();
    }

    @Test
    public void testSyncEnabledAnyConnection() throws IOException {
        Call<HackerNewsItem> call = Mockito.mock(Call.class);
        Mockito.when(call.execute()).thenThrow(IOException.class);
        Mockito.when(TestRestServiceFactory.hnRestService.cachedItem(ArgumentMatchers.any())).thenReturn(call);
        Mockito.when(TestRestServiceFactory.hnRestService.networkItem(ArgumentMatchers.any())).thenReturn(call);
        PreferenceManager.getDefaultSharedPreferences(service).edit().putString(service.getString(pref_offline_data), service.getString(offline_data_default)).apply();
        setNetworkType(TYPE_MOBILE);
        syncScheduler.scheduleSync(service, "1");
        adapter.onPerformSync(Mockito.mock(Account.class), getLastSyncExtras(), null, null, null);
        // should try cache, then network
        Mockito.verify(TestRestServiceFactory.hnRestService).cachedItem(ArgumentMatchers.any());
        Mockito.verify(TestRestServiceFactory.hnRestService).networkItem(ArgumentMatchers.any());
        assertThat(syncPreferences.getAll()).isEmpty();
    }

    @Test
    public void testSyncEnabledWifi() throws IOException {
        Call<HackerNewsItem> call = Mockito.mock(Call.class);
        Mockito.when(call.execute()).thenThrow(IOException.class);
        Mockito.when(TestRestServiceFactory.hnRestService.cachedItem(ArgumentMatchers.any())).thenReturn(call);
        Mockito.when(TestRestServiceFactory.hnRestService.networkItem(ArgumentMatchers.any())).thenReturn(call);
        syncScheduler.scheduleSync(service, "1");
        adapter.onPerformSync(Mockito.mock(Account.class), getLastSyncExtras(), null, null, null);
        // should try cache before network
        Mockito.verify(TestRestServiceFactory.hnRestService).cachedItem(ArgumentMatchers.any());
        Mockito.verify(TestRestServiceFactory.hnRestService).networkItem(ArgumentMatchers.any());
        assertThat(syncPreferences.getAll()).isEmpty();
        // on network response should try children
        Mockito.verify(call).enqueue(callbackCapture.capture());
        HackerNewsItem item = Mockito.mock(HackerNewsItem.class);
        Mockito.when(item.getKids()).thenReturn(new long[]{ 2L, 3L });
        callbackCapture.getValue().onResponse(null, Response.success(item));
        Mockito.verify(TestRestServiceFactory.hnRestService, Mockito.times(3)).cachedItem(ArgumentMatchers.any());
    }

    @Test
    public void testSyncChildrenDisabled() throws IOException {
        HackerNewsItem item = Mockito.mock(HackerNewsItem.class);
        Mockito.when(item.getKids()).thenReturn(new long[]{ 2L, 3L });
        Call<HackerNewsItem> call = Mockito.mock(Call.class);
        Mockito.when(call.execute()).thenReturn(Response.success(item));
        Mockito.when(TestRestServiceFactory.hnRestService.cachedItem(ArgumentMatchers.any())).thenReturn(call);
        PreferenceManager.getDefaultSharedPreferences(service).edit().putBoolean(service.getString(pref_offline_comments), false).apply();
        syncScheduler.scheduleSync(service, "1");
        adapter.onPerformSync(Mockito.mock(Account.class), getLastSyncExtras(), null, null, null);
        // should not sync children
        Mockito.verify(TestRestServiceFactory.hnRestService).cachedItem(ArgumentMatchers.any());
    }

    @Test
    public void testSyncDeferred() throws IOException {
        Call<HackerNewsItem> call = Mockito.mock(Call.class);
        Mockito.when(call.execute()).thenThrow(IOException.class);
        Mockito.when(TestRestServiceFactory.hnRestService.cachedItem(ArgumentMatchers.any())).thenReturn(call);
        Mockito.when(TestRestServiceFactory.hnRestService.networkItem(ArgumentMatchers.any())).thenReturn(call);
        syncPreferences.edit().putBoolean("1", true).putBoolean("2", true).apply();
        syncScheduler.scheduleSync(service, null);
        adapter.onPerformSync(Mockito.mock(Account.class), getLastSyncExtras(), null, null, null);
        ShadowContentResolver.Status syncStatus = ShadowContentResolver.getStatus(new Account("Materialistic", BuildConfig.APPLICATION_ID), PROVIDER_AUTHORITY);
        assertThat(syncStatus.syncRequests).isEqualTo(3);// original + 2 deferred

    }

    @Test
    public void testSyncReadabilityDisabled() throws IOException {
        HackerNewsItem item = new TestHnItem(1L) {
            @Override
            public boolean isStoryType() {
                return true;
            }

            @Override
            public String getRawUrl() {
                return "http://example.com";
            }
        };
        Call<HackerNewsItem> call = Mockito.mock(Call.class);
        Mockito.when(call.execute()).thenReturn(Response.success(item));
        Mockito.when(TestRestServiceFactory.hnRestService.cachedItem(ArgumentMatchers.any())).thenReturn(call);
        PreferenceManager.getDefaultSharedPreferences(service).edit().putBoolean(service.getString(pref_offline_readability), false).apply();
        syncScheduler.scheduleSync(service, "1");
        adapter.onPerformSync(Mockito.mock(Account.class), getLastSyncExtras(), null, null, null);
        Mockito.verify(TestRestServiceFactory.hnRestService).cachedItem(ArgumentMatchers.any());
        Mockito.verify(readabilityClient, Mockito.never()).parse(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void testSyncReadability() throws IOException {
        HackerNewsItem item = new TestHnItem(1L) {
            @Override
            public boolean isStoryType() {
                return true;
            }

            @Override
            public String getRawUrl() {
                return "http://example.com";
            }
        };
        Call<HackerNewsItem> call = Mockito.mock(Call.class);
        Mockito.when(call.execute()).thenReturn(Response.success(item));
        Mockito.when(TestRestServiceFactory.hnRestService.cachedItem(ArgumentMatchers.any())).thenReturn(call);
        syncScheduler.scheduleSync(service, "1");
        adapter.onPerformSync(Mockito.mock(Account.class), getLastSyncExtras(), null, null, null);
        Mockito.verify(TestRestServiceFactory.hnRestService).cachedItem(ArgumentMatchers.any());
        Mockito.verify(readabilityClient).parse(ArgumentMatchers.any(), ArgumentMatchers.eq("http://example.com"), ArgumentMatchers.any());
    }

    @Test
    public void testSyncReadabilityNoWifi() throws IOException {
        HackerNewsItem item = new TestHnItem(1L) {
            @Override
            public boolean isStoryType() {
                return true;
            }
        };
        Call<HackerNewsItem> call = Mockito.mock(Call.class);
        Mockito.when(call.execute()).thenReturn(Response.success(item));
        Mockito.when(TestRestServiceFactory.hnRestService.cachedItem(ArgumentMatchers.any())).thenReturn(call);
        setNetworkType(TYPE_MOBILE);
        syncScheduler.scheduleSync(service, "1");
        adapter.onPerformSync(Mockito.mock(Account.class), getLastSyncExtras(), null, null, null);
        Mockito.verify(readabilityClient, Mockito.never()).parse(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void testSyncReadabilityNotStory() throws IOException {
        HackerNewsItem item = new TestHnItem(1L) {
            @Override
            public boolean isStoryType() {
                return false;
            }
        };
        Call<HackerNewsItem> call = Mockito.mock(Call.class);
        Mockito.when(call.execute()).thenReturn(Response.success(item));
        Mockito.when(TestRestServiceFactory.hnRestService.cachedItem(ArgumentMatchers.any())).thenReturn(call);
        syncScheduler.scheduleSync(service, "1");
        adapter.onPerformSync(Mockito.mock(Account.class), getLastSyncExtras(), null, null, null);
        Mockito.verify(TestRestServiceFactory.hnRestService).cachedItem(ArgumentMatchers.any());
        Mockito.verify(readabilityClient, Mockito.never()).parse(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void testSyncWebCacheEmptyUrl() {
        new FavoriteManager(new InMemoryCache(), Schedulers.immediate(), Mockito.mock(SavedStoriesDao.class)).add(service, new Favorite("1", null, "title", System.currentTimeMillis()));
        assertThat(ShadowWebView.getLastGlobalLoadedUrl()).isNullOrEmpty();
    }

    @Test
    public void testSyncWebCache() throws IOException {
        ShadowWebView.lastGlobalLoadedUrl = null;
        PreferenceManager.getDefaultSharedPreferences(service).edit().putBoolean(service.getString(pref_offline_article), true).apply();
        HackerNewsItem item = new TestHnItem(1L) {
            @Override
            public boolean isStoryType() {
                return true;
            }

            @Override
            public String getUrl() {
                return "http://example.com";
            }
        };
        Call<HackerNewsItem> call = Mockito.mock(Call.class);
        Mockito.when(call.execute()).thenReturn(Response.success(item));
        Mockito.when(TestRestServiceFactory.hnRestService.cachedItem(ArgumentMatchers.any())).thenReturn(call);
        syncScheduler.scheduleSync(service, "1");
        adapter.onPerformSync(Mockito.mock(Account.class), getLastSyncExtras(), null, null, null);
        assertThat(ShadowWebView.getLastGlobalLoadedUrl()).contains("http://example.com");
    }

    @Test
    public void testSyncWebCacheDisabled() throws IOException {
        ShadowWebView.lastGlobalLoadedUrl = null;
        PreferenceManager.getDefaultSharedPreferences(service).edit().putBoolean(service.getString(pref_offline_article), false).apply();
        HackerNewsItem item = new TestHnItem(1L) {
            @Override
            public boolean isStoryType() {
                return true;
            }

            @Override
            public String getRawUrl() {
                return "http://example.com";
            }
        };
        Call<HackerNewsItem> call = Mockito.mock(Call.class);
        Mockito.when(call.execute()).thenReturn(Response.success(item));
        Mockito.when(TestRestServiceFactory.hnRestService.cachedItem(ArgumentMatchers.any())).thenReturn(call);
        syncScheduler.scheduleSync(service, "1");
        adapter.onPerformSync(Mockito.mock(Account.class), getLastSyncExtras(), null, null, null);
        assertThat(ShadowWebView.getLastGlobalLoadedUrl()).isNullOrEmpty();
    }

    @Test
    public void testNotification() throws IOException {
        Call<HackerNewsItem> call = Mockito.mock(Call.class);
        Mockito.when(call.execute()).thenReturn(Response.success(new TestHnItem(1L) {
            @Override
            public boolean isStoryType() {
                return true;
            }

            @Override
            public String getRawUrl() {
                return "http://example.com";
            }

            @Override
            public long[] getKids() {
                return new long[]{ 2L, 3L };
            }
        }));
        Mockito.when(TestRestServiceFactory.hnRestService.cachedItem(ArgumentMatchers.eq("1"))).thenReturn(call);
        Call<HackerNewsItem> kid1Call = Mockito.mock(Call.class);
        Mockito.when(kid1Call.execute()).thenReturn(Response.success(new TestHnItem(2L) {
            @Override
            public boolean isStoryType() {
                return false;
            }
        }));
        Mockito.when(TestRestServiceFactory.hnRestService.cachedItem(ArgumentMatchers.eq("2"))).thenReturn(kid1Call);
        Call<HackerNewsItem> kid2Call = Mockito.mock(Call.class);
        Mockito.when(kid2Call.execute()).thenThrow(IOException.class);
        Mockito.when(TestRestServiceFactory.hnRestService.cachedItem(ArgumentMatchers.eq("3"))).thenReturn(kid2Call);
        Mockito.when(TestRestServiceFactory.hnRestService.networkItem(ArgumentMatchers.eq("3"))).thenReturn(kid2Call);
        PreferenceManager.getDefaultSharedPreferences(service).edit().putBoolean(service.getString(pref_offline_notification), true).apply();
        syncScheduler.scheduleSync(service, "1");
        adapter.onPerformSync(Mockito.mock(Account.class), getLastSyncExtras(), null, null, null);
        Mockito.verify(readabilityClient).parse(ArgumentMatchers.any(), ArgumentMatchers.eq("http://example.com"), readabilityCallbackCaptor.capture());
        readabilityCallbackCaptor.getValue().onResponse("");
        ShadowNotificationManager notificationManager = Shadows.shadowOf(((NotificationManager) (service.getSystemService(NOTIFICATION_SERVICE))));
        ShadowNotification shadowNotification = Shadows.shadowOf(notificationManager.getNotification(1));
        assertThat(shadowNotification.getProgress()).isEqualTo(3);// self + kid 1 + readability

        assertThat(shadowNotification.getMax()).isEqualTo(104);// self + 2 kids + readability + web

        Shadows.shadowOf(adapter.syncDelegate.mWebView).getWebChromeClient().onProgressChanged(adapter.syncDelegate.mWebView, 100);
        Mockito.verify(kid2Call).enqueue(callbackCapture.capture());
        callbackCapture.getValue().onFailure(null, null);
        assertThat(notificationManager.getAllNotifications()).isEmpty();
    }

    @Test
    public void testBindService() {
        Assert.assertNotNull(service.onBind(null));
    }

    @Test
    public void testWifiChange() {
        setNetworkType(TYPE_MOBILE);
        new ItemSyncWifiReceiver().onReceive(service, new Intent(ConnectivityManager.CONNECTIVITY_ACTION));
        Assert.assertFalse(ShadowContentResolver.isSyncActive(createSyncAccount(), PROVIDER_AUTHORITY));
        setNetworkType(TYPE_WIFI);
        new ItemSyncWifiReceiver().onReceive(service, new Intent());
        Assert.assertFalse(ShadowContentResolver.isSyncActive(createSyncAccount(), PROVIDER_AUTHORITY));
        setNetworkType(TYPE_WIFI);
        new ItemSyncWifiReceiver().onReceive(service, new Intent(ConnectivityManager.CONNECTIVITY_ACTION));
        Assert.assertTrue(ShadowContentResolver.isSyncActive(createSyncAccount(), PROVIDER_AUTHORITY));
    }

    private static class TestItemSyncAdapter extends ItemSyncAdapter {
        SyncDelegate syncDelegate;

        TestItemSyncAdapter(Context context, RestServiceFactory factory, ReadabilityClient readabilityClient) {
            super(context, factory, readabilityClient);
        }

        @NonNull
        @Override
        SyncDelegate createSyncDelegate() {
            syncDelegate = super.createSyncDelegate();
            return syncDelegate;
        }
    }
}

