/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.worker.block.allocator;


import Allocator.Factory;
import PropertyKey.WORKER_ALLOCATOR_CLASS;
import alluxio.conf.ServerConfiguration;
import java.util.List;
import org.junit.Test;


/**
 * This is the class to test the "contract" of different kinds of allocators,
 * i.e., the general properties the allocators need to follow.
 */
public final class AllocatorContractTest extends AllocatorTestBase {
    protected List<String> mStrategies;

    /**
     * Tests that no allocation happens when the RAM, SSD and HDD size is more than the default one.
     */
    @Test
    public void shouldNotAllocate() throws Exception {
        for (String strategyName : mStrategies) {
            ServerConfiguration.set(WORKER_ALLOCATOR_CLASS, strategyName);
            resetManagerView();
            Allocator allocator = Factory.create(getManagerView());
            assertTempBlockMeta(allocator, mAnyDirInTierLoc1, ((AllocatorTestBase.DEFAULT_RAM_SIZE) + 1), false);
            assertTempBlockMeta(allocator, mAnyDirInTierLoc2, ((AllocatorTestBase.DEFAULT_SSD_SIZE) + 1), false);
            assertTempBlockMeta(allocator, mAnyDirInTierLoc3, ((AllocatorTestBase.DEFAULT_HDD_SIZE) + 1), false);
            assertTempBlockMeta(allocator, mAnyTierLoc, ((AllocatorTestBase.DEFAULT_HDD_SIZE) + 1), false);
            assertTempBlockMeta(allocator, mAnyTierLoc, ((AllocatorTestBase.DEFAULT_SSD_SIZE) + 1), true);
        }
    }

    /**
     * Tests that allocation happens when the RAM, SSD and HDD size is lower than the default size.
     */
    @Test
    public void shouldAllocate() throws Exception {
        for (String strategyName : mStrategies) {
            ServerConfiguration.set(WORKER_ALLOCATOR_CLASS, strategyName);
            resetManagerView();
            Allocator tierAllocator = Factory.create(getManagerView());
            for (int i = 0; i < (AllocatorTestBase.DEFAULT_RAM_NUM); i++) {
                assertTempBlockMeta(tierAllocator, mAnyDirInTierLoc1, ((AllocatorTestBase.DEFAULT_RAM_SIZE) - 1), true);
            }
            for (int i = 0; i < (AllocatorTestBase.DEFAULT_SSD_NUM); i++) {
                assertTempBlockMeta(tierAllocator, mAnyDirInTierLoc2, ((AllocatorTestBase.DEFAULT_SSD_SIZE) - 1), true);
            }
            for (int i = 0; i < (AllocatorTestBase.DEFAULT_HDD_NUM); i++) {
                assertTempBlockMeta(tierAllocator, mAnyDirInTierLoc3, ((AllocatorTestBase.DEFAULT_HDD_SIZE) - 1), true);
            }
            resetManagerView();
            Allocator anyAllocator = Factory.create(getManagerView());
            for (int i = 0; i < (AllocatorTestBase.DEFAULT_RAM_NUM); i++) {
                assertTempBlockMeta(anyAllocator, mAnyTierLoc, ((AllocatorTestBase.DEFAULT_RAM_SIZE) - 1), true);
            }
            for (int i = 0; i < (AllocatorTestBase.DEFAULT_SSD_NUM); i++) {
                assertTempBlockMeta(anyAllocator, mAnyTierLoc, ((AllocatorTestBase.DEFAULT_SSD_SIZE) - 1), true);
            }
            for (int i = 0; i < (AllocatorTestBase.DEFAULT_HDD_NUM); i++) {
                assertTempBlockMeta(anyAllocator, mAnyTierLoc, ((AllocatorTestBase.DEFAULT_HDD_SIZE) - 1), true);
            }
        }
    }
}

