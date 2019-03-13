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
package org.apache.beam.runners.dataflow.worker;


import Windmill.KeyedGetDataRequest.Builder;
import Windmill.TagBag;
import Windmill.TagValue;
import Windmill.WatermarkHold;
import WindmillStateReader.MAX_BAG_BYTES;
import WindmillStateReader.MAX_KEY_BYTES;
import java.util.concurrent.Future;
import org.apache.beam.runners.dataflow.worker.windmill.Windmill;
import org.apache.beam.runners.dataflow.worker.windmill.Windmill.KeyedGetDataRequest;
import org.apache.beam.sdk.coders.VarIntCoder;
import org.apache.beam.vendor.grpc.v1p13p1.com.google.protobuf.ByteString;
import org.hamcrest.Matchers;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link WindmillStateReader}.
 */
@RunWith(JUnit4.class)
@SuppressWarnings("FutureReturnValueIgnored")
public class WindmillStateReaderTest {
    private static final VarIntCoder INT_CODER = VarIntCoder.of();

    private static final String COMPUTATION = "computation";

    private static final ByteString DATA_KEY = ByteString.copyFromUtf8("DATA_KEY");

    private static final long SHARDING_KEY = 17L;

    private static final long WORK_TOKEN = 5043L;

    private static final long CONT_POSITION = 1391631351L;

    private static final ByteString STATE_KEY_1 = ByteString.copyFromUtf8("key1");

    private static final ByteString STATE_KEY_2 = ByteString.copyFromUtf8("key2");

    private static final String STATE_FAMILY = "family";

    @Mock
    private MetricTrackingWindmillServerStub mockWindmill;

    private WindmillStateReader underTest;

    @Test
    public void testReadBag() throws Exception {
        Future<Iterable<Integer>> future = underTest.bagFuture(WindmillStateReaderTest.STATE_KEY_1, WindmillStateReaderTest.STATE_FAMILY, WindmillStateReaderTest.INT_CODER);
        Mockito.verifyNoMoreInteractions(mockWindmill);
        Windmill.KeyedGetDataRequest.Builder expectedRequest = Windmill.KeyedGetDataRequest.newBuilder().setKey(WindmillStateReaderTest.DATA_KEY).setShardingKey(WindmillStateReaderTest.SHARDING_KEY).setWorkToken(WindmillStateReaderTest.WORK_TOKEN).setMaxBytes(MAX_KEY_BYTES).addBagsToFetch(TagBag.newBuilder().setTag(WindmillStateReaderTest.STATE_KEY_1).setStateFamily(WindmillStateReaderTest.STATE_FAMILY).setFetchMaxBytes(MAX_BAG_BYTES));
        Windmill.KeyedGetDataResponse.Builder response = Windmill.KeyedGetDataResponse.newBuilder().setKey(WindmillStateReaderTest.DATA_KEY).addBags(TagBag.newBuilder().setTag(WindmillStateReaderTest.STATE_KEY_1).setStateFamily(WindmillStateReaderTest.STATE_FAMILY).addValues(intData(5)).addValues(intData(6)));
        Mockito.when(mockWindmill.getStateData(WindmillStateReaderTest.COMPUTATION, expectedRequest.build())).thenReturn(response.build());
        Iterable<Integer> results = future.get();
        Mockito.verify(mockWindmill).getStateData(WindmillStateReaderTest.COMPUTATION, expectedRequest.build());
        for (Integer unused : results) {
            // Iterate over the results to force loading all the pages.
        }
        Mockito.verifyNoMoreInteractions(mockWindmill);
        Assert.assertThat(results, Matchers.contains(5, 6));
        WindmillStateReaderTest.assertNoReader(future);
    }

    @Test
    public void testReadBagWithContinuations() throws Exception {
        Future<Iterable<Integer>> future = underTest.bagFuture(WindmillStateReaderTest.STATE_KEY_1, WindmillStateReaderTest.STATE_FAMILY, WindmillStateReaderTest.INT_CODER);
        Mockito.verifyNoMoreInteractions(mockWindmill);
        Windmill.KeyedGetDataRequest.Builder expectedRequest1 = Windmill.KeyedGetDataRequest.newBuilder().setKey(WindmillStateReaderTest.DATA_KEY).setShardingKey(WindmillStateReaderTest.SHARDING_KEY).setWorkToken(WindmillStateReaderTest.WORK_TOKEN).setMaxBytes(MAX_KEY_BYTES).addBagsToFetch(TagBag.newBuilder().setTag(WindmillStateReaderTest.STATE_KEY_1).setStateFamily(WindmillStateReaderTest.STATE_FAMILY).setFetchMaxBytes(MAX_BAG_BYTES));
        Windmill.KeyedGetDataResponse.Builder response1 = Windmill.KeyedGetDataResponse.newBuilder().setKey(WindmillStateReaderTest.DATA_KEY).addBags(TagBag.newBuilder().setTag(WindmillStateReaderTest.STATE_KEY_1).setStateFamily(WindmillStateReaderTest.STATE_FAMILY).setContinuationPosition(WindmillStateReaderTest.CONT_POSITION).addValues(intData(5)).addValues(intData(6)));
        Windmill.KeyedGetDataRequest.Builder expectedRequest2 = Windmill.KeyedGetDataRequest.newBuilder().setKey(WindmillStateReaderTest.DATA_KEY).setShardingKey(WindmillStateReaderTest.SHARDING_KEY).setWorkToken(WindmillStateReaderTest.WORK_TOKEN).setMaxBytes(MAX_KEY_BYTES).addBagsToFetch(TagBag.newBuilder().setTag(WindmillStateReaderTest.STATE_KEY_1).setStateFamily(WindmillStateReaderTest.STATE_FAMILY).setFetchMaxBytes(MAX_BAG_BYTES).setRequestPosition(WindmillStateReaderTest.CONT_POSITION));
        Windmill.KeyedGetDataResponse.Builder response2 = Windmill.KeyedGetDataResponse.newBuilder().setKey(WindmillStateReaderTest.DATA_KEY).addBags(TagBag.newBuilder().setTag(WindmillStateReaderTest.STATE_KEY_1).setStateFamily(WindmillStateReaderTest.STATE_FAMILY).setRequestPosition(WindmillStateReaderTest.CONT_POSITION).addValues(intData(7)).addValues(intData(8)));
        Mockito.when(mockWindmill.getStateData(WindmillStateReaderTest.COMPUTATION, expectedRequest1.build())).thenReturn(response1.build());
        Mockito.when(mockWindmill.getStateData(WindmillStateReaderTest.COMPUTATION, expectedRequest2.build())).thenReturn(response2.build());
        Iterable<Integer> results = future.get();
        Mockito.verify(mockWindmill).getStateData(WindmillStateReaderTest.COMPUTATION, expectedRequest1.build());
        for (Integer unused : results) {
            // Iterate over the results to force loading all the pages.
        }
        Mockito.verify(mockWindmill).getStateData(WindmillStateReaderTest.COMPUTATION, expectedRequest2.build());
        Mockito.verifyNoMoreInteractions(mockWindmill);
        Assert.assertThat(results, Matchers.contains(5, 6, 7, 8));
        // NOTE: The future will still contain a reference to the underlying reader.
    }

    @Test
    public void testReadValue() throws Exception {
        Future<Integer> future = underTest.valueFuture(WindmillStateReaderTest.STATE_KEY_1, WindmillStateReaderTest.STATE_FAMILY, WindmillStateReaderTest.INT_CODER);
        Mockito.verifyNoMoreInteractions(mockWindmill);
        Windmill.KeyedGetDataRequest.Builder expectedRequest = Windmill.KeyedGetDataRequest.newBuilder().setKey(WindmillStateReaderTest.DATA_KEY).setShardingKey(WindmillStateReaderTest.SHARDING_KEY).setWorkToken(WindmillStateReaderTest.WORK_TOKEN).setMaxBytes(MAX_KEY_BYTES).addValuesToFetch(TagValue.newBuilder().setTag(WindmillStateReaderTest.STATE_KEY_1).setStateFamily(WindmillStateReaderTest.STATE_FAMILY).build());
        Windmill.KeyedGetDataResponse.Builder response = Windmill.KeyedGetDataResponse.newBuilder().setKey(WindmillStateReaderTest.DATA_KEY).addValues(TagValue.newBuilder().setTag(WindmillStateReaderTest.STATE_KEY_1).setStateFamily(WindmillStateReaderTest.STATE_FAMILY).setValue(intValue(8)));
        Mockito.when(mockWindmill.getStateData(WindmillStateReaderTest.COMPUTATION, expectedRequest.build())).thenReturn(response.build());
        Integer result = future.get();
        Mockito.verify(mockWindmill).getStateData(WindmillStateReaderTest.COMPUTATION, expectedRequest.build());
        Mockito.verifyNoMoreInteractions(mockWindmill);
        Assert.assertThat(result, Matchers.equalTo(8));
        WindmillStateReaderTest.assertNoReader(future);
    }

    @Test
    public void testReadWatermark() throws Exception {
        Future<Instant> future = underTest.watermarkFuture(WindmillStateReaderTest.STATE_KEY_1, WindmillStateReaderTest.STATE_FAMILY);
        Mockito.verifyNoMoreInteractions(mockWindmill);
        Windmill.KeyedGetDataRequest.Builder expectedRequest = Windmill.KeyedGetDataRequest.newBuilder().setKey(WindmillStateReaderTest.DATA_KEY).setShardingKey(WindmillStateReaderTest.SHARDING_KEY).setWorkToken(WindmillStateReaderTest.WORK_TOKEN).setMaxBytes(MAX_KEY_BYTES).addWatermarkHoldsToFetch(WatermarkHold.newBuilder().setTag(WindmillStateReaderTest.STATE_KEY_1).setStateFamily(WindmillStateReaderTest.STATE_FAMILY));
        Windmill.KeyedGetDataResponse.Builder response = Windmill.KeyedGetDataResponse.newBuilder().setKey(WindmillStateReaderTest.DATA_KEY).addWatermarkHolds(WatermarkHold.newBuilder().setTag(WindmillStateReaderTest.STATE_KEY_1).setStateFamily(WindmillStateReaderTest.STATE_FAMILY).addTimestamps(5000000).addTimestamps(6000000));
        Mockito.when(mockWindmill.getStateData(WindmillStateReaderTest.COMPUTATION, expectedRequest.build())).thenReturn(response.build());
        Instant result = future.get();
        Mockito.verify(mockWindmill).getStateData(WindmillStateReaderTest.COMPUTATION, expectedRequest.build());
        Assert.assertThat(result, Matchers.equalTo(new Instant(5000)));
        WindmillStateReaderTest.assertNoReader(future);
    }

    @Test
    public void testBatching() throws Exception {
        // Reads two bags and verifies that we batch them up correctly.
        Future<Instant> watermarkFuture = underTest.watermarkFuture(WindmillStateReaderTest.STATE_KEY_2, WindmillStateReaderTest.STATE_FAMILY);
        Future<Iterable<Integer>> bagFuture = underTest.bagFuture(WindmillStateReaderTest.STATE_KEY_1, WindmillStateReaderTest.STATE_FAMILY, WindmillStateReaderTest.INT_CODER);
        Mockito.verifyNoMoreInteractions(mockWindmill);
        ArgumentCaptor<Windmill.KeyedGetDataRequest> request = ArgumentCaptor.forClass(org.apache.beam.runners.dataflow.worker.windmill.Windmill.KeyedGetDataRequest.class);
        Windmill.KeyedGetDataResponse.Builder response = Windmill.KeyedGetDataResponse.newBuilder().setKey(WindmillStateReaderTest.DATA_KEY).addWatermarkHolds(WatermarkHold.newBuilder().setTag(WindmillStateReaderTest.STATE_KEY_2).setStateFamily(WindmillStateReaderTest.STATE_FAMILY).addTimestamps(5000000).addTimestamps(6000000)).addBags(TagBag.newBuilder().setTag(WindmillStateReaderTest.STATE_KEY_1).setStateFamily(WindmillStateReaderTest.STATE_FAMILY).addValues(intData(5)).addValues(intData(100)));
        Mockito.when(mockWindmill.getStateData(Mockito.eq(WindmillStateReaderTest.COMPUTATION), Mockito.isA(org.apache.beam.runners.dataflow.worker.windmill.Windmill.KeyedGetDataRequest.class))).thenReturn(response.build());
        Instant result = watermarkFuture.get();
        Mockito.verify(mockWindmill).getStateData(Mockito.eq(WindmillStateReaderTest.COMPUTATION), request.capture());
        // Verify the request looks right.
        org.apache.beam.runners.dataflow.worker.windmill.Windmill.KeyedGetDataRequest keyedRequest = request.getValue();
        Assert.assertThat(keyedRequest.getKey(), Matchers.equalTo(WindmillStateReaderTest.DATA_KEY));
        Assert.assertThat(keyedRequest.getWorkToken(), Matchers.equalTo(WindmillStateReaderTest.WORK_TOKEN));
        Assert.assertThat(keyedRequest.getBagsToFetchCount(), Matchers.equalTo(1));
        Assert.assertThat(keyedRequest.getBagsToFetch(0).getDeleteAll(), Matchers.equalTo(false));
        Assert.assertThat(keyedRequest.getBagsToFetch(0).getTag(), Matchers.equalTo(WindmillStateReaderTest.STATE_KEY_1));
        Assert.assertThat(keyedRequest.getWatermarkHoldsToFetchCount(), Matchers.equalTo(1));
        Assert.assertThat(keyedRequest.getWatermarkHoldsToFetch(0).getTag(), Matchers.equalTo(WindmillStateReaderTest.STATE_KEY_2));
        // Verify the values returned to the user.
        Assert.assertThat(result, Matchers.equalTo(new Instant(5000)));
        Mockito.verifyNoMoreInteractions(mockWindmill);
        Assert.assertThat(bagFuture.get(), Matchers.contains(5, 100));
        Mockito.verifyNoMoreInteractions(mockWindmill);
        // And verify that getting a future again returns the already completed future.
        Future<Instant> watermarkFuture2 = underTest.watermarkFuture(WindmillStateReaderTest.STATE_KEY_2, WindmillStateReaderTest.STATE_FAMILY);
        Assert.assertTrue(watermarkFuture2.isDone());
        WindmillStateReaderTest.assertNoReader(watermarkFuture);
        WindmillStateReaderTest.assertNoReader(watermarkFuture2);
    }

    @Test
    public void testNoStateFamily() throws Exception {
        Future<Integer> future = underTest.valueFuture(WindmillStateReaderTest.STATE_KEY_1, "", WindmillStateReaderTest.INT_CODER);
        Mockito.verifyNoMoreInteractions(mockWindmill);
        Windmill.KeyedGetDataRequest.Builder expectedRequest = Windmill.KeyedGetDataRequest.newBuilder().setKey(WindmillStateReaderTest.DATA_KEY).setShardingKey(WindmillStateReaderTest.SHARDING_KEY).setMaxBytes(MAX_KEY_BYTES).setWorkToken(WindmillStateReaderTest.WORK_TOKEN).addValuesToFetch(TagValue.newBuilder().setTag(WindmillStateReaderTest.STATE_KEY_1).setStateFamily("").build());
        Windmill.KeyedGetDataResponse.Builder response = Windmill.KeyedGetDataResponse.newBuilder().setKey(WindmillStateReaderTest.DATA_KEY).addValues(TagValue.newBuilder().setTag(WindmillStateReaderTest.STATE_KEY_1).setStateFamily("").setValue(intValue(8)));
        Mockito.when(mockWindmill.getStateData(WindmillStateReaderTest.COMPUTATION, expectedRequest.build())).thenReturn(response.build());
        Integer result = future.get();
        Mockito.verify(mockWindmill).getStateData(WindmillStateReaderTest.COMPUTATION, expectedRequest.build());
        Mockito.verifyNoMoreInteractions(mockWindmill);
        Assert.assertThat(result, Matchers.equalTo(8));
        WindmillStateReaderTest.assertNoReader(future);
    }

    @Test
    public void testKeyTokenInvalid() throws Exception {
        // Reads two bags and verifies that we batch them up correctly.
        Future<Instant> watermarkFuture = underTest.watermarkFuture(WindmillStateReaderTest.STATE_KEY_2, WindmillStateReaderTest.STATE_FAMILY);
        Future<Iterable<Integer>> bagFuture = underTest.bagFuture(WindmillStateReaderTest.STATE_KEY_1, WindmillStateReaderTest.STATE_FAMILY, WindmillStateReaderTest.INT_CODER);
        Mockito.verifyNoMoreInteractions(mockWindmill);
        Windmill.KeyedGetDataResponse.Builder response = Windmill.KeyedGetDataResponse.newBuilder().setKey(WindmillStateReaderTest.DATA_KEY).setFailed(true);
        Mockito.when(mockWindmill.getStateData(Mockito.eq(WindmillStateReaderTest.COMPUTATION), Mockito.isA(org.apache.beam.runners.dataflow.worker.windmill.Windmill.KeyedGetDataRequest.class))).thenReturn(response.build());
        try {
            watermarkFuture.get();
            Assert.fail("Expected KeyTokenInvalidException");
        } catch (Exception e) {
            Assert.assertTrue(KeyTokenInvalidException.isKeyTokenInvalidException(e));
        }
        try {
            bagFuture.get();
            Assert.fail("Expected KeyTokenInvalidException");
        } catch (Exception e) {
            Assert.assertTrue(KeyTokenInvalidException.isKeyTokenInvalidException(e));
        }
    }

    /**
     * Tests that multiple reads for the same tag in the same batch are cached. We can't compare the
     * futures since we've wrapped the delegate aronud them, so we just verify there is only one
     * queued lookup.
     */
    @Test
    public void testCachingWithinBatch() throws Exception {
        underTest.watermarkFuture(WindmillStateReaderTest.STATE_KEY_1, WindmillStateReaderTest.STATE_FAMILY);
        underTest.watermarkFuture(WindmillStateReaderTest.STATE_KEY_1, WindmillStateReaderTest.STATE_FAMILY);
        Assert.assertEquals(1, underTest.pendingLookups.size());
    }
}

