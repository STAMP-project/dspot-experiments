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
package org.apache.hadoop.fs.s3a.select;


import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.s3a.AbstractS3ATestBase;
import org.apache.hadoop.fs.s3a.S3AFileSystem;
import org.apache.hadoop.fs.s3a.S3ATestUtils;
import org.apache.hadoop.fs.s3a.Statistic;
import org.apache.hadoop.util.OperationDuration;
import org.junit.Test;


/**
 * Test the S3 Select CLI through some operations against landsat
 * and files generated from it.
 */
public class ITestS3SelectCLI extends AbstractS3SelectTest {
    public static final int LINE_COUNT = 100;

    public static final String SELECT_EVERYTHING = "SELECT * FROM S3OBJECT s";

    private SelectTool.SelectTool selectTool;

    private Configuration selectConf;

    public static final String D = "-D";

    private File localFile;

    private String landsatSrc;

    @Test
    public void testLandsatToFile() throws Throwable {
        describe("select part of the landsat to a file");
        int lineCount = ITestS3SelectCLI.LINE_COUNT;
        S3AFileSystem landsatFS = ((S3AFileSystem) (getLandsatGZ().getFileSystem(getConfiguration())));
        S3ATestUtils.MetricDiff selectCount = new S3ATestUtils.MetricDiff(landsatFS, Statistic.OBJECT_SELECT_REQUESTS);
        run(selectConf, selectTool, ITestS3SelectCLI.D, ITestS3SelectCLI.v(CSV_OUTPUT_QUOTE_CHARACTER, "'"), ITestS3SelectCLI.D, ITestS3SelectCLI.v(CSV_OUTPUT_QUOTE_FIELDS, CSV_OUTPUT_QUOTE_FIELDS_AS_NEEEDED), "select", ITestS3SelectCLI.o(OPT_HEADER), CSV_HEADER_OPT_USE, ITestS3SelectCLI.o(OPT_COMPRESSION), COMPRESSION_OPT_GZIP, ITestS3SelectCLI.o(OPT_LIMIT), Integer.toString(lineCount), ITestS3SelectCLI.o(OPT_OUTPUT), localFile.toString(), landsatSrc, ITestS3SelectLandsat.SELECT_SUNNY_ROWS_NO_LIMIT);
        List<String> lines = IOUtils.readLines(new FileInputStream(localFile), Charset.defaultCharset());
        AbstractS3ATestBase.LOG.info("Result from select:\n{}", lines.get(0));
        assertEquals(lineCount, lines.size());
        selectCount.assertDiffEquals("select count", 1);
        OperationDuration duration = selectTool.getSelectDuration();
        assertTrue("Select duration was not measured", ((duration.value()) > 0));
    }

    @Test
    public void testLandsatToConsole() throws Throwable {
        describe("select part of the landsat to the console");
        // this verifies the input stream was actually closed
        S3ATestUtils.MetricDiff readOps = new S3ATestUtils.MetricDiff(getFileSystem(), Statistic.STREAM_READ_OPERATIONS_INCOMPLETE);
        run(selectConf, selectTool, ITestS3SelectCLI.D, ITestS3SelectCLI.v(CSV_OUTPUT_QUOTE_CHARACTER, "'"), ITestS3SelectCLI.D, ITestS3SelectCLI.v(CSV_OUTPUT_QUOTE_FIELDS, CSV_OUTPUT_QUOTE_FIELDS_ALWAYS), "select", ITestS3SelectCLI.o(OPT_HEADER), CSV_HEADER_OPT_USE, ITestS3SelectCLI.o(OPT_COMPRESSION), COMPRESSION_OPT_GZIP, ITestS3SelectCLI.o(OPT_LIMIT), Integer.toString(ITestS3SelectCLI.LINE_COUNT), landsatSrc, ITestS3SelectLandsat.SELECT_SUNNY_ROWS_NO_LIMIT);
        assertEquals("Lines read and printed to console", ITestS3SelectCLI.LINE_COUNT, selectTool.getLinesRead());
        readOps.assertDiffEquals("Read operations are still considered active", 0);
    }

    @Test
    public void testSelectNothing() throws Throwable {
        describe("an empty select is not an error");
        run(selectConf, selectTool, "select", ITestS3SelectCLI.o(OPT_HEADER), CSV_HEADER_OPT_USE, ITestS3SelectCLI.o(OPT_COMPRESSION), COMPRESSION_OPT_GZIP, ITestS3SelectCLI.o(OPT_INPUTFORMAT), "csv", ITestS3SelectCLI.o(OPT_OUTPUTFORMAT), "csv", ITestS3SelectCLI.o(OPT_EXPECTED), "0", ITestS3SelectCLI.o(OPT_LIMIT), Integer.toString(ITestS3SelectCLI.LINE_COUNT), landsatSrc, ITestS3SelectLandsat.SELECT_NOTHING);
        assertEquals("Lines read and printed to console", 0, selectTool.getLinesRead());
    }

    @Test
    public void testLandsatToRemoteFile() throws Throwable {
        describe("select part of the landsat to a file");
        Path dest = path("testLandsatToRemoteFile.csv");
        run(selectConf, selectTool, ITestS3SelectCLI.D, ITestS3SelectCLI.v(CSV_OUTPUT_QUOTE_CHARACTER, "'"), ITestS3SelectCLI.D, ITestS3SelectCLI.v(CSV_OUTPUT_QUOTE_FIELDS, CSV_OUTPUT_QUOTE_FIELDS_ALWAYS), "select", ITestS3SelectCLI.o(OPT_HEADER), CSV_HEADER_OPT_USE, ITestS3SelectCLI.o(OPT_COMPRESSION), COMPRESSION_OPT_GZIP, ITestS3SelectCLI.o(OPT_LIMIT), Integer.toString(ITestS3SelectCLI.LINE_COUNT), ITestS3SelectCLI.o(OPT_OUTPUT), dest.toString(), landsatSrc, ITestS3SelectLandsat.SELECT_SUNNY_ROWS_NO_LIMIT);
        FileStatus status = getFileSystem().getFileStatus(dest);
        assertEquals(("Mismatch between bytes selected and file len in " + status), selectTool.getBytesRead(), status.getLen());
        assertIsFile(dest);
        // now select on that
        Configuration conf = getConfiguration();
        SelectTool.SelectTool tool2 = new SelectTool.SelectTool(conf);
        run(conf, tool2, "select", ITestS3SelectCLI.o(OPT_HEADER), CSV_HEADER_OPT_NONE, dest.toString(), ITestS3SelectCLI.SELECT_EVERYTHING);
    }

    @Test
    public void testUsage() throws Throwable {
        runToFailure(EXIT_USAGE, getConfiguration(), TOO_FEW_ARGUMENTS, selectTool, "select");
    }

    @Test
    public void testRejectionOfNonS3FS() throws Throwable {
        File dest = getTempFilename();
        runToFailure(EXIT_SERVICE_UNAVAILABLE, getConfiguration(), WRONG_FILESYSTEM, selectTool, "select", dest.toString(), ITestS3SelectCLI.SELECT_EVERYTHING);
    }

    @Test
    public void testFailMissingFile() throws Throwable {
        Path dest = path("testFailMissingFile.csv");
        runToFailure(EXIT_NOT_FOUND, getConfiguration(), "", selectTool, "select", dest.toString(), ITestS3SelectCLI.SELECT_EVERYTHING);
    }

    @Test
    public void testS3SelectDisabled() throws Throwable {
        Configuration conf = getConfiguration();
        conf.setBoolean(FS_S3A_SELECT_ENABLED, false);
        S3ATestUtils.disableFilesystemCaching(conf);
        runToFailure(EXIT_SERVICE_UNAVAILABLE, conf, SELECT_IS_DISABLED, selectTool, "select", ITestS3SelectCLI.o(OPT_HEADER), CSV_HEADER_OPT_USE, ITestS3SelectCLI.o(OPT_COMPRESSION), COMPRESSION_OPT_GZIP, ITestS3SelectCLI.o(OPT_LIMIT), Integer.toString(ITestS3SelectCLI.LINE_COUNT), landsatSrc, ITestS3SelectLandsat.SELECT_SUNNY_ROWS_NO_LIMIT);
    }

    @Test
    public void testSelectBadLimit() throws Throwable {
        runToFailure(EXIT_USAGE, getConfiguration(), "", selectTool, "select", ITestS3SelectCLI.o(OPT_HEADER), CSV_HEADER_OPT_USE, ITestS3SelectCLI.o(OPT_COMPRESSION), COMPRESSION_OPT_GZIP, ITestS3SelectCLI.o(OPT_LIMIT), "-1", landsatSrc, ITestS3SelectLandsat.SELECT_NOTHING);
    }

    @Test
    public void testSelectBadInputFormat() throws Throwable {
        runToFailure(EXIT_COMMAND_ARGUMENT_ERROR, getConfiguration(), "", selectTool, "select", ITestS3SelectCLI.o(OPT_HEADER), CSV_HEADER_OPT_USE, ITestS3SelectCLI.o(OPT_INPUTFORMAT), "pptx", ITestS3SelectCLI.o(OPT_COMPRESSION), COMPRESSION_OPT_GZIP, landsatSrc, ITestS3SelectLandsat.SELECT_NOTHING);
    }

    @Test
    public void testSelectBadOutputFormat() throws Throwable {
        runToFailure(EXIT_COMMAND_ARGUMENT_ERROR, getConfiguration(), "", selectTool, "select", ITestS3SelectCLI.o(OPT_HEADER), CSV_HEADER_OPT_USE, ITestS3SelectCLI.o(OPT_OUTPUTFORMAT), "pptx", ITestS3SelectCLI.o(OPT_COMPRESSION), COMPRESSION_OPT_GZIP, landsatSrc, ITestS3SelectLandsat.SELECT_NOTHING);
    }
}

