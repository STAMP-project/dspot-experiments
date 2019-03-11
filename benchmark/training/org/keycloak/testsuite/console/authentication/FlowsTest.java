/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.keycloak.testsuite.console.authentication;


import CreateExecutionForm.ProviderOption.RESET_PASSWORD;
import CreateFlowForm.FlowType.FORM;
import CreateFlowForm.FlowType.GENERIC;
import Flows.FlowOption.BROWSER;
import Flows.FlowOption.CLIENTS;
import Flows.FlowOption.DIRECT_GRANT;
import FlowsTable.Action.ADD_EXECUTION;
import FlowsTable.Action.ADD_FLOW;
import FlowsTable.Action.DELETE;
import FlowsTable.RequirementOption.ALTERNATIVE;
import FlowsTable.RequirementOption.DISABLED;
import FlowsTable.RequirementOption.OPTIONAL;
import FlowsTable.RequirementOption.REQUIRED;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsNot;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.representations.idm.AuthenticationExecutionExportRepresentation;
import org.keycloak.representations.idm.AuthenticationFlowRepresentation;
import org.keycloak.testsuite.console.AbstractConsoleTest;
import org.keycloak.testsuite.console.page.authentication.flows.CreateExecution;
import org.keycloak.testsuite.console.page.authentication.flows.CreateFlow;
import org.keycloak.testsuite.console.page.authentication.flows.Flows;


/**
 *
 *
 * @author <a href="mailto:vramik@redhat.com">Vlastislav Ramik</a>
 * @author <a href="mailto:pzaoral@redhat.com">Peter Zaoral</a>
 */
public class FlowsTest extends AbstractConsoleTest {
    @Page
    private Flows flowsPage;

    @Page
    private CreateFlow createFlowPage;

    @Page
    private CreateExecution createExecutionPage;

    @Test
    public void createDeleteFlowTest() {
        // Adding new flow
        flowsPage.clickNew();
        createFlowPage.form().setValues("testFlow", "testDesc", GENERIC);
        assertAlertSuccess();
        // Checking if test flow is created via rest
        AuthenticationFlowRepresentation testFlow = getFlowFromREST("testFlow");
        Assert.assertEquals("testFlow", testFlow.getAlias());
        // Checking if testFlow is selected in UI
        Assert.assertEquals("TestFlow", flowsPage.getFlowSelectValue());
        // Adding new execution flow within testFlow
        flowsPage.clickAddFlow();
        createFlowPage.form().setValues("testExecution", "executionDesc", GENERIC);
        assertAlertSuccess();
        // Checking if execution flow is created via rest
        testFlow = getFlowFromREST("testFlow");
        Assert.assertEquals("testExecution", testFlow.getAuthenticationExecutions().get(0).getFlowAlias());
        // Checking if testFlow is selected in UI
        Assert.assertEquals("TestFlow", flowsPage.getFlowSelectValue());
        // Deleting test flow
        flowsPage.clickDelete();
        modalDialog.confirmDeletion();
        assertAlertSuccess();
        // Checking if both test flow and execution flow is removed via UI
        Assert.assertThat(flowsPage.getFlowAllValues(), IsNot.not(CoreMatchers.hasItem("TestFlow")));
        // Checking if both test flow and execution flow is removed via rest
        Assert.assertThat(testRealmResource().flows().getFlows(), IsNot.not(CoreMatchers.hasItem(testFlow)));
    }

    @Test
    public void selectFlowOptionTest() {
        flowsPage.selectFlowOption(DIRECT_GRANT);
        Assert.assertEquals("Direct Grant", flowsPage.getFlowSelectValue());
        flowsPage.selectFlowOption(BROWSER);
        Assert.assertEquals("Browser", flowsPage.getFlowSelectValue());
        flowsPage.selectFlowOption(CLIENTS);
        Assert.assertEquals("Clients", flowsPage.getFlowSelectValue());
    }

    @Test
    public void createFlowWithEmptyAliasTest() {
        flowsPage.clickNew();
        createFlowPage.form().setValues("", "testDesc", GENERIC);
        assertAlertDanger();
        // rest:flow isn't present
    }

    @Test
    public void createNestedFlowWithEmptyAliasTest() {
        // best-effort: check empty alias in nested flow
        flowsPage.clickNew();
        createFlowPage.form().setValues("testFlow", "testDesc", GENERIC);
        flowsPage.clickAddFlow();
        createFlowPage.form().setValues("", "executionDesc", GENERIC);
        assertAlertDanger();
    }

    @Test
    public void copyFlowTest() {
        flowsPage.selectFlowOption(BROWSER);
        flowsPage.clickCopy();
        modalDialog.setName("test copy of browser");
        modalDialog.ok();
        assertAlertSuccess();
        // UI
        Assert.assertEquals("Test Copy Of Browser", flowsPage.getFlowSelectValue());
        Assert.assertTrue(flowsPage.table().getFlowsAliasesWithRequirements().containsKey("Test Copy Of Browser Forms"));
        Assert.assertEquals(6, flowsPage.table().getFlowsAliasesWithRequirements().size());
        // rest: copied flow present
        Assert.assertThat(testRealmResource().flows().getFlows().stream().map(AuthenticationFlowRepresentation::getAlias).collect(Collectors.toList()), CoreMatchers.hasItem(getFlowFromREST("test copy of browser").getAlias()));
    }

    @Test
    public void createDeleteExecutionTest() {
        // Adding new execution within testFlow
        flowsPage.clickNew();
        createFlowPage.form().setValues("testFlow", "testDesc", GENERIC);
        flowsPage.clickAddExecution();
        createExecutionPage.form().selectProviderOption(RESET_PASSWORD);
        createExecutionPage.form().save();
        assertAlertSuccess();
        // REST
        AuthenticationFlowRepresentation flowRest = getFlowFromREST("testFlow");
        Assert.assertEquals(1, flowRest.getAuthenticationExecutions().size());
        Assert.assertEquals("reset-password", flowRest.getAuthenticationExecutions().get(0).getAuthenticator());
        // UI
        Assert.assertEquals("TestFlow", flowsPage.getFlowSelectValue());
        Assert.assertEquals(1, flowsPage.table().getFlowsAliasesWithRequirements().size());
        Assert.assertTrue(flowsPage.table().getFlowsAliasesWithRequirements().keySet().contains("Reset Password"));
        // Deletion
        flowsPage.clickDelete();
        modalDialog.confirmDeletion();
        assertAlertSuccess();
        Assert.assertThat(flowsPage.getFlowAllValues(), IsNot.not(CoreMatchers.hasItem("TestFlow")));
    }

    @Test
    public void navigationTest() {
        flowsPage.selectFlowOption(BROWSER);
        flowsPage.clickCopy();
        modalDialog.ok();
        // init order
        // first should be Cookie
        // second Kerberos
        // third Identity provider redirector
        // fourth Test Copy Of Browser Forms
        // a) Username Password Form
        // b) OTP Form
        flowsPage.table().clickLevelDownButton("Cookie");
        assertAlertSuccess();
        flowsPage.table().clickLevelUpButton("Cookie");
        assertAlertSuccess();
        flowsPage.table().clickLevelUpButton("Kerberos");
        assertAlertSuccess();
        flowsPage.table().clickLevelDownButton("Identity Provider Redirector");
        assertAlertSuccess();
        flowsPage.table().clickLevelUpButton("OTP Form");
        assertAlertSuccess();
        List<String> expectedOrder = new ArrayList<>();
        Collections.addAll(expectedOrder, "Kerberos", "Cookie", "Copy Of Browser Forms", "OTP Form", "Username Password Form", "Identity Provider Redirector");
        // UI
        Assert.assertEquals(6, flowsPage.table().getFlowsAliasesWithRequirements().size());
        Assert.assertTrue(expectedOrder.containsAll(flowsPage.table().getFlowsAliasesWithRequirements().keySet()));
        // REST
        List<AuthenticationExecutionExportRepresentation> executionsRest = getFlowFromREST("Copy of browser").getAuthenticationExecutions();
        Assert.assertEquals("auth-spnego", executionsRest.get(0).getAuthenticator());
        Assert.assertEquals("auth-cookie", executionsRest.get(1).getAuthenticator());
        Assert.assertEquals("Copy of browser forms", executionsRest.get(2).getFlowAlias());
        Assert.assertEquals("identity-provider-redirector", executionsRest.get(3).getAuthenticator());
        flowsPage.clickDelete();
        modalDialog.confirmDeletion();
    }

    @Test
    public void requirementTest() {
        // rest: add or copy flow to test navigation (browser), add reset, password
        flowsPage.selectFlowOption(BROWSER);
        flowsPage.table().changeRequirement("Cookie", DISABLED);
        assertAlertSuccess();
        flowsPage.table().changeRequirement("Kerberos", REQUIRED);
        assertAlertSuccess();
        flowsPage.table().changeRequirement("Kerberos", ALTERNATIVE);
        assertAlertSuccess();
        flowsPage.table().changeRequirement("OTP Form", DISABLED);
        assertAlertSuccess();
        flowsPage.table().changeRequirement("OTP Form", OPTIONAL);
        assertAlertSuccess();
        // UI
        List<String> expectedOrder = new ArrayList<>();
        Collections.addAll(expectedOrder, "DISABLED", "ALTERNATIVE", "ALTERNATIVE", "ALTERNATIVE", "REQUIRED", "OPTIONAL");
        Assert.assertTrue(expectedOrder.containsAll(flowsPage.table().getFlowsAliasesWithRequirements().values()));
        // REST:
        List<AuthenticationExecutionExportRepresentation> browserFlow = getFlowFromREST("browser").getAuthenticationExecutions();
        Assert.assertEquals("DISABLED", browserFlow.get(0).getRequirement());
        Assert.assertEquals("ALTERNATIVE", browserFlow.get(1).getRequirement());
        Assert.assertEquals("ALTERNATIVE", browserFlow.get(2).getRequirement());
    }

    @Test
    public void actionsTest() {
        // rest: add or copy flow to test navigation (browser)
        flowsPage.selectFlowOption(BROWSER);
        flowsPage.clickCopy();
        modalDialog.ok();
        flowsPage.table().performAction("Cookie", DELETE);
        modalDialog.confirmDeletion();
        assertAlertSuccess();
        flowsPage.table().performAction("Kerberos", DELETE);
        modalDialog.confirmDeletion();
        assertAlertSuccess();
        flowsPage.table().performAction("Copy Of Browser Forms", ADD_FLOW);
        createFlowPage.form().setValues("nestedFlow", "testDesc", FORM);
        assertAlertSuccess();
        flowsPage.table().performAction("Copy Of Browser Forms", ADD_EXECUTION);
        createExecutionPage.form().selectProviderOption(RESET_PASSWORD);
        createExecutionPage.form().save();
        assertAlertSuccess();
        // UI
        List<String> expectedOrder = new ArrayList<>();
        Collections.addAll(expectedOrder, "Identity Provider Redirector", "Copy Of Browser Forms", "Username Password Form", "OTP Form", "NestedFlow", "Reset Password");
        Assert.assertEquals(6, flowsPage.table().getFlowsAliasesWithRequirements().size());
        Assert.assertTrue(expectedOrder.containsAll(flowsPage.table().getFlowsAliasesWithRequirements().keySet()));
        // REST
        List<AuthenticationExecutionExportRepresentation> executionsRest = getFlowFromREST("Copy of browser").getAuthenticationExecutions();
        Assert.assertEquals("identity-provider-redirector", executionsRest.get(0).getAuthenticator());
        String tmpFlowAlias = executionsRest.get(1).getFlowAlias();
        Assert.assertEquals("Copy of browser forms", tmpFlowAlias);
        Assert.assertEquals("Username Password Form", testRealmResource().flows().getExecutions(tmpFlowAlias).get(0).getDisplayName());
        Assert.assertEquals("nestedFlow", testRealmResource().flows().getExecutions(tmpFlowAlias).get(2).getDisplayName());
    }
}

