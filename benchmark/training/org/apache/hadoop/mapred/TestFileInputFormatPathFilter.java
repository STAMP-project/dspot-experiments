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
package org.apache.hadoop.mapred;


import java.io.IOException;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.junit.Test;


public class TestFileInputFormatPathFilter {
    public static class DummyFileInputFormat extends FileInputFormat {
        public RecordReader getRecordReader(InputSplit split, JobConf job, Reporter reporter) throws IOException {
            return null;
        }
    }

    private static FileSystem localFs = null;

    static {
        try {
            TestFileInputFormatPathFilter.localFs = FileSystem.getLocal(new JobConf());
        } catch (IOException e) {
            throw new RuntimeException("init failure", e);
        }
    }

    private static Path workDir = new Path(new Path(System.getProperty("test.build.data", "."), "data"), "TestFileInputFormatPathFilter");

    public static class TestPathFilter implements PathFilter {
        public boolean accept(Path path) {
            String name = path.getName();
            return (name.equals("TestFileInputFormatPathFilter")) || ((name.length()) == 1);
        }
    }

    @Test
    public void testWithoutPathFilterWithoutGlob() throws Exception {
        _testInputFiles(false, false);
    }

    @Test
    public void testWithoutPathFilterWithGlob() throws Exception {
        _testInputFiles(false, true);
    }

    @Test
    public void testWithPathFilterWithoutGlob() throws Exception {
        _testInputFiles(true, false);
    }

    @Test
    public void testWithPathFilterWithGlob() throws Exception {
        _testInputFiles(true, true);
    }
}

