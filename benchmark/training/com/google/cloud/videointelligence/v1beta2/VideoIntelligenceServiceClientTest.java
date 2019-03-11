/**
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.videointelligence.v1beta2;


import StatusCode.Code.INVALID_ARGUMENT;
import com.google.api.gax.grpc.GaxGrpcProperties;
import com.google.api.gax.grpc.testing.LocalChannelProvider;
import com.google.api.gax.grpc.testing.MockServiceHelper;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.longrunning.Operation;
import com.google.protobuf.Any;
import com.google.protobuf.GeneratedMessageV3;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.annotation.Generated;
import org.junit.Assert;
import org.junit.Test;

import static Feature.LABEL_DETECTION;


@Generated("by GAPIC")
public class VideoIntelligenceServiceClientTest {
    private static MockVideoIntelligenceService mockVideoIntelligenceService;

    private static MockServiceHelper serviceHelper;

    private VideoIntelligenceServiceClient client;

    private LocalChannelProvider channelProvider;

    @Test
    @SuppressWarnings("all")
    public void annotateVideoTest() throws Exception {
        AnnotateVideoResponse expectedResponse = AnnotateVideoResponse.newBuilder().build();
        Operation resultOperation = Operation.newBuilder().setName("annotateVideoTest").setDone(true).setResponse(Any.pack(expectedResponse)).build();
        VideoIntelligenceServiceClientTest.mockVideoIntelligenceService.addResponse(resultOperation);
        String inputUri = "gs://demomaker/cat.mp4";
        Feature featuresElement = LABEL_DETECTION;
        List<Feature> features = Arrays.asList(featuresElement);
        AnnotateVideoResponse actualResponse = client.annotateVideoAsync(inputUri, features).get();
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = VideoIntelligenceServiceClientTest.mockVideoIntelligenceService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        AnnotateVideoRequest actualRequest = ((AnnotateVideoRequest) (actualRequests.get(0)));
        Assert.assertEquals(inputUri, actualRequest.getInputUri());
        Assert.assertEquals(features, actualRequest.getFeaturesList());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void annotateVideoExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        VideoIntelligenceServiceClientTest.mockVideoIntelligenceService.addException(exception);
        try {
            String inputUri = "gs://demomaker/cat.mp4";
            Feature featuresElement = LABEL_DETECTION;
            List<Feature> features = Arrays.asList(featuresElement);
            client.annotateVideoAsync(inputUri, features).get();
            Assert.fail("No exception raised");
        } catch (ExecutionException e) {
            Assert.assertEquals(InvalidArgumentException.class, e.getCause().getClass());
            InvalidArgumentException apiException = ((InvalidArgumentException) (e.getCause()));
            Assert.assertEquals(INVALID_ARGUMENT, apiException.getStatusCode().getCode());
        }
    }
}

