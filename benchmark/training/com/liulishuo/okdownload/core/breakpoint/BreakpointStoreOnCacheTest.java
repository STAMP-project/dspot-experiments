/**
 * Copyright (c) 2017 LingoChamp Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.liulishuo.okdownload.core.breakpoint;


import EndCause.CANCELED;
import EndCause.COMPLETED;
import EndCause.ERROR;
import EndCause.FILE_BUSY;
import EndCause.PRE_ALLOCATE_FAILED;
import EndCause.SAME_TASK_BUSY;
import android.util.SparseArray;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.IdentifiedTask;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


// for SparseArray
@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class BreakpointStoreOnCacheTest {
    private BreakpointStoreOnCache storeOnCache;

    private final int insertedId = 6;

    @Mock
    private KeyToIdMap keyToIdMap;

    @Mock
    private BreakpointInfo info;

    @Mock
    private DownloadTask task;

    private SparseArray<BreakpointInfo> storedInfos;

    private SparseArray<IdentifiedTask> unStoredTasks;

    private List<Integer> sortedOccupiedIds;

    @Test
    public void createAndInsert() {
        final DownloadTask task = Mockito.mock(DownloadTask.class);
        Mockito.when(task.getId()).thenReturn(insertedId);
        Mockito.when(task.getParentFile()).thenReturn(new File("/p-path/"));
        Mockito.when(task.getFilename()).thenReturn("filename");
        Mockito.when(task.getUrl()).thenReturn("url");
        storeOnCache.createAndInsert(task);
        final BreakpointInfo info = storeOnCache.get(insertedId);
        assertThat(info).isNotNull();
        assertThat(info.id).isEqualTo(insertedId);
    }

    @Test
    public void onSyncToFilesystemSuccess() throws IOException {
        createAndInsert();
        final BreakpointInfo info = storeOnCache.get(insertedId);
        final BlockInfo blockInfo = Mockito.spy(new BlockInfo(0, 0, 0));
        info.addBlock(blockInfo);
        storeOnCache.onSyncToFilesystemSuccess(info, 0, 1);
        assertThat(blockInfo.getCurrentOffset()).isEqualTo(1);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void onSyncToFilesystemSuccess_infoNotEqual() throws IOException {
        createAndInsert();
        final BreakpointInfo info = storeOnCache.get(insertedId);
        final BlockInfo blockInfo = Mockito.spy(new BlockInfo(0, 0, 0));
        info.addBlock(blockInfo);
        thrown.expect(IOException.class);
        thrown.expectMessage("Info not on store!");
        storeOnCache.onSyncToFilesystemSuccess(Mockito.mock(BreakpointInfo.class), 0, 1);
    }

    @Test
    public void update() {
        createAndInsert();
        BreakpointInfo newOne = new BreakpointInfo(insertedId, "", new File(""), "newOne");
        // replace
        storeOnCache.update(newOne);
        BreakpointInfo onStoreInfo = storeOnCache.get(insertedId);
        assertThat(onStoreInfo.getFilename()).isEqualTo("newOne");
        final BlockInfo blockInfo = Mockito.mock(BlockInfo.class);
        onStoreInfo.addBlock(blockInfo);
        // Not replace.
        storeOnCache.update(onStoreInfo);
        assertThat(storeOnCache.get(insertedId)).isEqualTo(onStoreInfo);
    }

    @Test
    public void unStoredTasks() {
        final SparseArray<IdentifiedTask> unStoredTasks = new SparseArray();
        final SparseArray<BreakpointInfo> storedInfos = new SparseArray();
        storeOnCache = new BreakpointStoreOnCache(storedInfos, new ArrayList<Integer>(), new HashMap<String, String>(), unStoredTasks, new ArrayList<Integer>(), keyToIdMap);
        DownloadTask task = Mockito.mock(DownloadTask.class);
        Mockito.when(keyToIdMap.get(task)).thenReturn(null);
        Mockito.when(task.getId()).thenReturn(insertedId);
        unStoredTasks.put(task.getId(), task);
        Mockito.doReturn(true).when(task).compareIgnoreId(task);
        assertThat(storeOnCache.findOrCreateId(task)).isEqualTo(insertedId);
        storeOnCache.createAndInsert(task);
        assertThat(unStoredTasks.size()).isZero();
        assertThat(storedInfos.valueAt(0).getId()).isEqualTo(insertedId);
    }

    @Test
    public void findAnotherInfoFromCompare() {
        final SparseArray<IdentifiedTask> unStoredTasks = new SparseArray();
        final SparseArray<BreakpointInfo> storedInfos = new SparseArray();
        storeOnCache = new BreakpointStoreOnCache(storedInfos, new ArrayList<Integer>(), new HashMap<String, String>(), unStoredTasks, new ArrayList<Integer>(), keyToIdMap);
        final BreakpointInfo info1 = Mockito.mock(BreakpointInfo.class);
        final BreakpointInfo info2 = Mockito.mock(BreakpointInfo.class);
        final DownloadTask task = Mockito.mock(DownloadTask.class);
        storedInfos.put(insertedId, info1);
        Mockito.doReturn(true).when(info1).isSameFrom(task);
        Mockito.doReturn(false).when(info2).isSameFrom(task);
        BreakpointInfo result = storeOnCache.findAnotherInfoFromCompare(task, info1);
        assertThat(result).isNull();
        result = storeOnCache.findAnotherInfoFromCompare(task, info2);
        assertThat(result).isEqualToComparingFieldByField(info1);
    }

    @Test
    public void allocateId() {
        final List<Integer> sortedOccupiedIds = new ArrayList<>();
        storeOnCache = new BreakpointStoreOnCache(new SparseArray<BreakpointInfo>(), new ArrayList<Integer>(), new HashMap<String, String>(), new SparseArray<IdentifiedTask>(), sortedOccupiedIds, keyToIdMap);
        assertThat(storeOnCache.allocateId()).isEqualTo(1);
        // when
        sortedOccupiedIds.add(3);
        sortedOccupiedIds.add(5);
        sortedOccupiedIds.add(6);
        sortedOccupiedIds.add(7);
        assertThat(storeOnCache.allocateId()).isEqualTo(2);
        assertThat(sortedOccupiedIds).containsExactly(1, 2, 3, 5, 6, 7);
        assertThat(sortedOccupiedIds.get(1)).isEqualTo(2);
        assertThat(storeOnCache.allocateId()).isEqualTo(4);
        assertThat(storeOnCache.allocateId()).isEqualTo(8);
        assertThat(sortedOccupiedIds).containsExactly(1, 2, 3, 4, 5, 6, 7, 8);
        storeOnCache.remove(6);
        assertThat(sortedOccupiedIds).containsExactly(1, 2, 3, 4, 5, 7, 8);
        assertThat(storeOnCache.allocateId()).isEqualTo(6);
        assertThat(sortedOccupiedIds).containsExactly(1, 2, 3, 4, 5, 6, 7, 8);
        storeOnCache.onTaskEnd(1, COMPLETED, null);
        assertThat(sortedOccupiedIds).containsExactly(2, 3, 4, 5, 6, 7, 8);
        assertThat(storeOnCache.allocateId()).isEqualTo(1);
    }

    @Test
    public void urlFileNameMap() {
        final HashMap<String, String> urlFilenameMap = new HashMap<>();
        final String url1 = "url1";
        final String url2 = "url2";
        final String filename1 = "filename1";
        final String filename2 = "filename2";
        // init
        urlFilenameMap.put(url1, filename1);
        storeOnCache = new BreakpointStoreOnCache(new SparseArray<BreakpointInfo>(), new ArrayList<Integer>(), urlFilenameMap, new SparseArray<IdentifiedTask>(), new ArrayList<Integer>(), keyToIdMap);
        assertThat(storeOnCache.getResponseFilename(url1)).isEqualTo(filename1);
        assertThat(storeOnCache.getResponseFilename(url2)).isNull();
        // update
        final BreakpointInfo info = Mockito.mock(BreakpointInfo.class);
        Mockito.when(info.getUrl()).thenReturn(url2);
        Mockito.when(info.isTaskOnlyProvidedParentPath()).thenReturn(true);
        Mockito.doReturn(filename2).when(info).getFilename();
        storeOnCache.update(info);
        assertThat(storeOnCache.getResponseFilename(url2)).isEqualTo(filename2);
        // replace
        Mockito.when(info.getUrl()).thenReturn(url1);
        storeOnCache.update(info);
        assertThat(storeOnCache.getResponseFilename(url1)).isEqualTo(filename2);
    }

    @Test
    public void onTaskEnd_completed() {
        final BreakpointStoreOnCache cache = Mockito.spy(new BreakpointStoreOnCache(new SparseArray<BreakpointInfo>(), new ArrayList<Integer>(), new HashMap<String, String>(), new SparseArray<IdentifiedTask>(), new ArrayList<Integer>(), keyToIdMap));
        Mockito.doNothing().when(cache).remove(1);
        cache.onTaskEnd(1, COMPLETED, null);
        Mockito.verify(cache).remove(ArgumentMatchers.eq(1));
    }

    @Test
    public void onTaskEnd_nonCompleted() {
        final BreakpointStoreOnCache cache = Mockito.spy(new BreakpointStoreOnCache(new SparseArray<BreakpointInfo>(), new ArrayList<Integer>(), new HashMap<String, String>(), new SparseArray<IdentifiedTask>(), new ArrayList<Integer>(), keyToIdMap));
        Mockito.doNothing().when(cache).remove(1);
        cache.onTaskEnd(1, CANCELED, null);
        Mockito.verify(cache, Mockito.never()).remove(ArgumentMatchers.eq(1));
        cache.onTaskEnd(1, ERROR, null);
        Mockito.verify(cache, Mockito.never()).remove(ArgumentMatchers.eq(1));
        cache.onTaskEnd(1, FILE_BUSY, null);
        Mockito.verify(cache, Mockito.never()).remove(ArgumentMatchers.eq(1));
        cache.onTaskEnd(1, PRE_ALLOCATE_FAILED, null);
        Mockito.verify(cache, Mockito.never()).remove(ArgumentMatchers.eq(1));
        cache.onTaskEnd(1, SAME_TASK_BUSY, null);
        Mockito.verify(cache, Mockito.never()).remove(ArgumentMatchers.eq(1));
    }

    @Test
    public void remove() {
        final BreakpointStoreOnCache cache = Mockito.spy(new BreakpointStoreOnCache(storedInfos, new ArrayList<Integer>(), new HashMap<String, String>(), unStoredTasks, sortedOccupiedIds, keyToIdMap));
        storedInfos.put(1, info);
        sortedOccupiedIds.add(1);
        cache.remove(1);
        assertThat(storedInfos.size()).isZero();
        assertThat(sortedOccupiedIds).isEmpty();
        Mockito.verify(keyToIdMap).remove(ArgumentMatchers.eq(1));
    }

    @Test
    public void findOrCreateId() {
        final BreakpointStoreOnCache cache = Mockito.spy(new BreakpointStoreOnCache(storedInfos, new ArrayList<Integer>(), new HashMap<String, String>(), unStoredTasks, sortedOccupiedIds, keyToIdMap));
        Mockito.when(keyToIdMap.get(task)).thenReturn(null);
        Mockito.when(cache.allocateId()).thenReturn(1);
        assertThat(cache.findOrCreateId(task)).isEqualTo(1);
        Mockito.verify(keyToIdMap).add(ArgumentMatchers.eq(task), ArgumentMatchers.eq(1));
        Mockito.when(keyToIdMap.get(task)).thenReturn(2);
        assertThat(cache.findOrCreateId(task)).isEqualTo(2);
        Mockito.verify(keyToIdMap, Mockito.never()).add(ArgumentMatchers.eq(task), ArgumentMatchers.eq(2));
    }

    @Test
    public void getAfterCompleted() {
        assertThat(storeOnCache.getAfterCompleted(1)).isNull();
    }

    @Test
    public void markFileDirty() {
        List<Integer> fileDirtyList = new ArrayList<>();
        final BreakpointStoreOnCache cache = Mockito.spy(new BreakpointStoreOnCache(new SparseArray<BreakpointInfo>(), fileDirtyList, new HashMap<String, String>(), new SparseArray<IdentifiedTask>(), new ArrayList<Integer>(), keyToIdMap));
        assertThat(cache.markFileDirty(1)).isTrue();
        assertThat(fileDirtyList).containsExactly(1);
        assertThat(cache.markFileDirty(1)).isFalse();
        assertThat(cache.markFileDirty(2)).isTrue();
        assertThat(fileDirtyList).containsExactly(1, 2);
    }

    @Test
    public void markFileClear() {
        List<Integer> fileDirtyList = new ArrayList<>();
        fileDirtyList.add(1);
        final BreakpointStoreOnCache cache = Mockito.spy(new BreakpointStoreOnCache(new SparseArray<BreakpointInfo>(), fileDirtyList, new HashMap<String, String>(), new SparseArray<IdentifiedTask>(), new ArrayList<Integer>(), keyToIdMap));
        assertThat(cache.markFileClear(1)).isTrue();
        assertThat(fileDirtyList).isEmpty();
        assertThat(cache.markFileClear(2)).isFalse();
    }

    @Test
    public void isFileDirty() {
        List<Integer> fileDirtyList = new ArrayList<>();
        fileDirtyList.add(1);
        final BreakpointStoreOnCache cache = Mockito.spy(new BreakpointStoreOnCache(new SparseArray<BreakpointInfo>(), fileDirtyList, new HashMap<String, String>(), new SparseArray<IdentifiedTask>(), new ArrayList<Integer>(), keyToIdMap));
        assertThat(cache.isFileDirty(1)).isTrue();
        assertThat(cache.isFileDirty(2)).isFalse();
    }
}

