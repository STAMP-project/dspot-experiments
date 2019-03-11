

import MailApiClient.MailSendException;
import MailSenderService.Result;
import biz.paluch.spinach.DisqueClient;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.testcontainers.containers.GenericContainer;


/**
 * Created by rnorth on 03/01/2016.
 */
public class DisqueBackedMailSenderTest {
    @Rule
    public GenericContainer container = new GenericContainer("richnorth/disque:1.0-rc1").withExposedPorts(7711);

    private DisqueClient disqueClient;

    private MailSenderService service;

    private MailApiClient mockMailApiClient;

    private MailMessage dummyMessage1;

    private MailMessage dummyMessage2;

    private MailMessage dummyMessage3;

    @Test
    public void testSimpleSuccessfulSending() throws Exception {
        service.enqueueMessage(dummyMessage1);
        service.enqueueMessage(dummyMessage2);
        service.enqueueMessage(dummyMessage3);
        Mockito.when(mockMailApiClient.send(ArgumentMatchers.any(MailMessage.class))).thenReturn(true);
        service.doScheduledSend();
        Mockito.verify(mockMailApiClient).send(ArgumentMatchers.eq(dummyMessage1));
        Mockito.verify(mockMailApiClient).send(ArgumentMatchers.eq(dummyMessage2));
        Mockito.verify(mockMailApiClient).send(ArgumentMatchers.eq(dummyMessage3));
    }

    @Test
    public void testRetryOnFailure() throws Exception {
        service.enqueueMessage(dummyMessage1);
        service.enqueueMessage(dummyMessage2);
        service.enqueueMessage(dummyMessage3);
        info("Message 1 will fail to send on the first attempt");
        Mockito.when(mockMailApiClient.send(ArgumentMatchers.eq(dummyMessage1))).thenThrow(MailSendException.class).thenReturn(true);
        Mockito.when(mockMailApiClient.send(ArgumentMatchers.eq(dummyMessage2))).thenReturn(true);
        Mockito.when(mockMailApiClient.send(ArgumentMatchers.eq(dummyMessage3))).thenReturn(true);
        MailSenderService.Result result;
        context("Simulating sending messages");
        context("First sending attempt", 4);
        result = service.doScheduledSend();
        assertEquals("2 messages were 'sent' successfully", 2, result.successfulCount);
        assertEquals("1 messages failed", 1, result.failedCount);
        context("Second attempt", 4);
        result = service.doScheduledSend();
        assertEquals("1 message was 'sent' successfully", 1, result.successfulCount);
        assertEquals("0 messages failed", 0, result.failedCount);
        context("Third attempt", 4);
        info("No messages should be due to send this time");
        result = service.doScheduledSend();
        assertEquals("0 messages were 'sent' successfully", 0, result.successfulCount);
        assertEquals("0 messages failed", 0, result.failedCount);
        Mockito.verify(mockMailApiClient, Mockito.times(2)).send(ArgumentMatchers.eq(dummyMessage1));
        Mockito.verify(mockMailApiClient).send(ArgumentMatchers.eq(dummyMessage2));
        Mockito.verify(mockMailApiClient).send(ArgumentMatchers.eq(dummyMessage3));
    }
}

