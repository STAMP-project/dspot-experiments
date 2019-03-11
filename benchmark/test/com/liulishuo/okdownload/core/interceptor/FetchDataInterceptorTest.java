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


import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.OkDownload;
import com.liulishuo.okdownload.core.dispatcher.CallbackDispatcher;
import com.liulishuo.okdownload.core.download.DownloadChain;
import com.liulishuo.okdownload.core.download.DownloadStrategy;
import com.liulishuo.okdownload.core.file.MultiPointOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class FetchDataInterceptorTest {
    @Mock
    private InputStream inputStream;

    @Mock
    private MultiPointOutputStream outputStream;

    @Mock
    private DownloadTask task;

    @Mock
    private DownloadChain chain;

    private FetchDataInterceptor interceptor;

    @Test
    public void interceptFetch() throws IOException {
        final CallbackDispatcher dispatcher = OkDownload.with().callbackDispatcher();
        Mockito.doReturn(10).when(inputStream).read(ArgumentMatchers.any(byte[].class));
        Mockito.doReturn(true).when(dispatcher).isFetchProcessMoment(task);
        interceptor.interceptFetch(chain);
        final DownloadStrategy downloadStrategy = OkDownload.with().downloadStrategy();
        Mockito.verify(downloadStrategy).inspectNetworkOnWifi(ArgumentMatchers.eq(task));
        Mockito.verify(chain).increaseCallbackBytes(10L);
        Mockito.verify(chain).flushNoCallbackIncreaseBytes();
        Mockito.verify(outputStream).write(ArgumentMatchers.eq(0), ArgumentMatchers.any(byte[].class), ArgumentMatchers.eq(10));
    }
}

