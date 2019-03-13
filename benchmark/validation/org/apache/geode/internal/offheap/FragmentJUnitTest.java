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


import MemoryBlock.State.UNUSED;
import OffHeapStorage.MIN_SLAB_SIZE;
import OffHeapStoredObject.FILL_BYTE;
import java.util.Arrays;
import org.assertj.core.api.JUnitSoftAssertions;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static OffHeapStorage.MIN_SLAB_SIZE;


public class FragmentJUnitTest {
    private SlabImpl[] slabs;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void fragmentConstructorThrowsExceptionForNon8ByteAlignedAddress() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("address was not 8 byte aligned");
        new Fragment(((slabs[0].getMemoryAddress()) + 2), 0);
        Assert.fail("Constructor failed to throw exception for non-8-byte alignment");
    }

    @Test
    public void zeroSizeFragmentHasNoFreeSpace() {
        Fragment fragment = new Fragment(slabs[0].getMemoryAddress(), 0);
        Assert.assertThat(fragment.freeSpace(), CoreMatchers.is(0));
    }

    @Test
    public void unallocatedFragmentHasFreeSpaceEqualToFragmentSize() {
        Fragment fragment = new Fragment(slabs[0].getMemoryAddress(), slabs[0].getSize());
        softly.assertThat(fragment.getSize()).isEqualTo(((int) (MIN_SLAB_SIZE)));
        softly.assertThat(fragment.freeSpace()).isEqualTo(((int) (MIN_SLAB_SIZE)));
    }

    @Test
    public void allocatingFromFragmentReducesFreeSpace() {
        Fragment fragment = new Fragment(slabs[0].getMemoryAddress(), slabs[0].getSize());
        softly.assertThat(fragment.allocate(fragment.getFreeIndex(), ((fragment.getFreeIndex()) + 256))).isEqualTo(true);
        softly.assertThat(fragment.freeSpace()).isEqualTo(768);
        softly.assertThat(fragment.getFreeIndex()).isEqualTo(256);
    }

    @Test
    public void fragementAllocationIsUnsafeWithRespectToAllocationSize() {
        Fragment fragment = new Fragment(slabs[0].getMemoryAddress(), slabs[0].getSize());
        softly.assertThat(fragment.allocate(fragment.getFreeIndex(), (((fragment.getFreeIndex()) + ((int) (MIN_SLAB_SIZE))) + 8))).isEqualTo(true);
        softly.assertThat(fragment.freeSpace()).isEqualTo((-8));
    }

    @Test
    public void getBlockSizeReturnsFreeSpace() {
        Fragment fragment = new Fragment(slabs[0].getMemoryAddress(), slabs[0].getSize());
        softly.assertThat(fragment.allocate(fragment.getFreeIndex(), ((fragment.getFreeIndex()) + 256))).isEqualTo(true);
        softly.assertThat(fragment.getBlockSize()).isEqualTo(fragment.freeSpace());
    }

    @Test
    public void getMemoryAdressIsAlwaysFragmentBaseAddress() {
        Fragment fragment = new Fragment(slabs[0].getMemoryAddress(), slabs[0].getSize());
        softly.assertThat(fragment.getAddress()).isEqualTo(slabs[0].getMemoryAddress());
        fragment.allocate(fragment.getFreeIndex(), ((fragment.getFreeIndex()) + 256));
        softly.assertThat(fragment.getAddress()).isEqualTo(slabs[0].getMemoryAddress());
    }

    @Test
    public void getStateIsAlwaysStateUNUSED() {
        Fragment fragment = new Fragment(slabs[0].getMemoryAddress(), slabs[0].getSize());
        softly.assertThat(fragment.getState()).isEqualTo(UNUSED);
        fragment.allocate(fragment.getFreeIndex(), ((fragment.getFreeIndex()) + 256));
        softly.assertThat(fragment.getState()).isEqualTo(UNUSED);
    }

    @Test
    public void getFreeListIdIsAlwaysMinus1() {
        Fragment fragment = new Fragment(slabs[0].getMemoryAddress(), slabs[0].getSize());
        softly.assertThat(fragment.getFreeListId()).isEqualTo((-1));
        fragment.allocate(fragment.getFreeIndex(), ((fragment.getFreeIndex()) + 256));
        softly.assertThat(fragment.getFreeListId()).isEqualTo((-1));
    }

    @Test
    public void getRefCountIsAlwaysZero() {
        Fragment fragment = new Fragment(slabs[0].getMemoryAddress(), slabs[0].getSize());
        softly.assertThat(fragment.getRefCount()).isEqualTo(0);
        fragment.allocate(fragment.getFreeIndex(), ((fragment.getFreeIndex()) + 256));
        softly.assertThat(fragment.getRefCount()).isEqualTo(0);
    }

    @Test
    public void getDataTypeIsAlwaysNA() {
        Fragment fragment = new Fragment(slabs[0].getMemoryAddress(), slabs[0].getSize());
        softly.assertThat(fragment.getDataType()).isEqualTo("N/A");
        fragment.allocate(fragment.getFreeIndex(), ((fragment.getFreeIndex()) + 256));
        softly.assertThat(fragment.getDataType()).isEqualTo("N/A");
    }

    @Test
    public void isSerializedIsAlwaysFalse() {
        Fragment fragment = new Fragment(slabs[0].getMemoryAddress(), slabs[0].getSize());
        softly.assertThat(fragment.isSerialized()).isEqualTo(false);
        fragment.allocate(fragment.getFreeIndex(), ((fragment.getFreeIndex()) + 256));
        softly.assertThat(fragment.isSerialized()).isEqualTo(false);
    }

    @Test
    public void isCompressedIsAlwaysFalse() {
        Fragment fragment = new Fragment(slabs[0].getMemoryAddress(), slabs[0].getSize());
        softly.assertThat(fragment.isCompressed()).isEqualTo(false);
        fragment.allocate(fragment.getFreeIndex(), ((fragment.getFreeIndex()) + 256));
        softly.assertThat(fragment.isCompressed()).isEqualTo(false);
    }

    @Test
    public void getDataValueIsAlwaysNull() {
        Fragment fragment = new Fragment(slabs[0].getMemoryAddress(), slabs[0].getSize());
        softly.assertThat(fragment.getDataValue()).isNull();
        fragment.allocate(fragment.getFreeIndex(), ((fragment.getFreeIndex()) + 256));
        softly.assertThat(fragment.getDataValue()).isNull();
    }

    @Test
    public void fragmentEqualsComparesMemoryBlockAddresses() {
        Fragment fragment0 = new Fragment(slabs[0].getMemoryAddress(), slabs[0].getSize());
        Fragment sameFragment = new Fragment(slabs[0].getMemoryAddress(), slabs[0].getSize());
        Fragment fragment1 = new Fragment(slabs[1].getMemoryAddress(), slabs[1].getSize());
        softly.assertThat(fragment0.equals(sameFragment)).isEqualTo(true);
        softly.assertThat(fragment0.equals(fragment1)).isEqualTo(false);
    }

    @Test
    public void fragmentEqualsIsFalseForNonFragmentObjects() {
        Fragment fragment0 = new Fragment(slabs[0].getMemoryAddress(), slabs[0].getSize());
        Assert.assertThat(fragment0.equals(slabs[0]), CoreMatchers.is(false));
    }

    @Test
    public void fragmentHashCodeIsHashCodeOfItsMemoryAddress() {
        Fragment fragment0 = new Fragment(slabs[0].getMemoryAddress(), slabs[0].getSize());
        Fragment fragment1 = new Fragment(slabs[1].getMemoryAddress(), slabs[1].getSize());
        Long fragmentAddress = fragment0.getAddress();
        softly.assertThat(fragment0.hashCode()).isEqualTo(fragmentAddress.hashCode()).isNotEqualTo(fragment1.hashCode());
    }

    @Test
    public void fragmentFillSetsAllBytesToTheSameConstantValue() {
        Fragment fragment = new Fragment(slabs[0].getMemoryAddress(), slabs[0].getSize());
        Long fragmentAddress = fragment.getAddress();
        byte[] bytes = new byte[((int) (MIN_SLAB_SIZE))];
        byte[] expectedBytes = new byte[((int) (MIN_SLAB_SIZE))];
        Arrays.fill(expectedBytes, FILL_BYTE);
        fragment.fill();
        AddressableMemoryManager.readBytes(fragmentAddress, bytes, 0, ((int) (MIN_SLAB_SIZE)));
        Assert.assertThat(bytes, CoreMatchers.is(CoreMatchers.equalTo(expectedBytes)));
    }

    @Test
    public void getNextBlockThrowsExceptionForFragment() {
        expectedException.expect(UnsupportedOperationException.class);
        Fragment fragment = new Fragment(slabs[0].getMemoryAddress(), slabs[0].getSize());
        fragment.getNextBlock();
        Assert.fail("getNextBlock failed to throw UnsupportedOperationException");
    }

    @Test
    public void getSlabIdThrowsExceptionForFragment() {
        expectedException.expect(UnsupportedOperationException.class);
        Fragment fragment = new Fragment(slabs[0].getMemoryAddress(), slabs[0].getSize());
        fragment.getSlabId();
        Assert.fail("getSlabId failed to throw UnsupportedOperationException");
    }
}

