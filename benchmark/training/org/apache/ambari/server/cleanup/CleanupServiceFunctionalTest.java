/**
 * * Licensed to the Apache Software Foundation (ASF) under one
 *  * or more contributor license agreements.  See the NOTICE file
 *  * distributed with this work for additional information
 *  * regarding copyright ownership.  The ASF licenses this file
 *  * to you under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 */
package org.apache.ambari.server.cleanup;


import com.google.inject.Injector;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Functional test for the Cleanup process.
 */
@Ignore("Ignored in order not to run with the unit tests as it's time consuming. Should be part of a functional test suit.")
public class CleanupServiceFunctionalTest {
    private static Injector injector;

    @Test
    public void testIOCContext() throws Exception {
        // WHEN
        CleanupServiceImpl cleanupService = CleanupServiceFunctionalTest.injector.getInstance(CleanupServiceImpl.class);
        // THEN
        Assert.assertNotNull("The cleanupService instance should be present in the IoC context", cleanupService);
        // Assert.assertFalse("The cleanup registry shouldn't be empty", cleanupService.showCleanupRegistry().isEmpty());
    }

    @Test
    public void testRunCleanup() throws Exception {
        // GIVEN
        CleanupService<TimeBasedCleanupPolicy> cleanupService = CleanupServiceFunctionalTest.injector.getInstance(CleanupServiceImpl.class);
        TimeBasedCleanupPolicy cleanupPolicy = new TimeBasedCleanupPolicy("cluster-1", 1455891250758L);
        // WHEN
        cleanupService.cleanup(cleanupPolicy);
        // THEN
        // todo assert eg.:on the amount of deleted rows
    }

    @Test
    public void testServicesShouldBeInSingletonScope() throws Exception {
        // GIVEN
        // the cleanup guice context is build
        // WHEN
        CleanupService cleanupService1 = CleanupServiceFunctionalTest.injector.getInstance(CleanupServiceImpl.class);
        CleanupService cleanupService2 = CleanupServiceFunctionalTest.injector.getInstance(CleanupServiceImpl.class);
        // THEN
        Assert.assertEquals("The ChainedCleanupService is not in Singleton scope!", cleanupService1, cleanupService2);
        // Assert.assertEquals("Registered services are not is not in Singleton scope!", cleanupService1.showCleanupRegistry(), cleanupService2.showCleanupRegistry());
    }
}

