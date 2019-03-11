package com.insightfullogic.java8.exercises.chapter5;


import SampleData.johnColtrane;
import com.insightfullogic.java8.examples.chapter1.Artist;
import com.insightfullogic.java8.examples.chapter1.SampleData;
import org.junit.Assert;
import org.junit.Test;


public class LongestNameTest {
    @Test
    public void findsLongestNameByReduce() {
        Artist artist = LongestName.byReduce(SampleData.getThreeArtists());
        Assert.assertEquals(johnColtrane, artist);
    }

    @Test
    public void findsLongestNameByCollecting() {
        Artist artist = LongestName.byCollecting(SampleData.getThreeArtists());
        Assert.assertEquals(johnColtrane, artist);
    }
}

