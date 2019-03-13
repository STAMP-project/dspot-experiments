/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
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
package org.apache.geode.internal.cache;


import org.apache.geode.internal.cache.persistence.DiskStoreID;
import org.junit.Assert;
import org.junit.Test;


public class OrderedTombstoneMapJUnitTest {
    @Test
    public void test() {
        OrderedTombstoneMap<String> map = new OrderedTombstoneMap<String>();
        DiskStoreID id1 = DiskStoreID.random();
        DiskStoreID id2 = DiskStoreID.random();
        map.put(createVersionTag(id1, 1, 7), "one");
        map.put(createVersionTag(id1, 3, 2), "two");
        map.put(createVersionTag(id2, 3, 5), "three");
        map.put(createVersionTag(id1, 2, 3), "four");
        map.put(createVersionTag(id1, 0, 2), "five");
        map.put(createVersionTag(id2, 4, 4), "six");
        // Now make sure we get the entries in the order we expect (ordered by version tag with a member
        // and by timestampe otherwise.
        Assert.assertEquals("five", map.take().getValue());
        Assert.assertEquals("three", map.take().getValue());
        Assert.assertEquals("six", map.take().getValue());
        Assert.assertEquals("one", map.take().getValue());
        Assert.assertEquals("four", map.take().getValue());
        Assert.assertEquals("two", map.take().getValue());
    }
}

