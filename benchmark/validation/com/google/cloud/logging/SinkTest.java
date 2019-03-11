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


import com.google.api.core.ApiFutures;
import com.google.cloud.logging.SinkInfo.Destination.BucketDestination;
import com.google.cloud.logging.SinkInfo.Destination.DatasetDestination;
import com.google.cloud.logging.SinkInfo.VersionFormat;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;


public class SinkTest {
    private static final String NAME = "sink";

    private static final String FILTER = "logName=projects/my-projectid/logs/syslog AND severity>=ERROR";

    private static final VersionFormat VERSION = VersionFormat.V1;

    private static final String NEW_NAME = "newSink";

    private static final String NEW_FILTER = "logName=projects/my-projectid/logs/syslog";

    private static final VersionFormat NEW_VERSION = VersionFormat.V2;

    private static final BucketDestination BUCKET_DESTINATION = BucketDestination.of("bucket");

    private static final DatasetDestination DATASET_DESTINATION = DatasetDestination.of("dataset");

    private static final SinkInfo SINK_INFO = SinkInfo.newBuilder(SinkTest.NAME, SinkTest.BUCKET_DESTINATION).setFilter(SinkTest.FILTER).setVersionFormat(SinkTest.VERSION).build();

    private final Logging serviceMockReturnsOptions = createStrictMock(Logging.class);

    private final LoggingOptions mockOptions = createMock(LoggingOptions.class);

    private Logging logging;

    private Sink expectedSink;

    private Sink sink;

    @Test
    public void testBuilder() {
        initializeExpectedSink(2);
        replay(logging);
        Sink builtSink = expectedSink.toBuilder().setName(SinkTest.NEW_NAME).setFilter(SinkTest.NEW_FILTER).setDestination(SinkTest.DATASET_DESTINATION).setVersionFormat(SinkTest.NEW_VERSION).build();
        Assert.assertEquals(SinkTest.NEW_NAME, builtSink.getName());
        Assert.assertEquals(SinkTest.DATASET_DESTINATION, builtSink.getDestination());
        Assert.assertEquals(SinkTest.NEW_FILTER, builtSink.getFilter());
        Assert.assertEquals(SinkTest.NEW_VERSION, builtSink.getVersionFormat());
    }

    @Test
    public void testToBuilder() {
        initializeExpectedSink(2);
        replay(logging);
        compareSink(expectedSink, expectedSink.toBuilder().build());
    }

    @Test
    public void testReload() {
        initializeExpectedSink(2);
        SinkInfo updatedInfo = SinkTest.SINK_INFO.toBuilder().setFilter(SinkTest.NEW_FILTER).build();
        Sink expectedSink = new Sink(serviceMockReturnsOptions, new SinkInfo.BuilderImpl(updatedInfo));
        expect(logging.getOptions()).andReturn(mockOptions);
        expect(logging.getSink(SinkTest.NAME)).andReturn(expectedSink);
        replay(logging);
        initializeSink();
        Sink updatedSink = sink.reload();
        compareSink(expectedSink, updatedSink);
    }

    @Test
    public void testReloadNull() {
        initializeExpectedSink(1);
        expect(logging.getOptions()).andReturn(mockOptions);
        expect(logging.getSink(SinkTest.NAME)).andReturn(null);
        replay(logging);
        initializeSink();
        Assert.assertNull(sink.reload());
    }

    @Test
    public void testReloadAsync() throws InterruptedException, ExecutionException {
        initializeExpectedSink(2);
        SinkInfo updatedInfo = SinkTest.SINK_INFO.toBuilder().setFilter(SinkTest.NEW_FILTER).build();
        Sink expectedSink = new Sink(serviceMockReturnsOptions, new SinkInfo.BuilderImpl(updatedInfo));
        expect(logging.getOptions()).andReturn(mockOptions);
        expect(logging.getSinkAsync(SinkTest.NAME)).andReturn(ApiFutures.immediateFuture(expectedSink));
        replay(logging);
        initializeSink();
        Sink updatedSink = sink.reloadAsync().get();
        compareSink(expectedSink, updatedSink);
    }

    @Test
    public void testReloadAsyncNull() throws InterruptedException, ExecutionException {
        initializeExpectedSink(1);
        expect(logging.getOptions()).andReturn(mockOptions);
        expect(logging.getSinkAsync(SinkTest.NAME)).andReturn(ApiFutures.<Sink>immediateFuture(null));
        replay(logging);
        initializeSink();
        Assert.assertNull(sink.reloadAsync().get());
    }

    @Test
    public void testUpdate() {
        initializeExpectedSink(2);
        SinkInfo updatedInfo = SinkTest.SINK_INFO.toBuilder().setFilter(SinkTest.NEW_FILTER).build();
        Sink expectedSink = new Sink(serviceMockReturnsOptions, new SinkInfo.BuilderImpl(updatedInfo));
        expect(logging.getOptions()).andReturn(mockOptions).times(2);
        expect(logging.update(expectedSink)).andReturn(expectedSink);
        replay(logging);
        initializeSink();
        Sink updatedSink = sink.toBuilder().setFilter(SinkTest.NEW_FILTER).build().update();
        compareSink(expectedSink, updatedSink);
    }

    @Test
    public void testUpdateAsync() throws InterruptedException, ExecutionException {
        initializeExpectedSink(2);
        SinkInfo updatedInfo = SinkTest.SINK_INFO.toBuilder().setFilter(SinkTest.NEW_FILTER).build();
        Sink expectedSink = new Sink(serviceMockReturnsOptions, new SinkInfo.BuilderImpl(updatedInfo));
        expect(logging.getOptions()).andReturn(mockOptions).times(2);
        expect(logging.updateAsync(expectedSink)).andReturn(ApiFutures.immediateFuture(expectedSink));
        replay(logging);
        initializeSink();
        Sink updatedSink = sink.toBuilder().setFilter(SinkTest.NEW_FILTER).build().updateAsync().get();
        compareSink(expectedSink, updatedSink);
    }

    @Test
    public void testDeleteTrue() {
        initializeExpectedSink(1);
        expect(logging.getOptions()).andReturn(mockOptions);
        expect(logging.deleteSink(SinkTest.NAME)).andReturn(true);
        replay(logging);
        initializeSink();
        Assert.assertTrue(sink.delete());
    }

    @Test
    public void testDeleteFalse() {
        initializeExpectedSink(1);
        expect(logging.getOptions()).andReturn(mockOptions);
        expect(logging.deleteSink(SinkTest.NAME)).andReturn(false);
        replay(logging);
        initializeSink();
        Assert.assertFalse(sink.delete());
    }

    @Test
    public void testDeleteAsyncTrue() throws InterruptedException, ExecutionException {
        initializeExpectedSink(1);
        expect(logging.getOptions()).andReturn(mockOptions);
        expect(logging.deleteSinkAsync(SinkTest.NAME)).andReturn(ApiFutures.immediateFuture(true));
        replay(logging);
        initializeSink();
        Assert.assertTrue(sink.deleteAsync().get());
    }

    @Test
    public void testDeleteAsyncFalse() throws InterruptedException, ExecutionException {
        initializeExpectedSink(1);
        expect(logging.getOptions()).andReturn(mockOptions);
        expect(logging.deleteSinkAsync(SinkTest.NAME)).andReturn(ApiFutures.immediateFuture(false));
        replay(logging);
        initializeSink();
        Assert.assertFalse(sink.deleteAsync().get());
    }
}

