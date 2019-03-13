/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.admin;


import LayerGroupEditPage.GROUP;
import java.util.Iterator;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.ResourceInfo;
import org.geoserver.catalog.StoreInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.security.AdminRequest;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.geoserver.web.UnauthorizedPage;
import org.geoserver.web.data.layer.LayerPage;
import org.geoserver.web.data.layergroup.LayerGroupEditPage;
import org.geoserver.web.data.layergroup.LayerGroupPage;
import org.geoserver.web.data.store.DataAccessEditPage;
import org.geoserver.web.data.store.DataAccessNewPage;
import org.geoserver.web.data.store.StorePage;
import org.geoserver.web.data.workspace.WorkspaceEditPage;
import org.geoserver.web.data.workspace.WorkspaceNewPage;
import org.geoserver.web.data.workspace.WorkspacePage;
import org.geotools.data.property.PropertyDataStoreFactory;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractAdminPrivilegeTest extends GeoServerWicketTestSupport {
    @Test
    public void testWorkspaceAllPage() throws Exception {
        loginAsCite();
        GeoServerWicketTestSupport.tester.startPage(WorkspacePage.class);
        GeoServerWicketTestSupport.tester.assertRenderedPage(WorkspacePage.class);
        GeoServerWicketTestSupport.tester.assertNoErrorMessage();
        // assert only cite workspace visible
        DataView dv = ((DataView) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("table:listContainer:items")));
        Assert.assertEquals(1, dv.size());
        // the actual web request is finished, so we need to fake another one
        AdminRequest.start(new Object());
        Assert.assertEquals(1, dv.getDataProvider().size());
        WorkspaceInfo ws = ((WorkspaceInfo) (dv.getDataProvider().iterator(0, 1).next()));
        Assert.assertEquals("cite", ws.getName());
    }

    @Test
    public void testWorkspaceNewPage() throws Exception {
        loginAsCite();
        GeoServerWicketTestSupport.tester.startPage(WorkspaceNewPage.class);
        GeoServerWicketTestSupport.tester.assertRenderedPage(UnauthorizedPage.class);
    }

    @Test
    public void testWorkspaceEditPage() throws Exception {
        loginAsCite();
        GeoServerWicketTestSupport.tester.startPage(WorkspaceEditPage.class, new PageParameters().add("name", "cite"));
        GeoServerWicketTestSupport.tester.assertRenderedPage(WorkspaceEditPage.class);
        GeoServerWicketTestSupport.tester.assertNoErrorMessage();
    }

    @Test
    public void testWorkspaceEditPageUnauthorized() throws Exception {
        loginAsCite();
        GeoServerWicketTestSupport.tester.startPage(WorkspaceEditPage.class, new PageParameters().add("name", "cdf"));
        GeoServerWicketTestSupport.tester.assertErrorMessages(new String[]{ "Could not find workspace \"cdf\"" });
    }

    @Test
    public void testLayerAllPage() throws Exception {
        loginAsCite();
        GeoServerWicketTestSupport.tester.startPage(LayerPage.class);
        GeoServerWicketTestSupport.tester.assertRenderedPage(LayerPage.class);
        print(GeoServerWicketTestSupport.tester.getLastRenderedPage(), true, true, true);
        DataView dv = ((DataView) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("table:listContainer:items")));
        Assert.assertEquals(getCatalog().getResourcesByNamespace("cite", ResourceInfo.class).size(), dv.size());
    }

    @Test
    public void testStoreAllPage() throws Exception {
        loginAsCite();
        GeoServerWicketTestSupport.tester.startPage(StorePage.class);
        GeoServerWicketTestSupport.tester.assertRenderedPage(StorePage.class);
        GeoServerWicketTestSupport.tester.assertNoErrorMessage();
        DataView dv = ((DataView) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("table:listContainer:items")));
        Assert.assertEquals(getCatalog().getStoresByWorkspace("cite", StoreInfo.class).size(), dv.size());
    }

    @Test
    public void testStoreNewPage() throws Exception {
        loginAsCite();
        AdminRequest.start(new Object());
        final String dataStoreFactoryDisplayName = new PropertyDataStoreFactory().getDisplayName();
        GeoServerWicketTestSupport.tester.startPage(new DataAccessNewPage(dataStoreFactoryDisplayName));
        GeoServerWicketTestSupport.tester.assertRenderedPage(DataAccessNewPage.class);
        GeoServerWicketTestSupport.tester.assertNoErrorMessage();
        // the actual web request is finished, so we need to fake another one
        AdminRequest.start(new Object());
        DropDownChoice<WorkspaceInfo> wsChoice = ((DropDownChoice<WorkspaceInfo>) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("dataStoreForm:workspacePanel:border:border_body:paramValue")));
        Assert.assertEquals(1, wsChoice.getChoices().size());
        Assert.assertEquals("cite", wsChoice.getChoices().get(0).getName());
    }

    @Test
    public void testStoreEditPage() throws Exception {
        loginAsCite();
        GeoServerWicketTestSupport.tester.startPage(DataAccessEditPage.class, new PageParameters().add("wsName", "cite").add("storeName", "cite"));
        GeoServerWicketTestSupport.tester.assertRenderedPage(DataAccessEditPage.class);
        GeoServerWicketTestSupport.tester.assertNoErrorMessage();
    }

    @Test
    public void testStoreEditPageUnauthorized() throws Exception {
        loginAsCite();
        GeoServerWicketTestSupport.tester.startPage(DataAccessEditPage.class, new PageParameters().add("wsName", "cdf").add("storeName", "cdf"));
        GeoServerWicketTestSupport.tester.assertRenderedPage(StorePage.class);
        GeoServerWicketTestSupport.tester.assertErrorMessages(new String[]{ "Could not find data store \"cdf\" in workspace \"cdf\"" });
    }

    @Test
    public void testLayerGroupAllPageAsAdmin() throws Exception {
        login();
        GeoServerWicketTestSupport.tester.startPage(LayerGroupPage.class);
        GeoServerWicketTestSupport.tester.assertRenderedPage(LayerGroupPage.class);
        Catalog cat = getCatalog();
        DataView view = ((DataView) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("table:listContainer:items")));
        Assert.assertEquals(cat.getLayerGroups().size(), view.getItemCount());
    }

    @Test
    public void testLayerGroupAllPage() throws Exception {
        loginAsCite();
        GeoServerWicketTestSupport.tester.startPage(LayerGroupPage.class);
        GeoServerWicketTestSupport.tester.assertRenderedPage(LayerGroupPage.class);
        Catalog cat = getCatalog();
        DataView view = ((DataView) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("table:listContainer:items")));
        AdminRequest.start(new Object());
        Assert.assertEquals(cat.getLayerGroups().size(), view.getItemCount());
        for (Iterator<Item> it = view.getItems(); it.hasNext();) {
            String name = it.next().get("itemProperties:0:component:link:label").getDefaultModelObjectAsString();
            Assert.assertFalse("sf_local".equals(name));
        }
    }

    @Test
    public void testLayerGroupEditPageAsAdmin() throws Exception {
        login();
        GeoServerWicketTestSupport.tester.startPage(LayerGroupEditPage.class);
        GeoServerWicketTestSupport.tester.assertRenderedPage(LayerGroupEditPage.class);
        GeoServerWicketTestSupport.tester.assertModelValue("publishedinfo:tabs:panel:workspace", null);
        DropDownChoice choice = ((DropDownChoice) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("publishedinfo:tabs:panel:workspace")));
        Assert.assertTrue(choice.isNullValid());
        Assert.assertFalse(choice.isRequired());
    }

    @Test
    public void testLayerGroupEditPage() throws Exception {
        loginAsCite();
        GeoServerWicketTestSupport.tester.startPage(LayerGroupEditPage.class);
        GeoServerWicketTestSupport.tester.assertRenderedPage(LayerGroupEditPage.class);
        Catalog cat = getCatalog();
        GeoServerWicketTestSupport.tester.assertModelValue("publishedinfo:tabs:panel:workspace", cat.getWorkspaceByName("cite"));
        DropDownChoice choice = ((DropDownChoice) (GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("publishedinfo:tabs:panel:workspace")));
        Assert.assertFalse(choice.isNullValid());
        Assert.assertTrue(choice.isRequired());
    }

    @Test
    public void testLayerGroupEditPageGlobal() throws Exception {
        loginAsCite();
        GeoServerWicketTestSupport.tester.startPage(LayerGroupEditPage.class, new PageParameters().add(GROUP, "cite_global"));
        GeoServerWicketTestSupport.tester.assertRenderedPage(LayerGroupEditPage.class);
        // assert all form components disabled except for cancel
        Assert.assertFalse(GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("publishedinfo:tabs:panel:name").isEnabled());
        Assert.assertFalse(GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("publishedinfo:tabs:panel:workspace").isEnabled());
        Assert.assertNull(GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("publishedinfo:save"));
        Assert.assertTrue(GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("publishedinfo:cancel").isEnabled());
    }
}

