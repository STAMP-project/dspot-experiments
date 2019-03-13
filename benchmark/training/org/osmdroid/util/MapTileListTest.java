package org.osmdroid.util;


import java.util.Random;
import junit.framework.Assert;
import org.junit.Test;


/**
 * Unit tests related to {@link MapTileList}
 *
 * @since 6.0.0
 * @author Fabrice Fontaine
 */
public class MapTileListTest {
    private static final Random random = new Random();

    @Test
    public void testGetPut() {
        final int iterations = 100;
        final int maxSize = 20;
        for (int i = 0; i < iterations; i++) {
            final int size = 1 + (MapTileListTest.random.nextInt(maxSize));
            final long[] array = new long[size];
            for (int j = 0; j < size; j++) {
                array[j] = MapTileListTest.random.nextLong();
            }
            final MapTileList list = new MapTileList();
            for (int j = 0; j < size; j++) {
                list.put(array[j]);
            }
            check(array, list);
            list.clear();
            check(new long[0], list);
        }
    }

    /**
     *
     *
     * @since 6.0.2
     */
    @Test
    public void testPutBoundingBox() {
        final int iterations = 100;
        final int zoom = 4;
        final int max = 1 << zoom;
        final MapTileList list = new MapTileList();
        for (int i = 0; i < iterations; i++) {
            final int left = MapTileListTest.random.nextInt(max);
            final int top = MapTileListTest.random.nextInt(max);
            final int right = MapTileListTest.random.nextInt(max);
            final int bottom = MapTileListTest.random.nextInt(max);
            list.clear();
            list.put(zoom, left, top, right, bottom);
            final int spanX = ((right - left) + 1) + (right < left ? max : 0);
            final int spanY = ((bottom - top) + 1) + (bottom < top ? max : 0);
            final int expectedSize = spanX * spanY;
            Assert.assertEquals(expectedSize, list.getSize());
            Assert.assertTrue(list.contains(MapTileIndex.getTileIndex(zoom, left, top)));
            Assert.assertTrue(list.contains(MapTileIndex.getTileIndex(zoom, left, bottom)));
            Assert.assertTrue(list.contains(MapTileIndex.getTileIndex(zoom, right, top)));
            Assert.assertTrue(list.contains(MapTileIndex.getTileIndex(zoom, right, bottom)));
            for (int j = 0; j < (list.getSize()); j++) {
                Assert.assertEquals(zoom, MapTileIndex.getZoom(list.get(j)));
            }
        }
    }

    /**
     *
     *
     * @since 6.0.2
     */
    @Test
    public void testPutZoom() {
        final int maxZoom = 3;
        final int left = 0;
        final int top = 0;
        final MapTileList list = new MapTileList();
        for (int zoom = 0; zoom <= maxZoom; zoom++) {
            final int max = 1 << zoom;
            final int right = max - 1;
            final int bottom = max - 1;
            list.clear();
            list.put(zoom);
            final int spanX = ((right - left) + 1) + (right < left ? max : 0);
            final int spanY = ((bottom - top) + 1) + (bottom < top ? max : 0);
            final int expectedSize = spanX * spanY;
            Assert.assertEquals(expectedSize, list.getSize());
            Assert.assertTrue(list.contains(MapTileIndex.getTileIndex(zoom, left, top)));
            Assert.assertTrue(list.contains(MapTileIndex.getTileIndex(zoom, left, bottom)));
            Assert.assertTrue(list.contains(MapTileIndex.getTileIndex(zoom, right, top)));
            Assert.assertTrue(list.contains(MapTileIndex.getTileIndex(zoom, right, bottom)));
            for (int j = 0; j < (list.getSize()); j++) {
                Assert.assertEquals(zoom, MapTileIndex.getZoom(list.get(j)));
            }
        }
    }

    /**
     *
     *
     * @since 6.0.2
     */
    @Test
    public void testEmpty() {
        final MapTileList list = new MapTileList();
        Assert.assertEquals(0, list.getSize());
        // we don't care about 1234 but about a possible side-effect NPE
        Assert.assertFalse(list.contains(1234));
    }
}

