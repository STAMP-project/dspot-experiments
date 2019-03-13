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


import ContentResolver.SCHEME_CONTENT;
import ContentResolver.SCHEME_FILE;
import DownloadOutputStream.Factory;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.OkDownload;
import com.liulishuo.okdownload.TestUtils;
import com.liulishuo.okdownload.core.breakpoint.BlockInfo;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.file.DownloadOutputStream;
import com.liulishuo.okdownload.core.file.ProcessFileStrategy;
import java.io.File;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class BreakpointLocalCheckTest {
    private BreakpointLocalCheck check;

    @Mock
    private DownloadTask task;

    @Mock
    private BreakpointInfo info;

    @Mock
    private File fileOnInfo;

    @Mock
    private File fileOnTask;

    @Mock
    private BlockInfo blockInfo;

    @Mock
    private Uri contentUri;

    @Mock
    private Uri fileUri;

    @Test
    public void getCauseOrThrow_infoDirty() {
        check.infoRight = false;
        check.fileExist = true;
        check.outputStreamSupport = true;
        assertThat(check.getCauseOrThrow()).isEqualTo(INFO_DIRTY);
    }

    @Test
    public void getCauseOrThrow_fileNotExist() {
        check.infoRight = true;
        check.fileExist = false;
        check.outputStreamSupport = true;
        assertThat(check.getCauseOrThrow()).isEqualTo(FILE_NOT_EXIST);
    }

    @Test
    public void getCauseOrThrow_outputStreamNotSupport() {
        check.infoRight = true;
        check.fileExist = true;
        check.outputStreamSupport = false;
        assertThat(check.getCauseOrThrow()).isEqualTo(OUTPUT_STREAM_NOT_SUPPORT);
    }

    @Test(expected = IllegalStateException.class)
    public void getCauseOrThrow_notDirty() {
        check.infoRight = true;
        check.fileExist = true;
        check.outputStreamSupport = true;
        check.getCauseOrThrow();
    }

    @Test
    public void isInfoRightToResume_noBlock() {
        Mockito.when(info.getBlockCount()).thenReturn(0);
        assertThat(check.isInfoRightToResume()).isFalse();
    }

    @Test
    public void isInfoRightToResume_chunked() {
        Mockito.when(info.isChunked()).thenReturn(true);
        assertThat(check.isInfoRightToResume()).isFalse();
    }

    @Test
    public void isInfoRightToResume_noFile() {
        Mockito.when(info.getFile()).thenReturn(null);
        assertThat(check.isInfoRightToResume()).isFalse();
    }

    @Test
    public void isInfoRightToResume_fileNotEqual() {
        Mockito.when(task.getFile()).thenReturn(fileOnTask);
        assertThat(check.isInfoRightToResume()).isFalse();
    }

    @Test
    public void isInfoRightToResume_fileLengthLargerThanTotalLength() {
        Mockito.when(fileOnInfo.length()).thenReturn(2L);
        Mockito.when(info.getTotalLength()).thenReturn(1L);
        assertThat(check.isInfoRightToResume()).isFalse();
    }

    @Test
    public void isInfoRightToResume_instanceLengthEqual() {
        check = Mockito.spy(new BreakpointLocalCheck(task, info, 2));
        Mockito.when(info.getBlockCount()).thenReturn(1);
        Mockito.when(info.isChunked()).thenReturn(false);
        Mockito.when(info.getFile()).thenReturn(fileOnInfo);
        Mockito.when(task.getFile()).thenReturn(fileOnInfo);
        Mockito.when(info.getBlock(0)).thenReturn(blockInfo);
        Mockito.when(blockInfo.getContentLength()).thenReturn(1L);
        Mockito.when(contentUri.getScheme()).thenReturn(SCHEME_CONTENT);
        Mockito.when(fileUri.getScheme()).thenReturn(SCHEME_FILE);
        Mockito.when(info.getTotalLength()).thenReturn(1L);
        assertThat(check.isInfoRightToResume()).isFalse();
    }

    @Test
    public void isInfoRightToResume_blockRight() {
        Mockito.when(blockInfo.getContentLength()).thenReturn(0L);
        assertThat(check.isInfoRightToResume()).isFalse();
    }

    @Test
    public void isInfoRightToResume() {
        Mockito.when(info.getBlockCount()).thenReturn(1);
        Mockito.when(info.isChunked()).thenReturn(false);
        Mockito.when(info.getFile()).thenReturn(fileOnInfo);
        Mockito.when(task.getFile()).thenReturn(fileOnInfo);
        Mockito.when(info.getBlock(0)).thenReturn(blockInfo);
        Mockito.when(blockInfo.getContentLength()).thenReturn(1L);
        assertThat(check.isInfoRightToResume()).isTrue();
    }

    @Test
    public void isOutputStreamSupportResume_support() throws IOException {
        TestUtils.mockOkDownload();
        // support seek
        final DownloadOutputStream.Factory factory = OkDownload.with().outputStreamFactory();
        Mockito.when(factory.supportSeek()).thenReturn(true);
        assertThat(check.isOutputStreamSupportResume()).isTrue();
        // not support seek
        Mockito.when(factory.supportSeek()).thenReturn(false);
        // just one block
        Mockito.when(info.getBlockCount()).thenReturn(1);
        // not pre allocate length
        final ProcessFileStrategy strategy = OkDownload.with().processFileStrategy();
        Mockito.when(strategy.isPreAllocateLength(task)).thenReturn(false);
        assertThat(check.isOutputStreamSupportResume()).isTrue();
    }

    @Test
    public void isOutputStreamSupportResume_notSupport() throws IOException {
        TestUtils.mockOkDownload();
        final DownloadOutputStream.Factory factory = OkDownload.with().outputStreamFactory();
        Mockito.when(factory.supportSeek()).thenReturn(false);
        Mockito.when(info.getBlockCount()).thenReturn(2);
        assertThat(check.isOutputStreamSupportResume()).isFalse();
        Mockito.when(info.getBlockCount()).thenReturn(1);
        // pre allocate length but not support seek
        final ProcessFileStrategy strategy = OkDownload.with().processFileStrategy();
        Mockito.doReturn(true).when(strategy).isPreAllocateLength(task);
        assertThat(check.isOutputStreamSupportResume()).isFalse();
    }

    @Test
    public void isFileExistToResume_contentUri() throws IOException {
        TestUtils.mockOkDownload();
        final OkDownload okDownload = OkDownload.with();
        final Context context = Mockito.mock(Context.class);
        Mockito.when(okDownload.context()).thenReturn(context);
        final ContentResolver resolver = Mockito.mock(ContentResolver.class);
        Mockito.when(context.getContentResolver()).thenReturn(resolver);
        Mockito.when(task.getUri()).thenReturn(contentUri);
        assertThat(check.isFileExistToResume()).isFalse();
        // size > 0
        final Cursor cursor = Mockito.mock(Cursor.class);
        Mockito.when(resolver.query(contentUri, null, null, null, null)).thenReturn(cursor);
        Mockito.doReturn(1L).when(cursor).getLong(ArgumentMatchers.anyInt());
        assertThat(check.isFileExistToResume()).isTrue();
    }

    @Test
    public void isFileExistToResume_fileUri() {
        Mockito.when(task.getUri()).thenReturn(fileUri);
        Mockito.when(task.getFile()).thenReturn(null);
        assertThat(check.isFileExistToResume()).isFalse();
        final File file = Mockito.mock(File.class);
        Mockito.when(task.getFile()).thenReturn(file);
        Mockito.when(file.exists()).thenReturn(false);
        assertThat(check.isFileExistToResume()).isFalse();
        Mockito.when(file.exists()).thenReturn(true);
        assertThat(check.isFileExistToResume()).isTrue();
    }

    @Test
    public void check_notDirty() {
        Mockito.doReturn(true).when(check).isFileExistToResume();
        Mockito.doReturn(true).when(check).isInfoRightToResume();
        Mockito.doReturn(true).when(check).isOutputStreamSupportResume();
        check.check();
        assertThat(check.isDirty()).isFalse();
    }

    @Test
    public void check_fileNotExist() {
        Mockito.doReturn(false).when(check).isFileExistToResume();
        Mockito.doReturn(true).when(check).isInfoRightToResume();
        Mockito.doReturn(true).when(check).isOutputStreamSupportResume();
        check.check();
        assertThat(check.isDirty()).isTrue();
    }

    @Test
    public void check_infoNotRight() {
        Mockito.doReturn(true).when(check).isFileExistToResume();
        Mockito.doReturn(false).when(check).isInfoRightToResume();
        Mockito.doReturn(true).when(check).isOutputStreamSupportResume();
        check.check();
        assertThat(check.isDirty()).isTrue();
    }

    @Test
    public void check_outputStreamNotSupport() {
        Mockito.doReturn(true).when(check).isFileExistToResume();
        Mockito.doReturn(true).when(check).isInfoRightToResume();
        Mockito.doReturn(false).when(check).isOutputStreamSupportResume();
        check.check();
        assertThat(check.isDirty()).isTrue();
    }
}

