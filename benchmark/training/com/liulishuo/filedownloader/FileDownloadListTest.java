/**
 * Copyright (c) 2018 LingoChamp Inc.
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
package com.liulishuo.filedownloader;


import BaseDownloadTask.IRunningTask;
import com.liulishuo.okdownload.core.dispatcher.DownloadDispatcher;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class FileDownloadListTest {
    @Mock
    private FileDownloadListener listener;

    @Test
    public void get() {
        BaseDownloadTask.IRunningTask task = FileDownloadList.getImpl().get(0);
        Assert.assertNull(task);
        final DownloadTaskAdapter firstAddedTask = FileDownloadList.getImpl().list.get(0);
        firstAddedTask.assembleDownloadTask();
        final DownloadDispatcher downloadDispatcher = com.liulishuo.okdownload.OkDownload.with().downloadDispatcher();
        Mockito.when(downloadDispatcher.isRunning(firstAddedTask.getDownloadTask())).thenReturn(false);
        task = FileDownloadList.getImpl().get(firstAddedTask.getId());
        Assert.assertNull(task);
        final DownloadTaskAdapter secondAddedTask = FileDownloadList.getImpl().list.get(1);
        Mockito.when(downloadDispatcher.isRunning(secondAddedTask.getDownloadTask())).thenReturn(true);
        task = FileDownloadList.getImpl().get(secondAddedTask.getId());
        assertThat(task).isEqualTo(secondAddedTask);
    }

    @Test
    public void addQueueTask() {
        final DownloadTaskAdapter addedTask = FileDownloadList.getImpl().list.get(0);
        final int oldSize = FileDownloadList.getImpl().list.size();
        FileDownloadList.getImpl().addQueueTask(addedTask);
        assertThat(FileDownloadList.getImpl().list).hasSize(oldSize);
        final DownloadTaskAdapter newTask = Mockito.spy(FileDownloader.getImpl().create("url"));
        Mockito.doNothing().when(newTask).assembleDownloadTask();
        FileDownloadList.getImpl().addQueueTask(newTask);
        assertThat(FileDownloadList.getImpl().list).hasSize((oldSize + 1));
        Mockito.verify(newTask).markAdded2List();
        Mockito.verify(newTask).assembleDownloadTask();
    }

    @Test
    public void addIndependentTask() {
        final int oldSize = FileDownloadList.getImpl().list.size();
        final DownloadTaskAdapter mockIndependentTask = Mockito.spy(FileDownloader.getImpl().create("url"));
        FileDownloadList.getImpl().addIndependentTask(mockIndependentTask);
        assertThat(FileDownloadList.getImpl().list).hasSize((oldSize + 1));
        Mockito.verify(mockIndependentTask).setAttachKeyDefault();
        Mockito.verify(mockIndependentTask).markAdded2List();
        FileDownloadList.getImpl().addIndependentTask(mockIndependentTask);
        assertThat(FileDownloadList.getImpl().list).hasSize((oldSize + 1));
    }

    @Test
    public void assembleTasksToStart() {
        List<DownloadTaskAdapter> tasks = FileDownloadList.getImpl().assembleTasksToStart(listener);
        assertThat(tasks).hasSize(FileDownloadList.getImpl().list.size());
        for (DownloadTaskAdapter task : tasks) {
            Mockito.verify(task).setAttachKeyByQueue(listener.hashCode());
        }
        tasks = FileDownloadList.getImpl().assembleTasksToStart(Mockito.mock(FileDownloadListener.class));
        assertThat(tasks).hasSize(0);
        for (DownloadTaskAdapter downloadTaskAdapter : FileDownloadList.getImpl().list) {
            Mockito.when(downloadTaskAdapter.isAttached()).thenReturn(true);
        }
        tasks = FileDownloadList.getImpl().assembleTasksToStart(listener);
        assertThat(tasks).hasSize(0);
    }

    @Test
    public void getByFileDownloadListener() {
        final FileDownloadListener mockNewListener = Mockito.mock(FileDownloadListener.class);
        final int oldSize = FileDownloadList.getImpl().list.size();
        List<DownloadTaskAdapter> result = FileDownloadList.getImpl().getByFileDownloadListener(mockNewListener);
        assertThat(result).isEmpty();
        result = FileDownloadList.getImpl().getByFileDownloadListener(listener);
        assertThat(result).hasSize(oldSize);
    }

    @Test
    public void remove_willRunningTask() {
        assertThat(FileDownloadList.getImpl().remove(null, null)).isFalse();
        final DownloadTaskAdapter downloadTaskAdapter = FileDownloadList.getImpl().list.get(0);
        assertThat(FileDownloadList.getImpl().remove(downloadTaskAdapter)).isTrue();
    }
}

