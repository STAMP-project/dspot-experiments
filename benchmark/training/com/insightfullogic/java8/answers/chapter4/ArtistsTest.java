package com.insightfullogic.java8.answers.chapter4;


import com.insightfullogic.java8.examples.chapter1.Artist;
import com.insightfullogic.java8.examples.chapter1.SampleData;
import java.util.Optional;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;


public class ArtistsTest {
    private final ArtistsFixed optionalExamples = new ArtistsFixed(SampleData.getThreeArtists());

    @Test
    public void indexWithinRange() {
        Optional<Artist> artist = optionalExamples.getArtist(0);
        TestCase.assertTrue(artist.isPresent());
    }

    @Test
    public void indexOutsideRange() {
        Optional<Artist> artist = optionalExamples.getArtist(4);
        Assert.assertFalse(artist.isPresent());
    }

    @Test
    public void nameIndexInsideRange() {
        String artist = optionalExamples.getArtistName(0);
        Assert.assertEquals("John Coltrane", artist);
    }

    @Test
    public void nameIndexOutsideRange() {
        String artist = optionalExamples.getArtistName(4);
        assertEquals("unknown", artist);
    }
}

