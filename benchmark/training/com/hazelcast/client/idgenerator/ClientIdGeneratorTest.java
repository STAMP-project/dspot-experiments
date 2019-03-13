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
package com.hazelcast.client.idgenerator;


import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.IdGenerator;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientIdGeneratorTest extends HazelcastTestSupport {
    private final TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    private IdGenerator i;

    @Test
    public void testInit0_GivesId1() throws Exception {
        Assert.assertTrue(i.init(0));
        Assert.assertEquals(1, i.newId());
    }

    @Test
    public void testGenerator() throws Exception {
        Assert.assertTrue(i.init(3569));
        Assert.assertFalse(i.init(4569));
        Assert.assertEquals(3570, i.newId());
    }
}

