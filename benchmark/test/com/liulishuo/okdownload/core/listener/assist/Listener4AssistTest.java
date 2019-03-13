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
import Listener4Assist.AssistExtend;
import Listener4Assist.Listener4Callback;
import Listener4Assist.Listener4Model;
import android.util.SparseArray;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.breakpoint.BlockInfo;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class Listener4AssistTest {
    @Mock
    private BreakpointInfo info;

    @Mock
    private DownloadTask task;

    @Mock
    private Listener4Callback callback;

    @Mock
    private AssistExtend assistExtend;

    @Mock
    private ListenerModelHandler<Listener4Assist.Listener4Model> handler;

    private Listener4Model model;

    private Listener4Assist assist;

    @Test
    public void setCallback() {
        assist.setCallback(callback);
        assertThat(assist.callback).isEqualTo(callback);
    }

    @Test
    public void setAssistExtend() {
        assist.setAssistExtend(assistExtend);
        assertThat(assist.getAssistExtend()).isEqualTo(assistExtend);
    }

    @Test
    public void infoReady_dispatch() {
        // dispatch
        Mockito.when(assistExtend.dispatchInfoReady(ArgumentMatchers.eq(task), ArgumentMatchers.eq(info), ArgumentMatchers.eq(true), ArgumentMatchers.any(Listener4Model.class))).thenReturn(true);
        assist.infoReady(task, info, true);
        Mockito.verify(assist.callback, Mockito.never()).infoReady(ArgumentMatchers.eq(task), ArgumentMatchers.eq(info), ArgumentMatchers.eq(true), ArgumentMatchers.any(Listener4Model.class));
        Mockito.verify(handler).addAndGetModel(ArgumentMatchers.eq(task), ArgumentMatchers.eq(info));
    }

    @Test
    public void infoReady_noDispatch() {
        Mockito.when(handler.addAndGetModel(task, info)).thenReturn(model);
        Mockito.when(assistExtend.dispatchInfoReady(ArgumentMatchers.eq(task), ArgumentMatchers.eq(info), ArgumentMatchers.eq(true), ArgumentMatchers.any(Listener4Model.class))).thenReturn(false);
        assist.infoReady(task, info, true);
        Mockito.verify(assist.callback).infoReady(ArgumentMatchers.eq(task), ArgumentMatchers.eq(info), ArgumentMatchers.eq(true), ArgumentMatchers.eq(model));
        Mockito.verify(handler).addAndGetModel(ArgumentMatchers.eq(task), ArgumentMatchers.eq(info));
    }

    @Test
    public void fetchProgress_noModel() {
        Mockito.when(handler.getOrRecoverModel(task, info)).thenReturn(null);
        assist.fetchProgress(task, 0, 0);
        Mockito.verify(callback, Mockito.never()).progress(ArgumentMatchers.eq(task), ArgumentMatchers.eq(0L));
        Mockito.verify(assistExtend, Mockito.never()).dispatchFetchProgress(ArgumentMatchers.eq(task), ArgumentMatchers.eq(0), ArgumentMatchers.eq(0L), ArgumentMatchers.any(Listener4Model.class));
        Mockito.verify(handler).getOrRecoverModel(ArgumentMatchers.eq(task), ArgumentMatchers.eq(info));
    }

    @Test
    public void fetchProgress_dispatch() {
        Mockito.when(handler.getOrRecoverModel(task, info)).thenReturn(model);
        final int blockIndex = 0;
        final long increaseBytes = 2L;
        Mockito.when(assistExtend.dispatchFetchProgress(ArgumentMatchers.eq(task), ArgumentMatchers.eq(blockIndex), ArgumentMatchers.eq(increaseBytes), ArgumentMatchers.any(Listener4Model.class))).thenReturn(true);
        assist.fetchProgress(task, blockIndex, increaseBytes);
        Mockito.verify(callback, Mockito.never()).progress(ArgumentMatchers.eq(task), ArgumentMatchers.anyLong());
        Mockito.verify(callback, Mockito.never()).progressBlock(ArgumentMatchers.eq(task), ArgumentMatchers.eq(0), ArgumentMatchers.anyLong());
        Mockito.verify(handler).getOrRecoverModel(ArgumentMatchers.eq(task), ArgumentMatchers.eq(info));
    }

    @Test
    public void fetchProgress() {
        Mockito.when(handler.getOrRecoverModel(task, info)).thenReturn(model);
        assist.fetchProgress(task, 0, 3);
        assertThat(model.getCurrentOffset()).isEqualTo(3);
        assertThat(model.getBlockCurrentOffsetMap().get(0)).isEqualTo(3);
        assertThat(model.getBlockCurrentOffsetMap().get(1)).isEqualTo(0);
        assertThat(model.getBlockCurrentOffsetMap().get(2)).isEqualTo(0);
        Mockito.verify(callback).progressBlock(ArgumentMatchers.eq(task), ArgumentMatchers.eq(0), ArgumentMatchers.eq(3L));
        Mockito.verify(callback).progress(ArgumentMatchers.eq(task), ArgumentMatchers.eq(3L));
        Mockito.verify(handler).getOrRecoverModel(ArgumentMatchers.eq(task), ArgumentMatchers.eq(info));
        assist.fetchProgress(task, 1, 2);
        // not effect to single-task-model
        assertThat(model.getCurrentOffset()).isEqualTo(5);
        assertThat(model.getBlockCurrentOffsetMap().get(1)).isEqualTo(2);
        Mockito.verify(callback).progressBlock(ArgumentMatchers.eq(task), ArgumentMatchers.eq(1), ArgumentMatchers.eq(2L));
        Mockito.verify(callback).progress(ArgumentMatchers.eq(task), ArgumentMatchers.eq(5L));
    }

    @Test
    public void fetchEnd_dispatch() {
        Mockito.when(handler.getOrRecoverModel(task, info)).thenReturn(model);
        final int blockIndex = 0;
        Mockito.when(assistExtend.dispatchBlockEnd(ArgumentMatchers.eq(task), ArgumentMatchers.eq(blockIndex), ArgumentMatchers.any(Listener4Model.class))).thenReturn(true);
        assist.fetchEnd(task, blockIndex);
        Mockito.verify(callback, Mockito.never()).blockEnd(ArgumentMatchers.eq(task), ArgumentMatchers.eq(blockIndex), ArgumentMatchers.any(BlockInfo.class));
        Mockito.verify(handler).getOrRecoverModel(ArgumentMatchers.eq(task), ArgumentMatchers.eq(info));
    }

    @Test
    public void fetchEnd() {
        Mockito.when(handler.getOrRecoverModel(task, info)).thenReturn(model);
        assist.fetchEnd(task, 1);
        final BlockInfo blockInfo1 = info.getBlock(1);
        Mockito.verify(callback).blockEnd(ArgumentMatchers.eq(task), ArgumentMatchers.eq(1), ArgumentMatchers.eq(blockInfo1));
        Mockito.verify(handler).getOrRecoverModel(ArgumentMatchers.eq(task), ArgumentMatchers.eq(info));
    }

    @Test
    public void taskEnd_dispatch() {
        Mockito.when(handler.removeOrCreate(task, info)).thenReturn(model);
        Mockito.when(assistExtend.dispatchTaskEnd(ArgumentMatchers.eq(task), ArgumentMatchers.any(EndCause.class), ArgumentMatchers.nullable(Exception.class), ArgumentMatchers.any(Listener4Model.class))).thenReturn(true);
        assist.taskEnd(task, COMPLETED, null);
        Mockito.verify(callback, Mockito.never()).taskEnd(ArgumentMatchers.eq(task), ArgumentMatchers.eq(COMPLETED), ArgumentMatchers.nullable(Exception.class), ArgumentMatchers.any(Listener4Model.class));
    }

    @Test
    public void taskEnd_noModel() {
        Mockito.when(handler.removeOrCreate(task, info)).thenReturn(model);
        assist.taskEnd(task, COMPLETED, null);
        Mockito.verify(callback).taskEnd(ArgumentMatchers.eq(task), ArgumentMatchers.eq(COMPLETED), ArgumentMatchers.nullable(Exception.class), ArgumentMatchers.any(Listener4Model.class));
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
        Mockito.verify(handler).setAlwaysRecoverAssistModel(ArgumentMatchers.eq(true));
        assist.setAlwaysRecoverAssistModel(false);
        Mockito.verify(handler).setAlwaysRecoverAssistModel(ArgumentMatchers.eq(false));
    }

    @Test
    public void setAlwaysRecoverAssistModelIfNotSet() {
        assist.setAlwaysRecoverAssistModelIfNotSet(true);
        Mockito.verify(handler).setAlwaysRecoverAssistModelIfNotSet(ArgumentMatchers.eq(true));
        assist.setAlwaysRecoverAssistModelIfNotSet(false);
        Mockito.verify(handler).setAlwaysRecoverAssistModelIfNotSet(ArgumentMatchers.eq(false));
    }

    @Test
    public void getBlockCurrentOffsetMap() {
        model.blockCurrentOffsetMap = Mockito.mock(SparseArray.class);
        assertThat(model.getBlockCurrentOffsetMap()).isEqualTo(model.blockCurrentOffsetMap);
    }

    @Test
    public void getBlockCurrentOffset() {
        model.blockCurrentOffsetMap.put(1, 2L);
        assertThat(model.getBlockCurrentOffset(1)).isEqualTo(2L);
    }

    @Test
    public void cloneBlockCurrentOffsetMap() {
        final SparseArray map = Mockito.mock(SparseArray.class);
        final SparseArray cloned = Mockito.mock(SparseArray.class);
        model.blockCurrentOffsetMap = map;
        Mockito.when(map.clone()).thenReturn(cloned);
        assertThat(model.cloneBlockCurrentOffsetMap()).isEqualTo(cloned);
    }
}

