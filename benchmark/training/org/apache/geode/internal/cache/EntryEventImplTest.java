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
package org.apache.geode.internal.cache;


import Operation.CREATE;
import Token.INVALID;
import Token.NOT_AVAILABLE;
import Token.REMOVED_PHASE1;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.geode.cache.SerializedCacheValue;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.apache.geode.internal.cache.EntryEventImpl.NewValueImporter;
import org.apache.geode.internal.cache.EntryEventImpl.OldValueImporter;
import org.apache.geode.internal.cache.versions.VersionTag;
import org.apache.geode.internal.offheap.StoredObject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class EntryEventImplTest {
    private String key;

    @Test
    public void verifyToStringOutputHasRegionName() {
        // mock a region object
        LocalRegion region = Mockito.mock(LocalRegion.class);
        String expectedRegionName = "ExpectedFullRegionPathName";
        String value = "value1";
        KeyInfo keyInfo = new KeyInfo(key, value, null);
        Mockito.doReturn(expectedRegionName).when(region).getFullPath();
        Mockito.doReturn(keyInfo).when(region).getKeyInfo(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        // create entry event for the region
        EntryEventImpl e = createEntryEvent(region, value);
        // The name of the region should be in the toString text
        String toStringValue = e.toString();
        Assert.assertTrue(((("String " + expectedRegionName) + " was not in toString text: ") + toStringValue), ((toStringValue.indexOf(expectedRegionName)) > 0));
        // verify that toString called getFullPath method of region object
        Mockito.verify(region, Mockito.times(1)).getFullPath();
    }

    @Test
    public void verifyExportNewValueWithUnserializedStoredObject() {
        LocalRegion region = Mockito.mock(LocalRegion.class);
        Mockito.when(region.getOffHeap()).thenReturn(true);
        StoredObject newValue = Mockito.mock(StoredObject.class);
        byte[] newValueBytes = new byte[]{ 1, 2, 3 };
        Mockito.when(newValue.getValueAsHeapByteArray()).thenReturn(newValueBytes);
        NewValueImporter nvImporter = Mockito.mock(NewValueImporter.class);
        EntryEventImpl e = createEntryEvent(region, newValue);
        e.exportNewValue(nvImporter);
        Mockito.verify(nvImporter).importNewBytes(newValueBytes, false);
    }

    @Test
    public void verifyExportNewValueWithByteArray() {
        LocalRegion region = Mockito.mock(LocalRegion.class);
        byte[] newValue = new byte[]{ 1, 2, 3 };
        NewValueImporter nvImporter = Mockito.mock(NewValueImporter.class);
        EntryEventImpl e = createEntryEvent(region, newValue);
        e.exportNewValue(nvImporter);
        Mockito.verify(nvImporter).importNewBytes(newValue, false);
    }

    @Test
    public void verifyExportNewValueWithStringIgnoresNewValueBytes() {
        LocalRegion region = Mockito.mock(LocalRegion.class);
        String newValue = "newValue";
        NewValueImporter nvImporter = Mockito.mock(NewValueImporter.class);
        Mockito.when(nvImporter.prefersNewSerialized()).thenReturn(true);
        EntryEventImpl e = createEntryEvent(region, newValue);
        byte[] newValueBytes = new byte[]{ 1, 2 };
        e.newValueBytes = newValueBytes;
        e.exportNewValue(nvImporter);
        Mockito.verify(nvImporter).importNewBytes(newValueBytes, true);
    }

    @Test
    public void verifyExportNewValueWithByteArrayCachedDeserializable() {
        LocalRegion region = Mockito.mock(LocalRegion.class);
        CachedDeserializable newValue = Mockito.mock(CachedDeserializable.class);
        byte[] newValueBytes = new byte[]{ 1, 2, 3 };
        Mockito.when(newValue.getValue()).thenReturn(newValueBytes);
        NewValueImporter nvImporter = Mockito.mock(NewValueImporter.class);
        EntryEventImpl e = createEntryEvent(region, newValue);
        e.exportNewValue(nvImporter);
        Mockito.verify(nvImporter).importNewBytes(newValueBytes, true);
    }

    @Test
    public void verifyExportNewValueWithStringCachedDeserializable() {
        LocalRegion region = Mockito.mock(LocalRegion.class);
        CachedDeserializable newValue = Mockito.mock(CachedDeserializable.class);
        Object newValueObj = "newValueObj";
        Mockito.when(newValue.getValue()).thenReturn(newValueObj);
        NewValueImporter nvImporter = Mockito.mock(NewValueImporter.class);
        EntryEventImpl e = createEntryEvent(region, newValue);
        byte[] newValueBytes = new byte[]{ 1, 2 };
        e.newValueBytes = newValueBytes;
        e.setCachedSerializedNewValue(newValueBytes);
        e.exportNewValue(nvImporter);
        Mockito.verify(nvImporter).importNewObject(newValueObj, true);
    }

    @Test
    public void verifyExportNewValueWithStringCachedDeserializablePrefersNewValueBytes() {
        LocalRegion region = Mockito.mock(LocalRegion.class);
        CachedDeserializable newValue = Mockito.mock(CachedDeserializable.class);
        Object newValueObj = "newValueObj";
        Mockito.when(newValue.getValue()).thenReturn(newValueObj);
        NewValueImporter nvImporter = Mockito.mock(NewValueImporter.class);
        Mockito.when(nvImporter.prefersNewSerialized()).thenReturn(true);
        EntryEventImpl e = createEntryEvent(region, newValue);
        byte[] newValueBytes = new byte[]{ 1, 2 };
        e.newValueBytes = newValueBytes;
        e.exportNewValue(nvImporter);
        Mockito.verify(nvImporter).importNewBytes(newValueBytes, true);
    }

    @Test
    public void verifyExportNewValueWithStringCachedDeserializablePrefersCachedSerializedNewValue() {
        LocalRegion region = Mockito.mock(LocalRegion.class);
        CachedDeserializable newValue = Mockito.mock(CachedDeserializable.class);
        Object newValueObj = "newValueObj";
        Mockito.when(newValue.getValue()).thenReturn(newValueObj);
        NewValueImporter nvImporter = Mockito.mock(NewValueImporter.class);
        Mockito.when(nvImporter.prefersNewSerialized()).thenReturn(true);
        EntryEventImpl e = createEntryEvent(region, newValue);
        byte[] newValueBytes = new byte[]{ 1, 2 };
        e.setCachedSerializedNewValue(newValueBytes);
        e.exportNewValue(nvImporter);
        Mockito.verify(nvImporter).importNewBytes(newValueBytes, true);
    }

    @Test
    public void verifyExportNewValueWithSerializedStoredObjectAndImporterPrefersSerialized() {
        LocalRegion region = Mockito.mock(LocalRegion.class);
        Mockito.when(region.getOffHeap()).thenReturn(true);
        StoredObject newValue = Mockito.mock(StoredObject.class);
        Mockito.when(newValue.isSerialized()).thenReturn(true);
        byte[] newValueBytes = new byte[]{ 1, 2, 3 };
        Mockito.when(newValue.getValueAsHeapByteArray()).thenReturn(newValueBytes);
        NewValueImporter nvImporter = Mockito.mock(NewValueImporter.class);
        Mockito.when(nvImporter.prefersNewSerialized()).thenReturn(true);
        EntryEventImpl e = createEntryEvent(region, newValue);
        e.exportNewValue(nvImporter);
        Mockito.verify(nvImporter).importNewBytes(newValueBytes, true);
    }

    @Test
    public void verifyExportNewValueWithSerializedStoredObject() {
        LocalRegion region = Mockito.mock(LocalRegion.class);
        Mockito.when(region.getOffHeap()).thenReturn(true);
        StoredObject newValue = Mockito.mock(StoredObject.class);
        Mockito.when(newValue.isSerialized()).thenReturn(true);
        Object newValueDeserialized = "newValueDeserialized";
        Mockito.when(newValue.getValueAsDeserializedHeapObject()).thenReturn(newValueDeserialized);
        NewValueImporter nvImporter = Mockito.mock(NewValueImporter.class);
        EntryEventImpl e = createEntryEvent(region, newValue);
        e.exportNewValue(nvImporter);
        Mockito.verify(nvImporter).importNewObject(newValueDeserialized, true);
    }

    @Test
    public void verifyExportNewValueWithSerializedStoredObjectAndUnretainedNewReferenceOk() {
        LocalRegion region = Mockito.mock(LocalRegion.class);
        Mockito.when(region.getOffHeap()).thenReturn(true);
        StoredObject newValue = Mockito.mock(StoredObject.class);
        Mockito.when(newValue.isSerialized()).thenReturn(true);
        Object newValueDeserialized = "newValueDeserialized";
        Mockito.when(newValue.getValueAsDeserializedHeapObject()).thenReturn(newValueDeserialized);
        NewValueImporter nvImporter = Mockito.mock(NewValueImporter.class);
        Mockito.when(nvImporter.isUnretainedNewReferenceOk()).thenReturn(true);
        EntryEventImpl e = createEntryEvent(region, newValue);
        e.exportNewValue(nvImporter);
        Mockito.verify(nvImporter).importNewObject(newValue, true);
    }

    @Test
    public void verifyExportOldValueWithUnserializedStoredObject() {
        LocalRegion region = Mockito.mock(LocalRegion.class);
        Mockito.when(region.getOffHeap()).thenReturn(true);
        StoredObject oldValue = Mockito.mock(StoredObject.class);
        byte[] oldValueBytes = new byte[]{ 1, 2, 3 };
        Mockito.when(oldValue.getValueAsHeapByteArray()).thenReturn(oldValueBytes);
        OldValueImporter ovImporter = Mockito.mock(OldValueImporter.class);
        EntryEventImpl e = createEntryEvent(region, null);
        e.setOldValue(oldValue);
        e.exportOldValue(ovImporter);
        Mockito.verify(ovImporter).importOldBytes(oldValueBytes, false);
    }

    @Test
    public void verifyExportOldValueWithByteArray() {
        LocalRegion region = Mockito.mock(LocalRegion.class);
        byte[] oldValue = new byte[]{ 1, 2, 3 };
        OldValueImporter ovImporter = Mockito.mock(OldValueImporter.class);
        EntryEventImpl e = createEntryEvent(region, null);
        e.setOldValue(oldValue);
        e.exportOldValue(ovImporter);
        Mockito.verify(ovImporter).importOldBytes(oldValue, false);
    }

    @Test
    public void verifyExportOldValueWithStringIgnoresOldValueBytes() {
        LocalRegion region = Mockito.mock(LocalRegion.class);
        String oldValue = "oldValue";
        OldValueImporter ovImporter = Mockito.mock(OldValueImporter.class);
        Mockito.when(ovImporter.prefersOldSerialized()).thenReturn(true);
        EntryEventImpl e = createEntryEvent(region, null);
        byte[] oldValueBytes = new byte[]{ 1, 2, 3 };
        e.setSerializedOldValue(oldValueBytes);
        e.setOldValue(oldValue);
        e.exportOldValue(ovImporter);
        Mockito.verify(ovImporter).importOldObject(oldValue, true);
    }

    @Test
    public void verifyExportOldValuePrefersOldValueBytes() {
        LocalRegion region = Mockito.mock(LocalRegion.class);
        OldValueImporter ovImporter = Mockito.mock(OldValueImporter.class);
        Mockito.when(ovImporter.prefersOldSerialized()).thenReturn(true);
        EntryEventImpl e = createEntryEvent(region, null);
        byte[] oldValueBytes = new byte[]{ 1, 2, 3 };
        e.setSerializedOldValue(oldValueBytes);
        e.exportOldValue(ovImporter);
        Mockito.verify(ovImporter).importOldBytes(oldValueBytes, true);
    }

    @Test
    public void verifyExportOldValueWithCacheDeserializableByteArray() {
        LocalRegion region = Mockito.mock(LocalRegion.class);
        CachedDeserializable oldValue = Mockito.mock(CachedDeserializable.class);
        byte[] oldValueBytes = new byte[]{ 1, 2, 3 };
        Mockito.when(oldValue.getValue()).thenReturn(oldValueBytes);
        OldValueImporter ovImporter = Mockito.mock(OldValueImporter.class);
        EntryEventImpl e = createEntryEvent(region, null);
        e.setOldValue(oldValue);
        e.exportOldValue(ovImporter);
        Mockito.verify(ovImporter).importOldBytes(oldValueBytes, true);
    }

    @Test
    public void verifyExportOldValueWithCacheDeserializableString() {
        LocalRegion region = Mockito.mock(LocalRegion.class);
        CachedDeserializable oldValue = Mockito.mock(CachedDeserializable.class);
        Object oldValueObj = "oldValueObj";
        Mockito.when(oldValue.getValue()).thenReturn(oldValueObj);
        OldValueImporter ovImporter = Mockito.mock(OldValueImporter.class);
        EntryEventImpl e = createEntryEvent(region, null);
        e.setOldValue(oldValue);
        e.exportOldValue(ovImporter);
        Mockito.verify(ovImporter).importOldObject(oldValueObj, true);
    }

    @Test
    public void verifyExportOldValueWithCacheDeserializableOk() {
        LocalRegion region = Mockito.mock(LocalRegion.class);
        CachedDeserializable oldValue = Mockito.mock(CachedDeserializable.class);
        Object oldValueObj = "oldValueObj";
        Mockito.when(oldValue.getValue()).thenReturn(oldValueObj);
        OldValueImporter ovImporter = Mockito.mock(OldValueImporter.class);
        Mockito.when(ovImporter.isCachedDeserializableValueOk()).thenReturn(true);
        EntryEventImpl e = createEntryEvent(region, null);
        e.setOldValue(oldValue);
        e.exportOldValue(ovImporter);
        Mockito.verify(ovImporter).importOldObject(oldValue, true);
    }

    @Test
    public void verifyExportOldValueWithSerializedStoredObjectAndImporterPrefersSerialized() {
        LocalRegion region = Mockito.mock(LocalRegion.class);
        Mockito.when(region.getOffHeap()).thenReturn(true);
        StoredObject oldValue = Mockito.mock(StoredObject.class);
        Mockito.when(oldValue.isSerialized()).thenReturn(true);
        byte[] oldValueBytes = new byte[]{ 1, 2, 3 };
        Mockito.when(oldValue.getValueAsHeapByteArray()).thenReturn(oldValueBytes);
        OldValueImporter ovImporter = Mockito.mock(OldValueImporter.class);
        Mockito.when(ovImporter.prefersOldSerialized()).thenReturn(true);
        EntryEventImpl e = createEntryEvent(region, null);
        e.setOldValue(oldValue);
        e.exportOldValue(ovImporter);
        Mockito.verify(ovImporter).importOldBytes(oldValueBytes, true);
    }

    @Test
    public void verifyExportOldValueWithSerializedStoredObject() {
        LocalRegion region = Mockito.mock(LocalRegion.class);
        Mockito.when(region.getOffHeap()).thenReturn(true);
        StoredObject oldValue = Mockito.mock(StoredObject.class);
        Mockito.when(oldValue.isSerialized()).thenReturn(true);
        Object oldValueDeserialized = "newValueDeserialized";
        Mockito.when(oldValue.getValueAsDeserializedHeapObject()).thenReturn(oldValueDeserialized);
        OldValueImporter ovImporter = Mockito.mock(OldValueImporter.class);
        EntryEventImpl e = createEntryEvent(region, null);
        e.setOldValue(oldValue);
        e.exportOldValue(ovImporter);
        Mockito.verify(ovImporter).importOldObject(oldValueDeserialized, true);
    }

    @Test
    public void verifyExportOldValueWithSerializedStoredObjectAndUnretainedOldReferenceOk() {
        LocalRegion region = Mockito.mock(LocalRegion.class);
        Mockito.when(region.getOffHeap()).thenReturn(true);
        StoredObject oldValue = Mockito.mock(StoredObject.class);
        Mockito.when(oldValue.isSerialized()).thenReturn(true);
        Object oldValueDeserialized = "oldValueDeserialized";
        Mockito.when(oldValue.getValueAsDeserializedHeapObject()).thenReturn(oldValueDeserialized);
        OldValueImporter ovImporter = Mockito.mock(OldValueImporter.class);
        Mockito.when(ovImporter.isUnretainedOldReferenceOk()).thenReturn(true);
        EntryEventImpl e = createEntryEvent(region, null);
        e.setOldValue(oldValue);
        e.exportOldValue(ovImporter);
        Mockito.verify(ovImporter).importOldObject(oldValue, true);
    }

    @Test
    public void setOldValueUnforcedWithRemoveTokenChangesOldValueToNull() {
        LocalRegion region = Mockito.mock(LocalRegion.class);
        EntryEventImpl e = createEntryEvent(region, null);
        String UNINITIALIZED = "Uninitialized";
        e.basicSetOldValue(UNINITIALIZED);
        e.setOldValue(REMOVED_PHASE1, false);
        Assert.assertEquals(null, e.basicGetOldValue());
    }

    @Test
    public void setOldValueForcedWithRemoveTokenChangesOldValueToNull() {
        LocalRegion region = Mockito.mock(LocalRegion.class);
        EntryEventImpl e = createEntryEvent(region, null);
        String UNINITIALIZED = "Uninitialized";
        e.basicSetOldValue(UNINITIALIZED);
        e.setOldValue(REMOVED_PHASE1, true);
        Assert.assertEquals(null, e.basicGetOldValue());
    }

    @Test
    public void setOldValueUnforcedWithInvalidTokenNullsOldValue() {
        LocalRegion region = Mockito.mock(LocalRegion.class);
        EntryEventImpl e = createEntryEvent(region, null);
        String UNINITIALIZED = "Uninitialized";
        e.basicSetOldValue(UNINITIALIZED);
        e.setOldValue(INVALID, false);
        Assert.assertEquals(null, e.basicGetOldValue());
    }

    @Test
    public void setOldValueForcedWithInvalidTokenNullsOldValue() {
        LocalRegion region = Mockito.mock(LocalRegion.class);
        EntryEventImpl e = createEntryEvent(region, null);
        String UNINITIALIZED = "Uninitialized";
        e.basicSetOldValue(UNINITIALIZED);
        e.setOldValue(INVALID, true);
        Assert.assertEquals(null, e.basicGetOldValue());
    }

    @Test
    public void setOldValueUnforcedWithNullChangesOldValueToNull() {
        LocalRegion region = Mockito.mock(LocalRegion.class);
        EntryEventImpl e = createEntryEvent(region, null);
        String UNINITIALIZED = "Uninitialized";
        e.basicSetOldValue(UNINITIALIZED);
        e.setOldValue(null, false);
        Assert.assertEquals(null, e.basicGetOldValue());
    }

    @Test
    public void setOldValueForcedWithNullChangesOldValueToNull() {
        LocalRegion region = Mockito.mock(LocalRegion.class);
        EntryEventImpl e = createEntryEvent(region, null);
        String UNINITIALIZED = "Uninitialized";
        e.basicSetOldValue(UNINITIALIZED);
        e.setOldValue(null, true);
        Assert.assertEquals(null, e.basicGetOldValue());
    }

    @Test
    public void setOldValueForcedWithNotAvailableTokenSetsOldValue() {
        LocalRegion region = Mockito.mock(LocalRegion.class);
        EntryEventImpl e = createEntryEvent(region, null);
        String UNINITIALIZED = "Uninitialized";
        e.basicSetOldValue(UNINITIALIZED);
        e.setOldValue(NOT_AVAILABLE, true);
        Assert.assertEquals(NOT_AVAILABLE, e.basicGetOldValue());
    }

    @Test
    public void setOldValueUnforcedWithNotAvailableTokenSetsOldValue() {
        LocalRegion region = Mockito.mock(LocalRegion.class);
        EntryEventImpl e = createEntryEvent(region, null);
        String UNINITIALIZED = "Uninitialized";
        e.basicSetOldValue(UNINITIALIZED);
        e.setOldValue(NOT_AVAILABLE, false);
        Assert.assertEquals(NOT_AVAILABLE, e.basicGetOldValue());
    }

    @Test
    public void setOldUnforcedValueSetsOldValue() {
        LocalRegion region = Mockito.mock(LocalRegion.class);
        EntryEventImpl e = createEntryEvent(region, null);
        String UNINITIALIZED = "Uninitialized";
        e.basicSetOldValue(UNINITIALIZED);
        e.setOldValue("oldValue", false);
        Assert.assertEquals("oldValue", e.basicGetOldValue());
    }

    @Test
    public void setOldValueForcedSetsOldValue() {
        LocalRegion region = Mockito.mock(LocalRegion.class);
        EntryEventImpl e = createEntryEvent(region, null);
        String UNINITIALIZED = "Uninitialized";
        e.basicSetOldValue(UNINITIALIZED);
        e.setOldValue("oldValue", true);
        Assert.assertEquals("oldValue", e.basicGetOldValue());
    }

    @Test
    public void setOldValueUnforcedWithDisabledSetsNotAvailable() {
        EntryEventImpl e = new EntryEventImplTest.EntryEventImplWithOldValuesDisabled();
        String UNINITIALIZED = "Uninitialized";
        e.basicSetOldValue(UNINITIALIZED);
        e.setOldValue("oldValue", false);
        Assert.assertEquals(NOT_AVAILABLE, e.basicGetOldValue());
    }

    @Test
    public void setOldValueForcedWithDisabledSetsOldValue() {
        EntryEventImpl e = new EntryEventImplTest.EntryEventImplWithOldValuesDisabled();
        String UNINITIALIZED = "Uninitialized";
        e.basicSetOldValue(UNINITIALIZED);
        e.setOldValue("oldValue", true);
        Assert.assertEquals("oldValue", e.basicGetOldValue());
    }

    @Test
    public void verifyExternalReadMethodsBlockedByRelease() throws InterruptedException {
        LocalRegion region = Mockito.mock(LocalRegion.class);
        Mockito.when(region.getOffHeap()).thenReturn(true);
        StoredObject newValue = Mockito.mock(StoredObject.class);
        Mockito.when(newValue.hasRefCount()).thenReturn(true);
        Mockito.when(newValue.isSerialized()).thenReturn(true);
        Mockito.when(newValue.retain()).thenReturn(true);
        Mockito.when(newValue.getDeserializedValue(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn("newValue");
        final byte[] serializedNewValue = new byte[]{ ((byte) ('n')), ((byte) ('e')), ((byte) ('w')) };
        Mockito.when(newValue.getSerializedValue()).thenReturn(serializedNewValue);
        StoredObject oldValue = Mockito.mock(StoredObject.class);
        Mockito.when(oldValue.hasRefCount()).thenReturn(true);
        Mockito.when(oldValue.isSerialized()).thenReturn(true);
        Mockito.when(oldValue.retain()).thenReturn(true);
        Mockito.when(oldValue.getDeserializedValue(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn("oldValue");
        final byte[] serializedOldValue = new byte[]{ ((byte) ('o')), ((byte) ('l')), ((byte) ('d')) };
        Mockito.when(oldValue.getSerializedValue()).thenReturn(serializedOldValue);
        final CountDownLatch releaseCountDown = new CountDownLatch(1);
        final EntryEventImplTest.TestableEntryEventImpl e = new EntryEventImplTest.TestableEntryEventImpl(region, key, newValue, releaseCountDown);
        e.setOldValue(oldValue);
        Assert.assertEquals("newValue", getNewValue());
        Assert.assertEquals("oldValue", getOldValue());
        final SerializedCacheValue<?> serializableNewValue = getSerializedNewValue();
        Assert.assertEquals(serializedNewValue, serializableNewValue.getSerializedValue());
        Assert.assertEquals("newValue", serializableNewValue.getDeserializedValue());
        final SerializedCacheValue<?> serializableOldValue = getSerializedOldValue();
        Assert.assertEquals(serializedOldValue, serializableOldValue.getSerializedValue());
        Assert.assertEquals("oldValue", serializableOldValue.getDeserializedValue());
        Thread doRelease = new Thread(() -> {
            release();
        });
        doRelease.start();// release thread will be stuck until releaseCountDown changes

        await().timeout(15, TimeUnit.SECONDS).untilAsserted(() -> assertEquals(true, e.isWaitingOnRelease()));
        Assert.assertEquals(true, e.offHeapOk);
        Assert.assertEquals(true, doRelease.isAlive());
        // Now start a getNewValue. It should block on the release.
        Thread doGetNewValue = new Thread(() -> {
            e.getAndCacheNewValue();
        });
        doGetNewValue.start();
        // Now start a getOldValue. It should block on the release.
        Thread doGetOldValue = new Thread(() -> {
            e.getAndCacheOldValue();
        });
        doGetOldValue.start();
        // Now start a getSerializedValue on serializableNewValue. It should block on the release.
        Thread doSNVgetSerializedValue = new Thread(() -> {
            e.getAndCacheSerializedNew(serializableNewValue);
        });
        doSNVgetSerializedValue.start();
        // Now start a getDeserializedValue on serializableNewValue. It should block on the release.
        Thread doSNVgetDeserializedValue = new Thread(() -> {
            e.getAndCachDeserializedNew(serializableNewValue);
        });
        doSNVgetDeserializedValue.start();
        // Now start a getSerializedValue on serializableOldValue. It should block on the release.
        Thread doSOVgetSerializedValue = new Thread(() -> {
            e.getAndCacheSerializedOld(serializableOldValue);
        });
        doSOVgetSerializedValue.start();
        // Now start a getDeserializedValue on serializableOldValue. It should block on the release.
        Thread doSOVgetDeserializedValue = new Thread(() -> {
            e.getAndCachDeserializedOld(serializableOldValue);
        });
        doSOVgetDeserializedValue.start();
        await().timeout(15, TimeUnit.SECONDS).untilAsserted(() -> assertEquals(true, ((((((e.isAboutToCallGetNewValue()) && (e.isAboutToCallGetOldValue())) && (e.isAboutToCallSerializedNew())) && (e.isAboutToCallDeserializedNew())) && (e.isAboutToCallSerializedOld())) && (e.isAboutToCallDeserializedOld()))));
        // all the threads should now be hung waiting on release; so just wait for a little bit for it
        // to improperly finish
        doGetNewValue.join(50);
        if (e.hasFinishedCallOfGetNewValue()) {
            Assert.fail(("expected doGetNewValue thread to be hung. It completed with " + (e.getCachedNewValue())));
        }
        if (e.hasFinishedCallOfGetOldValue()) {
            Assert.fail(("expected doGetOldValue thread to be hung. It completed with " + (e.getCachedOldValue())));
        }
        if (e.hasFinishedCallOfSerializedNew()) {
            Assert.fail(("expected doSNVgetSerializedValue thread to be hung. It completed with " + (e.getTestCachedSerializedNew())));
        }
        if (e.hasFinishedCallOfDeserializedNew()) {
            Assert.fail(("expected doSNVgetDeserializedValue thread to be hung. It completed with " + (e.getCachedDeserializedNew())));
        }
        if (e.hasFinishedCallOfSerializedOld()) {
            Assert.fail(("expected doSOVgetSerializedValue thread to be hung. It completed with " + (e.getCachedSerializedOld())));
        }
        if (e.hasFinishedCallOfDeserializedOld()) {
            Assert.fail(("expected doSOVgetDeserializedValue thread to be hung. It completed with " + (e.getCachedDeserializedOld())));
        }
        // now signal the release to go ahead
        releaseCountDown.countDown();
        doRelease.join();
        Assert.assertEquals(false, e.offHeapOk);
        // which should allow getNewValue to complete
        await().timeout(15, TimeUnit.SECONDS).untilAsserted(() -> assertEquals(true, e.hasFinishedCallOfGetNewValue()));
        doGetNewValue.join();
        if (!((e.getCachedNewValue()) instanceof IllegalStateException)) {
            // since the release happened before getNewValue we expect it to get an exception
            Assert.fail(("unexpected success of getNewValue. It returned " + (e.getCachedNewValue())));
        }
        // which should allow getOldValue to complete
        await().timeout(15, TimeUnit.SECONDS).untilAsserted(() -> assertEquals(true, e.hasFinishedCallOfGetOldValue()));
        doGetOldValue.join();
        if (!((e.getCachedOldValue()) instanceof IllegalStateException)) {
            Assert.fail(("unexpected success of getOldValue. It returned " + (e.getCachedOldValue())));
        }
        // which should allow doSNVgetSerializedValue to complete
        await().timeout(15, TimeUnit.SECONDS).untilAsserted(() -> assertEquals(true, e.hasFinishedCallOfSerializedNew()));
        doSNVgetSerializedValue.join();
        if (!((e.getTestCachedSerializedNew()) instanceof IllegalStateException)) {
            Assert.fail(("unexpected success of new getSerializedValue. It returned " + (e.getTestCachedSerializedNew())));
        }
        // which should allow doSNVgetDeserializedValue to complete
        await().timeout(15, TimeUnit.SECONDS).untilAsserted(() -> assertEquals(true, e.hasFinishedCallOfDeserializedNew()));
        doSNVgetDeserializedValue.join();
        if (!((e.getCachedDeserializedNew()) instanceof IllegalStateException)) {
            Assert.fail(("unexpected success of new getDeserializedValue. It returned " + (e.getCachedDeserializedNew())));
        }
        // which should allow doSOVgetSerializedValue to complete
        await().timeout(15, TimeUnit.SECONDS).untilAsserted(() -> assertEquals(true, e.hasFinishedCallOfSerializedOld()));
        doSOVgetSerializedValue.join();
        if (!((e.getCachedSerializedOld()) instanceof IllegalStateException)) {
            Assert.fail(("unexpected success of old getSerializedValue. It returned " + (e.getCachedSerializedOld())));
        }
        // which should allow doSOVgetDeserializedValue to complete
        await().timeout(15, TimeUnit.SECONDS).untilAsserted(() -> assertEquals(true, e.hasFinishedCallOfDeserializedOld()));
        doSOVgetDeserializedValue.join();
        if (!((e.getCachedDeserializedOld()) instanceof IllegalStateException)) {
            Assert.fail(("unexpected success of old getDeserializedValue. It returned " + (e.getCachedDeserializedOld())));
        }
    }

    @Test
    public void testGetEventTimeWithNullVersionTag() {
        long timestamp = System.currentTimeMillis();
        LocalRegion region = Mockito.mock(LocalRegion.class);
        Mockito.when(region.cacheTimeMillis()).thenReturn(timestamp);
        EntryEventImpl e = createEntryEvent(region, null);
        assertThat(e.getEventTime(0L)).isEqualTo(timestamp);
    }

    @Test
    public void testGetEventTimeWithVersionTagConcurrencyChecksEnabled() {
        long timestamp = System.currentTimeMillis();
        LocalRegion region = Mockito.mock(LocalRegion.class);
        Mockito.when(region.getConcurrencyChecksEnabled()).thenReturn(true);
        EntryEventImpl e = createEntryEvent(region, null);
        VersionTag tag = VersionTag.create(Mockito.mock(InternalDistributedMember.class));
        tag.setVersionTimeStamp(timestamp);
        e.setVersionTag(tag);
        assertThat(e.getEventTime(0L)).isEqualTo(timestamp);
    }

    @Test
    public void testGetEventTimeWithVersionTagConcurrencyChecksEnabledWithSuggestedTime() {
        long timestamp = System.currentTimeMillis();
        long timestampPlus1 = timestamp + 1000L;
        long timestampPlus2 = timestamp + 2000L;
        LocalRegion region = Mockito.mock(LocalRegion.class);
        Mockito.when(region.getConcurrencyChecksEnabled()).thenReturn(true);
        Mockito.when(region.cacheTimeMillis()).thenReturn(timestamp);
        EntryEventImpl e = createEntryEvent(region, null);
        VersionTag tag = VersionTag.create(Mockito.mock(InternalDistributedMember.class));
        tag.setVersionTimeStamp(timestampPlus1);
        e.setVersionTag(tag);
        assertThat(e.getEventTime(timestampPlus2)).isEqualTo(timestampPlus2);
        assertThat(tag.getVersionTimeStamp()).isEqualTo(timestampPlus2);
    }

    @Test
    public void testGetEventTimeWithVersionTagConcurrencyChecksDisabledNoSuggestedTime() {
        long timestamp = System.currentTimeMillis();
        LocalRegion region = Mockito.mock(LocalRegion.class);
        Mockito.when(region.getConcurrencyChecksEnabled()).thenReturn(false);
        Mockito.when(region.cacheTimeMillis()).thenReturn(timestamp);
        EntryEventImpl e = createEntryEvent(region, null);
        VersionTag tag = VersionTag.create(Mockito.mock(InternalDistributedMember.class));
        tag.setVersionTimeStamp((timestamp + 1000L));
        e.setVersionTag(tag);
        assertThat(e.getEventTime(0L)).isEqualTo(timestamp);
    }

    @Test
    public void testGetEventTimeWithVersionTagConcurrencyChecksDisabledWithSuggestedTime() {
        long timestamp = System.currentTimeMillis();
        LocalRegion region = Mockito.mock(LocalRegion.class);
        Mockito.when(region.getConcurrencyChecksEnabled()).thenReturn(false);
        EntryEventImpl e = createEntryEvent(region, null);
        VersionTag tag = VersionTag.create(Mockito.mock(InternalDistributedMember.class));
        tag.setVersionTimeStamp((timestamp + 1000L));
        e.setVersionTag(tag);
        assertThat(e.getEventTime(timestamp)).isEqualTo(timestamp);
    }

    private static class EntryEventImplWithOldValuesDisabled extends EntryEventImpl {
        @Override
        protected boolean areOldValuesEnabled() {
            return false;
        }
    }

    private static class TestableEntryEventImpl extends EntryEventImpl {
        private final CountDownLatch releaseCountDown;

        private volatile boolean waitingOnRelease = false;

        private volatile boolean aboutToCallGetNewValue = false;

        private volatile boolean finishedCallOfGetNewValue = false;

        private volatile Object cachedNewValue = null;

        private volatile boolean aboutToCallGetOldValue = false;

        private volatile boolean finishedCallOfGetOldValue = false;

        private volatile Object cachedOldValue = null;

        private volatile boolean aboutToCallSerializedNew = false;

        private volatile Object testCachedSerializedNew = null;

        private volatile boolean finishedCallOfSerializedNew = false;

        private volatile boolean aboutToCallDeserializedNew = false;

        private volatile Object cachedDeserializedNew = null;

        private volatile boolean finishedCallOfDeserializedNew = false;

        private volatile boolean aboutToCallSerializedOld = false;

        private volatile Object cachedSerializedOld = null;

        private volatile boolean finishedCallOfSerializedOld = false;

        private volatile boolean aboutToCallDeserializedOld = false;

        private volatile Object cachedDeserializedOld = null;

        private volatile boolean finishedCallOfDeserializedOld = false;

        public TestableEntryEventImpl(LocalRegion region, Object key, Object newValue, CountDownLatch releaseCountDown) {
            super(region, CREATE, key, newValue, null, false, null, false, EntryEventImplTest.createEventID());
            callbacksInvoked(true);
            this.releaseCountDown = releaseCountDown;
        }

        public Object getCachedDeserializedOld() {
            return this.cachedDeserializedOld;
        }

        public boolean hasFinishedCallOfDeserializedOld() {
            return this.finishedCallOfDeserializedOld;
        }

        public Object getCachedSerializedOld() {
            return this.cachedSerializedOld;
        }

        public boolean hasFinishedCallOfSerializedOld() {
            return this.finishedCallOfSerializedOld;
        }

        public Object getCachedDeserializedNew() {
            return this.cachedDeserializedNew;
        }

        public Object getTestCachedSerializedNew() {
            return this.testCachedSerializedNew;
        }

        public boolean hasFinishedCallOfDeserializedNew() {
            return this.finishedCallOfDeserializedNew;
        }

        public boolean hasFinishedCallOfSerializedNew() {
            return this.finishedCallOfSerializedNew;
        }

        public boolean isAboutToCallDeserializedOld() {
            return this.aboutToCallDeserializedOld;
        }

        public boolean isAboutToCallSerializedOld() {
            return this.aboutToCallSerializedOld;
        }

        public boolean isAboutToCallDeserializedNew() {
            return this.aboutToCallDeserializedNew;
        }

        public boolean isAboutToCallSerializedNew() {
            return this.aboutToCallSerializedNew;
        }

        public void getAndCachDeserializedOld(SerializedCacheValue<?> serializableOldValue) {
            try {
                this.aboutToCallDeserializedOld = true;
                this.cachedDeserializedOld = serializableOldValue.getDeserializedValue();
            } catch (IllegalStateException ex) {
                this.cachedDeserializedOld = ex;
            } finally {
                this.finishedCallOfDeserializedOld = true;
            }
        }

        public void getAndCacheSerializedOld(SerializedCacheValue<?> serializableOldValue) {
            try {
                this.aboutToCallSerializedOld = true;
                this.cachedSerializedOld = serializableOldValue.getSerializedValue();
            } catch (IllegalStateException ex) {
                this.cachedSerializedOld = ex;
            } finally {
                this.finishedCallOfSerializedOld = true;
            }
        }

        public void getAndCachDeserializedNew(SerializedCacheValue<?> serializableNewValue) {
            try {
                this.aboutToCallDeserializedNew = true;
                this.cachedDeserializedNew = serializableNewValue.getDeserializedValue();
            } catch (IllegalStateException ex) {
                this.cachedDeserializedNew = ex;
            } finally {
                this.finishedCallOfDeserializedNew = true;
            }
        }

        public void getAndCacheSerializedNew(SerializedCacheValue<?> serializableNewValue) {
            try {
                this.aboutToCallSerializedNew = true;
                this.testCachedSerializedNew = serializableNewValue.getSerializedValue();
            } catch (IllegalStateException ex) {
                this.testCachedSerializedNew = ex;
            } finally {
                this.finishedCallOfSerializedNew = true;
            }
        }

        public Object getCachedNewValue() {
            return this.cachedNewValue;
        }

        public void getAndCacheNewValue() {
            try {
                this.aboutToCallGetNewValue = true;
                this.cachedNewValue = getNewValue();
            } catch (IllegalStateException ex) {
                this.cachedNewValue = ex;
            } finally {
                this.finishedCallOfGetNewValue = true;
            }
        }

        public Object getCachedOldValue() {
            return this.cachedOldValue;
        }

        public void getAndCacheOldValue() {
            try {
                this.aboutToCallGetOldValue = true;
                this.cachedOldValue = getOldValue();
            } catch (IllegalStateException ex) {
                this.cachedOldValue = ex;
            } finally {
                this.finishedCallOfGetOldValue = true;
            }
        }

        public boolean isWaitingOnRelease() {
            return this.waitingOnRelease;
        }

        public boolean isAboutToCallGetNewValue() {
            return this.aboutToCallGetNewValue;
        }

        public boolean hasFinishedCallOfGetNewValue() {
            return this.finishedCallOfGetNewValue;
        }

        public boolean isAboutToCallGetOldValue() {
            return this.aboutToCallGetOldValue;
        }

        public boolean hasFinishedCallOfGetOldValue() {
            return this.finishedCallOfGetOldValue;
        }

        @Override
        void testHookReleaseInProgress() {
            try {
                this.waitingOnRelease = true;
                this.releaseCountDown.await();
            } catch (InterruptedException e) {
                // quit waiting
            }
        }
    }
}

