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
package com.netflix.genie.web.properties;


import com.netflix.genie.test.categories.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Unit tests for the JobsProperties class.
 *
 * @author tgianos
 * @since 3.0.0
 */
@Category(UnitTest.class)
public class JobsPropertiesUnitTests {
    private JobsCleanupProperties cleanup;

    private JobsMemoryProperties memory;

    private JobsForwardingProperties forwarding;

    private JobsLocationsProperties locations;

    private JobsMaxProperties max;

    private JobsUsersProperties users;

    private ExponentialBackOffTriggerProperties completionBackOff;

    private JobsActiveLimitProperties activeLimit;

    private JobsProperties properties;

    /**
     * Make sure we can construct.
     */
    @Test
    public void canConstruct() {
        Assert.assertNotNull(this.properties.getCleanup());
        Assert.assertNotNull(this.properties.getMemory());
        Assert.assertNotNull(this.properties.getForwarding());
        Assert.assertNotNull(this.properties.getLocations());
        Assert.assertNotNull(this.properties.getMax());
        Assert.assertNotNull(this.properties.getUsers());
        Assert.assertNotNull(this.properties.getCompletionCheckBackOff());
        Assert.assertNotNull(this.properties.getActiveLimit());
    }

    /**
     * Make sure all the setters work.
     */
    @Test
    public void canSet() {
        this.properties.setCleanup(cleanup);
        this.properties.setForwarding(forwarding);
        this.properties.setLocations(locations);
        this.properties.setMax(max);
        this.properties.setMemory(memory);
        this.properties.setUsers(users);
        this.properties.setCompletionCheckBackOff(completionBackOff);
        this.properties.setActiveLimit(activeLimit);
    }
}

