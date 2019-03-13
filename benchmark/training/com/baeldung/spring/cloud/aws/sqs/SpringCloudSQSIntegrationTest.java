package com.baeldung.spring.cloud.aws.sqs;


import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.baeldung.spring.cloud.aws.SpringCloudAwsTestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-test.properties")
public class SpringCloudSQSIntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(SpringCloudSQSIntegrationTest.class);

    @Autowired
    @Lazy
    private SpringCloudSQS springCloudSQS;

    private static String receiveQueueName;

    private static String receiveQueueUrl;

    private static String sendQueueName;

    private static String sendQueueURl;

    @Test
    public void whenMessageSentAndVerified_thenSuccess() throws InterruptedException {
        String message = "Hello World";
        springCloudSQS.send(SpringCloudSQSIntegrationTest.sendQueueName, message);
        AmazonSQS amazonSQS = SpringCloudAwsTestUtil.amazonSQS();
        ReceiveMessageRequest request = new ReceiveMessageRequest(SpringCloudSQSIntegrationTest.sendQueueURl);
        request.setMaxNumberOfMessages(1);
        ReceiveMessageResult result = null;
        do {
            result = amazonSQS.receiveMessage(request);
            if ((result.getMessages().size()) == 0) {
                SpringCloudSQSIntegrationTest.logger.info("Message not received at first time, waiting for 1 second");
            }
        } while ((result.getMessages().size()) == 0 );
        assertThat(result.getMessages().get(0).getBody()).isEqualTo(message);
        // Delete message so that it doen't interfere with other test
        amazonSQS.deleteMessage(SpringCloudSQSIntegrationTest.sendQueueURl, result.getMessages().get(0).getReceiptHandle());
    }

    @Test
    public void whenConvertedMessageSentAndVerified_thenSuccess() throws IOException, InterruptedException {
        Greeting message = new Greeting("Hello", "World");
        springCloudSQS.send(SpringCloudSQSIntegrationTest.sendQueueName, message);
        AmazonSQS amazonSQS = SpringCloudAwsTestUtil.amazonSQS();
        ReceiveMessageRequest request = new ReceiveMessageRequest(SpringCloudSQSIntegrationTest.sendQueueURl);
        request.setMaxNumberOfMessages(1);
        ReceiveMessageResult result = null;
        do {
            result = amazonSQS.receiveMessage(request);
            if ((result.getMessages().size()) == 0) {
                SpringCloudSQSIntegrationTest.logger.info("Message not received at first time, waiting for 1 second");
            }
        } while ((result.getMessages().size()) == 0 );
        assertThat(new ObjectMapper().readValue(result.getMessages().get(0).getBody(), Greeting.class)).isEqualTo(message);
        // Delete message so that it doen't interfere with other test
        amazonSQS.deleteMessage(SpringCloudSQSIntegrationTest.sendQueueURl, result.getMessages().get(0).getReceiptHandle());
    }

    @Test
    public void givenMessageSent_whenMessageReceived_thenSuccess() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(5);
        springCloudSQS.setCountDownLatch(countDownLatch);
        AmazonSQS amazonSQS = SpringCloudAwsTestUtil.amazonSQS();
        for (int i = 0; i < 5; i++) {
            amazonSQS.sendMessage(SpringCloudSQSIntegrationTest.receiveQueueUrl, ("Hello World " + i));
            SpringCloudSQSIntegrationTest.logger.info("Sent message {}, waiting for 1 second", (i + 1));
            Thread.sleep(1000L);
        }
        countDownLatch.await();
    }
}

