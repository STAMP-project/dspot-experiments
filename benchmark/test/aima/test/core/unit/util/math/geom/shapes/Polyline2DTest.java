package aima.test.core.unit.util.math.geom.shapes;


import TransformMatrix2D.UNITY_MATRIX;
import aima.core.util.math.geom.shapes.Point2D;
import aima.core.util.math.geom.shapes.Polyline2D;
import aima.core.util.math.geom.shapes.Ray2D;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test case for the {@code aima.core.util.math.geom} package.
 * Tests valid implementation of the {@link IGeometric2D} interface by {@link Polyline2D}.
 *
 * @author Arno v. Borries
 * @author Jan Phillip Kretzschmar
 * @author Andreas Walscheid
 */
@SuppressWarnings("javadoc")
public class Polyline2DTest {
    private Point2D[] testVertices = new Point2D[]{ new Point2D(2.0, 2.0), new Point2D(5.0, 7.0), new Point2D(6.0, 4.0), new Point2D(6.0, (-3.0)) };

    private Polyline2D testPolylineOpen;

    private Polyline2D testPolylineClosed;

    private Point2D zeroPoint;

    @Test
    public final void testRandomPoint() {
        Point2D randomPoint = testPolylineOpen.randomPoint();
        for (int i = 0; i < 1000; i++) {
            randomPoint = testPolylineOpen.randomPoint();
            Assert.assertTrue("Random point on polyline.", testPolylineOpen.isInsideBorder(randomPoint));
        }
        for (int i = 0; i < 1000; i++) {
            randomPoint = testPolylineClosed.randomPoint();
            Assert.assertTrue("Random point in polygon.", testPolylineClosed.isInsideBorder(testPolylineClosed.randomPoint()));
        }
    }

    @Test
    public final void testIsInside() {
        Assert.assertFalse("Point cannot be inside polyline.", testPolylineOpen.isInside(new Point2D(3.0, 3.0)));
        Assert.assertFalse("Point on border of polyline.", testPolylineOpen.isInside(new Point2D(6.0, 2.0)));
        Assert.assertFalse("Point outside polygon.", testPolylineClosed.isInside(zeroPoint));
        Assert.assertFalse("Point on border of polygon.", testPolylineClosed.isInside(new Point2D(6.0, 2.0)));
        Assert.assertTrue("Point inside polygon.", testPolylineClosed.isInside(new Point2D(3.0, 3.0)));
    }

    @Test
    public final void testIsInsideBorder() {
        Assert.assertFalse("Point cannot be inside polyline.", testPolylineOpen.isInsideBorder(new Point2D(3.0, 3.0)));
        Assert.assertTrue("Point on border of polyline.", testPolylineOpen.isInsideBorder(new Point2D(6.0, 2.0)));
        Assert.assertFalse("Point outside polygon.", testPolylineClosed.isInsideBorder(zeroPoint));
        Assert.assertTrue("Point on border of polygon.", testPolylineClosed.isInsideBorder(new Point2D(6.0, 2.0)));
        Assert.assertTrue("Point inside polygon.", testPolylineClosed.isInsideBorder(new Point2D(3.0, 3.0)));
    }

    @Test
    public final void testRayCast() {
        // Static RayCast tests
        Assert.assertEquals("Ray doesn't intersect with polyline.", Double.POSITIVE_INFINITY, testPolylineOpen.rayCast(new Ray2D(1.0, 1.0, (-7.0), (-8.0))), 5.0E-6);
        Assert.assertEquals("Ray intersects with polyline.", Math.sqrt(2), testPolylineOpen.rayCast(new Ray2D(1.0, 1.0, 4.0, 4.0)), 5.0E-6);
        Assert.assertEquals("Ray doesn't intersect with polygon.", Double.POSITIVE_INFINITY, testPolylineClosed.rayCast(new Ray2D(1.0, 1.0, (-7.0), (-8.0))), 5.0E-6);
        Assert.assertEquals("Ray intersects with polygon.", Math.sqrt(2), testPolylineClosed.rayCast(new Ray2D(1.0, 1.0, 4.0, 4.0)), 5.0E-6);
        // Serial RayCast tests
        /* Point2D randomPoint;
        Point2D randomPointOnEdge;
        Line2D currentEdge;
        int counter = 500;
        do {
        for (int i = 1; i < testVertices.length; i++){
        currentEdge = new Line2D(testVertices[i], testVertices[i-1]);
        randomPointOnEdge = currentEdge.randomPoint();
        randomPoint = new Point2D(Util.generateRandomDoubleBetween(-1000.0d, 1000.0d), Util.generateRandomDoubleBetween(-1000.0d, 1000.0d));
        assertEquals("Serial rayCast test for Polyline2D (open).", randomPoint.distance(randomPointOnEdge), testPolylineOpen.rayCast(new Ray2D(randomPoint,randomPoint.vec(randomPointOnEdge))), 0.000005d);
        }
        counter -= 1;	
        } while (counter > 0);
         */
    }

    @Test
    public final void testGetBounds() {
        Assert.assertNotEquals("Not the bounding rectangle.", 1.0, testPolylineOpen.getBounds().getUpperLeft().getX(), 5.0E-6);
        Assert.assertNotEquals("Not the bounding rectangle.", 8.0, testPolylineOpen.getBounds().getUpperLeft().getY(), 5.0E-6);
        Assert.assertNotEquals("Not the bounding rectangle.", 8.0, testPolylineOpen.getBounds().getLowerRight().getX(), 5.0E-6);
        Assert.assertNotEquals("Not the bounding rectangle.", 1.0, testPolylineOpen.getBounds().getLowerRight().getY(), 5.0E-6);
        Assert.assertEquals("The bounding rectangle.", 2.0, testPolylineOpen.getBounds().getUpperLeft().getX(), 5.0E-6);
        Assert.assertEquals("The bounding rectangle.", 7.0, testPolylineOpen.getBounds().getUpperLeft().getY(), 5.0E-6);
        Assert.assertEquals("The bounding rectangle.", 6.0, testPolylineOpen.getBounds().getLowerRight().getX(), 5.0E-6);
        Assert.assertEquals("The bounding rectangle.", (-3.0), testPolylineOpen.getBounds().getLowerRight().getY(), 5.0E-6);
        Assert.assertNotEquals("Not the bounding rectangle.", 1.0, testPolylineClosed.getBounds().getUpperLeft().getX(), 5.0E-6);
        Assert.assertNotEquals("Not the bounding rectangle.", 8.0, testPolylineClosed.getBounds().getUpperLeft().getY(), 5.0E-6);
        Assert.assertNotEquals("Not the bounding rectangle.", 8.0, testPolylineClosed.getBounds().getLowerRight().getX(), 5.0E-6);
        Assert.assertNotEquals("Not the bounding rectangle.", 1.0, testPolylineClosed.getBounds().getLowerRight().getY(), 5.0E-6);
        Assert.assertEquals("The bounding rectangle.", 2.0, testPolylineClosed.getBounds().getUpperLeft().getX(), 5.0E-6);
        Assert.assertEquals("The bounding rectangle.", 7.0, testPolylineClosed.getBounds().getUpperLeft().getY(), 5.0E-6);
        Assert.assertEquals("The bounding rectangle.", 6.0, testPolylineClosed.getBounds().getLowerRight().getX(), 5.0E-6);
        Assert.assertEquals("The bounding rectangle.", (-3.0), testPolylineClosed.getBounds().getLowerRight().getY(), 5.0E-6);
    }

    @Test
    public final void testTransform() {
        for (int i = 0; i < (testPolylineOpen.getVertexes().length); i++) {
            Assert.assertEquals("Transformation by identity matrix", testPolylineOpen.transform(UNITY_MATRIX).getVertexes()[i].getX(), testPolylineOpen.getVertexes()[i].getX(), 5.0E-6);
            Assert.assertEquals("Transformation by identity matrix", testPolylineOpen.transform(UNITY_MATRIX).getVertexes()[i].getY(), testPolylineOpen.getVertexes()[i].getY(), 5.0E-6);
        }
        for (int i = 0; i < (testPolylineClosed.getVertexes().length); i++) {
            Assert.assertEquals("Transformation by identity matrix", testPolylineClosed.transform(UNITY_MATRIX).getVertexes()[i].getX(), testPolylineClosed.getVertexes()[i].getX(), 5.0E-6);
            Assert.assertEquals("Transformation by identity matrix", testPolylineClosed.transform(UNITY_MATRIX).getVertexes()[i].getY(), testPolylineClosed.getVertexes()[i].getY(), 5.0E-6);
        }
    }
}

