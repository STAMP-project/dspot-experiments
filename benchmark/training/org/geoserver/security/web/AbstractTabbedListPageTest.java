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


public abstract class AbstractTabbedListPageTest<T> extends AbstractSecurityWicketTestSupport {
    public static final String FIRST_COLUM_PATH = "itemProperties:0:component:link";

    @Test
    public void testRenders() throws Exception {
        tester.assertRenderedPage(listPage(getServiceName()).getClass());
    }

    @Test
    public void testEdit() throws Exception {
        // the name link for the first user
        AbstractSecurityPage listPage = ((AbstractSecurityPage) (listPage(getServiceName())));
        // tester.startPage(listPage);
        String search = getSearchString();
        Assert.assertNotNull(search);
        Component c = getFromList(AbstractTabbedListPageTest.FIRST_COLUM_PATH, search, getEditProperty());
        Assert.assertNotNull(c);
        tester.clickLink(c.getPageRelativePath());
        tester.assertRenderedPage(editPage(listPage).getClass());
        Assert.assertTrue(checkEditForm(search));
    }

    @Test
    public void testNew() throws Exception {
        listPage(getServiceName());
        tester.clickLink(((getTabbedPanelPath()) + ":panel:header:addNew"));
        Page newPage = tester.getLastRenderedPage();
        tester.assertRenderedPage(newPage.getClass());
    }

    @Test
    public void testRemove() throws Exception {
        addAdditonalData();
        doRemove(((getTabbedPanelPath()) + ":panel:header:removeSelected"));
    }
}

