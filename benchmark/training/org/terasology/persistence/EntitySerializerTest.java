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
package org.terasology.persistence;


import EntityData.Component;
import EntityData.Entity;
import EntityData.NameValue;
import EntityData.Value;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.terasology.context.Context;
import org.terasology.engine.SimpleUri;
import org.terasology.engine.module.ModuleManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.internal.EngineEntityManager;
import org.terasology.entitySystem.entity.internal.EntityInfoComponent;
import org.terasology.entitySystem.entity.internal.EntityScope;
import org.terasology.entitySystem.metadata.ComponentLibrary;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.stubs.IntegerComponent;
import org.terasology.entitySystem.stubs.MappedTypeComponent;
import org.terasology.entitySystem.stubs.StringComponent;
import org.terasology.persistence.serializers.EntitySerializer;
import org.terasology.protobuf.EntityData;


/**
 *
 */
public class EntitySerializerTest {
    private static Context context;

    private static ModuleManager moduleManager;

    private ComponentLibrary componentLibrary;

    private EngineEntityManager entityManager;

    private EntitySerializer entitySerializer;

    private Prefab prefab;

    @Test
    public void testDeltaNoUnchangedComponents() throws Exception {
        EntityRef entity = entityManager.create(prefab);
        EntityData.Entity entityData = entitySerializer.serialize(entity);
        Assert.assertEquals(entity.getId(), entityData.getId());
        Assert.assertEquals(prefab.getName(), entityData.getParentPrefab());
        Assert.assertEquals(0, entityData.getComponentCount());
        Assert.assertEquals(0, entityData.getRemovedComponentCount());
    }

    @Test
    public void testDeltaAddNewComponent() throws Exception {
        EntityRef entity = entityManager.create(prefab);
        entity.addComponent(new IntegerComponent(1));
        EntityData.Entity entityData = entitySerializer.serialize(entity);
        Assert.assertEquals(entity.getId(), entityData.getId());
        Assert.assertEquals(prefab.getName(), entityData.getParentPrefab());
        Assert.assertEquals(1, entityData.getComponentCount());
        Assert.assertEquals(0, entityData.getRemovedComponentCount());
        EntityData.Component componentData = entityData.getComponent(0);
        Assert.assertEquals("test:integer", componentData.getType());
        Assert.assertEquals(1, componentData.getFieldCount());
        EntityData.NameValue field = componentData.getField(0);
        Assert.assertEquals("value", field.getName());
        Assert.assertEquals(1, field.getValue().getInteger(0));
    }

    @Test
    public void testDeltaRemoveComponent() throws Exception {
        EntityRef entity = entityManager.create(prefab);
        entity.removeComponent(StringComponent.class);
        EntityData.Entity entityData = entitySerializer.serialize(entity);
        Assert.assertEquals(entity.getId(), entityData.getId());
        Assert.assertEquals(prefab.getName(), entityData.getParentPrefab());
        Assert.assertEquals(0, entityData.getComponentCount());
        Assert.assertEquals(Lists.newArrayList("test:string"), entityData.getRemovedComponentList());
    }

    @Test
    public void testDeltaChangedComponent() throws Exception {
        EntityRef entity = entityManager.create(prefab);
        StringComponent comp = entity.getComponent(StringComponent.class);
        comp.value = "Delta";
        entity.saveComponent(comp);
        EntityData.Entity entityData = entitySerializer.serialize(entity);
        Assert.assertEquals(entity.getId(), entityData.getId());
        Assert.assertEquals(prefab.getName(), entityData.getParentPrefab());
        Assert.assertEquals(1, entityData.getComponentCount());
        Assert.assertEquals(0, entityData.getRemovedComponentCount());
        EntityData.Component componentData = entityData.getComponent(0);
        Assert.assertEquals("test:string", componentData.getType());
        Assert.assertEquals(Lists.newArrayList(NameValue.newBuilder().setName("value").setValue(Value.newBuilder().addString("Delta").build()).build()), componentData.getFieldList());
    }

    @Test
    public void testDeltaLoadNoChange() throws Exception {
        EntityRef entity = entityManager.create("test:Test");
        EntityData.Entity entityData = entitySerializer.serialize(entity);
        long nextId = entityManager.getNextId();
        entityManager.clear();
        entityManager.setNextId(nextId);
        EntityRef loadedEntity = entitySerializer.deserialize(entityData);
        Assert.assertTrue(loadedEntity.exists());
        Assert.assertTrue(loadedEntity.hasComponent(StringComponent.class));
        Assert.assertEquals("Value", loadedEntity.getComponent(StringComponent.class).value);
    }

    @Test
    public void testDeltaLoadAddedComponent() throws Exception {
        EntityRef entity = entityManager.create("test:Test");
        entity.addComponent(new IntegerComponent(2));
        EntityData.Entity entityData = entitySerializer.serialize(entity);
        long nextId = entityManager.getNextId();
        entityManager.clear();
        entityManager.setNextId(nextId);
        EntityRef loadedEntity = entitySerializer.deserialize(entityData);
        Assert.assertTrue(loadedEntity.exists());
        Assert.assertTrue(loadedEntity.hasComponent(StringComponent.class));
        Assert.assertEquals("Value", loadedEntity.getComponent(StringComponent.class).value);
        Assert.assertTrue(loadedEntity.hasComponent(IntegerComponent.class));
        Assert.assertEquals(2, loadedEntity.getComponent(IntegerComponent.class).value);
    }

    @Test
    public void testDeltaLoadRemovedComponent() throws Exception {
        EntityRef entity = entityManager.create("test:Test");
        entity.removeComponent(StringComponent.class);
        EntityData.Entity entityData = entitySerializer.serialize(entity);
        long nextId = entityManager.getNextId();
        entityManager.clear();
        entityManager.setNextId(nextId);
        EntityRef loadedEntity = entitySerializer.deserialize(entityData);
        Assert.assertTrue(loadedEntity.exists());
        Assert.assertFalse(loadedEntity.hasComponent(StringComponent.class));
    }

    @Test
    public void testDeltaLoadChangedComponent() throws Exception {
        EntityRef entity = entityManager.create("test:Test");
        StringComponent comp = entity.getComponent(StringComponent.class);
        comp.value = "Delta";
        entity.saveComponent(comp);
        EntityData.Entity entityData = entitySerializer.serialize(entity);
        long nextId = entityManager.getNextId();
        entityManager.clear();
        entityManager.setNextId(nextId);
        EntityRef loadedEntity = entitySerializer.deserialize(entityData);
        Assert.assertTrue(loadedEntity.exists());
        Assert.assertTrue(loadedEntity.hasComponent(StringComponent.class));
        Assert.assertEquals("Delta", loadedEntity.getComponent(StringComponent.class).value);
    }

    @Test
    public void testPrefabMaintainedOverSerialization() throws Exception {
        EntityRef entity = entityManager.create(prefab);
        EntityData.Entity entityData = entitySerializer.serialize(entity);
        long nextId = entityManager.getNextId();
        entityManager.clear();
        entityManager.setNextId(nextId);
        EntityRef newEntity = entitySerializer.deserialize(entityData);
        Assert.assertTrue(newEntity.hasComponent(EntityInfoComponent.class));
        EntityInfoComponent comp = newEntity.getComponent(EntityInfoComponent.class);
        Assert.assertEquals(prefab, comp.parentPrefab);
    }

    @Test
    public void testAlwaysRelevantPersisted() throws Exception {
        EntityRef entity = entityManager.create(prefab);
        boolean defaultSetting = entity.isAlwaysRelevant();
        EntityScope newScope = (defaultSetting) ? CHUNK : GLOBAL;
        entity.setScope(newScope);
        EntityData.Entity entityData = entitySerializer.serialize(entity);
        long nextId = entityManager.getNextId();
        entityManager.clear();
        entityManager.setNextId(nextId);
        EntityRef newEntity = entitySerializer.deserialize(entityData);
        Assert.assertEquals(newScope, newEntity.getScope());
        Assert.assertEquals((!defaultSetting), newEntity.isAlwaysRelevant());
    }

    @Test
    public void testScopePersisted() {
        EntityRef entity = entityManager.create(prefab);
        for (EntityScope scope : EntityScope.values()) {
            entity.setScope(scope);
            entity = serializeDeserializeEntity(entity);
            Assert.assertEquals(scope, entity.getScope());
        }
    }

    @Test
    public void testMappedTypeHandling() throws Exception {
        componentLibrary.register(new SimpleUri("test", "mappedtype"), MappedTypeComponent.class);
        EntityRef entity = entityManager.create();
        entity.addComponent(new MappedTypeComponent());
        EntityData.Entity entityData = entitySerializer.serialize(entity);
        long nextId = entityManager.getNextId();
        entityManager.clear();
        entityManager.setNextId(nextId);
        EntityRef loadedEntity = entitySerializer.deserialize(entityData);
        Assert.assertTrue(loadedEntity.exists());
        Assert.assertTrue(loadedEntity.hasComponent(MappedTypeComponent.class));
    }
}

