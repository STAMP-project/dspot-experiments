/**
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.exoplayer2.source.dash.manifest;


import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.offline.StreamKey;
import com.google.android.exoplayer2.source.dash.manifest.SegmentBase.SingleSegmentBase;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit tests for {@link DashManifest}.
 */
@RunWith(RobolectricTestRunner.class)
public class DashManifestTest {
    private static final UtcTimingElement DUMMY_UTC_TIMING = new UtcTimingElement("", "");

    private static final SingleSegmentBase DUMMY_SEGMENT_BASE = new SingleSegmentBase();

    private static final Format DUMMY_FORMAT = Format.createSampleFormat("", "", 0);

    @Test
    public void testCopy() throws Exception {
        Representation[][][] representations = DashManifestTest.newRepresentations(3, 2, 3);
        DashManifest sourceManifest = DashManifestTest.newDashManifest(10, DashManifestTest.newPeriod("1", 1, DashManifestTest.newAdaptationSet(2, representations[0][0]), DashManifestTest.newAdaptationSet(3, representations[0][1])), DashManifestTest.newPeriod("4", 4, DashManifestTest.newAdaptationSet(5, representations[1][0]), DashManifestTest.newAdaptationSet(6, representations[1][1])), DashManifestTest.newPeriod("7", 7, DashManifestTest.newAdaptationSet(8, representations[2][0]), DashManifestTest.newAdaptationSet(9, representations[2][1])));
        List<StreamKey> keys = Arrays.asList(new StreamKey(0, 0, 0), new StreamKey(0, 0, 1), new StreamKey(0, 1, 2), new StreamKey(1, 0, 1), new StreamKey(1, 1, 0), new StreamKey(1, 1, 2), new StreamKey(2, 0, 1), new StreamKey(2, 0, 2), new StreamKey(2, 1, 0));
        // Keys don't need to be in any particular order
        Collections.shuffle(keys, new Random(0));
        DashManifest copyManifest = sourceManifest.copy(keys);
        DashManifest expectedManifest = DashManifestTest.newDashManifest(10, DashManifestTest.newPeriod("1", 1, DashManifestTest.newAdaptationSet(2, representations[0][0][0], representations[0][0][1]), DashManifestTest.newAdaptationSet(3, representations[0][1][2])), DashManifestTest.newPeriod("4", 4, DashManifestTest.newAdaptationSet(5, representations[1][0][1]), DashManifestTest.newAdaptationSet(6, representations[1][1][0], representations[1][1][2])), DashManifestTest.newPeriod("7", 7, DashManifestTest.newAdaptationSet(8, representations[2][0][1], representations[2][0][2]), DashManifestTest.newAdaptationSet(9, representations[2][1][0])));
        DashManifestTest.assertManifestEquals(expectedManifest, copyManifest);
    }

    @Test
    public void testCopySameAdaptationIndexButDifferentPeriod() throws Exception {
        Representation[][][] representations = DashManifestTest.newRepresentations(2, 1, 1);
        DashManifest sourceManifest = DashManifestTest.newDashManifest(10, DashManifestTest.newPeriod("1", 1, DashManifestTest.newAdaptationSet(2, representations[0][0])), DashManifestTest.newPeriod("4", 4, DashManifestTest.newAdaptationSet(5, representations[1][0])));
        DashManifest copyManifest = sourceManifest.copy(Arrays.asList(new StreamKey(0, 0, 0), new StreamKey(1, 0, 0)));
        DashManifest expectedManifest = DashManifestTest.newDashManifest(10, DashManifestTest.newPeriod("1", 1, DashManifestTest.newAdaptationSet(2, representations[0][0])), DashManifestTest.newPeriod("4", 4, DashManifestTest.newAdaptationSet(5, representations[1][0])));
        DashManifestTest.assertManifestEquals(expectedManifest, copyManifest);
    }

    @Test
    public void testCopySkipPeriod() throws Exception {
        Representation[][][] representations = DashManifestTest.newRepresentations(3, 2, 3);
        DashManifest sourceManifest = DashManifestTest.newDashManifest(10, DashManifestTest.newPeriod("1", 1, DashManifestTest.newAdaptationSet(2, representations[0][0]), DashManifestTest.newAdaptationSet(3, representations[0][1])), DashManifestTest.newPeriod("4", 4, DashManifestTest.newAdaptationSet(5, representations[1][0]), DashManifestTest.newAdaptationSet(6, representations[1][1])), DashManifestTest.newPeriod("7", 7, DashManifestTest.newAdaptationSet(8, representations[2][0]), DashManifestTest.newAdaptationSet(9, representations[2][1])));
        DashManifest copyManifest = sourceManifest.copy(Arrays.asList(new StreamKey(0, 0, 0), new StreamKey(0, 0, 1), new StreamKey(0, 1, 2), new StreamKey(2, 0, 1), new StreamKey(2, 0, 2), new StreamKey(2, 1, 0)));
        DashManifest expectedManifest = DashManifestTest.newDashManifest(7, DashManifestTest.newPeriod("1", 1, DashManifestTest.newAdaptationSet(2, representations[0][0][0], representations[0][0][1]), DashManifestTest.newAdaptationSet(3, representations[0][1][2])), DashManifestTest.newPeriod("7", 4, DashManifestTest.newAdaptationSet(8, representations[2][0][1], representations[2][0][2]), DashManifestTest.newAdaptationSet(9, representations[2][1][0])));
        DashManifestTest.assertManifestEquals(expectedManifest, copyManifest);
    }
}

