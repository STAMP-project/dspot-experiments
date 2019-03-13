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


import DownloadConnection.Connected;
import DownloadConnection.Factory;
import com.liulishuo.okdownload.DownloadListener;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.OkDownload;
import com.liulishuo.okdownload.core.Util;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.connection.DownloadConnection;
import com.liulishuo.okdownload.core.dispatcher.CallbackDispatcher;
import com.liulishuo.okdownload.core.exception.DownloadSecurityException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class ConnectTrialTest {
    private ConnectTrial connectTrial;

    @Mock
    private DownloadTask task;

    @Mock
    private BreakpointInfo info;

    @Mock
    private DownloadConnection connection;

    @Mock
    private Connected connected;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final String etag = "etag";

    private final String url = "https://jacksgong.com";

    @Test
    public void executeTrial() throws Exception {
        final String redirectLocation = "http://location";
        Mockito.when(connected.getRedirectLocation()).thenReturn(redirectLocation);
        connectTrial.executeTrial();
        final DownloadStrategy downloadStrategy = OkDownload.with().downloadStrategy();
        Mockito.verify(downloadStrategy).inspectNetworkOnWifi(ArgumentMatchers.eq(task));
        Mockito.verify(downloadStrategy).inspectNetworkAvailable();
        Mockito.verify(connection).addHeader(ArgumentMatchers.eq(Util.IF_MATCH), ArgumentMatchers.eq(etag));
        Mockito.verify(connection).addHeader(ArgumentMatchers.eq(Util.RANGE), ArgumentMatchers.eq("bytes=0-0"));
        Mockito.verify(task).setRedirectLocation(redirectLocation);
        Mockito.verify(connection).execute();
        Mockito.verify(connection).release();
    }

    @Test
    public void executeTrial_userHeader() throws Exception {
        Map<String, List<String>> userHeader = new HashMap<>();
        List<String> values = new ArrayList<>();
        values.add("header1-value1");
        values.add("header1-value2");
        userHeader.put("header1", values);
        values = new ArrayList<>();
        values.add("header2-value");
        userHeader.put("header2", values);
        Mockito.when(task.getHeaderMapFields()).thenReturn(userHeader);
        connectTrial.executeTrial();
        Mockito.verify(connection).addHeader(ArgumentMatchers.eq("header1"), ArgumentMatchers.eq("header1-value1"));
        Mockito.verify(connection).addHeader(ArgumentMatchers.eq("header1"), ArgumentMatchers.eq("header1-value2"));
        Mockito.verify(connection).addHeader(ArgumentMatchers.eq("header2"), ArgumentMatchers.eq("header2-value"));
    }

    @Test
    public void executeTrial_needTrialHeadMethod() throws IOException {
        Mockito.when(connectTrial.isNeedTrialHeadMethodForInstanceLength(ArgumentMatchers.anyLong(), ArgumentMatchers.eq(connected))).thenReturn(true);
        connectTrial.executeTrial();
        Mockito.verify(connectTrial).isNeedTrialHeadMethodForInstanceLength(ArgumentMatchers.anyLong(), ArgumentMatchers.eq(connected));
        Mockito.verify(connectTrial).trialHeadMethodForInstanceLength();
    }

    @Test
    public void executeTrial_noNeedTrialHeadMethod() throws IOException {
        Mockito.when(connectTrial.isNeedTrialHeadMethodForInstanceLength(ArgumentMatchers.anyLong(), ArgumentMatchers.eq(connected))).thenReturn(false);
        connectTrial.executeTrial();
        Mockito.verify(connectTrial).isNeedTrialHeadMethodForInstanceLength(ArgumentMatchers.anyLong(), ArgumentMatchers.eq(connected));
        Mockito.verify(connectTrial, Mockito.never()).trialHeadMethodForInstanceLength();
    }

    @Test
    public void getInstanceLength() throws Exception {
        Mockito.when(connected.getResponseHeaderField(Util.CONTENT_RANGE)).thenReturn("bytes 21010-47021/47022");
        // when(connected.getResponseHeaderField(CONTENT_LENGTH))
        // .thenReturn("100");
        connectTrial.executeTrial();
        assertThat(connectTrial.getInstanceLength()).isEqualTo(47022L);
        assertThat(connectTrial.isChunked()).isFalse();
        Mockito.when(connected.getResponseHeaderField(Util.CONTENT_RANGE)).thenReturn(null);
        // connectTrial.executeTrial();
        // assertThat(connectTrial.getInstanceLength()).isEqualTo(100L);
        // assertThat(connectTrial.isChunked()).isFalse();
        // when(connected.getResponseHeaderField(CONTENT_LENGTH))
        // .thenReturn(null);
        connectTrial.executeTrial();
        assertThat(connectTrial.getInstanceLength()).isEqualTo(Util.CHUNKED_CONTENT_LENGTH);
        assertThat(connectTrial.isChunked()).isTrue();
    }

    @Test
    public void isAcceptRange() throws Exception {
        Mockito.when(connected.getResponseCode()).thenReturn(HttpURLConnection.HTTP_PARTIAL);
        connectTrial.executeTrial();
        assertThat(connectTrial.isAcceptRange()).isTrue();
        Mockito.when(connected.getResponseCode()).thenReturn(0);
        connectTrial.executeTrial();
        assertThat(connectTrial.isAcceptRange()).isFalse();
        Mockito.when(connected.getResponseHeaderField(Util.ACCEPT_RANGES)).thenReturn("bytes");
        connectTrial.executeTrial();
        assertThat(connectTrial.isAcceptRange()).isTrue();
    }

    @Test
    public void isEtagOverdue() throws Exception {
        Mockito.when(connected.getResponseHeaderField(Util.ETAG)).thenReturn(etag);
        connectTrial.executeTrial();
        assertThat(connectTrial.isEtagOverdue()).isFalse();
        Mockito.when(connected.getResponseHeaderField(Util.ETAG)).thenReturn("newEtag");
        connectTrial.executeTrial();
        assertThat(connectTrial.isEtagOverdue()).isTrue();
        assertThat(connectTrial.getResponseEtag()).isEqualTo("newEtag");
    }

    @Test
    public void getResponseFilename() throws IOException {
        connectTrial.executeTrial();
        assertThat(connectTrial.getResponseFilename()).isNull();
        Mockito.when(connected.getResponseHeaderField(Util.CONTENT_DISPOSITION)).thenReturn("attachment;      filename=\"hello world\"");
        connectTrial.executeTrial();
        assertThat(connectTrial.getResponseFilename()).isEqualTo("hello world");
        Mockito.when(connected.getResponseHeaderField(Util.CONTENT_DISPOSITION)).thenReturn("attachment; filename=\"hello world\"");
        connectTrial.executeTrial();
        assertThat(connectTrial.getResponseFilename()).isEqualTo("hello world");
        Mockito.when(connected.getResponseHeaderField(Util.CONTENT_DISPOSITION)).thenReturn("attachment; filename=genome.jpeg\nabc");
        connectTrial.executeTrial();
        assertThat(connectTrial.getResponseFilename()).isEqualTo("genome.jpeg");
        Mockito.when(connected.getResponseHeaderField(Util.CONTENT_DISPOSITION)).thenReturn("attachment; filename=../data/data/genome.png");
        thrown.expect(DownloadSecurityException.class);
        thrown.expectMessage(("The filename [../data/data/genome.png] from the response is not " + ("allowable, because it contains '../', which can raise the directory traversal " + "vulnerability")));
        connectTrial.executeTrial();
        Mockito.when(connected.getResponseHeaderField(Util.CONTENT_DISPOSITION)).thenReturn("attachment; filename=a/b/../abc");
        thrown.expect(DownloadSecurityException.class);
        thrown.expectMessage(("The filename [a/b/../abc] from the response is not " + ("allowable, because it contains '../', which can raise the directory traversal " + "vulnerability")));
        connectTrial.executeTrial();
    }

    @Test
    public void getResponseCode() throws Exception {
        Mockito.when(connected.getResponseCode()).thenReturn(1);
        connectTrial.executeTrial();
        assertThat(connectTrial.getResponseCode()).isEqualTo(1);
    }

    @Test
    public void isNeedTrialHeadMethodForInstanceLength_oldOneIsValid_false() {
        assertThat(connectTrial.isNeedTrialHeadMethodForInstanceLength(1, connected)).isFalse();
    }

    @Test
    public void isNeedTrialHeadMethodForInstanceLength_contentRangeValid_false() {
        Mockito.when(connected.getResponseHeaderField(Util.CONTENT_RANGE)).thenReturn("has value");
        assertThat(connectTrial.isNeedTrialHeadMethodForInstanceLength(Util.CHUNKED_CONTENT_LENGTH, connected)).isFalse();
    }

    @Test
    public void isNeedTrialHeadMethodForInstanceLength_chunked_false() {
        Mockito.when(connected.getResponseHeaderField(Util.TRANSFER_ENCODING)).thenReturn(Util.VALUE_CHUNKED);
        assertThat(connectTrial.isNeedTrialHeadMethodForInstanceLength(Util.CHUNKED_CONTENT_LENGTH, connected)).isFalse();
    }

    @Test
    public void isNeedTrialHeadMethodForInstanceLength_contentLengthNotResponse_false() {
        Mockito.when(connected.getResponseHeaderField(Util.CONTENT_LENGTH)).thenReturn(null);
        assertThat(connectTrial.isNeedTrialHeadMethodForInstanceLength(Util.CHUNKED_CONTENT_LENGTH, connected)).isFalse();
    }

    @Test
    public void isNeedTrialHeadMethodForInstanceLength_true() {
        Mockito.when(connected.getResponseHeaderField(Util.CONTENT_RANGE)).thenReturn(null);
        Mockito.when(connected.getResponseHeaderField(Util.TRANSFER_ENCODING)).thenReturn("not chunked");
        Mockito.when(connected.getResponseHeaderField(Util.CONTENT_LENGTH)).thenReturn("1");
        assertThat(connectTrial.isNeedTrialHeadMethodForInstanceLength(Util.CHUNKED_CONTENT_LENGTH, connected)).isTrue();
    }

    @Test
    public void trialHeadMethodForInstanceLength() throws IOException {
        final DownloadConnection.Factory factory = OkDownload.with().connectionFactory();
        final DownloadConnection connection = Mockito.mock(DownloadConnection.class);
        Mockito.when(factory.create(ArgumentMatchers.anyString())).thenReturn(connection);
        final DownloadConnection.Connected connected = Mockito.mock(Connected.class);
        Mockito.when(connection.execute()).thenReturn(connected);
        Mockito.when(connected.getResponseHeaderField(Util.CONTENT_LENGTH)).thenReturn("10");
        final CallbackDispatcher callbackDispatcher = OkDownload.with().callbackDispatcher();
        final DownloadListener listener = Mockito.mock(DownloadListener.class);
        Mockito.when(callbackDispatcher.dispatch()).thenReturn(listener);
        connectTrial.trialHeadMethodForInstanceLength();
        Mockito.verify(connection).setRequestMethod(ArgumentMatchers.eq(Util.METHOD_HEAD));
        Mockito.verify(listener).connectTrialStart(ArgumentMatchers.eq(task), ArgumentMatchers.nullable(Map.class));
        Mockito.verify(listener).connectTrialEnd(ArgumentMatchers.eq(task), ArgumentMatchers.anyInt(), ArgumentMatchers.nullable(Map.class));
        assertThat(connectTrial.getInstanceLength()).isEqualTo(10L);
    }

    @Test
    public void trialHeadMethodForInstanceLength_userHeader() throws Exception {
        Map<String, List<String>> userHeader = new HashMap<>();
        List<String> values = new ArrayList<>();
        values.add("header1-value1");
        values.add("header1-value2");
        userHeader.put("header1", values);
        values = new ArrayList<>();
        values.add("header2-value");
        userHeader.put("header2", values);
        Mockito.when(task.getHeaderMapFields()).thenReturn(userHeader);
        connectTrial.trialHeadMethodForInstanceLength();
        Mockito.verify(connection).addHeader(ArgumentMatchers.eq("header1"), ArgumentMatchers.eq("header1-value1"));
        Mockito.verify(connection).addHeader(ArgumentMatchers.eq("header1"), ArgumentMatchers.eq("header1-value2"));
        Mockito.verify(connection).addHeader(ArgumentMatchers.eq("header2"), ArgumentMatchers.eq("header2-value"));
    }
}

