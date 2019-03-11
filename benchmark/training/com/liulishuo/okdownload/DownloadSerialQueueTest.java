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


import DownloadSerialQueue.ID_INVALID;
import EndCause.COMPLETED;
import java.util.ArrayList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class DownloadSerialQueueTest {
    private DownloadSerialQueue serialQueue;

    private ArrayList<DownloadTask> taskList;

    @Mock
    private DownloadListener listener;

    @Mock
    private DownloadTask task1;

    @Mock
    private DownloadTask task2;

    @Test
    public void setListener() {
        assertThat(serialQueue.listenerBunch.contain(listener)).isTrue();
        final DownloadListener anotherListener = Mockito.mock(DownloadListener.class);
        serialQueue.setListener(anotherListener);
        assertThat(serialQueue.listenerBunch.contain(listener)).isFalse();
        assertThat(serialQueue.listenerBunch.contain(anotherListener)).isTrue();
    }

    @Test
    public void enqueue() {
        Mockito.doNothing().when(serialQueue).startNewLooper();
        // order
        Mockito.when(task1.compareTo(task2)).thenReturn((-1));
        serialQueue.enqueue(task2);
        serialQueue.enqueue(task1);
        Mockito.verify(taskList).add(ArgumentMatchers.eq(task1));
        Mockito.verify(taskList).add(ArgumentMatchers.eq(task2));
        assertThat(taskList).containsExactly(task1, task2);
    }

    @Test
    public void enqueue_newLooper() {
        Mockito.doNothing().when(serialQueue).startNewLooper();
        serialQueue.paused = true;
        serialQueue.enqueue(task1);
        Mockito.verify(serialQueue, Mockito.never()).startNewLooper();
        serialQueue.looping = true;
        serialQueue.enqueue(task1);
        Mockito.verify(serialQueue, Mockito.never()).startNewLooper();
        serialQueue.paused = false;
        serialQueue.looping = false;
        serialQueue.enqueue(task1);
        Mockito.verify(serialQueue).startNewLooper();
        assertThat(serialQueue.looping).isTrue();
    }

    @Test
    public void pause() {
        serialQueue.pause();
        assertThat(serialQueue.paused).isTrue();
    }

    @Test
    public void pause_isRunning_cancel() {
        serialQueue.runningTask = task1;
        serialQueue.pause();
        Mockito.verify(task1).cancel();
        assertThat(taskList.get(0)).isEqualTo(task1);
    }

    @Test
    public void resume() {
        serialQueue.paused = true;
        serialQueue.looping = false;
        Mockito.doNothing().when(serialQueue).startNewLooper();
        taskList.add(Mockito.mock(DownloadTask.class));
        serialQueue.resume();
        Mockito.verify(serialQueue).startNewLooper();
        assertThat(serialQueue.paused).isFalse();
        assertThat(serialQueue.looping).isTrue();
    }

    @Test
    public void resume_notPaused() {
        serialQueue = Mockito.spy(new DownloadSerialQueue());
        serialQueue.paused = false;
        serialQueue.resume();
        Mockito.verify(serialQueue, Mockito.never()).startNewLooper();
    }

    @Test
    public void resume_listNotEmpty_unpark() {
        Mockito.doNothing().when(serialQueue).startNewLooper();
        serialQueue.paused = true;
        taskList.add(task1);
        serialQueue.resume();
        Mockito.verify(serialQueue).startNewLooper();
    }

    @Test
    public void getWorkingTaskId() {
        assertThat(serialQueue.getWorkingTaskId()).isEqualTo(ID_INVALID);
        Mockito.when(task1.getId()).thenReturn(1);
        serialQueue.runningTask = task1;
        assertThat(serialQueue.getWorkingTaskId()).isEqualTo(1);
    }

    @Test
    public void getWaitingTaskCount() {
        assertThat(serialQueue.getWaitingTaskCount()).isZero();
        taskList.add(task1);
        assertThat(serialQueue.getWaitingTaskCount()).isEqualTo(1);
    }

    @Test
    public void shutdown() {
        taskList.add(task1);
        serialQueue.runningTask = task2;
        final DownloadTask[] tasks = serialQueue.shutdown();
        Mockito.verify(task2).cancel();
        assertThat(serialQueue.shutedDown).isTrue();
        assertThat(tasks).containsExactly(task1);
    }

    @Test
    public void run() {
        // empty
        serialQueue.looping = true;
        serialQueue.run();
        assertThat(serialQueue.looping).isFalse();
        // non empty but paused
        serialQueue.looping = true;
        taskList.add(task1);
        taskList.add(task2);
        serialQueue.paused = true;
        serialQueue.run();
        Mockito.verify(task1, Mockito.never()).execute(ArgumentMatchers.any(DownloadListener.class));
        Mockito.verify(task2, Mockito.never()).execute(ArgumentMatchers.any(DownloadListener.class));
        Mockito.verify(taskList, Mockito.never()).remove(ArgumentMatchers.anyInt());
        assertThat(serialQueue.looping).isFalse();
        // non empty and non paused
        serialQueue.looping = true;
        serialQueue.paused = false;
        serialQueue.run();
        Mockito.verify(task1).execute(ArgumentMatchers.any(DownloadListener.class));
        Mockito.verify(task2).execute(ArgumentMatchers.any(DownloadListener.class));
        Mockito.verify(taskList, Mockito.times(2)).remove(ArgumentMatchers.eq(0));
        assertThat(serialQueue.looping).isFalse();
    }

    @Test
    public void taskStart() {
        serialQueue.taskStart(task1);
        assertThat(serialQueue.runningTask).isEqualTo(task1);
    }

    @Test
    public void taskEnd() {
        serialQueue.runningTask = task1;
        serialQueue.taskEnd(task1, COMPLETED, null);
        assertThat(serialQueue.runningTask).isNull();
    }
}

