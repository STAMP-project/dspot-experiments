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


import BaseDownloadTask.FinishListener;
import CompatListenerAssist.CompatListenerAssistCallback;
import DownloadTaskAdapter.KEY_TASK_ADAPTER;
import EndCause.CANCELED;
import EndCause.COMPLETED;
import EndCause.ERROR;
import EndCause.FILE_BUSY;
import EndCause.PRE_ALLOCATE_FAILED;
import EndCause.SAME_TASK_BUSY;
import android.os.Handler;
import com.liulishuo.filedownloader.exception.FileDownloadNetworkPolicyException;
import com.liulishuo.filedownloader.exception.FileDownloadOutOfSpaceException;
import com.liulishuo.filedownloader.exception.FileDownloadSecurityException;
import com.liulishuo.filedownloader.progress.ProgressAssist;
import com.liulishuo.filedownloader.retry.RetryAssist;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.exception.DownloadSecurityException;
import com.liulishuo.okdownload.core.exception.NetworkPolicyException;
import com.liulishuo.okdownload.core.exception.PreAllocateException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class CompatListenerAssistTest {
    @Mock
    private CompatListenerAssistCallback callback;

    @Mock
    private Handler uiHander;

    private CompatListenerAssist compatListenerAssist;

    @Test
    public void taskStart() {
        final DownloadTask mockDownloadTask = Mockito.mock(DownloadTask.class);
        final DownloadTaskAdapter mockTaskAdapter = Mockito.mock(DownloadTaskAdapter.class);
        Mockito.when(mockDownloadTask.getTag(KEY_TASK_ADAPTER)).thenReturn(null);
        compatListenerAssist.taskStart(mockDownloadTask);
        Mockito.verify(callback, Mockito.never()).pending(ArgumentMatchers.any(DownloadTaskAdapter.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
        Mockito.verify(callback, Mockito.never()).started(ArgumentMatchers.any(DownloadTaskAdapter.class));
        Mockito.when(mockDownloadTask.getTag(KEY_TASK_ADAPTER)).thenReturn(mockTaskAdapter);
        Mockito.when(mockTaskAdapter.getSoFarBytesInLong()).thenReturn(1L);
        Mockito.when(mockTaskAdapter.getTotalBytesInLong()).thenReturn(2L);
        compatListenerAssist.taskStart(mockDownloadTask);
        Mockito.verify(callback).pending(mockTaskAdapter, 1L, 2L);
        Mockito.verify(callback).started(mockTaskAdapter);
    }

    @Test
    public void connectStart() {
        final DownloadTask mockTask = Mockito.mock(DownloadTask.class);
        final DownloadTaskAdapter mockTaskAdapter = Mockito.mock(DownloadTaskAdapter.class);
        final ProgressAssist mockProgressAssist = Mockito.mock(ProgressAssist.class);
        Mockito.when(mockTask.getTag(KEY_TASK_ADAPTER)).thenReturn(null);
        Mockito.when(mockTaskAdapter.getProgressAssist()).thenReturn(mockProgressAssist);
        assertThat(compatListenerAssist.taskConnected.get()).isFalse();
        compatListenerAssist.connectStart(mockTask);
        assertThat(compatListenerAssist.taskConnected.get()).isTrue();
        Mockito.verify(mockTaskAdapter, Mockito.never()).getSoFarBytesInLong();
        Mockito.verify(mockTaskAdapter, Mockito.never()).getTotalBytesInLong();
        Mockito.verify(mockTaskAdapter, Mockito.never()).getProgressAssist();
        Mockito.verify(callback, Mockito.never()).connected(ArgumentMatchers.any(DownloadTaskAdapter.class), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
        compatListenerAssist.taskConnected.set(false);
        Mockito.when(mockTask.getTag(KEY_TASK_ADAPTER)).thenReturn(mockTaskAdapter);
        Mockito.when(mockTaskAdapter.getSoFarBytesInLong()).thenReturn(1L);
        Mockito.when(mockTaskAdapter.getTotalBytesInLong()).thenReturn(2L);
        compatListenerAssist.connectStart(mockTask);
        Mockito.verify(callback).connected(mockTaskAdapter, compatListenerAssist.getEtag(), compatListenerAssist.isResumable(), 1L, 2L);
        Mockito.verify(mockProgressAssist).calculateCallbackMinIntervalBytes(2);
        Mockito.verify(mockProgressAssist).initSofarBytes(1);
    }

    @Test
    public void fetchProgress() {
        final DownloadTask mockTask = Mockito.mock(DownloadTask.class);
        final DownloadTaskAdapter mockTaskAdapter = Mockito.mock(DownloadTaskAdapter.class);
        final ProgressAssist mockProgressAssist = Mockito.mock(ProgressAssist.class);
        Mockito.when(mockTask.getTag(KEY_TASK_ADAPTER)).thenReturn(null);
        Mockito.when(mockTaskAdapter.getProgressAssist()).thenReturn(mockProgressAssist);
        compatListenerAssist.fetchProgress(mockTask, 5);
        Mockito.verify(mockTaskAdapter, Mockito.never()).getProgressAssist();
        Mockito.verify(mockProgressAssist, Mockito.never()).onProgress(mockTaskAdapter, 5, callback);
        Mockito.when(mockTask.getTag(KEY_TASK_ADAPTER)).thenReturn(mockTaskAdapter);
        compatListenerAssist.fetchProgress(mockTask, 5);
        Mockito.verify(mockProgressAssist).onProgress(mockTaskAdapter, 5, callback);
    }

    @Test
    public void taskEnd_doNothing() {
        final DownloadTask mockTask = Mockito.mock(DownloadTask.class);
        Mockito.when(mockTask.getTag(KEY_TASK_ADAPTER)).thenReturn(null);
        compatListenerAssist.taskEnd(mockTask, CANCELED, null);
        Mockito.verify(compatListenerAssist, Mockito.never()).handleError(ArgumentMatchers.any(DownloadTaskAdapter.class), ArgumentMatchers.any(Exception.class));
        Mockito.verify(compatListenerAssist, Mockito.never()).handleCanceled(ArgumentMatchers.any(DownloadTaskAdapter.class));
        Mockito.verify(compatListenerAssist, Mockito.never()).handleWarn(ArgumentMatchers.any(DownloadTaskAdapter.class), ArgumentMatchers.any(EndCause.class), ArgumentMatchers.any(Exception.class));
        Mockito.verify(compatListenerAssist, Mockito.never()).handleComplete(ArgumentMatchers.any(DownloadTaskAdapter.class));
        Mockito.verify(compatListenerAssist, Mockito.never()).onTaskFinish(ArgumentMatchers.any(DownloadTaskAdapter.class));
    }

    @Test
    public void taskEnd_handleError() {
        final DownloadTask mockTask = Mockito.mock(DownloadTask.class);
        final DownloadTaskAdapter mockTaskAdapter = Mockito.mock(DownloadTaskAdapter.class);
        final ProgressAssist mockProgressAssist = Mockito.mock(ProgressAssist.class);
        Mockito.when(mockTask.getTag(KEY_TASK_ADAPTER)).thenReturn(mockTaskAdapter);
        Mockito.when(mockTaskAdapter.getProgressAssist()).thenReturn(mockProgressAssist);
        Mockito.doNothing().when(compatListenerAssist).handleError(mockTaskAdapter, null);
        Mockito.doNothing().when(compatListenerAssist).onTaskFinish(mockTaskAdapter);
        compatListenerAssist.taskEnd(mockTask, PRE_ALLOCATE_FAILED, null);
        compatListenerAssist.taskEnd(mockTask, ERROR, null);
        Mockito.verify(compatListenerAssist, Mockito.times(2)).handleError(mockTaskAdapter, null);
        Mockito.verify(compatListenerAssist, Mockito.times(2)).onTaskFinish(mockTaskAdapter);
        Mockito.verify(mockProgressAssist, Mockito.times(2)).clearProgress();
    }

    @Test
    public void taskEnd_handleCanceled() {
        final DownloadTask mockTask = Mockito.mock(DownloadTask.class);
        final DownloadTaskAdapter mockTaskAdapter = Mockito.mock(DownloadTaskAdapter.class);
        final ProgressAssist mockProgressAssist = Mockito.mock(ProgressAssist.class);
        Mockito.when(mockTask.getTag(KEY_TASK_ADAPTER)).thenReturn(mockTaskAdapter);
        Mockito.when(mockTaskAdapter.getProgressAssist()).thenReturn(mockProgressAssist);
        Mockito.doNothing().when(compatListenerAssist).handleCanceled(mockTaskAdapter);
        Mockito.doNothing().when(compatListenerAssist).onTaskFinish(mockTaskAdapter);
        compatListenerAssist.taskEnd(mockTask, CANCELED, null);
        Mockito.verify(compatListenerAssist).handleCanceled(mockTaskAdapter);
        Mockito.verify(compatListenerAssist).onTaskFinish(mockTaskAdapter);
        Mockito.verify(mockProgressAssist).clearProgress();
    }

    @Test
    public void taskEnd_handleWarn() {
        final DownloadTask mockTask = Mockito.mock(DownloadTask.class);
        final DownloadTaskAdapter mockTaskAdapter = Mockito.mock(DownloadTaskAdapter.class);
        final ProgressAssist mockProgressAssist = Mockito.mock(ProgressAssist.class);
        Mockito.when(mockTask.getTag(KEY_TASK_ADAPTER)).thenReturn(mockTaskAdapter);
        Mockito.when(mockTaskAdapter.getProgressAssist()).thenReturn(mockProgressAssist);
        Mockito.doNothing().when(compatListenerAssist).handleWarn(ArgumentMatchers.any(DownloadTaskAdapter.class), ArgumentMatchers.any(EndCause.class), ArgumentMatchers.any(Exception.class));
        Mockito.doNothing().when(compatListenerAssist).onTaskFinish(mockTaskAdapter);
        compatListenerAssist.taskEnd(mockTask, FILE_BUSY, null);
        compatListenerAssist.taskEnd(mockTask, SAME_TASK_BUSY, null);
        Mockito.verify(compatListenerAssist).handleWarn(mockTaskAdapter, FILE_BUSY, null);
        Mockito.verify(compatListenerAssist).handleWarn(mockTaskAdapter, SAME_TASK_BUSY, null);
        Mockito.verify(compatListenerAssist, Mockito.times(2)).onTaskFinish(mockTaskAdapter);
        Mockito.verify(mockProgressAssist, Mockito.times(2)).clearProgress();
    }

    @Test
    public void taskEnd_handleComplete() {
        final DownloadTask mockTask = Mockito.mock(DownloadTask.class);
        final DownloadTaskAdapter mockTaskAdapter = Mockito.mock(DownloadTaskAdapter.class);
        final ProgressAssist mockProgressAssist = Mockito.mock(ProgressAssist.class);
        Mockito.when(mockTask.getTag(KEY_TASK_ADAPTER)).thenReturn(mockTaskAdapter);
        Mockito.when(mockTaskAdapter.getProgressAssist()).thenReturn(mockProgressAssist);
        Mockito.doNothing().when(compatListenerAssist).handleComplete(mockTaskAdapter);
        Mockito.doNothing().when(compatListenerAssist).onTaskFinish(mockTaskAdapter);
        compatListenerAssist.taskEnd(mockTask, COMPLETED, null);
        Mockito.verify(compatListenerAssist).handleComplete(mockTaskAdapter);
        Mockito.verify(compatListenerAssist).onTaskFinish(mockTaskAdapter);
        Mockito.verify(mockProgressAssist).clearProgress();
    }

    @Test
    public void handleCanceled() {
        final DownloadTaskAdapter mockTaskAdapter = Mockito.mock(DownloadTaskAdapter.class);
        final ProgressAssist mockProgressAssist = Mockito.mock(ProgressAssist.class);
        Mockito.when(mockTaskAdapter.getProgressAssist()).thenReturn(mockProgressAssist);
        Mockito.when(mockProgressAssist.getSofarBytes()).thenReturn(1L);
        Mockito.when(mockTaskAdapter.getTotalBytesInLong()).thenReturn(2L);
        compatListenerAssist.handleCanceled(mockTaskAdapter);
        Mockito.verify(callback).paused(mockTaskAdapter, 1L, 2L);
    }

    @Test
    public void handleWarn() {
        final DownloadTaskAdapter mockTaskAdapter = Mockito.mock(DownloadTaskAdapter.class);
        compatListenerAssist.handleWarn(mockTaskAdapter, null, null);
        Mockito.verify(callback).warn(mockTaskAdapter);
    }

    @Test
    public void handleError_interceptByRetry() {
        final DownloadTaskAdapter mockTaskAdapter = Mockito.mock(DownloadTaskAdapter.class);
        final ProgressAssist mockProgressAssist = Mockito.mock(ProgressAssist.class);
        final RetryAssist mockRetryAssist = Mockito.mock(RetryAssist.class);
        final DownloadTask mockTask = Mockito.mock(DownloadTask.class);
        Mockito.when(mockTaskAdapter.getDownloadTask()).thenReturn(mockTask);
        Mockito.when(mockTaskAdapter.getProgressAssist()).thenReturn(mockProgressAssist);
        Mockito.when(mockTaskAdapter.getRetryAssist()).thenReturn(mockRetryAssist);
        Mockito.when(mockProgressAssist.getSofarBytes()).thenReturn(1L);
        Mockito.when(mockRetryAssist.canRetry()).thenReturn(true);
        Mockito.when(mockRetryAssist.getRetriedTimes()).thenReturn(1);
        compatListenerAssist.handleError(mockTaskAdapter, null);
        Mockito.verify(callback).retry(mockTaskAdapter, null, 2, 1L);
        Mockito.verify(mockRetryAssist).doRetry(mockTask);
        Mockito.verify(callback, Mockito.never()).error(ArgumentMatchers.any(DownloadTaskAdapter.class), ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void handleError_withNetWorkPolicyException() {
        final DownloadTaskAdapter mockTaskAdapter = Mockito.mock(DownloadTaskAdapter.class);
        Mockito.when(mockTaskAdapter.getRetryAssist()).thenReturn(null);
        final Exception netWorkPolicyException = Mockito.mock(NetworkPolicyException.class);
        compatListenerAssist.handleError(mockTaskAdapter, netWorkPolicyException);
        Mockito.verify(callback, Mockito.never()).retry(ArgumentMatchers.any(DownloadTaskAdapter.class), ArgumentMatchers.any(Throwable.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong());
        Mockito.verify(callback).error(ArgumentMatchers.any(DownloadTaskAdapter.class), ArgumentMatchers.any(FileDownloadNetworkPolicyException.class));
    }

    @Test
    public void handleError_withPreAllocateException() {
        final DownloadTaskAdapter mockTaskAdapter = Mockito.mock(DownloadTaskAdapter.class);
        final ProgressAssist mockProgressAssist = Mockito.mock(ProgressAssist.class);
        Mockito.when(mockTaskAdapter.getProgressAssist()).thenReturn(mockProgressAssist);
        Mockito.when(mockTaskAdapter.getRetryAssist()).thenReturn(null);
        Mockito.when(mockProgressAssist.getSofarBytes()).thenReturn(1L);
        final Exception mockException = Mockito.mock(PreAllocateException.class);
        compatListenerAssist.handleError(mockTaskAdapter, mockException);
        Mockito.verify(callback, Mockito.never()).retry(ArgumentMatchers.any(DownloadTaskAdapter.class), ArgumentMatchers.any(Throwable.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong());
        final ArgumentCaptor<Throwable> throwableCaptor = ArgumentCaptor.forClass(Throwable.class);
        final ArgumentCaptor<DownloadTaskAdapter> taskCaptor = ArgumentCaptor.forClass(DownloadTaskAdapter.class);
        Mockito.verify(callback).error(taskCaptor.capture(), throwableCaptor.capture());
        assertThat(taskCaptor.getValue()).isEqualTo(mockTaskAdapter);
        assertThat(throwableCaptor.getValue()).isExactlyInstanceOf(FileDownloadOutOfSpaceException.class);
    }

    @Test
    public void handleError_withDownloadSecurityException() {
        final DownloadTaskAdapter mockTaskAdapter = Mockito.mock(DownloadTaskAdapter.class);
        final ProgressAssist mockProgressAssist = Mockito.mock(ProgressAssist.class);
        Mockito.when(mockTaskAdapter.getProgressAssist()).thenReturn(mockProgressAssist);
        Mockito.when(mockTaskAdapter.getRetryAssist()).thenReturn(null);
        Mockito.when(mockProgressAssist.getSofarBytes()).thenReturn(1L);
        final Exception mockException = Mockito.mock(DownloadSecurityException.class);
        compatListenerAssist.handleError(mockTaskAdapter, mockException);
        Mockito.verify(callback, Mockito.never()).retry(ArgumentMatchers.any(DownloadTaskAdapter.class), ArgumentMatchers.any(Throwable.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong());
        final ArgumentCaptor<Throwable> throwableCaptor = ArgumentCaptor.forClass(Throwable.class);
        final ArgumentCaptor<DownloadTaskAdapter> taskCaptor = ArgumentCaptor.forClass(DownloadTaskAdapter.class);
        Mockito.verify(callback).error(taskCaptor.capture(), throwableCaptor.capture());
        assertThat(taskCaptor.getValue()).isEqualTo(mockTaskAdapter);
        assertThat(throwableCaptor.getValue()).isExactlyInstanceOf(FileDownloadSecurityException.class);
    }

    @Test
    public void handleError_withOtherException() {
        final DownloadTaskAdapter mockTaskAdapter = Mockito.mock(DownloadTaskAdapter.class);
        Mockito.when(mockTaskAdapter.getRetryAssist()).thenReturn(null);
        final Exception exception = Mockito.mock(Exception.class);
        compatListenerAssist.handleError(mockTaskAdapter, exception);
        Mockito.verify(callback, Mockito.never()).retry(ArgumentMatchers.any(DownloadTaskAdapter.class), ArgumentMatchers.any(Throwable.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong());
        Mockito.verify(callback).error(ArgumentMatchers.any(DownloadTaskAdapter.class), ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void onTaskFinish() {
        final FileDownloadList fileDownloadList = Mockito.spy(new FileDownloadList());
        FileDownloadList.setSingleton(fileDownloadList);
        final List<BaseDownloadTask.FinishListener> finishListeners = new ArrayList<>();
        final BaseDownloadTask.FinishListener mockFinishListener = Mockito.mock(FinishListener.class);
        finishListeners.add(mockFinishListener);
        final DownloadTaskAdapter mockTaskAdapter = Mockito.mock(DownloadTaskAdapter.class);
        Mockito.when(mockTaskAdapter.getFinishListeners()).thenReturn(finishListeners);
        compatListenerAssist.onTaskFinish(mockTaskAdapter);
        Mockito.verify(mockFinishListener).over(mockTaskAdapter);
        Mockito.verify(fileDownloadList).remove(mockTaskAdapter);
    }

    @Test
    public void handleComplete_syncCallback() throws Throwable {
        final DownloadTaskAdapter mockTaskAdapter = Mockito.mock(DownloadTaskAdapter.class);
        final DownloadTask mockTask = Mockito.mock(DownloadTask.class);
        Mockito.when(mockTaskAdapter.getDownloadTask()).thenReturn(mockTask);
        Mockito.when(mockTask.isAutoCallbackToUIThread()).thenReturn(false);
        compatListenerAssist.taskConnected.set(true);
        compatListenerAssist.handleComplete(mockTaskAdapter);
        Mockito.verify(callback).blockComplete(mockTaskAdapter);
        Mockito.verify(callback).completed(mockTaskAdapter);
        assertThat(compatListenerAssist.isReuseOldFile()).isFalse();
    }

    @Test
    public void handleComplete_syncCallback_error() throws Throwable {
        final DownloadTaskAdapter mockTaskAdapter = Mockito.mock(DownloadTaskAdapter.class);
        final DownloadTask mockTask = Mockito.mock(DownloadTask.class);
        final Exception mockException = Mockito.mock(Exception.class);
        Mockito.when(mockTaskAdapter.getDownloadTask()).thenReturn(mockTask);
        Mockito.when(mockTask.isAutoCallbackToUIThread()).thenReturn(false);
        Mockito.doNothing().when(compatListenerAssist).handleError(ArgumentMatchers.any(DownloadTaskAdapter.class), ArgumentMatchers.any(Exception.class));
        Mockito.doThrow(mockException).when(callback).blockComplete(mockTaskAdapter);
        compatListenerAssist.taskConnected.set(false);
        compatListenerAssist.handleComplete(mockTaskAdapter);
        Mockito.verify(callback, Mockito.never()).completed(mockTaskAdapter);
        Mockito.verify(compatListenerAssist).handleError(ArgumentMatchers.any(DownloadTaskAdapter.class), ArgumentMatchers.any(Exception.class));
        assertThat(compatListenerAssist.isReuseOldFile()).isTrue();
    }

    @Test
    public void handleBlockComplete() throws Throwable {
        final DownloadTaskAdapter mockTaskAdapter = Mockito.mock(DownloadTaskAdapter.class);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                final Runnable runnable = invocation.getArgument(0);
                runnable.run();
                return null;
            }
        }).when(uiHander).post(ArgumentMatchers.any(Runnable.class));
        compatListenerAssist.handleBlockComplete(mockTaskAdapter);
        Mockito.verify(callback).blockComplete(mockTaskAdapter);
        Mockito.verify(callback).completed(mockTaskAdapter);
    }

    @Test
    public void handleBlockComplete_error() throws Throwable {
        final DownloadTaskAdapter mockTaskAdapter = Mockito.mock(DownloadTaskAdapter.class);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                final Runnable runnable = invocation.getArgument(0);
                runnable.run();
                return null;
            }
        }).when(uiHander).post(ArgumentMatchers.any(Runnable.class));
        Mockito.doThrow(Mockito.mock(Exception.class)).when(callback).blockComplete(mockTaskAdapter);
        Mockito.doNothing().when(compatListenerAssist).handleError(ArgumentMatchers.any(DownloadTaskAdapter.class), ArgumentMatchers.any(Exception.class));
        compatListenerAssist.handleBlockComplete(mockTaskAdapter);
        Mockito.verify(compatListenerAssist).handleError(ArgumentMatchers.any(DownloadTaskAdapter.class), ArgumentMatchers.any(Exception.class));
        Mockito.verify(callback, Mockito.never()).completed(mockTaskAdapter);
    }
}

