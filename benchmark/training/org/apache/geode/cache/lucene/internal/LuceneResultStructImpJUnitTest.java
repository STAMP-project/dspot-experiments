/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.cache.lucene.internal;


import org.apache.geode.test.junit.categories.LuceneTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ LuceneTest.class })
public class LuceneResultStructImpJUnitTest {
    @Test
    public void hashCodeAndEquals() {
        // Create 2 equal structs
        LuceneResultStructImpl<String, String> result1 = new LuceneResultStructImpl<String, String>("key1", "value1", 5);
        LuceneResultStructImpl<String, String> result2 = new LuceneResultStructImpl<String, String>("key1", "value1", 5);
        Assert.assertEquals(result1, result1);
        Assert.assertEquals(result1, result2);
        Assert.assertEquals(result1.hashCode(), result2.hashCode());
        // And some unequal ones
        LuceneResultStructImpl<String, String> result3 = new LuceneResultStructImpl<String, String>("key2", "value1", 5);
        LuceneResultStructImpl<String, String> result4 = new LuceneResultStructImpl<String, String>("key1", "value2", 5);
        LuceneResultStructImpl<String, String> result5 = new LuceneResultStructImpl<String, String>("key1", "value1", 6);
        Assert.assertNotEquals(result1, result3);
        Assert.assertNotEquals(result1, result4);
        Assert.assertNotEquals(result1, result5);
    }
}

