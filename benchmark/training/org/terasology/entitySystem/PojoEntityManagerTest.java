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


import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.terasology.assets.ResourceUrn;
import org.terasology.context.Context;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.internal.PojoEntityManager;
import org.terasology.entitySystem.entity.internal.PojoEntityPool;
import org.terasology.entitySystem.entity.lifecycleEvents.BeforeDeactivateComponent;
import org.terasology.entitySystem.entity.lifecycleEvents.BeforeRemoveComponent;
import org.terasology.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.entitySystem.entity.lifecycleEvents.OnAddedComponent;
import org.terasology.entitySystem.entity.lifecycleEvents.OnChangedComponent;
import org.terasology.entitySystem.event.internal.EventSystem;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.prefab.PrefabData;
import org.terasology.entitySystem.stubs.EntityRefComponent;
import org.terasology.entitySystem.stubs.IntegerComponent;
import org.terasology.entitySystem.stubs.StringComponent;
import org.terasology.utilities.Assets;


/**
 *
 */
public class PojoEntityManagerTest {
    private static Context context;

    private PojoEntityManager entityManager;

    private Prefab prefab;

    @Test
    public void testCreateEntity() {
        EntityRef entity = entityManager.create();
        Assert.assertNotNull(entity);
        Assert.assertEquals(CHUNK, entity.getScope());
        Assert.assertTrue(entityManager.getGlobalPool().contains(entity.getId()));
        Assert.assertFalse(entityManager.getSectorManager().contains(entity.getId()));
    }

    @Test
    public void testCreateEntityWithComponent() {
        StringComponent comp = new StringComponent("Test");
        EntityRef entity = entityManager.create(comp);
        Assert.assertNotNull(entity);
        Assert.assertNotNull(entity.getComponent(StringComponent.class));
        Assert.assertEquals(comp, entity.getComponent(StringComponent.class));
        Assert.assertEquals(CHUNK, entity.getScope());
        Assert.assertTrue(entityManager.getGlobalPool().contains(entity.getId()));
        Assert.assertFalse(entityManager.getSectorManager().contains(entity.getId()));
    }

    @Test
    public void testAddAndRetrieveComponent() {
        EntityRef entity = entityManager.create();
        Assert.assertNotNull(entity);
        StringComponent comp = new StringComponent();
        entity.addComponent(comp);
        Assert.assertEquals(comp, entity.getComponent(StringComponent.class));
    }

    @Test
    public void testRemoveComponent() {
        EntityRef entity = entityManager.create();
        StringComponent comp = new StringComponent();
        entity.addComponent(comp);
        entity.removeComponent(StringComponent.class);
        Assert.assertNull(entity.getComponent(StringComponent.class));
    }

    @Test
    public void testReplaceComponent() {
        EntityRef entity = entityManager.create();
        StringComponent comp = new StringComponent();
        comp.value = "Hello";
        StringComponent comp2 = new StringComponent();
        comp2.value = "Goodbye";
        entity.addComponent(comp);
        entity.addComponent(comp2);
        Assert.assertEquals(comp2, entity.getComponent(StringComponent.class));
    }

    @Test
    public void testDestroyEntity() {
        EntityRef entity = entityManager.create();
        entity.addComponent(new StringComponent());
        entity.addComponent(new IntegerComponent());
        entity.destroy();
        Assert.assertNull(entity.getComponent(StringComponent.class));
        Assert.assertNull(entity.getComponent(IntegerComponent.class));
    }

    @Test
    public void testIterateComponents() {
        EntityRef entity = entityManager.create();
        StringComponent comp = new StringComponent();
        entity.addComponent(comp);
        for (Map.Entry<EntityRef, StringComponent> item : entityManager.listComponents(StringComponent.class)) {
            Assert.assertEquals(entity, item.getKey());
            Assert.assertEquals(comp, item.getValue());
        }
    }

    @Test
    public void testChangeComponentsDuringIterator() {
        EntityRef entity1 = entityManager.create();
        entity1.addComponent(new StringComponent());
        EntityRef entity2 = entityManager.create();
        entity2.addComponent(new StringComponent());
        Iterator<Map.Entry<EntityRef, StringComponent>> iterator = entityManager.listComponents(StringComponent.class).iterator();
        iterator.next();
        entity2.removeComponent(StringComponent.class);
        iterator.next();
    }

    @Test
    public void testAddComponentEventSent() {
        EventSystem eventSystem = Mockito.mock(EventSystem.class);
        entityManager.setEventSystem(eventSystem);
        EntityRef entity1 = entityManager.create();
        StringComponent comp = entity1.addComponent(new StringComponent());
        Mockito.verify(eventSystem).send(entity1, OnAddedComponent.newInstance(), comp);
        Mockito.verify(eventSystem).send(entity1, OnActivatedComponent.newInstance(), comp);
    }

    @Test
    public void testRemoveComponentEventSent() {
        EventSystem eventSystem = Mockito.mock(EventSystem.class);
        EntityRef entity1 = entityManager.create();
        StringComponent comp = entity1.addComponent(new StringComponent());
        entityManager.setEventSystem(eventSystem);
        entity1.removeComponent(StringComponent.class);
        Mockito.verify(eventSystem).send(entity1, BeforeDeactivateComponent.newInstance(), comp);
        Mockito.verify(eventSystem).send(entity1, BeforeRemoveComponent.newInstance(), comp);
    }

    @Test
    public void testChangeComponentEventSentWhenSave() {
        EventSystem eventSystem = Mockito.mock(EventSystem.class);
        EntityRef entity1 = entityManager.create();
        StringComponent comp = entity1.addComponent(new StringComponent());
        entityManager.setEventSystem(eventSystem);
        entity1.saveComponent(comp);
        Mockito.verify(eventSystem).send(entity1, OnChangedComponent.newInstance(), comp);
    }

    @Test
    public void testChangeComponentEventSentWhenAddOverExisting() {
        EventSystem eventSystem = Mockito.mock(EventSystem.class);
        EntityRef entity1 = entityManager.create();
        entity1.addComponent(new StringComponent());
        entityManager.setEventSystem(eventSystem);
        StringComponent comp2 = entity1.addComponent(new StringComponent());
        Mockito.verify(eventSystem).send(entity1, OnChangedComponent.newInstance(), comp2);
    }

    @Test
    public void testMassRemovedComponentEventSentOnDestroy() {
        EventSystem eventSystem = Mockito.mock(EventSystem.class);
        EntityRef entity1 = entityManager.create();
        entity1.addComponent(new StringComponent());
        entityManager.setEventSystem(eventSystem);
        entity1.destroy();
        Mockito.verify(eventSystem).send(entity1, BeforeDeactivateComponent.newInstance());
        Mockito.verify(eventSystem).send(entity1, BeforeRemoveComponent.newInstance());
    }

    @Test
    public void testIterateEntitiesFindsEntityWithSingleComponent() {
        EntityRef entity1 = entityManager.create();
        entity1.addComponent(new StringComponent());
        List<EntityRef> results = Lists.newArrayList(entityManager.getEntitiesWith(StringComponent.class));
        Assert.assertEquals(Lists.newArrayList(entity1), results);
    }

    @Test
    public void testIterateEntitiesDoesNotFindEntityMissingAComponent() {
        EntityRef entity1 = entityManager.create();
        entity1.addComponent(new StringComponent());
        List<EntityRef> results = Lists.newArrayList(entityManager.getEntitiesWith(StringComponent.class, IntegerComponent.class));
        Assert.assertEquals(Collections.<EntityRef>emptyList(), results);
    }

    @Test
    public void testIterateEntitiesFindsEntityWithTwoComponents() {
        EntityRef entity1 = entityManager.create();
        entity1.addComponent(new StringComponent());
        entity1.addComponent(new IntegerComponent());
        List<EntityRef> results = Lists.newArrayList(entityManager.getEntitiesWith(StringComponent.class, IntegerComponent.class));
        Assert.assertEquals(Lists.newArrayList(entity1), results);
    }

    @Test
    public void testIterateWithNoComponents() {
        List<EntityRef> results = Lists.newArrayList(entityManager.getEntitiesWith(StringComponent.class));
        Assert.assertEquals(Collections.<EntityRef>emptyList(), results);
    }

    @Test
    public void testGetComponentCountWhenNoComponents() {
        Assert.assertEquals(0, entityManager.getCountOfEntitiesWith(StringComponent.class));
    }

    @Test
    public void testGetComponentCount() {
        entityManager.create().addComponent(new StringComponent());
        entityManager.create().addComponent(new StringComponent());
        Assert.assertEquals(2, entityManager.getCountOfEntitiesWith(StringComponent.class));
    }

    @Test
    public void testDestroyingEntityInvalidatesEntityRefs() {
        EntityRef main = entityManager.create();
        main.addComponent(new StringComponent());
        EntityRef reference = entityManager.create();
        EntityRefComponent refComp = reference.addComponent(new EntityRefComponent());
        refComp.entityRef = entityManager.getEntitiesWith(StringComponent.class).iterator().next();
        Assert.assertTrue(main.exists());
        entityManager.getEntitiesWith(StringComponent.class).iterator().next().destroy();
        Assert.assertFalse(main.exists());
        Assert.assertFalse(refComp.entityRef.exists());
    }

    @Test
    public void testPrefabCopiedCorrectly() {
        EntityRef entity1 = entityManager.create(prefab);
        StringComponent comp = entity1.getComponent(StringComponent.class);
        Assert.assertEquals("Test", comp.value);
        comp.value = "One";
        entity1.saveComponent(comp);
        Assert.assertEquals("Test", prefab.getComponent(StringComponent.class).value);
        EntityRef entity2 = entityManager.create(prefab);
        Assert.assertEquals("Test", prefab.getComponent(StringComponent.class).value);
        Assert.assertEquals("One", entity1.getComponent(StringComponent.class).value);
        Assert.assertEquals("Test", entity2.getComponent(StringComponent.class).value);
    }

    @Test
    public void testPrefabCopiedCorrectly2() {
        EntityRef test1 = entityManager.create("unittest:myprefab");
        EntityRef test2 = entityManager.create("unittest:myprefab");
        // This returns true because the Objectids are Identical.
        Assert.assertFalse(((test1.getComponent(StringComponent.class)) == (test2.getComponent(StringComponent.class))));
    }

    @Test
    public void testPrefabPersistedRetainedCorrectly() {
        PrefabData protoPrefab = new PrefabData();
        protoPrefab.setPersisted(false);
        prefab = Assets.generateAsset(new ResourceUrn("unittest:nonpersistentPrefab"), protoPrefab, Prefab.class);
        EntityRef entity1 = entityManager.create(prefab);
        Assert.assertFalse(entity1.isPersistent());
    }

    @Test
    public void testIsLoadedTrueOnCreate() {
        EntityRef entity = entityManager.create();
        Assert.assertTrue(entity.isActive());
    }

    @Test
    public void testIsLoadedFalseAfterDestroyed() {
        EntityRef entity = entityManager.create();
        entity.destroy();
        Assert.assertFalse(entity.isActive());
    }

    @Test
    public void testIsLoadedFalseAfterPersist() {
        EntityRef entity = entityManager.create();
        entityManager.deactivateForStorage(entity);
        Assert.assertFalse(entity.isActive());
    }

    @Test
    public void testIsLoadedTrueAfterRestore() {
        entityManager.setNextId(3);
        EntityRef entity = entityManager.createEntityWithId(2, Collections.<Component>emptyList());
        Assert.assertTrue(entity.isActive());
    }

    @Test
    public void testLoadedStateClearedWhenEntityManagerCleared() {
        EntityRef entity = entityManager.create();
        entityManager.clear();
        Assert.assertFalse(entity.isActive());
    }

    @Test
    public void testDestructionOfUnloadedEntitiesPrevented() {
        EntityRef entity = entityManager.create();
        long id = entity.getId();
        entityManager.deactivateForStorage(entity);
        Assert.assertTrue(entity.exists());
        entity.destroy();
        Assert.assertTrue(entity.exists());
    }

    @Test
    public void testMoveToPool() {
        EntityRef entity = entityManager.create();
        long id = entity.getId();
        PojoEntityPool pool1 = new PojoEntityPool(entityManager);
        PojoEntityPool pool2 = new PojoEntityPool(entityManager);
        Assert.assertFalse(pool1.contains(id));
        Assert.assertFalse(pool2.contains(id));
        Assert.assertTrue(entityManager.moveToPool(id, pool1));
        Assert.assertTrue(pool1.contains(id));
        Assert.assertFalse(pool2.contains(id));
        Assert.assertTrue(entityManager.moveToPool(id, pool2));
        Assert.assertTrue(pool2.contains(id));
        Assert.assertFalse(pool1.contains(id));
    }
}

