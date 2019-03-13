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
package org.apache.hadoop.fs;


import HarFileSystem.VERSION;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.Shell;
import org.junit.Assert;
import org.junit.Test;


/**
 * This test class checks basic operations with {@link HarFileSystem} including
 * various initialization cases, getters, and modification methods.
 *
 * NB: to run this test from an IDE make sure the folder
 * "hadoop-common-project/hadoop-common/src/main/resources/" is added as a
 * source path. This will allow the system to pick up the "core-default.xml" and
 * "META-INF/services/..." resources from the class-path in the runtime.
 */
public class TestHarFileSystemBasics {
    private static final String ROOT_PATH = GenericTestUtils.getTempPath("testharfilesystembasics");

    private static final Path rootPath;

    static {
        String root = new Path(new File(TestHarFileSystemBasics.ROOT_PATH).getAbsolutePath(), "localfs").toUri().getPath();
        // Strip drive specifier on Windows, which would make the HAR URI invalid and
        // cause tests to fail.
        if (Shell.WINDOWS) {
            root = root.substring(((root.indexOf(':')) + 1));
        }
        rootPath = new Path(root);
    }

    // NB: .har suffix is necessary
    private static final Path harPath = new Path(TestHarFileSystemBasics.rootPath, "path1/path2/my.har");

    private FileSystem localFileSystem;

    private HarFileSystem harFileSystem;

    private Configuration conf;

    // ======== Positive tests:
    @Test
    public void testPositiveHarFileSystemBasics() throws Exception {
        // check Har version:
        Assert.assertEquals(VERSION, harFileSystem.getHarVersion());
        // check Har URI:
        final URI harUri = harFileSystem.getUri();
        Assert.assertEquals(TestHarFileSystemBasics.harPath.toUri().getPath(), harUri.getPath());
        Assert.assertEquals("har", harUri.getScheme());
        // check Har home path:
        final Path homePath = harFileSystem.getHomeDirectory();
        Assert.assertEquals(TestHarFileSystemBasics.harPath.toUri().getPath(), homePath.toUri().getPath());
        // check working directory:
        final Path workDirPath0 = harFileSystem.getWorkingDirectory();
        Assert.assertEquals(homePath, workDirPath0);
        // check that its impossible to reset the working directory
        // (#setWorkingDirectory should have no effect):
        harFileSystem.setWorkingDirectory(new Path("/foo/bar"));
        Assert.assertEquals(workDirPath0, harFileSystem.getWorkingDirectory());
    }

    @Test
    public void testPositiveNewHarFsOnTheSameUnderlyingFs() throws Exception {
        // Init 2nd har file system on the same underlying FS, so the
        // metadata gets reused:
        final HarFileSystem hfs = new HarFileSystem(localFileSystem);
        final URI uri = new URI(("har://" + (TestHarFileSystemBasics.harPath.toString())));
        hfs.initialize(uri, new Configuration());
        // the metadata should be reused from cache:
        Assert.assertTrue(((hfs.getMetadata()) == (harFileSystem.getMetadata())));
    }

    @Test
    public void testPositiveLruMetadataCacheFs() throws Exception {
        // Init 2nd har file system on the same underlying FS, so the
        // metadata gets reused:
        HarFileSystem hfs = new HarFileSystem(localFileSystem);
        URI uri = new URI(("har://" + (TestHarFileSystemBasics.harPath.toString())));
        hfs.initialize(uri, new Configuration());
        // the metadata should be reused from cache:
        Assert.assertTrue(((hfs.getMetadata()) == (harFileSystem.getMetadata())));
        // Create more hars, until the cache is full + 1; the last creation should evict the first entry from the cache
        for (int i = 0; i <= (hfs.METADATA_CACHE_ENTRIES_DEFAULT); i++) {
            Path p = new Path(TestHarFileSystemBasics.rootPath, (("path1/path2/my" + i) + ".har"));
            createHarFileSystem(conf, p);
        }
        // The first entry should not be in the cache anymore:
        hfs = new HarFileSystem(localFileSystem);
        uri = new URI(("har://" + (TestHarFileSystemBasics.harPath.toString())));
        hfs.initialize(uri, new Configuration());
        Assert.assertTrue(((hfs.getMetadata()) != (harFileSystem.getMetadata())));
    }

    @Test
    public void testPositiveInitWithoutUnderlyingFS() throws Exception {
        // Init HarFS with no constructor arg, so that the underlying FS object
        // is created on demand or got from cache in #initialize() method.
        final HarFileSystem hfs = new HarFileSystem();
        final URI uri = new URI(("har://" + (TestHarFileSystemBasics.harPath.toString())));
        hfs.initialize(uri, new Configuration());
    }

    @Test
    public void testPositiveListFilesNotEndInColon() throws Exception {
        // re-initialize the har file system with host name
        // make sure the qualified path name does not append ":" at the end of host name
        final URI uri = new URI(("har://file-localhost" + (TestHarFileSystemBasics.harPath.toString())));
        harFileSystem.initialize(uri, conf);
        Path p1 = new Path(("har://file-localhost" + (TestHarFileSystemBasics.harPath.toString())));
        Path p2 = harFileSystem.makeQualified(p1);
        Assert.assertTrue(p2.toUri().toString().startsWith("har://file-localhost/"));
    }

    @Test
    public void testListLocatedStatus() throws Exception {
        String testHarPath = this.getClass().getResource("/test.har").getPath();
        URI uri = new URI(("har://" + testHarPath));
        HarFileSystem hfs = new HarFileSystem(localFileSystem);
        hfs.initialize(uri, new Configuration());
        // test.har has the following contents:
        // dir1/1.txt
        // dir1/2.txt
        Set<String> expectedFileNames = new HashSet<String>();
        expectedFileNames.add("1.txt");
        expectedFileNames.add("2.txt");
        // List contents of dir, and ensure we find all expected files
        Path path = new Path("dir1");
        RemoteIterator<LocatedFileStatus> fileList = hfs.listLocatedStatus(path);
        while (fileList.hasNext()) {
            String fileName = fileList.next().getPath().getName();
            Assert.assertTrue((fileName + " not in expected files list"), expectedFileNames.contains(fileName));
            expectedFileNames.remove(fileName);
        } 
        Assert.assertEquals(("Didn't find all of the expected file names: " + expectedFileNames), 0, expectedFileNames.size());
    }

    @Test
    public void testMakeQualifiedPath() throws Exception {
        // Construct a valid har file system path with authority that
        // contains userinfo and port. The userinfo and port are useless
        // in local fs uri. They are only used to verify har file system
        // can correctly preserve the information for the underlying file system.
        String harPathWithUserinfo = "har://file-user:passwd@localhost:80" + (TestHarFileSystemBasics.harPath.toUri().getPath().toString());
        Path path = new Path(harPathWithUserinfo);
        Path qualifiedPath = path.getFileSystem(conf).makeQualified(path);
        Assert.assertTrue(String.format("The qualified path (%s) did not match the expected path (%s).", qualifiedPath.toString(), harPathWithUserinfo), qualifiedPath.toString().equals(harPathWithUserinfo));
    }

    // ========== Negative:
    @Test
    public void testNegativeInitWithoutIndex() throws Exception {
        // delete the index file:
        final Path indexPath = new Path(TestHarFileSystemBasics.harPath, "_index");
        localFileSystem.delete(indexPath, false);
        // now init the HarFs:
        final HarFileSystem hfs = new HarFileSystem(localFileSystem);
        final URI uri = new URI(("har://" + (TestHarFileSystemBasics.harPath.toString())));
        try {
            hfs.initialize(uri, new Configuration());
            Assert.fail("Exception expected.");
        } catch (IOException ioe) {
            // ok, expected.
        }
    }

    @Test
    public void testNegativeGetHarVersionOnNotInitializedFS() throws Exception {
        final HarFileSystem hfs = new HarFileSystem(localFileSystem);
        try {
            int version = hfs.getHarVersion();
            Assert.fail((("Exception expected, but got a Har version " + version) + "."));
        } catch (IOException ioe) {
            // ok, expected.
        }
    }

    @Test
    public void testNegativeInitWithAnUnsupportedVersion() throws Exception {
        // NB: should wait at least 1 second to ensure the timestamp of the master
        // index will change upon the writing, because Linux seems to update the
        // file modification
        // time with 1 second accuracy:
        Thread.sleep(1000);
        // write an unsupported version:
        writeVersionToMasterIndexImpl(7777, new Path(TestHarFileSystemBasics.harPath, "_masterindex"));
        // init the Har:
        final HarFileSystem hfs = new HarFileSystem(localFileSystem);
        // the metadata should *not* be reused from cache:
        Assert.assertFalse(((hfs.getMetadata()) == (harFileSystem.getMetadata())));
        final URI uri = new URI(("har://" + (TestHarFileSystemBasics.harPath.toString())));
        try {
            hfs.initialize(uri, new Configuration());
            Assert.fail("IOException expected.");
        } catch (IOException ioe) {
            // ok, expected.
        }
    }

    @Test
    public void testNegativeHarFsModifications() throws Exception {
        // all the modification methods of HarFS must lead to IOE.
        final Path fooPath = new Path(TestHarFileSystemBasics.rootPath, "foo/bar");
        localFileSystem.createNewFile(fooPath);
        try {
            harFileSystem.create(fooPath, new FsPermission("+rwx"), true, 1024, ((short) (88)), 1024, null);
            Assert.fail("IOException expected.");
        } catch (IOException ioe) {
            // ok, expected.
        }
        try {
            harFileSystem.setReplication(fooPath, ((short) (55)));
            Assert.fail("IOException expected.");
        } catch (IOException ioe) {
            // ok, expected.
        }
        try {
            harFileSystem.delete(fooPath, true);
            Assert.fail("IOException expected.");
        } catch (IOException ioe) {
            // ok, expected.
        }
        try {
            harFileSystem.mkdirs(fooPath, new FsPermission("+rwx"));
            Assert.fail("IOException expected.");
        } catch (IOException ioe) {
            // ok, expected.
        }
        final Path indexPath = new Path(TestHarFileSystemBasics.harPath, "_index");
        try {
            harFileSystem.copyFromLocalFile(false, indexPath, fooPath);
            Assert.fail("IOException expected.");
        } catch (IOException ioe) {
            // ok, expected.
        }
        try {
            harFileSystem.startLocalOutput(fooPath, indexPath);
            Assert.fail("IOException expected.");
        } catch (IOException ioe) {
            // ok, expected.
        }
        try {
            harFileSystem.completeLocalOutput(fooPath, indexPath);
            Assert.fail("IOException expected.");
        } catch (IOException ioe) {
            // ok, expected.
        }
        try {
            harFileSystem.setOwner(fooPath, "user", "group");
            Assert.fail("IOException expected.");
        } catch (IOException ioe) {
            // ok, expected.
        }
        try {
            harFileSystem.setPermission(fooPath, new FsPermission("+x"));
            Assert.fail("IOException expected.");
        } catch (IOException ioe) {
            // ok, expected.
        }
    }

    @Test
    public void testHarFsWithoutAuthority() throws Exception {
        final URI uri = harFileSystem.getUri();
        Assert.assertNull(("har uri authority not null: " + uri), uri.getAuthority());
        FileContext.getFileContext(uri, conf);
    }
}

