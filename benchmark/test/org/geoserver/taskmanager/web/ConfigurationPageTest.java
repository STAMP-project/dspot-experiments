/**
 * (c) 2017-2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.web;


import MetadataSyncTaskTypeImpl.NAME;
import java.io.IOException;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.taskmanager.AbstractWicketTaskManagerTest;
import org.geoserver.taskmanager.data.Attribute;
import org.geoserver.taskmanager.data.Batch;
import org.geoserver.taskmanager.data.Configuration;
import org.geoserver.taskmanager.data.Task;
import org.geoserver.taskmanager.data.impl.ConfigurationImpl;
import org.geoserver.taskmanager.util.TaskManagerDataUtil;
import org.geoserver.taskmanager.util.TaskManagerTaskUtil;
import org.geoserver.taskmanager.web.panel.ButtonPanel;
import org.geoserver.taskmanager.web.panel.FileUploadPanel;
import org.geoserver.taskmanager.web.panel.NamePanel;
import org.geoserver.taskmanager.web.panel.NewTaskPanel;
import org.geoserver.taskmanager.web.panel.PanelListPanel;
import org.geoserver.taskmanager.web.panel.TaskParameterPanel;
import org.geoserver.web.data.resource.ResourceConfigurationPage;
import org.geoserver.web.wicket.GeoServerTablePanel;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;


public class ConfigurationPageTest extends AbstractBatchesPanelTest<ConfigurationPage> {
    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    private TaskManagerDataUtil util;

    private TaskManagerTaskUtil tutil;

    private IModel<Configuration> configModel;

    private Scheduler scheduler;

    @SuppressWarnings("unchecked")
    @Test
    public void testTasksAndAttributes() {
        ConfigurationPage page = new ConfigurationPage(configModel);
        tester.startPage(page);
        tester.assertRenderedPage(ConfigurationPage.class);
        // plain fields
        tester.assertModelValue("configurationForm:name", "my_configuration");
        tester.assertModelValue("configurationForm:description", "my very new configuration");
        // tasks table
        GeoServerTablePanel<Task> tasksPanel = ((GeoServerTablePanel<Task>) (tester.getComponentFromLastRenderedPage("configurationForm:tasksPanel")));
        Assert.assertEquals(2, tasksPanel.getDataProvider().size());
        // attributes table
        GeoServerTablePanel<Attribute> attributesPanel = ((GeoServerTablePanel<Attribute>) (tester.getComponentFromLastRenderedPage("configurationForm:attributesPanel")));
        Assert.assertEquals(8, attributesPanel.getDataProvider().size());
        // add task
        tester.clickLink("configurationForm:addNew");
        tester.assertComponent("dialog:dialog:content:form:userPanel", NewTaskPanel.class);
        FormTester formTester = tester.newFormTester("dialog:dialog:content:form");
        formTester.setValue("userPanel:name", "task3");
        formTester.submit("submit");
        assertFeedback("dialog:dialog:content:form:userPanel:feedback", "required");
        formTester.select("userPanel:type", 9);
        formTester.submit("submit");
        Assert.assertEquals(3, tasksPanel.getDataProvider().size());
        Assert.assertEquals(3, configModel.getObject().getTasks().size());
        Assert.assertEquals(NAME, configModel.getObject().getTasks().get("task3").getType());
        Assert.assertEquals(10, attributesPanel.getDataProvider().size());
        // edit task parameters
        tester.clickLink("configurationForm:tasksPanel:listContainer:items:1:itemProperties:2:component:link");
        tester.assertComponent("dialog:dialog:content:form:userPanel", TaskParameterPanel.class);
        tester.assertModelValue("dialog:dialog:content:form:userPanel:parametersPanel:listContainer:items:4:itemProperties:1:component", "${target-table-name}");
        formTester = tester.newFormTester("dialog:dialog:content:form");
        formTester.setValue("userPanel:parametersPanel:listContainer:items:4:itemProperties:1:component:textfield", "${table-name}");
        formTester.submit("submit");
        // attributes are updated
        Assert.assertEquals(9, attributesPanel.getDataProvider().size());
        // edit task name
        tester.clickLink("configurationForm:tasksPanel:listContainer:items:1:itemProperties:0:component:link");
        tester.assertComponent("dialog:dialog:content:form:userPanel", NamePanel.class);
        formTester = tester.newFormTester("dialog:dialog:content:form");
        formTester.setValue("userPanel:textfield", "");
        formTester.submit("submit");
        assertFeedback("dialog:dialog:content:form:userPanel:feedback", "required");
        formTester.setValue("userPanel:textfield", "new_name_for_task");
        formTester.submit("submit");
        Assert.assertNotNull(configModel.getObject().getTasks().get("new_name_for_task").getName());
        Assert.assertEquals("new_name_for_task", configModel.getObject().getTasks().get("new_name_for_task").getName());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCopyTask() {
        ConfigurationPage page = new ConfigurationPage(configModel);
        tester.startPage(page);
        tester.assertRenderedPage(ConfigurationPage.class);
        // tasks table
        GeoServerTablePanel<Task> tasksPanel = ((GeoServerTablePanel<Task>) (tester.getComponentFromLastRenderedPage("configurationForm:tasksPanel")));
        Assert.assertEquals(2, tasksPanel.getDataProvider().size());
        // copy task
        tester.clickLink("configurationForm:addNew");
        tester.assertComponent("dialog:dialog:content:form:userPanel", NewTaskPanel.class);
        FormTester formTester = tester.newFormTester("dialog:dialog:content:form");
        formTester.select("userPanel:copy", 0);
        tester.executeAjaxEvent("dialog:dialog:content:form:userPanel:copy", "change");
        tester.assertModelValue("dialog:dialog:content:form:userPanel:type", CopyTableTaskTypeImpl.NAME);
        formTester.setValue("userPanel:name", "task3");
        formTester.submit("submit");
        Assert.assertEquals(3, tasksPanel.getDataProvider().size());
        Assert.assertEquals(3, configModel.getObject().getTasks().size());
        Assert.assertEquals(CopyTableTaskTypeImpl.NAME, configModel.getObject().getTasks().get("task3").getType());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testDeleteTasksAndSaveApplyCancel() throws SchedulerException {
        Batch dummyBatch = dummyBatch1();
        dummyBatch.setEnabled(true);
        dummyBatch = dao.save(dummyBatch);
        ConfigurationPage page = new ConfigurationPage(configModel);
        tester.startPage(page);
        tester.assertRenderedPage(ConfigurationPage.class);
        // save with tasks results in validation errors
        tester.clickLink("configurationForm:save");
        tester.assertRenderedPage(ConfigurationPage.class);
        assertFeedback("topFeedback", 7);
        assertFeedback("bottomFeedback", 7);
        // cancel
        FormTester formTester = tester.newFormTester("configurationForm");
        formTester.setValue("tasksPanel:listContainer:items:1:selectItemContainer:selectItem", true);
        tester.executeAjaxEvent("configurationForm:tasksPanel:listContainer:items:1:selectItemContainer:selectItem", "click");
        formTester.setValue("tasksPanel:listContainer:items:2:selectItemContainer:selectItem", true);
        tester.executeAjaxEvent("configurationForm:tasksPanel:listContainer:items:2:selectItemContainer:selectItem", "click");
        formTester.submit("removeSelected");
        tester.newFormTester("dialog:dialog:content:form").submit("submit");
        GeoServerTablePanel<Task> tasksPanel = ((GeoServerTablePanel<Task>) (tester.getComponentFromLastRenderedPage("configurationForm:tasksPanel")));
        Assert.assertEquals(0, tasksPanel.getDataProvider().size());
        formTester.setValue("name", "the_greatest_configuration");
        tester.clickLink("configurationForm:cancel");
        tester.assertRenderedPage(ConfigurationsPage.class);
        Assert.assertEquals(2, configModel.getObject().getTasks().size());
        Assert.assertEquals("my_configuration", configModel.getObject().getName());
        // apply
        page = new ConfigurationPage(configModel);
        tester.startPage(page);
        tester.assertRenderedPage(ConfigurationPage.class);
        formTester = tester.newFormTester("configurationForm");
        formTester.setValue("tasksPanel:listContainer:items:1:selectItemContainer:selectItem", true);
        tester.executeAjaxEvent("configurationForm:tasksPanel:listContainer:items:1:selectItemContainer:selectItem", "click");
        formTester.setValue("tasksPanel:listContainer:items:2:selectItemContainer:selectItem", true);
        tester.executeAjaxEvent("configurationForm:tasksPanel:listContainer:items:2:selectItemContainer:selectItem", "click");
        formTester.submit("removeSelected");
        tester.newFormTester("dialog:dialog:content:form").submit("submit");
        tasksPanel = ((GeoServerTablePanel<Task>) (tester.getComponentFromLastRenderedPage("configurationForm:tasksPanel")));
        Assert.assertEquals(0, tasksPanel.getDataProvider().size());
        formTester.setValue("name", "the_greatest_configuration");
        tester.clickLink("configurationForm:apply");
        tester.assertRenderedPage(ConfigurationPage.class);
        Assert.assertEquals("the_greatest_configuration", configModel.getObject().getName());
        Assert.assertEquals(0, dao.init(configModel.getObject()).getTasks().size());
        // new batch has been scheduled
        Assert.assertNotNull(scheduler.getJobDetail(JobKey.jobKey(dummyBatch.getId().toString())));
        // save
        formTester.setValue("name", "foo_bar_configuration");
        tester.clickLink("configurationForm:save");
        tester.assertRenderedPage(ConfigurationsPage.class);
        Assert.assertEquals("foo_bar_configuration", dao.reload(configModel.getObject()).getName());
        dao.delete(dummyBatch);
    }

    @Test
    public void testTemplateNotValidated() {
        configModel.getObject().setTemplate(true);
        dao.save(configModel.getObject());
        ConfigurationPage page = new ConfigurationPage(configModel);
        tester.startPage(page);
        tester.assertRenderedPage(ConfigurationPage.class);
        // save with tasks results in validation errors, not with template
        tester.clickLink("configurationForm:save");
        tester.assertRenderedPage(TemplatesPage.class);
    }

    @Test
    public void testMissingOrDuplicateName() {
        ConfigurationPage page = new ConfigurationPage(new ConfigurationImpl());
        tester.startPage(page);
        tester.assertRenderedPage(ConfigurationPage.class);
        // save without name
        tester.clickLink("configurationForm:save");
        tester.assertRenderedPage(ConfigurationPage.class);
        assertFeedback("topFeedback", "'Name' is required");
        assertFeedback("bottomFeedback", "'Name' is required");
        FormTester formTester = tester.newFormTester("configurationForm");
        formTester.setValue("name", "my_configuration");
        tester.clickLink("configurationForm:save");
        assertFeedback("topFeedback", "unique");
        assertFeedback("bottomFeedback", "unique");
    }

    @Test
    public void testActionEditLayer() {
        Task task3 = tutil.initTask(FileRemotePublicationTaskTypeImpl.NAME, "task3");
        util.addTaskToConfiguration(configModel.getObject(), task3);
        configModel.setObject(dao.save(configModel.getObject()));
        ConfigurationPage page = new ConfigurationPage(configModel);
        tester.startPage(page);
        tester.assertRenderedPage(ConfigurationPage.class);
        tester.assertComponent("configurationForm:attributesPanel:listContainer:items:10:itemProperties:2:component", PanelListPanel.class);
        tester.assertComponent("configurationForm:attributesPanel:listContainer:items:10:itemProperties:2:component:listview:0:panel", ButtonPanel.class);
        tester.assertModelValue("configurationForm:attributesPanel:listContainer:items:10:itemProperties:2:component:listview:0:panel:button", "Edit Layer..");
        FormTester formTester = tester.newFormTester("configurationForm");
        formTester.submit("attributesPanel:listContainer:items:10:itemProperties:2:component:listview:0:panel:button");
        assertFeedback("topFeedback", "You cannot execute this action with this value.");
        formTester.select("attributesPanel:listContainer:items:10:itemProperties:1:component:dropdown", 1);
        formTester.submit("attributesPanel:listContainer:items:10:itemProperties:2:component:listview:0:panel:button");
        tester.assertNoErrorMessage();
        tester.assertRenderedPage(ResourceConfigurationPage.class);
        tester.clickLink("publishedinfo:cancel");
        tester.assertRenderedPage(ConfigurationPage.class);
    }

    @Test
    public void testActionFileUpload() throws IOException {
        Task task4 = tutil.initTask(FileLocalPublicationTaskTypeImpl.NAME, "task4");
        util.addTaskToConfiguration(configModel.getObject(), task4);
        configModel.setObject(dao.save(configModel.getObject()));
        ConfigurationPage page = new ConfigurationPage(configModel);
        tester.startPage(page);
        tester.assertRenderedPage(ConfigurationPage.class);
        tester.assertComponent("configurationForm:attributesPanel:listContainer:items:10:itemProperties:2:component", PanelListPanel.class);
        tester.assertComponent("configurationForm:attributesPanel:listContainer:items:10:itemProperties:2:component:listview:0:panel", ButtonPanel.class);
        tester.assertModelValue("configurationForm:attributesPanel:listContainer:items:9:itemProperties:0:component", "fileService");
        tester.assertModelValue("configurationForm:attributesPanel:listContainer:items:10:itemProperties:2:component:listview:0:panel:button", "Upload..");
        FormTester formTester = tester.newFormTester("configurationForm");
        formTester.submit("attributesPanel:listContainer:items:10:itemProperties:2:component:listview:0:panel:button");
        assertFeedback("topFeedback", "You cannot execute this action with this value.");
        formTester.select("attributesPanel:listContainer:items:9:itemProperties:1:component:dropdown", 1);
        formTester.submit("attributesPanel:listContainer:items:10:itemProperties:2:component:listview:0:panel:button");
        tester.assertNoErrorMessage();
        tester.assertComponent("dialog:dialog:content:form:userPanel", FileUploadPanel.class);
        FormTester dialogFormTester = tester.newFormTester("dialog:dialog:content:form");
        dialogFormTester.submit("submit");
        tester.assertErrorMessages("Field 'File folder' is required.", "Field 'File' is required.");
        dialogFormTester.select("userPanel:folderSelection", 0);
        tester.assertComponent("dialog:dialog:content:form:userPanel:fileInput", FileUploadField.class);
        tester.assertComponent("dialog:dialog:content:form:userPanel:prepare", CheckBox.class);
        tester.assertModelValue("dialog:dialog:content:form:userPanel:prepare", true);
        dialogFormTester.setFile("userPanel:fileInput", new File(tempDir.newFile().getAbsolutePath()), "");
        dialogFormTester.submit("submit");
        tester.assertNoErrorMessage();
        tester.assertRenderedPage(ConfigurationPage.class);
    }
}

