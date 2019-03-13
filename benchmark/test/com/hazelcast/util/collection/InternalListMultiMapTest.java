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
package com.hazelcast.util.collection;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class InternalListMultiMapTest {
    private InternalListMultiMap<Integer, String> multiMap;

    @Test
    public void put_whenEmpty_thenInsertItIntoCollection() {
        multiMap.put(1, "value");
        Collection<String> results = multiMap.get(1);
        Assert.assertThat(results, Matchers.hasSize(1));
        Assert.assertThat(results, Matchers.contains("value"));
    }

    @Test
    public void put_whenKeyIsAlreadyAssociated_thenAppendItIntoCollection() {
        multiMap.put(1, "value");
        multiMap.put(1, "value");
        Collection<String> results = multiMap.get(1);
        Assert.assertThat(results, Matchers.hasSize(2));
        Assert.assertThat(results, Matchers.contains("value", "value"));
    }

    @Test
    public void entrySet_whenEmpty_thenReturnEmptySet() {
        Set<Map.Entry<Integer, List<String>>> entries = multiMap.entrySet();
        Assert.assertThat(entries, Matchers.hasSize(0));
    }
}

