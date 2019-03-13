package org.axonframework.spring.messaging;


import java.util.ArrayList;
import java.util.List;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.SimpleEventBus;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.event.EventListener;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
public class ApplicationContextEventPublisherTest {
    @Autowired
    private ApplicationContextEventPublisherTest.ListenerBean listenerBean;

    @Autowired
    private EventBus eventBus;

    @Test
    public void testEventsForwardedToListenerBean() {
        eventBus.publish(asEventMessage("test"));
        Assert.assertEquals("test", listenerBean.getEvents().get(0));
    }

    @Configuration
    public static class Context {
        @Bean
        public ApplicationContextEventPublisherTest.ListenerBean listenerBean() {
            return new ApplicationContextEventPublisherTest.ListenerBean();
        }

        @Bean
        public EventBus eventBus() {
            return SimpleEventBus.builder().build();
        }

        @Bean
        public ApplicationContextEventPublisher publisher(EventBus eventBus) {
            return new ApplicationContextEventPublisher(eventBus);
        }
    }

    public static class ListenerBean {
        private List<Object> events = new ArrayList<>();

        @EventListener
        public void handle(PayloadApplicationEvent<String> event) {
            events.add(event.getPayload());
        }

        public List<Object> getEvents() {
            return events;
        }
    }
}

