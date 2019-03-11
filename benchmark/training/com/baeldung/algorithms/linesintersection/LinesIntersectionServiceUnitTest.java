package com.baeldung.algorithms.linesintersection;


import java.awt.Point;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;


public class LinesIntersectionServiceUnitTest {
    private LinesIntersectionService service = new LinesIntersectionService();

    @Test
    public void givenNotParallelLines_whenCalculatePoint_thenPresent() {
        double m1 = 0;
        double b1 = 0;
        double m2 = 1;
        double b2 = -1;
        Optional<Point> point = service.calculateIntersectionPoint(m1, b1, m2, b2);
        Assert.assertTrue(point.isPresent());
        Assert.assertEquals(point.get().getX(), 1, 0.001);
        Assert.assertEquals(point.get().getY(), 0, 0.001);
    }

    @Test
    public void givenParallelLines_whenCalculatePoint_thenEmpty() {
        double m1 = 1;
        double b1 = 0;
        double m2 = 1;
        double b2 = -1;
        Optional<Point> point = service.calculateIntersectionPoint(m1, b1, m2, b2);
        Assert.assertFalse(point.isPresent());
    }
}

