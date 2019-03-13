package com.insightfullogic.java8.answers.chapter9;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class ArtistAnalyzerTest {
    private final ArtistAnalyzer analyser;

    public ArtistAnalyzerTest(ArtistAnalyzer analyser) {
        this.analyser = analyser;
    }

    @Test
    public void largerGroupsAreLarger() {
        assertLargerGroup(true, "The Beatles", "John Coltrane");
    }

    @Test
    public void smallerGroupsArentLarger() {
        assertLargerGroup(false, "John Coltrane", "The Beatles");
    }
}

