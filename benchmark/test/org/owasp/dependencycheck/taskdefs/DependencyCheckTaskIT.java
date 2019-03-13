/**
 * This file is part of dependency-check-ant.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) 2013 Jeremy Long. All Rights Reserved.
 */
package org.owasp.dependencycheck.taskdefs;


import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildFileRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.owasp.dependencycheck.BaseDBTestCase;


/**
 *
 *
 * @author Jeremy Long
 */
public class DependencyCheckTaskIT extends BaseDBTestCase {
    @Rule
    public BuildFileRule buildFileRule = new BuildFileRule();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    /**
     * Test of addFileSet method, of class DependencyCheckTask.
     */
    @Test
    public void testAddFileSet() throws Exception {
        File report = new File("target/dependency-check-report.html");
        if ((report.exists()) && (!(report.delete()))) {
            throw new Exception("Unable to delete 'target/dependency-check-report.html' prior to test.");
        }
        buildFileRule.executeTarget("test.fileset");
        Assert.assertTrue("DependencyCheck report was not generated", report.exists());
    }

    /**
     * Test of addFileList method, of class DependencyCheckTask.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAddFileList() throws Exception {
        File report = new File("target/dependency-check-report.xml");
        if (report.exists()) {
            if (!(report.delete())) {
                throw new Exception("Unable to delete 'target/DependencyCheck-Report.xml' prior to test.");
            }
        }
        buildFileRule.executeTarget("test.filelist");
        Assert.assertTrue("DependencyCheck report was not generated", report.exists());
    }

    /**
     * Test of addDirSet method, of class DependencyCheckTask.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAddDirSet() throws Exception {
        File report = new File("target/dependency-check-report.csv");
        if (report.exists()) {
            if (!(report.delete())) {
                throw new Exception("Unable to delete 'target/DependencyCheck-Vulnerability.html' prior to test.");
            }
        }
        buildFileRule.executeTarget("test.dirset");
        Assert.assertTrue("DependencyCheck report was not generated", report.exists());
    }

    /**
     * Test of getFailBuildOnCVSS method, of class DependencyCheckTask.
     */
    @Test
    public void testGetFailBuildOnCVSS() {
        expectedException.expect(BuildException.class);
        buildFileRule.executeTarget("failCVSS");
    }

    /**
     * Test the DependencyCheckTask where a CVE is suppressed.
     */
    @Test
    public void testSuppressingCVE() {
        // GIVEN an ant task with a vulnerability
        final String antTaskName = "suppression";
        // WHEN executing the ant task
        buildFileRule.executeTarget(antTaskName);
        if (((buildFileRule.getError()) != null) && (!(buildFileRule.getError().isEmpty()))) {
            System.out.println("----------------------------------------------------------");
            System.out.println(buildFileRule.getError());
            System.out.println("----------------------------------------------------------");
            System.out.println(buildFileRule.getFullLog());
            System.out.println("----------------------------------------------------------");
        }
        // THEN the ant task executed without error
        final File report = new File("target/suppression-report.html");
        Assert.assertTrue("Expected the DependencyCheck report to be generated", report.exists());
    }

    /**
     * Test the DependencyCheckTask deprecated suppression property throws an
     * exception with a warning.
     */
    @Test
    public void testSuppressingSingle() {
        // GIVEN an ant task with a vulnerability using the legacy property
        final String antTaskName = "suppression-single";
        // WHEN executing the ant task
        buildFileRule.executeTarget(antTaskName);
        // THEN the ant task executed without error
        final File report = new File("target/suppression-single-report.html");
        Assert.assertTrue("Expected the DependencyCheck report to be generated", report.exists());
    }

    /**
     * Test the DependencyCheckTask deprecated suppression property throws an
     * exception with a warning.
     */
    @Test
    public void testSuppressingMultiple() {
        // GIVEN an ant task with a vulnerability using multiple was to configure the suppression file
        final String antTaskName = "suppression-multiple";
        // WHEN executing the ant task
        buildFileRule.executeTarget(antTaskName);
        // THEN the ant task executed without error
        final File report = new File("target/suppression-multiple-report.html");
        Assert.assertTrue("Expected the DependencyCheck report to be generated", report.exists());
    }

    /**
     * Test the DependencyCheckTask retireJS configuration.
     */
    @Test
    public void testRetireJsConfiguration() {
        // GIVEN an ant task with a vulnerability using multiple was to configure the suppression file
        final String antTaskName = "retireJS";
        // WHEN executing the ant task
        buildFileRule.executeTarget(antTaskName);
        // THEN the ant task executed without error
        final File report = new File("target/retirejs-report.html");
        Assert.assertTrue("Expected the DependencyCheck report to be generated", report.exists());
    }
}

