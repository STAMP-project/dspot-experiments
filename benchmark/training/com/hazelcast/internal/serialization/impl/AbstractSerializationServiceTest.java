/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.internal.serialization.impl;


import HeapData.TYPE_OFFSET;
import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.nio.BufferObjectDataInput;
import com.hazelcast.nio.BufferObjectDataOutput;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.CustomSerializationTest;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import com.hazelcast.nio.serialization.StreamSerializer;
import com.hazelcast.nio.serialization.TypedDataSerializable;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class AbstractSerializationServiceTest {
    private AbstractSerializationService abstractSerializationService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void toBytes_withPadding() {
        String payload = "somepayload";
        int padding = 10;
        byte[] unpadded = abstractSerializationService.toBytes(payload, 0, true);
        byte[] padded = abstractSerializationService.toBytes(payload, 10, true);
        // make sure the size is expected
        Assert.assertEquals(((unpadded.length) + padding), padded.length);
        // check if the actual content is the same
        for (int k = 0; k < (unpadded.length); k++) {
            Assert.assertEquals(unpadded[k], padded[(k + padding)]);
        }
    }

    @Test
    public void testExternalizable() {
        AbstractSerializationServiceTest.ExternalizableValue original = new AbstractSerializationServiceTest.ExternalizableValue(100);
        Data data = abstractSerializationService.toData(original);
        AbstractSerializationServiceTest.ExternalizableValue found = abstractSerializationService.toObject(data);
        Assert.assertNotNull(found);
        Assert.assertEquals(original.value, found.value);
    }

    static class ExternalizableValue implements Externalizable {
        int value;

        ExternalizableValue() {
        }

        ExternalizableValue(int value) {
            this.value = value;
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeInt(value);
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            value = in.readInt();
        }
    }

    @Test
    public void testSerializable() {
        AbstractSerializationServiceTest.SerializableleValue original = new AbstractSerializationServiceTest.SerializableleValue(100);
        Data data = abstractSerializationService.toData(original);
        AbstractSerializationServiceTest.SerializableleValue found = abstractSerializationService.toObject(data);
        Assert.assertNotNull(found);
        Assert.assertEquals(original.value, found.value);
    }

    static class SerializableleValue implements Serializable {
        int value;

        SerializableleValue(int value) {
            this.value = value;
        }
    }

    @Test(expected = HazelcastSerializationException.class)
    public void testToBytesHandleThrowable() throws Exception {
        abstractSerializationService.register(StringBuffer.class, new AbstractSerializationServiceTest.StringBufferSerializer(true));
        abstractSerializationService.toBytes(new StringBuffer());
    }

    @Test
    public void testToObject_ServiceInactive() throws Exception {
        expectedException.expect(HazelcastSerializationException.class);
        expectedException.expectCause(Is.is(IsInstanceOf.<Throwable>instanceOf(HazelcastInstanceNotActiveException.class)));
        abstractSerializationService.register(StringBuffer.class, new AbstractSerializationServiceTest.StringBufferSerializer(false));
        Data data = abstractSerializationService.toData(new StringBuffer());
        abstractSerializationService.dispose();
        abstractSerializationService.toObject(data);
    }

    @Test(expected = HazelcastSerializationException.class)
    public void testWriteObject_serializerFail() throws Exception {
        abstractSerializationService.register(StringBuffer.class, new AbstractSerializationServiceTest.StringBufferSerializer(true));
        BufferObjectDataOutput out = abstractSerializationService.createObjectDataOutput();
        abstractSerializationService.writeObject(out, new StringBuffer());
    }

    @Test
    public void testReadObject_ServiceInactive() throws Exception {
        expectedException.expect(HazelcastSerializationException.class);
        expectedException.expectCause(Is.is(IsInstanceOf.<Throwable>instanceOf(HazelcastInstanceNotActiveException.class)));
        abstractSerializationService.register(StringBuffer.class, new AbstractSerializationServiceTest.StringBufferSerializer(false));
        Data data = abstractSerializationService.toData(new StringBuffer());
        abstractSerializationService.dispose();
        BufferObjectDataInput in = abstractSerializationService.createObjectDataInput(data);
        in.position(TYPE_OFFSET);
        abstractSerializationService.readObject(in);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegister_nullType() throws Exception {
        abstractSerializationService.register(null, new AbstractSerializationServiceTest.StringBufferSerializer(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegister_typeIdNegative() throws Exception {
        AbstractSerializationServiceTest.StringBufferSerializer serializer = new AbstractSerializationServiceTest.StringBufferSerializer(true);
        serializer.typeId = -10000;
        abstractSerializationService.register(StringBuffer.class, serializer);
    }

    @Test(expected = IllegalStateException.class)
    public void testGlobalRegister_doubleRegistration() throws Exception {
        abstractSerializationService.registerGlobal(new AbstractSerializationServiceTest.StringBufferSerializer(true));
        abstractSerializationService.registerGlobal(new AbstractSerializationServiceTest.StringBufferSerializer(true));
    }

    @Test(expected = IllegalStateException.class)
    public void testGlobalRegister_alreadyRegisteredType() throws Exception {
        abstractSerializationService.register(StringBuffer.class, new AbstractSerializationServiceTest.StringBufferSerializer(true));
        abstractSerializationService.registerGlobal(new AbstractSerializationServiceTest.TheOtherGlobalSerializer(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSafeRegister_ConstantType() throws Exception {
        abstractSerializationService.safeRegister(Integer.class, new AbstractSerializationServiceTest.StringBufferSerializer(true));
    }

    @Test(expected = IllegalStateException.class)
    public void testSafeRegister_alreadyRegisteredType() throws Exception {
        abstractSerializationService.safeRegister(StringBuffer.class, new AbstractSerializationServiceTest.StringBufferSerializer(true));
        abstractSerializationService.safeRegister(StringBuffer.class, new AbstractSerializationServiceTest.TheOtherGlobalSerializer(true));
    }

    @Test(expected = IllegalStateException.class)
    public void testSafeRegister_alreadyRegisteredTypeId() throws Exception {
        abstractSerializationService.safeRegister(StringBuffer.class, new AbstractSerializationServiceTest.StringBufferSerializer(true));
        abstractSerializationService.safeRegister(StringBuilder.class, new AbstractSerializationServiceTest.TheOtherGlobalSerializer(true));
    }

    @Test(expected = HazelcastInstanceNotActiveException.class)
    public void testSerializerFor_ServiceInactive() throws Exception {
        abstractSerializationService.dispose();
        abstractSerializationService.serializerFor(new CustomSerializationTest.Foo());
    }

    @Test
    public void testDeserializationForSpecificType() {
        AbstractSerializationServiceTest.BaseClass baseObject = new AbstractSerializationServiceTest.BaseClass(5, "abc");
        AbstractSerializationServiceTest.ExtendedClass extendedObject = new AbstractSerializationServiceTest.ExtendedClass(baseObject, 378);
        Data extendedData = abstractSerializationService.toData(extendedObject);
        Object deserializedObject = abstractSerializationService.toObject(extendedData);
        Assert.assertEquals(extendedObject, deserializedObject);
        deserializedObject = abstractSerializationService.toObject(extendedObject, AbstractSerializationServiceTest.BaseClass.class);
        Assert.assertEquals(baseObject, deserializedObject);
    }

    @Test
    public void testTypedSerialization() {
        AbstractSerializationServiceTest.BaseClass baseObject = new AbstractSerializationServiceTest.BaseClass();
        Data data = abstractSerializationService.toData(baseObject);
        Object deserializedObject = abstractSerializationService.toObject(data);
        Assert.assertEquals(baseObject, deserializedObject);
        AbstractSerializationServiceTest.TypedBaseClass typedBaseObject = new AbstractSerializationServiceTest.TypedBaseClass(baseObject);
        Data typedData = abstractSerializationService.toData(typedBaseObject);
        deserializedObject = abstractSerializationService.toObject(typedData);
        Assert.assertEquals(AbstractSerializationServiceTest.BaseClass.class, deserializedObject.getClass());
        Assert.assertEquals(baseObject, deserializedObject);
        deserializedObject = abstractSerializationService.toObject(typedData, AbstractSerializationServiceTest.TypedBaseClass.class);
        Assert.assertEquals(typedBaseObject, deserializedObject);
    }

    public static class TypedBaseClass implements DataSerializable , TypedDataSerializable {
        private final AbstractSerializationServiceTest.BaseClass innerObj;

        public TypedBaseClass() {
            innerObj = new AbstractSerializationServiceTest.BaseClass();
        }

        public TypedBaseClass(AbstractSerializationServiceTest.BaseClass innerObj) {
            this.innerObj = innerObj;
        }

        @Override
        public Class getClassType() {
            return AbstractSerializationServiceTest.BaseClass.class;
        }

        @Override
        public void writeData(ObjectDataOutput out) throws IOException {
            innerObj.writeData(out);
            out.writeInt(innerObj.intField);
        }

        @Override
        public void readData(ObjectDataInput in) throws IOException {
            innerObj.readData(in);
            innerObj.intField = in.readInt();
        }

        @Override
        public boolean equals(Object obj) {
            if ((this) == obj) {
                return true;
            }
            if (null == obj) {
                return false;
            }
            if ((null == obj) || (!(getClass().isAssignableFrom(obj.getClass())))) {
                return false;
            }
            AbstractSerializationServiceTest.TypedBaseClass rhs = ((AbstractSerializationServiceTest.TypedBaseClass) (obj));
            if (((null == (innerObj)) && (null != (rhs.innerObj))) || ((null != (innerObj)) && (null == (rhs.innerObj)))) {
                return false;
            }
            if ((null == (innerObj)) && (null == (rhs.innerObj))) {
                return true;
            }
            return innerObj.equals(rhs.innerObj);
        }

        @Override
        public int hashCode() {
            return (innerObj) != null ? innerObj.hashCode() : 0;
        }
    }

    public static class BaseClass implements DataSerializable {
        private int intField;

        private String stringField;

        public BaseClass() {
        }

        public BaseClass(AbstractSerializationServiceTest.BaseClass rhs) {
            this.intField = rhs.intField;
            this.stringField = rhs.stringField;
        }

        public BaseClass(int intField, String stringField) {
            this.intField = intField;
            this.stringField = stringField;
        }

        @Override
        public void writeData(ObjectDataOutput out) throws IOException {
            out.writeInt(intField);
            out.writeUTF(stringField);
        }

        @Override
        public void readData(ObjectDataInput in) throws IOException {
            intField = in.readInt();
            stringField = in.readUTF();
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || (!(getClass().isAssignableFrom(o.getClass())))) {
                return false;
            }
            AbstractSerializationServiceTest.BaseClass baseClass = ((AbstractSerializationServiceTest.BaseClass) (o));
            if ((intField) != (baseClass.intField)) {
                return false;
            }
            return (stringField) != null ? stringField.equals(baseClass.stringField) : (baseClass.stringField) == null;
        }

        @Override
        public int hashCode() {
            int result = intField;
            result = (31 * result) + ((stringField) != null ? stringField.hashCode() : 0);
            return result;
        }
    }

    public static class ExtendedClass extends AbstractSerializationServiceTest.BaseClass {
        private long longField;

        public ExtendedClass() {
        }

        public ExtendedClass(AbstractSerializationServiceTest.BaseClass baseObject, long longField) {
            super(baseObject);
            this.longField = longField;
        }

        @Override
        public void writeData(ObjectDataOutput out) throws IOException {
            super.writeData(out);
            out.writeLong(longField);
        }

        @Override
        public void readData(ObjectDataInput in) throws IOException {
            super.readData(in);
            longField = in.readLong();
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || (!(getClass().isAssignableFrom(o.getClass())))) {
                return false;
            }
            if (!(super.equals(o))) {
                return false;
            }
            AbstractSerializationServiceTest.ExtendedClass that = ((AbstractSerializationServiceTest.ExtendedClass) (o));
            return (longField) == (that.longField);
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = (31 * result) + ((int) ((longField) ^ ((longField) >>> 32)));
            return result;
        }
    }

    private class StringBufferSerializer implements StreamSerializer<StringBuffer> {
        int typeId = 100000;

        private boolean fail;

        public StringBufferSerializer(boolean fail) {
            this.fail = fail;
        }

        @Override
        public int getTypeId() {
            return typeId;
        }

        @Override
        public void destroy() {
        }

        @Override
        public void write(ObjectDataOutput out, StringBuffer stringBuffer) throws IOException {
            if (fail) {
                throw new RuntimeException();
            } else {
                out.writeUTF(stringBuffer.toString());
            }
        }

        @Override
        public StringBuffer read(ObjectDataInput in) throws IOException {
            if (fail) {
                throw new RuntimeException();
            } else {
                return new StringBuffer(in.readUTF());
            }
        }
    }

    private class TheOtherGlobalSerializer extends AbstractSerializationServiceTest.StringBufferSerializer {
        public TheOtherGlobalSerializer(boolean fail) {
            super(fail);
        }
    }
}

