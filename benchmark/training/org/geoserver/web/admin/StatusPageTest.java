/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.admin;


import HttpStatus.FOUND;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.geoserver.platform.ModuleStatus;
import org.geoserver.platform.ModuleStatusImpl;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;


public class StatusPageTest extends GeoServerWicketTestSupport {
    @Test
    public void testValues() {
        GeoServerWicketTestSupport.tester.assertRenderedPage(StatusPage.class);
        GeoServerWicketTestSupport.tester.assertLabel("tabs:panel:locks", "0");
        GeoServerWicketTestSupport.tester.assertLabel("tabs:panel:jai.memory.used", "0 KB");
    }

    @Test
    public void testFreeLocks() {
        GeoServerWicketTestSupport.tester.assertRenderedPage(StatusPage.class);
        GeoServerWicketTestSupport.tester.clickLink("tabs:panel:free.locks", false);
        GeoServerWicketTestSupport.tester.assertRenderedPage(StatusPage.class);
    }

    @Test
    public void testFreeMemory() {
        GeoServerWicketTestSupport.tester.assertRenderedPage(StatusPage.class);
        GeoServerWicketTestSupport.tester.clickLink("tabs:panel:free.memory", false);
        GeoServerWicketTestSupport.tester.assertRenderedPage(StatusPage.class);
    }

    @Test
    public void testFreeMemoryJAI() {
        GeoServerWicketTestSupport.tester.assertRenderedPage(StatusPage.class);
        GeoServerWicketTestSupport.tester.clickLink("tabs:panel:free.memory.jai", false);
        GeoServerWicketTestSupport.tester.assertRenderedPage(StatusPage.class);
    }

    @Test
    public void testClearCache() {
        GeoServerWicketTestSupport.tester.assertRenderedPage(StatusPage.class);
        GeoServerWicketTestSupport.tester.clickLink("tabs:panel:clear.resourceCache", true);
        GeoServerWicketTestSupport.tester.assertRenderedPage(StatusPage.class);
    }

    @Test
    public void testReloadConfig() {
        GeoServerWicketTestSupport.tester.assertRenderedPage(StatusPage.class);
        GeoServerWicketTestSupport.tester.clickLink("tabs:panel:reload.catalogConfig", true);
        GeoServerWicketTestSupport.tester.assertRenderedPage(StatusPage.class);
    }

    @Test
    public void testReload() throws Exception {
        // the status page was rendered as expected
        GeoServerWicketTestSupport.tester.assertRenderedPage(StatusPage.class);
        // now force a config reload
        getGeoServer().reload();
        // force the page reload
        login();
        GeoServerWicketTestSupport.tester.startPage(StatusPage.class);
        // check we did not NPE
        GeoServerWicketTestSupport.tester.assertRenderedPage(StatusPage.class);
    }

    @Test
    public void testModuleStatusPanel() {
        GeoServerWicketTestSupport.tester.assertRenderedPage(StatusPage.class);
        GeoServerWicketTestSupport.tester.clickLink("tabs:tabs-container:tabs:1:link", true);
        GeoServerWicketTestSupport.tester.assertContains("gs-main");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testModuleStatusPanelOrder() {
        GeoServerWicketTestSupport.tester.assertRenderedPage(StatusPage.class);
        GeoServerWicketTestSupport.tester.clickLink("tabs:tabs-container:tabs:1:link", true);
        GeoServerWicketTestSupport.tester.assertContains("gs-main");
        Component component = GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("tabs:panel:listViewContainer:modules");
        Assert.assertThat(component, CoreMatchers.instanceOf(ListView.class));
        List<String> modules = ((ListView<ModuleStatus>) (component)).getList().stream().map(ModuleStatus::getModule).collect(Collectors.toList());
        // verify that the expected modules are present
        Assert.assertThat(modules, CoreMatchers.hasItem("gs-main"));
        Assert.assertThat(modules, CoreMatchers.hasItem("gs-web-core"));
        Assert.assertThat(modules, CoreMatchers.hasItem("jvm"));
        // verify that the system modules are filtered
        Assert.assertThat(modules, CoreMatchers.not(CoreMatchers.hasItem(CoreMatchers.startsWith("system-"))));
        // verify that the modules are sorted
        List<String> sorted = modules.stream().sorted().collect(Collectors.toList());
        Assert.assertEquals(sorted, modules);
    }

    @Test
    public void testModuleStatusPanelVersion() {
        // Skip this test if we are excecuting from an IDE; the version is extracted from the
        // compiled jar
        Assume.assumeFalse(ModuleStatusImpl.class.getResource("ModuleStatusImpl.class").getProtocol().equals("file"));
        GeoServerWicketTestSupport.tester.assertRenderedPage(StatusPage.class);
        GeoServerWicketTestSupport.tester.clickLink("tabs:tabs-container:tabs:1:link", true);
        GeoServerWicketTestSupport.tester.assertContains("gs-main");
        Component component = GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("tabs:panel:listViewContainer:modules:0:version");
        Assert.assertTrue((component instanceof Label));
        Assert.assertNotNull(component.getDefaultModelObjectAsString());
        Assert.assertNotEquals("", component.getDefaultModelObjectAsString().trim());
    }

    @Test
    public void testModuleStatusPopup() {
        GeoServerWicketTestSupport.tester.assertRenderedPage(StatusPage.class);
        GeoServerWicketTestSupport.tester.clickLink("tabs:tabs-container:tabs:1:link", true);
        GeoServerWicketTestSupport.tester.clickLink("tabs:panel:listViewContainer:modules:0:msg", true);
        GeoServerWicketTestSupport.tester.assertRenderedPage(StatusPage.class);
        GeoServerWicketTestSupport.tester.assertContains("GeoServer Main");
        GeoServerWicketTestSupport.tester.assertContains("gs-main");
        GeoServerWicketTestSupport.tester.assertContains("Message:");
    }

    @Test
    public void testExtraTabExists() {
        // render the page, GeoServer status tab is show
        GeoServerWicketTestSupport.tester.assertRenderedPage(StatusPage.class);
        // click on the extra tab link
        GeoServerWicketTestSupport.tester.clickLink("tabs:tabs-container:tabs:2:link", true);
        // render extra tab content
        GeoServerWicketTestSupport.tester.assertRenderedPage(StatusPage.class);
        // check that extra tab content was rendered
        GeoServerWicketTestSupport.tester.assertContains("extra tab content");
        // check that the tab has the correct title
        Component component = GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("tabs:tabs-container:tabs:2:link:title");
        Assert.assertThat(component, CoreMatchers.instanceOf(Label.class));
        Label label = ((Label) (component));
        Assert.assertThat(label.getDefaultModel(), CoreMatchers.notNullValue());
        Assert.assertThat(label.getDefaultModel().getObject(), CoreMatchers.is("extra"));
    }

    /**
     * Extra tab definition that will be added to GeoServer status page.
     */
    public static final class ExtraTabDefinition implements StatusPage.TabDefinition {
        @Override
        public String getTitleKey() {
            return "StatusPageTest.extra";
        }

        @Override
        public Panel createPanel(String panelId, Page containerPage) {
            return new ExtraTabPanel(panelId);
        }
    }

    @Test
    public void redirectUnauthorizedToLogin() throws Exception {
        logout();
        MockHttpServletResponse response = getAsServletResponse(("web/wicket/bookmarkable/org.geoserver.web.admin" + ".StatusPage?29-1.ILinkListener-tabs-tabs~container-tabs-1-link"));
        Assert.assertEquals(FOUND.value(), response.getStatus());
        Assert.assertEquals("./org.geoserver.web.GeoServerLoginPage", response.getHeader("Location"));
    }
}

