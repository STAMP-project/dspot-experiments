package com.baeldung.spring.cloud.aws.sns;


import com.baeldung.spring.cloud.aws.sqs.Greeting;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-test.properties")
public class SpringCloudSNSIntegrationTest {
    @Autowired
    private SNSMessageSender snsMessageSender;

    private static String topicName;

    private static String topicArn;

    @Test
    public void whenMessagePublished_thenSuccess() {
        String subject = "Test Message";
        String message = "Hello World";
        snsMessageSender.send(SpringCloudSNSIntegrationTest.topicName, message, subject);
    }

    @Test
    public void whenConvertedMessagePublished_thenSuccess() {
        String subject = "Test Message";
        Greeting message = new Greeting("Helo", "World");
        snsMessageSender.send(SpringCloudSNSIntegrationTest.topicName, message, subject);
    }
}

