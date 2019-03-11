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
package com.liulishuo.okdownload.core.interceptor;


import DownloadConnection.Connected;
import com.liulishuo.okdownload.core.connection.DownloadConnection;
import com.liulishuo.okdownload.core.download.DownloadCache;
import com.liulishuo.okdownload.core.download.DownloadChain;
import com.liulishuo.okdownload.core.exception.InterruptException;
import com.liulishuo.okdownload.core.exception.RetryException;
import com.liulishuo.okdownload.core.file.MultiPointOutputStream;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class RetryInterceptorTest {
    private RetryInterceptor interceptor;

    @Mock
    private DownloadChain chain;

    @Mock
    private DownloadCache cache;

    @Mock
    private Connected connected;

    @Mock
    private MultiPointOutputStream outputStream;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void interceptConnect_interrupt() throws IOException {
        Mockito.when(cache.isInterrupt()).thenReturn(true);
        thrown.expect(InterruptException.class);
        interceptor.interceptConnect(chain);
        Mockito.verify(chain, Mockito.never()).processConnect();
        Mockito.verify(outputStream).catchBlockConnectException(chain.getBlockIndex());
    }

    @Test
    public void interceptConnect_retry() throws IOException {
        Mockito.doThrow(RetryException.class).doReturn(connected).when(chain).processConnect();
        interceptor.interceptConnect(chain);
        Mockito.verify(chain, Mockito.times(2)).processConnect();
        Mockito.verify(chain).resetConnectForRetry();
        Mockito.verify(cache, Mockito.never()).catchException(ArgumentMatchers.any(IOException.class));
        Mockito.verify(outputStream, Mockito.never()).catchBlockConnectException(chain.getBlockIndex());
    }

    @Test
    public void interceptConnect_userCanceled() throws IOException {
        Mockito.doThrow(InterruptException.class).when(chain).processConnect();
        Mockito.when(cache.isUserCanceled()).thenReturn(true);
        thrown.expect(InterruptException.class);
        interceptor.interceptConnect(chain);
        Mockito.verify(cache).catchException(ArgumentMatchers.any(IOException.class));
        Mockito.verify(outputStream).catchBlockConnectException(chain.getBlockIndex());
    }

    @Test
    public void interceptConnect_failedReleaseConnection() throws IOException {
        final DownloadConnection connection = Mockito.mock(DownloadConnection.class);
        Mockito.when(chain.getConnection()).thenReturn(connection);
        Mockito.doThrow(IOException.class).doReturn(connected).when(chain).processConnect();
        thrown.expect(IOException.class);
        interceptor.interceptConnect(chain);
        Mockito.verify(cache).catchException(ArgumentMatchers.any(IOException.class));
        Mockito.verify(outputStream).catchBlockConnectException(ArgumentMatchers.anyInt());
    }

    @Test
    public void interceptFetch_failedRelease() throws IOException {
        final MultiPointOutputStream outputStream = Mockito.mock(MultiPointOutputStream.class);
        Mockito.when(chain.getOutputStream()).thenReturn(outputStream);
        Mockito.doThrow(IOException.class).when(chain).processFetch();
        thrown.expect(IOException.class);
        interceptor.interceptFetch(chain);
        Mockito.verify(cache).catchException(ArgumentMatchers.any(IOException.class));
        Mockito.verify(outputStream).catchBlockConnectException(ArgumentMatchers.anyInt());
    }
}

