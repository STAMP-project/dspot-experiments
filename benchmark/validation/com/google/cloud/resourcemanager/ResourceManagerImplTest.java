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
package com.google.cloud.resourcemanager;


import ProjectField.CREATE_TIME;
import ProjectField.LABELS;
import ProjectField.NAME;
import ProjectInfo.State.ACTIVE;
import ProjectInfo.State.DELETE_REQUESTED;
import com.google.api.gax.paging.Page;
import com.google.cloud.Identity;
import com.google.cloud.Policy;
import com.google.cloud.Role;
import com.google.cloud.resourcemanager.ProjectInfo.ResourceId;
import com.google.cloud.resourcemanager.ResourceManager.ProjectGetOption;
import com.google.cloud.resourcemanager.ResourceManager.ProjectListOption;
import com.google.cloud.resourcemanager.spi.ResourceManagerRpcFactory;
import com.google.cloud.resourcemanager.spi.v1beta1.ResourceManagerRpc;
import com.google.cloud.resourcemanager.testing.LocalResourceManagerHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class ResourceManagerImplTest {
    private static final LocalResourceManagerHelper RESOURCE_MANAGER_HELPER = LocalResourceManagerHelper.create();

    private static final ResourceManager RESOURCE_MANAGER = ResourceManagerImplTest.RESOURCE_MANAGER_HELPER.getOptions().getService();

    private static final ProjectGetOption GET_FIELDS = ProjectGetOption.fields(NAME, CREATE_TIME);

    private static final ProjectListOption LIST_FIELDS = ProjectListOption.fields(NAME, LABELS);

    private static final ProjectListOption LIST_FILTER = ProjectListOption.filter("id:* name:myProject labels.color:blue LABELS.SIZE:*");

    private static final ProjectInfo PARTIAL_PROJECT = ProjectInfo.newBuilder("partial-project").build();

    private static final ResourceId PARENT = new ResourceId("id", "type");

    private static final ProjectInfo COMPLETE_PROJECT = ProjectInfo.newBuilder("complete-project").setName("name").setLabels(ImmutableMap.of("k1", "v1")).setParent(ResourceManagerImplTest.PARENT).build();

    private static final Map<ResourceManagerRpc.Option, ?> EMPTY_RPC_OPTIONS = ImmutableMap.of();

    private static final Policy POLICY = Policy.newBuilder().addIdentity(Role.owner(), Identity.user("me@gmail.com")).addIdentity(Role.editor(), Identity.serviceAccount("serviceaccount@gmail.com")).build();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testCreate() {
        Project returnedProject = ResourceManagerImplTest.RESOURCE_MANAGER.create(ResourceManagerImplTest.PARTIAL_PROJECT);
        compareReadWriteFields(ResourceManagerImplTest.PARTIAL_PROJECT, returnedProject);
        Assert.assertEquals(ACTIVE, returnedProject.getState());
        Assert.assertNull(returnedProject.getName());
        Assert.assertNull(returnedProject.getParent());
        Assert.assertNotNull(returnedProject.getProjectNumber());
        Assert.assertNotNull(returnedProject.getCreateTimeMillis());
        Assert.assertSame(ResourceManagerImplTest.RESOURCE_MANAGER, returnedProject.getResourceManager());
        try {
            ResourceManagerImplTest.RESOURCE_MANAGER.create(ResourceManagerImplTest.PARTIAL_PROJECT);
            Assert.fail("Should fail, project already exists.");
        } catch (ResourceManagerException e) {
            Assert.assertEquals(409, e.getCode());
            Assert.assertTrue(((e.getMessage().startsWith("A project with the same project ID")) && (e.getMessage().endsWith("already exists."))));
        }
        returnedProject = ResourceManagerImplTest.RESOURCE_MANAGER.create(ResourceManagerImplTest.COMPLETE_PROJECT);
        compareReadWriteFields(ResourceManagerImplTest.COMPLETE_PROJECT, returnedProject);
        Assert.assertEquals(ACTIVE, returnedProject.getState());
        Assert.assertNotNull(returnedProject.getProjectNumber());
        Assert.assertNotNull(returnedProject.getCreateTimeMillis());
        Assert.assertSame(ResourceManagerImplTest.RESOURCE_MANAGER, returnedProject.getResourceManager());
    }

    @Test
    public void testDelete() {
        ResourceManagerImplTest.RESOURCE_MANAGER.create(ResourceManagerImplTest.COMPLETE_PROJECT);
        ResourceManagerImplTest.RESOURCE_MANAGER.delete(ResourceManagerImplTest.COMPLETE_PROJECT.getProjectId());
        Assert.assertEquals(DELETE_REQUESTED, ResourceManagerImplTest.RESOURCE_MANAGER.get(ResourceManagerImplTest.COMPLETE_PROJECT.getProjectId()).getState());
        try {
            ResourceManagerImplTest.RESOURCE_MANAGER.delete("some-nonexistant-project-id");
            Assert.fail("Should fail because the project doesn't exist.");
        } catch (ResourceManagerException e) {
            Assert.assertEquals(403, e.getCode());
            Assert.assertTrue(e.getMessage().contains("not found."));
        }
    }

    @Test
    public void testGet() {
        ResourceManagerImplTest.RESOURCE_MANAGER.create(ResourceManagerImplTest.COMPLETE_PROJECT);
        Project returnedProject = ResourceManagerImplTest.RESOURCE_MANAGER.get(ResourceManagerImplTest.COMPLETE_PROJECT.getProjectId());
        compareReadWriteFields(ResourceManagerImplTest.COMPLETE_PROJECT, returnedProject);
        Assert.assertEquals(ResourceManagerImplTest.RESOURCE_MANAGER, returnedProject.getResourceManager());
        ResourceManagerImplTest.RESOURCE_MANAGER_HELPER.removeProject(ResourceManagerImplTest.COMPLETE_PROJECT.getProjectId());
        Assert.assertNull(ResourceManagerImplTest.RESOURCE_MANAGER.get(ResourceManagerImplTest.COMPLETE_PROJECT.getProjectId()));
    }

    @Test
    public void testGetWithOptions() {
        Project originalProject = ResourceManagerImplTest.RESOURCE_MANAGER.create(ResourceManagerImplTest.COMPLETE_PROJECT);
        Project returnedProject = ResourceManagerImplTest.RESOURCE_MANAGER.get(ResourceManagerImplTest.COMPLETE_PROJECT.getProjectId(), ResourceManagerImplTest.GET_FIELDS);
        Assert.assertFalse(ResourceManagerImplTest.COMPLETE_PROJECT.equals(returnedProject));
        Assert.assertEquals(ResourceManagerImplTest.COMPLETE_PROJECT.getProjectId(), returnedProject.getProjectId());
        Assert.assertEquals(ResourceManagerImplTest.COMPLETE_PROJECT.getName(), returnedProject.getName());
        Assert.assertEquals(originalProject.getCreateTimeMillis(), returnedProject.getCreateTimeMillis());
        Assert.assertNull(returnedProject.getParent());
        Assert.assertNull(returnedProject.getProjectNumber());
        Assert.assertNull(returnedProject.getState());
        Assert.assertTrue(returnedProject.getLabels().isEmpty());
        Assert.assertEquals(ResourceManagerImplTest.RESOURCE_MANAGER, originalProject.getResourceManager());
        Assert.assertEquals(ResourceManagerImplTest.RESOURCE_MANAGER, returnedProject.getResourceManager());
    }

    @Test
    public void testList() {
        Page<Project> projects = ResourceManagerImplTest.RESOURCE_MANAGER.list();
        Assert.assertFalse(projects.getValues().iterator().hasNext());
        ResourceManagerImplTest.RESOURCE_MANAGER.create(ResourceManagerImplTest.PARTIAL_PROJECT);
        ResourceManagerImplTest.RESOURCE_MANAGER.create(ResourceManagerImplTest.COMPLETE_PROJECT);
        for (Project p : ResourceManagerImplTest.RESOURCE_MANAGER.list().getValues()) {
            if (p.getProjectId().equals(ResourceManagerImplTest.PARTIAL_PROJECT.getProjectId())) {
                compareReadWriteFields(ResourceManagerImplTest.PARTIAL_PROJECT, p);
            } else
                if (p.getProjectId().equals(ResourceManagerImplTest.COMPLETE_PROJECT.getProjectId())) {
                    compareReadWriteFields(ResourceManagerImplTest.COMPLETE_PROJECT, p);
                } else {
                    Assert.fail("Some unexpected project returned by list.");
                }

            Assert.assertSame(ResourceManagerImplTest.RESOURCE_MANAGER, p.getResourceManager());
        }
    }

    @Test
    public void testListPaging() {
        ResourceManagerImplTest.RESOURCE_MANAGER.create(ResourceManagerImplTest.PARTIAL_PROJECT);
        ResourceManagerImplTest.RESOURCE_MANAGER.create(ResourceManagerImplTest.COMPLETE_PROJECT);
        Page<Project> page = ResourceManagerImplTest.RESOURCE_MANAGER.list(ProjectListOption.pageSize(1));
        Assert.assertNotNull(page.getNextPageToken());
        Iterator<Project> iterator = page.getValues().iterator();
        compareReadWriteFields(ResourceManagerImplTest.COMPLETE_PROJECT, iterator.next());
        Assert.assertFalse(iterator.hasNext());
        page = page.getNextPage();
        iterator = page.getValues().iterator();
        compareReadWriteFields(ResourceManagerImplTest.PARTIAL_PROJECT, iterator.next());
        Assert.assertFalse(iterator.hasNext());
        Assert.assertNull(page.getNextPageToken());
    }

    @Test
    public void testListFieldOptions() {
        ResourceManagerImplTest.RESOURCE_MANAGER.create(ResourceManagerImplTest.COMPLETE_PROJECT);
        Page<Project> projects = ResourceManagerImplTest.RESOURCE_MANAGER.list(ResourceManagerImplTest.LIST_FIELDS);
        Project returnedProject = projects.iterateAll().iterator().next();
        Assert.assertEquals(ResourceManagerImplTest.COMPLETE_PROJECT.getProjectId(), returnedProject.getProjectId());
        Assert.assertEquals(ResourceManagerImplTest.COMPLETE_PROJECT.getName(), returnedProject.getName());
        Assert.assertEquals(ResourceManagerImplTest.COMPLETE_PROJECT.getLabels(), returnedProject.getLabels());
        Assert.assertNull(returnedProject.getParent());
        Assert.assertNull(returnedProject.getProjectNumber());
        Assert.assertNull(returnedProject.getState());
        Assert.assertNull(returnedProject.getCreateTimeMillis());
        Assert.assertSame(ResourceManagerImplTest.RESOURCE_MANAGER, returnedProject.getResourceManager());
    }

    @Test
    public void testListPagingWithFieldOptions() {
        ResourceManagerImplTest.RESOURCE_MANAGER.create(ResourceManagerImplTest.PARTIAL_PROJECT);
        ResourceManagerImplTest.RESOURCE_MANAGER.create(ResourceManagerImplTest.COMPLETE_PROJECT);
        Page<Project> projects = ResourceManagerImplTest.RESOURCE_MANAGER.list(ResourceManagerImplTest.LIST_FIELDS, ProjectListOption.pageSize(1));
        Assert.assertNotNull(projects.getNextPageToken());
        Iterator<Project> iterator = projects.getValues().iterator();
        Project returnedProject = iterator.next();
        Assert.assertEquals(ResourceManagerImplTest.COMPLETE_PROJECT.getProjectId(), returnedProject.getProjectId());
        Assert.assertEquals(ResourceManagerImplTest.COMPLETE_PROJECT.getName(), returnedProject.getName());
        Assert.assertEquals(ResourceManagerImplTest.COMPLETE_PROJECT.getLabels(), returnedProject.getLabels());
        Assert.assertNull(returnedProject.getParent());
        Assert.assertNull(returnedProject.getProjectNumber());
        Assert.assertNull(returnedProject.getState());
        Assert.assertNull(returnedProject.getCreateTimeMillis());
        Assert.assertSame(ResourceManagerImplTest.RESOURCE_MANAGER, returnedProject.getResourceManager());
        Assert.assertFalse(iterator.hasNext());
        projects = projects.getNextPage();
        iterator = projects.getValues().iterator();
        returnedProject = iterator.next();
        Assert.assertEquals(ResourceManagerImplTest.PARTIAL_PROJECT.getProjectId(), returnedProject.getProjectId());
        Assert.assertEquals(ResourceManagerImplTest.PARTIAL_PROJECT.getName(), returnedProject.getName());
        Assert.assertEquals(ResourceManagerImplTest.PARTIAL_PROJECT.getLabels(), returnedProject.getLabels());
        Assert.assertNull(returnedProject.getParent());
        Assert.assertNull(returnedProject.getProjectNumber());
        Assert.assertNull(returnedProject.getState());
        Assert.assertNull(returnedProject.getCreateTimeMillis());
        Assert.assertSame(ResourceManagerImplTest.RESOURCE_MANAGER, returnedProject.getResourceManager());
        Assert.assertFalse(iterator.hasNext());
        Assert.assertNull(projects.getNextPageToken());
    }

    @Test
    public void testListFilterOptions() {
        ProjectInfo matchingProject = ProjectInfo.newBuilder("matching-project").setName("MyProject").setLabels(ImmutableMap.of("color", "blue", "size", "big")).build();
        ProjectInfo nonMatchingProject1 = ProjectInfo.newBuilder("non-matching-project1").setName("myProject").setLabels(ImmutableMap.of("color", "blue")).build();
        ProjectInfo nonMatchingProject2 = ProjectInfo.newBuilder("non-matching-project2").setName("myProj").setLabels(ImmutableMap.of("color", "blue", "size", "big")).build();
        ProjectInfo nonMatchingProject3 = ProjectInfo.newBuilder("non-matching-project3").build();
        ResourceManagerImplTest.RESOURCE_MANAGER.create(matchingProject);
        ResourceManagerImplTest.RESOURCE_MANAGER.create(nonMatchingProject1);
        ResourceManagerImplTest.RESOURCE_MANAGER.create(nonMatchingProject2);
        ResourceManagerImplTest.RESOURCE_MANAGER.create(nonMatchingProject3);
        for (Project p : ResourceManagerImplTest.RESOURCE_MANAGER.list(ResourceManagerImplTest.LIST_FILTER).getValues()) {
            Assert.assertFalse(p.equals(nonMatchingProject1));
            Assert.assertFalse(p.equals(nonMatchingProject2));
            compareReadWriteFields(matchingProject, p);
            Assert.assertSame(ResourceManagerImplTest.RESOURCE_MANAGER, p.getResourceManager());
        }
    }

    @Test
    public void testReplace() {
        ProjectInfo createdProject = ResourceManagerImplTest.RESOURCE_MANAGER.create(ResourceManagerImplTest.COMPLETE_PROJECT);
        Map<String, String> newLabels = ImmutableMap.of("new k1", "new v1");
        ProjectInfo anotherCompleteProject = ProjectInfo.newBuilder(ResourceManagerImplTest.COMPLETE_PROJECT.getProjectId()).setLabels(newLabels).setProjectNumber(987654321L).setCreateTimeMillis(230682061315L).setState(DELETE_REQUESTED).setParent(createdProject.getParent()).build();
        Project returnedProject = ResourceManagerImplTest.RESOURCE_MANAGER.replace(anotherCompleteProject);
        compareReadWriteFields(anotherCompleteProject, returnedProject);
        Assert.assertEquals(createdProject.getProjectNumber(), returnedProject.getProjectNumber());
        Assert.assertEquals(createdProject.getCreateTimeMillis(), returnedProject.getCreateTimeMillis());
        Assert.assertEquals(createdProject.getState(), returnedProject.getState());
        Assert.assertEquals(ResourceManagerImplTest.RESOURCE_MANAGER, returnedProject.getResourceManager());
        ProjectInfo nonexistantProject = ProjectInfo.newBuilder("some-project-id-that-does-not-exist").build();
        try {
            ResourceManagerImplTest.RESOURCE_MANAGER.replace(nonexistantProject);
            Assert.fail("Should fail because the project doesn't exist.");
        } catch (ResourceManagerException e) {
            Assert.assertEquals(403, e.getCode());
            Assert.assertTrue(e.getMessage().contains("the project was not found"));
        }
    }

    @Test
    public void testUndelete() {
        ResourceManagerImplTest.RESOURCE_MANAGER.create(ResourceManagerImplTest.COMPLETE_PROJECT);
        ResourceManagerImplTest.RESOURCE_MANAGER.delete(ResourceManagerImplTest.COMPLETE_PROJECT.getProjectId());
        Assert.assertEquals(DELETE_REQUESTED, ResourceManagerImplTest.RESOURCE_MANAGER.get(ResourceManagerImplTest.COMPLETE_PROJECT.getProjectId()).getState());
        ResourceManagerImplTest.RESOURCE_MANAGER.undelete(ResourceManagerImplTest.COMPLETE_PROJECT.getProjectId());
        ProjectInfo revivedProject = ResourceManagerImplTest.RESOURCE_MANAGER.get(ResourceManagerImplTest.COMPLETE_PROJECT.getProjectId());
        compareReadWriteFields(ResourceManagerImplTest.COMPLETE_PROJECT, revivedProject);
        Assert.assertEquals(ACTIVE, revivedProject.getState());
        try {
            ResourceManagerImplTest.RESOURCE_MANAGER.undelete("invalid-project-id");
            Assert.fail("Should fail because the project doesn't exist.");
        } catch (ResourceManagerException e) {
            Assert.assertEquals(403, e.getCode());
            Assert.assertTrue(e.getMessage().contains("the project was not found"));
        }
    }

    @Test
    public void testGetPolicy() {
        Assert.assertNull(ResourceManagerImplTest.RESOURCE_MANAGER.getPolicy(ResourceManagerImplTest.COMPLETE_PROJECT.getProjectId()));
        ResourceManagerImplTest.RESOURCE_MANAGER.create(ResourceManagerImplTest.COMPLETE_PROJECT);
        ResourceManagerImplTest.RESOURCE_MANAGER.replacePolicy(ResourceManagerImplTest.COMPLETE_PROJECT.getProjectId(), ResourceManagerImplTest.POLICY);
        Policy retrieved = ResourceManagerImplTest.RESOURCE_MANAGER.getPolicy(ResourceManagerImplTest.COMPLETE_PROJECT.getProjectId());
        Assert.assertEquals(ResourceManagerImplTest.POLICY.getBindings(), retrieved.getBindings());
        Assert.assertNotNull(retrieved.getEtag());
        Assert.assertEquals(0, retrieved.getVersion());
    }

    @Test
    public void testReplacePolicy() {
        try {
            ResourceManagerImplTest.RESOURCE_MANAGER.replacePolicy("nonexistent-project", ResourceManagerImplTest.POLICY);
            Assert.fail("Project doesn't exist.");
        } catch (ResourceManagerException e) {
            Assert.assertEquals(403, e.getCode());
            Assert.assertTrue(e.getMessage().endsWith("project was not found."));
        }
        ResourceManagerImplTest.RESOURCE_MANAGER.create(ResourceManagerImplTest.PARTIAL_PROJECT);
        Policy oldPolicy = ResourceManagerImplTest.RESOURCE_MANAGER.getPolicy(ResourceManagerImplTest.PARTIAL_PROJECT.getProjectId());
        ResourceManagerImplTest.RESOURCE_MANAGER.replacePolicy(ResourceManagerImplTest.PARTIAL_PROJECT.getProjectId(), ResourceManagerImplTest.POLICY);
        try {
            ResourceManagerImplTest.RESOURCE_MANAGER.replacePolicy(ResourceManagerImplTest.PARTIAL_PROJECT.getProjectId(), oldPolicy);
            Assert.fail("Policy with an invalid etag didn't cause error.");
        } catch (ResourceManagerException e) {
            Assert.assertEquals(409, e.getCode());
            Assert.assertTrue(e.getMessage().contains("Policy etag mismatch"));
        }
        String originalEtag = ResourceManagerImplTest.RESOURCE_MANAGER.getPolicy(ResourceManagerImplTest.PARTIAL_PROJECT.getProjectId()).getEtag();
        Policy newPolicy = ResourceManagerImplTest.RESOURCE_MANAGER.replacePolicy(ResourceManagerImplTest.PARTIAL_PROJECT.getProjectId(), ResourceManagerImplTest.POLICY);
        Assert.assertEquals(ResourceManagerImplTest.POLICY.getBindings(), newPolicy.getBindings());
        Assert.assertNotNull(newPolicy.getEtag());
        Assert.assertNotEquals(originalEtag, newPolicy.getEtag());
    }

    @Test
    public void testTestPermissions() {
        List<String> permissions = ImmutableList.of("resourcemanager.projects.get");
        try {
            ResourceManagerImplTest.RESOURCE_MANAGER.testPermissions("nonexistent-project", permissions);
            Assert.fail("Nonexistent project");
        } catch (ResourceManagerException e) {
            Assert.assertEquals(403, e.getCode());
            Assert.assertEquals("Project nonexistent-project not found.", e.getMessage());
        }
        ResourceManagerImplTest.RESOURCE_MANAGER.create(ResourceManagerImplTest.PARTIAL_PROJECT);
        Assert.assertEquals(ImmutableList.of(true), ResourceManagerImplTest.RESOURCE_MANAGER.testPermissions(ResourceManagerImplTest.PARTIAL_PROJECT.getProjectId(), permissions));
    }

    @Test
    public void testRetryableException() {
        ResourceManagerRpcFactory rpcFactoryMock = EasyMock.createMock(ResourceManagerRpcFactory.class);
        ResourceManagerRpc resourceManagerRpcMock = EasyMock.createMock(ResourceManagerRpc.class);
        EasyMock.expect(rpcFactoryMock.create(EasyMock.anyObject(ResourceManagerOptions.class))).andReturn(resourceManagerRpcMock);
        EasyMock.replay(rpcFactoryMock);
        ResourceManager resourceManagerMock = ResourceManagerOptions.newBuilder().setServiceRpcFactory(rpcFactoryMock).build().getService();
        EasyMock.expect(resourceManagerRpcMock.get(ResourceManagerImplTest.PARTIAL_PROJECT.getProjectId(), ResourceManagerImplTest.EMPTY_RPC_OPTIONS)).andThrow(new ResourceManagerException(500, "Internal Error")).andReturn(ResourceManagerImplTest.PARTIAL_PROJECT.toPb());
        EasyMock.replay(resourceManagerRpcMock);
        Project returnedProject = resourceManagerMock.get(ResourceManagerImplTest.PARTIAL_PROJECT.getProjectId());
        Assert.assertEquals(new Project(resourceManagerMock, new ProjectInfo.BuilderImpl(ResourceManagerImplTest.PARTIAL_PROJECT)), returnedProject);
    }

    @Test
    public void testNonRetryableException() {
        ResourceManagerRpcFactory rpcFactoryMock = EasyMock.createMock(ResourceManagerRpcFactory.class);
        ResourceManagerRpc resourceManagerRpcMock = EasyMock.createMock(ResourceManagerRpc.class);
        EasyMock.expect(rpcFactoryMock.create(EasyMock.anyObject(ResourceManagerOptions.class))).andReturn(resourceManagerRpcMock);
        EasyMock.replay(rpcFactoryMock);
        ResourceManager resourceManagerMock = ResourceManagerOptions.newBuilder().setServiceRpcFactory(rpcFactoryMock).build().getService();
        EasyMock.expect(resourceManagerRpcMock.get(ResourceManagerImplTest.PARTIAL_PROJECT.getProjectId(), ResourceManagerImplTest.EMPTY_RPC_OPTIONS)).andThrow(new ResourceManagerException(403, (("Project " + (ResourceManagerImplTest.PARTIAL_PROJECT.getProjectId())) + " not found."))).once();
        EasyMock.replay(resourceManagerRpcMock);
        thrown.expect(ResourceManagerException.class);
        thrown.expectMessage((("Project " + (ResourceManagerImplTest.PARTIAL_PROJECT.getProjectId())) + " not found."));
        resourceManagerMock.get(ResourceManagerImplTest.PARTIAL_PROJECT.getProjectId());
    }

    @Test
    public void testRuntimeException() {
        ResourceManagerRpcFactory rpcFactoryMock = EasyMock.createMock(ResourceManagerRpcFactory.class);
        ResourceManagerRpc resourceManagerRpcMock = EasyMock.createMock(ResourceManagerRpc.class);
        EasyMock.expect(rpcFactoryMock.create(EasyMock.anyObject(ResourceManagerOptions.class))).andReturn(resourceManagerRpcMock);
        EasyMock.replay(rpcFactoryMock);
        ResourceManager resourceManagerMock = ResourceManagerOptions.newBuilder().setServiceRpcFactory(rpcFactoryMock).build().getService();
        String exceptionMessage = "Artificial runtime exception";
        EasyMock.expect(resourceManagerRpcMock.get(ResourceManagerImplTest.PARTIAL_PROJECT.getProjectId(), ResourceManagerImplTest.EMPTY_RPC_OPTIONS)).andThrow(new RuntimeException(exceptionMessage));
        EasyMock.replay(resourceManagerRpcMock);
        thrown.expect(ResourceManagerException.class);
        thrown.expectMessage(exceptionMessage);
        resourceManagerMock.get(ResourceManagerImplTest.PARTIAL_PROJECT.getProjectId());
    }
}

