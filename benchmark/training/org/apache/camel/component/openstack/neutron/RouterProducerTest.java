/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.openstack.neutron;


import AttachInterfaceType.SUBNET;
import NeutronConstants.ATTACH_INTERFACE;
import NeutronConstants.DETACH_INTERFACE;
import NeutronConstants.ITERFACE_TYPE;
import NeutronConstants.PORT_ID;
import NeutronConstants.ROUTER_ID;
import NeutronConstants.SUBNET_ID;
import NeutronConstants.TENANT_ID;
import OpenstackConstants.CREATE;
import OpenstackConstants.DELETE;
import OpenstackConstants.GET;
import OpenstackConstants.GET_ALL;
import OpenstackConstants.ID;
import OpenstackConstants.NAME;
import OpenstackConstants.OPERATION;
import OpenstackConstants.UPDATE;
import java.util.List;
import org.apache.camel.component.openstack.AbstractProducerTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.openstack4j.api.networking.RouterService;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.network.AttachInterfaceType;
import org.openstack4j.model.network.Router;
import org.openstack4j.model.network.RouterInterface;
import org.openstack4j.openstack.networking.domain.NeutronRouterInterface;


@RunWith(MockitoJUnitRunner.class)
public class RouterProducerTest extends NeutronProducerTestSupport {
    private Router dummyRouter;

    @Mock
    private Router testOSrouter;

    @Mock
    private RouterService routerService;

    @Captor
    private ArgumentCaptor<Router> routerCaptor;

    @Captor
    private ArgumentCaptor<String> routerIdCaptor;

    @Captor
    private ArgumentCaptor<String> subnetIdCaptor;

    @Captor
    private ArgumentCaptor<String> portIdCaptor;

    @Captor
    private ArgumentCaptor<AttachInterfaceType> itfTypeCaptor;

    @Test
    public void createTest() throws Exception {
        msg.setHeader(OPERATION, CREATE);
        msg.setHeader(NAME, dummyRouter.getName());
        msg.setHeader(TENANT_ID, dummyRouter.getTenantId());
        producer.process(exchange);
        Mockito.verify(routerService).create(routerCaptor.capture());
        assertEqualsRouter(dummyRouter, routerCaptor.getValue());
        Assert.assertNotNull(msg.getBody(Router.class).getId());
    }

    @Test
    public void getTest() throws Exception {
        final String routerID = "myRouterID";
        msg.setHeader(OPERATION, GET);
        msg.setHeader(ROUTER_ID, routerID);
        producer.process(exchange);
        Mockito.verify(routerService).get(routerIdCaptor.capture());
        Assert.assertEquals(routerID, routerIdCaptor.getValue());
        assertEqualsRouter(testOSrouter, msg.getBody(Router.class));
    }

    @Test
    public void getAllTest() throws Exception {
        msg.setHeader(OPERATION, GET_ALL);
        producer.process(exchange);
        final List<Router> result = msg.getBody(List.class);
        Assert.assertTrue(((result.size()) == 2));
        Assert.assertEquals(testOSrouter, result.get(0));
    }

    @Test
    public void updateTest() throws Exception {
        final String routerID = "myRouterID";
        msg.setHeader(OPERATION, UPDATE);
        final Router tmp = createRouter();
        final String newName = "newName";
        tmp.setName(newName);
        Mockito.when(routerService.update(ArgumentMatchers.any())).thenReturn(tmp);
        dummyRouter.setId(routerID);
        msg.setBody(dummyRouter);
        producer.process(exchange);
        Mockito.verify(routerService).update(routerCaptor.capture());
        assertEqualsRouter(dummyRouter, routerCaptor.getValue());
        Assert.assertNotNull(routerCaptor.getValue().getId());
        Assert.assertEquals(newName, msg.getBody(Router.class).getName());
    }

    @Test
    public void deleteTest() throws Exception {
        Mockito.when(routerService.delete(ArgumentMatchers.anyString())).thenReturn(ActionResponse.actionSuccess());
        final String routerID = "myRouterID";
        msg.setHeader(OPERATION, DELETE);
        msg.setHeader(ID, routerID);
        producer.process(exchange);
        Mockito.verify(routerService).delete(routerIdCaptor.capture());
        Assert.assertEquals(routerID, routerIdCaptor.getValue());
        Assert.assertFalse(msg.isFault());
        // in case of failure
        final String failureMessage = "fail";
        Mockito.when(routerService.delete(ArgumentMatchers.anyString())).thenReturn(ActionResponse.actionFailed(failureMessage, 404));
        producer.process(exchange);
        Assert.assertTrue(msg.isFault());
        Assert.assertTrue(msg.getBody(String.class).contains(failureMessage));
    }

    @Test
    public void detachTest() throws Exception {
        final String routerID = "myRouterID";
        final String portId = "port";
        final String subnetId = "subnet";
        final RouterInterface ifce = new NeutronRouterInterface(subnetId, portId);
        Mockito.when(routerService.detachInterface(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(ifce);
        msg.setHeader(OPERATION, DETACH_INTERFACE);
        msg.setHeader(ROUTER_ID, routerID);
        msg.setHeader(SUBNET_ID, subnetId);
        msg.setHeader(PORT_ID, portId);
        producer.process(exchange);
        Mockito.verify(routerService).detachInterface(routerIdCaptor.capture(), subnetIdCaptor.capture(), portIdCaptor.capture());
        Assert.assertEquals(routerID, routerIdCaptor.getValue());
        Assert.assertEquals(subnetId, subnetIdCaptor.getValue());
        Assert.assertEquals(portId, portIdCaptor.getValue());
        Assert.assertEquals(ifce, msg.getBody(RouterInterface.class));
    }

    @Test
    public void attachTest() throws Exception {
        final String routerID = "myRouterID";
        final String subnetId = "subnet";
        final RouterInterface ifce = new NeutronRouterInterface(subnetId, null);
        Mockito.when(routerService.attachInterface(ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.anyString())).thenReturn(ifce);
        msg.setHeader(OPERATION, ATTACH_INTERFACE);
        msg.setHeader(ROUTER_ID, routerID);
        msg.setHeader(SUBNET_ID, subnetId);
        msg.setHeader(ITERFACE_TYPE, SUBNET);
        producer.process(exchange);
        Mockito.verify(routerService).attachInterface(routerIdCaptor.capture(), itfTypeCaptor.capture(), subnetIdCaptor.capture());
        Assert.assertEquals(routerID, routerIdCaptor.getValue());
        Assert.assertEquals(SUBNET, itfTypeCaptor.getValue());
        Assert.assertEquals(subnetId, subnetIdCaptor.getValue());
        Assert.assertEquals(ifce, msg.getBody(RouterInterface.class));
    }
}

