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
package org.apache.hadoop.mapreduce.v2.util;


import ApplicationClassLoader.SYSTEM_CLASSES_DEFAULT;
import ApplicationConstants.CLASS_PATH_SEPARATOR;
import ApplicationConstants.Environment.PWD;
import JHAdminConfig.MR_HISTORY_WEBAPP_ADDRESS;
import LocalResourceType.ARCHIVE;
import LocalResourceType.FILE;
import MRApps.TaskStateUI.COMPLETED;
import MRApps.TaskStateUI.PENDING;
import MRApps.TaskStateUI.RUNNING;
import MRConfig.MAPREDUCE_APP_SUBMISSION_CROSS_PLATFORM;
import MRJobConfig.CACHE_ARCHIVES;
import MRJobConfig.CACHE_ARCHIVES_SIZES;
import MRJobConfig.CACHE_ARCHIVES_TIMESTAMPS;
import MRJobConfig.CACHE_ARCHIVES_VISIBILITIES;
import MRJobConfig.CACHE_FILES_SIZES;
import MRJobConfig.CACHE_FILE_TIMESTAMPS;
import MRJobConfig.CACHE_FILE_VISIBILITIES;
import MRJobConfig.CLASSPATH_ARCHIVES;
import MRJobConfig.DEFAULT_MAPREDUCE_CROSS_PLATFORM_APPLICATION_CLASSPATH;
import MRJobConfig.MAPREDUCE_APPLICATION_CLASSPATH;
import MRJobConfig.MAPREDUCE_APPLICATION_FRAMEWORK_PATH;
import MRJobConfig.MAPREDUCE_JOB_CLASSLOADER;
import MRJobConfig.MAPREDUCE_JOB_USER_CLASSPATH_FIRST;
import MRJobConfig.MAPREDUCE_JVM_SYSTEM_PROPERTIES_TO_LOG;
import MRJobConfig.MR_AM_STAGING_DIR;
import TaskState.FAILED;
import TaskState.KILLED;
import TaskState.SCHEDULED;
import TaskState.SUCCEEDED;
import TaskType.MAP;
import TaskType.REDUCE;
import YarnConfiguration.DEFAULT_YARN_CROSS_PLATFORM_APPLICATION_CLASSPATH;
import YarnConfiguration.YARN_APPLICATION_CLASSPATH;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FilterFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobID;
import org.apache.hadoop.mapreduce.filecache.DistributedCache;
import org.apache.hadoop.mapreduce.v2.api.records.JobId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskAttemptId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskType;
import org.apache.hadoop.util.ApplicationClassLoader;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestMRApps {
    private static File testWorkDir = null;

    @Test(timeout = 120000)
    public void testJobIDtoString() {
        JobId jid = RecordFactoryProvider.getRecordFactory(null).newRecordInstance(JobId.class);
        jid.setAppId(ApplicationId.newInstance(0, 0));
        Assert.assertEquals("job_0_0000", MRApps.toString(jid));
    }

    @Test(timeout = 120000)
    public void testToJobID() {
        JobId jid = MRApps.toJobID("job_1_1");
        Assert.assertEquals(1, jid.getAppId().getClusterTimestamp());
        Assert.assertEquals(1, jid.getAppId().getId());
        Assert.assertEquals(1, jid.getId());// tests against some proto.id and not a job.id field

    }

    @Test(timeout = 120000, expected = IllegalArgumentException.class)
    public void testJobIDShort() {
        MRApps.toJobID("job_0_0_0");
    }

    // TODO_get.set
    @Test(timeout = 120000)
    public void testTaskIDtoString() {
        TaskId tid = RecordFactoryProvider.getRecordFactory(null).newRecordInstance(TaskId.class);
        tid.setJobId(RecordFactoryProvider.getRecordFactory(null).newRecordInstance(JobId.class));
        tid.getJobId().setAppId(ApplicationId.newInstance(0, 0));
        tid.setTaskType(MAP);
        TaskType type = tid.getTaskType();
        System.err.println(type);
        type = TaskType.REDUCE;
        System.err.println(type);
        System.err.println(tid.getTaskType());
        Assert.assertEquals("task_0_0000_m_000000", MRApps.toString(tid));
        tid.setTaskType(REDUCE);
        Assert.assertEquals("task_0_0000_r_000000", MRApps.toString(tid));
    }

    @Test(timeout = 120000)
    public void testToTaskID() {
        TaskId tid = MRApps.toTaskID("task_1_2_r_3");
        Assert.assertEquals(1, tid.getJobId().getAppId().getClusterTimestamp());
        Assert.assertEquals(2, tid.getJobId().getAppId().getId());
        Assert.assertEquals(2, tid.getJobId().getId());
        Assert.assertEquals(REDUCE, tid.getTaskType());
        Assert.assertEquals(3, tid.getId());
        tid = MRApps.toTaskID("task_1_2_m_3");
        Assert.assertEquals(MAP, tid.getTaskType());
    }

    @Test(timeout = 120000, expected = IllegalArgumentException.class)
    public void testTaskIDShort() {
        MRApps.toTaskID("task_0_0000_m");
    }

    @Test(timeout = 120000, expected = IllegalArgumentException.class)
    public void testTaskIDBadType() {
        MRApps.toTaskID("task_0_0000_x_000000");
    }

    // TODO_get.set
    @Test(timeout = 120000)
    public void testTaskAttemptIDtoString() {
        TaskAttemptId taid = RecordFactoryProvider.getRecordFactory(null).newRecordInstance(TaskAttemptId.class);
        taid.setTaskId(RecordFactoryProvider.getRecordFactory(null).newRecordInstance(TaskId.class));
        taid.getTaskId().setTaskType(MAP);
        taid.getTaskId().setJobId(RecordFactoryProvider.getRecordFactory(null).newRecordInstance(JobId.class));
        taid.getTaskId().getJobId().setAppId(ApplicationId.newInstance(0, 0));
        Assert.assertEquals("attempt_0_0000_m_000000_0", MRApps.toString(taid));
    }

    @Test(timeout = 120000)
    public void testToTaskAttemptID() {
        TaskAttemptId taid = MRApps.toTaskAttemptID("attempt_0_1_m_2_3");
        Assert.assertEquals(0, taid.getTaskId().getJobId().getAppId().getClusterTimestamp());
        Assert.assertEquals(1, taid.getTaskId().getJobId().getAppId().getId());
        Assert.assertEquals(1, taid.getTaskId().getJobId().getId());
        Assert.assertEquals(2, taid.getTaskId().getId());
        Assert.assertEquals(3, taid.getId());
    }

    @Test(timeout = 120000, expected = IllegalArgumentException.class)
    public void testTaskAttemptIDShort() {
        MRApps.toTaskAttemptID("attempt_0_0_0_m_0");
    }

    @Test(timeout = 120000)
    public void testGetJobFileWithUser() {
        Configuration conf = new Configuration();
        conf.set(MR_AM_STAGING_DIR, "/my/path/to/staging");
        String jobFile = MRApps.getJobFile(conf, "dummy-user", new JobID("dummy-job", 12345));
        Assert.assertNotNull("getJobFile results in null.", jobFile);
        Assert.assertEquals("jobFile with specified user is not as expected.", "/my/path/to/staging/dummy-user/.staging/job_dummy-job_12345/job.xml", jobFile);
    }

    @Test(timeout = 120000)
    public void testSetClasspath() throws IOException {
        Configuration conf = new Configuration();
        conf.setBoolean(MAPREDUCE_APP_SUBMISSION_CROSS_PLATFORM, true);
        Job job = Job.getInstance(conf);
        Map<String, String> environment = new HashMap<String, String>();
        MRApps.setClasspath(environment, job.getConfiguration());
        Assert.assertTrue(environment.get("CLASSPATH").startsWith(((PWD.$$()) + (ApplicationConstants.CLASS_PATH_SEPARATOR))));
        String yarnAppClasspath = job.getConfiguration().get(YARN_APPLICATION_CLASSPATH, StringUtils.join(",", DEFAULT_YARN_CROSS_PLATFORM_APPLICATION_CLASSPATH));
        if (yarnAppClasspath != null) {
            yarnAppClasspath = yarnAppClasspath.replaceAll(",\\s*", CLASS_PATH_SEPARATOR).trim();
        }
        Assert.assertTrue(environment.get("CLASSPATH").contains(yarnAppClasspath));
        String mrAppClasspath = job.getConfiguration().get(MAPREDUCE_APPLICATION_CLASSPATH, DEFAULT_MAPREDUCE_CROSS_PLATFORM_APPLICATION_CLASSPATH);
        if (mrAppClasspath != null) {
            mrAppClasspath = mrAppClasspath.replaceAll(",\\s*", CLASS_PATH_SEPARATOR).trim();
        }
        Assert.assertTrue(environment.get("CLASSPATH").contains(mrAppClasspath));
    }

    @Test(timeout = 120000)
    public void testSetClasspathWithArchives() throws IOException {
        File testTGZ = new File(TestMRApps.testWorkDir, "test.tgz");
        FileOutputStream out = new FileOutputStream(testTGZ);
        out.write(0);
        out.close();
        Configuration conf = new Configuration();
        conf.setBoolean(MAPREDUCE_APP_SUBMISSION_CROSS_PLATFORM, true);
        Job job = Job.getInstance(conf);
        conf = job.getConfiguration();
        String testTGZQualifiedPath = FileSystem.getLocal(conf).makeQualified(new Path(testTGZ.getAbsolutePath())).toString();
        conf.set(CLASSPATH_ARCHIVES, testTGZQualifiedPath);
        conf.set(CACHE_ARCHIVES, (testTGZQualifiedPath + "#testTGZ"));
        Map<String, String> environment = new HashMap<String, String>();
        MRApps.setClasspath(environment, conf);
        Assert.assertTrue(environment.get("CLASSPATH").startsWith(((PWD.$$()) + (ApplicationConstants.CLASS_PATH_SEPARATOR))));
        String confClasspath = job.getConfiguration().get(YARN_APPLICATION_CLASSPATH, StringUtils.join(",", DEFAULT_YARN_CROSS_PLATFORM_APPLICATION_CLASSPATH));
        if (confClasspath != null) {
            confClasspath = confClasspath.replaceAll(",\\s*", CLASS_PATH_SEPARATOR).trim();
        }
        Assert.assertTrue(environment.get("CLASSPATH").contains(confClasspath));
        Assert.assertTrue(environment.get("CLASSPATH").contains("testTGZ"));
    }

    @Test(timeout = 120000)
    public void testSetClasspathWithUserPrecendence() {
        Configuration conf = new Configuration();
        conf.setBoolean(MAPREDUCE_APP_SUBMISSION_CROSS_PLATFORM, true);
        conf.setBoolean(MAPREDUCE_JOB_USER_CLASSPATH_FIRST, true);
        Map<String, String> env = new HashMap<String, String>();
        try {
            MRApps.setClasspath(env, conf);
        } catch (Exception e) {
            Assert.fail("Got exception while setting classpath");
        }
        String env_str = env.get("CLASSPATH");
        String expectedClasspath = StringUtils.join(CLASS_PATH_SEPARATOR, Arrays.asList(PWD.$$(), "job.jar/*", "job.jar/classes/", "job.jar/lib/*", ((PWD.$$()) + "/*")));
        Assert.assertTrue("MAPREDUCE_JOB_USER_CLASSPATH_FIRST set, but not taking effect!", env_str.startsWith(expectedClasspath));
    }

    @Test(timeout = 120000)
    public void testSetClasspathWithNoUserPrecendence() {
        Configuration conf = new Configuration();
        conf.setBoolean(MAPREDUCE_APP_SUBMISSION_CROSS_PLATFORM, true);
        conf.setBoolean(MAPREDUCE_JOB_USER_CLASSPATH_FIRST, false);
        Map<String, String> env = new HashMap<String, String>();
        try {
            MRApps.setClasspath(env, conf);
        } catch (Exception e) {
            Assert.fail("Got exception while setting classpath");
        }
        String env_str = env.get("CLASSPATH");
        String expectedClasspath = StringUtils.join(CLASS_PATH_SEPARATOR, Arrays.asList("job.jar/*", "job.jar/classes/", "job.jar/lib/*", ((PWD.$$()) + "/*")));
        Assert.assertTrue(("MAPREDUCE_JOB_USER_CLASSPATH_FIRST false, and job.jar is not in" + " the classpath!"), env_str.contains(expectedClasspath));
        Assert.assertFalse("MAPREDUCE_JOB_USER_CLASSPATH_FIRST false, but taking effect!", env_str.startsWith(expectedClasspath));
    }

    @Test(timeout = 120000)
    public void testSetClasspathWithJobClassloader() throws IOException {
        Configuration conf = new Configuration();
        conf.setBoolean(MAPREDUCE_APP_SUBMISSION_CROSS_PLATFORM, true);
        conf.setBoolean(MAPREDUCE_JOB_CLASSLOADER, true);
        Map<String, String> env = new HashMap<String, String>();
        MRApps.setClasspath(env, conf);
        String cp = env.get("CLASSPATH");
        String appCp = env.get("APP_CLASSPATH");
        Assert.assertFalse(("MAPREDUCE_JOB_CLASSLOADER true, but job.jar is in the" + " classpath!"), cp.contains((("jar" + (ApplicationConstants.CLASS_PATH_SEPARATOR)) + "job")));
        Assert.assertFalse("MAPREDUCE_JOB_CLASSLOADER true, but PWD is in the classpath!", cp.contains("PWD"));
        String expectedAppClasspath = StringUtils.join(CLASS_PATH_SEPARATOR, Arrays.asList(PWD.$$(), "job.jar/*", "job.jar/classes/", "job.jar/lib/*", ((PWD.$$()) + "/*")));
        Assert.assertEquals(("MAPREDUCE_JOB_CLASSLOADER true, but job.jar is not in the app" + " classpath!"), expectedAppClasspath, appCp);
    }

    @Test(timeout = 3000000)
    public void testSetClasspathWithFramework() throws IOException {
        final String FRAMEWORK_NAME = "some-framework-name";
        final String FRAMEWORK_PATH = "some-framework-path#" + FRAMEWORK_NAME;
        Configuration conf = new Configuration();
        conf.setBoolean(MAPREDUCE_APP_SUBMISSION_CROSS_PLATFORM, true);
        conf.set(MAPREDUCE_APPLICATION_FRAMEWORK_PATH, FRAMEWORK_PATH);
        Map<String, String> env = new HashMap<String, String>();
        try {
            MRApps.setClasspath(env, conf);
            Assert.fail("Failed to catch framework path set without classpath change");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue("Unexpected IllegalArgumentException", e.getMessage().contains((("Could not locate MapReduce framework name '" + FRAMEWORK_NAME) + "'")));
        }
        env.clear();
        final String FRAMEWORK_CLASSPATH = FRAMEWORK_NAME + "/*.jar";
        conf.set(MAPREDUCE_APPLICATION_CLASSPATH, FRAMEWORK_CLASSPATH);
        MRApps.setClasspath(env, conf);
        final String stdClasspath = StringUtils.join(CLASS_PATH_SEPARATOR, Arrays.asList("job.jar/*", "job.jar/classes/", "job.jar/lib/*", ((PWD.$$()) + "/*")));
        String expectedClasspath = StringUtils.join(CLASS_PATH_SEPARATOR, Arrays.asList(PWD.$$(), FRAMEWORK_CLASSPATH, stdClasspath));
        Assert.assertEquals("Incorrect classpath with framework and no user precedence", expectedClasspath, env.get("CLASSPATH"));
        env.clear();
        conf.setBoolean(MAPREDUCE_JOB_USER_CLASSPATH_FIRST, true);
        MRApps.setClasspath(env, conf);
        expectedClasspath = StringUtils.join(CLASS_PATH_SEPARATOR, Arrays.asList(PWD.$$(), stdClasspath, FRAMEWORK_CLASSPATH));
        Assert.assertEquals("Incorrect classpath with framework and user precedence", expectedClasspath, env.get("CLASSPATH"));
    }

    @Test(timeout = 30000)
    public void testSetupDistributedCacheEmpty() throws IOException {
        Configuration conf = new Configuration();
        Map<String, LocalResource> localResources = new HashMap<String, LocalResource>();
        MRApps.setupDistributedCache(conf, localResources);
        Assert.assertTrue("Empty Config did not produce an empty list of resources", localResources.isEmpty());
    }

    @SuppressWarnings("deprecation")
    @Test(timeout = 120000)
    public void testSetupDistributedCacheConflicts() throws Exception {
        Configuration conf = new Configuration();
        conf.setClass("fs.mockfs.impl", TestMRApps.MockFileSystem.class, FileSystem.class);
        URI mockUri = URI.create("mockfs://mock/");
        FileSystem mockFs = getRawFileSystem();
        URI archive = new URI("mockfs://mock/tmp/something.zip#something");
        Path archivePath = new Path(archive);
        URI file = new URI("mockfs://mock/tmp/something.txt#something");
        Path filePath = new Path(file);
        Mockito.when(mockFs.resolvePath(archivePath)).thenReturn(archivePath);
        Mockito.when(mockFs.resolvePath(filePath)).thenReturn(filePath);
        DistributedCache.addCacheArchive(archive, conf);
        conf.set(CACHE_ARCHIVES_TIMESTAMPS, "10");
        conf.set(CACHE_ARCHIVES_SIZES, "10");
        conf.set(CACHE_ARCHIVES_VISIBILITIES, "true");
        DistributedCache.addCacheFile(file, conf);
        conf.set(CACHE_FILE_TIMESTAMPS, "11");
        conf.set(CACHE_FILES_SIZES, "11");
        conf.set(CACHE_FILE_VISIBILITIES, "true");
        Map<String, LocalResource> localResources = new HashMap<String, LocalResource>();
        MRApps.setupDistributedCache(conf, localResources);
        Assert.assertEquals(1, localResources.size());
        LocalResource lr = localResources.get("something");
        // Archive wins
        Assert.assertNotNull(lr);
        Assert.assertEquals(10L, lr.getSize());
        Assert.assertEquals(10L, lr.getTimestamp());
        Assert.assertEquals(ARCHIVE, lr.getType());
    }

    @SuppressWarnings("deprecation")
    @Test(timeout = 120000)
    public void testSetupDistributedCacheConflictsFiles() throws Exception {
        Configuration conf = new Configuration();
        conf.setClass("fs.mockfs.impl", TestMRApps.MockFileSystem.class, FileSystem.class);
        URI mockUri = URI.create("mockfs://mock/");
        FileSystem mockFs = getRawFileSystem();
        URI file = new URI("mockfs://mock/tmp/something.zip#something");
        Path filePath = new Path(file);
        URI file2 = new URI("mockfs://mock/tmp/something.txt#something");
        Path file2Path = new Path(file2);
        Mockito.when(mockFs.resolvePath(filePath)).thenReturn(filePath);
        Mockito.when(mockFs.resolvePath(file2Path)).thenReturn(file2Path);
        DistributedCache.addCacheFile(file, conf);
        DistributedCache.addCacheFile(file2, conf);
        conf.set(CACHE_FILE_TIMESTAMPS, "10,11");
        conf.set(CACHE_FILES_SIZES, "10,11");
        conf.set(CACHE_FILE_VISIBILITIES, "true,true");
        Map<String, LocalResource> localResources = new HashMap<String, LocalResource>();
        MRApps.setupDistributedCache(conf, localResources);
        Assert.assertEquals(1, localResources.size());
        LocalResource lr = localResources.get("something");
        // First one wins
        Assert.assertNotNull(lr);
        Assert.assertEquals(10L, lr.getSize());
        Assert.assertEquals(10L, lr.getTimestamp());
        Assert.assertEquals(FILE, lr.getType());
    }

    @SuppressWarnings("deprecation")
    @Test(timeout = 30000)
    public void testSetupDistributedCache() throws Exception {
        Configuration conf = new Configuration();
        conf.setClass("fs.mockfs.impl", TestMRApps.MockFileSystem.class, FileSystem.class);
        URI mockUri = URI.create("mockfs://mock/");
        FileSystem mockFs = getRawFileSystem();
        URI archive = new URI("mockfs://mock/tmp/something.zip");
        Path archivePath = new Path(archive);
        URI file = new URI("mockfs://mock/tmp/something.txt#something");
        Path filePath = new Path(file);
        Mockito.when(mockFs.resolvePath(archivePath)).thenReturn(archivePath);
        Mockito.when(mockFs.resolvePath(filePath)).thenReturn(filePath);
        DistributedCache.addCacheArchive(archive, conf);
        conf.set(CACHE_ARCHIVES_TIMESTAMPS, "10");
        conf.set(CACHE_ARCHIVES_SIZES, "10");
        conf.set(CACHE_ARCHIVES_VISIBILITIES, "true");
        DistributedCache.addCacheFile(file, conf);
        conf.set(CACHE_FILE_TIMESTAMPS, "11");
        conf.set(CACHE_FILES_SIZES, "11");
        conf.set(CACHE_FILE_VISIBILITIES, "true");
        Map<String, LocalResource> localResources = new HashMap<String, LocalResource>();
        MRApps.setupDistributedCache(conf, localResources);
        Assert.assertEquals(2, localResources.size());
        LocalResource lr = localResources.get("something.zip");
        Assert.assertNotNull(lr);
        Assert.assertEquals(10L, lr.getSize());
        Assert.assertEquals(10L, lr.getTimestamp());
        Assert.assertEquals(ARCHIVE, lr.getType());
        lr = localResources.get("something");
        Assert.assertNotNull(lr);
        Assert.assertEquals(11L, lr.getSize());
        Assert.assertEquals(11L, lr.getTimestamp());
        Assert.assertEquals(FILE, lr.getType());
    }

    static class MockFileSystem extends FilterFileSystem {
        MockFileSystem() {
            super(Mockito.mock(FileSystem.class));
        }

        public void initialize(URI name, Configuration conf) throws IOException {
        }
    }

    @Test
    public void testLogSystemProperties() throws Exception {
        Configuration conf = new Configuration();
        // test no logging
        conf.set(MAPREDUCE_JVM_SYSTEM_PROPERTIES_TO_LOG, " ");
        String value = MRApps.getSystemPropertiesToLog(conf);
        Assert.assertNull(value);
        // test logging of selected keys
        String classpath = "java.class.path";
        String os = "os.name";
        String version = "java.version";
        conf.set(MAPREDUCE_JVM_SYSTEM_PROPERTIES_TO_LOG, ((classpath + ", ") + os));
        value = MRApps.getSystemPropertiesToLog(conf);
        Assert.assertNotNull(value);
        Assert.assertTrue(value.contains(classpath));
        Assert.assertTrue(value.contains(os));
        Assert.assertFalse(value.contains(version));
    }

    @Test
    public void testTaskStateUI() {
        Assert.assertTrue(PENDING.correspondsTo(SCHEDULED));
        Assert.assertTrue(COMPLETED.correspondsTo(SUCCEEDED));
        Assert.assertTrue(COMPLETED.correspondsTo(FAILED));
        Assert.assertTrue(COMPLETED.correspondsTo(KILLED));
        Assert.assertTrue(RUNNING.correspondsTo(TaskState.RUNNING));
    }

    private static final String[] SYS_CLASSES = new String[]{ "/java/fake/Klass", "/javax/management/fake/Klass", "/org/apache/commons/logging/fake/Klass", "/org/apache/log4j/fake/Klass", "/org/apache/hadoop/fake/Klass" };

    private static final String[] DEFAULT_XMLS = new String[]{ "core-default.xml", "mapred-default.xml", "hdfs-default.xml", "yarn-default.xml" };

    @Test
    public void testSystemClasses() {
        final List<String> systemClasses = Arrays.asList(StringUtils.getTrimmedStrings(SYSTEM_CLASSES_DEFAULT));
        for (String defaultXml : TestMRApps.DEFAULT_XMLS) {
            Assert.assertTrue((defaultXml + " must be system resource"), ApplicationClassLoader.isSystemClass(defaultXml, systemClasses));
        }
        for (String klass : TestMRApps.SYS_CLASSES) {
            Assert.assertTrue((klass + " must be system class"), ApplicationClassLoader.isSystemClass(klass, systemClasses));
        }
        Assert.assertFalse("/fake/Klass must not be a system class", ApplicationClassLoader.isSystemClass("/fake/Klass", systemClasses));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidWebappAddress() throws Exception {
        Configuration conf = new Configuration();
        conf.set(MR_HISTORY_WEBAPP_ADDRESS, "19888");
        MRWebAppUtil.getApplicationWebURLOnJHSWithScheme(conf, ApplicationId.newInstance(0, 1));
    }
}

