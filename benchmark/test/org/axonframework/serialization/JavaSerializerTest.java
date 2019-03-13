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


import java.io.Serializable;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Allard Buijze
 */
public class JavaSerializerTest {
    private JavaSerializer testSubject;

    @Test
    public void testSerializeAndDeserialize() {
        SerializedObject<byte[]> serializedObject = testSubject.serialize(new JavaSerializerTest.MySerializableObject("hello"), byte[].class);
        Assert.assertEquals(JavaSerializerTest.MySerializableObject.class.getName(), serializedObject.getType().getName());
        Assert.assertEquals("2166108932776672373", serializedObject.getType().getRevision());
        Object actualResult = testSubject.deserialize(serializedObject);
        Assert.assertTrue((actualResult instanceof JavaSerializerTest.MySerializableObject));
        Assert.assertEquals("hello", ((JavaSerializerTest.MySerializableObject) (actualResult)).getSomeProperty());
    }

    @Test
    public void testClassForType() {
        Class actual = testSubject.classForType(new SimpleSerializedType(JavaSerializerTest.MySerializableObject.class.getName(), "2166108932776672373"));
        Assert.assertEquals(JavaSerializerTest.MySerializableObject.class, actual);
    }

    @Test
    public void testClassForType_CustomRevisionResolver() {
        testSubject = JavaSerializer.builder().revisionResolver(new FixedValueRevisionResolver("fixed")).build();
        Class actual = testSubject.classForType(new SimpleSerializedType(JavaSerializerTest.MySerializableObject.class.getName(), "fixed"));
        Assert.assertEquals(JavaSerializerTest.MySerializableObject.class, actual);
    }

    @Test
    public void testClassForType_UnknownClass() {
        Assert.assertEquals(UnknownSerializedType.class, testSubject.classForType(new SimpleSerializedType("unknown", "0")));
    }

    @Test
    public void testDeserializeNullValue() {
        SerializedObject<byte[]> serializedNull = testSubject.serialize(null, byte[].class);
        SimpleSerializedObject<byte[]> serializedNullString = new SimpleSerializedObject(serializedNull.getData(), byte[].class, testSubject.typeForClass(String.class));
        Assert.assertNull(testSubject.deserialize(serializedNull));
        Assert.assertNull(testSubject.deserialize(serializedNullString));
    }

    @Test
    public void testDeserializeEmptyBytes() {
        Assert.assertEquals(Void.class, testSubject.classForType(SerializedType.emptyType()));
        Assert.assertNull(testSubject.deserialize(new SimpleSerializedObject(new byte[0], byte[].class, SerializedType.emptyType())));
    }

    private static class MySerializableObject implements Serializable {
        private static final long serialVersionUID = 2166108932776672373L;

        private String someProperty;

        public MySerializableObject(String someProperty) {
            this.someProperty = someProperty;
        }

        public String getSomeProperty() {
            return someProperty;
        }
    }
}

