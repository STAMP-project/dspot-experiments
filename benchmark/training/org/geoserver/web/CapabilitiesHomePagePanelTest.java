/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web;


import java.util.Arrays;
import org.apache.wicket.markup.html.WebPage;
import org.geoserver.web.CapabilitiesHomePagePanel.CapsInfo;
import org.geotools.util.Version;
import org.junit.Test;


public class CapabilitiesHomePagePanelTest extends GeoServerWicketTestSupport {
    /* Empy WebPage to aid in testing CapabilitiesHomePagePanel as a component of this page (the
    accompanying CapabilitiesHomePagePanelTest$TestPage.html. Needed since
    WicketTester.assertListView does not work for a detached component, so this void page
    acts as container
     */
    public static class TestPage extends WebPage {
        private static final long serialVersionUID = -4374237095130771859L;
    }

    @Test
    public void testCapabilitiesLinks() {
        CapsInfo ci1 = new CapsInfo("FakeService1", new Version("1.0.0"), "../caps1_v1");
        CapsInfo ci2 = new CapsInfo("FakeService1", new Version("1.1.0"), "../caps1_v2");
        CapsInfo ci3 = new CapsInfo("FakeService2", new Version("1.1.0"), "../caps2");
        CapabilitiesHomePagePanel panel = new CapabilitiesHomePagePanel("capsList", Arrays.asList(ci1, ci2, ci3));
        CapabilitiesHomePagePanelTest.TestPage page = new CapabilitiesHomePagePanelTest.TestPage();
        page.add(panel);
        GeoServerWicketTestSupport.tester.startPage(page);
        // super.print(page, false, true);
        GeoServerWicketTestSupport.tester.assertListView("capsList:services", Arrays.asList("fakeservice1", "fakeservice2"));
        GeoServerWicketTestSupport.tester.assertLabel("capsList:services:0:service", "FAKESERVICE1");
        GeoServerWicketTestSupport.tester.assertLabel("capsList:services:1:service", "FAKESERVICE2");
        GeoServerWicketTestSupport.tester.assertListView("capsList:services:0:versions", Arrays.asList(ci1, ci2));
        GeoServerWicketTestSupport.tester.assertListView("capsList:services:1:versions", Arrays.asList(ci3));
        GeoServerWicketTestSupport.tester.assertLabel("capsList:services:0:versions:0:link:version", "1.0.0");
        GeoServerWicketTestSupport.tester.assertLabel("capsList:services:0:versions:1:link:version", "1.1.0");
    }
}

