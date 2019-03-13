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
package org.apache.beam.sdk.transforms.windowing;


import PaneInfo.PaneInfoCoder;
import Timing.EARLY;
import Timing.LATE;
import Timing.ON_TIME;
import Timing.UNKNOWN;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.testing.CoderProperties;
import org.apache.beam.sdk.transforms.windowing.PaneInfo.Timing;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link PaneInfo}.
 */
@RunWith(JUnit4.class)
public class PaneInfoTest {
    @Test
    public void testInterned() throws Exception {
        Assert.assertSame(PaneInfo.createPane(true, true, EARLY), PaneInfo.createPane(true, true, EARLY));
    }

    @Test
    public void testEncodingRoundTrip() throws Exception {
        Coder<PaneInfo> coder = PaneInfoCoder.INSTANCE;
        for (Timing timing : Timing.values()) {
            long onTimeIndex = (timing == (Timing.EARLY)) ? -1 : 37;
            CoderProperties.coderDecodeEncodeEqual(coder, PaneInfo.createPane(false, false, timing, 389, onTimeIndex));
            CoderProperties.coderDecodeEncodeEqual(coder, PaneInfo.createPane(false, true, timing, 5077, onTimeIndex));
            CoderProperties.coderDecodeEncodeEqual(coder, PaneInfo.createPane(true, false, timing, 0, 0));
            CoderProperties.coderDecodeEncodeEqual(coder, PaneInfo.createPane(true, true, timing, 0, 0));
        }
    }

    @Test
    public void testEncodings() {
        Assert.assertEquals("PaneInfo encoding assumes that there are only 4 Timing values.", 4, Timing.values().length);
        Assert.assertEquals("PaneInfo encoding should remain the same.", 0, PaneInfo.createPane(false, false, EARLY, 1, (-1)).getEncodedByte());
        Assert.assertEquals("PaneInfo encoding should remain the same.", 1, PaneInfo.createPane(true, false, EARLY).getEncodedByte());
        Assert.assertEquals("PaneInfo encoding should remain the same.", 3, PaneInfo.createPane(true, true, EARLY).getEncodedByte());
        Assert.assertEquals("PaneInfo encoding should remain the same.", 7, PaneInfo.createPane(true, true, ON_TIME).getEncodedByte());
        Assert.assertEquals("PaneInfo encoding should remain the same.", 11, PaneInfo.createPane(true, true, LATE).getEncodedByte());
        Assert.assertEquals("PaneInfo encoding should remain the same.", 15, PaneInfo.createPane(true, true, UNKNOWN).getEncodedByte());
    }
}

