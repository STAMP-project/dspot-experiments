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
package com.liulishuo.okdownload.core.download;


import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.OkDownload;
import com.liulishuo.okdownload.TestUtils;
import com.liulishuo.okdownload.core.Util;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.liulishuo.okdownload.core.exception.FileBusyAfterRunException;
import com.liulishuo.okdownload.core.exception.ServerCanceledException;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class BreakpointRemoteCheckTest {
    private BreakpointRemoteCheck check;

    @Mock
    private DownloadTask task;

    @Mock
    private BreakpointInfo info;

    @Mock
    private ConnectTrial connectTrial;

    private String responseFilename = "response.filename";

    private String responseEtag = "response.etag";

    @Test
    public void check_assembleBasicData() throws IOException {
        TestUtils.mockOkDownload();
        check.check();
        final DownloadStrategy strategy = OkDownload.with().downloadStrategy();
        Mockito.verify(strategy).validFilenameFromResponse(ArgumentMatchers.eq(responseFilename), ArgumentMatchers.eq(task), ArgumentMatchers.eq(info));
        Mockito.verify(info).setChunked(ArgumentMatchers.eq(connectTrial.isChunked()));
        Mockito.verify(info).setEtag(ArgumentMatchers.eq(responseEtag));
    }

    @Test(expected = FileBusyAfterRunException.class)
    public void check_fileConflictAfterRun() throws IOException {
        TestUtils.mockOkDownload();
        Mockito.when(OkDownload.with().downloadDispatcher().isFileConflictAfterRun(ArgumentMatchers.eq(task))).thenReturn(true);
        check.check();
    }

    @Test
    public void check_collectResult() throws IOException {
        TestUtils.mockOkDownload();
        Mockito.when(connectTrial.getInstanceLength()).thenReturn(1L);
        Mockito.when(connectTrial.isAcceptRange()).thenReturn(true);
        check.check();
        assertThat(check.isResumable()).isTrue();
        assertThat(check.getCause()).isNull();
        assertThat(check.getInstanceLength()).isEqualTo(1L);
        assertThat(check.isAcceptRange()).isTrue();
        final ResumeFailedCause cause = Mockito.mock(ResumeFailedCause.class);
        Mockito.when(OkDownload.with().downloadStrategy().getPreconditionFailedCause(ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(info), ArgumentMatchers.eq(responseEtag))).thenReturn(cause);
        check.check();
        assertThat(check.isResumable()).isFalse();
        assertThat(check.getCause()).isEqualTo(cause);
    }

    @Test(expected = ServerCanceledException.class)
    public void check_serverCanceled() throws IOException {
        TestUtils.mockOkDownload();
        Mockito.when(OkDownload.with().downloadStrategy().isServerCanceled(0, false)).thenReturn(true);
        check.check();
    }

    @Test
    public void check_trialSpecialPass() throws IOException {
        TestUtils.mockOkDownload();
        Mockito.when(OkDownload.with().downloadStrategy().isServerCanceled(0, false)).thenReturn(true);
        Mockito.doReturn(true).when(check).isTrialSpecialPass(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean());
        check.check();
        // no exception
    }

    @Test
    public void isTrialSpecialPass() {
        assertThat(check.isTrialSpecialPass(Util.RANGE_NOT_SATISFIABLE, 0, true)).isTrue();
        assertThat(check.isTrialSpecialPass(Util.RANGE_NOT_SATISFIABLE, (-1), true)).isFalse();
        assertThat(check.isTrialSpecialPass(Util.RANGE_NOT_SATISFIABLE, 0, false)).isFalse();
        assertThat(check.isTrialSpecialPass(Util.RANGE_NOT_SATISFIABLE, (-1), false)).isFalse();
        assertThat(check.isTrialSpecialPass(HttpURLConnection.HTTP_PARTIAL, 0, true)).isFalse();
    }

    @Test(expected = IllegalStateException.class)
    public void getCauseOrThrown() throws Exception {
        final ResumeFailedCause failedCause = Mockito.mock(ResumeFailedCause.class);
        check.failedCause = failedCause;
        assertThat(check.getCauseOrThrow()).isEqualTo(failedCause);
        check.failedCause = null;
        check.getCauseOrThrow();
    }
}

