package org.whispersystems.textsecuregcm.tests.push;


import ApnMessage.APN_PAYLOAD;
import ApnResult.Status.GENERIC_FAILURE;
import ApnResult.Status.NO_SUCH_USER;
import ApnResult.Status.SUCCESS;
import DeliveryPriority.IMMEDIATE;
import com.google.common.util.concurrent.ListenableFuture;
import com.relayrides.pushy.apns.ApnsClient;
import com.relayrides.pushy.apns.ApnsServerException;
import com.relayrides.pushy.apns.ClientNotConnectedException;
import com.relayrides.pushy.apns.PushNotificationResponse;
import com.relayrides.pushy.apns.util.SimpleApnsPushNotification;
import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.DefaultPromise;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.whispersystems.textsecuregcm.push.APNSender;
import org.whispersystems.textsecuregcm.push.ApnFallbackManager;
import org.whispersystems.textsecuregcm.push.ApnMessage;
import org.whispersystems.textsecuregcm.push.RetryingApnsClient;
import org.whispersystems.textsecuregcm.push.RetryingApnsClient.ApnResult;
import org.whispersystems.textsecuregcm.storage.Account;
import org.whispersystems.textsecuregcm.storage.AccountsManager;
import org.whispersystems.textsecuregcm.storage.Device;
import org.whispersystems.textsecuregcm.tests.util.SynchronousExecutorService;


public class APNSenderTest {
    private static final String DESTINATION_NUMBER = "+14151231234";

    private static final String DESTINATION_APN_ID = "foo";

    private final AccountsManager accountsManager = Mockito.mock(AccountsManager.class);

    private final Account destinationAccount = Mockito.mock(Account.class);

    private final Device destinationDevice = Mockito.mock(Device.class);

    private final ApnFallbackManager fallbackManager = Mockito.mock(ApnFallbackManager.class);

    private final DefaultEventExecutor executor = new DefaultEventExecutor();

    @Test
    public void testSendVoip() throws Exception {
        ApnsClient apnsClient = Mockito.mock(ApnsClient.class);
        PushNotificationResponse<SimpleApnsPushNotification> response = Mockito.mock(PushNotificationResponse.class);
        Mockito.when(response.isAccepted()).thenReturn(true);
        DefaultPromise<PushNotificationResponse<SimpleApnsPushNotification>> result = new DefaultPromise(executor);
        result.setSuccess(response);
        Mockito.when(apnsClient.sendNotification(ArgumentMatchers.any(SimpleApnsPushNotification.class))).thenReturn(result);
        RetryingApnsClient retryingApnsClient = new RetryingApnsClient(apnsClient, 10);
        ApnMessage message = new ApnMessage(APNSenderTest.DESTINATION_APN_ID, APNSenderTest.DESTINATION_NUMBER, 1, true);
        APNSender apnSender = new APNSender(new SynchronousExecutorService(), accountsManager, retryingApnsClient, "foo", false);
        apnSender.setApnFallbackManager(fallbackManager);
        ListenableFuture<ApnResult> sendFuture = apnSender.sendMessage(message);
        ApnResult apnResult = sendFuture.get();
        ArgumentCaptor<SimpleApnsPushNotification> notification = ArgumentCaptor.forClass(SimpleApnsPushNotification.class);
        Mockito.verify(apnsClient, Mockito.times(1)).sendNotification(notification.capture());
        assertThat(notification.getValue().getToken()).isEqualTo(APNSenderTest.DESTINATION_APN_ID);
        assertThat(notification.getValue().getExpiration()).isEqualTo(new Date(ApnMessage.MAX_EXPIRATION));
        assertThat(notification.getValue().getPayload()).isEqualTo(APN_PAYLOAD);
        assertThat(notification.getValue().getPriority()).isEqualTo(IMMEDIATE);
        assertThat(notification.getValue().getTopic()).isEqualTo("foo.voip");
        assertThat(apnResult.getStatus()).isEqualTo(SUCCESS);
        Mockito.verifyNoMoreInteractions(apnsClient);
        Mockito.verifyNoMoreInteractions(accountsManager);
        Mockito.verifyNoMoreInteractions(fallbackManager);
    }

    @Test
    public void testSendApns() throws Exception {
        ApnsClient apnsClient = Mockito.mock(ApnsClient.class);
        PushNotificationResponse<SimpleApnsPushNotification> response = Mockito.mock(PushNotificationResponse.class);
        Mockito.when(response.isAccepted()).thenReturn(true);
        DefaultPromise<PushNotificationResponse<SimpleApnsPushNotification>> result = new DefaultPromise(executor);
        result.setSuccess(response);
        Mockito.when(apnsClient.sendNotification(ArgumentMatchers.any(SimpleApnsPushNotification.class))).thenReturn(result);
        RetryingApnsClient retryingApnsClient = new RetryingApnsClient(apnsClient, 10);
        ApnMessage message = new ApnMessage(APNSenderTest.DESTINATION_APN_ID, APNSenderTest.DESTINATION_NUMBER, 1, false);
        APNSender apnSender = new APNSender(new SynchronousExecutorService(), accountsManager, retryingApnsClient, "foo", false);
        apnSender.setApnFallbackManager(fallbackManager);
        ListenableFuture<ApnResult> sendFuture = apnSender.sendMessage(message);
        ApnResult apnResult = sendFuture.get();
        ArgumentCaptor<SimpleApnsPushNotification> notification = ArgumentCaptor.forClass(SimpleApnsPushNotification.class);
        Mockito.verify(apnsClient, Mockito.times(1)).sendNotification(notification.capture());
        assertThat(notification.getValue().getToken()).isEqualTo(APNSenderTest.DESTINATION_APN_ID);
        assertThat(notification.getValue().getExpiration()).isEqualTo(new Date(ApnMessage.MAX_EXPIRATION));
        assertThat(notification.getValue().getPayload()).isEqualTo(APN_PAYLOAD);
        assertThat(notification.getValue().getPriority()).isEqualTo(IMMEDIATE);
        assertThat(notification.getValue().getTopic()).isEqualTo("foo");
        assertThat(apnResult.getStatus()).isEqualTo(SUCCESS);
        Mockito.verifyNoMoreInteractions(apnsClient);
        Mockito.verifyNoMoreInteractions(accountsManager);
        Mockito.verifyNoMoreInteractions(fallbackManager);
    }

    @Test
    public void testUnregisteredUser() throws Exception {
        ApnsClient apnsClient = Mockito.mock(ApnsClient.class);
        PushNotificationResponse<SimpleApnsPushNotification> response = Mockito.mock(PushNotificationResponse.class);
        Mockito.when(response.isAccepted()).thenReturn(false);
        Mockito.when(response.getRejectionReason()).thenReturn("Unregistered");
        DefaultPromise<PushNotificationResponse<SimpleApnsPushNotification>> result = new DefaultPromise(executor);
        result.setSuccess(response);
        Mockito.when(apnsClient.sendNotification(ArgumentMatchers.any(SimpleApnsPushNotification.class))).thenReturn(result);
        RetryingApnsClient retryingApnsClient = new RetryingApnsClient(apnsClient, 10);
        ApnMessage message = new ApnMessage(APNSenderTest.DESTINATION_APN_ID, APNSenderTest.DESTINATION_NUMBER, 1, true);
        APNSender apnSender = new APNSender(new SynchronousExecutorService(), accountsManager, retryingApnsClient, "foo", false);
        apnSender.setApnFallbackManager(fallbackManager);
        Mockito.when(destinationDevice.getApnId()).thenReturn(APNSenderTest.DESTINATION_APN_ID);
        Mockito.when(destinationDevice.getPushTimestamp()).thenReturn(((System.currentTimeMillis()) - (TimeUnit.SECONDS.toMillis(11))));
        ListenableFuture<ApnResult> sendFuture = apnSender.sendMessage(message);
        ApnResult apnResult = sendFuture.get();
        Thread.sleep(1000);// =(

        ArgumentCaptor<SimpleApnsPushNotification> notification = ArgumentCaptor.forClass(SimpleApnsPushNotification.class);
        Mockito.verify(apnsClient, Mockito.times(1)).sendNotification(notification.capture());
        assertThat(notification.getValue().getToken()).isEqualTo(APNSenderTest.DESTINATION_APN_ID);
        assertThat(notification.getValue().getExpiration()).isEqualTo(new Date(ApnMessage.MAX_EXPIRATION));
        assertThat(notification.getValue().getPayload()).isEqualTo(APN_PAYLOAD);
        assertThat(notification.getValue().getPriority()).isEqualTo(IMMEDIATE);
        assertThat(apnResult.getStatus()).isEqualTo(NO_SUCH_USER);
        Mockito.verifyNoMoreInteractions(apnsClient);
        Mockito.verify(accountsManager, Mockito.times(1)).get(ArgumentMatchers.eq(APNSenderTest.DESTINATION_NUMBER));
        Mockito.verify(destinationAccount, Mockito.times(1)).getDevice(1);
        Mockito.verify(destinationDevice, Mockito.times(1)).getApnId();
        Mockito.verify(destinationDevice, Mockito.times(1)).getPushTimestamp();
        // verify(destinationDevice, times(1)).setApnId(eq((String)null));
        // verify(destinationDevice, times(1)).setVoipApnId(eq((String)null));
        // verify(destinationDevice, times(1)).setFetchesMessages(eq(false));
        // verify(accountsManager, times(1)).update(eq(destinationAccount));
        Mockito.verify(fallbackManager, Mockito.times(1)).cancel(ArgumentMatchers.eq(destinationAccount), ArgumentMatchers.eq(destinationDevice));
        Mockito.verifyNoMoreInteractions(accountsManager);
        Mockito.verifyNoMoreInteractions(fallbackManager);
    }

    // @Test
    // public void testVoipUnregisteredUser() throws Exception {
    // ApnsClient      apnsClient      = mock(ApnsClient.class);
    // 
    // PushNotificationResponse<SimpleApnsPushNotification> response = mock(PushNotificationResponse.class);
    // when(response.isAccepted()).thenReturn(false);
    // when(response.getRejectionReason()).thenReturn("Unregistered");
    // 
    // DefaultPromise<PushNotificationResponse<SimpleApnsPushNotification>> result = new DefaultPromise<>(executor);
    // result.setSuccess(response);
    // 
    // when(apnsClient.sendNotification(any(SimpleApnsPushNotification.class)))
    // .thenReturn(result);
    // 
    // RetryingApnsClient retryingApnsClient = new RetryingApnsClient(apnsClient, 10);
    // ApnMessage         message            = new ApnMessage(DESTINATION_APN_ID, DESTINATION_NUMBER, 1, "message", true, 30);
    // APNSender          apnSender          = new APNSender(new SynchronousExecutorService(), accountsManager, retryingApnsClient, "foo", false);
    // apnSender.setApnFallbackManager(fallbackManager);
    // 
    // when(destinationDevice.getApnId()).thenReturn("baz");
    // when(destinationDevice.getVoipApnId()).thenReturn(DESTINATION_APN_ID);
    // when(destinationDevice.getPushTimestamp()).thenReturn(System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(11));
    // 
    // ListenableFuture<ApnResult> sendFuture = apnSender.sendMessage(message);
    // ApnResult apnResult = sendFuture.get();
    // 
    // Thread.sleep(1000); // =(
    // 
    // ArgumentCaptor<SimpleApnsPushNotification> notification = ArgumentCaptor.forClass(SimpleApnsPushNotification.class);
    // verify(apnsClient, times(1)).sendNotification(notification.capture());
    // 
    // assertThat(notification.getValue().getToken()).isEqualTo(DESTINATION_APN_ID);
    // assertThat(notification.getValue().getExpiration()).isEqualTo(new Date(30));
    // assertThat(notification.getValue().getPayload()).isEqualTo("message");
    // assertThat(notification.getValue().getPriority()).isEqualTo(DeliveryPriority.IMMEDIATE);
    // 
    // assertThat(apnResult.getStatus()).isEqualTo(ApnResult.Status.NO_SUCH_USER);
    // 
    // verifyNoMoreInteractions(apnsClient);
    // verify(accountsManager, times(1)).get(eq(DESTINATION_NUMBER));
    // verify(destinationAccount, times(1)).getDevice(1);
    // verify(destinationDevice, times(1)).getApnId();
    // verify(destinationDevice, times(1)).getVoipApnId();
    // verify(destinationDevice, times(1)).getPushTimestamp();
    // verify(destinationDevice, times(1)).setApnId(eq((String)null));
    // verify(destinationDevice, times(1)).setVoipApnId(eq((String)null));
    // verify(destinationDevice, times(1)).setFetchesMessages(eq(false));
    // verify(accountsManager, times(1)).update(eq(destinationAccount));
    // verify(fallbackManager, times(1)).cancel(eq(new WebsocketAddress(DESTINATION_NUMBER, 1)));
    // 
    // verifyNoMoreInteractions(accountsManager);
    // verifyNoMoreInteractions(fallbackManager);
    // }
    @Test
    public void testRecentUnregisteredUser() throws Exception {
        ApnsClient apnsClient = Mockito.mock(ApnsClient.class);
        PushNotificationResponse<SimpleApnsPushNotification> response = Mockito.mock(PushNotificationResponse.class);
        Mockito.when(response.isAccepted()).thenReturn(false);
        Mockito.when(response.getRejectionReason()).thenReturn("Unregistered");
        DefaultPromise<PushNotificationResponse<SimpleApnsPushNotification>> result = new DefaultPromise(executor);
        result.setSuccess(response);
        Mockito.when(apnsClient.sendNotification(ArgumentMatchers.any(SimpleApnsPushNotification.class))).thenReturn(result);
        RetryingApnsClient retryingApnsClient = new RetryingApnsClient(apnsClient, 10);
        ApnMessage message = new ApnMessage(APNSenderTest.DESTINATION_APN_ID, APNSenderTest.DESTINATION_NUMBER, 1, true);
        APNSender apnSender = new APNSender(new SynchronousExecutorService(), accountsManager, retryingApnsClient, "foo", false);
        apnSender.setApnFallbackManager(fallbackManager);
        Mockito.when(destinationDevice.getApnId()).thenReturn(APNSenderTest.DESTINATION_APN_ID);
        Mockito.when(destinationDevice.getPushTimestamp()).thenReturn(System.currentTimeMillis());
        ListenableFuture<ApnResult> sendFuture = apnSender.sendMessage(message);
        ApnResult apnResult = sendFuture.get();
        Thread.sleep(1000);// =(

        ArgumentCaptor<SimpleApnsPushNotification> notification = ArgumentCaptor.forClass(SimpleApnsPushNotification.class);
        Mockito.verify(apnsClient, Mockito.times(1)).sendNotification(notification.capture());
        assertThat(notification.getValue().getToken()).isEqualTo(APNSenderTest.DESTINATION_APN_ID);
        assertThat(notification.getValue().getExpiration()).isEqualTo(new Date(ApnMessage.MAX_EXPIRATION));
        assertThat(notification.getValue().getPayload()).isEqualTo(APN_PAYLOAD);
        assertThat(notification.getValue().getPriority()).isEqualTo(IMMEDIATE);
        assertThat(apnResult.getStatus()).isEqualTo(NO_SUCH_USER);
        Mockito.verifyNoMoreInteractions(apnsClient);
        Mockito.verify(accountsManager, Mockito.times(1)).get(ArgumentMatchers.eq(APNSenderTest.DESTINATION_NUMBER));
        Mockito.verify(destinationAccount, Mockito.times(1)).getDevice(1);
        Mockito.verify(destinationDevice, Mockito.times(1)).getApnId();
        Mockito.verify(destinationDevice, Mockito.times(1)).getPushTimestamp();
        Mockito.verifyNoMoreInteractions(destinationDevice);
        Mockito.verifyNoMoreInteractions(destinationAccount);
        Mockito.verifyNoMoreInteractions(accountsManager);
        Mockito.verifyNoMoreInteractions(fallbackManager);
    }

    // @Test
    // public void testUnregisteredUserOldApnId() throws Exception {
    // ApnsClient      apnsClient      = mock(ApnsClient.class);
    // 
    // PushNotificationResponse<SimpleApnsPushNotification> response = mock(PushNotificationResponse.class);
    // when(response.isAccepted()).thenReturn(false);
    // when(response.getRejectionReason()).thenReturn("Unregistered");
    // 
    // DefaultPromise<PushNotificationResponse<SimpleApnsPushNotification>> result = new DefaultPromise<>(executor);
    // result.setSuccess(response);
    // 
    // when(apnsClient.sendNotification(any(SimpleApnsPushNotification.class)))
    // .thenReturn(result);
    // 
    // RetryingApnsClient retryingApnsClient = new RetryingApnsClient(apnsClient, 10);
    // ApnMessage         message            = new ApnMessage(DESTINATION_APN_ID, DESTINATION_NUMBER, 1, "message", true, 30);
    // APNSender          apnSender          = new APNSender(new SynchronousExecutorService(), accountsManager, retryingApnsClient, "foo", false);
    // apnSender.setApnFallbackManager(fallbackManager);
    // 
    // when(destinationDevice.getApnId()).thenReturn("baz");
    // when(destinationDevice.getPushTimestamp()).thenReturn(System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(12));
    // 
    // ListenableFuture<ApnResult> sendFuture = apnSender.sendMessage(message);
    // ApnResult apnResult = sendFuture.get();
    // 
    // Thread.sleep(1000); // =(
    // 
    // ArgumentCaptor<SimpleApnsPushNotification> notification = ArgumentCaptor.forClass(SimpleApnsPushNotification.class);
    // verify(apnsClient, times(1)).sendNotification(notification.capture());
    // 
    // assertThat(notification.getValue().getToken()).isEqualTo(DESTINATION_APN_ID);
    // assertThat(notification.getValue().getExpiration()).isEqualTo(new Date(30));
    // assertThat(notification.getValue().getPayload()).isEqualTo("message");
    // assertThat(notification.getValue().getPriority()).isEqualTo(DeliveryPriority.IMMEDIATE);
    // 
    // assertThat(apnResult.getStatus()).isEqualTo(ApnResult.Status.NO_SUCH_USER);
    // 
    // verifyNoMoreInteractions(apnsClient);
    // verify(accountsManager, times(1)).get(eq(DESTINATION_NUMBER));
    // verify(destinationAccount, times(1)).getDevice(1);
    // verify(destinationDevice, times(2)).getApnId();
    // verify(destinationDevice, times(2)).getVoipApnId();
    // 
    // verifyNoMoreInteractions(destinationDevice);
    // verifyNoMoreInteractions(destinationAccount);
    // verifyNoMoreInteractions(accountsManager);
    // verifyNoMoreInteractions(fallbackManager);
    // }
    @Test
    public void testGenericFailure() throws Exception {
        ApnsClient apnsClient = Mockito.mock(ApnsClient.class);
        PushNotificationResponse<SimpleApnsPushNotification> response = Mockito.mock(PushNotificationResponse.class);
        Mockito.when(response.isAccepted()).thenReturn(false);
        Mockito.when(response.getRejectionReason()).thenReturn("BadTopic");
        DefaultPromise<PushNotificationResponse<SimpleApnsPushNotification>> result = new DefaultPromise(executor);
        result.setSuccess(response);
        Mockito.when(apnsClient.sendNotification(ArgumentMatchers.any(SimpleApnsPushNotification.class))).thenReturn(result);
        RetryingApnsClient retryingApnsClient = new RetryingApnsClient(apnsClient, 10);
        ApnMessage message = new ApnMessage(APNSenderTest.DESTINATION_APN_ID, APNSenderTest.DESTINATION_NUMBER, 1, true);
        APNSender apnSender = new APNSender(new SynchronousExecutorService(), accountsManager, retryingApnsClient, "foo", false);
        apnSender.setApnFallbackManager(fallbackManager);
        ListenableFuture<ApnResult> sendFuture = apnSender.sendMessage(message);
        ApnResult apnResult = sendFuture.get();
        ArgumentCaptor<SimpleApnsPushNotification> notification = ArgumentCaptor.forClass(SimpleApnsPushNotification.class);
        Mockito.verify(apnsClient, Mockito.times(1)).sendNotification(notification.capture());
        assertThat(notification.getValue().getToken()).isEqualTo(APNSenderTest.DESTINATION_APN_ID);
        assertThat(notification.getValue().getExpiration()).isEqualTo(new Date(ApnMessage.MAX_EXPIRATION));
        assertThat(notification.getValue().getPayload()).isEqualTo(APN_PAYLOAD);
        assertThat(notification.getValue().getPriority()).isEqualTo(IMMEDIATE);
        assertThat(apnResult.getStatus()).isEqualTo(GENERIC_FAILURE);
        Mockito.verifyNoMoreInteractions(apnsClient);
        Mockito.verifyNoMoreInteractions(accountsManager);
        Mockito.verifyNoMoreInteractions(fallbackManager);
    }

    @Test
    public void testTransientFailure() throws Exception {
        ApnsClient apnsClient = Mockito.mock(ApnsClient.class);
        PushNotificationResponse<SimpleApnsPushNotification> response = Mockito.mock(PushNotificationResponse.class);
        Mockito.when(response.isAccepted()).thenReturn(true);
        DefaultPromise<PushNotificationResponse<SimpleApnsPushNotification>> result = new DefaultPromise(executor);
        result.setFailure(new ClientNotConnectedException("lost connection"));
        DefaultPromise<Void> connectedResult = new DefaultPromise(executor);
        Mockito.when(apnsClient.sendNotification(ArgumentMatchers.any(SimpleApnsPushNotification.class))).thenReturn(result);
        Mockito.when(apnsClient.getReconnectionFuture()).thenReturn(connectedResult);
        RetryingApnsClient retryingApnsClient = new RetryingApnsClient(apnsClient, 10);
        ApnMessage message = new ApnMessage(APNSenderTest.DESTINATION_APN_ID, APNSenderTest.DESTINATION_NUMBER, 1, true);
        APNSender apnSender = new APNSender(new SynchronousExecutorService(), accountsManager, retryingApnsClient, "foo", false);
        apnSender.setApnFallbackManager(fallbackManager);
        ListenableFuture<ApnResult> sendFuture = apnSender.sendMessage(message);
        Thread.sleep(1000);
        assertThat(sendFuture.isDone()).isFalse();
        DefaultPromise<PushNotificationResponse<SimpleApnsPushNotification>> updatedResult = new DefaultPromise(executor);
        updatedResult.setSuccess(response);
        Mockito.when(apnsClient.sendNotification(ArgumentMatchers.any(SimpleApnsPushNotification.class))).thenReturn(updatedResult);
        connectedResult.setSuccess(null);
        ApnResult apnResult = sendFuture.get();
        ArgumentCaptor<SimpleApnsPushNotification> notification = ArgumentCaptor.forClass(SimpleApnsPushNotification.class);
        Mockito.verify(apnsClient, Mockito.times(2)).sendNotification(notification.capture());
        Mockito.verify(apnsClient, Mockito.times(1)).getReconnectionFuture();
        assertThat(notification.getValue().getToken()).isEqualTo(APNSenderTest.DESTINATION_APN_ID);
        assertThat(notification.getValue().getExpiration()).isEqualTo(new Date(ApnMessage.MAX_EXPIRATION));
        assertThat(notification.getValue().getPayload()).isEqualTo(APN_PAYLOAD);
        assertThat(notification.getValue().getPriority()).isEqualTo(IMMEDIATE);
        assertThat(apnResult.getStatus()).isEqualTo(SUCCESS);
        Mockito.verifyNoMoreInteractions(apnsClient);
        Mockito.verifyNoMoreInteractions(accountsManager);
        Mockito.verifyNoMoreInteractions(fallbackManager);
    }

    @Test
    public void testPersistentTransientFailure() throws Exception {
        ApnsClient apnsClient = Mockito.mock(ApnsClient.class);
        PushNotificationResponse<SimpleApnsPushNotification> response = Mockito.mock(PushNotificationResponse.class);
        Mockito.when(response.isAccepted()).thenReturn(true);
        DefaultPromise<PushNotificationResponse<SimpleApnsPushNotification>> result = new DefaultPromise(executor);
        result.setFailure(new ApnsServerException("apn servers suck again"));
        Mockito.when(apnsClient.sendNotification(ArgumentMatchers.any(SimpleApnsPushNotification.class))).thenReturn(result);
        RetryingApnsClient retryingApnsClient = new RetryingApnsClient(apnsClient, 3);
        ApnMessage message = new ApnMessage(APNSenderTest.DESTINATION_APN_ID, APNSenderTest.DESTINATION_NUMBER, 1, true);
        APNSender apnSender = new APNSender(new SynchronousExecutorService(), accountsManager, retryingApnsClient, "foo", false);
        apnSender.setApnFallbackManager(fallbackManager);
        ListenableFuture<ApnResult> sendFuture = apnSender.sendMessage(message);
        try {
            sendFuture.get();
            throw new AssertionError("future did not throw exception");
        } catch (Exception e) {
            // good
        }
        ArgumentCaptor<SimpleApnsPushNotification> notification = ArgumentCaptor.forClass(SimpleApnsPushNotification.class);
        Mockito.verify(apnsClient, Mockito.times(4)).sendNotification(notification.capture());
        assertThat(notification.getValue().getToken()).isEqualTo(APNSenderTest.DESTINATION_APN_ID);
        assertThat(notification.getValue().getExpiration()).isEqualTo(new Date(ApnMessage.MAX_EXPIRATION));
        assertThat(notification.getValue().getPayload()).isEqualTo(APN_PAYLOAD);
        assertThat(notification.getValue().getPriority()).isEqualTo(IMMEDIATE);
        Mockito.verifyNoMoreInteractions(apnsClient);
        Mockito.verifyNoMoreInteractions(accountsManager);
        Mockito.verifyNoMoreInteractions(fallbackManager);
    }
}

