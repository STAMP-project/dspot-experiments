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
package com.liulishuo.okdownload.core.listener;


import Listener4Assist.AssistExtend;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.liulishuo.okdownload.core.listener.assist.Listener4Assist;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.verify;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class DownloadListener4Test {
    private DownloadListener4 listener4;

    @Mock
    private BreakpointInfo info;

    @Mock
    private DownloadTask task;

    @Mock
    private ResumeFailedCause resumeFailedCause;

    @Mock
    private Listener4Assist assist;

    @Mock
    private AssistExtend assistExtend;

    @Mock
    private EndCause endCause;

    @Mock
    private Map<String, List<String>> tmpFields;

    @Mock
    private Exception exception;

    @Test
    public void setAssistExtend() {
        listener4.setAssistExtend(assistExtend);
        verify(assist).setAssistExtend(assistExtend);
    }

    @Test
    public void empty() {
        listener4.connectTrialStart(task, tmpFields);
        listener4.connectTrialEnd(task, 0, tmpFields);
        listener4.fetchStart(task, 0, 0);
    }

    @Test
    public void downloadFromBeginning() {
        listener4.downloadFromBeginning(task, info, resumeFailedCause);
        verify(assist).infoReady(ArgumentMatchers.eq(task), ArgumentMatchers.eq(info), ArgumentMatchers.eq(false));
    }

    @Test
    public void downloadFromBreakpoint() {
        listener4.downloadFromBreakpoint(task, info);
        verify(assist).infoReady(ArgumentMatchers.eq(task), ArgumentMatchers.eq(info), ArgumentMatchers.eq(true));
    }

    @Test
    public void fetchProgress() {
        listener4.fetchProgress(task, 1, 2);
        verify(assist).fetchProgress(ArgumentMatchers.eq(task), ArgumentMatchers.eq(1), ArgumentMatchers.eq(2L));
    }

    @Test
    public void fetchEnd() {
        listener4.fetchEnd(task, 1, 2);
        verify(assist).fetchEnd(ArgumentMatchers.eq(task), ArgumentMatchers.eq(1));
    }

    @Test
    public void taskEnd() {
        listener4.taskEnd(task, endCause, exception);
        verify(assist).taskEnd(ArgumentMatchers.eq(task), ArgumentMatchers.eq(endCause), ArgumentMatchers.eq(exception));
    }

    @Test
    public void isAlwaysRecoverAssistModel() {
        Mockito.when(assist.isAlwaysRecoverAssistModel()).thenReturn(true);
        assertThat(listener4.isAlwaysRecoverAssistModel()).isTrue();
        Mockito.when(assist.isAlwaysRecoverAssistModel()).thenReturn(false);
        assertThat(listener4.isAlwaysRecoverAssistModel()).isFalse();
    }

    @Test
    public void setAlwaysRecoverAssistModel() {
        listener4.setAlwaysRecoverAssistModel(true);
        verify(assist).setAlwaysRecoverAssistModel(ArgumentMatchers.eq(true));
        listener4.setAlwaysRecoverAssistModel(false);
        verify(assist).setAlwaysRecoverAssistModel(ArgumentMatchers.eq(false));
    }

    @Test
    public void setAlwaysRecoverAssistModelIfNotSet() {
        listener4.setAlwaysRecoverAssistModelIfNotSet(true);
        verify(assist).setAlwaysRecoverAssistModelIfNotSet(ArgumentMatchers.eq(true));
        listener4.setAlwaysRecoverAssistModelIfNotSet(false);
        verify(assist).setAlwaysRecoverAssistModelIfNotSet(ArgumentMatchers.eq(false));
    }
}

