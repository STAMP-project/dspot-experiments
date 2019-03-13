/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.vfny.geoserver.crs;


import org.geoserver.test.GeoServerSystemTestSupport;
import org.geotools.referencing.CRS;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.ConcatenatedOperation;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.MathTransform;


public class OvverideTransformationsTest extends GeoServerSystemTestSupport {
    private static final String JAVA_IO_TMPDIR = "java.io.tmpdir";

    private static final String SOURCE_CRS = "EPSG:TEST1";

    private static final String TARGET_CRS = "EPSG:TEST2";

    private static final double[] SRC_TEST_POINT = new double[]{ 39.592654167, 3.084896111 };

    private static final double[] DST_TEST_POINT = new double[]{ 39.594235744481225, 3.0844689951999427 };

    private static String OLD_TMP_VALUE;

    /**
     * Test method for {@link CoordinateOperationFactoryUsingWKT#createCoordinateOperation}.
     *
     * @throws TransformException
     * 		
     */
    @Test
    public void testCreateOperationFromCustomCodes() throws Exception {
        // Test CRSs
        CoordinateReferenceSystem source = CRS.decode(OvverideTransformationsTest.SOURCE_CRS);
        CoordinateReferenceSystem target = CRS.decode(OvverideTransformationsTest.TARGET_CRS);
        MathTransform mt = CRS.findMathTransform(source, target, true);
        // Test MathTransform
        double[] p = new double[2];
        mt.transform(OvverideTransformationsTest.SRC_TEST_POINT, 0, p, 0, 1);
        Assert.assertEquals(p[0], OvverideTransformationsTest.DST_TEST_POINT[0], 1.0E-8);
        Assert.assertEquals(p[1], OvverideTransformationsTest.DST_TEST_POINT[1], 1.0E-8);
    }

    /**
     * Test method for {@link CoordinateOperationFactoryUsingWKT#createCoordinateOperation}.
     *
     * @throws TransformException
     * 		
     */
    @Test
    public void testOverrideEPSGOperation() throws Exception {
        // Test CRSs
        CoordinateReferenceSystem source = CRS.decode("EPSG:4269");
        CoordinateReferenceSystem target = CRS.decode("EPSG:4326");
        MathTransform mt = CRS.findMathTransform(source, target, true);
        // Test MathTransform
        double[] p = new double[2];
        mt.transform(OvverideTransformationsTest.SRC_TEST_POINT, 0, p, 0, 1);
        Assert.assertEquals(p[0], OvverideTransformationsTest.DST_TEST_POINT[0], 1.0E-8);
        Assert.assertEquals(p[1], OvverideTransformationsTest.DST_TEST_POINT[1], 1.0E-8);
    }

    /**
     * Check we are actually using the EPSG database for anything not in override
     *
     * @throws TransformException
     * 		
     */
    @Test
    public void testFallbackOnEPSGDatabaseStd() throws Exception {
        // Test CRSs
        CoordinateReferenceSystem source = CRS.decode("EPSG:3002");
        CoordinateReferenceSystem target = CRS.decode("EPSG:4326");
        CoordinateOperation co = CRS.getCoordinateOperationFactory(true).createOperation(source, target);
        ConcatenatedOperation cco = ((ConcatenatedOperation) (co));
        // the EPSG one only has two steps, the non EPSG one 4
        Assert.assertEquals(2, cco.getOperations().size());
    }

    /**
     * See if we can use the stgeorge grid shift files as the ESPG db would like us to
     */
    @Test
    public void testNadCon() throws Exception {
        CoordinateReferenceSystem crs4138 = CRS.decode("EPSG:4138");
        CoordinateReferenceSystem crs4326 = CRS.decode("EPSG:4326");
        MathTransform mt = CRS.findMathTransform(crs4138, crs4326);
        Assert.assertTrue(mt.toWKT().contains("NADCON"));
        double[] src = new double[]{ -169.625, 56.575 };
        double[] expected = new double[]{ -169.62744, 56.576034 };
        double[] p = new double[2];
        mt.transform(src, 0, p, 0, 1);
        Assert.assertEquals(expected[0], p[0], 1.0E-6);
        Assert.assertEquals(expected[1], p[1], 1.0E-6);
    }
}

