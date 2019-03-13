package org.opentripplanner.common.geometry;


import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;

import static CompactLineString.STRAIGHT_LINE;
import static CompactLineString.STRAIGHT_LINE_PACKED;


public class CompactLineStringTest extends TestCase {
    @Test
    public final void testCompactString() {
        GeometryFactory gf = new GeometryFactory();
        List<Coordinate> c = new ArrayList<Coordinate>();
        double x0 = 1.111111111;
        double y0 = 0.123456789;
        double x1 = 2.0;
        double y1 = 0.0;
        c.add(new Coordinate(x0, y0));
        c.add(new Coordinate(x1, y1));
        LineString ls = gf.createLineString(c.toArray(new Coordinate[0]));
        int[] coords = CompactLineString.compactLineString(x0, y0, x1, y1, ls, false);
        TestCase.assertTrue((coords == (STRAIGHT_LINE)));// ==, not equals

        LineString ls2 = CompactLineString.uncompactLineString(x0, y0, x1, y1, coords, false);
        TestCase.assertTrue(ls.equalsExact(ls2, 1.5E-7));
        byte[] packedCoords = CompactLineString.compackLineString(x0, y0, x1, y1, ls, false);
        TestCase.assertTrue((packedCoords == (STRAIGHT_LINE_PACKED)));// ==, not equals

        ls2 = CompactLineString.uncompackLineString(x0, y0, x1, y1, packedCoords, false);
        TestCase.assertTrue(ls.equalsExact(ls2, 1.5E-7));
        c.clear();
        c.add(new Coordinate(x0, y0));
        c.add(new Coordinate((-179.99), 1.12345));
        c.add(new Coordinate(179.99, 1.12345));
        c.add(new Coordinate(x1, y1));
        ls = gf.createLineString(c.toArray(new Coordinate[0]));
        coords = CompactLineString.compactLineString(x0, y0, x1, y1, ls, false);
        TestCase.assertTrue((coords != (STRAIGHT_LINE)));
        ls2 = CompactLineString.uncompactLineString(x0, y0, x1, y1, coords, false);
        TestCase.assertTrue(ls.equalsExact(ls2, 1.5E-7));
        packedCoords = CompactLineString.compackLineString(x0, y0, x1, y1, ls, false);
        TestCase.assertTrue((packedCoords != (STRAIGHT_LINE_PACKED)));
        ls2 = CompactLineString.uncompackLineString(x0, y0, x1, y1, packedCoords, false);
        TestCase.assertTrue(ls.equalsExact(ls2, 1.5E-7));
        // Test reverse mode
        LineString lsi = ((LineString) (ls.reverse()));// The expected output

        int[] coords2 = CompactLineString.compactLineString(x1, y1, x0, y0, ls, true);
        TestCase.assertTrue((coords2 != (STRAIGHT_LINE)));
        TestCase.assertEquals(coords.length, coords2.length);
        for (int i = 0; i < (coords.length); i++)
            TestCase.assertEquals(coords[i], coords2[i]);

        ls2 = CompactLineString.uncompactLineString(x1, y1, x0, y0, coords2, true);
        TestCase.assertTrue(lsi.equalsExact(ls2, 1.5E-7));
        LineString ls3 = CompactLineString.uncompactLineString(x1, y1, x0, y0, coords, true);
        TestCase.assertTrue(lsi.equalsExact(ls3, 1.5E-7));
        byte[] packedCoords2 = CompactLineString.compackLineString(x1, y1, x0, y0, ls, true);
        TestCase.assertTrue((packedCoords2 != (STRAIGHT_LINE_PACKED)));
        TestCase.assertEquals(packedCoords.length, packedCoords2.length);
        for (int i = 0; i < (packedCoords.length); i++)
            TestCase.assertEquals(packedCoords[i], packedCoords2[i]);

        ls2 = CompactLineString.uncompackLineString(x1, y1, x0, y0, packedCoords2, true);
        TestCase.assertTrue(lsi.equalsExact(ls2, 1.5E-7));
        ls3 = CompactLineString.uncompackLineString(x1, y1, x0, y0, packedCoords, true);
        TestCase.assertTrue(lsi.equalsExact(ls2, 1.5E-7));
    }

    @Test
    public final void testDlugoszVarLenIntPacker() {
        packTest(new int[]{  }, 0);
        packTest(new int[]{ 0 }, 1);
        packTest(new int[]{ 63 }, 1);
        packTest(new int[]{ -64 }, 1);
        packTest(new int[]{ 64 }, 2);
        packTest(new int[]{ -65 }, 2);
        packTest(new int[]{ -8192 }, 2);
        packTest(new int[]{ -8193 }, 3);
        packTest(new int[]{ 8191 }, 2);
        packTest(new int[]{ 8192 }, 3);
        packTest(new int[]{ -1048576 }, 3);
        packTest(new int[]{ -1048577 }, 4);
        packTest(new int[]{ 1048575 }, 3);
        packTest(new int[]{ 1048576 }, 4);
        packTest(new int[]{ -67108864 }, 4);
        packTest(new int[]{ -67108865 }, 5);
        packTest(new int[]{ 67108863 }, 4);
        packTest(new int[]{ 67108864 }, 5);
        packTest(new int[]{ Integer.MAX_VALUE }, 5);
        packTest(new int[]{ Integer.MIN_VALUE }, 5);
        packTest(new int[]{ 0, 0 }, 2);
        packTest(new int[]{ 0, 0, 0 }, 3);
        packTest(new int[]{ 8100, 8200, 8300 }, 8);
    }
}

