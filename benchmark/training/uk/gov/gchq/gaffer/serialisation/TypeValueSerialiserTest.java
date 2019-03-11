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
import uk.gov.gchq.gaffer.types.TypeValue;


public class TypeValueSerialiserTest extends ToBytesSerialisationTest<TypeValue> {
    @Test
    public void testCanSerialiseDeSerialiseCorrectly() throws SerialisationException {
        TypeValue typeValue = new TypeValue("testType", "testValue");
        byte[] bytes = serialiser.serialise(typeValue);
        String serialisedForm = new String(bytes);
        Assert.assertEquals("testType\u0000testValue", serialisedForm);
        TypeValue deSerialisedTypeValue = serialiser.deserialise(bytes);
        Assert.assertEquals(typeValue.getType(), deSerialisedTypeValue.getType());
        Assert.assertEquals(typeValue.getValue(), deSerialisedTypeValue.getValue());
        Assert.assertEquals(typeValue, deSerialisedTypeValue);
    }

    @Test
    public void testCanSerialiseDeSerialiseCorrectlyValueOnly() throws SerialisationException {
        TypeValue typeValue = new TypeValue();
        typeValue.setValue("testValue");
        byte[] bytes = serialiser.serialise(typeValue);
        String serialisedForm = new String(bytes);
        Assert.assertEquals("\u0000testValue", serialisedForm);
        TypeValue deSerialisedTypeValue = serialiser.deserialise(bytes);
        Assert.assertNull(deSerialisedTypeValue.getType());
        Assert.assertEquals(typeValue.getValue(), deSerialisedTypeValue.getValue());
        Assert.assertEquals(typeValue, deSerialisedTypeValue);
    }

    @Test
    public void testCanSerialiseDeSerialiseCorrectlyTypeOnly() throws SerialisationException {
        TypeValue typeValue = new TypeValue();
        typeValue.setType("testType");
        byte[] bytes = serialiser.serialise(typeValue);
        String serialisedForm = new String(bytes);
        Assert.assertEquals("testType\u0000", serialisedForm);
        TypeValue deSerialisedTypeValue = serialiser.deserialise(bytes);
        Assert.assertEquals(typeValue.getType(), deSerialisedTypeValue.getType());
        Assert.assertNull(typeValue.getValue(), deSerialisedTypeValue.getValue());
        Assert.assertEquals(typeValue, deSerialisedTypeValue);
    }

    @Test
    public void testCanSerialiseDeserialiseCorrectlyAndBeEscaped() throws SerialisationException {
        TypeValue typeValue = new TypeValue("testType", "testValue");
        byte[] bytes = ByteArrayEscapeUtils.escape(serialiser.serialise(typeValue));
        String serialisedForm = new String(bytes);
        Assert.assertEquals("testType\u0001\u0001testValue", serialisedForm);
        TypeValue deSerialisedTypeValue = serialiser.deserialise(ByteArrayEscapeUtils.unEscape(bytes));
        Assert.assertEquals(typeValue.getType(), deSerialisedTypeValue.getType());
        Assert.assertEquals(typeValue.getValue(), deSerialisedTypeValue.getValue());
        Assert.assertEquals(typeValue, deSerialisedTypeValue);
    }
}

