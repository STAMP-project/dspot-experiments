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
package org.apache.hadoop.service.launcher;


import Service.STATE.STARTED;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.service.BreakableService;
import org.apache.hadoop.service.launcher.testservices.ExceptionInExecuteLaunchableService;
import org.apache.hadoop.service.launcher.testservices.LaunchableRunningService;
import org.apache.hadoop.service.launcher.testservices.NoArgsAllowedService;
import org.apache.hadoop.service.launcher.testservices.RunningService;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the inner launcher methods.
 */
@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
public class TestServiceLauncherInnerMethods extends AbstractServiceLauncherTestBase {
    @Test
    public void testLaunchService() throws Throwable {
        ServiceLauncher<NoArgsAllowedService> launcher = launchService(NoArgsAllowedService.class, new Configuration());
        NoArgsAllowedService service = launcher.getService();
        Assert.assertNotNull(("null service from " + launcher), service);
        stop();
    }

    @Test
    public void testLaunchServiceArgs() throws Throwable {
        launchExpectingException(NoArgsAllowedService.class, new Configuration(), "arguments", EXIT_COMMAND_ARGUMENT_ERROR, "one", "two");
    }

    @Test
    public void testAccessLaunchedService() throws Throwable {
        ServiceLauncher<LaunchableRunningService> launcher = launchService(LaunchableRunningService.class, new Configuration());
        LaunchableRunningService service = launcher.getService();
        assertInState(service, STARTED);
        service.failInRun = true;
        service.setExitCode(EXIT_CONNECTIVITY_PROBLEM);
        Assert.assertEquals(EXIT_CONNECTIVITY_PROBLEM, service.execute());
    }

    @Test
    public void testLaunchThrowableRaised() throws Throwable {
        launchExpectingException(ExceptionInExecuteLaunchableService.class, new Configuration(), "java.lang.OutOfMemoryError", EXIT_EXCEPTION_THROWN, ExceptionInExecuteLaunchableService.ARG_THROWABLE);
    }

    @Test
    public void testBreakableServiceLifecycle() throws Throwable {
        ServiceLauncher<BreakableService> launcher = launchService(BreakableService.class, new Configuration());
        BreakableService service = launcher.getService();
        Assert.assertNotNull(("null service from " + launcher), service);
        stop();
    }

    @Test
    public void testConfigLoading() throws Throwable {
        ServiceLauncher<BreakableService> launcher = new ServiceLauncher("BreakableService");
        List<String> configurationsToCreate = launcher.getConfigurationsToCreate();
        Assert.assertTrue(((configurationsToCreate.size()) > 1));
        int created = launcher.loadConfigurationClasses();
        Assert.assertEquals(1, created);
    }
}

