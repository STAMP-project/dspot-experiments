/**
 * (c) 2017-2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.web;


import java.util.List;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.taskmanager.AbstractWicketTaskManagerTest;
import org.geoserver.taskmanager.data.Configuration;
import org.geoserver.taskmanager.data.TaskManagerDao;
import org.geoserver.taskmanager.data.TaskManagerFactory;
import org.geoserver.taskmanager.web.panel.DropDownPanel;
import org.geoserver.web.wicket.GeoServerDialog;
import org.geoserver.web.wicket.GeoServerTablePanel;
import org.junit.Assert;
import org.junit.Test;


public class ConfigurationsPageTest extends AbstractWicketTaskManagerTest {
    private TaskManagerFactory fac;

    private TaskManagerDao dao;

    private Configuration config;

    @Test
    public void testPage() {
        ConfigurationsPage page = new ConfigurationsPage();
        tester.startPage(page);
        tester.assertRenderedPage(ConfigurationsPage.class);
        tester.assertComponent("configurationsPanel", GeoServerTablePanel.class);
        tester.assertComponent("dialog", GeoServerDialog.class);
        tester.assertComponent("addNew", AjaxLink.class);
        tester.assertComponent("removeSelected", AjaxLink.class);
    }

    @Test
    public void testConfigurations() throws Exception {
        ConfigurationsPage page = new ConfigurationsPage();
        Configuration dummy1 = dao.save(dummyConfiguration1());
        List<Configuration> configurations = dao.getConfigurations(false);
        tester.startPage(page);
        @SuppressWarnings("unchecked")
        GeoServerTablePanel<Configuration> table = ((GeoServerTablePanel<Configuration>) (tester.getComponentFromLastRenderedPage("configurationsPanel")));
        Assert.assertEquals(configurations.size(), table.getDataProvider().size());
        Assert.assertTrue(containsConfig(getConfigurationsFromTable(table), dummy1));
        Configuration dummy2 = dao.save(dummyConfiguration2());
        reset();
        Assert.assertEquals(((configurations.size()) + 1), table.getDataProvider().size());
        Assert.assertTrue(containsConfig(getConfigurationsFromTable(table), dummy2));
        dao.delete(dummy1);
        dao.delete(dummy2);
    }

    @Test
    public void testNew() {
        login();
        ConfigurationsPage page = new ConfigurationsPage();
        tester.startPage(page);
        tester.clickLink("addNew");
        tester.assertComponent("dialog:dialog:content:form:userPanel", DropDownPanel.class);
        tester.executeAjaxEvent("dialog:dialog:content:form:submit", "click");
        tester.assertRenderedPage(ConfigurationPage.class);
        tester.assertModelValue("configurationForm:description", null);
        logout();
    }

    @Test
    public void testEdit() {
        Configuration dummy1 = dao.save(dummyConfiguration1());
        login();
        ConfigurationsPage page = new ConfigurationsPage();
        tester.startPage(page);
        tester.clickLink("configurationsPanel:listContainer:items:1:itemProperties:1:component:link");
        tester.assertRenderedPage(ConfigurationPage.class);
        tester.assertModelValue("configurationForm:name", dummy1.getName());
        tester.assertModelValue("configurationForm:description", dummy1.getDescription());
        dao.delete(dummy1);
        logout();
    }

    @Test
    public void testNewFromTemplate() {
        login();
        ConfigurationsPage page = new ConfigurationsPage();
        tester.startPage(page);
        tester.clickLink("addNew");
        tester.assertComponent("dialog:dialog:content:form:userPanel", DropDownPanel.class);
        FormTester formTester = tester.newFormTester("dialog:dialog:content:form");
        formTester.select("userPanel:dropdown", 0);
        tester.executeAjaxEvent("dialog:dialog:content:form:submit", "click");
        tester.assertRenderedPage(ConfigurationPage.class);
        tester.assertModelValue("configurationForm:description", "template description");
        logout();
    }

    @Test
    public void testDelete() throws Exception {
        login();
        ConfigurationsPage page = new ConfigurationsPage();
        tester.startPage(page);
        @SuppressWarnings("unchecked")
        GeoServerTablePanel<Configuration> table = ((GeoServerTablePanel<Configuration>) (tester.getComponentFromLastRenderedPage("configurationsPanel")));
        Configuration dummy1 = dao.save(dummyConfiguration1());
        Configuration dummy2 = dao.save(dummyConfiguration2());
        Assert.assertTrue(containsConfig(dao.getConfigurations(false), dummy1));
        Assert.assertTrue(containsConfig(dao.getConfigurations(false), dummy2));
        reset();
        // sort descending on name
        tester.clickLink("configurationsPanel:listContainer:sortableLinks:1:header:link");
        tester.clickLink("configurationsPanel:listContainer:sortableLinks:1:header:link");
        // select
        CheckBox selector = ((CheckBox) (tester.getComponentFromLastRenderedPage("configurationsPanel:listContainer:items:1:selectItemContainer:selectItem")));
        tester.getRequest().setParameter(selector.getInputName(), "true");
        tester.executeAjaxEvent(selector, "click");
        Assert.assertEquals(dummy1.getId(), table.getSelection().get(0).getId());
        // click delete
        ModalWindow w = ((ModalWindow) (tester.getComponentFromLastRenderedPage("dialog:dialog")));
        Assert.assertFalse(w.isShown());
        tester.clickLink("removeSelected");
        Assert.assertTrue(w.isShown());
        // confirm
        tester.executeAjaxEvent("dialog:dialog:content:form:submit", "click");
        Assert.assertFalse(containsConfig(dao.getConfigurations(false), dummy1));
        Assert.assertTrue(containsConfig(dao.getConfigurations(false), dummy2));
        reset();
        Assert.assertFalse(containsConfig(getConfigurationsFromTable(table), dummy1));
        Assert.assertTrue(containsConfig(getConfigurationsFromTable(table), dummy2));
        dao.delete(dummy2);
        logout();
    }

    @Test
    public void testCopy() throws Exception {
        login();
        ConfigurationsPage page = new ConfigurationsPage();
        Configuration dummy1 = dao.save(dummyConfiguration1());
        tester.startPage(page);
        // select
        CheckBox selector = ((CheckBox) (tester.getComponentFromLastRenderedPage("configurationsPanel:listContainer:items:1:selectItemContainer:selectItem")));
        tester.getRequest().setParameter(selector.getInputName(), "true");
        tester.executeAjaxEvent(selector, "click");
        // click copy
        tester.clickLink("copySelected");
        dao.delete(dummy1);
        tester.assertRenderedPage(ConfigurationPage.class);
        tester.assertModelValue("configurationForm:description", "z description");
        logout();
    }
}

