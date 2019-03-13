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


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class OffHeapHelperJUnitTest extends AbstractStoredObjectTestBase {
    private StoredObject storedObject = null;

    private Object deserializedRegionEntryValue = null;

    private byte[] serializedRegionEntryValue = null;

    private MemoryAllocator ma;

    @Test
    public void getHeapFormOfSerializedStoredObjectReturnsDeserializedObject() {
        allocateOffHeapSerialized();
        Object heapObject = OffHeapHelper.getHeapForm(storedObject);
        Assert.assertThat("getHeapForm returns non-null object", heapObject, CoreMatchers.notNullValue());
        Assert.assertThat("Heap and off heap objects are different objects", heapObject, CoreMatchers.is(CoreMatchers.not(storedObject)));
        Assert.assertThat("Deserialzed values of offHeap object and returned object are equal", heapObject, CoreMatchers.is(CoreMatchers.equalTo(deserializedRegionEntryValue)));
    }

    @Test
    public void getHeapFormOfNonOffHeapObjectReturnsOriginal() {
        Object testObject = getValue();
        Object heapObject = OffHeapHelper.getHeapForm(testObject);
        Assert.assertNotNull(heapObject);
        Assert.assertSame(testObject, heapObject);
    }

    @Test
    public void getHeapFormWithNullReturnsNull() {
        Object testObject = null;
        Object returnObject = OffHeapHelper.getHeapForm(testObject);
        Assert.assertThat(returnObject, CoreMatchers.is(CoreMatchers.equalTo(null)));
    }

    @Test
    public void copyAndReleaseWithNullReturnsNull() {
        Object testObject = null;
        Object returnObject = OffHeapHelper.copyAndReleaseIfNeeded(testObject, null);
        Assert.assertThat(returnObject, CoreMatchers.is(CoreMatchers.equalTo(null)));
    }

    @Test
    public void copyAndReleaseWithRetainedDeserializedObjectDecreasesRefCnt() {
        allocateOffHeapDeserialized();
        Assert.assertTrue(storedObject.retain());
        Assert.assertThat("Retained chunk ref count", storedObject.getRefCount(), CoreMatchers.is(2));
        OffHeapHelper.copyAndReleaseIfNeeded(storedObject, null);
        Assert.assertThat("Chunk ref count decreases", storedObject.getRefCount(), CoreMatchers.is(1));
    }

    @Test
    public void copyAndReleaseWithNonRetainedObjectDecreasesRefCnt() {
        allocateOffHeapDeserialized();
        // assertTrue(storedObject.retain());
        Assert.assertThat("Retained chunk ref count", storedObject.getRefCount(), CoreMatchers.is(1));
        OffHeapHelper.copyAndReleaseIfNeeded(storedObject, null);
        Assert.assertThat("Chunk ref count decreases", storedObject.getRefCount(), CoreMatchers.is(0));
    }

    @Test
    public void copyAndReleaseWithDeserializedReturnsValueOfOriginal() {
        allocateOffHeapDeserialized();
        Assert.assertTrue(storedObject.retain());
        Object returnObject = OffHeapHelper.copyAndReleaseIfNeeded(storedObject, null);
        Assert.assertThat(returnObject, CoreMatchers.is(CoreMatchers.equalTo(deserializedRegionEntryValue)));
    }

    @Test
    public void copyAndReleaseWithSerializedReturnsValueOfOriginal() {
        allocateOffHeapSerialized();
        Assert.assertTrue(storedObject.retain());
        Object returnObject = getSerializedValue();
        Assert.assertThat(returnObject, CoreMatchers.is(CoreMatchers.equalTo(serializedRegionEntryValue)));
    }

    @Test
    public void copyAndReleaseNonStoredObjectReturnsOriginal() {
        Object testObject = getValue();
        Object returnObject = OffHeapHelper.copyAndReleaseIfNeeded(testObject, null);
        Assert.assertThat(returnObject, CoreMatchers.is(testObject));
    }

    @Test
    public void copyIfNeededWithNullReturnsNull() {
        Object testObject = null;
        Object returnObject = OffHeapHelper.copyAndReleaseIfNeeded(testObject, null);
        Assert.assertThat(returnObject, CoreMatchers.is(CoreMatchers.equalTo(null)));
    }

    @Test
    public void copyIfNeededNonOffHeapReturnsOriginal() {
        Object testObject = getValue();
        Object returnObject = OffHeapHelper.copyIfNeeded(testObject, null);
        Assert.assertThat(returnObject, CoreMatchers.is(testObject));
    }

    @Test
    public void copyIfNeededOffHeapSerializedReturnsValueOfOriginal() {
        allocateOffHeapSerialized();
        Object returnObject = getSerializedValue();
        Assert.assertThat(returnObject, CoreMatchers.is(CoreMatchers.equalTo(serializedRegionEntryValue)));
    }

    @Test
    public void copyIfNeededOffHeapDeserializedReturnsOriginal() {
        allocateOffHeapDeserialized();
        Object returnObject = OffHeapHelper.copyIfNeeded(storedObject, null);
        Assert.assertThat(returnObject, CoreMatchers.is(CoreMatchers.equalTo(deserializedRegionEntryValue)));
    }

    @Test
    public void copyIfNeededWithOffHeapDeserializedObjDoesNotRelease() {
        allocateOffHeapDeserialized();
        int initialRefCountOfObject = storedObject.getRefCount();
        OffHeapHelper.copyIfNeeded(storedObject, null);
        Assert.assertThat("Ref count after copy", storedObject.getRefCount(), CoreMatchers.is(initialRefCountOfObject));
    }

    @Test
    public void copyIfNeededWithOffHeapSerializedObjDoesNotRelease() {
        allocateOffHeapSerialized();
        int initialRefCountOfObject = storedObject.getRefCount();
        OffHeapHelper.copyIfNeeded(storedObject, null);
        Assert.assertThat("Ref count after copy", storedObject.getRefCount(), CoreMatchers.is(initialRefCountOfObject));
    }

    @Test
    public void releaseOfOffHeapDecrementsRefCount() {
        allocateOffHeapSerialized();
        Assert.assertThat("Initial Ref Count", storedObject.getRefCount(), CoreMatchers.is(1));
        OffHeapHelper.release(storedObject);
        Assert.assertThat("Ref Count Decremented", storedObject.getRefCount(), CoreMatchers.is(0));
    }

    @Test
    public void releaseOfOffHeapReturnsTrue() {
        allocateOffHeapSerialized();
        Assert.assertThat("Releasing OFfHeap object is true", OffHeapHelper.release(storedObject), CoreMatchers.is(true));
    }

    @Test
    public void releaseOfNonOffHeapReturnsFalse() {
        Object testObject = getValue();
        Assert.assertThat("Releasing OFfHeap object is true", OffHeapHelper.release(testObject), CoreMatchers.is(false));
    }

    @Test
    public void releaseWithOutTrackingOfOffHeapDecrementsRefCount() {
        allocateOffHeapSerialized();
        Assert.assertThat("Initial Ref Count", storedObject.getRefCount(), CoreMatchers.is(1));
        OffHeapHelper.releaseWithNoTracking(storedObject);
        Assert.assertThat("Ref Count Decremented", storedObject.getRefCount(), CoreMatchers.is(0));
    }

    @Test
    public void releaseWithoutTrackingOfOffHeapReturnsTrue() {
        allocateOffHeapSerialized();
        Assert.assertThat("Releasing OFfHeap object is true", OffHeapHelper.releaseWithNoTracking(storedObject), CoreMatchers.is(true));
    }

    @Test
    public void releaseWithoutTrackingOfNonOffHeapReturnsFalse() {
        Object testObject = getValue();
        Assert.assertThat("Releasing OFfHeap object is true", OffHeapHelper.releaseWithNoTracking(testObject), CoreMatchers.is(false));
    }

    @Test
    public void releaseAndTrackOwnerOfOffHeapDecrementsRefCount() {
        allocateOffHeapSerialized();
        Assert.assertThat("Initial Ref Count", storedObject.getRefCount(), CoreMatchers.is(1));
        OffHeapHelper.releaseAndTrackOwner(storedObject, "owner");
        Assert.assertThat("Ref Count Decremented", storedObject.getRefCount(), CoreMatchers.is(0));
    }

    @Test
    public void releaseAndTrackOwnerOfOffHeapReturnsTrue() {
        allocateOffHeapSerialized();
        Assert.assertThat("Releasing OFfHeap object is true", OffHeapHelper.releaseAndTrackOwner(storedObject, "owner"), CoreMatchers.is(true));
    }

    @Test
    public void releaseAndTrackOwnerOfNonOffHeapReturnsFalse() {
        Object testObject = getValue();
        Assert.assertThat("Releasing OFfHeap object is true", OffHeapHelper.releaseAndTrackOwner(testObject, "owner"), CoreMatchers.is(false));
    }
}

