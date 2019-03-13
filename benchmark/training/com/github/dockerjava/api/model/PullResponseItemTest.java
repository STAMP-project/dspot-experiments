/**
 * Copyright 2015, Zach Marshall.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.dockerjava.api.model;


import com.github.dockerjava.test.serdes.JSONTestHelper;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests logic of PullResponseItem's error/success handling by simulating a JSON response.
 *
 * @author Zach Marshall
 */
public class PullResponseItemTest {
    @Test
    public void pullNewerImage() throws IOException {
        PullResponseItem response = JSONTestHelper.testRoundTrip(PullResponseJSONSamples.pullImageResponse_newerImage, PullResponseItem.class);
        Assert.assertTrue(response.isPullSuccessIndicated());
        Assert.assertFalse(response.isErrorIndicated());
    }

    @Test
    public void pullUpToDate() throws IOException {
        PullResponseItem response = JSONTestHelper.testRoundTrip(PullResponseJSONSamples.pullImageResponse_upToDate, PullResponseItem.class);
        Assert.assertTrue(response.isPullSuccessIndicated());
        Assert.assertFalse(response.isErrorIndicated());
    }

    @Test
    public void pullLegacyRegistry() throws IOException {
        PullResponseItem response = JSONTestHelper.testRoundTrip(PullResponseJSONSamples.pullImageResponse_legacy, PullResponseItem.class);
        Assert.assertTrue(response.isPullSuccessIndicated());
        Assert.assertFalse(response.isErrorIndicated());
    }

    @Test
    public void pullAndEncounterError() throws IOException {
        PullResponseItem response = JSONTestHelper.testRoundTrip(PullResponseJSONSamples.pullImageResponse_error, PullResponseItem.class);
        Assert.assertFalse(response.isPullSuccessIndicated());
        Assert.assertTrue(response.isErrorIndicated());
    }
}

