/**
 * (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wcs;


import java.util.Map;
import org.geoserver.security.SecurityUtils;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.referencing.CRS;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.vfny.geoserver.wcs.WcsException;


/**
 * Performs integration tests using a mock {@link ResourceAccessManager}
 *
 * @author Andrea Aime - GeoSolutions
 */
public class ResourceAccessManagerWCSTest extends AbstractGetCoverageTest {
    @Test
    public void testNoLimits() throws Exception {
        Map<String, Object> raw = getWorld();
        authenticate("cite", "cite");
        GridCoverage[] coverages = executeGetCoverageKvp(raw);
        // basic checks
        Assert.assertEquals(1, coverages.length);
        GridCoverage2D coverage = ((GridCoverage2D) (coverages[0]));
        final CoordinateReferenceSystem wgs84Flipped = CRS.decode("urn:ogc:def:crs:EPSG:6.6:4326");
        Assert.assertEquals(wgs84Flipped, coverage.getEnvelope().getCoordinateReferenceSystem());
        Assert.assertEquals((-90.0), coverage.getEnvelope().getMinimum(0), 1.0E-6);
        Assert.assertEquals((-180.0), coverage.getEnvelope().getMinimum(1), 1.0E-6);
        Assert.assertEquals(90.0, coverage.getEnvelope().getMaximum(0), 1.0E-6);
        Assert.assertEquals(180.0, coverage.getEnvelope().getMaximum(1), 1.0E-6);
        // make sure it has not been cropped
        int[] value = new int[3];
        // some point in USA
        coverage.evaluate(((DirectPosition) (new org.geotools.geometry.DirectPosition2D(wgs84Flipped, 40, (-90)))), value);
        Assert.assertTrue(((value[0]) > 0));
        Assert.assertTrue(((value[1]) > 0));
        Assert.assertTrue(((value[2]) > 0));
        // some point in Europe
        coverage.evaluate(((DirectPosition) (new org.geotools.geometry.DirectPosition2D(wgs84Flipped, 45, 12))), value);
        Assert.assertTrue(((value[0]) > 0));
        Assert.assertTrue(((value[1]) > 0));
        Assert.assertTrue(((value[2]) > 0));
        CoverageCleanerCallback.disposeCoverage(coverage);
    }

    @Test
    public void testNoAccess() throws Exception {
        Map<String, Object> raw = getWorld();
        authenticate("cite_noworld", "cite");
        try {
            executeGetCoverageKvp(raw);
            Assert.fail("This should have failed with an exception");
        } catch (WcsException e) {
            // we should have got some complaint about the layer not being there
            Assert.assertTrue(e.getMessage().matches(".*wcs:World.*"));
        }
    }

    @Test
    public void testChallenge() throws Exception {
        Map<String, Object> raw = getWorld();
        authenticate("cite_noworld_challenge", "cite");
        try {
            executeGetCoverageKvp(raw);
            Assert.fail("This should have failed with a security exception");
        } catch (Throwable e) {
            // make sure we are dealing with some security exception
            Throwable se = null;
            while (((e.getCause()) != null) && ((e.getCause()) != e)) {
                e = e.getCause();
                if (SecurityUtils.isSecurityException(e)) {
                    se = e;
                }
            } 
            if (e == null) {
                Assert.fail("We should have got some sort of SpringSecurityException");
            } else {
                // some mumbling about not having enough privileges
                Assert.assertTrue(se.getMessage().contains("World"));
                Assert.assertTrue(se.getMessage().contains("privileges"));
            }
        }
    }

    @Test
    public void testRasterFilterUSA() throws Exception {
        Map<String, Object> raw = getWorld();
        authenticate("cite_usa", "cite");
        GridCoverage[] coverages = executeGetCoverageKvp(raw);
        // basic checks
        Assert.assertEquals(1, coverages.length);
        GridCoverage2D coverage = ((GridCoverage2D) (coverages[0]));
        final CoordinateReferenceSystem wgs84Flipped = CRS.decode("urn:ogc:def:crs:EPSG:6.6:4326");
        Assert.assertEquals(wgs84Flipped, coverage.getEnvelope().getCoordinateReferenceSystem());
        Assert.assertEquals((-90.0), coverage.getEnvelope().getMinimum(0), 1.0E-6);
        Assert.assertEquals((-180.0), coverage.getEnvelope().getMinimum(1), 1.0E-6);
        Assert.assertEquals(90.0, coverage.getEnvelope().getMaximum(0), 1.0E-6);
        Assert.assertEquals(180.0, coverage.getEnvelope().getMaximum(1), 1.0E-6);
        // make sure it has been cropped
        int[] value = new int[3];
        // some point in USA
        coverage.evaluate(((DirectPosition) (new org.geotools.geometry.DirectPosition2D(wgs84Flipped, 40, (-90)))), value);
        Assert.assertTrue(((value[0]) > 0));
        Assert.assertTrue(((value[1]) > 0));
        Assert.assertTrue(((value[2]) > 0));
        // some point in Europe (should have been cropped, we should get the bkg value)
        coverage.evaluate(((DirectPosition) (new org.geotools.geometry.DirectPosition2D(wgs84Flipped, 45, 12))), value);
        Assert.assertEquals(0, value[0]);
        Assert.assertEquals(0, value[1]);
        Assert.assertEquals(0, value[2]);
        CoverageCleanerCallback.disposeCoverage(coverage);
    }
}

