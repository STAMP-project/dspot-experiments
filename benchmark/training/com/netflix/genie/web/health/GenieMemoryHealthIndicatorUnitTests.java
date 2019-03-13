/**
 * Copyright 2016 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package com.netflix.genie.web.health;


import Status.OUT_OF_SERVICE;
import Status.UP;
import com.netflix.genie.test.categories.UnitTest;
import com.netflix.genie.web.properties.JobsProperties;
import com.netflix.genie.web.services.JobMetricsService;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;


/**
 * Unit tests for GenieHealthIndicator.
 *
 * @author tgianos
 * @since 3.0.0
 */
@Category(UnitTest.class)
public class GenieMemoryHealthIndicatorUnitTests {
    private static final int MAX_SYSTEM_MEMORY = 10240;

    private static final int DEFAULT_JOB_MEMORY = 1024;

    private static final int MAX_JOB_MEMORY = 5120;

    private GenieMemoryHealthIndicator genieMemoryHealthIndicator;

    private JobMetricsService jobMetricsService;

    private JobsProperties jobsProperties;

    /**
     * Test to make sure the various health conditions are met.
     */
    @Test
    public void canGetHealth() {
        Mockito.when(this.jobMetricsService.getNumActiveJobs()).thenReturn(1, 2, 3);
        Mockito.when(this.jobMetricsService.getUsedMemory()).thenReturn(1024, 2048, (((GenieMemoryHealthIndicatorUnitTests.MAX_SYSTEM_MEMORY) - (GenieMemoryHealthIndicatorUnitTests.MAX_JOB_MEMORY)) + 1));
        Assert.assertThat(this.genieMemoryHealthIndicator.health().getStatus(), Matchers.is(UP));
        Assert.assertThat(this.genieMemoryHealthIndicator.health().getStatus(), Matchers.is(UP));
        Assert.assertThat(this.genieMemoryHealthIndicator.health().getStatus(), Matchers.is(OUT_OF_SERVICE));
    }

    /**
     * Test to make sure invalid property values for job memory default/max don't result in exceptions.
     */
    @Test
    public void canGetHealthWithInvalidJobSettings() {
        jobsProperties.getMemory().setDefaultJobMemory(0);
        jobsProperties.getMemory().setMaxJobMemory(0);
        final Map<String, Object> healthDetails = genieMemoryHealthIndicator.health().getDetails();
        Assert.assertEquals(0, healthDetails.get("availableDefaultJobCapacity"));
        Assert.assertEquals(0, healthDetails.get("availableMaxJobCapacity"));
    }

    /**
     * Test to make at full capacity we don't show a negative capacity.
     */
    @Test
    public void nonNegativeJobsCapacity() {
        Mockito.when(this.jobMetricsService.getUsedMemory()).thenReturn(((GenieMemoryHealthIndicatorUnitTests.MAX_SYSTEM_MEMORY) + 100));
        final Map<String, Object> healthDetails = genieMemoryHealthIndicator.health().getDetails();
        Assert.assertEquals(0, healthDetails.get("availableDefaultJobCapacity"));
        Assert.assertEquals(0, healthDetails.get("availableMaxJobCapacity"));
    }
}

