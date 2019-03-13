package org.osmdroid.util;


import org.junit.Test;


/**
 *
 *
 * @since 6.0.0
 * @author Fabrice Fontaine
 */
public class SegmentIntersectionTest {
    @Test
    public void test_intersection() {
        // simple perpendicular segments
        test_intersection(new RectL(0, 500, 1000, 500), new RectL(500, 0, 500, 1000), new PointL(500, 500));
        // simple intersecting segments
        test_intersection(new RectL(0, 0, 100, 100), new RectL(0, 50, 100, 50), new PointL(50, 50));
        // parallel segments
        test_intersection(new RectL(0, 0, 100, 0), new RectL(0, 50, 100, 50), null);
        // overlapping parallel segments
        test_intersection(new RectL(0, 0, 100, 100), new RectL(50, 50, 1000, 1000), new PointL(75, 75));
        // non intersecting segments
        test_intersection(new RectL(0, 0, 100, 100), new RectL(0, 500, 100, 500), null);
        // high values
        test_intersection(new RectL(0, 0, (1 << 30), (1 << 30)), new RectL(0, (1 << 29), (1 << 30), (1 << 29)), new PointL((1 << 29), (1 << 29)));
        // actual numbers
        test_intersection(new RectL((-33554178), 402653480, (-33554178), 234881320), new RectL((-268435456), 268435455, 268435455, 268435455), new PointL((-33554178), 268435455));
    }
}

