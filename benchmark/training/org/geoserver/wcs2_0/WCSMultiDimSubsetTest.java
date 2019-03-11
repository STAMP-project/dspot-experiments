/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wcs2_0;


import ImageMosaicFormat.USE_JAI_IMAGEREAD;
import javax.xml.namespace.QName;
import junit.framework.TestCase;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.data.test.CiteTestData;
import org.geoserver.wcs.CoverageCleanerCallback;
import org.geoserver.wcs2_0.response.GranuleStack;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.geometry.Envelope2D;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.coverage.grid.GridCoverageReader;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValue;
import org.springframework.mock.web.MockHttpServletResponse;


public class WCSMultiDimSubsetTest extends WCSNetCDFBaseTest {
    private static final QName LAMBERTMOSAIC = new QName(CiteTestData.WCS_URI, "lambert", CiteTestData.WCS_PREFIX);

    /**
     * Tests if we can select a single pixel value using a WCS request
     */
    @Test
    public void sliceLambert() throws Exception {
        // check we can read it as a TIFF and it is similare to the original one
        GridCoverage2D targetCoverage = null;
        GridCoverage2D sourceCoverage = null;
        GridCoverageReader coverageReader = null;
        try {
            // === slicing on Y axis
            // source
            CoverageInfo coverageInfo = getCatalog().getCoverageByName(WCSMultiDimSubsetTest.LAMBERTMOSAIC.getLocalPart());
            coverageReader = coverageInfo.getGridCoverageReader(null, null);
            final ParameterValue<Boolean> useJAI = USE_JAI_IMAGEREAD.createValue();
            useJAI.setValue(false);
            sourceCoverage = ((GridCoverage2D) (coverageReader.read(new GeneralParameterValue[]{ useJAI })));
            final Envelope2D sourceEnvelope = sourceCoverage.getEnvelope2D();
            // subsample using the original extension
            MockHttpServletResponse response = getAsServletResponse((((((((((("wcs?request=GetCoverage&service=WCS&version=2.0.1" + ("&coverageId=wcs__lambert&&Format=application/custom" + "&subset=E,http://www.opengis.net/def/crs/EPSG/0/31300(")) + (sourceEnvelope.x)) + ",") + ((sourceEnvelope.x) + 25)) + ")") + "&subset=N,http://www.opengis.net/def/crs/EPSG/0/31300(") + (sourceEnvelope.y)) + ",") + ((sourceEnvelope.y) + 25)) + ")"));
            Assert.assertNotNull(response);
            targetCoverage = applicationContext.getBean(WCSResponseInterceptor.class).getLastResult();
            TestCase.assertEquals(((Object) (sourceCoverage.getCoordinateReferenceSystem())), ((Object) (targetCoverage.getCoordinateReferenceSystem())));
            Assert.assertTrue((targetCoverage instanceof GranuleStack));
            GridCoverage2D firstResult = getGranules().get(0);
            // checks
            TestCase.assertEquals(1, firstResult.getGridGeometry().getGridRange().getSpan(0));
            TestCase.assertEquals(1, firstResult.getGridGeometry().getGridRange().getSpan(1));
            TestCase.assertEquals(0, firstResult.getGridGeometry().getGridRange().getLow(0));
            TestCase.assertEquals(1, firstResult.getGridGeometry().getGridRange().getLow(1));
        } finally {
            if (coverageReader != null) {
                try {
                    coverageReader.dispose();
                } catch (Exception e) {
                    // Ignore it
                }
            }
            if (targetCoverage != null) {
                try {
                    CoverageCleanerCallback.disposeCoverage(targetCoverage);
                } catch (Exception e) {
                    // Ignore it
                }
            }
            if (sourceCoverage != null) {
                try {
                    CoverageCleanerCallback.disposeCoverage(sourceCoverage);
                } catch (Exception e) {
                    // Ignore it
                }
            }
        }
    }
}

