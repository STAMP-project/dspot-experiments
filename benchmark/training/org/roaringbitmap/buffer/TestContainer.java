/**
 * (c) the authors Licensed under the Apache License, Version 2.0.
 */
package org.roaringbitmap.buffer;


import MappeableArrayContainer.DEFAULT_MAX_SIZE;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

import static MappeableArrayContainer.DEFAULT_MAX_SIZE;
import static MappeableContainer.ContainerNames;


/**
 * Various tests on Container and its subclasses, ArrayContainer, RunContainer and BitmapContainer
 */
@SuppressWarnings({ "static-method" })
public class TestContainer {
    @Test
    public void testNames() {
        Assert.assertTrue(new MappeableBitmapContainer().getContainerName().equals(ContainerNames[0]));
        Assert.assertTrue(new MappeableArrayContainer().getContainerName().equals(ContainerNames[1]));
        Assert.assertTrue(new MappeableRunContainer().getContainerName().equals(ContainerNames[2]));
    }

    @Test
    public void inotTest1() {
        // Array container, range is complete
        final short[] content = new short[]{ 1, 3, 5, 7, 9 };
        MappeableContainer c = TestContainer.makeContainer(content);
        c = c.inot(0, 65536);
        final short[] s = new short[65536 - (content.length)];
        int pos = 0;
        for (int i = 0; i < 65536; ++i) {
            if ((Arrays.binarySearch(content, ((short) (i)))) < 0) {
                s[(pos++)] = ((short) (i));
            }
        }
        Assert.assertTrue(TestContainer.checkContent(c, s));
    }

    @Test
    public void inotTest10() {
        System.out.println("inotTest10");
        // Array container, inverting a range past any set bit
        final short[] content = new short[3];
        content[0] = 0;
        content[1] = 2;
        content[2] = 4;
        final MappeableContainer c = TestContainer.makeContainer(content);
        final MappeableContainer c1 = c.inot(65190, 65201);
        Assert.assertTrue((c1 instanceof MappeableArrayContainer));
        Assert.assertEquals(14, c1.getCardinality());
        Assert.assertTrue(TestContainer.checkContent(c1, new short[]{ 0, 2, 4, ((short) (65190)), ((short) (65191)), ((short) (65192)), ((short) (65193)), ((short) (65194)), ((short) (65195)), ((short) (65196)), ((short) (65197)), ((short) (65198)), ((short) (65199)), ((short) (65200)) }));
    }

    @Test
    public void inotTest10A() {
        System.out.println("inotTest10A");
        // Run container, inverting a range past any set bit
        final short[] content = new short[]{ 1, 2, 3, 4, 5 };
        MappeableContainer c = TestContainer.makeContainer(content);
        c = c.runOptimize();
        Assert.assertTrue((c instanceof MappeableRunContainer));
        final MappeableContainer c1 = c.inot(65190, 65201);
        Assert.assertTrue((c1 instanceof MappeableRunContainer));
        Assert.assertEquals(16, c1.getCardinality());
        Assert.assertTrue(TestContainer.checkContent(c1, new short[]{ 1, 2, 3, 4, 5, ((short) (65190)), ((short) (65191)), ((short) (65192)), ((short) (65193)), ((short) (65194)), ((short) (65195)), ((short) (65196)), ((short) (65197)), ((short) (65198)), ((short) (65199)), ((short) (65200)) }));
    }

    @Test
    public void inotTest1A() {
        // Run container, range is complete
        final short[] content = new short[]{ 1, 2, 3, 55, 56, 57 };
        MappeableContainer c = TestContainer.makeContainer(content);
        c = c.runOptimize();
        c = c.inot(0, 65536);
        final short[] s = new short[65536 - (content.length)];
        int pos = 0;
        for (int i = 0; i < 65536; ++i) {
            if ((Arrays.binarySearch(content, ((short) (i)))) < 0) {
                s[(pos++)] = ((short) (i));
            }
        }
        Assert.assertTrue(TestContainer.checkContent(c, s));
    }

    @Test
    public void inotTest2() {
        // Array and then Bitmap container, range is complete
        final short[] content = new short[]{ 1, 3, 5, 7, 9 };
        MappeableContainer c = TestContainer.makeContainer(content);
        c = c.inot(0, 65535);
        c = c.inot(0, 65535);
        Assert.assertTrue(TestContainer.checkContent(c, content));
    }

    @Test
    public void inotTest2A() {
        // Run, range is complete
        final short[] content = new short[]{ 1, 2, 3, 8, 9, 10, 11, 12, 13, 14 };
        MappeableContainer c = TestContainer.makeContainer(content);
        c = c.runOptimize();
        Assert.assertTrue((c instanceof MappeableRunContainer));
        c = c.inot(0, 65535);
        c = c.inot(0, 65535);
        Assert.assertTrue((c instanceof MappeableRunContainer));
        Assert.assertTrue(TestContainer.checkContent(c, content));
    }

    @Test
    public void inotTest3() {
        // Bitmap to bitmap, full range
        MappeableContainer c = new MappeableArrayContainer();
        for (int i = 0; i < 65536; i += 2) {
            c = c.add(((short) (i)));
        }
        c = c.inot(0, 65536);
        Assert.assertTrue(((c.contains(((short) (3)))) && (!(c.contains(((short) (4)))))));
        Assert.assertEquals(32768, c.getCardinality());
        c = c.inot(0, 65536);
        for (int i = 0; i < 65536; i += 2) {
            Assert.assertTrue(((c.contains(((short) (i)))) && (!(c.contains(((short) (i + 1)))))));
        }
    }

    @Test
    public void inotTest4() {
        // Array container, range is partial, result stays array
        final short[] content = new short[]{ 1, 3, 5, 7, 9 };
        MappeableContainer c = TestContainer.makeContainer(content);
        c = c.inot(4, 1000);
        Assert.assertTrue((c instanceof MappeableArrayContainer));
        Assert.assertEquals(((((999 - 4) + 1) - 3) + 2), c.getCardinality());
        c = c.inot(4, 1000);// back

        Assert.assertTrue(TestContainer.checkContent(c, content));
    }

    @Test
    public void inotTest4A() {
        // Run container, range is partial,
        final short[] content = new short[]{ 1, 2, 3, 5, 6, 7, 8 };
        MappeableContainer c = TestContainer.makeContainer(content);
        c = c.runOptimize();
        c = c.inot(4, 1000);
        Assert.assertTrue((c instanceof MappeableRunContainer));
        Assert.assertEquals(995, c.getCardinality());
        c = c.inot(4, 1000);// back

        Assert.assertTrue(TestContainer.checkContent(c, content));
    }

    @Test
    public void inotTest5() {
        System.out.println("inotTest5");
        // Bitmap container, range is partial, result stays bitmap
        final short[] content = new short[32768 - 5];
        content[0] = 0;
        content[1] = 2;
        content[2] = 4;
        content[3] = 6;
        content[4] = 8;
        for (int i = 10; i <= 32767; ++i) {
            content[((i - 10) + 5)] = ((short) (i));
        }
        MappeableContainer c = TestContainer.makeContainer(content);
        c = c.inot(4, 1000);
        Assert.assertTrue((c instanceof MappeableBitmapContainer));
        Assert.assertEquals(31773, c.getCardinality());
        c = c.inot(4, 1000);// back, as a bitmap

        Assert.assertTrue((c instanceof MappeableBitmapContainer));
        Assert.assertTrue(TestContainer.checkContent(c, content));
    }

    @Test
    public void inotTest5A() {
        System.out.println("inotTest5A");
        // Run container, range is partial, result stays run (repeats 4A somewhat)
        final short[] content = new short[32768 - 5];
        content[0] = 0;
        content[1] = 2;
        content[2] = 4;
        content[3] = 6;
        content[4] = 8;
        for (int i = 10; i <= 32767; ++i) {
            content[((i - 10) + 5)] = ((short) (i));
        }
        MappeableContainer c = TestContainer.makeContainer(content);
        c = c.runOptimize();
        c = c.inot(4, 1000);
        Assert.assertTrue((c instanceof MappeableRunContainer));
        Assert.assertEquals(31773, c.getCardinality());
        c = c.inot(4, 1000);// back, as a bitmap

        Assert.assertTrue((c instanceof MappeableRunContainer));
        Assert.assertTrue(TestContainer.checkContent(c, content));
    }

    @Test
    public void inotTest6() {
        System.out.println("inotTest6");
        // Bitmap container, range is partial and in one word, result
        // stays bitmap
        final short[] content = new short[32768 - 5];
        content[0] = 0;
        content[1] = 2;
        content[2] = 4;
        content[3] = 6;
        content[4] = 8;
        for (int i = 10; i <= 32767; ++i) {
            content[((i - 10) + 5)] = ((short) (i));
        }
        MappeableContainer c = TestContainer.makeContainer(content);
        c = c.inot(4, 9);
        Assert.assertTrue((c instanceof MappeableBitmapContainer));
        Assert.assertEquals(32762, c.getCardinality());
        c = c.inot(4, 9);// back, as a bitmap

        Assert.assertTrue((c instanceof MappeableBitmapContainer));
        Assert.assertTrue(TestContainer.checkContent(c, content));
    }

    @Test
    public void inotTest7() {
        System.out.println("inotTest7");
        // Bitmap container, range is partial, result flips to array
        final short[] content = new short[32768 - 5];
        content[0] = 0;
        content[1] = 2;
        content[2] = 4;
        content[3] = 6;
        content[4] = 8;
        for (int i = 10; i <= 32767; ++i) {
            content[((i - 10) + 5)] = ((short) (i));
        }
        MappeableContainer c = TestContainer.makeContainer(content);
        c = c.inot(5, 31001);
        if ((c.getCardinality()) <= (DEFAULT_MAX_SIZE)) {
            Assert.assertTrue((c instanceof MappeableArrayContainer));
        } else {
            Assert.assertTrue((c instanceof MappeableBitmapContainer));
        }
        Assert.assertEquals(1773, c.getCardinality());
        c = c.inot(5, 31001);// back, as a bitmap

        if ((c.getCardinality()) <= (DEFAULT_MAX_SIZE)) {
            Assert.assertTrue((c instanceof MappeableArrayContainer));
        } else {
            Assert.assertTrue((c instanceof MappeableBitmapContainer));
        }
        Assert.assertTrue(TestContainer.checkContent(c, content));
    }

    // case requiring contraction of ArrayContainer.
    @Test
    public void inotTest8() {
        System.out.println("inotTest8");
        // Array container
        final short[] content = new short[21];
        for (int i = 0; i < 18; ++i) {
            content[i] = ((short) (i));
        }
        content[18] = 21;
        content[19] = 22;
        content[20] = 23;
        MappeableContainer c = TestContainer.makeContainer(content);
        c = c.inot(5, 22);
        Assert.assertTrue((c instanceof MappeableArrayContainer));
        Assert.assertEquals(10, c.getCardinality());
        c = c.inot(5, 22);// back, as a bitmap

        Assert.assertTrue((c instanceof MappeableArrayContainer));
        Assert.assertTrue(TestContainer.checkContent(c, content));
    }

    // mostly same tests, except for not. (check original unaffected)
    @Test
    public void notTest1() {
        // Array container, range is complete
        final short[] content = new short[]{ 1, 3, 5, 7, 9 };
        final MappeableContainer c = TestContainer.makeContainer(content);
        final MappeableContainer c1 = c.not(0, 65536);
        final short[] s = new short[65536 - (content.length)];
        int pos = 0;
        for (int i = 0; i < 65536; ++i) {
            if ((Arrays.binarySearch(content, ((short) (i)))) < 0) {
                s[(pos++)] = ((short) (i));
            }
        }
        Assert.assertTrue(TestContainer.checkContent(c1, s));
        Assert.assertTrue(TestContainer.checkContent(c, content));
    }

    @Test
    public void notTest10() {
        System.out.println("notTest10");
        // Array container, inverting a range past any set bit
        // attempting to recreate a bug (but bug required extra space
        // in the array with just the right junk in it.
        final short[] content = new short[40];
        for (int i = 244; i <= 283; ++i) {
            content[(i - 244)] = ((short) (i));
        }
        final MappeableContainer c = TestContainer.makeContainer(content);
        final MappeableContainer c1 = c.not(51413, 51471);
        Assert.assertTrue((c1 instanceof MappeableArrayContainer));
        Assert.assertEquals((40 + 58), c1.getCardinality());
        final short[] rightAns = new short[98];
        for (int i = 244; i <= 283; ++i) {
            rightAns[(i - 244)] = ((short) (i));
        }
        for (int i = 51413; i <= 51470; ++i) {
            rightAns[((i - 51413) + 40)] = ((short) (i));
        }
        Assert.assertTrue(TestContainer.checkContent(c1, rightAns));
    }

    @Test
    public void notTest11() {
        System.out.println("notTest11");
        // Array container, inverting a range before any set bit
        // attempting to recreate a bug (but required extra space
        // in the array with the right junk in it.
        final short[] content = new short[40];
        for (int i = 244; i <= 283; ++i) {
            content[(i - 244)] = ((short) (i));
        }
        final MappeableContainer c = TestContainer.makeContainer(content);
        final MappeableContainer c1 = c.not(1, 59);
        Assert.assertTrue((c1 instanceof MappeableArrayContainer));
        Assert.assertEquals((40 + 58), c1.getCardinality());
        final short[] rightAns = new short[98];
        for (int i = 1; i <= 58; ++i) {
            rightAns[(i - 1)] = ((short) (i));
        }
        for (int i = 244; i <= 283; ++i) {
            rightAns[((i - 244) + 58)] = ((short) (i));
        }
        Assert.assertTrue(TestContainer.checkContent(c1, rightAns));
    }

    @Test
    public void notTest1A() {
        // Run container, range is complete
        final short[] content = new short[]{ 1, 2, 3, 6, 7, 8 };
        MappeableContainer c = TestContainer.makeContainer(content);
        c = c.runOptimize();
        final MappeableContainer c1 = c.not(0, 65536);
        final short[] s = new short[65536 - (content.length)];
        int pos = 0;
        for (int i = 0; i < 65536; ++i) {
            if ((Arrays.binarySearch(content, ((short) (i)))) < 0) {
                s[(pos++)] = ((short) (i));
            }
        }
        Assert.assertTrue(TestContainer.checkContent(c1, s));
        Assert.assertTrue(TestContainer.checkContent(c, content));
    }

    @Test
    public void notTest2() {
        // Array and then Bitmap container, range is complete
        final short[] content = new short[]{ 1, 3, 5, 7, 9 };
        final MappeableContainer c = TestContainer.makeContainer(content);
        final MappeableContainer c1 = c.not(0, 65535);
        final MappeableContainer c2 = c1.not(0, 65535);
        Assert.assertTrue(TestContainer.checkContent(c2, content));
    }

    @Test
    public void notTest2A() {
        // complete range
        final short[] content = new short[]{ 2, 3, 4, 7, 8 };
        MappeableContainer c = TestContainer.makeContainer(content);
        c = c.runOptimize();
        final MappeableContainer c1 = c.not(0, 65535);
        final MappeableContainer c2 = c1.not(0, 65535);
        Assert.assertTrue(TestContainer.checkContent(c2, content));
    }

    @Test
    public void notTest3() {
        // Bitmap to bitmap, full range
        MappeableContainer c = new MappeableArrayContainer();
        for (int i = 0; i < 65536; i += 2) {
            c = c.add(((short) (i)));
        }
        final MappeableContainer c1 = c.not(0, 65536);
        Assert.assertTrue(((c1.contains(((short) (3)))) && (!(c1.contains(((short) (4)))))));
        Assert.assertEquals(32768, c1.getCardinality());
        final MappeableContainer c2 = c1.not(0, 65536);
        for (int i = 0; i < 65536; i += 2) {
            Assert.assertTrue(((c2.contains(((short) (i)))) && (!(c2.contains(((short) (i + 1)))))));
        }
    }

    @Test
    public void notTest4() {
        System.out.println("notTest4");
        // Array container, range is partial, result stays array
        final short[] content = new short[]{ 1, 3, 5, 7, 9 };
        final MappeableContainer c = TestContainer.makeContainer(content);
        final MappeableContainer c1 = c.not(4, 1000);
        Assert.assertTrue((c1 instanceof MappeableArrayContainer));
        Assert.assertEquals(((((999 - 4) + 1) - 3) + 2), c1.getCardinality());
        final MappeableContainer c2 = c1.not(4, 1000);// back

        Assert.assertTrue(TestContainer.checkContent(c2, content));
    }

    @Test
    public void notTest4A() {
        System.out.println("notTest4A");
        // Runcontainer version
        final short[] content = new short[]{ 1, 2, 3, 5, 6, 7, 8 };
        MappeableContainer c = TestContainer.makeContainer(content);
        c = c.runOptimize();
        final MappeableContainer c1 = c.not(4, 1000);
        Assert.assertTrue((c1 instanceof MappeableRunContainer));
        Assert.assertEquals(995, c1.getCardinality());
        final MappeableContainer c2 = c1.not(4, 1000);// back

        Assert.assertTrue(TestContainer.checkContent(c2, content));
    }

    @Test
    public void notTest5() {
        System.out.println("notTest5");
        // Bitmap container, range is partial, result stays bitmap
        final short[] content = new short[32768 - 5];
        content[0] = 0;
        content[1] = 2;
        content[2] = 4;
        content[3] = 6;
        content[4] = 8;
        for (int i = 10; i <= 32767; ++i) {
            content[((i - 10) + 5)] = ((short) (i));
        }
        final MappeableContainer c = TestContainer.makeContainer(content);
        final MappeableContainer c1 = c.not(4, 1000);
        Assert.assertTrue((c1 instanceof MappeableBitmapContainer));
        Assert.assertEquals(31773, c1.getCardinality());
        final MappeableContainer c2 = c1.not(4, 1000);// back, as a bitmap

        Assert.assertTrue((c2 instanceof MappeableBitmapContainer));
        Assert.assertTrue(TestContainer.checkContent(c2, content));
    }

    @Test
    public void notTest6() {
        System.out.println("notTest6");
        // Bitmap container, range is partial and in one word, result
        // stays bitmap
        final short[] content = new short[32768 - 5];
        content[0] = 0;
        content[1] = 2;
        content[2] = 4;
        content[3] = 6;
        content[4] = 8;
        for (int i = 10; i <= 32767; ++i) {
            content[((i - 10) + 5)] = ((short) (i));
        }
        final MappeableContainer c = TestContainer.makeContainer(content);
        final MappeableContainer c1 = c.not(4, 9);
        Assert.assertTrue((c1 instanceof MappeableBitmapContainer));
        Assert.assertEquals(32762, c1.getCardinality());
        final MappeableContainer c2 = c1.not(4, 9);// back, as a bitmap

        Assert.assertTrue((c2 instanceof MappeableBitmapContainer));
        Assert.assertTrue(TestContainer.checkContent(c2, content));
    }

    @Test
    public void notTest7() {
        System.out.println("notTest7");
        // Bitmap container, range is partial, result flips to array
        final short[] content = new short[32768 - 5];
        content[0] = 0;
        content[1] = 2;
        content[2] = 4;
        content[3] = 6;
        content[4] = 8;
        for (int i = 10; i <= 32767; ++i) {
            content[((i - 10) + 5)] = ((short) (i));
        }
        final MappeableContainer c = TestContainer.makeContainer(content);
        final MappeableContainer c1 = c.not(5, 31001);
        if ((c1.getCardinality()) <= (DEFAULT_MAX_SIZE)) {
            Assert.assertTrue((c1 instanceof MappeableArrayContainer));
        } else {
            Assert.assertTrue((c1 instanceof MappeableBitmapContainer));
        }
        Assert.assertEquals(1773, c1.getCardinality());
        final MappeableContainer c2 = c1.not(5, 31001);// back, as a bitmap

        if ((c2.getCardinality()) <= (DEFAULT_MAX_SIZE)) {
            Assert.assertTrue((c2 instanceof MappeableArrayContainer));
        } else {
            Assert.assertTrue((c2 instanceof MappeableBitmapContainer));
        }
        Assert.assertTrue(TestContainer.checkContent(c2, content));
    }

    @Test
    public void notTest7A() {
        System.out.println("notTest7A");
        // Runcontainer version of notTest7
        final short[] content = new short[32768 - 5];
        content[0] = 0;
        content[1] = 2;
        content[2] = 4;
        content[3] = 6;
        content[4] = 8;
        for (int i = 10; i <= 32767; ++i) {
            content[((i - 10) + 5)] = ((short) (i));
        }
        MappeableContainer c = TestContainer.makeContainer(content);
        c = c.runOptimize();
        MappeableContainer c1 = c.not(5, 31001);
        Assert.assertTrue((c1 instanceof MappeableRunContainer));
        c1 = c1.runOptimize();// should not change

        Assert.assertTrue((c1 instanceof MappeableRunContainer));
        Assert.assertEquals(1773, c1.getCardinality());
        final MappeableContainer c2 = c1.not(5, 31001);// back, as a bitmap

        Assert.assertTrue((c2 instanceof MappeableRunContainer));
        Assert.assertTrue(TestContainer.checkContent(c2, content));
    }

    @Test
    public void notTest8() {
        System.out.println("notTest8");
        // Bitmap container, range is partial on the lower end
        final short[] content = new short[32768 - 5];
        content[0] = 0;
        content[1] = 2;
        content[2] = 4;
        content[3] = 6;
        content[4] = 8;
        for (int i = 10; i <= 32767; ++i) {
            content[((i - 10) + 5)] = ((short) (i));
        }
        final MappeableContainer c = TestContainer.makeContainer(content);
        final MappeableContainer c1 = c.not(4, 65536);
        Assert.assertTrue((c1 instanceof MappeableBitmapContainer));
        Assert.assertEquals(32773, c1.getCardinality());
        final MappeableContainer c2 = c1.not(4, 65536);// back, as a bitmap

        Assert.assertTrue((c2 instanceof MappeableBitmapContainer));
        Assert.assertTrue(TestContainer.checkContent(c2, content));
    }

    @Test
    public void notTest9() {
        System.out.println("notTest9");
        // Bitmap container, range is partial on the upper end, not
        // single word
        final short[] content = new short[32768 - 5];
        content[0] = 0;
        content[1] = 2;
        content[2] = 4;
        content[3] = 6;
        content[4] = 8;
        for (int i = 10; i <= 32767; ++i) {
            content[((i - 10) + 5)] = ((short) (i));
        }
        final MappeableContainer c = TestContainer.makeContainer(content);
        final MappeableContainer c1 = c.not(0, 65201);
        Assert.assertTrue((c1 instanceof MappeableBitmapContainer));
        Assert.assertEquals(32438, c1.getCardinality());
        final MappeableContainer c2 = c1.not(0, 65201);// back, as a bitmap

        Assert.assertTrue((c2 instanceof MappeableBitmapContainer));
        Assert.assertTrue(TestContainer.checkContent(c2, content));
    }

    @Test
    public void numberOfRuns() {
        short[] positions = new short[]{ 3, 4, 5, 10, 11, 13, 15, 62, 63, 64, 65 };
        MappeableContainer ac = new MappeableArrayContainer();
        MappeableContainer bc = new MappeableArrayContainer();
        MappeableContainer rc = new MappeableRunContainer();
        for (short position : positions) {
            ac.add(position);
            bc.add(position);
            rc.add(position);
        }
        Assert.assertEquals(rc.numberOfRuns(), ac.numberOfRuns());
        Assert.assertEquals(rc.numberOfRuns(), bc.numberOfRuns());
    }

    @Test
    public void rangeOfOnesTest1() {
        final MappeableContainer c = MappeableContainer.rangeOfOnes(4, 11);// sparse

        // assertTrue(c instanceof MappeableArrayContainer);
        Assert.assertEquals(((10 - 4) + 1), c.getCardinality());
        Assert.assertTrue(TestContainer.checkContent(c, new short[]{ 4, 5, 6, 7, 8, 9, 10 }));
    }

    @Test
    public void rangeOfOnesTest1A() {
        MappeableContainer c = MappeableContainer.rangeOfOnes(4, 11);// sparse

        c = c.runOptimize();
        Assert.assertTrue((c instanceof MappeableRunContainer));
        Assert.assertTrue(TestContainer.checkContent(c, new short[]{ 4, 5, 6, 7, 8, 9, 10 }));
    }

    @Test
    public void rangeOfOnesTest2() {
        final MappeableContainer c = MappeableContainer.rangeOfOnes(1000, 35001);// dense

        // assertTrue(c instanceof MappeableBitmapContainer);
        Assert.assertEquals(((35000 - 1000) + 1), c.getCardinality());
    }

    @Test
    public void rangeOfOnesTest2A() {
        final MappeableContainer c = MappeableContainer.rangeOfOnes(1000, 35001);// dense

        final short[] s = new short[(35000 - 1000) + 1];
        for (int i = 1000; i <= 35000; ++i) {
            s[(i - 1000)] = ((short) (i));
        }
        Assert.assertTrue(TestContainer.checkContent(c, s));
    }

    @Test
    public void rangeOfOnesTest2AA() {
        MappeableContainer c = MappeableContainer.rangeOfOnes(1000, 35001);// dense

        c = c.runOptimize();
        final short[] s = new short[(35000 - 1000) + 1];
        for (int i = 1000; i <= 35000; ++i) {
            s[(i - 1000)] = ((short) (i));
        }
        Assert.assertTrue(TestContainer.checkContent(c, s));
    }

    @Test
    public void rangeOfOnesTest3() {
        // bdry cases
        MappeableContainer.rangeOfOnes(1, DEFAULT_MAX_SIZE);
    }

    @Test
    public void rangeOfOnesTest4() {
        MappeableContainer.rangeOfOnes(1, ((DEFAULT_MAX_SIZE) + 2));
    }

    @Test
    public void transitionTest() {
        MappeableContainer c = new MappeableArrayContainer();
        for (int i = 0; i < 4096; ++i) {
            c = c.add(((short) (i)));
        }
        Assert.assertEquals(c.getCardinality(), 4096);
        Assert.assertTrue((c instanceof MappeableArrayContainer));
        for (int i = 0; i < 4096; ++i) {
            c = c.add(((short) (i)));
        }
        Assert.assertEquals(c.getCardinality(), 4096);
        Assert.assertTrue((c instanceof MappeableArrayContainer));
        c = c.add(((short) (4096)));
        Assert.assertEquals(c.getCardinality(), 4097);
        Assert.assertTrue((c instanceof MappeableBitmapContainer));
        c = c.remove(((short) (4096)));
        Assert.assertEquals(c.getCardinality(), 4096);
        Assert.assertTrue((c instanceof MappeableArrayContainer));
        c = c.runOptimize();
        Assert.assertEquals(c.getCardinality(), 4096);
        Assert.assertTrue((c instanceof MappeableRunContainer));
        c = c.inot(0, 4095);// just 4095 left

        c = c.runOptimize();
        Assert.assertEquals(c.getCardinality(), 1);
        Assert.assertTrue((c instanceof MappeableArrayContainer));
    }

    @Test
    public void testXorContainer() throws Exception {
        MappeableContainer rc1 = new MappeableRunContainer().add(2, 10).add(20, 40);
        MappeableContainer rc2 = new MappeableRunContainer().add(5, 11).add(13, 25);
        MappeableContainer bc1 = new MappeableBitmapContainer().add(10, 20);
        MappeableContainer bc2 = new MappeableBitmapContainer().add(21, 30);
        MappeableContainer ac1 = new MappeableArrayContainer().add(((short) (3))).add(((short) (79)));
        MappeableContainer ac2 = new MappeableArrayContainer().add(((short) (45))).add(((short) (56))).add(((short) (109)));
        for (Set<MappeableContainer> test : Sets.powerSet(ImmutableSet.of(rc1, rc2, bc1, bc2, ac1, ac2))) {
            Iterator<MappeableContainer> it = test.iterator();
            if ((test.size()) == 1) {
                // compare with self
                MappeableContainer x = it.next();
                Assert.assertEquals(x.xor(x).getCardinality(), x.xorCardinality(x));
            } else
                if ((test.size()) == 2) {
                    MappeableContainer x = it.next();
                    MappeableContainer y = it.next();
                    Assert.assertEquals(x.xor(y).getCardinality(), x.xorCardinality(y));
                    Assert.assertEquals(y.xor(x).getCardinality(), y.xorCardinality(x));
                }

        }
    }

    @Test(expected = NoSuchElementException.class)
    public void testFirst_Array() {
        new MappeableArrayContainer().first();
    }

    @Test(expected = NoSuchElementException.class)
    public void testLast_Array() {
        new MappeableArrayContainer().last();
    }

    @Test(expected = NoSuchElementException.class)
    public void testFirst_Run() {
        new MappeableRunContainer().first();
    }

    @Test(expected = NoSuchElementException.class)
    public void testLast_Run() {
        new MappeableRunContainer().last();
    }

    @Test(expected = NoSuchElementException.class)
    public void testFirst_Bitmap() {
        new MappeableBitmapContainer().first();
    }

    @Test(expected = NoSuchElementException.class)
    public void testLast_Bitmap() {
        new MappeableBitmapContainer().last();
    }

    @Test
    public void testFirstLast_Array() {
        testFirstLast(new MappeableArrayContainer());
    }

    @Test
    public void testFirstLast_Run() {
        testFirstLast(new MappeableRunContainer());
    }

    @Test
    public void testFirstLast_Bitmap() {
        testFirstLast(new MappeableBitmapContainer());
    }

    @Test
    public void testConsistentToString() {
        MappeableArrayContainer ac = new MappeableArrayContainer();
        MappeableBitmapContainer bc = new MappeableBitmapContainer();
        MappeableRunContainer rc = new MappeableRunContainer();
        for (short i : new short[]{ 0, 2, 17, Short.MAX_VALUE, -3, -1 }) {
            ac.add(i);
            bc.add(i);
            rc.add(i);
        }
        String expected = "{0,2,17,32767,65533,65535}";
        Assert.assertEquals(expected, ac.toString());
        Assert.assertEquals(expected, bc.toString());
        String normalizedRCstr = rc.toString().replaceAll("\\d+\\]\\[", "").replace('[', '{').replaceFirst(",\\d+\\]", "}");
        Assert.assertEquals(expected, normalizedRCstr);
    }
}

