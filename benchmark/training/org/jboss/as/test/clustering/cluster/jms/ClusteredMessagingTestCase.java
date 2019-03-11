/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.test.clustering.cluster.jms;


import java.util.UUID;
import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.naming.InitialContext;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.test.clustering.cluster.AbstractClusteringTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2015 Red Hat inc.
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ClusteredMessagingTestCase extends AbstractClusteringTestCase {
    protected final String jmsQueueName = "ClusteredMessagingTestCase-Queue";

    protected final String jmsQueueLookup = "jms/" + (jmsQueueName);

    protected final String jmsTopicName = "ClusteredMessagingTestCase-Topic";

    protected final String jmsTopicLookup = "jms/" + (jmsTopicName);

    public ClusteredMessagingTestCase() {
        super(AbstractClusteringTestCase.TWO_NODES, new String[]{  });
    }

    @Test
    public void testClusteredQueue() throws Exception {
        InitialContext contextFromServer0 = ClusteredMessagingTestCase.createJNDIContextFromServer0();
        InitialContext contextFromServer1 = ClusteredMessagingTestCase.createJNDIContextFromServer1();
        String text = UUID.randomUUID().toString();
        // WIP test if the problem is that the view is not yet propagated
        Thread.sleep(AbstractClusteringTestCase.GRACE_TIME_TO_MEMBERSHIP_CHANGE);
        // send to the queue on server 0
        ClusteredMessagingTestCase.sendMessage(contextFromServer0, jmsQueueLookup, text);
        // receive it from the queue on server 1
        ClusteredMessagingTestCase.receiveMessage(contextFromServer1, jmsQueueLookup, text);
        String anotherText = UUID.randomUUID().toString();
        // send to the queue on server 1
        ClusteredMessagingTestCase.sendMessage(contextFromServer1, jmsQueueLookup, anotherText);
        // receive it from the queue on server 0
        ClusteredMessagingTestCase.receiveMessage(contextFromServer0, jmsQueueLookup, anotherText);
    }

    @Test
    public void testClusteredTopic() throws Exception {
        InitialContext contextFromServer0 = ClusteredMessagingTestCase.createJNDIContextFromServer0();
        InitialContext contextFromServer1 = ClusteredMessagingTestCase.createJNDIContextFromServer1();
        try (JMSContext jmsContext0 = ClusteredMessagingTestCase.createJMSContext(ClusteredMessagingTestCase.createJNDIContextFromServer0());JMSContext jmsContext1 = ClusteredMessagingTestCase.createJMSContext(ClusteredMessagingTestCase.createJNDIContextFromServer1())) {
            JMSConsumer consumer0 = jmsContext0.createConsumer(((Destination) (contextFromServer0.lookup(jmsTopicLookup))));
            JMSConsumer consumer1 = jmsContext1.createConsumer(((Destination) (contextFromServer1.lookup(jmsTopicLookup))));
            String text = UUID.randomUUID().toString();
            // WIP test if the problem is that the view is not yet propagated
            Thread.sleep(AbstractClusteringTestCase.GRACE_TIME_TO_MEMBERSHIP_CHANGE);
            // send a message to the topic on server 0
            ClusteredMessagingTestCase.sendMessage(contextFromServer0, jmsTopicLookup, text);
            // consumers receive it on both servers
            ClusteredMessagingTestCase.receiveMessage(consumer0, text);
            ClusteredMessagingTestCase.receiveMessage(consumer1, text);
            String anotherText = UUID.randomUUID().toString();
            // send another message to topic on server 1
            ClusteredMessagingTestCase.sendMessage(contextFromServer1, jmsTopicLookup, anotherText);
            // consumers receive it on both servers
            ClusteredMessagingTestCase.receiveMessage(consumer0, anotherText);
            ClusteredMessagingTestCase.receiveMessage(consumer1, anotherText);
        }
    }
}

