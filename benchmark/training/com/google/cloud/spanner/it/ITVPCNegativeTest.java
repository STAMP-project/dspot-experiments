/**
 * Copyright 2019 Google LLC
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
package com.google.cloud.spanner.it;


import com.google.cloud.spanner.DatabaseAdminClient;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.InstanceAdminClient;
import com.google.cloud.spanner.IntegrationTest;
import com.google.cloud.spanner.KeySet;
import com.google.cloud.spanner.Options;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Integration tests for VPC-SC
 */
@Category(IntegrationTest.class)
@RunWith(JUnit4.class)
public class ITVPCNegativeTest {
    private static final String IN_VPCSC_TEST = System.getenv("GOOGLE_CLOUD_TESTS_IN_VPCSC");

    private static final String OUTSIDE_VPC_PROJECT = System.getenv("GOOGLE_CLOUD_TESTS_VPCSC_OUTSIDE_PERIMETER_PROJECT");

    private Spanner spanner;

    private InstanceAdminClient instanceAdminClient;

    private DatabaseAdminClient databaseAdminClient;

    private DatabaseClient databaseClient;

    @Test
    public void deniedListInstanceConfigs() {
        try {
            instanceAdminClient.listInstanceConfigs();
            Assert.fail("Expected PERMISSION_DENIED SpannerException");
        } catch (SpannerException e) {
            checkExceptionForVPCError(e);
        }
    }

    @Test
    public void deniedGetInstanceConfig() {
        try {
            instanceAdminClient.getInstanceConfig("nonexistent-configs");
            Assert.fail("Expected PERMISSION_DENIED SpannerException");
        } catch (SpannerException e) {
            checkExceptionForVPCError(e);
        }
    }

    @Test
    public void deniedListInstances() {
        try {
            instanceAdminClient.listInstances();
            Assert.fail("Expected PERMISSION_DENIED SpannerException");
        } catch (SpannerException e) {
            checkExceptionForVPCError(e);
        }
    }

    @Test
    public void deniedGetInstance() {
        try {
            instanceAdminClient.getInstance("non-existent");
            Assert.fail("Expected PERMISSION_DENIED SpannerException");
        } catch (SpannerException e) {
            checkExceptionForVPCError(e);
        }
    }

    @Test
    public void deniedListDatabases() {
        try {
            databaseAdminClient.listDatabases("nonexistent-instance", Options.pageSize(1));
            Assert.fail("Expected PERMISSION_DENIED SpannerException");
        } catch (SpannerException e) {
            checkExceptionForVPCError(e);
        }
    }

    @Test
    public void deniedGetDatabase() {
        try {
            databaseAdminClient.getDatabase("nonexistent-instance", "nonexistent-database");
            Assert.fail("Expected PERMISSION_DENIED SpannerException");
        } catch (SpannerException e) {
            checkExceptionForVPCError(e);
        }
    }

    @Test
    public void deniedRead() {
        try {
            // Tests that the initial create session request returns a permission denied.
            databaseClient.singleUse().read("nonexistent-table", KeySet.all(), Arrays.asList("nonexistent-col"));
        } catch (SpannerException e) {
            checkExceptionForVPCError(e);
        }
    }
}

