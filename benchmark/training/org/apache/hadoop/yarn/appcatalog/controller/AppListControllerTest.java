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
import org.apache.hadoop.yarn.service.api.records.Service;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Unit test for AppListController.
 */
public class AppListControllerTest {
    private AppListController controller;

    @Test
    public void testGetList() throws Exception {
        AppListController ac = Mockito.mock(AppListController.class);
        List<AppEntry> actual = new ArrayList<AppEntry>();
        Mockito.when(ac.getList()).thenReturn(actual);
        final List<AppEntry> result = ac.getList();
        Assert.assertEquals(result, actual);
    }

    @Test
    public void testDelete() throws Exception {
        String id = "application 1";
        AppListController ac = Mockito.mock(AppListController.class);
        Response expected = Response.ok().build();
        Mockito.when(ac.delete(id, id)).thenReturn(Response.ok().build());
        final Response actual = ac.delete(id, id);
        Assert.assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    public void testDeploy() throws Exception {
        String id = "application 1";
        AppListController ac = Mockito.mock(AppListController.class);
        Service service = new Service();
        Response expected = Response.ok().build();
        Mockito.when(ac.deploy(id, service)).thenReturn(Response.ok().build());
        final Response actual = ac.deploy(id, service);
        Assert.assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    public void testPathAnnotation() throws Exception {
        Assert.assertNotNull(this.controller.getClass().getAnnotations());
        MatcherAssert.assertThat("The controller has the annotation Path", this.controller.getClass().isAnnotationPresent(Path.class));
        final Path path = this.controller.getClass().getAnnotation(Path.class);
        MatcherAssert.assertThat("The path is /app_list", path.value(), Is.is("/app_list"));
    }
}

