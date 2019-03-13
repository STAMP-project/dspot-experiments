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
package com.google.cloud.resourcemanager.testing;


import ResourceManagerRpc.Option;
import ResourceManagerRpc.Option.FIELDS;
import ResourceManagerRpc.Option.FILTER;
import ResourceManagerRpc.Option.PAGE_SIZE;
import ResourceManagerRpc.Option.PAGE_TOKEN;
import com.google.api.services.cloudresourcemanager.model.Binding;
import com.google.api.services.cloudresourcemanager.model.Policy;
import com.google.api.services.cloudresourcemanager.model.Project;
import com.google.api.services.cloudresourcemanager.model.ResourceId;
import com.google.cloud.Tuple;
import com.google.cloud.resourcemanager.ResourceManagerException;
import com.google.cloud.resourcemanager.spi.v1beta1.ResourceManagerRpc;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class LocalResourceManagerHelperTest {
    private static final String DEFAULT_PARENT_ID = "12345";

    private static final String DEFAULT_PARENT_TYPE = "organization";

    private static final ResourceId PARENT = new ResourceId().setId(LocalResourceManagerHelperTest.DEFAULT_PARENT_ID).setType(LocalResourceManagerHelperTest.DEFAULT_PARENT_TYPE);

    private static final Map<ResourceManagerRpc.Option, ?> EMPTY_RPC_OPTIONS = ImmutableMap.of();

    private static final LocalResourceManagerHelper RESOURCE_MANAGER_HELPER = LocalResourceManagerHelper.create();

    private static final ResourceManagerRpc rpc = new com.google.cloud.resourcemanager.spi.v1beta1.HttpResourceManagerRpc(LocalResourceManagerHelperTest.RESOURCE_MANAGER_HELPER.getOptions());

    private static final Project PARTIAL_PROJECT = new Project().setProjectId("partial-project");

    private static final Project COMPLETE_PROJECT = new Project().setProjectId("complete-project").setName("full project").setLabels(ImmutableMap.of("k1", "v1", "k2", "v2"));

    private static final Project PROJECT_WITH_PARENT = LocalResourceManagerHelperTest.copyFrom(LocalResourceManagerHelperTest.COMPLETE_PROJECT).setProjectId("project-with-parent-id").setParent(LocalResourceManagerHelperTest.PARENT);

    private static final List<Binding> BINDINGS = ImmutableList.of(new Binding().setRole("roles/owner").setMembers(ImmutableList.of("user:me@gmail.com")), new Binding().setRole("roles/viewer").setMembers(ImmutableList.of("group:group@gmail.com")));

    private static final Policy POLICY = new Policy().setBindings(LocalResourceManagerHelperTest.BINDINGS);

    @Test
    public void testCreate() {
        Project returnedProject = LocalResourceManagerHelperTest.rpc.create(LocalResourceManagerHelperTest.PARTIAL_PROJECT);
        compareReadWriteFields(LocalResourceManagerHelperTest.PARTIAL_PROJECT, returnedProject);
        Assert.assertEquals("ACTIVE", returnedProject.getLifecycleState());
        Assert.assertNull(returnedProject.getLabels());
        Assert.assertNull(returnedProject.getName());
        Assert.assertNull(returnedProject.getParent());
        Assert.assertNotNull(returnedProject.getProjectNumber());
        Assert.assertNotNull(returnedProject.getCreateTime());
        Policy policy = LocalResourceManagerHelperTest.rpc.getPolicy(LocalResourceManagerHelperTest.PARTIAL_PROJECT.getProjectId());
        Assert.assertEquals(Collections.emptyList(), policy.getBindings());
        Assert.assertNotNull(policy.getEtag());
        Assert.assertEquals(0, policy.getVersion().intValue());
        LocalResourceManagerHelperTest.rpc.replacePolicy(LocalResourceManagerHelperTest.PARTIAL_PROJECT.getProjectId(), LocalResourceManagerHelperTest.POLICY);
        Assert.assertEquals(LocalResourceManagerHelperTest.POLICY.getBindings(), LocalResourceManagerHelperTest.rpc.getPolicy(LocalResourceManagerHelperTest.PARTIAL_PROJECT.getProjectId()).getBindings());
        try {
            LocalResourceManagerHelperTest.rpc.create(LocalResourceManagerHelperTest.PARTIAL_PROJECT);
            Assert.fail("Should fail, project already exists.");
        } catch (ResourceManagerException e) {
            Assert.assertEquals(409, e.getCode());
            Assert.assertTrue(((e.getMessage().startsWith("A project with the same project ID")) && (e.getMessage().endsWith("already exists."))));
            Assert.assertEquals(LocalResourceManagerHelperTest.POLICY.getBindings(), LocalResourceManagerHelperTest.rpc.getPolicy(LocalResourceManagerHelperTest.PARTIAL_PROJECT.getProjectId()).getBindings());
        }
        returnedProject = LocalResourceManagerHelperTest.rpc.create(LocalResourceManagerHelperTest.PROJECT_WITH_PARENT);
        compareReadWriteFields(LocalResourceManagerHelperTest.PROJECT_WITH_PARENT, returnedProject);
        Assert.assertEquals("ACTIVE", returnedProject.getLifecycleState());
        Assert.assertNotNull(returnedProject.getProjectNumber());
        Assert.assertNotNull(returnedProject.getCreateTime());
    }

    @Test
    public void testIsInvalidProjectId() {
        Project project = new Project();
        String invalidIDMessageSubstring = "invalid ID";
        expectInvalidArgumentException(project, "Project ID cannot be empty.");
        project.setProjectId("abcde");
        expectInvalidArgumentException(project, invalidIDMessageSubstring);
        project.setProjectId("this-project-id-is-more-than-thirty-characters-long");
        expectInvalidArgumentException(project, invalidIDMessageSubstring);
        project.setProjectId("project-id-with-invalid-character-?");
        expectInvalidArgumentException(project, invalidIDMessageSubstring);
        project.setProjectId("-invalid-start-character");
        expectInvalidArgumentException(project, invalidIDMessageSubstring);
        project.setProjectId("invalid-ending-character-");
        expectInvalidArgumentException(project, invalidIDMessageSubstring);
        project.setProjectId("some-valid-project-id-12345");
        LocalResourceManagerHelperTest.rpc.create(project);
        Assert.assertNotNull(LocalResourceManagerHelperTest.rpc.get(project.getProjectId(), LocalResourceManagerHelperTest.EMPTY_RPC_OPTIONS));
    }

    @Test
    public void testIsInvalidProjectName() {
        Project project = new Project().setProjectId("some-project-id");
        LocalResourceManagerHelperTest.rpc.create(project);
        Assert.assertNull(LocalResourceManagerHelperTest.rpc.get(project.getProjectId(), LocalResourceManagerHelperTest.EMPTY_RPC_OPTIONS).getName());
        LocalResourceManagerHelperTest.RESOURCE_MANAGER_HELPER.removeProject(project.getProjectId());
        project.setName("This is a valid name-\'\"!");
        LocalResourceManagerHelperTest.rpc.create(project);
        Assert.assertEquals(project.getName(), LocalResourceManagerHelperTest.rpc.get(project.getProjectId(), LocalResourceManagerHelperTest.EMPTY_RPC_OPTIONS).getName());
        LocalResourceManagerHelperTest.RESOURCE_MANAGER_HELPER.removeProject(project.getProjectId());
        project.setName("invalid-character-,");
        try {
            LocalResourceManagerHelperTest.rpc.create(project);
            Assert.fail("Should fail because of invalid project name.");
        } catch (ResourceManagerException e) {
            Assert.assertEquals(400, e.getCode());
            Assert.assertTrue(e.getMessage().contains("invalid name"));
        }
    }

    @Test
    public void testIsInvalidProjectLabels() {
        Project project = new Project().setProjectId("some-valid-project-id");
        String invalidLabelMessageSubstring = "invalid label entry";
        project.setLabels(ImmutableMap.of("", "v1"));
        expectInvalidArgumentException(project, invalidLabelMessageSubstring);
        project.setLabels(ImmutableMap.of("this-project-label-is-more-than-sixty-three-characters-long-so-it-should-fail", "v1"));
        expectInvalidArgumentException(project, invalidLabelMessageSubstring);
        project.setLabels(ImmutableMap.of("k1", "this-project-label-is-more-than-sixty-three-characters-long-so-it-should-fail"));
        expectInvalidArgumentException(project, invalidLabelMessageSubstring);
        project.setLabels(ImmutableMap.of("k1?", "v1"));
        expectInvalidArgumentException(project, invalidLabelMessageSubstring);
        project.setLabels(ImmutableMap.of("k1", "v1*"));
        expectInvalidArgumentException(project, invalidLabelMessageSubstring);
        project.setLabels(ImmutableMap.of("-k1", "v1"));
        expectInvalidArgumentException(project, invalidLabelMessageSubstring);
        project.setLabels(ImmutableMap.of("k1", "-v1"));
        expectInvalidArgumentException(project, invalidLabelMessageSubstring);
        project.setLabels(ImmutableMap.of("k1-", "v1"));
        expectInvalidArgumentException(project, invalidLabelMessageSubstring);
        project.setLabels(ImmutableMap.of("k1", "v1-"));
        expectInvalidArgumentException(project, invalidLabelMessageSubstring);
        Map<String, String> tooManyLabels = new HashMap<>();
        for (int i = 0; i < 257; i++) {
            tooManyLabels.put(("k" + (Integer.toString(i))), ("v" + (Integer.toString(i))));
        }
        project.setLabels(tooManyLabels);
        expectInvalidArgumentException(project, "exceeds the limit of 256 labels");
        project.setLabels(ImmutableMap.of("k-1", ""));
        LocalResourceManagerHelperTest.rpc.create(project);
        Assert.assertNotNull(LocalResourceManagerHelperTest.rpc.get(project.getProjectId(), LocalResourceManagerHelperTest.EMPTY_RPC_OPTIONS));
        Assert.assertTrue(LocalResourceManagerHelperTest.rpc.get(project.getProjectId(), LocalResourceManagerHelperTest.EMPTY_RPC_OPTIONS).getLabels().get("k-1").isEmpty());
    }

    @Test
    public void testDelete() {
        LocalResourceManagerHelperTest.rpc.create(LocalResourceManagerHelperTest.COMPLETE_PROJECT);
        LocalResourceManagerHelperTest.rpc.delete(LocalResourceManagerHelperTest.COMPLETE_PROJECT.getProjectId());
        Assert.assertEquals("DELETE_REQUESTED", LocalResourceManagerHelperTest.rpc.get(LocalResourceManagerHelperTest.COMPLETE_PROJECT.getProjectId(), LocalResourceManagerHelperTest.EMPTY_RPC_OPTIONS).getLifecycleState());
        try {
            LocalResourceManagerHelperTest.rpc.delete("some-nonexistant-project-id");
            Assert.fail("Should fail because the project doesn't exist.");
        } catch (ResourceManagerException e) {
            Assert.assertEquals(403, e.getCode());
            Assert.assertTrue(e.getMessage().contains("not found."));
        }
    }

    @Test
    public void testDeleteWhenDeleteInProgress() {
        LocalResourceManagerHelperTest.rpc.create(LocalResourceManagerHelperTest.COMPLETE_PROJECT);
        LocalResourceManagerHelperTest.RESOURCE_MANAGER_HELPER.changeLifecycleState(LocalResourceManagerHelperTest.COMPLETE_PROJECT.getProjectId(), "DELETE_IN_PROGRESS");
        try {
            LocalResourceManagerHelperTest.rpc.delete(LocalResourceManagerHelperTest.COMPLETE_PROJECT.getProjectId());
            Assert.fail("Should fail because the project is not ACTIVE.");
        } catch (ResourceManagerException e) {
            Assert.assertEquals(400, e.getCode());
            Assert.assertTrue(e.getMessage().contains("the lifecycle state was not ACTIVE"));
        }
    }

    @Test
    public void testDeleteWhenDeleteRequested() {
        LocalResourceManagerHelperTest.rpc.create(LocalResourceManagerHelperTest.COMPLETE_PROJECT);
        LocalResourceManagerHelperTest.RESOURCE_MANAGER_HELPER.changeLifecycleState(LocalResourceManagerHelperTest.COMPLETE_PROJECT.getProjectId(), "DELETE_REQUESTED");
        try {
            LocalResourceManagerHelperTest.rpc.delete(LocalResourceManagerHelperTest.COMPLETE_PROJECT.getProjectId());
            Assert.fail("Should fail because the project is not ACTIVE.");
        } catch (ResourceManagerException e) {
            Assert.assertEquals(400, e.getCode());
            Assert.assertTrue(e.getMessage().contains("the lifecycle state was not ACTIVE"));
        }
    }

    @Test
    public void testGet() {
        LocalResourceManagerHelperTest.rpc.create(LocalResourceManagerHelperTest.COMPLETE_PROJECT);
        Project returnedProject = LocalResourceManagerHelperTest.rpc.get(LocalResourceManagerHelperTest.COMPLETE_PROJECT.getProjectId(), LocalResourceManagerHelperTest.EMPTY_RPC_OPTIONS);
        compareReadWriteFields(LocalResourceManagerHelperTest.COMPLETE_PROJECT, returnedProject);
        LocalResourceManagerHelperTest.RESOURCE_MANAGER_HELPER.removeProject(LocalResourceManagerHelperTest.COMPLETE_PROJECT.getProjectId());
        Assert.assertNull(LocalResourceManagerHelperTest.rpc.get(LocalResourceManagerHelperTest.COMPLETE_PROJECT.getProjectId(), LocalResourceManagerHelperTest.EMPTY_RPC_OPTIONS));
    }

    @Test
    public void testGetWithOptions() {
        Project originalProject = LocalResourceManagerHelperTest.rpc.create(LocalResourceManagerHelperTest.COMPLETE_PROJECT);
        Map<ResourceManagerRpc.Option, Object> rpcOptions = new HashMap<>();
        rpcOptions.put(FIELDS, "projectId,name,createTime");
        Project returnedProject = LocalResourceManagerHelperTest.rpc.get(LocalResourceManagerHelperTest.COMPLETE_PROJECT.getProjectId(), rpcOptions);
        Assert.assertFalse(LocalResourceManagerHelperTest.COMPLETE_PROJECT.equals(returnedProject));
        Assert.assertEquals(LocalResourceManagerHelperTest.COMPLETE_PROJECT.getProjectId(), returnedProject.getProjectId());
        Assert.assertEquals(LocalResourceManagerHelperTest.COMPLETE_PROJECT.getName(), returnedProject.getName());
        Assert.assertEquals(originalProject.getCreateTime(), returnedProject.getCreateTime());
        Assert.assertNull(returnedProject.getParent());
        Assert.assertNull(returnedProject.getProjectNumber());
        Assert.assertNull(returnedProject.getLifecycleState());
        Assert.assertNull(returnedProject.getLabels());
    }

    @Test
    public void testList() {
        Tuple<String, Iterable<Project>> projects = LocalResourceManagerHelperTest.rpc.list(LocalResourceManagerHelperTest.EMPTY_RPC_OPTIONS);
        Assert.assertNull(projects.x());
        Assert.assertFalse(projects.y().iterator().hasNext());
        LocalResourceManagerHelperTest.rpc.create(LocalResourceManagerHelperTest.COMPLETE_PROJECT);
        LocalResourceManagerHelperTest.RESOURCE_MANAGER_HELPER.changeLifecycleState(LocalResourceManagerHelperTest.COMPLETE_PROJECT.getProjectId(), "DELETE_REQUESTED");
        LocalResourceManagerHelperTest.rpc.create(LocalResourceManagerHelperTest.PROJECT_WITH_PARENT);
        projects = LocalResourceManagerHelperTest.rpc.list(LocalResourceManagerHelperTest.EMPTY_RPC_OPTIONS);
        for (Project p : projects.y()) {
            if (p.getProjectId().equals(LocalResourceManagerHelperTest.COMPLETE_PROJECT.getProjectId())) {
                compareReadWriteFields(LocalResourceManagerHelperTest.COMPLETE_PROJECT, p);
            } else
                if (p.getProjectId().equals(LocalResourceManagerHelperTest.PROJECT_WITH_PARENT.getProjectId())) {
                    compareReadWriteFields(LocalResourceManagerHelperTest.PROJECT_WITH_PARENT, p);
                } else {
                    Assert.fail("Unexpected project in list.");
                }

        }
    }

    @Test
    public void testInvalidListPaging() {
        Map<ResourceManagerRpc.Option, Object> rpcOptions = new HashMap<>();
        rpcOptions.put(PAGE_SIZE, (-1));
        try {
            LocalResourceManagerHelperTest.rpc.list(rpcOptions);
        } catch (ResourceManagerException e) {
            Assert.assertEquals("Page size must be greater than 0.", e.getMessage());
        }
    }

    @Test
    public void testListPaging() {
        Map<ResourceManagerRpc.Option, Object> rpcOptions = new HashMap<>();
        rpcOptions.put(PAGE_SIZE, 1);
        LocalResourceManagerHelperTest.rpc.create(LocalResourceManagerHelperTest.PARTIAL_PROJECT);
        LocalResourceManagerHelperTest.rpc.create(LocalResourceManagerHelperTest.COMPLETE_PROJECT);
        Tuple<String, Iterable<Project>> projects = LocalResourceManagerHelperTest.rpc.list(rpcOptions);
        Assert.assertNotNull(projects.x());
        Iterator<Project> iterator = projects.y().iterator();
        compareReadWriteFields(LocalResourceManagerHelperTest.COMPLETE_PROJECT, iterator.next());
        Assert.assertFalse(iterator.hasNext());
        rpcOptions = new HashMap();
        rpcOptions.put(PAGE_TOKEN, projects.x());
        projects = LocalResourceManagerHelperTest.rpc.list(rpcOptions);
        iterator = projects.y().iterator();
        compareReadWriteFields(LocalResourceManagerHelperTest.PARTIAL_PROJECT, iterator.next());
        Assert.assertFalse(iterator.hasNext());
        Assert.assertNull(projects.x());
    }

    @Test
    public void testListFieldOptions() {
        Map<ResourceManagerRpc.Option, Object> rpcOptions = new HashMap<>();
        rpcOptions.put(FIELDS, "projects(projectId,name,labels),nextPageToken");
        LocalResourceManagerHelperTest.rpc.create(LocalResourceManagerHelperTest.PROJECT_WITH_PARENT);
        Tuple<String, Iterable<Project>> projects = LocalResourceManagerHelperTest.rpc.list(rpcOptions);
        Project returnedProject = projects.y().iterator().next();
        Assert.assertFalse(LocalResourceManagerHelperTest.PROJECT_WITH_PARENT.equals(returnedProject));
        Assert.assertEquals(LocalResourceManagerHelperTest.PROJECT_WITH_PARENT.getProjectId(), returnedProject.getProjectId());
        Assert.assertEquals(LocalResourceManagerHelperTest.PROJECT_WITH_PARENT.getName(), returnedProject.getName());
        Assert.assertEquals(LocalResourceManagerHelperTest.PROJECT_WITH_PARENT.getLabels(), returnedProject.getLabels());
        Assert.assertNull(returnedProject.getParent());
        Assert.assertNull(returnedProject.getProjectNumber());
        Assert.assertNull(returnedProject.getLifecycleState());
        Assert.assertNull(returnedProject.getCreateTime());
    }

    @Test
    public void testListPageTokenFieldOptions() {
        Map<ResourceManagerRpc.Option, Object> rpcOptions = new HashMap<>();
        rpcOptions.put(PAGE_SIZE, 1);
        rpcOptions.put(FIELDS, "nextPageToken,projects(projectId,name)");
        LocalResourceManagerHelperTest.rpc.create(LocalResourceManagerHelperTest.PARTIAL_PROJECT);
        LocalResourceManagerHelperTest.rpc.create(LocalResourceManagerHelperTest.COMPLETE_PROJECT);
        Tuple<String, Iterable<Project>> projects = LocalResourceManagerHelperTest.rpc.list(rpcOptions);
        Assert.assertNotNull(projects.x());
        Iterator<Project> iterator = projects.y().iterator();
        Project returnedProject = iterator.next();
        Assert.assertEquals(LocalResourceManagerHelperTest.COMPLETE_PROJECT.getProjectId(), returnedProject.getProjectId());
        Assert.assertEquals(LocalResourceManagerHelperTest.COMPLETE_PROJECT.getName(), returnedProject.getName());
        Assert.assertNull(returnedProject.getLabels());
        Assert.assertNull(returnedProject.getParent());
        Assert.assertNull(returnedProject.getProjectNumber());
        Assert.assertNull(returnedProject.getLifecycleState());
        Assert.assertNull(returnedProject.getCreateTime());
        Assert.assertFalse(iterator.hasNext());
        rpcOptions.put(PAGE_TOKEN, projects.x());
        projects = LocalResourceManagerHelperTest.rpc.list(rpcOptions);
        iterator = projects.y().iterator();
        returnedProject = iterator.next();
        Assert.assertEquals(LocalResourceManagerHelperTest.PARTIAL_PROJECT.getProjectId(), returnedProject.getProjectId());
        Assert.assertEquals(LocalResourceManagerHelperTest.PARTIAL_PROJECT.getName(), returnedProject.getName());
        Assert.assertNull(returnedProject.getLabels());
        Assert.assertNull(returnedProject.getParent());
        Assert.assertNull(returnedProject.getProjectNumber());
        Assert.assertNull(returnedProject.getLifecycleState());
        Assert.assertNull(returnedProject.getCreateTime());
        Assert.assertNull(projects.x());
    }

    @Test
    public void testListNoPageTokenFieldOptions() {
        Map<ResourceManagerRpc.Option, Object> rpcOptions = new HashMap<>();
        rpcOptions.put(PAGE_SIZE, 1);
        rpcOptions.put(FIELDS, "projects(projectId,name)");
        LocalResourceManagerHelperTest.rpc.create(LocalResourceManagerHelperTest.PARTIAL_PROJECT);
        LocalResourceManagerHelperTest.rpc.create(LocalResourceManagerHelperTest.COMPLETE_PROJECT);
        Tuple<String, Iterable<Project>> projects = LocalResourceManagerHelperTest.rpc.list(rpcOptions);
        Assert.assertNull(projects.x());
        Iterator<Project> iterator = projects.y().iterator();
        Project returnedProject = iterator.next();
        Assert.assertEquals(LocalResourceManagerHelperTest.COMPLETE_PROJECT.getProjectId(), returnedProject.getProjectId());
        Assert.assertEquals(LocalResourceManagerHelperTest.COMPLETE_PROJECT.getName(), returnedProject.getName());
        Assert.assertNull(returnedProject.getLabels());
        Assert.assertNull(returnedProject.getParent());
        Assert.assertNull(returnedProject.getProjectNumber());
        Assert.assertNull(returnedProject.getLifecycleState());
        Assert.assertNull(returnedProject.getCreateTime());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testListPageTokenNoFieldsOptions() {
        Map<ResourceManagerRpc.Option, Object> rpcOptions = new HashMap<>();
        rpcOptions.put(PAGE_SIZE, 1);
        rpcOptions.put(FIELDS, "nextPageToken");
        LocalResourceManagerHelperTest.rpc.create(LocalResourceManagerHelperTest.PARTIAL_PROJECT);
        LocalResourceManagerHelperTest.rpc.create(LocalResourceManagerHelperTest.COMPLETE_PROJECT);
        Tuple<String, Iterable<Project>> projects = LocalResourceManagerHelperTest.rpc.list(rpcOptions);
        Assert.assertNotNull(projects.x());
        Assert.assertNull(projects.y());
        rpcOptions.put(PAGE_TOKEN, projects.x());
        projects = LocalResourceManagerHelperTest.rpc.list(rpcOptions);
        Assert.assertNull(projects.x());
        Assert.assertNull(projects.y());
    }

    @Test
    public void testListFilterOptions() {
        Map<ResourceManagerRpc.Option, Object> rpcFilterOptions = new HashMap<>();
        rpcFilterOptions.put(FILTER, "id:* name:myProject labels.color:blue LABELS.SIZE:*");
        Project matchingProject = new Project().setProjectId("matching-project").setName("MyProject").setLabels(ImmutableMap.of("color", "blue", "size", "big"));
        Project nonMatchingProject1 = new Project().setProjectId("non-matching-project1").setName("myProject");
        nonMatchingProject1.setLabels(ImmutableMap.of("color", "blue"));
        Project nonMatchingProject2 = new Project().setProjectId("non-matching-project2").setName("myProj").setLabels(ImmutableMap.of("color", "blue", "size", "big"));
        Project nonMatchingProject3 = new Project().setProjectId("non-matching-project3");
        LocalResourceManagerHelperTest.rpc.create(matchingProject);
        LocalResourceManagerHelperTest.rpc.create(nonMatchingProject1);
        LocalResourceManagerHelperTest.rpc.create(nonMatchingProject2);
        LocalResourceManagerHelperTest.rpc.create(nonMatchingProject3);
        for (Project p : LocalResourceManagerHelperTest.rpc.list(rpcFilterOptions).y()) {
            Assert.assertFalse(p.equals(nonMatchingProject1));
            Assert.assertFalse(p.equals(nonMatchingProject2));
            compareReadWriteFields(matchingProject, p);
        }
    }

    @Test
    public void testReplace() {
        Project createdProject = LocalResourceManagerHelperTest.rpc.create(LocalResourceManagerHelperTest.COMPLETE_PROJECT);
        String newName = "new name";
        Map<String, String> newLabels = ImmutableMap.of("new k1", "new v1");
        Project anotherCompleteProject = new Project().setProjectId(LocalResourceManagerHelperTest.COMPLETE_PROJECT.getProjectId()).setName(newName).setLabels(newLabels).setProjectNumber(987654321L).setCreateTime("2000-01-01T00:00:00.001Z").setLifecycleState("DELETE_REQUESTED");
        Project returnedProject = LocalResourceManagerHelperTest.rpc.replace(anotherCompleteProject);
        compareReadWriteFields(anotherCompleteProject, returnedProject);
        Assert.assertEquals(createdProject.getProjectNumber(), returnedProject.getProjectNumber());
        Assert.assertEquals(createdProject.getCreateTime(), returnedProject.getCreateTime());
        Assert.assertEquals(createdProject.getLifecycleState(), returnedProject.getLifecycleState());
        Project nonexistantProject = new Project();
        nonexistantProject.setProjectId("some-project-id-that-does-not-exist");
        try {
            LocalResourceManagerHelperTest.rpc.replace(nonexistantProject);
            Assert.fail("Should fail because the project doesn't exist.");
        } catch (ResourceManagerException e) {
            Assert.assertEquals(403, e.getCode());
            Assert.assertTrue(e.getMessage().contains("the project was not found"));
        }
    }

    @Test
    public void testReplaceWhenDeleteRequested() {
        LocalResourceManagerHelperTest.rpc.create(LocalResourceManagerHelperTest.COMPLETE_PROJECT);
        LocalResourceManagerHelperTest.rpc.delete(LocalResourceManagerHelperTest.COMPLETE_PROJECT.getProjectId());
        Project anotherProject = new Project().setProjectId(LocalResourceManagerHelperTest.COMPLETE_PROJECT.getProjectId());
        try {
            LocalResourceManagerHelperTest.rpc.replace(anotherProject);
            Assert.fail("Should fail because the project is not ACTIVE.");
        } catch (ResourceManagerException e) {
            Assert.assertEquals(400, e.getCode());
            Assert.assertTrue(e.getMessage().contains("the lifecycle state was not ACTIVE"));
        }
    }

    @Test
    public void testReplaceWhenDeleteInProgress() {
        LocalResourceManagerHelperTest.rpc.create(LocalResourceManagerHelperTest.COMPLETE_PROJECT);
        LocalResourceManagerHelperTest.RESOURCE_MANAGER_HELPER.changeLifecycleState(LocalResourceManagerHelperTest.COMPLETE_PROJECT.getProjectId(), "DELETE_IN_PROGRESS");
        Project anotherProject = new Project().setProjectId(LocalResourceManagerHelperTest.COMPLETE_PROJECT.getProjectId());
        try {
            LocalResourceManagerHelperTest.rpc.replace(anotherProject);
            Assert.fail("Should fail because the project is not ACTIVE.");
        } catch (ResourceManagerException e) {
            Assert.assertEquals(400, e.getCode());
            Assert.assertTrue(e.getMessage().contains("the lifecycle state was not ACTIVE"));
        }
    }

    @Test
    public void testReplaceAddingParent() {
        LocalResourceManagerHelperTest.rpc.create(LocalResourceManagerHelperTest.COMPLETE_PROJECT);
        Project anotherProject = new Project().setProjectId(LocalResourceManagerHelperTest.COMPLETE_PROJECT.getProjectId()).setParent(LocalResourceManagerHelperTest.PARENT);
        try {
            LocalResourceManagerHelperTest.rpc.replace(anotherProject);
            Assert.fail("Should fail because the project's parent was modified after creation.");
        } catch (ResourceManagerException e) {
            Assert.assertEquals(400, e.getCode());
            Assert.assertEquals(("The server currently only supports setting the parent once " + "and does not allow unsetting it."), e.getMessage());
        }
    }

    @Test
    public void testReplaceRemovingParent() {
        LocalResourceManagerHelperTest.rpc.create(LocalResourceManagerHelperTest.PROJECT_WITH_PARENT);
        Project anotherProject = new Project().setProjectId(LocalResourceManagerHelperTest.PROJECT_WITH_PARENT.getProjectId());
        try {
            LocalResourceManagerHelperTest.rpc.replace(anotherProject);
            Assert.fail("Should fail because the project's parent was unset.");
        } catch (ResourceManagerException e) {
            Assert.assertEquals(400, e.getCode());
            Assert.assertEquals(("The server currently only supports setting the parent once " + "and does not allow unsetting it."), e.getMessage());
        }
    }

    @Test
    public void testUndelete() {
        LocalResourceManagerHelperTest.rpc.create(LocalResourceManagerHelperTest.COMPLETE_PROJECT);
        LocalResourceManagerHelperTest.rpc.delete(LocalResourceManagerHelperTest.COMPLETE_PROJECT.getProjectId());
        Assert.assertEquals("DELETE_REQUESTED", LocalResourceManagerHelperTest.rpc.get(LocalResourceManagerHelperTest.COMPLETE_PROJECT.getProjectId(), LocalResourceManagerHelperTest.EMPTY_RPC_OPTIONS).getLifecycleState());
        LocalResourceManagerHelperTest.rpc.undelete(LocalResourceManagerHelperTest.COMPLETE_PROJECT.getProjectId());
        Project revivedProject = LocalResourceManagerHelperTest.rpc.get(LocalResourceManagerHelperTest.COMPLETE_PROJECT.getProjectId(), LocalResourceManagerHelperTest.EMPTY_RPC_OPTIONS);
        compareReadWriteFields(LocalResourceManagerHelperTest.COMPLETE_PROJECT, revivedProject);
        Assert.assertEquals("ACTIVE", revivedProject.getLifecycleState());
        try {
            LocalResourceManagerHelperTest.rpc.undelete("invalid-project-id");
            Assert.fail("Should fail because the project doesn't exist.");
        } catch (ResourceManagerException e) {
            Assert.assertEquals(403, e.getCode());
            Assert.assertTrue(e.getMessage().contains("the project was not found"));
        }
    }

    @Test
    public void testUndeleteWhenActive() {
        LocalResourceManagerHelperTest.rpc.create(LocalResourceManagerHelperTest.COMPLETE_PROJECT);
        try {
            LocalResourceManagerHelperTest.rpc.undelete(LocalResourceManagerHelperTest.COMPLETE_PROJECT.getProjectId());
            Assert.fail("Should fail because the project is not deleted.");
        } catch (ResourceManagerException e) {
            Assert.assertEquals(400, e.getCode());
            Assert.assertTrue(e.getMessage().contains("lifecycle state was not DELETE_REQUESTED"));
        }
    }

    @Test
    public void testUndeleteWhenDeleteInProgress() {
        LocalResourceManagerHelperTest.rpc.create(LocalResourceManagerHelperTest.COMPLETE_PROJECT);
        LocalResourceManagerHelperTest.RESOURCE_MANAGER_HELPER.changeLifecycleState(LocalResourceManagerHelperTest.COMPLETE_PROJECT.getProjectId(), "DELETE_IN_PROGRESS");
        try {
            LocalResourceManagerHelperTest.rpc.undelete(LocalResourceManagerHelperTest.COMPLETE_PROJECT.getProjectId());
            Assert.fail("Should fail because the project is in the process of being deleted.");
        } catch (ResourceManagerException e) {
            Assert.assertEquals(400, e.getCode());
            Assert.assertTrue(e.getMessage().contains("lifecycle state was not DELETE_REQUESTED"));
        }
    }

    @Test
    public void testGetPolicy() {
        Assert.assertNull(LocalResourceManagerHelperTest.rpc.getPolicy("nonexistent-project"));
        LocalResourceManagerHelperTest.rpc.create(LocalResourceManagerHelperTest.PARTIAL_PROJECT);
        Policy policy = LocalResourceManagerHelperTest.rpc.getPolicy(LocalResourceManagerHelperTest.PARTIAL_PROJECT.getProjectId());
        Assert.assertEquals(Collections.emptyList(), policy.getBindings());
        Assert.assertNotNull(policy.getEtag());
    }

    @Test
    public void testReplacePolicy() {
        try {
            LocalResourceManagerHelperTest.rpc.replacePolicy("nonexistent-project", LocalResourceManagerHelperTest.POLICY);
            Assert.fail("Project doesn't exist.");
        } catch (ResourceManagerException e) {
            Assert.assertEquals(403, e.getCode());
            Assert.assertTrue(e.getMessage().contains("project was not found"));
        }
        LocalResourceManagerHelperTest.rpc.create(LocalResourceManagerHelperTest.PARTIAL_PROJECT);
        Policy invalidPolicy = new Policy().setEtag("wrong-etag");
        try {
            LocalResourceManagerHelperTest.rpc.replacePolicy(LocalResourceManagerHelperTest.PARTIAL_PROJECT.getProjectId(), invalidPolicy);
            Assert.fail("Invalid etag.");
        } catch (ResourceManagerException e) {
            Assert.assertEquals(409, e.getCode());
            Assert.assertTrue(e.getMessage().startsWith("Policy etag mismatch"));
        }
        String originalEtag = LocalResourceManagerHelperTest.rpc.getPolicy(LocalResourceManagerHelperTest.PARTIAL_PROJECT.getProjectId()).getEtag();
        Policy newPolicy = LocalResourceManagerHelperTest.rpc.replacePolicy(LocalResourceManagerHelperTest.PARTIAL_PROJECT.getProjectId(), LocalResourceManagerHelperTest.POLICY);
        Assert.assertEquals(LocalResourceManagerHelperTest.POLICY.getBindings(), newPolicy.getBindings());
        Assert.assertNotNull(newPolicy.getEtag());
        Assert.assertNotEquals(originalEtag, newPolicy.getEtag());
    }

    @Test
    public void testTestPermissions() {
        List<String> permissions = ImmutableList.of("resourcemanager.projects.get");
        try {
            LocalResourceManagerHelperTest.rpc.testPermissions("nonexistent-project", permissions);
            Assert.fail("Nonexistent project.");
        } catch (ResourceManagerException e) {
            Assert.assertEquals(403, e.getCode());
            Assert.assertEquals("Project nonexistent-project not found.", e.getMessage());
        }
        LocalResourceManagerHelperTest.rpc.create(LocalResourceManagerHelperTest.PARTIAL_PROJECT);
        Assert.assertEquals(ImmutableList.of(true), LocalResourceManagerHelperTest.rpc.testPermissions(LocalResourceManagerHelperTest.PARTIAL_PROJECT.getProjectId(), permissions));
    }

    @Test
    public void testChangeLifecycleStatus() {
        Assert.assertFalse(LocalResourceManagerHelperTest.RESOURCE_MANAGER_HELPER.changeLifecycleState(LocalResourceManagerHelperTest.COMPLETE_PROJECT.getProjectId(), "DELETE_IN_PROGRESS"));
        LocalResourceManagerHelperTest.rpc.create(LocalResourceManagerHelperTest.COMPLETE_PROJECT);
        Assert.assertTrue(LocalResourceManagerHelperTest.RESOURCE_MANAGER_HELPER.changeLifecycleState(LocalResourceManagerHelperTest.COMPLETE_PROJECT.getProjectId(), "DELETE_IN_PROGRESS"));
        Assert.assertEquals("DELETE_IN_PROGRESS", LocalResourceManagerHelperTest.rpc.get(LocalResourceManagerHelperTest.COMPLETE_PROJECT.getProjectId(), LocalResourceManagerHelperTest.EMPTY_RPC_OPTIONS).getLifecycleState());
        try {
            LocalResourceManagerHelperTest.RESOURCE_MANAGER_HELPER.changeLifecycleState(LocalResourceManagerHelperTest.COMPLETE_PROJECT.getProjectId(), "INVALID_STATE");
            Assert.fail("Should fail because of an invalid lifecycle state");
        } catch (IllegalArgumentException e) {
            // ignore
        }
    }

    @Test
    public void testRemoveProject() {
        Assert.assertFalse(LocalResourceManagerHelperTest.RESOURCE_MANAGER_HELPER.removeProject(LocalResourceManagerHelperTest.COMPLETE_PROJECT.getProjectId()));
        LocalResourceManagerHelperTest.rpc.create(LocalResourceManagerHelperTest.COMPLETE_PROJECT);
        Assert.assertNotNull(LocalResourceManagerHelperTest.rpc.getPolicy(LocalResourceManagerHelperTest.COMPLETE_PROJECT.getProjectId()));
        Assert.assertTrue(LocalResourceManagerHelperTest.RESOURCE_MANAGER_HELPER.removeProject(LocalResourceManagerHelperTest.COMPLETE_PROJECT.getProjectId()));
        Assert.assertNull(LocalResourceManagerHelperTest.rpc.get(LocalResourceManagerHelperTest.COMPLETE_PROJECT.getProjectId(), LocalResourceManagerHelperTest.EMPTY_RPC_OPTIONS));
        Assert.assertNull(LocalResourceManagerHelperTest.rpc.getPolicy(LocalResourceManagerHelperTest.COMPLETE_PROJECT.getProjectId()));
    }
}

