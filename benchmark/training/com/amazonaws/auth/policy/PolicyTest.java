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


import Principal.All;
import Principal.AllServices;
import Principal.AllUsers;
import Principal.AllWebProviders;
import Services.AmazonEC2;
import Services.AmazonElasticTranscoder;
import StringComparisonType.StringNotLike;
import WebIdentityProviders.Amazon;
import com.amazonaws.auth.policy.Principal.Services;
import com.amazonaws.auth.policy.Principal.WebIdentityProviders;
import com.amazonaws.auth.policy.Statement.Effect;
import com.amazonaws.auth.policy.conditions.IpAddressCondition;
import com.amazonaws.auth.policy.conditions.IpAddressCondition.IpAddressComparisonType;
import com.amazonaws.auth.policy.conditions.StringCondition.StringComparisonType;
import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;


/**
 * Unit tests for constructing policy objects and serializing them to JSON.
 */
public class PolicyTest {
    @Test
    public void testPrincipals() {
        Policy policy = new Policy();
        policy.withStatements(new Statement(Effect.Allow).withResources(new Resource("resource")).withPrincipals(new Principal("accountId1"), new Principal("accountId2")).withActions(new PolicyTest.TestAction("action")));
        JsonNode jsonPolicyNode = Jackson.jsonNodeOf(policy.toJson());
        JsonNode statementArray = jsonPolicyNode.get("Statement");
        Assert.assertTrue(statementArray.isArray());
        Assert.assertTrue(((statementArray.size()) == 1));
        JsonNode statement = statementArray.get(0);
        Assert.assertTrue(statement.has("Resource"));
        Assert.assertTrue(statement.has("Principal"));
        Assert.assertTrue(statement.has("Action"));
        Assert.assertTrue(statement.has("Effect"));
        JsonNode users = statement.get("Principal").get("AWS");
        Assert.assertEquals(2, users.size());
        Assert.assertEquals(users.get(0).asText(), "accountId1");
        Assert.assertEquals(users.get(1).asText(), "accountId2");
        policy = new Policy();
        policy.withStatements(new Statement(Effect.Allow).withResources(new Resource("resource")).withPrincipals(new Principal(Services.AmazonEC2), new Principal(Services.AmazonElasticTranscoder)).withActions(new PolicyTest.TestAction("action")));
        jsonPolicyNode = Jackson.jsonNodeOf(policy.toJson());
        statementArray = jsonPolicyNode.get("Statement");
        Assert.assertTrue(((statementArray.size()) == 1));
        statement = statementArray.get(0);
        Assert.assertTrue(statement.has("Resource"));
        Assert.assertTrue(statement.has("Principal"));
        Assert.assertTrue(statement.has("Action"));
        Assert.assertTrue(statement.has("Effect"));
        JsonNode services = statement.get("Principal").get("Service");
        Assert.assertTrue(services.isArray());
        Assert.assertTrue(((services.size()) == 2));
        Assert.assertEquals(AmazonEC2.getServiceId(), services.get(0).asText());
        Assert.assertEquals(AmazonElasticTranscoder.getServiceId(), services.get(1).asText());
        policy = new Policy();
        policy.withStatements(new Statement(Effect.Allow).withResources(new Resource("resource")).withPrincipals(AllUsers).withActions(new PolicyTest.TestAction("action")));
        jsonPolicyNode = Jackson.jsonNodeOf(policy.toJson());
        statementArray = jsonPolicyNode.get("Statement");
        Assert.assertTrue(((statementArray.size()) == 1));
        statement = statementArray.get(0);
        Assert.assertTrue(statement.has("Resource"));
        Assert.assertTrue(statement.has("Principal"));
        Assert.assertTrue(statement.has("Action"));
        Assert.assertTrue(statement.has("Effect"));
        users = statement.get("Principal").get("AWS");
        Assert.assertEquals(users.asText(), "*");
        policy = new Policy();
        policy.withStatements(new Statement(Effect.Allow).withResources(new Resource("resource")).withPrincipals(AllServices, AllUsers).withActions(new PolicyTest.TestAction("action")));
        jsonPolicyNode = Jackson.jsonNodeOf(policy.toJson());
        statementArray = jsonPolicyNode.get("Statement");
        Assert.assertTrue(((statementArray.size()) == 1));
        statement = statementArray.get(0);
        Assert.assertTrue(statement.has("Resource"));
        Assert.assertTrue(statement.has("Principal"));
        Assert.assertTrue(statement.has("Action"));
        Assert.assertTrue(statement.has("Effect"));
        users = statement.get("Principal").get("AWS");
        services = statement.get("Principal").get("Service");
        Assert.assertEquals(users.asText(), "*");
        Assert.assertEquals(services.asText(), "*");
        policy = new Policy();
        policy.withStatements(new Statement(Effect.Allow).withResources(new Resource("resource")).withPrincipals(AllServices, AllUsers, AllWebProviders).withActions(new PolicyTest.TestAction("action")));
        jsonPolicyNode = Jackson.jsonNodeOf(policy.toJson());
        statementArray = jsonPolicyNode.get("Statement");
        Assert.assertTrue(((statementArray.size()) == 1));
        statement = statementArray.get(0);
        Assert.assertTrue(statement.has("Resource"));
        Assert.assertTrue(statement.has("Principal"));
        Assert.assertTrue(statement.has("Action"));
        Assert.assertTrue(statement.has("Effect"));
        users = statement.get("Principal").get("AWS");
        services = statement.get("Principal").get("Service");
        JsonNode webProviders = statement.get("Principal").get("Federated");
        Assert.assertEquals(users.asText(), "*");
        Assert.assertEquals(services.asText(), "*");
        Assert.assertEquals(webProviders.asText(), "*");
        policy = new Policy();
        policy.withStatements(new Statement(Effect.Allow).withResources(new Resource("resource")).withPrincipals(AllServices, AllUsers, AllWebProviders, All).withActions(new PolicyTest.TestAction("action")));
        jsonPolicyNode = Jackson.jsonNodeOf(policy.toJson());
        statementArray = jsonPolicyNode.get("Statement");
        Assert.assertTrue(((statementArray.size()) == 1));
        statement = statementArray.get(0);
        Assert.assertTrue(statement.has("Resource"));
        Assert.assertTrue(statement.has("Principal"));
        Assert.assertTrue(statement.has("Action"));
        Assert.assertTrue(statement.has("Effect"));
        users = statement.get("Principal").get("AWS");
        services = statement.get("Principal").get("Service");
        webProviders = statement.get("Principal").get("Federated");
        JsonNode allUsers = statement.get("Principal").get("*");
        Assert.assertEquals(users.asText(), "*");
        Assert.assertEquals(services.asText(), "*");
        Assert.assertEquals(webProviders.asText(), "*");
        Assert.assertEquals(allUsers.asText(), "*");
        policy = new Policy();
        policy.withStatements(new Statement(Effect.Allow).withResources(new Resource("resource")).withPrincipals(new Principal("accountId1"), AllUsers).withActions(new PolicyTest.TestAction("action")));
        jsonPolicyNode = Jackson.jsonNodeOf(policy.toJson());
        statementArray = jsonPolicyNode.get("Statement");
        Assert.assertTrue(((statementArray.size()) == 1));
        statement = statementArray.get(0);
        Assert.assertTrue(statement.has("Resource"));
        Assert.assertTrue(statement.has("Principal"));
        Assert.assertTrue(statement.has("Action"));
        Assert.assertTrue(statement.has("Effect"));
        users = statement.get("Principal").get("AWS");
        Assert.assertTrue(users.isArray());
        Assert.assertEquals(users.get(0).asText(), "accountId1");
        Assert.assertEquals(users.get(1).asText(), "*");
        policy = new Policy();
        policy.withStatements(new Statement(Effect.Allow).withResources(new Resource("resource")).withPrincipals(new Principal(Services.AmazonEC2), AllServices, new Principal("accountId1")).withActions(new PolicyTest.TestAction("action")));
        jsonPolicyNode = Jackson.jsonNodeOf(policy.toJson());
        statementArray = jsonPolicyNode.get("Statement");
        Assert.assertTrue(((statementArray.size()) == 1));
        statement = statementArray.get(0);
        Assert.assertTrue(statement.has("Resource"));
        Assert.assertTrue(statement.has("Principal"));
        Assert.assertTrue(statement.has("Action"));
        Assert.assertTrue(statement.has("Effect"));
        users = statement.get("Principal").get("AWS");
        services = statement.get("Principal").get("Service");
        Assert.assertEquals(users.asText(), "accountId1");
        Assert.assertEquals(services.get(0).asText(), AmazonEC2.getServiceId());
        Assert.assertEquals(services.get(1).asText(), "*");
        policy = new Policy();
        policy.withStatements(new Statement(Effect.Allow).withResources(new Resource("resource")).withPrincipals(new Principal(Services.AmazonEC2), AllServices, new Principal("accountId1"), new Principal(WebIdentityProviders.Amazon), AllWebProviders).withActions(new PolicyTest.TestAction("action")));
        jsonPolicyNode = Jackson.jsonNodeOf(policy.toJson());
        statementArray = jsonPolicyNode.get("Statement");
        Assert.assertTrue(((statementArray.size()) == 1));
        statement = statementArray.get(0);
        Assert.assertTrue(statement.has("Resource"));
        Assert.assertTrue(statement.has("Principal"));
        Assert.assertTrue(statement.has("Action"));
        Assert.assertTrue(statement.has("Effect"));
        users = statement.get("Principal").get("AWS");
        services = statement.get("Principal").get("Service");
        webProviders = statement.get("Principal").get("Federated");
        Assert.assertEquals(services.get(0).asText(), AmazonEC2.getServiceId());
        Assert.assertEquals(services.get(1).asText(), "*");
        Assert.assertEquals(users.asText(), "accountId1");
        Assert.assertEquals(webProviders.get(0).asText(), Amazon.getWebIdentityProvider());
        Assert.assertEquals(webProviders.get(1).asText(), "*");
    }

    /**
     * Policies with multiple conditions that use the same comparison type must
     * be merged together in the JSON format, otherwise there will be two keys
     * with the same name and one will override the other.
     */
    @Test
    public void testMultipleConditionKeysForConditionType() throws Exception {
        Policy policy = new Policy();
        policy.withStatements(new Statement(Effect.Allow).withResources(new Resource("arn:aws:sqs:us-east-1:987654321000:MyQueue")).withPrincipals(AllUsers).withActions(new PolicyTest.TestAction("foo")).withConditions(new com.amazonaws.auth.policy.conditions.StringCondition(StringComparisonType.StringNotLike, "key1", "foo"), new com.amazonaws.auth.policy.conditions.StringCondition(StringComparisonType.StringNotLike, "key1", "bar")));
        JsonNode jsonPolicy = Jackson.jsonNodeOf(policy.toJson());
        JsonNode statementArray = jsonPolicy.get("Statement");
        Assert.assertEquals(statementArray.size(), 1);
        JsonNode conditions = statementArray.get(0).get("Condition");
        Assert.assertEquals(conditions.size(), 1);
        JsonNode stringLikeCondition = conditions.get(StringNotLike.toString());
        Assert.assertTrue(stringLikeCondition.has("key1"));
        Assert.assertFalse(stringLikeCondition.has("key2"));
        assertValidStatementIds(policy);
    }

    /**
     * Tests serializing a more complex policy object with multiple statements.
     */
    @Test
    public void testMultipleStatements() throws Exception {
        Policy policy = new Policy("S3PolicyId1");
        policy.withStatements(new Statement(Effect.Allow).withPrincipals(AllUsers).withActions(new PolicyTest.TestAction("action1")).withResources(new Resource("resource")).withConditions(new IpAddressCondition("192.168.143.0/24"), new IpAddressCondition(IpAddressComparisonType.NotIpAddress, "192.168.143.188/32")), new Statement(Effect.Deny).withPrincipals(AllUsers).withActions(new PolicyTest.TestAction("action2")).withResources(new Resource("resource")).withConditions(new IpAddressCondition("10.1.2.0/24")));
        JsonNode jsonPolicy = Jackson.jsonNodeOf(policy.toJson());
        Assert.assertTrue(jsonPolicy.has("Id"));
        JsonNode statementArray = jsonPolicy.get("Statement");
        Assert.assertEquals(statementArray.size(), 2);
        assertValidStatementIds(policy);
        JsonNode statement;
        for (int i = 0; i < (statementArray.size()); i++) {
            statement = statementArray.get(i);
            Assert.assertTrue(statement.has("Sid"));
            Assert.assertTrue(statement.has("Effect"));
            Assert.assertTrue(statement.has("Principal"));
            Assert.assertTrue(statement.has("Action"));
            Assert.assertTrue(statement.has("Resource"));
            Assert.assertTrue(statement.has("Condition"));
        }
    }

    /**
     * Tests that a policy correctly assigns unique statement IDs to any added
     * statements without IDs yet.
     */
    @Test
    public void testStatementIdAssignment() throws Exception {
        Policy policy = new Policy("S3PolicyId1");
        policy.withStatements(withId("0").withPrincipals(AllUsers).withActions(new PolicyTest.TestAction("action1")), withId("1").withPrincipals(AllUsers).withActions(new PolicyTest.TestAction("action1")), new Statement(Effect.Deny).withPrincipals(AllUsers).withActions(new PolicyTest.TestAction("action2")));
        assertValidStatementIds(policy);
    }

    /**
     * Tests that a Statement including a Resource specified as a NotResource element is
     * serialized to JSON with a "NotResource" attribute
     */
    @Test
    public void testNotResourceElement() {
        Policy policy = new Policy("S3PolicyId1").withStatements(withId("0").withPrincipals(AllUsers).withActions(new PolicyTest.TestAction("action1")).withResources(new Resource("any-resource").withIsNotType(true)));
        JsonNode jsonPolicyNode = Jackson.jsonNodeOf(policy.toJson());
        JsonNode statementArray = jsonPolicyNode.get("Statement");
        Assert.assertTrue(((statementArray.size()) == 1));
        JsonNode statement = statementArray.get(0);
        Assert.assertTrue(statement.has("NotResource"));
    }

    /**
     * Tests that a Statement with an empty resources list results in no Resource element
     */
    @Test
    public void testPolicyWithNoResources() {
        Statement statement = withId("0").withPrincipals(AllUsers).withActions(new PolicyTest.TestAction("action1"));
        statement.setResources(new ArrayList<Resource>());
        Policy policy = new Policy("S3PolicyId1").withStatements(statement);
        JsonNode jsonPolicyNode = Jackson.jsonNodeOf(policy.toJson());
        JsonNode statementArray = jsonPolicyNode.get("Statement");
        JsonNode statementNode = statementArray.get(0);
        Assert.assertFalse(statementNode.has("Resource"));
    }

    /**
     * Tests that constructing a Statement with a Resource and a NotResource element will fail
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCannotCreateStatementWithBothResourceAndNotResource() {
        new Policy("S3PolicyId1").withStatements(withId("0").withPrincipals(AllUsers).withActions(new PolicyTest.TestAction("action1")).withResources(new Resource("any-resource").withIsNotType(true), new Resource("any-other-resource")));
    }

    /**
     * Tests that serializing a Statement with a Resource and a NotResource element will fail
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCannotSerializeStatementWithBothResourceAndNotResource() {
        Statement statement = withId("0").withPrincipals(AllUsers).withActions(new PolicyTest.TestAction("action1")).withResources(new Resource("any-resource").withIsNotType(true));
        statement.getResources().add(new Resource("any-other-resource"));
        Policy policy = new Policy("S3PolicyId1").withStatements(statement);
        policy.toJson();
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

    @Test
    public void testPrincipalAccountId() {
        String ID_WITH_HYPHEN = "a-b-c-d-e-f-g";
        String ID_WITHOUT_HYPHEN = "abcdefg";
        assertEquals(ID_WITHOUT_HYPHEN, new Principal(ID_WITH_HYPHEN).getId());
        assertEquals(ID_WITHOUT_HYPHEN, new Principal("AWS", ID_WITH_HYPHEN).getId());
        assertEquals(ID_WITH_HYPHEN, new Principal("Federated", ID_WITH_HYPHEN).getId());
        assertEquals(ID_WITH_HYPHEN, new Principal("AWS", ID_WITH_HYPHEN, false).getId());
    }
}

