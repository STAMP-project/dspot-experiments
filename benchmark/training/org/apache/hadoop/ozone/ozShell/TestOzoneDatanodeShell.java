/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.ozone.ozShell;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.ozone.HddsDatanodeService;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This test class specified for testing Ozone datanode shell command.
 */
@RunWith(Parameterized.class)
public class TestOzoneDatanodeShell {
    private static final Logger LOG = LoggerFactory.getLogger(TestOzoneDatanodeShell.class);

    /**
     * Set the timeout for every test.
     */
    @Rule
    public Timeout testTimeout = new Timeout(300000);

    private static File baseDir;

    private static OzoneConfiguration conf = null;

    private static MiniOzoneCluster cluster = null;

    private static HddsDatanodeService datanode = null;

    private final ByteArrayOutputStream out = new ByteArrayOutputStream();

    private final ByteArrayOutputStream err = new ByteArrayOutputStream();

    private static final PrintStream OLD_OUT = System.out;

    private static final PrintStream OLD_ERR = System.err;

    @Parameterized.Parameter
    @SuppressWarnings("visibilitymodifier")
    public Class clientProtocol;

    @Test
    public void testDatanodeIncompleteCommand() {
        TestOzoneDatanodeShell.LOG.info("Running testDatanodeIncompleteCommand");
        String expectedError = "Incomplete command";
        String[] args = new String[]{  };// executing 'ozone datanode'

        executeDatanodeWithError(TestOzoneDatanodeShell.datanode, args, expectedError, "Usage: ozone datanode [-hV] [--verbose] [-D=<String=String>]...");
    }
}

