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
package com.google.cloud.automl.v1beta1;


import com.google.api.gax.grpc.GaxGrpcProperties;
import com.google.api.gax.grpc.testing.LocalChannelProvider;
import com.google.api.gax.grpc.testing.MockServiceHelper;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.protobuf.GeneratedMessageV3;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import org.junit.Assert;
import org.junit.Test;


@Generated("by GAPIC")
public class PredictionServiceClientTest {
    private static MockAutoMl mockAutoMl;

    private static MockPredictionService mockPredictionService;

    private static MockServiceHelper serviceHelper;

    private PredictionServiceClient client;

    private LocalChannelProvider channelProvider;

    @Test
    @SuppressWarnings("all")
    public void predictTest() {
        PredictResponse expectedResponse = PredictResponse.newBuilder().build();
        PredictionServiceClientTest.mockPredictionService.addResponse(expectedResponse);
        ModelName name = ModelName.of("[PROJECT]", "[LOCATION]", "[MODEL]");
        ExamplePayload payload = ExamplePayload.newBuilder().build();
        Map<String, String> params = new HashMap<>();
        PredictResponse actualResponse = client.predict(name, payload, params);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = PredictionServiceClientTest.mockPredictionService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        PredictRequest actualRequest = ((PredictRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, ModelName.parse(actualRequest.getName()));
        Assert.assertEquals(payload, actualRequest.getPayload());
        Assert.assertEquals(params, actualRequest.getParamsMap());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void predictExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        PredictionServiceClientTest.mockPredictionService.addException(exception);
        try {
            ModelName name = ModelName.of("[PROJECT]", "[LOCATION]", "[MODEL]");
            ExamplePayload payload = ExamplePayload.newBuilder().build();
            Map<String, String> params = new HashMap<>();
            client.predict(name, payload, params);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }
}

