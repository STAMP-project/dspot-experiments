package org.mp4parser.tools.boxes.fragment;


import java.io.IOException;
import org.junit.Test;


public class TrackFragmentRandomAccessBoxTest {
    @Test
    public void testRoundtrip() throws IOException {
        testRoundtrip(1, 1, 1);
        testRoundtrip(2, 1, 1);
        testRoundtrip(4, 1, 1);
        testRoundtrip(1, 2, 1);
        testRoundtrip(2, 2, 1);
        testRoundtrip(4, 2, 1);
        testRoundtrip(1, 4, 1);
        testRoundtrip(2, 4, 1);
        testRoundtrip(4, 4, 1);
        testRoundtrip(1, 1, 2);
        testRoundtrip(2, 1, 2);
        testRoundtrip(4, 1, 2);
        testRoundtrip(1, 2, 2);
        testRoundtrip(2, 2, 2);
        testRoundtrip(4, 2, 2);
        testRoundtrip(1, 4, 2);
        testRoundtrip(2, 4, 2);
        testRoundtrip(4, 4, 2);
        testRoundtrip(1, 1, 4);
        testRoundtrip(2, 1, 4);
        testRoundtrip(4, 1, 4);
        testRoundtrip(1, 2, 4);
        testRoundtrip(2, 2, 4);
        testRoundtrip(4, 2, 4);
        testRoundtrip(1, 4, 4);
        testRoundtrip(2, 4, 4);
        testRoundtrip(4, 4, 4);
    }
}

