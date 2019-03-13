package org.mp4parser.boxes.iso14496.part12;


import java.io.IOException;
import org.junit.Test;


public class ItemLocationBoxTest {
    int[] v = new int[]{ 1, 2, 4, 8 };

    @Test
    public void testSimpleRoundTrip() throws IOException {
        for (int i : v) {
            for (int i1 : v) {
                for (int i2 : v) {
                    for (int i3 : v) {
                        testSimpleRoundTrip(i, i1, i2, i3);
                    }
                }
            }
        }
    }

    @Test
    public void testSimpleRoundWithEntriesTrip() throws IOException {
        for (int i : v) {
            for (int i1 : v) {
                for (int i2 : v) {
                    for (int i3 : v) {
                        testSimpleRoundWithEntriesTrip(i, i1, i2, i3);
                    }
                }
            }
        }
    }

    @Test
    public void testSimpleRoundWithEntriesAndExtentsTrip() throws IOException {
        for (int i : v) {
            for (int i1 : v) {
                for (int i2 : v) {
                    for (int i3 : v) {
                        testSimpleRoundWithEntriesAndExtentsTrip(i, i1, i2, i3);
                    }
                }
            }
        }
    }

    @Test
    public void testExtent() throws IOException {
        testExtent(1, 2, 4, 8);
        testExtent(2, 4, 8, 1);
        testExtent(4, 8, 1, 2);
        testExtent(8, 1, 2, 4);
    }

    @Test
    public void testItem() throws IOException {
        testItem(1, 2, 4, 8);
        testItem(2, 4, 8, 1);
        testItem(4, 8, 1, 2);
        testItem(8, 1, 2, 4);
    }

    @Test
    public void testItemVersionZero() throws IOException {
        testItemVersionZero(1, 2, 4, 8);
        testItemVersionZero(2, 4, 8, 1);
        testItemVersionZero(4, 8, 1, 2);
        testItemVersionZero(8, 1, 2, 4);
    }
}

