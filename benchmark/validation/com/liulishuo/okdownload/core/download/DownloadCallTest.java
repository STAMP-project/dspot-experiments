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
package com.liulishuo.okdownload.core.download;


import EndCause.COMPLETED;
import EndCause.ERROR;
import EndCause.FILE_BUSY;
import EndCause.PRE_ALLOCATE_FAILED;
import com.liulishuo.okdownload.DownloadListener;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.OkDownload;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.breakpoint.DownloadStore;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.liulishuo.okdownload.core.dispatcher.CallbackDispatcher;
import com.liulishuo.okdownload.core.dispatcher.DownloadDispatcher;
import com.liulishuo.okdownload.core.file.FileLock;
import com.liulishuo.okdownload.core.file.MultiPointOutputStream;
import com.liulishuo.okdownload.core.file.ProcessFileStrategy;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static DownloadCall.MAX_COUNT_RETRY_FOR_PRECONDITION_FAILED;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class DownloadCallTest {
    private DownloadCall call;

    @Mock
    private DownloadTask task;

    @Mock
    private BreakpointInfo info;

    @Mock
    private DownloadStore store;

    @Mock
    private FileLock fileLock;

    @Test
    public void execute_createIfNon() throws IOException, InterruptedException {
        Mockito.when(store.get(ArgumentMatchers.anyInt())).thenReturn(null);
        Mockito.when(store.createAndInsert(task)).thenReturn(info);
        Mockito.doReturn(Mockito.mock(BreakpointRemoteCheck.class)).when(call).createRemoteCheck(ArgumentMatchers.eq(info));
        Mockito.doReturn(Mockito.mock(BreakpointLocalCheck.class)).when(call).createLocalCheck(ArgumentMatchers.eq(info), ArgumentMatchers.anyLong());
        Mockito.doNothing().when(call).start(ArgumentMatchers.any(DownloadCache.class), ArgumentMatchers.eq(info));
        call.execute();
        Mockito.verify(store).createAndInsert(task);
        Mockito.verify(call).setInfoToTask(ArgumentMatchers.eq(info));
    }

    @Test
    public void execute_remoteCheck() throws IOException, InterruptedException {
        final BreakpointRemoteCheck remoteCheck = Mockito.mock(BreakpointRemoteCheck.class);
        Mockito.doReturn(remoteCheck).when(call).createRemoteCheck(ArgumentMatchers.eq(info));
        Mockito.doReturn(Mockito.mock(BreakpointLocalCheck.class)).when(call).createLocalCheck(ArgumentMatchers.eq(info), ArgumentMatchers.anyLong());
        Mockito.doNothing().when(call).start(ArgumentMatchers.any(DownloadCache.class), ArgumentMatchers.eq(info));
        call.execute();
        Mockito.verify(remoteCheck).check();
    }

    @Test
    public void execute_waitForRelease() throws IOException, InterruptedException {
        setupFileStrategy();
        final BreakpointRemoteCheck remoteCheck = Mockito.mock(BreakpointRemoteCheck.class);
        Mockito.doReturn(remoteCheck).when(call).createRemoteCheck(ArgumentMatchers.eq(info));
        Mockito.doReturn(Mockito.mock(BreakpointLocalCheck.class)).when(call).createLocalCheck(ArgumentMatchers.eq(info), ArgumentMatchers.anyLong());
        Mockito.doNothing().when(call).start(ArgumentMatchers.any(DownloadCache.class), ArgumentMatchers.eq(info));
        Mockito.when(task.getFile()).thenReturn(new File("certain-path"));
        call.execute();
        Mockito.verify(fileLock).waitForRelease(ArgumentMatchers.eq(new File("certain-path").getAbsolutePath()));
    }

    @Test
    public void execute_reuseAnotherInfo() throws IOException, InterruptedException {
        Mockito.doReturn(Mockito.mock(BreakpointRemoteCheck.class)).when(call).createRemoteCheck(ArgumentMatchers.eq(info));
        Mockito.doReturn(Mockito.mock(BreakpointLocalCheck.class)).when(call).createLocalCheck(ArgumentMatchers.eq(info), ArgumentMatchers.anyLong());
        Mockito.doNothing().when(call).start(ArgumentMatchers.any(DownloadCache.class), ArgumentMatchers.eq(info));
        call.execute();
        final DownloadStrategy strategy = OkDownload.with().downloadStrategy();
        Mockito.verify(strategy).inspectAnotherSameInfo(ArgumentMatchers.eq(task), ArgumentMatchers.eq(info), ArgumentMatchers.eq(0L));
    }

    @Test
    public void execute_localCheck() throws InterruptedException {
        final BreakpointLocalCheck localCheck = Mockito.mock(BreakpointLocalCheck.class);
        final BreakpointRemoteCheck remoteCheck = Mockito.mock(BreakpointRemoteCheck.class);
        Mockito.doReturn(remoteCheck).when(call).createRemoteCheck(ArgumentMatchers.eq(info));
        Mockito.doReturn(localCheck).when(call).createLocalCheck(ArgumentMatchers.eq(info), ArgumentMatchers.anyLong());
        Mockito.doNothing().when(call).start(ArgumentMatchers.any(DownloadCache.class), ArgumentMatchers.eq(info));
        Mockito.when(remoteCheck.isResumable()).thenReturn(false);
        call.execute();
        Mockito.verify(localCheck, Mockito.never()).check();
        Mockito.when(remoteCheck.isResumable()).thenReturn(true);
        call.execute();
        Mockito.verify(localCheck).check();
    }

    @Test
    public void execute_assembleBlockData() throws IOException, InterruptedException {
        setupFileStrategy();
        final ProcessFileStrategy fileStrategy = OkDownload.with().processFileStrategy();
        final BreakpointLocalCheck localCheck = Mockito.mock(BreakpointLocalCheck.class);
        final BreakpointRemoteCheck remoteCheck = Mockito.mock(BreakpointRemoteCheck.class);
        final ResumeFailedCause failedCauseByRemote = Mockito.mock(ResumeFailedCause.class);
        final ResumeFailedCause failedCauseByLocal = Mockito.mock(ResumeFailedCause.class);
        Mockito.doReturn(remoteCheck).when(call).createRemoteCheck(ArgumentMatchers.eq(info));
        Mockito.doReturn(localCheck).when(call).createLocalCheck(ArgumentMatchers.eq(info), ArgumentMatchers.anyLong());
        Mockito.doReturn(failedCauseByRemote).when(remoteCheck).getCauseOrThrow();
        Mockito.doReturn(failedCauseByLocal).when(localCheck).getCauseOrThrow();
        Mockito.doNothing().when(call).assembleBlockAndCallbackFromBeginning(ArgumentMatchers.eq(info), ArgumentMatchers.eq(remoteCheck), ArgumentMatchers.any(ResumeFailedCause.class));
        Mockito.doNothing().when(call).start(ArgumentMatchers.any(DownloadCache.class), ArgumentMatchers.eq(info));
        Mockito.when(remoteCheck.isResumable()).thenReturn(false);
        call.execute();
        Mockito.verify(call).assembleBlockAndCallbackFromBeginning(ArgumentMatchers.eq(info), ArgumentMatchers.eq(remoteCheck), ArgumentMatchers.eq(failedCauseByRemote));
        Mockito.verify(fileStrategy).discardProcess(ArgumentMatchers.eq(task));
        Mockito.when(remoteCheck.isResumable()).thenReturn(true);
        Mockito.when(localCheck.isDirty()).thenReturn(false);
        call.execute();
        Mockito.verify(call, Mockito.never()).assembleBlockAndCallbackFromBeginning(ArgumentMatchers.eq(info), ArgumentMatchers.eq(remoteCheck), ArgumentMatchers.eq(failedCauseByLocal));
        // callback download from breakpoint.
        final DownloadListener listener = OkDownload.with().callbackDispatcher().dispatch();
        Mockito.verify(listener).downloadFromBreakpoint(ArgumentMatchers.eq(task), ArgumentMatchers.eq(info));
        Mockito.when(localCheck.isDirty()).thenReturn(true);
        call.execute();
        Mockito.verify(call).assembleBlockAndCallbackFromBeginning(ArgumentMatchers.eq(info), ArgumentMatchers.eq(remoteCheck), ArgumentMatchers.eq(failedCauseByLocal));
        Mockito.verify(fileStrategy, Mockito.times(2)).discardProcess(ArgumentMatchers.eq(task));
    }

    @Test
    public void execute_start() throws InterruptedException {
        Mockito.doReturn(Mockito.mock(BreakpointRemoteCheck.class)).when(call).createRemoteCheck(ArgumentMatchers.eq(info));
        Mockito.doReturn(Mockito.mock(BreakpointLocalCheck.class)).when(call).createLocalCheck(ArgumentMatchers.eq(info), ArgumentMatchers.anyLong());
        Mockito.doNothing().when(call).start(ArgumentMatchers.any(DownloadCache.class), ArgumentMatchers.eq(info));
        call.execute();
        Mockito.verify(call).start(ArgumentMatchers.any(DownloadCache.class), ArgumentMatchers.eq(info));
    }

    @Test
    public void execute_preconditionFailed() throws IOException, InterruptedException {
        setupFileStrategy();
        final DownloadCache cache = Mockito.mock(DownloadCache.class);
        Mockito.doReturn(cache).when(call).createCache(ArgumentMatchers.eq(info));
        Mockito.when(cache.isPreconditionFailed()).thenReturn(true, false);
        final ResumeFailedCause resumeFailedCause = Mockito.mock(ResumeFailedCause.class);
        Mockito.doReturn(resumeFailedCause).when(cache).getResumeFailedCause();
        Mockito.doNothing().when(call).start(ArgumentMatchers.eq(cache), ArgumentMatchers.eq(info));
        Mockito.doReturn(Mockito.mock(BreakpointRemoteCheck.class)).when(call).createRemoteCheck(ArgumentMatchers.eq(info));
        Mockito.doReturn(Mockito.mock(BreakpointLocalCheck.class)).when(call).createLocalCheck(ArgumentMatchers.eq(info), ArgumentMatchers.anyLong());
        call.execute();
        Mockito.verify(call, Mockito.times(2)).start(ArgumentMatchers.eq(cache), ArgumentMatchers.eq(info));
        final ProcessFileStrategy fileStrategy = OkDownload.with().processFileStrategy();
        final int id = task.getId();
        Mockito.verify(store).remove(ArgumentMatchers.eq(id));
        Mockito.verify(fileStrategy, Mockito.times(2)).discardProcess(ArgumentMatchers.eq(task));
    }

    @Test
    public void execute_preconditionFailedMaxTimes() throws IOException, InterruptedException {
        final CallbackDispatcher dispatcher = OkDownload.with().callbackDispatcher();
        final DownloadCache cache = Mockito.mock(DownloadCache.class);
        Mockito.doReturn(cache).when(call).createCache(ArgumentMatchers.eq(info));
        Mockito.doNothing().when(call).start(ArgumentMatchers.eq(cache), ArgumentMatchers.eq(info));
        Mockito.doReturn(Mockito.mock(BreakpointRemoteCheck.class)).when(call).createRemoteCheck(ArgumentMatchers.eq(info));
        Mockito.doReturn(Mockito.mock(BreakpointLocalCheck.class)).when(call).createLocalCheck(ArgumentMatchers.eq(info), ArgumentMatchers.anyLong());
        Mockito.doReturn(Mockito.mock(DownloadListener.class)).when(dispatcher).dispatch();
        Mockito.when(cache.isPreconditionFailed()).thenReturn(true);
        call.execute();
        Mockito.verify(call, Mockito.times(((MAX_COUNT_RETRY_FOR_PRECONDITION_FAILED) + 1))).start(ArgumentMatchers.eq(cache), ArgumentMatchers.eq(info));
        // only once.
        final DownloadListener listener = OkDownload.with().callbackDispatcher().dispatch();
        Mockito.verify(listener).taskStart(ArgumentMatchers.eq(task));
    }

    @Test
    public void execute_urlIsEmpty() throws InterruptedException {
        Mockito.when(task.getUrl()).thenReturn("");
        call.execute();
        final DownloadListener listener = OkDownload.with().callbackDispatcher().dispatch();
        Mockito.verify(call, Mockito.never()).start(ArgumentMatchers.any(DownloadCache.class), ArgumentMatchers.any(BreakpointInfo.class));
        ArgumentCaptor<IOException> captor = ArgumentCaptor.forClass(IOException.class);
        Mockito.verify(listener).taskEnd(ArgumentMatchers.eq(task), ArgumentMatchers.eq(ERROR), captor.capture());
        IOException exception = captor.getValue();
        assertThat(exception.getMessage()).isEqualTo("unexpected url: ");
    }

    @Test
    public void execute_finish() throws IOException, InterruptedException {
        setupFileStrategy();
        assertThat(call.finishing).isFalse();
        final OkDownload okDownload = OkDownload.with();
        final CallbackDispatcher dispatcher = OkDownload.with().callbackDispatcher();
        final DownloadCache cache = Mockito.mock(DownloadCache.class);
        final DownloadListener listener = Mockito.mock(DownloadListener.class);
        final ProcessFileStrategy fileStrategy = okDownload.processFileStrategy();
        final IOException iOException = Mockito.mock(IOException.class);
        final MultiPointOutputStream multiPointOutputStream = Mockito.mock(MultiPointOutputStream.class);
        Mockito.doNothing().when(call).start(ArgumentMatchers.eq(cache), ArgumentMatchers.eq(info));
        Mockito.doReturn(Mockito.mock(BreakpointRemoteCheck.class)).when(call).createRemoteCheck(ArgumentMatchers.eq(info));
        Mockito.doReturn(Mockito.mock(BreakpointLocalCheck.class)).when(call).createLocalCheck(ArgumentMatchers.eq(info), ArgumentMatchers.anyLong());
        Mockito.doReturn(info).when(store).createAndInsert(ArgumentMatchers.eq(task));
        Mockito.doReturn(listener).when(dispatcher).dispatch();
        Mockito.doReturn(cache).when(call).createCache(ArgumentMatchers.eq(info));
        Mockito.when(cache.getRealCause()).thenReturn(iOException);
        Mockito.when(cache.getOutputStream()).thenReturn(multiPointOutputStream);
        call.canceled = true;
        call.execute();
        assertThat(call.finishing).isTrue();
        // no callback when cancel status, because it has been handled on the cancel operation.
        Mockito.verify(listener, Mockito.never()).taskEnd(ArgumentMatchers.any(DownloadTask.class), ArgumentMatchers.any(EndCause.class), ArgumentMatchers.nullable(Exception.class));
        call.canceled = false;
        call.execute();
        Mockito.verify(listener).taskEnd(ArgumentMatchers.eq(task), ArgumentMatchers.eq(COMPLETED), ArgumentMatchers.nullable(IOException.class));
        Mockito.verify(store).onTaskEnd(ArgumentMatchers.eq(task.getId()), ArgumentMatchers.eq(COMPLETED), ArgumentMatchers.nullable(Exception.class));
        final int id = task.getId();
        Mockito.verify(store).markFileClear(ArgumentMatchers.eq(id));
        Mockito.verify(fileStrategy).completeProcessStream(ArgumentMatchers.eq(multiPointOutputStream), ArgumentMatchers.eq(task));
        Mockito.verify(cache, Mockito.times(1)).setRedirectLocation(((String) (ArgumentMatchers.any())));
        Mockito.when(cache.isPreAllocateFailed()).thenReturn(true);
        call.execute();
        Mockito.verify(listener).taskEnd(task, PRE_ALLOCATE_FAILED, iOException);
        Mockito.verify(cache, Mockito.times(2)).setRedirectLocation(((String) (ArgumentMatchers.any())));
        Mockito.when(cache.isFileBusyAfterRun()).thenReturn(true);
        call.execute();
        Mockito.verify(listener).taskEnd(task, FILE_BUSY, null);
        Mockito.verify(cache, Mockito.times(3)).setRedirectLocation(((String) (ArgumentMatchers.any())));
        Mockito.when(cache.isServerCanceled()).thenReturn(true);
        call.execute();
        Mockito.verify(listener).taskEnd(task, ERROR, iOException);
        Mockito.verify(cache, Mockito.times(4)).setRedirectLocation(((String) (ArgumentMatchers.any())));
        Mockito.when(cache.isUserCanceled()).thenReturn(false);
        Mockito.when(cache.isServerCanceled()).thenReturn(false);
        Mockito.when(cache.isUnknownError()).thenReturn(true);
        call.execute();
        Mockito.verify(listener, Mockito.times(2)).taskEnd(task, ERROR, iOException);
        Mockito.verify(cache, Mockito.times(5)).setRedirectLocation(((String) (ArgumentMatchers.any())));
    }

    @Test
    public void finished_callToDispatch() {
        call.finished();
        Mockito.verify(OkDownload.with().downloadDispatcher()).finish(call);
    }

    @Test
    public void compareTo() {
        final DownloadCall compareCall = Mockito.mock(DownloadCall.class);
        Mockito.when(compareCall.getPriority()).thenReturn(6);
        Mockito.when(call.getPriority()).thenReturn(3);
        final int result = call.compareTo(compareCall);
        assertThat(result).isEqualTo(3);
    }

    @Test
    public void start() throws InterruptedException {
        final DownloadCache cache = Mockito.mock(DownloadCache.class);
        final MultiPointOutputStream outputStream = Mockito.mock(MultiPointOutputStream.class);
        Mockito.doNothing().when(call).startBlocks(ArgumentMatchers.<DownloadChain>anyList());
        Mockito.when(cache.getOutputStream()).thenReturn(outputStream);
        call.start(cache, info);
        ArgumentCaptor<List<DownloadChain>> captor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(call).startBlocks(captor.capture());
        final List<DownloadChain> chainList = captor.getValue();
        assertThat(chainList.size()).isEqualTo(3);
        assertThat(chainList.get(0).getBlockIndex()).isEqualTo(0);
        assertThat(chainList.get(1).getBlockIndex()).isEqualTo(1);
        assertThat(chainList.get(2).getBlockIndex()).isEqualTo(2);
        ArgumentCaptor<List<Integer>> setRequireStreamBlocksCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(outputStream).setRequireStreamBlocks(setRequireStreamBlocksCaptor.capture());
        final List<Integer> blockIndexList = setRequireStreamBlocksCaptor.getValue();
        assertThat(blockIndexList.size()).isEqualTo(3);
        assertThat(blockIndexList.get(0)).isEqualTo(0);
        assertThat(blockIndexList.get(1)).isEqualTo(1);
        assertThat(blockIndexList.get(2)).isEqualTo(2);
    }

    @Test
    public void startBlocks() throws InterruptedException {
        ArrayList<DownloadChain> runningBlockList = Mockito.spy(new ArrayList<DownloadChain>());
        call = Mockito.spy(new DownloadCall(task, false, runningBlockList, store));
        final Future mockFuture = Mockito.mock(Future.class);
        Mockito.doReturn(mockFuture).when(call).submitChain(ArgumentMatchers.any(DownloadChain.class));
        List<DownloadChain> chains = new ArrayList<>();
        chains.add(Mockito.mock(DownloadChain.class));
        chains.add(Mockito.mock(DownloadChain.class));
        chains.add(Mockito.mock(DownloadChain.class));
        call.startBlocks(chains);
        Mockito.verify(call, Mockito.times(3)).submitChain(ArgumentMatchers.any(DownloadChain.class));
        Mockito.verify(runningBlockList).addAll(ArgumentMatchers.eq(chains));
        Mockito.verify(runningBlockList).removeAll(ArgumentMatchers.eq(chains));
    }

    @Test
    public void cancel() {
        assertThat(call.cancel()).isTrue();
        // canceled
        assertThat(call.cancel()).isFalse();
    }

    @Test
    public void cancel_cache() {
        final DownloadCache cache = Mockito.mock(DownloadCache.class);
        final MultiPointOutputStream outputStream = Mockito.mock(MultiPointOutputStream.class);
        call.cache = cache;
        Mockito.when(cache.getOutputStream()).thenReturn(outputStream);
        call.cancel();
        Mockito.verify(call.cache).setUserCanceled();
        Mockito.verify(outputStream).cancelAsync();
    }

    @Test
    public void cancel_finishing() {
        call.finishing = true;
        assertThat(call.cancel()).isFalse();
        final DownloadDispatcher dispatcher = OkDownload.with().downloadDispatcher();
        Mockito.verify(dispatcher, Mockito.never()).flyingCanceled(ArgumentMatchers.eq(call));
    }

    @Test
    public void assembleBlockAndCallbackFromBeginning() {
        final BreakpointRemoteCheck remoteCheck = Mockito.mock(BreakpointRemoteCheck.class);
        final ResumeFailedCause failedCause = Mockito.mock(ResumeFailedCause.class);
        call.assembleBlockAndCallbackFromBeginning(info, remoteCheck, failedCause);
        final DownloadListener listener = OkDownload.with().callbackDispatcher().dispatch();
        Mockito.verify(listener).downloadFromBeginning(ArgumentMatchers.eq(task), ArgumentMatchers.eq(info), ArgumentMatchers.eq(failedCause));
    }
}

