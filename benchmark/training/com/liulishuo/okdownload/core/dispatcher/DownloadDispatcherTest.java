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


import EndCause.COMPLETED;
import com.liulishuo.okdownload.DownloadListener;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.OkDownload;
import com.liulishuo.okdownload.TestUtils;
import com.liulishuo.okdownload.core.IdentifiedTask;
import com.liulishuo.okdownload.core.breakpoint.BreakpointStore;
import com.liulishuo.okdownload.core.breakpoint.DownloadStore;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.download.DownloadCall;
import com.liulishuo.okdownload.core.download.DownloadStrategy;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class DownloadDispatcherTest {
    private DownloadDispatcher dispatcher;

    private List<DownloadCall> readyAsyncCalls;

    private List<DownloadCall> runningAsyncCalls;

    private List<DownloadCall> runningSyncCalls;

    private List<DownloadCall> finishingCalls;

    @Mock
    private DownloadStore store;

    private File existFile = new File("./p-path/filename");

    @Test
    public void enqueue_conflict_notEnqueue() {
        final DownloadTask mockReadyTask = mockTask();
        final DownloadCall readyCall = DownloadCall.create(mockReadyTask, true, store);
        readyAsyncCalls.add(readyCall);
        final DownloadTask mockRunningAsyncTask = mockTask();
        final DownloadCall runningAsyncCall = Mockito.spy(DownloadCall.create(mockRunningAsyncTask, true, store));
        runningAsyncCalls.add(runningAsyncCall);
        final DownloadTask mockRunningSyncTask = mockTask();
        final DownloadCall runningSyncCall = DownloadCall.create(mockRunningSyncTask, false, store);
        runningSyncCalls.add(runningSyncCall);
        dispatcher.enqueue(mockReadyTask);
        dispatcher.enqueue(mockRunningAsyncTask);
        dispatcher.execute(mockRunningSyncTask);
        assertThat(readyAsyncCalls).containsOnlyOnce(readyCall);
        assertThat(runningAsyncCalls).containsOnlyOnce(runningAsyncCall);
        assertThat(runningSyncCalls).containsOnlyOnce(runningSyncCall);
        verifyTaskEnd(mockReadyTask, SAME_TASK_BUSY, null);
        verifyTaskEnd(mockRunningAsyncTask, SAME_TASK_BUSY, null);
        verifyTaskEnd(mockRunningSyncTask, SAME_TASK_BUSY, null);
        final DownloadTask mockFileBusyTask1 = mockTask();
        Mockito.doReturn(mockReadyTask.getFile()).when(mockFileBusyTask1).getFile();
        dispatcher.enqueue(mockFileBusyTask1);
        verifyTaskEnd(mockFileBusyTask1, FILE_BUSY, null);
        final DownloadTask mockFileBusyTask2 = mockTask();
        Mockito.doReturn(mockRunningAsyncTask.getFile()).when(mockFileBusyTask2).getFile();
        dispatcher.execute(mockFileBusyTask2);
        verifyTaskEnd(mockFileBusyTask2, FILE_BUSY, null);
        final DownloadTask mockFileBusyTask3 = mockTask();
        Mockito.doReturn(mockRunningSyncTask.getFile()).when(mockFileBusyTask3).getFile();
        dispatcher.enqueue(mockFileBusyTask3);
        verifyTaskEnd(mockFileBusyTask3, FILE_BUSY, null);
        // ignore canceled
        assertThat(runningAsyncCalls.size()).isEqualTo(1);
        Mockito.when(runningAsyncCall.isCanceled()).thenReturn(true);
        dispatcher.enqueue(mockRunningAsyncTask);
        assertThat(runningAsyncCalls.size()).isEqualTo(2);
    }

    @Test
    public void enqueue_maxTaskCountControl() {
        maxRunningTask();
        final DownloadTask mockTask = mockTask();
        dispatcher.enqueue(mockTask);
        assertThat(readyAsyncCalls).hasSize(1);
        assertThat(readyAsyncCalls.get(0).task).isEqualTo(mockTask);
        assertThat(runningSyncCalls).isEmpty();
        assertThat(runningAsyncCalls).hasSize(dispatcher.maxParallelRunningCount);
    }

    @Test
    public void enqueue_countIgnoreCanceled() {
        maxRunningTask();
        assertThat(runningAsyncCalls).hasSize(dispatcher.maxParallelRunningCount);
        final DownloadTask task = mockTask();
        final DownloadCall canceledCall = runningAsyncCalls.get(0);
        dispatcher.cancel(canceledCall.task);
        // maybe here is bad design, because of here relate to DownloadCall#cancel we have to invoke
        // flyingCanceled manually which does on DownloadCall#cancel
        dispatcher.flyingCanceled(canceledCall);
        dispatcher.enqueue(task);
        assertThat(readyAsyncCalls).hasSize(0);
        assertThat(runningAsyncCalls).hasSize(((dispatcher.maxParallelRunningCount) + 1));
        assertThat(runningAsyncCalls.get(dispatcher.maxParallelRunningCount).task).isEqualTo(task);
        assertThat(runningSyncCalls).isEmpty();
    }

    @Test
    public void enqueue_priority() {
        final DownloadTask mockTask1 = mockTask();
        Mockito.when(mockTask1.getPriority()).thenReturn(1);
        final DownloadTask mockTask2 = mockTask();
        Mockito.when(mockTask2.getPriority()).thenReturn(2);
        final DownloadTask mockTask3 = mockTask();
        Mockito.when(mockTask3.getPriority()).thenReturn(3);
        maxRunningTask();
        dispatcher.enqueue(mockTask2);
        dispatcher.enqueue(mockTask1);
        dispatcher.enqueue(mockTask3);
        assertThat(readyAsyncCalls.get(0).task).isEqualTo(mockTask3);
        assertThat(readyAsyncCalls.get(1).task).isEqualTo(mockTask2);
        assertThat(readyAsyncCalls.get(2).task).isEqualTo(mockTask1);
    }

    @Test
    public void enqueue_tasks() throws IOException {
        TestUtils.mockOkDownload();
        final CallbackDispatcher callbackDispatcher = OkDownload.with().callbackDispatcher();
        DownloadTask[] tasks = new DownloadTask[]{ Mockito.mock(DownloadTask.class), Mockito.mock(DownloadTask.class), Mockito.mock(DownloadTask.class) };
        Mockito.doReturn(true).when(dispatcher).inspectCompleted(ArgumentMatchers.any(DownloadTask.class), ArgumentMatchers.any(Collection.class));
        Mockito.doReturn(true).when(dispatcher).inspectForConflict(ArgumentMatchers.any(DownloadTask.class), ArgumentMatchers.any(Collection.class), ArgumentMatchers.any(Collection.class), ArgumentMatchers.any(Collection.class));
        dispatcher.enqueue(tasks);
        Mockito.verify(callbackDispatcher).endTasks(ArgumentMatchers.any(Collection.class), ArgumentMatchers.any(Collection.class), ArgumentMatchers.any(Collection.class));
    }

    @Test
    public void enqueue_tasksWithNetworkNotAvailable() throws IOException {
        TestUtils.mockOkDownload();
        final CallbackDispatcher callbackDispatcher = OkDownload.with().callbackDispatcher();
        final DownloadStrategy downloadStrategy = OkDownload.with().downloadStrategy();
        DownloadTask[] tasks = new DownloadTask[]{ Mockito.mock(DownloadTask.class), Mockito.mock(DownloadTask.class), Mockito.mock(DownloadTask.class) };
        Mockito.doThrow(UnknownHostException.class).when(downloadStrategy).inspectNetworkAvailable();
        dispatcher.enqueue(tasks);
        final ArgumentCaptor<Collection<DownloadTask>> listCaptor = ArgumentCaptor.forClass(Collection.class);
        Mockito.verify(callbackDispatcher, Mockito.never()).endTasks(ArgumentMatchers.any(Collection.class), ArgumentMatchers.any(Collection.class), ArgumentMatchers.any(Collection.class));
        assertThat(readyAsyncCalls).isEmpty();
        Mockito.verify(dispatcher, Mockito.never()).getExecutorService();
        final ArgumentCaptor<Exception> causeCaptor = ArgumentCaptor.forClass(Exception.class);
        Mockito.verify(callbackDispatcher).endTasksWithError(listCaptor.capture(), causeCaptor.capture());
        assertThat(listCaptor.getValue()).containsExactly(tasks);
        assertThat(causeCaptor.getValue()).isExactlyInstanceOf(UnknownHostException.class);
    }

    @Test
    public void execute() {
        final DownloadTask mockTask = mockTask();
        dispatcher.execute(mockTask);
        ArgumentCaptor<DownloadCall> callCaptor = ArgumentCaptor.forClass(DownloadCall.class);
        Mockito.verify(runningSyncCalls).add(callCaptor.capture());
        final DownloadCall call = callCaptor.getValue();
        assertThat(call.task).isEqualTo(mockTask);
        Mockito.verify(dispatcher).syncRunCall(call);
    }

    @Test
    public void cancel_readyAsyncCall() throws IOException {
        TestUtils.mockOkDownload();
        final DownloadListener listener = OkDownload.with().callbackDispatcher().dispatch();
        final DownloadTask task = Mockito.mock(DownloadTask.class);
        Mockito.when(task.getId()).thenReturn(1);
        final DownloadCall call = Mockito.spy(DownloadCall.create(task, false, store));
        readyAsyncCalls.add(call);
        dispatcher.cancel(task);
        Mockito.verify(call, Mockito.never()).cancel();
        Mockito.verify(store, Mockito.never()).onTaskEnd(ArgumentMatchers.eq(1), ArgumentMatchers.eq(CANCELED), ArgumentMatchers.nullable(Exception.class));
        Mockito.verify(listener).taskEnd(ArgumentMatchers.eq(task), ArgumentMatchers.eq(CANCELED), ArgumentMatchers.nullable(Exception.class));
        assertThat(readyAsyncCalls.isEmpty()).isTrue();
    }

    @Test
    public void cancel_runningAsync() throws IOException {
        TestUtils.mockOkDownload();
        final DownloadListener listener = OkDownload.with().callbackDispatcher().dispatch();
        final DownloadTask task = Mockito.mock(DownloadTask.class);
        Mockito.when(task.getId()).thenReturn(1);
        final DownloadCall call = Mockito.spy(DownloadCall.create(task, false, store));
        runningAsyncCalls.add(call);
        dispatcher.cancel(task);
        Mockito.verify(call).cancel();
        Mockito.verify(listener).taskEnd(ArgumentMatchers.eq(task), ArgumentMatchers.eq(CANCELED), ArgumentMatchers.nullable(Exception.class));
    }

    @Test
    public void cancel_runningSync() {
        final DownloadListener listener = OkDownload.with().callbackDispatcher().dispatch();
        final DownloadTask task = Mockito.mock(DownloadTask.class);
        Mockito.when(task.getId()).thenReturn(1);
        final DownloadCall call = Mockito.spy(DownloadCall.create(task, false, store));
        runningSyncCalls.add(call);
        dispatcher.cancel(task);
        Mockito.verify(call).cancel();
        Mockito.verify(listener).taskEnd(ArgumentMatchers.eq(task), ArgumentMatchers.eq(CANCELED), ArgumentMatchers.nullable(Exception.class));
    }

    @Test
    public void cancel_notSameReferenceButSameId_runningSync() {
        final DownloadListener listener = OkDownload.with().callbackDispatcher().dispatch();
        final DownloadTask runningSyncTask = Mockito.mock(DownloadTask.class);
        final DownloadTask runningAsyncTask = Mockito.mock(DownloadTask.class);
        final DownloadTask readyAsyncTask = Mockito.mock(DownloadTask.class);
        Mockito.when(runningSyncTask.getId()).thenReturn(1);
        Mockito.when(runningAsyncTask.getId()).thenReturn(1);
        Mockito.when(readyAsyncTask.getId()).thenReturn(1);
        final DownloadTask sameIdTask = Mockito.mock(DownloadTask.class);
        Mockito.when(sameIdTask.getId()).thenReturn(1);
        final DownloadCall runningSyncCall = Mockito.spy(DownloadCall.create(runningSyncTask, false, store));
        runningSyncCalls.add(runningSyncCall);
        dispatcher.cancel(sameIdTask);
        Mockito.verify(runningSyncCall).cancel();
        Mockito.verify(listener).taskEnd(ArgumentMatchers.eq(runningSyncTask), ArgumentMatchers.eq(CANCELED), ArgumentMatchers.nullable(Exception.class));
    }

    @Test
    public void cancel_notSameReferenceButSameId_runningAsync() {
        final DownloadListener listener = OkDownload.with().callbackDispatcher().dispatch();
        final DownloadTask runningSyncTask = Mockito.mock(DownloadTask.class);
        final DownloadTask runningAsyncTask = Mockito.mock(DownloadTask.class);
        final DownloadTask readyAsyncTask = Mockito.mock(DownloadTask.class);
        Mockito.when(runningSyncTask.getId()).thenReturn(1);
        Mockito.when(runningAsyncTask.getId()).thenReturn(1);
        Mockito.when(readyAsyncTask.getId()).thenReturn(1);
        final DownloadTask sameIdTask = Mockito.mock(DownloadTask.class);
        Mockito.when(sameIdTask.getId()).thenReturn(1);
        final DownloadCall runningAsyncCall = Mockito.spy(DownloadCall.create(runningAsyncTask, false, store));
        runningAsyncCalls.add(runningAsyncCall);
        dispatcher.cancel(sameIdTask);
        Mockito.verify(runningAsyncCall).cancel();
        Mockito.verify(listener).taskEnd(ArgumentMatchers.eq(runningAsyncTask), ArgumentMatchers.eq(CANCELED), ArgumentMatchers.nullable(Exception.class));
    }

    @Test
    public void cancel_notSameReferenceButSameId_readyAsync() {
        final DownloadListener listener = OkDownload.with().callbackDispatcher().dispatch();
        final DownloadTask runningSyncTask = Mockito.mock(DownloadTask.class);
        final DownloadTask runningAsyncTask = Mockito.mock(DownloadTask.class);
        final DownloadTask readyAsyncTask = Mockito.mock(DownloadTask.class);
        Mockito.when(runningSyncTask.getId()).thenReturn(1);
        Mockito.when(runningAsyncTask.getId()).thenReturn(1);
        Mockito.when(readyAsyncTask.getId()).thenReturn(1);
        final DownloadCall readyAsyncCall = Mockito.spy(DownloadCall.create(readyAsyncTask, false, store));
        readyAsyncCalls.add(readyAsyncCall);
        final DownloadTask sameIdTask = Mockito.mock(DownloadTask.class);
        Mockito.when(sameIdTask.getId()).thenReturn(1);
        dispatcher.cancel(sameIdTask);
        Mockito.verify(listener).taskEnd(ArgumentMatchers.eq(readyAsyncTask), ArgumentMatchers.eq(CANCELED), ArgumentMatchers.nullable(Exception.class));
    }

    @Test
    public void cancel_withId() {
        Mockito.doReturn(true).when(dispatcher).cancelLocked(ArgumentMatchers.any(IdentifiedTask.class));
        dispatcher.cancel(1);
        final ArgumentCaptor<IdentifiedTask> captor = ArgumentCaptor.forClass(IdentifiedTask.class);
        Mockito.verify(dispatcher).cancelLocked(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(1);
    }

    @Test
    public void cancel_bunch() throws IOException {
        TestUtils.mockOkDownload();
        final CallbackDispatcher callbackDispatcher = OkDownload.with().callbackDispatcher();
        final DownloadTask readyASyncCallTask = Mockito.mock(DownloadTask.class);
        Mockito.when(readyASyncCallTask.getId()).thenReturn(1);
        final DownloadCall readyAsyncCall = Mockito.spy(DownloadCall.create(readyASyncCallTask, false, store));
        readyAsyncCalls.add(readyAsyncCall);
        final DownloadTask runningAsyncCallTask = Mockito.mock(DownloadTask.class);
        Mockito.when(runningAsyncCallTask.getId()).thenReturn(2);
        final DownloadCall runningAsyncCall = Mockito.spy(DownloadCall.create(runningAsyncCallTask, false, store));
        runningSyncCalls.add(runningAsyncCall);
        final DownloadTask runningSyncCallTask = Mockito.mock(DownloadTask.class);
        Mockito.when(runningSyncCallTask.getId()).thenReturn(3);
        final DownloadCall runningSyncCall = Mockito.spy(DownloadCall.create(runningSyncCallTask, false, store));
        runningSyncCalls.add(runningSyncCall);
        DownloadTask[] tasks = new DownloadTask[3];
        tasks[0] = readyASyncCallTask;
        tasks[1] = runningAsyncCallTask;
        tasks[2] = runningSyncCallTask;
        dispatcher.cancel(tasks);
        ArgumentCaptor<Collection<DownloadTask>> callbackCanceledList = ArgumentCaptor.forClass(Collection.class);
        Mockito.verify(callbackDispatcher).endTasksWithCanceled(callbackCanceledList.capture());
        assertThat(callbackCanceledList.getValue()).containsExactly(readyASyncCallTask, runningAsyncCallTask, runningSyncCallTask);
        Mockito.verify(store, Mockito.never()).onTaskEnd(ArgumentMatchers.eq(1), ArgumentMatchers.eq(CANCELED), ArgumentMatchers.nullable(Exception.class));
        Mockito.verify(readyAsyncCall, Mockito.never()).cancel();
        Mockito.verify(runningAsyncCall).cancel();
        Mockito.verify(runningSyncCall).cancel();
    }

    @Test
    public void cancelAll() {
        List<DownloadCall> mockReadyAsyncCalls = new ArrayList<>();
        mockReadyAsyncCalls.add(Mockito.spy(DownloadCall.create(mockTask(1), true, store)));
        mockReadyAsyncCalls.add(Mockito.spy(DownloadCall.create(mockTask(2), true, store)));
        mockReadyAsyncCalls.add(Mockito.spy(DownloadCall.create(mockTask(3), true, store)));
        mockReadyAsyncCalls.add(Mockito.spy(DownloadCall.create(mockTask(4), true, store)));
        readyAsyncCalls.addAll(mockReadyAsyncCalls);
        runningAsyncCalls.add(Mockito.spy(DownloadCall.create(mockTask(5), true, store)));
        runningAsyncCalls.add(Mockito.spy(DownloadCall.create(mockTask(6), true, store)));
        runningAsyncCalls.add(Mockito.spy(DownloadCall.create(mockTask(7), true, store)));
        runningAsyncCalls.add(Mockito.spy(DownloadCall.create(mockTask(8), true, store)));
        runningSyncCalls.add(Mockito.spy(DownloadCall.create(mockTask(9), false, store)));
        runningSyncCalls.add(Mockito.spy(DownloadCall.create(mockTask(10), false, store)));
        runningSyncCalls.add(Mockito.spy(DownloadCall.create(mockTask(11), false, store)));
        runningSyncCalls.add(Mockito.spy(DownloadCall.create(mockTask(12), false, store)));
        dispatcher.cancelAll();
        // verify readyAsyncCalls
        assertThat(readyAsyncCalls).isEmpty();
        final ArgumentCaptor<Collection<DownloadTask>> callCaptor = ArgumentCaptor.forClass(Collection.class);
        Mockito.verify(OkDownload.with().callbackDispatcher()).endTasksWithCanceled(callCaptor.capture());
        assertThat(callCaptor.getValue()).hasSize(12);
        for (DownloadCall call : mockReadyAsyncCalls) {
            Mockito.verify(call, Mockito.never()).cancel();
        }
        // verify runningAsyncCalls
        assertThat(runningAsyncCalls).hasSize(4);
        for (DownloadCall call : runningAsyncCalls) {
            Mockito.verify(call).cancel();
        }
        // verify runningSyncCalls
        assertThat(runningSyncCalls).hasSize(4);
        for (DownloadCall call : runningSyncCalls) {
            Mockito.verify(call).cancel();
        }
    }

    @Test(expected = AssertionError.class)
    public void finish_removeFailed_exception() {
        dispatcher.finish(Mockito.mock(DownloadCall.class));
    }

    @Test
    public void finish_asyncExecuted() {
        final DownloadCall mockRunningCall = DownloadCall.create(mockTask(), true, store);
        runningAsyncCalls.add(mockRunningCall);
        final DownloadCall mockReadyCall = DownloadCall.create(mockTask(), true, store);
        readyAsyncCalls.add(mockReadyCall);
        dispatcher.finish(mockRunningCall);
        Mockito.verify(runningAsyncCalls).remove(mockRunningCall);
        assertThat(runningAsyncCalls).containsExactly(mockReadyCall);
        assertThat(readyAsyncCalls).isEmpty();
        final ExecutorService executorService = dispatcher.getExecutorService();
        Mockito.verify(executorService).execute(mockReadyCall);
    }

    @Test
    public void finish_removeCallFromRightList() {
        final DownloadCall finishingSyncCall = DownloadCall.create(mockTask(1), false, store);
        final DownloadCall finishingAsyncCall = DownloadCall.create(mockTask(2), true, store);
        final DownloadCall runningAsyncCall = DownloadCall.create(mockTask(3), true, store);
        final DownloadCall runningSyncCall = DownloadCall.create(mockTask(4), false, store);
        finishingCalls.add(finishingAsyncCall);
        finishingCalls.add(finishingSyncCall);
        runningAsyncCalls.add(runningAsyncCall);
        runningSyncCalls.add(runningSyncCall);
        dispatcher.finish(finishingAsyncCall);
        assertThat(finishingCalls).hasSize(1);
        Mockito.verify(finishingCalls).remove(finishingAsyncCall);
        Mockito.verify(runningAsyncCalls, Mockito.never()).remove(ArgumentMatchers.any(DownloadCall.class));
        Mockito.verify(runningSyncCalls, Mockito.never()).remove(ArgumentMatchers.any(DownloadCall.class));
        dispatcher.finish(finishingSyncCall);
        assertThat(finishingCalls).hasSize(0);
        Mockito.verify(finishingCalls).remove(finishingSyncCall);
        Mockito.verify(runningAsyncCalls, Mockito.never()).remove(ArgumentMatchers.any(DownloadCall.class));
        Mockito.verify(runningSyncCalls, Mockito.never()).remove(ArgumentMatchers.any(DownloadCall.class));
        dispatcher.finish(runningAsyncCall);
        Mockito.verify(runningAsyncCalls).remove(runningAsyncCall);
        Mockito.verify(runningSyncCalls, Mockito.never()).remove(ArgumentMatchers.any(DownloadCall.class));
        dispatcher.finish(runningSyncCall);
        Mockito.verify(runningSyncCalls).remove(ArgumentMatchers.any(DownloadCall.class));
    }

    @Test
    public void isFileConflictAfterRun() {
        final DownloadTask mockAsyncTask = mockTask();
        final DownloadTask samePathTask = mockTask();
        Mockito.doReturn(mockAsyncTask.getFile()).when(samePathTask).getFile();
        DownloadCall call = Mockito.spy(DownloadCall.create(mockAsyncTask, true, store));
        runningAsyncCalls.add(call);
        boolean isConflict = dispatcher.isFileConflictAfterRun(samePathTask);
        assertThat(isConflict).isTrue();
        // ignore canceled
        Mockito.when(call.isCanceled()).thenReturn(true);
        isConflict = dispatcher.isFileConflictAfterRun(samePathTask);
        assertThat(isConflict).isFalse();
        // not canceled and another path task
        Mockito.when(call.isCanceled()).thenReturn(false);
        final DownloadTask mockSyncTask = mockTask();
        Mockito.doReturn(mockSyncTask.getFile()).when(samePathTask).getFile();
        runningSyncCalls.add(DownloadCall.create(mockSyncTask, false, store));
        isConflict = dispatcher.isFileConflictAfterRun(samePathTask);
        assertThat(isConflict).isTrue();
        final DownloadTask noSamePathTask = mockTask();
        isConflict = dispatcher.isFileConflictAfterRun(noSamePathTask);
        assertThat(isConflict).isFalse();
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void setMaxParallelRunningCount() {
        Mockito.doReturn(Mockito.mock(DownloadDispatcherTest.MockDownloadDispatcher.class)).when(OkDownload.with()).downloadDispatcher();
        thrown.expect(IllegalStateException.class);
        DownloadDispatcher.setMaxParallelRunningCount(1);
        Mockito.doReturn(dispatcher).when(OkDownload.with()).breakpointStore();
        DownloadDispatcher.setMaxParallelRunningCount(0);
        assertThat(dispatcher.maxParallelRunningCount).isEqualTo(1);
        DownloadDispatcher.setMaxParallelRunningCount(2);
        assertThat(dispatcher.maxParallelRunningCount).isEqualTo(2);
    }

    @Test
    public void inspectCompleted() throws IOException {
        TestUtils.mockOkDownload();
        final CallbackDispatcher callbackDispatcher = OkDownload.with().callbackDispatcher();
        final DownloadTask task = Mockito.mock(DownloadTask.class);
        Mockito.when(task.isPassIfAlreadyCompleted()).thenReturn(true);
        Mockito.when(task.getId()).thenReturn(0);
        Mockito.when(task.getUrl()).thenReturn("url");
        Mockito.when(task.getParentFile()).thenReturn(existFile.getParentFile());
        final BreakpointStore store = OkDownload.with().breakpointStore();
        Mockito.doReturn(existFile.getName()).when(store).getResponseFilename("url");
        // valid filename failed.
        final DownloadStrategy downloadStrategy = OkDownload.with().downloadStrategy();
        Mockito.doReturn(false).when(downloadStrategy).validFilenameFromStore(task);
        assertThat(dispatcher.inspectCompleted(task)).isFalse();
        Mockito.verify(callbackDispatcher, Mockito.never()).dispatch();
        // valid filename success.
        Mockito.doReturn(true).when(downloadStrategy).validFilenameFromStore(task);
        assertThat(dispatcher.inspectCompleted(task)).isTrue();
        final DownloadListener listener = callbackDispatcher.dispatch();
        Mockito.verify(listener).taskEnd(ArgumentMatchers.eq(task), ArgumentMatchers.eq(COMPLETED), ArgumentMatchers.nullable(Exception.class));
        Mockito.verify(downloadStrategy).validInfoOnCompleted(ArgumentMatchers.eq(task), ArgumentMatchers.eq(this.store));
    }

    @Test
    public void inspectCompleted_collection() throws IOException {
        TestUtils.mockOkDownload();
        final DownloadStrategy downloadStrategy = OkDownload.with().downloadStrategy();
        final CallbackDispatcher callbackDispatcher = OkDownload.with().callbackDispatcher();
        final DownloadListener listener = callbackDispatcher.dispatch();
        final DownloadTask task = Mockito.mock(DownloadTask.class);
        Mockito.when(task.isPassIfAlreadyCompleted()).thenReturn(true);
        Mockito.when(task.getId()).thenReturn(0);
        Mockito.when(task.getUrl()).thenReturn("url");
        Mockito.when(task.getParentFile()).thenReturn(existFile.getParentFile());
        final BreakpointStore store = OkDownload.with().breakpointStore();
        Mockito.doReturn(existFile.getName()).when(store).getResponseFilename("url");
        Mockito.doReturn(true).when(downloadStrategy).validFilenameFromStore(task);
        final Collection<DownloadTask> completedCollection = new ArrayList<>();
        assertThat(dispatcher.inspectCompleted(task, completedCollection)).isTrue();
        Mockito.verify(listener, Mockito.never()).taskEnd(ArgumentMatchers.eq(task), ArgumentMatchers.any(EndCause.class), ArgumentMatchers.nullable(Exception.class));
        assertThat(completedCollection).containsExactly(task);
    }

    @Test
    public void inspectForConflict_sameTask() throws IOException {
        TestUtils.mockOkDownload();
        final CallbackDispatcher callbackDispatcher = OkDownload.with().callbackDispatcher();
        final DownloadListener listener = callbackDispatcher.dispatch();
        DownloadTask task = Mockito.mock(DownloadTask.class);
        final Collection<DownloadCall> calls = new ArrayList<>();
        final Collection<DownloadTask> sameTaskList = new ArrayList<>();
        final Collection<DownloadTask> fileBusyList = new ArrayList<>();
        final DownloadCall call = Mockito.mock(DownloadCall.class);
        Mockito.when(call.equalsTask(task)).thenReturn(true);
        calls.add(call);
        assertThat(dispatcher.inspectForConflict(task, calls, sameTaskList, fileBusyList)).isTrue();
        assertThat(sameTaskList).containsExactly(task);
        assertThat(fileBusyList).isEmpty();
        Mockito.verify(listener, Mockito.never()).taskEnd(ArgumentMatchers.eq(task), ArgumentMatchers.any(EndCause.class), ArgumentMatchers.nullable(Exception.class));
    }

    @Test
    public void inspectForConflict_sameTask_isFinishing() {
        final DownloadTask task = Mockito.mock(DownloadTask.class);
        final DownloadCall call = Mockito.mock(DownloadCall.class);
        Mockito.when(call.equalsTask(task)).thenReturn(true);
        Mockito.when(call.isFinishing()).thenReturn(true);
        final Collection<DownloadCall> calls = new ArrayList<>();
        final Collection<DownloadTask> sameTaskList = new ArrayList<>();
        final Collection<DownloadTask> fileBusyList = new ArrayList<>();
        calls.add(call);
        assertThat(dispatcher.inspectForConflict(task, calls, sameTaskList, fileBusyList)).isFalse();
        assertThat(fileBusyList).isEmpty();
        assertThat(sameTaskList).isEmpty();
        assertThat(finishingCalls).hasSize(1);
    }

    @Test
    public void inspectForConflict_fileBusy() throws IOException {
        TestUtils.mockOkDownload();
        final CallbackDispatcher callbackDispatcher = OkDownload.with().callbackDispatcher();
        final DownloadListener listener = callbackDispatcher.dispatch();
        DownloadTask task = Mockito.mock(DownloadTask.class);
        final Collection<DownloadCall> calls = new ArrayList<>();
        final Collection<DownloadTask> sameTaskList = new ArrayList<>();
        final Collection<DownloadTask> fileBusyList = new ArrayList<>();
        final DownloadCall call = Mockito.mock(DownloadCall.class);
        final File file = Mockito.mock(File.class);
        Mockito.when(task.getFile()).thenReturn(file);
        Mockito.when(call.getFile()).thenReturn(file);
        calls.add(call);
        assertThat(dispatcher.inspectForConflict(task, calls, sameTaskList, fileBusyList)).isTrue();
        assertThat(sameTaskList).isEmpty();
        assertThat(fileBusyList).containsExactly(task);
        Mockito.verify(listener, Mockito.never()).taskEnd(ArgumentMatchers.eq(task), ArgumentMatchers.any(EndCause.class), ArgumentMatchers.nullable(Exception.class));
    }

    @Test
    public void findSameTask_readyAsyncCall() {
        final DownloadTask task = Mockito.mock(DownloadTask.class);
        final DownloadStore store = Mockito.mock(DownloadStore.class);
        final DownloadCall canceledCall = Mockito.spy(DownloadCall.create(Mockito.mock(DownloadTask.class), true, store));
        final DownloadCall nonCanceledCall = Mockito.spy(DownloadCall.create(Mockito.mock(DownloadTask.class), true, store));
        Mockito.when(canceledCall.isCanceled()).thenReturn(true);
        Mockito.when(canceledCall.equalsTask(task)).thenReturn(true);
        Mockito.when(nonCanceledCall.equalsTask(task)).thenReturn(true);
        readyAsyncCalls.add(canceledCall);
        readyAsyncCalls.add(nonCanceledCall);
        assertThat(dispatcher.findSameTask(task)).isEqualTo(nonCanceledCall.task);
    }

    @Test
    public void findSameTask_runningAsyncCall() {
        final DownloadTask task = Mockito.mock(DownloadTask.class);
        final DownloadStore store = Mockito.mock(DownloadStore.class);
        final DownloadCall canceledCall = Mockito.spy(DownloadCall.create(Mockito.mock(DownloadTask.class), true, store));
        final DownloadCall nonCanceledCall = Mockito.spy(DownloadCall.create(Mockito.mock(DownloadTask.class), true, store));
        Mockito.when(canceledCall.equalsTask(task)).thenReturn(true);
        Mockito.when(canceledCall.isCanceled()).thenReturn(true);
        Mockito.when(nonCanceledCall.equalsTask(task)).thenReturn(true);
        runningAsyncCalls.add(canceledCall);
        runningAsyncCalls.add(nonCanceledCall);
        assertThat(dispatcher.findSameTask(task)).isEqualTo(nonCanceledCall.task);
    }

    @Test
    public void findSameTask_runningSyncCall() {
        final DownloadTask task = Mockito.mock(DownloadTask.class);
        final DownloadStore store = Mockito.mock(DownloadStore.class);
        final DownloadCall canceledCall = Mockito.spy(DownloadCall.create(Mockito.mock(DownloadTask.class), false, store));
        final DownloadCall nonCanceledCall = Mockito.spy(DownloadCall.create(Mockito.mock(DownloadTask.class), false, store));
        Mockito.when(canceledCall.isCanceled()).thenReturn(true);
        Mockito.when(canceledCall.equalsTask(task)).thenReturn(true);
        Mockito.when(nonCanceledCall.equalsTask(task)).thenReturn(true);
        runningSyncCalls.add(canceledCall);
        runningSyncCalls.add(nonCanceledCall);
        assertThat(dispatcher.findSameTask(task)).isEqualTo(nonCanceledCall.task);
    }

    @Test
    public void findSameTask_nonMatch() {
        final DownloadTask task = Mockito.mock(DownloadTask.class);
        assertThat(dispatcher.findSameTask(task)).isNull();
    }

    @Test
    public void isRunning_async_true() {
        final DownloadCall mockRunningCall = Mockito.spy(DownloadCall.create(mockTask(), true, store));
        runningAsyncCalls.add(mockRunningCall);
        Mockito.when(mockRunningCall.isCanceled()).thenReturn(false);
        final boolean result = dispatcher.isRunning(mockRunningCall.task);
        assertThat(result).isEqualTo(true);
    }

    @Test
    public void isRunning_async_false() {
        final DownloadCall mockRunningCall = Mockito.spy(DownloadCall.create(mockTask(1), true, store));
        runningAsyncCalls.add(mockRunningCall);
        // because of cancelled
        Mockito.when(mockRunningCall.isCanceled()).thenReturn(true);
        boolean result = dispatcher.isRunning(mockRunningCall.task);
        assertThat(result).isEqualTo(false);
        // because of no running task
        runningAsyncCalls.clear();
        Mockito.when(mockRunningCall.isCanceled()).thenReturn(false);
        result = dispatcher.isRunning(mockRunningCall.task);
        assertThat(result).isEqualTo(false);
        // because of the task is not in the running list
        runningAsyncCalls.add(mockRunningCall);
        result = dispatcher.isRunning(mockTask(2));
        assertThat(result).isEqualTo(false);
    }

    @Test
    public void isRunning_sync_true() {
        final DownloadCall mockRunningCall = Mockito.spy(DownloadCall.create(mockTask(), false, store));
        runningSyncCalls.add(mockRunningCall);
        Mockito.when(mockRunningCall.isCanceled()).thenReturn(false);
        final boolean result = dispatcher.isRunning(mockRunningCall.task);
        assertThat(result).isEqualTo(true);
    }

    @Test
    public void isRunning_sync_false() {
        final DownloadCall mockRunningCall = Mockito.spy(DownloadCall.create(mockTask(1), false, store));
        runningSyncCalls.add(mockRunningCall);
        // because of cancelled
        Mockito.when(mockRunningCall.isCanceled()).thenReturn(true);
        boolean result = dispatcher.isRunning(mockRunningCall.task);
        assertThat(result).isEqualTo(false);
        // because of no running task
        runningSyncCalls.clear();
        Mockito.when(mockRunningCall.isCanceled()).thenReturn(false);
        result = dispatcher.isRunning(mockRunningCall.task);
        assertThat(result).isEqualTo(false);
        // because of the task is not in the running list
        runningSyncCalls.add(mockRunningCall);
        result = dispatcher.isRunning(mockTask(2));
        assertThat(result).isEqualTo(false);
    }

    @Test
    public void isPending_true() {
        final DownloadCall mockReadyCall = Mockito.spy(DownloadCall.create(mockTask(1), true, store));
        Mockito.when(mockReadyCall.isCanceled()).thenReturn(false);
        readyAsyncCalls.add(mockReadyCall);
        boolean result = dispatcher.isPending(mockReadyCall.task);
        assertThat(result).isEqualTo(true);
    }

    @Test
    public void isPending_false() {
        // because of no pending task
        boolean result = dispatcher.isPending(mockTask(0));
        assertThat(result).isEqualTo(false);
        // because of the task is not in pending list
        final DownloadCall pendingCall1 = Mockito.spy(DownloadCall.create(mockTask(1), true, store));
        Mockito.when(pendingCall1.isCanceled()).thenReturn(false);
        readyAsyncCalls.add(pendingCall1);
        result = dispatcher.isPending(mockTask(0));
        assertThat(result).isEqualTo(false);
        // because of the task is cancelled
        Mockito.when(pendingCall1.isCanceled()).thenReturn(true);
        result = dispatcher.isPending(pendingCall1.task);
        assertThat(result).isEqualTo(false);
    }

    private static class MockDownloadDispatcher extends DownloadDispatcher {}
}

