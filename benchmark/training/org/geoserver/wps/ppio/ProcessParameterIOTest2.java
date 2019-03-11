/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps.ppio;


import org.geoserver.wps.WPSTestSupport;
import org.geotools.data.Parameter;
import org.junit.Test;


public class ProcessParameterIOTest2 extends WPSTestSupport {
    @Test
    public void testGetWFSByMimeType() {
        Parameter p = new Parameter("WFS", WFSPPIO.class);
        ProcessParameterIO ppio = ProcessParameterIO.find(p, null, "text/xml");
        System.out.println(ppio);
    }
}

