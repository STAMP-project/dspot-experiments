/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.web.data;


import AccessMode.WRITE;
import GeoServerRole.ANY_ROLE;
import MockData.CITE_PREFIX;
import MockData.LAKES;
import org.apache.wicket.extensions.markup.html.form.palette.component.Recorder;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.data.test.MockData;
import org.geoserver.security.impl.DataAccessRule;
import org.geoserver.security.web.AbstractSecurityWicketTestSupport;
import org.geoserver.security.web.role.NewRolePage;
import org.geoserver.test.RunTestSetup;
import org.junit.Assert;
import org.junit.Test;


public class EditDataAccessRulePageTest extends AbstractSecurityWicketTestSupport {
    EditDataAccessRulePage page;

    String ruleName = ((((MockData.CITE_PREFIX) + ".") + (LAKES.getLocalPart())) + ".") + (WRITE.getAlias());

    @Test
    public void testFill() throws Exception {
        tester.startPage((page = new EditDataAccessRulePage(getRule(ruleName))));
        tester.assertRenderedPage(EditDataAccessRulePage.class);
        tester.assertModelValue("form:root", CITE_PREFIX);
        tester.assertModelValue("form:layerContainer:layerAndLabel:layer", LAKES.getLocalPart());
        tester.assertModelValue("form:accessMode", WRITE);
        // Does not work with Palette
        // tester.assertModelValue("form:roles:roles:recorder", { ROLE_WMS,ROLE_WFS });
        tester.assertModelValue("form:roles:anyRole", Boolean.FALSE);
        tester.assertComponent("form:roles:palette:recorder", Recorder.class);
        FormTester form = tester.newFormTester("form");
        form.setValue("roles:anyRole", true);
        // open new role dialog again to ensure that the current state is not lost
        form.submit("roles:addRole");
        tester.assertRenderedPage(NewRolePage.class);
        tester.clickLink("form:cancel");
        tester.assertRenderedPage(EditDataAccessRulePage.class);
        form = tester.newFormTester("form");
        form.setValue("roles:anyRole", true);
        form.submit("save");
        tester.assertErrorMessages(new String[0]);
        tester.assertRenderedPage(DataSecurityPage.class);
        DataAccessRule rule = getRule(ruleName);
        Assert.assertNotNull(rule);
        Assert.assertEquals(1, rule.getRoles().size());
        Assert.assertEquals(ANY_ROLE, rule.getRoles().iterator().next());
    }

    @Test
    @RunTestSetup
    public void testEmptyRoles() throws Exception {
        // initializeForXML();
        initializeServiceRules();
        tester.startPage((page = new EditDataAccessRulePage(getRule(ruleName))));
        FormTester form = tester.newFormTester("form");
        form.setValue("roles:palette:recorder", "");
        form.submit("save");
        tester.assertRenderedPage(EditDataAccessRulePage.class);
        // print(tester.getLastRenderedPage(),true,true);
        Assert.assertTrue(testErrorMessagesWithRegExp(".*no role.*"));
        tester.assertRenderedPage(EditDataAccessRulePage.class);
    }

    @Test
    public void testReadOnlyRoleService() throws Exception {
        // initializeForXML();
        activateRORoleService();
        tester.startPage((page = new EditDataAccessRulePage(getRule(ruleName))));
        tester.assertInvisible("form:roles:addRole");
    }
}

