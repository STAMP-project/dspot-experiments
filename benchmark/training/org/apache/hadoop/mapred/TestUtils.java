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


import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.junit.Assert;
import org.junit.Test;

import static FileOutputCommitter.SUCCEEDED_FILE_NAME;


public class TestUtils {
    private static final Path[] LOG_PATHS = new Path[]{ new Path("file:///foo/_logs"), new Path("file:///foo/_logs/"), new Path("_logs/"), new Path("_logs") };

    private static final Path[] SUCCEEDED_PATHS = new Path[]{ new Path(("file:///blah/" + (SUCCEEDED_FILE_NAME))) };

    private static final Path[] PASS_PATHS = new Path[]{ new Path("file:///my_logs/blah"), new Path("file:///a/b/c"), new Path("file:///foo/_logs/blah"), new Path("_logs/foo"), new Path((("file:///blah/" + (SUCCEEDED_FILE_NAME)) + "/bar")) };

    @Test
    public void testOutputFilesFilter() {
        PathFilter filter = new Utils.OutputFileUtils.OutputFilesFilter();
        for (Path p : TestUtils.LOG_PATHS) {
            Assert.assertFalse(filter.accept(p));
        }
        for (Path p : TestUtils.SUCCEEDED_PATHS) {
            Assert.assertFalse(filter.accept(p));
        }
        for (Path p : TestUtils.PASS_PATHS) {
            Assert.assertTrue(filter.accept(p));
        }
    }

    @Test
    public void testLogFilter() {
        PathFilter filter = new Utils.OutputFileUtils.OutputLogFilter();
        for (Path p : TestUtils.LOG_PATHS) {
            Assert.assertFalse(filter.accept(p));
        }
        for (Path p : TestUtils.SUCCEEDED_PATHS) {
            Assert.assertTrue(filter.accept(p));
        }
        for (Path p : TestUtils.PASS_PATHS) {
            Assert.assertTrue(filter.accept(p));
        }
    }
}

