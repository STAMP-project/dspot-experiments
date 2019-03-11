/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.data.workspace;


import org.apache.wicket.markup.repeater.data.DataView;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class WorkspacePageTest extends GeoServerWicketTestSupport {
    @Test
    public void testLoad() {
        GeoServerWicketTestSupport.tester.assertRenderedPage(WorkspacePage.class);
        GeoServerWicketTestSupport.tester.assertNoErrorMessage();
        DataView dv = ((DataView) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("table:listContainer:items")));
        Assert.assertEquals(dv.size(), getCatalog().getWorkspaces().size());
        WorkspaceInfo ws = ((WorkspaceInfo) (dv.getDataProvider().iterator(0, 1).next()));
        Assert.assertEquals("cdf", ws.getName());
    }
}

