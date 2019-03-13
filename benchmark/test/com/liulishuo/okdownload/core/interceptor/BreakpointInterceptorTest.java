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
import com.liulishuo.okdownload.DownloadListener;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.OkDownload;
import com.liulishuo.okdownload.TestUtils;
import com.liulishuo.okdownload.core.Util;
import com.liulishuo.okdownload.core.breakpoint.BlockInfo;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.breakpoint.DownloadStore;
import com.liulishuo.okdownload.core.download.DownloadCache;
import com.liulishuo.okdownload.core.download.DownloadChain;
import com.liulishuo.okdownload.core.exception.RetryException;
import com.liulishuo.okdownload.core.file.MultiPointOutputStream;
import java.io.IOException;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class BreakpointInterceptorTest {
    private BreakpointInterceptor interceptor;

    @Mock
    private BreakpointInfo info;

    @Mock
    private DownloadStore store;

    @Mock
    private Connected connected;

    @Mock
    private DownloadCache cache;

    @Mock
    private BlockInfo blockInfo;

    @Mock
    private DownloadTask task;

    private DownloadChain chain;

    private final String existPath = "./exist-path";

    @Test
    public void interceptConnect_process() throws IOException {
        Mockito.when(store.update(ArgumentMatchers.any(BreakpointInfo.class))).thenReturn(true);
        interceptor.interceptConnect(chain);
        Mockito.verify(store).update(chain.getInfo());
        Mockito.verify(chain).processConnect();
    }

    @Test
    public void interceptFetch_finish() throws IOException {
        BlockInfo blockInfo2 = new BlockInfo(10, 20);
        Mockito.when(chain.getResponseContentLength()).thenReturn(new Long(10));
        Mockito.when(chain.getInfo()).thenReturn(info);
        Mockito.when(chain.getOutputStream()).thenReturn(Mockito.mock(MultiPointOutputStream.class));
        Mockito.when(chain.getBlockIndex()).thenReturn(1);
        Mockito.when(info.getBlock(1)).thenReturn(blockInfo2);
        Mockito.when(info.getBlockCount()).thenReturn(2);
        Mockito.when(chain.loopFetch()).thenReturn(1L, 1L, 2L, 1L, 5L, (-1L));
        final long contentLength = interceptor.interceptFetch(chain);
        Mockito.verify(chain, Mockito.times(6)).loopFetch();
        Mockito.verify(chain).flushNoCallbackIncreaseBytes();
        assertThat(contentLength).isEqualTo(10);
    }

    @Test
    public void interceptFetch_chunked() throws IOException {
        final BlockInfo blockInfo = new BlockInfo(0, 0);
        Mockito.when(chain.getResponseContentLength()).thenReturn(Long.valueOf(Util.CHUNKED_CONTENT_LENGTH));
        Mockito.when(chain.getInfo()).thenReturn(info);
        Mockito.when(chain.getOutputStream()).thenReturn(Mockito.mock(MultiPointOutputStream.class));
        Mockito.when(info.getBlock(ArgumentMatchers.anyInt())).thenReturn(blockInfo);
        Mockito.when(chain.loopFetch()).thenReturn(1L, 1L, 2L, 1L, 5L, (-1L));
        final long contentLength = interceptor.interceptFetch(chain);
        Mockito.verify(chain, Mockito.times(6)).loopFetch();
        assertThat(contentLength).isEqualTo(10);
    }

    @Test
    public void getRangeRightFromContentRange() {
        assertThat(BreakpointInterceptor.getRangeRightFromContentRange("bytes 1-111/222")).isEqualTo(111L);
        assertThat(BreakpointInterceptor.getRangeRightFromContentRange("bytes 1 -111/222")).isEqualTo(111L);
        assertThat(BreakpointInterceptor.getRangeRightFromContentRange("bytes 1 - 111/222")).isEqualTo(111L);
        assertThat(BreakpointInterceptor.getRangeRightFromContentRange("bytes 1 - 111 /222")).isEqualTo(111L);
        assertThat(BreakpointInterceptor.getRangeRightFromContentRange("bytes 1 - 111 / 222")).isEqualTo(111L);
    }

    @Test
    public void getExactContentLength_contentRange() {
        Mockito.when(connected.getResponseHeaderField(Util.CONTENT_RANGE)).thenReturn("bytes 0-111/222");
        assertThat(interceptor.getExactContentLengthRangeFrom0(connected)).isEqualTo(112L);
    }

    @Test
    public void getExactContentLength_contentLength() {
        Mockito.when(connected.getResponseHeaderField(Util.CONTENT_LENGTH)).thenReturn("123");
        assertThat(interceptor.getExactContentLengthRangeFrom0(connected)).isEqualTo(123L);
    }

    @Test
    public void interceptConnect_singleBlockCheck() throws IOException {
        TestUtils.mockOkDownload();
        Mockito.when(chain.processConnect()).thenReturn(connected);
        Mockito.when(chain.getInfo()).thenReturn(info);
        Mockito.when(chain.getCache()).thenReturn(cache);
        Mockito.when(info.getBlock(0)).thenReturn(blockInfo);
        Mockito.when(info.getBlockCount()).thenReturn(1);
        Mockito.when(info.isChunked()).thenReturn(false);
        Mockito.when(info.getTotalLength()).thenReturn(1L);
        Mockito.doReturn(2L).when(interceptor).getExactContentLengthRangeFrom0(connected);
        interceptor.interceptConnect(chain);
        ArgumentCaptor<BlockInfo> captor = ArgumentCaptor.forClass(BlockInfo.class);
        Mockito.verify(info).addBlock(captor.capture());
        Mockito.verify(info).resetBlockInfos();
        final BlockInfo addBlockInfo = captor.getValue();
        assertThat(addBlockInfo.getRangeLeft()).isZero();
        assertThat(addBlockInfo.getContentLength()).isEqualTo(2L);
        final DownloadListener listener = OkDownload.with().callbackDispatcher().dispatch();
        Mockito.verify(listener).downloadFromBeginning(ArgumentMatchers.eq(task), ArgumentMatchers.eq(info), ArgumentMatchers.eq(CONTENT_LENGTH_CHANGED));
    }

    @Test(expected = RetryException.class)
    public void interceptConnect_singleBlockCheck_fromBreakpoint() throws IOException {
        Mockito.when(chain.processConnect()).thenReturn(connected);
        Mockito.when(chain.getInfo()).thenReturn(info);
        Mockito.when(chain.getCache()).thenReturn(cache);
        Mockito.when(info.getBlock(0)).thenReturn(blockInfo);
        Mockito.when(info.getBlockCount()).thenReturn(1);
        Mockito.when(info.isChunked()).thenReturn(false);
        Mockito.when(info.getTotalLength()).thenReturn(1L);
        Mockito.doReturn(2L).when(interceptor).getExactContentLengthRangeFrom0(connected);
        Mockito.when(blockInfo.getRangeLeft()).thenReturn(1L);
        interceptor.interceptConnect(chain);
    }

    @Test
    public void interceptConnect_singleBlockCheck_expect() throws IOException {
        Mockito.when(chain.processConnect()).thenReturn(connected);
        Mockito.when(chain.getInfo()).thenReturn(info);
        Mockito.when(chain.getCache()).thenReturn(cache);
        Mockito.when(info.getBlockCount()).thenReturn(1);
        Mockito.when(info.isChunked()).thenReturn(false);
        Mockito.when(info.getBlock(0)).thenReturn(blockInfo);
        Mockito.when(info.getTotalLength()).thenReturn(2L);
        Mockito.doReturn(2L).when(interceptor).getExactContentLengthRangeFrom0(connected);
        interceptor.interceptConnect(chain);
        Mockito.verify(info, Mockito.never()).resetBlockInfos();
        Mockito.verify(info, Mockito.never()).addBlock(ArgumentMatchers.any(BlockInfo.class));
    }

    @Test
    public void interceptConnect_notSingleBlockOrChunked() throws IOException {
        Mockito.when(chain.processConnect()).thenReturn(connected);
        Mockito.when(chain.getInfo()).thenReturn(info);
        Mockito.when(chain.getCache()).thenReturn(cache);
        // not single block
        Mockito.when(info.getBlockCount()).thenReturn(2);
        Mockito.when(info.isChunked()).thenReturn(false);
        interceptor.interceptConnect(chain);
        Mockito.verify(info, Mockito.never()).resetBlockInfos();
        Mockito.verify(info, Mockito.never()).addBlock(ArgumentMatchers.any(BlockInfo.class));
        // chunked
        Mockito.when(info.getBlockCount()).thenReturn(1);
        Mockito.when(info.isChunked()).thenReturn(true);
        interceptor.interceptConnect(chain);
        Mockito.verify(info, Mockito.never()).resetBlockInfos();
        Mockito.verify(info, Mockito.never()).addBlock(ArgumentMatchers.any(BlockInfo.class));
    }
}

