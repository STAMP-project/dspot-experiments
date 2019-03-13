/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.twitter.distributedlog.logsegment;


import com.twitter.distributedlog.util.Sizable;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test Case for {@link RollingPolicy}s.
 */
public class TestRollingPolicy {
    static class TestSizable implements Sizable {
        long size;

        TestSizable(long size) {
            this.size = size;
        }

        @Override
        public long size() {
            return size;
        }
    }

    @Test(timeout = 60000)
    public void testTimeBasedRollingPolicy() {
        TimeBasedRollingPolicy policy1 = new TimeBasedRollingPolicy(Long.MAX_VALUE);
        TestRollingPolicy.TestSizable maxSize = new TestRollingPolicy.TestSizable(Long.MAX_VALUE);
        Assert.assertFalse(policy1.shouldRollover(maxSize, System.currentTimeMillis()));
        long currentMs = System.currentTimeMillis();
        TimeBasedRollingPolicy policy2 = new TimeBasedRollingPolicy(1000);
        Assert.assertTrue(policy2.shouldRollover(maxSize, (currentMs - (2 * 1000))));
    }

    @Test(timeout = 60000)
    public void testSizeBasedRollingPolicy() {
        SizeBasedRollingPolicy policy = new SizeBasedRollingPolicy(1000);
        TestRollingPolicy.TestSizable sizable1 = new TestRollingPolicy.TestSizable(10);
        Assert.assertFalse(policy.shouldRollover(sizable1, 0L));
        TestRollingPolicy.TestSizable sizable2 = new TestRollingPolicy.TestSizable(10000);
        Assert.assertTrue(policy.shouldRollover(sizable2, 0L));
    }
}

