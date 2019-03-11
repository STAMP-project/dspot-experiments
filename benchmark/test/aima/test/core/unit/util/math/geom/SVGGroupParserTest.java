package aima.test.core.unit.util.math.geom;


import aima.core.util.math.geom.SVGGroupParser;
import aima.core.util.math.geom.shapes.Circle2D;
import aima.core.util.math.geom.shapes.Ellipse2D;
import aima.core.util.math.geom.shapes.IGeometric2D;
import aima.core.util.math.geom.shapes.Line2D;
import aima.core.util.math.geom.shapes.Point2D;
import aima.core.util.math.geom.shapes.Polyline2D;
import aima.core.util.math.geom.shapes.Rect2D;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.stream.XMLStreamException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test case for the {@code aima.core.util.math.geometry} package.
 * Tests valid implementation of the {@link IGroupParser} interface by {@link SVGGroupParser}.
 *
 * @author Arno v. Borries
 * @author Jan Phillip Kretzschmar
 * @author Andreas Walscheid
 */
@SuppressWarnings("javadoc")
public class SVGGroupParserTest {
    private final String file = "test.svg";

    private final String groupID = "obstacles";

    private SVGGroupParser testParser;

    private ArrayList<IGeometric2D> testGroup;

    private Point2D[] testVertices1 = new Point2D[]{ new Point2D(100.0, 100.0), new Point2D(150.0, 140.0), new Point2D(120.0, 140.0), new Point2D(80.0, 100.0), new Point2D(55.0, 60.0), new Point2D(85.0, 80.0) };

    private Point2D[] testVertices2 = new Point2D[]{ new Point2D(500.0, 150.0), new Point2D(570.0, 200.0), new Point2D(600.0, 236.0), new Point2D(550.0, 300.0), new Point2D(400.0, 220.0), new Point2D(385.0, 80.0) };

    @Test
    public void testParse() {
        IGeometric2D x;
        IGeometric2D y;
        Iterator<IGeometric2D> j;
        try {
            j = testParser.parse(this.getClass().getResourceAsStream(file), groupID).iterator();
            Iterator<IGeometric2D> i = testGroup.iterator();
            while (i.hasNext()) {
                x = i.next();
                y = j.next();
                if ((x instanceof Circle2D) && (y instanceof Circle2D)) {
                    Assert.assertEquals("Circle: Center-X.", getCenter().getX(), getCenter().getX(), 5.0E-6);
                    Assert.assertEquals("Circle: Center-Y.", getCenter().getY(), getCenter().getY(), 5.0E-6);
                    Assert.assertEquals("Circle: Radius.", getRadius(), getRadius(), 5.0E-6);
                } else
                    if ((x instanceof Ellipse2D) && (y instanceof Ellipse2D)) {
                        Assert.assertEquals("Ellipse: Center-X.", getCenter().getX(), getCenter().getX(), 5.0E-6);
                        Assert.assertEquals("Ellipse: Center-Y.", getCenter().getY(), getCenter().getY(), 5.0E-6);
                        Assert.assertEquals("Ellipse: Horizontal length.", getHorizontalLength(), getHorizontalLength(), 5.0E-6);
                        Assert.assertEquals("Ellipse: Vertical length.", getVerticalLength(), getVerticalLength(), 5.0E-6);
                        Assert.assertEquals("Ellipse: Rotation angle.", getAngle(), getAngle(), 5.0E-6);
                    } else
                        if ((x instanceof Line2D) && (y instanceof Line2D)) {
                            Assert.assertEquals("Line: Start-X.", getStart().getX(), getStart().getX(), 5.0E-6);
                            Assert.assertEquals("Line: Start-Y.", getStart().getY(), getStart().getY(), 5.0E-6);
                            Assert.assertEquals("Line: End-X.", getEnd().getX(), getEnd().getX(), 5.0E-6);
                            Assert.assertEquals("Line: End-Y.", getEnd().getY(), getEnd().getY(), 5.0E-6);
                        } else
                            if ((x instanceof Polyline2D) && (y instanceof Polyline2D)) {
                                if ((getVertexes().length) != (getVertexes().length))
                                    Assert.fail();
                                else {
                                    for (int k = 0; k < (getVertexes().length); k++) {
                                        Assert.assertEquals("Polygon: Vertex-X", getVertexes()[k].getX(), getVertexes()[k].getX(), 5.0E-6);
                                        Assert.assertEquals("Polygon: Vertex-Y", getVertexes()[k].getY(), getVertexes()[k].getY(), 5.0E-6);
                                    }
                                }
                            } else
                                if ((x instanceof Rect2D) && (y instanceof Rect2D)) {
                                    Assert.assertEquals("Line: UpperLeft-X.", getUpperLeft().getX(), getUpperLeft().getX(), 5.0E-6);
                                    Assert.assertEquals("Line: UpperLeft-Y.", getUpperLeft().getY(), getUpperLeft().getY(), 5.0E-6);
                                    Assert.assertEquals("Line: LowerRight-X.", getLowerRight().getX(), getLowerRight().getX(), 5.0E-6);
                                    Assert.assertEquals("Line: LowerRight-Y.", getLowerRight().getY(), getLowerRight().getY(), 5.0E-6);
                                }




            } 
            Assert.assertFalse("Both groups are the same length and contain only legal shapes.", ((i.hasNext()) || (j.hasNext())));
        } catch (XMLStreamException e) {
            e.printStackTrace();
            Assert.fail();
        } catch (NullPointerException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}

