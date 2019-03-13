package com.baeldung.spring.cloud.aws.sns;


import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.cloud.aws.messaging.endpoint.NotificationStatus;


public class SNSEndpointControllerUnitTest {
    SNSEndpointController snsEndpointController;

    @Test
    public void whenReceivedNotificationInvoked_thenSuccess() {
        snsEndpointController.receiveNotification("Message", "Subject");
    }

    @Test
    public void whenConfirmUnsubscribeReturned_thenSuccess() {
        NotificationStatus notificationStatus = Mockito.mock(NotificationStatus.class);
        Mockito.doNothing().when(notificationStatus).confirmSubscription();
        snsEndpointController.confirmUnsubscribeMessage(notificationStatus);
    }

    @Test
    public void whenConfirmSubscriptionReturned_thenSuccess() {
        NotificationStatus notificationStatus = Mockito.mock(NotificationStatus.class);
        Mockito.doNothing().when(notificationStatus).confirmSubscription();
        snsEndpointController.confirmSubscriptionMessage(notificationStatus);
    }
}

