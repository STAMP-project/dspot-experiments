package com.ctrip.framework.apollo.internals;


import HttpServletResponse.SC_NOT_MODIFIED;
import HttpServletResponse.SC_OK;
import com.ctrip.framework.apollo.core.dto.ApolloConfigNotification;
import com.ctrip.framework.apollo.core.dto.ApolloNotificationMessages;
import com.ctrip.framework.apollo.core.dto.ServiceDTO;
import com.ctrip.framework.apollo.util.ConfigUtil;
import com.ctrip.framework.apollo.util.http.HttpRequest;
import com.ctrip.framework.apollo.util.http.HttpResponse;
import com.ctrip.framework.apollo.util.http.HttpUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.SettableFuture;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class RemoteConfigLongPollServiceTest {
    private RemoteConfigLongPollService remoteConfigLongPollService;

    @Mock
    private HttpResponse<List<ApolloConfigNotification>> pollResponse;

    @Mock
    private HttpUtil httpUtil;

    @Mock
    private ConfigServiceLocator configServiceLocator;

    private Type responseType;

    private static String someServerUrl;

    private static String someAppId;

    private static String someCluster;

    @Test
    public void testSubmitLongPollNamespaceWith304Response() throws Exception {
        RemoteConfigRepository someRepository = Mockito.mock(RemoteConfigRepository.class);
        final String someNamespace = "someNamespace";
        Mockito.when(pollResponse.getStatusCode()).thenReturn(SC_NOT_MODIFIED);
        final SettableFuture<Boolean> longPollFinished = SettableFuture.create();
        Mockito.doAnswer(new Answer<HttpResponse<List<ApolloConfigNotification>>>() {
            @Override
            public HttpResponse<List<ApolloConfigNotification>> answer(InvocationOnMock invocation) throws Throwable {
                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException e) {
                }
                HttpRequest request = getArgumentAt(0, HttpRequest.class);
                Assert.assertTrue(request.getUrl().contains(((RemoteConfigLongPollServiceTest.someServerUrl) + "/notifications/v2?")));
                Assert.assertTrue(request.getUrl().contains(("appId=" + (RemoteConfigLongPollServiceTest.someAppId))));
                Assert.assertTrue(request.getUrl().contains(("cluster=" + (RemoteConfigLongPollServiceTest.someCluster))));
                Assert.assertTrue(request.getUrl().contains("notifications="));
                Assert.assertTrue(request.getUrl().contains(someNamespace));
                longPollFinished.set(true);
                return pollResponse;
            }
        }).when(httpUtil).doGet(ArgumentMatchers.any(HttpRequest.class), ArgumentMatchers.eq(responseType));
        remoteConfigLongPollService.submit(someNamespace, someRepository);
        longPollFinished.get(5000, TimeUnit.MILLISECONDS);
        remoteConfigLongPollService.stopLongPollingRefresh();
        Mockito.verify(someRepository, Mockito.never()).onLongPollNotified(ArgumentMatchers.any(ServiceDTO.class), ArgumentMatchers.any(ApolloNotificationMessages.class));
    }

    @Test
    public void testSubmitLongPollNamespaceWith200Response() throws Exception {
        RemoteConfigRepository someRepository = Mockito.mock(RemoteConfigRepository.class);
        final String someNamespace = "someNamespace";
        ApolloNotificationMessages notificationMessages = new ApolloNotificationMessages();
        String someKey = "someKey";
        long someNotificationId = 1;
        String anotherKey = "anotherKey";
        long anotherNotificationId = 2;
        notificationMessages.put(someKey, someNotificationId);
        notificationMessages.put(anotherKey, anotherNotificationId);
        ApolloConfigNotification someNotification = Mockito.mock(ApolloConfigNotification.class);
        Mockito.when(someNotification.getNamespaceName()).thenReturn(someNamespace);
        Mockito.when(someNotification.getMessages()).thenReturn(notificationMessages);
        Mockito.when(pollResponse.getStatusCode()).thenReturn(SC_OK);
        Mockito.when(pollResponse.getBody()).thenReturn(Lists.newArrayList(someNotification));
        Mockito.doAnswer(new Answer<HttpResponse<List<ApolloConfigNotification>>>() {
            @Override
            public HttpResponse<List<ApolloConfigNotification>> answer(InvocationOnMock invocation) throws Throwable {
                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException e) {
                }
                return pollResponse;
            }
        }).when(httpUtil).doGet(ArgumentMatchers.any(HttpRequest.class), ArgumentMatchers.eq(responseType));
        final SettableFuture<Boolean> onNotified = SettableFuture.create();
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                onNotified.set(true);
                return null;
            }
        }).when(someRepository).onLongPollNotified(ArgumentMatchers.any(ServiceDTO.class), ArgumentMatchers.any(ApolloNotificationMessages.class));
        remoteConfigLongPollService.submit(someNamespace, someRepository);
        onNotified.get(5000, TimeUnit.MILLISECONDS);
        remoteConfigLongPollService.stopLongPollingRefresh();
        final ArgumentCaptor<ApolloNotificationMessages> captor = ArgumentCaptor.forClass(ApolloNotificationMessages.class);
        Mockito.verify(someRepository, Mockito.times(1)).onLongPollNotified(ArgumentMatchers.any(ServiceDTO.class), captor.capture());
        ApolloNotificationMessages captured = captor.getValue();
        Assert.assertEquals(2, captured.getDetails().size());
        Assert.assertEquals(someNotificationId, captured.get(someKey).longValue());
        Assert.assertEquals(anotherNotificationId, captured.get(anotherKey).longValue());
    }

    @Test
    public void testSubmitLongPollMultipleNamespaces() throws Exception {
        RemoteConfigRepository someRepository = Mockito.mock(RemoteConfigRepository.class);
        RemoteConfigRepository anotherRepository = Mockito.mock(RemoteConfigRepository.class);
        final String someNamespace = "someNamespace";
        final String anotherNamespace = "anotherNamespace";
        final ApolloConfigNotification someNotification = Mockito.mock(ApolloConfigNotification.class);
        Mockito.when(someNotification.getNamespaceName()).thenReturn(someNamespace);
        final ApolloConfigNotification anotherNotification = Mockito.mock(ApolloConfigNotification.class);
        Mockito.when(anotherNotification.getNamespaceName()).thenReturn(anotherNamespace);
        final SettableFuture<Boolean> submitAnotherNamespaceStart = SettableFuture.create();
        final SettableFuture<Boolean> submitAnotherNamespaceFinish = SettableFuture.create();
        Mockito.doAnswer(new Answer<HttpResponse<List<ApolloConfigNotification>>>() {
            final AtomicInteger counter = new AtomicInteger();

            @Override
            public HttpResponse<List<ApolloConfigNotification>> answer(InvocationOnMock invocation) throws Throwable {
                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException e) {
                }
                // the first time
                if ((counter.incrementAndGet()) == 1) {
                    HttpRequest request = getArgumentAt(0, HttpRequest.class);
                    Assert.assertTrue(request.getUrl().contains("notifications="));
                    Assert.assertTrue(request.getUrl().contains(someNamespace));
                    submitAnotherNamespaceStart.set(true);
                    Mockito.when(pollResponse.getStatusCode()).thenReturn(SC_OK);
                    Mockito.when(pollResponse.getBody()).thenReturn(Lists.newArrayList(someNotification));
                } else
                    if (submitAnotherNamespaceFinish.get()) {
                        HttpRequest request = getArgumentAt(0, HttpRequest.class);
                        Assert.assertTrue(request.getUrl().contains("notifications="));
                        Assert.assertTrue(request.getUrl().contains(someNamespace));
                        Assert.assertTrue(request.getUrl().contains(anotherNamespace));
                        Mockito.when(pollResponse.getStatusCode()).thenReturn(SC_OK);
                        Mockito.when(pollResponse.getBody()).thenReturn(Lists.newArrayList(anotherNotification));
                    } else {
                        Mockito.when(pollResponse.getStatusCode()).thenReturn(SC_NOT_MODIFIED);
                        Mockito.when(pollResponse.getBody()).thenReturn(null);
                    }

                return pollResponse;
            }
        }).when(httpUtil).doGet(ArgumentMatchers.any(HttpRequest.class), ArgumentMatchers.eq(responseType));
        final SettableFuture<Boolean> onAnotherRepositoryNotified = SettableFuture.create();
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                onAnotherRepositoryNotified.set(true);
                return null;
            }
        }).when(anotherRepository).onLongPollNotified(ArgumentMatchers.any(ServiceDTO.class), ArgumentMatchers.any(ApolloNotificationMessages.class));
        remoteConfigLongPollService.submit(someNamespace, someRepository);
        submitAnotherNamespaceStart.get(5000, TimeUnit.MILLISECONDS);
        remoteConfigLongPollService.submit(anotherNamespace, anotherRepository);
        submitAnotherNamespaceFinish.set(true);
        onAnotherRepositoryNotified.get(5000, TimeUnit.MILLISECONDS);
        remoteConfigLongPollService.stopLongPollingRefresh();
        Mockito.verify(someRepository, Mockito.times(1)).onLongPollNotified(ArgumentMatchers.any(ServiceDTO.class), ArgumentMatchers.any(ApolloNotificationMessages.class));
        Mockito.verify(anotherRepository, Mockito.times(1)).onLongPollNotified(ArgumentMatchers.any(ServiceDTO.class), ArgumentMatchers.any(ApolloNotificationMessages.class));
    }

    @Test
    public void testSubmitLongPollMultipleNamespacesWithMultipleNotificationsReturned() throws Exception {
        RemoteConfigRepository someRepository = Mockito.mock(RemoteConfigRepository.class);
        RemoteConfigRepository anotherRepository = Mockito.mock(RemoteConfigRepository.class);
        final String someNamespace = "someNamespace";
        final String anotherNamespace = "anotherNamespace";
        ApolloNotificationMessages notificationMessages = new ApolloNotificationMessages();
        String someKey = "someKey";
        long someNotificationId = 1;
        notificationMessages.put(someKey, someNotificationId);
        ApolloNotificationMessages anotherNotificationMessages = new ApolloNotificationMessages();
        String anotherKey = "anotherKey";
        long anotherNotificationId = 2;
        anotherNotificationMessages.put(anotherKey, anotherNotificationId);
        final ApolloConfigNotification someNotification = Mockito.mock(ApolloConfigNotification.class);
        Mockito.when(someNotification.getNamespaceName()).thenReturn(someNamespace);
        Mockito.when(someNotification.getMessages()).thenReturn(notificationMessages);
        final ApolloConfigNotification anotherNotification = Mockito.mock(ApolloConfigNotification.class);
        Mockito.when(anotherNotification.getNamespaceName()).thenReturn(anotherNamespace);
        Mockito.when(anotherNotification.getMessages()).thenReturn(anotherNotificationMessages);
        Mockito.when(pollResponse.getStatusCode()).thenReturn(SC_OK);
        Mockito.when(pollResponse.getBody()).thenReturn(Lists.newArrayList(someNotification, anotherNotification));
        Mockito.doAnswer(new Answer<HttpResponse<List<ApolloConfigNotification>>>() {
            @Override
            public HttpResponse<List<ApolloConfigNotification>> answer(InvocationOnMock invocation) throws Throwable {
                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException e) {
                }
                return pollResponse;
            }
        }).when(httpUtil).doGet(ArgumentMatchers.any(HttpRequest.class), ArgumentMatchers.eq(responseType));
        final SettableFuture<Boolean> someRepositoryNotified = SettableFuture.create();
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                someRepositoryNotified.set(true);
                return null;
            }
        }).when(someRepository).onLongPollNotified(ArgumentMatchers.any(ServiceDTO.class), ArgumentMatchers.any(ApolloNotificationMessages.class));
        final SettableFuture<Boolean> anotherRepositoryNotified = SettableFuture.create();
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                anotherRepositoryNotified.set(true);
                return null;
            }
        }).when(anotherRepository).onLongPollNotified(ArgumentMatchers.any(ServiceDTO.class), ArgumentMatchers.any(ApolloNotificationMessages.class));
        remoteConfigLongPollService.submit(someNamespace, someRepository);
        remoteConfigLongPollService.submit(anotherNamespace, anotherRepository);
        someRepositoryNotified.get(5000, TimeUnit.MILLISECONDS);
        anotherRepositoryNotified.get(5000, TimeUnit.MILLISECONDS);
        remoteConfigLongPollService.stopLongPollingRefresh();
        final ArgumentCaptor<ApolloNotificationMessages> captor = ArgumentCaptor.forClass(ApolloNotificationMessages.class);
        final ArgumentCaptor<ApolloNotificationMessages> anotherCaptor = ArgumentCaptor.forClass(ApolloNotificationMessages.class);
        Mockito.verify(someRepository, Mockito.times(1)).onLongPollNotified(ArgumentMatchers.any(ServiceDTO.class), captor.capture());
        Mockito.verify(anotherRepository, Mockito.times(1)).onLongPollNotified(ArgumentMatchers.any(ServiceDTO.class), anotherCaptor.capture());
        ApolloNotificationMessages result = captor.getValue();
        Assert.assertEquals(1, result.getDetails().size());
        Assert.assertEquals(someNotificationId, result.get(someKey).longValue());
        ApolloNotificationMessages anotherResult = anotherCaptor.getValue();
        Assert.assertEquals(1, anotherResult.getDetails().size());
        Assert.assertEquals(anotherNotificationId, anotherResult.get(anotherKey).longValue());
    }

    @Test
    public void testSubmitLongPollNamespaceWithMessagesUpdated() throws Exception {
        RemoteConfigRepository someRepository = Mockito.mock(RemoteConfigRepository.class);
        final String someNamespace = "someNamespace";
        ApolloNotificationMessages notificationMessages = new ApolloNotificationMessages();
        String someKey = "someKey";
        long someNotificationId = 1;
        notificationMessages.put(someKey, someNotificationId);
        ApolloConfigNotification someNotification = Mockito.mock(ApolloConfigNotification.class);
        Mockito.when(someNotification.getNamespaceName()).thenReturn(someNamespace);
        Mockito.when(someNotification.getMessages()).thenReturn(notificationMessages);
        Mockito.when(pollResponse.getStatusCode()).thenReturn(SC_OK);
        Mockito.when(pollResponse.getBody()).thenReturn(Lists.newArrayList(someNotification));
        Mockito.doAnswer(new Answer<HttpResponse<List<ApolloConfigNotification>>>() {
            @Override
            public HttpResponse<List<ApolloConfigNotification>> answer(InvocationOnMock invocation) throws Throwable {
                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException e) {
                }
                return pollResponse;
            }
        }).when(httpUtil).doGet(ArgumentMatchers.any(HttpRequest.class), ArgumentMatchers.eq(responseType));
        final SettableFuture<Boolean> onNotified = SettableFuture.create();
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                onNotified.set(true);
                return null;
            }
        }).when(someRepository).onLongPollNotified(ArgumentMatchers.any(ServiceDTO.class), ArgumentMatchers.any(ApolloNotificationMessages.class));
        remoteConfigLongPollService.submit(someNamespace, someRepository);
        onNotified.get(5000, TimeUnit.MILLISECONDS);
        // reset to 304
        Mockito.when(pollResponse.getStatusCode()).thenReturn(SC_NOT_MODIFIED);
        final ArgumentCaptor<ApolloNotificationMessages> captor = ArgumentCaptor.forClass(ApolloNotificationMessages.class);
        Mockito.verify(someRepository, Mockito.times(1)).onLongPollNotified(ArgumentMatchers.any(ServiceDTO.class), captor.capture());
        ApolloNotificationMessages captured = captor.getValue();
        Assert.assertEquals(1, captured.getDetails().size());
        Assert.assertEquals(someNotificationId, captured.get(someKey).longValue());
        final SettableFuture<Boolean> anotherOnNotified = SettableFuture.create();
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                anotherOnNotified.set(true);
                return null;
            }
        }).when(someRepository).onLongPollNotified(ArgumentMatchers.any(ServiceDTO.class), ArgumentMatchers.any(ApolloNotificationMessages.class));
        String anotherKey = "anotherKey";
        long anotherNotificationId = 2;
        notificationMessages.put(anotherKey, anotherNotificationId);
        // send notifications
        Mockito.when(pollResponse.getStatusCode()).thenReturn(SC_OK);
        anotherOnNotified.get(5000, TimeUnit.MILLISECONDS);
        remoteConfigLongPollService.stopLongPollingRefresh();
        Mockito.verify(someRepository, Mockito.times(2)).onLongPollNotified(ArgumentMatchers.any(ServiceDTO.class), captor.capture());
        captured = captor.getValue();
        Assert.assertEquals(2, captured.getDetails().size());
        Assert.assertEquals(someNotificationId, captured.get(someKey).longValue());
        Assert.assertEquals(anotherNotificationId, captured.get(anotherKey).longValue());
    }

    @Test
    public void testAssembleLongPollRefreshUrl() throws Exception {
        String someUri = RemoteConfigLongPollServiceTest.someServerUrl;
        String someAppId = "someAppId";
        String someCluster = "someCluster+ &.-_someSign";
        String someNamespace = "someName";
        long someNotificationId = 1;
        Map<String, Long> notificationsMap = ImmutableMap.of(someNamespace, someNotificationId);
        String longPollRefreshUrl = remoteConfigLongPollService.assembleLongPollRefreshUrl(someUri, someAppId, someCluster, null, notificationsMap);
        Assert.assertTrue(longPollRefreshUrl.contains(((RemoteConfigLongPollServiceTest.someServerUrl) + "/notifications/v2?")));
        Assert.assertTrue(longPollRefreshUrl.contains(("appId=" + someAppId)));
        Assert.assertTrue(longPollRefreshUrl.contains("cluster=someCluster%2B+%26.-_someSign"));
        Assert.assertTrue(longPollRefreshUrl.contains((((("notifications=%5B%7B%22namespaceName%22%3A%22" + someNamespace) + "%22%2C%22notificationId%22%3A") + 1) + "%7D%5D")));
    }

    @Test
    public void testAssembleLongPollRefreshUrlWithMultipleNamespaces() throws Exception {
        String someUri = RemoteConfigLongPollServiceTest.someServerUrl;
        String someAppId = "someAppId";
        String someCluster = "someCluster+ &.-_someSign";
        String someNamespace = "someName";
        String anotherNamespace = "anotherName";
        long someNotificationId = 1;
        long anotherNotificationId = 2;
        Map<String, Long> notificationsMap = ImmutableMap.of(someNamespace, someNotificationId, anotherNamespace, anotherNotificationId);
        String longPollRefreshUrl = remoteConfigLongPollService.assembleLongPollRefreshUrl(someUri, someAppId, someCluster, null, notificationsMap);
        Assert.assertTrue(longPollRefreshUrl.contains(((RemoteConfigLongPollServiceTest.someServerUrl) + "/notifications/v2?")));
        Assert.assertTrue(longPollRefreshUrl.contains(("appId=" + someAppId)));
        Assert.assertTrue(longPollRefreshUrl.contains("cluster=someCluster%2B+%26.-_someSign"));
        Assert.assertTrue(longPollRefreshUrl.contains((((((((("notifications=%5B%7B%22namespaceName%22%3A%22" + someNamespace) + "%22%2C%22notificationId%22%3A") + someNotificationId) + "%7D%2C%7B%22namespaceName%22%3A%22") + anotherNamespace) + "%22%2C%22notificationId%22%3A") + anotherNotificationId) + "%7D%5D")));
    }

    public static class MockConfigUtil extends ConfigUtil {
        @Override
        public String getAppId() {
            return RemoteConfigLongPollServiceTest.someAppId;
        }

        @Override
        public String getCluster() {
            return RemoteConfigLongPollServiceTest.someCluster;
        }

        @Override
        public String getDataCenter() {
            return null;
        }

        @Override
        public int getLoadConfigQPS() {
            return 200;
        }

        @Override
        public int getLongPollQPS() {
            return 200;
        }

        @Override
        public long getLongPollingInitialDelayInMills() {
            return 0;
        }
    }
}

