/**
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud;


import com.google.cloud.Policy.DefaultMarshaller;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class PolicyTest {
    private static final Identity ALL_USERS = Identity.allUsers();

    private static final Identity ALL_AUTH_USERS = Identity.allAuthenticatedUsers();

    private static final Identity USER = Identity.user("abc@gmail.com");

    private static final Identity SERVICE_ACCOUNT = Identity.serviceAccount("service-account@gmail.com");

    private static final Identity GROUP = Identity.group("group@gmail.com");

    private static final Identity DOMAIN = Identity.domain("google.com");

    private static final Role VIEWER = Role.viewer();

    private static final Role EDITOR = Role.editor();

    private static final Role OWNER = Role.owner();

    private static final Map<Role, ImmutableSet<Identity>> BINDINGS = ImmutableMap.of(PolicyTest.VIEWER, ImmutableSet.of(PolicyTest.USER, PolicyTest.SERVICE_ACCOUNT, PolicyTest.ALL_USERS), PolicyTest.EDITOR, ImmutableSet.of(PolicyTest.ALL_AUTH_USERS, PolicyTest.GROUP, PolicyTest.DOMAIN));

    private static final Policy SIMPLE_POLICY = Policy.newBuilder().addIdentity(PolicyTest.VIEWER, PolicyTest.USER, PolicyTest.SERVICE_ACCOUNT, PolicyTest.ALL_USERS).addIdentity(PolicyTest.EDITOR, PolicyTest.ALL_AUTH_USERS, PolicyTest.GROUP, PolicyTest.DOMAIN).build();

    private static final Policy FULL_POLICY = Policy.newBuilder().setBindings(PolicyTest.SIMPLE_POLICY.getBindings()).setEtag("etag").setVersion(1).build();

    @Test
    public void testBuilder() {
        Assert.assertEquals(PolicyTest.BINDINGS, PolicyTest.SIMPLE_POLICY.getBindings());
        Assert.assertEquals(null, PolicyTest.SIMPLE_POLICY.getEtag());
        Assert.assertEquals(0, PolicyTest.SIMPLE_POLICY.getVersion());
        Assert.assertEquals(PolicyTest.BINDINGS, PolicyTest.FULL_POLICY.getBindings());
        Assert.assertEquals("etag", PolicyTest.FULL_POLICY.getEtag());
        Assert.assertEquals(1, PolicyTest.FULL_POLICY.getVersion());
        Map<Role, Set<Identity>> editorBinding = ImmutableMap.<Role, Set<Identity>>builder().put(PolicyTest.EDITOR, PolicyTest.BINDINGS.get(PolicyTest.EDITOR)).build();
        Policy policy = PolicyTest.FULL_POLICY.toBuilder().setBindings(editorBinding).build();
        Assert.assertEquals(editorBinding, policy.getBindings());
        Assert.assertEquals("etag", policy.getEtag());
        Assert.assertEquals(1, policy.getVersion());
        policy = PolicyTest.SIMPLE_POLICY.toBuilder().removeRole(PolicyTest.EDITOR).build();
        Assert.assertEquals(ImmutableMap.of(PolicyTest.VIEWER, PolicyTest.BINDINGS.get(PolicyTest.VIEWER)), policy.getBindings());
        Assert.assertNull(policy.getEtag());
        Assert.assertEquals(0, policy.getVersion());
        policy = policy.toBuilder().removeIdentity(PolicyTest.VIEWER, PolicyTest.USER, PolicyTest.ALL_USERS).addIdentity(PolicyTest.VIEWER, PolicyTest.DOMAIN, PolicyTest.GROUP).build();
        Assert.assertEquals(ImmutableMap.of(PolicyTest.VIEWER, ImmutableSet.of(PolicyTest.SERVICE_ACCOUNT, PolicyTest.DOMAIN, PolicyTest.GROUP)), policy.getBindings());
        Assert.assertNull(policy.getEtag());
        Assert.assertEquals(0, policy.getVersion());
        policy = Policy.newBuilder().removeIdentity(PolicyTest.VIEWER, PolicyTest.USER).addIdentity(PolicyTest.OWNER, PolicyTest.USER, PolicyTest.SERVICE_ACCOUNT).addIdentity(PolicyTest.EDITOR, PolicyTest.GROUP).removeIdentity(PolicyTest.EDITOR, PolicyTest.GROUP).build();
        Assert.assertEquals(ImmutableMap.of(PolicyTest.OWNER, ImmutableSet.of(PolicyTest.USER, PolicyTest.SERVICE_ACCOUNT)), policy.getBindings());
        Assert.assertNull(policy.getEtag());
        Assert.assertEquals(0, policy.getVersion());
    }

    @Test
    public void testIllegalPolicies() {
        try {
            Policy.newBuilder().addIdentity(null, PolicyTest.USER);
            Assert.fail("Null role should cause exception.");
        } catch (NullPointerException ex) {
            Assert.assertEquals("The role cannot be null.", ex.getMessage());
        }
        try {
            Policy.newBuilder().addIdentity(PolicyTest.VIEWER, null, PolicyTest.USER);
            Assert.fail("Null identity should cause exception.");
        } catch (NullPointerException ex) {
            Assert.assertEquals("Null identities are not permitted.", ex.getMessage());
        }
        try {
            Policy.newBuilder().addIdentity(PolicyTest.VIEWER, PolicyTest.USER, ((Identity[]) (null)));
            Assert.fail("Null identity should cause exception.");
        } catch (NullPointerException ex) {
            Assert.assertEquals("Null identities are not permitted.", ex.getMessage());
        }
        try {
            Policy.newBuilder().setBindings(null);
            Assert.fail("Null bindings map should cause exception.");
        } catch (NullPointerException ex) {
            Assert.assertEquals("The provided map of bindings cannot be null.", ex.getMessage());
        }
        try {
            Map<Role, Set<Identity>> bindings = new HashMap<>();
            bindings.put(PolicyTest.VIEWER, null);
            Policy.newBuilder().setBindings(bindings);
            Assert.fail("Null set of identities should cause exception.");
        } catch (NullPointerException ex) {
            Assert.assertEquals("A role cannot be assigned to a null set of identities.", ex.getMessage());
        }
        try {
            Map<Role, Set<Identity>> bindings = new HashMap<>();
            Set<Identity> identities = new HashSet<>();
            identities.add(null);
            bindings.put(PolicyTest.VIEWER, identities);
            Policy.newBuilder().setBindings(bindings);
            Assert.fail("Null identity should cause exception.");
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("Null identities are not permitted.", ex.getMessage());
        }
    }

    @Test
    public void testEqualsHashCode() {
        Assert.assertNotNull(PolicyTest.FULL_POLICY);
        Policy emptyPolicy = Policy.newBuilder().build();
        Policy anotherPolicy = Policy.newBuilder().build();
        Assert.assertEquals(emptyPolicy, anotherPolicy);
        Assert.assertEquals(emptyPolicy.hashCode(), anotherPolicy.hashCode());
        Assert.assertNotEquals(PolicyTest.FULL_POLICY, PolicyTest.SIMPLE_POLICY);
        Assert.assertNotEquals(PolicyTest.FULL_POLICY.hashCode(), PolicyTest.SIMPLE_POLICY.hashCode());
        Policy copy = PolicyTest.SIMPLE_POLICY.toBuilder().build();
        Assert.assertEquals(PolicyTest.SIMPLE_POLICY, copy);
        Assert.assertEquals(PolicyTest.SIMPLE_POLICY.hashCode(), copy.hashCode());
    }

    @Test
    public void testBindings() {
        Assert.assertTrue(Policy.newBuilder().build().getBindings().isEmpty());
        Assert.assertEquals(PolicyTest.BINDINGS, PolicyTest.SIMPLE_POLICY.getBindings());
    }

    @Test
    public void testEtag() {
        Assert.assertNull(PolicyTest.SIMPLE_POLICY.getEtag());
        Assert.assertEquals("etag", PolicyTest.FULL_POLICY.getEtag());
    }

    @Test
    public void testVersion() {
        Assert.assertEquals(0, PolicyTest.SIMPLE_POLICY.getVersion());
        Assert.assertEquals(1, PolicyTest.FULL_POLICY.getVersion());
    }

    @Test
    public void testDefaultMarshaller() {
        DefaultMarshaller marshaller = new DefaultMarshaller();
        Policy emptyPolicy = Policy.newBuilder().build();
        Assert.assertEquals(emptyPolicy, marshaller.fromPb(marshaller.toPb(emptyPolicy)));
        Assert.assertEquals(PolicyTest.SIMPLE_POLICY, marshaller.fromPb(marshaller.toPb(PolicyTest.SIMPLE_POLICY)));
        Assert.assertEquals(PolicyTest.FULL_POLICY, marshaller.fromPb(marshaller.toPb(PolicyTest.FULL_POLICY)));
        com.google.iam.v1.Policy policyPb = com.google.iam.v1.Policy.getDefaultInstance();
        Policy policy = marshaller.fromPb(policyPb);
        Assert.assertTrue(policy.getBindings().isEmpty());
        Assert.assertNull(policy.getEtag());
        Assert.assertEquals(0, policy.getVersion());
    }
}

