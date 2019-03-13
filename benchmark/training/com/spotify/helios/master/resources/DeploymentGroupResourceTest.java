/**
 * -
 * -\-\-
 * Helios Services
 * --
 * Copyright (C) 2016 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.helios.master.resources;


import CreateDeploymentGroupResponse.Status;
import Response.Status.NOT_FOUND;
import Response.Status.OK;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.spotify.helios.common.descriptors.DeploymentGroup;
import com.spotify.helios.common.descriptors.HostSelector;
import com.spotify.helios.common.descriptors.JobId;
import com.spotify.helios.common.descriptors.RolloutOptions;
import com.spotify.helios.master.DeploymentGroupDoesNotExistException;
import com.spotify.helios.master.DeploymentGroupExistsException;
import com.spotify.helios.master.JobDoesNotExistException;
import com.spotify.helios.master.MasterModel;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static RemoveDeploymentGroupResponse.Status.DEPLOYMENT_GROUP_NOT_FOUND;
import static RemoveDeploymentGroupResponse.Status.REMOVED;
import static RollingUpdateResponse.Status.JOB_NOT_FOUND;


@RunWith(MockitoJUnitRunner.class)
public class DeploymentGroupResourceTest {
    private static final HostSelector ROLE_SELECTOR = HostSelector.parse("role=my_role");

    private static final HostSelector FOO_SELECTOR = HostSelector.parse("foo=bar");

    private static final HostSelector BAZ_SELECTOR = HostSelector.parse("baz=qux");

    @Mock
    private MasterModel model;

    private DeploymentGroupResource resource;

    @Test
    public void testGetNonExistingDeploymentGroup() throws Exception {
        Mockito.when(model.getDeploymentGroup(ArgumentMatchers.anyString())).thenThrow(new DeploymentGroupDoesNotExistException(""));
        final Response response = resource.getDeploymentGroup("foobar");
        Assert.assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetDeploymentGroup() throws Exception {
        final JobId jobId = JobId.newBuilder().setName("my_job").setVersion("0.2").setHash("1234").build();
        final DeploymentGroup dg = DeploymentGroup.newBuilder().setName("foo").setHostSelectors(ImmutableList.of(DeploymentGroupResourceTest.ROLE_SELECTOR, DeploymentGroupResourceTest.FOO_SELECTOR)).setJobId(jobId).build();
        Mockito.when(model.getDeploymentGroup("foo")).thenReturn(dg);
        final Response response = resource.getDeploymentGroup("foo");
        Assert.assertEquals(OK.getStatusCode(), response.getStatus());
        Assert.assertEquals(dg, response.getEntity());
    }

    @Test
    public void testCreateNewDeploymentGroup() {
        final Response response = resource.createDeploymentGroup(Mockito.mock(DeploymentGroup.class));
        Assert.assertEquals(OK.getStatusCode(), response.getStatus());
        Assert.assertEquals(new com.spotify.helios.common.protocol.CreateDeploymentGroupResponse(Status.CREATED), response.getEntity());
    }

    @Test
    public void testCreateExistingSameDeploymentGroup() throws Exception {
        final DeploymentGroup dg = Mockito.mock(DeploymentGroup.class);
        Mockito.when(dg.getName()).thenReturn("foo");
        Mockito.when(dg.getHostSelectors()).thenReturn(Lists.newArrayList(DeploymentGroupResourceTest.FOO_SELECTOR));
        Mockito.doThrow(new DeploymentGroupExistsException("")).when(model).addDeploymentGroup(dg);
        Mockito.when(model.getDeploymentGroup("foo")).thenReturn(dg);
        final Response response = resource.createDeploymentGroup(dg);
        Assert.assertEquals(OK.getStatusCode(), response.getStatus());
        Assert.assertEquals(new com.spotify.helios.common.protocol.CreateDeploymentGroupResponse(Status.NOT_MODIFIED), response.getEntity());
    }

    @Test
    public void testCreateExistingConflictingDeploymentGroup() throws Exception {
        final DeploymentGroup dg = Mockito.mock(DeploymentGroup.class);
        Mockito.when(dg.getName()).thenReturn("foo");
        Mockito.when(dg.getHostSelectors()).thenReturn(Lists.newArrayList(DeploymentGroupResourceTest.FOO_SELECTOR));
        Mockito.doThrow(new DeploymentGroupExistsException("")).when(model).addDeploymentGroup(dg);
        final DeploymentGroup existing = Mockito.mock(DeploymentGroup.class);
        Mockito.when(existing.getHostSelectors()).thenReturn(Lists.newArrayList(DeploymentGroupResourceTest.BAZ_SELECTOR));
        Mockito.when(model.getDeploymentGroup("foo")).thenReturn(existing);
        final Response response = resource.createDeploymentGroup(dg);
        Assert.assertEquals(OK.getStatusCode(), response.getStatus());
        Assert.assertEquals(new com.spotify.helios.common.protocol.CreateDeploymentGroupResponse(Status.CONFLICT), response.getEntity());
    }

    @Test
    public void testRemoveDeploymentGroup() throws Exception {
        final Response response = resource.removeDeploymentGroup("foo");
        Assert.assertEquals(OK.getStatusCode(), response.getStatus());
        Assert.assertEquals(new com.spotify.helios.common.protocol.RemoveDeploymentGroupResponse(REMOVED), response.getEntity());
    }

    @Test
    public void testRemoveNonExistingDeploymentGroup() throws Exception {
        Mockito.doThrow(new DeploymentGroupDoesNotExistException("")).when(model).removeDeploymentGroup(ArgumentMatchers.anyString());
        final Response response = resource.removeDeploymentGroup("foo");
        Assert.assertEquals(OK.getStatusCode(), response.getStatus());
        Assert.assertEquals(new com.spotify.helios.common.protocol.RemoveDeploymentGroupResponse(DEPLOYMENT_GROUP_NOT_FOUND), response.getEntity());
    }

    @Test
    public void testRollingUpdateDeploymentGroupDoesNotExist() throws Exception {
        Mockito.doThrow(new DeploymentGroupDoesNotExistException("")).when(model).rollingUpdate(ArgumentMatchers.any(DeploymentGroup.class), ArgumentMatchers.any(JobId.class), ArgumentMatchers.any(RolloutOptions.class));
        final Response response = resource.rollingUpdate("foo", new com.spotify.helios.common.protocol.RollingUpdateRequest(new JobId("foo", "0.3", "1234"), RolloutOptions.getDefault()));
        Assert.assertEquals(OK.getStatusCode(), response.getStatus());
        Assert.assertEquals(new com.spotify.helios.common.protocol.RollingUpdateResponse(RollingUpdateResponse.Status.DEPLOYMENT_GROUP_NOT_FOUND), response.getEntity());
    }

    @Test
    public void testRollingUpdateJobDoesNotExist() throws Exception {
        Mockito.doThrow(new JobDoesNotExistException("")).when(model).rollingUpdate(ArgumentMatchers.any(DeploymentGroup.class), ArgumentMatchers.any(JobId.class), ArgumentMatchers.any(RolloutOptions.class));
        final Response response = resource.rollingUpdate("foo", new com.spotify.helios.common.protocol.RollingUpdateRequest(new JobId("foo", "0.3", "1234"), RolloutOptions.getDefault()));
        Assert.assertEquals(OK.getStatusCode(), response.getStatus());
        Assert.assertEquals(new com.spotify.helios.common.protocol.RollingUpdateResponse(JOB_NOT_FOUND), response.getEntity());
    }
}

