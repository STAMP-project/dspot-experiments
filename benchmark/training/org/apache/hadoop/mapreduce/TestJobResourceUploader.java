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
package org.apache.hadoop.mapreduce;


import MRJobConfig.CACHE_ARCHIVES;
import MRJobConfig.CACHE_FILES;
import MRJobConfig.MAX_RESOURCES;
import MRJobConfig.MAX_RESOURCES_MB;
import MRJobConfig.MAX_SINGLE_RESOURCE_MB;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.mapred.JobConf;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * A class for unit testing JobResourceUploader.
 */
public class TestJobResourceUploader {
    @Test
    public void testStringToPath() throws IOException {
        Configuration conf = new Configuration();
        JobResourceUploader uploader = new JobResourceUploader(FileSystem.getLocal(conf), false);
        Assert.assertEquals("Failed: absolute, no scheme, with fragment", "/testWithFragment.txt", uploader.stringToPath("/testWithFragment.txt#fragment.txt").toString());
        Assert.assertEquals("Failed: absolute, with scheme, with fragment", "file:/testWithFragment.txt", uploader.stringToPath("file:///testWithFragment.txt#fragment.txt").toString());
        Assert.assertEquals("Failed: relative, no scheme, with fragment", "testWithFragment.txt", uploader.stringToPath("testWithFragment.txt#fragment.txt").toString());
        Assert.assertEquals("Failed: relative, no scheme, no fragment", "testWithFragment.txt", uploader.stringToPath("testWithFragment.txt").toString());
        Assert.assertEquals("Failed: absolute, with scheme, no fragment", "file:/testWithFragment.txt", uploader.stringToPath("file:///testWithFragment.txt").toString());
    }

    @Test
    public void testAllDefaults() throws IOException {
        TestJobResourceUploader.ResourceConf.Builder b = new TestJobResourceUploader.ResourceConf.Builder();
        runLimitsTest(b.build(), true, null);
    }

    @Test
    public void testNoLimitsWithResources() throws IOException {
        TestJobResourceUploader.ResourceConf.Builder b = new TestJobResourceUploader.ResourceConf.Builder();
        b.setNumOfDCArchives(1);
        b.setNumOfDCFiles(1);
        b.setNumOfTmpArchives(10);
        b.setNumOfTmpFiles(1);
        b.setNumOfTmpLibJars(1);
        b.setJobJar(true);
        b.setSizeOfResource(10);
        runLimitsTest(b.build(), true, null);
    }

    @Test
    public void testAtResourceLimit() throws IOException {
        TestJobResourceUploader.ResourceConf.Builder b = new TestJobResourceUploader.ResourceConf.Builder();
        b.setNumOfDCArchives(1);
        b.setNumOfDCFiles(1);
        b.setNumOfTmpArchives(1);
        b.setNumOfTmpFiles(1);
        b.setNumOfTmpLibJars(1);
        b.setJobJar(true);
        b.setMaxResources(6);
        runLimitsTest(b.build(), true, null);
    }

    @Test
    public void testOverResourceLimit() throws IOException {
        TestJobResourceUploader.ResourceConf.Builder b = new TestJobResourceUploader.ResourceConf.Builder();
        b.setNumOfDCArchives(1);
        b.setNumOfDCFiles(1);
        b.setNumOfTmpArchives(1);
        b.setNumOfTmpFiles(2);
        b.setNumOfTmpLibJars(1);
        b.setJobJar(true);
        b.setMaxResources(6);
        runLimitsTest(b.build(), false, TestJobResourceUploader.ResourceViolation.NUMBER_OF_RESOURCES);
    }

    @Test
    public void testAtResourcesMBLimit() throws IOException {
        TestJobResourceUploader.ResourceConf.Builder b = new TestJobResourceUploader.ResourceConf.Builder();
        b.setNumOfDCArchives(1);
        b.setNumOfDCFiles(1);
        b.setNumOfTmpArchives(1);
        b.setNumOfTmpFiles(2);
        b.setNumOfTmpLibJars(1);
        b.setJobJar(true);
        b.setMaxResourcesMB(7);
        b.setSizeOfResource(1);
        runLimitsTest(b.build(), true, null);
    }

    @Test
    public void testOverResourcesMBLimit() throws IOException {
        TestJobResourceUploader.ResourceConf.Builder b = new TestJobResourceUploader.ResourceConf.Builder();
        b.setNumOfDCArchives(1);
        b.setNumOfDCFiles(2);
        b.setNumOfTmpArchives(1);
        b.setNumOfTmpFiles(2);
        b.setNumOfTmpLibJars(1);
        b.setJobJar(true);
        b.setMaxResourcesMB(7);
        b.setSizeOfResource(1);
        runLimitsTest(b.build(), false, TestJobResourceUploader.ResourceViolation.TOTAL_RESOURCE_SIZE);
    }

    @Test
    public void testAtSingleResourceMBLimit() throws IOException {
        TestJobResourceUploader.ResourceConf.Builder b = new TestJobResourceUploader.ResourceConf.Builder();
        b.setNumOfDCArchives(1);
        b.setNumOfDCFiles(2);
        b.setNumOfTmpArchives(1);
        b.setNumOfTmpFiles(2);
        b.setNumOfTmpLibJars(1);
        b.setJobJar(true);
        b.setMaxSingleResourceMB(1);
        b.setSizeOfResource(1);
        runLimitsTest(b.build(), true, null);
    }

    @Test
    public void testOverSingleResourceMBLimit() throws IOException {
        TestJobResourceUploader.ResourceConf.Builder b = new TestJobResourceUploader.ResourceConf.Builder();
        b.setNumOfDCArchives(1);
        b.setNumOfDCFiles(2);
        b.setNumOfTmpArchives(1);
        b.setNumOfTmpFiles(2);
        b.setNumOfTmpLibJars(1);
        b.setJobJar(true);
        b.setMaxSingleResourceMB(1);
        b.setSizeOfResource(10);
        runLimitsTest(b.build(), false, TestJobResourceUploader.ResourceViolation.SINGLE_RESOURCE_SIZE);
    }

    private String destinationPathPrefix = "hdfs:///destinationPath/";

    private String[] expectedFilesNoFrags = new String[]{ (destinationPathPrefix) + "tmpFiles0.txt", (destinationPathPrefix) + "tmpFiles1.txt", (destinationPathPrefix) + "tmpFiles2.txt", (destinationPathPrefix) + "tmpFiles3.txt", (destinationPathPrefix) + "tmpFiles4.txt", (destinationPathPrefix) + "tmpjars0.jar", (destinationPathPrefix) + "tmpjars1.jar" };

    private String[] expectedFilesWithFrags = new String[]{ (destinationPathPrefix) + "tmpFiles0.txt#tmpFilesfragment0.txt", (destinationPathPrefix) + "tmpFiles1.txt#tmpFilesfragment1.txt", (destinationPathPrefix) + "tmpFiles2.txt#tmpFilesfragment2.txt", (destinationPathPrefix) + "tmpFiles3.txt#tmpFilesfragment3.txt", (destinationPathPrefix) + "tmpFiles4.txt#tmpFilesfragment4.txt", (destinationPathPrefix) + "tmpjars0.jar#tmpjarsfragment0.jar", (destinationPathPrefix) + "tmpjars1.jar#tmpjarsfragment1.jar" };

    // We use the local fs for the submitFS in the StubedUploader, so libjars
    // should be replaced with a single path.
    private String[] expectedFilesWithWildcard = new String[]{ (destinationPathPrefix) + "tmpFiles0.txt", (destinationPathPrefix) + "tmpFiles1.txt", (destinationPathPrefix) + "tmpFiles2.txt", (destinationPathPrefix) + "tmpFiles3.txt", (destinationPathPrefix) + "tmpFiles4.txt", "file:///libjars-submit-dir/libjars/*" };

    private String[] expectedArchivesNoFrags = new String[]{ (destinationPathPrefix) + "tmpArchives0.tgz", (destinationPathPrefix) + "tmpArchives1.tgz" };

    private String[] expectedArchivesWithFrags = new String[]{ (destinationPathPrefix) + "tmpArchives0.tgz#tmpArchivesfragment0.tgz", (destinationPathPrefix) + "tmpArchives1.tgz#tmpArchivesfragment1.tgz" };

    private String jobjarSubmitDir = "/jobjar-submit-dir";

    private String basicExpectedJobJar = (jobjarSubmitDir) + "/job.jar";

    @Test
    public void testPathsWithNoFragNoSchemeRelative() throws IOException {
        TestJobResourceUploader.ResourceConf.Builder b = new TestJobResourceUploader.ResourceConf.Builder();
        b.setNumOfTmpFiles(5);
        b.setNumOfTmpLibJars(2);
        b.setNumOfTmpArchives(2);
        b.setJobJar(true);
        b.setPathsWithScheme(false);
        b.setPathsWithFrags(false);
        TestJobResourceUploader.ResourceConf rConf = b.build();
        JobConf jConf = new JobConf();
        JobResourceUploader uploader = new TestJobResourceUploader.StubedUploader(jConf);
        runTmpResourcePathTest(uploader, rConf, jConf, expectedFilesNoFrags, expectedArchivesNoFrags, basicExpectedJobJar);
    }

    @Test
    public void testPathsWithNoFragNoSchemeAbsolute() throws IOException {
        TestJobResourceUploader.ResourceConf.Builder b = new TestJobResourceUploader.ResourceConf.Builder();
        b.setNumOfTmpFiles(5);
        b.setNumOfTmpLibJars(2);
        b.setNumOfTmpArchives(2);
        b.setJobJar(true);
        b.setPathsWithFrags(false);
        b.setPathsWithScheme(false);
        b.setAbsolutePaths(true);
        TestJobResourceUploader.ResourceConf rConf = b.build();
        JobConf jConf = new JobConf();
        JobResourceUploader uploader = new TestJobResourceUploader.StubedUploader(jConf);
        runTmpResourcePathTest(uploader, rConf, jConf, expectedFilesNoFrags, expectedArchivesNoFrags, basicExpectedJobJar);
    }

    @Test
    public void testPathsWithFragNoSchemeAbsolute() throws IOException {
        TestJobResourceUploader.ResourceConf.Builder b = new TestJobResourceUploader.ResourceConf.Builder();
        b.setNumOfTmpFiles(5);
        b.setNumOfTmpLibJars(2);
        b.setNumOfTmpArchives(2);
        b.setJobJar(true);
        b.setPathsWithFrags(true);
        b.setPathsWithScheme(false);
        b.setAbsolutePaths(true);
        TestJobResourceUploader.ResourceConf rConf = b.build();
        JobConf jConf = new JobConf();
        JobResourceUploader uploader = new TestJobResourceUploader.StubedUploader(jConf);
        runTmpResourcePathTest(uploader, rConf, jConf, expectedFilesWithFrags, expectedArchivesWithFrags, basicExpectedJobJar);
    }

    @Test
    public void testPathsWithFragNoSchemeRelative() throws IOException {
        TestJobResourceUploader.ResourceConf.Builder b = new TestJobResourceUploader.ResourceConf.Builder();
        b.setNumOfTmpFiles(5);
        b.setNumOfTmpLibJars(2);
        b.setNumOfTmpArchives(2);
        b.setJobJar(true);
        b.setPathsWithFrags(true);
        b.setAbsolutePaths(false);
        b.setPathsWithScheme(false);
        TestJobResourceUploader.ResourceConf rConf = b.build();
        JobConf jConf = new JobConf();
        JobResourceUploader uploader = new TestJobResourceUploader.StubedUploader(jConf);
        runTmpResourcePathTest(uploader, rConf, jConf, expectedFilesWithFrags, expectedArchivesWithFrags, basicExpectedJobJar);
    }

    @Test
    public void testPathsWithFragSchemeAbsolute() throws IOException {
        TestJobResourceUploader.ResourceConf.Builder b = new TestJobResourceUploader.ResourceConf.Builder();
        b.setNumOfTmpFiles(5);
        b.setNumOfTmpLibJars(2);
        b.setNumOfTmpArchives(2);
        b.setJobJar(true);
        b.setPathsWithFrags(true);
        b.setAbsolutePaths(true);
        b.setPathsWithScheme(true);
        TestJobResourceUploader.ResourceConf rConf = b.build();
        JobConf jConf = new JobConf();
        JobResourceUploader uploader = new TestJobResourceUploader.StubedUploader(jConf);
        runTmpResourcePathTest(uploader, rConf, jConf, expectedFilesWithFrags, expectedArchivesWithFrags, basicExpectedJobJar);
    }

    @Test
    public void testPathsWithNoFragWithSchemeAbsolute() throws IOException {
        TestJobResourceUploader.ResourceConf.Builder b = new TestJobResourceUploader.ResourceConf.Builder();
        b.setNumOfTmpFiles(5);
        b.setNumOfTmpLibJars(2);
        b.setNumOfTmpArchives(2);
        b.setJobJar(true);
        b.setPathsWithFrags(false);
        b.setPathsWithScheme(true);
        b.setAbsolutePaths(true);
        TestJobResourceUploader.ResourceConf rConf = b.build();
        JobConf jConf = new JobConf();
        JobResourceUploader uploader = new TestJobResourceUploader.StubedUploader(jConf);
        runTmpResourcePathTest(uploader, rConf, jConf, expectedFilesNoFrags, expectedArchivesNoFrags, basicExpectedJobJar);
    }

    @Test
    public void testPathsWithNoFragAndWildCard() throws IOException {
        TestJobResourceUploader.ResourceConf.Builder b = new TestJobResourceUploader.ResourceConf.Builder();
        b.setNumOfTmpFiles(5);
        b.setNumOfTmpLibJars(4);
        b.setNumOfTmpArchives(2);
        b.setJobJar(true);
        b.setPathsWithFrags(false);
        b.setPathsWithScheme(true);
        b.setAbsolutePaths(true);
        TestJobResourceUploader.ResourceConf rConf = b.build();
        JobConf jConf = new JobConf();
        JobResourceUploader uploader = new TestJobResourceUploader.StubedUploader(jConf, true);
        runTmpResourcePathTest(uploader, rConf, jConf, expectedFilesWithWildcard, expectedArchivesNoFrags, basicExpectedJobJar);
    }

    @Test
    public void testPathsWithFragsAndWildCard() throws IOException {
        TestJobResourceUploader.ResourceConf.Builder b = new TestJobResourceUploader.ResourceConf.Builder();
        b.setNumOfTmpFiles(5);
        b.setNumOfTmpLibJars(2);
        b.setNumOfTmpArchives(2);
        b.setJobJar(true);
        b.setPathsWithFrags(true);
        b.setPathsWithScheme(true);
        b.setAbsolutePaths(true);
        TestJobResourceUploader.ResourceConf rConf = b.build();
        JobConf jConf = new JobConf();
        JobResourceUploader uploader = new TestJobResourceUploader.StubedUploader(jConf, true);
        runTmpResourcePathTest(uploader, rConf, jConf, expectedFilesWithFrags, expectedArchivesWithFrags, basicExpectedJobJar);
    }

    @Test
    public void testErasureCodingDefault() throws IOException {
        testErasureCodingSetting(true);
    }

    @Test
    public void testErasureCodingDisabled() throws IOException {
        testErasureCodingSetting(false);
    }

    private enum ResourceViolation {

        NUMBER_OF_RESOURCES,
        TOTAL_RESOURCE_SIZE,
        SINGLE_RESOURCE_SIZE;}

    private final FileStatus mockedStatus = Mockito.mock(FileStatus.class);

    private static class ResourceConf {
        private final int maxResources;

        private final long maxResourcesMB;

        private final long maxSingleResourceMB;

        private final int numOfTmpFiles;

        private final int numOfTmpArchives;

        private final int numOfTmpLibJars;

        private final boolean jobJar;

        private final int numOfDCFiles;

        private final int numOfDCArchives;

        private final long sizeOfResource;

        private final boolean pathsWithFrags;

        private final boolean pathsWithScheme;

        private final boolean absolutePaths;

        private ResourceConf() {
            this(new TestJobResourceUploader.ResourceConf.Builder());
        }

        private ResourceConf(TestJobResourceUploader.ResourceConf.Builder builder) {
            this.maxResources = builder.maxResources;
            this.maxResourcesMB = builder.maxResourcesMB;
            this.maxSingleResourceMB = builder.maxSingleResourceMB;
            this.numOfTmpFiles = builder.numOfTmpFiles;
            this.numOfTmpArchives = builder.numOfTmpArchives;
            this.numOfTmpLibJars = builder.numOfTmpLibJars;
            this.jobJar = builder.jobJar;
            this.numOfDCFiles = builder.numOfDCFiles;
            this.numOfDCArchives = builder.numOfDCArchives;
            this.sizeOfResource = builder.sizeOfResource;
            this.pathsWithFrags = builder.pathsWithFrags;
            this.pathsWithScheme = builder.pathsWithScheme;
            this.absolutePaths = builder.absolutePaths;
        }

        static class Builder {
            // Defaults
            private int maxResources = 0;

            private long maxResourcesMB = 0;

            private long maxSingleResourceMB = 0;

            private int numOfTmpFiles = 0;

            private int numOfTmpArchives = 0;

            private int numOfTmpLibJars = 0;

            private boolean jobJar = false;

            private int numOfDCFiles = 0;

            private int numOfDCArchives = 0;

            private long sizeOfResource = 0;

            private boolean pathsWithFrags = false;

            private boolean pathsWithScheme = false;

            private boolean absolutePaths = true;

            private Builder() {
            }

            private TestJobResourceUploader.ResourceConf.Builder setMaxResources(int max) {
                this.maxResources = max;
                return this;
            }

            private TestJobResourceUploader.ResourceConf.Builder setMaxResourcesMB(long max) {
                this.maxResourcesMB = max;
                return this;
            }

            private TestJobResourceUploader.ResourceConf.Builder setMaxSingleResourceMB(long max) {
                this.maxSingleResourceMB = max;
                return this;
            }

            private TestJobResourceUploader.ResourceConf.Builder setNumOfTmpFiles(int num) {
                this.numOfTmpFiles = num;
                return this;
            }

            private TestJobResourceUploader.ResourceConf.Builder setNumOfTmpArchives(int num) {
                this.numOfTmpArchives = num;
                return this;
            }

            private TestJobResourceUploader.ResourceConf.Builder setNumOfTmpLibJars(int num) {
                this.numOfTmpLibJars = num;
                return this;
            }

            private TestJobResourceUploader.ResourceConf.Builder setJobJar(boolean jar) {
                this.jobJar = jar;
                return this;
            }

            private TestJobResourceUploader.ResourceConf.Builder setNumOfDCFiles(int num) {
                this.numOfDCFiles = num;
                return this;
            }

            private TestJobResourceUploader.ResourceConf.Builder setNumOfDCArchives(int num) {
                this.numOfDCArchives = num;
                return this;
            }

            private TestJobResourceUploader.ResourceConf.Builder setSizeOfResource(long sizeMB) {
                this.sizeOfResource = sizeMB;
                return this;
            }

            private TestJobResourceUploader.ResourceConf.Builder setPathsWithFrags(boolean fragments) {
                this.pathsWithFrags = fragments;
                return this;
            }

            private TestJobResourceUploader.ResourceConf.Builder setPathsWithScheme(boolean scheme) {
                this.pathsWithScheme = scheme;
                return this;
            }

            private TestJobResourceUploader.ResourceConf.Builder setAbsolutePaths(boolean absolute) {
                this.absolutePaths = absolute;
                return this;
            }

            TestJobResourceUploader.ResourceConf build() {
                return new TestJobResourceUploader.ResourceConf(this);
            }
        }

        private Job setupJobConf(JobConf conf) throws IOException {
            conf.set("tmpfiles", buildPathString("tmpFiles", this.numOfTmpFiles, ".txt"));
            conf.set("tmpjars", buildPathString("tmpjars", this.numOfTmpLibJars, ".jar"));
            conf.set("tmparchives", buildPathString("tmpArchives", this.numOfTmpArchives, ".tgz"));
            conf.set(CACHE_ARCHIVES, buildDistributedCachePathString("cacheArchives", this.numOfDCArchives, ".tgz"));
            conf.set(CACHE_FILES, buildDistributedCachePathString("cacheFiles", this.numOfDCFiles, ".txt"));
            if (this.jobJar) {
                String fragment = "";
                if (pathsWithFrags) {
                    fragment = "#jobjarfrag.jar";
                }
                if (pathsWithScheme) {
                    conf.setJar(("file:///jobjar.jar" + fragment));
                } else {
                    if (absolutePaths) {
                        conf.setJar(("/jobjar.jar" + fragment));
                    } else {
                        conf.setJar(("jobjar.jar" + fragment));
                    }
                }
            }
            conf.setInt(MAX_RESOURCES, this.maxResources);
            conf.setLong(MAX_RESOURCES_MB, this.maxResourcesMB);
            conf.setLong(MAX_SINGLE_RESOURCE_MB, this.maxSingleResourceMB);
            return new Job(conf);
        }

        // We always want absolute paths with a scheme in the DistributedCache, so
        // we use a separate method to construct the path string.
        private String buildDistributedCachePathString(String pathPrefix, int numOfPaths, String extension) {
            if (numOfPaths < 1) {
                return "";
            } else {
                StringBuilder b = new StringBuilder();
                b.append(buildPathStringSub(pathPrefix, ("file:///" + pathPrefix), extension, 0));
                for (int i = 1; i < numOfPaths; i++) {
                    b.append(("," + (buildPathStringSub(pathPrefix, ("file:///" + pathPrefix), extension, i))));
                }
                return b.toString();
            }
        }

        private String buildPathString(String pathPrefix, int numOfPaths, String extension) {
            if (numOfPaths < 1) {
                return "";
            } else {
                StringBuilder b = new StringBuilder();
                String processedPath;
                if (pathsWithScheme) {
                    processedPath = "file:///" + pathPrefix;
                } else {
                    if (absolutePaths) {
                        processedPath = "/" + pathPrefix;
                    } else {
                        processedPath = pathPrefix;
                    }
                }
                b.append(buildPathStringSub(pathPrefix, processedPath, extension, 0));
                for (int i = 1; i < numOfPaths; i++) {
                    b.append(("," + (buildPathStringSub(pathPrefix, processedPath, extension, i))));
                }
                return b.toString();
            }
        }

        private String buildPathStringSub(String pathPrefix, String processedPath, String extension, int num) {
            if (pathsWithFrags) {
                return ((((((processedPath + num) + extension) + "#") + pathPrefix) + "fragment") + num) + extension;
            } else {
                return (processedPath + num) + extension;
            }
        }
    }

    private class StubedUploader extends JobResourceUploader {
        StubedUploader(JobConf conf) throws IOException {
            this(conf, false);
        }

        StubedUploader(JobConf conf, boolean useWildcard) throws IOException {
            super(FileSystem.getLocal(conf), useWildcard);
        }

        StubedUploader(FileSystem fs, boolean useWildcard) throws IOException {
            super(fs, useWildcard);
        }

        @Override
        FileStatus getFileStatus(Map<URI, FileStatus> statCache, Configuration job, Path p) throws IOException {
            return mockedStatus;
        }

        @Override
        boolean mkdirs(FileSystem fs, Path dir, FsPermission permission) throws IOException {
            // Do nothing. Stubbed out to avoid side effects. We don't actually need
            // to create submit dirs.
            return true;
        }

        @Override
        Path copyRemoteFiles(Path parentDir, Path originalPath, Configuration conf, short replication) throws IOException {
            return new Path(((destinationPathPrefix) + (originalPath.getName())));
        }

        @Override
        void copyJar(Path originalJarPath, Path submitJarFile, short replication) throws IOException {
            // Do nothing. Stubbed out to avoid side effects. We don't actually need
            // to copy the jar to the remote fs.
        }
    }
}

