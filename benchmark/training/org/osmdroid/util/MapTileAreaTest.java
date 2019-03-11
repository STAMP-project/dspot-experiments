package org.osmdroid.util;


import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import junit.framework.Assert;
import org.junit.Test;


/**
 * Unit tests related to {@link MapTileArea}
 *
 * @since 6.0.3
 * @author Fabrice Fontaine
 */
public class MapTileAreaTest {
    private Random mRandom = new Random();

    @Test
    public void testSetAll() {
        final MapTileArea area = new MapTileArea();
        int zoom;
        zoom = 0;
        area.set(zoom, (-10), (-100), 50, 90);
        checkAll(zoom, area);
        zoom = 1;
        area.set(zoom, (-10), (-100), 50, 90);
        checkAll(zoom, area);
        zoom = 2;
        area.set(zoom, (-10), (-100), 50, 90);
        checkAll(zoom, area);
    }

    @Test
    public void testSize() {
        final MapTileArea area = new MapTileArea();
        for (int zoom = 0; zoom <= (TileSystem.getMaximumZoomLevel()); zoom++) {
            final int mapTileUpperBound = getMapTileUpperBound(zoom);
            final long size = ((long) (mapTileUpperBound)) * mapTileUpperBound;
            if (size >= (Integer.MAX_VALUE)) {
                return;
            }
            setNewWorld(area, zoom);
            Assert.assertEquals(size, area.size());
            Assert.assertTrue(area.iterator().hasNext());
        }
    }

    @Test
    public void testCorners() {
        final MapTileArea area = new MapTileArea();
        for (int zoom = 0; zoom <= (TileSystem.getMaximumZoomLevel()); zoom++) {
            final int mapTileUpperBound = getMapTileUpperBound(zoom);
            final int max = mapTileUpperBound - 1;
            setNewWorld(area, zoom);
            Assert.assertTrue(area.contains(MapTileIndex.getTileIndex(zoom, 0, 0)));
            Assert.assertTrue(area.contains(MapTileIndex.getTileIndex(zoom, 0, max)));
            Assert.assertTrue(area.contains(MapTileIndex.getTileIndex(zoom, max, max)));
            Assert.assertTrue(area.contains(MapTileIndex.getTileIndex(zoom, max, 0)));
        }
    }

    @Test
    public void testNextSize() {
        final Set<Long> set = new HashSet<>();
        final MapTileArea area = new MapTileArea();
        for (int zoom = 0; zoom <= (TileSystem.getMaximumZoomLevel()); zoom++) {
            final int mapTileUpperBound = getMapTileUpperBound(zoom);
            final long size = ((long) (mapTileUpperBound)) * mapTileUpperBound;
            if (size >= (Integer.MAX_VALUE)) {
                return;
            }
            if (size >= 1000) {
                // let's be reasonable
                return;
            }
            setNewWorld(area, zoom);
            Assert.assertEquals(size, area.size());
            int count = 0;
            set.clear();
            for (final long mapTileIndex : area) {
                count++;
                Assert.assertEquals(zoom, MapTileIndex.getZoom(mapTileIndex));
                final int x = MapTileIndex.getX(mapTileIndex);
                final int y = MapTileIndex.getY(mapTileIndex);
                Assert.assertTrue(((x >= 0) && (x < mapTileUpperBound)));
                Assert.assertTrue(((y >= 0) && (y < mapTileUpperBound)));
                set.add(mapTileIndex);
            }
            Assert.assertEquals(size, set.size());
            Assert.assertEquals(size, count);
        }
    }

    @Test
    public void testPerformances() {
        final MapTileList list = new MapTileList();
        final MapTileArea area = new MapTileArea();
        final int zoom = 10;
        final int size = 10;
        long start;
        long end;
        long duration1;
        long duration2;
        // checking if area is faster than list during initialization
        list.ensureCapacity((size * size));
        start = System.nanoTime();
        list.put(zoom, 0, 0, (size - 1), (size - 1));
        end = System.nanoTime();
        duration1 = end - start;
        start = System.nanoTime();
        area.set(zoom, 0, 0, (size - 1), (size - 1));
        end = System.nanoTime();
        duration2 = end - start;
        checkDuration(duration1, duration2);
        // find all items of the list
        checkContainDuration(zoom, zoom, (size - 1), (size - 1), list, area, true);
        // cannot find any item on lower zooms
        checkContainDuration(0, (zoom - 1), 0, 0, list, area, false);
        // cannot find any item on higher zooms
        checkContainDuration((zoom + 1), TileSystem.getMaximumZoomLevel(), 0, 0, list, area, false);
    }
}

