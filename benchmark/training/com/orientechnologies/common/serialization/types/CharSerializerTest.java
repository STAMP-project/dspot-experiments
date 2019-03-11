/**
 * Copyright 2010-2016 OrientDB LTD (http://orientdb.com)
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
package com.orientechnologies.common.serialization.types;


import com.orientechnologies.orient.core.storage.impl.local.paginated.wal.OWALChanges;
import com.orientechnologies.orient.core.storage.impl.local.paginated.wal.OWALChangesTree;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ilya Bershadskiy (ibersh20-at-gmail.com)
 * @since 19.01.12
 */
public class CharSerializerTest {
    private static final int FIELD_SIZE = 2;

    private static final Character OBJECT = ((char) (new Random().nextInt()));

    byte[] stream = new byte[CharSerializerTest.FIELD_SIZE];

    private OCharSerializer charSerializer;

    @Test
    public void testFieldSize() {
        Assert.assertEquals(charSerializer.getObjectSize(null), CharSerializerTest.FIELD_SIZE);
    }

    @Test
    public void testSerialize() {
        charSerializer.serialize(CharSerializerTest.OBJECT, stream, 0);
        Assert.assertEquals(charSerializer.deserialize(stream, 0), CharSerializerTest.OBJECT);
    }

    @Test
    public void testSerializeNative() {
        charSerializer.serializeNative(CharSerializerTest.OBJECT, stream, 0);
        Assert.assertEquals(charSerializer.deserializeNativeObject(stream, 0), CharSerializerTest.OBJECT);
    }

    @Test
    public void testNativeDirectMemoryCompatibility() {
        charSerializer.serializeNative(CharSerializerTest.OBJECT, stream, 0);
        ByteBuffer buffer = ByteBuffer.allocateDirect(stream.length).order(ByteOrder.nativeOrder());
        buffer.position(0);
        buffer.put(stream);
        buffer.position(0);
        Assert.assertEquals(charSerializer.deserializeFromByteBufferObject(buffer), CharSerializerTest.OBJECT);
    }

    @Test
    public void testSerializeInWALChanges() {
        final int serializationOffset = 5;
        final ByteBuffer buffer = ByteBuffer.allocateDirect(((CharSerializerTest.FIELD_SIZE) + serializationOffset)).order(ByteOrder.nativeOrder());
        final byte[] data = new byte[CharSerializerTest.FIELD_SIZE];
        charSerializer.serializeNative(CharSerializerTest.OBJECT, data, 0);
        OWALChanges walChanges = new OWALChangesTree();
        walChanges.setBinaryValue(buffer, data, serializationOffset);
        Assert.assertEquals(charSerializer.getObjectSizeInByteBuffer(buffer, walChanges, serializationOffset), CharSerializerTest.FIELD_SIZE);
        Assert.assertEquals(charSerializer.deserializeFromByteBufferObject(buffer, walChanges, serializationOffset), CharSerializerTest.OBJECT);
    }
}

