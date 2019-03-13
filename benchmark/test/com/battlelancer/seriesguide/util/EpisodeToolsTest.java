package com.battlelancer.seriesguide.util;


import EpisodeFlags.SKIPPED;
import EpisodeFlags.UNWATCHED;
import EpisodeFlags.WATCHED;
import com.battlelancer.seriesguide.ui.episodes.EpisodeTools;
import org.junit.Assert;
import org.junit.Test;


public class EpisodeToolsTest {
    @Test
    public void test_isWatched() {
        assertThat(EpisodeTools.isWatched(WATCHED)).isTrue();
        assertThat(EpisodeTools.isWatched(SKIPPED)).isFalse();
        assertThat(EpisodeTools.isWatched(UNWATCHED)).isFalse();
    }

    @Test
    public void test_isUnwatched() {
        assertThat(EpisodeTools.isUnwatched(UNWATCHED)).isTrue();
        assertThat(EpisodeTools.isUnwatched(WATCHED)).isFalse();
        assertThat(EpisodeTools.isUnwatched(SKIPPED)).isFalse();
    }

    @Test
    public void test_validateFlags() {
        EpisodeTools.validateFlags(UNWATCHED);
        EpisodeTools.validateFlags(WATCHED);
        EpisodeTools.validateFlags(SKIPPED);
        try {
            EpisodeTools.validateFlags(123);
            Assert.fail("IllegalArgumentException not thrown");
        } catch (Exception e) {
            assertThat(e).isInstanceOf(IllegalArgumentException.class);
        }
    }
}

