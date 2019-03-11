/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps.gs;


import junit.framework.Assert;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.util.NullProgressListener;
import org.geotools.process.raster.ContourProcess;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;


/**
 * Test class for the contour process.
 *
 * @author Simone Giannecchini, GeoSolutions SAS
 */
public class ContourProcessTest extends BaseRasterToVectorTest {
    /**
     * Test basic capabilities for the contour process. It works on the DEM tiff and produces a
     * shapefile. Nothing more nothing less.
     */
    @Test
    public void testProcessStandaloneBasicValues() throws Exception {
        GridCoverage2D gc = extractCoverageSubset();
        // extract just two isolines
        final double[] levels = new double[2];
        levels[0] = 1500;
        levels[1] = 1700;
        final ContourProcess process = new ContourProcess();
        final SimpleFeatureCollection fc = process.execute(gc, 0, levels, null, false, false, null, new NullProgressListener());
        Assert.assertNotNull(fc);
        Assert.assertTrue(((fc.size()) > 0));
        SimpleFeatureIterator fi = fc.features();
        while (fi.hasNext()) {
            SimpleFeature sf = fi.next();
            Double value = ((Double) (sf.getAttribute("value")));
            Assert.assertTrue(((value == 1500.0) || (value == 1700.0)));
        } 
        fi.close();
    }

    /**
     * Test basic capabilities for the contour process. It works on the DEM tiff and produces a
     * shapefile. Nothing more nothing less.
     */
    @Test
    public void testProcessStandaloneBasicInterval() throws Exception {
        final GridCoverage2D gc = extractCoverageSubset();
        final double step = 100;
        final ContourProcess process = new ContourProcess();
        final SimpleFeatureCollection fc = process.execute(gc, 0, null, Double.valueOf(step), false, false, null, new NullProgressListener());
        Assert.assertNotNull(fc);
        Assert.assertTrue(((fc.size()) > 0));
        SimpleFeatureIterator fi = fc.features();
        while (fi.hasNext()) {
            SimpleFeature sf = fi.next();
            Double value = ((Double) (sf.getAttribute("value")));
            Assert.assertTrue((value > 0));
        } 
        fi.close();
    }
}

