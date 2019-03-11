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
package org.apache.beam.runners.dataflow.options;


import DataflowPipelineOptions.FlexResourceSchedulingGoal.COST_OPTIMIZED;
import DataflowPipelineOptions.FlexResourceSchedulingGoal.UNSPECIFIED;
import java.util.List;
import org.apache.beam.sdk.extensions.gcp.storage.NoopPathValidator;
import org.apache.beam.sdk.io.FileSystems;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.testing.ResetDateTimeProvider;
import org.apache.beam.sdk.testing.RestoreSystemProperties;
import org.apache.beam.vendor.guava.v20_0.com.google.common.base.Splitter;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.ThrowableMessageMatcher;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link DataflowPipelineOptions}.
 */
@RunWith(JUnit4.class)
public class DataflowPipelineOptionsTest {
    @Rule
    public TestRule restoreSystemProperties = new RestoreSystemProperties();

    @Rule
    public ResetDateTimeProvider resetDateTimeProviderRule = new ResetDateTimeProvider();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testJobNameIsSet() {
        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        options.setJobName("TestJobName");
        Assert.assertEquals("TestJobName", options.getJobName());
    }

    @Test
    public void testUserNameIsNotSet() {
        resetDateTimeProviderRule.setDateTimeFixed("2014-12-08T19:07:06.698Z");
        System.getProperties().remove("user.name");
        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        options.setAppName("TestApplication");
        List<String> nameComponents = Splitter.on('-').splitToList(options.getJobName());
        Assert.assertEquals(4, nameComponents.size());
        Assert.assertEquals("testapplication", nameComponents.get(0));
        Assert.assertEquals("", nameComponents.get(1));
        Assert.assertEquals("1208190706", nameComponents.get(2));
        // Verify the last component is a hex integer (unsigned).
        Long.parseLong(nameComponents.get(3), 16);
        Assert.assertTrue(((options.getJobName().length()) <= 40));
    }

    @Test
    public void testAppNameAndUserNameAreLong() {
        resetDateTimeProviderRule.setDateTimeFixed("2014-12-08T19:07:06.698Z");
        System.getProperties().put("user.name", "abcdeabcdeabcdeabcdeabcdeabcde");
        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        options.setAppName("1234567890123456789012345678901234567890");
        List<String> nameComponents = Splitter.on('-').splitToList(options.getJobName());
        Assert.assertEquals(4, nameComponents.size());
        Assert.assertEquals("a234567890123456789012345678901234567890", nameComponents.get(0));
        Assert.assertEquals("abcdeabcdeabcdeabcdeabcdeabcde", nameComponents.get(1));
        Assert.assertEquals("1208190706", nameComponents.get(2));
        // Verify the last component is a hex integer (unsigned).
        Long.parseLong(nameComponents.get(3), 16);
    }

    @Test
    public void testAppNameIsLong() {
        resetDateTimeProviderRule.setDateTimeFixed("2014-12-08T19:07:06.698Z");
        System.getProperties().put("user.name", "abcde");
        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        options.setAppName("1234567890123456789012345678901234567890");
        List<String> nameComponents = Splitter.on('-').splitToList(options.getJobName());
        Assert.assertEquals(4, nameComponents.size());
        Assert.assertEquals("a234567890123456789012345678901234567890", nameComponents.get(0));
        Assert.assertEquals("abcde", nameComponents.get(1));
        Assert.assertEquals("1208190706", nameComponents.get(2));
        // Verify the last component is a hex integer (unsigned).
        Long.parseLong(nameComponents.get(3), 16);
    }

    @Test
    public void testUserNameIsLong() {
        resetDateTimeProviderRule.setDateTimeFixed("2014-12-08T19:07:06.698Z");
        System.getProperties().put("user.name", "abcdeabcdeabcdeabcdeabcdeabcde");
        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        options.setAppName("1234567890");
        List<String> nameComponents = Splitter.on('-').splitToList(options.getJobName());
        Assert.assertEquals(4, nameComponents.size());
        Assert.assertEquals("a234567890", nameComponents.get(0));
        Assert.assertEquals("abcdeabcdeabcdeabcdeabcdeabcde", nameComponents.get(1));
        Assert.assertEquals("1208190706", nameComponents.get(2));
        // Verify the last component is a hex integer (unsigned).
        Long.parseLong(nameComponents.get(3), 16);
    }

    @Test
    public void testUtf8UserNameAndApplicationNameIsNormalized() {
        resetDateTimeProviderRule.setDateTimeFixed("2014-12-08T19:07:06.698Z");
        System.getProperties().put("user.name", "?i ?nt??n???n?l ");
        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        options.setAppName("f??n?t?k ?so?si?e??n");
        List<String> nameComponents = Splitter.on('-').splitToList(options.getJobName());
        Assert.assertEquals(4, nameComponents.size());
        Assert.assertEquals("f00n0t0k00so0si0e00n", nameComponents.get(0));
        Assert.assertEquals("0i00nt00n000n0l0", nameComponents.get(1));
        Assert.assertEquals("1208190706", nameComponents.get(2));
        // Verify the last component is a hex integer (unsigned).
        Long.parseLong(nameComponents.get(3), 16);
    }

    @Test
    public void testStagingLocation() {
        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        options.setPathValidatorClass(NoopPathValidator.class);
        options.setTempLocation("gs://temp_location");
        options.setStagingLocation("gs://staging_location");
        Assert.assertEquals("gs://temp_location", options.getGcpTempLocation());
        Assert.assertEquals("gs://staging_location", options.getStagingLocation());
    }

    @Test
    public void testDefaultToTempLocation() {
        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        FileSystems.setDefaultPipelineOptions(options);
        options.setPathValidatorClass(NoopPathValidator.class);
        options.setTempLocation("gs://temp_location/");
        Assert.assertEquals("gs://temp_location/", options.getGcpTempLocation());
        Assert.assertEquals("gs://temp_location/staging/", options.getStagingLocation());
    }

    @Test
    public void testDefaultToGcpTempLocation() {
        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        FileSystems.setDefaultPipelineOptions(options);
        options.setPathValidatorClass(NoopPathValidator.class);
        options.setTempLocation("gs://temp_location/");
        options.setGcpTempLocation("gs://gcp_temp_location/");
        Assert.assertEquals("gs://gcp_temp_location/staging/", options.getStagingLocation());
    }

    @Test
    public void testDefaultFlexRSGoal() {
        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        Assert.assertEquals(UNSPECIFIED, options.getFlexRSGoal());
        options.setFlexRSGoal(COST_OPTIMIZED);
        Assert.assertEquals(COST_OPTIMIZED, options.getFlexRSGoal());
    }

    @Test
    public void testDefaultNoneGcsTempLocation() {
        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        options.setTempLocation("file://temp_location");
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(("Error constructing default value for stagingLocation: " + "failed to retrieve gcpTempLocation."));
        thrown.expectCause(ThrowableMessageMatcher.hasMessage(Matchers.containsString("Error constructing default value for gcpTempLocation")));
        options.getStagingLocation();
    }

    @Test
    public void testDefaultInvalidGcpTempLocation() {
        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        options.setGcpTempLocation("file://temp_location");
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(("Error constructing default value for stagingLocation: gcpTempLocation is not" + " a valid GCS path"));
        thrown.expectCause(ThrowableMessageMatcher.hasMessage(Matchers.containsString("Expected a valid 'gs://' path")));
        options.getStagingLocation();
    }

    @Test
    public void testDefaultStagingLocationUnset() {
        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        options.setProject("");
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Error constructing default value for stagingLocation");
        options.getStagingLocation();
    }
}

