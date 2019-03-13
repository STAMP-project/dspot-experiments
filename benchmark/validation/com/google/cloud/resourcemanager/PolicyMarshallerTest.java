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
package com.google.cloud.resourcemanager;


import PolicyMarshaller.INSTANCE;
import com.google.cloud.Identity;
import com.google.cloud.Policy;
import com.google.cloud.Role;
import org.junit.Assert;
import org.junit.Test;


public class PolicyMarshallerTest {
    private static final Identity ALL_USERS = Identity.allUsers();

    private static final Identity ALL_AUTH_USERS = Identity.allAuthenticatedUsers();

    private static final Identity USER = Identity.user("abc@gmail.com");

    private static final Identity SERVICE_ACCOUNT = Identity.serviceAccount("service-account@gmail.com");

    private static final Identity GROUP = Identity.group("group@gmail.com");

    private static final Identity DOMAIN = Identity.domain("google.com");

    private static final Role VIEWER = Role.viewer();

    private static final Role EDITOR = Role.editor();

    private static final Role OWNER = Role.owner();

    private static final Role SOME_ROLE = Role.of("roles/some-role");

    private static final Policy SIMPLE_POLICY = Policy.newBuilder().addIdentity(PolicyMarshallerTest.OWNER, PolicyMarshallerTest.USER).addIdentity(PolicyMarshallerTest.VIEWER, PolicyMarshallerTest.ALL_USERS).addIdentity(PolicyMarshallerTest.EDITOR, PolicyMarshallerTest.ALL_AUTH_USERS, PolicyMarshallerTest.DOMAIN).addIdentity(PolicyMarshallerTest.SOME_ROLE, PolicyMarshallerTest.SERVICE_ACCOUNT, PolicyMarshallerTest.GROUP).build();

    private static final Policy FULL_POLICY = INSTANCE.fromPb(INSTANCE.toPb(PolicyMarshallerTest.SIMPLE_POLICY).setEtag("etag").setVersion(1));

    @Test
    public void testToAndFromPb() {
        Assert.assertEquals(PolicyMarshallerTest.FULL_POLICY, INSTANCE.fromPb(INSTANCE.toPb(PolicyMarshallerTest.FULL_POLICY)));
        Assert.assertEquals(PolicyMarshallerTest.SIMPLE_POLICY, INSTANCE.fromPb(INSTANCE.toPb(PolicyMarshallerTest.SIMPLE_POLICY)));
        com.google.api.services.cloudresourcemanager.model.Policy policyPb = new com.google.api.services.cloudresourcemanager.model.Policy();
        Policy policy = INSTANCE.fromPb(policyPb);
        Assert.assertTrue(policy.getBindings().isEmpty());
        Assert.assertNull(policy.getEtag());
        Assert.assertEquals(0, policy.getVersion());
    }
}

