/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.data.store;


import FeedbackMessage.ERROR;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.List;
import org.apache.wicket.Component;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class DataAccessEditPageTest extends GeoServerWicketTestSupport {
    private DataStoreInfo store;

    @Test
    public void testLoad() {
        GeoServerWicketTestSupport.tester.assertRenderedPage(DataAccessEditPage.class);
        GeoServerWicketTestSupport.tester.assertNoErrorMessage();
        GeoServerWicketTestSupport.tester.assertLabel("dataStoreForm:storeType", "Properties");
        GeoServerWicketTestSupport.tester.assertModelValue("dataStoreForm:dataStoreNamePanel:border:border_body:paramValue", "cite");
        String expectedPath = getPath();
        GeoServerWicketTestSupport.tester.assertModelValue("dataStoreForm:parametersPanel:parameters:0:parameterPanel:border:border_body:paramValue", expectedPath);
    }

    // This is disabled due to bad interactions between the submit link and the form submit
    // I need to reproduce ina stand alone test case and report to the Wicket devs
    // public void testEditName() {
    // 
    // FormTester form = tester.newFormTester("dataStoreForm");
    // prefillForm(form);
    // form.setValue("dataStoreNamePanel:border:border_body:paramValue", "citeModified");
    // form.submit();
    // tester.assertNoErrorMessage();
    // tester.clickLink("dataStoreForm:save");
    // tester.assertNoErrorMessage();
    // 
    // tester.assertRenderedPage(StorePage.class);
    // }
    @Test
    public void testNameRequired() {
        FormTester form = GeoServerWicketTestSupport.tester.newFormTester("dataStoreForm");
        form.setValue("dataStoreNamePanel:border:border_body:paramValue", null);
        form.setValue("workspacePanel:border:border_body:paramValue", "cite");
        form.submit();
        // missing click link , the validation triggers before it
        GeoServerWicketTestSupport.tester.debugComponentTrees();
        GeoServerWicketTestSupport.tester.assertRenderedPage(DataAccessEditPage.class);
        List<String> l = Lists.transform(GeoServerWicketTestSupport.tester.getMessages(ERROR), new Function<Serializable, String>() {
            @Override
            public String apply(Serializable input) {
                return input.toString();
            }
        });
        Assert.assertTrue(l.contains("Field 'Data Source Name' is required."));
        // tester.assertErrorMessages(new String[] { "Field 'Data Source Name' is required." });
    }

    @Test
    public void testEditDettached() throws Exception {
        final Catalog catalog = getCatalog();
        DataStoreInfo ds = catalog.getFactory().createDataStore();
        new org.geoserver.catalog.CatalogBuilder(catalog).updateDataStore(ds, store);
        Assert.assertNull(ds.getId());
        try {
            GeoServerWicketTestSupport.tester.startPage(new DataAccessEditPage(ds));
            GeoServerWicketTestSupport.tester.assertNoErrorMessage();
            FormTester form = GeoServerWicketTestSupport.tester.newFormTester("dataStoreForm");
            form.select("workspacePanel:border:border_body:paramValue", 4);
            Component wsDropDown = GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("dataStoreForm:workspacePanel:border:border_body:paramValue");
            GeoServerWicketTestSupport.tester.executeAjaxEvent(wsDropDown, "change");
            form.setValue("dataStoreNamePanel:border:border_body:paramValue", "foo");
            form.setValue("parametersPanel:parameters:0:parameterPanel:border:border_body:paramValue", "/foo");
            GeoServerWicketTestSupport.tester.clickLink("dataStoreForm:save", true);
            GeoServerWicketTestSupport.tester.assertNoErrorMessage();
            catalog.save(ds);
            Assert.assertNotNull(ds.getId());
            Assert.assertEquals("foo", ds.getName());
        } finally {
            catalog.remove(ds);
        }
    }

    @Test
    public void testDataStoreEdit() throws Exception {
        final Catalog catalog = getCatalog();
        DataStoreInfo ds = catalog.getFactory().createDataStore();
        new org.geoserver.catalog.CatalogBuilder(catalog).updateDataStore(ds, store);
        Assert.assertNull(ds.getId());
        try {
            GeoServerWicketTestSupport.tester.startPage(new DataAccessEditPage(ds));
            GeoServerWicketTestSupport.tester.assertNoErrorMessage();
            FormTester form = GeoServerWicketTestSupport.tester.newFormTester("dataStoreForm");
            form.select("workspacePanel:border:border_body:paramValue", 4);
            Component wsDropDown = GeoServerWicketTestSupport.tester.getComponentFromLastRenderedPage("dataStoreForm:workspacePanel:border:border_body:paramValue");
            GeoServerWicketTestSupport.tester.executeAjaxEvent(wsDropDown, "change");
            form.setValue("dataStoreNamePanel:border:border_body:paramValue", "foo");
            form.setValue("parametersPanel:parameters:0:parameterPanel:border:border_body:paramValue", "/foo");
            GeoServerWicketTestSupport.tester.clickLink("dataStoreForm:save", true);
            GeoServerWicketTestSupport.tester.assertNoErrorMessage();
            catalog.save(ds);
            Assert.assertNotNull(ds.getId());
            DataStoreInfo expandedStore = catalog.getResourcePool().clone(ds, true);
            Assert.assertNotNull(expandedStore.getId());
            Assert.assertNotNull(expandedStore.getCatalog());
            catalog.validate(expandedStore, false).throwIfInvalid();
        } finally {
            catalog.remove(ds);
        }
    }
}

