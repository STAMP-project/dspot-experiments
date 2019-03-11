/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.auth.policy;


import ConditionFactory.SOURCE_IP_CONDITION_KEY;
import Effect.Allow;
import Effect.Deny;
import Principal.All;
import Principal.AllServices;
import Principal.AllUsers;
import Principal.AllWebProviders;
import Services.AWSCloudHSM;
import Services.AmazonEC2;
import Services.AmazonElasticTranscoder;
import com.amazonaws.auth.policy.Principal.Services;
import com.amazonaws.auth.policy.Statement.Effect;
import com.amazonaws.auth.policy.conditions.IpAddressCondition;
import com.amazonaws.auth.policy.conditions.IpAddressCondition.IpAddressComparisonType;
import com.amazonaws.auth.policy.conditions.StringCondition.StringComparisonType;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for generating AWS policy object from JSON string.
 */
public class PolicyReaderTest {
    final String POLICY_VERSION = "2012-10-17";

    @Test
    public void testPrincipals() {
        Policy policy = new Policy();
        policy.withStatements(new Statement(Effect.Allow).withResources(new Resource("resource")).withPrincipals(new Principal("accountId1"), new Principal("accountId2")).withActions(new PolicyReaderTest.TestAction("action")));
        policy = Policy.fromJson(policy.toJson());
        Assert.assertEquals(1, policy.getStatements().size());
        List<Statement> statements = new LinkedList<Statement>(policy.getStatements());
        Assert.assertEquals(Allow, statements.get(0).getEffect());
        Assert.assertEquals("action", statements.get(0).getActions().get(0).getActionName());
        Assert.assertEquals("resource", statements.get(0).getResources().get(0).getId());
        Assert.assertEquals(2, statements.get(0).getPrincipals().size());
        Assert.assertEquals("AWS", statements.get(0).getPrincipals().get(0).getProvider());
        Assert.assertEquals("accountId1", statements.get(0).getPrincipals().get(0).getId());
        Assert.assertEquals("AWS", statements.get(0).getPrincipals().get(1).getProvider());
        Assert.assertEquals("accountId2", statements.get(0).getPrincipals().get(1).getId());
        policy = new Policy();
        policy.withStatements(new Statement(Effect.Allow).withResources(new Resource("resource")).withPrincipals(new Principal(Services.AmazonEC2), new Principal(Services.AmazonElasticTranscoder)).withActions(new PolicyReaderTest.TestAction("action")));
        policy = Policy.fromJson(policy.toJson());
        Assert.assertEquals(1, policy.getStatements().size());
        statements = new LinkedList<Statement>(policy.getStatements());
        Assert.assertEquals(Allow, statements.get(0).getEffect());
        Assert.assertEquals(1, statements.get(0).getActions().size());
        Assert.assertEquals("action", statements.get(0).getActions().get(0).getActionName());
        Assert.assertEquals(2, statements.get(0).getPrincipals().size());
        Assert.assertEquals("Service", statements.get(0).getPrincipals().get(0).getProvider());
        Assert.assertEquals(AmazonEC2.getServiceId(), statements.get(0).getPrincipals().get(0).getId());
        Assert.assertEquals("Service", statements.get(0).getPrincipals().get(1).getProvider());
        Assert.assertEquals(AmazonElasticTranscoder.getServiceId(), statements.get(0).getPrincipals().get(1).getId());
        policy = new Policy();
        policy.withStatements(new Statement(Effect.Allow).withResources(new Resource("resource")).withPrincipals(All).withActions(new PolicyReaderTest.TestAction("action")));
        policy = Policy.fromJson(policy.toJson());
        Assert.assertEquals(1, policy.getStatements().size());
        statements = new LinkedList<Statement>(policy.getStatements());
        Assert.assertEquals(Allow, statements.get(0).getEffect());
        Assert.assertEquals(1, statements.get(0).getActions().size());
        Assert.assertEquals("action", statements.get(0).getActions().get(0).getActionName());
        Assert.assertEquals(1, statements.get(0).getPrincipals().size());
        Assert.assertEquals(All, statements.get(0).getPrincipals().get(0));
        policy = new Policy();
        policy.withStatements(new Statement(Effect.Allow).withResources(new Resource("resource")).withPrincipals(AllUsers, AllServices, AllWebProviders).withActions(new PolicyReaderTest.TestAction("action")));
        policy = Policy.fromJson(policy.toJson());
        Assert.assertEquals(1, policy.getStatements().size());
        statements = new LinkedList<Statement>(policy.getStatements());
        Assert.assertEquals(Allow, statements.get(0).getEffect());
        Assert.assertEquals(1, statements.get(0).getActions().size());
        Assert.assertEquals("action", statements.get(0).getActions().get(0).getActionName());
        Assert.assertEquals(3, statements.get(0).getPrincipals().size());
        Assert.assertThat(statements.get(0).getPrincipals(), Matchers.contains(AllUsers, AllServices, AllWebProviders));
    }

    @Test
    public void testMultipleConditionKeysForConditionType() throws Exception {
        Policy policy = new Policy();
        policy.withStatements(new Statement(Effect.Allow).withResources(new Resource("arn:aws:sqs:us-east-1:987654321000:MyQueue")).withPrincipals(AllUsers).withActions(new PolicyReaderTest.TestAction("foo")).withConditions(new com.amazonaws.auth.policy.conditions.StringCondition(StringComparisonType.StringNotLike, "key1", "foo"), new com.amazonaws.auth.policy.conditions.StringCondition(StringComparisonType.StringNotLike, "key1", "bar")));
        policy = Policy.fromJson(policy.toJson());
        Assert.assertEquals(1, policy.getStatements().size());
        List<Statement> statements = new LinkedList<Statement>(policy.getStatements());
        Assert.assertEquals(Allow, statements.get(0).getEffect());
        Assert.assertEquals(1, statements.get(0).getActions().size());
        Assert.assertEquals("foo", statements.get(0).getActions().get(0).getActionName());
        Assert.assertEquals(1, statements.get(0).getConditions().size());
        Assert.assertEquals("StringNotLike", statements.get(0).getConditions().get(0).getType());
        Assert.assertEquals("key1", statements.get(0).getConditions().get(0).getConditionKey());
        Assert.assertEquals(2, statements.get(0).getConditions().get(0).getValues().size());
        Assert.assertEquals("foo", statements.get(0).getConditions().get(0).getValues().get(0));
        Assert.assertEquals("bar", statements.get(0).getConditions().get(0).getValues().get(1));
    }

    /**
     * Test policy parsing when the "Effect" is not mentioned in a Statement.
     * The Effect must be default to "Deny" when it is not mentioned.
     */
    @Test
    public void testPolicyParsingWithNoEffect() {
        String jsonString = "{" + ((((((("\"Statement\": [{" + "\"Action\": [") + "\"elasticmapreduce:*\",") + "\"iam:PassRole\"") + "],") + "\"Resource\": [\"*\"]") + "}]") + "}");
        Policy policy = Policy.fromJson(jsonString);
        Assert.assertEquals(1, policy.getStatements().size());
        List<Statement> statements = new LinkedList<Statement>(policy.getStatements());
        Assert.assertEquals(Deny, statements.get(0).getEffect());
        Assert.assertEquals(1, statements.size());
    }

    @Test
    public void testMultipleStatements() throws Exception {
        Policy policy = new Policy("S3PolicyId1");
        policy.withStatements(withId("0").withPrincipals(AllUsers).withActions(new PolicyReaderTest.TestAction("action1")).withResources(new Resource("resource")).withConditions(new IpAddressCondition("192.168.143.0/24"), new IpAddressCondition(IpAddressComparisonType.NotIpAddress, "192.168.143.188/32")), withId("1").withPrincipals(AllUsers).withActions(new PolicyReaderTest.TestAction("action2")).withResources(new Resource("resource")).withConditions(new IpAddressCondition("10.1.2.0/24")));
        policy = Policy.fromJson(policy.toJson());
        Assert.assertEquals(2, policy.getStatements().size());
        Assert.assertEquals("S3PolicyId1", policy.getId());
        List<Statement> statements = new LinkedList<Statement>(policy.getStatements());
        Assert.assertEquals(Allow, statements.get(0).getEffect());
        Assert.assertEquals("0", statements.get(0).getId());
        Assert.assertEquals(1, statements.get(0).getPrincipals().size());
        Assert.assertEquals("*", statements.get(0).getPrincipals().get(0).getId());
        Assert.assertEquals("AWS", statements.get(0).getPrincipals().get(0).getProvider());
        Assert.assertEquals(1, statements.get(0).getResources().size());
        Assert.assertEquals("resource", statements.get(0).getResources().get(0).getId());
        Assert.assertEquals(1, statements.get(0).getActions().size());
        Assert.assertEquals("action1", statements.get(0).getActions().get(0).getActionName());
        Assert.assertEquals(2, statements.get(0).getConditions().size());
        Assert.assertEquals("IpAddress", statements.get(0).getConditions().get(0).getType());
        Assert.assertEquals(SOURCE_IP_CONDITION_KEY, statements.get(0).getConditions().get(0).getConditionKey());
        Assert.assertEquals(1, statements.get(0).getConditions().get(0).getValues().size());
        Assert.assertEquals("192.168.143.0/24", statements.get(0).getConditions().get(0).getValues().get(0));
        Assert.assertEquals("NotIpAddress", statements.get(0).getConditions().get(1).getType());
        Assert.assertEquals(1, statements.get(0).getConditions().get(1).getValues().size());
        Assert.assertEquals("192.168.143.188/32", statements.get(0).getConditions().get(1).getValues().get(0));
        Assert.assertEquals(SOURCE_IP_CONDITION_KEY, statements.get(1).getConditions().get(0).getConditionKey());
        Assert.assertEquals(Deny, statements.get(1).getEffect());
        Assert.assertEquals("1", statements.get(1).getId());
        Assert.assertEquals(1, statements.get(1).getPrincipals().size());
        Assert.assertEquals("*", statements.get(1).getPrincipals().get(0).getId());
        Assert.assertEquals("AWS", statements.get(1).getPrincipals().get(0).getProvider());
        Assert.assertEquals(1, statements.get(1).getResources().size());
        Assert.assertEquals("resource", statements.get(1).getResources().get(0).getId());
        Assert.assertEquals(1, statements.get(1).getActions().size());
        Assert.assertEquals("action2", statements.get(1).getActions().get(0).getActionName());
        Assert.assertEquals(1, statements.get(1).getConditions().size());
        Assert.assertEquals("IpAddress", statements.get(1).getConditions().get(0).getType());
        Assert.assertEquals(SOURCE_IP_CONDITION_KEY, statements.get(0).getConditions().get(0).getConditionKey());
        Assert.assertEquals(1, statements.get(0).getConditions().get(0).getValues().size());
        Assert.assertEquals("10.1.2.0/24", statements.get(1).getConditions().get(0).getValues().get(0));
    }

    @Test
    public void testNoJsonArray() {
        String jsonString = "{" + ((((((((((((((("\"Version\": \"2012-10-17\"," + "\"Statement\": [") + "{") + "\"Effect\": \"Allow\",") + "\"Principal\": {") + "\"AWS\": \"*\"") + "},") + "\"Action\": \"sts:AssumeRole\",") + "\"Condition\": {") + "\"IpAddress\": {") + " \"aws:SourceIp\": \"10.10.10.10/32\"") + "}") + "}") + "}") + "]") + "}");
        Policy policy = Policy.fromJson(jsonString);
        Assert.assertEquals(POLICY_VERSION, policy.getVersion());
        List<Statement> statements = new LinkedList<Statement>(policy.getStatements());
        Assert.assertEquals(1, statements.size());
        Assert.assertEquals(1, statements.get(0).getActions().size());
        Assert.assertEquals(Allow, statements.get(0).getEffect());
        Assert.assertEquals("sts:AssumeRole", statements.get(0).getActions().get(0).getActionName());
        Assert.assertEquals(1, statements.get(0).getConditions().size());
        Assert.assertEquals("IpAddress", statements.get(0).getConditions().get(0).getType());
        Assert.assertEquals("aws:SourceIp", statements.get(0).getConditions().get(0).getConditionKey());
        Assert.assertEquals(1, statements.get(0).getConditions().get(0).getValues().size());
        Assert.assertEquals("10.10.10.10/32", statements.get(0).getConditions().get(0).getValues().get(0));
        Assert.assertEquals(1, statements.get(0).getPrincipals().size());
        Assert.assertEquals("*", statements.get(0).getPrincipals().get(0).getId());
        Assert.assertEquals("AWS", statements.get(0).getPrincipals().get(0).getProvider());
    }

    @Test
    public void testNoStatementArray() {
        String policy = "{\n" + (((((((("  \"Version\": \"2012-10-17\",\n" + "  \"Statement\": {\n") + "    \"Effect\": \"Allow\",\n") + "    \"Action\": [\n") + "      \"acm:DescribeCertificate\"") + "    ],\n") + "    \"Resource\": \"*\"\n") + "  }\n") + "}");
        Policy p = Policy.fromJson(policy);
        Assert.assertEquals(POLICY_VERSION, p.getVersion());
        List<Statement> statements = new LinkedList<Statement>(p.getStatements());
        Assert.assertEquals(1, statements.size());
        Assert.assertEquals(Allow, statements.get(0).getEffect());
        Assert.assertEquals(1, statements.get(0).getActions().size());
        Assert.assertEquals("acm:DescribeCertificate", statements.get(0).getActions().get(0).getActionName());
        Assert.assertEquals("*", statements.get(0).getResources().get(0).getId());
    }

    /**
     * Tests that SAML-based federated user is supported as principal.
     */
    @Test
    public void testFederatedUserBySAMLProvider() {
        String jsonString = "{" + (((((((((((((((("\"Version\":\"2012-10-17\"," + "\"Statement\":[") + "{") + "\"Sid\":\"\",") + "\"Effect\":\"Allow\",") + "\"Principal\":{") + "\"Federated\":\"arn:aws:iam::862954416975:saml-provider/myprovider\"") + "},") + "\"Action\":\"sts:AssumeRoleWithSAML\",") + "\"Condition\":{") + "\"StringEquals\":{") + "\"SAML:aud\":\"https://signin.aws.amazon.com/saml\"") + "}") + "}") + "}") + "]") + "}");
        Policy policy = Policy.fromJson(jsonString);
        Assert.assertEquals(POLICY_VERSION, policy.getVersion());
        List<Statement> statements = new LinkedList<Statement>(policy.getStatements());
        Assert.assertEquals(1, statements.size());
        Assert.assertEquals(1, statements.get(0).getActions().size());
        Assert.assertEquals(Allow, statements.get(0).getEffect());
        Assert.assertEquals("sts:AssumeRoleWithSAML", statements.get(0).getActions().get(0).getActionName());
        Assert.assertEquals(1, statements.get(0).getConditions().size());
        Assert.assertEquals("StringEquals", statements.get(0).getConditions().get(0).getType());
        Assert.assertEquals("SAML:aud", statements.get(0).getConditions().get(0).getConditionKey());
        Assert.assertEquals(1, statements.get(0).getConditions().get(0).getValues().size());
        Assert.assertEquals("https://signin.aws.amazon.com/saml", statements.get(0).getConditions().get(0).getValues().get(0));
        Assert.assertEquals(1, statements.get(0).getPrincipals().size());
        Assert.assertEquals("arn:aws:iam::862954416975:saml-provider/myprovider", statements.get(0).getPrincipals().get(0).getId());
        Assert.assertEquals("Federated", statements.get(0).getPrincipals().get(0).getProvider());
    }

    @Test
    public void testCloudHSMServicePrincipal() {
        String jsonString = "{" + ((((((("\"Version\":\"2008-10-17\"," + "\"Statement\":[") + "{\"Sid\":\"\",") + "\"Effect\":\"Allow\",") + "\"Principal\":{\"Service\":\"cloudhsm.amazonaws.com\"},") + "\"Action\":\"sts:AssumeRole\"}") + "]") + "}");
        Policy policy = Policy.fromJson(jsonString);
        Assert.assertEquals(POLICY_VERSION, policy.getVersion());
        List<Statement> statements = new LinkedList<Statement>(policy.getStatements());
        Assert.assertEquals(1, statements.size());
        Assert.assertEquals(1, statements.get(0).getActions().size());
        Assert.assertEquals(Allow, statements.get(0).getEffect());
        Assert.assertEquals("sts:AssumeRole", statements.get(0).getActions().get(0).getActionName());
        Assert.assertEquals(0, statements.get(0).getConditions().size());
        Assert.assertEquals(1, statements.get(0).getPrincipals().size());
        Assert.assertEquals(AWSCloudHSM.getServiceId(), statements.get(0).getPrincipals().get(0).getId());
        Assert.assertEquals("Service", statements.get(0).getPrincipals().get(0).getProvider());
    }

    private class TestAction implements Action {
        private final String name;

        public TestAction(String name) {
            this.name = name;
        }

        public String getActionName() {
            return name;
        }
    }

    /**
     * This test case was written as result of the following TT
     *
     * @see TT:0030871921

    When a service is mentioned in the principal, we always try to
    figure out the service from
    <code>com.amazonaws.auth.policy.Principal.Services</code> enum. For
    new services introduced, if the enum is not updated, then the parsing
    fails.
     */
    @Test
    public void testPrincipalWithServiceNotInServicesEnum() {
        String jsonString = "{" + ((((((((((("\"Version\":\"2008-10-17\"," + "\"Statement\":[") + "{") + "\"Sid\":\"\",") + "\"Effect\":\"Allow\",") + "\"Principal\":{") + "\"Service\":\"workspaces.amazonaws.com\" ") + "},") + "\"Action\":\"sts:AssumeRole\"") + "}") + "]") + "}");
        Policy policy = Policy.fromJson(jsonString);
        Assert.assertEquals(POLICY_VERSION, policy.getVersion());
        List<Statement> statements = new LinkedList<Statement>(policy.getStatements());
        Assert.assertEquals(1, statements.size());
        Assert.assertEquals(1, statements.get(0).getActions().size());
        Assert.assertEquals(Allow, statements.get(0).getEffect());
        Assert.assertEquals("sts:AssumeRole", statements.get(0).getActions().get(0).getActionName());
        Assert.assertEquals(0, statements.get(0).getConditions().size());
        Assert.assertEquals(1, statements.get(0).getPrincipals().size());
        Assert.assertEquals("workspaces.amazonaws.com", statements.get(0).getPrincipals().get(0).getId());
        Assert.assertEquals("Service", statements.get(0).getPrincipals().get(0).getProvider());
    }

    @Test
    public void testAccountNamePrincipalWithDashesAreStrippedByDefault() {
        String jsonString = "{" + ((((((((("\"Version\": \"2012-10-17\"," + "\"Statement\": [") + "{") + "\"Effect\": \"Allow\",") + "\"Principal\": {") + "\"AWS\": \"test-string\"") + "}") + "}") + "]") + "}");
        Policy policy = Policy.fromJson(jsonString);
        List<Statement> statements = new ArrayList<Statement>(policy.getStatements());
        Assert.assertEquals("teststring", statements.get(0).getPrincipals().get(0).getId());
    }

    @Test
    public void testAccountNamePrincipalWithDashesAreNotStrippedWhenDisabled() {
        String jsonString = "{" + ((((((((("\"Version\": \"2012-10-17\"," + "\"Statement\": [") + "{") + "\"Effect\": \"Allow\",") + "\"Principal\": {") + "\"AWS\": \"test-string\"") + "}") + "}") + "]") + "}");
        Policy policy = Policy.fromJson(jsonString, new PolicyReaderOptions().withStripAwsPrincipalIdHyphensEnabled(false));
        List<Statement> statements = new ArrayList<Statement>(policy.getStatements());
        Assert.assertEquals("test-string", statements.get(0).getPrincipals().get(0).getId());
    }
}

