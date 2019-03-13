/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs;


import org.junit.Test;


public class WFSServiceExceptionTest extends WFSTestSupport {
    @Test
    public void testJsonpException() throws Exception {
        testJsonpException("1.1.0");
    }

    @Test
    public void testJsonException() throws Exception {
        testJsonException("1.1.0");
    }

    @Test
    public void testJsonpException20() throws Exception {
        testJsonpException("2.0.0");
    }

    @Test
    public void testJsonException20() throws Exception {
        testJsonException("2.0.0");
    }
}

