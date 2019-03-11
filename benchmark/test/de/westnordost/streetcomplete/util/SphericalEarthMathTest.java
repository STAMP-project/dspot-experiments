package de.westnordost.streetcomplete.util;


import de.westnordost.osmapi.map.data.BoundingBox;
import de.westnordost.osmapi.map.data.LatLon;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

import static SphericalEarthMath.EARTH_RADIUS;


public class SphericalEarthMathTest {
    private static LatLon HH = SphericalEarthMathTest.p(10.0, 53.5);

    /* ++++++++++++++++++++++++++++++++ test distance functions +++++++++++++++++++++++++++++++++ */
    @Test
    public void distanceToBerlin() {
        checkHamburgTo(52.4, 13.4, 259, 117, 110);
    }

    @Test
    public void distanceToL?beck() {
        checkHamburgTo(53.85, 10.68, 59, 49, 61);
    }

    @Test
    public void distanceToLosAngeles() {
        checkHamburgTo(34, (-118), 9075, 319, 206);
    }

    @Test
    public void distanceToReykjavik() {
        checkHamburgTo(64.11, (-21.98), 2152, 316, 280);
    }

    @Test
    public void distanceToPortElizabeth() {
        checkHamburgTo((-33.9), (-25.6), 10307, 209, 231);
    }

    @Test
    public void distanceToPoles() {
        checkHamburgTo(90.0, 123.0, 4059, 0, null);
        checkHamburgTo((-90.0), 0.0, 15956, 180, null);
    }

    @Test
    public void distanceToOtherSideOfEarth() {
        checkHamburgTo((-53.5), (-170.0), ((int) ((Math.PI) * 6371)), 270, 180);
    }

    @Test
    public void shortDistance() {
        LatLon one = SphericalEarthMathTest.p(9.9782365, 53.5712482);
        LatLon two = SphericalEarthMathTest.p(9.9782517, 53.5712528);
        Assert.assertEquals(1, ((int) (SphericalEarthMath.distance(one, two))));
    }

    @Test
    public void distanceOfPolylineIsZeroForOnePosition() {
        List<LatLon> positions = new ArrayList<>();
        positions.add(SphericalEarthMathTest.p(0, 0));
        Assert.assertEquals(0.0, SphericalEarthMath.distance(positions), 0);
    }

    @Test
    public void distanceOfPolylineForTwoPositions() {
        List<LatLon> positions = new ArrayList<>();
        LatLon p0 = SphericalEarthMathTest.p(0, 0);
        LatLon p1 = SphericalEarthMathTest.p(1, 1);
        positions.add(p0);
        positions.add(p1);
        Assert.assertEquals(SphericalEarthMath.distance(p0, p1), SphericalEarthMath.distance(positions), 0);
    }

    @Test
    public void distanceOfPolylineForThreePositions() {
        LatLon p0 = SphericalEarthMathTest.p(0, 0);
        LatLon p1 = SphericalEarthMathTest.p(1, 1);
        LatLon p2 = SphericalEarthMathTest.p(2, 2);
        List<LatLon> positions = new ArrayList(Arrays.asList(p0, p1, p2));
        Assert.assertEquals(((SphericalEarthMath.distance(p0, p1)) + (SphericalEarthMath.distance(p1, p2))), SphericalEarthMath.distance(positions), 1.0E-16);
    }

    /* +++++++++++++++++++++++++++++ test creation of bounding boxes ++++++++++++++++++++++++++++ */
    @Test
    public void enclosingBoundingBox() {
        LatLon pos = SphericalEarthMathTest.p(0, 0);
        BoundingBox bbox = SphericalEarthMath.enclosingBoundingBox(pos, 5000);
        int dist = ((int) ((Math.sqrt(2)) * 5000));
        // all four corners of the bbox should be 'radius' away
        Assert.assertEquals(dist, Math.round(SphericalEarthMath.distance(pos, bbox.getMin())));
        Assert.assertEquals(dist, Math.round(SphericalEarthMath.distance(pos, bbox.getMax())));
        Assert.assertEquals(dist, Math.round(SphericalEarthMath.distance(pos, SphericalEarthMathTest.p(bbox.getMaxLongitude(), bbox.getMinLatitude()))));
        Assert.assertEquals(dist, Math.round(SphericalEarthMath.distance(pos, SphericalEarthMathTest.p(bbox.getMinLongitude(), bbox.getMaxLatitude()))));
        Assert.assertEquals(225, Math.round(SphericalEarthMath.bearing(pos, bbox.getMin())));
        Assert.assertEquals(45, Math.round(SphericalEarthMath.bearing(pos, bbox.getMax())));
    }

    @Test
    public void enclosingBoundingBoxCrosses180thMeridian() {
        LatLon pos = SphericalEarthMathTest.p(180, 0);
        BoundingBox bbox = SphericalEarthMath.enclosingBoundingBox(pos, 5000);
        Assert.assertTrue(bbox.crosses180thMeridian());
    }

    @Test
    public void enclosingBoundingBoxLineEmptyFails() {
        List<LatLon> positions = new ArrayList<>();
        try {
            SphericalEarthMath.enclosingBoundingBox(positions);
            Assert.fail();
        } catch (IllegalArgumentException ignore) {
        }
    }

    @Test
    public void enclosingBoundingBoxLine() {
        List<LatLon> positions = new ArrayList<>();
        positions.add(SphericalEarthMathTest.p(0, (-4)));
        positions.add(SphericalEarthMathTest.p(3, 12));
        positions.add(SphericalEarthMathTest.p(16, 1));
        positions.add(SphericalEarthMathTest.p((-6), 0));
        BoundingBox bbox = SphericalEarthMath.enclosingBoundingBox(positions);
        Assert.assertEquals((-4.0), bbox.getMinLatitude(), 0);
        Assert.assertEquals(12.0, bbox.getMaxLatitude(), 0);
        Assert.assertEquals(16.0, bbox.getMaxLongitude(), 0);
        Assert.assertEquals((-6.0), bbox.getMinLongitude(), 0);
    }

    @Test
    public void enclosingBoundingBoxLineCrosses180thMeridian() {
        List<LatLon> positions = new ArrayList<>();
        positions.add(SphericalEarthMathTest.p(160, 10));
        positions.add(SphericalEarthMathTest.p((-150), 0));
        positions.add(SphericalEarthMathTest.p(180, (-10)));
        BoundingBox bbox = SphericalEarthMath.enclosingBoundingBox(positions);
        Assert.assertTrue(bbox.crosses180thMeridian());
        Assert.assertEquals((-10.0), bbox.getMinLatitude(), 0);
        Assert.assertEquals(10.0, bbox.getMaxLatitude(), 0);
        Assert.assertEquals((-150.0), bbox.getMaxLongitude(), 0);
        Assert.assertEquals(160.0, bbox.getMinLongitude(), 0);
    }

    /* ++++++++++++++++++++++++++++++ test translating of positions +++++++++++++++++++++++++++++ */
    @Test
    public void translateLatitudeNorth() {
        checkTranslate(1000, 0);
    }

    @Test
    public void translateLatitudeSouth() {
        checkTranslate(1000, 180);
    }

    @Test
    public void translateLatitudeWest() {
        checkTranslate(1000, 270);
    }

    @Test
    public void translateLatitudeEast() {
        checkTranslate(1000, 90);
    }

    @Test
    public void translateLatitudeNorthEast() {
        checkTranslate(1000, 45);
    }

    @Test
    public void translateLatitudeSouthEast() {
        checkTranslate(1000, 135);
    }

    @Test
    public void translateLatitudeSouthWest() {
        checkTranslate(1000, 225);
    }

    @Test
    public void translateLatitudeNorthWest() {
        checkTranslate(1000, 315);
    }

    @Test
    public void translateOverBoundaries() {
        // cross 180th meridian both ways
        checkTranslate(SphericalEarthMathTest.p(179.9999999, 0), 1000, 90);
        checkTranslate(SphericalEarthMathTest.p((-179.9999999), 0), 1000, 270);
        // cross north pole and come out on the other side
        // should come out at 45,-90
        int quarterOfEarth = ((int) (((Math.PI) / 2) * (EARTH_RADIUS)));
        checkTranslate(SphericalEarthMathTest.p(90, (+45)), quarterOfEarth, 0);
        // should come out at -45,-90
        checkTranslate(SphericalEarthMathTest.p(90, (-45)), quarterOfEarth, 180);
    }

    /* +++++++++++++++++++++++++++++ test calculation of center line ++++++++++++++++++++++++++++ */
    @Test
    public void centerLineForPointFails() {
        List<LatLon> positions = new ArrayList<>();
        positions.add(SphericalEarthMathTest.p(0, 0));
        try {
            SphericalEarthMath.centerLineOfPolyline(positions);
            Assert.fail();
        } catch (IllegalArgumentException ignore) {
        }
    }

    @Test
    public void centerLineOfPolylineWithZeroLength() {
        LatLon p0 = SphericalEarthMathTest.p(0, 0);
        LatLon p1 = SphericalEarthMathTest.p(0, 0);
        LatLon p2 = SphericalEarthMathTest.p(0, 0);
        List<LatLon> positions = new ArrayList(Arrays.asList(p0, p1, p2));
        Assert.assertThat(SphericalEarthMath.centerLineOfPolyline(positions)).containsExactly(p0, p1);
    }

    @Test
    public void centerLineOfLineIsThatLine() {
        LatLon p0 = SphericalEarthMathTest.p(0, 0);
        LatLon p1 = SphericalEarthMathTest.p(1, 1);
        List<LatLon> positions = new ArrayList(Arrays.asList(p0, p1));
        Assert.assertThat(SphericalEarthMath.centerLineOfPolyline(positions)).containsExactly(p0, p1);
    }

    @Test
    public void centerLineOfPolylineIsTheMiddleOne() {
        LatLon p0 = SphericalEarthMathTest.p(0, 0);
        LatLon p1 = SphericalEarthMathTest.p(1, 1);
        LatLon p2 = SphericalEarthMathTest.p(2, 2);
        LatLon p3 = SphericalEarthMathTest.p(3, 3);
        List<LatLon> positions = new ArrayList(Arrays.asList(p0, p1, p2, p3));
        Assert.assertThat(SphericalEarthMath.centerLineOfPolyline(positions)).containsExactly(p1, p2);
    }

    @Test
    public void centerLineOfPolylineIsNotMiddleOneBecauseItIsSoLong() {
        LatLon p0 = SphericalEarthMathTest.p(0, 0);
        LatLon p1 = SphericalEarthMathTest.p(10, 10);
        LatLon p2 = SphericalEarthMathTest.p(11, 11);
        LatLon p3 = SphericalEarthMathTest.p(12, 12);
        List<LatLon> positions = new ArrayList(Arrays.asList(p0, p1, p2, p3));
        Assert.assertThat(SphericalEarthMath.centerLineOfPolyline(positions)).containsExactly(p0, p1);
    }

    /* +++++++++++++++++++++++++ test calculation of center point of line +++++++++++++++++++++++ */
    @Test
    public void centerPointForEmptyPolyListFails() {
        List<LatLon> positions = new ArrayList<>();
        try {
            SphericalEarthMath.centerPointOfPolyline(positions);
            Assert.fail();
        } catch (IllegalArgumentException ignore) {
        }
    }

    @Test
    public void centerOfPolylineWithZeroLength() {
        List<LatLon> polyline = new ArrayList<>();
        polyline.add(SphericalEarthMathTest.p(20, 20));
        polyline.add(SphericalEarthMathTest.p(20, 20));
        Assert.assertEquals(SphericalEarthMathTest.p(20, 20), SphericalEarthMath.centerPointOfPolyline(polyline));
    }

    @Test
    public void centerOfLine() {
        List<LatLon> polyline = new ArrayList<>();
        LatLon pos0 = SphericalEarthMathTest.p((-20), 80);
        LatLon pos1 = SphericalEarthMathTest.p(20, (-60));
        polyline.add(pos0);
        polyline.add(pos1);
        Assert.assertEquals(SphericalEarthMathTest.p(0, 10), SphericalEarthMath.centerPointOfPolyline(polyline));
    }

    @Test
    public void centerOfLineThatCrosses180thMeridian() {
        List<LatLon> polyline = new ArrayList<>();
        LatLon pos0 = SphericalEarthMathTest.p(170, 0);
        LatLon pos1 = SphericalEarthMathTest.p((-150), 0);
        polyline.add(pos0);
        polyline.add(pos1);
        Assert.assertEquals(SphericalEarthMathTest.p((-170), 0), SphericalEarthMath.centerPointOfPolyline(polyline));
        List<LatLon> polyline2 = new ArrayList<>();
        LatLon pos2 = SphericalEarthMathTest.p(150, 0);
        LatLon pos3 = SphericalEarthMathTest.p((-170), 0);
        polyline2.add(pos2);
        polyline2.add(pos3);
        Assert.assertEquals(SphericalEarthMathTest.p(170, 0), SphericalEarthMath.centerPointOfPolyline(polyline2));
    }

    /* +++++++++++++++++++++++ test calculation of center point of polygon ++++++++++++++++++++++ */
    @Test
    public void centerPointForEmptyPolygonFails() {
        List<LatLon> positions = new ArrayList<>();
        try {
            SphericalEarthMath.centerPointOfPolygon(positions);
            Assert.fail();
        } catch (IllegalArgumentException ignore) {
        }
    }

    @Test
    public void centerOfPolygonWithNoAreaSimplyReturnsFirstPoint() {
        List<LatLon> positions = new ArrayList<>();
        positions.add(SphericalEarthMathTest.p(10, 10));
        positions.add(SphericalEarthMathTest.p(10, 20));
        positions.add(SphericalEarthMathTest.p(10, 30));
        Assert.assertEquals(SphericalEarthMathTest.p(10, 10), SphericalEarthMath.centerPointOfPolygon(positions));
    }

    @Test
    public void centerOfPolygonAtOrigin() {
        SphericalEarthMathTest.ShorthandLatLon center = SphericalEarthMathTest.p(0, 0);
        Assert.assertEquals(center, SphericalEarthMath.centerPointOfPolygon(SphericalEarthMathTest.createRhombusAround(center, 1)));
    }

    @Test
    public void centerOfPolygonAt180thMeridian() {
        SphericalEarthMathTest.ShorthandLatLon center = SphericalEarthMathTest.p(179.9, 0);
        Assert.assertEquals(center, SphericalEarthMath.centerPointOfPolygon(SphericalEarthMathTest.createRhombusAround(center, 1)));
    }

    /* +++++++++++++++++++++++++++++++ test point in polygon check ++++++++++++++++++++++++++++++ */
    @Test
    public void pointAtPolygonVertexIsInPolygon() {
        List<LatLon> square = SphericalEarthMathTest.createSquareWithPointsAtCenterOfEdgesAround(SphericalEarthMathTest.p(0, 0), 10);
        for (LatLon pos : square) {
            Assert.assertTrue(SphericalEarthMath.isInPolygon(pos, square));
        }
    }

    @Test
    public void pointAtPolygonVertexIsInPolygonAt180thMeridian() {
        List<LatLon> square = SphericalEarthMathTest.createSquareWithPointsAtCenterOfEdgesAround(SphericalEarthMathTest.p(180, 0), 10);
        for (LatLon pos : square) {
            Assert.assertTrue(SphericalEarthMath.isInPolygon(pos, square));
        }
    }

    @Test
    public void pointAtPolygonEdgeIsInPolygon() {
        List<LatLon> square = SphericalEarthMathTest.createSquareAround(SphericalEarthMathTest.p(0, 0), 10);
        Assert.assertTrue(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p(0, 10), square));
        Assert.assertTrue(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p(10, 0), square));
        Assert.assertTrue(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p((-10), 0), square));
        Assert.assertTrue(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p(0, (-10)), square));
    }

    @Test
    public void pointAtPolygonEdgeIsInPolygonAt180thMeridian() {
        List<LatLon> square = SphericalEarthMathTest.createSquareAround(SphericalEarthMathTest.p(180, 0), 10);
        Assert.assertTrue(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p(180, 10), square));
        Assert.assertTrue(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p((-170), 0), square));
        Assert.assertTrue(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p(170, 0), square));
        Assert.assertTrue(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p(180, (-10)), square));
    }

    @Test
    public void pointInPolygonIsInPolygon() {
        Assert.assertTrue(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p(0, 0), Arrays.asList(SphericalEarthMathTest.p(1, 1), SphericalEarthMathTest.p(1, (-2)), SphericalEarthMathTest.p((-2), 1))));
    }

    @Test
    public void pointInPolygonIsInPolygonAt180thMeridian() {
        Assert.assertTrue(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p(180, 0), Arrays.asList(SphericalEarthMathTest.p((-179), 1), SphericalEarthMathTest.p((-179), (-2)), SphericalEarthMathTest.p(178, 1))));
    }

    // The counting number algorithm in particular needs to handle a special case where the ray
    // intersects the polygon in a polygon vertex
    @Test
    public void pointInPolygonWhoseRayIntersectAVertexIsInPolygon() {
        Assert.assertTrue(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p(0, 0), SphericalEarthMathTest.createRhombusAround(SphericalEarthMathTest.p(0, 0), 1)));
    }

    @Test
    public void pointInPolygonWhoseRayIntersectAVertexIsInPolygonAt180thMeridian() {
        Assert.assertTrue(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p(180, 0), SphericalEarthMathTest.createRhombusAround(SphericalEarthMathTest.p(180, 0), 1)));
    }

    @Test
    public void pointOutsidePolygonWhoseRayIntersectAVertexIsOutsidePolygon() {
        List<LatLon> rhombus = SphericalEarthMathTest.createRhombusAround(SphericalEarthMathTest.p(0, 0), 1);
        // four checks here because the ray could be cast in any direction
        Assert.assertFalse(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p((-2), 1), rhombus));
        Assert.assertFalse(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p((-2), 0), rhombus));
        Assert.assertFalse(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p(1, (-2)), rhombus));
        Assert.assertFalse(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p(0, (-2)), rhombus));
    }

    @Test
    public void pointOutsidePolygonWhoseRayIntersectAVertexIsOutsidePolygonAt180thMeridian() {
        List<LatLon> rhombus = SphericalEarthMathTest.createRhombusAround(SphericalEarthMathTest.p(180, 0), 1);
        // four checks here because the ray could be cast in any direction
        Assert.assertFalse(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p(178, 1), rhombus));
        Assert.assertFalse(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p(178, 0), rhombus));
        Assert.assertFalse(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p((-179), (-2)), rhombus));
        Assert.assertFalse(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p(180, (-2)), rhombus));
    }

    @Test
    public void pointInPolygonWhoseRayIntersectsPolygonEdgesIsInsidePolygon() {
        List<LatLon> bonbon = SphericalEarthMathTest.createBonbonAround(SphericalEarthMathTest.p(0, 0));
        Assert.assertTrue(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p(0, 0), bonbon));
    }

    @Test
    public void pointInPolygonWhoseRayIntersectsPolygonEdgesIsInsidePolygonAt180thMeridian() {
        List<LatLon> bonbon = SphericalEarthMathTest.createBonbonAround(SphericalEarthMathTest.p(180, 0));
        Assert.assertTrue(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p(180, 0), bonbon));
    }

    @Test
    public void pointOutsidePolygonWhoseRayIntersectsPolygonEdgesIsOutsidePolygon() {
        List<LatLon> bonbon = SphericalEarthMathTest.createBonbonAround(SphericalEarthMathTest.p(0, 0));
        // four checks here because the ray could be cast in any direction
        Assert.assertFalse(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p((-3), 0), bonbon));
        Assert.assertFalse(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p((+3), 0), bonbon));
        Assert.assertFalse(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p(0, (+3)), bonbon));
        Assert.assertFalse(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p(0, (-3)), bonbon));
    }

    @Test
    public void pointOutsidePolygonWhoseRayIntersectsPolygonEdgesIsOutsidePolygonAt180thMeridian() {
        List<LatLon> bonbon = SphericalEarthMathTest.createBonbonAround(SphericalEarthMathTest.p(180, 0));
        // four checks here because the ray could be cast in any direction
        Assert.assertFalse(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p(177, 0), bonbon));
        Assert.assertFalse(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p((-177), 0), bonbon));
        Assert.assertFalse(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p(180, (+3)), bonbon));
        Assert.assertFalse(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p(180, (-3)), bonbon));
    }

    @Test
    public void pointOutsidePolygonIsOutsidePolygon() {
        Assert.assertFalse(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p(0, 11), SphericalEarthMathTest.createSquareAround(SphericalEarthMathTest.p(0, 0), 10)));
    }

    @Test
    public void pointOutsidePolygonIsOutsidePolygonAt180thMeridian() {
        Assert.assertFalse(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p((-169), 0), SphericalEarthMathTest.createSquareAround(SphericalEarthMathTest.p(180, 0), 10)));
    }

    @Test
    public void polygonDirectionDoesNotMatter() {
        List<LatLon> square = SphericalEarthMathTest.createSquareAround(SphericalEarthMathTest.p(0, 0), 10);
        Collections.reverse(square);
        Assert.assertTrue(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p(5, 5), square));
    }

    @Test
    public void polygonDirectionDoesNotMatterAt180thMeridian() {
        List<LatLon> square = SphericalEarthMathTest.createSquareAround(SphericalEarthMathTest.p(180, 0), 10);
        Collections.reverse(square);
        Assert.assertTrue(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p((-175), 5), square));
    }

    @Test
    public void pointInHoleOfConcavePolygonIsOutsidePolygon() {
        List<LatLon> r = SphericalEarthMathTest.createRhombusWithHoleAround(SphericalEarthMathTest.p(0, 0));
        Assert.assertFalse(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p(0, 0), r));
        Assert.assertFalse(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p(0, 0.5), r));
    }

    @Test
    public void pointInHoleOfConcavePolygonIsOutsidePolygonAt180thMeridian() {
        List<LatLon> r = SphericalEarthMathTest.createRhombusWithHoleAround(SphericalEarthMathTest.p(180, 0));
        Assert.assertFalse(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p(180, 0), r));
        Assert.assertFalse(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p(180, 0.5), r));
    }

    @Test
    public void pointInShellOfConcavePolygonIsInsidePolygon() {
        List<LatLon> r = SphericalEarthMathTest.createRhombusWithHoleAround(SphericalEarthMathTest.p(0, 0));
        Assert.assertTrue(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p(0.75, 0.75), r));
        Assert.assertTrue(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p(1.5, 0), r));
    }

    @Test
    public void pointInShellOfConcavePolygonIsInsidePolygonAt180thMeridian() {
        List<LatLon> r = SphericalEarthMathTest.createRhombusWithHoleAround(SphericalEarthMathTest.p(180, 0));
        Assert.assertTrue(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p((-179.25), 0.75), r));
        Assert.assertTrue(SphericalEarthMath.isInPolygon(SphericalEarthMathTest.p((-178.5), 0), r));
    }

    /* +++++++++++++++++++++++++++++ test point in multipolygon check +++++++++++++++++++++++++++ */
    @Test
    public void emptyListDefinedClockwiseFails() {
        try {
            SphericalEarthMath.isRingDefinedClockwise(Collections.emptyList());
            Assert.fail();
        } catch (IllegalArgumentException ignore) {
        }
    }

    @Test
    public void listDefinedClockwise() {
        List<LatLon> polygon = SphericalEarthMathTest.createRhombusAround(SphericalEarthMathTest.p(0, 0), 1);
        Assert.assertFalse(SphericalEarthMath.isRingDefinedClockwise(polygon));
        Collections.reverse(polygon);
        Assert.assertTrue(SphericalEarthMath.isRingDefinedClockwise(polygon));
    }

    @Test
    public void listDefinedClockwiseOn180thMeridian() {
        List<LatLon> polygon = SphericalEarthMathTest.createRhombusAround(SphericalEarthMathTest.p(180, 0), 1);
        Assert.assertFalse(SphericalEarthMath.isRingDefinedClockwise(polygon));
        Collections.reverse(polygon);
        Assert.assertTrue(SphericalEarthMath.isRingDefinedClockwise(polygon));
    }

    @Test
    public void pointInMultipolygon() {
        SphericalEarthMathTest.ShorthandLatLon origin = SphericalEarthMathTest.p(0, 0);
        List<LatLon> shell = SphericalEarthMathTest.createRhombusAround(origin, 1);
        List<LatLon> hole = SphericalEarthMathTest.createRhombusAround(origin, 3);
        Collections.reverse(hole);
        List<LatLon> shellinhole = SphericalEarthMathTest.createRhombusAround(origin, 5);
        List<List<LatLon>> mp = new ArrayList<>();
        mp.add(shell);
        mp.add(hole);
        mp.add(shellinhole);
        Assert.assertTrue(SphericalEarthMath.isInMultipolygon(origin, mp));
        Assert.assertFalse(SphericalEarthMath.isInMultipolygon(SphericalEarthMathTest.p(0, 2), mp));
        Assert.assertTrue(SphericalEarthMath.isInMultipolygon(SphericalEarthMathTest.p(0, 4), mp));
        Assert.assertFalse(SphericalEarthMath.isInMultipolygon(SphericalEarthMathTest.p(0, 6), mp));
    }

    private static class ShorthandLatLon implements LatLon {
        public ShorthandLatLon(double x, double y) {
            this.x = SphericalEarthMath.normalizeLongitude(x);
            this.y = y;
        }

        final double y;

        final double x;

        @Override
        public double getLatitude() {
            return y;
        }

        @Override
        public double getLongitude() {
            return x;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof LatLon) {
                LatLon o = ((LatLon) (obj));
                return ((o.getLatitude()) == (getLatitude())) && ((o.getLongitude()) == (getLongitude()));
            }
            return false;
        }
    }
}

