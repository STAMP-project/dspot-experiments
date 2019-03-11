/**
 * (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.vfny.geoserver;


import ProjectionPolicy.FORCE_DECLARED;
import ProjectionPolicy.NONE;
import ProjectionPolicy.REPROJECT_TO_DECLARED;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.CoverageStoreInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.ResourcePool;
import org.geoserver.data.test.MockData;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.referencing.CRS;
import org.geotools.util.factory.GeoTools;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;
import org.opengis.feature.Feature;


public class ProjectionPolicyTest extends GeoServerSystemTestSupport {
    static WKTReader WKT = new WKTReader();

    @Test
    public void testForce() throws Exception {
        FeatureTypeInfo fti = getCatalog().getFeatureTypeByName(MockData.BASIC_POLYGONS.getLocalPart());
        Assert.assertEquals("EPSG:4269", fti.getSRS());
        Assert.assertEquals(FORCE_DECLARED, fti.getProjectionPolicy());
        FeatureCollection fc = fti.getFeatureSource(null, null).getFeatures();
        Assert.assertEquals(CRS.decode("EPSG:4269"), fc.getSchema().getCoordinateReferenceSystem());
        FeatureIterator fi = fc.features();
        Feature f = fi.next();
        fi.close();
        Assert.assertEquals(CRS.decode("EPSG:4269"), f.getType().getCoordinateReferenceSystem());
    }

    @Test
    public void testReproject() throws Exception {
        FeatureTypeInfo fti = getCatalog().getFeatureTypeByName(MockData.POLYGONS.getLocalPart());
        Assert.assertEquals("EPSG:4326", fti.getSRS());
        Assert.assertEquals(REPROJECT_TO_DECLARED, fti.getProjectionPolicy());
        FeatureCollection fc = fti.getFeatureSource(null, null).getFeatures();
        Assert.assertEquals(CRS.decode("EPSG:4326"), fc.getSchema().getCoordinateReferenceSystem());
        FeatureIterator fi = fc.features();
        Feature f = fi.next();
        // test that geometry was actually reprojected
        Geometry g = ((Geometry) (f.getDefaultGeometryProperty().getValue()));
        Assert.assertFalse(g.equalsExact(ProjectionPolicyTest.WKT.read("POLYGON((500225 500025,500225 500075,500275 500050,500275 500025,500225 500025))")));
        fi.close();
        Assert.assertEquals(CRS.decode("EPSG:4326"), f.getType().getCoordinateReferenceSystem());
    }

    @Test
    public void testLeaveNative() throws Exception {
        FeatureTypeInfo fti = getCatalog().getFeatureTypeByName(MockData.LINES.getLocalPart());
        Assert.assertEquals("EPSG:3004", fti.getSRS());
        Assert.assertEquals(NONE, fti.getProjectionPolicy());
        FeatureCollection fc = fti.getFeatureSource(null, null).getFeatures();
        Assert.assertEquals(CRS.decode("EPSG:32615"), fc.getSchema().getCoordinateReferenceSystem());
        FeatureIterator fi = fc.features();
        Feature f = fi.next();
        // test that the geometry was left in tact
        Geometry g = ((Geometry) (f.getDefaultGeometryProperty().getValue()));
        Assert.assertTrue(g.equalsExact(ProjectionPolicyTest.WKT.read("LINESTRING(500125 500025,500175 500075)")));
        fi.close();
        Assert.assertEquals(CRS.decode("EPSG:32615"), f.getType().getCoordinateReferenceSystem());
    }

    @Test
    public void testWithRename() throws Exception {
        FeatureTypeInfo fti = getCatalog().getFeatureTypeByName("MyPoints");
        Assert.assertEquals("EPSG:4326", fti.getSRS());
        Assert.assertEquals(REPROJECT_TO_DECLARED, fti.getProjectionPolicy());
        FeatureCollection fc = fti.getFeatureSource(null, null).getFeatures();
        Assert.assertEquals(CRS.decode("EPSG:4326"), fc.getSchema().getCoordinateReferenceSystem());
        FeatureIterator fi = fc.features();
        Feature f = fi.next();
        // test that geometry was reprojected
        Geometry g = ((Geometry) (f.getDefaultGeometryProperty().getValue()));
        Assert.assertFalse(g.equalsExact(ProjectionPolicyTest.WKT.read("POINT(500050 500050)")));
        fi.close();
        Assert.assertEquals(CRS.decode("EPSG:4326"), f.getType().getCoordinateReferenceSystem());
    }

    @Test
    public void testForceCoverage() throws Exception {
        // force the data to another projection
        Catalog catalog = getCatalog();
        CoverageInfo ci = catalog.getCoverageByName("usa");
        ci.setProjectionPolicy(FORCE_DECLARED);
        ci.setSRS("EPSG:3857");
        catalog.save(ci);
        ci = catalog.getCoverageByName("usa");
        Assert.assertEquals(FORCE_DECLARED, ci.getProjectionPolicy());
        Assert.assertEquals("EPSG:3857", ci.getSRS());
        // now get the reader via the coverage info
        GridCoverage2DReader r;
        r = ((GridCoverage2DReader) (ci.getGridCoverageReader(null, GeoTools.getDefaultHints())));
        Assert.assertTrue(CRS.equalsIgnoreMetadata(CRS.decode("EPSG:3857"), r.getCoordinateReferenceSystem()));
        // and again without any hint
        r = ((GridCoverage2DReader) (ci.getGridCoverageReader(null, null)));
        Assert.assertTrue(CRS.equalsIgnoreMetadata(CRS.decode("EPSG:3857"), r.getCoordinateReferenceSystem()));
        // get the reader straight: we should get back the native projection
        CoverageStoreInfo store = catalog.getCoverageStoreByName("usa");
        final ResourcePool rpool = catalog.getResourcePool();
        r = ((GridCoverage2DReader) (rpool.getGridCoverageReader(store, GeoTools.getDefaultHints())));
        Assert.assertTrue(CRS.equalsIgnoreMetadata(CRS.decode("EPSG:4326"), r.getCoordinateReferenceSystem()));
    }
}

