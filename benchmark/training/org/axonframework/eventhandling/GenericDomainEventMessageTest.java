/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.eventhandling;


import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import org.axonframework.messaging.MetaData;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Allard Buijze
 */
public class GenericDomainEventMessageTest {
    @Test
    public void testConstructor() {
        Object payload = new Object();
        long seqNo = 0;
        String id = UUID.randomUUID().toString();
        GenericDomainEventMessage<Object> message1 = new GenericDomainEventMessage("type", id, seqNo, payload);
        Map<String, Object> metaDataMap = Collections.singletonMap("key", "value");
        MetaData metaData = MetaData.from(metaDataMap);
        GenericDomainEventMessage<Object> message2 = new GenericDomainEventMessage("type", id, seqNo, payload, metaData);
        GenericDomainEventMessage<Object> message3 = new GenericDomainEventMessage("type", id, seqNo, payload, metaDataMap);
        Assert.assertSame(id, message1.getAggregateIdentifier());
        Assert.assertEquals(seqNo, message1.getSequenceNumber());
        Assert.assertSame(MetaData.emptyInstance(), message1.getMetaData());
        Assert.assertEquals(Object.class, message1.getPayload().getClass());
        Assert.assertEquals(Object.class, message1.getPayloadType());
        Assert.assertSame(id, message2.getAggregateIdentifier());
        Assert.assertEquals(seqNo, message2.getSequenceNumber());
        Assert.assertSame(metaData, message2.getMetaData());
        Assert.assertEquals(Object.class, message2.getPayload().getClass());
        Assert.assertEquals(Object.class, message2.getPayloadType());
        Assert.assertSame(id, message3.getAggregateIdentifier());
        Assert.assertEquals(seqNo, message3.getSequenceNumber());
        Assert.assertNotSame(metaDataMap, message3.getMetaData());
        Assert.assertEquals(metaDataMap, message3.getMetaData());
        Assert.assertEquals(Object.class, message3.getPayload().getClass());
        Assert.assertEquals(Object.class, message3.getPayloadType());
        Assert.assertFalse(message1.getIdentifier().equals(message2.getIdentifier()));
        Assert.assertFalse(message1.getIdentifier().equals(message3.getIdentifier()));
        Assert.assertFalse(message2.getIdentifier().equals(message3.getIdentifier()));
    }

    @Test
    public void testWithMetaData() {
        Object payload = new Object();
        long seqNo = 0;
        String id = UUID.randomUUID().toString();
        Map<String, Object> metaDataMap = Collections.singletonMap("key", "value");
        MetaData metaData = MetaData.from(metaDataMap);
        GenericDomainEventMessage<Object> message = new GenericDomainEventMessage("type", id, seqNo, payload, metaData);
        GenericDomainEventMessage<Object> message1 = message.withMetaData(MetaData.emptyInstance());
        GenericDomainEventMessage<Object> message2 = message.withMetaData(MetaData.from(Collections.singletonMap("key", ((Object) ("otherValue")))));
        Assert.assertEquals(0, message1.getMetaData().size());
        Assert.assertEquals(1, message2.getMetaData().size());
    }

    @Test
    public void testAndMetaData() {
        Object payload = new Object();
        long seqNo = 0;
        String id = UUID.randomUUID().toString();
        Map<String, Object> metaDataMap = Collections.singletonMap("key", "value");
        MetaData metaData = MetaData.from(metaDataMap);
        GenericDomainEventMessage<Object> message = new GenericDomainEventMessage("type", id, seqNo, payload, metaData);
        GenericDomainEventMessage<Object> message1 = message.andMetaData(MetaData.emptyInstance());
        GenericDomainEventMessage<Object> message2 = message.andMetaData(MetaData.from(Collections.singletonMap("key", ((Object) ("otherValue")))));
        Assert.assertEquals(1, message1.getMetaData().size());
        Assert.assertEquals("value", message1.getMetaData().get("key"));
        Assert.assertEquals(1, message2.getMetaData().size());
        Assert.assertEquals("otherValue", message2.getMetaData().get("key"));
    }

    @Test
    public void testToString() {
        String actual = new GenericDomainEventMessage("AggregateType", "id1", 1, "MyPayload").andMetaData(MetaData.with("key", "value").and("key2", 13)).toString();
        Assert.assertTrue(("Wrong output: " + actual), actual.startsWith("GenericDomainEventMessage{payload={MyPayload}, metadata={"));
        Assert.assertTrue(("Wrong output: " + actual), actual.contains("'key'->'value'"));
        Assert.assertTrue(("Wrong output: " + actual), actual.contains("'key2'->'13'"));
        Assert.assertTrue(("Wrong output: " + actual), actual.contains("', timestamp='"));
        Assert.assertTrue(("Wrong output: " + actual), actual.contains("', aggregateIdentifier='id1'"));
        Assert.assertTrue(("Wrong output: " + actual), actual.contains("', aggregateType='AggregateType'"));
        Assert.assertTrue(("Wrong output: " + actual), actual.contains("', sequenceNumber=1"));
        Assert.assertTrue(("Wrong output: " + actual), actual.endsWith("}"));
    }
}

