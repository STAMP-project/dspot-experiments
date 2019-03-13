/**
 * Copyright 2015 Google LLC
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
package com.google.cloud.storage;


import ProjectRole.VIEWERS;
import Role.READER;
import Type.DOMAIN;
import Type.GROUP;
import Type.PROJECT;
import Type.UNKNOWN;
import Type.USER;
import com.google.api.services.storage.model.BucketAccessControl;
import com.google.api.services.storage.model.ObjectAccessControl;
import com.google.cloud.storage.Acl.Domain;
import com.google.cloud.storage.Acl.Entity;
import com.google.cloud.storage.Acl.Group;
import com.google.cloud.storage.Acl.Project;
import com.google.cloud.storage.Acl.Project.ProjectRole;
import com.google.cloud.storage.Acl.RawEntity;
import com.google.cloud.storage.Acl.Role;
import com.google.cloud.storage.Acl.User;
import org.junit.Assert;
import org.junit.Test;


public class AclTest {
    private static final Role ROLE = Role.OWNER;

    private static final Entity ENTITY = User.ofAllAuthenticatedUsers();

    private static final String ETAG = "etag";

    private static final String ID = "id";

    private static final Acl ACL = Acl.newBuilder(AclTest.ENTITY, AclTest.ROLE).setEtag(AclTest.ETAG).setId(AclTest.ID).build();

    @Test
    public void testBuilder() {
        Assert.assertEquals(AclTest.ROLE, AclTest.ACL.getRole());
        Assert.assertEquals(AclTest.ENTITY, AclTest.ACL.getEntity());
        Assert.assertEquals(AclTest.ETAG, AclTest.ACL.getEtag());
        Assert.assertEquals(AclTest.ID, AclTest.ACL.getId());
    }

    @Test
    public void testToBuilder() {
        Assert.assertEquals(AclTest.ACL, AclTest.ACL.toBuilder().build());
        Acl acl = AclTest.ACL.toBuilder().setEtag("otherEtag").setId("otherId").setRole(READER).setEntity(User.ofAllUsers()).build();
        Assert.assertEquals(READER, acl.getRole());
        Assert.assertEquals(User.ofAllUsers(), acl.getEntity());
        Assert.assertEquals("otherEtag", acl.getEtag());
        Assert.assertEquals("otherId", acl.getId());
    }

    @Test
    public void testToAndFromPb() {
        Assert.assertEquals(AclTest.ACL, Acl.fromPb(AclTest.ACL.toBucketPb()));
        Assert.assertEquals(AclTest.ACL, Acl.fromPb(AclTest.ACL.toObjectPb()));
    }

    @Test
    public void testDomainEntity() {
        Domain acl = new Domain("d1");
        Assert.assertEquals("d1", acl.getDomain());
        Assert.assertEquals(DOMAIN, acl.getType());
        String pb = acl.toPb();
        Assert.assertEquals(acl, Entity.fromPb(pb));
    }

    @Test
    public void testGroupEntity() {
        Group acl = new Group("g1");
        Assert.assertEquals("g1", acl.getEmail());
        Assert.assertEquals(GROUP, acl.getType());
        String pb = acl.toPb();
        Assert.assertEquals(acl, Entity.fromPb(pb));
    }

    @Test
    public void testUserEntity() {
        User acl = new User("u1");
        Assert.assertEquals("u1", acl.getEmail());
        Assert.assertEquals(USER, acl.getType());
        String pb = acl.toPb();
        Assert.assertEquals(acl, Entity.fromPb(pb));
    }

    @Test
    public void testProjectEntity() {
        Project acl = new Project(ProjectRole.VIEWERS, "p1");
        Assert.assertEquals(VIEWERS, acl.getProjectRole());
        Assert.assertEquals("p1", acl.getProjectId());
        Assert.assertEquals(PROJECT, acl.getType());
        String pb = acl.toPb();
        Assert.assertEquals(acl, Entity.fromPb(pb));
    }

    @Test
    public void testRawEntity() {
        Entity acl = new RawEntity("bla");
        Assert.assertEquals("bla", acl.getValue());
        Assert.assertEquals(UNKNOWN, acl.getType());
        String pb = acl.toPb();
        Assert.assertEquals(acl, Entity.fromPb(pb));
    }

    @Test
    public void testOf() {
        Acl acl = Acl.of(User.ofAllUsers(), READER);
        Assert.assertEquals(User.ofAllUsers(), acl.getEntity());
        Assert.assertEquals(READER, acl.getRole());
        ObjectAccessControl objectPb = acl.toObjectPb();
        Assert.assertEquals(acl, Acl.fromPb(objectPb));
        BucketAccessControl bucketPb = acl.toBucketPb();
        Assert.assertEquals(acl, Acl.fromPb(bucketPb));
    }
}

