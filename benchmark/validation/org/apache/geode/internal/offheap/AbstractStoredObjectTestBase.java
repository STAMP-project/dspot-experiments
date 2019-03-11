/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.offheap;


import DataSerializableFixedID.VM_CACHED_DESERIALIZABLE;
import java.io.DataOutput;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public abstract class AbstractStoredObjectTestBase {
    @Test
    public void getValueAsDeserializedHeapObjectShouldReturnDeserializedValueIfValueIsSerialized() {
        Object regionEntryValue = getValue();
        StoredObject storedObject = createValueAsSerializedStoredObject(regionEntryValue);
        Object actualRegionEntryValue = storedObject.getValueAsDeserializedHeapObject();
        Assert.assertEquals(regionEntryValue, actualRegionEntryValue);
    }

    @Test
    public void getValueAsDeserializedHeapObjectShouldReturnValueAsIsIfNotSerialized() {
        byte[] regionEntryValue = getValueAsByteArray();
        StoredObject storedObject = createValueAsUnserializedStoredObject(regionEntryValue);
        byte[] deserializedValue = ((byte[]) (storedObject.getValueAsDeserializedHeapObject()));
        Assert.assertArrayEquals(regionEntryValue, deserializedValue);
    }

    @Test
    public void getValueAsHeapByteArrayShouldReturnSerializedByteArrayIfValueIsSerialized() {
        Object regionEntryValue = getValue();
        StoredObject storedObject = createValueAsSerializedStoredObject(regionEntryValue);
        byte[] valueInSerializedByteArray = ((byte[]) (storedObject.getValueAsHeapByteArray()));
        Object actualRegionEntryValue = convertSerializedByteArrayToObject(valueInSerializedByteArray);
        Assert.assertEquals(regionEntryValue, actualRegionEntryValue);
    }

    @Test
    public void getValueAsHeapByteArrayShouldReturnDeserializedByteArrayIfValueIsNotSerialized() {
        Object regionEntryValue = getValue();
        StoredObject storedObject = createValueAsUnserializedStoredObject(regionEntryValue);
        byte[] valueInByteArray = ((byte[]) (storedObject.getValueAsHeapByteArray()));
        Object actualRegionEntryValue = convertByteArrayToObject(valueInByteArray);
        Assert.assertEquals(regionEntryValue, actualRegionEntryValue);
    }

    @Test
    public void getStringFormShouldReturnStringFromDeserializedValue() {
        Object regionEntryValue = getValue();
        StoredObject storedObject = createValueAsSerializedStoredObject(regionEntryValue);
        String stringForm = storedObject.getStringForm();
        Assert.assertEquals(String.valueOf(regionEntryValue), stringForm);
    }

    @Test
    public void getValueShouldReturnSerializedValue() {
        Object regionEntryValue = getValue();
        StoredObject storedObject = createValueAsSerializedStoredObject(regionEntryValue);
        byte[] valueAsSerializedByteArray = ((byte[]) (storedObject.getValue()));
        Object actualValue = convertSerializedByteArrayToObject(valueAsSerializedByteArray);
        Assert.assertEquals(regionEntryValue, actualValue);
    }

    @Test(expected = IllegalStateException.class)
    public void getValueShouldThrowExceptionIfValueIsNotSerialized() {
        Object regionEntryValue = getValue();
        StoredObject storedObject = createValueAsUnserializedStoredObject(regionEntryValue);
        byte[] deserializedValue = ((byte[]) (storedObject.getValue()));
    }

    @Test
    public void getDeserializedWritableCopyShouldReturnDeserializedValue() {
        byte[] regionEntryValue = getValueAsByteArray();
        StoredObject storedObject = createValueAsSerializedStoredObject(regionEntryValue);
        Assert.assertArrayEquals(regionEntryValue, ((byte[]) (storedObject.getDeserializedWritableCopy(null, null))));
    }

    @Test
    public void writeValueAsByteArrayWritesToProvidedDataOutput() throws IOException {
        byte[] regionEntryValue = getValueAsByteArray();
        StoredObject storedObject = createValueAsSerializedStoredObject(regionEntryValue);
        DataOutput dataOutput = Mockito.mock(DataOutput.class);
        storedObject.writeValueAsByteArray(dataOutput);
        Mockito.verify(dataOutput, Mockito.times(1)).write(storedObject.getSerializedValue(), 0, storedObject.getSerializedValue().length);
    }

    @Test
    public void sendToShouldWriteSerializedValueToDataOutput() throws IOException {
        Object regionEntryValue = getValue();
        StoredObject storedObject = createValueAsSerializedStoredObject(regionEntryValue);
        DataOutput dataOutput = Mockito.mock(DataOutput.class);
        storedObject.sendTo(dataOutput);
        Mockito.verify(dataOutput, Mockito.times(1)).write(storedObject.getSerializedValue());
    }

    @Test
    public void sendToShouldWriteDeserializedObjectToDataOutput() throws IOException {
        byte[] regionEntryValue = getValueAsByteArray();
        StoredObject storedObject = createValueAsUnserializedStoredObject(regionEntryValue);
        DataOutput dataOutput = Mockito.mock(DataOutput.class);
        storedObject.sendTo(dataOutput);
        Mockito.verify(dataOutput, Mockito.times(1)).write(regionEntryValue, 0, regionEntryValue.length);
    }

    @Test
    public void sendAsByteArrayShouldWriteSerializedValueToDataOutput() throws IOException {
        Object regionEntryValue = getValue();
        StoredObject storedObject = createValueAsSerializedStoredObject(regionEntryValue);
        DataOutput dataOutput = Mockito.mock(DataOutput.class);
        storedObject.sendAsByteArray(dataOutput);
        Mockito.verify(dataOutput, Mockito.times(1)).write(storedObject.getSerializedValue(), 0, storedObject.getSerializedValue().length);
    }

    @Test
    public void sendAsByteArrayShouldWriteDeserializedObjectToDataOutput() throws IOException {
        byte[] regionEntryValue = getValueAsByteArray();
        StoredObject storedObject = createValueAsUnserializedStoredObject(regionEntryValue);
        DataOutput dataOutput = Mockito.mock(DataOutput.class);
        storedObject.sendAsByteArray(dataOutput);
        Mockito.verify(dataOutput, Mockito.times(1)).write(regionEntryValue, 0, regionEntryValue.length);
    }

    @Test
    public void sendAsCachedDeserializableShouldWriteSerializedValueToDataOutputAndSetsHeader() throws IOException {
        Object regionEntryValue = getValue();
        StoredObject storedObject = createValueAsSerializedStoredObject(regionEntryValue);
        DataOutput dataOutput = Mockito.mock(DataOutput.class);
        storedObject.sendAsCachedDeserializable(dataOutput);
        Mockito.verify(dataOutput, Mockito.times(1)).writeByte(VM_CACHED_DESERIALIZABLE);
        Mockito.verify(dataOutput, Mockito.times(1)).write(storedObject.getSerializedValue(), 0, storedObject.getSerializedValue().length);
    }

    @Test(expected = IllegalStateException.class)
    public void sendAsCachedDeserializableShouldThrowExceptionIfValueIsNotSerialized() throws IOException {
        Object regionEntryValue = getValue();
        StoredObject storedObject = createValueAsUnserializedStoredObject(regionEntryValue);
        DataOutput dataOutput = Mockito.mock(DataOutput.class);
        storedObject.sendAsCachedDeserializable(dataOutput);
    }
}

