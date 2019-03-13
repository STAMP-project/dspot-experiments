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
package com.google.cloud.vision.v1p1beta1;


import com.google.api.gax.grpc.GaxGrpcProperties;
import com.google.api.gax.grpc.testing.LocalChannelProvider;
import com.google.api.gax.grpc.testing.MockServiceHelper;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.protobuf.GeneratedMessageV3;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.junit.Assert;
import org.junit.Test;


@Generated("by GAPIC")
public class ImageAnnotatorClientTest {
    private static MockImageAnnotator mockImageAnnotator;

    private static MockServiceHelper serviceHelper;

    private ImageAnnotatorClient client;

    private LocalChannelProvider channelProvider;

    @Test
    @SuppressWarnings("all")
    public void batchAnnotateImagesTest() {
        BatchAnnotateImagesResponse expectedResponse = BatchAnnotateImagesResponse.newBuilder().build();
        ImageAnnotatorClientTest.mockImageAnnotator.addResponse(expectedResponse);
        List<AnnotateImageRequest> requests = new ArrayList<>();
        BatchAnnotateImagesResponse actualResponse = client.batchAnnotateImages(requests);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = ImageAnnotatorClientTest.mockImageAnnotator.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        BatchAnnotateImagesRequest actualRequest = ((BatchAnnotateImagesRequest) (actualRequests.get(0)));
        Assert.assertEquals(requests, actualRequest.getRequestsList());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void batchAnnotateImagesExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        ImageAnnotatorClientTest.mockImageAnnotator.addException(exception);
        try {
            List<AnnotateImageRequest> requests = new ArrayList<>();
            client.batchAnnotateImages(requests);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }
}

