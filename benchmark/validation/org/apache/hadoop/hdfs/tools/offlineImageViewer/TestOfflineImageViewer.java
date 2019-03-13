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
package org.apache.hadoop.hdfs.tools.offlineImageViewer;


import BlockType.STRIPED;
import ErasureCodeConstants.RS_CODEC_NAME;
import ErasureCodingPolicyState.ENABLED;
import INodeFile.DEFAULT_REPL_FOR_STRIPED_BLOCKS;
import OfflineImageReconstructor.LOG;
import PBImageCorruptionDetector.OutputEntryBuilder;
import PBImageXmlWriter.INODE_SECTION_BLOCK_TYPE;
import PBImageXmlWriter.INODE_SECTION_EC_POLICY_ID;
import PBImageXmlWriter.INODE_SECTION_INODE;
import PBImageXmlWriter.INODE_SECTION_XATTRS;
import PBImageXmlWriter.SECTION_NAME;
import PBImageXmlWriter.SECTION_REPLICATION;
import SafeModeAction.SAFEMODE_ENTER;
import SystemErasureCodingPolicies.XOR_2_1_POLICY_ID;
import com.google.common.collect.Maps;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeysPublic;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileSystemTestHelper;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.SystemErasureCodingPolicies;
import org.apache.hadoop.hdfs.server.namenode.FSImageTestUtil;
import org.apache.hadoop.hdfs.server.namenode.NameNodeLayoutVersion;
import org.apache.hadoop.hdfs.web.WebHdfsFileSystem;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.LambdaTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class TestOfflineImageViewer {
    private static final Logger LOG = LoggerFactory.getLogger(OfflineImageViewerPB.class);

    private static final int NUM_DIRS = 3;

    private static final int FILES_PER_DIR = 4;

    private static final String TEST_RENEWER = "JobTracker";

    private static File originalFsimage = null;

    private static int filesECCount = 0;

    private static String addedErasureCodingPolicyName = null;

    private static final long FILE_NODE_ID_1 = 16388;

    private static final long FILE_NODE_ID_2 = 16389;

    private static final long FILE_NODE_ID_3 = 16394;

    private static final long DIR_NODE_ID = 16391;

    // namespace as written to dfs, to be compared with viewer's output
    static final HashMap<String, FileStatus> writtenFiles = Maps.newHashMap();

    static int dirCount = 0;

    private static File tempDir;

    @Test(expected = IOException.class)
    public void testTruncatedFSImage() throws IOException {
        File truncatedFile = new File(TestOfflineImageViewer.tempDir, "truncatedFsImage");
        PrintStream output = new PrintStream(NullOutputStream.NULL_OUTPUT_STREAM);
        copyPartOfFile(TestOfflineImageViewer.originalFsimage, truncatedFile);
        try (RandomAccessFile r = new RandomAccessFile(truncatedFile, "r")) {
            visit(r);
        }
    }

    @Test
    public void testFileDistributionCalculator() throws IOException {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream();PrintStream o = new PrintStream(output);RandomAccessFile r = new RandomAccessFile(TestOfflineImageViewer.originalFsimage, "r")) {
            visit(r);
            o.close();
            String outputString = output.toString();
            Pattern p = Pattern.compile("totalFiles = (\\d+)\n");
            Matcher matcher = p.matcher(outputString);
            Assert.assertTrue(((matcher.find()) && ((matcher.groupCount()) == 1)));
            int totalFiles = Integer.parseInt(matcher.group(1));
            Assert.assertEquals(((((TestOfflineImageViewer.NUM_DIRS) * (TestOfflineImageViewer.FILES_PER_DIR)) + (TestOfflineImageViewer.filesECCount)) + 1), totalFiles);
            p = Pattern.compile("totalDirectories = (\\d+)\n");
            matcher = p.matcher(outputString);
            Assert.assertTrue(((matcher.find()) && ((matcher.groupCount()) == 1)));
            int totalDirs = Integer.parseInt(matcher.group(1));
            // totalDirs includes root directory
            Assert.assertEquals(((TestOfflineImageViewer.dirCount) + 1), totalDirs);
            FileStatus maxFile = Collections.max(TestOfflineImageViewer.writtenFiles.values(), new Comparator<FileStatus>() {
                @Override
                public int compare(FileStatus first, FileStatus second) {
                    return (first.getLen()) < (second.getLen()) ? -1 : (first.getLen()) == (second.getLen()) ? 0 : 1;
                }
            });
            p = Pattern.compile("maxFileSize = (\\d+)\n");
            matcher = p.matcher(output.toString("UTF-8"));
            Assert.assertTrue(((matcher.find()) && ((matcher.groupCount()) == 1)));
            Assert.assertEquals(maxFile.getLen(), Long.parseLong(matcher.group(1)));
        }
    }

    @Test
    public void testFileDistributionCalculatorWithOptions() throws Exception {
        int status = OfflineImageViewerPB.run(new String[]{ "-i", TestOfflineImageViewer.originalFsimage.getAbsolutePath(), "-o", "-", "-p", "FileDistribution", "-maxSize", "512", "-step", "8" });
        Assert.assertEquals(0, status);
    }

    /**
     * SAX handler to verify EC Files and their policies.
     */
    class ECXMLHandler extends DefaultHandler {
        private boolean isInode = false;

        private boolean isAttrRepl = false;

        private boolean isAttrName = false;

        private boolean isXAttrs = false;

        private boolean isAttrECPolicy = false;

        private boolean isAttrBlockType = false;

        private String currentInodeName;

        private String currentECPolicy;

        private String currentBlockType;

        private String currentRepl;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if (qName.equalsIgnoreCase(INODE_SECTION_INODE)) {
                isInode = true;
            } else
                if (((isInode) && (!(isXAttrs))) && (qName.equalsIgnoreCase(SECTION_NAME))) {
                    isAttrName = true;
                } else
                    if ((isInode) && (qName.equalsIgnoreCase(SECTION_REPLICATION))) {
                        isAttrRepl = true;
                    } else
                        if ((isInode) && (qName.equalsIgnoreCase(INODE_SECTION_EC_POLICY_ID))) {
                            isAttrECPolicy = true;
                        } else
                            if ((isInode) && (qName.equalsIgnoreCase(INODE_SECTION_BLOCK_TYPE))) {
                                isAttrBlockType = true;
                            } else
                                if ((isInode) && (qName.equalsIgnoreCase(INODE_SECTION_XATTRS))) {
                                    isXAttrs = true;
                                }





        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            if (qName.equalsIgnoreCase(INODE_SECTION_INODE)) {
                if (((currentInodeName) != null) && ((currentInodeName.length()) > 0)) {
                    if (((currentBlockType) != null) && (currentBlockType.equalsIgnoreCase(STRIPED.name()))) {
                        Assert.assertEquals((("INode '" + (currentInodeName)) + "' has unexpected EC Policy!"), Byte.parseByte(currentECPolicy), XOR_2_1_POLICY_ID);
                        Assert.assertEquals((("INode '" + (currentInodeName)) + "' has unexpected replication!"), currentRepl, Short.toString(DEFAULT_REPL_FOR_STRIPED_BLOCKS));
                    }
                }
                isInode = false;
                currentInodeName = "";
                currentECPolicy = "";
                currentRepl = "";
            } else
                if (qName.equalsIgnoreCase(INODE_SECTION_XATTRS)) {
                    isXAttrs = false;
                }

        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            String value = new String(ch, start, length);
            if (isAttrName) {
                currentInodeName = value;
                isAttrName = false;
            } else
                if (isAttrRepl) {
                    currentRepl = value;
                    isAttrRepl = false;
                } else
                    if (isAttrECPolicy) {
                        currentECPolicy = value;
                        isAttrECPolicy = false;
                    } else
                        if (isAttrBlockType) {
                            currentBlockType = value;
                            isAttrBlockType = false;
                        }



        }
    }

    @Test
    public void testPBImageXmlWriter() throws IOException, ParserConfigurationException, SAXException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream o = new PrintStream(output);
        PBImageXmlWriter v = new PBImageXmlWriter(new Configuration(), o);
        try (RandomAccessFile r = new RandomAccessFile(TestOfflineImageViewer.originalFsimage, "r")) {
            v.visit(r);
        }
        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser parser = spf.newSAXParser();
        final String xml = output.toString();
        TestOfflineImageViewer.ECXMLHandler ecxmlHandler = new TestOfflineImageViewer.ECXMLHandler();
        parser.parse(new InputSource(new StringReader(xml)), ecxmlHandler);
    }

    @Test
    public void testWebImageViewer() throws Exception {
        WebImageViewer viewer = new WebImageViewer(NetUtils.createSocketAddr("localhost:0"));
        try {
            viewer.initServer(TestOfflineImageViewer.originalFsimage.getAbsolutePath());
            int port = viewer.getPort();
            // create a WebHdfsFileSystem instance
            URI uri = new URI(("webhdfs://localhost:" + (String.valueOf(port))));
            Configuration conf = new Configuration();
            WebHdfsFileSystem webhdfs = ((WebHdfsFileSystem) (FileSystem.get(uri, conf)));
            // verify the number of directories
            FileStatus[] statuses = webhdfs.listStatus(new Path("/"));
            Assert.assertEquals(TestOfflineImageViewer.dirCount, statuses.length);
            // verify the number of files in the directory
            statuses = webhdfs.listStatus(new Path("/dir0"));
            Assert.assertEquals(TestOfflineImageViewer.FILES_PER_DIR, statuses.length);
            // compare a file
            FileStatus status = webhdfs.listStatus(new Path("/dir0/file0"))[0];
            FileStatus expected = TestOfflineImageViewer.writtenFiles.get("/dir0/file0");
            TestOfflineImageViewer.compareFile(expected, status);
            // LISTSTATUS operation to an empty directory
            statuses = webhdfs.listStatus(new Path("/emptydir"));
            Assert.assertEquals(0, statuses.length);
            // LISTSTATUS operation to a invalid path
            URL url = new URL((("http://localhost:" + port) + "/webhdfs/v1/invalid/?op=LISTSTATUS"));
            verifyHttpResponseCode(HttpURLConnection.HTTP_NOT_FOUND, url);
            // LISTSTATUS operation to a invalid prefix
            url = new URL((("http://localhost:" + port) + "/foo"));
            verifyHttpResponseCode(HttpURLConnection.HTTP_NOT_FOUND, url);
            // Verify the Erasure Coded empty file status
            Path emptyECFilePath = new Path("/ec/EmptyECFile.txt");
            FileStatus actualEmptyECFileStatus = webhdfs.getFileStatus(new Path(emptyECFilePath.toString()));
            FileStatus expectedEmptyECFileStatus = TestOfflineImageViewer.writtenFiles.get(emptyECFilePath.toString());
            System.out.println(webhdfs.getFileStatus(new Path(emptyECFilePath.toString())));
            TestOfflineImageViewer.compareFile(expectedEmptyECFileStatus, actualEmptyECFileStatus);
            // Verify the Erasure Coded small file status
            Path smallECFilePath = new Path("/ec/SmallECFile.txt");
            FileStatus actualSmallECFileStatus = webhdfs.getFileStatus(new Path(smallECFilePath.toString()));
            FileStatus expectedSmallECFileStatus = TestOfflineImageViewer.writtenFiles.get(smallECFilePath.toString());
            TestOfflineImageViewer.compareFile(expectedSmallECFileStatus, actualSmallECFileStatus);
            // GETFILESTATUS operation
            status = webhdfs.getFileStatus(new Path("/dir0/file0"));
            TestOfflineImageViewer.compareFile(expected, status);
            // GETFILESTATUS operation to a invalid path
            url = new URL((("http://localhost:" + port) + "/webhdfs/v1/invalid/?op=GETFILESTATUS"));
            verifyHttpResponseCode(HttpURLConnection.HTTP_NOT_FOUND, url);
            // invalid operation
            url = new URL((("http://localhost:" + port) + "/webhdfs/v1/?op=INVALID"));
            verifyHttpResponseCode(HttpURLConnection.HTTP_BAD_REQUEST, url);
            // invalid method
            url = new URL((("http://localhost:" + port) + "/webhdfs/v1/?op=LISTSTATUS"));
            HttpURLConnection connection = ((HttpURLConnection) (url.openConnection()));
            connection.setRequestMethod("POST");
            connection.connect();
            Assert.assertEquals(HttpURLConnection.HTTP_BAD_METHOD, connection.getResponseCode());
        } finally {
            // shutdown the viewer
            viewer.close();
        }
    }

    @Test
    public void testWebImageViewerNullOp() throws Exception {
        WebImageViewer viewer = new WebImageViewer(NetUtils.createSocketAddr("localhost:0"));
        try {
            viewer.initServer(TestOfflineImageViewer.originalFsimage.getAbsolutePath());
            int port = viewer.getPort();
            // null op
            URL url = new URL((("http://localhost:" + port) + "/webhdfs/v1/"));
            // should get HTTP_BAD_REQUEST. NPE gets HTTP_INTERNAL_ERROR
            verifyHttpResponseCode(HttpURLConnection.HTTP_BAD_REQUEST, url);
        } finally {
            // shutdown the viewer
            viewer.close();
        }
    }

    @Test
    public void testWebImageViewerSecureMode() throws Exception {
        Configuration conf = new Configuration();
        conf.set(CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTHENTICATION, "kerberos");
        try (WebImageViewer viewer = new WebImageViewer(NetUtils.createSocketAddr("localhost:0"), conf)) {
            RuntimeException ex = LambdaTestUtils.intercept(RuntimeException.class, "WebImageViewer does not support secure mode.", () -> viewer.start("foo"));
        } finally {
            conf.set(CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTHENTICATION, "simple");
            UserGroupInformation.setConfiguration(conf);
        }
    }

    @Test
    public void testPBDelimitedWriter() throws IOException, InterruptedException {
        testPBDelimitedWriter("");// Test in memory db.

        testPBDelimitedWriter(((new FileSystemTestHelper().getTestRootDir()) + "/delimited.db"));
    }

    @Test
    public void testOutputEntryBuilder() throws IOException {
        PBImageCorruptionDetector corrDetector = new PBImageCorruptionDetector(null, ",", "");
        PBImageCorruption c1 = new PBImageCorruption(342, true, false, 3);
        PBImageCorruptionDetector.OutputEntryBuilder entryBuilder1 = new PBImageCorruptionDetector.OutputEntryBuilder(corrDetector, false);
        entryBuilder1.setParentId(1).setCorruption(c1).setParentPath("/dir1/dir2/");
        Assert.assertEquals(entryBuilder1.build(), "MissingChild,342,false,/dir1/dir2/,1,,,3");
        corrDetector = new PBImageCorruptionDetector(null, "\t", "");
        PBImageCorruption c2 = new PBImageCorruption(781, false, true, 0);
        PBImageCorruptionDetector.OutputEntryBuilder entryBuilder2 = new PBImageCorruptionDetector.OutputEntryBuilder(corrDetector, true);
        entryBuilder2.setParentPath("/dir3/").setCorruption(c2).setName("folder").setNodeType("Node");
        Assert.assertEquals(entryBuilder2.build(), "CorruptNode\t781\ttrue\t/dir3/\tMissing\tfolder\tNode\t0");
    }

    @Test
    public void testPBCorruptionDetector() throws IOException, InterruptedException {
        testPBCorruptionDetector("");// Test in memory db.

        testPBCorruptionDetector(((new FileSystemTestHelper().getTestRootDir()) + "/corruption.db"));
    }

    @Test
    public void testInvalidProcessorOption() throws Exception {
        int status = OfflineImageViewerPB.run(new String[]{ "-i", TestOfflineImageViewer.originalFsimage.getAbsolutePath(), "-o", "-", "-p", "invalid" });
        Assert.assertTrue("Exit code returned for invalid processor option is incorrect", (status != 0));
    }

    @Test
    public void testOfflineImageViewerHelpMessage() throws Throwable {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final PrintStream out = new PrintStream(bytes);
        final PrintStream oldOut = System.out;
        try {
            System.setOut(out);
            int status = OfflineImageViewerPB.run(new String[]{ "-h" });
            Assert.assertTrue("Exit code returned for help option is incorrect", (status == 0));
            Assert.assertFalse("Invalid Command error displayed when help option is passed.", bytes.toString().contains("Error parsing command-line options"));
            status = OfflineImageViewerPB.run(new String[]{ "-h", "-i", TestOfflineImageViewer.originalFsimage.getAbsolutePath(), "-o", "-", "-p", "FileDistribution", "-maxSize", "512", "-step", "8" });
            Assert.assertTrue("Exit code returned for help with other option is incorrect", (status == (-1)));
        } finally {
            System.setOut(oldOut);
            IOUtils.closeStream(out);
        }
    }

    @Test(expected = IOException.class)
    public void testDelimitedWithExistingFolder() throws IOException, InterruptedException {
        File tempDelimitedDir = null;
        try {
            String tempDelimitedDirName = "tempDirDelimited";
            String tempDelimitedDirPath = ((new FileSystemTestHelper().getTestRootDir()) + "/") + tempDelimitedDirName;
            tempDelimitedDir = new File(tempDelimitedDirPath);
            Assert.assertTrue("Couldn't create temp directory!", tempDelimitedDir.mkdirs());
            testPBDelimitedWriter(tempDelimitedDirPath);
        } finally {
            if (tempDelimitedDir != null) {
                FileUtils.deleteDirectory(tempDelimitedDir);
            }
        }
    }

    @Test
    public void testCorruptionDetectionSingleFileCorruption() throws Exception {
        List<Long> corruptions = Collections.singletonList(TestOfflineImageViewer.FILE_NODE_ID_1);
        String result = testCorruptionDetectorRun(1, corruptions, "");
        String expected = readExpectedFile("testSingleFileCorruption.csv");
        Assert.assertEquals(expected, result);
        result = testCorruptionDetectorRun(2, corruptions, ((new FileSystemTestHelper().getTestRootDir()) + "/corruption2.db"));
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testCorruptionDetectionMultipleFileCorruption() throws Exception {
        List<Long> corruptions = Arrays.asList(TestOfflineImageViewer.FILE_NODE_ID_1, TestOfflineImageViewer.FILE_NODE_ID_2, TestOfflineImageViewer.FILE_NODE_ID_3);
        String result = testCorruptionDetectorRun(3, corruptions, "");
        String expected = readExpectedFile("testMultipleFileCorruption.csv");
        Assert.assertEquals(expected, result);
        result = testCorruptionDetectorRun(4, corruptions, ((new FileSystemTestHelper().getTestRootDir()) + "/corruption4.db"));
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testCorruptionDetectionSingleFolderCorruption() throws Exception {
        List<Long> corruptions = Collections.singletonList(TestOfflineImageViewer.DIR_NODE_ID);
        String result = testCorruptionDetectorRun(5, corruptions, "");
        String expected = readExpectedFile("testSingleFolderCorruption.csv");
        Assert.assertEquals(expected, result);
        result = testCorruptionDetectorRun(6, corruptions, ((new FileSystemTestHelper().getTestRootDir()) + "/corruption6.db"));
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testCorruptionDetectionMultipleCorruption() throws Exception {
        List<Long> corruptions = Arrays.asList(TestOfflineImageViewer.FILE_NODE_ID_1, TestOfflineImageViewer.FILE_NODE_ID_2, TestOfflineImageViewer.FILE_NODE_ID_3, TestOfflineImageViewer.DIR_NODE_ID);
        String result = testCorruptionDetectorRun(7, corruptions, "");
        String expected = readExpectedFile("testMultipleCorruption.csv");
        Assert.assertEquals(expected, result);
        result = testCorruptionDetectorRun(8, corruptions, ((new FileSystemTestHelper().getTestRootDir()) + "/corruption8.db"));
        Assert.assertEquals(expected, result);
    }

    /**
     * Tests the ReverseXML processor.
     *
     * 1. Translate fsimage -> reverseImage.xml
     * 2. Translate reverseImage.xml -> reverseImage
     * 3. Translate reverseImage -> reverse2Image.xml
     * 4. Verify that reverseImage.xml and reverse2Image.xml match
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testReverseXmlRoundTrip() throws Throwable {
        GenericTestUtils.setLogLevel(OfflineImageReconstructor.LOG, Level.TRACE);
        File reverseImageXml = new File(TestOfflineImageViewer.tempDir, "reverseImage.xml");
        File reverseImage = new File(TestOfflineImageViewer.tempDir, "reverseImage");
        File reverseImage2Xml = new File(TestOfflineImageViewer.tempDir, "reverseImage2.xml");
        TestOfflineImageViewer.LOG.info(((((("Creating reverseImage.xml=" + (reverseImageXml.getAbsolutePath())) + ", reverseImage=") + (reverseImage.getAbsolutePath())) + ", reverseImage2Xml=") + (reverseImage2Xml.getAbsolutePath())));
        if ((OfflineImageViewerPB.run(new String[]{ "-p", "XML", "-i", TestOfflineImageViewer.originalFsimage.getAbsolutePath(), "-o", reverseImageXml.getAbsolutePath() })) != 0) {
            throw new IOException("oiv returned failure creating first XML file.");
        }
        if ((OfflineImageViewerPB.run(new String[]{ "-p", "ReverseXML", "-i", reverseImageXml.getAbsolutePath(), "-o", reverseImage.getAbsolutePath() })) != 0) {
            throw new IOException("oiv returned failure recreating fsimage file.");
        }
        if ((OfflineImageViewerPB.run(new String[]{ "-p", "XML", "-i", reverseImage.getAbsolutePath(), "-o", reverseImage2Xml.getAbsolutePath() })) != 0) {
            throw new IOException(("oiv returned failure creating second " + "XML file."));
        }
        // The XML file we wrote based on the re-created fsimage should be the
        // same as the one we dumped from the original fsimage.
        Assert.assertEquals("", GenericTestUtils.getFilesDiff(reverseImageXml, reverseImage2Xml));
    }

    /**
     * Tests that the ReverseXML processor doesn't accept XML files with the wrong
     * layoutVersion.
     */
    @Test
    public void testReverseXmlWrongLayoutVersion() throws Throwable {
        File imageWrongVersion = new File(TestOfflineImageViewer.tempDir, "imageWrongVersion.xml");
        PrintWriter writer = new PrintWriter(imageWrongVersion, "UTF-8");
        try {
            writer.println("<?xml version=\"1.0\"?>");
            writer.println("<fsimage>");
            writer.println("<version>");
            writer.println(String.format("<layoutVersion>%d</layoutVersion>", ((NameNodeLayoutVersion.CURRENT_LAYOUT_VERSION) + 1)));
            writer.println("<onDiskVersion>1</onDiskVersion>");
            writer.println(("<oivRevision>" + "545bbef596c06af1c3c8dca1ce29096a64608478</oivRevision>"));
            writer.println("</version>");
            writer.println("</fsimage>");
        } finally {
            writer.close();
        }
        try {
            OfflineImageReconstructor.run(imageWrongVersion.getAbsolutePath(), ((imageWrongVersion.getAbsolutePath()) + ".out"));
            Assert.fail(("Expected OfflineImageReconstructor to fail with " + "version mismatch."));
        } catch (Throwable t) {
            GenericTestUtils.assertExceptionContains("Layout version mismatch.", t);
        }
    }

    @Test
    public void testFileDistributionCalculatorForException() throws Exception {
        File fsimageFile = null;
        Configuration conf = new Configuration();
        HashMap<String, FileStatus> files = Maps.newHashMap();
        // Create a initial fsimage file
        try (MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build()) {
            cluster.waitActive();
            DistributedFileSystem hdfs = cluster.getFileSystem();
            // Create a reasonable namespace
            Path dir = new Path("/dir");
            hdfs.mkdirs(dir);
            files.put(dir.toString(), TestOfflineImageViewer.pathToFileEntry(hdfs, dir.toString()));
            // Create files with byte size that can't be divided by step size,
            // the byte size for here are 3, 9, 15, 21.
            for (int i = 0; i < (TestOfflineImageViewer.FILES_PER_DIR); i++) {
                Path file = new Path(dir, ("file" + i));
                DFSTestUtil.createFile(hdfs, file, ((6 * i) + 3), ((short) (1)), 0);
                files.put(file.toString(), TestOfflineImageViewer.pathToFileEntry(hdfs, file.toString()));
            }
            // Write results to the fsimage file
            hdfs.setSafeMode(SAFEMODE_ENTER, false);
            hdfs.saveNamespace();
            // Determine location of fsimage file
            fsimageFile = FSImageTestUtil.findLatestImageFile(FSImageTestUtil.getFSImage(cluster.getNameNode()).getStorage().getStorageDir(0));
            if (fsimageFile == null) {
                throw new RuntimeException("Didn't generate or can't find fsimage");
            }
        }
        // Run the test with params -maxSize 23 and -step 4, it will not throw
        // ArrayIndexOutOfBoundsException with index 6 when deals with
        // 21 byte size file.
        int status = OfflineImageViewerPB.run(new String[]{ "-i", fsimageFile.getAbsolutePath(), "-o", "-", "-p", "FileDistribution", "-maxSize", "23", "-step", "4" });
        Assert.assertEquals(0, status);
    }

    @Test
    public void testOfflineImageViewerMaxSizeAndStepOptions() throws Exception {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final PrintStream out = new PrintStream(bytes);
        final PrintStream oldOut = System.out;
        try {
            System.setOut(out);
            // Add the -h option to make the test only for option parsing,
            // and don't need to do the following operations.
            OfflineImageViewer.main(new String[]{ "-i", "-", "-o", "-", "-p", "FileDistribution", "-maxSize", "512", "-step", "8", "-h" });
            Assert.assertFalse(bytes.toString().contains("Error parsing command-line options: "));
        } finally {
            System.setOut(oldOut);
            IOUtils.closeStream(out);
        }
    }

    @Test
    public void testOfflineImageViewerWithFormatOption() throws Exception {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final PrintStream out = new PrintStream(bytes);
        final PrintStream oldOut = System.out;
        try {
            System.setOut(out);
            int status = OfflineImageViewerPB.run(new String[]{ "-i", TestOfflineImageViewer.originalFsimage.getAbsolutePath(), "-o", "-", "-p", "FileDistribution", "-maxSize", "512", "-step", "8", "-format" });
            Assert.assertEquals(0, status);
            Assert.assertTrue(bytes.toString().contains("(0 B, 8 B]"));
        } finally {
            System.setOut(oldOut);
            IOUtils.closeStream(out);
        }
    }

    @Test
    public void testOfflineImageViewerForECPolicies() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream o = new PrintStream(output);
        PBImageXmlWriter v = new PBImageXmlWriter(new Configuration(), o);
        v.visit(new RandomAccessFile(TestOfflineImageViewer.originalFsimage, "r"));
        final String xml = output.toString();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml));
        Document dom = db.parse(is);
        NodeList ecSection = dom.getElementsByTagName(PBImageXmlWriter.ERASURE_CODING_SECTION_NAME);
        Assert.assertEquals(1, ecSection.getLength());
        NodeList policies = dom.getElementsByTagName(PBImageXmlWriter.ERASURE_CODING_SECTION_POLICY);
        Assert.assertEquals((1 + (SystemErasureCodingPolicies.getPolicies().size())), policies.getLength());
        for (int i = 0; i < (policies.getLength()); i++) {
            Element policy = ((Element) (policies.item(i)));
            String name = TestOfflineImageViewer.getXmlString(policy, PBImageXmlWriter.ERASURE_CODING_SECTION_POLICY_NAME);
            if (name.equals(TestOfflineImageViewer.addedErasureCodingPolicyName)) {
                String cellSize = TestOfflineImageViewer.getXmlString(policy, PBImageXmlWriter.ERASURE_CODING_SECTION_POLICY_CELL_SIZE);
                Assert.assertEquals("1024", cellSize);
                String state = TestOfflineImageViewer.getXmlString(policy, PBImageXmlWriter.ERASURE_CODING_SECTION_POLICY_STATE);
                Assert.assertEquals(ENABLED.toString(), state);
                Element schema = ((Element) (policy.getElementsByTagName(PBImageXmlWriter.ERASURE_CODING_SECTION_SCHEMA).item(0)));
                String codecName = TestOfflineImageViewer.getXmlString(schema, PBImageXmlWriter.ERASURE_CODING_SECTION_SCHEMA_CODEC_NAME);
                Assert.assertEquals(RS_CODEC_NAME, codecName);
                NodeList options = schema.getElementsByTagName(PBImageXmlWriter.ERASURE_CODING_SECTION_SCHEMA_OPTION);
                Assert.assertEquals(2, options.getLength());
                Element option1 = ((Element) (options.item(0)));
                Assert.assertEquals("k1", TestOfflineImageViewer.getXmlString(option1, "key"));
                Assert.assertEquals("v1", TestOfflineImageViewer.getXmlString(option1, "value"));
                Element option2 = ((Element) (options.item(1)));
                Assert.assertEquals("k2", TestOfflineImageViewer.getXmlString(option2, "key"));
                Assert.assertEquals("v2", TestOfflineImageViewer.getXmlString(option2, "value"));
            }
        }
    }
}

