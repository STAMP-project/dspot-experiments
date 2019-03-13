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
package org.axonframework.serialization;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Allard Buijze
 */
public class LazyDeserializingObjectTest {
    private Serializer mockSerializer;

    private SerializedType mockType;

    private SerializedObject mockObject;

    private String mockDeserializedObject = "I'm a mock";

    @SuppressWarnings("unchecked")
    @Test
    public void testLazilyDeserialized() {
        LazyDeserializingObject<Object> testSubject = new LazyDeserializingObject(mockObject, mockSerializer);
        Mockito.verify(mockSerializer, Mockito.never()).deserialize(ArgumentMatchers.any(SerializedObject.class));
        Assert.assertEquals(String.class, testSubject.getType());
        Assert.assertFalse(testSubject.isDeserialized());
        Mockito.verify(mockSerializer, Mockito.never()).deserialize(ArgumentMatchers.any(SerializedObject.class));
        Assert.assertSame(mockDeserializedObject, testSubject.getObject());
        Assert.assertTrue(testSubject.isDeserialized());
    }

    @Test(expected = Exception.class)
    public void testLazilyDeserialized_NullObject() {
        new LazyDeserializingObject(null, mockSerializer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLazilyDeserialized_NullSerializer() {
        new LazyDeserializingObject(mockObject, null);
    }

    @Test
    public void testWithProvidedDeserializedInstance() {
        LazyDeserializingObject<Object> testSubject = new LazyDeserializingObject(mockDeserializedObject);
        Assert.assertEquals(mockDeserializedObject.getClass(), testSubject.getType());
        Assert.assertSame(mockDeserializedObject, testSubject.getObject());
        Assert.assertTrue(testSubject.isDeserialized());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithProvidedDeserializedNullInstance() {
        new LazyDeserializingObject(null);
    }
}

