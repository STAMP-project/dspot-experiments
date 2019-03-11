/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wcs2_0.eo.web;


import WCSEOMetadata.ENABLED.key;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.wcs.WCSInfo;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class WCSEOAdminPanelTest extends GeoServerWicketTestSupport {
    private WCSInfo wcs;

    @Test
    public void testEditBasic() {
        // print(tester.getLastRenderedPage(), true, true);
        FormTester ft = tester.newFormTester("form");
        ft.setValue("panel:enabled", true);
        ft.submit();
        // print(tester.getLastRenderedPage(), true, true);
        tester.assertModelValue("form:panel:enabled", true);
        Assert.assertTrue(((boolean) (wcs.getMetadata().get(key, Boolean.class))));
    }
}

