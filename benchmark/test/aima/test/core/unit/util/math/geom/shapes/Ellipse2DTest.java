package aima.test.core.unit.util.math.geom.shapes;


import aima.core.util.Util;
import aima.core.util.math.geom.shapes.Ellipse2D;
import aima.core.util.math.geom.shapes.Point2D;
import aima.core.util.math.geom.shapes.Ray2D;
import aima.core.util.math.geom.shapes.TransformMatrix2D;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test case for the {@code aima.core.util.math.geom} package.
 * Tests valid implementation of the {@link IGeometric2D} interface by {@link Ellipse2D}.
 *
 * @author Arno v. Borries
 * @author Jan Phillip Kretzschmar
 * @author Andreas Walscheid
 */
@SuppressWarnings("javadoc")
public class Ellipse2DTest {
    private Ellipse2D testEllipse;

    private Point2D center;

    private Point2D zeroPoint;

    @Test
    public final void testRandomPoint() {
        for (int i = 0; i < 1000; i++) {
            Assert.assertTrue("Random point in ellipse", testEllipse.isInsideBorder(testEllipse.randomPoint()));
        }
    }

    @Test
    public final void testIsInside() {
        Assert.assertFalse("Point not inside ellipse.", testEllipse.isInside(zeroPoint));
        Assert.assertFalse("Point on border.", testEllipse.isInside(new Point2D(12.0, 9.0)));
        Assert.assertTrue("Point inside ellipse.", testEllipse.isInside(new Point2D(10.0, 12.0)));
    }

    @Test
    public final void testIsInsideBorder() {
        Assert.assertFalse("Point not inside ellipse.", testEllipse.isInsideBorder(zeroPoint));
        Assert.assertTrue("Point on border.", testEllipse.isInsideBorder(new Point2D(12.0, 9.0)));
        Assert.assertTrue("Point inside ellipse.", testEllipse.isInsideBorder(new Point2D(10.0, 12.0)));
    }

    @Test
    public final void testRayCast() {
        // static tests
        Assert.assertEquals("Ray doesn't intersect.", Double.POSITIVE_INFINITY, testEllipse.rayCast(new Ray2D(0.0, 0.0, 0.0, 2.0)), 5.0E-6);
        Assert.assertEquals("Ray intersects.", 2.0, testEllipse.rayCast(new Ray2D(0.0, 14.0, 12.0, 14.0)), 5.0E-6);
        // serial tests
        Ellipse2D randomEllipse;
        Point2D randomPointOnEllipse;
        Point2D randomPoint;
        double currentXRadius;
        double currentYRadius;
        double xvalue;
        double yvalue;
        double randomAngle;
        int sector;
        int counter = 1000;
        do {
            randomEllipse = new Ellipse2D(new Point2D(Util.generateRandomDoubleBetween((-500.0), 500.0), Util.generateRandomDoubleBetween((-500.0), 500.0)), Util.generateRandomDoubleBetween(0.0, 200.0), Util.generateRandomDoubleBetween(0.0, 200.0));
            currentXRadius = randomEllipse.getHorizontalLength();
            currentYRadius = randomEllipse.getVerticalLength();
            xvalue = Util.generateRandomDoubleBetween(0.0, currentXRadius);
            yvalue = (currentYRadius * (Math.sqrt(((currentXRadius * currentXRadius) - (xvalue * xvalue))))) / currentXRadius;
            sector = Util.randomNumberBetween(1, 4);
            switch (sector) {
                case 2 :
                    {
                        yvalue = -yvalue;
                        randomPoint = new Point2D(Util.generateRandomDoubleBetween(((randomEllipse.getCenter().getX()) + xvalue), 1000.0), Util.generateRandomDoubleBetween((-1000.0), ((randomEllipse.getCenter().getY()) + yvalue)));
                        break;
                    }
                case 3 :
                    {
                        xvalue = -xvalue;
                        yvalue = -yvalue;
                        randomPoint = new Point2D(Util.generateRandomDoubleBetween((-1000.0), ((randomEllipse.getCenter().getX()) + xvalue)), Util.generateRandomDoubleBetween((-1000.0), ((randomEllipse.getCenter().getY()) + yvalue)));
                        break;
                    }
                case 4 :
                    {
                        xvalue = -xvalue;
                        randomPoint = new Point2D(Util.generateRandomDoubleBetween((-1000.0), ((randomEllipse.getCenter().getX()) + xvalue)), Util.generateRandomDoubleBetween(((randomEllipse.getCenter().getY()) + yvalue), 1000.0));
                        break;
                    }
                default :
                    {
                        randomPoint = new Point2D(Util.generateRandomDoubleBetween(((randomEllipse.getCenter().getX()) + xvalue), 1000.0), Util.generateRandomDoubleBetween(((randomEllipse.getCenter().getY()) + yvalue), 1000.0));
                        break;
                    }
            }
            randomPointOnEllipse = new Point2D(((randomEllipse.getCenter().getX()) + xvalue), ((randomEllipse.getCenter().getY()) + yvalue));
            randomAngle = Util.generateRandomDoubleBetween(((-(Math.PI)) / 2), ((Math.PI) / 2));
            randomEllipse = ((Ellipse2D) (randomEllipse.transform(TransformMatrix2D.rotate(randomAngle))));
            randomPoint = TransformMatrix2D.rotate(randomAngle).multiply(randomPoint);
            randomPointOnEllipse = TransformMatrix2D.rotate(randomAngle).multiply(randomPointOnEllipse);
            // System.out.printf("RayCast No. %d: Ellipse at (%.2f,%.2f), radii: (%.2f,%.2f). Rotation angle: %.2f, original angle: %.2f, point on ellipse: (%.2f,%.2f), outside point: (%.2f,%.2f), distance: %.2f.\n", 1000-counter, randomEllipse.getCenter().getX(), randomEllipse.getCenter().getY(), randomEllipse.getHorizontalLength(), randomEllipse.getVerticalLength(), randomEllipse.getAngle(), randomAngle, randomPointOnEllipse.getX(), randomPointOnEllipse.getY(), randomPoint.getX(), randomPoint.getY(), randomPoint.distance(randomPointOnEllipse));
            Assert.assertEquals("Serial rayCast test for Circle2D.", randomPoint.distance(randomPointOnEllipse), randomEllipse.rayCast(new Ray2D(randomPoint, randomPoint.vec(randomPointOnEllipse))), 5.0E-6);
            counter -= 1;
        } while (counter > 0 );
    }

    @Test
    public final void testGetBounds() {
        Assert.assertNotEquals("Not the bounding rectangle ULX.", 1.0, testEllipse.getBounds().getUpperLeft().getX(), 5.0E-6);
        Assert.assertNotEquals("Not the bounding rectangle ULY.", 6.0, testEllipse.getBounds().getUpperLeft().getY(), 5.0E-6);
        Assert.assertNotEquals("Not the bounding rectangle LRX.", 6.0, testEllipse.getBounds().getLowerRight().getX(), 5.0E-6);
        Assert.assertNotEquals("Not the bounding rectangle LRY.", 1.0, testEllipse.getBounds().getLowerRight().getY(), 5.0E-6);
        Assert.assertEquals("The bounding rectangle ULX.", 2.0, testEllipse.getBounds().getUpperLeft().getX(), 5.0E-6);
        Assert.assertEquals("The bounding rectangle ULY.", 19.0, testEllipse.getBounds().getUpperLeft().getY(), 5.0E-6);
        Assert.assertEquals("The bounding rectangle LRX.", 22.0, testEllipse.getBounds().getLowerRight().getX(), 5.0E-6);
        Assert.assertEquals("The bounding rectangle LRY.", 9.0, testEllipse.getBounds().getLowerRight().getY(), 5.0E-6);
    }

    @Test
    public final void testTransform() {
        Assert.assertEquals("Transformation by identity matrix: X-value.", getCenter().getX(), testEllipse.getCenter().getX(), 5.0E-6);
        Assert.assertEquals("Transformation by identity matrix: Y-value.", getCenter().getY(), testEllipse.getCenter().getY(), 5.0E-6);
        Assert.assertEquals("Transformation by identity matrix: X-radius.", getHorizontalLength(), testEllipse.getHorizontalLength(), 5.0E-6);
        Assert.assertEquals("Transformation by identity matrix: Y-radius.", getVerticalLength(), testEllipse.getVerticalLength(), 5.0E-6);
        Assert.assertEquals("Transformation by identity matrix: angle.", getAngle(), testEllipse.getAngle(), 5.0E-6);
        Assert.assertEquals("Translating Ellipse: X-Value.", getCenter().getX(), 16.0, 5.0E-6);
        Assert.assertEquals("Translating Ellipse: Y-Value.", getCenter().getY(), 19.0, 5.0E-6);
        Assert.assertEquals("Scaling Ellipse: X-Value.", getHorizontalLength(), 5.0, 5.0E-6);
        Assert.assertEquals("Scaling Ellipse: Y-Value.", getVerticalLength(), 10.0, 5.0E-6);
    }
}

