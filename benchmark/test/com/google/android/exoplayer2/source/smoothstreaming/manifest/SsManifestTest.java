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
package com.google.android.exoplayer2.source.smoothstreaming.manifest;


import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.mp4.TrackEncryptionBox;
import com.google.android.exoplayer2.offline.StreamKey;
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifest.ProtectionElement;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit tests for {@link SsManifest}.
 */
@RunWith(RobolectricTestRunner.class)
public class SsManifestTest {
    private static final ProtectionElement DUMMY_PROTECTION_ELEMENT = new ProtectionElement(C.WIDEVINE_UUID, new byte[0], new TrackEncryptionBox[0]);

    @Test
    public void testCopy() throws Exception {
        Format[][] formats = SsManifestTest.newFormats(2, 3);
        SsManifest sourceManifest = SsManifestTest.newSsManifest(SsManifestTest.newStreamElement("1", formats[0]), SsManifestTest.newStreamElement("2", formats[1]));
        List<StreamKey> keys = Arrays.asList(new StreamKey(0, 0), new StreamKey(0, 2), new StreamKey(1, 0));
        // Keys don't need to be in any particular order
        Collections.shuffle(keys, new Random(0));
        SsManifest copyManifest = sourceManifest.copy(keys);
        SsManifest expectedManifest = SsManifestTest.newSsManifest(SsManifestTest.newStreamElement("1", formats[0][0], formats[0][2]), SsManifestTest.newStreamElement("2", formats[1][0]));
        SsManifestTest.assertManifestEquals(expectedManifest, copyManifest);
    }

    @Test
    public void testCopyRemoveStreamElement() throws Exception {
        Format[][] formats = SsManifestTest.newFormats(2, 3);
        SsManifest sourceManifest = SsManifestTest.newSsManifest(SsManifestTest.newStreamElement("1", formats[0]), SsManifestTest.newStreamElement("2", formats[1]));
        List<StreamKey> keys = Collections.singletonList(new StreamKey(1, 0));
        SsManifest copyManifest = sourceManifest.copy(keys);
        SsManifest expectedManifest = SsManifestTest.newSsManifest(SsManifestTest.newStreamElement("2", formats[1][0]));
        SsManifestTest.assertManifestEquals(expectedManifest, copyManifest);
    }
}

