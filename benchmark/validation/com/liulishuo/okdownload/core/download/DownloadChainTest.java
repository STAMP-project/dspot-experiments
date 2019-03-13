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
package com.liulishuo.okdownload.core.download;


import DownloadConnection.Connected;
import DownloadConnection.Factory;
import Interceptor.Connect;
import Interceptor.Fetch;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.OkDownload;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.breakpoint.DownloadStore;
import com.liulishuo.okdownload.core.connection.DownloadConnection;
import com.liulishuo.okdownload.core.exception.InterruptException;
import com.liulishuo.okdownload.core.interceptor.BreakpointInterceptor;
import com.liulishuo.okdownload.core.interceptor.FetchDataInterceptor;
import com.liulishuo.okdownload.core.interceptor.Interceptor;
import com.liulishuo.okdownload.core.interceptor.RetryInterceptor;
import com.liulishuo.okdownload.core.interceptor.connect.CallServerInterceptor;
import com.liulishuo.okdownload.core.interceptor.connect.HeaderInterceptor;
import java.io.IOException;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class DownloadChainTest {
    private DownloadChain chain;

    @Mock
    private DownloadStore store;

    @Mock
    private BreakpointInfo info;

    @Mock
    private DownloadCache cache;

    @Test
    public void getConnectionOrCreate() throws IOException {
        final String infoUrl = "infoUrl";
        final DownloadConnection.Factory connectionFactory = OkDownload.with().connectionFactory();
        final BreakpointInfo info = Mockito.mock(BreakpointInfo.class);
        Mockito.when(info.getUrl()).thenReturn(infoUrl);
        DownloadChain chain = DownloadChain.createChain(0, Mockito.mock(DownloadTask.class), info, Mockito.mock(DownloadCache.class), store);
        // using info url
        final DownloadConnection connection = chain.getConnectionOrCreate();
        Mockito.verify(connectionFactory).create(infoUrl);
        // using created one.
        assertThat(chain.getConnectionOrCreate()).isEqualTo(connection);
        final String redirectLocation = "redirectLocation";
        final DownloadCache cache = Mockito.mock(DownloadCache.class);
        Mockito.when(cache.getRedirectLocation()).thenReturn(redirectLocation);
        chain = DownloadChain.createChain(0, Mockito.mock(DownloadTask.class), info, cache, store);
        // using redirect location instead of info url.
        chain.getConnectionOrCreate();
        Mockito.verify(connectionFactory).create(redirectLocation);
    }

    @Test(expected = InterruptException.class)
    public void start_interrupt() throws IOException {
        Mockito.when(cache.isInterrupt()).thenReturn(true);
        Mockito.doReturn(Mockito.mock(Connected.class)).when(chain).processConnect();
        chain.start();
    }

    @Test
    public void start() throws IOException {
        final DownloadConnection.Connected connected = Mockito.mock(Connected.class);
        Mockito.doReturn(connected).when(chain).processConnect();
        Mockito.doReturn(100L).when(chain).processFetch();
        chain.start();
        final List<Interceptor.Connect> connectInterceptorList = chain.connectInterceptorList;
        assertThat(connectInterceptorList).hasSize(4);
        assertThat(connectInterceptorList.get(0)).isInstanceOf(RetryInterceptor.class);
        assertThat(connectInterceptorList.get(1)).isInstanceOf(BreakpointInterceptor.class);
        assertThat(connectInterceptorList.get(2)).isInstanceOf(HeaderInterceptor.class);
        assertThat(connectInterceptorList.get(3)).isInstanceOf(CallServerInterceptor.class);
        final List<Interceptor.Fetch> fetchInterceptorList = chain.fetchInterceptorList;
        assertThat(fetchInterceptorList).hasSize(3);
        assertThat(fetchInterceptorList.get(0)).isInstanceOf(RetryInterceptor.class);
        assertThat(fetchInterceptorList.get(1)).isInstanceOf(BreakpointInterceptor.class);
        assertThat(fetchInterceptorList.get(2)).isInstanceOf(FetchDataInterceptor.class);
    }

    @Test
    public void processConnect() throws IOException {
        final Interceptor.Connect connect = Mockito.mock(Connect.class);
        chain.connectInterceptorList.add(connect);
        chain.connectIndex = 0;
        chain.processConnect();
        assertThat(chain.connectIndex).isEqualTo(1);
        Mockito.verify(connect).interceptConnect(chain);
    }

    @Test
    public void processFetch() throws IOException {
        final Interceptor.Fetch fetch = Mockito.mock(Fetch.class);
        chain.fetchInterceptorList.add(fetch);
        chain.fetchIndex = 0;
        chain.processFetch();
        assertThat(chain.fetchIndex).isEqualTo(1);
        Mockito.verify(fetch).interceptFetch(chain);
    }

    @Test
    public void loopFetch() throws IOException {
        final Interceptor.Fetch fetch1 = Mockito.mock(Fetch.class);
        chain.fetchInterceptorList.add(fetch1);
        final Interceptor.Fetch fetch2 = Mockito.mock(Fetch.class);
        chain.fetchInterceptorList.add(fetch2);
        final Interceptor.Fetch fetch3 = Mockito.mock(Fetch.class);
        chain.fetchInterceptorList.add(fetch3);
        chain.fetchIndex = 0;
        // 1
        chain.loopFetch();
        // 2
        chain.loopFetch();
        // 3
        chain.loopFetch();
        // 3
        chain.loopFetch();
        // 3
        chain.loopFetch();
        assertThat(chain.fetchIndex).isEqualTo(3);
        Mockito.verify(fetch1).interceptFetch(chain);
        Mockito.verify(fetch2).interceptFetch(chain);
        Mockito.verify(fetch3, Mockito.times(3)).interceptFetch(chain);
    }

    @Test(expected = IllegalAccessError.class)
    public void run_twiceTime() throws IOException {
        Mockito.doNothing().when(chain).start();
        chain.run();
        Mockito.verify(chain).releaseConnectionAsync();
        chain.run();
    }

    @Test
    public void flushNoCallbackIncreaseBytes() {
        chain.increaseCallbackBytes(10L);
        chain.increaseCallbackBytes(6L);
        assertThat(chain.noCallbackIncreaseBytes).isEqualTo(16L);
        chain.flushNoCallbackIncreaseBytes();
        Mockito.verify(OkDownload.with().callbackDispatcher().dispatch()).fetchProgress(ArgumentMatchers.eq(chain.getTask()), ArgumentMatchers.eq(0), ArgumentMatchers.eq(16L));
        assertThat(chain.noCallbackIncreaseBytes).isZero();
    }

    @Test
    public void setResponseContentLength() {
        chain.setResponseContentLength(10);
        assertThat(chain.getResponseContentLength()).isEqualTo(10);
    }

    @Test
    public void cancel() {
        chain.currentThread = Mockito.mock(Thread.class);
        chain.finished.set(true);
        chain.cancel();
        Mockito.verify(chain.currentThread, Mockito.never()).interrupt();
        chain.finished.set(false);
        chain.currentThread = null;
        chain.cancel();
        chain.currentThread = Mockito.mock(Thread.class);
        chain.cancel();
        Mockito.verify(chain.currentThread).interrupt();
    }

    @Test
    public void getInfo() {
        assertThat(chain.getInfo()).isEqualTo(info);
    }

    @Test
    public void connection() {
        final DownloadConnection connection = Mockito.mock(DownloadConnection.class);
        chain.setConnection(connection);
        assertThat(chain.getConnection()).isEqualTo(connection);
    }

    @Test
    public void getCache() {
        assertThat(chain.getCache()).isEqualTo(cache);
    }

    @Test
    public void releaseConnection() {
        final DownloadConnection connection = Mockito.mock(DownloadConnection.class);
        chain.setConnection(connection);
        chain.releaseConnection();
        Mockito.verify(connection).release();
        assertThat(chain.getConnection()).isNull();
    }

    @Test
    public void resetConnectForRetry() {
        chain.connectIndex = 2;
        Mockito.doNothing().when(chain).releaseConnection();
        chain.resetConnectForRetry();
        assertThat(chain.connectIndex).isEqualTo(1);
        Mockito.verify(chain).releaseConnection();
    }

    @Test
    public void getDownloadStore() {
        assertThat(chain.getDownloadStore()).isEqualTo(store);
    }
}

