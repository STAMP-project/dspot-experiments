/**
 * (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wcs.xml;


import java.io.StringReader;
import net.opengis.wcs10.DescribeCoverageType;
import org.geoserver.wcs.xml.v1_0_0.WcsXmlReader;
import org.geotools.wcs.WCSConfiguration;
import org.junit.Assert;
import org.junit.Test;


public class DescribeCoverageXmlParserTest {
    private WCSConfiguration configuration;

    private WcsXmlReader reader;

    @Test
    public void testBasic() throws Exception {
        String request = "<DescribeCoverage" + ((((((("  version=\"1.0.0\"" + "  service=\"WCS\"") + "  xmlns=\"http://www.opengis.net/wcs\"") + "  xmlns:nurc=\"http://www.nurc.nato.int\"") + "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"") + "  xsi:schemaLocation=\"http://www.opengis.net/wcs schemas/wcs/1.0.0/describeCoverage.xsd\">") + "    <Coverage>nurc:Pk50095</Coverage>    ") + "</DescribeCoverage>");
        // smoke test, we only try out a very basic request
        DescribeCoverageType cap = ((DescribeCoverageType) (reader.read(null, new StringReader(request), null)));
        Assert.assertEquals("WCS", cap.getService());
        Assert.assertEquals("1.0.0", cap.getVersion());
        Assert.assertEquals(1, cap.getCoverage().size());
        Assert.assertEquals("nurc:Pk50095", cap.getCoverage().get(0));
    }
}

