package org.roaringbitmap;


import java.util.Random;
import org.junit.Test;


public class TestRankIteratorsOfContainers {
    @Test
    public void testBitmapContainer1() {
        BitmapContainer container = new BitmapContainer();
        container.add(((short) (123)));
        container.add(((short) (65535)));
        testContainerRanksOnNext(container);
    }

    @Test
    public void testBitmapContainer2() {
        BitmapContainer container = new BitmapContainer();
        Random rnd = new Random(0);
        fillRandom(container, rnd);
        testContainerIterators(container);
    }

    @Test
    public void testBitmapContainer3() {
        BitmapContainer container = new BitmapContainer();
        fillRange(container, 0, 65535);
        testContainerIterators(container);
    }

    @Test
    public void testBitmapContainer4() {
        BitmapContainer container = new BitmapContainer();
        fillRange(container, 1024, 2048);
        fillRange(container, (8192 + 7), (24576 - 7));
        fillRange(container, 65534, 65535);
        testContainerIterators(container);
    }

    @Test
    public void testArrayContainer1() {
        ArrayContainer container = new ArrayContainer();
        container.add(((short) (123)));
        testContainerRanksOnNext(container);
    }

    @Test
    public void testArrayContainer2() {
        ArrayContainer container = new ArrayContainer();
        Random rnd = new Random(0);
        fillRandom(container, rnd);
        testContainerIterators(container);
    }

    @Test
    public void testArrayContainer3() {
        ArrayContainer container = new ArrayContainer();
        fillRange(container, 0, 1024);
        fillRange(container, 2048, 4096);
        fillRange(container, (65535 - 7), (65535 - 5));
    }

    @Test
    public void testRunContainer1() {
        RunContainer container = new RunContainer();
        container.add(((short) (123)));
        testContainerIterators(container);
    }

    @Test
    public void testRunContainer2() {
        RunContainer container = new RunContainer();
        Random rnd = new Random(0);
        fillRandom(container, rnd);
        testContainerIterators(container);
    }

    @Test
    public void testRunContainer3() {
        RunContainer container = new RunContainer();
        fillRange(container, 0, 1024);
        fillRange(container, (1024 + 3), (1024 + 5));
        fillRange(container, (1024 + 30), (1024 + 37));
        fillRange(container, (65535 - 7), (65535 - 5));
        testContainerIterators(container);
    }

    @Test
    public void testOverflow() {
        testContainerOverflow(new ArrayContainer(), false);// -- will be converted to BitmapContainer

        testContainerOverflow(new BitmapContainer(), true);
        testContainerOverflow(new RunContainer(), true);
    }
}

