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


import EntityRef.NULL;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.terasology.context.Context;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.internal.EntityInfoComponent;
import org.terasology.entitySystem.entity.internal.EntityScope;
import org.terasology.entitySystem.entity.internal.PojoEntityManager;


public class BaseEntityRefTest {
    private static Context context;

    private PojoEntityManager entityManager;

    private EntityRef ref;

    @Test
    public void testSetScope() {
        ref = entityManager.create();
        Assert.assertEquals(CHUNK, ref.getScope());
        for (EntityScope scope : EntityScope.values()) {
            ref.setScope(scope);
            Assert.assertEquals(ref.getScope(), scope);
        }
        // Move into sector scope
        ref.setScope(SECTOR);
        Assert.assertEquals(ref.getScope(), SECTOR);
        Assert.assertTrue(entityManager.getSectorManager().contains(ref.getId()));
        Assert.assertFalse(entityManager.getGlobalPool().contains(ref.getId()));
        // And move back to global scope
        ref.setScope(GLOBAL);
        Assert.assertEquals(ref.getScope(), GLOBAL);
        Assert.assertTrue(entityManager.getGlobalPool().contains(ref.getId()));
        Assert.assertFalse(entityManager.getSectorManager().contains(ref.getId()));
    }

    @Test
    public void testCreateWithScopeInfoComponent() {
        EntityInfoComponent info = new EntityInfoComponent();
        info.scope = SECTOR;
        EntityInfoComponent info2 = new EntityInfoComponent();
        info2.scope = SECTOR;
        ref = entityManager.create(info);
        Assert.assertEquals(SECTOR, ref.getScope());
        long safeId = ref.getId();
        ref.destroy();
        ref = entityManager.createEntityWithId(safeId, Lists.newArrayList(info2));
        Assert.assertNotNull(ref);
        Assert.assertNotEquals(NULL, ref);
        Assert.assertEquals(SECTOR, ref.getScope());
    }
}

