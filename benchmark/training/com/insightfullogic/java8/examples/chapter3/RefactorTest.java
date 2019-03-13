package com.insightfullogic.java8.examples.chapter3;


import Refactor.LongTrackFinder;
import SampleData.aLoveSupreme;
import SampleData.sampleShortAlbum;
import com.insightfullogic.java8.examples.chapter1.Album;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import org.junit.Test;

import static Refactor.Step0;
import static Refactor.Step1;
import static Refactor.Step2;
import static Refactor.Step3;
import static Refactor.Step4;


public class RefactorTest {
    @Test
    public void allStringJoins() {
        List<Supplier<Refactor.LongTrackFinder>> finders = Arrays.<Supplier<Refactor.LongTrackFinder>>asList(Step0::new, Step1::new, Step2::new, Step3::new, Step4::new);
        List<Album> albums = Collections.unmodifiableList(Arrays.asList(aLoveSupreme, sampleShortAlbum));
        List<Album> noTracks = Collections.unmodifiableList(Arrays.asList(sampleShortAlbum));
        finders.forEach(( finder) -> {
            System.out.println(("Testing: " + (finder.toString())));
            Refactor.LongTrackFinder longTrackFinder = finder.get();
            Set<String> longTracks = longTrackFinder.findLongTracks(albums);
            assertEquals("[Acknowledgement, Resolution]", longTracks.toString());
            longTracks = longTrackFinder.findLongTracks(noTracks);
            assertTrue(longTracks.isEmpty());
        });
    }
}

