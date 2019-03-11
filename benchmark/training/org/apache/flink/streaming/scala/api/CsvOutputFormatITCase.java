/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.streaming.scala.api;


import FileSystem.WriteMode.NO_OVERWRITE;
import WordCountData.TEXT;
import org.apache.flink.streaming.api.scala.OutputFormatTestPrograms;
import org.apache.flink.test.util.AbstractTestBase;
import org.junit.Assert;
import org.junit.Test;


/**
 * IT cases for the {@link org.apache.flink.api.java.io.CsvOutputFormat}.
 */
public class CsvOutputFormatITCase extends AbstractTestBase {
    protected String resultPath;

    @Test
    public void testPath() throws Exception {
        OutputFormatTestPrograms.wordCountToCsv(TEXT, resultPath);
    }

    @Test
    public void testPathMillis() throws Exception {
        OutputFormatTestPrograms.wordCountToCsv(TEXT, resultPath);
    }

    @Test
    public void testPathWriteMode() throws Exception {
        OutputFormatTestPrograms.wordCountToCsv(TEXT, resultPath, NO_OVERWRITE);
    }

    @Test
    public void testPathWriteModeMillis() throws Exception {
        OutputFormatTestPrograms.wordCountToCsv(TEXT, resultPath, NO_OVERWRITE);
    }

    @Test
    public void testPathWriteModeMillisDelimiter() throws Exception {
        OutputFormatTestPrograms.wordCountToCsv(TEXT, resultPath, NO_OVERWRITE, "\n", ",");
    }

    @Test
    public void failPathWriteMode() throws Exception {
        OutputFormatTestPrograms.wordCountToCsv(TEXT, resultPath);
        try {
            OutputFormatTestPrograms.wordCountToCsv(TEXT, resultPath, NO_OVERWRITE);
            Assert.fail("File should exist.");
        } catch (Exception e) {
            Assert.assertTrue(e.getCause().getMessage().contains("File already exists"));
        }
    }

    @Test
    public void failPathWriteModeMillis() throws Exception {
        OutputFormatTestPrograms.wordCountToCsv(TEXT, resultPath);
        try {
            OutputFormatTestPrograms.wordCountToCsv(TEXT, resultPath, NO_OVERWRITE);
            Assert.fail("File should exist");
        } catch (Exception e) {
            Assert.assertTrue(e.getCause().getMessage().contains("File already exists"));
        }
    }

    @Test
    public void failPathWriteModeMillisDelimiter() throws Exception {
        OutputFormatTestPrograms.wordCountToCsv(TEXT, resultPath);
        try {
            OutputFormatTestPrograms.wordCountToCsv(TEXT, resultPath, NO_OVERWRITE, "\n", ",");
            Assert.fail("File should exist.");
        } catch (Exception e) {
            Assert.assertTrue(e.getCause().getMessage().contains("File already exists"));
        }
    }
}

