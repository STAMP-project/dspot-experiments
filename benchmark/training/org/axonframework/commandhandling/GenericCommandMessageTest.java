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
package org.axonframework.commandhandling;


import java.util.Collections;
import java.util.Map;
import org.axonframework.messaging.MetaData;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Allard Buijze
 */
public class GenericCommandMessageTest {
    @Test
    public void testConstructor() {
        Object payload = new Object();
        CommandMessage<Object> message1 = GenericCommandMessage.asCommandMessage(payload);
        Map<String, Object> metaDataMap = Collections.singletonMap("key", "value");
        MetaData metaData = MetaData.from(metaDataMap);
        GenericCommandMessage<Object> message2 = new GenericCommandMessage(payload, metaData);
        GenericCommandMessage<Object> message3 = new GenericCommandMessage(payload, metaDataMap);
        Assert.assertSame(MetaData.emptyInstance(), message1.getMetaData());
        Assert.assertEquals(Object.class, message1.getPayload().getClass());
        Assert.assertEquals(Object.class, message1.getPayloadType());
        Assert.assertSame(metaData, message2.getMetaData());
        Assert.assertEquals(Object.class, message2.getPayload().getClass());
        Assert.assertEquals(Object.class, message2.getPayloadType());
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
        Map<String, Object> metaDataMap = Collections.singletonMap("key", "value");
        MetaData metaData = MetaData.from(metaDataMap);
        GenericCommandMessage<Object> message = new GenericCommandMessage(payload, metaData);
        GenericCommandMessage<Object> message1 = message.withMetaData(MetaData.emptyInstance());
        GenericCommandMessage<Object> message2 = message.withMetaData(MetaData.from(Collections.singletonMap("key", ((Object) ("otherValue")))));
        Assert.assertEquals(0, message1.getMetaData().size());
        Assert.assertEquals(1, message2.getMetaData().size());
    }

    @Test
    public void testAndMetaData() {
        Object payload = new Object();
        Map<String, Object> metaDataMap = Collections.singletonMap("key", "value");
        MetaData metaData = MetaData.from(metaDataMap);
        GenericCommandMessage<Object> message = new GenericCommandMessage(payload, metaData);
        GenericCommandMessage<Object> message1 = message.andMetaData(MetaData.emptyInstance());
        GenericCommandMessage<Object> message2 = message.andMetaData(MetaData.from(Collections.singletonMap("key", ((Object) ("otherValue")))));
        Assert.assertEquals(1, message1.getMetaData().size());
        Assert.assertEquals("value", message1.getMetaData().get("key"));
        Assert.assertEquals(1, message2.getMetaData().size());
        Assert.assertEquals("otherValue", message2.getMetaData().get("key"));
    }

    @Test
    public void testToString() {
        String actual = GenericCommandMessage.asCommandMessage("MyPayload").andMetaData(MetaData.with("key", "value").and("key2", 13)).toString();
        Assert.assertTrue(("Wrong output: " + actual), actual.startsWith("GenericCommandMessage{payload={MyPayload}, metadata={"));
        Assert.assertTrue(("Wrong output: " + actual), actual.contains("'key'->'value'"));
        Assert.assertTrue(("Wrong output: " + actual), actual.contains("'key2'->'13'"));
        Assert.assertTrue(("Wrong output: " + actual), actual.endsWith("', commandName='java.lang.String'}"));
        Assert.assertEquals(("Wrong output: " + actual), 173, actual.length());
    }
}

