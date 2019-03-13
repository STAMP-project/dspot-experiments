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
package com.liulishuo.okdownload.core.file;


import EndCause.CANCELED;
import MultiPointOutputStream.StreamsState;
import android.net.Uri;
import android.os.StatFs;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.OkDownload;
import com.liulishuo.okdownload.core.breakpoint.BlockInfo;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.breakpoint.DownloadStore;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.exception.PreAllocateException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


// for sparseArray.
@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class MultiPointOutputStreamTest {
    private MultiPointOutputStream multiPointOutputStream;

    private final String parentPath = "./p-path/";

    private final File existFile = new File("./p-path/filename");

    @Mock
    private BreakpointInfo info;

    @Mock
    private DownloadTask task;

    @Mock
    private DownloadStore store;

    @Mock
    private Runnable syncRunnable;

    @Mock
    private DownloadOutputStream stream0;

    @Mock
    private Future syncFuture;

    @Mock
    private Thread runSyncThread;

    final byte[] bytes = new byte[6];

    @Test
    public void write() throws IOException {
        Mockito.doReturn(stream0).when(multiPointOutputStream).outputStream(ArgumentMatchers.anyInt());
        Mockito.doNothing().when(multiPointOutputStream).inspectAndPersist();
        multiPointOutputStream.noSyncLengthMap.put(1, new AtomicLong());
        multiPointOutputStream.noSyncLengthMap.put(2, new AtomicLong());
        multiPointOutputStream.write(2, bytes, 16);
        Mockito.verify(stream0).write(ArgumentMatchers.eq(bytes), ArgumentMatchers.eq(0), ArgumentMatchers.eq(16));
        assertThat(multiPointOutputStream.allNoSyncLength.get()).isEqualTo(16);
        assertThat(multiPointOutputStream.noSyncLengthMap.get(1).get()).isEqualTo(0);
        assertThat(multiPointOutputStream.noSyncLengthMap.get(2).get()).isEqualTo(16);
        Mockito.verify(multiPointOutputStream).inspectAndPersist();
    }

    @Test
    public void write_notRun_withCancelled() throws IOException {
        multiPointOutputStream.canceled = true;
        multiPointOutputStream.write(0, bytes, 16);
        Mockito.verify(multiPointOutputStream, Mockito.never()).outputStream(0);
        Mockito.verify(multiPointOutputStream, Mockito.never()).inspectAndPersist();
    }

    @Test
    public void cancel_syncNotRun_withSyncFutureIsNull() throws IOException {
        multiPointOutputStream.requireStreamBlocks = new ArrayList<Integer>() {
            {
                add(0);
                add(1);
            }
        };
        multiPointOutputStream.allNoSyncLength.set(1);
        Mockito.doNothing().when(multiPointOutputStream).close(ArgumentMatchers.anyInt());
        Mockito.doNothing().when(multiPointOutputStream).ensureSync(true, (-1));
        multiPointOutputStream.syncFuture = null;
        final ProcessFileStrategy strategy = OkDownload.with().processFileStrategy();
        final FileLock fileLock = Mockito.mock(FileLock.class);
        Mockito.when(strategy.getFileLock()).thenReturn(fileLock);
        multiPointOutputStream.cancel();
        assertThat(multiPointOutputStream.noMoreStreamList).containsExactly(0, 1);
        Mockito.verify(multiPointOutputStream, Mockito.never()).ensureSync(ArgumentMatchers.eq(true), ArgumentMatchers.eq((-1)));
        Mockito.verify(multiPointOutputStream).close(ArgumentMatchers.eq(0));
        Mockito.verify(multiPointOutputStream).close(ArgumentMatchers.eq(1));
        Mockito.verify(fileLock, Mockito.never()).increaseLock(ArgumentMatchers.eq(existFile.getAbsolutePath()));
        Mockito.verify(fileLock, Mockito.never()).decreaseLock(ArgumentMatchers.eq(existFile.getAbsolutePath()));
        Mockito.verify(store).onTaskEnd(ArgumentMatchers.eq(task.getId()), ArgumentMatchers.eq(CANCELED), ArgumentMatchers.nullable(Exception.class));
    }

    @Test
    public void cancel_syncNotRun_withSyncFutureHasDone() throws IOException {
        multiPointOutputStream.requireStreamBlocks = new ArrayList<Integer>() {
            {
                add(0);
                add(1);
            }
        };
        multiPointOutputStream.allNoSyncLength.set(1);
        Mockito.doNothing().when(multiPointOutputStream).close(ArgumentMatchers.anyInt());
        Mockito.doNothing().when(multiPointOutputStream).ensureSync(true, (-1));
        multiPointOutputStream.syncFuture = Mockito.mock(Future.class);
        Mockito.when(multiPointOutputStream.syncFuture.isDone()).thenReturn(true);
        final ProcessFileStrategy strategy = OkDownload.with().processFileStrategy();
        final FileLock fileLock = Mockito.mock(FileLock.class);
        Mockito.when(strategy.getFileLock()).thenReturn(fileLock);
        multiPointOutputStream.cancel();
        assertThat(multiPointOutputStream.noMoreStreamList).containsExactly(0, 1);
        Mockito.verify(multiPointOutputStream, Mockito.never()).ensureSync(ArgumentMatchers.eq(true), ArgumentMatchers.eq((-1)));
        Mockito.verify(multiPointOutputStream).close(ArgumentMatchers.eq(0));
        Mockito.verify(multiPointOutputStream).close(ArgumentMatchers.eq(1));
        Mockito.verify(fileLock, Mockito.never()).increaseLock(ArgumentMatchers.eq(existFile.getAbsolutePath()));
        Mockito.verify(fileLock, Mockito.never()).decreaseLock(ArgumentMatchers.eq(existFile.getAbsolutePath()));
        Mockito.verify(store).onTaskEnd(ArgumentMatchers.eq(task.getId()), ArgumentMatchers.eq(CANCELED), ArgumentMatchers.nullable(Exception.class));
    }

    @Test
    public void cancel_notRun_withRequireStreamBlocksNotInitial() throws IOException {
        multiPointOutputStream.allNoSyncLength.set(1);
        final ProcessFileStrategy strategy = OkDownload.with().processFileStrategy();
        final FileLock fileLock = Mockito.mock(FileLock.class);
        Mockito.when(strategy.getFileLock()).thenReturn(fileLock);
        multiPointOutputStream.cancel();
        Mockito.verify(multiPointOutputStream, Mockito.never()).ensureSync(ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyInt());
        Mockito.verify(fileLock, Mockito.never()).increaseLock(ArgumentMatchers.eq(existFile.getAbsolutePath()));
        Mockito.verify(fileLock, Mockito.never()).decreaseLock(ArgumentMatchers.eq(existFile.getAbsolutePath()));
        Mockito.verify(multiPointOutputStream, Mockito.never()).close(ArgumentMatchers.anyInt());
        Mockito.verify(store, Mockito.never()).onTaskEnd(ArgumentMatchers.anyInt(), ArgumentMatchers.any(EndCause.class), ArgumentMatchers.any(Exception.class));
    }

    @Test
    public void cancel_notRun_withCancelled() throws IOException {
        multiPointOutputStream.allNoSyncLength.set(1);
        multiPointOutputStream.canceled = true;
        final ProcessFileStrategy strategy = OkDownload.with().processFileStrategy();
        final FileLock fileLock = Mockito.mock(FileLock.class);
        Mockito.when(strategy.getFileLock()).thenReturn(fileLock);
        multiPointOutputStream.cancel();
        assertThat(multiPointOutputStream.noMoreStreamList).hasSize(0);
        Mockito.verify(multiPointOutputStream, Mockito.never()).ensureSync(ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyInt());
        Mockito.verify(fileLock, Mockito.never()).increaseLock(ArgumentMatchers.eq(existFile.getAbsolutePath()));
        Mockito.verify(fileLock, Mockito.never()).decreaseLock(ArgumentMatchers.eq(existFile.getAbsolutePath()));
        Mockito.verify(multiPointOutputStream, Mockito.never()).close(ArgumentMatchers.anyInt());
        Mockito.verify(store, Mockito.never()).onTaskEnd(ArgumentMatchers.anyInt(), ArgumentMatchers.any(EndCause.class), ArgumentMatchers.any(Exception.class));
    }

    @Test
    public void cancel() throws IOException {
        multiPointOutputStream.requireStreamBlocks = new ArrayList<Integer>() {
            {
                add(0);
                add(1);
            }
        };
        multiPointOutputStream.noSyncLengthMap.put(0, new AtomicLong());
        multiPointOutputStream.noSyncLengthMap.put(1, new AtomicLong());
        multiPointOutputStream.allNoSyncLength.set(1);
        final ProcessFileStrategy strategy = OkDownload.with().processFileStrategy();
        final FileLock fileLock = Mockito.mock(FileLock.class);
        Mockito.when(strategy.getFileLock()).thenReturn(fileLock);
        Mockito.doNothing().when(multiPointOutputStream).close(ArgumentMatchers.anyInt());
        Mockito.doNothing().when(multiPointOutputStream).ensureSync(true, (-1));
        multiPointOutputStream.cancel();
        assertThat(multiPointOutputStream.noMoreStreamList).containsExactly(0, 1);
        Mockito.verify(multiPointOutputStream).ensureSync(ArgumentMatchers.eq(true), ArgumentMatchers.eq((-1)));
        Mockito.verify(multiPointOutputStream).close(ArgumentMatchers.eq(0));
        Mockito.verify(multiPointOutputStream).close(ArgumentMatchers.eq(1));
        Mockito.verify(fileLock).increaseLock(ArgumentMatchers.eq(existFile.getAbsolutePath()));
        Mockito.verify(fileLock).decreaseLock(ArgumentMatchers.eq(existFile.getAbsolutePath()));
        Mockito.verify(store).onTaskEnd(ArgumentMatchers.eq(task.getId()), ArgumentMatchers.eq(CANCELED), ArgumentMatchers.nullable(Exception.class));
    }

    @Test
    public void ensureSync_syncJobNotRunYet() {
        multiPointOutputStream.syncFuture = null;
        multiPointOutputStream.ensureSync(true, (-1));
        Mockito.verify(multiPointOutputStream, Mockito.never()).unparkThread(ArgumentMatchers.any(Thread.class));
        Mockito.verify(multiPointOutputStream, Mockito.never()).parkThread();
        assertThat(multiPointOutputStream.parkedRunBlockThreadMap.size()).isZero();
        multiPointOutputStream.syncFuture = syncFuture;
        Mockito.when(syncFuture.isDone()).thenReturn(true);
        multiPointOutputStream.ensureSync(true, (-1));
        Mockito.verify(multiPointOutputStream, Mockito.never()).unparkThread(ArgumentMatchers.any(Thread.class));
        Mockito.verify(multiPointOutputStream, Mockito.never()).parkThread();
        assertThat(multiPointOutputStream.parkedRunBlockThreadMap.size()).isZero();
    }

    @Test
    public void ensureSync_noMoreStream() throws InterruptedException, ExecutionException {
        Mockito.doNothing().when(multiPointOutputStream).unparkThread(ArgumentMatchers.nullable(Thread.class));
        Mockito.doNothing().when(multiPointOutputStream).parkThread();
        Mockito.doNothing().when(multiPointOutputStream).parkThread(25);
        multiPointOutputStream.ensureSync(true, (-1));
        Mockito.verify(multiPointOutputStream, Mockito.times(2)).unparkThread(ArgumentMatchers.eq(runSyncThread));
        Mockito.verify(syncFuture).get();
        Mockito.verify(multiPointOutputStream, Mockito.never()).parkThread();
        assertThat(multiPointOutputStream.parkedRunBlockThreadMap.size()).isZero();
    }

    @Test
    public void ensureSync_notNoMoreStream() {
        Mockito.doNothing().when(multiPointOutputStream).unparkThread(ArgumentMatchers.nullable(Thread.class));
        Mockito.doNothing().when(multiPointOutputStream).parkThread();
        Mockito.doNothing().when(multiPointOutputStream).parkThread(25);
        multiPointOutputStream.ensureSync(false, 1);
        Mockito.verify(multiPointOutputStream).unparkThread(ArgumentMatchers.eq(runSyncThread));
        Mockito.verify(multiPointOutputStream).parkThread();
        assertThat(multiPointOutputStream.parkedRunBlockThreadMap.size()).isOne();
        assertThat(multiPointOutputStream.parkedRunBlockThreadMap.get(1)).isEqualTo(Thread.currentThread());
    }

    @Test
    public void ensureSync_loop() {
        Mockito.doNothing().when(multiPointOutputStream).unparkThread(ArgumentMatchers.nullable(Thread.class));
        Mockito.doNothing().when(multiPointOutputStream).parkThread();
        Mockito.doNothing().when(multiPointOutputStream).parkThread(25);
        multiPointOutputStream.runSyncThread = null;
        Mockito.when(multiPointOutputStream.isRunSyncThreadValid()).thenReturn(false, true);
        multiPointOutputStream.ensureSync(false, 1);
        Mockito.verify(multiPointOutputStream).parkThread(ArgumentMatchers.eq(25L));
        Mockito.verify(multiPointOutputStream).unparkThread(ArgumentMatchers.nullable(Thread.class));
    }

    @Test
    public void done_noMoreStream() throws IOException {
        Mockito.doNothing().when(multiPointOutputStream).close(1);
        Mockito.doNothing().when(multiPointOutputStream).ensureSync(true, 1);
        Mockito.doNothing().when(multiPointOutputStream).inspectStreamState(multiPointOutputStream.doneState);
        multiPointOutputStream.noSyncLengthMap.put(1, new AtomicLong(10));
        multiPointOutputStream.doneState.isNoMoreStream = true;
        multiPointOutputStream.done(1);
        assertThat(multiPointOutputStream.noMoreStreamList).containsExactly(1);
        Mockito.verify(multiPointOutputStream).ensureSync(ArgumentMatchers.eq(true), ArgumentMatchers.eq(1));
        Mockito.verify(multiPointOutputStream).close(ArgumentMatchers.eq(1));
    }

    @Test
    public void done_notNoMoreStream() throws IOException {
        Mockito.doNothing().when(multiPointOutputStream).close(1);
        Mockito.doNothing().when(multiPointOutputStream).ensureSync(false, 1);
        Mockito.doNothing().when(multiPointOutputStream).inspectStreamState(multiPointOutputStream.doneState);
        multiPointOutputStream.noSyncLengthMap.put(1, new AtomicLong(10));
        multiPointOutputStream.doneState.isNoMoreStream = false;
        multiPointOutputStream.done(1);
        assertThat(multiPointOutputStream.noMoreStreamList).containsExactly(1);
        Mockito.verify(multiPointOutputStream).ensureSync(ArgumentMatchers.eq(false), ArgumentMatchers.eq(1));
        Mockito.verify(multiPointOutputStream).close(ArgumentMatchers.eq(1));
    }

    @Test(expected = IOException.class)
    public void done_syncException() throws IOException {
        multiPointOutputStream.syncException = new IOException();
        multiPointOutputStream.done(1);
    }

    @Test
    public void done_syncNotRun() throws IOException {
        Mockito.doNothing().when(multiPointOutputStream).close(1);
        multiPointOutputStream.noSyncLengthMap.put(1, new AtomicLong(10));
        multiPointOutputStream.syncFuture = null;
        multiPointOutputStream.done(1);
        assertThat(multiPointOutputStream.noMoreStreamList).containsExactly(1);
        Mockito.verify(multiPointOutputStream, Mockito.never()).ensureSync(ArgumentMatchers.eq(false), ArgumentMatchers.eq(1));
        Mockito.verify(multiPointOutputStream).close(ArgumentMatchers.eq(1));
    }

    @Test
    public void runSyncDelayException() throws IOException {
        final IOException exception = Mockito.mock(IOException.class);
        Mockito.doThrow(exception).when(multiPointOutputStream).runSync();
        multiPointOutputStream.runSyncDelayException();
        assertThat(multiPointOutputStream.syncException).isEqualTo(exception);
    }

    @Test
    public void runSync() throws IOException {
        final MultiPointOutputStream.StreamsState state = Mockito.spy(new MultiPointOutputStream.StreamsState());
        multiPointOutputStream.state = state;
        Mockito.when(state.isStreamsEndOrChanged()).thenReturn(false, false, false, true);
        Mockito.doNothing().when(multiPointOutputStream).parkThread(ArgumentMatchers.anyLong());
        Mockito.doNothing().when(multiPointOutputStream).unparkThread(ArgumentMatchers.any(Thread.class));
        Mockito.doNothing().when(multiPointOutputStream).flushProcess();
        Mockito.doNothing().when(multiPointOutputStream).inspectStreamState(state);
        final Thread thread0 = Mockito.mock(Thread.class);
        final Thread thread1 = Mockito.mock(Thread.class);
        multiPointOutputStream.parkedRunBlockThreadMap.put(0, thread0);
        multiPointOutputStream.parkedRunBlockThreadMap.put(1, thread1);
        multiPointOutputStream.allNoSyncLength.set(1);
        state.newNoMoreStreamBlockList.add(0);
        state.newNoMoreStreamBlockList.add(1);
        state.isNoMoreStream = true;
        Mockito.when(multiPointOutputStream.isNoNeedFlushForLength()).thenReturn(true, false);
        Mockito.when(multiPointOutputStream.getNextParkMillisecond()).thenReturn(1L, (-1L));
        multiPointOutputStream.runSync();
        // first default + scheduler one + last one.
        Mockito.verify(multiPointOutputStream, Mockito.times(3)).flushProcess();
        assertThat(multiPointOutputStream.parkedRunBlockThreadMap.size()).isZero();
        Mockito.verify(multiPointOutputStream).unparkThread(ArgumentMatchers.eq(thread0));
        Mockito.verify(multiPointOutputStream).unparkThread(ArgumentMatchers.eq(thread1));
    }

    @Test
    public void isNoNeedFlushForLength() {
        multiPointOutputStream.allNoSyncLength.set(0);
        // syncBufferSize is 0.
        assertThat(multiPointOutputStream.isNoNeedFlushForLength()).isFalse();
        multiPointOutputStream.allNoSyncLength.set((-1));
        assertThat(multiPointOutputStream.isNoNeedFlushForLength()).isTrue();
    }

    @Test
    public void getNextParkMillisecond() {
        // syncBufferIntervalMills is 0.
        Mockito.when(multiPointOutputStream.now()).thenReturn(2L);
        multiPointOutputStream.lastSyncTimestamp.set(1L);
        assertThat(multiPointOutputStream.getNextParkMillisecond()).isEqualTo((-1L));
    }

    @Test
    public void flushProcess() throws IOException {
        final DownloadOutputStream outputStream = Mockito.mock(DownloadOutputStream.class);
        Mockito.doReturn(outputStream).when(multiPointOutputStream).outputStream(1);
        Mockito.when(info.getBlock(1)).thenReturn(Mockito.mock(BlockInfo.class));
        multiPointOutputStream.allNoSyncLength.addAndGet(10);
        multiPointOutputStream.noSyncLengthMap.put(1, new AtomicLong(10));
        multiPointOutputStream.outputStreamMap.put(1, Mockito.mock(DownloadOutputStream.class));
        multiPointOutputStream.flushProcess();
        Mockito.verify(store).onSyncToFilesystemSuccess(info, 1, 10);
        assertThat(multiPointOutputStream.allNoSyncLength.get()).isZero();
        assertThat(multiPointOutputStream.noSyncLengthMap.get(1).get()).isZero();
    }

    @Test
    public void inspectAndPersist() throws IOException {
        final Future newFuture = Mockito.mock(Future.class);
        Mockito.doReturn(newFuture).when(multiPointOutputStream).executeSyncRunnableAsync();
        multiPointOutputStream.syncFuture = syncFuture;
        multiPointOutputStream.inspectAndPersist();
        // not change
        assertThat(multiPointOutputStream.syncFuture).isEqualTo(syncFuture);
        multiPointOutputStream.syncFuture = null;
        multiPointOutputStream.inspectAndPersist();
        // changed
        assertThat(multiPointOutputStream.syncFuture).isEqualTo(newFuture);
    }

    @Test(expected = IOException.class)
    public void inspectAndPersist_syncException() throws IOException {
        multiPointOutputStream.syncException = new IOException();
        multiPointOutputStream.inspectAndPersist();
    }

    @Test(expected = IOException.class)
    public void inspectComplete_notFull() throws IOException {
        final BlockInfo blockInfo = Mockito.mock(BlockInfo.class);
        Mockito.when(info.getBlock(1)).thenReturn(blockInfo);
        Mockito.when(blockInfo.getContentLength()).thenReturn(9L);
        Mockito.when(blockInfo.getCurrentOffset()).thenReturn(10L);
        multiPointOutputStream.inspectComplete(1);
    }

    @Test(expected = IOException.class)
    public void inspectComplete_syncException() throws IOException {
        multiPointOutputStream.syncException = new IOException();
        multiPointOutputStream.inspectAndPersist();
    }

    @Test
    public void outputStream() throws IOException {
        prepareOutputStreamEnv();
        BlockInfo blockInfo = Mockito.mock(BlockInfo.class);
        Mockito.when(info.getBlock(0)).thenReturn(blockInfo);
        Mockito.when(blockInfo.getRangeLeft()).thenReturn(10L);
        Mockito.when(info.getTotalLength()).thenReturn(20L);
        Mockito.when(info.isChunked()).thenReturn(false);
        assertThat(multiPointOutputStream.outputStreamMap.get(0)).isNull();
        final DownloadOutputStream outputStream = multiPointOutputStream.outputStream(0);
        assertThat(outputStream).isNotNull();
        assertThat(multiPointOutputStream.outputStreamMap.get(0)).isEqualTo(outputStream);
        Mockito.verify(outputStream).seek(ArgumentMatchers.eq(10L));
        Mockito.verify(outputStream).setLength(ArgumentMatchers.eq(20L));
        final int id = task.getId();
        Mockito.verify(store).markFileDirty(id);
    }

    @Test
    public void close_noExist() throws IOException {
        final DownloadOutputStream stream0 = Mockito.mock(DownloadOutputStream.class);
        final DownloadOutputStream stream1 = Mockito.mock(DownloadOutputStream.class);
        multiPointOutputStream.outputStreamMap.put(0, stream0);
        multiPointOutputStream.outputStreamMap.put(1, stream1);
        multiPointOutputStream.close(2);
        Mockito.verify(stream0, Mockito.never()).close();
        Mockito.verify(stream1, Mockito.never()).close();
        assertThat(multiPointOutputStream.outputStreamMap.size()).isEqualTo(2);
    }

    @Test
    public void close() throws IOException {
        final DownloadOutputStream stream0 = Mockito.mock(DownloadOutputStream.class);
        final DownloadOutputStream stream1 = Mockito.mock(DownloadOutputStream.class);
        multiPointOutputStream.outputStreamMap.put(0, stream0);
        multiPointOutputStream.outputStreamMap.put(1, stream1);
        multiPointOutputStream.close(1);
        Mockito.verify(stream0, Mockito.never()).close();
        Mockito.verify(stream1).close();
        assertThat(multiPointOutputStream.outputStreamMap.size()).isEqualTo(1);
        assertThat(multiPointOutputStream.outputStreamMap.get(0)).isEqualTo(stream0);
    }

    @Test
    public void inspectStreamState() {
        final MultiPointOutputStream.StreamsState state = new MultiPointOutputStream.StreamsState();
        multiPointOutputStream.outputStreamMap.put(0, stream0);
        multiPointOutputStream.outputStreamMap.put(0, stream0);
        multiPointOutputStream.outputStreamMap.put(1, stream0);
        multiPointOutputStream.outputStreamMap.put(1, stream0);
        multiPointOutputStream.requireStreamBlocks = new ArrayList<Integer>() {
            {
                add(0);
                add(1);
            }
        };
        // no noMoreStreamList
        multiPointOutputStream.inspectStreamState(state);
        assertThat(state.isNoMoreStream).isFalse();
        assertThat(state.noMoreStreamBlockList).isEmpty();
        assertThat(state.newNoMoreStreamBlockList).isEmpty();
        // 1 noMoreStreamList
        multiPointOutputStream.noMoreStreamList.add(1);
        multiPointOutputStream.inspectStreamState(state);
        assertThat(state.isNoMoreStream).isFalse();
        assertThat(state.noMoreStreamBlockList).containsExactly(1);
        assertThat(state.newNoMoreStreamBlockList).containsExactly(1);
        // 1,0 noMoreStreamList
        multiPointOutputStream.noMoreStreamList.add(0);
        multiPointOutputStream.inspectStreamState(state);
        assertThat(state.isNoMoreStream).isTrue();
        assertThat(state.noMoreStreamBlockList).containsExactly(1, 0);
        assertThat(state.newNoMoreStreamBlockList).containsExactly(0);
        // 1,0 noMoreStreamList again
        multiPointOutputStream.inspectStreamState(state);
        assertThat(state.isNoMoreStream).isTrue();
        assertThat(state.noMoreStreamBlockList).containsExactly(1, 0);
        assertThat(state.newNoMoreStreamBlockList).isEmpty();
    }

    @Test
    public void setRequireStreamBlocks() {
        assertThat(multiPointOutputStream.requireStreamBlocks).isEqualTo(null);
        final List<Integer> requireStreamBlocks = new ArrayList<Integer>() {
            {
                add(0);
                add(1);
                add(2);
            }
        };
        multiPointOutputStream.setRequireStreamBlocks(requireStreamBlocks);
        assertThat(multiPointOutputStream.requireStreamBlocks).isEqualTo(requireStreamBlocks);
    }

    @Test
    public void outputStream_contain_returnDirectly() throws IOException {
        final DownloadOutputStream outputStream = Mockito.mock(DownloadOutputStream.class);
        multiPointOutputStream.outputStreamMap.put(1, outputStream);
        assertThat(multiPointOutputStream.outputStream(1)).isEqualTo(outputStream);
    }

    @Test
    public void outputStream_rangeLeft0_noSeek() throws IOException {
        prepareOutputStreamEnv();
        BlockInfo blockInfo = Mockito.mock(BlockInfo.class);
        Mockito.when(info.getBlock(0)).thenReturn(blockInfo);
        Mockito.when(blockInfo.getRangeLeft()).thenReturn(0L);
        Mockito.when(info.getTotalLength()).thenReturn(20L);
        Mockito.when(info.isChunked()).thenReturn(false);
        final DownloadOutputStream outputStream = multiPointOutputStream.outputStream(0);
        Mockito.verify(outputStream, Mockito.never()).seek(ArgumentMatchers.anyLong());
        Mockito.verify(outputStream).setLength(ArgumentMatchers.eq(20L));
    }

    @Test
    public void outputStream_chunked_noPreAllocate() throws IOException {
        prepareOutputStreamEnv();
        BlockInfo blockInfo = Mockito.mock(BlockInfo.class);
        Mockito.when(info.getBlock(0)).thenReturn(blockInfo);
        Mockito.when(blockInfo.getRangeLeft()).thenReturn(0L);
        Mockito.when(info.isChunked()).thenReturn(true);
        final DownloadOutputStream outputStream = multiPointOutputStream.outputStream(0);
        Mockito.verify(outputStream, Mockito.never()).seek(ArgumentMatchers.anyLong());
        Mockito.verify(outputStream, Mockito.never()).setLength(ArgumentMatchers.anyLong());
    }

    @Test
    public void outputStream_nonFileScheme() throws IOException {
        prepareOutputStreamEnv();
        final Uri uri = task.getUri();
        Mockito.when(uri.getScheme()).thenReturn("content");
        BlockInfo blockInfo = Mockito.mock(BlockInfo.class);
        Mockito.when(info.getBlock(0)).thenReturn(blockInfo);
        Mockito.when(blockInfo.getRangeLeft()).thenReturn(0L);
        Mockito.when(info.getTotalLength()).thenReturn(20L);
        Mockito.when(info.isChunked()).thenReturn(false);
        final DownloadOutputStream outputStream = multiPointOutputStream.outputStream(0);
        Mockito.verify(outputStream, Mockito.never()).seek(ArgumentMatchers.anyLong());
        Mockito.verify(outputStream).setLength(ArgumentMatchers.eq(20L));
        Mockito.verify(multiPointOutputStream, Mockito.never()).inspectFreeSpace(ArgumentMatchers.any(StatFs.class), ArgumentMatchers.anyLong());
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void inspectFreeSpace_freeSpaceNotEnough() throws PreAllocateException {
        final StatFs statFs = Mockito.mock(StatFs.class);
        Mockito.when(statFs.getAvailableBlocks()).thenReturn(1);
        Mockito.when(statFs.getBlockSize()).thenReturn(2);
        thrown.expectMessage("There is Free space less than Require space: 2 < 3");
        thrown.expect(PreAllocateException.class);
        multiPointOutputStream.inspectFreeSpace(statFs, 3);
    }

    @Test
    public void inspectFreeSpace() throws PreAllocateException {
        final StatFs statFs = Mockito.mock(StatFs.class);
        Mockito.when(statFs.getAvailableBlocks()).thenReturn(1);
        Mockito.when(statFs.getBlockSize()).thenReturn(2);
        multiPointOutputStream.inspectFreeSpace(statFs, 2);
    }

    @Test
    public void catchBlockConnectException() {
        multiPointOutputStream.catchBlockConnectException(2);
        assertThat(multiPointOutputStream.noMoreStreamList).hasSize(1);
        assertThat(multiPointOutputStream.noMoreStreamList).containsExactly(2);
    }
}

