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
package org.springframework.jms.support.destination;


import javax.jms.Queue;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicSession;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.jms.StubQueue;
import org.springframework.jms.StubTopic;


/**
 *
 *
 * @author Rick Evans
 */
public class DynamicDestinationResolverTests {
    private static final String DESTINATION_NAME = "foo";

    @Test
    public void resolveWithPubSubTopicSession() throws Exception {
        Topic expectedDestination = new StubTopic();
        TopicSession session = Mockito.mock(TopicSession.class);
        BDDMockito.given(session.createTopic(DynamicDestinationResolverTests.DESTINATION_NAME)).willReturn(expectedDestination);
        DynamicDestinationResolverTests.testResolveDestination(session, expectedDestination, true);
    }

    @Test
    public void resolveWithPubSubVanillaSession() throws Exception {
        Topic expectedDestination = new StubTopic();
        Session session = Mockito.mock(Session.class);
        BDDMockito.given(session.createTopic(DynamicDestinationResolverTests.DESTINATION_NAME)).willReturn(expectedDestination);
        DynamicDestinationResolverTests.testResolveDestination(session, expectedDestination, true);
    }

    @Test
    public void resolveWithPointToPointQueueSession() throws Exception {
        Queue expectedDestination = new StubQueue();
        Session session = Mockito.mock(QueueSession.class);
        BDDMockito.given(session.createQueue(DynamicDestinationResolverTests.DESTINATION_NAME)).willReturn(expectedDestination);
        DynamicDestinationResolverTests.testResolveDestination(session, expectedDestination, false);
    }

    @Test
    public void resolveWithPointToPointVanillaSession() throws Exception {
        Queue expectedDestination = new StubQueue();
        Session session = Mockito.mock(Session.class);
        BDDMockito.given(session.createQueue(DynamicDestinationResolverTests.DESTINATION_NAME)).willReturn(expectedDestination);
        DynamicDestinationResolverTests.testResolveDestination(session, expectedDestination, false);
    }
}

