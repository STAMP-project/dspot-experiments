/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.flow.controller;


import org.junit.Assert;
import org.junit.Test;


/**
 * This test just checks the basic OWS flow controller matches requests as expecte, for a
 * concurrency test see {@link GlobalFlowControllerTest}
 *
 * @author Andrea Aime - OpenGeo
 */
public class OWSRequestMatcherTest extends AbstractFlowControllerTest {
    @Test
    public void testMatchService() {
        OWSRequestMatcher controller = new OWSRequestMatcher("WMS");
        Assert.assertFalse(controller.apply(buildRequest("WFS", "GetFeature", "GML")));
        Assert.assertTrue(controller.apply(buildRequest("WMS", "GetMap", "image/png")));
        Assert.assertTrue(controller.apply(buildRequest("WMS", "GetFeatureInfo", "image/png")));
    }

    @Test
    public void testMatchServiceRequest() {
        OWSRequestMatcher controller = new OWSRequestMatcher("WMS", "GetMap");
        Assert.assertFalse(controller.apply(buildRequest("WFS", "GetFeature", "GML")));
        Assert.assertTrue(controller.apply(buildRequest("WMS", "GETMAP", "image/png")));
        Assert.assertFalse(controller.apply(buildRequest("WMS", "GetFeatureInfo", "image/png")));
    }

    @Test
    public void testMatchServiceRequestOutputFormat() {
        OWSRequestMatcher controller = new OWSRequestMatcher("WMS", "GetMap", "image/png");
        Assert.assertFalse(controller.apply(buildRequest("WFS", "GetFeature", "GML")));
        Assert.assertTrue(controller.apply(buildRequest("WMS", "GETMAP", "image/png")));
        Assert.assertFalse(controller.apply(buildRequest("WMS", "GETMAP", "application/pdf")));
        Assert.assertFalse(controller.apply(buildRequest("WMS", "GetFeatureInfo", "image/png")));
    }
}

