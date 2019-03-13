package com.insightfullogic.java8.examples.chapter9;


import SampleData.johnLennon;
import com.insightfullogic.java8.examples.chapter1.Artist;
import com.insightfullogic.java8.examples.chapter1.SampleData;
import org.junit.Assert;
import org.junit.Test;


public class RxExamplesTest {
    @Test
    public void filtersAlbums() throws InterruptedException {
        RxExamples examples = new RxExamples(SampleData.getThreeArtists());
        Artist artist = examples.search("John", "UK", 5).toBlockingObservable().single();
        Assert.assertEquals(johnLennon, artist);
    }
}

