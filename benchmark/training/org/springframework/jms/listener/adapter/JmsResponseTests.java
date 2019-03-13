/**
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.jms.listener.adapter;


import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.jms.support.destination.DestinationResolver;


/**
 *
 *
 * @author Stephane Nicoll
 */
public class JmsResponseTests {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void destinationDoesNotUseDestinationResolver() throws JMSException {
        Destination destination = Mockito.mock(Destination.class);
        Destination actual = JmsResponse.forDestination("foo", destination).resolveDestination(null, null);
        Assert.assertSame(destination, actual);
    }

    @Test
    public void resolveDestinationForQueue() throws JMSException {
        Session session = Mockito.mock(Session.class);
        DestinationResolver destinationResolver = Mockito.mock(DestinationResolver.class);
        Destination destination = Mockito.mock(Destination.class);
        BDDMockito.given(destinationResolver.resolveDestinationName(session, "myQueue", false)).willReturn(destination);
        JmsResponse<String> jmsResponse = JmsResponse.forQueue("foo", "myQueue");
        Destination actual = jmsResponse.resolveDestination(destinationResolver, session);
        Assert.assertSame(destination, actual);
    }

    @Test
    public void createWithNulResponse() {
        thrown.expect(IllegalArgumentException.class);
        JmsResponse.forQueue(null, "myQueue");
    }

    @Test
    public void createWithNullQueueName() {
        thrown.expect(IllegalArgumentException.class);
        JmsResponse.forQueue("foo", null);
    }

    @Test
    public void createWithNullTopicName() {
        thrown.expect(IllegalArgumentException.class);
        JmsResponse.forTopic("foo", null);
    }

    @Test
    public void createWithNulDestination() {
        thrown.expect(IllegalArgumentException.class);
        JmsResponse.forDestination("foo", null);
    }
}

