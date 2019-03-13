/**
 * Copyright (C) 2016 The Android Open Source Project
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
package com.google.android.exoplayer2.extractor.ogg;


import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.io.IOException;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit test for {@link DefaultOggSeeker}.
 */
@RunWith(RobolectricTestRunner.class)
public final class DefaultOggSeekerTest {
    @Test
    public void testSetupWithUnsetEndPositionFails() {
        try {
            /* startPosition= */
            /* endPosition= */
            /* streamReader= */
            /* firstPayloadPageSize= */
            /* firstPayloadPageGranulePosition= */
            /* firstPayloadPageIsLastPage= */
            new DefaultOggSeeker(0, C.LENGTH_UNSET, new DefaultOggSeekerTest.TestStreamReader(), 1, 1, false);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // ignored
        }
    }

    @Test
    public void testSeeking() throws IOException, InterruptedException {
        Random random = new Random(0);
        for (int i = 0; i < 100; i++) {
            testSeeking(random);
        }
    }

    private static class TestStreamReader extends StreamReader {
        @Override
        protected long preparePayload(ParsableByteArray packet) {
            return 0;
        }

        @Override
        protected boolean readHeaders(ParsableByteArray packet, long position, SetupData setupData) throws IOException, InterruptedException {
            return false;
        }
    }
}

