/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.mapreduce.task.reduce;


import Counters.Counter;
import Fetcher.TOO_MANY_REQ_STATUS_CODE;
import Reporter.NULL;
import SecureShuffleUtils.HTTP_HEADER_REPLY_URL_HASH;
import SecureShuffleUtils.HTTP_HEADER_URL_HASH;
import ShuffleHeader.DEFAULT_HTTP_HEADER_NAME;
import ShuffleHeader.DEFAULT_HTTP_HEADER_VERSION;
import ShuffleHeader.HTTP_HEADER_NAME;
import ShuffleHeader.HTTP_HEADER_VERSION;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import javax.crypto.SecretKey;
import org.apache.hadoop.fs.ChecksumException;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.IFileInputStream;
import org.apache.hadoop.mapred.IFileOutputStream;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.mapreduce.security.SecureShuffleUtils;
import org.apache.hadoop.util.DiskChecker.DiskErrorException;
import org.apache.hadoop.util.Time;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test that the Fetcher does what we expect it to.
 */
public class TestFetcher {
    private static final Logger LOG = LoggerFactory.getLogger(TestFetcher.class);

    JobConf job = null;

    JobConf jobWithRetry = null;

    TaskAttemptID id = null;

    ShuffleSchedulerImpl<Text, Text> ss = null;

    MergeManagerImpl<Text, Text> mm = null;

    Reporter r = null;

    ShuffleClientMetrics metrics = null;

    ExceptionReporter except = null;

    SecretKey key = null;

    HttpURLConnection connection = null;

    Counter allErrs = null;

    final String encHash = "vFE234EIFCiBgYs2tCXY/SjT8Kg=";

    final MapHost host = new MapHost("localhost", "http://localhost:8080/");

    final TaskAttemptID map1ID = TaskAttemptID.forName("attempt_0_1_m_1_1");

    final TaskAttemptID map2ID = TaskAttemptID.forName("attempt_0_1_m_2_1");

    FileSystem fs = null;

    @Rule
    public TestName name = new TestName();

    @Test
    public void testReduceOutOfDiskSpace() throws Throwable {
        TestFetcher.LOG.info("testReduceOutOfDiskSpace");
        Fetcher<Text, Text> underTest = new TestFetcher.FakeFetcher<Text, Text>(job, id, ss, mm, r, metrics, except, key, connection);
        String replyHash = SecureShuffleUtils.generateHash(encHash.getBytes(), key);
        ShuffleHeader header = new ShuffleHeader(map1ID.toString(), 10, 10, 1);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        header.write(new DataOutputStream(bout));
        ByteArrayInputStream in = new ByteArrayInputStream(bout.toByteArray());
        Mockito.when(connection.getResponseCode()).thenReturn(200);
        Mockito.when(connection.getHeaderField(HTTP_HEADER_NAME)).thenReturn(DEFAULT_HTTP_HEADER_NAME);
        Mockito.when(connection.getHeaderField(HTTP_HEADER_VERSION)).thenReturn(DEFAULT_HTTP_HEADER_VERSION);
        Mockito.when(connection.getHeaderField(HTTP_HEADER_REPLY_URL_HASH)).thenReturn(replyHash);
        Mockito.when(connection.getInputStream()).thenReturn(in);
        Mockito.when(mm.reserve(ArgumentMatchers.any(TaskAttemptID.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt())).thenThrow(new DiskErrorException("No disk space available"));
        underTest.copyFromHost(host);
        Mockito.verify(ss).reportLocalError(ArgumentMatchers.any(IOException.class));
    }

    @Test(timeout = 30000)
    public void testCopyFromHostConnectionTimeout() throws Exception {
        Mockito.when(connection.getInputStream()).thenThrow(new SocketTimeoutException("This is a fake timeout :)"));
        Fetcher<Text, Text> underTest = new TestFetcher.FakeFetcher<Text, Text>(job, id, ss, mm, r, metrics, except, key, connection);
        underTest.copyFromHost(host);
        Mockito.verify(connection).addRequestProperty(HTTP_HEADER_URL_HASH, encHash);
        Mockito.verify(allErrs).increment(1);
        Mockito.verify(ss).copyFailed(map1ID, host, false, false);
        Mockito.verify(ss).copyFailed(map2ID, host, false, false);
        Mockito.verify(ss).putBackKnownMapOutput(ArgumentMatchers.any(MapHost.class), ArgumentMatchers.eq(map1ID));
        Mockito.verify(ss).putBackKnownMapOutput(ArgumentMatchers.any(MapHost.class), ArgumentMatchers.eq(map2ID));
    }

    @Test
    public void testCopyFromHostConnectionRejected() throws Exception {
        Mockito.when(connection.getResponseCode()).thenReturn(TOO_MANY_REQ_STATUS_CODE);
        Fetcher<Text, Text> fetcher = new TestFetcher.FakeFetcher(job, id, ss, mm, r, metrics, except, key, connection);
        fetcher.copyFromHost(host);
        Assert.assertEquals("No host failure is expected.", ss.hostFailureCount(host.getHostName()), 0);
        Assert.assertEquals("No fetch failure is expected.", ss.fetchFailureCount(map1ID), 0);
        Assert.assertEquals("No fetch failure is expected.", ss.fetchFailureCount(map2ID), 0);
        Mockito.verify(ss).penalize(ArgumentMatchers.eq(host), ArgumentMatchers.anyLong());
        Mockito.verify(ss).putBackKnownMapOutput(ArgumentMatchers.any(MapHost.class), ArgumentMatchers.eq(map1ID));
        Mockito.verify(ss).putBackKnownMapOutput(ArgumentMatchers.any(MapHost.class), ArgumentMatchers.eq(map2ID));
    }

    @Test
    public void testCopyFromHostBogusHeader() throws Exception {
        Fetcher<Text, Text> underTest = new TestFetcher.FakeFetcher<Text, Text>(job, id, ss, mm, r, metrics, except, key, connection);
        String replyHash = SecureShuffleUtils.generateHash(encHash.getBytes(), key);
        Mockito.when(connection.getResponseCode()).thenReturn(200);
        Mockito.when(connection.getHeaderField(HTTP_HEADER_NAME)).thenReturn(DEFAULT_HTTP_HEADER_NAME);
        Mockito.when(connection.getHeaderField(HTTP_HEADER_VERSION)).thenReturn(DEFAULT_HTTP_HEADER_VERSION);
        Mockito.when(connection.getHeaderField(HTTP_HEADER_REPLY_URL_HASH)).thenReturn(replyHash);
        ByteArrayInputStream in = new ByteArrayInputStream("\u00010 BOGUS DATA\nBOGUS DATA\nBOGUS DATA\n".getBytes());
        Mockito.when(connection.getInputStream()).thenReturn(in);
        underTest.copyFromHost(host);
        Mockito.verify(connection).addRequestProperty(HTTP_HEADER_URL_HASH, encHash);
        Mockito.verify(allErrs).increment(1);
        Mockito.verify(ss).copyFailed(map1ID, host, true, false);
        Mockito.verify(ss).copyFailed(map2ID, host, true, false);
        Mockito.verify(ss).putBackKnownMapOutput(ArgumentMatchers.any(MapHost.class), ArgumentMatchers.eq(map1ID));
        Mockito.verify(ss).putBackKnownMapOutput(ArgumentMatchers.any(MapHost.class), ArgumentMatchers.eq(map2ID));
    }

    @Test
    public void testCopyFromHostIncompatibleShuffleVersion() throws Exception {
        String replyHash = SecureShuffleUtils.generateHash(encHash.getBytes(), key);
        Mockito.when(connection.getResponseCode()).thenReturn(200);
        Mockito.when(connection.getHeaderField(HTTP_HEADER_NAME)).thenReturn("mapreduce").thenReturn("other").thenReturn("other");
        Mockito.when(connection.getHeaderField(HTTP_HEADER_VERSION)).thenReturn("1.0.1").thenReturn("1.0.0").thenReturn("1.0.1");
        Mockito.when(connection.getHeaderField(HTTP_HEADER_REPLY_URL_HASH)).thenReturn(replyHash);
        ByteArrayInputStream in = new ByteArrayInputStream(new byte[0]);
        Mockito.when(connection.getInputStream()).thenReturn(in);
        for (int i = 0; i < 3; ++i) {
            Fetcher<Text, Text> underTest = new TestFetcher.FakeFetcher<Text, Text>(job, id, ss, mm, r, metrics, except, key, connection);
            underTest.copyFromHost(host);
        }
        Mockito.verify(connection, Mockito.times(3)).addRequestProperty(HTTP_HEADER_URL_HASH, encHash);
        Mockito.verify(allErrs, Mockito.times(3)).increment(1);
        Mockito.verify(ss, Mockito.times(3)).copyFailed(map1ID, host, false, false);
        Mockito.verify(ss, Mockito.times(3)).copyFailed(map2ID, host, false, false);
        Mockito.verify(ss, Mockito.times(3)).putBackKnownMapOutput(ArgumentMatchers.any(MapHost.class), ArgumentMatchers.eq(map1ID));
        Mockito.verify(ss, Mockito.times(3)).putBackKnownMapOutput(ArgumentMatchers.any(MapHost.class), ArgumentMatchers.eq(map2ID));
    }

    @Test
    public void testCopyFromHostIncompatibleShuffleVersionWithRetry() throws Exception {
        String replyHash = SecureShuffleUtils.generateHash(encHash.getBytes(), key);
        Mockito.when(connection.getResponseCode()).thenReturn(200);
        Mockito.when(connection.getHeaderField(HTTP_HEADER_NAME)).thenReturn("mapreduce").thenReturn("other").thenReturn("other");
        Mockito.when(connection.getHeaderField(HTTP_HEADER_VERSION)).thenReturn("1.0.1").thenReturn("1.0.0").thenReturn("1.0.1");
        Mockito.when(connection.getHeaderField(HTTP_HEADER_REPLY_URL_HASH)).thenReturn(replyHash);
        ByteArrayInputStream in = new ByteArrayInputStream(new byte[0]);
        Mockito.when(connection.getInputStream()).thenReturn(in);
        for (int i = 0; i < 3; ++i) {
            Fetcher<Text, Text> underTest = new TestFetcher.FakeFetcher<Text, Text>(jobWithRetry, id, ss, mm, r, metrics, except, key, connection);
            underTest.copyFromHost(host);
        }
        Mockito.verify(connection, Mockito.times(3)).addRequestProperty(HTTP_HEADER_URL_HASH, encHash);
        Mockito.verify(allErrs, Mockito.times(3)).increment(1);
        Mockito.verify(ss, Mockito.times(3)).copyFailed(map1ID, host, false, false);
        Mockito.verify(ss, Mockito.times(3)).copyFailed(map2ID, host, false, false);
        Mockito.verify(ss, Mockito.times(3)).putBackKnownMapOutput(ArgumentMatchers.any(MapHost.class), ArgumentMatchers.eq(map1ID));
        Mockito.verify(ss, Mockito.times(3)).putBackKnownMapOutput(ArgumentMatchers.any(MapHost.class), ArgumentMatchers.eq(map2ID));
    }

    @Test
    public void testCopyFromHostWait() throws Exception {
        Fetcher<Text, Text> underTest = new TestFetcher.FakeFetcher<Text, Text>(job, id, ss, mm, r, metrics, except, key, connection);
        String replyHash = SecureShuffleUtils.generateHash(encHash.getBytes(), key);
        Mockito.when(connection.getResponseCode()).thenReturn(200);
        Mockito.when(connection.getHeaderField(HTTP_HEADER_REPLY_URL_HASH)).thenReturn(replyHash);
        ShuffleHeader header = new ShuffleHeader(map1ID.toString(), 10, 10, 1);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        header.write(new DataOutputStream(bout));
        ByteArrayInputStream in = new ByteArrayInputStream(bout.toByteArray());
        Mockito.when(connection.getInputStream()).thenReturn(in);
        Mockito.when(connection.getHeaderField(HTTP_HEADER_NAME)).thenReturn(DEFAULT_HTTP_HEADER_NAME);
        Mockito.when(connection.getHeaderField(HTTP_HEADER_VERSION)).thenReturn(DEFAULT_HTTP_HEADER_VERSION);
        // Defaults to null, which is what we want to test
        Mockito.when(mm.reserve(ArgumentMatchers.any(TaskAttemptID.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt())).thenReturn(null);
        underTest.copyFromHost(host);
        Mockito.verify(connection).addRequestProperty(HTTP_HEADER_URL_HASH, encHash);
        Mockito.verify(allErrs, Mockito.never()).increment(1);
        Mockito.verify(ss, Mockito.never()).copyFailed(map1ID, host, true, false);
        Mockito.verify(ss, Mockito.never()).copyFailed(map2ID, host, true, false);
        Mockito.verify(ss).putBackKnownMapOutput(ArgumentMatchers.any(MapHost.class), ArgumentMatchers.eq(map1ID));
        Mockito.verify(ss).putBackKnownMapOutput(ArgumentMatchers.any(MapHost.class), ArgumentMatchers.eq(map2ID));
    }

    @SuppressWarnings("unchecked")
    @Test(timeout = 10000)
    public void testCopyFromHostCompressFailure() throws Exception {
        InMemoryMapOutput<Text, Text> immo = Mockito.mock(InMemoryMapOutput.class);
        Fetcher<Text, Text> underTest = new TestFetcher.FakeFetcher<Text, Text>(job, id, ss, mm, r, metrics, except, key, connection);
        String replyHash = SecureShuffleUtils.generateHash(encHash.getBytes(), key);
        Mockito.when(connection.getResponseCode()).thenReturn(200);
        Mockito.when(connection.getHeaderField(HTTP_HEADER_REPLY_URL_HASH)).thenReturn(replyHash);
        ShuffleHeader header = new ShuffleHeader(map1ID.toString(), 10, 10, 1);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        header.write(new DataOutputStream(bout));
        ByteArrayInputStream in = new ByteArrayInputStream(bout.toByteArray());
        Mockito.when(connection.getInputStream()).thenReturn(in);
        Mockito.when(connection.getHeaderField(HTTP_HEADER_NAME)).thenReturn(DEFAULT_HTTP_HEADER_NAME);
        Mockito.when(connection.getHeaderField(HTTP_HEADER_VERSION)).thenReturn(DEFAULT_HTTP_HEADER_VERSION);
        Mockito.when(mm.reserve(ArgumentMatchers.any(TaskAttemptID.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt())).thenReturn(immo);
        Mockito.doThrow(new InternalError()).when(immo).shuffle(ArgumentMatchers.any(MapHost.class), ArgumentMatchers.any(InputStream.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ShuffleClientMetrics.class), ArgumentMatchers.any(Reporter.class));
        underTest.copyFromHost(host);
        Mockito.verify(connection).addRequestProperty(HTTP_HEADER_URL_HASH, encHash);
        Mockito.verify(ss, Mockito.times(1)).copyFailed(map1ID, host, true, false);
    }

    @SuppressWarnings("unchecked")
    @Test(timeout = 10000)
    public void testCopyFromHostOnAnyException() throws Exception {
        InMemoryMapOutput<Text, Text> immo = Mockito.mock(InMemoryMapOutput.class);
        Fetcher<Text, Text> underTest = new TestFetcher.FakeFetcher<Text, Text>(job, id, ss, mm, r, metrics, except, key, connection);
        String replyHash = SecureShuffleUtils.generateHash(encHash.getBytes(), key);
        Mockito.when(connection.getResponseCode()).thenReturn(200);
        Mockito.when(connection.getHeaderField(HTTP_HEADER_REPLY_URL_HASH)).thenReturn(replyHash);
        ShuffleHeader header = new ShuffleHeader(map1ID.toString(), 10, 10, 1);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        header.write(new DataOutputStream(bout));
        ByteArrayInputStream in = new ByteArrayInputStream(bout.toByteArray());
        Mockito.when(connection.getInputStream()).thenReturn(in);
        Mockito.when(connection.getHeaderField(HTTP_HEADER_NAME)).thenReturn(DEFAULT_HTTP_HEADER_NAME);
        Mockito.when(connection.getHeaderField(HTTP_HEADER_VERSION)).thenReturn(DEFAULT_HTTP_HEADER_VERSION);
        Mockito.when(mm.reserve(ArgumentMatchers.any(TaskAttemptID.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt())).thenReturn(immo);
        Mockito.doThrow(new ArrayIndexOutOfBoundsException()).when(immo).shuffle(ArgumentMatchers.any(MapHost.class), ArgumentMatchers.any(InputStream.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ShuffleClientMetrics.class), ArgumentMatchers.any(Reporter.class));
        underTest.copyFromHost(host);
        Mockito.verify(connection).addRequestProperty(HTTP_HEADER_URL_HASH, encHash);
        Mockito.verify(ss, Mockito.times(1)).copyFailed(map1ID, host, true, false);
    }

    @SuppressWarnings("unchecked")
    @Test(timeout = 10000)
    public void testCopyFromHostWithRetry() throws Exception {
        InMemoryMapOutput<Text, Text> immo = Mockito.mock(InMemoryMapOutput.class);
        ss = Mockito.mock(ShuffleSchedulerImpl.class);
        Fetcher<Text, Text> underTest = new TestFetcher.FakeFetcher<Text, Text>(jobWithRetry, id, ss, mm, r, metrics, except, key, connection, true);
        String replyHash = SecureShuffleUtils.generateHash(encHash.getBytes(), key);
        Mockito.when(connection.getResponseCode()).thenReturn(200);
        Mockito.when(connection.getHeaderField(HTTP_HEADER_REPLY_URL_HASH)).thenReturn(replyHash);
        ShuffleHeader header = new ShuffleHeader(map1ID.toString(), 10, 10, 1);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        header.write(new DataOutputStream(bout));
        ByteArrayInputStream in = new ByteArrayInputStream(bout.toByteArray());
        Mockito.when(connection.getInputStream()).thenReturn(in);
        Mockito.when(connection.getHeaderField(HTTP_HEADER_NAME)).thenReturn(DEFAULT_HTTP_HEADER_NAME);
        Mockito.when(connection.getHeaderField(HTTP_HEADER_VERSION)).thenReturn(DEFAULT_HTTP_HEADER_VERSION);
        Mockito.when(mm.reserve(ArgumentMatchers.any(TaskAttemptID.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt())).thenReturn(immo);
        final long retryTime = Time.monotonicNow();
        Mockito.doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock ignore) throws IOException {
                // Emulate host down for 3 seconds.
                if (((Time.monotonicNow()) - retryTime) <= 3000) {
                    throw new InternalError();
                }
                return null;
            }
        }).when(immo).shuffle(ArgumentMatchers.any(MapHost.class), ArgumentMatchers.any(InputStream.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ShuffleClientMetrics.class), ArgumentMatchers.any(Reporter.class));
        underTest.copyFromHost(host);
        Mockito.verify(ss, Mockito.never()).copyFailed(ArgumentMatchers.any(TaskAttemptID.class), ArgumentMatchers.any(MapHost.class), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
    }

    @SuppressWarnings("unchecked")
    @Test(timeout = 10000)
    public void testCopyFromHostWithRetryThenTimeout() throws Exception {
        InMemoryMapOutput<Text, Text> immo = Mockito.mock(InMemoryMapOutput.class);
        Fetcher<Text, Text> underTest = new TestFetcher.FakeFetcher<Text, Text>(jobWithRetry, id, ss, mm, r, metrics, except, key, connection);
        String replyHash = SecureShuffleUtils.generateHash(encHash.getBytes(), key);
        Mockito.when(connection.getResponseCode()).thenReturn(200).thenThrow(new SocketTimeoutException("forced timeout"));
        Mockito.when(connection.getHeaderField(HTTP_HEADER_REPLY_URL_HASH)).thenReturn(replyHash);
        ShuffleHeader header = new ShuffleHeader(map1ID.toString(), 10, 10, 1);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        header.write(new DataOutputStream(bout));
        ByteArrayInputStream in = new ByteArrayInputStream(bout.toByteArray());
        Mockito.when(connection.getInputStream()).thenReturn(in);
        Mockito.when(connection.getHeaderField(HTTP_HEADER_NAME)).thenReturn(DEFAULT_HTTP_HEADER_NAME);
        Mockito.when(connection.getHeaderField(HTTP_HEADER_VERSION)).thenReturn(DEFAULT_HTTP_HEADER_VERSION);
        Mockito.when(mm.reserve(ArgumentMatchers.any(TaskAttemptID.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt())).thenReturn(immo);
        Mockito.doThrow(new IOException("forced error")).when(immo).shuffle(ArgumentMatchers.any(MapHost.class), ArgumentMatchers.any(InputStream.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ShuffleClientMetrics.class), ArgumentMatchers.any(Reporter.class));
        underTest.copyFromHost(host);
        Mockito.verify(allErrs).increment(1);
        Mockito.verify(ss, Mockito.times(1)).copyFailed(map1ID, host, false, false);
        Mockito.verify(ss, Mockito.times(1)).copyFailed(map2ID, host, false, false);
        Mockito.verify(ss, Mockito.times(1)).putBackKnownMapOutput(ArgumentMatchers.any(MapHost.class), ArgumentMatchers.eq(map1ID));
        Mockito.verify(ss, Mockito.times(1)).putBackKnownMapOutput(ArgumentMatchers.any(MapHost.class), ArgumentMatchers.eq(map2ID));
    }

    @Test
    public void testCopyFromHostExtraBytes() throws Exception {
        Fetcher<Text, Text> underTest = new TestFetcher.FakeFetcher<Text, Text>(job, id, ss, mm, r, metrics, except, key, connection);
        String replyHash = SecureShuffleUtils.generateHash(encHash.getBytes(), key);
        Mockito.when(connection.getResponseCode()).thenReturn(200);
        Mockito.when(connection.getHeaderField(HTTP_HEADER_NAME)).thenReturn(DEFAULT_HTTP_HEADER_NAME);
        Mockito.when(connection.getHeaderField(HTTP_HEADER_VERSION)).thenReturn(DEFAULT_HTTP_HEADER_VERSION);
        Mockito.when(connection.getHeaderField(HTTP_HEADER_REPLY_URL_HASH)).thenReturn(replyHash);
        ShuffleHeader header = new ShuffleHeader(map1ID.toString(), 14, 10, 1);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bout);
        IFileOutputStream ios = new IFileOutputStream(dos);
        header.write(dos);
        ios.write("MAPDATA123".getBytes());
        ios.finish();
        ShuffleHeader header2 = new ShuffleHeader(map2ID.toString(), 14, 10, 1);
        IFileOutputStream ios2 = new IFileOutputStream(dos);
        header2.write(dos);
        ios2.write("MAPDATA456".getBytes());
        ios2.finish();
        ByteArrayInputStream in = new ByteArrayInputStream(bout.toByteArray());
        Mockito.when(connection.getInputStream()).thenReturn(in);
        // 8 < 10 therefore there appear to be extra bytes in the IFileInputStream
        IFileWrappedMapOutput<Text, Text> mapOut = new InMemoryMapOutput<Text, Text>(job, map1ID, mm, 8, null, true);
        IFileWrappedMapOutput<Text, Text> mapOut2 = new InMemoryMapOutput<Text, Text>(job, map2ID, mm, 10, null, true);
        Mockito.when(mm.reserve(ArgumentMatchers.eq(map1ID), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt())).thenReturn(mapOut);
        Mockito.when(mm.reserve(ArgumentMatchers.eq(map2ID), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt())).thenReturn(mapOut2);
        underTest.copyFromHost(host);
        Mockito.verify(allErrs).increment(1);
        Mockito.verify(ss).copyFailed(map1ID, host, true, false);
        Mockito.verify(ss, Mockito.never()).copyFailed(map2ID, host, true, false);
        Mockito.verify(ss).putBackKnownMapOutput(ArgumentMatchers.any(MapHost.class), ArgumentMatchers.eq(map1ID));
        Mockito.verify(ss).putBackKnownMapOutput(ArgumentMatchers.any(MapHost.class), ArgumentMatchers.eq(map2ID));
    }

    @Test
    public void testCorruptedIFile() throws Exception {
        final int fetcher = 7;
        Path onDiskMapOutputPath = new Path(((name.getMethodName()) + "/foo"));
        Path shuffledToDisk = OnDiskMapOutput.getTempPath(onDiskMapOutputPath, fetcher);
        fs = FileSystem.getLocal(job).getRaw();
        IFileWrappedMapOutput<Text, Text> odmo = new OnDiskMapOutput<Text, Text>(map1ID, mm, 100L, job, fetcher, true, fs, onDiskMapOutputPath);
        String mapData = "MAPDATA12345678901234567890";
        ShuffleHeader header = new ShuffleHeader(map1ID.toString(), 14, 10, 1);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bout);
        IFileOutputStream ios = new IFileOutputStream(dos);
        header.write(dos);
        int headerSize = dos.size();
        try {
            ios.write(mapData.getBytes());
        } finally {
            ios.close();
        }
        int dataSize = (bout.size()) - headerSize;
        // Ensure that the OnDiskMapOutput shuffler can successfully read the data.
        MapHost host = new MapHost("TestHost", "http://test/url");
        ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
        try {
            // Read past the shuffle header.
            bin.read(new byte[headerSize], 0, headerSize);
            odmo.shuffle(host, bin, dataSize, dataSize, metrics, NULL);
        } finally {
            bin.close();
        }
        // Now corrupt the IFile data.
        byte[] corrupted = bout.toByteArray();
        corrupted[(headerSize + (dataSize / 2))] = 0;
        try {
            bin = new ByteArrayInputStream(corrupted);
            // Read past the shuffle header.
            bin.read(new byte[headerSize], 0, headerSize);
            odmo.shuffle(host, bin, dataSize, dataSize, metrics, NULL);
            Assert.fail("OnDiskMapOutput.shuffle didn't detect the corrupted map partition file");
        } catch (ChecksumException e) {
            TestFetcher.LOG.info("The expected checksum exception was thrown.", e);
        } finally {
            bin.close();
        }
        // Ensure that the shuffled file can be read.
        IFileInputStream iFin = new IFileInputStream(fs.open(shuffledToDisk), dataSize, job);
        try {
            iFin.read(new byte[dataSize], 0, dataSize);
        } finally {
            iFin.close();
        }
    }

    @Test(timeout = 10000)
    public void testInterruptInMemory() throws Exception {
        final int FETCHER = 2;
        IFileWrappedMapOutput<Text, Text> immo = Mockito.spy(new InMemoryMapOutput<Text, Text>(job, id, mm, 100, null, true));
        Mockito.when(mm.reserve(ArgumentMatchers.any(TaskAttemptID.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt())).thenReturn(immo);
        Mockito.doNothing().when(mm).waitForResource();
        Mockito.when(ss.getHost()).thenReturn(host);
        String replyHash = SecureShuffleUtils.generateHash(encHash.getBytes(), key);
        Mockito.when(connection.getResponseCode()).thenReturn(200);
        Mockito.when(connection.getHeaderField(HTTP_HEADER_NAME)).thenReturn(DEFAULT_HTTP_HEADER_NAME);
        Mockito.when(connection.getHeaderField(HTTP_HEADER_VERSION)).thenReturn(DEFAULT_HTTP_HEADER_VERSION);
        Mockito.when(connection.getHeaderField(HTTP_HEADER_REPLY_URL_HASH)).thenReturn(replyHash);
        ShuffleHeader header = new ShuffleHeader(map1ID.toString(), 10, 10, 1);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        header.write(new DataOutputStream(bout));
        final TestFetcher.StuckInputStream in = new TestFetcher.StuckInputStream(new ByteArrayInputStream(bout.toByteArray()));
        Mockito.when(connection.getInputStream()).thenReturn(in);
        Mockito.doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock ignore) throws IOException {
                in.close();
                return null;
            }
        }).when(connection).disconnect();
        Fetcher<Text, Text> underTest = new TestFetcher.FakeFetcher<Text, Text>(job, id, ss, mm, r, metrics, except, key, connection, FETCHER);
        underTest.start();
        // wait for read in inputstream
        in.waitForFetcher();
        underTest.shutDown();
        underTest.join();// rely on test timeout to kill if stuck

        Assert.assertTrue(in.wasClosedProperly());
        Mockito.verify(immo).abort();
    }

    @Test(timeout = 10000)
    public void testInterruptOnDisk() throws Exception {
        final int FETCHER = 7;
        Path p = new Path("file:///tmp/foo");
        Path pTmp = OnDiskMapOutput.getTempPath(p, FETCHER);
        FileSystem mFs = Mockito.mock(FileSystem.class, Mockito.RETURNS_DEEP_STUBS);
        IFileWrappedMapOutput<Text, Text> odmo = Mockito.spy(new OnDiskMapOutput<Text, Text>(map1ID, mm, 100L, job, FETCHER, true, mFs, p));
        Mockito.when(mm.reserve(ArgumentMatchers.any(TaskAttemptID.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt())).thenReturn(odmo);
        Mockito.doNothing().when(mm).waitForResource();
        Mockito.when(ss.getHost()).thenReturn(host);
        String replyHash = SecureShuffleUtils.generateHash(encHash.getBytes(), key);
        Mockito.when(connection.getResponseCode()).thenReturn(200);
        Mockito.when(connection.getHeaderField(HTTP_HEADER_REPLY_URL_HASH)).thenReturn(replyHash);
        ShuffleHeader header = new ShuffleHeader(map1ID.toString(), 10, 10, 1);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        header.write(new DataOutputStream(bout));
        final TestFetcher.StuckInputStream in = new TestFetcher.StuckInputStream(new ByteArrayInputStream(bout.toByteArray()));
        Mockito.when(connection.getInputStream()).thenReturn(in);
        Mockito.when(connection.getHeaderField(HTTP_HEADER_NAME)).thenReturn(DEFAULT_HTTP_HEADER_NAME);
        Mockito.when(connection.getHeaderField(HTTP_HEADER_VERSION)).thenReturn(DEFAULT_HTTP_HEADER_VERSION);
        Mockito.doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock ignore) throws IOException {
                in.close();
                return null;
            }
        }).when(connection).disconnect();
        Fetcher<Text, Text> underTest = new TestFetcher.FakeFetcher<Text, Text>(job, id, ss, mm, r, metrics, except, key, connection, FETCHER);
        underTest.start();
        // wait for read in inputstream
        in.waitForFetcher();
        underTest.shutDown();
        underTest.join();// rely on test timeout to kill if stuck

        Assert.assertTrue(in.wasClosedProperly());
        Mockito.verify(mFs).create(ArgumentMatchers.eq(pTmp));
        Mockito.verify(mFs).delete(ArgumentMatchers.eq(pTmp), ArgumentMatchers.eq(false));
        Mockito.verify(odmo).abort();
    }

    @SuppressWarnings("unchecked")
    @Test(timeout = 10000)
    public void testCopyFromHostWithRetryUnreserve() throws Exception {
        InMemoryMapOutput<Text, Text> immo = Mockito.mock(InMemoryMapOutput.class);
        Fetcher<Text, Text> underTest = new TestFetcher.FakeFetcher<Text, Text>(jobWithRetry, id, ss, mm, r, metrics, except, key, connection);
        String replyHash = SecureShuffleUtils.generateHash(encHash.getBytes(), key);
        Mockito.when(connection.getResponseCode()).thenReturn(200);
        Mockito.when(connection.getHeaderField(HTTP_HEADER_REPLY_URL_HASH)).thenReturn(replyHash);
        ShuffleHeader header = new ShuffleHeader(map1ID.toString(), 10, 10, 1);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        header.write(new DataOutputStream(bout));
        ByteArrayInputStream in = new ByteArrayInputStream(bout.toByteArray());
        Mockito.when(connection.getInputStream()).thenReturn(in);
        Mockito.when(connection.getHeaderField(HTTP_HEADER_NAME)).thenReturn(DEFAULT_HTTP_HEADER_NAME);
        Mockito.when(connection.getHeaderField(HTTP_HEADER_VERSION)).thenReturn(DEFAULT_HTTP_HEADER_VERSION);
        // Verify that unreserve occurs if an exception happens after shuffle
        // buffer is reserved.
        Mockito.when(mm.reserve(ArgumentMatchers.any(TaskAttemptID.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt())).thenReturn(immo);
        Mockito.doThrow(new IOException("forced error")).when(immo).shuffle(ArgumentMatchers.any(MapHost.class), ArgumentMatchers.any(InputStream.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ShuffleClientMetrics.class), ArgumentMatchers.any(Reporter.class));
        underTest.copyFromHost(host);
        Mockito.verify(immo).abort();
    }

    public static class FakeFetcher<K, V> extends Fetcher<K, V> {
        // If connection need to be reopen.
        private boolean renewConnection = false;

        public FakeFetcher(JobConf job, TaskAttemptID reduceId, ShuffleSchedulerImpl<K, V> scheduler, MergeManagerImpl<K, V> merger, Reporter reporter, ShuffleClientMetrics metrics, ExceptionReporter exceptionReporter, SecretKey jobTokenSecret, HttpURLConnection connection) {
            super(job, reduceId, scheduler, merger, reporter, metrics, exceptionReporter, jobTokenSecret);
            this.connection = connection;
        }

        public FakeFetcher(JobConf job, TaskAttemptID reduceId, ShuffleSchedulerImpl<K, V> scheduler, MergeManagerImpl<K, V> merger, Reporter reporter, ShuffleClientMetrics metrics, ExceptionReporter exceptionReporter, SecretKey jobTokenSecret, HttpURLConnection connection, boolean renewConnection) {
            super(job, reduceId, scheduler, merger, reporter, metrics, exceptionReporter, jobTokenSecret);
            this.connection = connection;
            this.renewConnection = renewConnection;
        }

        public FakeFetcher(JobConf job, TaskAttemptID reduceId, ShuffleSchedulerImpl<K, V> scheduler, MergeManagerImpl<K, V> merger, Reporter reporter, ShuffleClientMetrics metrics, ExceptionReporter exceptionReporter, SecretKey jobTokenSecret, HttpURLConnection connection, int id) {
            super(job, reduceId, scheduler, merger, reporter, metrics, exceptionReporter, jobTokenSecret, id);
            this.connection = connection;
        }

        @Override
        protected void openConnection(URL url) throws IOException {
            if ((null == (TestFetcher.this.connection)) || (renewConnection)) {
                super.openConnection(url);
            }
            // already 'opened' the mocked connection
            return;
        }
    }

    static class StuckInputStream extends FilterInputStream {
        boolean stuck = false;

        volatile boolean closed = false;

        StuckInputStream(InputStream inner) {
            super(inner);
        }

        int freeze() throws IOException {
            synchronized(this) {
                stuck = true;
                notify();
            }
            // connection doesn't throw InterruptedException, but may return some
            // bytes geq 0 or throw an exception
            while ((!(Thread.currentThread().isInterrupted())) || (closed)) {
                // spin
                if (closed) {
                    throw new IOException("underlying stream closed, triggered an error");
                }
            } 
            return 0;
        }

        @Override
        public int read() throws IOException {
            int ret = super.read();
            if (ret != (-1)) {
                return ret;
            }
            return freeze();
        }

        @Override
        public int read(byte[] b) throws IOException {
            int ret = super.read(b);
            if (ret != (-1)) {
                return ret;
            }
            return freeze();
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int ret = super.read(b, off, len);
            if (ret != (-1)) {
                return ret;
            }
            return freeze();
        }

        @Override
        public void close() throws IOException {
            closed = true;
        }

        public synchronized void waitForFetcher() throws InterruptedException {
            while (!(stuck)) {
                wait();
            } 
        }

        public boolean wasClosedProperly() {
            return closed;
        }
    }
}

