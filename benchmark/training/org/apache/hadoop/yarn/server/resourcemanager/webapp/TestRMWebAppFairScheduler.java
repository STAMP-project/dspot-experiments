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


import RMAppState.ACCEPTED;
import RMAppState.FINAL_SAVING;
import RMAppState.FINISHED;
import RMAppState.NEW;
import RMAppState.NEW_SAVING;
import RMAppState.RUNNING;
import RMAppState.SUBMITTED;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppState;
import org.apache.hadoop.yarn.webapp.test.WebAppTests;
import org.junit.Assert;
import org.junit.Test;


public class TestRMWebAppFairScheduler {
    @Test
    public void testFairSchedulerWebAppPage() {
        List<RMAppState> appStates = Arrays.asList(NEW, NEW_SAVING, SUBMITTED);
        final RMContext rmContext = TestRMWebAppFairScheduler.mockRMContext(appStates);
        Injector injector = WebAppTests.createMockInjector(RMContext.class, rmContext, new Module() {
            @Override
            public void configure(Binder binder) {
                try {
                    ResourceManager mockRmWithFairScheduler = TestRMWebAppFairScheduler.mockRm(rmContext);
                    binder.bind(ResourceManager.class).toInstance(mockRmWithFairScheduler);
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            }
        });
        FairSchedulerPage fsViewInstance = injector.getInstance(FairSchedulerPage.class);
        fsViewInstance.render();
        WebAppTests.flushOutput(injector);
    }

    /**
     * Testing inconsistent state between AbstractYarnScheduler#applications and
     *  RMContext#applications
     */
    @Test
    public void testFairSchedulerWebAppPageInInconsistentState() {
        List<RMAppState> appStates = Arrays.asList(NEW, NEW_SAVING, SUBMITTED, RUNNING, FINAL_SAVING, ACCEPTED, FINISHED);
        final RMContext rmContext = TestRMWebAppFairScheduler.mockRMContext(appStates);
        Injector injector = WebAppTests.createMockInjector(RMContext.class, rmContext, new Module() {
            @Override
            public void configure(Binder binder) {
                try {
                    ResourceManager mockRmWithFairScheduler = TestRMWebAppFairScheduler.mockRmWithApps(rmContext);
                    binder.bind(ResourceManager.class).toInstance(mockRmWithFairScheduler);
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            }
        });
        FairSchedulerPage fsViewInstance = injector.getInstance(FairSchedulerPage.class);
        try {
            fsViewInstance.render();
        } catch (Exception e) {
            Assert.fail(("Failed to render FairSchedulerPage: " + (StringUtils.stringifyException(e))));
        }
        WebAppTests.flushOutput(injector);
    }
}

