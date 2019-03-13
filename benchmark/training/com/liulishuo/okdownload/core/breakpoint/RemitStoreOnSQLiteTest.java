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
import Util.Logger;
import android.database.sqlite.SQLiteDatabase;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.OkDownload;
import com.liulishuo.okdownload.core.Util;
import java.io.IOException;
import java.util.ArrayList;
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


@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class RemitStoreOnSQLiteTest {
    private RemitStoreOnSQLite store;

    @Mock
    private BreakpointSQLiteHelper helper;

    @Mock
    private RemitSyncToDBHelper remitHelper;

    @Mock
    private BreakpointStoreOnSQLite storeOnSQLite;

    private BreakpointStoreOnCache onCache;

    @Test
    public void createAndInsert_notFreeToDatabase() throws IOException {
        final DownloadTask task = Mockito.mock(DownloadTask.class);
        Mockito.when(task.getId()).thenReturn(1);
        Mockito.when(remitHelper.isNotFreeToDatabase(1)).thenReturn(true);
        store.createAndInsert(task);
        Mockito.verify(onCache).createAndInsert(ArgumentMatchers.eq(task));
        Mockito.verify(helper, Mockito.never()).insert(ArgumentMatchers.any(BreakpointInfo.class));
    }

    @Test
    public void createAndInsert_freeToDatabase() throws IOException {
        final DownloadTask task = Mockito.mock(DownloadTask.class);
        Mockito.when(task.getId()).thenReturn(1);
        Mockito.when(remitHelper.isNotFreeToDatabase(1)).thenReturn(false);
        store.createAndInsert(task);
        Mockito.verify(onCache, Mockito.never()).createAndInsert(ArgumentMatchers.eq(task));
        Mockito.verify(storeOnSQLite).createAndInsert(ArgumentMatchers.eq(task));
    }

    @Test
    public void onTaskStart() {
        store.onTaskStart(1);
        Mockito.verify(remitHelper).onTaskStart(ArgumentMatchers.eq(1));
    }

    @Test
    public void onSyncToFilesystemSuccess_notFreeToDatabase() throws IOException {
        final BreakpointInfo info = Mockito.mock(BreakpointInfo.class);
        Mockito.when(info.getId()).thenReturn(1);
        Mockito.when(info.getBlock(0)).thenReturn(Mockito.mock(BlockInfo.class));
        Mockito.when(remitHelper.isNotFreeToDatabase(1)).thenReturn(true);
        Mockito.doNothing().when(onCache).onSyncToFilesystemSuccess(info, 0, 10);
        store.onSyncToFilesystemSuccess(info, 0, 10);
        Mockito.verify(onCache).onSyncToFilesystemSuccess(ArgumentMatchers.eq(info), ArgumentMatchers.eq(0), ArgumentMatchers.eq(10L));
        Mockito.verify(helper, Mockito.never()).updateBlockIncrease(ArgumentMatchers.eq(info), ArgumentMatchers.eq(0), ArgumentMatchers.anyLong());
    }

    @Test
    public void onSyncToFilesystemSuccess_freeToDatabase() throws IOException {
        final BreakpointInfo info = Mockito.mock(BreakpointInfo.class);
        Mockito.when(info.getId()).thenReturn(1);
        Mockito.when(info.getBlock(0)).thenReturn(Mockito.mock(BlockInfo.class));
        Mockito.when(remitHelper.isNotFreeToDatabase(1)).thenReturn(false);
        Mockito.doNothing().when(storeOnSQLite).onSyncToFilesystemSuccess(info, 0, 10);
        store.onSyncToFilesystemSuccess(info, 0, 10);
        Mockito.verify(onCache, Mockito.never()).onSyncToFilesystemSuccess(ArgumentMatchers.eq(info), ArgumentMatchers.eq(0), ArgumentMatchers.eq(10L));
        Mockito.verify(storeOnSQLite).onSyncToFilesystemSuccess(ArgumentMatchers.eq(info), ArgumentMatchers.eq(0), ArgumentMatchers.eq(10L));
    }

    @Test
    public void update_notFreeToDatabase() throws IOException {
        final BreakpointInfo info = Mockito.mock(BreakpointInfo.class);
        Mockito.when(info.getId()).thenReturn(1);
        Mockito.when(remitHelper.isNotFreeToDatabase(1)).thenReturn(true);
        store.update(info);
        Mockito.verify(onCache).update(ArgumentMatchers.eq(info));
        Mockito.verify(helper, Mockito.never()).updateInfo(ArgumentMatchers.eq(info));
    }

    @Test
    public void update_freeToDatabase() throws IOException {
        final BreakpointInfo info = Mockito.mock(BreakpointInfo.class);
        Mockito.when(info.getId()).thenReturn(1);
        Mockito.when(remitHelper.isNotFreeToDatabase(1)).thenReturn(false);
        store.update(info);
        Mockito.verify(onCache, Mockito.never()).update(ArgumentMatchers.eq(info));
        Mockito.verify(storeOnSQLite).update(ArgumentMatchers.eq(info));
    }

    @Test
    public void onTaskEnd() {
        store.onTaskEnd(1, COMPLETED, null);
        Mockito.verify(onCache).onTaskEnd(ArgumentMatchers.eq(1), ArgumentMatchers.eq(COMPLETED), ArgumentMatchers.nullable(Exception.class));
        Mockito.verify(remitHelper).discard(ArgumentMatchers.eq(1));
    }

    @Test
    public void onTaskEnd_notCompleted() {
        store.onTaskEnd(1, CANCELED, null);
        Mockito.verify(onCache).onTaskEnd(ArgumentMatchers.eq(1), ArgumentMatchers.eq(CANCELED), ArgumentMatchers.nullable(Exception.class));
        Mockito.verify(remitHelper).endAndEnsureToDB(ArgumentMatchers.eq(1));
    }

    @Test
    public void remove() {
        store.remove(1);
        Mockito.verify(onCache).remove(ArgumentMatchers.eq(1));
        Mockito.verify(remitHelper).discard(ArgumentMatchers.eq(1));
    }

    @Test
    public void syncCacheToDB() throws IOException {
        Util.setLogger(Mockito.mock(Logger.class));
        final BreakpointInfo info = Mockito.mock(BreakpointInfo.class);
        Mockito.when(onCache.get(1)).thenReturn(info);
        store.syncCacheToDB(1);
        Mockito.verify(helper).removeInfo(ArgumentMatchers.eq(1));
        Mockito.verify(helper, Mockito.never()).insert(ArgumentMatchers.eq(info));
        Mockito.when(info.getFilename()).thenReturn("filename");
        store.syncCacheToDB(1);
        Mockito.verify(helper, Mockito.never()).insert(ArgumentMatchers.eq(info));
        Mockito.when(info.getTotalOffset()).thenReturn(1L);
        store.syncCacheToDB(1);
        Mockito.verify(helper).insert(ArgumentMatchers.eq(info));
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void setRemitToDBDelayMillis() {
        OkDownload.setSingletonInstance(Mockito.mock(OkDownload.class));
        Mockito.doReturn(Mockito.mock(BreakpointStoreOnCache.class)).when(OkDownload.with()).breakpointStore();
        thrown.expect(IllegalStateException.class);
        RemitStoreOnSQLite.setRemitToDBDelayMillis(1);
        Mockito.doReturn(store).when(OkDownload.with()).breakpointStore();
        RemitStoreOnSQLite.setRemitToDBDelayMillis((-1));
        assertThat(remitHelper.delayMillis).isEqualTo(0);
        RemitStoreOnSQLite.setRemitToDBDelayMillis(1);
        assertThat(remitHelper.delayMillis).isEqualTo(1);
    }

    @Test
    public void syncCacheToDB_list() throws IOException {
        final List<Integer> ids = new ArrayList<>(2);
        ids.add(1);
        ids.add(2);
        final SQLiteDatabase db = Mockito.mock(SQLiteDatabase.class);
        Mockito.when(helper.getWritableDatabase()).thenReturn(db);
        store.syncCacheToDB(ids);
        Mockito.verify(store).syncCacheToDB(ArgumentMatchers.eq(1));
        Mockito.verify(store).syncCacheToDB(ArgumentMatchers.eq(2));
        Mockito.verify(db).beginTransaction();
        Mockito.verify(db).setTransactionSuccessful();
        Mockito.verify(db).endTransaction();
    }

    @Test
    public void removeInfo() {
        store.removeInfo(1);
        Mockito.verify(helper).removeInfo(ArgumentMatchers.eq(1));
    }

    @Test
    public void getAfterCompleted() {
        assertThat(store.getAfterCompleted(1)).isNull();
    }

    @Test
    public void markFileDirty() {
        Mockito.doReturn(true).when(storeOnSQLite).markFileDirty(1);
        assertThat(store.markFileDirty(1)).isTrue();
        Mockito.doReturn(false).when(storeOnSQLite).markFileDirty(1);
        assertThat(store.markFileDirty(1)).isFalse();
    }

    @Test
    public void markFileClear() {
        Mockito.doReturn(true).when(storeOnSQLite).markFileClear(1);
        assertThat(store.markFileClear(1)).isTrue();
        Mockito.doReturn(false).when(storeOnSQLite).markFileClear(1);
        assertThat(store.markFileClear(1)).isFalse();
    }

    @Test
    public void isFileDirty() {
        Mockito.doReturn(true).when(storeOnSQLite).isFileDirty(1);
        assertThat(store.isFileDirty(1)).isTrue();
        Mockito.doReturn(false).when(storeOnSQLite).isFileDirty(1);
        assertThat(store.isFileDirty(1)).isFalse();
    }
}

