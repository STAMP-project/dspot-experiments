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
package org.apache.beam.runners.dataflow.worker.status;


import Dataflow.Projects.Locations.Jobs.Debug.SendCapture;
import DebugCapture.Config;
import DebugCapture.Manager;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.dataflow.model.GetDebugConfigResponse;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 * Tests for {@link DebugCapture}.
 */
@RunWith(JUnit4.class)
public class DebugCaptureTest {
    private static final String PROJECT_ID = "some-project";

    private static final String REGION = "some-region";

    private static final String JOB_ID = "some-job";

    private static final String WORKER_ID = "some-host-abcd";

    private static final String UPDATE_CONFIG_JSON = "{\"expire_timestamp_usec\":11,\"capture_frequency_usec\":12}";

    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    private SendCapture mockSendCapture;

    private GetDebugConfigResponse fakeGetConfigResponse;

    @Test
    public void testUpdateConfig() throws Exception {
        DebugCapture.Manager debugCaptureManager = new DebugCapture.Manager(buildDataflowWorkerOptions(), Collections.emptyList());
        Assert.assertEquals(0, debugCaptureManager.captureConfig.expireTimestampUsec);
        Assert.assertEquals(0, debugCaptureManager.captureConfig.captureFrequencyUsec);
        debugCaptureManager.updateConfig();
        Assert.assertEquals(11, debugCaptureManager.captureConfig.expireTimestampUsec);
        Assert.assertEquals(12, debugCaptureManager.captureConfig.captureFrequencyUsec);
    }

    @Test
    public void testSendCapture() throws Exception {
        DebugCapture.Manager debugCaptureManager = new DebugCapture.Manager(buildDataflowWorkerOptions(), Collections.emptyList());
        DebugCapture.Config config = debugCaptureManager.captureConfig;
        // If the config is disabled, regardless it is expired or not, we don't send capture.
        config.captureFrequencyUsec = 0;// disabled

        config.expireTimestampUsec = Long.MAX_VALUE;// never expired

        debugCaptureManager.maybeSendCapture();
        Mockito.verify(mockSendCapture, Mockito.never()).execute();
        // If the config is enabled but expired, then we don't send capture.
        config.captureFrequencyUsec = 1000;
        config.expireTimestampUsec = 0;// expired

        debugCaptureManager.maybeSendCapture();
        Mockito.verify(mockSendCapture, Mockito.never()).execute();
        // If the config is enabled and not expired, but it is not the time to send capture, then we
        // don't send capture.
        config.captureFrequencyUsec = Long.MAX_VALUE;// never send capture

        config.expireTimestampUsec = Long.MAX_VALUE;// never expired

        debugCaptureManager.maybeSendCapture();
        Mockito.verify(mockSendCapture, Mockito.never()).execute();
        // Otherwise, we will send capture.
        config.captureFrequencyUsec = 1000;
        config.expireTimestampUsec = Long.MAX_VALUE;// never expired

        debugCaptureManager.maybeSendCapture();
        Mockito.verify(mockSendCapture).execute();
    }
}

