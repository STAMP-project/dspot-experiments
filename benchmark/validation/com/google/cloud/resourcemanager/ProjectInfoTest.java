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


import ProjectInfo.ResourceId;
import ProjectInfo.State;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class ProjectInfoTest {
    private static final String PROJECT_ID = "project-id";

    private static final String NAME = "myProj";

    private static final Map<String, String> LABELS = ImmutableMap.of("k1", "v1", "k2", "v2");

    private static final Long PROJECT_NUMBER = 123L;

    private static final Long CREATE_TIME_MILLIS = 123456789L;

    private static final State STATE = State.DELETE_REQUESTED;

    private static final ResourceId PARENT = new ProjectInfo.ResourceId("id", "organization");

    private static final ProjectInfo FULL_PROJECT_INFO = ProjectInfo.newBuilder(ProjectInfoTest.PROJECT_ID).setName(ProjectInfoTest.NAME).setLabels(ProjectInfoTest.LABELS).setProjectNumber(ProjectInfoTest.PROJECT_NUMBER).setCreateTimeMillis(ProjectInfoTest.CREATE_TIME_MILLIS).setState(ProjectInfoTest.STATE).setParent(ProjectInfoTest.PARENT).build();

    private static final ProjectInfo PARTIAL_PROJECT_INFO = ProjectInfo.newBuilder(ProjectInfoTest.PROJECT_ID).build();

    private static final ProjectInfo UNNAMED_PROJECT_FROM_LIST = ProjectInfoTest.PARTIAL_PROJECT_INFO.toBuilder().setName("Unnamed").build();

    @Test
    public void testBuilder() {
        Assert.assertEquals(ProjectInfoTest.PROJECT_ID, ProjectInfoTest.FULL_PROJECT_INFO.getProjectId());
        Assert.assertEquals(ProjectInfoTest.NAME, ProjectInfoTest.FULL_PROJECT_INFO.getName());
        Assert.assertEquals(ProjectInfoTest.LABELS, ProjectInfoTest.FULL_PROJECT_INFO.getLabels());
        Assert.assertEquals(ProjectInfoTest.PROJECT_NUMBER, ProjectInfoTest.FULL_PROJECT_INFO.getProjectNumber());
        Assert.assertEquals(ProjectInfoTest.CREATE_TIME_MILLIS, ProjectInfoTest.FULL_PROJECT_INFO.getCreateTimeMillis());
        Assert.assertEquals(ProjectInfoTest.STATE, ProjectInfoTest.FULL_PROJECT_INFO.getState());
        Assert.assertEquals(ProjectInfoTest.PROJECT_ID, ProjectInfoTest.PARTIAL_PROJECT_INFO.getProjectId());
        Assert.assertEquals(null, ProjectInfoTest.PARTIAL_PROJECT_INFO.getName());
        Assert.assertTrue(ProjectInfoTest.PARTIAL_PROJECT_INFO.getLabels().isEmpty());
        Assert.assertEquals(null, ProjectInfoTest.PARTIAL_PROJECT_INFO.getProjectNumber());
        Assert.assertEquals(null, ProjectInfoTest.PARTIAL_PROJECT_INFO.getCreateTimeMillis());
        Assert.assertEquals(null, ProjectInfoTest.PARTIAL_PROJECT_INFO.getState());
    }

    @Test
    public void testToBuilder() {
        compareProjects(ProjectInfoTest.FULL_PROJECT_INFO, ProjectInfoTest.FULL_PROJECT_INFO.toBuilder().build());
        compareProjects(ProjectInfoTest.PARTIAL_PROJECT_INFO, ProjectInfoTest.PARTIAL_PROJECT_INFO.toBuilder().build());
    }

    @Test
    public void testToAndFromPb() {
        Assert.assertTrue(ProjectInfoTest.FULL_PROJECT_INFO.toPb().getCreateTime().endsWith("Z"));
        compareProjects(ProjectInfoTest.FULL_PROJECT_INFO, ProjectInfo.fromPb(ProjectInfoTest.FULL_PROJECT_INFO.toPb()));
        compareProjects(ProjectInfoTest.PARTIAL_PROJECT_INFO, ProjectInfo.fromPb(ProjectInfoTest.PARTIAL_PROJECT_INFO.toPb()));
        compareProjects(ProjectInfoTest.PARTIAL_PROJECT_INFO, ProjectInfo.fromPb(ProjectInfoTest.UNNAMED_PROJECT_FROM_LIST.toPb()));
    }

    @Test
    public void testEquals() {
        compareProjects(ProjectInfoTest.FULL_PROJECT_INFO, ProjectInfo.newBuilder(ProjectInfoTest.PROJECT_ID).setName(ProjectInfoTest.NAME).setLabels(ProjectInfoTest.LABELS).setProjectNumber(ProjectInfoTest.PROJECT_NUMBER).setCreateTimeMillis(ProjectInfoTest.CREATE_TIME_MILLIS).setState(ProjectInfoTest.STATE).setParent(ProjectInfoTest.PARENT).build());
        compareProjects(ProjectInfoTest.PARTIAL_PROJECT_INFO, ProjectInfo.newBuilder(ProjectInfoTest.PROJECT_ID).build());
        Assert.assertNotEquals(ProjectInfoTest.FULL_PROJECT_INFO, ProjectInfoTest.PARTIAL_PROJECT_INFO);
    }
}

