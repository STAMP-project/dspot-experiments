/**
 * Copyright 2017 LINE Corporation
 *
 *  LINE Corporation licenses this file to you under the Apache License,
 *  version 2.0 (the "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at:
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations
 *  under the License.
 */
package com.linecorp.armeria.common;


import com.google.common.collect.ImmutableList;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;


public class MediaTypeSetTest {
    @Test
    public void getters() {
        final MediaTypeSet set = new MediaTypeSet(MediaType.HTML_UTF_8, MediaType.PLAIN_TEXT_UTF_8);
        assertThat(set).containsOnly(MediaType.HTML_UTF_8, MediaType.PLAIN_TEXT_UTF_8).hasSize(2);
    }

    @Test
    public void matchList() {
        final MediaTypeSet set = new MediaTypeSet(MediaType.HTML_UTF_8, MediaType.PLAIN_TEXT_UTF_8);
        // No ranges
        assertThat(set.match(ImmutableList.of())).isEmpty();
        // One match
        assertThat(set.match(MediaType.PLAIN_TEXT_UTF_8)).contains(MediaType.PLAIN_TEXT_UTF_8);
        assertThat(set.match(MediaType.HTML_UTF_8)).contains(MediaType.HTML_UTF_8);
        // More than one range
        assertThat(set.match(MediaType.HTML_UTF_8.withParameter("q", "0.5"), MediaType.PLAIN_TEXT_UTF_8)).contains(MediaType.PLAIN_TEXT_UTF_8);
        // Wildcard match
        assertThat(set.match(MediaType.ANY_AUDIO_TYPE, MediaType.ANY_TYPE)).contains(MediaType.HTML_UTF_8);
        // No matches
        assertThat(set.match(MediaType.WEBM_VIDEO)).isEmpty();
    }

    @Test
    public void matchHeaders() {
        final MediaTypeSet set = new MediaTypeSet(MediaType.HTML_UTF_8, MediaType.PLAIN_TEXT_UTF_8);
        // No ranges
        assertThat(set.matchHeaders()).isEmpty();
        assertThat(set.matchHeaders("")).isEmpty();
        assertThat(set.matchHeaders(ImmutableList.of())).isEmpty();
        // More than one range
        assertThat(set.matchHeaders("text/html; q=0.5, text/plain")).contains(MediaType.PLAIN_TEXT_UTF_8);
        assertThat(set.matchHeaders(ImmutableList.of("text/html, text/plain; q=0.5"))).contains(MediaType.HTML_UTF_8);
        // Wildcard match
        assertThat(set.matchHeaders("audio/*, */*")).contains(MediaType.HTML_UTF_8);
        // No matches
        assertThat(set.matchHeaders("video/webm")).isEmpty();
    }

    @Test
    public void moreSpecificRangeWins() {
        final MediaType HTML_UTF_8_LEVEL_1 = MediaType.HTML_UTF_8.withParameter("level", "1");
        final MediaTypeSet set = new MediaTypeSet(MediaType.WEBM_VIDEO, MediaType.PLAIN_TEXT_UTF_8, MediaType.HTML_UTF_8, HTML_UTF_8_LEVEL_1);
        assertThat(set.matchHeaders("*/*")).contains(MediaType.WEBM_VIDEO);
        assertThat(set.matchHeaders("*/*, text/*")).contains(MediaType.PLAIN_TEXT_UTF_8);
        assertThat(set.matchHeaders("text/*, text/html")).contains(MediaType.HTML_UTF_8);
        assertThat(set.matchHeaders("text/html, text/html; level=0")).contains(MediaType.HTML_UTF_8);
        assertThat(set.matchHeaders("text/html, text/html; level=1")).contains(HTML_UTF_8_LEVEL_1);
    }

    @Test
    public void invalidRange() {
        final MediaTypeSet set = new MediaTypeSet(MediaType.HTML_UTF_8);
        assertThat(set.matchHeaders("foo, */*")).contains(MediaType.HTML_UTF_8);
    }

    @Test
    public void invalidQValue() {
        final MediaTypeSet set = new MediaTypeSet(MediaType.HTML_UTF_8, MediaType.PLAIN_TEXT_UTF_8);
        // A bad qvalue is interpreted as 0.
        assertThat(set.matchHeaders("text/*; q=bad, text/plain; q=0.5")).contains(MediaType.PLAIN_TEXT_UTF_8);
    }

    @Test
    public void parameterMatching() {
        final MediaType HTML_US_ASCII = MediaType.HTML_UTF_8.withCharset(StandardCharsets.US_ASCII);
        final MediaTypeSet set = new MediaTypeSet(MediaType.HTML_UTF_8, HTML_US_ASCII);
        assertThat(set.matchHeaders("*/*")).contains(MediaType.HTML_UTF_8);
        // Parameter requirement must be respected.
        assertThat(set.matchHeaders("*/*; charset=UTF-8")).contains(MediaType.HTML_UTF_8);
        assertThat(set.matchHeaders("*/*; charset=US-ASCII")).contains(HTML_US_ASCII);
        // Case-insensitive comparison
        assertThat(set.matchHeaders("*/*; charset=utf-8")).contains(MediaType.HTML_UTF_8);
        assertThat(set.matchHeaders("*/*; charset=us-ascii")).contains(HTML_US_ASCII);
        // Parameter requirements did not meet.
        assertThat(set.matchHeaders("*/*; charset=UTF-8; mode=foo")).isEmpty();
    }

    @Test
    public void testAddRanges() {
        final List<MediaType> ranges = new ArrayList<>();
        // Single element without whitespaces
        MediaTypeSet.addRanges(ranges, "text/plain");
        assertThat(ranges).containsExactly(MediaType.parse("text/plain"));
        ranges.clear();
        // Multiple elements without whitespaces
        MediaTypeSet.addRanges(ranges, "text/plain,text/html");
        assertThat(ranges).containsExactly(MediaType.parse("text/plain"), MediaType.parse("text/html"));
        ranges.clear();
        // Single element with whitespaces
        MediaTypeSet.addRanges(ranges, " text/plain ");
        assertThat(ranges).containsExactly(MediaType.parse("text/plain"));
        ranges.clear();
        // Multiple elements with whitespaces
        MediaTypeSet.addRanges(ranges, " text/plain , text/html ");
        assertThat(ranges).containsExactly(MediaType.parse("text/plain"), MediaType.parse("text/html"));
        ranges.clear();
        // Quoted strings
        MediaTypeSet.addRanges(ranges, "text/plain; foo=\"b\\\"a,r\", text/html; bar=\"b\\a\\z\"");
        assertThat(ranges).containsExactly(MediaType.parse("text/plain; foo=\"b\\\"a,r\""), MediaType.parse("text/html; bar=baz"));
        ranges.clear();
        // Empty elements
        MediaTypeSet.addRanges(ranges, ",,,");
        assertThat(ranges).isEmpty();
        ranges.clear();
        // Empty elements with whitespaces
        MediaTypeSet.addRanges(ranges, " , , , ");
        assertThat(ranges).isEmpty();
        ranges.clear();
    }
}

