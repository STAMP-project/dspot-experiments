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


import Identity.Type.ALL_AUTHENTICATED_USERS;
import Identity.Type.ALL_USERS;
import Identity.Type.DOMAIN;
import Identity.Type.GROUP;
import Identity.Type.PROJECT_EDITOR;
import Identity.Type.PROJECT_OWNER;
import Identity.Type.PROJECT_VIEWER;
import Identity.Type.SERVICE_ACCOUNT;
import Identity.Type.USER;
import org.junit.Assert;
import org.junit.Test;


public class IdentityTest {
    private static final Identity ALL_USERS = Identity.allUsers();

    private static final Identity ALL_AUTH_USERS = Identity.allAuthenticatedUsers();

    private static final Identity USER = Identity.user("abc@gmail.com");

    private static final Identity SERVICE_ACCOUNT = Identity.serviceAccount("service-account@gmail.com");

    private static final Identity GROUP = Identity.group("group@gmail.com");

    private static final Identity DOMAIN = Identity.domain("google.com");

    private static final Identity PROJECT_OWNER = Identity.projectOwner("my-sample-project");

    private static final Identity PROJECT_EDITOR = Identity.projectEditor("my-sample-project");

    private static final Identity PROJECT_VIEWER = Identity.projectViewer("my-sample-project");

    @Test
    public void testAllUsers() {
        Assert.assertEquals(Identity.Type.ALL_USERS, IdentityTest.ALL_USERS.getType());
        Assert.assertNull(IdentityTest.ALL_USERS.getValue());
    }

    @Test
    public void testAllAuthenticatedUsers() {
        Assert.assertEquals(ALL_AUTHENTICATED_USERS, IdentityTest.ALL_AUTH_USERS.getType());
        Assert.assertNull(IdentityTest.ALL_AUTH_USERS.getValue());
    }

    @Test
    public void testUser() {
        Assert.assertEquals(Identity.Type.USER, IdentityTest.USER.getType());
        Assert.assertEquals("abc@gmail.com", IdentityTest.USER.getValue());
    }

    @Test(expected = NullPointerException.class)
    public void testUserNullEmail() {
        Identity.user(null);
    }

    @Test
    public void testServiceAccount() {
        Assert.assertEquals(Identity.Type.SERVICE_ACCOUNT, IdentityTest.SERVICE_ACCOUNT.getType());
        Assert.assertEquals("service-account@gmail.com", IdentityTest.SERVICE_ACCOUNT.getValue());
    }

    @Test(expected = NullPointerException.class)
    public void testServiceAccountNullEmail() {
        Identity.serviceAccount(null);
    }

    @Test
    public void testGroup() {
        Assert.assertEquals(Identity.Type.GROUP, IdentityTest.GROUP.getType());
        Assert.assertEquals("group@gmail.com", IdentityTest.GROUP.getValue());
    }

    @Test(expected = NullPointerException.class)
    public void testGroupNullEmail() {
        Identity.group(null);
    }

    @Test
    public void testDomain() {
        Assert.assertEquals(Identity.Type.DOMAIN, IdentityTest.DOMAIN.getType());
        Assert.assertEquals("google.com", IdentityTest.DOMAIN.getValue());
    }

    @Test(expected = NullPointerException.class)
    public void testDomainNullId() {
        Identity.domain(null);
    }

    @Test
    public void testProjectOwner() {
        Assert.assertEquals(Identity.Type.PROJECT_OWNER, IdentityTest.PROJECT_OWNER.getType());
        Assert.assertEquals("my-sample-project", IdentityTest.PROJECT_OWNER.getValue());
    }

    @Test(expected = NullPointerException.class)
    public void testProjectOwnerNullId() {
        Identity.projectOwner(null);
    }

    @Test
    public void testProjectEditor() {
        Assert.assertEquals(Identity.Type.PROJECT_EDITOR, IdentityTest.PROJECT_EDITOR.getType());
        Assert.assertEquals("my-sample-project", IdentityTest.PROJECT_EDITOR.getValue());
    }

    @Test(expected = NullPointerException.class)
    public void testProjectEditorNullId() {
        Identity.projectEditor(null);
    }

    @Test
    public void testProjectViewer() {
        Assert.assertEquals(Identity.Type.PROJECT_VIEWER, IdentityTest.PROJECT_VIEWER.getType());
        Assert.assertEquals("my-sample-project", IdentityTest.PROJECT_VIEWER.getValue());
    }

    @Test(expected = NullPointerException.class)
    public void testProjectViewerNullId() {
        Identity.projectViewer(null);
    }

    @Test
    public void testIdentityToAndFromPb() {
        compareIdentities(IdentityTest.ALL_USERS, Identity.valueOf(IdentityTest.ALL_USERS.strValue()));
        compareIdentities(IdentityTest.ALL_AUTH_USERS, Identity.valueOf(IdentityTest.ALL_AUTH_USERS.strValue()));
        compareIdentities(IdentityTest.USER, Identity.valueOf(IdentityTest.USER.strValue()));
        compareIdentities(IdentityTest.SERVICE_ACCOUNT, Identity.valueOf(IdentityTest.SERVICE_ACCOUNT.strValue()));
        compareIdentities(IdentityTest.GROUP, Identity.valueOf(IdentityTest.GROUP.strValue()));
        compareIdentities(IdentityTest.DOMAIN, Identity.valueOf(IdentityTest.DOMAIN.strValue()));
        compareIdentities(IdentityTest.PROJECT_OWNER, Identity.valueOf(IdentityTest.PROJECT_OWNER.strValue()));
        compareIdentities(IdentityTest.PROJECT_EDITOR, Identity.valueOf(IdentityTest.PROJECT_EDITOR.strValue()));
        compareIdentities(IdentityTest.PROJECT_VIEWER, Identity.valueOf(IdentityTest.PROJECT_VIEWER.strValue()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfEmpty() {
        Identity.valueOf("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfThreePart() {
        Identity.valueOf("a:b:c");
    }

    @Test
    public void testUnrecognizedToString() {
        Assert.assertEquals("a:b", Identity.valueOf("a:b").strValue());
    }
}

