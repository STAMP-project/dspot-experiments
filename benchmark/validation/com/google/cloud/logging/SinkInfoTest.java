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


import Destination.Type.BUCKET;
import Destination.Type.DATASET;
import Destination.Type.TOPIC;
import VersionFormat.V1;
import VersionFormat.V2;
import com.google.cloud.logging.SinkInfo.Destination;
import com.google.cloud.logging.SinkInfo.Destination.BucketDestination;
import com.google.cloud.logging.SinkInfo.Destination.DatasetDestination;
import com.google.cloud.logging.SinkInfo.Destination.TopicDestination;
import com.google.cloud.logging.SinkInfo.VersionFormat;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class SinkInfoTest {
    private static final String NAME = "name";

    private static final String FILTER = "logName=projects/my-projectid/logs/syslog AND severity>=ERROR";

    private static final VersionFormat VERSION = VersionFormat.V1;

    private static final BucketDestination BUCKET_DESTINATION = BucketDestination.of("bucket");

    private static final DatasetDestination DATASET_DESTINATION = DatasetDestination.of("project", "dataset");

    private static final TopicDestination TOPIC_DESTINATION = TopicDestination.of("project", "topic");

    private static final SinkInfo BUCKET_SINK_INFO = SinkInfo.newBuilder(SinkInfoTest.NAME, SinkInfoTest.BUCKET_DESTINATION).setFilter(SinkInfoTest.FILTER).setVersionFormat(SinkInfoTest.VERSION).build();

    private static final SinkInfo DATASET_SINK_INFO = SinkInfo.newBuilder(SinkInfoTest.NAME, SinkInfoTest.DATASET_DESTINATION).setFilter(SinkInfoTest.FILTER).setVersionFormat(SinkInfoTest.VERSION).build();

    private static final SinkInfo TOPIC_SINK_INFO = SinkInfo.newBuilder(SinkInfoTest.NAME, SinkInfoTest.TOPIC_DESTINATION).setFilter(SinkInfoTest.FILTER).setVersionFormat(SinkInfoTest.VERSION).build();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testOfBucketDestination() {
        Assert.assertEquals(BUCKET, SinkInfoTest.BUCKET_DESTINATION.getType());
        Assert.assertEquals("bucket", SinkInfoTest.BUCKET_DESTINATION.getBucket());
    }

    @Test
    public void testOfDatasetDestination() {
        Assert.assertEquals(DATASET, SinkInfoTest.DATASET_DESTINATION.getType());
        Assert.assertEquals("project", SinkInfoTest.DATASET_DESTINATION.getProject());
        Assert.assertEquals("dataset", SinkInfoTest.DATASET_DESTINATION.getDataset());
        DatasetDestination datasetDestination = DatasetDestination.of("dataset");
        Assert.assertNull(datasetDestination.getProject());
        Assert.assertEquals("dataset", datasetDestination.getDataset());
    }

    @Test
    public void testOfTopicDestination() {
        Assert.assertEquals(TOPIC, SinkInfoTest.TOPIC_DESTINATION.getType());
        Assert.assertEquals("project", SinkInfoTest.TOPIC_DESTINATION.getProject());
        Assert.assertEquals("topic", SinkInfoTest.TOPIC_DESTINATION.getTopic());
        TopicDestination topicDestination = TopicDestination.of("topic");
        Assert.assertNull(topicDestination.getProject());
        Assert.assertEquals("topic", topicDestination.getTopic());
    }

    @Test
    public void testToAndFromPbDestination() {
        BucketDestination bucketDestination = Destination.fromPb(SinkInfoTest.BUCKET_DESTINATION.toPb("other"));
        Assert.assertEquals(BUCKET, bucketDestination.getType());
        Assert.assertEquals("bucket", bucketDestination.getBucket());
        compareBucketDestination(SinkInfoTest.BUCKET_DESTINATION, bucketDestination);
        DatasetDestination datasetDestination = Destination.fromPb(SinkInfoTest.DATASET_DESTINATION.toPb("other"));
        Assert.assertEquals(DATASET, datasetDestination.getType());
        Assert.assertEquals("project", datasetDestination.getProject());
        Assert.assertEquals("dataset", datasetDestination.getDataset());
        compareDatasetDestination(SinkInfoTest.DATASET_DESTINATION, datasetDestination);
        TopicDestination topicDestination = Destination.fromPb(SinkInfoTest.TOPIC_DESTINATION.toPb("other"));
        Assert.assertEquals(TOPIC, topicDestination.getType());
        Assert.assertEquals("project", topicDestination.getProject());
        Assert.assertEquals("topic", topicDestination.getTopic());
        compareTopicDestination(SinkInfoTest.TOPIC_DESTINATION, topicDestination);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("wrongDestination is not a valid sink destination");
        Destination.fromPb("wrongDestination");
    }

    @Test
    public void testToAndFromPbDestination_NoProjectId() {
        DatasetDestination datasetDestination = DatasetDestination.fromPb(DatasetDestination.of("dataset").toPb("project"));
        compareDatasetDestination(SinkInfoTest.DATASET_DESTINATION, datasetDestination);
        Assert.assertEquals("project", datasetDestination.getProject());
        TopicDestination topicDestination = TopicDestination.fromPb(TopicDestination.of("topic").toPb("project"));
        Assert.assertEquals("project", topicDestination.getProject());
        compareTopicDestination(SinkInfoTest.TOPIC_DESTINATION, topicDestination);
    }

    @Test
    public void testBuilder() {
        Assert.assertEquals(SinkInfoTest.NAME, SinkInfoTest.BUCKET_SINK_INFO.getName());
        Assert.assertEquals(SinkInfoTest.BUCKET_DESTINATION, SinkInfoTest.BUCKET_SINK_INFO.getDestination());
        Assert.assertEquals(SinkInfoTest.FILTER, SinkInfoTest.BUCKET_SINK_INFO.getFilter());
        Assert.assertEquals(SinkInfoTest.VERSION, SinkInfoTest.BUCKET_SINK_INFO.getVersionFormat());
        Assert.assertEquals(SinkInfoTest.NAME, SinkInfoTest.DATASET_SINK_INFO.getName());
        Assert.assertEquals(SinkInfoTest.DATASET_DESTINATION, SinkInfoTest.DATASET_SINK_INFO.getDestination());
        Assert.assertEquals(SinkInfoTest.FILTER, SinkInfoTest.DATASET_SINK_INFO.getFilter());
        Assert.assertEquals(SinkInfoTest.VERSION, SinkInfoTest.DATASET_SINK_INFO.getVersionFormat());
        Assert.assertEquals(SinkInfoTest.NAME, SinkInfoTest.TOPIC_SINK_INFO.getName());
        Assert.assertEquals(SinkInfoTest.TOPIC_DESTINATION, SinkInfoTest.TOPIC_SINK_INFO.getDestination());
        Assert.assertEquals(SinkInfoTest.FILTER, SinkInfoTest.TOPIC_SINK_INFO.getFilter());
        Assert.assertEquals(SinkInfoTest.VERSION, SinkInfoTest.TOPIC_SINK_INFO.getVersionFormat());
    }

    @Test
    public void testToBuilder() {
        compareSinkInfo(SinkInfoTest.BUCKET_SINK_INFO, SinkInfoTest.BUCKET_SINK_INFO.toBuilder().build());
        compareSinkInfo(SinkInfoTest.DATASET_SINK_INFO, SinkInfoTest.DATASET_SINK_INFO.toBuilder().build());
        compareSinkInfo(SinkInfoTest.TOPIC_SINK_INFO, SinkInfoTest.TOPIC_SINK_INFO.toBuilder().build());
        SinkInfo updatedSinkInfo = SinkInfoTest.BUCKET_SINK_INFO.toBuilder().setDestination(SinkInfoTest.TOPIC_DESTINATION).setName("newName").setFilter("logName=projects/my-projectid/logs/syslog").setVersionFormat(V2).build();
        Assert.assertEquals("newName", updatedSinkInfo.getName());
        Assert.assertEquals(SinkInfoTest.TOPIC_DESTINATION, updatedSinkInfo.getDestination());
        Assert.assertEquals("logName=projects/my-projectid/logs/syslog", updatedSinkInfo.getFilter());
        Assert.assertEquals(V2, updatedSinkInfo.getVersionFormat());
        updatedSinkInfo = SinkInfoTest.BUCKET_SINK_INFO.toBuilder().setDestination(SinkInfoTest.BUCKET_DESTINATION).setName(SinkInfoTest.NAME).setFilter(SinkInfoTest.FILTER).setVersionFormat(V1).build();
        Assert.assertEquals(SinkInfoTest.BUCKET_SINK_INFO, updatedSinkInfo);
    }

    @Test
    public void testToAndFromPb() {
        compareSinkInfo(SinkInfoTest.BUCKET_SINK_INFO, SinkInfo.fromPb(SinkInfoTest.BUCKET_SINK_INFO.toPb("project")));
        compareSinkInfo(SinkInfoTest.DATASET_SINK_INFO, SinkInfo.fromPb(SinkInfoTest.DATASET_SINK_INFO.toPb("project")));
        compareSinkInfo(SinkInfoTest.TOPIC_SINK_INFO, SinkInfo.fromPb(SinkInfoTest.TOPIC_SINK_INFO.toPb("project")));
        SinkInfo sinkInfo = SinkInfo.of("name", SinkInfoTest.BUCKET_DESTINATION);
        compareSinkInfo(sinkInfo, SinkInfo.fromPb(sinkInfo.toPb("project")));
        sinkInfo = SinkInfo.of("name", SinkInfoTest.DATASET_DESTINATION);
        compareSinkInfo(sinkInfo, SinkInfo.fromPb(sinkInfo.toPb("project")));
        sinkInfo = SinkInfo.of("name", SinkInfoTest.TOPIC_DESTINATION);
        compareSinkInfo(sinkInfo, SinkInfo.fromPb(sinkInfo.toPb("project")));
    }

    @Test
    public void testToAndFromPb_NoProjectId() {
        DatasetDestination datasetDestination = DatasetDestination.of("dataset");
        SinkInfo sinkInfo = SinkInfo.of("name", SinkInfoTest.DATASET_DESTINATION);
        compareSinkInfo(sinkInfo, SinkInfo.fromPb(SinkInfo.of("name", datasetDestination).toPb("project")));
        TopicDestination topicDestination = TopicDestination.of("topic");
        sinkInfo = SinkInfo.of("name", SinkInfoTest.TOPIC_DESTINATION);
        compareSinkInfo(sinkInfo, SinkInfo.fromPb(SinkInfo.of("name", topicDestination).toPb("project")));
    }
}

