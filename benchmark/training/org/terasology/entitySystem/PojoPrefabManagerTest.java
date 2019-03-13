/**
 * Copyright 2013 MovingBlocks
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


import org.junit.Assert;
import org.junit.Test;
import org.terasology.assets.ResourceUrn;
import org.terasology.entitySystem.metadata.ComponentLibrary;
import org.terasology.entitySystem.metadata.EntitySystemLibrary;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.prefab.PrefabData;
import org.terasology.entitySystem.prefab.internal.PojoPrefabManager;
import org.terasology.entitySystem.stubs.StringComponent;
import org.terasology.utilities.Assets;


/**
 *
 */
public class PojoPrefabManagerTest {
    public static final String PREFAB_NAME = "unittest:myprefab";

    private EntitySystemLibrary entitySystemLibrary;

    private ComponentLibrary componentLibrary;

    private PojoPrefabManager prefabManager;

    @Test
    public void testRetrieveNonExistentPrefab() {
        Assert.assertNull(prefabManager.getPrefab(PojoPrefabManagerTest.PREFAB_NAME));
    }

    @Test
    public void testRetrievePrefab() {
        PrefabData data = new PrefabData();
        data.addComponent(new StringComponent("Test"));
        Prefab prefab = Assets.generateAsset(new ResourceUrn(PojoPrefabManagerTest.PREFAB_NAME), data, Prefab.class);
        Prefab ref = prefabManager.getPrefab(PojoPrefabManagerTest.PREFAB_NAME);
        Assert.assertNotNull(ref);
        Assert.assertEquals(PojoPrefabManagerTest.PREFAB_NAME, ref.getName());
    }
}

