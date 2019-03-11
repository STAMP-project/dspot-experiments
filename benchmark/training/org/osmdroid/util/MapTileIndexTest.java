package org.osmdroid.util;


import java.util.Random;
import org.junit.Test;


/**
 * Unit tests related to {@link MapTileIndex}
 *
 * @since 6.0.0
 * @author Fabrice Fontaine
 */
public class MapTileIndexTest {
    private static final Random random = new Random();

    @Test
    public void testIndex() {
        final int iterations = 1000;
        for (int i = 0; i < iterations; i++) {
            final int zoom = getRandomZoom();
            final int x = getRandomXY(zoom);
            final int y = getRandomXY(zoom);
            final long index = MapTileIndex.getTileIndex(zoom, x, y);
            checkIndex(index, zoom, x, y);
        }
    }
}

