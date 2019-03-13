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


import org.junit.Assert;
import org.junit.Test;


public class FullEntityTest {
    private static final Key KEY1 = Key.newBuilder("ds1", "k1", "n1").build();

    private static final Key KEY2 = Key.newBuilder("ds1", "k2", 1).build();

    private static final IncompleteKey INCOMPLETE_KEY = IncompleteKey.newBuilder("ds1", "k2").build();

    private static final Entity ENTITY = Entity.newBuilder(FullEntityTest.KEY1).set("foo", "bar").build();

    private static final FullEntity<Key> COMPLETE_ENTITY1 = FullEntityTest.ENTITY;

    private static final FullEntity<Key> COMPLETE_ENTITY2 = FullEntity.newBuilder(FullEntityTest.KEY2).set("foo", "bar").build();

    private static final FullEntity<IncompleteKey> INCOMPLETE_ENTITY = Entity.newBuilder(FullEntityTest.INCOMPLETE_KEY).set("a", "b").build();

    @Test
    public void testFullEntity() throws Exception {
        Assert.assertTrue(FullEntityTest.COMPLETE_ENTITY1.hasKey());
        Assert.assertEquals(FullEntityTest.KEY1, FullEntityTest.COMPLETE_ENTITY1.getKey());
        Assert.assertEquals("bar", FullEntityTest.COMPLETE_ENTITY1.getString("foo"));
        Assert.assertTrue(FullEntityTest.COMPLETE_ENTITY2.hasKey());
        Assert.assertEquals(FullEntityTest.KEY2, FullEntityTest.COMPLETE_ENTITY2.getKey());
        Assert.assertEquals("bar", FullEntityTest.COMPLETE_ENTITY2.getString("foo"));
    }

    @Test
    public void testNoKey() throws Exception {
        FullEntity<IncompleteKey> entity = FullEntity.newBuilder().set("foo", "bar").build();
        Assert.assertFalse(entity.hasKey());
        Assert.assertNull(entity.getKey());
        Assert.assertEquals("bar", entity.getString("foo"));
        entity = FullEntity.newBuilder(((IncompleteKey) (null))).build();
        Assert.assertFalse(entity.hasKey());
        Assert.assertNull(entity.getKey());
    }

    @Test
    public void testCopyFrom() throws Exception {
        FullEntity.Builder<Key> builder1 = FullEntity.newBuilder(FullEntityTest.ENTITY);
        Assert.assertEquals(FullEntityTest.ENTITY, builder1.build());
        builder1 = FullEntity.newBuilder(FullEntityTest.COMPLETE_ENTITY1);
        Assert.assertEquals(FullEntityTest.COMPLETE_ENTITY1, builder1.build());
        FullEntity.Builder<IncompleteKey> builder2 = FullEntity.newBuilder(FullEntityTest.INCOMPLETE_ENTITY);
        Assert.assertEquals(FullEntityTest.INCOMPLETE_ENTITY, builder2.build());
    }
}

