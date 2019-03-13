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
package com.netflix.genie.common.internal.util;


import ProcessStatus.APPLICATION_CONF_FILES_COPY_FAILURE;
import ProcessStatus.APPLICATION_JAR_FILES_COPY_FAILURE;
import ProcessStatus.CLUSTER_CONF_FILES_COPY_FAILURE;
import ProcessStatus.COMMAND_CONF_FILES_COPY_FAILURE;
import ProcessStatus.COMMAND_RUN_FAILURE;
import ProcessStatus.ENV_VARIABLES_SOURCE_AND_SETUP_FAILURE;
import ProcessStatus.HADOOP_LOCAL_CONF_COPY_FAILURE;
import ProcessStatus.JOB_DEPENDENCIES_COPY_FAILURE;
import ProcessStatus.JOB_INTERRUPTED;
import ProcessStatus.JOB_KILLED;
import ProcessStatus.MKDIR_CONF_FAILURE;
import ProcessStatus.MKDIR_JAR_FAILURE;
import ProcessStatus.SUCCESS;
import ProcessStatus.UPDATE_CORE_SITE_XML_FAILURE;
import ProcessStatus.ZOMBIE_JOB;
import com.netflix.genie.common.exceptions.GeniePreconditionException;
import com.netflix.genie.test.categories.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Tests for the ProcessStatus enum.
 *
 * @author tgianos
 * @since 2.0.0
 */
@Category(UnitTest.class)
public class ProcessStatusUnitTests {
    /**
     * Test to make sure the error codes are correct.
     */
    @Test
    public void testErrorCodes() {
        Assert.assertEquals((-1), JOB_INTERRUPTED.getExitCode());
        Assert.assertEquals(0, SUCCESS.getExitCode());
        Assert.assertEquals(201, MKDIR_JAR_FAILURE.getExitCode());
        Assert.assertEquals(202, MKDIR_CONF_FAILURE.getExitCode());
        Assert.assertEquals(203, HADOOP_LOCAL_CONF_COPY_FAILURE.getExitCode());
        Assert.assertEquals(204, UPDATE_CORE_SITE_XML_FAILURE.getExitCode());
        Assert.assertEquals(205, ENV_VARIABLES_SOURCE_AND_SETUP_FAILURE.getExitCode());
        Assert.assertEquals(206, CLUSTER_CONF_FILES_COPY_FAILURE.getExitCode());
        Assert.assertEquals(207, COMMAND_CONF_FILES_COPY_FAILURE.getExitCode());
        Assert.assertEquals(208, APPLICATION_CONF_FILES_COPY_FAILURE.getExitCode());
        Assert.assertEquals(209, APPLICATION_JAR_FILES_COPY_FAILURE.getExitCode());
        Assert.assertEquals(210, JOB_DEPENDENCIES_COPY_FAILURE.getExitCode());
        Assert.assertEquals(211, JOB_KILLED.getExitCode());
        Assert.assertEquals(212, ZOMBIE_JOB.getExitCode());
        Assert.assertEquals(213, COMMAND_RUN_FAILURE.getExitCode());
    }

    /**
     * Test to make sure the messages are correct.
     */
    @Test
    public void testMessages() {
        Assert.assertEquals("Job execution interrupted.", JOB_INTERRUPTED.getMessage());
        Assert.assertEquals("Success.", SUCCESS.getMessage());
        Assert.assertEquals("Failed to create job jar dir.", MKDIR_JAR_FAILURE.getMessage());
        Assert.assertEquals("Failed to create job conf dir.", MKDIR_CONF_FAILURE.getMessage());
        Assert.assertEquals("Failed copying Hadoop files from local conf dir to current job conf dir.", HADOOP_LOCAL_CONF_COPY_FAILURE.getMessage());
        Assert.assertEquals("Failed updating core-site.xml to add certain parameters.", UPDATE_CORE_SITE_XML_FAILURE.getMessage());
        Assert.assertEquals("Failed while sourcing resource envProperty files.", ENV_VARIABLES_SOURCE_AND_SETUP_FAILURE.getMessage());
        Assert.assertEquals("Failed copying cluster conf files from S3", CLUSTER_CONF_FILES_COPY_FAILURE.getMessage());
        Assert.assertEquals("Failed copying command conf files from S3", COMMAND_CONF_FILES_COPY_FAILURE.getMessage());
        Assert.assertEquals("Failed copying application conf files from S3", APPLICATION_CONF_FILES_COPY_FAILURE.getMessage());
        Assert.assertEquals("Failed copying application jar files from S3", APPLICATION_JAR_FILES_COPY_FAILURE.getMessage());
        Assert.assertEquals("Job failed copying dependent files.", JOB_DEPENDENCIES_COPY_FAILURE.getMessage());
        Assert.assertEquals("Job killed after it exceeded system limits", JOB_KILLED.getMessage());
        Assert.assertEquals("Job has been marked as a zombie", ZOMBIE_JOB.getMessage());
        Assert.assertEquals("Command failed with non-zero exit code.", COMMAND_RUN_FAILURE.getMessage());
    }

    /**
     * Test to make sure the parse method works for valid cases.
     *
     * @throws GeniePreconditionException
     * 		If any precondition isn't met.
     */
    @Test
    public void testParse() throws GeniePreconditionException {
        Assert.assertEquals(JOB_INTERRUPTED, ProcessStatus.parse((-1)));
        Assert.assertEquals(SUCCESS, ProcessStatus.parse(0));
        Assert.assertEquals(MKDIR_JAR_FAILURE, ProcessStatus.parse(201));
        Assert.assertEquals(MKDIR_CONF_FAILURE, ProcessStatus.parse(202));
        Assert.assertEquals(HADOOP_LOCAL_CONF_COPY_FAILURE, ProcessStatus.parse(203));
        Assert.assertEquals(UPDATE_CORE_SITE_XML_FAILURE, ProcessStatus.parse(204));
        Assert.assertEquals(ENV_VARIABLES_SOURCE_AND_SETUP_FAILURE, ProcessStatus.parse(205));
        Assert.assertEquals(CLUSTER_CONF_FILES_COPY_FAILURE, ProcessStatus.parse(206));
        Assert.assertEquals(COMMAND_CONF_FILES_COPY_FAILURE, ProcessStatus.parse(207));
        Assert.assertEquals(APPLICATION_CONF_FILES_COPY_FAILURE, ProcessStatus.parse(208));
        Assert.assertEquals(APPLICATION_JAR_FILES_COPY_FAILURE, ProcessStatus.parse(209));
        Assert.assertEquals(JOB_DEPENDENCIES_COPY_FAILURE, ProcessStatus.parse(210));
        Assert.assertEquals(JOB_KILLED, ProcessStatus.parse(211));
        Assert.assertEquals(ZOMBIE_JOB, ProcessStatus.parse(212));
        Assert.assertEquals(COMMAND_RUN_FAILURE, ProcessStatus.parse(213));
    }

    /**
     * Test to make sure the parse method works for valid cases.
     *
     * @throws GeniePreconditionException
     * 		If any precondition isn't met.
     */
    @Test(expected = GeniePreconditionException.class)
    public void testParseBadErrorCode() throws GeniePreconditionException {
        Assert.assertEquals(COMMAND_RUN_FAILURE, ProcessStatus.parse((-2490354)));
    }
}

