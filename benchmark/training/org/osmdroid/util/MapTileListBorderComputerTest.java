package org.osmdroid.util;


import java.util.HashSet;
import java.util.Set;
import org.junit.Test;


/**
 * Unit tests related to {@link MapTileListBorderComputer}
 *
 * @since 6.0.2
 * @author Fabrice Fontaine
 * @deprecated Use {@link MapTileAreaBorderComputerTest} instead
 */
@Deprecated
public class MapTileListBorderComputerTest {
    /**
     * Checking border on one point, with modulo side effects, in "include all" mode on
     */
    @Test
    public void testOnePointModuloInclude() {
        final MapTileList source = new MapTileList();
        final MapTileList dest = new MapTileList();
        final Set<Long> set = new HashSet<>();
        final int border = 2;
        final MapTileListBorderComputer computer = new MapTileListBorderComputer(border, true);
        final int zoom = 5;
        final int sourceX = 1;
        final int sourceY = 31;
        source.put(MapTileIndex.getTileIndex(zoom, sourceX, sourceY));
        add(set, zoom, sourceX, sourceY, border);
        computer.computeFromSource(source, dest);
        check(dest, set, zoom);
    }

    /**
     * Checking border on one point, with modulo side effects, in "include all" mode off
     */
    @Test
    public void testOnePointModulo() {
        final MapTileList source = new MapTileList();
        final MapTileList dest = new MapTileList();
        final Set<Long> set = new HashSet<>();
        final int border = 2;
        final MapTileListBorderComputer computer = new MapTileListBorderComputer(border, false);
        final int zoom = 5;
        final int sourceX = 1;
        final int sourceY = 31;
        source.put(MapTileIndex.getTileIndex(zoom, sourceX, sourceY));
        add(set, zoom, sourceX, sourceY, border);
        set.remove(MapTileIndex.getTileIndex(zoom, sourceX, sourceY));
        computer.computeFromSource(source, dest);
        check(dest, set, zoom);
    }

    /**
     * Checking border on two contiguous points, with modulo side effects, in "include all" mode on
     */
    @Test
    public void testTwoContiguousPointsModuloInclude() {
        final MapTileList source = new MapTileList();
        final MapTileList dest = new MapTileList();
        final Set<Long> set = new HashSet<>();
        final int border = 2;
        final MapTileListBorderComputer computer = new MapTileListBorderComputer(border, true);
        final int zoom = 5;
        final int sourceX = 1;
        final int sourceY = 31;
        source.put(MapTileIndex.getTileIndex(zoom, sourceX, sourceY));
        source.put(MapTileIndex.getTileIndex(zoom, (sourceX + 1), sourceY));
        add(set, zoom, sourceX, sourceY, border);
        add(set, zoom, (sourceX + 1), sourceY, border);
        computer.computeFromSource(source, dest);
        check(dest, set, zoom);
    }
}

