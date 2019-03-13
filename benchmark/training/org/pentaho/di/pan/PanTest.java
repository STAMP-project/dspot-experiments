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
package org.pentaho.di.pan;


import CommandExecutorCodes.Pan;
import CommandExecutorCodes.Pan.CMD_LINE_PRINT;
import CommandExecutorCodes.Pan.COULD_NOT_LOAD_TRANS;
import CommandExecutorCodes.Pan.ERRORS_DURING_PROCESSING;
import CommandExecutorCodes.Pan.ERROR_LOADING_STEPS_PLUGINS;
import CommandExecutorCodes.Pan.SUCCESS;
import CommandExecutorCodes.Pan.UNEXPECTED_ERROR;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.security.Permission;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleSecurityException;
import org.pentaho.di.core.parameters.NamedParams;
import org.pentaho.di.core.parameters.NamedParamsDefault;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.repository.RepositoriesMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryMeta;
import org.pentaho.di.repository.RepositoryOperation;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;


public class PanTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

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
    public void testPanStatusCodes() throws Exception {
        Assert.assertNull(Pan.getByCode(9999));
        Assert.assertNotNull(Pan.getByCode(0));
        Assert.assertEquals(UNEXPECTED_ERROR, Pan.getByCode(2));
        Assert.assertEquals(CMD_LINE_PRINT, Pan.getByCode(9));
        Assert.assertEquals("The transformation ran without a problem", Pan.getByCode(0).getDescription());
        Assert.assertEquals("The transformation couldn't be loaded from XML or the Repository", Pan.getByCode(7).getDescription());
        Assert.assertTrue(Pan.isFailedExecution(COULD_NOT_LOAD_TRANS.getCode()));
        Assert.assertTrue(Pan.isFailedExecution(ERROR_LOADING_STEPS_PLUGINS.getCode()));
        Assert.assertFalse(Pan.isFailedExecution(SUCCESS.getCode()));
        Assert.assertFalse(Pan.isFailedExecution(ERRORS_DURING_PROCESSING.getCode()));
    }

    @Test
    public void testConfigureParameters() throws Exception {
        TransMeta transMeta = new TransMeta();
        transMeta.addParameterDefinition(PanTest.TEST_PARAM_NAME, PanTest.DEFAULT_PARAM_VALUE, "This tests a default parameter");
        Assert.assertEquals("Default parameter was not set correctly on TransMeta", PanTest.DEFAULT_PARAM_VALUE, transMeta.getParameterDefault(PanTest.TEST_PARAM_NAME));
        Assert.assertEquals("Parameter value should be blank in TransMeta", "", transMeta.getParameterValue(PanTest.TEST_PARAM_NAME));
        Trans trans = new Trans(transMeta);
        Assert.assertEquals("Default parameter was not set correctly on Trans", PanTest.DEFAULT_PARAM_VALUE, trans.getParameterDefault(PanTest.TEST_PARAM_NAME));
        Assert.assertEquals("Parameter value should be blank in Trans", "", trans.getParameterValue(PanTest.TEST_PARAM_NAME));
        NamedParams params = new NamedParamsDefault();
        params.addParameterDefinition(PanTest.TEST_PARAM_NAME, PanTest.NOT_DEFAULT_PARAM_VALUE, "This tests a non-default parameter");
        params.setParameterValue(PanTest.TEST_PARAM_NAME, PanTest.NOT_DEFAULT_PARAM_VALUE);
        Pan.configureParameters(trans, params, transMeta);
        Assert.assertEquals("Parameter was not set correctly in Trans", PanTest.NOT_DEFAULT_PARAM_VALUE, trans.getParameterValue(PanTest.TEST_PARAM_NAME));
        Assert.assertEquals("Parameter was not set correctly in TransMeta", PanTest.NOT_DEFAULT_PARAM_VALUE, transMeta.getParameterValue(PanTest.TEST_PARAM_NAME));
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
        PanCommandExecutor testPanCommandExecutor = new PanTest.PanCommandExecutorForTesting(null, null, mockRepositoriesMeta);
        origSysOut = System.out;
        origSysErr = System.err;
        try {
            System.setOut(new PrintStream(sysOutContent));
            System.setErr(new PrintStream(sysErrContent));
            Pan.setCommandExecutor(testPanCommandExecutor);
            Pan.main(new String[]{ "/listrep" });
        } catch (SecurityException e) {
            // All OK / expected: SecurityException is purposely thrown when Pan triggers System.exitJVM()
            System.out.println(sysOutContent);
            Assert.assertTrue(sysOutContent.toString().contains(TEST_REPO_DUMMY_NAME));
            Assert.assertTrue(sysOutContent.toString().contains(TEST_REPO_DUMMY_DESC));
            Result result = Pan.getCommandExecutor().getResult();
            Assert.assertNotNull(result);
            Assert.assertEquals(result.getExitStatus(), SUCCESS.getCode());
        } finally {
            // sanitize
            Pan.setCommandExecutor(null);
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
        PanCommandExecutor testPanCommandExecutor = new PanTest.PanCommandExecutorForTesting(mockRepository, mockRepositoryMeta, null);
        origSysOut = System.out;
        origSysErr = System.err;
        try {
            System.setOut(new PrintStream(sysOutContent));
            System.setErr(new PrintStream(sysErrContent));
            Pan.setCommandExecutor(testPanCommandExecutor);
            // (case-insensitive) should accept either 'Y' (default) or 'true'
            Pan.main(new String[]{ "/listdir:true", "/rep:test-repo", "/level:Basic" });
        } catch (SecurityException e) {
            // All OK / expected: SecurityException is purposely thrown when Pan triggers System.exitJVM()
            System.out.println(sysOutContent);
            Assert.assertTrue(sysOutContent.toString().contains(DUMMY_DIR_1));
            Assert.assertTrue(sysOutContent.toString().contains(DUMMY_DIR_2));
            Result result = Pan.getCommandExecutor().getResult();
            Assert.assertNotNull(result);
            Assert.assertEquals(result.getExitStatus(), SUCCESS.getCode());
        } finally {
            // sanitize
            Pan.setCommandExecutor(null);
            System.setOut(origSysOut);
            System.setErr(origSysErr);
        }
    }

    @Test
    public void testListTrans() throws Exception {
        PrintStream origSysOut;
        PrintStream origSysErr;
        final String DUMMY_TRANS_1 = "test-trans-name-1";
        final String DUMMY_TRANS_2 = "test-trans-name-2";
        Mockito.when(mockRepository.getTransformationNames(ArgumentMatchers.anyObject(), ArgumentMatchers.anyBoolean())).thenReturn(new String[]{ DUMMY_TRANS_1, DUMMY_TRANS_2 });
        Mockito.when(mockRepository.loadRepositoryDirectoryTree()).thenReturn(mockRepositoryDirectory);
        PanCommandExecutor testPanCommandExecutor = new PanTest.PanCommandExecutorForTesting(mockRepository, mockRepositoryMeta, null);
        origSysOut = System.out;
        origSysErr = System.err;
        try {
            System.setOut(new PrintStream(sysOutContent));
            System.setErr(new PrintStream(sysErrContent));
            Pan.setCommandExecutor(testPanCommandExecutor);
            // (case-insensitive) should accept either 'Y' (default) or 'true'
            Pan.main(new String[]{ "/listtrans:Y", "/rep:test-repo", "/level:Basic" });
        } catch (SecurityException e) {
            // All OK / expected: SecurityException is purposely thrown when Pan triggers System.exitJVM()
            System.out.println(sysOutContent);
            Assert.assertTrue(sysOutContent.toString().contains(DUMMY_TRANS_1));
            Assert.assertTrue(sysOutContent.toString().contains(DUMMY_TRANS_2));
            Result result = Pan.getCommandExecutor().getResult();
            Assert.assertNotNull(result);
            Assert.assertEquals(result.getExitStatus(), SUCCESS.getCode());
        } finally {
            // sanitize
            Pan.setCommandExecutor(null);
            System.setOut(origSysOut);
            System.setErr(origSysErr);
        }
    }

    private class PanCommandExecutorForTesting extends PanCommandExecutor {
        private Repository testRepository;

        private RepositoryMeta testRepositoryMeta;

        private RepositoriesMeta testRepositoriesMeta;

        public PanCommandExecutorForTesting(Repository testRepository, RepositoryMeta testRepositoryMeta, RepositoriesMeta testRepositoriesMeta) {
            super(Pan.class);
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

