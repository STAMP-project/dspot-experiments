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


import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.nio.serialization.SerializationV1DataSerializable;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ObjectDataOutputStreamTest {
    private InternalSerializationService mockSerializationService;

    private ObjectDataOutputStream dataOutputStream;

    private OutputStream mockOutputStream;

    private InternalSerializationService serializationService;

    @Test
    public void testSampleEncodeDecode() throws IOException {
        SerializationV1DataSerializable testData = SerializationV1DataSerializable.createInstanceWithNonNullFields();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1000);
        ObjectDataOutputStream output = SerializationUtil.createObjectDataOutputStream(outputStream, serializationService);
        testData.writeData(output);
        output.flush();
        byte[] buf = outputStream.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(buf);
        ObjectDataInputStream input = SerializationUtil.createObjectDataInputStream(inputStream, serializationService);
        SerializationV1DataSerializable testDataFromSerializer = new SerializationV1DataSerializable();
        testDataFromSerializer.readData(input);
        Assert.assertTrue(testData.equals(testDataFromSerializer));
    }

    @Test
    public void testSampleEncodeDecode_with_null_arrays() throws IOException {
        SerializationV1DataSerializable testData = new SerializationV1DataSerializable();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1000);
        ObjectDataOutputStream output = SerializationUtil.createObjectDataOutputStream(outputStream, serializationService);
        testData.writeData(output);
        output.flush();
        byte[] buf = outputStream.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(buf);
        ObjectDataInputStream input = SerializationUtil.createObjectDataInputStream(inputStream, serializationService);
        SerializationV1DataSerializable testDataFromSerializer = new SerializationV1DataSerializable();
        testDataFromSerializer.readData(input);
        Assert.assertTrue(testData.equals(testDataFromSerializer));
    }

    @Test
    public void testWriteB() throws Exception {
        dataOutputStream.write(1);
        Mockito.verify(mockOutputStream).write(1);
    }

    @Test
    public void testWriteForBOffLen() throws Exception {
        byte[] someInput = new byte[1];
        dataOutputStream.write(someInput, 0, someInput.length);
        Mockito.verify(mockOutputStream).write(someInput, 0, someInput.length);
    }

    @Test
    public void testWriteObject() throws Exception {
        dataOutputStream.writeObject("INPUT");
        Mockito.verify(mockSerializationService).writeObject(dataOutputStream, "INPUT");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testToByteArray() throws Exception {
        dataOutputStream.toByteArray();
    }

    @Test
    public void testFlush() throws Exception {
        dataOutputStream.flush();
        Mockito.verify(mockOutputStream).flush();
    }

    @Test
    public void testClose() throws Exception {
        dataOutputStream.close();
        Mockito.verify(mockOutputStream).close();
    }

    @Test
    public void testGetByteOrder() throws Exception {
        ByteOrder byteOrderActual = dataOutputStream.getByteOrder();
        Assert.assertEquals(serializationService.getByteOrder(), byteOrderActual);
    }
}

