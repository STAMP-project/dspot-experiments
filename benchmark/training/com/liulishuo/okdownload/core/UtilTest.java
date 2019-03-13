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
package com.liulishuo.okdownload.core;


import ConnectivityManager.TYPE_MOBILE;
import ConnectivityManager.TYPE_WIFI;
import Util.Logger;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.StatFs;
import com.liulishuo.okdownload.BuildConfig;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.OkDownload;
import com.liulishuo.okdownload.TestUtils;
import com.liulishuo.okdownload.core.breakpoint.BlockInfo;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.connection.DownloadConnection;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadFactory;
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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class UtilTest {
    @Mock
    private Logger logger;

    @Mock
    private Exception e;

    private String tag = "tag";

    private String msg = "msg";

    @Test
    public void setLogger() {
        Util.setLogger(logger);
        assertThat(Util.getLogger()).isEqualTo(logger);
    }

    @Test
    public void e() {
        Util.setLogger(logger);
        Util.enableConsoleLog();
        Util.e(tag, msg, e);
        Mockito.verify(logger, never()).e(eq(tag), eq(msg), ArgumentMatchers.eq(e));
        Util.setLogger(logger);
        Util.e(tag, msg, e);
        verify(logger).e(eq(tag), eq(msg), ArgumentMatchers.eq(e));
    }

    @Test
    public void w() {
        Util.setLogger(logger);
        Util.enableConsoleLog();
        Util.w(tag, msg);
        Mockito.verify(logger, never()).w(eq(tag), eq(msg));
        Util.setLogger(logger);
        Util.w(tag, msg);
        verify(logger).w(eq(tag), eq(msg));
    }

    @Test
    public void d() {
        Util.setLogger(logger);
        Util.enableConsoleLog();
        Util.d(tag, msg);
        Mockito.verify(logger, never()).d(eq(tag), eq(msg));
        Util.setLogger(logger);
        Util.d(tag, msg);
        verify(logger).d(eq(tag), eq(msg));
    }

    @Test
    public void i() {
        Util.setLogger(logger);
        Util.enableConsoleLog();
        Util.i(tag, msg);
        Mockito.verify(logger, never()).i(eq(tag), eq(msg));
        Util.setLogger(logger);
        Util.i(tag, msg);
        verify(logger).i(eq(tag), eq(msg));
    }

    @Test
    public void isEmpty() {
        assertThat(Util.isEmpty(null)).isTrue();
        assertThat(Util.isEmpty("")).isTrue();
        assertThat(Util.isEmpty("1")).isFalse();
    }

    @Test
    public void threadFactory() {
        final String name = "name";
        final boolean daemon = true;
        final ThreadFactory factory = Util.threadFactory(name, daemon);
        assertThat(factory.newThread(Mockito.mock(Runnable.class)).getName()).isEqualTo(name);
        assertThat(factory.newThread(Mockito.mock(Runnable.class)).isDaemon()).isEqualTo(daemon);
    }

    @Test
    public void md5() {
        assertThat(Util.md5("abc")).isEqualTo("900150983cd24fb0d6963f7d28e17f72");
        assertThat(Util.md5("")).isEqualTo("d41d8cd98f00b204e9800998ecf8427e");
    }

    @Test
    public void isCorrectFull() throws Exception {
        assertThat(Util.isCorrectFull(1, 2)).isFalse();
        assertThat(Util.isCorrectFull(2, 2)).isTrue();
    }

    @Test
    public void resetBlockIfDirty_offsetLessThan0_reset() throws Exception {
        Util.setLogger(logger);
        final BlockInfo info = Mockito.mock(BlockInfo.class);
        Mockito.when(info.getCurrentOffset()).thenReturn((-1L));
        Util.resetBlockIfDirty(info);
        verify(info).resetBlock();
    }

    @Test
    public void resetBlockIfDirty_offsetLargerThanContent_reset() throws Exception {
        Util.setLogger(logger);
        final BlockInfo info = Mockito.spy(new BlockInfo(0, 1));
        info.increaseCurrentOffset(2);
        Util.resetBlockIfDirty(info);
        verify(info).resetBlock();
    }

    @Test
    public void getFreeSpaceBytes() {
        final StatFs statFs = Mockito.mock(StatFs.class);
        Mockito.when(statFs.getAvailableBlocks()).thenReturn(1);
        Mockito.when(statFs.getBlockSize()).thenReturn(2);
        assertThat(Util.getFreeSpaceBytes(statFs)).isEqualTo(2L);
    }

    @Test
    public void humanReadableBytes() throws Exception {
        assertThat(Util.humanReadableBytes(1054, true)).isEqualTo("1.1 kB");
        assertThat(Util.humanReadableBytes(1054, false)).isEqualTo("1.0 KiB");
    }

    @Test
    public void createDefaultDatabase() throws Exception {
        Util.createDefaultDatabase(Mockito.mock(Context.class));
    }

    @Test
    public void createDefaultConnectionFactory() throws Exception {
        Util.createDefaultConnectionFactory();
    }

    @Test
    public void assembleBlock_oneBlock() throws Exception {
        TestUtils.mockOkDownload();
        final DownloadTask task = Mockito.mock(DownloadTask.class);
        final BreakpointInfo info = Mockito.mock(BreakpointInfo.class);
        final ArgumentCaptor<BlockInfo> capture = ArgumentCaptor.forClass(BlockInfo.class);
        Mockito.when(OkDownload.with().downloadStrategy().isUseMultiBlock(false)).thenReturn(false);
        Util.assembleBlock(task, info, 10, false);
        verify(info).addBlock(capture.capture());
        List<BlockInfo> infoList = capture.getAllValues();
        assertThat(infoList.size()).isOne();
        BlockInfo blockInfo = infoList.get(0);
        assertThat(blockInfo.getStartOffset()).isZero();
        assertThat(blockInfo.getCurrentOffset()).isZero();
        assertThat(blockInfo.getContentLength()).isEqualTo(10L);
    }

    @Test
    public void assembleBlock_multiBlock() throws Exception {
        TestUtils.mockOkDownload();
        final DownloadTask task = Mockito.mock(DownloadTask.class);
        final BreakpointInfo info = Mockito.mock(BreakpointInfo.class);
        final ArgumentCaptor<BlockInfo> capture = ArgumentCaptor.forClass(BlockInfo.class);
        Mockito.when(OkDownload.with().downloadStrategy().isUseMultiBlock(false)).thenReturn(true);
        Mockito.when(OkDownload.with().downloadStrategy().determineBlockCount(task, 10)).thenReturn(3);
        Util.assembleBlock(task, info, 10, false);
        Mockito.verify(info, Mockito.times(3)).addBlock(capture.capture());
        List<BlockInfo> infoList = capture.getAllValues();
        assertThat(infoList.size()).isEqualTo(3);
        BlockInfo blockInfo1 = infoList.get(0);
        assertThat(blockInfo1.getStartOffset()).isZero();
        assertThat(blockInfo1.getCurrentOffset()).isZero();
        assertThat(blockInfo1.getContentLength()).isEqualTo(4L);
        BlockInfo blockInfo2 = infoList.get(1);
        assertThat(blockInfo2.getStartOffset()).isEqualTo(4L);
        assertThat(blockInfo2.getCurrentOffset()).isZero();
        assertThat(blockInfo2.getContentLength()).isEqualTo(3L);
        BlockInfo blockInfo3 = infoList.get(2);
        assertThat(blockInfo3.getStartOffset()).isEqualTo(7L);
        assertThat(blockInfo3.getCurrentOffset()).isZero();
        assertThat(blockInfo3.getContentLength()).isEqualTo(3L);
    }

    @Test
    public void parseContentLength() {
        assertThat(Util.parseContentLength(null)).isEqualTo(Util.CHUNKED_CONTENT_LENGTH);
        assertThat(Util.parseContentLength("123")).isEqualTo(123L);
        assertThat(Util.parseContentLength("-")).isEqualTo(Util.CHUNKED_CONTENT_LENGTH);
    }

    @Test
    public void parseContentLengthFromContentRange() {
        String length801ContentRange = "bytes 200-1000/67589";
        assertThat(Util.parseContentLengthFromContentRange(length801ContentRange)).isEqualTo(801);
        assertThat(Util.parseContentLengthFromContentRange(null)).isEqualTo(Util.CHUNKED_CONTENT_LENGTH);
        assertThat(Util.parseContentLengthFromContentRange("")).isEqualTo(Util.CHUNKED_CONTENT_LENGTH);
        assertThat(Util.parseContentLengthFromContentRange("invalid")).isEqualTo(Util.CHUNKED_CONTENT_LENGTH);
    }

    @Test
    public void isNetworkNotOnWifiType() {
        assertThat(Util.isNetworkNotOnWifiType(null)).isTrue();
        final ConnectivityManager manager = Mockito.mock(ConnectivityManager.class);
        Mockito.when(manager.getActiveNetworkInfo()).thenReturn(null);
        assertThat(Util.isNetworkNotOnWifiType(manager)).isTrue();
        final NetworkInfo info = Mockito.mock(NetworkInfo.class);
        Mockito.when(manager.getActiveNetworkInfo()).thenReturn(info);
        Mockito.when(info.getType()).thenReturn(TYPE_MOBILE);
        assertThat(Util.isNetworkNotOnWifiType(manager)).isTrue();
        Mockito.when(info.getType()).thenReturn(TYPE_WIFI);
        assertThat(Util.isNetworkNotOnWifiType(manager)).isFalse();
    }

    @Test
    public void isNetworkAvailable() {
        assertThat(Util.isNetworkAvailable(null)).isTrue();
        final ConnectivityManager manager = Mockito.mock(ConnectivityManager.class);
        Mockito.when(manager.getActiveNetworkInfo()).thenReturn(null);
        assertThat(Util.isNetworkAvailable(manager)).isFalse();
        final NetworkInfo info = Mockito.mock(NetworkInfo.class);
        Mockito.when(manager.getActiveNetworkInfo()).thenReturn(info);
        Mockito.when(info.isConnected()).thenReturn(false);
        assertThat(Util.isNetworkAvailable(manager)).isFalse();
        Mockito.when(info.isConnected()).thenReturn(true);
        assertThat(Util.isNetworkAvailable(manager)).isTrue();
    }

    @Test
    public void getFilenameFromContentUri() throws IOException {
        TestUtils.mockOkDownload();
        final Uri contentUri = Mockito.mock(Uri.class);
        final ContentResolver resolver = Mockito.mock(ContentResolver.class);
        final OkDownload okDownload = OkDownload.with();
        final Context context = Mockito.mock(Context.class);
        Mockito.when(okDownload.context()).thenReturn(context);
        Mockito.when(context.getContentResolver()).thenReturn(resolver);
        // null cursor
        Mockito.when(resolver.query(contentUri, null, null, null, null)).thenReturn(null);
        assertThat(Util.getFilenameFromContentUri(contentUri)).isNull();
        // valid cursor
        final Cursor cursor = Mockito.mock(Cursor.class);
        Mockito.when(resolver.query(contentUri, null, null, null, null)).thenReturn(cursor);
        Mockito.when(cursor.getString(ArgumentMatchers.anyInt())).thenReturn("filename");
        assertThat(Util.getFilenameFromContentUri(contentUri)).isEqualTo("filename");
    }

    @Test
    public void getSizeFromContentUri() throws IOException {
        TestUtils.mockOkDownload();
        final Uri contentUri = Mockito.mock(Uri.class);
        final ContentResolver resolver = Mockito.mock(ContentResolver.class);
        final OkDownload okDownload = OkDownload.with();
        final Context context = Mockito.mock(Context.class);
        Mockito.when(okDownload.context()).thenReturn(context);
        Mockito.when(context.getContentResolver()).thenReturn(resolver);
        // null cursor
        Mockito.when(resolver.query(contentUri, null, null, null, null)).thenReturn(null);
        assertThat(Util.getSizeFromContentUri(contentUri)).isZero();
        // valid cursor
        final Cursor cursor = Mockito.mock(Cursor.class);
        Mockito.when(resolver.query(contentUri, null, null, null, null)).thenReturn(cursor);
        Mockito.when(cursor.getLong(ArgumentMatchers.anyInt())).thenReturn(1L);
        assertThat(Util.getSizeFromContentUri(contentUri)).isOne();
        verify(cursor).close();
    }

    @Test
    public void addUserRequestHeaderField() throws IOException {
        Map<String, List<String>> userHeaderMap = new HashMap<>();
        List<String> values = new ArrayList<>();
        values.add("header1-value1");
        values.add("header1-value2");
        userHeaderMap.put("header1", values);
        values = new ArrayList<>();
        values.add("header2-value");
        userHeaderMap.put("header2", values);
        final DownloadConnection connection = Mockito.mock(DownloadConnection.class);
        Util.addUserRequestHeaderField(userHeaderMap, connection);
        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(connection, Mockito.times(3)).addHeader(nameCaptor.capture(), valueCaptor.capture());
        assertThat(nameCaptor.getAllValues()).containsExactlyInAnyOrder("header1", "header1", "header2");
        assertThat(valueCaptor.getAllValues()).containsExactlyInAnyOrder("header1-value1", "header1-value2", "header2-value");
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void inspectUserHeader() throws IOException {
        Map<String, List<String>> userHeaderMap = new HashMap<>();
        userHeaderMap.put("header1", new ArrayList<String>());
        userHeaderMap.put("header2", new ArrayList<String>());
        Util.inspectUserHeader(userHeaderMap);
        userHeaderMap.put(Util.RANGE, new ArrayList<String>());
        thrown.expect(IOException.class);
        thrown.expectMessage(((((Util.IF_MATCH) + " and ") + (Util.RANGE)) + " only can be handle by internal!"));
        Util.inspectUserHeader(userHeaderMap);
        userHeaderMap.remove(Util.RANGE);
        userHeaderMap.put(Util.IF_MATCH, new ArrayList<String>());
        thrown.expect(IOException.class);
        thrown.expectMessage(((((Util.IF_MATCH) + " and ") + (Util.RANGE)) + " only can be handle by internal!"));
        Util.inspectUserHeader(userHeaderMap);
    }

    @Test
    public void addDefaultUserAgent() {
        final DownloadConnection connection = Mockito.mock(DownloadConnection.class);
        Util.addDefaultUserAgent(connection);
        final ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).addHeader(keyCaptor.capture(), valueCaptor.capture());
        assertThat(keyCaptor.getValue()).isEqualTo(Util.USER_AGENT);
        assertThat(valueCaptor.getValue()).isEqualTo(("OkDownload/" + (BuildConfig.VERSION_NAME)));
    }
}

