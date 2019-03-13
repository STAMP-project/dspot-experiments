package com.baeldung.algorithms.rectanglesoverlap;


import org.junit.Assert;
import org.junit.Test;


public class RectangleUnitTest {
    @Test
    public void givenTwoOverlappingRectangles_whenisOverlappingCalled_shouldReturnTrue() {
        Rectangle rectangle1 = new Rectangle(new Point(2, 1), new Point(4, 3));
        Rectangle rectangle2 = new Rectangle(new Point(1, 1), new Point(6, 4));
        Assert.assertTrue(rectangle1.isOverlapping(rectangle2));
        rectangle1 = new Rectangle(new Point((-5), (-2)), new Point(2, 3));
        rectangle2 = new Rectangle(new Point((-2), (-1)), new Point(5, 2));
        Assert.assertTrue(rectangle1.isOverlapping(rectangle2));
        rectangle1 = new Rectangle(new Point((-5), 1), new Point(2, 4));
        rectangle2 = new Rectangle(new Point((-2), (-2)), new Point(5, 5));
        Assert.assertTrue(rectangle1.isOverlapping(rectangle2));
    }

    @Test
    public void givenTwoNonOverlappingRectangles_whenisOverlappingCalled_shouldReturnFalse() {
        Rectangle rectangle1 = new Rectangle(new Point((-5), 1), new Point((-3), 4));
        Rectangle rectangle2 = new Rectangle(new Point((-2), (-2)), new Point(5, 5));
        Assert.assertFalse(rectangle1.isOverlapping(rectangle2));
        rectangle1 = new Rectangle(new Point((-5), 1), new Point(3, 4));
        rectangle2 = new Rectangle(new Point((-2), (-2)), new Point(5, (-1)));
        Assert.assertFalse(rectangle1.isOverlapping(rectangle2));
        rectangle1 = new Rectangle(new Point((-2), 1), new Point(0, 3));
        rectangle2 = new Rectangle(new Point(3, 1), new Point(5, 4));
        Assert.assertFalse(rectangle1.isOverlapping(rectangle2));
    }
}

