/**
 * Copyright 2016 Netflix, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 *
 */
package com.netflix.conductor.contribs.queue.sqs;


import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.ListQueuesRequest;
import com.amazonaws.services.sqs.model.ListQueuesResult;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.google.common.util.concurrent.Uninterruptibles;
import com.netflix.conductor.core.events.queue.Message;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import rx.Observable;


/**
 *
 *
 * @author Viren
 */
public class TestSQSObservableQueue {
    @Test
    public void test() {
        List<Message> messages = new LinkedList<>();
        Observable.range(0, 10).forEach((Integer x) -> messages.add(new Message(("" + x), ("payload: " + x), null)));
        Assert.assertEquals(10, messages.size());
        SQSObservableQueue queue = Mockito.mock(SQSObservableQueue.class);
        Mockito.when(queue.getOrCreateQueue()).thenReturn("junit_queue_url");
        Answer<?> answer = ((Answer<List<Message>>) (( invocation) -> Collections.emptyList()));
        Mockito.when(queue.receiveMessages()).thenReturn(messages).thenAnswer(answer);
        Mockito.when(queue.getOnSubscribe()).thenCallRealMethod();
        Mockito.when(queue.observe()).thenCallRealMethod();
        List<Message> found = new LinkedList<>();
        Observable<Message> observable = queue.observe();
        Assert.assertNotNull(observable);
        observable.subscribe(found::add);
        Uninterruptibles.sleepUninterruptibly(1000, TimeUnit.MILLISECONDS);
        Assert.assertEquals(messages.size(), found.size());
        Assert.assertEquals(messages, found);
    }

    @Test
    public void testException() {
        com.amazonaws.services.sqs.model.Message message = new com.amazonaws.services.sqs.model.Message().withMessageId("test").withBody("").withReceiptHandle("receiptHandle");
        Answer<?> answer = ((Answer<ReceiveMessageResult>) (( invocation) -> new ReceiveMessageResult()));
        AmazonSQSClient client = Mockito.mock(AmazonSQSClient.class);
        Mockito.when(client.listQueues(ArgumentMatchers.any(ListQueuesRequest.class))).thenReturn(new ListQueuesResult().withQueueUrls("junit_queue_url"));
        Mockito.when(client.receiveMessage(ArgumentMatchers.any(ReceiveMessageRequest.class))).thenThrow(new RuntimeException("Error in SQS communication")).thenReturn(new ReceiveMessageResult().withMessages(message)).thenAnswer(answer);
        SQSObservableQueue queue = new SQSObservableQueue.Builder().withQueueName("junit").withClient(client).build();
        List<Message> found = new LinkedList<>();
        Observable<Message> observable = queue.observe();
        Assert.assertNotNull(observable);
        observable.subscribe(found::add);
        Uninterruptibles.sleepUninterruptibly(1000, TimeUnit.MILLISECONDS);
        Assert.assertEquals(1, found.size());
    }
}

