package com.piggymetrics.notification.service;


import NotificationType.BACKUP;
import NotificationType.REMIND;
import com.google.common.collect.ImmutableList;
import com.piggymetrics.notification.client.AccountServiceClient;
import com.piggymetrics.notification.domain.Recipient;
import java.io.IOException;
import javax.mail.MessagingException;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


public class NotificationServiceImplTest {
    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Mock
    private RecipientService recipientService;

    @Mock
    private AccountServiceClient client;

    @Mock
    private EmailService emailService;

    @Test
    public void shouldSendBackupNotificationsEvenWhenErrorsOccursForSomeRecipients() throws IOException, InterruptedException, MessagingException {
        final String attachment = "json";
        Recipient withError = new Recipient();
        withError.setAccountName("with-error");
        Recipient withNoError = new Recipient();
        withNoError.setAccountName("with-no-error");
        Mockito.when(client.getAccount(withError.getAccountName())).thenThrow(new RuntimeException());
        Mockito.when(client.getAccount(withNoError.getAccountName())).thenReturn(attachment);
        Mockito.when(recipientService.findReadyToNotify(BACKUP)).thenReturn(ImmutableList.of(withNoError, withError));
        notificationService.sendBackupNotifications();
        // TODO test concurrent code in a right way
        Mockito.verify(emailService, Mockito.timeout(100)).send(BACKUP, withNoError, attachment);
        Mockito.verify(recipientService, Mockito.timeout(100)).markNotified(BACKUP, withNoError);
        Mockito.verify(recipientService, Mockito.never()).markNotified(BACKUP, withError);
    }

    @Test
    public void shouldSendRemindNotificationsEvenWhenErrorsOccursForSomeRecipients() throws IOException, InterruptedException, MessagingException {
        final String attachment = "json";
        Recipient withError = new Recipient();
        withError.setAccountName("with-error");
        Recipient withNoError = new Recipient();
        withNoError.setAccountName("with-no-error");
        Mockito.when(recipientService.findReadyToNotify(REMIND)).thenReturn(ImmutableList.of(withNoError, withError));
        Mockito.doThrow(new RuntimeException()).when(emailService).send(REMIND, withError, null);
        notificationService.sendRemindNotifications();
        // TODO test concurrent code in a right way
        Mockito.verify(emailService, Mockito.timeout(100)).send(REMIND, withNoError, null);
        Mockito.verify(recipientService, Mockito.timeout(100)).markNotified(REMIND, withNoError);
        Mockito.verify(recipientService, Mockito.never()).markNotified(REMIND, withError);
    }
}

