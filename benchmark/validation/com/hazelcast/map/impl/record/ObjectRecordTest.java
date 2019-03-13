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
package com.hazelcast.map.impl.record;


import com.hazelcast.core.IMap;
import com.hazelcast.query.SampleTestObjects;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * Uses basic map functionality with different configuration than existing ones.
 * Actually, it tests {@link ObjectRecord#getValue()} and {@link ObjectRecord#setValue(Object)}.
 */
@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ObjectRecordTest extends HazelcastTestSupport {
    private IMap<Integer, Object> map;

    @Test
    public void testGetValue() throws Exception {
        map.put(1, new SampleTestObjects.Employee("alex", 26, true, 25));
        map.get(1);
        HazelcastTestSupport.assertSizeEventually(1, map);
    }

    @Test
    public void testSetValue() throws Exception {
        map.put(1, new SampleTestObjects.Employee("alex", 26, true, 25));
        map.put(1, new SampleTestObjects.Employee("tom", 24, true, 10));
        HazelcastTestSupport.assertSizeEventually(1, map);
    }
}

