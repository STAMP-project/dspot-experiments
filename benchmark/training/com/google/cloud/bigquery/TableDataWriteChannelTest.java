/**
 * Copyright 2015 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.bigquery;


import CaptureType.ALL;
import JobInfo.CreateDisposition.CREATE_IF_NEEDED;
import JobInfo.WriteDisposition.WRITE_APPEND;
import TableDataWriteChannel.StateImpl;
import com.google.cloud.RestorableState;
import com.google.cloud.WriteChannel;
import com.google.cloud.bigquery.spi.BigQueryRpcFactory;
import com.google.cloud.bigquery.spi.v2.BigQueryRpc;
import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;
import org.easymock.Capture;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class TableDataWriteChannelTest {
    private static final String UPLOAD_ID = "uploadid";

    private static final TableId TABLE_ID = TableId.of("dataset", "table");

    private static final WriteChannelConfiguration LOAD_CONFIGURATION = WriteChannelConfiguration.newBuilder(TableDataWriteChannelTest.TABLE_ID).setCreateDisposition(CREATE_IF_NEEDED).setWriteDisposition(WRITE_APPEND).setFormatOptions(FormatOptions.json()).setIgnoreUnknownValues(true).setMaxBadRecords(10).build();

    private static final int MIN_CHUNK_SIZE = 256 * 1024;

    private static final int DEFAULT_CHUNK_SIZE = 8 * (TableDataWriteChannelTest.MIN_CHUNK_SIZE);

    private static final int CUSTOM_CHUNK_SIZE = 4 * (TableDataWriteChannelTest.MIN_CHUNK_SIZE);

    private static final Random RANDOM = new Random();

    private static final LoadJobConfiguration JOB_CONFIGURATION = LoadJobConfiguration.of(TableDataWriteChannelTest.TABLE_ID, "URI");

    private static final JobInfo JOB_INFO = JobInfo.of(JobId.of(), TableDataWriteChannelTest.JOB_CONFIGURATION);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private BigQueryOptions options;

    private BigQueryRpcFactory rpcFactoryMock;

    private BigQueryRpc bigqueryRpcMock;

    private BigQueryFactory bigqueryFactoryMock;

    private BigQuery bigqueryMock;

    private Job job;

    private TableDataWriteChannel writer;

    @Test
    public void testCreate() {
        expect(bigqueryRpcMock.open(new com.google.api.services.bigquery.model.Job().setJobReference(TableDataWriteChannelTest.JOB_INFO.getJobId().toPb()).setConfiguration(TableDataWriteChannelTest.LOAD_CONFIGURATION.toPb()))).andReturn(TableDataWriteChannelTest.UPLOAD_ID);
        replay(bigqueryRpcMock);
        writer = new TableDataWriteChannel(options, TableDataWriteChannelTest.JOB_INFO.getJobId(), TableDataWriteChannelTest.LOAD_CONFIGURATION);
        Assert.assertTrue(writer.isOpen());
        Assert.assertNull(writer.getJob());
    }

    @Test
    public void testCreateRetryableError() {
        BigQueryException exception = new BigQueryException(new SocketException("Socket closed"));
        expect(bigqueryRpcMock.open(new com.google.api.services.bigquery.model.Job().setJobReference(TableDataWriteChannelTest.JOB_INFO.getJobId().toPb()).setConfiguration(TableDataWriteChannelTest.LOAD_CONFIGURATION.toPb()))).andThrow(exception);
        expect(bigqueryRpcMock.open(new com.google.api.services.bigquery.model.Job().setJobReference(TableDataWriteChannelTest.JOB_INFO.getJobId().toPb()).setConfiguration(TableDataWriteChannelTest.LOAD_CONFIGURATION.toPb()))).andReturn(TableDataWriteChannelTest.UPLOAD_ID);
        replay(bigqueryRpcMock);
        writer = new TableDataWriteChannel(options, TableDataWriteChannelTest.JOB_INFO.getJobId(), TableDataWriteChannelTest.LOAD_CONFIGURATION);
        Assert.assertTrue(writer.isOpen());
        Assert.assertNull(writer.getJob());
    }

    @Test
    public void testCreateNonRetryableError() {
        expect(bigqueryRpcMock.open(new com.google.api.services.bigquery.model.Job().setJobReference(TableDataWriteChannelTest.JOB_INFO.getJobId().toPb()).setConfiguration(TableDataWriteChannelTest.LOAD_CONFIGURATION.toPb()))).andThrow(new RuntimeException());
        replay(bigqueryRpcMock);
        thrown.expect(RuntimeException.class);
        new TableDataWriteChannel(options, TableDataWriteChannelTest.JOB_INFO.getJobId(), TableDataWriteChannelTest.LOAD_CONFIGURATION);
    }

    @Test
    public void testWriteWithoutFlush() throws IOException {
        expect(bigqueryRpcMock.open(new com.google.api.services.bigquery.model.Job().setJobReference(TableDataWriteChannelTest.JOB_INFO.getJobId().toPb()).setConfiguration(TableDataWriteChannelTest.LOAD_CONFIGURATION.toPb()))).andReturn(TableDataWriteChannelTest.UPLOAD_ID);
        replay(bigqueryRpcMock);
        writer = new TableDataWriteChannel(options, TableDataWriteChannelTest.JOB_INFO.getJobId(), TableDataWriteChannelTest.LOAD_CONFIGURATION);
        Assert.assertEquals(TableDataWriteChannelTest.MIN_CHUNK_SIZE, writer.write(ByteBuffer.allocate(TableDataWriteChannelTest.MIN_CHUNK_SIZE)));
        Assert.assertNull(writer.getJob());
    }

    @Test
    public void testWriteWithFlush() throws IOException {
        expect(bigqueryRpcMock.open(new com.google.api.services.bigquery.model.Job().setJobReference(TableDataWriteChannelTest.JOB_INFO.getJobId().toPb()).setConfiguration(TableDataWriteChannelTest.LOAD_CONFIGURATION.toPb()))).andReturn(TableDataWriteChannelTest.UPLOAD_ID);
        Capture<byte[]> capturedBuffer = Capture.newInstance();
        expect(bigqueryRpcMock.write(eq(TableDataWriteChannelTest.UPLOAD_ID), capture(capturedBuffer), eq(0), eq(0L), eq(TableDataWriteChannelTest.CUSTOM_CHUNK_SIZE), eq(false))).andReturn(null);
        replay(bigqueryRpcMock);
        writer = new TableDataWriteChannel(options, TableDataWriteChannelTest.JOB_INFO.getJobId(), TableDataWriteChannelTest.LOAD_CONFIGURATION);
        writer.setChunkSize(TableDataWriteChannelTest.CUSTOM_CHUNK_SIZE);
        ByteBuffer buffer = TableDataWriteChannelTest.randomBuffer(TableDataWriteChannelTest.CUSTOM_CHUNK_SIZE);
        Assert.assertEquals(TableDataWriteChannelTest.CUSTOM_CHUNK_SIZE, writer.write(buffer));
        Assert.assertArrayEquals(buffer.array(), capturedBuffer.getValue());
        Assert.assertNull(writer.getJob());
    }

    @Test
    public void testWritesAndFlush() throws IOException {
        expect(bigqueryRpcMock.open(new com.google.api.services.bigquery.model.Job().setJobReference(TableDataWriteChannelTest.JOB_INFO.getJobId().toPb()).setConfiguration(TableDataWriteChannelTest.LOAD_CONFIGURATION.toPb()))).andReturn(TableDataWriteChannelTest.UPLOAD_ID);
        Capture<byte[]> capturedBuffer = Capture.newInstance();
        expect(bigqueryRpcMock.write(eq(TableDataWriteChannelTest.UPLOAD_ID), capture(capturedBuffer), eq(0), eq(0L), eq(TableDataWriteChannelTest.DEFAULT_CHUNK_SIZE), eq(false))).andReturn(null);
        replay(bigqueryRpcMock);
        writer = new TableDataWriteChannel(options, TableDataWriteChannelTest.JOB_INFO.getJobId(), TableDataWriteChannelTest.LOAD_CONFIGURATION);
        ByteBuffer[] buffers = new ByteBuffer[(TableDataWriteChannelTest.DEFAULT_CHUNK_SIZE) / (TableDataWriteChannelTest.MIN_CHUNK_SIZE)];
        for (int i = 0; i < (buffers.length); i++) {
            buffers[i] = TableDataWriteChannelTest.randomBuffer(TableDataWriteChannelTest.MIN_CHUNK_SIZE);
            Assert.assertEquals(TableDataWriteChannelTest.MIN_CHUNK_SIZE, writer.write(buffers[i]));
        }
        for (int i = 0; i < (buffers.length); i++) {
            Assert.assertArrayEquals(buffers[i].array(), Arrays.copyOfRange(capturedBuffer.getValue(), ((TableDataWriteChannelTest.MIN_CHUNK_SIZE) * i), ((TableDataWriteChannelTest.MIN_CHUNK_SIZE) * (i + 1))));
        }
        Assert.assertNull(writer.getJob());
    }

    @Test
    public void testCloseWithoutFlush() throws IOException {
        expect(bigqueryRpcMock.open(new com.google.api.services.bigquery.model.Job().setJobReference(TableDataWriteChannelTest.JOB_INFO.getJobId().toPb()).setConfiguration(TableDataWriteChannelTest.LOAD_CONFIGURATION.toPb()))).andReturn(TableDataWriteChannelTest.UPLOAD_ID);
        Capture<byte[]> capturedBuffer = Capture.newInstance();
        expect(bigqueryRpcMock.write(eq(TableDataWriteChannelTest.UPLOAD_ID), capture(capturedBuffer), eq(0), eq(0L), eq(0), eq(true))).andReturn(job.toPb());
        replay(bigqueryRpcMock);
        writer = new TableDataWriteChannel(options, TableDataWriteChannelTest.JOB_INFO.getJobId(), TableDataWriteChannelTest.LOAD_CONFIGURATION);
        Assert.assertTrue(writer.isOpen());
        writer.close();
        Assert.assertArrayEquals(new byte[0], capturedBuffer.getValue());
        Assert.assertTrue((!(writer.isOpen())));
        Assert.assertEquals(job, writer.getJob());
    }

    @Test
    public void testCloseWithFlush() throws IOException {
        expect(bigqueryRpcMock.open(new com.google.api.services.bigquery.model.Job().setJobReference(TableDataWriteChannelTest.JOB_INFO.getJobId().toPb()).setConfiguration(TableDataWriteChannelTest.LOAD_CONFIGURATION.toPb()))).andReturn(TableDataWriteChannelTest.UPLOAD_ID);
        Capture<byte[]> capturedBuffer = Capture.newInstance();
        ByteBuffer buffer = TableDataWriteChannelTest.randomBuffer(TableDataWriteChannelTest.MIN_CHUNK_SIZE);
        expect(bigqueryRpcMock.write(eq(TableDataWriteChannelTest.UPLOAD_ID), capture(capturedBuffer), eq(0), eq(0L), eq(TableDataWriteChannelTest.MIN_CHUNK_SIZE), eq(true))).andReturn(job.toPb());
        replay(bigqueryRpcMock);
        writer = new TableDataWriteChannel(options, TableDataWriteChannelTest.JOB_INFO.getJobId(), TableDataWriteChannelTest.LOAD_CONFIGURATION);
        Assert.assertTrue(writer.isOpen());
        writer.write(buffer);
        writer.close();
        Assert.assertEquals(TableDataWriteChannelTest.DEFAULT_CHUNK_SIZE, capturedBuffer.getValue().length);
        Assert.assertArrayEquals(buffer.array(), Arrays.copyOf(capturedBuffer.getValue(), TableDataWriteChannelTest.MIN_CHUNK_SIZE));
        Assert.assertTrue((!(writer.isOpen())));
        Assert.assertEquals(job, writer.getJob());
    }

    @Test
    public void testWriteClosed() throws IOException {
        expect(bigqueryRpcMock.open(new com.google.api.services.bigquery.model.Job().setJobReference(TableDataWriteChannelTest.JOB_INFO.getJobId().toPb()).setConfiguration(TableDataWriteChannelTest.LOAD_CONFIGURATION.toPb()))).andReturn(TableDataWriteChannelTest.UPLOAD_ID);
        Capture<byte[]> capturedBuffer = Capture.newInstance();
        expect(bigqueryRpcMock.write(eq(TableDataWriteChannelTest.UPLOAD_ID), capture(capturedBuffer), eq(0), eq(0L), eq(0), eq(true))).andReturn(job.toPb());
        replay(bigqueryRpcMock);
        writer = new TableDataWriteChannel(options, TableDataWriteChannelTest.JOB_INFO.getJobId(), TableDataWriteChannelTest.LOAD_CONFIGURATION);
        writer.close();
        Assert.assertEquals(job, writer.getJob());
        try {
            writer.write(ByteBuffer.allocate(TableDataWriteChannelTest.MIN_CHUNK_SIZE));
            Assert.fail("Expected TableDataWriteChannel write to throw IOException");
        } catch (IOException ex) {
            // expected
        }
    }

    @Test
    public void testSaveAndRestore() throws IOException {
        expect(bigqueryRpcMock.open(new com.google.api.services.bigquery.model.Job().setJobReference(TableDataWriteChannelTest.JOB_INFO.getJobId().toPb()).setConfiguration(TableDataWriteChannelTest.LOAD_CONFIGURATION.toPb()))).andReturn(TableDataWriteChannelTest.UPLOAD_ID);
        Capture<byte[]> capturedBuffer = Capture.newInstance(ALL);
        Capture<Long> capturedPosition = Capture.newInstance(ALL);
        expect(bigqueryRpcMock.write(eq(TableDataWriteChannelTest.UPLOAD_ID), capture(capturedBuffer), eq(0), captureLong(capturedPosition), eq(TableDataWriteChannelTest.DEFAULT_CHUNK_SIZE), eq(false))).andReturn(null).times(2);
        replay(bigqueryRpcMock);
        ByteBuffer buffer1 = TableDataWriteChannelTest.randomBuffer(TableDataWriteChannelTest.DEFAULT_CHUNK_SIZE);
        ByteBuffer buffer2 = TableDataWriteChannelTest.randomBuffer(TableDataWriteChannelTest.DEFAULT_CHUNK_SIZE);
        writer = new TableDataWriteChannel(options, TableDataWriteChannelTest.JOB_INFO.getJobId(), TableDataWriteChannelTest.LOAD_CONFIGURATION);
        Assert.assertEquals(TableDataWriteChannelTest.DEFAULT_CHUNK_SIZE, writer.write(buffer1));
        Assert.assertArrayEquals(buffer1.array(), capturedBuffer.getValues().get(0));
        Assert.assertEquals(new Long(0L), capturedPosition.getValues().get(0));
        Assert.assertNull(writer.getJob());
        RestorableState<WriteChannel> writerState = writer.capture();
        WriteChannel restoredWriter = writerState.restore();
        Assert.assertEquals(TableDataWriteChannelTest.DEFAULT_CHUNK_SIZE, restoredWriter.write(buffer2));
        Assert.assertArrayEquals(buffer2.array(), capturedBuffer.getValues().get(1));
        Assert.assertEquals(new Long(TableDataWriteChannelTest.DEFAULT_CHUNK_SIZE), capturedPosition.getValues().get(1));
    }

    @Test
    public void testSaveAndRestoreClosed() throws IOException {
        expect(bigqueryRpcMock.open(new com.google.api.services.bigquery.model.Job().setJobReference(TableDataWriteChannelTest.JOB_INFO.getJobId().toPb()).setConfiguration(TableDataWriteChannelTest.LOAD_CONFIGURATION.toPb()))).andReturn(TableDataWriteChannelTest.UPLOAD_ID);
        Capture<byte[]> capturedBuffer = Capture.newInstance();
        expect(bigqueryRpcMock.write(eq(TableDataWriteChannelTest.UPLOAD_ID), capture(capturedBuffer), eq(0), eq(0L), eq(0), eq(true))).andReturn(job.toPb());
        replay(bigqueryRpcMock);
        writer = new TableDataWriteChannel(options, TableDataWriteChannelTest.JOB_INFO.getJobId(), TableDataWriteChannelTest.LOAD_CONFIGURATION);
        writer.close();
        Assert.assertEquals(job, writer.getJob());
        RestorableState<WriteChannel> writerState = writer.capture();
        RestorableState<WriteChannel> expectedWriterState = StateImpl.builder(options, TableDataWriteChannelTest.LOAD_CONFIGURATION, TableDataWriteChannelTest.UPLOAD_ID, job).setBuffer(null).setChunkSize(TableDataWriteChannelTest.DEFAULT_CHUNK_SIZE).setIsOpen(false).setPosition(0).build();
        WriteChannel restoredWriter = writerState.restore();
        Assert.assertArrayEquals(new byte[0], capturedBuffer.getValue());
        Assert.assertEquals(expectedWriterState, restoredWriter.capture());
    }

    @Test
    public void testStateEquals() {
        expect(bigqueryRpcMock.open(new com.google.api.services.bigquery.model.Job().setJobReference(TableDataWriteChannelTest.JOB_INFO.getJobId().toPb()).setConfiguration(TableDataWriteChannelTest.LOAD_CONFIGURATION.toPb()))).andReturn(TableDataWriteChannelTest.UPLOAD_ID).times(2);
        replay(bigqueryRpcMock);
        writer = new TableDataWriteChannel(options, TableDataWriteChannelTest.JOB_INFO.getJobId(), TableDataWriteChannelTest.LOAD_CONFIGURATION);
        // avoid closing when you don't want partial writes upon failure
        @SuppressWarnings("resource")
        WriteChannel writer2 = new TableDataWriteChannel(options, TableDataWriteChannelTest.JOB_INFO.getJobId(), TableDataWriteChannelTest.LOAD_CONFIGURATION);
        RestorableState<WriteChannel> state = writer.capture();
        RestorableState<WriteChannel> state2 = writer2.capture();
        Assert.assertEquals(state, state2);
        Assert.assertEquals(state.hashCode(), state2.hashCode());
        Assert.assertEquals(state.toString(), state2.toString());
    }
}

