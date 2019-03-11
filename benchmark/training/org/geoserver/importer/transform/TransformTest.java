/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2014 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.importer.transform;


import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import junit.framework.Assert;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.CRS;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 *
 *
 * @author Ian Schneider <ischneider@boundlessgeo.com>
 */
public class TransformTest {
    SimpleFeatureTypeBuilder sftb = new SimpleFeatureTypeBuilder();

    @Test
    public void testDateFormatTransform() throws Exception {
        SimpleFeature f = transform(new DateFormatTransform("date", null), "date", String.class, "1980-09-10");
        GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        cal.setTime(((Date) (f.getAttribute("date"))));
        Assert.assertEquals(1980, cal.get(GregorianCalendar.YEAR));
        Assert.assertEquals(9, ((cal.get(GregorianCalendar.MONTH)) + 1));// 0-based month!

        Assert.assertEquals(10, cal.get(GregorianCalendar.DAY_OF_MONTH));
    }

    @Test
    public void testIntegerFieldToDateTransform() throws Exception {
        SimpleFeature f = transform(new IntegerFieldToDateTransform("number"), "number", Integer.class, 1999);
        GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        cal.setTime(((Date) (f.getAttribute("number"))));
        Assert.assertEquals(1999, cal.get(GregorianCalendar.YEAR));
        Assert.assertEquals(1, ((cal.get(GregorianCalendar.MONTH)) + 1));// 0-based month!

        Assert.assertEquals(1, cal.get(GregorianCalendar.DAY_OF_MONTH));
    }

    @Test
    public void testNumberFormatTransform() throws Exception {
        SimpleFeature f = transform(new NumberFormatTransform("number", Double.class), "number", String.class, "1234.5678");
        Assert.assertEquals(1234.5678, f.getAttribute("number"));
    }

    @Test
    public void testReprojectTransform() throws Exception {
        CoordinateReferenceSystem crs = CRS.decode("EPSG:3857");
        sftb.setName("ft");
        sftb.add("geom", Geometry.class, CRS.decode("EPSG:4326"));
        SimpleFeatureType type = sftb.buildFeatureType();
        SimpleFeature f = transformType(new ReprojectTransform(crs), ((SimpleFeatureType) (type)), new GeometryFactory().createPoint(new Coordinate(1.0, 1.0)));
        Point p = ((Point) (f.getAttribute("geom")));
        Assert.assertEquals(111319.49079327357, p.getX());
        Assert.assertEquals(111325.14286638486, p.getY());
    }
}

