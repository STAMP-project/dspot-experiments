/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.filters;


import XFrameOptionsFilter.GEOSERVER_XFRAME_POLICY;
import XFrameOptionsFilter.GEOSERVER_XFRAME_SHOULD_SET_POLICY;
import java.io.IOException;
import javax.servlet.ServletException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Simple test to make sure the XFrameOptions filter works and is configurable.
 */
public class XFrameOptionsFilterTest {
    @Test
    public void doFilter() throws Exception {
        String header = getXStreamHeader();
        Assert.assertEquals("Expect default XFrameOption to be DENY", "SAMEORIGIN", header);
    }

    @Test
    public void testFilterWithNoSetPolicy() throws IOException, ServletException {
        String currentShouldSetProperty = System.getProperty(GEOSERVER_XFRAME_SHOULD_SET_POLICY);
        System.setProperty(GEOSERVER_XFRAME_SHOULD_SET_POLICY, "false");
        String header = getXStreamHeader();
        Assert.assertEquals("Expect default XFrameOption to be null", null, header);
        if (currentShouldSetProperty != null) {
            System.setProperty(GEOSERVER_XFRAME_SHOULD_SET_POLICY, currentShouldSetProperty);
        }
    }

    @Test
    public void testFilterWithSameOrigin() throws IOException, ServletException {
        String currentShouldSetProperty = System.getProperty(GEOSERVER_XFRAME_POLICY);
        System.setProperty(GEOSERVER_XFRAME_POLICY, "DENY");
        String header = getXStreamHeader();
        Assert.assertEquals("Expect default XFrameOption to be DENY", "DENY", header);
        if (currentShouldSetProperty != null) {
            System.setProperty(GEOSERVER_XFRAME_POLICY, currentShouldSetProperty);
        }
    }
}

