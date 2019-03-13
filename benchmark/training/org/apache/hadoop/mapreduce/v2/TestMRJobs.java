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
package org.apache.hadoop.mapreduce.v2;


import FileOutputCommitter.SUCCEEDED_FILE_NAME;
import JobPriority.HIGH;
import JobPriority.NORMAL;
import JobPriority.UNDEFINED_PRIORITY;
import JobStatus.State.SUCCEEDED;
import MRConfig.MASTER_ADDRESS;
import MRJobConfig.IO_SORT_MB;
import MRJobConfig.JOB_UBERTASK_ENABLE;
import MRJobConfig.MAPREDUCE_JOB_CLASSLOADER;
import MRJobConfig.MAP_LOG_LEVEL;
import MRJobConfig.MAP_MAX_ATTEMPTS;
import MRJobConfig.MAX_RESOURCES;
import MRJobConfig.MAX_RESOURCES_MB;
import MRJobConfig.MAX_SINGLE_RESOURCE_MB;
import MRJobConfig.MR_AM_LOG_BACKUPS;
import MRJobConfig.MR_AM_LOG_KB;
import MRJobConfig.MR_AM_LOG_LEVEL;
import MRJobConfig.MR_AM_TO_RM_HEARTBEAT_INTERVAL_MS;
import MRJobConfig.SHARED_CACHE_MODE;
import MRJobConfig.TASK_LOG_BACKUPS;
import MRJobConfig.TASK_TIMEOUT;
import MRJobConfig.TASK_USERLOG_LIMIT;
import RMAppState.FAILED;
import RMAppState.FINISHED;
import RMAppState.KILLED;
import TaskCompletionEvent.Status.TIPFAILED;
import TaskLog.LogName;
import TaskLog.LogName.STDOUT;
import YarnConfiguration.NM_LOG_DIRS;
import YarnConfiguration.RM_SCHEDULER;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URI;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.RandomTextWriterJob;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.SleepJob;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.mapreduce.TaskCompletionEvent;
import org.apache.hadoop.mapreduce.TaskID;
import org.apache.hadoop.mapreduce.TaskType;
import org.apache.hadoop.mapreduce.TypeConverter;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.mapreduce.v2.api.records.JobId;
import org.apache.hadoop.mapreduce.v2.app.AppContext;
import org.apache.hadoop.mapreduce.v2.app.MRAppMaster;
import org.apache.hadoop.mapreduce.v2.app.speculate.DefaultSpeculator;
import org.apache.hadoop.util.ApplicationClassLoader;
import org.apache.hadoop.util.JarFinder;
import org.apache.hadoop.util.Shell;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppState;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FairScheduler;
import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestMRJobs {
    private static final Logger LOG = LoggerFactory.getLogger(TestMRJobs.class);

    private static final EnumSet<RMAppState> TERMINAL_RM_APP_STATES = EnumSet.of(FINISHED, FAILED, KILLED);

    private static final int NUM_NODE_MGRS = 3;

    private static final String TEST_IO_SORT_MB = "11";

    private static final int DEFAULT_REDUCES = 2;

    protected int numSleepReducers = TestMRJobs.DEFAULT_REDUCES;

    protected static MiniMRYarnCluster mrCluster;

    protected static MiniDFSCluster dfsCluster;

    private static Configuration conf = new Configuration();

    private static FileSystem localFs;

    private static FileSystem remoteFs;

    static {
        try {
            TestMRJobs.localFs = FileSystem.getLocal(TestMRJobs.conf);
        } catch (IOException io) {
            throw new RuntimeException("problem getting local fs", io);
        }
    }

    private static Path TEST_ROOT_DIR = TestMRJobs.localFs.makeQualified(new Path("target", ((TestMRJobs.class.getName()) + "-tmpDir")));

    static Path APP_JAR = new Path(TestMRJobs.TEST_ROOT_DIR, "MRAppJar.jar");

    private static final String OUTPUT_ROOT_DIR = "/tmp/" + (TestMRJobs.class.getSimpleName());

    private static final Path TEST_RESOURCES_DIR = new Path(TestMRJobs.TEST_ROOT_DIR, "localizedResources");

    @Test(timeout = 300000)
    public void testSleepJob() throws Exception {
        testSleepJobInternal(false);
    }

    @Test(timeout = 300000)
    public void testSleepJobWithRemoteJar() throws Exception {
        testSleepJobInternal(true);
    }

    @Test(timeout = 300000)
    public void testSleepJobWithLocalResourceUnderLimit() throws Exception {
        Configuration sleepConf = new Configuration(getConfig());
        // set limits to well above what is expected
        sleepConf.setInt(MAX_RESOURCES, 6);
        sleepConf.setLong(MAX_RESOURCES_MB, 6);
        TestMRJobs.setupJobResourceDirs();
        sleepConf.set("tmpfiles", TestMRJobs.TEST_RESOURCES_DIR.toString());
        testSleepJobInternal(sleepConf, false, true, null);
    }

    @Test(timeout = 300000)
    public void testSleepJobWithLocalResourceSizeOverLimit() throws Exception {
        Configuration sleepConf = new Configuration(getConfig());
        // set limits to well below what is expected
        sleepConf.setLong(MAX_RESOURCES_MB, 1);
        TestMRJobs.setupJobResourceDirs();
        sleepConf.set("tmpfiles", TestMRJobs.TEST_RESOURCES_DIR.toString());
        testSleepJobInternal(sleepConf, false, false, TestMRJobs.ResourceViolation.TOTAL_RESOURCE_SIZE);
    }

    @Test(timeout = 300000)
    public void testSleepJobWithLocalResourceNumberOverLimit() throws Exception {
        Configuration sleepConf = new Configuration(getConfig());
        // set limits to well below what is expected
        sleepConf.setInt(MAX_RESOURCES, 1);
        TestMRJobs.setupJobResourceDirs();
        sleepConf.set("tmpfiles", TestMRJobs.TEST_RESOURCES_DIR.toString());
        testSleepJobInternal(sleepConf, false, false, TestMRJobs.ResourceViolation.NUMBER_OF_RESOURCES);
    }

    @Test(timeout = 300000)
    public void testSleepJobWithLocalResourceCheckAndRemoteJar() throws Exception {
        Configuration sleepConf = new Configuration(getConfig());
        // set limits to well above what is expected
        sleepConf.setInt(MAX_RESOURCES, 6);
        sleepConf.setLong(MAX_RESOURCES_MB, 6);
        TestMRJobs.setupJobResourceDirs();
        sleepConf.set("tmpfiles", TestMRJobs.TEST_RESOURCES_DIR.toString());
        testSleepJobInternal(sleepConf, true, true, null);
    }

    @Test(timeout = 300000)
    public void testSleepJobWithLocalIndividualResourceOverLimit() throws Exception {
        Configuration sleepConf = new Configuration(getConfig());
        // set limits to well below what is expected
        sleepConf.setInt(MAX_SINGLE_RESOURCE_MB, 1);
        TestMRJobs.setupJobResourceDirs();
        sleepConf.set("tmpfiles", TestMRJobs.TEST_RESOURCES_DIR.toString());
        testSleepJobInternal(sleepConf, false, false, TestMRJobs.ResourceViolation.SINGLE_RESOURCE_SIZE);
    }

    @Test(timeout = 300000)
    public void testSleepJobWithLocalIndividualResourceUnderLimit() throws Exception {
        Configuration sleepConf = new Configuration(getConfig());
        // set limits to well below what is expected
        sleepConf.setInt(MAX_SINGLE_RESOURCE_MB, 2);
        TestMRJobs.setupJobResourceDirs();
        sleepConf.set("tmpfiles", TestMRJobs.TEST_RESOURCES_DIR.toString());
        testSleepJobInternal(sleepConf, false, true, null);
    }

    private enum ResourceViolation {

        NUMBER_OF_RESOURCES,
        TOTAL_RESOURCE_SIZE,
        SINGLE_RESOURCE_SIZE;}

    @Test(timeout = 3000000)
    public void testJobWithChangePriority() throws Exception {
        Configuration sleepConf = new Configuration(getConfig());
        // Assumption can be removed when FS priority support is implemented
        Assume.assumeFalse(sleepConf.get(RM_SCHEDULER).equals(FairScheduler.class.getCanonicalName()));
        if (!(new File(MiniMRYarnCluster.APPJAR).exists())) {
            TestMRJobs.LOG.info((("MRAppJar " + (MiniMRYarnCluster.APPJAR)) + " not found. Not running test."));
            return;
        }
        // set master address to local to test that local mode applied if framework
        // equals local
        sleepConf.set(MASTER_ADDRESS, "local");
        sleepConf.setInt(MR_AM_TO_RM_HEARTBEAT_INTERVAL_MS, 5);
        SleepJob sleepJob = new SleepJob();
        sleepJob.setConf(sleepConf);
        Job job = sleepJob.createJob(1, 1, 1000, 20, 50, 1);
        job.addFileToClassPath(TestMRJobs.APP_JAR);// The AppMaster jar itself.

        job.setJarByClass(SleepJob.class);
        job.setMaxMapAttempts(1);// speed up failures

        job.submit();
        // Set the priority to HIGH
        job.setPriority(HIGH);
        waitForPriorityToUpdate(job, HIGH);
        // Verify the priority from job itself
        Assert.assertEquals(job.getPriority(), HIGH);
        // Change priority to NORMAL (3) with new api
        job.setPriorityAsInteger(3);// Verify the priority from job itself

        waitForPriorityToUpdate(job, NORMAL);
        Assert.assertEquals(job.getPriority(), NORMAL);
        // Change priority to a high integer value with new api
        job.setPriorityAsInteger(89);// Verify the priority from job itself

        waitForPriorityToUpdate(job, UNDEFINED_PRIORITY);
        Assert.assertEquals(job.getPriority(), UNDEFINED_PRIORITY);
        boolean succeeded = job.waitForCompletion(true);
        Assert.assertTrue(succeeded);
        Assert.assertEquals(SUCCEEDED, job.getJobState());
    }

    @Test(timeout = 300000)
    public void testJobClassloader() throws IOException, ClassNotFoundException, InterruptedException {
        testJobClassloader(false);
    }

    @Test(timeout = 300000)
    public void testJobClassloaderWithCustomClasses() throws IOException, ClassNotFoundException, InterruptedException {
        testJobClassloader(true);
    }

    public static class CustomOutputFormat<K, V> extends NullOutputFormat<K, V> {
        public CustomOutputFormat() {
            verifyClassLoader(getClass());
        }

        /**
         * Verifies that the class was loaded by the job classloader if it is in the
         * context of the MRAppMaster, and if not throws an exception to fail the
         * job.
         */
        private void verifyClassLoader(Class<?> cls) {
            // to detect that it is instantiated in the context of the MRAppMaster, we
            // inspect the stack trace and determine a caller is MRAppMaster
            for (StackTraceElement e : new Throwable().getStackTrace()) {
                if ((e.getClassName().equals(MRAppMaster.class.getName())) && (!((cls.getClassLoader()) instanceof ApplicationClassLoader))) {
                    throw new ExceptionInInitializerError("incorrect classloader used");
                }
            }
        }
    }

    public static class CustomSpeculator extends DefaultSpeculator {
        public CustomSpeculator(Configuration conf, AppContext context) {
            super(conf, context);
            verifyClassLoader(getClass());
        }

        /**
         * Verifies that the class was loaded by the job classloader if it is in the
         * context of the MRAppMaster, and if not throws an exception to fail the
         * job.
         */
        private void verifyClassLoader(Class<?> cls) {
            // to detect that it is instantiated in the context of the MRAppMaster, we
            // inspect the stack trace and determine a caller is MRAppMaster
            for (StackTraceElement e : new Throwable().getStackTrace()) {
                if ((e.getClassName().equals(MRAppMaster.class.getName())) && (!((cls.getClassLoader()) instanceof ApplicationClassLoader))) {
                    throw new ExceptionInInitializerError("incorrect classloader used");
                }
            }
        }
    }

    @Test(timeout = 60000)
    public void testRandomWriter() throws IOException, ClassNotFoundException, InterruptedException {
        TestMRJobs.LOG.info("\n\n\nStarting testRandomWriter().");
        if (!(new File(MiniMRYarnCluster.APPJAR).exists())) {
            TestMRJobs.LOG.info((("MRAppJar " + (MiniMRYarnCluster.APPJAR)) + " not found. Not running test."));
            return;
        }
        RandomTextWriterJob randomWriterJob = new RandomTextWriterJob();
        getConfig().set(RandomTextWriterJob.TOTAL_BYTES, "3072");
        getConfig().set(RandomTextWriterJob.BYTES_PER_MAP, "1024");
        Job job = randomWriterJob.createJob(getConfig());
        Path outputDir = new Path(TestMRJobs.OUTPUT_ROOT_DIR, "random-output");
        FileOutputFormat.setOutputPath(job, outputDir);
        job.setSpeculativeExecution(false);
        job.addFileToClassPath(TestMRJobs.APP_JAR);// The AppMaster jar itself.

        job.setJarByClass(RandomTextWriterJob.class);
        job.setMaxMapAttempts(1);// speed up failures

        job.submit();
        String trackingUrl = job.getTrackingURL();
        String jobId = job.getJobID().toString();
        boolean succeeded = job.waitForCompletion(true);
        Assert.assertTrue(succeeded);
        Assert.assertEquals(SUCCEEDED, job.getJobState());
        Assert.assertTrue(((("Tracking URL was " + trackingUrl) + " but didn't Match Job ID ") + jobId), trackingUrl.endsWith(((jobId.substring(jobId.lastIndexOf("_"))) + "/")));
        // Make sure there are three files in the output-dir
        RemoteIterator<FileStatus> iterator = FileContext.getFileContext(getConfig()).listStatus(outputDir);
        int count = 0;
        while (iterator.hasNext()) {
            FileStatus file = iterator.next();
            if (!(file.getPath().getName().equals(SUCCEEDED_FILE_NAME))) {
                count++;
            }
        } 
        Assert.assertEquals("Number of part files is wrong!", 3, count);
        verifyRandomWriterCounters(job);
        // TODO later:  add explicit "isUber()" checks of some sort
    }

    @Test(timeout = 60000)
    public void testFailingMapper() throws IOException, ClassNotFoundException, InterruptedException {
        TestMRJobs.LOG.info("\n\n\nStarting testFailingMapper().");
        if (!(new File(MiniMRYarnCluster.APPJAR).exists())) {
            TestMRJobs.LOG.info((("MRAppJar " + (MiniMRYarnCluster.APPJAR)) + " not found. Not running test."));
            return;
        }
        Job job = runFailingMapperJob();
        TaskID taskID = new TaskID(job.getJobID(), TaskType.MAP, 0);
        TaskAttemptID aId = new TaskAttemptID(taskID, 0);
        System.out.println((("Diagnostics for " + aId) + " :"));
        for (String diag : job.getTaskDiagnostics(aId)) {
            System.out.println(diag);
        }
        aId = new TaskAttemptID(taskID, 1);
        System.out.println((("Diagnostics for " + aId) + " :"));
        for (String diag : job.getTaskDiagnostics(aId)) {
            System.out.println(diag);
        }
        TaskCompletionEvent[] events = job.getTaskCompletionEvents(0, 2);
        Assert.assertEquals(TaskCompletionEvent.Status.FAILED, events[0].getStatus());
        Assert.assertEquals(TIPFAILED, events[1].getStatus());
        Assert.assertEquals(JobStatus.State.FAILED, job.getJobState());
        verifyFailingMapperCounters(job);
        // TODO later:  add explicit "isUber()" checks of some sort
    }

    @Test(timeout = 120000)
    public void testContainerRollingLog() throws IOException, ClassNotFoundException, InterruptedException {
        if (!(new File(MiniMRYarnCluster.APPJAR).exists())) {
            TestMRJobs.LOG.info((("MRAppJar " + (MiniMRYarnCluster.APPJAR)) + " not found. Not running test."));
            return;
        }
        final SleepJob sleepJob = new SleepJob();
        final JobConf sleepConf = new JobConf(getConfig());
        sleepConf.set(MAP_LOG_LEVEL, Level.ALL.toString());
        final long userLogKb = 4;
        sleepConf.setLong(TASK_USERLOG_LIMIT, userLogKb);
        sleepConf.setInt(TASK_LOG_BACKUPS, 3);
        sleepConf.set(MR_AM_LOG_LEVEL, Level.ALL.toString());
        final long amLogKb = 7;
        sleepConf.setLong(MR_AM_LOG_KB, amLogKb);
        sleepConf.setInt(MR_AM_LOG_BACKUPS, 7);
        sleepJob.setConf(sleepConf);
        final Job job = sleepJob.createJob(1, 0, 1L, 100, 0L, 0);
        job.setJarByClass(SleepJob.class);
        job.addFileToClassPath(TestMRJobs.APP_JAR);// The AppMaster jar itself.

        job.waitForCompletion(true);
        final JobId jobId = TypeConverter.toYarn(job.getJobID());
        final ApplicationId appID = jobId.getAppId();
        int pollElapsed = 0;
        while (true) {
            Thread.sleep(1000);
            pollElapsed += 1000;
            if (TestMRJobs.TERMINAL_RM_APP_STATES.contains(getResourceManager().getRMContext().getRMApps().get(appID).getState())) {
                break;
            }
            if (pollElapsed >= 60000) {
                TestMRJobs.LOG.warn("application did not reach terminal state within 60 seconds");
                break;
            }
        } 
        Assert.assertEquals(FINISHED, getResourceManager().getRMContext().getRMApps().get(appID).getState());
        // Job finished, verify logs
        // 
        final String appIdStr = appID.toString();
        final String appIdSuffix = appIdStr.substring("application_".length(), appIdStr.length());
        final String containerGlob = ("container_" + appIdSuffix) + "_*_*";
        final String syslogGlob = (((appIdStr + (Path.SEPARATOR)) + containerGlob) + (Path.SEPARATOR)) + (LogName.SYSLOG);
        int numAppMasters = 0;
        int numMapTasks = 0;
        for (int i = 0; i < (TestMRJobs.NUM_NODE_MGRS); i++) {
            final Configuration nmConf = TestMRJobs.mrCluster.getNodeManager(i).getConfig();
            for (String logDir : nmConf.getTrimmedStrings(NM_LOG_DIRS)) {
                final Path absSyslogGlob = new Path(((logDir + (Path.SEPARATOR)) + syslogGlob));
                TestMRJobs.LOG.info(("Checking for glob: " + absSyslogGlob));
                final FileStatus[] syslogs = TestMRJobs.localFs.globStatus(absSyslogGlob);
                for (FileStatus slog : syslogs) {
                    boolean foundAppMaster = job.isUber();
                    final Path containerPathComponent = slog.getPath().getParent();
                    if (!foundAppMaster) {
                        final ContainerId cid = ContainerId.fromString(containerPathComponent.getName());
                        foundAppMaster = ((cid.getContainerId()) & (ContainerId.CONTAINER_ID_BITMASK)) == 1;
                    }
                    final FileStatus[] sysSiblings = TestMRJobs.localFs.globStatus(new Path(containerPathComponent, ((LogName.SYSLOG) + "*")));
                    // sort to ensure for i > 0 sysSiblings[i] == "syslog.i"
                    Arrays.sort(sysSiblings);
                    if (foundAppMaster) {
                        numAppMasters++;
                    } else {
                        numMapTasks++;
                    }
                    if (foundAppMaster) {
                        Assert.assertSame("Unexpected number of AM sylog* files", ((sleepConf.getInt(MR_AM_LOG_BACKUPS, 0)) + 1), sysSiblings.length);
                        Assert.assertTrue(("AM syslog.1 length kb should be >= " + amLogKb), ((sysSiblings[1].getLen()) >= (amLogKb * 1024)));
                    } else {
                        Assert.assertSame("Unexpected number of MR task sylog* files", ((sleepConf.getInt(TASK_LOG_BACKUPS, 0)) + 1), sysSiblings.length);
                        Assert.assertTrue(("MR syslog.1 length kb should be >= " + userLogKb), ((sysSiblings[1].getLen()) >= (userLogKb * 1024)));
                    }
                }
            }
        }
        // Make sure we checked non-empty set
        // 
        Assert.assertEquals("No AppMaster log found!", 1, numAppMasters);
        if (sleepConf.getBoolean(JOB_UBERTASK_ENABLE, false)) {
            Assert.assertEquals("MapTask log with uber found!", 0, numMapTasks);
        } else {
            Assert.assertEquals("No MapTask log found!", 1, numMapTasks);
        }
    }

    public static class DistributedCacheChecker extends Mapper<LongWritable, Text, NullWritable, NullWritable> {
        @Override
        public void setup(Context context) throws IOException {
            Configuration conf = context.getConfiguration();
            Path[] localFiles = context.getLocalCacheFiles();
            URI[] files = context.getCacheFiles();
            Path[] localArchives = context.getLocalCacheArchives();
            URI[] archives = context.getCacheArchives();
            // Check that 4 (2 + appjar + DistrubutedCacheChecker jar) files
            // and 2 archives are present
            Assert.assertEquals(4, localFiles.length);
            Assert.assertEquals(4, files.length);
            Assert.assertEquals(2, localArchives.length);
            Assert.assertEquals(2, archives.length);
            // Check lengths of the files
            Map<String, Path> filesMap = TestMRJobs.DistributedCacheChecker.pathsToMap(localFiles);
            Assert.assertTrue(filesMap.containsKey("distributed.first.symlink"));
            Assert.assertEquals(1, TestMRJobs.localFs.getFileStatus(filesMap.get("distributed.first.symlink")).getLen());
            Assert.assertTrue(filesMap.containsKey("distributed.second.jar"));
            Assert.assertTrue(((TestMRJobs.localFs.getFileStatus(filesMap.get("distributed.second.jar")).getLen()) > 1));
            // Check extraction of the archive
            Map<String, Path> archivesMap = TestMRJobs.DistributedCacheChecker.pathsToMap(localArchives);
            Assert.assertTrue(archivesMap.containsKey("distributed.third.jar"));
            Assert.assertTrue(TestMRJobs.localFs.exists(new Path(archivesMap.get("distributed.third.jar"), "distributed.jar.inside3")));
            Assert.assertTrue(archivesMap.containsKey("distributed.fourth.jar"));
            Assert.assertTrue(TestMRJobs.localFs.exists(new Path(archivesMap.get("distributed.fourth.jar"), "distributed.jar.inside4")));
            // Check the class loaders
            TestMRJobs.LOG.info(("Java Classpath: " + (System.getProperty("java.class.path"))));
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            // Both the file and the archive should have been added to classpath, so
            // both should be reachable via the class loader.
            Assert.assertNotNull(cl.getResource("distributed.jar.inside2"));
            Assert.assertNotNull(cl.getResource("distributed.jar.inside3"));
            Assert.assertNotNull(cl.getResource("distributed.jar.inside4"));
            // The Job Jar should have been extracted to a folder named "job.jar" and
            // added to the classpath; the two jar files in the lib folder in the Job
            // Jar should have also been added to the classpath
            Assert.assertNotNull(cl.getResource("job.jar/"));
            Assert.assertNotNull(cl.getResource("job.jar/lib/lib1.jar"));
            Assert.assertNotNull(cl.getResource("job.jar/lib/lib2.jar"));
            // Check that the symlink for the renaming was created in the cwd;
            File symlinkFile = new File("distributed.first.symlink");
            Assert.assertTrue(symlinkFile.exists());
            Assert.assertEquals(1, symlinkFile.length());
            // Check that the symlink for the Job Jar was created in the cwd and
            // points to the extracted directory
            File jobJarDir = new File("job.jar");
            if (Shell.WINDOWS) {
                Assert.assertTrue(TestMRJobs.DistributedCacheChecker.isWindowsSymlinkedDirectory(jobJarDir));
            } else {
                Assert.assertTrue(FileUtils.isSymlink(jobJarDir));
                Assert.assertTrue(jobJarDir.isDirectory());
            }
        }

        /**
         * Used on Windows to determine if the specified file is a symlink that
         * targets a directory.  On most platforms, these checks can be done using
         * commons-io.  On Windows, the commons-io implementation is unreliable and
         * always returns false.  Instead, this method checks the output of the dir
         * command.  After migrating to Java 7, this method can be removed in favor
         * of the new method java.nio.file.Files.isSymbolicLink, which is expected to
         * work cross-platform.
         *
         * @param file
         * 		File to check
         * @return boolean true if the file is a symlink that targets a directory
         * @throws IOException
         * 		thrown for any I/O error
         */
        private static boolean isWindowsSymlinkedDirectory(File file) throws IOException {
            String dirOut = Shell.execCommand("cmd", "/c", "dir", file.getAbsoluteFile().getParent());
            StringReader sr = new StringReader(dirOut);
            BufferedReader br = new BufferedReader(sr);
            try {
                String line = br.readLine();
                while (line != null) {
                    line = br.readLine();
                    if ((line.contains(file.getName())) && (line.contains("<SYMLINKD>"))) {
                        return true;
                    }
                } 
                return false;
            } finally {
                IOUtils.closeStream(br);
                IOUtils.closeStream(sr);
            }
        }

        /**
         * Returns a mapping of the final component of each path to the corresponding
         * Path instance.  This assumes that every given Path has a unique string in
         * the final path component, which is true for these tests.
         *
         * @param paths
         * 		Path[] to map
         * @return Map<String, Path> mapping the final component of each path to the
        corresponding Path instance
         */
        private static Map<String, Path> pathsToMap(Path[] paths) {
            Map<String, Path> map = new HashMap<String, Path>();
            for (Path path : paths) {
                map.put(path.getName(), path);
            }
            return map;
        }
    }

    @Test(timeout = 300000)
    public void testDistributedCache() throws Exception {
        testDistributedCache(false);
    }

    @Test(timeout = 300000)
    public void testDistributedCacheWithWildcards() throws Exception {
        testDistributedCache(true);
    }

    @Test(timeout = 120000)
    public void testThreadDumpOnTaskTimeout() throws IOException, ClassNotFoundException, InterruptedException {
        if (!(new File(MiniMRYarnCluster.APPJAR).exists())) {
            TestMRJobs.LOG.info((("MRAppJar " + (MiniMRYarnCluster.APPJAR)) + " not found. Not running test."));
            return;
        }
        final SleepJob sleepJob = new SleepJob();
        final JobConf sleepConf = new JobConf(getConfig());
        sleepConf.setLong(TASK_TIMEOUT, (3 * 1000L));
        sleepConf.setInt(MAP_MAX_ATTEMPTS, 1);
        sleepJob.setConf(sleepConf);
        if ((this) instanceof TestUberAM) {
            sleepConf.setInt(MR_AM_TO_RM_HEARTBEAT_INTERVAL_MS, (30 * 1000));
        }
        // sleep for 10 seconds to trigger a kill with thread dump
        final Job job = sleepJob.createJob(1, 0, ((10 * 60) * 1000L), 1, 0L, 0);
        job.setJarByClass(SleepJob.class);
        job.addFileToClassPath(TestMRJobs.APP_JAR);// The AppMaster jar itself.

        job.waitForCompletion(true);
        final JobId jobId = TypeConverter.toYarn(job.getJobID());
        final ApplicationId appID = jobId.getAppId();
        int pollElapsed = 0;
        while (true) {
            Thread.sleep(1000);
            pollElapsed += 1000;
            if (TestMRJobs.TERMINAL_RM_APP_STATES.contains(getResourceManager().getRMContext().getRMApps().get(appID).getState())) {
                break;
            }
            if (pollElapsed >= 60000) {
                TestMRJobs.LOG.warn("application did not reach terminal state within 60 seconds");
                break;
            }
        } 
        // Job finished, verify logs
        // 
        final String appIdStr = appID.toString();
        final String appIdSuffix = appIdStr.substring("application_".length(), appIdStr.length());
        final String containerGlob = ("container_" + appIdSuffix) + "_*_*";
        final String syslogGlob = (((appIdStr + (Path.SEPARATOR)) + containerGlob) + (Path.SEPARATOR)) + (LogName.SYSLOG);
        int numAppMasters = 0;
        int numMapTasks = 0;
        for (int i = 0; i < (TestMRJobs.NUM_NODE_MGRS); i++) {
            final Configuration nmConf = TestMRJobs.mrCluster.getNodeManager(i).getConfig();
            for (String logDir : nmConf.getTrimmedStrings(NM_LOG_DIRS)) {
                final Path absSyslogGlob = new Path(((logDir + (Path.SEPARATOR)) + syslogGlob));
                TestMRJobs.LOG.info(("Checking for glob: " + absSyslogGlob));
                for (FileStatus syslog : TestMRJobs.localFs.globStatus(absSyslogGlob)) {
                    boolean foundAppMaster = false;
                    boolean foundThreadDump = false;
                    // Determine the container type
                    final BufferedReader syslogReader = new BufferedReader(new InputStreamReader(TestMRJobs.localFs.open(syslog.getPath())));
                    try {
                        for (String line; (line = syslogReader.readLine()) != null;) {
                            if (line.contains(MRAppMaster.class.getName())) {
                                foundAppMaster = true;
                                break;
                            }
                        }
                    } finally {
                        syslogReader.close();
                    }
                    // Check for thread dump in stdout
                    final Path stdoutPath = new Path(syslog.getPath().getParent(), STDOUT.toString());
                    final BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(TestMRJobs.localFs.open(stdoutPath)));
                    try {
                        for (String line; (line = stdoutReader.readLine()) != null;) {
                            if (line.contains("Full thread dump")) {
                                foundThreadDump = true;
                                break;
                            }
                        }
                    } finally {
                        stdoutReader.close();
                    }
                    if (foundAppMaster) {
                        numAppMasters++;
                        if ((this) instanceof TestUberAM) {
                            Assert.assertTrue("No thread dump", foundThreadDump);
                        } else {
                            Assert.assertFalse("Unexpected thread dump", foundThreadDump);
                        }
                    } else {
                        numMapTasks++;
                        Assert.assertTrue("No thread dump", foundThreadDump);
                    }
                }
            }
        }
        // Make sure we checked non-empty set
        // 
        Assert.assertEquals("No AppMaster log found!", 1, numAppMasters);
        if (sleepConf.getBoolean(JOB_UBERTASK_ENABLE, false)) {
            Assert.assertSame("MapTask log with uber found!", 0, numMapTasks);
        } else {
            Assert.assertSame("No MapTask log found!", 1, numMapTasks);
        }
    }

    @Test
    public void testSharedCache() throws Exception {
        Path localJobJarPath = makeJobJarWithLib(TestMRJobs.TEST_ROOT_DIR.toUri().toString());
        if (!(new File(MiniMRYarnCluster.APPJAR).exists())) {
            TestMRJobs.LOG.info((("MRAppJar " + (MiniMRYarnCluster.APPJAR)) + " not found. Not running test."));
            return;
        }
        Job job = Job.getInstance(getConfig());
        Configuration jobConf = job.getConfiguration();
        jobConf.set(SHARED_CACHE_MODE, "enabled");
        Path inputFile = createTempFile("input-file", "x");
        // Create jars with a single file inside them.
        Path second = makeJar(new Path(TestMRJobs.TEST_ROOT_DIR, "distributed.second.jar"), 2);
        Path third = makeJar(new Path(TestMRJobs.TEST_ROOT_DIR, "distributed.third.jar"), 3);
        Path fourth = makeJar(new Path(TestMRJobs.TEST_ROOT_DIR, "distributed.fourth.jar"), 4);
        // Add libjars to job conf
        jobConf.set("tmpjars", (((((second.toString()) + ",") + (third.toString())) + ",") + (fourth.toString())));
        // Because the job jar is a "dummy" jar, we need to include the jar with
        // DistributedCacheChecker or it won't be able to find it
        Path distributedCacheCheckerJar = new Path(JarFinder.getJar(TestMRJobs.SharedCacheChecker.class));
        job.addFileToClassPath(distributedCacheCheckerJar.makeQualified(TestMRJobs.localFs.getUri(), distributedCacheCheckerJar.getParent()));
        job.setMapperClass(TestMRJobs.SharedCacheChecker.class);
        job.setOutputFormatClass(NullOutputFormat.class);
        FileInputFormat.setInputPaths(job, inputFile);
        job.setMaxMapAttempts(1);// speed up failures

        job.submit();
        String trackingUrl = job.getTrackingURL();
        String jobId = job.getJobID().toString();
        Assert.assertTrue(job.waitForCompletion(true));
        Assert.assertTrue(((("Tracking URL was " + trackingUrl) + " but didn't Match Job ID ") + jobId), trackingUrl.endsWith(((jobId.substring(jobId.lastIndexOf("_"))) + "/")));
    }

    /**
     * An identity mapper for testing the shared cache.
     */
    public static class SharedCacheChecker extends Mapper<LongWritable, Text, NullWritable, NullWritable> {
        @Override
        public void setup(Context context) throws IOException {
        }
    }

    public static class ConfVerificationMapper extends SleepJob.SleepMapper {
        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            super.setup(context);
            final Configuration conf = context.getConfiguration();
            // check if the job classloader is enabled and verify the TCCL
            if (conf.getBoolean(MAPREDUCE_JOB_CLASSLOADER, false)) {
                ClassLoader tccl = Thread.currentThread().getContextClassLoader();
                if (!(tccl instanceof ApplicationClassLoader)) {
                    throw new IOException(((("TCCL expected: " + (ApplicationClassLoader.class.getName())) + ", actual: ") + (tccl.getClass().getName())));
                }
            }
            final String ioSortMb = conf.get(IO_SORT_MB);
            if (!(TestMRJobs.TEST_IO_SORT_MB.equals(ioSortMb))) {
                throw new IOException(((("io.sort.mb expected: " + (TestMRJobs.TEST_IO_SORT_MB)) + ", actual: ") + ioSortMb));
            }
        }
    }
}

