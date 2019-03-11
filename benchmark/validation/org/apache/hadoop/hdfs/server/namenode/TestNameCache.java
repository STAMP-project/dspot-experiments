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
package org.apache.hadoop.hdfs.server.namenode;


import org.junit.Assert;
import org.junit.Test;


/**
 * Test for {@link NameCache} class
 */
public class TestNameCache {
    @Test
    public void testDictionary() throws Exception {
        // Create dictionary with useThreshold 2
        NameCache<String> cache = new NameCache<String>(2);
        String[] matching = new String[]{ "part1", "part10000000", "fileabc", "abc", "filepart" };
        String[] notMatching = new String[]{ "spart1", "apart", "abcd", "def" };
        for (String s : matching) {
            // Add useThreshold times so the names are promoted to dictionary
            cache.put(s);
            Assert.assertTrue((s == (cache.put(s))));
        }
        for (String s : notMatching) {
            // Add < useThreshold times so the names are not promoted to dictionary
            cache.put(s);
        }
        // Mark dictionary as initialized
        cache.initialized();
        for (String s : matching) {
            verifyNameReuse(cache, s, true);
        }
        // Check dictionary size
        Assert.assertEquals(matching.length, cache.size());
        for (String s : notMatching) {
            verifyNameReuse(cache, s, false);
        }
        cache.reset();
        cache.initialized();
        for (String s : matching) {
            verifyNameReuse(cache, s, false);
        }
        for (String s : notMatching) {
            verifyNameReuse(cache, s, false);
        }
    }
}

