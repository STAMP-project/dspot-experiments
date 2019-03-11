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


import com.liulishuo.okdownload.DownloadListener;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;


public class DownloadListenerBunchTest {
    private DownloadListenerBunch listenerBunch;

    @Mock
    private DownloadListener listener1;

    @Mock
    private DownloadListener listener2;

    @Mock
    private DownloadTask task;

    @Mock
    private BreakpointInfo info;

    @Mock
    private ResumeFailedCause resumeFailedCause;

    @Mock
    private Map<String, List<String>> headerFields;

    @Mock
    private EndCause endCause;

    @Mock
    private Exception realCause;

    @Test
    public void build() {
        DownloadListenerBunch.Builder builder = new DownloadListenerBunch.Builder();
        DownloadListenerBunch listenerBunch = builder.append(listener1).append(listener2).build();
        assertThat(listenerBunch.listenerList).containsExactly(listener1, listener2);
    }

    @Test
    public void taskStart() throws Exception {
        listenerBunch.taskStart(task);
        verify(listener1).taskStart(org.mockito.ArgumentMatchers.eq(task));
        verify(listener2).taskStart(org.mockito.ArgumentMatchers.eq(task));
    }

    @Test
    public void connectTrialStart() throws Exception {
        listenerBunch.connectTrialStart(task, headerFields);
        verify(listener1).connectTrialStart(org.mockito.ArgumentMatchers.eq(task), org.mockito.ArgumentMatchers.eq(headerFields));
        verify(listener2).connectTrialStart(org.mockito.ArgumentMatchers.eq(task), org.mockito.ArgumentMatchers.eq(headerFields));
    }

    @Test
    public void connectTrialEnd() throws Exception {
        listenerBunch.connectTrialEnd(task, 200, headerFields);
        verify(listener1).connectTrialEnd(org.mockito.ArgumentMatchers.eq(task), org.mockito.ArgumentMatchers.eq(200), org.mockito.ArgumentMatchers.eq(headerFields));
        verify(listener2).connectTrialEnd(org.mockito.ArgumentMatchers.eq(task), org.mockito.ArgumentMatchers.eq(200), org.mockito.ArgumentMatchers.eq(headerFields));
    }

    @Test
    public void downloadFromBeginning() throws Exception {
        listenerBunch.downloadFromBeginning(task, info, resumeFailedCause);
        verify(listener1).downloadFromBeginning(org.mockito.ArgumentMatchers.eq(task), org.mockito.ArgumentMatchers.eq(info), org.mockito.ArgumentMatchers.eq(resumeFailedCause));
        verify(listener2).downloadFromBeginning(org.mockito.ArgumentMatchers.eq(task), org.mockito.ArgumentMatchers.eq(info), org.mockito.ArgumentMatchers.eq(resumeFailedCause));
    }

    @Test
    public void downloadFromBreakpoint() throws Exception {
        listenerBunch.downloadFromBreakpoint(task, info);
        verify(listener1).downloadFromBreakpoint(org.mockito.ArgumentMatchers.eq(task), org.mockito.ArgumentMatchers.eq(info));
        verify(listener2).downloadFromBreakpoint(org.mockito.ArgumentMatchers.eq(task), org.mockito.ArgumentMatchers.eq(info));
    }

    @Test
    public void connectStart() throws Exception {
        listenerBunch.connectStart(task, 1, headerFields);
        verify(listener1).connectStart(org.mockito.ArgumentMatchers.eq(task), org.mockito.ArgumentMatchers.eq(1), org.mockito.ArgumentMatchers.eq(headerFields));
        verify(listener2).connectStart(org.mockito.ArgumentMatchers.eq(task), org.mockito.ArgumentMatchers.eq(1), org.mockito.ArgumentMatchers.eq(headerFields));
    }

    @Test
    public void connectEnd() throws Exception {
        listenerBunch.connectEnd(task, 1, 1, headerFields);
        verify(listener1).connectEnd(org.mockito.ArgumentMatchers.eq(task), org.mockito.ArgumentMatchers.eq(1), org.mockito.ArgumentMatchers.eq(1), org.mockito.ArgumentMatchers.eq(headerFields));
        verify(listener2).connectEnd(org.mockito.ArgumentMatchers.eq(task), org.mockito.ArgumentMatchers.eq(1), org.mockito.ArgumentMatchers.eq(1), org.mockito.ArgumentMatchers.eq(headerFields));
    }

    @Test
    public void fetchStart() throws Exception {
        listenerBunch.fetchStart(task, 1, 1L);
        verify(listener1).fetchStart(org.mockito.ArgumentMatchers.eq(task), org.mockito.ArgumentMatchers.eq(1), org.mockito.ArgumentMatchers.eq(1L));
        verify(listener2).fetchStart(org.mockito.ArgumentMatchers.eq(task), org.mockito.ArgumentMatchers.eq(1), org.mockito.ArgumentMatchers.eq(1L));
    }

    @Test
    public void fetchProgress() throws Exception {
        listenerBunch.fetchProgress(task, 1, 1L);
        verify(listener1).fetchProgress(org.mockito.ArgumentMatchers.eq(task), org.mockito.ArgumentMatchers.eq(1), org.mockito.ArgumentMatchers.eq(1L));
        verify(listener2).fetchProgress(org.mockito.ArgumentMatchers.eq(task), org.mockito.ArgumentMatchers.eq(1), org.mockito.ArgumentMatchers.eq(1L));
    }

    @Test
    public void fetchEnd() throws Exception {
        listenerBunch.fetchEnd(task, 1, 1L);
        verify(listener1).fetchEnd(org.mockito.ArgumentMatchers.eq(task), org.mockito.ArgumentMatchers.eq(1), org.mockito.ArgumentMatchers.eq(1L));
        verify(listener2).fetchEnd(org.mockito.ArgumentMatchers.eq(task), org.mockito.ArgumentMatchers.eq(1), org.mockito.ArgumentMatchers.eq(1L));
    }

    @Test
    public void taskEnd() {
        listenerBunch.taskEnd(task, endCause, realCause);
        verify(listener1).taskEnd(org.mockito.ArgumentMatchers.eq(task), org.mockito.ArgumentMatchers.eq(endCause), eq(realCause));
        verify(listener2).taskEnd(org.mockito.ArgumentMatchers.eq(task), org.mockito.ArgumentMatchers.eq(endCause), eq(realCause));
    }

    @Test
    public void contain() throws Exception {
        assertThat(listenerBunch.contain(listener1)).isTrue();
        assertThat(listenerBunch.contain(listener2)).isTrue();
        assertThat(listenerBunch.contain(Mockito.mock(DownloadListener.class))).isFalse();
    }

    @Test
    public void indexOf() {
        assertThat(listenerBunch.indexOf(listener1)).isZero();
        assertThat(listenerBunch.indexOf(listener2)).isOne();
        assertThat(listenerBunch.indexOf(Mockito.mock(DownloadListener.class))).isEqualTo((-1));
    }
}

