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
package org.apache.hadoop.mapreduce.v2.app;


import EventWriter.WriteMode;
import JHAdminConfig.MR_HS_JHIST_FORMAT;
import JobStateInternal.ERROR;
import JobStateInternal.FAILED;
import JobStateInternal.SUCCEEDED;
import MRJobConfig.MR_AM_STAGING_DIR;
import MRJobConfig.NUM_REDUCES;
import UserGroupInformation.HADOOP_TOKEN_FILE_LOCATION;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.TypeConverter;
import org.apache.hadoop.mapreduce.jobhistory.EventWriter;
import org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter.FILEOUTPUTCOMMITTER_ALGORITHM_VERSION;
import org.apache.hadoop.mapreduce.split.JobSplitWriter;
import org.apache.hadoop.mapreduce.v2.api.records.JobId;
import org.apache.hadoop.mapreduce.v2.jobhistory.JobHistoryUtils;
import org.apache.hadoop.mapreduce.v2.util.MRApps;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.ExitUtil;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.security.AMRMTokenIdentifier;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;


public class TestMRAppMaster {
    private static final Logger LOG = LoggerFactory.getLogger(TestMRAppMaster.class);

    private static final Path TEST_ROOT_DIR = new Path(System.getProperty("test.build.data", "target/test-dir"));

    private static final Path testDir = new Path(TestMRAppMaster.TEST_ROOT_DIR, ((TestMRAppMaster.class.getName()) + "-tmpDir"));

    static String stagingDir = new Path(TestMRAppMaster.testDir, "staging").toString();

    private static FileContext localFS = null;

    @Test
    public void testMRAppMasterForDifferentUser() throws IOException, InterruptedException {
        String applicationAttemptIdStr = "appattempt_1317529182569_0004_000001";
        String containerIdStr = "container_1317529182569_0004_000001_1";
        String userName = "TestAppMasterUser";
        ApplicationAttemptId applicationAttemptId = ApplicationAttemptId.fromString(applicationAttemptIdStr);
        ContainerId containerId = ContainerId.fromString(containerIdStr);
        MRAppMasterTest appMaster = new MRAppMasterTest(applicationAttemptId, containerId, "host", (-1), (-1), System.currentTimeMillis());
        JobConf conf = new JobConf();
        conf.set(MR_AM_STAGING_DIR, TestMRAppMaster.stagingDir);
        MRAppMaster.initAndStartAppMaster(appMaster, conf, userName);
        Path userPath = new Path(TestMRAppMaster.stagingDir, userName);
        Path userStagingPath = new Path(userPath, ".staging");
        Assert.assertEquals(userStagingPath.toString(), appMaster.stagingDirPath.toString());
    }

    @Test
    public void testMRAppMasterMidLock() throws IOException, InterruptedException {
        String applicationAttemptIdStr = "appattempt_1317529182569_0004_000002";
        String containerIdStr = "container_1317529182569_0004_000002_1";
        String userName = "TestAppMasterUser";
        JobConf conf = new JobConf();
        conf.set(MR_AM_STAGING_DIR, TestMRAppMaster.stagingDir);
        conf.setInt(FILEOUTPUTCOMMITTER_ALGORITHM_VERSION, 1);
        ApplicationAttemptId applicationAttemptId = ApplicationAttemptId.fromString(applicationAttemptIdStr);
        JobId jobId = TypeConverter.toYarn(TypeConverter.fromYarn(applicationAttemptId.getApplicationId()));
        Path start = MRApps.getStartJobCommitFile(conf, userName, jobId);
        FileSystem fs = FileSystem.get(conf);
        // Create the file, but no end file so we should unregister with an error.
        fs.create(start).close();
        ContainerId containerId = ContainerId.fromString(containerIdStr);
        MRAppMaster appMaster = new MRAppMasterTest(applicationAttemptId, containerId, "host", (-1), (-1), System.currentTimeMillis(), false, false);
        boolean caught = false;
        try {
            MRAppMaster.initAndStartAppMaster(appMaster, conf, userName);
        } catch (IOException e) {
            // The IO Exception is expected
            TestMRAppMaster.LOG.info("Caught expected Exception", e);
            caught = true;
        }
        Assert.assertTrue(caught);
        Assert.assertTrue(appMaster.errorHappenedShutDown);
        Assert.assertEquals(ERROR, appMaster.forcedState);
        appMaster.stop();
        // verify the final status is FAILED
        verifyFailedStatus(((MRAppMasterTest) (appMaster)), "FAILED");
    }

    @Test
    public void testMRAppMasterJobLaunchTime() throws IOException, InterruptedException {
        String applicationAttemptIdStr = "appattempt_1317529182569_0004_000002";
        String containerIdStr = "container_1317529182569_0004_000002_1";
        String userName = "TestAppMasterUser";
        JobConf conf = new JobConf();
        conf.set(MR_AM_STAGING_DIR, TestMRAppMaster.stagingDir);
        conf.setInt(NUM_REDUCES, 0);
        conf.set(MR_HS_JHIST_FORMAT, "json");
        ApplicationAttemptId applicationAttemptId = ApplicationAttemptId.fromString(applicationAttemptIdStr);
        JobId jobId = TypeConverter.toYarn(TypeConverter.fromYarn(applicationAttemptId.getApplicationId()));
        File dir = new File(MRApps.getStagingAreaDir(conf, userName).toString(), jobId.toString());
        dir.mkdirs();
        File historyFile = new File(JobHistoryUtils.getStagingJobHistoryFile(new Path(dir.toURI().toString()), jobId, ((applicationAttemptId.getAttemptId()) - 1)).toUri().getRawPath());
        historyFile.createNewFile();
        FSDataOutputStream out = new FSDataOutputStream(new FileOutputStream(historyFile), null);
        EventWriter writer = new EventWriter(out, WriteMode.JSON);
        writer.close();
        FileSystem fs = FileSystem.get(conf);
        JobSplitWriter.createSplitFiles(new Path(dir.getAbsolutePath()), conf, fs, new org.apache.hadoop.mapred.InputSplit[0]);
        ContainerId containerId = ContainerId.fromString(containerIdStr);
        MRAppMasterTestLaunchTime appMaster = new MRAppMasterTestLaunchTime(applicationAttemptId, containerId, "host", (-1), (-1), System.currentTimeMillis());
        MRAppMaster.initAndStartAppMaster(appMaster, conf, userName);
        stop();
        Assert.assertTrue("Job launch time should not be negative.", ((appMaster.jobLaunchTime.get()) >= 0));
    }

    @Test
    public void testMRAppMasterSuccessLock() throws IOException, InterruptedException {
        String applicationAttemptIdStr = "appattempt_1317529182569_0004_000002";
        String containerIdStr = "container_1317529182569_0004_000002_1";
        String userName = "TestAppMasterUser";
        JobConf conf = new JobConf();
        conf.set(MR_AM_STAGING_DIR, TestMRAppMaster.stagingDir);
        ApplicationAttemptId applicationAttemptId = ApplicationAttemptId.fromString(applicationAttemptIdStr);
        JobId jobId = TypeConverter.toYarn(TypeConverter.fromYarn(applicationAttemptId.getApplicationId()));
        Path start = MRApps.getStartJobCommitFile(conf, userName, jobId);
        Path end = MRApps.getEndJobCommitSuccessFile(conf, userName, jobId);
        FileSystem fs = FileSystem.get(conf);
        fs.create(start).close();
        fs.create(end).close();
        ContainerId containerId = ContainerId.fromString(containerIdStr);
        MRAppMaster appMaster = new MRAppMasterTest(applicationAttemptId, containerId, "host", (-1), (-1), System.currentTimeMillis(), false, false);
        boolean caught = false;
        try {
            MRAppMaster.initAndStartAppMaster(appMaster, conf, userName);
        } catch (IOException e) {
            // The IO Exception is expected
            TestMRAppMaster.LOG.info("Caught expected Exception", e);
            caught = true;
        }
        Assert.assertTrue(caught);
        Assert.assertTrue(appMaster.errorHappenedShutDown);
        Assert.assertEquals(SUCCEEDED, appMaster.forcedState);
        appMaster.stop();
        // verify the final status is SUCCEEDED
        verifyFailedStatus(((MRAppMasterTest) (appMaster)), "SUCCEEDED");
    }

    @Test
    public void testMRAppMasterFailLock() throws IOException, InterruptedException {
        String applicationAttemptIdStr = "appattempt_1317529182569_0004_000002";
        String containerIdStr = "container_1317529182569_0004_000002_1";
        String userName = "TestAppMasterUser";
        JobConf conf = new JobConf();
        conf.set(MR_AM_STAGING_DIR, TestMRAppMaster.stagingDir);
        ApplicationAttemptId applicationAttemptId = ApplicationAttemptId.fromString(applicationAttemptIdStr);
        JobId jobId = TypeConverter.toYarn(TypeConverter.fromYarn(applicationAttemptId.getApplicationId()));
        Path start = MRApps.getStartJobCommitFile(conf, userName, jobId);
        Path end = MRApps.getEndJobCommitFailureFile(conf, userName, jobId);
        FileSystem fs = FileSystem.get(conf);
        fs.create(start).close();
        fs.create(end).close();
        ContainerId containerId = ContainerId.fromString(containerIdStr);
        MRAppMaster appMaster = new MRAppMasterTest(applicationAttemptId, containerId, "host", (-1), (-1), System.currentTimeMillis(), false, false);
        boolean caught = false;
        try {
            MRAppMaster.initAndStartAppMaster(appMaster, conf, userName);
        } catch (IOException e) {
            // The IO Exception is expected
            TestMRAppMaster.LOG.info("Caught expected Exception", e);
            caught = true;
        }
        Assert.assertTrue(caught);
        Assert.assertTrue(appMaster.errorHappenedShutDown);
        Assert.assertEquals(FAILED, appMaster.forcedState);
        appMaster.stop();
        // verify the final status is FAILED
        verifyFailedStatus(((MRAppMasterTest) (appMaster)), "FAILED");
    }

    @Test
    public void testMRAppMasterMissingStaging() throws IOException, InterruptedException {
        String applicationAttemptIdStr = "appattempt_1317529182569_0004_000002";
        String containerIdStr = "container_1317529182569_0004_000002_1";
        String userName = "TestAppMasterUser";
        JobConf conf = new JobConf();
        conf.set(MR_AM_STAGING_DIR, TestMRAppMaster.stagingDir);
        ApplicationAttemptId applicationAttemptId = ApplicationAttemptId.fromString(applicationAttemptIdStr);
        // Delete the staging directory
        File dir = new File(TestMRAppMaster.stagingDir);
        if (dir.exists()) {
            FileUtils.deleteDirectory(dir);
        }
        ContainerId containerId = ContainerId.fromString(containerIdStr);
        MRAppMaster appMaster = new MRAppMasterTest(applicationAttemptId, containerId, "host", (-1), (-1), System.currentTimeMillis(), false, false);
        boolean caught = false;
        try {
            MRAppMaster.initAndStartAppMaster(appMaster, conf, userName);
        } catch (IOException e) {
            // The IO Exception is expected
            TestMRAppMaster.LOG.info("Caught expected Exception", e);
            caught = true;
        }
        Assert.assertTrue(caught);
        Assert.assertTrue(appMaster.errorHappenedShutDown);
        // Copying the history file is disabled, but it is not really visible from
        // here
        Assert.assertEquals(ERROR, appMaster.forcedState);
        appMaster.stop();
    }

    @Test(timeout = 30000)
    public void testMRAppMasterMaxAppAttempts() throws IOException, InterruptedException {
        // No matter what's the maxAppAttempt or attempt id, the isLastRetry always
        // equals to false
        Boolean[] expectedBools = new Boolean[]{ false, false, false };
        String applicationAttemptIdStr = "appattempt_1317529182569_0004_000002";
        String containerIdStr = "container_1317529182569_0004_000002_1";
        String userName = "TestAppMasterUser";
        ApplicationAttemptId applicationAttemptId = ApplicationAttemptId.fromString(applicationAttemptIdStr);
        ContainerId containerId = ContainerId.fromString(containerIdStr);
        JobConf conf = new JobConf();
        conf.set(MR_AM_STAGING_DIR, TestMRAppMaster.stagingDir);
        File stagingDir = new File(MRApps.getStagingAreaDir(conf, userName).toString());
        stagingDir.mkdirs();
        for (int i = 0; i < (expectedBools.length); ++i) {
            MRAppMasterTest appMaster = new MRAppMasterTest(applicationAttemptId, containerId, "host", (-1), (-1), System.currentTimeMillis(), false, true);
            MRAppMaster.initAndStartAppMaster(appMaster, conf, userName);
            Assert.assertEquals("isLastAMRetry is correctly computed.", expectedBools[i], isLastAMRetry());
        }
    }

    @Test
    public void testMRAppMasterCredentials() throws Exception {
        GenericTestUtils.setRootLogLevel(Level.DEBUG);
        // Simulate credentials passed to AM via client->RM->NM
        Credentials credentials = new Credentials();
        byte[] identifier = "MyIdentifier".getBytes();
        byte[] password = "MyPassword".getBytes();
        Text kind = new Text("MyTokenKind");
        Text service = new Text("host:port");
        Token<? extends TokenIdentifier> myToken = new Token<TokenIdentifier>(identifier, password, kind, service);
        Text tokenAlias = new Text("myToken");
        credentials.addToken(tokenAlias, myToken);
        Text appTokenService = new Text("localhost:0");
        Token<AMRMTokenIdentifier> appToken = new Token<AMRMTokenIdentifier>(identifier, password, AMRMTokenIdentifier.KIND_NAME, appTokenService);
        credentials.addToken(appTokenService, appToken);
        Text keyAlias = new Text("mySecretKeyAlias");
        credentials.addSecretKey(keyAlias, "mySecretKey".getBytes());
        Token<? extends TokenIdentifier> storedToken = credentials.getToken(tokenAlias);
        JobConf conf = new JobConf();
        Path tokenFilePath = new Path(TestMRAppMaster.testDir, "tokens-file");
        Map<String, String> newEnv = new HashMap<String, String>();
        newEnv.put(HADOOP_TOKEN_FILE_LOCATION, tokenFilePath.toUri().getPath());
        TestMRAppMaster.setNewEnvironmentHack(newEnv);
        credentials.writeTokenStorageFile(tokenFilePath, conf);
        ApplicationId appId = ApplicationId.newInstance(12345, 56);
        ApplicationAttemptId applicationAttemptId = ApplicationAttemptId.newInstance(appId, 1);
        ContainerId containerId = ContainerId.newContainerId(applicationAttemptId, 546);
        String userName = UserGroupInformation.getCurrentUser().getShortUserName();
        // Create staging dir, so MRAppMaster doesn't barf.
        File stagingDir = new File(MRApps.getStagingAreaDir(conf, userName).toString());
        stagingDir.mkdirs();
        // Set login-user to null as that is how real world MRApp starts with.
        // This is null is the reason why token-file is read by UGI.
        UserGroupInformation.setLoginUser(null);
        MRAppMasterTest appMaster = new MRAppMasterTest(applicationAttemptId, containerId, "host", (-1), (-1), System.currentTimeMillis(), false, true);
        MRAppMaster.initAndStartAppMaster(appMaster, conf, userName);
        // Now validate the task credentials
        Credentials appMasterCreds = appMaster.getCredentials();
        Assert.assertNotNull(appMasterCreds);
        Assert.assertEquals(1, appMasterCreds.numberOfSecretKeys());
        Assert.assertEquals(1, appMasterCreds.numberOfTokens());
        // Validate the tokens - app token should not be present
        Token<? extends TokenIdentifier> usedToken = appMasterCreds.getToken(tokenAlias);
        Assert.assertNotNull(usedToken);
        Assert.assertEquals(storedToken, usedToken);
        // Validate the keys
        byte[] usedKey = appMasterCreds.getSecretKey(keyAlias);
        Assert.assertNotNull(usedKey);
        Assert.assertEquals("mySecretKey", new String(usedKey));
        // The credentials should also be added to conf so that OuputCommitter can
        // access it - app token should not be present
        Credentials confCredentials = conf.getCredentials();
        Assert.assertEquals(1, confCredentials.numberOfSecretKeys());
        Assert.assertEquals(1, confCredentials.numberOfTokens());
        Assert.assertEquals(storedToken, confCredentials.getToken(tokenAlias));
        Assert.assertEquals("mySecretKey", new String(confCredentials.getSecretKey(keyAlias)));
        // Verify the AM's ugi - app token should be present
        Credentials ugiCredentials = appMaster.getUgi().getCredentials();
        Assert.assertEquals(1, ugiCredentials.numberOfSecretKeys());
        Assert.assertEquals(2, ugiCredentials.numberOfTokens());
        Assert.assertEquals(storedToken, ugiCredentials.getToken(tokenAlias));
        Assert.assertEquals(appToken, ugiCredentials.getToken(appTokenService));
        Assert.assertEquals("mySecretKey", new String(ugiCredentials.getSecretKey(keyAlias)));
    }

    @Test
    public void testMRAppMasterShutDownJob() throws Exception, InterruptedException {
        String applicationAttemptIdStr = "appattempt_1317529182569_0004_000002";
        String containerIdStr = "container_1317529182569_0004_000002_1";
        String userName = "TestAppMasterUser";
        ApplicationAttemptId applicationAttemptId = ApplicationAttemptId.fromString(applicationAttemptIdStr);
        ContainerId containerId = ContainerId.fromString(containerIdStr);
        JobConf conf = new JobConf();
        conf.set(MR_AM_STAGING_DIR, TestMRAppMaster.stagingDir);
        File stagingDir = new File(MRApps.getStagingAreaDir(conf, userName).toString());
        stagingDir.mkdirs();
        MRAppMasterTest appMaster = Mockito.spy(new MRAppMasterTest(applicationAttemptId, containerId, "host", (-1), (-1), System.currentTimeMillis(), false, true));
        MRAppMaster.initAndStartAppMaster(appMaster, conf, userName);
        Mockito.doReturn(conf).when(appMaster).getConfig();
        appMaster.isLastAMRetry = true;
        serviceStop();
        // Test normal shutdown.
        shutDownJob();
        Assert.assertTrue("Expected shutDownJob to terminate.", ExitUtil.terminateCalled());
        Assert.assertEquals("Expected shutDownJob to exit with status code of 0.", 0, ExitUtil.getFirstExitException().status);
        // Test shutdown with exception.
        ExitUtil.resetFirstExitException();
        String msg = "Injected Exception";
        notifyIsLastAMRetry(ArgumentMatchers.anyBoolean());
        shutDownJob();
        Assert.assertTrue(("Expected message from ExitUtil.ExitException to be " + msg), ExitUtil.getFirstExitException().getMessage().contains(msg));
        Assert.assertEquals("Expected shutDownJob to exit with status code of 1.", 1, ExitUtil.getFirstExitException().status);
    }
}

