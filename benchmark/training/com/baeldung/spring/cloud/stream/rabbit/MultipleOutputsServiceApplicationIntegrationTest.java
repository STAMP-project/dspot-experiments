package com.baeldung.spring.cloud.stream.rabbit;


import com.baeldung.spring.cloud.stream.rabbit.processor.MyProcessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MultipleOutputsServiceApplication.class)
@DirtiesContext
public class MultipleOutputsServiceApplicationIntegrationTest {
    @Autowired
    private MyProcessor pipe;

    @Autowired
    private MessageCollector messageCollector;

    @Test
    public void whenSendMessage_thenResponseIsInAOutput() {
        whenSendMessage(1);
        thenPayloadInChannelIs(pipe.anOutput(), 1);
    }

    @Test
    public void whenSendMessage_thenResponseIsInAnotherOutput() {
        whenSendMessage(11);
        thenPayloadInChannelIs(pipe.anotherOutput(), 11);
    }
}

