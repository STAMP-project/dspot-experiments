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
package com.hazelcast.internal.memory;


import com.hazelcast.internal.memory.impl.AbstractUnsafeDependentMemoryAccessorTest;
import com.hazelcast.internal.memory.impl.AlignmentUtil;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class })
public class GlobalMemoryAccessorRegistryTest extends AbstractUnsafeDependentMemoryAccessorTest {
    @Test
    public void testConstructor() {
        HazelcastTestSupport.assertUtilityConstructor(GlobalMemoryAccessorRegistry.class);
    }

    @Test
    public void test_getMemoryAccessor_default() {
        Assert.assertNotNull(GlobalMemoryAccessorRegistry.getDefaultGlobalMemoryAccessor());
    }

    @Test
    public void test_getMemoryAccessor_standard() {
        checkStandardMemoryAccessorAvailable();
    }

    @Test
    public void test_getMemoryAccessor_alignmentAware() {
        checkAlignmentAwareMemoryAccessorAvailable();
    }

    @Test
    public void test_getMemoryAccessor_platformAware() {
        if (AlignmentUtil.isUnalignedAccessAllowed()) {
            checkStandardMemoryAccessorAvailable();
        } else {
            checkAlignmentAwareMemoryAccessorAvailable();
        }
    }
}

