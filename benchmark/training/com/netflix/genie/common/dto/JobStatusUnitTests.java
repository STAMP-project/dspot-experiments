/**
 * Copyright 2015 Netflix, Inc.
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
package com.netflix.genie.common.dto;


import JobStatus.ACCEPTED;
import JobStatus.CLAIMED;
import JobStatus.FAILED;
import JobStatus.INIT;
import JobStatus.INVALID;
import JobStatus.KILLED;
import JobStatus.RESERVED;
import JobStatus.RESOLVED;
import JobStatus.RUNNING;
import JobStatus.SUCCEEDED;
import com.netflix.genie.common.exceptions.GeniePreconditionException;
import com.netflix.genie.test.categories.UnitTest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Tests for the JobStatus enum.
 *
 * @author tgianos
 * @since 2.0.0
 */
@Category(UnitTest.class)
public class JobStatusUnitTests {
    /**
     * Tests whether a valid job status is parsed correctly.
     *
     * @throws GeniePreconditionException
     * 		If any precondition isn't met.
     */
    @Test
    public void testValidJobStatus() throws GeniePreconditionException {
        Assert.assertEquals(RUNNING, JobStatus.parse(RUNNING.name().toLowerCase()));
        Assert.assertEquals(FAILED, JobStatus.parse(FAILED.name().toLowerCase()));
        Assert.assertEquals(KILLED, JobStatus.parse(KILLED.name().toLowerCase()));
        Assert.assertEquals(INIT, JobStatus.parse(INIT.name().toLowerCase()));
        Assert.assertEquals(SUCCEEDED, JobStatus.parse(SUCCEEDED.name().toLowerCase()));
        Assert.assertEquals(RESERVED, JobStatus.parse(RESERVED.name().toLowerCase()));
        Assert.assertEquals(RESOLVED, JobStatus.parse(RESOLVED.name().toLowerCase()));
        Assert.assertEquals(CLAIMED, JobStatus.parse(CLAIMED.name().toLowerCase()));
        Assert.assertEquals(ACCEPTED, JobStatus.parse(ACCEPTED.name().toLowerCase()));
    }

    /**
     * Tests whether an invalid job status returns null.
     *
     * @throws GeniePreconditionException
     * 		If any precondition isn't met.
     */
    @Test(expected = GeniePreconditionException.class)
    public void testInvalidJobStatus() throws GeniePreconditionException {
        JobStatus.parse("DOES_NOT_EXIST");
    }

    /**
     * Tests whether an invalid application status throws exception.
     *
     * @throws GeniePreconditionException
     * 		If any precondition isn't met.
     */
    @Test(expected = GeniePreconditionException.class)
    public void testBlankJobStatus() throws GeniePreconditionException {
        JobStatus.parse("  ");
    }

    /**
     * Test to make sure isActive is working properly.
     */
    @Test
    public void testIsActive() {
        Assert.assertTrue(RUNNING.isActive());
        Assert.assertTrue(INIT.isActive());
        Assert.assertFalse(FAILED.isActive());
        Assert.assertFalse(INVALID.isActive());
        Assert.assertFalse(KILLED.isActive());
        Assert.assertFalse(SUCCEEDED.isActive());
        Assert.assertTrue(RESERVED.isActive());
        Assert.assertTrue(RESOLVED.isActive());
        Assert.assertTrue(CLAIMED.isActive());
        Assert.assertTrue(ACCEPTED.isActive());
    }

    /**
     * Test to make sure isFinished is working properly.
     */
    @Test
    public void testIsFinished() {
        Assert.assertFalse(RUNNING.isFinished());
        Assert.assertFalse(INIT.isFinished());
        Assert.assertTrue(FAILED.isFinished());
        Assert.assertTrue(INVALID.isFinished());
        Assert.assertTrue(KILLED.isFinished());
        Assert.assertTrue(SUCCEEDED.isFinished());
        Assert.assertFalse(RESERVED.isFinished());
        Assert.assertFalse(RESOLVED.isFinished());
        Assert.assertFalse(CLAIMED.isFinished());
        Assert.assertFalse(ACCEPTED.isFinished());
    }

    /**
     * Test to make sure isResolvable is working properly.
     */
    @Test
    public void testIsResolvable() {
        Assert.assertFalse(RUNNING.isResolvable());
        Assert.assertFalse(INIT.isResolvable());
        Assert.assertFalse(FAILED.isResolvable());
        Assert.assertFalse(INVALID.isResolvable());
        Assert.assertFalse(KILLED.isResolvable());
        Assert.assertFalse(SUCCEEDED.isResolvable());
        Assert.assertTrue(RESERVED.isResolvable());
        Assert.assertFalse(RESOLVED.isResolvable());
        Assert.assertFalse(CLAIMED.isResolvable());
        Assert.assertFalse(ACCEPTED.isResolvable());
    }

    /**
     * Test to make sure isClaimable is working properly.
     */
    @Test
    public void testIsClaimable() {
        Assert.assertFalse(RUNNING.isClaimable());
        Assert.assertFalse(INIT.isClaimable());
        Assert.assertFalse(FAILED.isClaimable());
        Assert.assertFalse(INVALID.isClaimable());
        Assert.assertFalse(KILLED.isClaimable());
        Assert.assertFalse(SUCCEEDED.isClaimable());
        Assert.assertFalse(RESERVED.isClaimable());
        Assert.assertTrue(RESOLVED.isClaimable());
        Assert.assertFalse(CLAIMED.isClaimable());
        Assert.assertTrue(ACCEPTED.isClaimable());
    }

    /**
     * Make sure all the active statuses are present in the set.
     */
    @Test
    public void testGetActivesStatuses() {
        Assert.assertThat(JobStatus.getActiveStatuses().size(), Matchers.is(6));
        Assert.assertTrue(JobStatus.getActiveStatuses().contains(INIT));
        Assert.assertTrue(JobStatus.getActiveStatuses().contains(RUNNING));
        Assert.assertTrue(JobStatus.getActiveStatuses().contains(RESERVED));
        Assert.assertTrue(JobStatus.getActiveStatuses().contains(RESOLVED));
        Assert.assertTrue(JobStatus.getActiveStatuses().contains(CLAIMED));
        Assert.assertTrue(JobStatus.getActiveStatuses().contains(ACCEPTED));
    }

    /**
     * Make sure all the finished statuses are present in the set.
     */
    @Test
    public void testGetFinishedStatuses() {
        Assert.assertThat(JobStatus.getFinishedStatuses().size(), Matchers.is(4));
        Assert.assertTrue(JobStatus.getFinishedStatuses().contains(INVALID));
        Assert.assertTrue(JobStatus.getFinishedStatuses().contains(FAILED));
        Assert.assertTrue(JobStatus.getFinishedStatuses().contains(KILLED));
        Assert.assertTrue(JobStatus.getFinishedStatuses().contains(SUCCEEDED));
    }

    /**
     * Make sure all the claimable status are present in the set.
     */
    @Test
    public void testGetResolvableStatuses() {
        Assert.assertThat(JobStatus.getResolvableStatuses().size(), Matchers.is(1));
        Assert.assertThat(JobStatus.getResolvableStatuses(), Matchers.hasItem(RESERVED));
    }

    /**
     * Make sure all the claimable status are present in the set.
     */
    @Test
    public void testGetClaimableStatuses() {
        Assert.assertThat(JobStatus.getClaimableStatuses().size(), Matchers.is(2));
        Assert.assertThat(JobStatus.getClaimableStatuses(), Matchers.hasItems(RESOLVED, ACCEPTED));
    }
}

