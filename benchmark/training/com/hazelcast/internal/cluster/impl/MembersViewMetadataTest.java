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
package com.hazelcast.internal.cluster.impl;


import com.hazelcast.nio.Address;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MembersViewMetadataTest {
    @Test
    public void equalsAndHashCode() throws Exception {
        final MembersViewMetadata metadata = new MembersViewMetadata(new Address("localhost", 1234), "memberUUID", new Address("localhost", 4321), 0);
        MembersViewMetadataTest.assertEqualAndHashCode(metadata, metadata);
        Assert.assertNotEquals(metadata, null);
        Assert.assertNotEquals(metadata, "");
        MembersViewMetadataTest.assertEqualAndHashCode(metadata, new MembersViewMetadata(new Address("localhost", 1234), "memberUUID", new Address("localhost", 4321), 0));
        MembersViewMetadataTest.assertNotEqualAndHashCode(metadata, new MembersViewMetadata(new Address("localhost", 999), "memberUUID", new Address("localhost", 4321), 0));
        MembersViewMetadataTest.assertNotEqualAndHashCode(metadata, new MembersViewMetadata(new Address("localhost", 1234), "memberUUID999", new Address("localhost", 4321), 0));
        MembersViewMetadataTest.assertNotEqualAndHashCode(metadata, new MembersViewMetadata(new Address("localhost", 1234), "memberUUID", new Address("localhost", 999), 0));
        MembersViewMetadataTest.assertNotEqualAndHashCode(metadata, new MembersViewMetadata(new Address("localhost", 1234), "memberUUID", new Address("localhost", 4321), 999));
    }
}

