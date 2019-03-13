/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps.web;


import java.util.Iterator;
import org.apache.wicket.Component;
import org.apache.wicket.markup.repeater.OddEvenItem;
import org.apache.wicket.markup.repeater.data.DataView;
import org.geoserver.wps.ProcessGroupInfo;
import org.geoserver.wps.WPSInfo;
import org.geoserver.wps.web.FilteredProcessesProvider.FilteredProcess;
import org.junit.Assert;
import org.junit.Test;


public class ProcessSelectionPageTest extends WPSPagesTestSupport {
    @Test
    public void test() throws Exception {
        login();
        WPSInfo wps = getGeoServerApplication().getGeoServer().getService(WPSInfo.class);
        ProcessGroupInfo pgi = getGeoGroup(wps.getProcessGroups());
        // start the page
        WPSAccessRulePage accessRulePage = ((WPSAccessRulePage) (tester.startPage(new WPSAccessRulePage())));
        ProcessSelectionPage selectionPage = ((ProcessSelectionPage) (tester.startPage(new ProcessSelectionPage(accessRulePage, pgi))));
        // print(selectionPage, true, true);
        // grab the table and check its contents
        DataView datas = ((DataView) (tester.getComponentFromLastRenderedPage("form:selectionTable:listContainer:items")));
        for (Iterator it = datas.getItems(); it.hasNext();) {
            OddEvenItem item = ((OddEvenItem) (it.next()));
            FilteredProcess fp = ((FilteredProcess) (item.getDefaultModelObject()));
            Component validatedLabel = item.get("itemProperties:5:component");
            if (fp.getName().getLocalPart().equals("buffer")) {
                Assert.assertEquals("*", validatedLabel.getDefaultModelObject());
            } else {
                Assert.assertEquals("", validatedLabel.getDefaultModelObject());
            }
        }
    }
}

