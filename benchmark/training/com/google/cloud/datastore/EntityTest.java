/**
 * Copyright 2015 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.datastore;


import Entity.Builder;
import org.junit.Assert;
import org.junit.Test;


public class EntityTest {
    private static final Key KEY1 = Key.newBuilder("ds1", "k1", "n1").build();

    private static final Key KEY2 = Key.newBuilder("ds1", "k2", 1).build();

    private static final IncompleteKey INCOMPLETE_KEY = IncompleteKey.newBuilder("ds1", "k2").build();

    private static final Entity ENTITY = Entity.newBuilder(EntityTest.KEY1).set("foo", "bar").build();

    private static final FullEntity<IncompleteKey> INCOMPLETE_ENTITY = Entity.newBuilder(EntityTest.INCOMPLETE_KEY).set("a", "b").build();

    @Test
    public void testEntity() throws Exception {
        Assert.assertTrue(EntityTest.ENTITY.hasKey());
        Assert.assertEquals(EntityTest.KEY1, EntityTest.ENTITY.getKey());
        Assert.assertEquals("bar", EntityTest.ENTITY.getString("foo"));
    }

    @Test
    public void testCopyFrom() throws Exception {
        Entity.Builder builder = Entity.newBuilder(EntityTest.ENTITY);
        Assert.assertEquals(EntityTest.ENTITY, builder.build());
        Entity entity = builder.setKey(EntityTest.KEY2).build();
        Assert.assertNotEquals(EntityTest.ENTITY, entity);
        Assert.assertEquals(EntityTest.KEY2, entity.getKey());
        Assert.assertEquals(EntityTest.ENTITY.getProperties(), entity.getProperties());
    }

    @Test
    public void testCopyFromIncompleteEntity() throws Exception {
        Entity.Builder builder = Entity.newBuilder(EntityTest.KEY2, EntityTest.INCOMPLETE_ENTITY);
        Entity entity = builder.build();
        Assert.assertNotEquals(EntityTest.INCOMPLETE_ENTITY, entity);
        Assert.assertEquals(EntityTest.INCOMPLETE_ENTITY.getProperties(), entity.getProperties());
    }
}

