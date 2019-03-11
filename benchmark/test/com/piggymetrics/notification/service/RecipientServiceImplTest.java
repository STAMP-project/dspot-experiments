package com.piggymetrics.notification.service;


import Frequency.MONTHLY;
import Frequency.WEEKLY;
import NotificationType.BACKUP;
import NotificationType.REMIND;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.piggymetrics.notification.domain.NotificationSettings;
import com.piggymetrics.notification.domain.Recipient;
import com.piggymetrics.notification.repository.RecipientRepository;
import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


public class RecipientServiceImplTest {
    @InjectMocks
    private RecipientServiceImpl recipientService;

    @Mock
    private RecipientRepository repository;

    @Test
    public void shouldFindByAccountName() {
        Recipient recipient = new Recipient();
        recipient.setAccountName("test");
        Mockito.when(repository.findByAccountName(recipient.getAccountName())).thenReturn(recipient);
        Recipient found = recipientService.findByAccountName(recipient.getAccountName());
        Assert.assertEquals(recipient, found);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToFindRecipientWhenAccountNameIsEmpty() {
        recipientService.findByAccountName("");
    }

    @Test
    public void shouldSaveRecipient() {
        NotificationSettings remind = new NotificationSettings();
        remind.setActive(true);
        remind.setFrequency(WEEKLY);
        remind.setLastNotified(null);
        NotificationSettings backup = new NotificationSettings();
        backup.setActive(false);
        backup.setFrequency(MONTHLY);
        backup.setLastNotified(new Date());
        Recipient recipient = new Recipient();
        recipient.setEmail("test@test.com");
        recipient.setScheduledNotifications(ImmutableMap.of(BACKUP, backup, REMIND, remind));
        Recipient saved = recipientService.save("test", recipient);
        Mockito.verify(repository).save(recipient);
        Assert.assertNotNull(saved.getScheduledNotifications().get(REMIND).getLastNotified());
        Assert.assertEquals("test", saved.getAccountName());
    }

    @Test
    public void shouldFindReadyToNotifyWhenNotificationTypeIsBackup() {
        final List<Recipient> recipients = ImmutableList.of(new Recipient());
        Mockito.when(repository.findReadyForBackup()).thenReturn(recipients);
        List<Recipient> found = recipientService.findReadyToNotify(BACKUP);
        Assert.assertEquals(recipients, found);
    }

    @Test
    public void shouldFindReadyToNotifyWhenNotificationTypeIsRemind() {
        final List<Recipient> recipients = ImmutableList.of(new Recipient());
        Mockito.when(repository.findReadyForRemind()).thenReturn(recipients);
        List<Recipient> found = recipientService.findReadyToNotify(REMIND);
        Assert.assertEquals(recipients, found);
    }

    @Test
    public void shouldMarkAsNotified() {
        NotificationSettings remind = new NotificationSettings();
        remind.setActive(true);
        remind.setFrequency(WEEKLY);
        remind.setLastNotified(null);
        Recipient recipient = new Recipient();
        recipient.setAccountName("test");
        recipient.setEmail("test@test.com");
        recipient.setScheduledNotifications(ImmutableMap.of(REMIND, remind));
        recipientService.markNotified(REMIND, recipient);
        Assert.assertNotNull(recipient.getScheduledNotifications().get(REMIND).getLastNotified());
        Mockito.verify(repository).save(recipient);
    }
}

