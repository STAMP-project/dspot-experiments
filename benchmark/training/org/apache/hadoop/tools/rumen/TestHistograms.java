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
package org.apache.hadoop.tools.rumen;


import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


@Ignore
public class TestHistograms {
    /**
     *
     *
     * @throws IOException
     * 		There should be files in the directory named by
     * 		${test.build.data}/rumen/histogram-test .
     * 		
     * 		There will be pairs of files, inputXxx.json and goldXxx.json .
     * 		
     * 		We read the input file as a HistogramRawTestData in json. Then we
     * 		create a Histogram using the data field, and then a
     * 		LoggedDiscreteCDF using the percentiles and scale field. Finally,
     * 		we read the corresponding goldXxx.json as a LoggedDiscreteCDF and
     * 		deepCompare them.
     */
    @Test
    public void testHistograms() throws IOException {
        final Configuration conf = new Configuration();
        final FileSystem lfs = FileSystem.getLocal(conf);
        final Path rootInputDir = lfs.makeQualified(new Path(System.getProperty("test.tools.input.dir", "target/input")));
        final Path rootInputFile = new Path(rootInputDir, "rumen/histogram-tests");
        FileStatus[] tests = lfs.listStatus(rootInputFile);
        for (int i = 0; i < (tests.length); ++i) {
            Path filePath = tests[i].getPath();
            String fileName = filePath.getName();
            if (fileName.startsWith("input")) {
                String testName = fileName.substring("input".length());
                Path goldFilePath = new Path(rootInputFile, ("gold" + testName));
                Assert.assertTrue("Gold file dies not exist", lfs.exists(goldFilePath));
                LoggedDiscreteCDF newResult = TestHistograms.histogramFileToCDF(filePath, lfs);
                System.out.println(("Testing a Histogram for " + fileName));
                FSDataInputStream goldStream = lfs.open(goldFilePath);
                JsonObjectMapperParser<LoggedDiscreteCDF> parser = new JsonObjectMapperParser<LoggedDiscreteCDF>(goldStream, LoggedDiscreteCDF.class);
                try {
                    LoggedDiscreteCDF dcdf = parser.getNext();
                    dcdf.deepCompare(newResult, new TreePath(null, "<root>"));
                } catch (DeepInequalityException e) {
                    Assert.fail(e.path.toString());
                } finally {
                    parser.close();
                }
            }
        }
    }
}

