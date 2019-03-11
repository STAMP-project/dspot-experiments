/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.data.workspace;


import FeedbackMessage.ERROR;
import MockData.CITE_PREFIX;
import MockData.CITE_URI;
import java.util.List;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.NamespaceInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.config.GeoServer;
import org.geoserver.config.SettingsInfo;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class WorkspaceEditPageTest extends GeoServerWicketTestSupport {
    private WorkspaceInfo citeWorkspace;

    @Test
    public void testURIRequired() {
        FormTester form = GeoServerWicketTestSupport.tester.newFormTester("form");
        form.setValue("uri", "");
        form.submit();
        GeoServerWicketTestSupport.tester.assertRenderedPage(WorkspaceEditPage.class);
        GeoServerWicketTestSupport.tester.assertErrorMessages(new String[]{ "Field 'uri' is required." });
    }

    @Test
    public void testLoad() {
        GeoServerWicketTestSupport.tester.assertRenderedPage(WorkspaceEditPage.class);
        GeoServerWicketTestSupport.tester.assertNoErrorMessage();
        GeoServerWicketTestSupport.tester.assertModelValue("form:name", CITE_PREFIX);
        GeoServerWicketTestSupport.tester.assertModelValue("form:uri", CITE_URI);
    }

    @Test
    public void testValidURI() {
        FormTester form = GeoServerWicketTestSupport.tester.newFormTester("form");
        form.setValue("uri", "http://www.geoserver.org");
        form.submit();
        GeoServerWicketTestSupport.tester.assertRenderedPage(WorkspacePage.class);
        GeoServerWicketTestSupport.tester.assertNoErrorMessage();
    }

    @Test
    public void testInvalidURI() {
        FormTester form = GeoServerWicketTestSupport.tester.newFormTester("form");
        form.setValue("uri", "not a valid uri");
        form.submit();
        GeoServerWicketTestSupport.tester.assertRenderedPage(WorkspaceEditPage.class);
        List messages = GeoServerWicketTestSupport.tester.getMessages(ERROR);
        Assert.assertEquals(1, messages.size());
        Assert.assertEquals("Invalid URI syntax: not a valid uri", getMessage());
    }

    /**
     * See GEOS-3322, upon a namespace URI change the datastores connection parameter shall be
     * changed accordingly
     */
    @Test
    public void testUpdatesDataStoresNamespace() {
        final Catalog catalog = getCatalog();
        final List<DataStoreInfo> storesInitial = catalog.getStoresByWorkspace(citeWorkspace, DataStoreInfo.class);
        final NamespaceInfo citeNamespace = catalog.getNamespaceByPrefix(citeWorkspace.getName());
        for (DataStoreInfo store : storesInitial) {
            Assert.assertEquals(citeNamespace.getURI(), store.getConnectionParameters().get("namespace"));
        }
        FormTester form = GeoServerWicketTestSupport.tester.newFormTester("form");
        final String newNsURI = "http://www.geoserver.org/changed";
        form.setValue("uri", newNsURI);
        form.submit();
        GeoServerWicketTestSupport.tester.assertNoErrorMessage();
        List<DataStoreInfo> storesChanged = catalog.getStoresByWorkspace(citeWorkspace, DataStoreInfo.class);
        for (DataStoreInfo store : storesChanged) {
            Assert.assertEquals(newNsURI, store.getConnectionParameters().get("namespace"));
        }
    }

    @Test
    public void testDefaultCheckbox() {
        Assert.assertFalse(getCatalog().getDefaultWorkspace().getName().equals(CITE_PREFIX));
        FormTester form = GeoServerWicketTestSupport.tester.newFormTester("form");
        form.setValue("default", "true");
        form.submit();
        GeoServerWicketTestSupport.tester.assertNoErrorMessage();
        Assert.assertEquals(CITE_PREFIX, getCatalog().getDefaultWorkspace().getName());
    }

    @Test
    public void testEnableSettings() throws Exception {
        GeoServer gs = getGeoServer();
        Assert.assertNull(gs.getSettings(citeWorkspace));
        FormTester form = GeoServerWicketTestSupport.tester.newFormTester("form");
        form.setValue("settings:enabled", true);
        form.submit();
        GeoServerWicketTestSupport.tester.assertNoErrorMessage();
        Assert.assertNotNull(gs.getSettings(citeWorkspace));
    }

    @Test
    public void testLocalworkspaceRemovePrefix() throws Exception {
        GeoServer gs = getGeoServer();
        SettingsInfo settings = gs.getFactory().createSettings();
        settings.setLocalWorkspaceIncludesPrefix(true);
        settings.setWorkspace(citeWorkspace);
        gs.add(settings);
        Assert.assertNotNull(gs.getSettings(citeWorkspace));
        GeoServerWicketTestSupport.tester.startPage(new WorkspaceEditPage(citeWorkspace));
        GeoServerWicketTestSupport.tester.assertRenderedPage(WorkspaceEditPage.class);
        FormTester form = GeoServerWicketTestSupport.tester.newFormTester("form");
        form.setValue("settings:settingsContainer:otherSettings:localWorkspaceIncludesPrefix", false);
        form.submit();
        Assert.assertEquals(false, settings.isLocalWorkspaceIncludesPrefix());
    }

    @Test
    public void testDisableSettings() throws Exception {
        GeoServer gs = getGeoServer();
        SettingsInfo settings = gs.getFactory().createSettings();
        settings.setProxyBaseUrl("http://foo.org");
        settings.setWorkspace(citeWorkspace);
        gs.add(settings);
        Assert.assertNotNull(gs.getSettings(citeWorkspace));
        GeoServerWicketTestSupport.tester.startPage(new WorkspaceEditPage(citeWorkspace));
        GeoServerWicketTestSupport.tester.assertRenderedPage(WorkspaceEditPage.class);
        FormTester form = GeoServerWicketTestSupport.tester.newFormTester("form");
        Assert.assertEquals("http://foo.org", form.getTextComponentValue("settings:settingsContainer:otherSettings:proxyBaseUrl"));
        form.setValue("settings:enabled", false);
        form.submit();
        Assert.assertNull(gs.getSettings(citeWorkspace));
    }

    @Test
    public void testDisablingIsolatedWorkspace() {
        // create two workspaces with the same namespace, one of them is isolated
        createWorkspace("test_a1", "http://www.test_a.org", false);
        createWorkspace("test_a2", "http://www.test_a.org", true);
        // edit the second workspace to make it non isolated, this should fail
        updateWorkspace("test_a2", "test_a2", "http://www.test_a.org", false);
        GeoServerWicketTestSupport.tester.assertRenderedPage(WorkspaceEditPage.class);
        GeoServerWicketTestSupport.tester.assertErrorMessages(new String[]{ "Namespace with URI 'http://www.test_a.org' already exists." });
        // edit the first workspace and make it isolated
        updateWorkspace("test_a1", "test_a1", "http://www.test_a.org", true);
        GeoServerWicketTestSupport.tester.assertRenderedPage(WorkspacePage.class);
        GeoServerWicketTestSupport.tester.assertNoErrorMessage();
        // edit the second workspace to make it non isolated
        updateWorkspace("test_a2", "test_a2", "http://www.test_a.org", false);
        GeoServerWicketTestSupport.tester.assertRenderedPage(WorkspacePage.class);
        GeoServerWicketTestSupport.tester.assertNoErrorMessage();
        // check that the catalog contains the expected objects
        Catalog catalog = getCatalog();
        // validate the first workspace
        Assert.assertThat(catalog.getWorkspaceByName("test_a1"), CoreMatchers.notNullValue());
        Assert.assertThat(catalog.getWorkspaceByName("test_a1").isIsolated(), CoreMatchers.is(true));
        Assert.assertThat(catalog.getNamespaceByPrefix("test_a1"), CoreMatchers.notNullValue());
        Assert.assertThat(catalog.getNamespaceByPrefix("test_a1").getURI(), CoreMatchers.is("http://www.test_a.org"));
        Assert.assertThat(catalog.getNamespaceByPrefix("test_a1").isIsolated(), CoreMatchers.is(true));
        // validate the second workspace
        Assert.assertThat(catalog.getWorkspaceByName("test_a2"), CoreMatchers.notNullValue());
        Assert.assertThat(catalog.getWorkspaceByName("test_a2").isIsolated(), CoreMatchers.is(false));
        Assert.assertThat(catalog.getNamespaceByPrefix("test_a2"), CoreMatchers.notNullValue());
        Assert.assertThat(catalog.getNamespaceByPrefix("test_a2").getURI(), CoreMatchers.is("http://www.test_a.org"));
        Assert.assertThat(catalog.getNamespaceByPrefix("test_a2").isIsolated(), CoreMatchers.is(false));
        // validate the global namespace, i.e. non isolated namespace
        Assert.assertThat(catalog.getNamespaceByURI("http://www.test_a.org").getPrefix(), CoreMatchers.is("test_a2"));
    }

    @Test
    public void testUpdatingIsolatedWorkspaceName() {
        // create two workspaces with the same namespace, one of them is isolated
        createWorkspace("test_b1", "http://www.test_b.org", false);
        createWorkspace("test_b2", "http://www.test_b.org", true);
        // change second workspace name and try to make non isolated, this should fail
        updateWorkspace("test_b2", "test_b3", "http://www.test_b.org", false);
        GeoServerWicketTestSupport.tester.assertRenderedPage(WorkspaceEditPage.class);
        GeoServerWicketTestSupport.tester.assertErrorMessages(new String[]{ "Namespace with URI 'http://www.test_b.org' already exists." });
        // check that the catalog contains the expected objects
        Catalog catalog = getCatalog();
        // validate the first workspace
        Assert.assertThat(catalog.getWorkspaceByName("test_b1"), CoreMatchers.notNullValue());
        Assert.assertThat(catalog.getWorkspaceByName("test_b1").isIsolated(), CoreMatchers.is(false));
        Assert.assertThat(catalog.getNamespaceByPrefix("test_b1"), CoreMatchers.notNullValue());
        Assert.assertThat(catalog.getNamespaceByPrefix("test_b1").getURI(), CoreMatchers.is("http://www.test_b.org"));
        Assert.assertThat(catalog.getNamespaceByPrefix("test_b1").isIsolated(), CoreMatchers.is(false));
        // validate the second workspace
        Assert.assertThat(catalog.getWorkspaceByName("test_b2"), CoreMatchers.notNullValue());
        Assert.assertThat(catalog.getWorkspaceByName("test_b2").isIsolated(), CoreMatchers.is(true));
        Assert.assertThat(catalog.getNamespaceByPrefix("test_b2"), CoreMatchers.notNullValue());
        Assert.assertThat(catalog.getNamespaceByPrefix("test_b2").getURI(), CoreMatchers.is("http://www.test_b.org"));
        Assert.assertThat(catalog.getNamespaceByPrefix("test_b2").isIsolated(), CoreMatchers.is(true));
        // validate the global namespace, i.e. non isolated namespace
        Assert.assertThat(catalog.getNamespaceByURI("http://www.test_b.org").getPrefix(), CoreMatchers.is("test_b1"));
        // assert that no workspace with the updated name exists
        Assert.assertThat(catalog.getWorkspaceByName("test_b3"), CoreMatchers.nullValue());
    }
}

