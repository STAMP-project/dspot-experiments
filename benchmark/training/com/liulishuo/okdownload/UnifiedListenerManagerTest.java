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


import EndCause.CANCELED;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.liulishuo.okdownload.core.listener.DownloadListener1;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class UnifiedListenerManagerTest {
    private UnifiedListenerManager listenerManager;

    @Mock
    private DownloadListener listener;

    @Test
    public void detachListener() {
        final ArrayList<DownloadListener> list = new ArrayList<>();
        list.add(listener);
        listenerManager.realListenerMap.put(1, list);
        final DownloadTask task = mockTask(1);
        listenerManager.detachListener(task, listener);
        assertThat(listenerManager.realListenerMap.size()).isZero();
        // detach by listener ignore host task.
        list.add(listener);
        listenerManager.realListenerMap.put(2, list);
        assertThat(listenerManager.realListenerMap.size()).isEqualTo(1);
        listenerManager.detachListener(listener);
        assertThat(listenerManager.realListenerMap.size()).isZero();
    }

    @Test
    public void attachListener() {
        final DownloadTask task = mockTask(2);
        listenerManager.attachListener(task, listener);
        assertThat(listenerManager.realListenerMap.size()).isEqualTo(1);
        assertThat(listenerManager.realListenerMap.get(2)).containsExactly(listener);
        final DownloadListener1 listener1 = Mockito.mock(DownloadListener1.class);
        listenerManager.attachListener(task, listener1);
        Mockito.verify(listener1).setAlwaysRecoverAssistModelIfNotSet(ArgumentMatchers.eq(true));
    }

    @Test
    public void attachAndEnqueueIfNotRun() {
        final DownloadTask task = mockTask(1);
        Mockito.doNothing().when(listenerManager).attachListener(ArgumentMatchers.eq(task), ArgumentMatchers.eq(listener));
        Mockito.doReturn(true).when(listenerManager).isTaskPendingOrRunning(ArgumentMatchers.eq(task));
        listenerManager.attachAndEnqueueIfNotRun(task, listener);
        assertThat(listenerManager.realListenerMap.size()).isZero();
        Mockito.verify(task, Mockito.never()).enqueue(ArgumentMatchers.eq(listenerManager.hostListener));
        Mockito.verify(listenerManager).attachListener(ArgumentMatchers.eq(task), ArgumentMatchers.eq(listener));
        Mockito.doReturn(false).when(listenerManager).isTaskPendingOrRunning(ArgumentMatchers.eq(task));
        listenerManager.attachAndEnqueueIfNotRun(task, listener);
        Mockito.verify(task).enqueue(ArgumentMatchers.eq(listenerManager.hostListener));
        Mockito.verify(listenerManager, Mockito.times(2)).attachListener(ArgumentMatchers.eq(task), ArgumentMatchers.eq(listener));
    }

    @Test
    public void executeTaskWithUnifiedListener() {
        final DownloadTask task = mockTask(1);
        Mockito.doNothing().when(listenerManager).attachListener(ArgumentMatchers.eq(task), ArgumentMatchers.eq(listener));
        Mockito.doNothing().when(task).execute(ArgumentMatchers.eq(listenerManager.hostListener));
        listenerManager.executeTaskWithUnifiedListener(task, listener);
        Mockito.verify(listenerManager).attachListener(ArgumentMatchers.eq(task), ArgumentMatchers.eq(listener));
        Mockito.verify(task).execute(ArgumentMatchers.eq(listenerManager.hostListener));
    }

    @Test
    public void hostListener() {
        final DownloadListener listener1 = Mockito.mock(DownloadListener.class);
        final DownloadListener listener2 = Mockito.mock(DownloadListener.class);
        final ArrayList<DownloadListener> list = new ArrayList<>();
        list.add(listener1);
        list.add(listener2);
        listenerManager.realListenerMap.put(1, list);
        final Map<String, List<String>> headerFields = Mockito.mock(Map.class);
        final BreakpointInfo info = Mockito.mock(BreakpointInfo.class);
        final ResumeFailedCause resumeFailedCause = Mockito.mock(ResumeFailedCause.class);
        final EndCause endCause = Mockito.mock(EndCause.class);
        final Exception exception = Mockito.mock(Exception.class);
        final DownloadTask task = mockTask(1);
        final DownloadTask noAttachTask = mockTask(2);
        final DownloadListener listener = listenerManager.hostListener;
        listener.taskStart(task);
        listener.taskStart(noAttachTask);
        Mockito.verify(listener1).taskStart(ArgumentMatchers.eq(task));
        Mockito.verify(listener2).taskStart(ArgumentMatchers.eq(task));
        listener.connectTrialStart(task, headerFields);
        listener.connectTrialStart(noAttachTask, headerFields);
        Mockito.verify(listener1).connectTrialStart(ArgumentMatchers.eq(task), ArgumentMatchers.eq(headerFields));
        Mockito.verify(listener2).connectTrialStart(ArgumentMatchers.eq(task), ArgumentMatchers.eq(headerFields));
        listener.connectTrialEnd(task, 200, headerFields);
        listener.connectTrialEnd(noAttachTask, 200, headerFields);
        Mockito.verify(listener1).connectTrialEnd(ArgumentMatchers.eq(task), ArgumentMatchers.eq(200), ArgumentMatchers.eq(headerFields));
        Mockito.verify(listener2).connectTrialEnd(ArgumentMatchers.eq(task), ArgumentMatchers.eq(200), ArgumentMatchers.eq(headerFields));
        listener.downloadFromBeginning(task, info, resumeFailedCause);
        listener.downloadFromBeginning(noAttachTask, info, resumeFailedCause);
        Mockito.verify(listener1).downloadFromBeginning(ArgumentMatchers.eq(task), ArgumentMatchers.eq(info), ArgumentMatchers.eq(resumeFailedCause));
        Mockito.verify(listener2).downloadFromBeginning(ArgumentMatchers.eq(task), ArgumentMatchers.eq(info), ArgumentMatchers.eq(resumeFailedCause));
        listener.downloadFromBreakpoint(task, info);
        listener.downloadFromBreakpoint(noAttachTask, info);
        Mockito.verify(listener1).downloadFromBreakpoint(ArgumentMatchers.eq(task), ArgumentMatchers.eq(info));
        Mockito.verify(listener2).downloadFromBreakpoint(ArgumentMatchers.eq(task), ArgumentMatchers.eq(info));
        listener.connectStart(task, 1, headerFields);
        listener.connectStart(noAttachTask, 1, headerFields);
        Mockito.verify(listener1).connectStart(ArgumentMatchers.eq(task), ArgumentMatchers.eq(1), ArgumentMatchers.eq(headerFields));
        Mockito.verify(listener2).connectStart(ArgumentMatchers.eq(task), ArgumentMatchers.eq(1), ArgumentMatchers.eq(headerFields));
        listener.connectEnd(task, 1, 200, headerFields);
        listener.connectEnd(noAttachTask, 1, 200, headerFields);
        Mockito.verify(listener1).connectEnd(ArgumentMatchers.eq(task), ArgumentMatchers.eq(1), ArgumentMatchers.eq(200), ArgumentMatchers.eq(headerFields));
        Mockito.verify(listener2).connectEnd(ArgumentMatchers.eq(task), ArgumentMatchers.eq(1), ArgumentMatchers.eq(200), ArgumentMatchers.eq(headerFields));
        listener.fetchStart(task, 1, 2L);
        listener.fetchStart(noAttachTask, 1, 2L);
        Mockito.verify(listener1).fetchStart(ArgumentMatchers.eq(task), ArgumentMatchers.eq(1), ArgumentMatchers.eq(2L));
        Mockito.verify(listener2).fetchStart(ArgumentMatchers.eq(task), ArgumentMatchers.eq(1), ArgumentMatchers.eq(2L));
        listener.fetchProgress(task, 1, 2L);
        listener.fetchProgress(noAttachTask, 1, 2L);
        Mockito.verify(listener1).fetchProgress(ArgumentMatchers.eq(task), ArgumentMatchers.eq(1), ArgumentMatchers.eq(2L));
        Mockito.verify(listener2).fetchProgress(ArgumentMatchers.eq(task), ArgumentMatchers.eq(1), ArgumentMatchers.eq(2L));
        listener.fetchEnd(task, 1, 2L);
        listener.fetchEnd(noAttachTask, 1, 2L);
        Mockito.verify(listener1).fetchEnd(ArgumentMatchers.eq(task), ArgumentMatchers.eq(1), ArgumentMatchers.eq(2L));
        Mockito.verify(listener2).fetchEnd(ArgumentMatchers.eq(task), ArgumentMatchers.eq(1), ArgumentMatchers.eq(2L));
        listener.taskEnd(task, endCause, exception);
        listener.taskEnd(noAttachTask, endCause, exception);
        Mockito.verify(listener1).taskEnd(ArgumentMatchers.eq(task), ArgumentMatchers.eq(endCause), ArgumentMatchers.eq(exception));
        Mockito.verify(listener2).taskEnd(ArgumentMatchers.eq(task), ArgumentMatchers.eq(endCause), ArgumentMatchers.eq(exception));
    }

    @Test
    public void taskEnd_detachListener() {
        final DownloadListener listener1 = Mockito.mock(DownloadListener.class);
        final ArrayList<DownloadListener> list = new ArrayList<>();
        list.add(listener1);
        listenerManager.realListenerMap.put(1, list);
        final DownloadTask task = mockTask(1);
        final DownloadListener listener = listenerManager.hostListener;
        listenerManager.autoRemoveListenerIdList.add(1);
        listener.taskEnd(task, CANCELED, null);
        assertThat(listenerManager.realListenerMap.size()).isZero();
    }

    @Test
    public void detachListener_taskId() {
        final ArrayList<DownloadListener> listenerList = new ArrayList<>();
        listenerList.add(Mockito.mock(DownloadListener.class));
        listenerManager.realListenerMap.put(1, listenerList);
        listenerManager.detachListener(1);
        assertThat(listenerManager.realListenerMap.size()).isZero();
    }

    @Test
    public void addAutoRemoveListenersWhenTaskEnd() {
        listenerManager.addAutoRemoveListenersWhenTaskEnd(1);
        listenerManager.addAutoRemoveListenersWhenTaskEnd(1);
        assertThat(listenerManager.autoRemoveListenerIdList).containsExactly(1);
    }

    @Test
    public void removeAutoRemoveListenersWhenTaskEnd() {
        listenerManager.autoRemoveListenerIdList.add(1);
        listenerManager.removeAutoRemoveListenersWhenTaskEnd(1);
        assertThat(listenerManager.autoRemoveListenerIdList).isEmpty();
    }
}

