/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.web.data;


import AccessMode.ADMIN;
import AccessMode.READ;
import DataAccessRule.ANY;
import MockData.BRIDGES;
import MockData.CITE_PREFIX;
import MockData.STREAMS;
import java.util.List;
import org.apache.wicket.extensions.markup.html.form.palette.component.Recorder;
import org.apache.wicket.util.tester.FormTester;
import org.geoserver.data.test.MockData;
import org.geoserver.security.AccessMode;
import org.geoserver.security.impl.DataAccessRule;
import org.geoserver.security.impl.DataAccessRuleDAO;
import org.geoserver.security.web.AbstractSecurityWicketTestSupport;
import org.geoserver.security.web.role.NewRolePage;
import org.junit.Assert;
import org.junit.Test;


public class NewDataAccessRulePageTest extends AbstractSecurityWicketTestSupport {
    NewDataAccessRulePage page;

    @Test
    public void testFillAndSwitchToNewRolePage() throws Exception {
        testFill(true);
    }

    @Test
    public void testFill() throws Exception {
        testFill(false);
    }

    @Test
    public void testDuplicateRule() throws Exception {
        initializeServiceRules();
        addRule();
        tester.assertNoErrorMessage();
        addRule();
        Assert.assertTrue(testErrorMessagesWithRegExp(((((".*" + (MockData.CITE_PREFIX)) + "\\.") + (BRIDGES.getLocalPart())) + ".*")));
        tester.assertRenderedPage(NewDataAccessRulePage.class);
    }

    @Test
    public void testEmptyRoles() throws Exception {
        initializeServiceRules();
        tester.startPage((page = new NewDataAccessRulePage()));
        FormTester form = tester.newFormTester("form");
        int index = indexOf(page.rootChoice.getChoices(), CITE_PREFIX);
        form.select("root", index);
        tester.executeAjaxEvent("form:root", "change");
        form = tester.newFormTester("form");
        index = indexOf(page.layerChoice.getChoices(), STREAMS.getLocalPart());
        form.select("layerContainer:layerAndLabel:layer", index);
        index = page.accessModeChoice.getChoices().indexOf(READ);
        form.select("accessMode", index);
        form.submit("save");
        Assert.assertTrue(testErrorMessagesWithRegExp(".*no role.*"));
        tester.assertRenderedPage(NewDataAccessRulePage.class);
    }

    @Test
    public void testReadOnlyRoleService() throws Exception {
        activateRORoleService();
        tester.startPage((page = new NewDataAccessRulePage()));
        tester.assertInvisible("form:roles:addRole");
    }

    @Test
    public void testAddAdminRule() throws Exception {
        tester.startPage((page = new NewDataAccessRulePage()));
        tester.assertRenderedPage(NewDataAccessRulePage.class);
        FormTester form = tester.newFormTester("form");
        int index = indexOf(page.rootChoice.getChoices(), CITE_PREFIX);
        form.select("root", index);
        tester.executeAjaxEvent("form:root", "change");
        form = tester.newFormTester("form");
        index = indexOf(page.layerChoice.getChoices(), ANY);
        form.select("layerContainer:layerAndLabel:layer", index);
        index = page.accessModeChoice.getChoices().indexOf(ADMIN);
        form.select("accessMode", index);
        tester.assertComponent("form:roles:palette:recorder", Recorder.class);
        // add a role on the fly
        form.submit("roles:addRole");
        tester.assertRenderedPage(NewRolePage.class);
        form = tester.newFormTester("form");
        form.setValue("name", "ROLE_NEW");
        form.submit("save");
        tester.assertNoErrorMessage();
        // assign the new role to the method
        form = tester.newFormTester("form");
        tester.assertRenderedPage(NewDataAccessRulePage.class);
        form.setValue("roles:palette:recorder", gaService.getRoleByName("ROLE_NEW").getAuthority());
        // reopen new role dialog again to ensure that the current state is not lost
        form.submit("roles:addRole");
        tester.assertRenderedPage(NewRolePage.class);
        tester.clickLink("form:cancel");
        tester.assertRenderedPage(NewDataAccessRulePage.class);
        DataAccessRuleDAO dao = DataAccessRuleDAO.get();
        DataAccessRule rule = new DataAccessRule(MockData.CITE_PREFIX, DataAccessRule.ANY, AccessMode.ADMIN);
        Assert.assertFalse(dao.getRules().contains(rule));
        // now save
        form = tester.newFormTester("form");
        form.submit("save");
        dao.reload();
        Assert.assertTrue(dao.getRules().contains(rule));
    }

    @Test
    public void testAddGlobalLayerGroupRule() throws Exception {
        tester.startPage((page = new NewDataAccessRulePage()));
        tester.assertRenderedPage(NewDataAccessRulePage.class);
        FormTester form = tester.newFormTester("form");
        form.setValue("globalGroupRule", true);
        tester.executeAjaxEvent("form:globalGroupRule", "change");
        // need to set it again, the ajax event apparently resets the form...
        form.setValue("globalGroupRule", true);
        int index = indexOf(page.rootChoice.getChoices(), AbstractSecurityWicketTestSupport.NATURE_GROUP);
        form.select("root", index);
        // this one should have been made invisible
        tester.assertInvisible("form:layerContainer:layerAndLabel");
        // setup access mode
        index = page.accessModeChoice.getChoices().indexOf(READ);
        form.select("accessMode", index);
        // allow all roles for simplicity
        form.setValue("roles:anyRole", true);
        // tester.debugComponentTrees();
        form.submit("save");
        tester.assertNoErrorMessage();
        // check the global group rule has been setup
        DataAccessRuleDAO dao = DataAccessRuleDAO.get();
        DataAccessRule rule = new DataAccessRule(AbstractSecurityWicketTestSupport.NATURE_GROUP, null, AccessMode.READ);
        final List<DataAccessRule> rules = dao.getRules();
        Assert.assertTrue(rules.contains(rule));
    }

    @Test
    public void testWorkspaceGlobalLayerGroupRule() throws Exception {
        tester.startPage((page = new NewDataAccessRulePage()));
        tester.assertRenderedPage(NewDataAccessRulePage.class);
        FormTester form = tester.newFormTester("form");
        int index = indexOf(page.rootChoice.getChoices(), CITE_PREFIX);
        form.select("root", index);
        tester.executeAjaxEvent("form:root", "change");
        form.setValue("roles:anyRole", true);
        tester.executeAjaxEvent("form:roles:anyRole", "click");
        // start again, the ajax event voided the previous selection...
        form.select("root", index);
        // select workspace specific group
        index = indexOf(page.layerChoice.getChoices(), AbstractSecurityWicketTestSupport.CITE_NATURE_GROUP);
        Assert.assertNotEquals((-1), index);
        form.select("layerContainer:layerAndLabel:layer", index);
        // setup access mode
        index = page.accessModeChoice.getChoices().indexOf(READ);
        form.select("accessMode", index);
        // allow all roles for simplicity
        form.setValue("roles:anyRole", true);
        form.submit("save");
        tester.assertNoErrorMessage();
        // check the global group rule has been setup
        DataAccessRuleDAO dao = DataAccessRuleDAO.get();
        DataAccessRule rule = new DataAccessRule(MockData.CITE_PREFIX, AbstractSecurityWicketTestSupport.CITE_NATURE_GROUP, AccessMode.READ);
        final List<DataAccessRule> rules = dao.getRules();
        Assert.assertTrue(rules.contains(rule));
    }
}

