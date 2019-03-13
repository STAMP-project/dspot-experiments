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
package com.google.cloud.bigquery;


import Dataset.Access;
import Role.READER;
import Type.DOMAIN;
import Type.GROUP;
import Type.USER;
import Type.VIEW;
import com.google.api.services.bigquery.model.Dataset;
import com.google.cloud.bigquery.Acl.Domain;
import com.google.cloud.bigquery.Acl.Entity;
import com.google.cloud.bigquery.Acl.Group;
import com.google.cloud.bigquery.Acl.User;
import com.google.cloud.bigquery.Acl.View;
import org.junit.Assert;
import org.junit.Test;


public class AclTest {
    @Test
    public void testDomainEntity() {
        Domain entity = new Domain("d1");
        Assert.assertEquals("d1", entity.getDomain());
        Assert.assertEquals(DOMAIN, entity.getType());
        Dataset.Access pb = entity.toPb();
        Assert.assertEquals(entity, Entity.fromPb(pb));
    }

    @Test
    public void testGroupEntity() {
        Group entity = new Group("g1");
        Assert.assertEquals("g1", entity.getIdentifier());
        Assert.assertEquals(GROUP, entity.getType());
        Dataset.Access pb = entity.toPb();
        Assert.assertEquals(entity, Entity.fromPb(pb));
    }

    @Test
    public void testSpecialGroupEntity() {
        Group entity = Group.ofAllAuthenticatedUsers();
        Assert.assertEquals("allAuthenticatedUsers", entity.getIdentifier());
        entity = Group.ofProjectWriters();
        Assert.assertEquals("projectWriters", entity.getIdentifier());
        entity = Group.ofProjectReaders();
        Assert.assertEquals("projectReaders", entity.getIdentifier());
        entity = Group.ofProjectOwners();
        Assert.assertEquals("projectOwners", entity.getIdentifier());
    }

    @Test
    public void testUserEntity() {
        User entity = new User("u1");
        Assert.assertEquals("u1", entity.getEmail());
        Assert.assertEquals(USER, entity.getType());
        Dataset.Access pb = entity.toPb();
        Assert.assertEquals(entity, Entity.fromPb(pb));
    }

    @Test
    public void testViewEntity() {
        TableId viewId = TableId.of("project", "dataset", "view");
        View entity = new View(viewId);
        Assert.assertEquals(viewId, entity.getId());
        Assert.assertEquals(VIEW, entity.getType());
        Dataset.Access pb = entity.toPb();
        Assert.assertEquals(entity, Entity.fromPb(pb));
    }

    @Test
    public void testOf() {
        Acl acl = Acl.of(Group.ofAllAuthenticatedUsers(), READER);
        Assert.assertEquals(Group.ofAllAuthenticatedUsers(), acl.getEntity());
        Assert.assertEquals(READER, acl.getRole());
        Dataset.Access pb = acl.toPb();
        Assert.assertEquals(acl, Acl.fromPb(pb));
        View view = new View(TableId.of("project", "dataset", "view"));
        acl = Acl.of(view);
        Assert.assertEquals(view, acl.getEntity());
        Assert.assertEquals(null, acl.getRole());
    }
}

