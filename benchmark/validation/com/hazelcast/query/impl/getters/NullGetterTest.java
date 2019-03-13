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
package com.hazelcast.query.impl.getters;


import NullGetter.NULL_GETTER;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class NullGetterTest {
    @Test
    public void test_getValue() throws Exception {
        Object value = NULL_GETTER.getValue(new Object());
        Assert.assertNull(value);
    }

    @Test
    public void test_getReturnType() {
        Class returnType = NULL_GETTER.getReturnType();
        Assert.assertNull(returnType);
    }

    @Test
    public void test_isCacheable() {
        boolean cacheable = NULL_GETTER.isCacheable();
        Assert.assertFalse(cacheable);
    }
}

