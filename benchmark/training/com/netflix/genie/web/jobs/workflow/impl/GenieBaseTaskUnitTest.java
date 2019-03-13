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
package com.netflix.genie.web.jobs.workflow.impl;


import AdminResources.APPLICATION;
import AdminResources.CLUSTER;
import AdminResources.COMMAND;
import FileType.CONFIG;
import FileType.DEPENDENCIES;
import FileType.SETUP;
import com.netflix.genie.common.exceptions.GenieException;
import com.netflix.genie.test.categories.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Tests for GenieBaseTask.
 *
 * @author amsharma
 * @since 3.0.0
 */
@Category(UnitTest.class)
public class GenieBaseTaskUnitTest {
    private GenieBaseTask genieBaseTask;

    /**
     * Test the buildLocalPath method for config file type for applications.
     *
     * @throws GenieException
     * 		if there is a problem.
     */
    @Test
    public void testBuildLocalPathConfigApplication() throws GenieException {
        final String localPath = this.genieBaseTask.buildLocalFilePath("dirpath", "id", "filepath/filename", CONFIG, APPLICATION);
        Assert.assertEquals("dirpath/genie/applications/id/config/filename", localPath);
    }

    /**
     * Test the buildLocalPath method for config file type for applications.
     *
     * @throws GenieException
     * 		if there is a problem.
     */
    @Test
    public void testBuildLocalPathSetupApplication() throws GenieException {
        final String localPath = this.genieBaseTask.buildLocalFilePath("dirpath", "id", "filepath/filename", SETUP, APPLICATION);
        Assert.assertEquals("dirpath/genie/applications/id/filename", localPath);
    }

    /**
     * Test the buildLocalPath method for dependency file type for applications.
     *
     * @throws GenieException
     * 		if there is a problem.
     */
    @Test
    public void testBuildLocalPathDependenciesApplication() throws GenieException {
        final String localPath = this.genieBaseTask.buildLocalFilePath("dirpath", "id", "filepath/filename", DEPENDENCIES, APPLICATION);
        Assert.assertEquals("dirpath/genie/applications/id/dependencies/filename", localPath);
    }

    /**
     * Test the buildLocalPath method for config file type for command.
     *
     * @throws GenieException
     * 		if there is a problem.
     */
    @Test
    public void testBuildLocalPathConfigCommand() throws GenieException {
        final String localPath = this.genieBaseTask.buildLocalFilePath("dirpath", "id", "filepath/filename", CONFIG, COMMAND);
        Assert.assertEquals("dirpath/genie/command/id/config/filename", localPath);
    }

    /**
     * Test the buildLocalPath method for config file type for command.
     *
     * @throws GenieException
     * 		if there is a problem.
     */
    @Test
    public void testBuildLocalPathSetupCommand() throws GenieException {
        final String localPath = this.genieBaseTask.buildLocalFilePath("dirpath", "id", "filepath/filename", SETUP, COMMAND);
        Assert.assertEquals("dirpath/genie/command/id/filename", localPath);
    }

    /**
     * Test the buildLocalPath method for dependency file type for command.
     *
     * @throws GenieException
     * 		if there is a problem.
     */
    @Test
    public void testBuildLocalPathDependenciesCommand() throws GenieException {
        final String localPath = this.genieBaseTask.buildLocalFilePath("dirpath", "id", "filepath/filename", DEPENDENCIES, COMMAND);
        Assert.assertEquals("dirpath/genie/command/id/dependencies/filename", localPath);
    }

    /**
     * Test the buildLocalPath method for config file type for cluster.
     *
     * @throws GenieException
     * 		if there is a problem.
     */
    @Test
    public void testBuildLocalPathConfigCluster() throws GenieException {
        final String localPath = this.genieBaseTask.buildLocalFilePath("dirpath", "id", "filepath/filename", CONFIG, CLUSTER);
        Assert.assertEquals("dirpath/genie/cluster/id/config/filename", localPath);
    }

    /**
     * Test the buildLocalPath method for config file type for cluster.
     *
     * @throws GenieException
     * 		if there is a problem.
     */
    @Test
    public void testBuildLocalPathSetupCluster() throws GenieException {
        final String localPath = this.genieBaseTask.buildLocalFilePath("dirpath", "id", "filepath/filename", SETUP, CLUSTER);
        Assert.assertEquals("dirpath/genie/cluster/id/filename", localPath);
    }

    /**
     * Test the buildLocalPath method for dependency file type for command.
     *
     * @throws GenieException
     * 		if there is a problem.
     */
    @Test
    public void testBuildLocalPathDependenciesCluster() throws GenieException {
        final String localPath = this.genieBaseTask.buildLocalFilePath("dirpath", "id", "filepath/filename", DEPENDENCIES, CLUSTER);
        Assert.assertEquals("dirpath/genie/cluster/id/dependencies/filename", localPath);
    }
}

