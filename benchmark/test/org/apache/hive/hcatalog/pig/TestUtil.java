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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.hive.hcatalog.pig;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test utilities for selectively disabling specific test methods for given storage formats.
 */
public class TestUtil {
    private static final Map<String, Set<String>> SAMPLE_DISABLED_TESTS_MAP = new HashMap<String, Set<String>>() {
        {
            put("test", new HashSet<String>() {
                {
                    add("testShouldSkip");
                }
            });
        }
    };

    @Test
    public void testShouldSkip() {
        Assert.assertTrue(TestUtil.shouldSkip("test", TestUtil.SAMPLE_DISABLED_TESTS_MAP));
    }

    @Test
    public void testShouldNotSkip() {
        Assert.assertFalse(TestUtil.shouldSkip("test", TestUtil.SAMPLE_DISABLED_TESTS_MAP));
        Assert.assertFalse(TestUtil.shouldSkip("foo", TestUtil.SAMPLE_DISABLED_TESTS_MAP));
    }
}

