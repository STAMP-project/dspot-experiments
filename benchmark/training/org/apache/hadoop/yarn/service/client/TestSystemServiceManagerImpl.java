/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.service.client;


import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.service.api.records.Service;
import org.apache.hadoop.yarn.service.conf.SliderExitCodes;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test class for system service manager.
 */
public class TestSystemServiceManagerImpl {
    private static final Logger LOG = LoggerFactory.getLogger(TestSystemServiceManagerImpl.class);

    private SystemServiceManagerImpl systemService;

    private Configuration conf;

    private String resourcePath = "system-services";

    private String[] users = new String[]{ "user1", "user2" };

    private static Map<String, Set<String>> loadedServices = new HashMap<>();

    private static Map<String, Set<String>> savedServices = new HashMap<>();

    private static Map<String, Set<String>> submittedServices = new HashMap<>();

    @Test
    public void testSystemServiceSubmission() throws Exception {
        systemService.start();
        /* verify for ignored sevices count */
        Map<String, Integer> ignoredUserServices = systemService.getIgnoredUserServices();
        Assert.assertEquals(1, ignoredUserServices.size());
        Assert.assertTrue("User user1 doesn't exist.", ignoredUserServices.containsKey(users[0]));
        int count = ignoredUserServices.get(users[0]);
        Assert.assertEquals(1, count);
        Assert.assertEquals(1, systemService.getBadFileNameExtensionSkipCounter());
        Assert.assertEquals(1, systemService.getBadDirSkipCounter());
        Map<String, Set<Service>> userServices = systemService.getSyncUserServices();
        Assert.assertEquals(TestSystemServiceManagerImpl.loadedServices.size(), userServices.size());
        verifyForScannedUserServices(userServices);
        verifyForLaunchedUserServices();
        // 2nd time launch service to handle if service exist scenario
        systemService.launchUserService(userServices);
        verifyForLaunchedUserServices();
        // verify start of stopped services
        TestSystemServiceManagerImpl.submittedServices.clear();
        systemService.launchUserService(userServices);
        verifyForLaunchedUserServices();
    }

    class TestServiceClient extends ServiceClient {
        @Override
        protected void serviceStart() throws Exception {
            // do nothing
        }

        @Override
        protected void serviceStop() throws Exception {
            // do nothing
        }

        @Override
        protected void serviceInit(Configuration configuration) throws Exception {
            // do nothing
        }

        @Override
        public int actionBuild(Service service) throws IOException, YarnException {
            String userName = UserGroupInformation.getCurrentUser().getShortUserName();
            Set<String> services = TestSystemServiceManagerImpl.savedServices.get(userName);
            if (services == null) {
                services = new HashSet<>();
                TestSystemServiceManagerImpl.savedServices.put(userName, services);
            }
            if (services.contains(service.getName())) {
                String message = ("Failed to save service " + (service.getName())) + ", because it already exists.";
                throw new org.apache.hadoop.yarn.service.exceptions.SliderException(SliderExitCodes.EXIT_INSTANCE_EXISTS, message);
            }
            services.add(service.getName());
            return 0;
        }

        @Override
        public ApplicationId actionStartAndGetId(String serviceName) throws IOException, YarnException {
            String userName = UserGroupInformation.getCurrentUser().getShortUserName();
            Set<String> services = TestSystemServiceManagerImpl.submittedServices.get(userName);
            if (services == null) {
                services = new HashSet<>();
                TestSystemServiceManagerImpl.submittedServices.put(userName, services);
            }
            if (services.contains(serviceName)) {
                String message = ("Failed to create service " + serviceName) + ", because it is already running.";
                throw new YarnException(message);
            }
            services.add(serviceName);
            return ApplicationId.newInstance(System.currentTimeMillis(), 1);
        }
    }
}

