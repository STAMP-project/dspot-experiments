package com.piggymetrics.notification.repository;


import Frequency.MONTHLY;
import Frequency.QUARTERLY;
import Frequency.WEEKLY;
import NotificationType.BACKUP;
import NotificationType.REMIND;
import com.google.common.collect.ImmutableMap;
import com.piggymetrics.notification.domain.NotificationSettings;
import com.piggymetrics.notification.domain.Recipient;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@DataMongoTest
public class RecipientRepositoryTest {
    @Autowired
    private RecipientRepository repository;

    @Test
    public void shouldFindByAccountName() {
        NotificationSettings remind = new NotificationSettings();
        remind.setActive(true);
        remind.setFrequency(WEEKLY);
        remind.setLastNotified(new Date(0));
        NotificationSettings backup = new NotificationSettings();
        backup.setActive(false);
        backup.setFrequency(MONTHLY);
        backup.setLastNotified(new Date());
        Recipient recipient = new Recipient();
        recipient.setAccountName("test");
        recipient.setEmail("test@test.com");
        recipient.setScheduledNotifications(ImmutableMap.of(BACKUP, backup, REMIND, remind));
        repository.save(recipient);
        Recipient found = repository.findByAccountName(recipient.getAccountName());
        Assert.assertEquals(recipient.getAccountName(), found.getAccountName());
        Assert.assertEquals(recipient.getEmail(), found.getEmail());
        Assert.assertEquals(recipient.getScheduledNotifications().get(BACKUP).getActive(), found.getScheduledNotifications().get(BACKUP).getActive());
        Assert.assertEquals(recipient.getScheduledNotifications().get(BACKUP).getFrequency(), found.getScheduledNotifications().get(BACKUP).getFrequency());
        Assert.assertEquals(recipient.getScheduledNotifications().get(BACKUP).getLastNotified(), found.getScheduledNotifications().get(BACKUP).getLastNotified());
        Assert.assertEquals(recipient.getScheduledNotifications().get(REMIND).getActive(), found.getScheduledNotifications().get(REMIND).getActive());
        Assert.assertEquals(recipient.getScheduledNotifications().get(REMIND).getFrequency(), found.getScheduledNotifications().get(REMIND).getFrequency());
        Assert.assertEquals(recipient.getScheduledNotifications().get(REMIND).getLastNotified(), found.getScheduledNotifications().get(REMIND).getLastNotified());
    }

    @Test
    public void shouldFindReadyForRemindWhenFrequencyIsWeeklyAndLastNotifiedWas8DaysAgo() {
        NotificationSettings remind = new NotificationSettings();
        remind.setActive(true);
        remind.setFrequency(WEEKLY);
        remind.setLastNotified(DateUtils.addDays(new Date(), (-8)));
        Recipient recipient = new Recipient();
        recipient.setAccountName("test");
        recipient.setEmail("test@test.com");
        recipient.setScheduledNotifications(ImmutableMap.of(REMIND, remind));
        repository.save(recipient);
        List<Recipient> found = repository.findReadyForRemind();
        Assert.assertFalse(found.isEmpty());
    }

    @Test
    public void shouldNotFindReadyForRemindWhenFrequencyIsWeeklyAndLastNotifiedWasYesterday() {
        NotificationSettings remind = new NotificationSettings();
        remind.setActive(true);
        remind.setFrequency(WEEKLY);
        remind.setLastNotified(DateUtils.addDays(new Date(), (-1)));
        Recipient recipient = new Recipient();
        recipient.setAccountName("test");
        recipient.setEmail("test@test.com");
        recipient.setScheduledNotifications(ImmutableMap.of(REMIND, remind));
        repository.save(recipient);
        List<Recipient> found = repository.findReadyForRemind();
        Assert.assertTrue(found.isEmpty());
    }

    @Test
    public void shouldNotFindReadyForRemindWhenNotificationIsNotActive() {
        NotificationSettings remind = new NotificationSettings();
        remind.setActive(false);
        remind.setFrequency(WEEKLY);
        remind.setLastNotified(DateUtils.addDays(new Date(), (-30)));
        Recipient recipient = new Recipient();
        recipient.setAccountName("test");
        recipient.setEmail("test@test.com");
        recipient.setScheduledNotifications(ImmutableMap.of(REMIND, remind));
        repository.save(recipient);
        List<Recipient> found = repository.findReadyForRemind();
        Assert.assertTrue(found.isEmpty());
    }

    @Test
    public void shouldNotFindReadyForBackupWhenFrequencyIsQuaterly() {
        NotificationSettings remind = new NotificationSettings();
        remind.setActive(true);
        remind.setFrequency(QUARTERLY);
        remind.setLastNotified(DateUtils.addDays(new Date(), (-91)));
        Recipient recipient = new Recipient();
        recipient.setAccountName("test");
        recipient.setEmail("test@test.com");
        recipient.setScheduledNotifications(ImmutableMap.of(BACKUP, remind));
        repository.save(recipient);
        List<Recipient> found = repository.findReadyForBackup();
        Assert.assertFalse(found.isEmpty());
    }
}

