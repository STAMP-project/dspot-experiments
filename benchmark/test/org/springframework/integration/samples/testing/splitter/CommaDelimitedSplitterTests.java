/**
 * Copyright 2002-2011 the original author or authors.
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
package org.springframework.integration.samples.testing.splitter;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Shows how to test a custom splitter. Unit test for the class and
 * tests for the integration subflow.
 * The splitter has direct input and output channels. The splitter would
 * be a fragment of a larger flow. Since the output channel is direct,
 * it has no subscribers outside the context of a larger flow. So,
 * in this test case, we bridge it to a {@link QueueChannel} to
 * facilitate easy testing.
 *
 * @author Gary Russell
 * @since 2.0.2
 */
// default context name is <ClassName>-context.xml
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class CommaDelimitedSplitterTests {
    @Autowired
    MessageChannel inputChannel;

    @Autowired
    QueueChannel testChannel;

    @Test
    public void unitTestClass3() {
        List<String> splits = new CommaDelimitedSplitter().split("   a    , b,    c ");
        Assert.assertEquals("Expected 3 splits", 3, splits.size());
        Assert.assertEquals("a", splits.get(0));
        Assert.assertEquals("b", splits.get(1));
        Assert.assertEquals("c", splits.get(2));
    }

    @Test
    public void unitTestClass2() {
        List<String> splits = new CommaDelimitedSplitter().split("   a    ,,    c ");
        Assert.assertEquals("Expected 2 splits", 2, splits.size());
        Assert.assertEquals("a", splits.get(0));
        Assert.assertEquals("c", splits.get(1));
    }

    @Test
    public void unitTestClass0() {
        List<String> splits = new CommaDelimitedSplitter().split(",,, ,,    ,,  ,,");
        Assert.assertEquals("Expected 0 splits", 0, splits.size());
    }

    @Test
    public void testOne() {
        inputChannel.send(MessageBuilder.withPayload("   a   ").build());
        Message<?> outMessage = testChannel.receive(0);
        Assert.assertNotNull(outMessage);
        Assert.assertThat(outMessage, hasPayload("a"));
        outMessage = testChannel.receive(0);
        Assert.assertNull("Only one message expected", outMessage);
    }

    @Test
    public void testTwo() {
        inputChannel.send(MessageBuilder.withPayload("   a ,z  ").build());
        Message<?> outMessage = testChannel.receive(0);
        Assert.assertNotNull(outMessage);
        Assert.assertThat(outMessage, hasPayload("a"));
        outMessage = testChannel.receive(0);
        Assert.assertNotNull(outMessage);
        Assert.assertThat(outMessage, hasPayload("z"));
        outMessage = testChannel.receive(0);
        Assert.assertNull("Only two messages expected", outMessage);
    }

    @Test
    public void testSkipEmpty() {
        inputChannel.send(MessageBuilder.withPayload("   a ,,z  ").build());
        Message<?> outMessage = testChannel.receive(0);
        Assert.assertNotNull(outMessage);
        Assert.assertThat(outMessage, hasPayload("a"));
        outMessage = testChannel.receive(0);
        Assert.assertNotNull(outMessage);
        Assert.assertThat(outMessage, hasPayload("z"));
        outMessage = testChannel.receive(0);
        Assert.assertNull("Only two messages expected", outMessage);
    }

    @Test
    public void testNone() {
        inputChannel.send(MessageBuilder.withPayload("  ,, ,,, ,,,,, ,,,,,,,  ").build());
        Message<?> outMessage = testChannel.receive(0);
        Assert.assertNull("No messages expected", outMessage);
    }

    @Test
    public void testEmpty() {
        inputChannel.send(MessageBuilder.withPayload("").build());
        Message<?> outMessage = testChannel.receive(0);
        Assert.assertNull("No messages expected", outMessage);
    }
}

