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


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class BreakpointSQLiteHelperTest {
    private BreakpointSQLiteHelper helper;

    @Mock
    private SQLiteDatabase db;

    private BreakpointInfo insertedInfo1;

    private BreakpointInfo insertedInfo2;

    @Test
    public void onCreate() {
        helper.onCreate(db);
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(db, Mockito.times(4)).execSQL(captor.capture());
        List<String> values = captor.getAllValues();
        assertThat(values.size()).isEqualTo(4);
        assertThat(values.get(3)).isEqualTo((((("CREATE TABLE IF NOT EXISTS " + (BreakpointSQLiteHelper.TASK_FILE_DIRTY_TABLE_NAME)) + "( ") + (BreakpointSQLiteKey.ID)) + " INTEGER PRIMARY KEY)"));
    }

    @Test
    public void onUpgrade() {
        helper.onUpgrade(db, 2, 3);
        Mockito.verify(db).execSQL((((("CREATE TABLE IF NOT EXISTS " + (BreakpointSQLiteHelper.TASK_FILE_DIRTY_TABLE_NAME)) + "( ") + (BreakpointSQLiteKey.ID)) + " INTEGER PRIMARY KEY)"));
    }

    @Test
    public void markFileDirty() {
        Mockito.doReturn(db).when(helper).getWritableDatabase();
        helper.markFileDirty(1);
        ArgumentCaptor<ContentValues> captor = ArgumentCaptor.forClass(ContentValues.class);
        Mockito.verify(db).insert(ArgumentMatchers.eq(BreakpointSQLiteHelper.TASK_FILE_DIRTY_TABLE_NAME), ArgumentMatchers.nullable(String.class), captor.capture());
        assertThat(captor.getValue().get(BreakpointSQLiteKey.ID)).isEqualTo(1);
    }

    @Test
    public void markFileClear() {
        Mockito.doReturn(db).when(helper).getWritableDatabase();
        helper.markFileClear(1);
        ArgumentCaptor<String[]> captor = ArgumentCaptor.forClass(String[].class);
        Mockito.verify(db).delete(ArgumentMatchers.eq(BreakpointSQLiteHelper.TASK_FILE_DIRTY_TABLE_NAME), ArgumentMatchers.eq(((BreakpointSQLiteKey.ID) + " = ?")), captor.capture());
        assertThat(captor.getValue()[0]).isEqualTo("1");
    }

    @Test
    public void loadDirtyFileList() {
        Mockito.doReturn(db).when(helper).getWritableDatabase();
        Cursor cursor = Mockito.mock(Cursor.class);
        Mockito.doReturn(cursor).when(db).rawQuery(("SELECT * FROM " + (BreakpointSQLiteHelper.TASK_FILE_DIRTY_TABLE_NAME)), null);
        Mockito.doReturn(true, true, false).when(cursor).moveToNext();
        Mockito.doReturn(1, 2).when(cursor).getInt(ArgumentMatchers.anyInt());
        assertThat(helper.loadDirtyFileList()).containsExactly(1, 2);
    }

    @Test
    public void loadToCacheAndInsert() {
        final SparseArray<BreakpointInfo> infoSparseArray = helper.loadToCache();
        assertThat(infoSparseArray.size()).isEqualTo(2);
        final BreakpointInfo info1 = infoSparseArray.get(insertedInfo1.id);
        assertThat(info1.getBlockCount()).isEqualTo(1);
        assertThat(info1.getUrl()).isEqualTo("url1");
        assertThat(info1.parentFile.getAbsolutePath()).isEqualTo(new File("p-path1").getAbsolutePath());
        assertThat(info1.getFilename()).isNull();
        final BreakpointInfo info2 = infoSparseArray.get(insertedInfo2.id);
        assertThat(info2.getBlockCount()).isEqualTo(2);
        assertThat(info2.getUrl()).isEqualTo("url2");
        assertThat(info2.parentFile.getAbsolutePath()).isEqualTo(new File("p-path2").getAbsolutePath());
        assertThat(info2.getFilename()).isEqualTo("filename2");
    }

    @Test
    public void loadResponseFilenameToMap_updateFilename() {
        final String url1 = "url1";
        final String filename1 = "filename1";
        final String url2 = "url2";
        final String filename2 = "filename2";
        helper.updateFilename(url1, filename1);
        helper.updateFilename(url2, filename2);
        final HashMap<String, String> urlFilenameMap = helper.loadResponseFilenameToMap();
        assertThat(urlFilenameMap).containsEntry(url1, filename1);
        assertThat(urlFilenameMap).containsEntry(url2, filename2);
    }

    @Test
    public void updateBlockIncrease() {
        assertThat(insertedInfo2.getBlock(1).getCurrentOffset()).isEqualTo(10);
        helper.updateBlockIncrease(insertedInfo2, 1, 15);
        BreakpointInfo info2 = helper.loadToCache().get(insertedInfo2.id);
        assertThat(info2.getBlock(1).getCurrentOffset()).isEqualTo(15);
    }

    @Test
    public void updateInfo() throws IOException {
        BreakpointInfo info1 = helper.loadToCache().get(insertedInfo1.id);
        assertThat(info1.getEtag()).isNull();
        info1.setEtag("new-etag");
        helper.updateInfo(info1);
        info1 = helper.loadToCache().get(info1.id);
        assertThat(info1.getEtag()).isEqualTo("new-etag");
    }

    @Test
    public void removeInfo() {
        helper = Mockito.spy(helper);
        helper.removeInfo(insertedInfo2.id);
        final SparseArray<BreakpointInfo> infoSparseArray = helper.loadToCache();
        assertThat(infoSparseArray.size()).isEqualTo(1);
        assertThat(infoSparseArray.get(infoSparseArray.keyAt(0)).id).isEqualTo(insertedInfo1.id);
        Mockito.verify(helper).removeBlock(insertedInfo2.id);
    }

    @Test
    public void removeBlock() {
        assertThat(insertedInfo2.getBlockCount()).isEqualTo(2);
        helper.removeBlock(insertedInfo2.id);
        assertThat(helper.loadToCache().get(insertedInfo2.id).getBlockCount()).isZero();
    }
}

