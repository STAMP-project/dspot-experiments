/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wcs2_0.eo;


import java.io.IOException;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.wcs2_0.eo.response.GranuleCoverageExtension;
import org.geoserver.wcs2_0.eo.response.GranuleCoverageInfo;
import org.geoserver.wcs2_0.eo.response.SingleGranuleGridCoverageReader;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.coverage.grid.GridCoverageReader;


/**
 * This class is used for testing the {@link GranuleCoverageExtensionTest} class.
 *
 * @author Nicola Lagomarsini
 */
public class GranuleCoverageExtensionTest extends WCSEOTestSupport {
    @Test
    public void testDecodeGranule() {
        String qualifiedName = "sf__timeranges_granule_timeranges.1";
        // Getting the extension
        GranuleCoverageExtension gce = GeoServerExtensions.bean(GranuleCoverageExtension.class);
        // Ensure it is enabled the EO extension
        Assert.assertTrue(gce.isEOEnabled());
        // Handling the id
        String coverageId = gce.handleCoverageId(qualifiedName);
        Assert.assertEquals("sf__timeranges", coverageId);
        String granuleId = gce.getGranuleId(qualifiedName);
        Assert.assertEquals("timeranges.1", granuleId);
        String covId = gce.getCoverageId(qualifiedName);
        Assert.assertEquals("sf__timeranges", covId);
    }

    @Test
    public void testEncodeCoverage() throws IOException {
        // Getting the extension
        GranuleCoverageExtension gce = GeoServerExtensions.bean(GranuleCoverageExtension.class);
        // Getting the Catalog
        Catalog catalog = getCatalog();
        // Get the coverage for the watertemp layer
        CoverageInfo ci = catalog.getCoverageByName("sf:timeranges");
        // Ensure it is enabled the EO extension
        Assert.assertTrue(gce.isEOEnabled());
        // Get the CoverageInfo for the single Granule
        String granuleId = "sf__timeranges_granule_timeranges.1";
        CoverageInfo coverageInfo = gce.handleCoverageInfo(granuleId, ci);
        Assert.assertTrue((coverageInfo instanceof GranuleCoverageInfo));
        GridCoverageReader gridCoverageReader = coverageInfo.getGridCoverageReader(null, null);
        Assert.assertTrue((gridCoverageReader instanceof SingleGranuleGridCoverageReader));
    }
}

