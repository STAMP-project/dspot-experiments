package aima.test.core.unit.util.math.geom.shapes;


import aima.core.util.Util;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test case for the {@code aima.core.util.math.geom} package.
 * Tests valid implementation of the {@link IGeometric2D} interface by {@link Rect2D}.
 *
 * @author Arno v. Borries
 * @author Jan Phillip Kretzschmar
 * @author Andreas Walscheid
 */
@SuppressWarnings("javadoc")
public class Rect2DTest {
    private Rect2D testRect;

    private Point2D zeroPoint;

    @Test
    public final void testRandomPoint() {
        for (int i = 0; i < 1000; i++) {
            Assert.assertTrue("Random point in rectangle.", testRect.isInsideBorder(testRect.randomPoint()));
        }
    }

    @Test
    public final void testIsInside() {
        Assert.assertFalse("Point not inside rectangle.", testRect.isInside(zeroPoint));
        Assert.assertFalse("Point on border.", testRect.isInside(new Point2D(3.0, 6.0)));
        Assert.assertTrue("Point inside rectangle.", testRect.isInside(new Point2D(4.0, 6.0)));
    }

    @Test
    public final void testIsInsideBorder() {
        Assert.assertFalse("Point not inside rectangle.", testRect.isInsideBorder(zeroPoint));
        Assert.assertTrue("Point on border.", testRect.isInsideBorder(new Point2D(3.0, 6.0)));
        Assert.assertTrue("Point inside rectangle.", testRect.isInsideBorder(new Point2D(4.0, 6.0)));
    }

    @Test
    public final void testRayCast() {
        // Static RayCast tests
        Assert.assertEquals("Ray doesn't intersect.", Double.POSITIVE_INFINITY, testRect.rayCast(new Ray2D(1.0, 1.0, (-7.0), (-8.0))), 5.0E-6);
        Assert.assertEquals("Ray intersects.", Math.sqrt(2), testRect.rayCast(new Ray2D(2.0, 3.0, 3.0, 4.0)), 5.0E-6);
        // Serial RayCast tests
        Rect2D randomRect;
        Line2D currentSide;
        Point2D randomPointOnSide;
        Point2D randomPoint;
        int i = 100;
        do {
            randomRect = new Rect2D(Util.generateRandomDoubleBetween((-1000.0), 1000.0), Util.generateRandomDoubleBetween((-1000.0), 1000.0), Util.generateRandomDoubleBetween((-1000.0), 1000.0), Util.generateRandomDoubleBetween((-1000.0), 1000.0));
            int j = 50;
            do {
                currentSide = new Line2D(randomRect.getUpperLeft(), randomRect.getUpperRight());
                randomPointOnSide = currentSide.randomPoint();
                randomPoint = new Point2D(Util.generateRandomDoubleBetween((-1000.0), 1000.0), Util.generateRandomDoubleBetween(randomPointOnSide.getY(), 1000.0));
                Assert.assertEquals("Serial rayCast test for Rect2D, upper side.", randomPoint.distance(randomPointOnSide), randomRect.rayCast(new Ray2D(randomPoint, randomPoint.vec(randomPointOnSide))), 5.0E-6);
                currentSide = new Line2D(randomRect.getLowerLeft(), randomRect.getLowerRight());
                randomPointOnSide = currentSide.randomPoint();
                randomPoint = new Point2D(Util.generateRandomDoubleBetween((-1000.0), 1000.0), Util.generateRandomDoubleBetween((-1000.0), randomPointOnSide.getY()));
                Assert.assertEquals("Serial rayCast test for Rect2D, lower side.", randomPoint.distance(randomPointOnSide), randomRect.rayCast(new Ray2D(randomPoint, randomPoint.vec(randomPointOnSide))), 5.0E-6);
                currentSide = new Line2D(randomRect.getLowerLeft(), randomRect.getUpperLeft());
                randomPointOnSide = currentSide.randomPoint();
                randomPoint = new Point2D(Util.generateRandomDoubleBetween((-1000.0), randomPointOnSide.getX()), Util.generateRandomDoubleBetween((-1000.0), 1000.0));
                Assert.assertEquals("Serial rayCast test for Rect2D, left side.", randomPoint.distance(randomPointOnSide), randomRect.rayCast(new Ray2D(randomPoint, randomPoint.vec(randomPointOnSide))), 5.0E-6);
                currentSide = new Line2D(randomRect.getLowerRight(), randomRect.getUpperRight());
                randomPointOnSide = currentSide.randomPoint();
                randomPoint = new Point2D(Util.generateRandomDoubleBetween(randomPointOnSide.getX(), 1000.0), Util.generateRandomDoubleBetween((-1000.0), 1000.0));
                Assert.assertEquals("Serial rayCast test for Rect2D, right side.", randomPoint.distance(randomPointOnSide), randomRect.rayCast(new Ray2D(randomPoint, randomPoint.vec(randomPointOnSide))), 5.0E-6);
                j -= 1;
            } while (j > 0 );
            i -= 1;
        } while (i > 0 );
    }

    @Test
    public final void testGetBounds() {
        Assert.assertNotEquals("Not the bounding rectangle.", 1.0, getUpperLeft().getX(), 5.0E-6);
        Assert.assertNotEquals("Not the bounding rectangle.", 6.0, getUpperLeft().getY(), 5.0E-6);
        Assert.assertNotEquals("Not the bounding rectangle.", 6.0, getLowerRight().getX(), 5.0E-6);
        Assert.assertNotEquals("Not the bounding rectangle.", 1.0, getLowerRight().getY(), 5.0E-6);
        Assert.assertEquals("The bounding rectangle.", 3.0, getUpperLeft().getX(), 5.0E-6);
        Assert.assertEquals("The bounding rectangle.", 8.0, getUpperLeft().getY(), 5.0E-6);
        Assert.assertEquals("The bounding rectangle.", 5.0, getLowerRight().getX(), 5.0E-6);
        Assert.assertEquals("The bounding rectangle.", 4.0, getLowerRight().getY(), 5.0E-6);
    }

    @Test
    public final void testTransform() {
        Assert.assertEquals("Transformation by identity matrix.", getUpperLeft().getX(), testRect.getUpperLeft().getX(), 5.0E-6);
        Assert.assertEquals("Transformation by identity matrix.", getUpperLeft().getY(), testRect.getUpperLeft().getY(), 5.0E-6);
        Assert.assertEquals("Transformation by identity matrix.", getLowerRight().getX(), testRect.getLowerRight().getX(), 5.0E-6);
        Assert.assertEquals("Transformation by identity matrix.", getLowerRight().getY(), testRect.getLowerRight().getY(), 5.0E-6);
        Assert.assertEquals("Translating rectangle: ULX.", getUpperLeft().getX(), 6.0, 5.0E-6);
        Assert.assertEquals("Translating rectangle: ULY.", getUpperLeft().getY(), 13.0, 5.0E-6);
        Assert.assertEquals("Translating rectangle: LRX.", getLowerRight().getX(), 8.0, 5.0E-6);
        Assert.assertEquals("Translating rectangle: LRY.", getLowerRight().getY(), 9.0, 5.0E-6);
        Assert.assertEquals("Scaling rectangle: ULX.", getUpperLeft().getX(), 6.0, 5.0E-6);
        Assert.assertEquals("Scaling rectangle: ULY.", getUpperLeft().getY(), 32.0, 5.0E-6);
        Assert.assertEquals("Scaling rectangle: ULX.", getLowerRight().getX(), 10.0, 5.0E-6);
        Assert.assertEquals("Scaling rectangle: ULY.", getLowerRight().getY(), 16.0, 5.0E-6);
    }
}

