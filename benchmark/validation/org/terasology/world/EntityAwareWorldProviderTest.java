/**
 * Copyright 2018 MovingBlocks
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
package org.terasology.world;


import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.terasology.TerasologyTestingEnvironment;
import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.internal.EngineEntityManager;
import org.terasology.entitySystem.entity.lifecycleEvents.BeforeDeactivateComponent;
import org.terasology.entitySystem.entity.lifecycleEvents.BeforeRemoveComponent;
import org.terasology.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.entitySystem.entity.lifecycleEvents.OnAddedComponent;
import org.terasology.entitySystem.entity.lifecycleEvents.OnChangedComponent;
import org.terasology.entitySystem.event.Event;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.event.internal.EventReceiver;
import org.terasology.entitySystem.event.internal.EventSystem;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.stubs.ForceBlockActiveComponent;
import org.terasology.entitySystem.stubs.IntegerComponent;
import org.terasology.entitySystem.stubs.RetainedOnBlockChangeComponent;
import org.terasology.entitySystem.stubs.StringComponent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.math.geom.Vector3i;
import org.terasology.network.NetworkComponent;
import org.terasology.testUtil.WorldProviderCoreStub;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockComponent;
import org.terasology.world.internal.EntityAwareWorldProvider;


public class EntityAwareWorldProviderTest extends TerasologyTestingEnvironment {
    private EntityAwareWorldProvider worldProvider;

    private WorldProviderCoreStub worldStub;

    private EngineEntityManager entityManager;

    private Prefab prefabWithString;

    private Block airBlock;

    private Block plainBlock;

    private Block blockWithString;

    private Block blockWithDifferentString;

    private Block blockWithRetainedComponent;

    private Block keepActiveBlock;

    private Block blockInFamilyOne;

    private Block blockInFamilyTwo;

    @Test
    public void testGetTemporaryBlockSendsNoEvent() {
        EntityAwareWorldProviderTest.BlockEventChecker checker = new EntityAwareWorldProviderTest.BlockEventChecker();
        entityManager.getEventSystem().registerEventHandler(checker);
        EntityRef blockEntity = worldProvider.getBlockEntityAt(new Vector3i(0, 0, 0));
        Assert.assertTrue(blockEntity.exists());
        Assert.assertFalse(checker.addedReceived);
        Assert.assertFalse(checker.activateReceived);
        Assert.assertFalse(checker.deactivateReceived);
        Assert.assertFalse(checker.removedReceived);
    }

    @Test
    public void testTemporaryCleanedUpWithNoEvent() {
        EntityAwareWorldProviderTest.BlockEventChecker checker = new EntityAwareWorldProviderTest.BlockEventChecker();
        entityManager.getEventSystem().registerEventHandler(checker);
        EntityRef blockEntity = worldProvider.getBlockEntityAt(new Vector3i(0, 0, 0));
        worldProvider.update(1.0F);
        Assert.assertFalse(blockEntity.exists());
        Assert.assertFalse(checker.addedReceived);
        Assert.assertFalse(checker.activateReceived);
        Assert.assertFalse(checker.deactivateReceived);
        Assert.assertFalse(checker.removedReceived);
    }

    @Test
    public void testActiveBlockNotCleanedUp() {
        Block testBlock = new Block();
        testBlock.setKeepActive(true);
        // BlockFamily blockFamily = new SymmetricFamily(new BlockUri("test:keepActive"), testBlock);
        // blockManager.addBlockFamily(blockFamily, true);
        worldStub.setBlock(Vector3i.zero(), testBlock);
        EntityAwareWorldProviderTest.BlockEventChecker checker = new EntityAwareWorldProviderTest.BlockEventChecker();
        entityManager.getEventSystem().registerEventHandler(checker);
        EntityRef blockEntity = worldProvider.getBlockEntityAt(new Vector3i(0, 0, 0));
        worldProvider.update(1.0F);
        Assert.assertTrue(blockEntity.exists());
        Assert.assertTrue(blockEntity.isActive());
        Assert.assertTrue(checker.addedReceived);
        Assert.assertTrue(checker.activateReceived);
    }

    @Test
    public void testPrefabUpdatedWhenBlockChanged() {
        worldProvider.setBlock(Vector3i.zero(), blockWithString);
        Assert.assertEquals(blockWithString.getPrefab().get().getName(), worldProvider.getBlockEntityAt(new Vector3i(0, 0, 0)).getParentPrefab().getName());
        worldProvider.setBlock(Vector3i.zero(), blockWithDifferentString);
        Assert.assertEquals(blockWithDifferentString.getPrefab().get().getName(), worldProvider.getBlockEntityAt(new Vector3i(0, 0, 0)).getParentPrefab().getName());
    }

    @Test
    public void testEntityNotRemovedIfForceBlockActiveComponentAdded() {
        EntityRef blockEntity = worldProvider.getBlockEntityAt(new Vector3i(0, 0, 0));
        blockEntity.addComponent(new ForceBlockActiveComponent());
        worldProvider.update(1.0F);
        Assert.assertTrue(blockEntity.exists());
        Assert.assertTrue(blockEntity.isActive());
    }

    @Test
    public void testEntityBecomesTemporaryWhenChangedFromAKeepActiveBlock() {
        worldProvider.setBlock(Vector3i.zero(), keepActiveBlock);
        EntityRef blockEntity = worldProvider.getBlockEntityAt(new Vector3i(0, 0, 0));
        worldProvider.setBlock(Vector3i.zero(), airBlock);
        worldProvider.update(1.0F);
        Assert.assertFalse(blockEntity.isActive());
    }

    @Test
    public void testEntityBecomesTemporaryIfForceBlockActiveComponentRemoved() {
        EntityRef blockEntity = worldProvider.getBlockEntityAt(new Vector3i(0, 0, 0));
        blockEntity.addComponent(new ForceBlockActiveComponent());
        worldProvider.update(1.0F);
        blockEntity.removeComponent(ForceBlockActiveComponent.class);
        worldProvider.update(1.0F);
        Assert.assertFalse(blockEntity.exists());
        Assert.assertFalse(blockEntity.isActive());
    }

    @Test
    public void testEntityExtraComponentsRemovedBeforeCleanUp() {
        EntityRef entity = worldProvider.getBlockEntityAt(new Vector3i(0, 0, 0));
        entity.addComponent(new StringComponent("test"));
        EntityAwareWorldProviderTest.LifecycleEventChecker checker = new EntityAwareWorldProviderTest.LifecycleEventChecker(entityManager.getEventSystem(), StringComponent.class);
        worldProvider.update(1.0F);
        Assert.assertEquals(Lists.newArrayList(new EntityAwareWorldProviderTest.EventInfo(BeforeDeactivateComponent.newInstance(), entity), new EntityAwareWorldProviderTest.EventInfo(BeforeRemoveComponent.newInstance(), entity)), checker.receivedEvents);
    }

    @Test
    public void testEntityExtraComponentsRemovedBeforeCleanUpForBlocksWithPrefabs() {
        worldStub.setBlock(Vector3i.zero(), blockWithString);
        EntityRef entity = worldProvider.getBlockEntityAt(new Vector3i(0, 0, 0));
        entity.addComponent(new IntegerComponent(1));
        EntityAwareWorldProviderTest.LifecycleEventChecker checker = new EntityAwareWorldProviderTest.LifecycleEventChecker(entityManager.getEventSystem(), IntegerComponent.class);
        worldProvider.update(1.0F);
        Assert.assertEquals(Lists.newArrayList(new EntityAwareWorldProviderTest.EventInfo(BeforeDeactivateComponent.newInstance(), entity), new EntityAwareWorldProviderTest.EventInfo(BeforeRemoveComponent.newInstance(), entity)), checker.receivedEvents);
    }

    @Test
    public void testEntityMissingComponentsAddedBeforeCleanUp() {
        worldStub.setBlock(Vector3i.zero(), blockWithString);
        EntityRef entity = worldProvider.getBlockEntityAt(new Vector3i(0, 0, 0));
        entity.removeComponent(StringComponent.class);
        EntityAwareWorldProviderTest.LifecycleEventChecker checker = new EntityAwareWorldProviderTest.LifecycleEventChecker(entityManager.getEventSystem(), StringComponent.class);
        worldProvider.update(1.0F);
        Assert.assertEquals(Lists.newArrayList(new EntityAwareWorldProviderTest.EventInfo(OnAddedComponent.newInstance(), entity), new EntityAwareWorldProviderTest.EventInfo(OnActivatedComponent.newInstance(), entity)), checker.receivedEvents);
    }

    @Test
    public void testChangedComponentsRevertedBeforeCleanUp() {
        worldStub.setBlock(Vector3i.zero(), blockWithString);
        EntityRef entity = worldProvider.getBlockEntityAt(new Vector3i(0, 0, 0));
        StringComponent comp = entity.getComponent(StringComponent.class);
        comp.value = "Moo";
        entity.saveComponent(comp);
        EntityAwareWorldProviderTest.LifecycleEventChecker checker = new EntityAwareWorldProviderTest.LifecycleEventChecker(entityManager.getEventSystem(), StringComponent.class);
        worldProvider.update(1.0F);
        Assert.assertEquals(Lists.newArrayList(new EntityAwareWorldProviderTest.EventInfo(OnChangedComponent.newInstance(), entity)), checker.receivedEvents);
    }

    @Test
    public void testAllComponentsNotMarkedAsRetainedRemovedOnBlockChange() {
        worldStub.setBlock(Vector3i.zero(), blockWithString);
        EntityRef entity = worldProvider.getBlockEntityAt(new Vector3i(0, 0, 0));
        entity.addComponent(new ForceBlockActiveComponent());
        entity.addComponent(new RetainedOnBlockChangeComponent(2));
        worldProvider.setBlock(Vector3i.zero(), airBlock);
        Assert.assertTrue(entity.hasComponent(RetainedOnBlockChangeComponent.class));
        Assert.assertFalse(entity.hasComponent(ForceBlockActiveComponent.class));
    }

    @Test
    public void testRetainedComponentsNotAltered() {
        EntityRef entity = worldProvider.getBlockEntityAt(new Vector3i(0, 0, 0));
        entity.addComponent(new RetainedOnBlockChangeComponent(2));
        worldProvider.setBlock(Vector3i.zero(), blockWithRetainedComponent);
        Assert.assertEquals(2, entity.getComponent(RetainedOnBlockChangeComponent.class).value);
    }

    @Test
    public void testMetworkComponentAddedWhenChangedToNonTemporary() {
        EntityAwareWorldProviderTest.LifecycleEventChecker checker = new EntityAwareWorldProviderTest.LifecycleEventChecker(entityManager.getEventSystem(), NetworkComponent.class);
        EntityRef entity = worldProvider.getBlockEntityAt(new Vector3i(0, 0, 0));
        entity.addComponent(new RetainedOnBlockChangeComponent(2));
        Assert.assertEquals(Lists.newArrayList(new EntityAwareWorldProviderTest.EventInfo(OnAddedComponent.newInstance(), entity), new EntityAwareWorldProviderTest.EventInfo(OnActivatedComponent.newInstance(), entity)), checker.receivedEvents);
        Assert.assertTrue(entity.hasComponent(NetworkComponent.class));
    }

    @Test
    public void testNetworkComponentRemovedWhenTemporaryCleanedUp() {
        EntityRef entity = worldProvider.getBlockEntityAt(new Vector3i(0, 0, 0));
        entity.addComponent(new RetainedOnBlockChangeComponent(2));
        EntityAwareWorldProviderTest.LifecycleEventChecker checker = new EntityAwareWorldProviderTest.LifecycleEventChecker(entityManager.getEventSystem(), NetworkComponent.class);
        entity.removeComponent(RetainedOnBlockChangeComponent.class);
        worldProvider.update(1.0F);
        Assert.assertEquals(Lists.newArrayList(new EntityAwareWorldProviderTest.EventInfo(BeforeDeactivateComponent.newInstance(), entity), new EntityAwareWorldProviderTest.EventInfo(BeforeRemoveComponent.newInstance(), entity)), checker.receivedEvents);
    }

    @Test
    public void testComponentsNotAlteredIfBlockInSameFamily() {
        worldProvider.setBlock(Vector3i.zero(), blockInFamilyOne);
        EntityRef entity = worldProvider.getBlockEntityAt(Vector3i.zero());
        entity.addComponent(new IntegerComponent());
        worldProvider.setBlock(Vector3i.zero(), blockInFamilyTwo);
        Assert.assertNotNull(entity.getComponent(IntegerComponent.class));
    }

    @Test
    public void testComponentsAlteredIfBlockInSameFamilyWhenForced() {
        worldProvider.setBlock(Vector3i.zero(), blockInFamilyOne);
        EntityRef entity = worldProvider.getBlockEntityAt(Vector3i.zero());
        entity.addComponent(new IntegerComponent());
        worldProvider.setBlockForceUpdateEntity(Vector3i.zero(), blockInFamilyTwo);
        Assert.assertNull(entity.getComponent(IntegerComponent.class));
    }

    @Test
    public void testComponentUntouchedIfRetainRequested() {
        worldProvider.setBlock(Vector3i.zero(), blockInFamilyOne);
        EntityRef entity = worldProvider.getBlockEntityAt(Vector3i.zero());
        entity.addComponent(new IntegerComponent());
        worldProvider.setBlockRetainComponent(Vector3i.zero(), blockWithString, IntegerComponent.class);
        Assert.assertNotNull(entity.getComponent(IntegerComponent.class));
    }

    public static class LifecycleEventChecker {
        public List<EntityAwareWorldProviderTest.EventInfo> receivedEvents = Lists.newArrayList();

        public LifecycleEventChecker(EventSystem eventSystem, Class<? extends Component> forComponent) {
            eventSystem.registerEventReceiver(new EntityAwareWorldProviderTest.LifecycleEventChecker.LifecycleEventReceiver(), OnAddedComponent.class, forComponent);
            eventSystem.registerEventReceiver(new EntityAwareWorldProviderTest.LifecycleEventChecker.LifecycleEventReceiver(), OnActivatedComponent.class, forComponent);
            eventSystem.registerEventReceiver(new EntityAwareWorldProviderTest.LifecycleEventChecker.LifecycleEventReceiver(), OnChangedComponent.class, forComponent);
            eventSystem.registerEventReceiver(new EntityAwareWorldProviderTest.LifecycleEventChecker.LifecycleEventReceiver(), BeforeDeactivateComponent.class, forComponent);
            eventSystem.registerEventReceiver(new EntityAwareWorldProviderTest.LifecycleEventChecker.LifecycleEventReceiver(), BeforeRemoveComponent.class, forComponent);
        }

        private class LifecycleEventReceiver<T extends Event> implements EventReceiver<T> {
            @Override
            public void onEvent(T event, EntityRef entity) {
                receivedEvents.add(new EntityAwareWorldProviderTest.EventInfo(event, entity));
            }
        }
    }

    public static class BlockEventChecker extends BaseComponentSystem {
        public boolean addedReceived;

        public boolean activateReceived;

        public boolean deactivateReceived;

        public boolean removedReceived;

        @ReceiveEvent(components = BlockComponent.class)
        public void onAdded(OnAddedComponent event, EntityRef entity) {
            addedReceived = true;
        }

        @ReceiveEvent(components = BlockComponent.class)
        public void onActivated(OnActivatedComponent event, EntityRef entity) {
            activateReceived = true;
        }

        @ReceiveEvent(components = BlockComponent.class)
        public void onDeactivated(BeforeDeactivateComponent event, EntityRef entity) {
            deactivateReceived = true;
        }

        @ReceiveEvent(components = BlockComponent.class)
        public void onRemoved(BeforeRemoveComponent event, EntityRef entity) {
            removedReceived = true;
        }
    }

    public static class EventInfo {
        public EntityRef targetEntity;

        public Event event;

        public EventInfo(Event event, EntityRef target) {
            this.event = event;
            this.targetEntity = target;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == (this)) {
                return true;
            }
            if (obj instanceof EntityAwareWorldProviderTest.EventInfo) {
                EntityAwareWorldProviderTest.EventInfo other = ((EntityAwareWorldProviderTest.EventInfo) (obj));
                return (Objects.equal(other.targetEntity, targetEntity)) && (Objects.equal(other.event, event));
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(targetEntity, event);
        }
    }
}

