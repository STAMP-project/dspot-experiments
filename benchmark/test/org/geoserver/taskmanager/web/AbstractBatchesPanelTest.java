/**
 * (c) 2017-2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.web;


import java.util.Collection;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.CheckBox;
import org.geoserver.taskmanager.AbstractWicketTaskManagerTest;
import org.geoserver.taskmanager.data.Batch;
import org.geoserver.taskmanager.data.TaskManagerDao;
import org.geoserver.taskmanager.data.TaskManagerFactory;
import org.geoserver.web.wicket.GeoServerDialog;
import org.geoserver.web.wicket.GeoServerTablePanel;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractBatchesPanelTest<T extends Page> extends AbstractWicketTaskManagerTest {
    protected TaskManagerFactory fac;

    protected TaskManagerDao dao;

    @Test
    public void testPage() {
        T page = newPage();
        tester.startPage(page);
        tester.assertComponent(((prefix()) + "batchesPanel:form:batchesPanel"), GeoServerTablePanel.class);
        tester.assertComponent(((prefix()) + "batchesPanel:dialog"), GeoServerDialog.class);
        tester.assertComponent(((prefix()) + "batchesPanel:addNew"), AjaxLink.class);
        tester.assertComponent(((prefix()) + "batchesPanel:removeSelected"), AjaxLink.class);
    }

    @Test
    public void testBatches() throws Exception {
        Batch dummy1 = dao.save(dummyBatch1());
        Batch dummy2 = dao.save(dummyBatch2());
        T page = newPage();
        Collection<Batch> batches = getBatches();
        tester.startPage(page);
        @SuppressWarnings("unchecked")
        GeoServerTablePanel<Batch> table = ((GeoServerTablePanel<Batch>) (tester.getComponentFromLastRenderedPage(((prefix()) + "batchesPanel:form:batchesPanel"))));
        Assert.assertEquals(batches.size(), table.getDataProvider().size());
        Assert.assertTrue(containsBatch(getBatchesFromTable(table), dummy1));
        Assert.assertTrue(containsBatch(getBatchesFromTable(table), dummy2));
        dao.delete(dummy1);
        dao.delete(dummy2);
    }

    @Test
    public void testNew() {
        login();
        T page = newPage();
        tester.startPage(page);
        tester.clickLink(((prefix()) + "batchesPanel:addNew"));
        tester.assertRenderedPage(BatchPage.class);
        logout();
    }

    @Test
    public void testEdit() {
        login();
        Batch dummy1 = dao.save(dummyBatch1());
        T page = newPage();
        tester.startPage(page);
        tester.clickLink(((prefix()) + "batchesPanel:form:batchesPanel:listContainer:items:1:itemProperties:1:component:link"));
        tester.assertRenderedPage(BatchPage.class);
        tester.assertModelValue("batchForm:name", dummy1.getName());
        dao.delete(dummy1);
        logout();
    }

    @Test
    public void testDelete() throws Exception {
        login();
        Batch dummy1 = dao.save(dummyBatch1());
        Batch dummy2 = dao.save(dummyBatch2());
        T page = newPage();
        tester.startPage(page);
        @SuppressWarnings("unchecked")
        GeoServerTablePanel<Batch> table = ((GeoServerTablePanel<Batch>) (tester.getComponentFromLastRenderedPage(((prefix()) + "batchesPanel:form:batchesPanel"))));
        Assert.assertTrue(containsBatch(getBatches(), dummy1));
        Assert.assertTrue(containsBatch(getBatches(), dummy2));
        // sort descending on name
        tester.clickLink(((prefix()) + "batchesPanel:form:batchesPanel:listContainer:sortableLinks:1:header:link"));
        tester.clickLink(((prefix()) + "batchesPanel:form:batchesPanel:listContainer:sortableLinks:1:header:link"));
        // select
        CheckBox selector = ((CheckBox) (tester.getComponentFromLastRenderedPage(((prefix()) + "batchesPanel:form:batchesPanel:listContainer:items:1:selectItemContainer:selectItem"))));
        tester.getRequest().setParameter(selector.getInputName(), "true");
        tester.executeAjaxEvent(selector, "click");
        Assert.assertEquals(1, table.getSelection().size());
        Assert.assertEquals(dummy1.getId(), table.getSelection().get(0).getId());
        // click delete
        ModalWindow w = ((ModalWindow) (tester.getComponentFromLastRenderedPage(((prefix()) + "batchesPanel:dialog:dialog"))));
        Assert.assertFalse(w.isShown());
        tester.clickLink(((prefix()) + "batchesPanel:removeSelected"));
        Assert.assertTrue(w.isShown());
        // confirm
        tester.executeAjaxEvent(((prefix()) + "batchesPanel:dialog:dialog:content:form:submit"), "click");
        Assert.assertFalse(containsBatch(getBatches(), dummy1));
        Assert.assertTrue(containsBatch(getBatches(), dummy2));
        reset();
        Assert.assertFalse(containsBatch(getBatchesFromTable(table), dummy1));
        Assert.assertTrue(containsBatch(getBatchesFromTable(table), dummy2));
        dao.delete(dummy2);
        logout();
    }
}

