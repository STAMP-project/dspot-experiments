package com.insightfullogic.java8.examples.chapter9;


import com.insightfullogic.java8.examples.chapter1.Album;
import com.insightfullogic.java8.examples.chapter1.Artist;
import com.insightfullogic.java8.examples.chapter1.SampleData;
import com.insightfullogic.java8.examples.chapter1.Track;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


public class AlbumLookupTest {
    interface AlbumLookupFactory extends BiFunction<List<Track>, List<Artist>, AlbumLookup> {}

    @Test
    public void albumLookedUp() {
        Album album = SampleData.aLoveSupreme;
        List<Track> trackList = album.getTrackList();
        List<Artist> musicianList = album.getMusicianList();
        AlbumLookupTest.AlbumLookupFactory completable = CompletableAlbumLookup::new;
        AlbumLookupTest.AlbumLookupFactory future = FutureAlbumLookup::new;
        Stream.of(completable, future).forEach(( factory) -> {
            AlbumLookup lookup = factory.apply(trackList, musicianList);
            System.out.println(("Testing: " + (lookup.getClass().getSimpleName())));
            Album result = lookup.lookupByName(album.getName());
            Assert.assertEquals(trackList, result.getTrackList());
            Assert.assertEquals(musicianList, result.getMusicianList());
        });
    }
}

