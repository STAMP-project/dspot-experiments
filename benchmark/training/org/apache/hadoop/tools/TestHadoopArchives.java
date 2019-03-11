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
package org.apache.hadoop.tools;


import HadoopArchives.TEST_HADOOP_ARCHIVES_JAR_PATH;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FsShell;
import org.apache.hadoop.fs.HarFileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.security.Groups;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.JarFinder;
import org.apache.hadoop.util.ToolRunner;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;


/**
 * test {@link HadoopArchives}
 */
public class TestHadoopArchives {
    public static final String HADOOP_ARCHIVES_JAR = JarFinder.getJar(HadoopArchives.class);

    {
        GenericTestUtils.setLogLevel(LoggerFactory.getLogger(Groups.class), Level.ERROR);
    }

    private static final String inputDir = "input";

    private Path inputPath;

    private Path archivePath;

    private final List<String> fileList = new ArrayList<String>();

    private MiniDFSCluster dfscluster;

    private Configuration conf;

    private FileSystem fs;

    @Test
    public void testRelativePath() throws Exception {
        final Path sub1 = new Path(inputPath, "dir1");
        fs.mkdirs(sub1);
        TestHadoopArchives.createFile(inputPath, fs, sub1.getName(), "a");
        final FsShell shell = new FsShell(conf);
        final List<String> originalPaths = TestHadoopArchives.lsr(shell, "input");
        System.out.println(("originalPaths: " + originalPaths));
        // make the archive:
        final String fullHarPathStr = makeArchive();
        // compare results:
        final List<String> harPaths = TestHadoopArchives.lsr(shell, fullHarPathStr);
        Assert.assertEquals(originalPaths, harPaths);
    }

    @Test
    public void testRelativePathWitRepl() throws Exception {
        final Path sub1 = new Path(inputPath, "dir1");
        fs.mkdirs(sub1);
        TestHadoopArchives.createFile(inputPath, fs, sub1.getName(), "a");
        final FsShell shell = new FsShell(conf);
        final List<String> originalPaths = TestHadoopArchives.lsr(shell, "input");
        System.out.println(("originalPaths: " + originalPaths));
        // make the archive:
        final String fullHarPathStr = makeArchiveWithRepl();
        // compare results:
        final List<String> harPaths = TestHadoopArchives.lsr(shell, fullHarPathStr);
        Assert.assertEquals(originalPaths, harPaths);
    }

    @Test
    public void testOutputPathValidity() throws Exception {
        final String inputPathStr = inputPath.toUri().getPath();
        final URI uri = fs.getUri();
        final String harName = "foo.har";
        System.setProperty(TEST_HADOOP_ARCHIVES_JAR_PATH, TestHadoopArchives.HADOOP_ARCHIVES_JAR);
        final HadoopArchives har = new HadoopArchives(conf);
        PrintStream stderr = System.err;
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        PrintStream newErr = new PrintStream(byteStream);
        System.setErr(newErr);
        // fail if the archive path already exists
        TestHadoopArchives.createFile(archivePath, fs, harName);
        final String[] args = new String[]{ "-archiveName", harName, "-p", inputPathStr, "*", archivePath.toString() };
        Assert.assertEquals((-1), ToolRunner.run(har, args));
        String output = byteStream.toString();
        final Path outputPath = new Path(archivePath, harName);
        Assert.assertTrue(((output.indexOf((("Archive path: " + (outputPath.toString())) + " already exists"))) != (-1)));
        byteStream.reset();
        // fail if the destination directory is a file
        TestHadoopArchives.createFile(archivePath, fs, "sub1");
        final Path archivePath2 = new Path(archivePath, "sub1");
        final String[] args2 = new String[]{ "-archiveName", harName, "-p", inputPathStr, "*", archivePath2.toString() };
        Assert.assertEquals((-1), ToolRunner.run(har, args2));
        output = byteStream.toString();
        Assert.assertTrue(((output.indexOf((("Destination " + (archivePath2.toString())) + " should be a directory but is a file"))) != (-1)));
        System.setErr(stderr);
    }

    @Test
    public void testPathWithSpaces() throws Exception {
        // create files/directories with spaces
        TestHadoopArchives.createFile(inputPath, fs, "c c");
        final Path sub1 = new Path(inputPath, "sub 1");
        fs.mkdirs(sub1);
        TestHadoopArchives.createFile(sub1, fs, "file x y z");
        TestHadoopArchives.createFile(sub1, fs, "file");
        TestHadoopArchives.createFile(sub1, fs, "x");
        TestHadoopArchives.createFile(sub1, fs, "y");
        TestHadoopArchives.createFile(sub1, fs, "z");
        final Path sub2 = new Path(inputPath, "sub 1 with suffix");
        fs.mkdirs(sub2);
        TestHadoopArchives.createFile(sub2, fs, "z");
        final FsShell shell = new FsShell(conf);
        final String inputPathStr = inputPath.toUri().getPath();
        final List<String> originalPaths = TestHadoopArchives.lsr(shell, inputPathStr);
        // make the archive:
        final String fullHarPathStr = makeArchive();
        // compare results
        final List<String> harPaths = TestHadoopArchives.lsr(shell, fullHarPathStr);
        Assert.assertEquals(originalPaths, harPaths);
    }

    @Test
    public void testSingleFile() throws Exception {
        final Path sub1 = new Path(inputPath, "dir1");
        fs.mkdirs(sub1);
        String singleFileName = "a";
        TestHadoopArchives.createFile(inputPath, fs, sub1.getName(), singleFileName);
        final FsShell shell = new FsShell(conf);
        final List<String> originalPaths = TestHadoopArchives.lsr(shell, sub1.toString());
        System.out.println(("originalPaths: " + originalPaths));
        // make the archive:
        final String fullHarPathStr = makeArchive(sub1, singleFileName);
        // compare results:
        final List<String> harPaths = TestHadoopArchives.lsr(shell, fullHarPathStr);
        Assert.assertEquals(originalPaths, harPaths);
    }

    @Test
    public void testGlobFiles() throws Exception {
        final Path sub1 = new Path(inputPath, "dir1");
        final Path sub2 = new Path(inputPath, "dir2");
        fs.mkdirs(sub1);
        String fileName = "a";
        TestHadoopArchives.createFile(inputPath, fs, sub1.getName(), fileName);
        TestHadoopArchives.createFile(inputPath, fs, sub2.getName(), fileName);
        TestHadoopArchives.createFile(inputPath, fs, sub1.getName(), "b");// not part of result

        final String glob = "dir{1,2}/a";
        final FsShell shell = new FsShell(conf);
        final List<String> originalPaths = TestHadoopArchives.lsr(shell, inputPath.toString(), (((inputPath) + "/") + glob));
        System.out.println(("originalPaths: " + originalPaths));
        // make the archive:
        final String fullHarPathStr = makeArchive(inputPath, glob);
        // compare results:
        final List<String> harPaths = TestHadoopArchives.lsr(shell, fullHarPathStr, ((fullHarPathStr + "/") + glob));
        Assert.assertEquals(originalPaths, harPaths);
    }

    @Test
    public void testReadFileContent() throws Exception {
        fileList.add(TestHadoopArchives.createFile(inputPath, fs, "c c"));
        final Path sub1 = new Path(inputPath, "sub 1");
        fs.mkdirs(sub1);
        fileList.add(TestHadoopArchives.createFile(inputPath, fs, sub1.getName(), "file x y z"));
        fileList.add(TestHadoopArchives.createFile(inputPath, fs, sub1.getName(), "file"));
        fileList.add(TestHadoopArchives.createFile(inputPath, fs, sub1.getName(), "x"));
        fileList.add(TestHadoopArchives.createFile(inputPath, fs, sub1.getName(), "y"));
        fileList.add(TestHadoopArchives.createFile(inputPath, fs, sub1.getName(), "z"));
        final Path sub2 = new Path(inputPath, "sub 1 with suffix");
        fs.mkdirs(sub2);
        fileList.add(TestHadoopArchives.createFile(inputPath, fs, sub2.getName(), "z"));
        // Generate a big binary file content:
        final byte[] binContent = TestHadoopArchives.prepareBin();
        fileList.add(TestHadoopArchives.createFile(inputPath, fs, binContent, sub2.getName(), "bin"));
        fileList.add(TestHadoopArchives.createFile(inputPath, fs, new byte[0], sub2.getName(), "zero-length"));
        final String fullHarPathStr = makeArchive();
        // Create fresh HarFs:
        final HarFileSystem harFileSystem = new HarFileSystem(fs);
        try {
            final URI harUri = new URI(fullHarPathStr);
            harFileSystem.initialize(harUri, fs.getConf());
            // now read the file content and compare it against the expected:
            int readFileCount = 0;
            for (final String pathStr0 : fileList) {
                final Path path = new Path(((fullHarPathStr + (Path.SEPARATOR)) + pathStr0));
                final String baseName = path.getName();
                final FileStatus status = harFileSystem.getFileStatus(path);
                if (status.isFile()) {
                    // read the file:
                    final byte[] actualContentSimple = TestHadoopArchives.readAllSimple(harFileSystem.open(path), true);
                    final byte[] actualContentBuffer = TestHadoopArchives.readAllWithBuffer(harFileSystem.open(path), true);
                    Assert.assertArrayEquals(actualContentSimple, actualContentBuffer);
                    final byte[] actualContentFully = TestHadoopArchives.readAllWithReadFully(actualContentSimple.length, harFileSystem.open(path), true);
                    Assert.assertArrayEquals(actualContentSimple, actualContentFully);
                    final byte[] actualContentSeek = TestHadoopArchives.readAllWithSeek(actualContentSimple.length, harFileSystem.open(path), true);
                    Assert.assertArrayEquals(actualContentSimple, actualContentSeek);
                    final byte[] actualContentRead4 = TestHadoopArchives.readAllWithRead4(harFileSystem.open(path), true);
                    Assert.assertArrayEquals(actualContentSimple, actualContentRead4);
                    final byte[] actualContentSkip = TestHadoopArchives.readAllWithSkip(actualContentSimple.length, harFileSystem.open(path), harFileSystem.open(path), true);
                    Assert.assertArrayEquals(actualContentSimple, actualContentSkip);
                    if ("bin".equals(baseName)) {
                        Assert.assertArrayEquals(binContent, actualContentSimple);
                    } else
                        if ("zero-length".equals(baseName)) {
                            Assert.assertEquals(0, actualContentSimple.length);
                        } else {
                            String actual = new String(actualContentSimple, "UTF-8");
                            Assert.assertEquals(baseName, actual);
                        }

                    readFileCount++;
                }
            }
            Assert.assertEquals(fileList.size(), readFileCount);
        } finally {
            harFileSystem.close();
        }
    }

    /* Tests copying from archive file system to a local file system */
    @Test
    public void testCopyToLocal() throws Exception {
        final String fullHarPathStr = makeArchive();
        // make path to copy the file to:
        final String tmpDir = (System.getProperty("test.build.data", "build/test/data")) + "/work-dir/har-fs-tmp";
        final Path tmpPath = new Path(tmpDir);
        final LocalFileSystem localFs = FileSystem.getLocal(new Configuration());
        localFs.delete(tmpPath, true);
        localFs.mkdirs(tmpPath);
        Assert.assertTrue(localFs.exists(tmpPath));
        // Create fresh HarFs:
        final HarFileSystem harFileSystem = new HarFileSystem(fs);
        try {
            final URI harUri = new URI(fullHarPathStr);
            harFileSystem.initialize(harUri, fs.getConf());
            final Path sourcePath = new Path(((fullHarPathStr + (Path.SEPARATOR)) + "a"));
            final Path targetPath = new Path(tmpPath, "straus");
            // copy the Har file to a local file system:
            harFileSystem.copyToLocalFile(false, sourcePath, targetPath);
            FileStatus straus = localFs.getFileStatus(targetPath);
            // the file should contain just 1 character:
            Assert.assertEquals(1, straus.getLen());
        } finally {
            harFileSystem.close();
            localFs.delete(tmpPath, true);
        }
    }
}

