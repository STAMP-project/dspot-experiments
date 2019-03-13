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
package com.liulishuo.okdownload.core.listener.assist;


import EndCause.COMPLETED;
import Listener1Assist.Listener1Callback;
import Listener1Assist.Listener1Model;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.ArgumentMatchers.eq;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class Listener1AssistTest {
    private Listener1Assist assist;

    @Mock
    private Listener1Callback callback;

    @Mock
    private DownloadTask task;

    @Mock
    private BreakpointInfo info;

    @Mock
    private ResumeFailedCause cause;

    @Mock
    private ListenerModelHandler<Listener1Assist.Listener1Model> handler;

    @Mock
    private Listener1Model model;

    private final int taskId = 1;

    @Test
    public void taskStart() {
        Mockito.when(handler.addAndGetModel(task, null)).thenReturn(model);
        assist.taskStart(task);
        Mockito.verify(callback).taskStart(eq(task), eq(model));
    }

    @Test
    public void taskEnd() {
        Mockito.when(handler.removeOrCreate(task, info)).thenReturn(model);
        assist.taskEnd(task, COMPLETED, null);
        Mockito.verify(handler).removeOrCreate(eq(task), ArgumentMatchers.nullable(BreakpointInfo.class));
        Mockito.verify(callback).taskEnd(eq(task), eq(COMPLETED), ArgumentMatchers.nullable(Exception.class), eq(model));
    }

    @Test
    public void downloadFromBeginning() {
        // no model
        Mockito.when(handler.getOrRecoverModel(task, info)).thenReturn(null);
        assist.downloadFromBeginning(task, info, cause);
        Mockito.verify(model, Mockito.never()).onInfoValid(eq(info));
        Listener1Assist.Listener1Model model = Mockito.spy(new Listener1Assist.Listener1Model(1));
        Mockito.when(handler.getOrRecoverModel(task, info)).thenReturn(model);
        assist.downloadFromBeginning(task, info, cause);
        Mockito.verify(model).onInfoValid(eq(info));
        model = new Listener1Assist.Listener1Model(1);
        Mockito.when(handler.getOrRecoverModel(task, info)).thenReturn(model);
        assist.downloadFromBeginning(task, info, cause);
        assertThat(model.isStarted).isTrue();
        assertThat(model.isFromResumed).isFalse();
        assertThat(model.isFirstConnect).isTrue();
        Mockito.verify(callback, Mockito.never()).retry(eq(task), eq(cause));
        // retry
        model.isStarted = true;
        Mockito.when(handler.getOrRecoverModel(task, info)).thenReturn(model);
        assist.downloadFromBeginning(task, info, cause);
        Mockito.verify(callback).retry(eq(task), eq(cause));
    }

    @Test
    public void downloadFromBreakpoint() {
        // no model
        Mockito.when(handler.getOrRecoverModel(task, info)).thenReturn(null);
        assist.downloadFromBreakpoint(task, info);
        Mockito.verify(model, Mockito.never()).onInfoValid(eq(info));
        Mockito.when(handler.getOrRecoverModel(task, info)).thenReturn(model);
        assist.downloadFromBreakpoint(task, info);
        Mockito.verify(model).onInfoValid(eq(info));
        // assign
        final Listener1Assist.Listener1Model model = new Listener1Assist.Listener1Model(1);
        Mockito.when(handler.getOrRecoverModel(task, info)).thenReturn(model);
        assist.downloadFromBreakpoint(task, info);
        assertThat(model.isStarted).isTrue();
        assertThat(model.isFromResumed).isTrue();
        assertThat(model.isFirstConnect).isTrue();
    }

    @Test
    public void connectEnd() {
        // no model
        Mockito.when(handler.getOrRecoverModel(task, info)).thenReturn(null);
        assist.connectEnd(task);
        Mockito.verify(callback, Mockito.never()).connected(eq(task), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
        // callback
        final Listener1Assist.Listener1Model model = new Listener1Assist.Listener1Model(1);
        model.onInfoValid(info);
        model.blockCount = 1;
        model.currentOffset.set(2);
        model.totalLength = 3;
        Mockito.when(handler.getOrRecoverModel(task, info)).thenReturn(model);
        assist.connectEnd(task);
        Mockito.verify(callback).connected(eq(task), ArgumentMatchers.eq(1), ArgumentMatchers.eq(2L), ArgumentMatchers.eq(3L));
        // assign
        model.isFromResumed = true;
        model.isFirstConnect = true;
        assist.connectEnd(task);
        assertThat(model.isFirstConnect).isFalse();
    }

    @Test
    public void fetchProgress() {
        // no model
        Mockito.when(handler.getOrRecoverModel(task, info)).thenReturn(null);
        assist.fetchProgress(task, 1);
        Mockito.verify(callback, Mockito.never()).progress(eq(task), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
        // callback
        final Listener1Assist.Listener1Model model = new Listener1Assist.Listener1Model(1);
        model.currentOffset.set(2);
        model.totalLength = 3;
        Mockito.when(handler.getOrRecoverModel(task, info)).thenReturn(model);
        assist.fetchProgress(task, 1);
        Mockito.verify(callback).progress(eq(task), ArgumentMatchers.eq((2L + 1L)), ArgumentMatchers.eq(3L));
    }

    @Test
    public void isAlwaysRecoverAssistModel() {
        Mockito.when(handler.isAlwaysRecoverAssistModel()).thenReturn(true);
        assertThat(assist.isAlwaysRecoverAssistModel()).isTrue();
        Mockito.when(handler.isAlwaysRecoverAssistModel()).thenReturn(false);
        assertThat(assist.isAlwaysRecoverAssistModel()).isFalse();
    }

    @Test
    public void setAlwaysRecoverAssistModel() {
        assist.setAlwaysRecoverAssistModel(true);
        Mockito.verify(handler).setAlwaysRecoverAssistModel(eq(true));
        assist.setAlwaysRecoverAssistModel(false);
        Mockito.verify(handler).setAlwaysRecoverAssistModel(eq(false));
    }

    @Test
    public void setAlwaysRecoverAssistModelIfNotSet() {
        assist.setAlwaysRecoverAssistModelIfNotSet(true);
        Mockito.verify(handler).setAlwaysRecoverAssistModelIfNotSet(eq(true));
        assist.setAlwaysRecoverAssistModelIfNotSet(false);
        Mockito.verify(handler).setAlwaysRecoverAssistModelIfNotSet(eq(false));
    }
}

