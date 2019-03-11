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


import EndCause.COMPLETED;
import com.liulishuo.okdownload.DownloadTask;
import java.io.File;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class BreakpointStoreOnSqliteTest {
    private BreakpointStoreOnSQLite store;

    private BreakpointSQLiteHelper helper;

    private BreakpointStoreOnCache onCache;

    private static int index = 0;

    @Test
    public void get_createAndInsert_onSyncToFilesystemSuccess_update() throws IOException {
        final int id1 = store.findOrCreateId(BreakpointStoreOnSqliteTest.mockTask());
        final int id2 = store.findOrCreateId(BreakpointStoreOnSqliteTest.mockTask());
        assertThat(id1).isNotEqualTo(id2);
        Mockito.verify(onCache, Mockito.times(2)).findOrCreateId(ArgumentMatchers.any(DownloadTask.class));
        final DownloadTask task = Mockito.mock(DownloadTask.class);
        Mockito.when(task.getId()).thenReturn(id2);
        Mockito.when(task.getUrl()).thenReturn("url");
        Mockito.when(task.getParentFile()).thenReturn(new File("p-path"));
        Mockito.doReturn("filename").when(task).getFilename();
        store.createAndInsert(task);
        final BreakpointInfo info2 = onCache.get(id2);
        assertThat(info2).isNotNull();
        Mockito.verify(helper).insert(info2);
        info2.addBlock(new BlockInfo(0, 20, 5));
        store.onSyncToFilesystemSuccess(info2, 0, 10);
        Mockito.verify(onCache).onSyncToFilesystemSuccess(info2, 0, 10);
        Mockito.verify(helper).updateBlockIncrease(info2, 0, 15);
        info2.setEtag("new-etag");
        store.update(info2);
        Mockito.verify(onCache).update(info2);
        Mockito.verify(helper).updateInfo(info2);
    }

    @Test
    public void update_updateFilename() throws IOException {
        final BreakpointInfo info = Mockito.mock(BreakpointInfo.class);
        Mockito.when(info.isTaskOnlyProvidedParentPath()).thenReturn(false);
        Mockito.when(info.getUrl()).thenReturn("url");
        Mockito.when(info.getFilename()).thenReturn("filename");
        store.update(info);
        Mockito.verify(helper, Mockito.never()).updateFilename(ArgumentMatchers.eq("url"), ArgumentMatchers.eq("filename"));
        Mockito.when(info.isTaskOnlyProvidedParentPath()).thenReturn(true);
        store.update(info);
        Mockito.verify(helper).updateFilename(ArgumentMatchers.eq("url"), ArgumentMatchers.eq("filename"));
    }

    @Test
    public void completeDownload() {
        final int id = store.findOrCreateId(Mockito.mock(DownloadTask.class));
        store.onTaskEnd(id, COMPLETED, null);
        Mockito.verify(onCache).onTaskEnd(ArgumentMatchers.eq(id), ArgumentMatchers.eq(COMPLETED), ArgumentMatchers.nullable(Exception.class));
        Mockito.verify(helper).removeInfo(id);
    }

    @Test
    public void discard() {
        final int id = store.findOrCreateId(Mockito.mock(DownloadTask.class));
        store.remove(id);
        Mockito.verify(onCache).remove(id);
        Mockito.verify(helper).removeInfo(id);
    }

    @Test
    public void getAfterCompleted() {
        assertThat(store.getAfterCompleted(1)).isNull();
    }

    @Test
    public void markFileDirty() {
        Mockito.doReturn(false).when(onCache).markFileDirty(1);
        assertThat(store.markFileDirty(1)).isFalse();
        Mockito.verify(helper, Mockito.never()).markFileDirty(ArgumentMatchers.eq(1));
        Mockito.doReturn(true).when(onCache).markFileDirty(1);
        assertThat(store.markFileDirty(1)).isTrue();
        Mockito.verify(helper).markFileDirty(ArgumentMatchers.eq(1));
    }

    @Test
    public void markFileClear() {
        Mockito.doReturn(false).when(onCache).markFileClear(1);
        assertThat(store.markFileClear(1)).isFalse();
        Mockito.verify(helper, Mockito.never()).markFileClear(ArgumentMatchers.eq(1));
        Mockito.doReturn(true).when(onCache).markFileClear(1);
        assertThat(store.markFileClear(1)).isTrue();
        Mockito.verify(helper).markFileClear(ArgumentMatchers.eq(1));
    }

    @Test
    public void isFileDirty() {
        Mockito.doReturn(true).when(onCache).isFileDirty(1);
        assertThat(store.isFileDirty(1)).isTrue();
        Mockito.doReturn(false).when(onCache).isFileDirty(1);
        assertThat(store.isFileDirty(1)).isFalse();
    }
}

