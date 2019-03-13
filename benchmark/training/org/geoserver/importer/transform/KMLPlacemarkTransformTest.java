/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.importer.transform;


import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.kml.Folder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;


public class KMLPlacemarkTransformTest extends TestCase {
    private KMLPlacemarkTransform kmlPlacemarkTransform;

    private SimpleFeatureType origType;

    private SimpleFeatureType transformedType;

    public void testFeatureType() throws Exception {
        SimpleFeatureType result = kmlPlacemarkTransform.convertFeatureType(origType);
        assertBinding(result, "LookAt", Point.class);
        assertBinding(result, "Region", LinearRing.class);
        assertBinding(result, "Folder", String.class);
    }

    public void testGeometry() throws Exception {
        SimpleFeatureBuilder fb = new SimpleFeatureBuilder(origType);
        GeometryFactory gf = new GeometryFactory();
        fb.set("Geometry", gf.createPoint(new Coordinate(3.0, 4.0)));
        SimpleFeature feature = fb.buildFeature("testgeometry");
        TestCase.assertEquals("Unexpected Geometry class", Point.class, feature.getAttribute("Geometry").getClass());
        TestCase.assertEquals("Unexpected default geometry", Point.class, feature.getDefaultGeometry().getClass());
        SimpleFeature result = kmlPlacemarkTransform.convertFeature(feature, transformedType);
        TestCase.assertEquals("Invalid Geometry class", Point.class, result.getAttribute("Geometry").getClass());
        TestCase.assertEquals("Unexpected default geometry", Point.class, feature.getDefaultGeometry().getClass());
    }

    public void testLookAtProperty() throws Exception {
        SimpleFeatureBuilder fb = new SimpleFeatureBuilder(origType);
        GeometryFactory gf = new GeometryFactory();
        Coordinate c = new Coordinate(3.0, 4.0);
        fb.set("LookAt", gf.createPoint(c));
        SimpleFeature feature = fb.buildFeature("testlookat");
        TestCase.assertEquals("Unexpected LookAt attribute class", Point.class, feature.getAttribute("LookAt").getClass());
        SimpleFeature result = kmlPlacemarkTransform.convertFeature(feature, transformedType);
        TestCase.assertEquals("Invalid LookAt attribute class", Point.class, result.getAttribute("LookAt").getClass());
    }

    public void testFolders() throws Exception {
        SimpleFeatureBuilder fb = new SimpleFeatureBuilder(origType);
        List<Folder> folders = new ArrayList<Folder>(2);
        folders.add(new Folder("foo"));
        folders.add(new Folder("bar"));
        fb.featureUserData("Folder", folders);
        SimpleFeature feature = fb.buildFeature("testFolders");
        SimpleFeature newFeature = kmlPlacemarkTransform.convertFeature(feature, transformedType);
        TestCase.assertEquals("foo -> bar", newFeature.getAttribute("Folder"));
    }
}

