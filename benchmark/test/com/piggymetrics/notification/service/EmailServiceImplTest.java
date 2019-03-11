package com.piggymetrics.notification.service;


import NotificationType.BACKUP;
import NotificationType.REMIND;
import com.piggymetrics.notification.domain.Recipient;
import java.io.IOException;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;


public class EmailServiceImplTest {
    @InjectMocks
    private EmailServiceImpl emailService;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private Environment env;

    @Captor
    private ArgumentCaptor<MimeMessage> captor;

    @Test
    public void shouldSendBackupEmail() throws IOException, MessagingException {
        final String subject = "subject";
        final String text = "text";
        final String attachment = "attachment.json";
        Recipient recipient = new Recipient();
        recipient.setAccountName("test");
        recipient.setEmail("test@test.com");
        Mockito.when(env.getProperty(BACKUP.getSubject())).thenReturn(subject);
        Mockito.when(env.getProperty(BACKUP.getText())).thenReturn(text);
        Mockito.when(env.getProperty(BACKUP.getAttachment())).thenReturn(attachment);
        emailService.send(BACKUP, recipient, "{\"name\":\"test\"");
        Mockito.verify(mailSender).send(captor.capture());
        MimeMessage message = captor.getValue();
        Assert.assertEquals(subject, message.getSubject());
        // TODO check other fields
    }

    @Test
    public void shouldSendRemindEmail() throws IOException, MessagingException {
        final String subject = "subject";
        final String text = "text";
        Recipient recipient = new Recipient();
        recipient.setAccountName("test");
        recipient.setEmail("test@test.com");
        Mockito.when(env.getProperty(REMIND.getSubject())).thenReturn(subject);
        Mockito.when(env.getProperty(REMIND.getText())).thenReturn(text);
        emailService.send(REMIND, recipient, null);
        Mockito.verify(mailSender).send(captor.capture());
        MimeMessage message = captor.getValue();
        Assert.assertEquals(subject, message.getSubject());
        // TODO check other fields
    }
}

