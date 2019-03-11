/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.web.data;


import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.security.impl.DataAccessRule;
import org.geoserver.security.web.AbstractListPageTest;
import org.junit.Assert;
import org.junit.Test;


public class DataSecurityPageTest extends AbstractListPageTest<DataAccessRule> {
    @Test
    public void testDefaultCatalogMode() throws Exception {
        tester.startPage(DataSecurityPage.class);
        tester.assertRenderedPage(DataSecurityPage.class);
        Assert.assertEquals("HIDE", tester.getComponentFromLastRenderedPage("catalogModeForm:catalogMode").getDefaultModelObject().toString());
    }

    @Test
    public void testEditCatalogMode() throws Exception {
        tester.startPage(DataSecurityPage.class);
        tester.assertRenderedPage(DataSecurityPage.class);
        // simple test
        Assert.assertFalse("CHALLENGE".equals(tester.getComponentFromLastRenderedPage("catalogModeForm:catalogMode").getDefaultModelObject()));
        // edit catalogMode value
        final FormTester form = tester.newFormTester("catalogModeForm");
        form.select("catalogMode", 1);
        form.getForm().visitChildren(RadioChoice.class, ( component, visit) -> {
            if (component.getId().equals("catalogMode")) {
                ((RadioChoice) (component)).onSelectionChanged();
            }
        });
        Assert.assertEquals("MIXED", tester.getComponentFromLastRenderedPage("catalogModeForm:catalogMode").getDefaultModelObject().toString());
    }
}

