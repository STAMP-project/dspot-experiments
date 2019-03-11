/**
 * Copyright 2013 MovingBlocks
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
package org.terasology.persistence.typeHandling.coreTypes;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.terasology.persistence.typeHandling.DeserializationContext;
import org.terasology.persistence.typeHandling.PersistedData;
import org.terasology.persistence.typeHandling.SerializationContext;


/**
 *
 */
public class EnumTypeHandlerSerializerTest {
    enum TestEnum {

        NON_NULL;}

    @Test
    public void testNullValue() throws Exception {
        PersistedData nullData = Mockito.mock(PersistedData.class);
        Mockito.when(nullData.isNull()).thenReturn(true);
        SerializationContext serializationContext = Mockito.mock(SerializationContext.class);
        Mockito.when(serializationContext.createNull()).thenReturn(nullData);
        EnumTypeHandler<EnumTypeHandlerSerializerTest.TestEnum> handler = new EnumTypeHandler(EnumTypeHandlerSerializerTest.TestEnum.class);
        PersistedData serializedNull = handler.serialize(null, serializationContext);
        Assert.assertEquals(nullData, serializedNull);
        DeserializationContext deserializationContext = Mockito.mock(DeserializationContext.class);
        EnumTypeHandlerSerializerTest.TestEnum deserializedValue = handler.deserialize(nullData, deserializationContext);
        Assert.assertEquals(null, deserializedValue);
    }

    @Test
    public void testNonNullValue() throws Exception {
        PersistedData data = Mockito.mock(PersistedData.class);
        Mockito.when(data.getAsString()).thenReturn(EnumTypeHandlerSerializerTest.TestEnum.NON_NULL.toString());
        Mockito.when(data.isString()).thenReturn(true);
        SerializationContext serializationContext = Mockito.mock(SerializationContext.class);
        Mockito.when(serializationContext.create(EnumTypeHandlerSerializerTest.TestEnum.NON_NULL.toString())).thenReturn(data);
        EnumTypeHandler<EnumTypeHandlerSerializerTest.TestEnum> handler = new EnumTypeHandler(EnumTypeHandlerSerializerTest.TestEnum.class);
        PersistedData serializedNonNull = handler.serialize(EnumTypeHandlerSerializerTest.TestEnum.NON_NULL, serializationContext);
        Assert.assertEquals(data, serializedNonNull);
        DeserializationContext deserializationContext = Mockito.mock(DeserializationContext.class);
        EnumTypeHandlerSerializerTest.TestEnum deserializedValue = handler.deserialize(data, deserializationContext);
        Assert.assertEquals(EnumTypeHandlerSerializerTest.TestEnum.NON_NULL, deserializedValue);
    }
}

