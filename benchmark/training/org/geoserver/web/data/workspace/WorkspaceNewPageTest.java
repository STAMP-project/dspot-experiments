/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.data.workspace;


import MockTestData.CITE_PREFIX;
import MockTestData.CITE_URI;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.catalog.Catalog;
import org.geoserver.data.test.MockTestData;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class WorkspaceNewPageTest extends GeoServerWicketTestSupport {
    @Test
    public void testLoad() {
        GeoServerWicketTestSupport.tester.assertRenderedPage(WorkspaceNewPage.class);
        GeoServerWicketTestSupport.tester.assertNoErrorMessage();
        GeoServerWicketTestSupport.tester.assertComponent("form:name", TextField.class);
        GeoServerWicketTestSupport.tester.assertComponent("form:uri", TextField.class);
    }

    @Test
    public void testNameRequired() {
        FormTester form = GeoServerWicketTestSupport.tester.newFormTester("form");
        form.setValue("uri", "http://www.geoserver.org");
        form.submit();
        GeoServerWicketTestSupport.tester.assertRenderedPage(WorkspaceNewPage.class);
        GeoServerWicketTestSupport.tester.assertErrorMessages(new String[]{ "Field 'Name' is required." });
    }

    @Test
    public void testURIRequired() {
        FormTester form = GeoServerWicketTestSupport.tester.newFormTester("form");
        form.setValue("name", "test");
        form.submit();
        GeoServerWicketTestSupport.tester.assertRenderedPage(WorkspaceNewPage.class);
        GeoServerWicketTestSupport.tester.assertErrorMessages(new String[]{ "Field 'uri' is required." });
    }

    @Test
    public void testValid() {
        FormTester form = GeoServerWicketTestSupport.tester.newFormTester("form");
        form.setValue("name", "abc");
        form.setValue("uri", "http://www.geoserver.org");
        form.setValue("default", "true");
        form.submit();
        GeoServerWicketTestSupport.tester.assertRenderedPage(WorkspacePage.class);
        GeoServerWicketTestSupport.tester.assertNoErrorMessage();
        Assert.assertEquals("abc", getCatalog().getDefaultWorkspace().getName());
    }

    @Test
    public void testInvalidURI() {
        FormTester form = GeoServerWicketTestSupport.tester.newFormTester("form");
        form.setValue("name", "def");
        form.setValue("uri", "not a valid uri");
        form.submit();
        GeoServerWicketTestSupport.tester.assertRenderedPage(WorkspaceNewPage.class);
        GeoServerWicketTestSupport.tester.assertErrorMessages(new String[]{ "Invalid URI syntax: not a valid uri" });
    }

    @Test
    public void testInvalidName() {
        FormTester form = GeoServerWicketTestSupport.tester.newFormTester("form");
        form.setValue("name", "default");
        form.setValue("uri", "http://www.geoserver.org");
        form.submit();
        GeoServerWicketTestSupport.tester.assertRenderedPage(WorkspaceNewPage.class);
        GeoServerWicketTestSupport.tester.assertErrorMessages(new String[]{ "Invalid workspace name: \"default\" is a reserved keyword" });
    }

    @Test
    public void testDuplicateURI() {
        FormTester form = GeoServerWicketTestSupport.tester.newFormTester("form");
        form.setValue("name", "def");
        form.setValue("uri", CITE_URI);
        form.submit();
        GeoServerWicketTestSupport.tester.assertRenderedPage(WorkspaceNewPage.class);
        GeoServerWicketTestSupport.tester.assertErrorMessages(new String[]{ ("Namespace with URI '" + (MockTestData.CITE_URI)) + "' already exists." });
        // Make sure the workspace doesn't get added if the namespace fails
        Assert.assertNull(getCatalog().getWorkspaceByName("def"));
        Assert.assertNull(getCatalog().getNamespaceByPrefix("def"));
    }

    @Test
    public void testDuplicateName() {
        FormTester form = GeoServerWicketTestSupport.tester.newFormTester("form");
        form.setValue("name", CITE_PREFIX);
        form.setValue("uri", "http://www.geoserver.org");
        form.submit();
        GeoServerWicketTestSupport.tester.assertRenderedPage(WorkspaceNewPage.class);
        GeoServerWicketTestSupport.tester.assertErrorMessages(new String[]{ ("Workspace named '" + (MockTestData.CITE_PREFIX)) + "' already exists." });
    }

    @Test
    public void addIsolatedWorkspacesWithSameNameSpace() {
        Catalog catalog = getCatalog();
        // create the first workspace
        createWorkspace("test_a", "http://www.test.org", false);
        GeoServerWicketTestSupport.tester.assertRenderedPage(WorkspacePage.class);
        GeoServerWicketTestSupport.tester.assertNoErrorMessage();
        // check that the correct objects were created in the catalog
        Assert.assertThat(catalog.getWorkspaceByName("test_a"), CoreMatchers.notNullValue());
        Assert.assertThat(catalog.getWorkspaceByName("test_a").isIsolated(), CoreMatchers.is(false));
        Assert.assertThat(catalog.getNamespaceByPrefix("test_a"), CoreMatchers.notNullValue());
        Assert.assertThat(catalog.getNamespaceByPrefix("test_a").isIsolated(), CoreMatchers.is(false));
        Assert.assertThat(catalog.getNamespaceByURI("http://www.test.org"), CoreMatchers.notNullValue());
        // try to create non isolated workspace with the same namespace
        createWorkspace("test_b", "http://www.test.org", false);
        GeoServerWicketTestSupport.tester.assertRenderedPage(WorkspaceNewPage.class);
        GeoServerWicketTestSupport.tester.assertErrorMessages(new String[]{ "Namespace with URI 'http://www.test.org' already exists." });
        // check that no objects were created in the catalog
        Assert.assertThat(catalog.getWorkspaceByName("test_b"), CoreMatchers.nullValue());
        Assert.assertThat(catalog.getNamespaceByPrefix("test_b"), CoreMatchers.nullValue());
        Assert.assertThat(catalog.getNamespaceByURI("http://www.test.org"), CoreMatchers.notNullValue());
        Assert.assertThat(catalog.getNamespaceByURI("http://www.test.org").getPrefix(), CoreMatchers.is("test_a"));
        // create isolated workspace with the same namespace
        createWorkspace("test_b", "http://www.test.org", true);
        GeoServerWicketTestSupport.tester.assertRenderedPage(WorkspacePage.class);
        GeoServerWicketTestSupport.tester.assertNoErrorMessage();
        // check that no objects were created in the catalog
        Assert.assertThat(catalog.getWorkspaceByName("test_b"), CoreMatchers.notNullValue());
        Assert.assertThat(catalog.getWorkspaceByName("test_b").isIsolated(), CoreMatchers.is(true));
        Assert.assertThat(catalog.getNamespaceByPrefix("test_b"), CoreMatchers.notNullValue());
        Assert.assertThat(catalog.getNamespaceByPrefix("test_b").isIsolated(), CoreMatchers.is(true));
        Assert.assertThat(catalog.getNamespaceByPrefix("test_b").getURI(), CoreMatchers.is("http://www.test.org"));
        Assert.assertThat(catalog.getNamespaceByURI("http://www.test.org").getPrefix(), CoreMatchers.is("test_a"));
        Assert.assertThat(catalog.getNamespaceByURI("http://www.test.org").isIsolated(), CoreMatchers.is(false));
    }
}

