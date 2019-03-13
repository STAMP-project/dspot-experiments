/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.amqp.processors;


import PublishAMQP.EXCHANGE;
import PublishAMQP.HOST;
import PublishAMQP.REL_FAILURE;
import PublishAMQP.REL_SUCCESS;
import PublishAMQP.ROUTING_KEY;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.GetResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


public class PublishAMQPTest {
    @Test
    public void validateSuccessfullPublishAndTransferToSuccess() throws Exception {
        final PublishAMQP pubProc = new PublishAMQPTest.LocalPublishAMQP();
        final TestRunner runner = TestRunners.newTestRunner(pubProc);
        runner.setProperty(HOST, "injvm");
        runner.setProperty(EXCHANGE, "myExchange");
        runner.setProperty(ROUTING_KEY, "key1");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("foo", "bar");
        attributes.put("amqp$contentType", "foo/bar");
        attributes.put("amqp$contentEncoding", "foobar123");
        attributes.put("amqp$headers", "foo=bar,foo2=bar2,foo3");
        attributes.put("amqp$deliveryMode", "1");
        attributes.put("amqp$priority", "2");
        attributes.put("amqp$correlationId", "correlationId123");
        attributes.put("amqp$replyTo", "replyTo123");
        attributes.put("amqp$expiration", "expiration123");
        attributes.put("amqp$messageId", "messageId123");
        attributes.put("amqp$timestamp", "123456789");
        attributes.put("amqp$type", "type123");
        attributes.put("amqp$userId", "userId123");
        attributes.put("amqp$appId", "appId123");
        attributes.put("amqp$clusterId", "clusterId123");
        runner.enqueue("Hello Joe".getBytes(), attributes);
        runner.run();
        final MockFlowFile successFF = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertNotNull(successFF);
        final Channel channel = ((PublishAMQPTest.LocalPublishAMQP) (pubProc)).getConnection().createChannel();
        final GetResponse msg1 = channel.basicGet("queue1", true);
        Assert.assertNotNull(msg1);
        Assert.assertEquals("foo/bar", msg1.getProps().getContentType());
        Assert.assertEquals("foobar123", msg1.getProps().getContentEncoding());
        final Map<String, Object> headerMap = msg1.getProps().getHeaders();
        final Object foo = headerMap.get("foo");
        final Object foo2 = headerMap.get("foo2");
        final Object foo3 = headerMap.get("foo3");
        Assert.assertEquals("bar", foo.toString());
        Assert.assertEquals("bar2", foo2.toString());
        Assert.assertNull(foo3);
        Assert.assertEquals(((Integer) (1)), msg1.getProps().getDeliveryMode());
        Assert.assertEquals(((Integer) (2)), msg1.getProps().getPriority());
        Assert.assertEquals("correlationId123", msg1.getProps().getCorrelationId());
        Assert.assertEquals("replyTo123", msg1.getProps().getReplyTo());
        Assert.assertEquals("expiration123", msg1.getProps().getExpiration());
        Assert.assertEquals("messageId123", msg1.getProps().getMessageId());
        Assert.assertEquals(new Date(123456789L), msg1.getProps().getTimestamp());
        Assert.assertEquals("type123", msg1.getProps().getType());
        Assert.assertEquals("userId123", msg1.getProps().getUserId());
        Assert.assertEquals("appId123", msg1.getProps().getAppId());
        Assert.assertEquals("clusterId123", msg1.getProps().getClusterId());
        Assert.assertNotNull(channel.basicGet("queue2", true));
    }

    @Test
    public void validateFailedPublishAndTransferToFailure() throws Exception {
        PublishAMQP pubProc = new PublishAMQPTest.LocalPublishAMQP();
        TestRunner runner = TestRunners.newTestRunner(pubProc);
        runner.setProperty(HOST, "injvm");
        runner.setProperty(EXCHANGE, "badToTheBone");
        runner.setProperty(ROUTING_KEY, "key1");
        runner.enqueue("Hello Joe".getBytes());
        runner.run();
        Assert.assertTrue(runner.getFlowFilesForRelationship(REL_SUCCESS).isEmpty());
        Assert.assertNotNull(runner.getFlowFilesForRelationship(REL_FAILURE).get(0));
    }

    public static class LocalPublishAMQP extends PublishAMQP {
        private TestConnection connection;

        public LocalPublishAMQP() {
            final Map<String, List<String>> routingMap = Collections.singletonMap("key1", Arrays.asList("queue1", "queue2"));
            final Map<String, String> exchangeToRoutingKeymap = Collections.singletonMap("myExchange", "key1");
            connection = new TestConnection(exchangeToRoutingKeymap, routingMap);
        }

        @Override
        protected Connection createConnection(ProcessContext context) {
            return connection;
        }

        public Connection getConnection() {
            return connection;
        }
    }
}

