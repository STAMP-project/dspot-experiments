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
package com.google.android.exoplayer2.text.webvtt;


import java.util.ArrayList;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit test for {@link WebvttSubtitle}.
 */
@RunWith(RobolectricTestRunner.class)
public class WebvttSubtitleTest {
    private static final String FIRST_SUBTITLE_STRING = "This is the first subtitle.";

    private static final String SECOND_SUBTITLE_STRING = "This is the second subtitle.";

    private static final String FIRST_AND_SECOND_SUBTITLE_STRING = ((WebvttSubtitleTest.FIRST_SUBTITLE_STRING) + "\n") + (WebvttSubtitleTest.SECOND_SUBTITLE_STRING);

    private static final WebvttSubtitle emptySubtitle = new WebvttSubtitle(Collections.emptyList());

    private static final WebvttSubtitle simpleSubtitle;

    static {
        ArrayList<WebvttCue> simpleSubtitleCues = new ArrayList<>();
        WebvttCue firstCue = new WebvttCue(1000000, 2000000, WebvttSubtitleTest.FIRST_SUBTITLE_STRING);
        simpleSubtitleCues.add(firstCue);
        WebvttCue secondCue = new WebvttCue(3000000, 4000000, WebvttSubtitleTest.SECOND_SUBTITLE_STRING);
        simpleSubtitleCues.add(secondCue);
        simpleSubtitle = new WebvttSubtitle(simpleSubtitleCues);
    }

    private static final WebvttSubtitle overlappingSubtitle;

    static {
        ArrayList<WebvttCue> overlappingSubtitleCues = new ArrayList<>();
        WebvttCue firstCue = new WebvttCue(1000000, 3000000, WebvttSubtitleTest.FIRST_SUBTITLE_STRING);
        overlappingSubtitleCues.add(firstCue);
        WebvttCue secondCue = new WebvttCue(2000000, 4000000, WebvttSubtitleTest.SECOND_SUBTITLE_STRING);
        overlappingSubtitleCues.add(secondCue);
        overlappingSubtitle = new WebvttSubtitle(overlappingSubtitleCues);
    }

    private static final WebvttSubtitle nestedSubtitle;

    static {
        ArrayList<WebvttCue> nestedSubtitleCues = new ArrayList<>();
        WebvttCue firstCue = new WebvttCue(1000000, 4000000, WebvttSubtitleTest.FIRST_SUBTITLE_STRING);
        nestedSubtitleCues.add(firstCue);
        WebvttCue secondCue = new WebvttCue(2000000, 3000000, WebvttSubtitleTest.SECOND_SUBTITLE_STRING);
        nestedSubtitleCues.add(secondCue);
        nestedSubtitle = new WebvttSubtitle(nestedSubtitleCues);
    }

    @Test
    public void testEventCount() {
        assertThat(WebvttSubtitleTest.emptySubtitle.getEventTimeCount()).isEqualTo(0);
        assertThat(WebvttSubtitleTest.simpleSubtitle.getEventTimeCount()).isEqualTo(4);
        assertThat(WebvttSubtitleTest.overlappingSubtitle.getEventTimeCount()).isEqualTo(4);
        assertThat(WebvttSubtitleTest.nestedSubtitle.getEventTimeCount()).isEqualTo(4);
    }

    @Test
    public void testSimpleSubtitleEventTimes() {
        testSubtitleEventTimesHelper(WebvttSubtitleTest.simpleSubtitle);
    }

    @Test
    public void testSimpleSubtitleEventIndices() {
        testSubtitleEventIndicesHelper(WebvttSubtitleTest.simpleSubtitle);
    }

    @Test
    public void testSimpleSubtitleText() {
        // Test before first subtitle
        assertSingleCueEmpty(WebvttSubtitleTest.simpleSubtitle.getCues(0));
        assertSingleCueEmpty(WebvttSubtitleTest.simpleSubtitle.getCues(500000));
        assertSingleCueEmpty(WebvttSubtitleTest.simpleSubtitle.getCues(999999));
        // Test first subtitle
        assertSingleCueTextEquals(WebvttSubtitleTest.FIRST_SUBTITLE_STRING, WebvttSubtitleTest.simpleSubtitle.getCues(1000000));
        assertSingleCueTextEquals(WebvttSubtitleTest.FIRST_SUBTITLE_STRING, WebvttSubtitleTest.simpleSubtitle.getCues(1500000));
        assertSingleCueTextEquals(WebvttSubtitleTest.FIRST_SUBTITLE_STRING, WebvttSubtitleTest.simpleSubtitle.getCues(1999999));
        // Test after first subtitle, before second subtitle
        assertSingleCueEmpty(WebvttSubtitleTest.simpleSubtitle.getCues(2000000));
        assertSingleCueEmpty(WebvttSubtitleTest.simpleSubtitle.getCues(2500000));
        assertSingleCueEmpty(WebvttSubtitleTest.simpleSubtitle.getCues(2999999));
        // Test second subtitle
        assertSingleCueTextEquals(WebvttSubtitleTest.SECOND_SUBTITLE_STRING, WebvttSubtitleTest.simpleSubtitle.getCues(3000000));
        assertSingleCueTextEquals(WebvttSubtitleTest.SECOND_SUBTITLE_STRING, WebvttSubtitleTest.simpleSubtitle.getCues(3500000));
        assertSingleCueTextEquals(WebvttSubtitleTest.SECOND_SUBTITLE_STRING, WebvttSubtitleTest.simpleSubtitle.getCues(3999999));
        // Test after second subtitle
        assertSingleCueEmpty(WebvttSubtitleTest.simpleSubtitle.getCues(4000000));
        assertSingleCueEmpty(WebvttSubtitleTest.simpleSubtitle.getCues(4500000));
        assertSingleCueEmpty(WebvttSubtitleTest.simpleSubtitle.getCues(Long.MAX_VALUE));
    }

    @Test
    public void testOverlappingSubtitleEventTimes() {
        testSubtitleEventTimesHelper(WebvttSubtitleTest.overlappingSubtitle);
    }

    @Test
    public void testOverlappingSubtitleEventIndices() {
        testSubtitleEventIndicesHelper(WebvttSubtitleTest.overlappingSubtitle);
    }

    @Test
    public void testOverlappingSubtitleText() {
        // Test before first subtitle
        assertSingleCueEmpty(WebvttSubtitleTest.overlappingSubtitle.getCues(0));
        assertSingleCueEmpty(WebvttSubtitleTest.overlappingSubtitle.getCues(500000));
        assertSingleCueEmpty(WebvttSubtitleTest.overlappingSubtitle.getCues(999999));
        // Test first subtitle
        assertSingleCueTextEquals(WebvttSubtitleTest.FIRST_SUBTITLE_STRING, WebvttSubtitleTest.overlappingSubtitle.getCues(1000000));
        assertSingleCueTextEquals(WebvttSubtitleTest.FIRST_SUBTITLE_STRING, WebvttSubtitleTest.overlappingSubtitle.getCues(1500000));
        assertSingleCueTextEquals(WebvttSubtitleTest.FIRST_SUBTITLE_STRING, WebvttSubtitleTest.overlappingSubtitle.getCues(1999999));
        // Test after first and second subtitle
        assertSingleCueTextEquals(WebvttSubtitleTest.FIRST_AND_SECOND_SUBTITLE_STRING, WebvttSubtitleTest.overlappingSubtitle.getCues(2000000));
        assertSingleCueTextEquals(WebvttSubtitleTest.FIRST_AND_SECOND_SUBTITLE_STRING, WebvttSubtitleTest.overlappingSubtitle.getCues(2500000));
        assertSingleCueTextEquals(WebvttSubtitleTest.FIRST_AND_SECOND_SUBTITLE_STRING, WebvttSubtitleTest.overlappingSubtitle.getCues(2999999));
        // Test second subtitle
        assertSingleCueTextEquals(WebvttSubtitleTest.SECOND_SUBTITLE_STRING, WebvttSubtitleTest.overlappingSubtitle.getCues(3000000));
        assertSingleCueTextEquals(WebvttSubtitleTest.SECOND_SUBTITLE_STRING, WebvttSubtitleTest.overlappingSubtitle.getCues(3500000));
        assertSingleCueTextEquals(WebvttSubtitleTest.SECOND_SUBTITLE_STRING, WebvttSubtitleTest.overlappingSubtitle.getCues(3999999));
        // Test after second subtitle
        assertSingleCueEmpty(WebvttSubtitleTest.overlappingSubtitle.getCues(4000000));
        assertSingleCueEmpty(WebvttSubtitleTest.overlappingSubtitle.getCues(4500000));
        assertSingleCueEmpty(WebvttSubtitleTest.overlappingSubtitle.getCues(Long.MAX_VALUE));
    }

    @Test
    public void testNestedSubtitleEventTimes() {
        testSubtitleEventTimesHelper(WebvttSubtitleTest.nestedSubtitle);
    }

    @Test
    public void testNestedSubtitleEventIndices() {
        testSubtitleEventIndicesHelper(WebvttSubtitleTest.nestedSubtitle);
    }

    @Test
    public void testNestedSubtitleText() {
        // Test before first subtitle
        assertSingleCueEmpty(WebvttSubtitleTest.nestedSubtitle.getCues(0));
        assertSingleCueEmpty(WebvttSubtitleTest.nestedSubtitle.getCues(500000));
        assertSingleCueEmpty(WebvttSubtitleTest.nestedSubtitle.getCues(999999));
        // Test first subtitle
        assertSingleCueTextEquals(WebvttSubtitleTest.FIRST_SUBTITLE_STRING, WebvttSubtitleTest.nestedSubtitle.getCues(1000000));
        assertSingleCueTextEquals(WebvttSubtitleTest.FIRST_SUBTITLE_STRING, WebvttSubtitleTest.nestedSubtitle.getCues(1500000));
        assertSingleCueTextEquals(WebvttSubtitleTest.FIRST_SUBTITLE_STRING, WebvttSubtitleTest.nestedSubtitle.getCues(1999999));
        // Test after first and second subtitle
        assertSingleCueTextEquals(WebvttSubtitleTest.FIRST_AND_SECOND_SUBTITLE_STRING, WebvttSubtitleTest.nestedSubtitle.getCues(2000000));
        assertSingleCueTextEquals(WebvttSubtitleTest.FIRST_AND_SECOND_SUBTITLE_STRING, WebvttSubtitleTest.nestedSubtitle.getCues(2500000));
        assertSingleCueTextEquals(WebvttSubtitleTest.FIRST_AND_SECOND_SUBTITLE_STRING, WebvttSubtitleTest.nestedSubtitle.getCues(2999999));
        // Test first subtitle
        assertSingleCueTextEquals(WebvttSubtitleTest.FIRST_SUBTITLE_STRING, WebvttSubtitleTest.nestedSubtitle.getCues(3000000));
        assertSingleCueTextEquals(WebvttSubtitleTest.FIRST_SUBTITLE_STRING, WebvttSubtitleTest.nestedSubtitle.getCues(3500000));
        assertSingleCueTextEquals(WebvttSubtitleTest.FIRST_SUBTITLE_STRING, WebvttSubtitleTest.nestedSubtitle.getCues(3999999));
        // Test after second subtitle
        assertSingleCueEmpty(WebvttSubtitleTest.nestedSubtitle.getCues(4000000));
        assertSingleCueEmpty(WebvttSubtitleTest.nestedSubtitle.getCues(4500000));
        assertSingleCueEmpty(WebvttSubtitleTest.nestedSubtitle.getCues(Long.MAX_VALUE));
    }
}

