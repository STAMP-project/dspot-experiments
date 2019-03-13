/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.cdi.test;


import ContextName.Literal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.cdi.ContextName;
import org.apache.camel.cdi.Uri;
import org.apache.camel.component.mock.MockEndpoint;
import org.hamcrest.Matchers;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class MultiContextEventEndpointTest {
    @Inject
    @ContextName("first")
    @Uri("mock:consumeString")
    private MockEndpoint firstConsumeString;

    @Inject
    @ContextName("second")
    @Uri("mock:consumeString")
    private MockEndpoint secondConsumeString;

    @Inject
    @ContextName("first")
    @Uri("direct:produceString")
    private ProducerTemplate firstProduceString;

    @Inject
    @ContextName("second")
    @Uri("direct:produceString")
    private ProducerTemplate secondProduceString;

    @Inject
    private Event<Object> objectEvent;

    @Inject
    private MultiContextEventEndpointTest.EventObserver observer;

    @Test
    @InSequence(2)
    public void sendEventsToConsumers() throws InterruptedException {
        firstConsumeString.expectedMessageCount(1);
        firstConsumeString.expectedBodiesReceived("testFirst");
        secondConsumeString.expectedMessageCount(2);
        secondConsumeString.expectedBodiesReceived("testSecond1", "testSecond2");
        objectEvent.select(String.class, Literal.of("first")).fire("testFirst");
        objectEvent.select(String.class, Literal.of("second")).fire("testSecond1");
        objectEvent.select(String.class, Literal.of("second")).fire("testSecond2");
        assertIsSatisfied(2L, TimeUnit.SECONDS, firstConsumeString, secondConsumeString);
    }

    @Test
    @InSequence(3)
    public void sendMessagesToProducers() {
        firstProduceString.sendBody("testFirst");
        secondProduceString.sendBody("testSecond");
        Assert.assertThat(observer.getObjectEvents(), Matchers.<Object>contains("testFirst", "testSecond"));
        Assert.assertThat(observer.getStringEvents(), Matchers.contains("testFirst", "testSecond"));
        Assert.assertThat(observer.getFirstStringEvents(), Matchers.contains("testFirst"));
        Assert.assertThat(observer.secondStringEvents(), Matchers.contains("testSecond"));
    }

    @ApplicationScoped
    static class EventObserver {
        private final List<Object> objectEvents = new ArrayList<>();

        private final List<String> stringEvents = new ArrayList<>();

        private final List<String> firstStringEvents = new ArrayList<>();

        private final List<String> secondStringEvents = new ArrayList<>();

        void collectObjectEvents(@Observes
        Object event) {
            objectEvents.add(event);
        }

        void collectStringEvents(@Observes
        String event) {
            stringEvents.add(event);
        }

        void collectFirstStringEvents(@Observes
        @ContextName("first")
        String event) {
            firstStringEvents.add(event);
        }

        void collectSecondStringEvents(@Observes
        @ContextName("second")
        String event) {
            secondStringEvents.add(event);
        }

        List<Object> getObjectEvents() {
            return objectEvents;
        }

        List<String> getStringEvents() {
            return stringEvents;
        }

        List<String> getFirstStringEvents() {
            return firstStringEvents;
        }

        List<String> secondStringEvents() {
            return secondStringEvents;
        }

        void reset() {
            objectEvents.clear();
            stringEvents.clear();
            firstStringEvents.clear();
            secondStringEvents.clear();
        }
    }
}

