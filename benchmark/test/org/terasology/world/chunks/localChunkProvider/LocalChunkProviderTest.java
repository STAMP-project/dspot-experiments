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
package org.terasology.world.chunks.localChunkProvider;


import gnu.trove.list.TIntList;
import gnu.trove.map.hash.TShortObjectHashMap;
import java.util.Collections;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.EntityStore;
import org.terasology.entitySystem.event.Event;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.math.geom.Vector3i;
import org.terasology.persistence.ChunkStore;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.OnActivatedBlocks;
import org.terasology.world.block.OnAddedBlocks;
import org.terasology.world.chunks.Chunk;
import org.terasology.world.chunks.blockdata.ExtraBlockDataManager;
import org.terasology.world.chunks.event.OnChunkGenerated;
import org.terasology.world.chunks.event.OnChunkLoaded;
import org.terasology.world.chunks.internal.ReadyChunkInfo;


public class LocalChunkProviderTest {
    private LocalChunkProvider chunkProvider;

    private ChunkFinalizer chunkFinalizer;

    private EntityManager entityManager;

    private BlockManager blockManager;

    private ExtraBlockDataManager extraDataManager;

    private BlockEntityRegistry blockEntityRegistry;

    private EntityRef worldEntity;

    private ChunkCache chunkCache;

    @Test
    public void testCompleteUpdateMarksChunkReady() throws Exception {
        final Chunk chunk = LocalChunkProviderTest.mockChunkAt(0, 0, 0);
        final ReadyChunkInfo readyChunkInfo = ReadyChunkInfo.createForNewChunk(chunk, new TShortObjectHashMap(), Collections.emptyList());
        Mockito.when(chunkFinalizer.completeFinalization()).thenReturn(readyChunkInfo);
        chunkProvider.completeUpdate();
        Mockito.verify(chunk).markReady();
    }

    @Test
    public void testCompleteUpdateHandlesFinalizedChunkIfReady() throws Exception {
        final Chunk chunk = LocalChunkProviderTest.mockChunkAt(0, 0, 0);
        final ReadyChunkInfo readyChunkInfo = ReadyChunkInfo.createForNewChunk(chunk, new TShortObjectHashMap(), Collections.emptyList());
        Mockito.when(chunkFinalizer.completeFinalization()).thenReturn(readyChunkInfo);
        chunkProvider.completeUpdate();
        final InOrder inOrderVerification = Mockito.inOrder(worldEntity);
        inOrderVerification.verify(worldEntity).send(ArgumentMatchers.any(OnChunkGenerated.class));
        inOrderVerification.verify(worldEntity).send(ArgumentMatchers.any(OnChunkLoaded.class));
    }

    @Test
    public void testCompleteUpdateGeneratesStoredEntities() throws Exception {
        final Chunk chunk = LocalChunkProviderTest.mockChunkAt(0, 0, 0);
        final LocalChunkProviderTest.ChunkProviderTestComponent testComponent = new LocalChunkProviderTest.ChunkProviderTestComponent();
        final EntityStore entityStore = LocalChunkProviderTest.createEntityStoreWithComponents(testComponent);
        final List<EntityStore> entityStores = Collections.singletonList(entityStore);
        final ReadyChunkInfo readyChunkInfo = ReadyChunkInfo.createForNewChunk(chunk, new TShortObjectHashMap(), entityStores);
        Mockito.when(chunkFinalizer.completeFinalization()).thenReturn(readyChunkInfo);
        final EntityRef mockEntity = Mockito.mock(EntityRef.class);
        Mockito.when(entityManager.create()).thenReturn(mockEntity);
        chunkProvider.completeUpdate();
        Mockito.verify(mockEntity).addComponent(ArgumentMatchers.eq(testComponent));
    }

    @Test
    public void testCompleteUpdateGeneratesStoredEntitiesFromPrefab() throws Exception {
        final Chunk chunk = LocalChunkProviderTest.mockChunkAt(0, 0, 0);
        final Prefab prefab = Mockito.mock(Prefab.class);
        final LocalChunkProviderTest.ChunkProviderTestComponent testComponent = new LocalChunkProviderTest.ChunkProviderTestComponent();
        final EntityStore entityStore = LocalChunkProviderTest.createEntityStoreWithPrefabAndComponents(prefab, testComponent);
        final List<EntityStore> entityStores = Collections.singletonList(entityStore);
        final ReadyChunkInfo readyChunkInfo = ReadyChunkInfo.createForNewChunk(chunk, new TShortObjectHashMap(), entityStores);
        Mockito.when(chunkFinalizer.completeFinalization()).thenReturn(readyChunkInfo);
        final EntityRef mockEntity = Mockito.mock(EntityRef.class);
        Mockito.when(entityManager.create(ArgumentMatchers.any(Prefab.class))).thenReturn(mockEntity);
        chunkProvider.completeUpdate();
        Mockito.verify(entityManager).create(ArgumentMatchers.eq(prefab));
        Mockito.verify(mockEntity).addComponent(ArgumentMatchers.eq(testComponent));
    }

    @Test
    public void testCompleteUpdateRestoresEntitiesForRestoredChunks() throws Exception {
        final Chunk chunk = LocalChunkProviderTest.mockChunkAt(0, 0, 0);
        final ChunkStore chunkStore = Mockito.mock(ChunkStore.class);
        final ReadyChunkInfo readyChunkInfo = ReadyChunkInfo.createForRestoredChunk(chunk, new TShortObjectHashMap(), chunkStore, Collections.emptyList());
        Mockito.when(chunkFinalizer.completeFinalization()).thenReturn(readyChunkInfo);
        chunkProvider.completeUpdate();
        Mockito.verify(chunkStore).restoreEntities();
    }

    @Test
    public void testCompleteUpdateSendsBlockAddedEvents() throws Exception {
        final Chunk chunk = LocalChunkProviderTest.mockChunkAt(0, 0, 0);
        final short blockId = 42;
        final EntityRef blockEntity = Mockito.mock(EntityRef.class);
        LocalChunkProviderTest.registerBlockWithIdAndEntity(blockId, blockEntity, blockManager);
        final TShortObjectHashMap<TIntList> blockPositionMappings = new TShortObjectHashMap<>();
        blockPositionMappings.put(blockId, LocalChunkProviderTest.withPositions(new Vector3i(1, 2, 3)));
        final ReadyChunkInfo readyChunkInfo = ReadyChunkInfo.createForRestoredChunk(chunk, blockPositionMappings, Mockito.mock(ChunkStore.class), Collections.emptyList());
        Mockito.when(chunkFinalizer.completeFinalization()).thenReturn(readyChunkInfo);
        chunkProvider.completeUpdate();
        final ArgumentCaptor<Event> eventArgumentCaptor = ArgumentCaptor.forClass(Event.class);
        Mockito.verify(blockEntity, Mockito.atLeastOnce()).send(eventArgumentCaptor.capture());
        final Event event = eventArgumentCaptor.getAllValues().get(0);
        MatcherAssert.assertThat(event, CoreMatchers.instanceOf(OnAddedBlocks.class));
        MatcherAssert.assertThat(getBlockPositions(), CoreMatchers.hasItem(new Vector3i(1, 2, 3)));
    }

    @Test
    public void testCompleteUpdateSendsBlockActivatedEvents() throws Exception {
        final Chunk chunk = LocalChunkProviderTest.mockChunkAt(0, 0, 0);
        final TShortObjectHashMap<TIntList> blockPositionMappings = new TShortObjectHashMap<>();
        final short blockId = 42;
        final EntityRef blockEntity = Mockito.mock(EntityRef.class);
        LocalChunkProviderTest.registerBlockWithIdAndEntity(blockId, blockEntity, blockManager);
        blockPositionMappings.put(blockId, LocalChunkProviderTest.withPositions(new Vector3i(1, 2, 3)));
        final ReadyChunkInfo readyChunkInfo = ReadyChunkInfo.createForRestoredChunk(chunk, blockPositionMappings, Mockito.mock(ChunkStore.class), Collections.emptyList());
        Mockito.when(chunkFinalizer.completeFinalization()).thenReturn(readyChunkInfo);
        chunkProvider.completeUpdate();
        final ArgumentCaptor<Event> eventArgumentCaptor = ArgumentCaptor.forClass(Event.class);
        Mockito.verify(blockEntity, Mockito.atLeastOnce()).send(eventArgumentCaptor.capture());
        final Event event = eventArgumentCaptor.getAllValues().get(1);
        MatcherAssert.assertThat(event, CoreMatchers.instanceOf(OnActivatedBlocks.class));
        MatcherAssert.assertThat(getBlockPositions(), CoreMatchers.hasItem(new Vector3i(1, 2, 3)));
    }

    private static class ChunkProviderTestComponent implements Component {}
}

