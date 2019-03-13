/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wcs2_0;


import java.util.Map;
import javax.xml.namespace.QName;
import org.geoserver.data.test.MockData;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.referencing.operation.transform.AffineTransform2D;
import org.junit.Test;
import org.opengis.coverage.grid.GridEnvelope;


/**
 * Testing WCS 2.0 Core {@link GetCoverage}
 *
 * @author Simone Giannecchini, GeoSolutions SAS
 * @author Emanuele Tajariol, GeoSolutions SAS
 */
public class GetCoverageTest extends WCSTestSupport {
    private static final QName RAIN = new QName(MockData.SF_URI, "rain", MockData.SF_PREFIX);

    private GridCoverage2DReader coverageReader;

    private AffineTransform2D originalMathTransform;

    @Test
    public void testAllowSubsamplingOnScaleFactor() throws Exception {
        // setup a request
        Map<String, String> raw = setupGetCoverageRain();
        raw.put("scalefactor", "0.5");
        assertScalingByHalf(raw);
    }

    @Test
    public void testAllowSubsamplingOnScaleExtent() throws Exception {
        GridEnvelope range = coverageReader.getOriginalGridRange();
        int width = range.getSpan(0);
        int height = range.getSpan(1);
        // setup a request
        Map<String, String> raw = setupGetCoverageRain();
        raw.put("scaleextent", (((("i(0," + (width / 2)) + "),j(0,") + (height / 2)) + ")"));
        assertScalingByHalf(raw);
    }
}

