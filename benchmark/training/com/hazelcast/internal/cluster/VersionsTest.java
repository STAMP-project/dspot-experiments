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
package com.hazelcast.internal.cluster;


import Versions.V3_10;
import Versions.V3_11;
import Versions.V3_8;
import Versions.V3_9;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.version.Version;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class VersionsTest extends HazelcastTestSupport {
    @Test
    public void testConstructor() {
        HazelcastTestSupport.assertUtilityConstructor(Versions.class);
    }

    @Test
    public void version_3_8() {
        Assert.assertEquals(Version.of(3, 8), V3_8);
    }

    @Test
    public void version_3_9() {
        Assert.assertEquals(Version.of(3, 9), V3_9);
    }

    @Test
    public void version_3_10() {
        Assert.assertEquals(Version.of(3, 10), V3_10);
    }

    @Test
    public void version_3_11() {
        Assert.assertEquals(Version.of(3, 11), V3_11);
    }
}

