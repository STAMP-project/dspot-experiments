package aima.test.core.unit.util.math.geom.shapes;


import TransformMatrix2D.UNITY_MATRIX;
import aima.core.util.Util;
import aima.core.util.math.geom.shapes.Line2D;
import aima.core.util.math.geom.shapes.Point2D;
import aima.core.util.math.geom.shapes.Ray2D;
import aima.core.util.math.geom.shapes.Vector2D;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test case for the {@code aima.core.util.math.geom} package.
 * Tests valid implementation of the {@link IGeometric2D} interface by {@link Line2D}.
 *
 * @author Arno v. Borries
 * @author Jan Phillip Kretzschmar
 * @author Andreas Walscheid
 */
@SuppressWarnings("javadoc")
public class Line2DTest {
    private Line2D testLine;

    private Line2D testLine2;

    private Line2D testLine3;

    private Line2D testLine4;

    private Point2D zeroPoint;

    @Test
    public final void testRandomPoint() {
        Assert.assertTrue("Random point on horizontal line", testLine4.isInsideBorder(testLine4.randomPoint()));
        for (int i = 0; i < 1000; i++) {
            Assert.assertTrue("Random point on line.", testLine.isInsideBorder(testLine.randomPoint()));
        }
    }

    @Test
    public final void testIsInside() {
        Assert.assertFalse("Point not on line.", testLine.isInside(zeroPoint));
        Assert.assertFalse("Point on line.", testLine.isInside(new Point2D(3.0, 4.0)));
    }

    @Test
    public final void testIsInsideBorder() {
        Assert.assertFalse("Point not on line.", testLine.isInsideBorder(zeroPoint));
        Assert.assertTrue("Point on line.", testLine.isInsideBorder(new Point2D(3.0, 4.0)));
        Assert.assertTrue("Point on line.", testLine2.isInsideBorder(new Point2D(6.0, 2.0)));
    }

    @Test
    public final void testRayCast() {
        // Static rayCast tests
        Assert.assertEquals("Ray doesn't intersect.", Double.POSITIVE_INFINITY, testLine.rayCast(new Ray2D(1.0, 1.0, (-7.0), (-8.0))), 5.0E-6);
        Assert.assertEquals("Ray intersects.", Math.sqrt(2), testLine.rayCast(new Ray2D(2.0, 5.0, 4.0, 3.0)), 5.0E-6);
        Assert.assertEquals("Ray intersects.", 6, testLine2.rayCast(new Ray2D(zeroPoint, Vector2D.X_VECTOR)), 5.0E-6);
        Assert.assertEquals("Ray intersects.", 3.0, testLine2.rayCast(new Ray2D(new Point2D(3.0, 3.0), Vector2D.X_VECTOR)), 5.0E-6);
        Assert.assertEquals("Ray intersects.", Double.POSITIVE_INFINITY, testLine2.rayCast(new Ray2D(new Point2D(6.0, 2.0), Vector2D.X_VECTOR)), 5.0E-6);
        Assert.assertEquals("Ray intersects.", 3.6, testLine3.rayCast(new Ray2D(zeroPoint, Vector2D.X_VECTOR)), 5.0E-6);
        // Serial RayCast tests
        Point2D randomPoint;
        Line2D randomLine;
        Point2D randomPointOnLine;
        int counter = 5000;
        // generate a random point and another random point on a random Line and compare the distance between the two with a rayCast from the former towards the latter.
        do {
            randomLine = new Line2D(Util.generateRandomDoubleBetween((-1000.0), 1000.0), Util.generateRandomDoubleBetween((-1000.0), 1000.0), Util.generateRandomDoubleBetween((-1000.0), 1000.0), Util.generateRandomDoubleBetween((-1000.0), 1000.0));
            randomPointOnLine = randomLine.randomPoint();
            randomPoint = new Point2D(Util.generateRandomDoubleBetween((-1000.0), 1000.0), Util.generateRandomDoubleBetween((-1000.0), 1000.0));
            // System.out.printf("Line2D rayCast test no. %d: Line (%.2f,%.2f,%.2f,%.2f), point (%.2f,%.2f), point on line (%.2f,%.2f), distance: %.2f.\n", counter, randomLine.getStart().getX(), randomLine.getStart().getY(), randomLine.getEnd().getX(), randomLine.getEnd().getY(), randomPoint.getX(), randomPoint.getY(), randomPointOnLine.getX(), randomPointOnLine.getY(), randomPoint.distance(randomPointOnLine));
            Assert.assertEquals("Serial rayCast test for Line2D.", randomPoint.distance(randomPointOnLine), randomLine.rayCast(new Ray2D(randomPoint, randomPoint.vec(randomPointOnLine))), 5.0E-6);
            counter -= 1;
        } while (counter > 0 );
    }

    @Test
    public final void testGetBounds() {
        Assert.assertNotEquals("Not the bounding rectangle.", 1.0, testLine.getBounds().getUpperLeft().getX(), 5.0E-6);
        Assert.assertNotEquals("Not the bounding rectangle.", 6.0, testLine.getBounds().getUpperLeft().getY(), 5.0E-6);
        Assert.assertNotEquals("Not the bounding rectangle.", 6.0, testLine.getBounds().getLowerRight().getX(), 5.0E-6);
        Assert.assertNotEquals("Not the bounding rectangle.", 1.0, testLine.getBounds().getLowerRight().getY(), 5.0E-6);
        Assert.assertEquals("The bounding rectangle.", 2.0, testLine.getBounds().getUpperLeft().getX(), 5.0E-6);
        Assert.assertEquals("The bounding rectangle.", 5.0, testLine.getBounds().getUpperLeft().getY(), 5.0E-6);
        Assert.assertEquals("The bounding rectangle.", 4.0, testLine.getBounds().getLowerRight().getX(), 5.0E-6);
        Assert.assertEquals("The bounding rectangle.", 3.0, testLine.getBounds().getLowerRight().getY(), 5.0E-6);
    }

    @Test
    public final void testTransform() {
        Assert.assertEquals("Transformation by identity matrix.", testLine.transform(UNITY_MATRIX).getStart().getX(), testLine.getStart().getX(), 5.0E-6);
        Assert.assertEquals("Transformation by identity matrix.", testLine.transform(UNITY_MATRIX).getStart().getY(), testLine.getStart().getY(), 5.0E-6);
        Assert.assertEquals("Transformation by identity matrix.", testLine.transform(UNITY_MATRIX).getEnd().getX(), testLine.getEnd().getX(), 5.0E-6);
        Assert.assertEquals("Transformation by identity matrix.", testLine.transform(UNITY_MATRIX).getEnd().getY(), testLine.getEnd().getY(), 5.0E-6);
    }
}

