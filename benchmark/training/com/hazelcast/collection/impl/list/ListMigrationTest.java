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
package com.hazelcast.collection.impl.list;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ListMigrationTest extends HazelcastTestSupport {
    private List list;

    private HazelcastInstance local;

    private HazelcastInstance remote1;

    private HazelcastInstance remote2;

    @Test
    public void test() {
        for (int k = 0; k < 100; k++) {
            list.add(k);
        }
        remote1.shutdown();
        remote2.shutdown();
        Assert.assertEquals(100, list.size());
        for (int k = 0; k < 100; k++) {
            Assert.assertEquals(("the set doesn't contain:" + k), k, list.get(k));
        }
    }
}

