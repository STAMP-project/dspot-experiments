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
package org.apache.hadoop.yarn.server.resourcemanager.webapp;


import NodeState.LOST;
import NodeState.UNHEALTHY;
import YarnApplicationState.ACCEPTED;
import YarnApplicationState.RUNNING;
import YarnWebParams.APP_STATE;
import YarnWebParams.NODE_STATE;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import java.io.IOException;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.hadoop.yarn.server.security.ApplicationACLsManager;
import org.apache.hadoop.yarn.util.StringHelper;
import org.apache.hadoop.yarn.webapp.Params;
import org.apache.hadoop.yarn.webapp.test.WebAppTests;
import org.junit.Assert;
import org.junit.Test;


public class TestRMWebApp {
    static final int GiB = 1024;// MiB


    @Test
    public void testControllerIndex() {
        Injector injector = WebAppTests.createMockInjector(TestRMWebApp.class, this, new Module() {
            @Override
            public void configure(Binder binder) {
                binder.bind(ApplicationACLsManager.class).toInstance(new ApplicationACLsManager(new Configuration()));
            }
        });
        RmController c = injector.getInstance(RmController.class);
        c.index();
        Assert.assertEquals("Applications", c.get(Params.TITLE, "unknown"));
    }

    @Test
    public void testView() {
        Injector injector = WebAppTests.createMockInjector(RMContext.class, TestRMWebApp.mockRMContext(15, 1, 2, (8 * (TestRMWebApp.GiB))), new Module() {
            @Override
            public void configure(Binder binder) {
                try {
                    ResourceManager mockRm = TestRMWebApp.mockRm(3, 1, 2, (8 * (TestRMWebApp.GiB)));
                    binder.bind(ResourceManager.class).toInstance(mockRm);
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            }
        });
        RmView rmViewInstance = injector.getInstance(RmView.class);
        rmViewInstance.set(APP_STATE, RUNNING.toString());
        rmViewInstance.render();
        WebAppTests.flushOutput(injector);
        rmViewInstance.set(APP_STATE, StringHelper.cjoin(ACCEPTED.toString(), RUNNING.toString()));
        rmViewInstance.render();
        WebAppTests.flushOutput(injector);
        Map<String, String> moreParams = rmViewInstance.context().requestContext().moreParams();
        String appsTableColumnsMeta = moreParams.get("ui.dataTables.apps.init");
        Assert.assertTrue(((appsTableColumnsMeta.indexOf("natural")) != (-1)));
    }

    @Test
    public void testNodesPage() {
        // 10 nodes. Two of each type.
        final RMContext rmContext = TestRMWebApp.mockRMContext(3, 2, 12, (8 * (TestRMWebApp.GiB)));
        Injector injector = WebAppTests.createMockInjector(RMContext.class, rmContext, new Module() {
            @Override
            public void configure(Binder binder) {
                try {
                    binder.bind(ResourceManager.class).toInstance(TestRMWebApp.mockRm(rmContext));
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            }
        });
        // All nodes
        NodesPage instance = injector.getInstance(NodesPage.class);
        instance.render();
        WebAppTests.flushOutput(injector);
        // Unhealthy nodes
        instance.moreParams().put(NODE_STATE, UNHEALTHY.toString());
        instance.render();
        WebAppTests.flushOutput(injector);
        // Lost nodes
        instance.moreParams().put(NODE_STATE, LOST.toString());
        instance.render();
        WebAppTests.flushOutput(injector);
    }
}

