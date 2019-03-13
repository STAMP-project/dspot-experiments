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


import Project.Builder;
import ProjectInfo.State;
import com.google.cloud.Identity;
import com.google.cloud.Policy;
import com.google.cloud.Role;
import com.google.cloud.resourcemanager.ProjectInfo.ResourceId;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class ProjectTest {
    private static final String PROJECT_ID = "project-id";

    private static final String NAME = "myProj";

    private static final Map<String, String> LABELS = ImmutableMap.of("k1", "v1", "k2", "v2");

    private static final Long PROJECT_NUMBER = 123L;

    private static final Long CREATE_TIME_MILLIS = 123456789L;

    private static final State STATE = State.DELETE_REQUESTED;

    private static final ProjectInfo PROJECT_INFO = ProjectInfo.newBuilder(ProjectTest.PROJECT_ID).setName(ProjectTest.NAME).setLabels(ProjectTest.LABELS).setProjectNumber(ProjectTest.PROJECT_NUMBER).setCreateTimeMillis(ProjectTest.CREATE_TIME_MILLIS).setState(ProjectTest.STATE).build();

    private static final Identity USER = Identity.user("abc@gmail.com");

    private static final Identity SERVICE_ACCOUNT = Identity.serviceAccount("service-account@gmail.com");

    private static final Policy POLICY = Policy.newBuilder().addIdentity(Role.owner(), ProjectTest.USER).addIdentity(Role.editor(), ProjectTest.SERVICE_ACCOUNT).build();

    private ResourceManager serviceMockReturnsOptions = createStrictMock(ResourceManager.class);

    private ResourceManagerOptions mockOptions = createMock(ResourceManagerOptions.class);

    private ResourceManager resourceManager;

    private Project expectedProject;

    private Project project;

    @Test
    public void testToBuilder() {
        initializeExpectedProject(4);
        replay(resourceManager);
        compareProjects(expectedProject, expectedProject.toBuilder().build());
    }

    @Test
    public void testBuilder() {
        expect(resourceManager.getOptions()).andReturn(mockOptions).times(7);
        replay(resourceManager);
        Project.Builder builder = new Project.Builder(new Project(resourceManager, new ProjectInfo.BuilderImpl("wrong-id")));
        Project project = builder.setProjectId(ProjectTest.PROJECT_ID).setName(ProjectTest.NAME).setLabels(ProjectTest.LABELS).setProjectNumber(ProjectTest.PROJECT_NUMBER).setCreateTimeMillis(ProjectTest.CREATE_TIME_MILLIS).setState(ProjectTest.STATE).build();
        Assert.assertEquals(ProjectTest.PROJECT_ID, project.getProjectId());
        Assert.assertEquals(ProjectTest.NAME, project.getName());
        Assert.assertEquals(ProjectTest.LABELS, project.getLabels());
        Assert.assertEquals(ProjectTest.PROJECT_NUMBER, project.getProjectNumber());
        Assert.assertEquals(ProjectTest.CREATE_TIME_MILLIS, project.getCreateTimeMillis());
        Assert.assertEquals(ProjectTest.STATE, project.getState());
        Assert.assertEquals(resourceManager.getOptions(), project.getResourceManager().getOptions());
        Assert.assertNull(project.getParent());
        ResourceId parent = new ResourceId("id", "type");
        project = project.toBuilder().clearLabels().addLabel("k3", "v3").addLabel("k4", "v4").removeLabel("k4").setParent(parent).build();
        Assert.assertEquals(ProjectTest.PROJECT_ID, project.getProjectId());
        Assert.assertEquals(ProjectTest.NAME, project.getName());
        Assert.assertEquals(ImmutableMap.of("k3", "v3"), project.getLabels());
        Assert.assertEquals(ProjectTest.PROJECT_NUMBER, project.getProjectNumber());
        Assert.assertEquals(ProjectTest.CREATE_TIME_MILLIS, project.getCreateTimeMillis());
        Assert.assertEquals(ProjectTest.STATE, project.getState());
        Assert.assertEquals(resourceManager.getOptions(), project.getResourceManager().getOptions());
        Assert.assertEquals(parent, project.getParent());
    }

    @Test
    public void testGet() {
        initializeExpectedProject(1);
        expect(resourceManager.get(ProjectTest.PROJECT_INFO.getProjectId())).andReturn(expectedProject);
        replay(resourceManager);
        Project loadedProject = resourceManager.get(ProjectTest.PROJECT_INFO.getProjectId());
        Assert.assertEquals(expectedProject, loadedProject);
    }

    @Test
    public void testReload() {
        initializeExpectedProject(2);
        ProjectInfo newInfo = ProjectTest.PROJECT_INFO.toBuilder().addLabel("k3", "v3").build();
        Project expectedProject = new Project(serviceMockReturnsOptions, new ProjectInfo.BuilderImpl(newInfo));
        expect(resourceManager.getOptions()).andReturn(mockOptions);
        expect(resourceManager.get(ProjectTest.PROJECT_INFO.getProjectId())).andReturn(expectedProject);
        replay(resourceManager);
        initializeProject();
        Project newProject = project.reload();
        Assert.assertEquals(expectedProject, newProject);
    }

    @Test
    public void testLoadNull() {
        initializeExpectedProject(1);
        expect(resourceManager.get(ProjectTest.PROJECT_INFO.getProjectId())).andReturn(null);
        replay(resourceManager);
        Assert.assertNull(resourceManager.get(ProjectTest.PROJECT_INFO.getProjectId()));
    }

    @Test
    public void testReloadNull() {
        initializeExpectedProject(1);
        expect(resourceManager.getOptions()).andReturn(mockOptions);
        expect(resourceManager.get(ProjectTest.PROJECT_INFO.getProjectId())).andReturn(null);
        replay(resourceManager);
        Project reloadedProject = reload();
        Assert.assertNull(reloadedProject);
    }

    @Test
    public void testResourceManager() {
        initializeExpectedProject(1);
        replay(resourceManager);
        Assert.assertEquals(serviceMockReturnsOptions, expectedProject.getResourceManager());
    }

    @Test
    public void testDelete() {
        initializeExpectedProject(1);
        expect(resourceManager.getOptions()).andReturn(mockOptions);
        resourceManager.delete(ProjectTest.PROJECT_INFO.getProjectId());
        replay(resourceManager);
        initializeProject();
        project.delete();
    }

    @Test
    public void testUndelete() {
        initializeExpectedProject(1);
        expect(resourceManager.getOptions()).andReturn(mockOptions);
        resourceManager.undelete(ProjectTest.PROJECT_INFO.getProjectId());
        replay(resourceManager);
        initializeProject();
        project.undelete();
    }

    @Test
    public void testReplace() {
        initializeExpectedProject(2);
        Project expectedReplacedProject = expectedProject.toBuilder().addLabel("k3", "v3").build();
        expect(resourceManager.getOptions()).andReturn(mockOptions).times(2);
        expect(resourceManager.replace(anyObject(Project.class))).andReturn(expectedReplacedProject);
        replay(resourceManager);
        initializeProject();
        Project newProject = new Project(resourceManager, new ProjectInfo.BuilderImpl(expectedReplacedProject));
        Project actualReplacedProject = newProject.replace();
        compareProjectInfos(expectedReplacedProject, actualReplacedProject);
    }

    @Test
    public void testGetPolicy() {
        expect(resourceManager.getOptions()).andReturn(mockOptions).times(1);
        expect(resourceManager.getPolicy(ProjectTest.PROJECT_ID)).andReturn(ProjectTest.POLICY);
        replay(resourceManager);
        initializeProject();
        Assert.assertEquals(ProjectTest.POLICY, project.getPolicy());
    }

    @Test
    public void testReplacePolicy() {
        expect(resourceManager.getOptions()).andReturn(mockOptions).times(1);
        expect(resourceManager.replacePolicy(ProjectTest.PROJECT_ID, ProjectTest.POLICY)).andReturn(ProjectTest.POLICY);
        replay(resourceManager);
        initializeProject();
        Assert.assertEquals(ProjectTest.POLICY, project.replacePolicy(ProjectTest.POLICY));
    }

    @Test
    public void testTestPermissions() {
        List<Boolean> response = ImmutableList.of(true, true);
        String getPermission = "resourcemanager.projects.get";
        String deletePermission = "resourcemanager.projects.delete";
        expect(resourceManager.getOptions()).andReturn(mockOptions).times(1);
        expect(resourceManager.testPermissions(ProjectTest.PROJECT_ID, ImmutableList.of(getPermission, deletePermission))).andReturn(response);
        replay(resourceManager);
        initializeProject();
        Assert.assertEquals(response, project.testPermissions(ImmutableList.of(getPermission, deletePermission)));
    }
}

