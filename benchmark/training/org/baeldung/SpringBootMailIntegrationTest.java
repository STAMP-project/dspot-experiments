package org.baeldung;


import java.util.List;
import org.baeldung.boot.Application;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class SpringBootMailIntegrationTest {
    @Autowired
    private JavaMailSender javaMailSender;

    private Wiser wiser;

    private String userTo = "user2@localhost";

    private String userFrom = "user1@localhost";

    private String subject = "Test subject";

    private String textMail = "Text subject mail";

    @Test
    public void givenMail_whenSendAndReceived_thenCorrect() throws Exception {
        SimpleMailMessage message = composeEmailMessage();
        javaMailSender.send(message);
        List<WiserMessage> messages = wiser.getMessages();
        Assert.assertThat(messages, Matchers.hasSize(1));
        WiserMessage wiserMessage = messages.get(0);
        Assert.assertEquals(userFrom, wiserMessage.getEnvelopeSender());
        Assert.assertEquals(userTo, wiserMessage.getEnvelopeReceiver());
        Assert.assertEquals(subject, getSubject(wiserMessage));
        Assert.assertEquals(textMail, getMessage(wiserMessage));
    }
}

