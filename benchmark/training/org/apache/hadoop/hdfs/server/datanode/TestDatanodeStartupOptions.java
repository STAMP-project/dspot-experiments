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
package org.apache.hadoop.hdfs.server.datanode;


import StartupOption.REGULAR;
import StartupOption.ROLLBACK;
import org.apache.hadoop.conf.Configuration;
import org.junit.Test;


/**
 * This test verifies DataNode command line processing.
 */
public class TestDatanodeStartupOptions {
    private Configuration conf = null;

    /**
     * A few options that should all parse successfully.
     */
    @Test(timeout = 60000)
    public void testStartupSuccess() {
        TestDatanodeStartupOptions.checkExpected(true, REGULAR, conf);
        TestDatanodeStartupOptions.checkExpected(true, REGULAR, conf, "-regular");
        TestDatanodeStartupOptions.checkExpected(true, REGULAR, conf, "-REGULAR");
        TestDatanodeStartupOptions.checkExpected(true, ROLLBACK, conf, "-rollback");
    }

    /**
     * A few options that should all fail to parse.
     */
    @Test(timeout = 60000)
    public void testStartupFailure() {
        TestDatanodeStartupOptions.checkExpected(false, REGULAR, conf, "unknownoption");
        TestDatanodeStartupOptions.checkExpected(false, REGULAR, conf, "-regular -rollback");
    }
}

