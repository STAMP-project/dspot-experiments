/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.data.layer;


import java.util.List;
import javax.xml.namespace.QName;
import org.apache.wicket.markup.html.form.CheckBox;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.data.test.MockData;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geoserver.web.wicket.GeoServerTablePanel;
import org.junit.Assert;
import org.junit.Test;


public class LayerPageTest extends GeoServerWicketTestSupport {
    public static QName GS_BUILDINGS = new QName(MockData.DEFAULT_URI, "Buildings", MockData.DEFAULT_PREFIX);

    @Test
    public void testBasicActions() {
        login();
        // test that we can load the page
        GeoServerWicketTestSupport.tester.startPage(new LayerPage());
        GeoServerWicketTestSupport.tester.assertRenderedPage(LayerPage.class);
        GeoServerWicketTestSupport.tester.assertNoErrorMessage();
        // check it has two layers
        GeoServerTablePanel table = ((GeoServerTablePanel) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("table")));
        Assert.assertEquals(2, table.getDataProvider().size());
        List<String> workspaces = getWorkspaces(table);
        Assert.assertTrue(workspaces.contains("cite"));
        Assert.assertTrue(workspaces.contains("gs"));
        // sort on workspace once (top to bottom)
        String wsSortPath = "table:listContainer:sortableLinks:3:header:link";
        GeoServerWicketTestSupport.tester.clickLink(wsSortPath, true);
        workspaces = getWorkspaces(table);
        Assert.assertEquals("cite", workspaces.get(0));
        Assert.assertEquals("gs", workspaces.get(1));
        // sort on workspace twice (bottom to top)
        GeoServerWicketTestSupport.tester.clickLink(wsSortPath, true);
        workspaces = getWorkspaces(table);
        Assert.assertEquals("gs", workspaces.get(0));
        Assert.assertEquals("cite", workspaces.get(1));
        // select second layer
        String checkBoxPath = "table:listContainer:items:6:selectItemContainer:selectItem";
        CheckBox selector = ((CheckBox) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage(checkBoxPath)));
        // dirty trick, how to set a form component value without a form
        GeoServerWicketTestSupport.tester.getRequest().setParameter(selector.getInputName(), "true");
        GeoServerWicketTestSupport.tester.executeAjaxEvent(selector, "click");
        Assert.assertEquals(1, table.getSelection().size());
        LayerInfo li = ((LayerInfo) (table.getSelection().get(0)));
        Assert.assertEquals("cite", li.getResource().getStore().getWorkspace().getName());
    }
}

