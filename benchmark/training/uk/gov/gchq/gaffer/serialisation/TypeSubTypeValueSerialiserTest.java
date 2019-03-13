/**
 * Copyright 2016-2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.serialisation;


import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.ByteArrayEscapeUtils;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.types.TypeSubTypeValue;


public class TypeSubTypeValueSerialiserTest extends ToBytesSerialisationTest<TypeSubTypeValue> {
    private static final TypeSubTypeValueSerialiser serialiser = new TypeSubTypeValueSerialiser();

    @Test
    public void testCanSerialiseDeSerialiseCorrectly() throws SerialisationException {
        TypeSubTypeValue typeSubTypeValue = new TypeSubTypeValue("testType", "testSubType", "testValue");
        byte[] bytes = TypeSubTypeValueSerialiserTest.serialiser.serialise(typeSubTypeValue);
        String serialisedForm = new String(bytes);
        Assert.assertEquals("testType\u0000testSubType\u0000testValue", serialisedForm);
        TypeSubTypeValue deSerialisedTypeSubTypeValue = TypeSubTypeValueSerialiserTest.serialiser.deserialise(bytes);
        Assert.assertEquals(typeSubTypeValue.getType(), deSerialisedTypeSubTypeValue.getType());
        Assert.assertEquals(typeSubTypeValue.getSubType(), deSerialisedTypeSubTypeValue.getSubType());
        Assert.assertEquals(typeSubTypeValue.getValue(), deSerialisedTypeSubTypeValue.getValue());
        Assert.assertEquals(typeSubTypeValue, deSerialisedTypeSubTypeValue);
    }

    @Test
    public void testCanSerialiseDeSerialiseCorrectlyValueOnly() throws SerialisationException {
        TypeSubTypeValue typeSubTypeValue = new TypeSubTypeValue();
        typeSubTypeValue.setValue("testValue");
        byte[] bytes = TypeSubTypeValueSerialiserTest.serialiser.serialise(typeSubTypeValue);
        String serialisedForm = new String(bytes);
        Assert.assertEquals("\u0000\u0000testValue", serialisedForm);
        TypeSubTypeValue deSerialisedTypeSubTypeValue = TypeSubTypeValueSerialiserTest.serialiser.deserialise(bytes);
        Assert.assertNull(deSerialisedTypeSubTypeValue.getType());
        Assert.assertNull(deSerialisedTypeSubTypeValue.getSubType());
        Assert.assertEquals(typeSubTypeValue.getValue(), deSerialisedTypeSubTypeValue.getValue());
        Assert.assertEquals(typeSubTypeValue, deSerialisedTypeSubTypeValue);
    }

    @Test
    public void testCanSerialiseDeSerialiseCorrectlyTypeValueOnly() throws SerialisationException {
        TypeSubTypeValue typeSubTypeValue = new TypeSubTypeValue();
        typeSubTypeValue.setValue("testValue");
        typeSubTypeValue.setType("testType");
        byte[] bytes = TypeSubTypeValueSerialiserTest.serialiser.serialise(typeSubTypeValue);
        String serialisedForm = new String(bytes);
        Assert.assertEquals("testType\u0000\u0000testValue", serialisedForm);
        TypeSubTypeValue deSerialisedTypeSubTypeValue = TypeSubTypeValueSerialiserTest.serialiser.deserialise(bytes);
        Assert.assertEquals(typeSubTypeValue.getType(), deSerialisedTypeSubTypeValue.getType());
        Assert.assertNull(deSerialisedTypeSubTypeValue.getSubType());
        Assert.assertEquals(typeSubTypeValue.getValue(), deSerialisedTypeSubTypeValue.getValue());
        Assert.assertEquals(typeSubTypeValue, deSerialisedTypeSubTypeValue);
    }

    @Test
    public void testCanSerialiseDeSerialiseCorrectlySubTypeValueOnly() throws SerialisationException {
        TypeSubTypeValue typeSubTypeValue = new TypeSubTypeValue();
        typeSubTypeValue.setValue("testValue");
        typeSubTypeValue.setSubType("testSubType");
        byte[] bytes = TypeSubTypeValueSerialiserTest.serialiser.serialise(typeSubTypeValue);
        String serialisedForm = new String(bytes);
        Assert.assertEquals("\u0000testSubType\u0000testValue", serialisedForm);
        TypeSubTypeValue deSerialisedTypeSubTypeValue = TypeSubTypeValueSerialiserTest.serialiser.deserialise(bytes);
        Assert.assertNull(deSerialisedTypeSubTypeValue.getType());
        Assert.assertEquals(typeSubTypeValue.getSubType(), deSerialisedTypeSubTypeValue.getSubType());
        Assert.assertEquals(typeSubTypeValue.getValue(), deSerialisedTypeSubTypeValue.getValue());
        Assert.assertEquals(typeSubTypeValue, deSerialisedTypeSubTypeValue);
    }

    @Test
    public void testCanSerialiseDeSerialiseCorrectlyTypeOnly() throws SerialisationException {
        TypeSubTypeValue typeSubTypeValue = new TypeSubTypeValue();
        typeSubTypeValue.setType("testType");
        byte[] bytes = TypeSubTypeValueSerialiserTest.serialiser.serialise(typeSubTypeValue);
        String serialisedForm = new String(bytes);
        Assert.assertEquals("testType\u0000\u0000", serialisedForm);
        TypeSubTypeValue deSerialisedTypeSubTypeValue = TypeSubTypeValueSerialiserTest.serialiser.deserialise(bytes);
        Assert.assertEquals(typeSubTypeValue.getType(), deSerialisedTypeSubTypeValue.getType());
        Assert.assertNull(deSerialisedTypeSubTypeValue.getSubType());
        Assert.assertNull(typeSubTypeValue.getValue(), deSerialisedTypeSubTypeValue.getValue());
        Assert.assertEquals(typeSubTypeValue, deSerialisedTypeSubTypeValue);
    }

    @Test
    public void testCanSerialiseDeSerialiseCorrectlySubTypeOnly() throws SerialisationException {
        TypeSubTypeValue typeSubTypeValue = new TypeSubTypeValue();
        typeSubTypeValue.setSubType("testSubType");
        byte[] bytes = TypeSubTypeValueSerialiserTest.serialiser.serialise(typeSubTypeValue);
        String serialisedForm = new String(bytes);
        Assert.assertEquals("\u0000testSubType\u0000", serialisedForm);
        TypeSubTypeValue deSerialisedTypeSubTypeValue = TypeSubTypeValueSerialiserTest.serialiser.deserialise(bytes);
        Assert.assertNull(deSerialisedTypeSubTypeValue.getType());
        Assert.assertEquals(typeSubTypeValue.getSubType(), deSerialisedTypeSubTypeValue.getSubType());
        Assert.assertNull(typeSubTypeValue.getValue(), deSerialisedTypeSubTypeValue.getValue());
        Assert.assertEquals(typeSubTypeValue, deSerialisedTypeSubTypeValue);
    }

    @Test
    public void testCanSerialiseDeSerialiseCorrectlyTypeSubTypeOnly() throws SerialisationException {
        TypeSubTypeValue typeSubTypeValue = new TypeSubTypeValue();
        typeSubTypeValue.setType("testType");
        typeSubTypeValue.setSubType("testSubType");
        byte[] bytes = TypeSubTypeValueSerialiserTest.serialiser.serialise(typeSubTypeValue);
        String serialisedForm = new String(bytes);
        Assert.assertEquals("testType\u0000testSubType\u0000", serialisedForm);
        TypeSubTypeValue deSerialisedTypeSubTypeValue = TypeSubTypeValueSerialiserTest.serialiser.deserialise(bytes);
        Assert.assertEquals(typeSubTypeValue.getType(), deSerialisedTypeSubTypeValue.getType());
        Assert.assertEquals(typeSubTypeValue.getSubType(), deSerialisedTypeSubTypeValue.getSubType());
        Assert.assertNull(typeSubTypeValue.getValue(), deSerialisedTypeSubTypeValue.getValue());
        Assert.assertEquals(typeSubTypeValue, deSerialisedTypeSubTypeValue);
    }

    @Test
    public void testCanSerialiseDeserialiseCorrectlyAndBeEscaped() throws SerialisationException {
        TypeSubTypeValue typeSubTypeValue = new TypeSubTypeValue("testType", "testSubType", "testValue");
        byte[] bytes = ByteArrayEscapeUtils.escape(TypeSubTypeValueSerialiserTest.serialiser.serialise(typeSubTypeValue));
        String serialisedForm = new String(bytes);
        Assert.assertEquals("testType\u0001\u0001testSubType\u0001\u0001testValue", serialisedForm);
        TypeSubTypeValue deSerialisedTypeSubTypeValue = TypeSubTypeValueSerialiserTest.serialiser.deserialise(ByteArrayEscapeUtils.unEscape(bytes));
        Assert.assertEquals(typeSubTypeValue.getType(), deSerialisedTypeSubTypeValue.getType());
        Assert.assertEquals(typeSubTypeValue.getSubType(), deSerialisedTypeSubTypeValue.getSubType());
        Assert.assertEquals(typeSubTypeValue.getValue(), deSerialisedTypeSubTypeValue.getValue());
        Assert.assertEquals(typeSubTypeValue, deSerialisedTypeSubTypeValue);
    }
}

