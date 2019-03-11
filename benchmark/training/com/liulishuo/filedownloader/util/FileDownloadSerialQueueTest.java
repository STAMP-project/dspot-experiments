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
package com.liulishuo.filedownloader.util;


import DownloadTaskAdapter.KEY_TASK_ADAPTER;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.CompatListenerAdapter;
import com.liulishuo.filedownloader.DownloadTaskAdapter;
import com.liulishuo.filedownloader.FileDownloadList;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.okdownload.DownloadListener;
import com.liulishuo.okdownload.DownloadSerialQueue;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.UnifiedListenerManager;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class FileDownloadSerialQueueTest {
    private DownloadSerialQueue serialQueue;

    private UnifiedListenerManager listenerManager;

    private DownloadListener hostListener;

    private FileDownloadSerialQueue fileDownloadSerialQueue;

    private FileDownloadList fileDownloadList;

    @Test
    public void constructor() {
        assertThat(fileDownloadSerialQueue.listenerManager).isEqualTo(listenerManager);
        assertThat(fileDownloadSerialQueue.serialQueue).isEqualTo(serialQueue);
        Mockito.verify(serialQueue).setListener(hostListener);
    }

    @Test
    public void enqueue() {
        final DownloadTaskAdapter mockBaseTask = Mockito.spy(FileDownloader.getImpl().create("url"));
        final DownloadTask mockDownloadTask = Mockito.mock(DownloadTask.class);
        final CompatListenerAdapter mockCompatListener = Mockito.mock(CompatListenerAdapter.class);
        final int taskId = 1;
        Mockito.doReturn(taskId).when(mockBaseTask).getId();
        Mockito.doReturn(mockDownloadTask).when(mockBaseTask).getDownloadTask();
        Mockito.doReturn(mockCompatListener).when(mockBaseTask).getCompatListener();
        Mockito.doNothing().when(mockBaseTask).assembleDownloadTask();
        fileDownloadSerialQueue.enqueue(mockBaseTask);
        Mockito.verify(mockBaseTask).assembleDownloadTask();
        Mockito.verify(fileDownloadList).addIndependentTask(mockBaseTask);
        Mockito.verify(listenerManager).addAutoRemoveListenersWhenTaskEnd(taskId);
        Mockito.verify(listenerManager).attachListener(mockDownloadTask, mockCompatListener);
        Mockito.verify(serialQueue).enqueue(mockDownloadTask);
    }

    @Test
    public void pause() {
        fileDownloadSerialQueue.pause();
        Mockito.verify(serialQueue).pause();
    }

    @Test
    public void resume() {
        fileDownloadSerialQueue.resume();
        Mockito.verify(serialQueue).resume();
    }

    @Test
    public void getWorkingTaskId() {
        fileDownloadSerialQueue.getWorkingTaskId();
        Mockito.verify(serialQueue).getWorkingTaskId();
    }

    @Test
    public void getWaitingTaskCount() {
        fileDownloadSerialQueue.getWaitingTaskCount();
        Mockito.verify(serialQueue).getWaitingTaskCount();
    }

    @Test
    public void shutDown() {
        final DownloadTask downloadTask1 = Mockito.mock(DownloadTask.class);
        final DownloadTask downloadTask2 = Mockito.mock(DownloadTask.class);
        final DownloadTask downloadTask3 = Mockito.mock(DownloadTask.class);
        final DownloadTaskAdapter downloadTaskAdapter1 = Mockito.mock(DownloadTaskAdapter.class);
        final DownloadTaskAdapter downloadTaskAdapter2 = Mockito.mock(DownloadTaskAdapter.class);
        final DownloadTaskAdapter downloadTaskAdapter3 = Mockito.mock(DownloadTaskAdapter.class);
        Mockito.when(downloadTask1.getTag(KEY_TASK_ADAPTER)).thenReturn(downloadTaskAdapter1);
        Mockito.when(downloadTask2.getTag(KEY_TASK_ADAPTER)).thenReturn(downloadTaskAdapter2);
        Mockito.when(downloadTask3.getTag(KEY_TASK_ADAPTER)).thenReturn(downloadTaskAdapter3);
        final DownloadTask[] downloadTasks = new DownloadTask[]{ downloadTask1, downloadTask2, downloadTask3 };
        Mockito.when(serialQueue.shutdown()).thenReturn(downloadTasks);
        final List<BaseDownloadTask> result = fileDownloadSerialQueue.shutdown();
        assertThat(result).hasSize(downloadTasks.length);
        Mockito.verify(fileDownloadList).remove(downloadTaskAdapter1);
        Mockito.verify(fileDownloadList).remove(downloadTaskAdapter2);
        Mockito.verify(fileDownloadList).remove(downloadTaskAdapter3);
    }
}

