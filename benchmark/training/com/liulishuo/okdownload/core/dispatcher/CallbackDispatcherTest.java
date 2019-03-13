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
package com.liulishuo.okdownload.core.dispatcher;


import CallbackDispatcher.DefaultTransmitListener;
import DownloadTask.TaskHideWrapper;
import EndCause.CANCELED;
import EndCause.COMPLETED;
import EndCause.ERROR;
import EndCause.FILE_BUSY;
import EndCause.SAME_TASK_BUSY;
import android.os.Handler;
import com.liulishuo.okdownload.DownloadListener;
import com.liulishuo.okdownload.DownloadMonitor;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.OkDownload;
import com.liulishuo.okdownload.TestUtils;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
public class CallbackDispatcherTest {
    private CallbackDispatcher dispatcher;

    private DefaultTransmitListener transmit;

    @Mock
    private Handler handler;

    @Test
    public void dispatch_ui() {
        final DownloadTask task = Mockito.mock(DownloadTask.class);
        final DownloadListener listener = Mockito.mock(DownloadListener.class);
        Mockito.when(task.getListener()).thenReturn(listener);
        final BreakpointInfo info = Mockito.mock(BreakpointInfo.class);
        final Map<String, List<String>> headerFields = Mockito.mock(Map.class);
        final ResumeFailedCause resumeFailedCause = Mockito.mock(ResumeFailedCause.class);
        final EndCause endCause = Mockito.mock(EndCause.class);
        final Exception exception = Mockito.mock(Exception.class);
        Mockito.when(task.isAutoCallbackToUIThread()).thenReturn(true);
        dispatcher.dispatch().taskStart(task);
        Mockito.verify(listener).taskStart(ArgumentMatchers.eq(task));
        dispatcher.dispatch().connectTrialStart(task, headerFields);
        Mockito.verify(listener).connectTrialStart(ArgumentMatchers.eq(task), ArgumentMatchers.eq(headerFields));
        dispatcher.dispatch().connectTrialEnd(task, 200, headerFields);
        Mockito.verify(listener).connectTrialEnd(ArgumentMatchers.eq(task), ArgumentMatchers.eq(200), ArgumentMatchers.eq(headerFields));
        dispatcher.dispatch().downloadFromBeginning(task, info, resumeFailedCause);
        Mockito.verify(listener).downloadFromBeginning(ArgumentMatchers.eq(task), ArgumentMatchers.eq(info), ArgumentMatchers.eq(resumeFailedCause));
        dispatcher.dispatch().downloadFromBreakpoint(task, info);
        Mockito.verify(listener).downloadFromBreakpoint(ArgumentMatchers.eq(task), ArgumentMatchers.eq(info));
        dispatcher.dispatch().connectStart(task, 1, headerFields);
        Mockito.verify(listener).connectStart(ArgumentMatchers.eq(task), ArgumentMatchers.eq(1), ArgumentMatchers.eq(headerFields));
        dispatcher.dispatch().connectEnd(task, 2, 200, headerFields);
        Mockito.verify(listener).connectEnd(ArgumentMatchers.eq(task), ArgumentMatchers.eq(2), ArgumentMatchers.eq(200), ArgumentMatchers.eq(headerFields));
        dispatcher.dispatch().fetchStart(task, 1, 2L);
        Mockito.verify(listener).fetchStart(ArgumentMatchers.eq(task), ArgumentMatchers.eq(1), ArgumentMatchers.eq(2L));
        dispatcher.dispatch().fetchProgress(task, 1, 2L);
        Mockito.verify(listener).fetchProgress(ArgumentMatchers.eq(task), ArgumentMatchers.eq(1), ArgumentMatchers.eq(2L));
        dispatcher.dispatch().fetchEnd(task, 1, 2L);
        Mockito.verify(listener).fetchEnd(ArgumentMatchers.eq(task), ArgumentMatchers.eq(1), ArgumentMatchers.eq(2L));
        dispatcher.dispatch().taskEnd(task, endCause, exception);
        Mockito.verify(listener).taskEnd(ArgumentMatchers.eq(task), ArgumentMatchers.eq(endCause), ArgumentMatchers.eq(exception));
    }

    @Test
    public void dispatch_nonUi() {
        final DownloadTask task = Mockito.mock(DownloadTask.class);
        final DownloadListener listener = Mockito.mock(DownloadListener.class);
        Mockito.when(task.getListener()).thenReturn(listener);
        final BreakpointInfo info = Mockito.mock(BreakpointInfo.class);
        final Map<String, List<String>> headerFields = Mockito.mock(Map.class);
        final ResumeFailedCause resumeFailedCause = Mockito.mock(ResumeFailedCause.class);
        final EndCause endCause = Mockito.mock(EndCause.class);
        final Exception exception = Mockito.mock(Exception.class);
        Mockito.when(task.isAutoCallbackToUIThread()).thenReturn(false);
        dispatcher.dispatch().taskStart(task);
        Mockito.verify(listener).taskStart(ArgumentMatchers.eq(task));
        Mockito.verify(transmit).inspectTaskStart(ArgumentMatchers.eq(task));
        dispatcher.dispatch().connectTrialStart(task, headerFields);
        Mockito.verify(listener).connectTrialStart(ArgumentMatchers.eq(task), ArgumentMatchers.eq(headerFields));
        dispatcher.dispatch().connectTrialEnd(task, 200, headerFields);
        Mockito.verify(listener).connectTrialEnd(ArgumentMatchers.eq(task), ArgumentMatchers.eq(200), ArgumentMatchers.eq(headerFields));
        dispatcher.dispatch().downloadFromBeginning(task, info, resumeFailedCause);
        Mockito.verify(listener).downloadFromBeginning(ArgumentMatchers.eq(task), ArgumentMatchers.eq(info), ArgumentMatchers.eq(resumeFailedCause));
        Mockito.verify(transmit).inspectDownloadFromBeginning(ArgumentMatchers.eq(task), ArgumentMatchers.eq(info), ArgumentMatchers.eq(resumeFailedCause));
        dispatcher.dispatch().downloadFromBreakpoint(task, info);
        Mockito.verify(listener).downloadFromBreakpoint(ArgumentMatchers.eq(task), ArgumentMatchers.eq(info));
        Mockito.verify(transmit).inspectDownloadFromBreakpoint(ArgumentMatchers.eq(task), ArgumentMatchers.eq(info));
        dispatcher.dispatch().connectStart(task, 1, headerFields);
        Mockito.verify(listener).connectStart(ArgumentMatchers.eq(task), ArgumentMatchers.eq(1), ArgumentMatchers.eq(headerFields));
        dispatcher.dispatch().connectEnd(task, 2, 200, headerFields);
        Mockito.verify(listener).connectEnd(ArgumentMatchers.eq(task), ArgumentMatchers.eq(2), ArgumentMatchers.eq(200), ArgumentMatchers.eq(headerFields));
        dispatcher.dispatch().fetchStart(task, 1, 2L);
        Mockito.verify(listener).fetchStart(ArgumentMatchers.eq(task), ArgumentMatchers.eq(1), ArgumentMatchers.eq(2L));
        dispatcher.dispatch().fetchProgress(task, 1, 2L);
        Mockito.verify(listener).fetchProgress(ArgumentMatchers.eq(task), ArgumentMatchers.eq(1), ArgumentMatchers.eq(2L));
        dispatcher.dispatch().fetchEnd(task, 1, 2L);
        Mockito.verify(listener).fetchEnd(ArgumentMatchers.eq(task), ArgumentMatchers.eq(1), ArgumentMatchers.eq(2L));
        dispatcher.dispatch().taskEnd(task, endCause, exception);
        Mockito.verify(listener).taskEnd(ArgumentMatchers.eq(task), ArgumentMatchers.eq(endCause), ArgumentMatchers.eq(exception));
        Mockito.verify(transmit).inspectTaskEnd(ArgumentMatchers.eq(task), ArgumentMatchers.eq(endCause), ArgumentMatchers.eq(exception));
    }

    @Test
    public void monitor_taskStart() throws IOException {
        TestUtils.mockOkDownload();
        final DownloadMonitor monitor = Mockito.mock(DownloadMonitor.class);
        final OkDownload okDownload = OkDownload.with();
        Mockito.when(okDownload.getMonitor()).thenReturn(monitor);
        final DownloadTask task = Mockito.mock(DownloadTask.class);
        transmit.inspectTaskStart(task);
        Mockito.verify(monitor).taskStart(ArgumentMatchers.eq(task));
    }

    @Test
    public void monitor_trialConnectEnd() throws IOException {
        TestUtils.mockOkDownload();
        final DownloadMonitor monitor = Mockito.mock(DownloadMonitor.class);
        final OkDownload okDownload = OkDownload.with();
        Mockito.when(okDownload.getMonitor()).thenReturn(monitor);
        final DownloadTask task = Mockito.mock(DownloadTask.class);
        final BreakpointInfo info = Mockito.mock(BreakpointInfo.class);
        final ResumeFailedCause resumeFailedCause = Mockito.mock(ResumeFailedCause.class);
        transmit.inspectDownloadFromBeginning(task, info, resumeFailedCause);
        Mockito.verify(monitor).taskDownloadFromBeginning(ArgumentMatchers.eq(task), ArgumentMatchers.eq(info), ArgumentMatchers.eq(resumeFailedCause));
        transmit.inspectDownloadFromBreakpoint(task, info);
        Mockito.verify(monitor).taskDownloadFromBreakpoint(ArgumentMatchers.eq(task), ArgumentMatchers.eq(info));
    }

    @Test
    public void monitor_taskEnd() throws IOException {
        TestUtils.mockOkDownload();
        final DownloadMonitor monitor = Mockito.mock(DownloadMonitor.class);
        final OkDownload okDownload = OkDownload.with();
        Mockito.when(okDownload.getMonitor()).thenReturn(monitor);
        final DownloadTask task = Mockito.mock(DownloadTask.class);
        final EndCause endCause = Mockito.mock(EndCause.class);
        final Exception exception = Mockito.mock(Exception.class);
        transmit.inspectTaskEnd(task, endCause, exception);
        Mockito.verify(monitor).taskEnd(ArgumentMatchers.eq(task), ArgumentMatchers.eq(endCause), ArgumentMatchers.eq(exception));
    }

    @Test
    public void endTasks() {
        final Collection<DownloadTask> completedTaskCollection = new ArrayList<>();
        final Collection<DownloadTask> sameTaskConflictCollection = new ArrayList<>();
        final Collection<DownloadTask> fileBusyCollection = new ArrayList<>();
        dispatcher.endTasks(completedTaskCollection, sameTaskConflictCollection, fileBusyCollection);
        Mockito.verify(handler, Mockito.never()).post(ArgumentMatchers.any(Runnable.class));
        final DownloadTask autoUiTask = Mockito.mock(DownloadTask.class);
        final DownloadTask nonUiTask = Mockito.mock(DownloadTask.class);
        final DownloadListener nonUiListener = Mockito.mock(DownloadListener.class);
        final DownloadListener autoUiListener = Mockito.mock(DownloadListener.class);
        Mockito.when(autoUiTask.getListener()).thenReturn(autoUiListener);
        Mockito.when(autoUiTask.isAutoCallbackToUIThread()).thenReturn(true);
        Mockito.when(nonUiTask.getListener()).thenReturn(nonUiListener);
        Mockito.when(nonUiTask.isAutoCallbackToUIThread()).thenReturn(false);
        completedTaskCollection.add(autoUiTask);
        completedTaskCollection.add(nonUiTask);
        sameTaskConflictCollection.add(autoUiTask);
        sameTaskConflictCollection.add(nonUiTask);
        fileBusyCollection.add(autoUiTask);
        fileBusyCollection.add(nonUiTask);
        dispatcher.endTasks(completedTaskCollection, sameTaskConflictCollection, fileBusyCollection);
        Mockito.verify(nonUiListener).taskEnd(ArgumentMatchers.eq(nonUiTask), ArgumentMatchers.eq(COMPLETED), ArgumentMatchers.nullable(Exception.class));
        Mockito.verify(nonUiListener).taskEnd(ArgumentMatchers.eq(nonUiTask), ArgumentMatchers.eq(SAME_TASK_BUSY), ArgumentMatchers.nullable(Exception.class));
        Mockito.verify(nonUiListener).taskEnd(ArgumentMatchers.eq(nonUiTask), ArgumentMatchers.eq(FILE_BUSY), ArgumentMatchers.nullable(Exception.class));
        Mockito.verify(handler).post(ArgumentMatchers.any(Runnable.class));
        Mockito.verify(autoUiListener).taskEnd(ArgumentMatchers.eq(autoUiTask), ArgumentMatchers.eq(COMPLETED), ArgumentMatchers.nullable(Exception.class));
        Mockito.verify(autoUiListener).taskEnd(ArgumentMatchers.eq(autoUiTask), ArgumentMatchers.eq(SAME_TASK_BUSY), ArgumentMatchers.nullable(Exception.class));
        Mockito.verify(autoUiListener).taskEnd(ArgumentMatchers.eq(autoUiTask), ArgumentMatchers.eq(FILE_BUSY), ArgumentMatchers.nullable(Exception.class));
    }

    @Test
    public void endTasksWithError() {
        final Collection<DownloadTask> errorCollection = new ArrayList<>();
        final Exception realCause = Mockito.mock(Exception.class);
        final DownloadTask autoUiTask = Mockito.mock(DownloadTask.class);
        final DownloadTask nonUiTask = Mockito.mock(DownloadTask.class);
        final DownloadListener nonUiListener = Mockito.mock(DownloadListener.class);
        final DownloadListener autoUiListener = Mockito.mock(DownloadListener.class);
        Mockito.when(autoUiTask.getListener()).thenReturn(autoUiListener);
        Mockito.when(autoUiTask.isAutoCallbackToUIThread()).thenReturn(true);
        Mockito.when(nonUiTask.getListener()).thenReturn(nonUiListener);
        Mockito.when(nonUiTask.isAutoCallbackToUIThread()).thenReturn(false);
        errorCollection.add(autoUiTask);
        errorCollection.add(nonUiTask);
        dispatcher.endTasksWithError(errorCollection, realCause);
        Mockito.verify(nonUiListener).taskEnd(ArgumentMatchers.eq(nonUiTask), ArgumentMatchers.eq(ERROR), ArgumentMatchers.eq(realCause));
        Mockito.verify(handler).post(ArgumentMatchers.any(Runnable.class));
        Mockito.verify(autoUiListener).taskEnd(ArgumentMatchers.eq(autoUiTask), ArgumentMatchers.eq(ERROR), ArgumentMatchers.eq(realCause));
    }

    @Test
    public void endTasksWithCanceled() {
        final Collection<DownloadTask> canceledCollection = new ArrayList<>();
        final DownloadTask autoUiTask = Mockito.mock(DownloadTask.class);
        final DownloadTask nonUiTask = Mockito.mock(DownloadTask.class);
        final DownloadListener nonUiListener = Mockito.mock(DownloadListener.class);
        final DownloadListener autoUiListener = Mockito.mock(DownloadListener.class);
        Mockito.when(autoUiTask.getListener()).thenReturn(autoUiListener);
        Mockito.when(autoUiTask.isAutoCallbackToUIThread()).thenReturn(true);
        Mockito.when(nonUiTask.getListener()).thenReturn(nonUiListener);
        Mockito.when(nonUiTask.isAutoCallbackToUIThread()).thenReturn(false);
        canceledCollection.add(autoUiTask);
        canceledCollection.add(nonUiTask);
        dispatcher.endTasksWithCanceled(canceledCollection);
        Mockito.verify(nonUiListener).taskEnd(ArgumentMatchers.eq(nonUiTask), ArgumentMatchers.eq(CANCELED), ArgumentMatchers.nullable(Exception.class));
        Mockito.verify(handler).post(ArgumentMatchers.any(Runnable.class));
        Mockito.verify(autoUiListener).taskEnd(ArgumentMatchers.eq(autoUiTask), ArgumentMatchers.eq(CANCELED), ArgumentMatchers.nullable(Exception.class));
    }

    @Test
    public void isFetchProcessMoment_noMinInterval() {
        final DownloadTask task = Mockito.mock(DownloadTask.class);
        Mockito.when(task.getMinIntervalMillisCallbackProcess()).thenReturn(0);
        assertThat(dispatcher.isFetchProcessMoment(task)).isTrue();
    }

    @Test
    public void isFetchProcessMoment_largeThanMinInterval() {
        final DownloadTask task = Mockito.mock(DownloadTask.class);
        Mockito.when(task.getMinIntervalMillisCallbackProcess()).thenReturn(1);
        assertThat(dispatcher.isFetchProcessMoment(task)).isTrue();
    }

    @Test
    public void isFetchProcessMoment_lessThanMinInterval() {
        final DownloadTask task = Mockito.mock(DownloadTask.class);
        Mockito.when(task.getMinIntervalMillisCallbackProcess()).thenReturn(Integer.MAX_VALUE);
        assertThat(dispatcher.isFetchProcessMoment(task)).isFalse();
    }

    @Test
    public void fetchProgress_setMinInterval() {
        final DownloadTask task = Mockito.spy(new DownloadTask.Builder("https://jacksgong.com", "parentPath", "filename").setMinIntervalMillisCallbackProcess(1).build());
        final DownloadListener listener = Mockito.mock(DownloadListener.class);
        Mockito.when(task.getListener()).thenReturn(listener);
        dispatcher.dispatch().fetchProgress(task, 1, 2);
        assertThat(TaskHideWrapper.getLastCallbackProcessTs(task)).isNotZero();
    }
}

