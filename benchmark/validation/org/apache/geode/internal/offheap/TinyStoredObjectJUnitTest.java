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


import DSCODE.ILLEGAL;
import org.apache.geode.compression.Compressor;
import org.apache.geode.internal.cache.BytesAndBitsForCompactor;
import org.apache.geode.internal.cache.CachePerfStats;
import org.apache.geode.internal.cache.EntryEventImpl;
import org.apache.geode.internal.cache.RegionEntryContext;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TinyStoredObjectJUnitTest extends AbstractStoredObjectTestBase {
    @Test
    public void shouldReturnCorrectEncodingAddress() {
        TinyStoredObject address1 = new TinyStoredObject(10001L);
        Assert.assertNotNull(address1);
        Assert.assertEquals("Encoding address should be:", 10001, address1.getAddress());
        TinyStoredObject address2 = new TinyStoredObject(10002L);
        Assert.assertNotNull(address2);
        Assert.assertEquals("Returning always 10001 expected 10002", 10002, address2.getAddress());
    }

    @Test
    public void twoAddressesShouldBeEqualIfEncodingAddressIsSame() {
        TinyStoredObject address1 = new TinyStoredObject(10001L);
        TinyStoredObject address2 = new TinyStoredObject(10001L);
        Assert.assertEquals("Two addresses are equal if encoding address is same", true, address1.equals(address2));
    }

    @Test
    public void twoAddressesShouldNotBeEqualIfEncodingAddressIsNotSame() {
        TinyStoredObject address1 = new TinyStoredObject(10001L);
        TinyStoredObject address2 = new TinyStoredObject(10002L);
        Assert.assertEquals("Two addresses are not equal if encoding address is not same", false, address1.equals(address2));
    }

    @Test
    public void twoAddressesAreNotEqualIfTheyAreNotTypeDataAsAddress() {
        TinyStoredObject address1 = new TinyStoredObject(10001L);
        Long address2 = new Long(10002L);
        Assert.assertEquals("Two addresses are not equal if encoding address is not same", false, address1.equals(address2));
    }

    @Test
    public void addressHashCodeShouldBe() {
        TinyStoredObject address1 = new TinyStoredObject(10001L);
        Assert.assertEquals("", 10001, address1.hashCode());
    }

    @Test
    public void getSizeInBytesAlwaysReturnsZero() {
        TinyStoredObject address1 = new TinyStoredObject(10001L);
        TinyStoredObject address2 = new TinyStoredObject(10002L);
        Assert.assertEquals("getSizeInBytes", 0, address1.getSizeInBytes());
        Assert.assertEquals("getSizeInBytes", 0, address2.getSizeInBytes());
    }

    @Test
    public void getValueSizeInBytesAlwaysReturnsZero() {
        TinyStoredObject address1 = new TinyStoredObject(10001L);
        TinyStoredObject address2 = new TinyStoredObject(10002L);
        Assert.assertEquals("getSizeInBytes", 0, address1.getValueSizeInBytes());
        Assert.assertEquals("getSizeInBytes", 0, address2.getValueSizeInBytes());
    }

    @Test
    public void isCompressedShouldReturnTrueIfCompressed() {
        Object regionEntryValue = getValue();
        TinyStoredObject offheapAddress = createValueAsCompressedStoredObject(regionEntryValue);
        Assert.assertEquals("Should return true as it is compressed", true, offheapAddress.isCompressed());
    }

    @Test
    public void isCompressedShouldReturnFalseIfNotCompressed() {
        Object regionEntryValue = getValue();
        TinyStoredObject offheapAddress = createValueAsUncompressedStoredObject(regionEntryValue);
        Assert.assertEquals("Should return false as it is compressed", false, offheapAddress.isCompressed());
    }

    @Test
    public void isSerializedShouldReturnTrueIfSeriazlied() {
        Object regionEntryValue = getValue();
        TinyStoredObject offheapAddress = createValueAsSerializedStoredObject(regionEntryValue);
        Assert.assertEquals("Should return true as it is serialized", true, offheapAddress.isSerialized());
    }

    @Test
    public void isSerializedShouldReturnFalseIfNotSeriazlied() {
        Object regionEntryValue = getValue();
        TinyStoredObject offheapAddress = createValueAsUnserializedStoredObject(regionEntryValue);
        Assert.assertEquals("Should return false as it is serialized", false, offheapAddress.isSerialized());
    }

    @Test
    public void getDecompressedBytesShouldReturnDecompressedBytesIfCompressed() {
        Object regionEntryValue = getValue();
        byte[] regionEntryValueAsBytes = convertValueToByteArray(regionEntryValue);
        // encode a non-serialized and compressed entry value to address - last argument is to let that
        // it is compressed
        long encodedAddress = OffHeapRegionEntryHelper.encodeDataAsAddress(regionEntryValueAsBytes, false, true);
        TinyStoredObject offheapAddress = new TinyStoredObject(encodedAddress);
        RegionEntryContext regionContext = Mockito.mock(RegionEntryContext.class);
        CachePerfStats cacheStats = Mockito.mock(CachePerfStats.class);
        Compressor compressor = Mockito.mock(Compressor.class);
        long startTime = 10000L;
        // mock required things
        Mockito.when(regionContext.getCompressor()).thenReturn(compressor);
        Mockito.when(compressor.decompress(regionEntryValueAsBytes)).thenReturn(regionEntryValueAsBytes);
        Mockito.when(regionContext.getCachePerfStats()).thenReturn(cacheStats);
        Mockito.when(cacheStats.startDecompression()).thenReturn(startTime);
        // invoke the thing
        byte[] bytes = offheapAddress.getDecompressedBytes(regionContext);
        // verify the thing happened
        Mockito.verify(cacheStats, Mockito.atLeastOnce()).startDecompression();
        Mockito.verify(compressor, Mockito.times(1)).decompress(regionEntryValueAsBytes);
        Mockito.verify(cacheStats, Mockito.atLeastOnce()).endDecompression(startTime);
        Assert.assertArrayEquals(regionEntryValueAsBytes, bytes);
    }

    @Test
    public void getDecompressedBytesShouldNotTryToDecompressIfNotCompressed() {
        Object regionEntryValue = getValue();
        TinyStoredObject offheapAddress = createValueAsUncompressedStoredObject(regionEntryValue);
        // mock the thing
        RegionEntryContext regionContext = Mockito.mock(RegionEntryContext.class);
        Compressor compressor = Mockito.mock(Compressor.class);
        Mockito.when(regionContext.getCompressor()).thenReturn(compressor);
        // invoke the thing
        byte[] actualValueInBytes = offheapAddress.getDecompressedBytes(regionContext);
        // createValueAsUncompressedStoredObject does uses a serialized value - so convert it to object
        Object actualRegionValue = convertSerializedByteArrayToObject(actualValueInBytes);
        // verify the thing happened
        Mockito.verify(regionContext, Mockito.never()).getCompressor();
        Assert.assertEquals(regionEntryValue, actualRegionValue);
    }

    @Test
    public void getRawBytesShouldReturnAByteArray() {
        byte[] regionEntryValueAsBytes = getValueAsByteArray();
        TinyStoredObject offheapAddress = createValueAsUnserializedStoredObject(regionEntryValueAsBytes);
        byte[] actual = offheapAddress.getRawBytes();
        Assert.assertArrayEquals(regionEntryValueAsBytes, actual);
    }

    @Test
    public void getSerializedValueShouldReturnASerializedByteArray() {
        Object regionEntryValue = getValue();
        TinyStoredObject offheapAddress = createValueAsSerializedStoredObject(regionEntryValue);
        byte[] actualSerializedValue = offheapAddress.getSerializedValue();
        Object actualRegionEntryValue = convertSerializedByteArrayToObject(actualSerializedValue);
        Assert.assertEquals(regionEntryValue, actualRegionEntryValue);
    }

    @Test
    public void getDeserializedObjectShouldReturnADeserializedObject() {
        Object regionEntryValue = getValue();
        TinyStoredObject offheapAddress = createValueAsSerializedStoredObject(regionEntryValue);
        Integer actualRegionEntryValue = ((Integer) (offheapAddress.getDeserializedValue(null, null)));
        Assert.assertEquals(regionEntryValue, actualRegionEntryValue);
    }

    @Test
    public void getDeserializedObjectShouldReturnAByteArrayAsIsIfNotSerialized() {
        byte[] regionEntryValueAsBytes = getValueAsByteArray();
        TinyStoredObject offheapAddress = createValueAsUnserializedStoredObject(regionEntryValueAsBytes);
        byte[] deserializeValue = ((byte[]) (offheapAddress.getDeserializedValue(null, null)));
        Assert.assertArrayEquals(regionEntryValueAsBytes, deserializeValue);
    }

    @Test
    public void fillSerializedValueShouldFillWrapperWithSerializedValueIfValueIsSerialized() {
        Object regionEntryValue = getValue();
        byte[] serializedRegionEntryValue = EntryEventImpl.serialize(regionEntryValue);
        // encode a serialized entry value to address
        long encodedAddress = OffHeapRegionEntryHelper.encodeDataAsAddress(serializedRegionEntryValue, true, false);
        TinyStoredObject offheapAddress = new TinyStoredObject(encodedAddress);
        // mock the things
        BytesAndBitsForCompactor wrapper = Mockito.mock(BytesAndBitsForCompactor.class);
        byte userBits = 1;
        offheapAddress.fillSerializedValue(wrapper, userBits);
        Mockito.verify(wrapper, Mockito.times(1)).setData(serializedRegionEntryValue, userBits, serializedRegionEntryValue.length, true);
    }

    @Test
    public void fillSerializedValueShouldFillWrapperWithDeserializedValueIfValueIsNotSerialized() {
        Object regionEntryValue = getValue();
        byte[] regionEntryValueAsBytes = convertValueToByteArray(regionEntryValue);
        // encode a un serialized entry value to address
        long encodedAddress = OffHeapRegionEntryHelper.encodeDataAsAddress(regionEntryValueAsBytes, false, false);
        TinyStoredObject offheapAddress = new TinyStoredObject(encodedAddress);
        // mock the things
        BytesAndBitsForCompactor wrapper = Mockito.mock(BytesAndBitsForCompactor.class);
        byte userBits = 1;
        offheapAddress.fillSerializedValue(wrapper, userBits);
        Mockito.verify(wrapper, Mockito.times(1)).setData(regionEntryValueAsBytes, userBits, regionEntryValueAsBytes.length, true);
    }

    @Test
    public void getStringFormShouldCatchExceptionAndReturnErrorMessageAsString() {
        Object regionEntryValueAsBytes = getValue();
        byte[] serializedValue = EntryEventImpl.serialize(regionEntryValueAsBytes);
        // store -127 (DSCODE.ILLEGAL) - in order the deserialize to throw exception
        serializedValue[0] = ILLEGAL.toByte();
        // encode a un serialized entry value to address
        long encodedAddress = OffHeapRegionEntryHelper.encodeDataAsAddress(serializedValue, true, false);
        TinyStoredObject offheapAddress = new TinyStoredObject(encodedAddress);
        String errorMessage = offheapAddress.getStringForm();
        Assert.assertEquals(true, errorMessage.contains("Could not convert object to string because "));
    }
}

