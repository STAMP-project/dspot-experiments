/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.topic.impl.reliable;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static org.mockito.Mockito.verify;


/**
 * Even though the ReliableMessageListenerAdapter is extremely simple, we do need to get it tested due to the guarantees
 * provided.
 */
@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ReliableMessageListenerAdapterTest extends HazelcastTestSupport {
    @Test
    public void testRegistration() {
        HazelcastInstance hz = createHazelcastInstance();
        ReliableTopicProxy<String> topic = ((ReliableTopicProxy<String>) (hz.<String>getReliableTopic("topic")));
        ReliableMessageListenerAdapterTest.MessageListenerMock listener = new ReliableMessageListenerAdapterTest.MessageListenerMock();
        String id = topic.addMessageListener(listener);
        MessageRunner runner = topic.runnersMap.get(id);
        Assert.assertNotNull(runner);
        ReliableMessageListenerAdapter adapter = HazelcastTestSupport.assertInstanceOf(ReliableMessageListenerAdapter.class, runner.listener);
        Assert.assertSame(listener, adapter.messageListener);
    }

    // checks if the Adapter really works!
    @Test
    public void integrationTest() {
        HazelcastInstance hz = createHazelcastInstance();
        ReliableTopicProxy<String> topic = ((ReliableTopicProxy<String>) (hz.<String>getReliableTopic("topic")));
        final ReliableMessageListenerAdapterTest.MessageListenerMock listener = new ReliableMessageListenerAdapterTest.MessageListenerMock();
        topic.addMessageListener(listener);
        topic.publish("item");
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(Collections.singletonList("item"), listener.messages);
            }
        });
    }

    class MessageListenerMock implements MessageListener<String> {
        private final List<String> messages = new CopyOnWriteArrayList<String>();

        @Override
        public void onMessage(Message<String> message) {
            messages.add(message.getMessageObject());
        }
    }

    @Test
    public void retrieveInitialSequence() {
        MessageListener<String> listener = createMessageListenerMock();
        ReliableMessageListenerAdapter<String> adapter = new ReliableMessageListenerAdapter<String>(listener);
        Assert.assertEquals((-1), adapter.retrieveInitialSequence());
    }

    @Test
    public void isTerminal() {
        MessageListener<String> listener = createMessageListenerMock();
        ReliableMessageListenerAdapter<String> adapter = new ReliableMessageListenerAdapter<String>(listener);
        Assert.assertFalse(adapter.isTerminal(new RuntimeException()));
        Assert.assertFalse(adapter.isTerminal(new Exception()));
    }

    @Test
    public void isLossTolerant() {
        MessageListener<String> listener = createMessageListenerMock();
        ReliableMessageListenerAdapter<String> adapter = new ReliableMessageListenerAdapter<String>(listener);
        Assert.assertFalse(adapter.isLossTolerant());
    }

    @Test
    public void onMessage() {
        MessageListener<String> listener = createMessageListenerMock();
        ReliableMessageListenerAdapter<String> adapter = new ReliableMessageListenerAdapter<String>(listener);
        Message<String> message = new Message<String>("foo", "foo", System.currentTimeMillis(), null);
        adapter.onMessage(message);
        verify(listener).onMessage(message);
    }

    @Test
    public void test_toString() {
        MessageListener<String> listener = createMessageListenerMock();
        Mockito.when(listener.toString()).thenReturn("foobar");
        ReliableMessageListenerAdapter<String> adapter = new ReliableMessageListenerAdapter<String>(listener);
        Assert.assertEquals("foobar", adapter.toString());
    }

    @Test
    public void storeSequence() {
        MessageListener<String> listener = createMessageListenerMock();
        ReliableMessageListenerAdapter<String> adapter = new ReliableMessageListenerAdapter<String>(listener);
        adapter.storeSequence(10);
    }
}

