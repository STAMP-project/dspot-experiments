/**
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.entitySystem;


import PojoEntityManager.NULL_ID;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.terasology.context.Context;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.internal.PojoEntityManager;
import org.terasology.entitySystem.entity.internal.PojoEntityPool;


/**
 *
 */
public class PojoEntityPoolTest {
    private PojoEntityPool pool;

    private static Context context;

    private PojoEntityManager entityManager;

    @Test
    public void testContains() {
        TestCase.assertFalse(pool.contains(NULL_ID));
        TestCase.assertFalse(pool.contains(1000000));
        EntityRef ref = entityManager.create();
        entityManager.moveToPool(ref.getId(), pool);
        Assert.assertTrue(pool.contains(ref.getId()));
    }

    @Test
    public void testRemove() {
        EntityRef ref = entityManager.create();
        entityManager.moveToPool(ref.getId(), pool);
        Assert.assertTrue(pool.contains(ref.getId()));
        pool.remove(ref.getId());
        TestCase.assertFalse(pool.contains(ref.getId()));
    }
}

