package org.apereo.cas.support.sms;


import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import lombok.val;
import org.apereo.cas.configuration.model.support.sms.AmazonSnsProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * This is {@link AmazonSimpleNotificationServiceSmsSenderTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class AmazonSimpleNotificationServiceSmsSenderTests {
    @Test
    public void verifyAction() {
        val snsClient = Mockito.mock(AmazonSNS.class);
        val result = new PublishResult();
        result.setMessageId("PASSED");
        Mockito.when(snsClient.publish(ArgumentMatchers.any(PublishRequest.class))).thenReturn(result);
        val properties = new AmazonSnsProperties();
        properties.setMaxPrice("100");
        properties.setSenderId("SenderId");
        val sender = new AmazonSimpleNotificationServiceSmsSender(snsClient, properties);
        Assertions.assertTrue(sender.send("1234567890", "1234567890", "TestMessage"));
    }
}

