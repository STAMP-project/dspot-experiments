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
package org.apache.beam.sdk.io.kinesis;


import InitialPositionInStream.TRIM_HORIZON;
import KinesisIO.Write;
import com.amazonaws.http.SdkHttpMetadata;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.model.DescribeStreamResult;
import com.amazonaws.services.kinesis.producer.IKinesisProducer;
import com.amazonaws.services.kinesis.producer.KinesisProducerConfiguration;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests for {@link KinesisIO.Write}.
 */
@RunWith(JUnit4.class)
public class KinesisMockWriteTest {
    private static final String STREAM = "BEAM";

    private static final String PARTITION_KEY = "partitionKey";

    @Rule
    public final transient TestPipeline p = TestPipeline.create();

    @Rule
    public final transient TestPipeline p2 = TestPipeline.create();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testWriteBuildsCorrectly() {
        Properties properties = new Properties();
        properties.setProperty("KinesisEndpoint", "localhost");
        properties.setProperty("KinesisPort", "4567");
        KinesisIO.Write write = KinesisIO.write().withStreamName(KinesisMockWriteTest.STREAM).withPartitionKey(KinesisMockWriteTest.PARTITION_KEY).withPartitioner(new KinesisMockWriteTest.BasicKinesisPartitioner()).withAWSClientsProvider(new KinesisMockWriteTest.FakeKinesisProvider()).withProducerProperties(properties).withRetries(10);
        Assert.assertEquals(KinesisMockWriteTest.STREAM, write.getStreamName());
        Assert.assertEquals(KinesisMockWriteTest.PARTITION_KEY, write.getPartitionKey());
        Assert.assertEquals(properties, write.getProducerProperties());
        Assert.assertEquals(KinesisMockWriteTest.FakeKinesisProvider.class, write.getAWSClientsProvider().getClass());
        Assert.assertEquals(KinesisMockWriteTest.BasicKinesisPartitioner.class, write.getPartitioner().getClass());
        Assert.assertEquals(10, write.getRetries());
        Assert.assertEquals("localhost", write.getProducerProperties().getProperty("KinesisEndpoint"));
        Assert.assertEquals("4567", write.getProducerProperties().getProperty("KinesisPort"));
    }

    @Test
    public void testWriteValidationFailsMissingStreamName() {
        KinesisIO.Write write = KinesisIO.write().withPartitionKey(KinesisMockWriteTest.PARTITION_KEY).withAWSClientsProvider(new KinesisMockWriteTest.FakeKinesisProvider());
        thrown.expect(IllegalArgumentException.class);
        write.expand(null);
    }

    @Test
    public void testWriteValidationFailsMissingPartitioner() {
        KinesisIO.Write write = KinesisIO.write().withStreamName(KinesisMockWriteTest.STREAM).withAWSClientsProvider(new KinesisMockWriteTest.FakeKinesisProvider());
        thrown.expect(IllegalArgumentException.class);
        write.expand(null);
    }

    @Test
    public void testWriteValidationFailsPartitionerAndPartitioneKey() {
        KinesisIO.Write write = KinesisIO.write().withStreamName(KinesisMockWriteTest.STREAM).withPartitionKey(KinesisMockWriteTest.PARTITION_KEY).withPartitioner(new KinesisMockWriteTest.BasicKinesisPartitioner()).withAWSClientsProvider(new KinesisMockWriteTest.FakeKinesisProvider());
        thrown.expect(IllegalArgumentException.class);
        write.expand(null);
    }

    @Test
    public void testWriteValidationFailsMissingAWSClientsProvider() {
        KinesisIO.Write write = KinesisIO.write().withPartitionKey(KinesisMockWriteTest.PARTITION_KEY).withStreamName(KinesisMockWriteTest.STREAM);
        thrown.expect(IllegalArgumentException.class);
        write.expand(null);
    }

    @Test
    public void testNotExistedStream() {
        Iterable<byte[]> data = ImmutableList.of("1".getBytes(StandardCharsets.UTF_8));
        p.apply(Create.of(data)).apply(KinesisIO.write().withStreamName(KinesisMockWriteTest.STREAM).withPartitionKey(KinesisMockWriteTest.PARTITION_KEY).withAWSClientsProvider(new KinesisMockWriteTest.FakeKinesisProvider(false)));
        thrown.expect(RuntimeException.class);
        p.run().waitUntilFinish();
    }

    @Test
    public void testSetInvalidProperty() {
        Properties properties = new Properties();
        properties.setProperty("KinesisPort", "qwe");
        Iterable<byte[]> data = ImmutableList.of("1".getBytes(StandardCharsets.UTF_8));
        p.apply(Create.of(data)).apply(KinesisIO.write().withStreamName(KinesisMockWriteTest.STREAM).withPartitionKey(KinesisMockWriteTest.PARTITION_KEY).withAWSClientsProvider(new KinesisMockWriteTest.FakeKinesisProvider()).withProducerProperties(properties));
        thrown.expect(RuntimeException.class);
        p.run().waitUntilFinish();
    }

    @Test
    public void testWrite() {
        KinesisServiceMock kinesisService = KinesisServiceMock.getInstance();
        Properties properties = new Properties();
        properties.setProperty("KinesisEndpoint", "localhost");
        properties.setProperty("KinesisPort", "4567");
        properties.setProperty("VerifyCertificate", "false");
        Iterable<byte[]> data = ImmutableList.of("1".getBytes(StandardCharsets.UTF_8), "2".getBytes(StandardCharsets.UTF_8), "3".getBytes(StandardCharsets.UTF_8));
        p.apply(Create.of(data)).apply(KinesisIO.write().withStreamName(KinesisMockWriteTest.STREAM).withPartitionKey(KinesisMockWriteTest.PARTITION_KEY).withAWSClientsProvider(new KinesisMockWriteTest.FakeKinesisProvider()).withProducerProperties(properties));
        p.run().waitUntilFinish();
        Assert.assertEquals(3, kinesisService.getAddedRecords().get());
    }

    @Test
    public void testWriteFailed() {
        Iterable<byte[]> data = ImmutableList.of("1".getBytes(StandardCharsets.UTF_8));
        p.apply(Create.of(data)).apply(KinesisIO.write().withStreamName(KinesisMockWriteTest.STREAM).withPartitionKey(KinesisMockWriteTest.PARTITION_KEY).withAWSClientsProvider(new KinesisMockWriteTest.FakeKinesisProvider().setFailedFlush(true)).withRetries(1));
        thrown.expect(RuntimeException.class);
        p.run().waitUntilFinish();
    }

    @Test
    public void testWriteAndReadFromMockKinesis() {
        KinesisServiceMock kinesisService = KinesisServiceMock.getInstance();
        Iterable<byte[]> data = ImmutableList.of("1".getBytes(StandardCharsets.UTF_8), "2".getBytes(StandardCharsets.UTF_8));
        p.apply(Create.of(data)).apply(KinesisIO.write().withStreamName(KinesisMockWriteTest.STREAM).withPartitionKey(KinesisMockWriteTest.PARTITION_KEY).withAWSClientsProvider(new KinesisMockWriteTest.FakeKinesisProvider()));
        p.run().waitUntilFinish();
        Assert.assertEquals(2, kinesisService.getAddedRecords().get());
        List<List<AmazonKinesisMock.TestData>> testData = kinesisService.getShardedData();
        int noOfShards = 1;
        int noOfEventsPerShard = 2;
        PCollection<AmazonKinesisMock.TestData> result = p2.apply(KinesisIO.read().withStreamName(KinesisMockWriteTest.STREAM).withInitialPositionInStream(TRIM_HORIZON).withAWSClientsProvider(new AmazonKinesisMock.Provider(testData, 10)).withMaxNumRecords((noOfShards * noOfEventsPerShard))).apply(ParDo.of(new KinesisMockReadTest.KinesisRecordToTestData()));
        PAssert.that(result).containsInAnyOrder(Iterables.concat(testData));
        p2.run().waitUntilFinish();
    }

    private static final class BasicKinesisPartitioner implements KinesisPartitioner {
        @Override
        public String getPartitionKey(byte[] value) {
            return String.valueOf(value.length);
        }

        @Override
        public String getExplicitHashKey(byte[] value) {
            return null;
        }
    }

    private static final class FakeKinesisProvider implements AWSClientsProvider {
        private boolean isExistingStream = true;

        private boolean isFailedFlush = false;

        public FakeKinesisProvider() {
        }

        public FakeKinesisProvider(boolean isExistingStream) {
            this.isExistingStream = isExistingStream;
        }

        public KinesisMockWriteTest.FakeKinesisProvider setFailedFlush(boolean failedFlush) {
            isFailedFlush = failedFlush;
            return this;
        }

        @Override
        public AmazonKinesis getKinesisClient() {
            return getMockedAmazonKinesisClient();
        }

        @Override
        public AmazonCloudWatch getCloudWatchClient() {
            throw new RuntimeException("Not implemented");
        }

        @Override
        public IKinesisProducer createKinesisProducer(KinesisProducerConfiguration config) {
            return new KinesisProducerMock(config, isFailedFlush);
        }

        private AmazonKinesis getMockedAmazonKinesisClient() {
            int statusCode = (isExistingStream) ? 200 : 404;
            SdkHttpMetadata httpMetadata = Mockito.mock(SdkHttpMetadata.class);
            Mockito.when(httpMetadata.getHttpStatusCode()).thenReturn(statusCode);
            DescribeStreamResult streamResult = Mockito.mock(DescribeStreamResult.class);
            Mockito.when(streamResult.getSdkHttpMetadata()).thenReturn(httpMetadata);
            AmazonKinesis client = Mockito.mock(AmazonKinesis.class);
            Mockito.when(client.describeStream(ArgumentMatchers.any(String.class))).thenReturn(streamResult);
            return client;
        }
    }
}

