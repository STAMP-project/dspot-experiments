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
package org.terasology.persistence.internal;


import com.google.common.collect.Lists;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.terasology.TerasologyTestingEnvironment;
import org.terasology.engine.bootstrap.EntitySystemSetupUtil;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.internal.EngineEntityManager;
import org.terasology.entitySystem.stubs.EntityRefComponent;
import org.terasology.entitySystem.stubs.StringComponent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.module.ModuleEnvironment;
import org.terasology.persistence.ChunkStore;
import org.terasology.persistence.PlayerStore;
import org.terasology.persistence.StorageManager;
import org.terasology.recording.RecordAndReplayCurrentStatus;
import org.terasology.recording.RecordAndReplaySerializer;
import org.terasology.recording.RecordAndReplayUtils;
import org.terasology.registry.CoreRegistry;
import org.terasology.world.biomes.BiomeManager;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.world.chunks.Chunk;
import org.terasology.world.chunks.ChunkProvider;
import org.terasology.world.chunks.blockdata.ExtraBlockDataManager;


public class StorageManagerTest extends TerasologyTestingEnvironment {
    public static final String PLAYER_ID = "someId";

    public static final Vector3i CHUNK_POS = new Vector3i(1, 2, 3);

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private ModuleEnvironment moduleEnvironment;

    private ReadWriteStorageManager esm;

    private EngineEntityManager entityManager;

    private BlockManager blockManager;

    private BiomeManager biomeManager;

    private ExtraBlockDataManager extraDataManager;

    private Block testBlock;

    private Block testBlock2;

    private EntityRef character;

    private Path savePath;

    private RecordAndReplaySerializer recordAndReplaySerializer;

    private RecordAndReplayUtils recordAndReplayUtils;

    private RecordAndReplayCurrentStatus recordAndReplayCurrentStatus;

    @Test
    public void testGetUnstoredPlayerReturnsNewStor() {
        PlayerStore store = esm.loadPlayerStore(StorageManagerTest.PLAYER_ID);
        Assert.assertNotNull(store);
        Assert.assertEquals(new Vector3f(), store.getRelevanceLocation());
        Assert.assertFalse(store.hasCharacter());
        Assert.assertEquals(StorageManagerTest.PLAYER_ID, store.getId());
    }

    @Test
    public void testStoreAndRestoreOfPlayerWithoutCharacter() {
        // remove character from player:
        character.destroy();
        esm.waitForCompletionOfPreviousSaveAndStartSaving();
        esm.finishSavingAndShutdown();
        PlayerStore restoredStore = esm.loadPlayerStore(StorageManagerTest.PLAYER_ID);
        Assert.assertNotNull(restoredStore);
        Assert.assertFalse(restoredStore.hasCharacter());
        Assert.assertEquals(new Vector3f(), restoredStore.getRelevanceLocation());
    }

    @Test
    public void testPlayerRelevanceLocationSurvivesStorage() {
        Vector3f loc = new Vector3f(1, 2, 3);
        character.addComponent(new LocationComponent(loc));
        esm.waitForCompletionOfPreviousSaveAndStartSaving();
        esm.finishSavingAndShutdown();
        PlayerStore restored = esm.loadPlayerStore(StorageManagerTest.PLAYER_ID);
        Assert.assertEquals(loc, restored.getRelevanceLocation());
    }

    @Test
    public void testCharacterSurvivesStorage() {
        esm.waitForCompletionOfPreviousSaveAndStartSaving();
        esm.finishSavingAndShutdown();
        PlayerStore restored = esm.loadPlayerStore(StorageManagerTest.PLAYER_ID);
        restored.restoreEntities();
        Assert.assertTrue(restored.hasCharacter());
        Assert.assertEquals(character, restored.getCharacter());
    }

    @Test
    public void testGlobalEntitiesStoredAndRestored() throws Exception {
        EntityRef entity = entityManager.create(new StringComponent("Test"));
        long entityId = entity.getId();
        esm.waitForCompletionOfPreviousSaveAndStartSaving();
        esm.finishSavingAndShutdown();
        EntitySystemSetupUtil.addReflectionBasedLibraries(context);
        EntitySystemSetupUtil.addEntityManagementRelatedClasses(context);
        EngineEntityManager newEntityManager = context.get(EngineEntityManager.class);
        StorageManager newSM = new ReadWriteStorageManager(savePath, moduleEnvironment, newEntityManager, blockManager, biomeManager, extraDataManager, false, recordAndReplaySerializer, recordAndReplayUtils, recordAndReplayCurrentStatus);
        newSM.loadGlobalStore();
        List<EntityRef> entities = Lists.newArrayList(newEntityManager.getEntitiesWith(StringComponent.class));
        Assert.assertEquals(1, entities.size());
        Assert.assertEquals(entityId, entities.get(0).getId());
    }

    @Test
    public void testReferenceRemainsValidOverStorageRestoral() throws Exception {
        EntityRef someEntity = entityManager.create();
        character.addComponent(new EntityRefComponent(someEntity));
        esm.waitForCompletionOfPreviousSaveAndStartSaving();
        esm.finishSavingAndShutdown();
        EntitySystemSetupUtil.addReflectionBasedLibraries(context);
        EntitySystemSetupUtil.addEntityManagementRelatedClasses(context);
        EngineEntityManager newEntityManager = context.get(EngineEntityManager.class);
        StorageManager newSM = new ReadWriteStorageManager(savePath, moduleEnvironment, newEntityManager, blockManager, biomeManager, extraDataManager, false, recordAndReplaySerializer, recordAndReplayUtils, recordAndReplayCurrentStatus);
        newSM.loadGlobalStore();
        PlayerStore restored = newSM.loadPlayerStore(StorageManagerTest.PLAYER_ID);
        restored.restoreEntities();
        Assert.assertTrue(restored.getCharacter().getComponent(EntityRefComponent.class).entityRef.exists());
    }

    @Test
    public void testGetUnstoredChunkReturnsNothing() {
        esm.loadChunkStore(StorageManagerTest.CHUNK_POS);
    }

    @Test
    public void testStoreAndRestoreChunkStore() {
        Chunk chunk = new org.terasology.world.chunks.internal.ChunkImpl(StorageManagerTest.CHUNK_POS, blockManager, biomeManager, extraDataManager);
        chunk.setBlock(0, 0, 0, testBlock);
        chunk.markReady();
        ChunkProvider chunkProvider = Mockito.mock(ChunkProvider.class);
        Mockito.when(chunkProvider.getAllChunks()).thenReturn(Arrays.asList(chunk));
        CoreRegistry.put(ChunkProvider.class, chunkProvider);
        esm.waitForCompletionOfPreviousSaveAndStartSaving();
        esm.finishSavingAndShutdown();
        ChunkStore restored = esm.loadChunkStore(StorageManagerTest.CHUNK_POS);
        Assert.assertNotNull(restored);
        Assert.assertEquals(StorageManagerTest.CHUNK_POS, restored.getChunkPosition());
        Assert.assertNotNull(restored.getChunk());
        Assert.assertEquals(testBlock, restored.getChunk().getBlock(0, 0, 0));
    }

    @Test
    public void testChunkSurvivesStorageSaveAndRestore() throws Exception {
        Chunk chunk = new org.terasology.world.chunks.internal.ChunkImpl(StorageManagerTest.CHUNK_POS, blockManager, biomeManager, extraDataManager);
        chunk.setBlock(0, 0, 0, testBlock);
        chunk.setBlock(0, 4, 2, testBlock2);
        chunk.markReady();
        ChunkProvider chunkProvider = Mockito.mock(ChunkProvider.class);
        Mockito.when(chunkProvider.getAllChunks()).thenReturn(Arrays.asList(chunk));
        Mockito.when(chunkProvider.getChunk(Matchers.any(Vector3i.class))).thenReturn(chunk);
        CoreRegistry.put(ChunkProvider.class, chunkProvider);
        boolean storeChunkInZips = true;
        esm.setStoreChunksInZips(storeChunkInZips);
        esm.waitForCompletionOfPreviousSaveAndStartSaving();
        esm.finishSavingAndShutdown();
        EntitySystemSetupUtil.addReflectionBasedLibraries(context);
        EntitySystemSetupUtil.addEntityManagementRelatedClasses(context);
        EngineEntityManager newEntityManager = context.get(EngineEntityManager.class);
        StorageManager newSM = new ReadWriteStorageManager(savePath, moduleEnvironment, newEntityManager, blockManager, biomeManager, extraDataManager, storeChunkInZips, recordAndReplaySerializer, recordAndReplayUtils, recordAndReplayCurrentStatus);
        newSM.loadGlobalStore();
        ChunkStore restored = newSM.loadChunkStore(StorageManagerTest.CHUNK_POS);
        Assert.assertNotNull(restored);
        Assert.assertEquals(StorageManagerTest.CHUNK_POS, restored.getChunkPosition());
        Assert.assertNotNull(restored.getChunk());
        Assert.assertEquals(testBlock, restored.getChunk().getBlock(0, 0, 0));
        Assert.assertEquals(testBlock2, restored.getChunk().getBlock(0, 4, 2));
    }

    @Test
    public void testEntitySurvivesStorageInChunkStore() throws Exception {
        Chunk chunk = new org.terasology.world.chunks.internal.ChunkImpl(StorageManagerTest.CHUNK_POS, blockManager, biomeManager, extraDataManager);
        chunk.setBlock(0, 0, 0, testBlock);
        chunk.markReady();
        ChunkProvider chunkProvider = Mockito.mock(ChunkProvider.class);
        Mockito.when(chunkProvider.getAllChunks()).thenReturn(Arrays.asList(chunk));
        CoreRegistry.put(ChunkProvider.class, chunkProvider);
        EntityRef entity = entityManager.create();
        long id = entity.getId();
        LocationComponent locationComponent = new LocationComponent();
        Vector3f positionInChunk = new Vector3f(chunk.getAABB().getMin());
        positionInChunk.x += 1;
        positionInChunk.y += 1;
        positionInChunk.z += 1;
        locationComponent.setWorldPosition(positionInChunk);
        entity.addComponent(locationComponent);
        esm.waitForCompletionOfPreviousSaveAndStartSaving();
        esm.finishSavingAndShutdown();
        EntitySystemSetupUtil.addReflectionBasedLibraries(context);
        EntitySystemSetupUtil.addEntityManagementRelatedClasses(context);
        EngineEntityManager newEntityManager = context.get(EngineEntityManager.class);
        StorageManager newSM = new ReadWriteStorageManager(savePath, moduleEnvironment, newEntityManager, blockManager, biomeManager, extraDataManager, false, recordAndReplaySerializer, recordAndReplayUtils, recordAndReplayCurrentStatus);
        newSM.loadGlobalStore();
        ChunkStore restored = newSM.loadChunkStore(StorageManagerTest.CHUNK_POS);
        restored.restoreEntities();
        EntityRef ref = newEntityManager.getEntity(id);
        Assert.assertTrue(ref.exists());
        Assert.assertTrue(ref.isActive());
    }

    @Test
    public void testCanSavePlayerWithoutUnloading() throws Exception {
        esm.waitForCompletionOfPreviousSaveAndStartSaving();
        esm.finishSavingAndShutdown();
        Assert.assertTrue(character.isActive());
    }
}

