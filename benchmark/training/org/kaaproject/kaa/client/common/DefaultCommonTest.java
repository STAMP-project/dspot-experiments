/**
 * Copyright 2014-2016 CyberVision, Inc.
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
package org.kaaproject.kaa.client.common;


import java.nio.ByteBuffer;
import java.util.UUID;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.apache.avro.Schema;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class DefaultCommonTest {
    @Test
    public void testCommonValue() {
        CommonValue value = new DefaultCommonValue(null);
        Assert.assertTrue(value.isNull());
        Assert.assertNull(value.getInteger());
        Assert.assertEquals("null", value.toString());
        Assert.assertNull(value.getNumber());
        value = new DefaultCommonValue(new Integer(5));
        Assert.assertTrue(value.isNumber());
        Assert.assertEquals(new Integer(5), value.getNumber());
        Assert.assertNull(value.getBytes());
        ByteBuffer expectedBytes = ByteBuffer.wrap(new byte[]{ 1, 2, 3 });
        value = new DefaultCommonValue(expectedBytes);
        Assert.assertTrue(value.isBytes());
        Assert.assertEquals(expectedBytes, value.getBytes());
        Assert.assertNull(value.getBoolean());
        value = new DefaultCommonValue(new Boolean(true));
        Assert.assertTrue(value.isBoolean());
        Assert.assertTrue(value.getBoolean());
        Assert.assertNull(value.getDouble());
        value = new DefaultCommonValue(new Double(5.0));
        Assert.assertTrue(value.isDouble());
        Assert.assertEquals(new Double(5.0), value.getDouble());
        Assert.assertNull(value.getLong());
        value = new DefaultCommonValue(new Long(123));
        Assert.assertTrue(value.isLong());
        Assert.assertEquals(new Long(123), value.getLong());
        Assert.assertNull(value.getFloat());
        value = new DefaultCommonValue(new Float(5.0));
        Assert.assertTrue(value.isFloat());
        Assert.assertEquals(new Float(5.0), value.getFloat());
        Assert.assertNull(value.getEnum());
        Schema schema = Mockito.mock(Schema.class);
        value = new DefaultCommonValue(new DefaultCommonEnum(schema, "enum"));
        Assert.assertTrue(value.isEnum());
        Assert.assertEquals("enum", value.getEnum().getSymbol());
        Assert.assertEquals("enum", value.toString());
        Assert.assertNull(value.getString());
        Assert.assertNull(value.getRecord());
        Assert.assertNull(value.getArray());
        Assert.assertNull(value.getFixed());
        EqualsVerifier.forClass(DefaultCommonValue.class).verify();
    }

    @Test
    public void testCommonRecord() {
        EqualsVerifier.forClass(DefaultCommonRecord.class).verify();
        Schema schema = Mockito.mock(Schema.class);
        CommonRecord record = new DefaultCommonRecord(schema);
        UUID uuid = new UUID(1234, 5678);
        record.setUuid(uuid);
        Assert.assertEquals(uuid, record.getUuid());
    }

    @Test
    public void testCommonArray() {
        EqualsVerifier.forClass(DefaultCommonArray.class).verify();
    }

    @Test
    public void testCommonEnum() {
        EqualsVerifier.forClass(DefaultCommonEnum.class).verify();
        Schema schema = Mockito.mock(Schema.class);
        CommonEnum e = new DefaultCommonEnum(schema, "enum");
        Assert.assertEquals(schema, e.getSchema());
    }

    @Test
    public void testCommonFixed() {
        EqualsVerifier.forClass(DefaultCommonFixed.class).verify();
    }
}

