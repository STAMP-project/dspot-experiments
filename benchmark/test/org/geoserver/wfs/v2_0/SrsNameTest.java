/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs.v2_0;


import SrsNameStyle.NORMAL;
import SrsNameStyle.URL;
import SrsNameStyle.URN;
import SrsNameStyle.URN2;
import SrsNameStyle.XML;
import org.junit.Test;


public class SrsNameTest extends WFS20TestSupport {
    @Test
    public void testSrsNameSyntax() throws Exception {
        doTestSrsNameSyntax(URN2, false);
        doTestSrsNameSyntax(URN, true);
        doTestSrsNameSyntax(URL, true);
        doTestSrsNameSyntax(NORMAL, true);
        doTestSrsNameSyntax(XML, true);
    }
}

