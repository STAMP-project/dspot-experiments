package com.insightfullogic.java8.exercises.chapter3;


import com.insightfullogic.java8.examples.chapter1.Album;
import com.insightfullogic.java8.examples.chapter1.SampleData;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


public class Question1Test {
    @Test
    public void addsEmptyList() {
        int result = Question1.addUp(Stream.empty());
        Assert.assertEquals(0, result);
    }

    @Test
    public void addsListWithValues() {
        int result = Question1.addUp(Stream.of(1, 3, (-2)));
        Assert.assertEquals(2, result);
    }

    @Test
    public void extractsNamesAndOriginsOfArtists() {
        List<String> namesAndOrigins = Question1.getNamesAndOrigins(SampleData.getThreeArtists());
        Assert.assertEquals(Arrays.asList("John Coltrane", "US", "John Lennon", "UK", "The Beatles", "UK"), namesAndOrigins);
    }

    @Test
    public void findsShortAlbums() {
        List<Album> input = Arrays.asList(SampleData.manyTrackAlbum, SampleData.sampleShortAlbum, SampleData.aLoveSupreme);
        List<Album> result = Question1.getAlbumsWithAtMostThreeTracks(input);
        Assert.assertEquals(Arrays.asList(SampleData.sampleShortAlbum, SampleData.aLoveSupreme), result);
    }
}

