/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs.v1_1;


import org.geoserver.wfs.WFSTestSupport;
import org.geotools.xsd.Parser;
import org.junit.Assert;
import org.junit.Test;


public class WFSXmlTest extends WFSTestSupport {
    @Test
    public void testValid() throws Exception {
        Parser parser = new Parser(configuration());
        parser.parse(getClass().getResourceAsStream("GetFeature.xml"));
        Assert.assertEquals(0, parser.getValidationErrors().size());
    }

    @Test
    public void testInvalid() throws Exception {
        Parser parser = new Parser(configuration());
        parser.setValidating(true);
        parser.parse(getClass().getResourceAsStream("GetFeature-invalid.xml"));
        Assert.assertTrue(((parser.getValidationErrors().size()) > 0));
    }
}

