package com.insightfullogic.java8.answers.chapter9;


import org.junit.Assert;
import org.junit.Test;


public class BlockingArtistAnalyzerTest {
    private final BlockingArtistAnalyzer analyser = new BlockingArtistAnalyzer(new FakeLookupService()::lookupArtistName);

    @Test
    public void largerGroupsAreLarger() {
        Assert.assertTrue(analyser.isLargerGroup("The Beatles", "John Coltrane"));
    }

    @Test
    public void smallerGroupsArentLarger() {
        Assert.assertFalse(analyser.isLargerGroup("John Coltrane", "The Beatles"));
    }
}

