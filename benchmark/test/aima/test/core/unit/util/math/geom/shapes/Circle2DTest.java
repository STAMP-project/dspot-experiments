package aima.test.core.unit.util.math.geom.shapes;


import aima.core.util.Util;
import aima.core.util.math.geom.shapes.Circle2D;
import aima.core.util.math.geom.shapes.Point2D;
import aima.core.util.math.geom.shapes.Ray2D;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test case for the {@code aima.core.util.math.geom} package.
 * Tests valid implementation of the {@link IGeometric2D} interface by {@link Circle2D}.
 *
 * @author Arno v. Borries
 * @author Jan Phillip Kretzschmar
 * @author Andreas Walscheid
 */
@SuppressWarnings("javadoc")
public class Circle2DTest {
    private Circle2D testCircle;

    private Point2D center;

    private Point2D zeroPoint;

    @Test
    public final void testRandomPoint() {
        for (int i = 0; i < 1000; i++) {
            Assert.assertTrue("Random point in circle", testCircle.isInsideBorder(testCircle.randomPoint()));
        }
    }

    @Test
    public final void testIsInside() {
        Assert.assertFalse("Point not inside circle.", testCircle.isInside(zeroPoint));
        Assert.assertFalse("Point on border.", testCircle.isInside(new Point2D(12.0, 4.0)));
        Assert.assertTrue("Point inside circle.", testCircle.isInside(new Point2D(10.0, 8.0)));
    }

    @Test
    public final void testIsInsideBorder() {
        Assert.assertFalse("Point not inside circle.", testCircle.isInsideBorder(zeroPoint));
        Assert.assertTrue("Point on border.", testCircle.isInsideBorder(new Point2D(12.0, 4.0)));
        Assert.assertTrue("Point inside circle.", testCircle.isInsideBorder(new Point2D(10.0, 8.0)));
    }

    @Test
    public final void testRayCast() {
        // static tests
        Assert.assertEquals("Ray doesn't intersect.", Double.POSITIVE_INFINITY, testCircle.rayCast(new Ray2D(1.0, 1.0, 0.0, 2.0)), 5.0E-6);
        Assert.assertEquals("Ray intersects.", Math.sqrt(2), testCircle.rayCast(new Ray2D(11.0, 3.0, 12.0, 4.0)), 5.0E-6);
        // serial tests
        Circle2D randomCircle;
        Point2D randomPointOnCircle;
        Point2D randomPoint;
        double currentRadius;
        double xvalue;
        double yvalue;
        int sector;
        int counter = 1000;
        do {
            randomCircle = new Circle2D(new Point2D(Util.generateRandomDoubleBetween((-500.0), 500.0), Util.generateRandomDoubleBetween((-500.0), 500.0)), Util.generateRandomDoubleBetween(0.0, 200.0));
            currentRadius = randomCircle.getRadius();
            xvalue = Util.generateRandomDoubleBetween(0.0, currentRadius);
            yvalue = Math.sqrt(((currentRadius * currentRadius) - (xvalue * xvalue)));
            sector = Util.randomNumberBetween(1, 4);
            switch (sector) {
                case 2 :
                    {
                        yvalue = -yvalue;
                        randomPoint = new Point2D(Util.generateRandomDoubleBetween(((randomCircle.getCenter().getX()) + xvalue), 1000.0), Util.generateRandomDoubleBetween((-1000.0), ((randomCircle.getCenter().getY()) + yvalue)));
                        break;
                    }
                case 3 :
                    {
                        xvalue = -xvalue;
                        yvalue = -yvalue;
                        randomPoint = new Point2D(Util.generateRandomDoubleBetween((-1000.0), ((randomCircle.getCenter().getX()) + xvalue)), Util.generateRandomDoubleBetween((-1000.0), ((randomCircle.getCenter().getY()) + yvalue)));
                        break;
                    }
                case 4 :
                    {
                        xvalue = -xvalue;
                        randomPoint = new Point2D(Util.generateRandomDoubleBetween((-1000.0), ((randomCircle.getCenter().getX()) + xvalue)), Util.generateRandomDoubleBetween(((randomCircle.getCenter().getY()) + yvalue), 1000.0));
                        break;
                    }
                default :
                    {
                        randomPoint = new Point2D(Util.generateRandomDoubleBetween(((randomCircle.getCenter().getX()) + xvalue), 1000.0), Util.generateRandomDoubleBetween(((randomCircle.getCenter().getY()) + yvalue), 1000.0));
                        break;
                    }
            }
            randomPointOnCircle = new Point2D(((randomCircle.getCenter().getX()) + xvalue), ((randomCircle.getCenter().getY()) + yvalue));
            // System.out.printf("Circle at (%.2f,%.2f), Radius %.2f. Point on Circle: (%.2f,%.2f). Outside Point: (%.2f,%.2f). Distance: %.2f.\n", randomCircle.getCenter().getX(), randomCircle.getCenter().getY(), randomCircle.getRadius(), randomPointOnCircle.getX(), randomPointOnCircle.getY(), randomPoint.getX(), randomPoint.getY(), randomPoint.distance(randomPointOnCircle));
            Assert.assertEquals("Serial rayCast test for Circle2D.", randomPoint.distance(randomPointOnCircle), randomCircle.rayCast(new Ray2D(randomPoint, randomPoint.vec(randomPointOnCircle))), 5.0E-6);
            counter -= 1;
        } while (counter > 0 );
    }

    @Test
    public final void testGetBounds() {
        Assert.assertNotEquals("Not the bounding rectangle ULX.", 1.0, testCircle.getBounds().getUpperLeft().getX(), 5.0E-6);
        Assert.assertNotEquals("Not the bounding rectangle ULY.", 6.0, testCircle.getBounds().getUpperLeft().getY(), 5.0E-6);
        Assert.assertNotEquals("Not the bounding rectangle LRX.", 6.0, testCircle.getBounds().getLowerRight().getX(), 5.0E-6);
        Assert.assertNotEquals("Not the bounding rectangle LRY.", 1.0, testCircle.getBounds().getLowerRight().getY(), 5.0E-6);
        Assert.assertEquals("The bounding rectangle ULX.", 2.0, testCircle.getBounds().getUpperLeft().getX(), 5.0E-6);
        Assert.assertEquals("The bounding rectangle ULY.", 24.0, testCircle.getBounds().getUpperLeft().getY(), 5.0E-6);
        Assert.assertEquals("The bounding rectangle LRX.", 22.0, testCircle.getBounds().getLowerRight().getX(), 5.0E-6);
        Assert.assertEquals("The bounding rectangle LRY.", 4.0, testCircle.getBounds().getLowerRight().getY(), 5.0E-6);
    }

    @Test
    public final void testTransform() {
        Assert.assertEquals("Transformation by identity matrix: X-value.", getCenter().getX(), testCircle.getCenter().getX(), 5.0E-6);
        Assert.assertEquals("Transformation by identity matrix: Y-value.", getCenter().getY(), testCircle.getCenter().getY(), 5.0E-6);
        Assert.assertEquals("Transformation by identity matrix: radius.", getRadius(), testCircle.getRadius(), 5.0E-6);
        Assert.assertEquals("Translating circle: X-Value.", getCenter().getX(), 16.0, 5.0E-6);
        Assert.assertEquals("Translating circle: Y-Value.", getCenter().getY(), 19.0, 5.0E-6);
        Assert.assertEquals("Scaling circle into ellipse: X-Value.", getHorizontalLength(), 20.0, 5.0E-6);
        Assert.assertEquals("Scaling circle into ellipse: Y-Value.", getVerticalLength(), 5.0, 5.0E-6);
    }
}

