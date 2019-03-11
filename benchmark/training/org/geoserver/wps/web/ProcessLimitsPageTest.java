/**
 * (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps.web;


import org.apache.wicket.markup.repeater.OddEvenItem;
import org.geoserver.wps.ProcessGroupInfo;
import org.geoserver.wps.ProcessInfo;
import org.geoserver.wps.WPSInfo;
import org.geoserver.wps.web.FilteredProcessesProvider.FilteredProcess;
import org.geoserver.wps.web.ProcessLimitsPage.InputLimit;
import org.geotools.util.NumberRange;
import org.junit.Assert;
import org.junit.Test;


public class ProcessLimitsPageTest extends WPSPagesTestSupport {
    @Test
    public void test() throws Exception {
        login();
        WPSInfo wps = getGeoServerApplication().getGeoServer().getService(WPSInfo.class);
        ProcessGroupInfo rasterGroup = getRasterGroup(wps.getProcessGroups());
        ProcessInfo pi = getProcess(rasterGroup.getFilteredProcesses(), "Contour");
        // start the pages
        WPSAccessRulePage accessRulePage = ((WPSAccessRulePage) (tester.startPage(new WPSAccessRulePage())));
        ProcessSelectionPage selectionPage = ((ProcessSelectionPage) (tester.startPage(new ProcessSelectionPage(accessRulePage, rasterGroup))));
        FilteredProcess filteredProcess = new FilteredProcess(pi.getName(), "");
        filteredProcess.setValidators(pi.getValidators());
        ProcessLimitsPage limitsPage = ((ProcessLimitsPage) (tester.startPage(new ProcessLimitsPage(selectionPage, filteredProcess))));
        // print(limitsPage, true, true);
        // grab the table and check its contents (the order should be stable, we are iterating over
        // the process inputs)
        OddEvenItem item = ((OddEvenItem) (tester.getComponentFromLastRenderedPage("form:table:listContainer:items:1")));
        // max input size
        InputLimit il = ((InputLimit) (item.getDefaultModelObject()));
        Assert.assertEquals("data", il.getName());
        Assert.assertEquals(Integer.valueOf(1), item.get("itemProperties:2:component:text").getDefaultModelObject());
        // levels range validator
        item = ((OddEvenItem) (tester.getComponentFromLastRenderedPage("form:table:listContainer:items:3")));
        il = ((InputLimit) (item.getDefaultModelObject()));
        Assert.assertEquals("levels", il.getName());
        Assert.assertEquals(new NumberRange(Double.class, (-8000.0), 8000.0), item.get("itemProperties:2:component:range").getDefaultModelObject());
        // multiplicity validator
        item = ((OddEvenItem) (tester.getComponentFromLastRenderedPage("form:table:listContainer:items:4")));
        il = ((InputLimit) (item.getDefaultModelObject()));
        Assert.assertEquals("levels", il.getName());
        Assert.assertEquals(Integer.valueOf(3), item.get("itemProperties:2:component:text").getDefaultModelObject());
    }
}

