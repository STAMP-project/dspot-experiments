/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.web.admin;


import StyleEditPage.NAME;
import java.util.Iterator;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.geoserver.catalog.Catalog;
import org.geoserver.security.AdminRequest;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geoserver.wms.web.data.StyleEditPage;
import org.geoserver.wms.web.data.StyleNewPage;
import org.geoserver.wms.web.data.StylePage;
import org.junit.Assert;
import org.junit.Test;


public class AdminPrivilegesTest extends GeoServerWicketTestSupport {
    @Test
    public void testStyleAllPageAsAdmin() throws Exception {
        login();
        tester.startPage(StylePage.class);
        tester.assertRenderedPage(StylePage.class);
        tester.debugComponentTrees();
        Catalog cat = getCatalog();
        DataView view = ((DataView) (tester.getComponentFromLastRenderedPage("table:listContainer:items")));
        Assert.assertEquals(cat.getStyles().size(), view.getItemCount());
    }

    @Test
    public void testStyleAllPage() throws Exception {
        loginAsCite();
        tester.startPage(StylePage.class);
        tester.assertRenderedPage(StylePage.class);
        Catalog cat = getCatalog();
        DataView view = ((DataView) (tester.getComponentFromLastRenderedPage("table:listContainer:items")));
        // logged in as CITE, will only see styles in this workspace
        int expected = 1;
        AdminRequest.start(new Object());
        Assert.assertEquals(expected, view.getItemCount());
        for (Iterator<Item> it = view.getItems(); it.hasNext();) {
            String name = it.next().get("itemProperties:0:component:link:label").getDefaultModelObjectAsString();
            Assert.assertFalse("sf_style".equals(name));
        }
    }

    @Test
    public void testStyleNewPageAsAdmin() throws Exception {
        login();
        tester.startPage(StyleNewPage.class);
        tester.assertRenderedPage(StyleNewPage.class);
        tester.assertModelValue("styleForm:context:panel:workspace", null);
        DropDownChoice choice = ((DropDownChoice) (tester.getComponentFromLastRenderedPage("styleForm:context:panel:workspace")));
        Assert.assertTrue(choice.isNullValid());
        Assert.assertFalse(choice.isRequired());
    }

    @Test
    public void testStyleNewPage() throws Exception {
        loginAsCite();
        tester.startPage(StyleNewPage.class);
        tester.assertRenderedPage(StyleNewPage.class);
        Catalog cat = getCatalog();
        tester.assertModelValue("styleForm:context:panel:workspace", cat.getWorkspaceByName("cite"));
        DropDownChoice choice = ((DropDownChoice) (tester.getComponentFromLastRenderedPage("styleForm:context:panel:workspace")));
        Assert.assertFalse(choice.isNullValid());
        Assert.assertTrue(choice.isRequired());
    }

    @Test
    public void testStyleEditPageGlobal() throws Exception {
        loginAsCite();
        tester.startPage(StyleEditPage.class, new PageParameters().add(NAME, "point"));
        tester.assertRenderedPage(StyleEditPage.class);
        // assert all form components disabled except for cancel
        Assert.assertFalse(tester.getComponentFromLastRenderedPage("styleForm:context:panel:name").isEnabled());
        Assert.assertFalse(tester.getComponentFromLastRenderedPage("styleForm:context:panel:workspace").isEnabled());
        Assert.assertFalse(tester.getComponentFromLastRenderedPage("styleForm:context:panel:copy").isEnabled());
        Assert.assertTrue(tester.getComponentFromLastRenderedPage("cancel").isEnabled());
    }
}

