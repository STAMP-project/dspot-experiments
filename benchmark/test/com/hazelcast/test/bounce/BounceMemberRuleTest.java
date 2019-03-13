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
package com.hazelcast.test.bounce;


import com.hazelcast.config.Config;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * Test basic BounceMemberRule functionality
 */
@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class BounceMemberRuleTest {
    private String mapName = HazelcastTestSupport.randomMapName();

    @Rule
    public BounceMemberRule bounceMemberRule = BounceMemberRule.with(new Config()).clusterSize(3).build();

    @Test
    public void test_mapContainsExpectedKey() {
        Assert.assertTrue(getMapFromTestDriver().containsKey("1"));
    }

    @Test(expected = AssertionError.class)
    public void fails_immediately() {
        Assert.assertFalse(getMapFromTestDriver().containsKey("1"));
    }

    @Test(expected = AssertionError.class)
    public void fails_fromRunnable() {
        bounceMemberRule.test(new Runnable[]{ new Runnable() {
            @Override
            public void run() {
                Assert.assertFalse(getMapFromTestDriver().containsKey("1"));
            }
        } });
    }

    @Test(expected = AssertionError.class)
    public void test_cannotSubmit_afterTasksAlreadySubmitted() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                // do nothing
            }
        };
        bounceMemberRule.test(new Runnable[]{ task });
        // next statement will throw an AssertionError
        bounceMemberRule.test(new Runnable[]{ task });
    }

    @Test(expected = AssertionError.class)
    public void fails_whenRanRepeatedly() {
        bounceMemberRule.testRepeatedly(new Runnable[]{ new Runnable() {
            @Override
            public void run() {
                Assert.assertFalse(getMapFromTestDriver().containsKey("1"));
            }
        } }, 10);
    }
}

