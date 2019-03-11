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
package com.liulishuo.okdownload;


import StatusUtil.Status;
import StatusUtil.Status.COMPLETED;
import StatusUtil.Status.IDLE;
import StatusUtil.Status.UNKNOWN;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.breakpoint.BreakpointStore;
import com.liulishuo.okdownload.core.dispatcher.DownloadDispatcher;
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
public class StatusUtilTest {
    private File file;

    private String url = "url";

    @Test
    public void getStatus() throws IOException {
        file.getParentFile().mkdirs();
        file.createNewFile();
        assertThat(file.exists()).isTrue();
        StatusUtil.Status status = StatusUtil.getStatus(url, file.getParent(), file.getName());
        assertThat(status).isEqualTo(Status.COMPLETED);
        // no filename
        status = StatusUtil.getStatus(url, file.getParentFile().getPath(), null);
        assertThat(status).isEqualTo(Status.UNKNOWN);
        final DownloadDispatcher dispatcher = OkDownload.with().downloadDispatcher();
        Mockito.doReturn(true).when(dispatcher).isRunning(ArgumentMatchers.any(DownloadTask.class));
        status = StatusUtil.getStatus(url, file.getParentFile().getPath(), null);
        assertThat(status).isEqualTo(Status.RUNNING);
        Mockito.doReturn(true).when(dispatcher).isPending(ArgumentMatchers.any(DownloadTask.class));
        status = StatusUtil.getStatus(url, file.getParentFile().getPath(), null);
        assertThat(status).isEqualTo(Status.PENDING);
    }

    @Test
    public void isCompleted() throws IOException {
        assertThat(file.exists()).isFalse();
        boolean isCompleted = StatusUtil.isCompleted(url, file.getParentFile().getPath(), file.getName());
        assertThat(isCompleted).isFalse();
        file.getParentFile().mkdirs();
        file.createNewFile();
        isCompleted = StatusUtil.isCompleted(url, file.getParentFile().getPath(), file.getName());
        assertThat(isCompleted).isTrue();
        final BreakpointStore store = OkDownload.with().breakpointStore();
        Mockito.doReturn(Mockito.mock(BreakpointInfo.class)).when(store).get(ArgumentMatchers.anyInt());
        isCompleted = StatusUtil.isCompleted(url, file.getParentFile().getPath(), file.getName());
        assertThat(isCompleted).isFalse();
    }

    @Test
    public void isCompletedOrUnknown_infoNotExist() throws IOException {
        final DownloadTask task = Mockito.mock(DownloadTask.class);
        Mockito.when(task.getUrl()).thenReturn("url");
        Mockito.when(task.getParentFile()).thenReturn(file.getParentFile());
        file.getParentFile().mkdirs();
        file.createNewFile();
        final BreakpointStore store = OkDownload.with().breakpointStore();
        // on memory cache ---> unknown
        Mockito.doReturn(true).when(store).isOnlyMemoryCache();
        assertThat(StatusUtil.isCompletedOrUnknown(task)).isEqualTo(UNKNOWN);
        Mockito.doReturn(false).when(store).isOnlyMemoryCache();
        // file dirty ---> unknown
        Mockito.doReturn(true).when(store).isFileDirty(1);
        Mockito.when(task.getId()).thenReturn(1);
        assertThat(StatusUtil.isCompletedOrUnknown(task)).isEqualTo(UNKNOWN);
        Mockito.doReturn(false).when(store).isFileDirty(1);
        // filename is null and can't find ---> unknown
        Mockito.doReturn(null).when(store).getResponseFilename(ArgumentMatchers.anyString());
        assertThat(StatusUtil.isCompletedOrUnknown(task)).isEqualTo(UNKNOWN);
        // filename is null but found on store but not exist ---> unknown
        Mockito.doReturn("no-exist-filename").when(store).getResponseFilename(ArgumentMatchers.anyString());
        assertThat(StatusUtil.isCompletedOrUnknown(task)).isEqualTo(UNKNOWN);
        // filename is null but found on store and exist ---> completed
        Mockito.doReturn(file.getName()).when(store).getResponseFilename(ArgumentMatchers.anyString());
        assertThat(StatusUtil.isCompletedOrUnknown(task)).isEqualTo(COMPLETED);
        // file name not null and exist
        Mockito.when(task.getFilename()).thenReturn(file.getName());
        assertThat(StatusUtil.isCompletedOrUnknown(task)).isEqualTo(COMPLETED);
        // info is only memory cache
        Mockito.when(store.isOnlyMemoryCache()).thenReturn(true);
        assertThat(StatusUtil.isCompletedOrUnknown(task)).isEqualTo(UNKNOWN);
    }

    @Test
    public void isCompletedOrUnknown_infoExist() throws IOException {
        final DownloadTask task = Mockito.mock(DownloadTask.class);
        file.getParentFile().mkdirs();
        file.createNewFile();
        // case of info exist
        final BreakpointStore store = OkDownload.with().breakpointStore();
        final BreakpointInfo info = Mockito.mock(BreakpointInfo.class);
        Mockito.doReturn(info).when(store).get(ArgumentMatchers.anyInt());
        // info exist but no filename ---> unknown
        assertThat(StatusUtil.isCompletedOrUnknown(task)).isEqualTo(UNKNOWN);
        // info exist but filename not the same ---> unknown
        Mockito.when(task.getFilename()).thenReturn("filename");
        assertThat(StatusUtil.isCompletedOrUnknown(task)).isEqualTo(UNKNOWN);
        // info exist and filename is the same but file not exist --> unknown
        Mockito.when(info.getFilename()).thenReturn("filename");
        assertThat(StatusUtil.isCompletedOrUnknown(task)).isEqualTo(UNKNOWN);
        // info exist and filename is null and file from info is exist --> idle
        Mockito.when(info.getFile()).thenReturn(file);
        Mockito.when(task.getParentFile()).thenReturn(file.getParentFile());
        Mockito.when(task.getFilename()).thenReturn(null);
        Mockito.when(info.getTotalLength()).thenReturn(1L);
        assertThat(StatusUtil.isCompletedOrUnknown(task)).isEqualTo(IDLE);
        // info exist and filename is the same but offset not the same to total ---> idle
        Mockito.when(task.getFilename()).thenReturn("filename");
        Mockito.when(task.getFile()).thenReturn(file);
        Mockito.when(info.getFile()).thenReturn(file);
        assertThat(StatusUtil.isCompletedOrUnknown(task)).isEqualTo(IDLE);
        // info exist and filename is the same and offset the same to total ---> completed
        Mockito.when(info.getTotalOffset()).thenReturn(1L);
        assertThat(StatusUtil.isCompletedOrUnknown(task)).isEqualTo(COMPLETED);
        Mockito.when(info.getTotalLength()).thenReturn(0L);
        assertThat(StatusUtil.isCompletedOrUnknown(task)).isEqualTo(UNKNOWN);
    }

    @Test
    public void getCurrentInfo() {
        final BreakpointStore store = OkDownload.with().breakpointStore();
        final BreakpointInfo origin = Mockito.mock(BreakpointInfo.class);
        Mockito.doReturn(origin).when(store).get(ArgumentMatchers.anyInt());
        StatusUtil.getCurrentInfo(Mockito.mock(DownloadTask.class));
        Mockito.verify(origin).copy();
    }

    @Test
    public void createFinder() {
        DownloadTask task = StatusUtil.createFinder(url, file.getParent(), null);
        assertThat(task.getFile()).isNull();
        TestUtils.assertFile(task.getParentFile()).isEqualTo(file.getParentFile());
        task = StatusUtil.createFinder(url, file.getParent(), file.getName());
        TestUtils.assertFile(task.getFile()).isEqualTo(file);
    }

    @Test
    public void isSameTaskPendingOrRunning() throws IOException {
        TestUtils.mockOkDownload();
        final DownloadTask task = Mockito.mock(DownloadTask.class);
        final DownloadDispatcher dispatcher = OkDownload.with().downloadDispatcher();
        Mockito.when(dispatcher.findSameTask(task)).thenReturn(task);
        assertThat(StatusUtil.isSameTaskPendingOrRunning(task)).isTrue();
        Mockito.when(dispatcher.findSameTask(task)).thenReturn(null);
        assertThat(StatusUtil.isSameTaskPendingOrRunning(task)).isFalse();
    }

    @Test
    public void getCurrentInfo_urlParentPathFilename() {
        final BreakpointStore store = OkDownload.with().breakpointStore();
        final BreakpointInfo origin = Mockito.mock(BreakpointInfo.class);
        Mockito.doReturn(origin).when(store).get(ArgumentMatchers.anyInt());
        StatusUtil.getCurrentInfo("https://jacksgong.com", "parentPath", "filename");
        Mockito.verify(origin).copy();
    }
}

