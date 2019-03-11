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
package com.liulishuo.okdownload.core.interceptor.connect;


import DownloadConnection.Connected;
import com.liulishuo.okdownload.BuildConfig;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.OkDownload;
import com.liulishuo.okdownload.TestUtils;
import com.liulishuo.okdownload.core.Util;
import com.liulishuo.okdownload.core.breakpoint.BlockInfo;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.connection.DownloadConnection;
import com.liulishuo.okdownload.core.download.DownloadChain;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class HeaderInterceptorTest {
    private HeaderInterceptor interceptor;

    @Test
    public void interceptConnect_range() throws IOException {
        final DownloadChain chain = TestUtils.mockDownloadChain();
        DownloadConnection connection = chain.getConnectionOrCreate();
        final BreakpointInfo info = chain.getInfo();
        Mockito.when(info.getBlockCount()).thenReturn(3);
        Mockito.when(info.getBlock(0)).thenReturn(new BlockInfo(0, 10));
        interceptor.interceptConnect(chain);
        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(connection, Mockito.times(2)).addHeader(nameCaptor.capture(), valueCaptor.capture());
        assertThat(nameCaptor.getAllValues()).containsExactlyInAnyOrder(Util.RANGE, Util.USER_AGENT);
        assertThat(valueCaptor.getAllValues()).containsExactlyInAnyOrder("bytes=0-9", ("OkDownload/" + (BuildConfig.VERSION_NAME)));
        Mockito.when(chain.getBlockIndex()).thenReturn(1);
        Mockito.when(info.getBlock(1)).thenReturn(new BlockInfo(10, 10));
        // new one.
        connection = Mockito.mock(DownloadConnection.class);
        Mockito.when(chain.getConnectionOrCreate()).thenReturn(connection);
        interceptor.interceptConnect(chain);
        nameCaptor = ArgumentCaptor.forClass(String.class);
        valueCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(connection, Mockito.times(2)).addHeader(nameCaptor.capture(), valueCaptor.capture());
        assertThat(nameCaptor.getAllValues()).containsExactlyInAnyOrder(Util.RANGE, Util.USER_AGENT);
        assertThat(valueCaptor.getAllValues()).containsExactlyInAnyOrder("bytes=10-19", ("OkDownload/" + (BuildConfig.VERSION_NAME)));
        Mockito.when(chain.getBlockIndex()).thenReturn(2);
        Mockito.when(info.getBlock(2)).thenReturn(new BlockInfo(20, 10));
        // new one.
        connection = Mockito.mock(DownloadConnection.class);
        Mockito.when(chain.getConnectionOrCreate()).thenReturn(connection);
        interceptor.interceptConnect(chain);
        nameCaptor = ArgumentCaptor.forClass(String.class);
        valueCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(connection, Mockito.times(2)).addHeader(nameCaptor.capture(), valueCaptor.capture());
        assertThat(nameCaptor.getAllValues()).containsExactlyInAnyOrder(Util.RANGE, Util.USER_AGENT);
        assertThat(valueCaptor.getAllValues()).containsExactlyInAnyOrder("bytes=20-29", ("OkDownload/" + (BuildConfig.VERSION_NAME)));
    }

    @Test
    public void interceptConnect() throws IOException {
        Map<String, List<String>> customHeader = new HashMap<>();
        List<String> values = new ArrayList<>();
        values.add("header1-value1");
        values.add("header1-value2");
        customHeader.put("header1", values);
        values = new ArrayList<>();
        values.add("header2-value");
        customHeader.put("header2", values);
        final DownloadChain chain = TestUtils.mockDownloadChain();
        Mockito.when(chain.getInfo().getBlock(0)).thenReturn(new BlockInfo(0, 10));
        final DownloadConnection connection = chain.getConnectionOrCreate();
        final DownloadConnection.Connected connected = chain.processConnect();
        final BreakpointInfo info = chain.getInfo();
        final DownloadTask task = chain.getTask();
        Mockito.when(task.getHeaderMapFields()).thenReturn(customHeader);
        Mockito.when(info.getEtag()).thenReturn("etag1");
        interceptor.interceptConnect(chain);
        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(connection, Mockito.times(6)).addHeader(nameCaptor.capture(), valueCaptor.capture());
        assertThat(nameCaptor.getAllValues()).containsExactlyInAnyOrder("header1", "header1", "header2", Util.RANGE, Util.IF_MATCH, Util.USER_AGENT);
        assertThat(valueCaptor.getAllValues()).containsExactlyInAnyOrder("header1-value1", "header1-value2", "header2-value", "bytes=0-9", "etag1", ("OkDownload/" + (BuildConfig.VERSION_NAME)));
        Mockito.verify(OkDownload.with().downloadStrategy()).resumeAvailableResponseCheck(ArgumentMatchers.eq(connected), ArgumentMatchers.eq(0), ArgumentMatchers.eq(info));
        Mockito.verify(OkDownload.with().downloadStrategy().resumeAvailableResponseCheck(connected, 0, info)).inspect();
        ArgumentCaptor<Long> contentLengthCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(chain).setResponseContentLength(contentLengthCaptor.capture());
        assertThat(contentLengthCaptor.getValue()).isEqualTo(Util.CHUNKED_CONTENT_LENGTH);
        Mockito.when(connected.getResponseHeaderField(Util.CONTENT_LENGTH)).thenReturn("10");
        interceptor.interceptConnect(chain);
        Mockito.verify(chain, Mockito.times(2)).setResponseContentLength(contentLengthCaptor.capture());
        assertThat(contentLengthCaptor.getAllValues()).containsOnly((-1L), 10L);
        Mockito.when(connected.getResponseHeaderField(Util.CONTENT_LENGTH)).thenReturn(null);
        Mockito.when(connected.getResponseHeaderField(Util.CONTENT_RANGE)).thenReturn("bytes 2-5/333");
        interceptor.interceptConnect(chain);
        Mockito.verify(chain, Mockito.times(3)).setResponseContentLength(contentLengthCaptor.capture());
        assertThat(contentLengthCaptor.getAllValues()).containsOnly((-1L), 10L, 4L);
    }

    @Test
    public void interceptConnect_userProvideUserAgent() throws IOException {
        Map<String, List<String>> customHeader = new HashMap<>();
        List<String> values = new ArrayList<>();
        values.add("header1-value1");
        values.add("header1-value2");
        customHeader.put(Util.USER_AGENT, values);
        values = new ArrayList<>();
        values.add("header2-value");
        customHeader.put("header2", values);
        final DownloadChain chain = TestUtils.mockDownloadChain();
        Mockito.when(chain.getInfo().getBlock(0)).thenReturn(new BlockInfo(0, 10));
        final DownloadConnection connection = chain.getConnectionOrCreate();
        final DownloadConnection.Connected connected = chain.processConnect();
        final BreakpointInfo info = chain.getInfo();
        final DownloadTask task = chain.getTask();
        Mockito.when(task.getHeaderMapFields()).thenReturn(customHeader);
        interceptor.interceptConnect(chain);
        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(connection, Mockito.times(4)).addHeader(nameCaptor.capture(), valueCaptor.capture());
        assertThat(nameCaptor.getAllValues()).containsExactlyInAnyOrder(Util.USER_AGENT, Util.USER_AGENT, Util.RANGE, "header2");
        assertThat(valueCaptor.getAllValues()).containsExactlyInAnyOrder("header1-value1", "header1-value2", "header2-value", "bytes=0-9");
    }
}

