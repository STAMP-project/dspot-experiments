/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.kitchen;


import CommandExecutorCodes.Kitchen;
import CommandExecutorCodes.Kitchen.CMD_LINE_PRINT;
import CommandExecutorCodes.Kitchen.COULD_NOT_LOAD_JOB;
import CommandExecutorCodes.Kitchen.ERRORS_DURING_PROCESSING;
import CommandExecutorCodes.Kitchen.ERROR_LOADING_STEPS_PLUGINS;
import CommandExecutorCodes.Kitchen.SUCCESS;
import CommandExecutorCodes.Kitchen.UNEXPECTED_ERROR;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.security.Permission;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleSecurityException;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.RepositoriesMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryMeta;
import org.pentaho.di.repository.RepositoryOperation;


public class KitchenTest {
    private static final String TEST_PARAM_NAME = "testParam";

    private static final String DEFAULT_PARAM_VALUE = "default value";

    private static final String NOT_DEFAULT_PARAM_VALUE = "not the default value";

    private ByteArrayOutputStream sysOutContent;

    private ByteArrayOutputStream sysErrContent;

    private SecurityManager oldSecurityManager;

    RepositoriesMeta mockRepositoriesMeta;

    RepositoryMeta mockRepositoryMeta;

    Repository mockRepository;

    RepositoryDirectoryInterface mockRepositoryDirectory;

    @Test
    public void testKitchenStatusCodes() throws Exception {
        Assert.assertNull(Kitchen.getByCode(9999));
        Assert.assertNotNull(Kitchen.getByCode(0));
        Assert.assertEquals(UNEXPECTED_ERROR, Kitchen.getByCode(2));
        Assert.assertEquals(CMD_LINE_PRINT, Kitchen.getByCode(9));
        Assert.assertEquals("The job ran without a problem", Kitchen.getByCode(0).getDescription());
        Assert.assertEquals("The job couldn't be loaded from XML or the Repository", Kitchen.getByCode(7).getDescription());
        Assert.assertTrue(Kitchen.isFailedExecution(COULD_NOT_LOAD_JOB.getCode()));
        Assert.assertTrue(Kitchen.isFailedExecution(ERROR_LOADING_STEPS_PLUGINS.getCode()));
        Assert.assertFalse(Kitchen.isFailedExecution(SUCCESS.getCode()));
        Assert.assertFalse(Kitchen.isFailedExecution(ERRORS_DURING_PROCESSING.getCode()));
    }

    @Test
    public void testConfigureParameters() throws Exception {
        JobMeta jobMeta = new JobMeta();
        jobMeta.addParameterDefinition(KitchenTest.TEST_PARAM_NAME, KitchenTest.DEFAULT_PARAM_VALUE, "This tests a default parameter");
        Assert.assertEquals("Default parameter was not set correctly on JobMeta", KitchenTest.DEFAULT_PARAM_VALUE, jobMeta.getParameterDefault(KitchenTest.TEST_PARAM_NAME));
        Assert.assertEquals("Parameter value should be blank in JobMeta", "", jobMeta.getParameterValue(KitchenTest.TEST_PARAM_NAME));
        Job job = new Job(null, jobMeta);
        job.copyParametersFrom(jobMeta);
        Assert.assertEquals("Default parameter was not set correctly on Job", KitchenTest.DEFAULT_PARAM_VALUE, job.getParameterDefault(KitchenTest.TEST_PARAM_NAME));
        Assert.assertEquals("Parameter value should be blank in Job", "", job.getParameterValue(KitchenTest.TEST_PARAM_NAME));
    }

    @Test
    public void testListRepos() throws Exception {
        PrintStream origSysOut;
        PrintStream origSysErr;
        final String TEST_REPO_DUMMY_NAME = "dummy-repo-name";
        final String TEST_REPO_DUMMY_DESC = "dummy-repo-description";
        Mockito.when(mockRepositoryMeta.getName()).thenReturn(TEST_REPO_DUMMY_NAME);
        Mockito.when(mockRepositoryMeta.getDescription()).thenReturn(TEST_REPO_DUMMY_DESC);
        Mockito.when(mockRepositoriesMeta.nrRepositories()).thenReturn(1);
        Mockito.when(mockRepositoriesMeta.getRepository(0)).thenReturn(mockRepositoryMeta);
        KitchenCommandExecutor testPanCommandExecutor = new KitchenTest.KitchenCommandExecutorForTesting(null, null, mockRepositoriesMeta);
        origSysOut = System.out;
        origSysErr = System.err;
        try {
            System.setOut(new PrintStream(sysOutContent));
            System.setErr(new PrintStream(sysErrContent));
            Kitchen.setCommandExecutor(testPanCommandExecutor);
            Kitchen.main(new String[]{ "/listrep" });
        } catch (SecurityException e) {
            // All OK / expected: SecurityException is purposely thrown when Pan triggers System.exitJVM()
            System.out.println(sysOutContent);
            Assert.assertTrue(sysOutContent.toString().contains(TEST_REPO_DUMMY_NAME));
            Assert.assertTrue(sysOutContent.toString().contains(TEST_REPO_DUMMY_DESC));
            Result result = Kitchen.getCommandExecutor().getResult();
            Assert.assertNotNull(result);
            Assert.assertEquals(result.getExitStatus(), COULD_NOT_LOAD_JOB.getCode());
        } finally {
            // sanitize
            Kitchen.setCommandExecutor(null);
            System.setOut(origSysOut);
            System.setErr(origSysErr);
        }
    }

    @Test
    public void testListDirs() throws Exception {
        PrintStream origSysOut;
        PrintStream origSysErr;
        final String DUMMY_DIR_1 = "test-dir-1";
        final String DUMMY_DIR_2 = "test-dir-2";
        Mockito.when(mockRepository.getDirectoryNames(ArgumentMatchers.anyObject())).thenReturn(new String[]{ DUMMY_DIR_1, DUMMY_DIR_2 });
        Mockito.when(mockRepository.loadRepositoryDirectoryTree()).thenReturn(mockRepositoryDirectory);
        KitchenCommandExecutor testPanCommandExecutor = new KitchenTest.KitchenCommandExecutorForTesting(mockRepository, mockRepositoryMeta, null);
        origSysOut = System.out;
        origSysErr = System.err;
        try {
            System.setOut(new PrintStream(sysOutContent));
            System.setErr(new PrintStream(sysErrContent));
            Kitchen.setCommandExecutor(testPanCommandExecutor);
            // (case-insensitive) should accept either 'Y' (default) or 'true'
            Kitchen.main(new String[]{ "/listdir:true", "/rep:test-repo", "/level:Basic" });
        } catch (SecurityException e) {
            // All OK / expected: SecurityException is purposely thrown when Pan triggers System.exitJVM()
            System.out.println(sysOutContent);
            Assert.assertTrue(sysOutContent.toString().contains(DUMMY_DIR_1));
            Assert.assertTrue(sysOutContent.toString().contains(DUMMY_DIR_2));
            Result result = Kitchen.getCommandExecutor().getResult();
            Assert.assertNotNull(result);
            Assert.assertEquals(result.getExitStatus(), COULD_NOT_LOAD_JOB.getCode());
        } finally {
            // sanitize
            Kitchen.setCommandExecutor(null);
            System.setOut(origSysOut);
            System.setErr(origSysErr);
        }
    }

    @Test
    public void testListJobs() throws Exception {
        PrintStream origSysOut;
        PrintStream origSysErr;
        final String DUMMY_JOB_1 = "test-job-name-1";
        final String DUMMY_JOB_2 = "test-job-name-2";
        Mockito.when(mockRepository.getJobNames(ArgumentMatchers.anyObject(), ArgumentMatchers.anyBoolean())).thenReturn(new String[]{ DUMMY_JOB_1, DUMMY_JOB_2 });
        Mockito.when(mockRepository.loadRepositoryDirectoryTree()).thenReturn(mockRepositoryDirectory);
        KitchenCommandExecutor testPanCommandExecutor = new KitchenTest.KitchenCommandExecutorForTesting(mockRepository, mockRepositoryMeta, null);
        origSysOut = System.out;
        origSysErr = System.err;
        try {
            System.setOut(new PrintStream(sysOutContent));
            System.setErr(new PrintStream(sysErrContent));
            Kitchen.setCommandExecutor(testPanCommandExecutor);
            // (case-insensitive) should accept either 'Y' (default) or 'true'
            Kitchen.main(new String[]{ "/listjobs:Y", "/rep:test-repo", "/level:Basic" });
        } catch (SecurityException e) {
            // All OK / expected: SecurityException is purposely thrown when Pan triggers System.exitJVM()
            System.out.println(sysOutContent);
            Assert.assertTrue(sysOutContent.toString().contains(DUMMY_JOB_1));
            Assert.assertTrue(sysOutContent.toString().contains(DUMMY_JOB_2));
            Result result = Kitchen.getCommandExecutor().getResult();
            Assert.assertNotNull(result);
            Assert.assertEquals(result.getExitStatus(), COULD_NOT_LOAD_JOB.getCode());
        } finally {
            // sanitize
            Kitchen.setCommandExecutor(null);
            System.setOut(origSysOut);
            System.setErr(origSysErr);
        }
    }

    private class KitchenCommandExecutorForTesting extends KitchenCommandExecutor {
        private Repository testRepository;

        private RepositoryMeta testRepositoryMeta;

        private RepositoriesMeta testRepositoriesMeta;

        public KitchenCommandExecutorForTesting(Repository testRepository, RepositoryMeta testRepositoryMeta, RepositoriesMeta testRepositoriesMeta) {
            super(Kitchen.class);
            this.testRepository = testRepository;
            this.testRepositoryMeta = testRepositoryMeta;
            this.testRepositoriesMeta = testRepositoriesMeta;
        }

        @Override
        public RepositoriesMeta loadRepositoryInfo(String loadingAvailableRepMsgTkn, String noRepsDefinedMsgTkn) throws KettleException {
            return (testRepositoriesMeta) != null ? testRepositoriesMeta : super.loadRepositoryInfo(loadingAvailableRepMsgTkn, noRepsDefinedMsgTkn);
        }

        @Override
        public RepositoryMeta loadRepositoryConnection(final String repoName, String loadingAvailableRepMsgTkn, String noRepsDefinedMsgTkn, String findingRepMsgTkn) throws KettleException {
            return (testRepositoryMeta) != null ? testRepositoryMeta : super.loadRepositoryConnection(repoName, loadingAvailableRepMsgTkn, noRepsDefinedMsgTkn, findingRepMsgTkn);
        }

        @Override
        public Repository establishRepositoryConnection(RepositoryMeta repositoryMeta, final String username, final String password, final RepositoryOperation... operations) throws KettleException, KettleSecurityException {
            return (testRepository) != null ? testRepository : super.establishRepositoryConnection(repositoryMeta, username, password, operations);
        }
    }

    public class MySecurityManager extends SecurityManager {
        private SecurityManager baseSecurityManager;

        public MySecurityManager(SecurityManager baseSecurityManager) {
            this.baseSecurityManager = baseSecurityManager;
        }

        @Override
        public void checkPermission(Permission permission) {
            if (permission.getName().startsWith("exitVM")) {
                throw new SecurityException("System exit not allowed");
            }
            if ((baseSecurityManager) != null) {
                baseSecurityManager.checkPermission(permission);
            } else {
                return;
            }
        }
    }
}

