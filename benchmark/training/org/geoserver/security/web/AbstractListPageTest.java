/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.web;


import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractListPageTest<T> extends AbstractSecurityWicketTestSupport {
    public static final String ITEMS_PATH = "table:listContainer:items";

    public static final String FIRST_COLUM_PATH = "itemProperties:0:component:link";

    @Test
    public void testRenders() throws Exception {
        initializeForXML();
        tester.startPage(listPage(null));
        tester.assertRenderedPage(listPage(null).getClass());
    }

    @Test
    public void testEdit() throws Exception {
        // the name link for the first user
        initializeForXML();
        // insertValues();
        tester.startPage(listPage(null));
        String search = getSearchString();
        Assert.assertNotNull(search);
        Component c = getFromList(AbstractListPageTest.FIRST_COLUM_PATH, search, getEditProperty());
        Assert.assertNotNull(c);
        tester.clickLink(c.getPageRelativePath());
        tester.assertRenderedPage(editPage().getClass());
        Assert.assertTrue(checkEditForm(search));
    }

    @Test
    public void testNew() throws Exception {
        initializeForXML();
        tester.startPage(listPage(null));
        tester.clickLink("headerPanel:addNew");
        Page newPage = tester.getLastRenderedPage();
        tester.assertRenderedPage(newPage.getClass());
    }

    @Test
    public void testRemove() throws Exception {
        initializeForXML();
        insertValues();
        addAdditonalData();
        doRemove("headerPanel:removeSelected");
    }
}

