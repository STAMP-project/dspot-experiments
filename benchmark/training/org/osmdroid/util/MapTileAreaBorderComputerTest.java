package org.osmdroid.util;


import java.util.HashSet;
import java.util.Set;
import org.junit.Test;


/**
 * Unit tests related to {@link MapTileAreaBorderComputer}
 *
 * @since 6.0.3
 * @author Fabrice Fontaine
 */
public class MapTileAreaBorderComputerTest {
    /**
     * Checking border on one point, with modulo side effects
     */
    @Test
    public void testOnePointModulo() {
        final MapTileArea source = new MapTileArea();
        final MapTileArea dest = new MapTileArea();
        final Set<Long> set = new HashSet<>();
        final int border = 2;
        final MapTileAreaBorderComputer computer = new MapTileAreaBorderComputer(border);
        final int zoom = 5;
        final int sourceX = 1;
        final int sourceY = 31;
        source.set(zoom, sourceX, sourceY, sourceX, sourceY);
        add(set, zoom, sourceX, sourceY, border);
        computer.computeFromSource(source, dest);
        check(dest, set, zoom);
    }

    /**
     * Checking border on two contiguous points, with modulo side effects
     */
    @Test
    public void testTwoContiguousPointsModulo() {
        final MapTileArea source = new MapTileArea();
        final MapTileArea dest = new MapTileArea();
        final Set<Long> set = new HashSet<>();
        final int border = 2;
        final MapTileAreaBorderComputer computer = new MapTileAreaBorderComputer(border);
        final int zoom = 5;
        final int sourceX = 1;
        final int sourceY = 31;
        source.set(zoom, sourceX, sourceY, (sourceX + 1), sourceY);
        add(set, zoom, sourceX, sourceY, border);
        add(set, zoom, (sourceX + 1), sourceY, border);
        computer.computeFromSource(source, dest);
        check(dest, set, zoom);
    }
}

