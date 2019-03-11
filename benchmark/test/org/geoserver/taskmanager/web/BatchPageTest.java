package org.geoserver.taskmanager.web;


import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.taskmanager.AbstractWicketTaskManagerTest;
import org.geoserver.taskmanager.data.Batch;
import org.geoserver.taskmanager.data.TaskManagerDao;
import org.geoserver.taskmanager.data.TaskManagerFactory;
import org.geoserver.taskmanager.util.TaskManagerDataUtil;
import org.geoserver.taskmanager.util.TaskManagerTaskUtil;
import org.geoserver.taskmanager.web.model.BatchElementsModel;
import org.geoserver.taskmanager.web.panel.DropDownPanel;
import org.geoserver.web.wicket.GeoServerTablePanel;
import org.junit.Assert;
import org.junit.Test;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;


public class BatchPageTest extends AbstractWicketTaskManagerTest {
    private TaskManagerFactory fac;

    private TaskManagerDao dao;

    private Batch batch;

    private TaskManagerDataUtil util;

    private TaskManagerTaskUtil tutil;

    private Scheduler scheduler;

    @Test
    public void testFrequency() {
        IModel<Batch> batchModel = new org.apache.wicket.model.Model<Batch>(batch);
        BatchPage page = new BatchPage(batchModel, null);
        tester.startPage(page);
        tester.assertRenderedPage(BatchPage.class);
        tester.assertModelValue("batchForm:name", "this is the test batch");
        tester.assertModelValue("batchForm:description", "this is the test description");
        tester.assertModelValue("batchForm:enabled", true);
        tester.assertModelValue("batchForm:configuration", "my_configuration");
        FormTester formTester = tester.newFormTester("batchForm");
        tester.assertInvisible("batchForm:frequency:time");
        tester.assertInvisible("batchForm:frequency:dayOfWeek");
        tester.assertInvisible("batchForm:frequency:dayOfMonth");
        tester.assertInvisible("batchForm:frequency:custom");
        // daily
        formTester.select("frequency:type", 1);
        tester.executeAjaxEvent("batchForm:frequency:type", "change");
        tester.assertVisible("batchForm:frequency:time");
        tester.assertInvisible("batchForm:frequency:dayOfWeek");
        tester.assertInvisible("batchForm:frequency:dayOfMonth");
        tester.assertInvisible("batchForm:frequency:custom");
        formTester.setValue("frequency:time", "12:34");
        formTester.submit("apply");
        Assert.assertEquals("0 34 12 * * ?", batchModel.getObject().getFrequency());
        // per week
        formTester.select("frequency:type", 2);
        tester.executeAjaxEvent("batchForm:frequency:type", "change");
        tester.assertVisible("batchForm:frequency:time");
        tester.assertVisible("batchForm:frequency:dayOfWeek");
        tester.assertInvisible("batchForm:frequency:dayOfMonth");
        tester.assertInvisible("batchForm:frequency:custom");
        formTester.select("frequency:dayOfWeek", 2);
        formTester.submit("apply");
        Assert.assertEquals("0 34 12 ? * Wed", batchModel.getObject().getFrequency());
        // per month
        formTester.select("frequency:type", 3);
        tester.executeAjaxEvent("batchForm:frequency:type", "change");
        tester.assertVisible("batchForm:frequency:time");
        tester.assertInvisible("batchForm:frequency:dayOfWeek");
        tester.assertVisible("batchForm:frequency:dayOfMonth");
        tester.assertInvisible("batchForm:frequency:custom");
        formTester.select("frequency:dayOfMonth", 5);
        formTester.submit("apply");
        Assert.assertEquals("0 34 12 6 * ?", batchModel.getObject().getFrequency());
        // custom
        formTester.select("frequency:type", 4);
        tester.executeAjaxEvent("batchForm:frequency:type", "change");
        tester.assertInvisible("batchForm:frequency:time");
        tester.assertInvisible("batchForm:frequency:dayOfWeek");
        tester.assertInvisible("batchForm:frequency:dayOfMonth");
        tester.assertVisible("batchForm:frequency:custom");
        formTester.setValue("frequency:custom", "0 0 * * * ?");
        formTester.submit("apply");
        Assert.assertEquals("0 0 * * * ?", batchModel.getObject().getFrequency());
    }

    @Test
    public void testTasksAndSchedule() throws SchedulerException {
        IModel<Batch> batchModel = new org.apache.wicket.model.Model<Batch>(batch);
        BatchPage page = new BatchPage(batchModel, null);
        tester.startPage(page);
        tester.assertRenderedPage(BatchPage.class);
        tester.clickLink("batchForm:addNew");
        tester.assertComponent("dialog:dialog:content:form:userPanel", DropDownPanel.class);
        Assert.assertEquals(3, ((DropDownChoice<?>) (tester.getComponentFromLastRenderedPage("dialog:dialog:content:form:userPanel:dropdown"))).getChoices().size());
        FormTester formTester = tester.newFormTester("dialog:dialog:content:form");
        formTester.select("userPanel:dropdown", 0);
        formTester.submit("submit");
        BatchElementsModel provider = ((BatchElementsModel) (((GeoServerTablePanel<?>) (tester.getComponentFromLastRenderedPage("batchForm:tasksPanel"))).getDataProvider()));
        Assert.assertEquals(1, provider.size());
        Assert.assertEquals("task1", provider.getItems().get(0).getTask().getName());
        tester.clickLink("batchForm:addNew");
        tester.assertComponent("dialog:dialog:content:form:userPanel", DropDownPanel.class);
        formTester = tester.newFormTester("dialog:dialog:content:form");
        Assert.assertEquals(2, ((DropDownChoice<?>) (tester.getComponentFromLastRenderedPage("dialog:dialog:content:form:userPanel:dropdown"))).getChoices().size());
        formTester.select("userPanel:dropdown", 1);
        formTester.submit("submit");
        Assert.assertEquals(2, provider.size());
        Assert.assertEquals("task3", provider.getItems().get(1).getTask().getName());
        tester.clickLink("batchForm:addNew");
        tester.assertComponent("dialog:dialog:content:form:userPanel", DropDownPanel.class);
        formTester = tester.newFormTester("dialog:dialog:content:form");
        Assert.assertEquals(1, ((DropDownChoice<?>) (tester.getComponentFromLastRenderedPage("dialog:dialog:content:form:userPanel:dropdown"))).getChoices().size());
        formTester.select("userPanel:dropdown", 0);
        formTester.submit("submit");
        Assert.assertEquals(3, provider.size());
        Assert.assertEquals("task2", provider.getItems().get(2).getTask().getName());
        // use arrows to change order
        // problem: doesn't use wicket visibility, but style tag
        // and I'm not sure if we can actually test that here
        // tester.assertVisible("batchForm:tasksPanel:listContainer:items:1:itemProperties:0:component:down");
        // tester.assertInvisible("batchForm:tasksPanel:listContainer:items:1:itemProperties:0:component:up");
        // tester.assertVisible("batchForm:tasksPanel:listContainer:items:2:itemProperties:0:component:down");
        // tester.assertVisible("batchForm:tasksPanel:listContainer:items:2:itemProperties:0:component:up");
        // tester.assertInvisible("batchForm:tasksPanel:listContainer:items:3:itemProperties:0:component:down");
        // tester.assertVisible("batchForm:tasksPanel:listContainer:items:3:itemProperties:0:component:up");
        tester.clickLink("batchForm:tasksPanel:listContainer:items:2:itemProperties:0:component:up:link");
        tester.clickLink("batchForm:tasksPanel:listContainer:items:5:itemProperties:0:component:down:link");
        Assert.assertEquals("task3", provider.getItems().get(0).getTask().getName());
        Assert.assertEquals("task2", provider.getItems().get(1).getTask().getName());
        Assert.assertEquals("task1", provider.getItems().get(2).getTask().getName());
        // select and delete
        CheckBox selector = ((CheckBox) (tester.getComponentFromLastRenderedPage("batchForm:tasksPanel:listContainer:items:7:selectItemContainer:selectItem")));
        tester.getRequest().setParameter(selector.getInputName(), "true");
        tester.executeAjaxEvent(selector, "click");
        selector = ((CheckBox) (tester.getComponentFromLastRenderedPage("batchForm:tasksPanel:listContainer:items:9:selectItemContainer:selectItem")));
        tester.getRequest().setParameter(selector.getInputName(), "true");
        tester.executeAjaxEvent(selector, "click");
        tester.clickLink("batchForm:removeSelected");
        tester.executeAjaxEvent("dialog:dialog:content:form:submit", "click");
        Assert.assertEquals(1, provider.size());
        Assert.assertEquals("task2", provider.getItems().get(0).getTask().getName());
        formTester = tester.newFormTester("batchForm");
        formTester.select("frequency:type", 1);
        formTester.setValue("frequency:time", "00:00");
        formTester.submit("save");
        // new batch has been scheduled
        Assert.assertNotNull(scheduler.getJobDetail(JobKey.jobKey(batch.getId().toString())));
    }
}

