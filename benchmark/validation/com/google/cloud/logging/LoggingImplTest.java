/**
 * Copyright 2016 Google LLC
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
package com.google.cloud.logging;


import Destination.BucketDestination;
import Logging.SortingOrder.DESCENDING;
import Severity.DEFAULT;
import Severity.EMERGENCY;
import SortingField.TIMESTAMP;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.core.SettableApiFuture;
import com.google.api.gax.paging.AsyncPage;
import com.google.api.gax.paging.Page;
import com.google.cloud.MonitoredResource;
import com.google.cloud.MonitoredResourceDescriptor;
import com.google.cloud.com.google.api.MonitoredResourceDescriptor;
import com.google.cloud.logging.Logging.EntryListOption;
import com.google.cloud.logging.Logging.ListOption;
import com.google.cloud.logging.Logging.WriteOption;
import com.google.cloud.logging.Payload.StringPayload;
import com.google.cloud.logging.spi.LoggingRpcFactory;
import com.google.cloud.logging.spi.v2.LoggingRpc;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.logging.v2.CreateLogMetricRequest;
import com.google.logging.v2.CreateSinkRequest;
import com.google.logging.v2.DeleteLogMetricRequest;
import com.google.logging.v2.DeleteLogRequest;
import com.google.logging.v2.DeleteSinkRequest;
import com.google.logging.v2.GetLogMetricRequest;
import com.google.logging.v2.GetSinkRequest;
import com.google.logging.v2.ListLogEntriesRequest;
import com.google.logging.v2.ListLogEntriesResponse;
import com.google.logging.v2.ListLogMetricsRequest;
import com.google.logging.v2.ListLogMetricsResponse;
import com.google.logging.v2.ListMonitoredResourceDescriptorsRequest;
import com.google.logging.v2.ListMonitoredResourceDescriptorsResponse;
import com.google.logging.v2.ListSinksRequest;
import com.google.logging.v2.ListSinksResponse;
import com.google.logging.v2.LogMetric;
import com.google.logging.v2.LogSink;
import com.google.logging.v2.UpdateLogMetricRequest;
import com.google.logging.v2.UpdateSinkRequest;
import com.google.logging.v2.WriteLogEntriesRequest;
import com.google.logging.v2.WriteLogEntriesResponse;
import com.google.protobuf.Empty;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class LoggingImplTest {
    private static final String PROJECT = "project";

    private static final String PROJECT_PB = "projects/" + (LoggingImplTest.PROJECT);

    private static final String SINK_NAME = "sink";

    private static final SinkInfo SINK_INFO = SinkInfo.of(LoggingImplTest.SINK_NAME, BucketDestination.of("bucket"));

    private static final String SINK_NAME_PB = (("projects/" + (LoggingImplTest.PROJECT)) + "/sinks/") + (LoggingImplTest.SINK_NAME);

    private static final String METRIC_NAME = "metric";

    private static final String METRIC_NAME_PB = (("projects/" + (LoggingImplTest.PROJECT)) + "/metrics/") + (LoggingImplTest.METRIC_NAME);

    private static final String FILTER = "logName=projects/my-projectid/logs/syslog";

    private static final String DESCRIPTION = "description";

    private static final MetricInfo METRIC_INFO = MetricInfo.newBuilder(LoggingImplTest.METRIC_NAME, LoggingImplTest.FILTER).setDescription(LoggingImplTest.DESCRIPTION).build();

    private static final MonitoredResourceDescriptor DESCRIPTOR_PB = com.google.api.MonitoredResourceDescriptor.getDefaultInstance();

    private static final MonitoredResourceDescriptor DESCRIPTOR = MonitoredResourceDescriptor.fromPb(LoggingImplTest.DESCRIPTOR_PB);

    private static final String LOG_NAME = "log";

    private static final String LOG_NAME_PB = (("projects/" + (LoggingImplTest.PROJECT)) + "/logs/") + (LoggingImplTest.LOG_NAME);

    private static final MonitoredResource MONITORED_RESOURCE = MonitoredResource.newBuilder("global").addLabel("project-id", LoggingImplTest.PROJECT).build();

    private static final LogEntry LOG_ENTRY1 = LogEntry.newBuilder(StringPayload.of("entry1")).setLogName(LoggingImplTest.LOG_NAME).setResource(LoggingImplTest.MONITORED_RESOURCE).build();

    private static final LogEntry LOG_ENTRY2 = LogEntry.newBuilder(StringPayload.of("entry2")).setLogName(LoggingImplTest.LOG_NAME).setResource(LoggingImplTest.MONITORED_RESOURCE).build();

    private static final Function<SinkInfo, LogSink> SINK_TO_PB_FUNCTION = new Function<SinkInfo, LogSink>() {
        @Override
        public LogSink apply(SinkInfo sinkInfo) {
            return sinkInfo.toPb(LoggingImplTest.PROJECT);
        }
    };

    private static final Function<MetricInfo, LogMetric> METRIC_TO_PB_FUNCTION = new Function<MetricInfo, LogMetric>() {
        @Override
        public LogMetric apply(MetricInfo metricInfo) {
            return metricInfo.toPb();
        }
    };

    private static final Function<MonitoredResourceDescriptor, com.google.api.MonitoredResourceDescriptor> DESCRIPTOR_TO_PB_FUNCTION = new Function<MonitoredResourceDescriptor, com.google.api.MonitoredResourceDescriptor>() {
        @Override
        public MonitoredResourceDescriptor apply(MonitoredResourceDescriptor descriptor) {
            return descriptor.toPb();
        }
    };

    private LoggingOptions options;

    private LoggingRpcFactory rpcFactoryMock;

    private LoggingRpc loggingRpcMock;

    private Logging logging;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testGetOptions() {
        EasyMock.replay(rpcFactoryMock, loggingRpcMock);
        Assert.assertSame(options, options.getService().getOptions());
    }

    @Test
    public void testCreateSink() {
        LogSink sinkPb = LoggingImplTest.SINK_INFO.toPb(LoggingImplTest.PROJECT);
        ApiFuture<LogSink> response = ApiFutures.immediateFuture(sinkPb);
        CreateSinkRequest request = CreateSinkRequest.newBuilder().setParent(LoggingImplTest.PROJECT_PB).setSink(sinkPb).build();
        EasyMock.expect(loggingRpcMock.create(request)).andReturn(response);
        EasyMock.replay(rpcFactoryMock, loggingRpcMock);
        logging = options.getService();
        Sink sink = logging.create(LoggingImplTest.SINK_INFO);
        Assert.assertEquals(new Sink(logging, new SinkInfo.BuilderImpl(LoggingImplTest.SINK_INFO)), sink);
    }

    @Test
    public void testCreateSinkAsync() throws InterruptedException, ExecutionException {
        LogSink sinkPb = LoggingImplTest.SINK_INFO.toPb(LoggingImplTest.PROJECT);
        ApiFuture<LogSink> response = ApiFutures.immediateFuture(sinkPb);
        CreateSinkRequest request = CreateSinkRequest.newBuilder().setParent(LoggingImplTest.PROJECT_PB).setSink(sinkPb).build();
        EasyMock.expect(loggingRpcMock.create(request)).andReturn(response);
        EasyMock.replay(rpcFactoryMock, loggingRpcMock);
        logging = options.getService();
        Sink sink = logging.createAsync(LoggingImplTest.SINK_INFO).get();
        Assert.assertEquals(new Sink(logging, new SinkInfo.BuilderImpl(LoggingImplTest.SINK_INFO)), sink);
    }

    @Test
    public void testUpdateSink() {
        LogSink sinkPb = LoggingImplTest.SINK_INFO.toPb(LoggingImplTest.PROJECT);
        ApiFuture<LogSink> response = ApiFutures.immediateFuture(sinkPb);
        UpdateSinkRequest request = UpdateSinkRequest.newBuilder().setSinkName(LoggingImplTest.SINK_NAME_PB).setSink(sinkPb).build();
        EasyMock.expect(loggingRpcMock.update(request)).andReturn(response);
        EasyMock.replay(rpcFactoryMock, loggingRpcMock);
        logging = options.getService();
        Sink sink = logging.update(LoggingImplTest.SINK_INFO);
        Assert.assertEquals(new Sink(logging, new SinkInfo.BuilderImpl(LoggingImplTest.SINK_INFO)), sink);
    }

    @Test
    public void testUpdateSinkAsync() throws InterruptedException, ExecutionException {
        LogSink sinkPb = LoggingImplTest.SINK_INFO.toPb(LoggingImplTest.PROJECT);
        ApiFuture<LogSink> response = ApiFutures.immediateFuture(sinkPb);
        UpdateSinkRequest request = UpdateSinkRequest.newBuilder().setSinkName(LoggingImplTest.SINK_NAME_PB).setSink(sinkPb).build();
        EasyMock.expect(loggingRpcMock.update(request)).andReturn(response);
        EasyMock.replay(rpcFactoryMock, loggingRpcMock);
        logging = options.getService();
        Sink sink = logging.updateAsync(LoggingImplTest.SINK_INFO).get();
        Assert.assertEquals(new Sink(logging, new SinkInfo.BuilderImpl(LoggingImplTest.SINK_INFO)), sink);
    }

    @Test
    public void testGetSink() {
        LogSink sinkPb = LoggingImplTest.SINK_INFO.toPb(LoggingImplTest.PROJECT);
        ApiFuture<LogSink> response = ApiFutures.immediateFuture(sinkPb);
        GetSinkRequest request = GetSinkRequest.newBuilder().setSinkName(LoggingImplTest.SINK_NAME_PB).build();
        EasyMock.expect(loggingRpcMock.get(request)).andReturn(response);
        EasyMock.replay(rpcFactoryMock, loggingRpcMock);
        logging = options.getService();
        Sink sink = logging.getSink(LoggingImplTest.SINK_NAME);
        Assert.assertEquals(new Sink(logging, new SinkInfo.BuilderImpl(LoggingImplTest.SINK_INFO)), sink);
    }

    @Test
    public void testGetSink_Null() {
        ApiFuture<LogSink> response = ApiFutures.immediateFuture(null);
        GetSinkRequest request = GetSinkRequest.newBuilder().setSinkName(LoggingImplTest.SINK_NAME_PB).build();
        EasyMock.expect(loggingRpcMock.get(request)).andReturn(response);
        EasyMock.replay(rpcFactoryMock, loggingRpcMock);
        logging = options.getService();
        Assert.assertNull(logging.getSink(LoggingImplTest.SINK_NAME));
    }

    @Test
    public void testGetSinkAsync() throws InterruptedException, ExecutionException {
        LogSink sinkPb = LoggingImplTest.SINK_INFO.toPb(LoggingImplTest.PROJECT);
        ApiFuture<LogSink> response = ApiFutures.immediateFuture(sinkPb);
        GetSinkRequest request = GetSinkRequest.newBuilder().setSinkName(LoggingImplTest.SINK_NAME_PB).build();
        EasyMock.expect(loggingRpcMock.get(request)).andReturn(response);
        EasyMock.replay(rpcFactoryMock, loggingRpcMock);
        logging = options.getService();
        Sink sink = logging.getSinkAsync(LoggingImplTest.SINK_NAME).get();
        Assert.assertEquals(new Sink(logging, new SinkInfo.BuilderImpl(LoggingImplTest.SINK_INFO)), sink);
    }

    @Test
    public void testGetSinkAsync_Null() throws InterruptedException, ExecutionException {
        ApiFuture<LogSink> response = ApiFutures.immediateFuture(null);
        GetSinkRequest request = GetSinkRequest.newBuilder().setSinkName(LoggingImplTest.SINK_NAME_PB).build();
        EasyMock.expect(loggingRpcMock.get(request)).andReturn(response);
        EasyMock.replay(rpcFactoryMock, loggingRpcMock);
        logging = options.getService();
        Assert.assertNull(logging.getSinkAsync(LoggingImplTest.SINK_NAME).get());
    }

    @Test
    public void testDeleteSink() {
        DeleteSinkRequest request = DeleteSinkRequest.newBuilder().setSinkName(LoggingImplTest.SINK_NAME_PB).build();
        ApiFuture<Empty> response = ApiFutures.immediateFuture(Empty.getDefaultInstance());
        EasyMock.expect(loggingRpcMock.delete(request)).andReturn(response);
        EasyMock.replay(rpcFactoryMock, loggingRpcMock);
        logging = options.getService();
        Assert.assertTrue(logging.deleteSink(LoggingImplTest.SINK_NAME));
    }

    @Test
    public void testDeleteSink_Null() {
        DeleteSinkRequest request = DeleteSinkRequest.newBuilder().setSinkName(LoggingImplTest.SINK_NAME_PB).build();
        ApiFuture<Empty> response = ApiFutures.immediateFuture(null);
        EasyMock.expect(loggingRpcMock.delete(request)).andReturn(response);
        EasyMock.replay(rpcFactoryMock, loggingRpcMock);
        logging = options.getService();
        Assert.assertFalse(logging.deleteSink(LoggingImplTest.SINK_NAME));
    }

    @Test
    public void testDeleteSinkAsync() throws InterruptedException, ExecutionException {
        DeleteSinkRequest request = DeleteSinkRequest.newBuilder().setSinkName(LoggingImplTest.SINK_NAME_PB).build();
        ApiFuture<Empty> response = ApiFutures.immediateFuture(Empty.getDefaultInstance());
        EasyMock.expect(loggingRpcMock.delete(request)).andReturn(response);
        EasyMock.replay(rpcFactoryMock, loggingRpcMock);
        logging = options.getService();
        Assert.assertTrue(logging.deleteSinkAsync(LoggingImplTest.SINK_NAME).get());
    }

    @Test
    public void testDeleteSinkAsync_Null() throws InterruptedException, ExecutionException {
        DeleteSinkRequest request = DeleteSinkRequest.newBuilder().setSinkName(LoggingImplTest.SINK_NAME_PB).build();
        ApiFuture<Empty> response = ApiFutures.immediateFuture(null);
        EasyMock.expect(loggingRpcMock.delete(request)).andReturn(response);
        EasyMock.replay(rpcFactoryMock, loggingRpcMock);
        logging = options.getService();
        Assert.assertFalse(logging.deleteSinkAsync(LoggingImplTest.SINK_NAME).get());
    }

    @Test
    public void testListSinks() {
        String cursor = "cursor";
        EasyMock.replay(rpcFactoryMock);
        logging = options.getService();
        ListSinksRequest request = ListSinksRequest.newBuilder().setParent(LoggingImplTest.PROJECT_PB).build();
        List<Sink> sinkList = ImmutableList.of(new Sink(logging, new SinkInfo.BuilderImpl(LoggingImplTest.SINK_INFO)), new Sink(logging, new SinkInfo.BuilderImpl(LoggingImplTest.SINK_INFO)));
        ListSinksResponse response = ListSinksResponse.newBuilder().setNextPageToken(cursor).addAllSinks(Lists.transform(sinkList, LoggingImplTest.SINK_TO_PB_FUNCTION)).build();
        ApiFuture<ListSinksResponse> futureResponse = ApiFutures.immediateFuture(response);
        EasyMock.expect(loggingRpcMock.list(request)).andReturn(futureResponse);
        EasyMock.replay(loggingRpcMock);
        Page<Sink> page = logging.listSinks();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(sinkList.toArray(), Iterables.toArray(page.getValues(), Sink.class));
    }

    @Test
    public void testListSinksNextPage() {
        String cursor1 = "cursor";
        EasyMock.replay(rpcFactoryMock);
        logging = options.getService();
        ListSinksRequest request1 = ListSinksRequest.newBuilder().setParent(LoggingImplTest.PROJECT_PB).build();
        ListSinksRequest request2 = ListSinksRequest.newBuilder().setParent(LoggingImplTest.PROJECT_PB).setPageToken(cursor1).build();
        List<Sink> sinkList1 = ImmutableList.of(new Sink(logging, new SinkInfo.BuilderImpl(LoggingImplTest.SINK_INFO)), new Sink(logging, new SinkInfo.BuilderImpl(LoggingImplTest.SINK_INFO)));
        List<Sink> sinkList2 = ImmutableList.of(new Sink(logging, new SinkInfo.BuilderImpl(LoggingImplTest.SINK_INFO)));
        ListSinksResponse response1 = ListSinksResponse.newBuilder().setNextPageToken(cursor1).addAllSinks(Lists.transform(sinkList1, LoggingImplTest.SINK_TO_PB_FUNCTION)).build();
        String cursor2 = "nextCursor";
        ListSinksResponse response2 = ListSinksResponse.newBuilder().setNextPageToken(cursor2).addAllSinks(Lists.transform(sinkList2, LoggingImplTest.SINK_TO_PB_FUNCTION)).build();
        ApiFuture<ListSinksResponse> futureResponse1 = ApiFutures.immediateFuture(response1);
        ApiFuture<ListSinksResponse> futureResponse2 = ApiFutures.immediateFuture(response2);
        EasyMock.expect(loggingRpcMock.list(request1)).andReturn(futureResponse1);
        EasyMock.expect(loggingRpcMock.list(request2)).andReturn(futureResponse2);
        EasyMock.replay(loggingRpcMock);
        Page<Sink> page = logging.listSinks();
        Assert.assertEquals(cursor1, page.getNextPageToken());
        Assert.assertArrayEquals(sinkList1.toArray(), Iterables.toArray(page.getValues(), Sink.class));
        page = page.getNextPage();
        Assert.assertEquals(cursor2, page.getNextPageToken());
        Assert.assertArrayEquals(sinkList2.toArray(), Iterables.toArray(page.getValues(), Sink.class));
    }

    @Test
    public void testListSinksEmpty() {
        EasyMock.replay(rpcFactoryMock);
        logging = options.getService();
        ListSinksRequest request = ListSinksRequest.newBuilder().setParent(LoggingImplTest.PROJECT_PB).build();
        List<Sink> sinkList = ImmutableList.of();
        ListSinksResponse response = ListSinksResponse.newBuilder().setNextPageToken("").addAllSinks(Lists.transform(sinkList, LoggingImplTest.SINK_TO_PB_FUNCTION)).build();
        ApiFuture<ListSinksResponse> futureResponse = ApiFutures.immediateFuture(response);
        EasyMock.expect(loggingRpcMock.list(request)).andReturn(futureResponse);
        EasyMock.replay(loggingRpcMock);
        Page<Sink> page = logging.listSinks();
        Assert.assertNull(page.getNextPageToken());
        Assert.assertNull(page.getNextPage());
        Assert.assertArrayEquals(sinkList.toArray(), Iterables.toArray(page.getValues(), Sink.class));
    }

    @Test
    public void testListSinksWithOptions() {
        String cursor = "cursor";
        EasyMock.replay(rpcFactoryMock);
        logging = options.getService();
        ListSinksRequest request = ListSinksRequest.newBuilder().setPageToken(cursor).setPageSize(42).setParent(LoggingImplTest.PROJECT_PB).build();
        List<Sink> sinkList = ImmutableList.of(new Sink(logging, new SinkInfo.BuilderImpl(LoggingImplTest.SINK_INFO)), new Sink(logging, new SinkInfo.BuilderImpl(LoggingImplTest.SINK_INFO)));
        ListSinksResponse response = ListSinksResponse.newBuilder().setNextPageToken(cursor).addAllSinks(Lists.transform(sinkList, LoggingImplTest.SINK_TO_PB_FUNCTION)).build();
        ApiFuture<ListSinksResponse> futureResponse = ApiFutures.immediateFuture(response);
        EasyMock.expect(loggingRpcMock.list(request)).andReturn(futureResponse);
        EasyMock.replay(loggingRpcMock);
        Page<Sink> page = logging.listSinks(ListOption.pageSize(42), ListOption.pageToken(cursor));
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(sinkList.toArray(), Iterables.toArray(page.getValues(), Sink.class));
    }

    @Test
    public void testListSinksAsync() throws InterruptedException, ExecutionException {
        String cursor = "cursor";
        EasyMock.replay(rpcFactoryMock);
        logging = options.getService();
        ListSinksRequest request = ListSinksRequest.newBuilder().setParent(LoggingImplTest.PROJECT_PB).build();
        List<Sink> sinkList = ImmutableList.of(new Sink(logging, new SinkInfo.BuilderImpl(LoggingImplTest.SINK_INFO)), new Sink(logging, new SinkInfo.BuilderImpl(LoggingImplTest.SINK_INFO)));
        ListSinksResponse response = ListSinksResponse.newBuilder().setNextPageToken(cursor).addAllSinks(Lists.transform(sinkList, LoggingImplTest.SINK_TO_PB_FUNCTION)).build();
        ApiFuture<ListSinksResponse> futureResponse = ApiFutures.immediateFuture(response);
        EasyMock.expect(loggingRpcMock.list(request)).andReturn(futureResponse);
        EasyMock.replay(loggingRpcMock);
        AsyncPage<Sink> page = logging.listSinksAsync().get();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(sinkList.toArray(), Iterables.toArray(page.getValues(), Sink.class));
    }

    @Test
    public void testListSinksAsyncNextPage() throws InterruptedException, ExecutionException {
        String cursor1 = "cursor";
        EasyMock.replay(rpcFactoryMock);
        logging = options.getService();
        ListSinksRequest request1 = ListSinksRequest.newBuilder().setParent(LoggingImplTest.PROJECT_PB).build();
        ListSinksRequest request2 = ListSinksRequest.newBuilder().setParent(LoggingImplTest.PROJECT_PB).setPageToken(cursor1).build();
        List<Sink> sinkList1 = ImmutableList.of(new Sink(logging, new SinkInfo.BuilderImpl(LoggingImplTest.SINK_INFO)), new Sink(logging, new SinkInfo.BuilderImpl(LoggingImplTest.SINK_INFO)));
        List<Sink> sinkList2 = ImmutableList.of(new Sink(logging, new SinkInfo.BuilderImpl(LoggingImplTest.SINK_INFO)));
        ListSinksResponse response1 = ListSinksResponse.newBuilder().setNextPageToken(cursor1).addAllSinks(Lists.transform(sinkList1, LoggingImplTest.SINK_TO_PB_FUNCTION)).build();
        String cursor2 = "nextCursor";
        ListSinksResponse response2 = ListSinksResponse.newBuilder().setNextPageToken(cursor2).addAllSinks(Lists.transform(sinkList2, LoggingImplTest.SINK_TO_PB_FUNCTION)).build();
        ApiFuture<ListSinksResponse> futureResponse1 = ApiFutures.immediateFuture(response1);
        ApiFuture<ListSinksResponse> futureResponse2 = ApiFutures.immediateFuture(response2);
        EasyMock.expect(loggingRpcMock.list(request1)).andReturn(futureResponse1);
        EasyMock.expect(loggingRpcMock.list(request2)).andReturn(futureResponse2);
        EasyMock.replay(loggingRpcMock);
        AsyncPage<Sink> page = logging.listSinksAsync().get();
        Assert.assertEquals(cursor1, page.getNextPageToken());
        Assert.assertArrayEquals(sinkList1.toArray(), Iterables.toArray(page.getValues(), Sink.class));
        page = page.getNextPageAsync().get();
        Assert.assertEquals(cursor2, page.getNextPageToken());
        Assert.assertArrayEquals(sinkList2.toArray(), Iterables.toArray(page.getValues(), Sink.class));
    }

    @Test
    public void testListSinksAsyncEmpty() throws InterruptedException, ExecutionException {
        EasyMock.replay(rpcFactoryMock);
        logging = options.getService();
        ListSinksRequest request = ListSinksRequest.newBuilder().setParent(LoggingImplTest.PROJECT_PB).build();
        List<Sink> sinkList = ImmutableList.of();
        ListSinksResponse response = ListSinksResponse.newBuilder().setNextPageToken("").addAllSinks(Lists.transform(sinkList, LoggingImplTest.SINK_TO_PB_FUNCTION)).build();
        ApiFuture<ListSinksResponse> futureResponse = ApiFutures.immediateFuture(response);
        EasyMock.expect(loggingRpcMock.list(request)).andReturn(futureResponse);
        EasyMock.replay(loggingRpcMock);
        AsyncPage<Sink> page = logging.listSinksAsync().get();
        Assert.assertNull(page.getNextPageToken());
        Assert.assertNull(page.getNextPage());
        Assert.assertArrayEquals(sinkList.toArray(), Iterables.toArray(page.getValues(), Sink.class));
    }

    @Test
    public void testListSinksWithOptionsAsync() throws InterruptedException, ExecutionException {
        String cursor = "cursor";
        EasyMock.replay(rpcFactoryMock);
        logging = options.getService();
        ListSinksRequest request = ListSinksRequest.newBuilder().setPageToken(cursor).setPageSize(42).setParent(LoggingImplTest.PROJECT_PB).build();
        List<Sink> sinkList = ImmutableList.of(new Sink(logging, new SinkInfo.BuilderImpl(LoggingImplTest.SINK_INFO)), new Sink(logging, new SinkInfo.BuilderImpl(LoggingImplTest.SINK_INFO)));
        ListSinksResponse response = ListSinksResponse.newBuilder().setNextPageToken(cursor).addAllSinks(Lists.transform(sinkList, LoggingImplTest.SINK_TO_PB_FUNCTION)).build();
        ApiFuture<ListSinksResponse> futureResponse = ApiFutures.immediateFuture(response);
        EasyMock.expect(loggingRpcMock.list(request)).andReturn(futureResponse);
        EasyMock.replay(loggingRpcMock);
        AsyncPage<Sink> page = logging.listSinksAsync(ListOption.pageSize(42), ListOption.pageToken(cursor)).get();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(sinkList.toArray(), Iterables.toArray(page.getValues(), Sink.class));
    }

    @Test
    public void testCreateMetric() {
        LogMetric metricPb = LoggingImplTest.METRIC_INFO.toPb();
        ApiFuture<LogMetric> response = ApiFutures.immediateFuture(metricPb);
        CreateLogMetricRequest request = CreateLogMetricRequest.newBuilder().setParent(LoggingImplTest.PROJECT_PB).setMetric(metricPb).build();
        EasyMock.expect(loggingRpcMock.create(request)).andReturn(response);
        EasyMock.replay(rpcFactoryMock, loggingRpcMock);
        logging = options.getService();
        Metric metric = logging.create(LoggingImplTest.METRIC_INFO);
        Assert.assertEquals(new Metric(logging, new MetricInfo.BuilderImpl(LoggingImplTest.METRIC_INFO)), metric);
    }

    @Test
    public void testCreateMetricAsync() throws InterruptedException, ExecutionException {
        LogMetric metricPb = LoggingImplTest.METRIC_INFO.toPb();
        ApiFuture<LogMetric> response = ApiFutures.immediateFuture(metricPb);
        CreateLogMetricRequest request = CreateLogMetricRequest.newBuilder().setParent(LoggingImplTest.PROJECT_PB).setMetric(metricPb).build();
        EasyMock.expect(loggingRpcMock.create(request)).andReturn(response);
        EasyMock.replay(rpcFactoryMock, loggingRpcMock);
        logging = options.getService();
        Metric metric = logging.createAsync(LoggingImplTest.METRIC_INFO).get();
        Assert.assertEquals(new Metric(logging, new MetricInfo.BuilderImpl(LoggingImplTest.METRIC_INFO)), metric);
    }

    @Test
    public void testUpdateMetric() {
        LogMetric sinkPb = LoggingImplTest.METRIC_INFO.toPb();
        ApiFuture<LogMetric> response = ApiFutures.immediateFuture(sinkPb);
        UpdateLogMetricRequest request = UpdateLogMetricRequest.newBuilder().setMetricName(LoggingImplTest.METRIC_NAME_PB).setMetric(sinkPb).build();
        EasyMock.expect(loggingRpcMock.update(request)).andReturn(response);
        EasyMock.replay(rpcFactoryMock, loggingRpcMock);
        logging = options.getService();
        Metric sink = logging.update(LoggingImplTest.METRIC_INFO);
        Assert.assertEquals(new Metric(logging, new MetricInfo.BuilderImpl(LoggingImplTest.METRIC_INFO)), sink);
    }

    @Test
    public void testUpdateMetricAsync() throws InterruptedException, ExecutionException {
        LogMetric sinkPb = LoggingImplTest.METRIC_INFO.toPb();
        ApiFuture<LogMetric> response = ApiFutures.immediateFuture(sinkPb);
        UpdateLogMetricRequest request = UpdateLogMetricRequest.newBuilder().setMetricName(LoggingImplTest.METRIC_NAME_PB).setMetric(sinkPb).build();
        EasyMock.expect(loggingRpcMock.update(request)).andReturn(response);
        EasyMock.replay(rpcFactoryMock, loggingRpcMock);
        logging = options.getService();
        Metric sink = logging.updateAsync(LoggingImplTest.METRIC_INFO).get();
        Assert.assertEquals(new Metric(logging, new MetricInfo.BuilderImpl(LoggingImplTest.METRIC_INFO)), sink);
    }

    @Test
    public void testGetMetric() {
        LogMetric sinkPb = LoggingImplTest.METRIC_INFO.toPb();
        ApiFuture<LogMetric> response = ApiFutures.immediateFuture(sinkPb);
        GetLogMetricRequest request = GetLogMetricRequest.newBuilder().setMetricName(LoggingImplTest.METRIC_NAME_PB).build();
        EasyMock.expect(loggingRpcMock.get(request)).andReturn(response);
        EasyMock.replay(rpcFactoryMock, loggingRpcMock);
        logging = options.getService();
        Metric sink = logging.getMetric(LoggingImplTest.METRIC_NAME);
        Assert.assertEquals(new Metric(logging, new MetricInfo.BuilderImpl(LoggingImplTest.METRIC_INFO)), sink);
    }

    @Test
    public void testGetMetric_Null() {
        ApiFuture<LogMetric> response = ApiFutures.immediateFuture(null);
        GetLogMetricRequest request = GetLogMetricRequest.newBuilder().setMetricName(LoggingImplTest.METRIC_NAME_PB).build();
        EasyMock.expect(loggingRpcMock.get(request)).andReturn(response);
        EasyMock.replay(rpcFactoryMock, loggingRpcMock);
        logging = options.getService();
        Assert.assertNull(logging.getMetric(LoggingImplTest.METRIC_NAME));
    }

    @Test
    public void testGetMetricAsync() throws InterruptedException, ExecutionException {
        LogMetric sinkPb = LoggingImplTest.METRIC_INFO.toPb();
        ApiFuture<LogMetric> response = ApiFutures.immediateFuture(sinkPb);
        GetLogMetricRequest request = GetLogMetricRequest.newBuilder().setMetricName(LoggingImplTest.METRIC_NAME_PB).build();
        EasyMock.expect(loggingRpcMock.get(request)).andReturn(response);
        EasyMock.replay(rpcFactoryMock, loggingRpcMock);
        logging = options.getService();
        Metric sink = logging.getMetricAsync(LoggingImplTest.METRIC_NAME).get();
        Assert.assertEquals(new Metric(logging, new MetricInfo.BuilderImpl(LoggingImplTest.METRIC_INFO)), sink);
    }

    @Test
    public void testGetMetricAsync_Null() throws InterruptedException, ExecutionException {
        ApiFuture<LogMetric> response = ApiFutures.immediateFuture(null);
        GetLogMetricRequest request = GetLogMetricRequest.newBuilder().setMetricName(LoggingImplTest.METRIC_NAME_PB).build();
        EasyMock.expect(loggingRpcMock.get(request)).andReturn(response);
        EasyMock.replay(rpcFactoryMock, loggingRpcMock);
        logging = options.getService();
        Assert.assertNull(logging.getMetricAsync(LoggingImplTest.METRIC_NAME).get());
    }

    @Test
    public void testDeleteMetric() {
        DeleteLogMetricRequest request = DeleteLogMetricRequest.newBuilder().setMetricName(LoggingImplTest.METRIC_NAME_PB).build();
        ApiFuture<Empty> response = ApiFutures.immediateFuture(Empty.getDefaultInstance());
        EasyMock.expect(loggingRpcMock.delete(request)).andReturn(response);
        EasyMock.replay(rpcFactoryMock, loggingRpcMock);
        logging = options.getService();
        Assert.assertTrue(logging.deleteMetric(LoggingImplTest.METRIC_NAME));
    }

    @Test
    public void testDeleteMetric_Null() {
        DeleteLogMetricRequest request = DeleteLogMetricRequest.newBuilder().setMetricName(LoggingImplTest.METRIC_NAME_PB).build();
        ApiFuture<Empty> response = ApiFutures.immediateFuture(null);
        EasyMock.expect(loggingRpcMock.delete(request)).andReturn(response);
        EasyMock.replay(rpcFactoryMock, loggingRpcMock);
        logging = options.getService();
        Assert.assertFalse(logging.deleteMetric(LoggingImplTest.METRIC_NAME));
    }

    @Test
    public void testDeleteMetricAsync() throws InterruptedException, ExecutionException {
        DeleteLogMetricRequest request = DeleteLogMetricRequest.newBuilder().setMetricName(LoggingImplTest.METRIC_NAME_PB).build();
        ApiFuture<Empty> response = ApiFutures.immediateFuture(Empty.getDefaultInstance());
        EasyMock.expect(loggingRpcMock.delete(request)).andReturn(response);
        EasyMock.replay(rpcFactoryMock, loggingRpcMock);
        logging = options.getService();
        Assert.assertTrue(logging.deleteMetricAsync(LoggingImplTest.METRIC_NAME).get());
    }

    @Test
    public void testDeleteMetricAsync_Null() throws InterruptedException, ExecutionException {
        DeleteLogMetricRequest request = DeleteLogMetricRequest.newBuilder().setMetricName(LoggingImplTest.METRIC_NAME_PB).build();
        ApiFuture<Empty> response = ApiFutures.immediateFuture(null);
        EasyMock.expect(loggingRpcMock.delete(request)).andReturn(response);
        EasyMock.replay(rpcFactoryMock, loggingRpcMock);
        logging = options.getService();
        Assert.assertFalse(logging.deleteMetricAsync(LoggingImplTest.METRIC_NAME).get());
    }

    @Test
    public void testListMetrics() {
        String cursor = "cursor";
        EasyMock.replay(rpcFactoryMock);
        logging = options.getService();
        ListLogMetricsRequest request = ListLogMetricsRequest.newBuilder().setParent(LoggingImplTest.PROJECT_PB).build();
        List<Metric> sinkList = ImmutableList.of(new Metric(logging, new MetricInfo.BuilderImpl(LoggingImplTest.METRIC_INFO)), new Metric(logging, new MetricInfo.BuilderImpl(LoggingImplTest.METRIC_INFO)));
        ListLogMetricsResponse response = ListLogMetricsResponse.newBuilder().setNextPageToken(cursor).addAllMetrics(Lists.transform(sinkList, LoggingImplTest.METRIC_TO_PB_FUNCTION)).build();
        ApiFuture<ListLogMetricsResponse> futureResponse = ApiFutures.immediateFuture(response);
        EasyMock.expect(loggingRpcMock.list(request)).andReturn(futureResponse);
        EasyMock.replay(loggingRpcMock);
        Page<Metric> page = logging.listMetrics();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(sinkList.toArray(), Iterables.toArray(page.getValues(), Metric.class));
    }

    @Test
    public void testListMetricsNextPage() {
        String cursor1 = "cursor";
        EasyMock.replay(rpcFactoryMock);
        logging = options.getService();
        ListLogMetricsRequest request1 = ListLogMetricsRequest.newBuilder().setParent(LoggingImplTest.PROJECT_PB).build();
        ListLogMetricsRequest request2 = ListLogMetricsRequest.newBuilder().setParent(LoggingImplTest.PROJECT_PB).setPageToken(cursor1).build();
        List<Metric> sinkList1 = ImmutableList.of(new Metric(logging, new MetricInfo.BuilderImpl(LoggingImplTest.METRIC_INFO)), new Metric(logging, new MetricInfo.BuilderImpl(LoggingImplTest.METRIC_INFO)));
        List<Metric> sinkList2 = ImmutableList.of(new Metric(logging, new MetricInfo.BuilderImpl(LoggingImplTest.METRIC_INFO)));
        ListLogMetricsResponse response1 = ListLogMetricsResponse.newBuilder().setNextPageToken(cursor1).addAllMetrics(Lists.transform(sinkList1, LoggingImplTest.METRIC_TO_PB_FUNCTION)).build();
        String cursor2 = "nextCursor";
        ListLogMetricsResponse response2 = ListLogMetricsResponse.newBuilder().setNextPageToken(cursor2).addAllMetrics(Lists.transform(sinkList2, LoggingImplTest.METRIC_TO_PB_FUNCTION)).build();
        ApiFuture<ListLogMetricsResponse> futureResponse1 = ApiFutures.immediateFuture(response1);
        ApiFuture<ListLogMetricsResponse> futureResponse2 = ApiFutures.immediateFuture(response2);
        EasyMock.expect(loggingRpcMock.list(request1)).andReturn(futureResponse1);
        EasyMock.expect(loggingRpcMock.list(request2)).andReturn(futureResponse2);
        EasyMock.replay(loggingRpcMock);
        Page<Metric> page = logging.listMetrics();
        Assert.assertEquals(cursor1, page.getNextPageToken());
        Assert.assertArrayEquals(sinkList1.toArray(), Iterables.toArray(page.getValues(), Metric.class));
        page = page.getNextPage();
        Assert.assertEquals(cursor2, page.getNextPageToken());
        Assert.assertArrayEquals(sinkList2.toArray(), Iterables.toArray(page.getValues(), Metric.class));
    }

    @Test
    public void testListMetricsEmpty() {
        EasyMock.replay(rpcFactoryMock);
        logging = options.getService();
        ListLogMetricsRequest request = ListLogMetricsRequest.newBuilder().setParent(LoggingImplTest.PROJECT_PB).build();
        List<Metric> sinkList = ImmutableList.of();
        ListLogMetricsResponse response = ListLogMetricsResponse.newBuilder().setNextPageToken("").addAllMetrics(Lists.transform(sinkList, LoggingImplTest.METRIC_TO_PB_FUNCTION)).build();
        ApiFuture<ListLogMetricsResponse> futureResponse = ApiFutures.immediateFuture(response);
        EasyMock.expect(loggingRpcMock.list(request)).andReturn(futureResponse);
        EasyMock.replay(loggingRpcMock);
        Page<Metric> page = logging.listMetrics();
        Assert.assertNull(page.getNextPageToken());
        Assert.assertNull(page.getNextPage());
        Assert.assertArrayEquals(sinkList.toArray(), Iterables.toArray(page.getValues(), Metric.class));
    }

    @Test
    public void testListMetricsWithOptions() {
        String cursor = "cursor";
        EasyMock.replay(rpcFactoryMock);
        logging = options.getService();
        ListLogMetricsRequest request = ListLogMetricsRequest.newBuilder().setPageToken(cursor).setPageSize(42).setParent(LoggingImplTest.PROJECT_PB).build();
        List<Metric> sinkList = ImmutableList.of(new Metric(logging, new MetricInfo.BuilderImpl(LoggingImplTest.METRIC_INFO)), new Metric(logging, new MetricInfo.BuilderImpl(LoggingImplTest.METRIC_INFO)));
        ListLogMetricsResponse response = ListLogMetricsResponse.newBuilder().setNextPageToken(cursor).addAllMetrics(Lists.transform(sinkList, LoggingImplTest.METRIC_TO_PB_FUNCTION)).build();
        ApiFuture<ListLogMetricsResponse> futureResponse = ApiFutures.immediateFuture(response);
        EasyMock.expect(loggingRpcMock.list(request)).andReturn(futureResponse);
        EasyMock.replay(loggingRpcMock);
        Page<Metric> page = logging.listMetrics(ListOption.pageSize(42), ListOption.pageToken(cursor));
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(sinkList.toArray(), Iterables.toArray(page.getValues(), Metric.class));
    }

    @Test
    public void testListMetricsAsync() throws InterruptedException, ExecutionException {
        String cursor = "cursor";
        EasyMock.replay(rpcFactoryMock);
        logging = options.getService();
        ListLogMetricsRequest request = ListLogMetricsRequest.newBuilder().setParent(LoggingImplTest.PROJECT_PB).build();
        List<Metric> sinkList = ImmutableList.of(new Metric(logging, new MetricInfo.BuilderImpl(LoggingImplTest.METRIC_INFO)), new Metric(logging, new MetricInfo.BuilderImpl(LoggingImplTest.METRIC_INFO)));
        ListLogMetricsResponse response = ListLogMetricsResponse.newBuilder().setNextPageToken(cursor).addAllMetrics(Lists.transform(sinkList, LoggingImplTest.METRIC_TO_PB_FUNCTION)).build();
        ApiFuture<ListLogMetricsResponse> futureResponse = ApiFutures.immediateFuture(response);
        EasyMock.expect(loggingRpcMock.list(request)).andReturn(futureResponse);
        EasyMock.replay(loggingRpcMock);
        AsyncPage<Metric> page = logging.listMetricsAsync().get();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(sinkList.toArray(), Iterables.toArray(page.getValues(), Metric.class));
    }

    @Test
    public void testListMetricsAsyncNextPage() throws InterruptedException, ExecutionException {
        String cursor1 = "cursor";
        EasyMock.replay(rpcFactoryMock);
        logging = options.getService();
        ListLogMetricsRequest request1 = ListLogMetricsRequest.newBuilder().setParent(LoggingImplTest.PROJECT_PB).build();
        ListLogMetricsRequest request2 = ListLogMetricsRequest.newBuilder().setParent(LoggingImplTest.PROJECT_PB).setPageToken(cursor1).build();
        List<Metric> sinkList1 = ImmutableList.of(new Metric(logging, new MetricInfo.BuilderImpl(LoggingImplTest.METRIC_INFO)), new Metric(logging, new MetricInfo.BuilderImpl(LoggingImplTest.METRIC_INFO)));
        List<Metric> sinkList2 = ImmutableList.of(new Metric(logging, new MetricInfo.BuilderImpl(LoggingImplTest.METRIC_INFO)));
        ListLogMetricsResponse response1 = ListLogMetricsResponse.newBuilder().setNextPageToken(cursor1).addAllMetrics(Lists.transform(sinkList1, LoggingImplTest.METRIC_TO_PB_FUNCTION)).build();
        String cursor2 = "nextCursor";
        ListLogMetricsResponse response2 = ListLogMetricsResponse.newBuilder().setNextPageToken(cursor2).addAllMetrics(Lists.transform(sinkList2, LoggingImplTest.METRIC_TO_PB_FUNCTION)).build();
        ApiFuture<ListLogMetricsResponse> futureResponse1 = ApiFutures.immediateFuture(response1);
        ApiFuture<ListLogMetricsResponse> futureResponse2 = ApiFutures.immediateFuture(response2);
        EasyMock.expect(loggingRpcMock.list(request1)).andReturn(futureResponse1);
        EasyMock.expect(loggingRpcMock.list(request2)).andReturn(futureResponse2);
        EasyMock.replay(loggingRpcMock);
        AsyncPage<Metric> page = logging.listMetricsAsync().get();
        Assert.assertEquals(cursor1, page.getNextPageToken());
        Assert.assertArrayEquals(sinkList1.toArray(), Iterables.toArray(page.getValues(), Metric.class));
        page = page.getNextPageAsync().get();
        Assert.assertEquals(cursor2, page.getNextPageToken());
        Assert.assertArrayEquals(sinkList2.toArray(), Iterables.toArray(page.getValues(), Metric.class));
    }

    @Test
    public void testListMetricsAsyncEmpty() throws InterruptedException, ExecutionException {
        EasyMock.replay(rpcFactoryMock);
        logging = options.getService();
        ListLogMetricsRequest request = ListLogMetricsRequest.newBuilder().setParent(LoggingImplTest.PROJECT_PB).build();
        List<Metric> sinkList = ImmutableList.of();
        ListLogMetricsResponse response = ListLogMetricsResponse.newBuilder().setNextPageToken("").addAllMetrics(Lists.transform(sinkList, LoggingImplTest.METRIC_TO_PB_FUNCTION)).build();
        ApiFuture<ListLogMetricsResponse> futureResponse = ApiFutures.immediateFuture(response);
        EasyMock.expect(loggingRpcMock.list(request)).andReturn(futureResponse);
        EasyMock.replay(loggingRpcMock);
        AsyncPage<Metric> page = logging.listMetricsAsync().get();
        Assert.assertNull(page.getNextPageToken());
        Assert.assertNull(page.getNextPage());
        Assert.assertArrayEquals(sinkList.toArray(), Iterables.toArray(page.getValues(), Metric.class));
    }

    @Test
    public void testListMetricsWithOptionsAsync() throws InterruptedException, ExecutionException {
        String cursor = "cursor";
        EasyMock.replay(rpcFactoryMock);
        logging = options.getService();
        ListLogMetricsRequest request = ListLogMetricsRequest.newBuilder().setPageToken(cursor).setPageSize(42).setParent(LoggingImplTest.PROJECT_PB).build();
        List<Metric> sinkList = ImmutableList.of(new Metric(logging, new MetricInfo.BuilderImpl(LoggingImplTest.METRIC_INFO)), new Metric(logging, new MetricInfo.BuilderImpl(LoggingImplTest.METRIC_INFO)));
        ListLogMetricsResponse response = ListLogMetricsResponse.newBuilder().setNextPageToken(cursor).addAllMetrics(Lists.transform(sinkList, LoggingImplTest.METRIC_TO_PB_FUNCTION)).build();
        ApiFuture<ListLogMetricsResponse> futureResponse = ApiFutures.immediateFuture(response);
        EasyMock.expect(loggingRpcMock.list(request)).andReturn(futureResponse);
        EasyMock.replay(loggingRpcMock);
        AsyncPage<Metric> page = logging.listMetricsAsync(ListOption.pageSize(42), ListOption.pageToken(cursor)).get();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(sinkList.toArray(), Iterables.toArray(page.getValues(), Metric.class));
    }

    @Test
    public void testListResourceDescriptor() {
        String cursor = "cursor";
        EasyMock.replay(rpcFactoryMock);
        logging = options.getService();
        ListMonitoredResourceDescriptorsRequest request = ListMonitoredResourceDescriptorsRequest.newBuilder().build();
        List<MonitoredResourceDescriptor> descriptorList = ImmutableList.of(LoggingImplTest.DESCRIPTOR, LoggingImplTest.DESCRIPTOR);
        ListMonitoredResourceDescriptorsResponse response = ListMonitoredResourceDescriptorsResponse.newBuilder().setNextPageToken(cursor).addAllResourceDescriptors(Lists.transform(descriptorList, LoggingImplTest.DESCRIPTOR_TO_PB_FUNCTION)).build();
        ApiFuture<ListMonitoredResourceDescriptorsResponse> futureResponse = ApiFutures.immediateFuture(response);
        EasyMock.expect(loggingRpcMock.list(request)).andReturn(futureResponse);
        EasyMock.replay(loggingRpcMock);
        Page<MonitoredResourceDescriptor> page = logging.listMonitoredResourceDescriptors();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(descriptorList.toArray(), Iterables.toArray(page.getValues(), MonitoredResourceDescriptor.class));
    }

    @Test
    public void testListResourceDescriptorNextPage() {
        String cursor1 = "cursor";
        EasyMock.replay(rpcFactoryMock);
        logging = options.getService();
        ListMonitoredResourceDescriptorsRequest request1 = ListMonitoredResourceDescriptorsRequest.newBuilder().build();
        ListMonitoredResourceDescriptorsRequest request2 = ListMonitoredResourceDescriptorsRequest.newBuilder().setPageToken(cursor1).build();
        List<MonitoredResourceDescriptor> descriptorList1 = ImmutableList.of(LoggingImplTest.DESCRIPTOR, LoggingImplTest.DESCRIPTOR);
        List<MonitoredResourceDescriptor> descriptorList2 = ImmutableList.of(LoggingImplTest.DESCRIPTOR);
        ListMonitoredResourceDescriptorsResponse response1 = ListMonitoredResourceDescriptorsResponse.newBuilder().setNextPageToken(cursor1).addAllResourceDescriptors(Lists.transform(descriptorList1, LoggingImplTest.DESCRIPTOR_TO_PB_FUNCTION)).build();
        String cursor2 = "nextCursor";
        ListMonitoredResourceDescriptorsResponse response2 = ListMonitoredResourceDescriptorsResponse.newBuilder().setNextPageToken(cursor2).addAllResourceDescriptors(Lists.transform(descriptorList2, LoggingImplTest.DESCRIPTOR_TO_PB_FUNCTION)).build();
        ApiFuture<ListMonitoredResourceDescriptorsResponse> futureResponse1 = ApiFutures.immediateFuture(response1);
        ApiFuture<ListMonitoredResourceDescriptorsResponse> futureResponse2 = ApiFutures.immediateFuture(response2);
        EasyMock.expect(loggingRpcMock.list(request1)).andReturn(futureResponse1);
        EasyMock.expect(loggingRpcMock.list(request2)).andReturn(futureResponse2);
        EasyMock.replay(loggingRpcMock);
        Page<MonitoredResourceDescriptor> page = logging.listMonitoredResourceDescriptors();
        Assert.assertEquals(cursor1, page.getNextPageToken());
        Assert.assertArrayEquals(descriptorList1.toArray(), Iterables.toArray(page.getValues(), MonitoredResourceDescriptor.class));
        page = page.getNextPage();
        Assert.assertEquals(cursor2, page.getNextPageToken());
        Assert.assertArrayEquals(descriptorList2.toArray(), Iterables.toArray(page.getValues(), MonitoredResourceDescriptor.class));
    }

    @Test
    public void testListResourceDescriptorEmpty() {
        EasyMock.replay(rpcFactoryMock);
        logging = options.getService();
        ListMonitoredResourceDescriptorsRequest request = ListMonitoredResourceDescriptorsRequest.newBuilder().build();
        List<MonitoredResourceDescriptor> descriptorList = ImmutableList.of();
        ListMonitoredResourceDescriptorsResponse response = ListMonitoredResourceDescriptorsResponse.newBuilder().setNextPageToken("").addAllResourceDescriptors(Lists.transform(descriptorList, LoggingImplTest.DESCRIPTOR_TO_PB_FUNCTION)).build();
        ApiFuture<ListMonitoredResourceDescriptorsResponse> futureResponse = ApiFutures.immediateFuture(response);
        EasyMock.expect(loggingRpcMock.list(request)).andReturn(futureResponse);
        EasyMock.replay(loggingRpcMock);
        Page<MonitoredResourceDescriptor> page = logging.listMonitoredResourceDescriptors();
        Assert.assertNull(page.getNextPageToken());
        Assert.assertNull(page.getNextPage());
        Assert.assertArrayEquals(descriptorList.toArray(), Iterables.toArray(page.getValues(), MonitoredResourceDescriptor.class));
    }

    @Test
    public void testListResourceDescriptorWithOptions() {
        String cursor = "cursor";
        EasyMock.replay(rpcFactoryMock);
        logging = options.getService();
        ListMonitoredResourceDescriptorsRequest request = ListMonitoredResourceDescriptorsRequest.newBuilder().setPageToken(cursor).setPageSize(42).build();
        List<MonitoredResourceDescriptor> descriptorList = ImmutableList.of(LoggingImplTest.DESCRIPTOR, LoggingImplTest.DESCRIPTOR);
        ListMonitoredResourceDescriptorsResponse response = ListMonitoredResourceDescriptorsResponse.newBuilder().setNextPageToken(cursor).addAllResourceDescriptors(Lists.transform(descriptorList, LoggingImplTest.DESCRIPTOR_TO_PB_FUNCTION)).build();
        ApiFuture<ListMonitoredResourceDescriptorsResponse> futureResponse = ApiFutures.immediateFuture(response);
        EasyMock.expect(loggingRpcMock.list(request)).andReturn(futureResponse);
        EasyMock.replay(loggingRpcMock);
        Page<MonitoredResourceDescriptor> page = logging.listMonitoredResourceDescriptors(ListOption.pageSize(42), ListOption.pageToken(cursor));
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(descriptorList.toArray(), Iterables.toArray(page.getValues(), MonitoredResourceDescriptor.class));
    }

    @Test
    public void testListResourceDescriptorAsync() throws InterruptedException, ExecutionException {
        String cursor = "cursor";
        EasyMock.replay(rpcFactoryMock);
        logging = options.getService();
        ListMonitoredResourceDescriptorsRequest request = ListMonitoredResourceDescriptorsRequest.newBuilder().build();
        List<MonitoredResourceDescriptor> descriptorList = ImmutableList.of(LoggingImplTest.DESCRIPTOR, LoggingImplTest.DESCRIPTOR);
        ListMonitoredResourceDescriptorsResponse response = ListMonitoredResourceDescriptorsResponse.newBuilder().setNextPageToken(cursor).addAllResourceDescriptors(Lists.transform(descriptorList, LoggingImplTest.DESCRIPTOR_TO_PB_FUNCTION)).build();
        ApiFuture<ListMonitoredResourceDescriptorsResponse> futureResponse = ApiFutures.immediateFuture(response);
        EasyMock.expect(loggingRpcMock.list(request)).andReturn(futureResponse);
        EasyMock.replay(loggingRpcMock);
        AsyncPage<MonitoredResourceDescriptor> page = logging.listMonitoredResourceDescriptorsAsync().get();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(descriptorList.toArray(), Iterables.toArray(page.getValues(), MonitoredResourceDescriptor.class));
    }

    @Test
    public void testListResourceDescriptorAsyncNextPage() throws InterruptedException, ExecutionException {
        String cursor1 = "cursor";
        EasyMock.replay(rpcFactoryMock);
        logging = options.getService();
        ListMonitoredResourceDescriptorsRequest request1 = ListMonitoredResourceDescriptorsRequest.newBuilder().build();
        ListMonitoredResourceDescriptorsRequest request2 = ListMonitoredResourceDescriptorsRequest.newBuilder().setPageToken(cursor1).build();
        List<MonitoredResourceDescriptor> descriptorList1 = ImmutableList.of(LoggingImplTest.DESCRIPTOR, LoggingImplTest.DESCRIPTOR);
        List<MonitoredResourceDescriptor> descriptorList2 = ImmutableList.of(LoggingImplTest.DESCRIPTOR);
        ListMonitoredResourceDescriptorsResponse response1 = ListMonitoredResourceDescriptorsResponse.newBuilder().setNextPageToken(cursor1).addAllResourceDescriptors(Lists.transform(descriptorList1, LoggingImplTest.DESCRIPTOR_TO_PB_FUNCTION)).build();
        String cursor2 = "nextCursor";
        ListMonitoredResourceDescriptorsResponse response2 = ListMonitoredResourceDescriptorsResponse.newBuilder().setNextPageToken(cursor2).addAllResourceDescriptors(Lists.transform(descriptorList2, LoggingImplTest.DESCRIPTOR_TO_PB_FUNCTION)).build();
        ApiFuture<ListMonitoredResourceDescriptorsResponse> futureResponse1 = ApiFutures.immediateFuture(response1);
        ApiFuture<ListMonitoredResourceDescriptorsResponse> futureResponse2 = ApiFutures.immediateFuture(response2);
        EasyMock.expect(loggingRpcMock.list(request1)).andReturn(futureResponse1);
        EasyMock.expect(loggingRpcMock.list(request2)).andReturn(futureResponse2);
        EasyMock.replay(loggingRpcMock);
        AsyncPage<MonitoredResourceDescriptor> page = logging.listMonitoredResourceDescriptorsAsync().get();
        Assert.assertEquals(cursor1, page.getNextPageToken());
        Assert.assertArrayEquals(descriptorList1.toArray(), Iterables.toArray(page.getValues(), MonitoredResourceDescriptor.class));
        page = page.getNextPageAsync().get();
        Assert.assertEquals(cursor2, page.getNextPageToken());
        Assert.assertArrayEquals(descriptorList2.toArray(), Iterables.toArray(page.getValues(), MonitoredResourceDescriptor.class));
    }

    @Test
    public void testListResourceDescriptorAsyncEmpty() throws InterruptedException, ExecutionException {
        EasyMock.replay(rpcFactoryMock);
        logging = options.getService();
        ListMonitoredResourceDescriptorsRequest request = ListMonitoredResourceDescriptorsRequest.newBuilder().build();
        List<MonitoredResourceDescriptor> descriptorList = ImmutableList.of();
        ListMonitoredResourceDescriptorsResponse response = ListMonitoredResourceDescriptorsResponse.newBuilder().setNextPageToken("").addAllResourceDescriptors(Lists.transform(descriptorList, LoggingImplTest.DESCRIPTOR_TO_PB_FUNCTION)).build();
        ApiFuture<ListMonitoredResourceDescriptorsResponse> futureResponse = ApiFutures.immediateFuture(response);
        EasyMock.expect(loggingRpcMock.list(request)).andReturn(futureResponse);
        EasyMock.replay(loggingRpcMock);
        AsyncPage<MonitoredResourceDescriptor> page = logging.listMonitoredResourceDescriptorsAsync().get();
        Assert.assertNull(page.getNextPageToken());
        Assert.assertNull(page.getNextPage());
        Assert.assertArrayEquals(descriptorList.toArray(), Iterables.toArray(page.getValues(), MonitoredResourceDescriptor.class));
    }

    @Test
    public void testListResourceDescriptorAsyncWithOptions() throws InterruptedException, ExecutionException {
        String cursor = "cursor";
        EasyMock.replay(rpcFactoryMock);
        logging = options.getService();
        ListMonitoredResourceDescriptorsRequest request = ListMonitoredResourceDescriptorsRequest.newBuilder().setPageToken(cursor).setPageSize(42).build();
        List<MonitoredResourceDescriptor> descriptorList = ImmutableList.of(LoggingImplTest.DESCRIPTOR, LoggingImplTest.DESCRIPTOR);
        ListMonitoredResourceDescriptorsResponse response = ListMonitoredResourceDescriptorsResponse.newBuilder().setNextPageToken(cursor).addAllResourceDescriptors(Lists.transform(descriptorList, LoggingImplTest.DESCRIPTOR_TO_PB_FUNCTION)).build();
        ApiFuture<ListMonitoredResourceDescriptorsResponse> futureResponse = ApiFutures.immediateFuture(response);
        EasyMock.expect(loggingRpcMock.list(request)).andReturn(futureResponse);
        EasyMock.replay(loggingRpcMock);
        AsyncPage<MonitoredResourceDescriptor> page = logging.listMonitoredResourceDescriptorsAsync(ListOption.pageSize(42), ListOption.pageToken(cursor)).get();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(descriptorList.toArray(), Iterables.toArray(page.getValues(), MonitoredResourceDescriptor.class));
    }

    @Test
    public void testDeleteLog() {
        DeleteLogRequest request = DeleteLogRequest.newBuilder().setLogName(LoggingImplTest.LOG_NAME_PB).build();
        ApiFuture<Empty> response = ApiFutures.immediateFuture(Empty.getDefaultInstance());
        EasyMock.expect(loggingRpcMock.delete(request)).andReturn(response);
        EasyMock.replay(rpcFactoryMock, loggingRpcMock);
        logging = options.getService();
        Assert.assertTrue(logging.deleteLog(LoggingImplTest.LOG_NAME));
    }

    @Test
    public void testDeleteLog_Null() {
        DeleteLogRequest request = DeleteLogRequest.newBuilder().setLogName(LoggingImplTest.LOG_NAME_PB).build();
        EasyMock.expect(loggingRpcMock.delete(request)).andReturn(ApiFutures.<Empty>immediateFuture(null));
        EasyMock.replay(rpcFactoryMock, loggingRpcMock);
        logging = options.getService();
        Assert.assertFalse(logging.deleteLog(LoggingImplTest.LOG_NAME));
    }

    @Test
    public void testDeleteLogAync() throws InterruptedException, ExecutionException {
        DeleteLogRequest request = DeleteLogRequest.newBuilder().setLogName(LoggingImplTest.LOG_NAME_PB).build();
        ApiFuture<Empty> response = ApiFutures.immediateFuture(Empty.getDefaultInstance());
        EasyMock.expect(loggingRpcMock.delete(request)).andReturn(response);
        EasyMock.replay(rpcFactoryMock, loggingRpcMock);
        logging = options.getService();
        Assert.assertTrue(logging.deleteLogAsync(LoggingImplTest.LOG_NAME).get());
    }

    @Test
    public void testDeleteLogAsync_Null() throws InterruptedException, ExecutionException {
        DeleteLogRequest request = DeleteLogRequest.newBuilder().setLogName(LoggingImplTest.LOG_NAME_PB).build();
        EasyMock.expect(loggingRpcMock.delete(request)).andReturn(ApiFutures.<Empty>immediateFuture(null));
        EasyMock.replay(rpcFactoryMock, loggingRpcMock);
        logging = options.getService();
        Assert.assertFalse(logging.deleteLogAsync(LoggingImplTest.LOG_NAME).get());
    }

    @Test
    public void testWriteLogEntries() {
        WriteLogEntriesRequest request = WriteLogEntriesRequest.newBuilder().addAllEntries(Iterables.transform(ImmutableList.of(LoggingImplTest.LOG_ENTRY1, LoggingImplTest.LOG_ENTRY2), LogEntry.toPbFunction(LoggingImplTest.PROJECT))).build();
        WriteLogEntriesResponse response = WriteLogEntriesResponse.newBuilder().build();
        EasyMock.expect(loggingRpcMock.write(request)).andReturn(ApiFutures.immediateFuture(response));
        EasyMock.replay(rpcFactoryMock, loggingRpcMock);
        logging = options.getService();
        logging.write(ImmutableList.of(LoggingImplTest.LOG_ENTRY1, LoggingImplTest.LOG_ENTRY2));
    }

    @Test
    public void testWriteLogEntriesDoesnotEnableFlushByDefault() {
        WriteLogEntriesRequest request = WriteLogEntriesRequest.newBuilder().addAllEntries(Iterables.transform(ImmutableList.of(LoggingImplTest.LOG_ENTRY1, LoggingImplTest.LOG_ENTRY2.toBuilder().setSeverity(EMERGENCY).build()), LogEntry.toPbFunction(LoggingImplTest.PROJECT))).build();
        ApiFuture<WriteLogEntriesResponse> apiFuture = SettableApiFuture.create();
        EasyMock.expect(loggingRpcMock.write(request)).andReturn(apiFuture);
        EasyMock.replay(rpcFactoryMock, loggingRpcMock);
        logging = options.getService();
        logging.write(ImmutableList.of(LoggingImplTest.LOG_ENTRY1, LoggingImplTest.LOG_ENTRY2.toBuilder().setSeverity(EMERGENCY).build()));
    }

    @Test
    public void testWriteLogEntriesWithSeverityFlushEnabled() {
        WriteLogEntriesRequest request = WriteLogEntriesRequest.newBuilder().addAllEntries(Iterables.transform(ImmutableList.of(LoggingImplTest.LOG_ENTRY1, LoggingImplTest.LOG_ENTRY2), LogEntry.toPbFunction(LoggingImplTest.PROJECT))).build();
        WriteLogEntriesResponse response = WriteLogEntriesResponse.newBuilder().build();
        EasyMock.expect(loggingRpcMock.write(request)).andReturn(ApiFutures.immediateFuture(response));
        EasyMock.replay(rpcFactoryMock, loggingRpcMock);
        logging = options.getService();
        logging.setFlushSeverity(DEFAULT);
        logging.write(ImmutableList.of(LoggingImplTest.LOG_ENTRY1, LoggingImplTest.LOG_ENTRY2));
    }

    @Test
    public void testWriteLogEntriesWithOptions() {
        Map<String, String> labels = ImmutableMap.of("key", "value");
        WriteLogEntriesRequest request = WriteLogEntriesRequest.newBuilder().putAllLabels(labels).setLogName(LoggingImplTest.LOG_NAME_PB).setResource(LoggingImplTest.MONITORED_RESOURCE.toPb()).addAllEntries(Iterables.transform(ImmutableList.of(LoggingImplTest.LOG_ENTRY1, LoggingImplTest.LOG_ENTRY2), LogEntry.toPbFunction(LoggingImplTest.PROJECT))).build();
        WriteLogEntriesResponse response = WriteLogEntriesResponse.newBuilder().build();
        EasyMock.expect(loggingRpcMock.write(request)).andReturn(ApiFutures.immediateFuture(response));
        EasyMock.replay(rpcFactoryMock, loggingRpcMock);
        logging = options.getService();
        logging.write(ImmutableList.of(LoggingImplTest.LOG_ENTRY1, LoggingImplTest.LOG_ENTRY2), WriteOption.logName(LoggingImplTest.LOG_NAME), WriteOption.resource(LoggingImplTest.MONITORED_RESOURCE), WriteOption.labels(labels));
    }

    @Test
    public void testWriteLogEntriesAsync() throws InterruptedException, ExecutionException {
        WriteLogEntriesRequest request = WriteLogEntriesRequest.newBuilder().addAllEntries(Iterables.transform(ImmutableList.of(LoggingImplTest.LOG_ENTRY1, LoggingImplTest.LOG_ENTRY2), LogEntry.toPbFunction(LoggingImplTest.PROJECT))).build();
        WriteLogEntriesResponse response = WriteLogEntriesResponse.newBuilder().build();
        EasyMock.expect(loggingRpcMock.write(request)).andReturn(ApiFutures.immediateFuture(response));
        EasyMock.replay(rpcFactoryMock, loggingRpcMock);
        logging = options.getService();
        logging.write(ImmutableList.of(LoggingImplTest.LOG_ENTRY1, LoggingImplTest.LOG_ENTRY2));
        logging.flush();
    }

    @Test
    public void testWriteLogEntriesAsyncWithOptions() {
        Map<String, String> labels = ImmutableMap.of("key", "value");
        WriteLogEntriesRequest request = WriteLogEntriesRequest.newBuilder().putAllLabels(labels).setLogName(LoggingImplTest.LOG_NAME_PB).setResource(LoggingImplTest.MONITORED_RESOURCE.toPb()).addAllEntries(Iterables.transform(ImmutableList.of(LoggingImplTest.LOG_ENTRY1, LoggingImplTest.LOG_ENTRY2), LogEntry.toPbFunction(LoggingImplTest.PROJECT))).build();
        WriteLogEntriesResponse response = WriteLogEntriesResponse.newBuilder().build();
        EasyMock.expect(loggingRpcMock.write(request)).andReturn(ApiFutures.immediateFuture(response));
        EasyMock.replay(rpcFactoryMock, loggingRpcMock);
        logging = options.getService();
        logging.write(ImmutableList.of(LoggingImplTest.LOG_ENTRY1, LoggingImplTest.LOG_ENTRY2), WriteOption.logName(LoggingImplTest.LOG_NAME), WriteOption.resource(LoggingImplTest.MONITORED_RESOURCE), WriteOption.labels(labels));
        logging.flush();
    }

    @Test
    public void testListLogEntries() {
        String cursor = "cursor";
        EasyMock.replay(rpcFactoryMock);
        logging = options.getService();
        ListLogEntriesRequest request = ListLogEntriesRequest.newBuilder().addProjectIds(LoggingImplTest.PROJECT).build();
        List<LogEntry> entriesList = ImmutableList.of(LoggingImplTest.LOG_ENTRY1, LoggingImplTest.LOG_ENTRY2);
        ListLogEntriesResponse response = ListLogEntriesResponse.newBuilder().setNextPageToken(cursor).addAllEntries(Lists.transform(entriesList, LogEntry.toPbFunction(LoggingImplTest.PROJECT))).build();
        ApiFuture<ListLogEntriesResponse> futureResponse = ApiFutures.immediateFuture(response);
        EasyMock.expect(loggingRpcMock.list(request)).andReturn(futureResponse);
        EasyMock.replay(loggingRpcMock);
        Page<LogEntry> page = logging.listLogEntries();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(entriesList.toArray(), Iterables.toArray(page.getValues(), LogEntry.class));
    }

    @Test
    public void testListLogEntriesNextPage() throws InterruptedException, ExecutionException {
        String cursor1 = "cursor";
        EasyMock.replay(rpcFactoryMock);
        logging = options.getService();
        ListLogEntriesRequest request1 = ListLogEntriesRequest.newBuilder().addProjectIds(LoggingImplTest.PROJECT).build();
        ListLogEntriesRequest request2 = ListLogEntriesRequest.newBuilder().addProjectIds(LoggingImplTest.PROJECT).setPageToken(cursor1).build();
        List<LogEntry> descriptorList1 = ImmutableList.of(LoggingImplTest.LOG_ENTRY1, LoggingImplTest.LOG_ENTRY2);
        List<LogEntry> descriptorList2 = ImmutableList.of(LoggingImplTest.LOG_ENTRY1);
        ListLogEntriesResponse response1 = ListLogEntriesResponse.newBuilder().setNextPageToken(cursor1).addAllEntries(Lists.transform(descriptorList1, LogEntry.toPbFunction(LoggingImplTest.PROJECT))).build();
        String cursor2 = "nextCursor";
        ListLogEntriesResponse response2 = ListLogEntriesResponse.newBuilder().setNextPageToken(cursor2).addAllEntries(Lists.transform(descriptorList2, LogEntry.toPbFunction(LoggingImplTest.PROJECT))).build();
        ApiFuture<ListLogEntriesResponse> futureResponse1 = ApiFutures.immediateFuture(response1);
        ApiFuture<ListLogEntriesResponse> futureResponse2 = ApiFutures.immediateFuture(response2);
        EasyMock.expect(loggingRpcMock.list(request1)).andReturn(futureResponse1);
        EasyMock.expect(loggingRpcMock.list(request2)).andReturn(futureResponse2);
        EasyMock.replay(loggingRpcMock);
        AsyncPage<LogEntry> page = logging.listLogEntriesAsync().get();
        Assert.assertEquals(cursor1, page.getNextPageToken());
        Assert.assertArrayEquals(descriptorList1.toArray(), Iterables.toArray(page.getValues(), LogEntry.class));
        page = page.getNextPageAsync().get();
        Assert.assertEquals(cursor2, page.getNextPageToken());
        Assert.assertArrayEquals(descriptorList2.toArray(), Iterables.toArray(page.getValues(), LogEntry.class));
    }

    @Test
    public void testListLogEntriesEmpty() {
        String cursor = "cursor";
        EasyMock.replay(rpcFactoryMock);
        logging = options.getService();
        ListLogEntriesRequest request = ListLogEntriesRequest.newBuilder().addProjectIds(LoggingImplTest.PROJECT).build();
        List<LogEntry> entriesList = ImmutableList.of();
        ListLogEntriesResponse response = ListLogEntriesResponse.newBuilder().setNextPageToken(cursor).addAllEntries(Lists.transform(entriesList, LogEntry.toPbFunction(LoggingImplTest.PROJECT))).build();
        ApiFuture<ListLogEntriesResponse> futureResponse = ApiFutures.immediateFuture(response);
        EasyMock.expect(loggingRpcMock.list(request)).andReturn(futureResponse);
        EasyMock.replay(loggingRpcMock);
        Page<LogEntry> page = logging.listLogEntries();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(entriesList.toArray(), Iterables.toArray(page.getValues(), LogEntry.class));
    }

    @Test
    public void testListLogEntriesWithOptions() {
        String cursor = "cursor";
        EasyMock.replay(rpcFactoryMock);
        logging = options.getService();
        ListLogEntriesRequest request = ListLogEntriesRequest.newBuilder().addProjectIds(LoggingImplTest.PROJECT).setOrderBy("timestamp desc").setFilter("logName:syslog").build();
        List<LogEntry> entriesList = ImmutableList.of(LoggingImplTest.LOG_ENTRY1, LoggingImplTest.LOG_ENTRY2);
        ListLogEntriesResponse response = ListLogEntriesResponse.newBuilder().setNextPageToken(cursor).addAllEntries(Lists.transform(entriesList, LogEntry.toPbFunction(LoggingImplTest.PROJECT))).build();
        ApiFuture<ListLogEntriesResponse> futureResponse = ApiFutures.immediateFuture(response);
        EasyMock.expect(loggingRpcMock.list(request)).andReturn(futureResponse);
        EasyMock.replay(loggingRpcMock);
        Page<LogEntry> page = logging.listLogEntries(EntryListOption.filter("logName:syslog"), EntryListOption.sortOrder(TIMESTAMP, DESCENDING));
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(entriesList.toArray(), Iterables.toArray(page.getValues(), LogEntry.class));
    }

    @Test
    public void testListLogEntriesAsync() throws InterruptedException, ExecutionException {
        String cursor = "cursor";
        EasyMock.replay(rpcFactoryMock);
        logging = options.getService();
        ListLogEntriesRequest request = ListLogEntriesRequest.newBuilder().addProjectIds(LoggingImplTest.PROJECT).build();
        List<LogEntry> entriesList = ImmutableList.of(LoggingImplTest.LOG_ENTRY1, LoggingImplTest.LOG_ENTRY2);
        ListLogEntriesResponse response = ListLogEntriesResponse.newBuilder().setNextPageToken(cursor).addAllEntries(Lists.transform(entriesList, LogEntry.toPbFunction(LoggingImplTest.PROJECT))).build();
        ApiFuture<ListLogEntriesResponse> futureResponse = ApiFutures.immediateFuture(response);
        EasyMock.expect(loggingRpcMock.list(request)).andReturn(futureResponse);
        EasyMock.replay(loggingRpcMock);
        AsyncPage<LogEntry> page = logging.listLogEntriesAsync().get();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(entriesList.toArray(), Iterables.toArray(page.getValues(), LogEntry.class));
    }

    @Test
    public void testListLogEntriesAsyncNextPage() {
        String cursor1 = "cursor";
        EasyMock.replay(rpcFactoryMock);
        logging = options.getService();
        ListLogEntriesRequest request1 = ListLogEntriesRequest.newBuilder().addProjectIds(LoggingImplTest.PROJECT).build();
        ListLogEntriesRequest request2 = ListLogEntriesRequest.newBuilder().addProjectIds(LoggingImplTest.PROJECT).setPageToken(cursor1).build();
        List<LogEntry> descriptorList1 = ImmutableList.of(LoggingImplTest.LOG_ENTRY1, LoggingImplTest.LOG_ENTRY2);
        List<LogEntry> descriptorList2 = ImmutableList.of(LoggingImplTest.LOG_ENTRY1);
        ListLogEntriesResponse response1 = ListLogEntriesResponse.newBuilder().setNextPageToken(cursor1).addAllEntries(Lists.transform(descriptorList1, LogEntry.toPbFunction(LoggingImplTest.PROJECT))).build();
        String cursor2 = "nextCursor";
        ListLogEntriesResponse response2 = ListLogEntriesResponse.newBuilder().setNextPageToken(cursor2).addAllEntries(Lists.transform(descriptorList2, LogEntry.toPbFunction(LoggingImplTest.PROJECT))).build();
        ApiFuture<ListLogEntriesResponse> futureResponse1 = ApiFutures.immediateFuture(response1);
        ApiFuture<ListLogEntriesResponse> futureResponse2 = ApiFutures.immediateFuture(response2);
        EasyMock.expect(loggingRpcMock.list(request1)).andReturn(futureResponse1);
        EasyMock.expect(loggingRpcMock.list(request2)).andReturn(futureResponse2);
        EasyMock.replay(loggingRpcMock);
        Page<LogEntry> page = logging.listLogEntries();
        Assert.assertEquals(cursor1, page.getNextPageToken());
        Assert.assertArrayEquals(descriptorList1.toArray(), Iterables.toArray(page.getValues(), LogEntry.class));
        page = page.getNextPage();
        Assert.assertEquals(cursor2, page.getNextPageToken());
        Assert.assertArrayEquals(descriptorList2.toArray(), Iterables.toArray(page.getValues(), LogEntry.class));
    }

    @Test
    public void testListLogEntriesAyncEmpty() throws InterruptedException, ExecutionException {
        String cursor = "cursor";
        EasyMock.replay(rpcFactoryMock);
        logging = options.getService();
        ListLogEntriesRequest request = ListLogEntriesRequest.newBuilder().addProjectIds(LoggingImplTest.PROJECT).build();
        List<LogEntry> entriesList = ImmutableList.of();
        ListLogEntriesResponse response = ListLogEntriesResponse.newBuilder().setNextPageToken(cursor).addAllEntries(Lists.transform(entriesList, LogEntry.toPbFunction(LoggingImplTest.PROJECT))).build();
        ApiFuture<ListLogEntriesResponse> futureResponse = ApiFutures.immediateFuture(response);
        EasyMock.expect(loggingRpcMock.list(request)).andReturn(futureResponse);
        EasyMock.replay(loggingRpcMock);
        AsyncPage<LogEntry> page = logging.listLogEntriesAsync().get();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(entriesList.toArray(), Iterables.toArray(page.getValues(), LogEntry.class));
    }

    @Test
    public void testListLogEntriesAsyncWithOptions() throws InterruptedException, ExecutionException {
        String cursor = "cursor";
        EasyMock.replay(rpcFactoryMock);
        logging = options.getService();
        ListLogEntriesRequest request = ListLogEntriesRequest.newBuilder().addProjectIds(LoggingImplTest.PROJECT).setOrderBy("timestamp desc").setFilter("logName:syslog").build();
        List<LogEntry> entriesList = ImmutableList.of(LoggingImplTest.LOG_ENTRY1, LoggingImplTest.LOG_ENTRY2);
        ListLogEntriesResponse response = ListLogEntriesResponse.newBuilder().setNextPageToken(cursor).addAllEntries(Lists.transform(entriesList, LogEntry.toPbFunction(LoggingImplTest.PROJECT))).build();
        ApiFuture<ListLogEntriesResponse> futureResponse = ApiFutures.immediateFuture(response);
        EasyMock.expect(loggingRpcMock.list(request)).andReturn(futureResponse);
        EasyMock.replay(loggingRpcMock);
        AsyncPage<LogEntry> page = logging.listLogEntriesAsync(EntryListOption.filter("logName:syslog"), EntryListOption.sortOrder(TIMESTAMP, DESCENDING)).get();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(entriesList.toArray(), Iterables.toArray(page.getValues(), LogEntry.class));
    }

    @Test
    public void testFlush() throws InterruptedException {
        SettableApiFuture<WriteLogEntriesResponse> mockRpcResponse = SettableApiFuture.create();
        replay(rpcFactoryMock);
        logging = options.getService();
        WriteLogEntriesRequest request = WriteLogEntriesRequest.newBuilder().addAllEntries(Iterables.transform(ImmutableList.of(LoggingImplTest.LOG_ENTRY1, LoggingImplTest.LOG_ENTRY2), LogEntry.toPbFunction(LoggingImplTest.PROJECT))).build();
        EasyMock.expect(loggingRpcMock.write(request)).andReturn(mockRpcResponse);
        EasyMock.replay(loggingRpcMock);
        // no messages, nothing to flush.
        logging.flush();
        // send a message
        logging.write(ImmutableList.of(LoggingImplTest.LOG_ENTRY1, LoggingImplTest.LOG_ENTRY2));
        Thread flushWaiter = new Thread(new Runnable() {
            @Override
            public void run() {
                logging.flush();
            }
        });
        flushWaiter.start();
        // flushWaiter should be waiting for mockRpc to complete.
        flushWaiter.join(1000);
        Assert.assertTrue(flushWaiter.isAlive());
        // With the RPC completed, flush should return, and the thread should terminate.
        mockRpcResponse.set(null);
        flushWaiter.join(1000);
        Assert.assertFalse(flushWaiter.isAlive());
    }

    @Test
    public void testFlushStress() throws InterruptedException {
        SettableApiFuture<WriteLogEntriesResponse> mockRpcResponse = SettableApiFuture.create();
        mockRpcResponse.set(null);
        replay(rpcFactoryMock);
        logging = options.getService();
        WriteLogEntriesRequest request = WriteLogEntriesRequest.newBuilder().addAllEntries(Iterables.transform(ImmutableList.of(LoggingImplTest.LOG_ENTRY1), LogEntry.toPbFunction(LoggingImplTest.PROJECT))).build();
        Thread[] threads = new Thread[100];
        EasyMock.expect(loggingRpcMock.write(request)).andReturn(mockRpcResponse).times(threads.length);
        EasyMock.replay(loggingRpcMock);
        // log and flush concurrently in many threads to trigger a ConcurrentModificationException
        final AtomicInteger exceptions = new AtomicInteger(0);
        for (int i = 0; i < (threads.length); i++) {
            threads[i] = new Thread() {
                @Override
                public void run() {
                    try {
                        logging.write(ImmutableList.of(LoggingImplTest.LOG_ENTRY1));
                        logging.flush();
                    } catch (Exception ex) {
                        exceptions.incrementAndGet();
                    }
                }
            };
            threads[i].start();
        }
        for (int i = 0; i < (threads.length); i++) {
            threads[i].join();
        }
        Assert.assertSame(0, exceptions.get());
    }
}

