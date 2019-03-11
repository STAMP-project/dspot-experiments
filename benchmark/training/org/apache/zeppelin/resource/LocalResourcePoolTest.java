/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zeppelin.resource;


import org.junit.Assert;
import org.junit.Test;


/**
 * Unittest for LocalResourcePool
 */
public class LocalResourcePoolTest {
    @Test
    public void testGetPutResourcePool() {
        LocalResourcePool pool = new LocalResourcePool("pool1");
        Assert.assertEquals("pool1", pool.id());
        Assert.assertNull(pool.get("notExists"));
        pool.put("item1", "value1");
        Resource resource = pool.get("item1");
        Assert.assertNotNull(resource);
        Assert.assertEquals(pool.id(), resource.getResourceId().getResourcePoolId());
        Assert.assertEquals("value1", resource.get());
        Assert.assertTrue(resource.isLocal());
        Assert.assertTrue(resource.isSerializable());
        Assert.assertEquals(1, pool.getAll().size());
        Assert.assertNotNull(pool.remove("item1"));
        Assert.assertNull(pool.remove("item1"));
    }
}

