/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.appcatalog.controller;


import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import org.apache.hadoop.yarn.appcatalog.model.AppEntry;
import org.apache.hadoop.yarn.service.api.records.Component;
import org.apache.hadoop.yarn.service.api.records.Container;
import org.apache.hadoop.yarn.service.api.records.Service;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Unit test for AppDetailsController.
 */
public class AppDetailsControllerTest {
    private AppDetailsController controller;

    @Test
    public void testGetDetails() throws Exception {
        String id = "application 1";
        AppDetailsController ac = Mockito.mock(AppDetailsController.class);
        AppEntry actual = new AppEntry();
        actual.setName(id);
        Mockito.when(ac.getDetails(id)).thenReturn(actual);
        final AppEntry result = ac.getDetails(id);
        Assert.assertEquals(result, actual);
    }

    @Test
    public void testGetStatus() throws Exception {
        String id = "application 1";
        AppDetailsController ac = Mockito.mock(AppDetailsController.class);
        Service yarnfile = new Service();
        Component comp = new Component();
        Container c = new Container();
        c.setId("container-1");
        List<Container> containers = new ArrayList<Container>();
        containers.add(c);
        comp.setContainers(containers);
        yarnfile.addComponent(comp);
        AppEntry actual = new AppEntry();
        actual.setName(id);
        actual.setYarnfile(yarnfile);
        Mockito.when(ac.getStatus(id)).thenReturn(actual);
        final AppEntry result = ac.getStatus(id);
        Assert.assertEquals(result, actual);
    }

    @Test
    public void testStopApp() throws Exception {
        String id = "application 1";
        AppDetailsController ac = Mockito.mock(AppDetailsController.class);
        Service yarnfile = new Service();
        Component comp = new Component();
        Container c = new Container();
        c.setId("container-1");
        List<Container> containers = new ArrayList<Container>();
        containers.add(c);
        comp.setContainers(containers);
        yarnfile.addComponent(comp);
        Response expected = Response.ok().build();
        Mockito.when(ac.stopApp(id)).thenReturn(Response.ok().build());
        final Response actual = ac.stopApp(id);
        Assert.assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    public void testRestartApp() throws Exception {
        String id = "application 1";
        AppDetailsController ac = Mockito.mock(AppDetailsController.class);
        Service yarnfile = new Service();
        Component comp = new Component();
        Container c = new Container();
        c.setId("container-1");
        List<Container> containers = new ArrayList<Container>();
        containers.add(c);
        comp.setContainers(containers);
        yarnfile.addComponent(comp);
        Response expected = Response.ok().build();
        Mockito.when(ac.restartApp(id)).thenReturn(Response.ok().build());
        final Response actual = ac.restartApp(id);
        Assert.assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    public void testPathAnnotation() throws Exception {
        Assert.assertNotNull(this.controller.getClass().getAnnotations());
        MatcherAssert.assertThat("The controller has the annotation Path", this.controller.getClass().isAnnotationPresent(Path.class));
        final Path path = this.controller.getClass().getAnnotation(Path.class);
        MatcherAssert.assertThat("The path is /app_details", path.value(), Is.is("/app_details"));
    }
}

