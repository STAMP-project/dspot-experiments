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
package org.apache.hadoop.cli;


import CLITestHelper.TestConfigFileParser;
import org.apache.hadoop.cli.util.CLICommandErasureCodingCli;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.xml.sax.SAXException;


public class TestErasureCodingCLI extends CLITestHelper {
    private final int NUM_OF_DATANODES = 3;

    private MiniDFSCluster dfsCluster = null;

    private DistributedFileSystem fs = null;

    private String namenode = null;

    @Rule
    public Timeout globalTimeout = new Timeout(300000);

    private class TestErasureCodingAdmin extends CLITestHelper.TestConfigFileParser {
        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (qName.equals("ec-admin-command")) {
                if ((testCommands) != null) {
                    testCommands.add(new CLITestCmdErasureCoding(charString, new CLICommandErasureCodingCli()));
                } else
                    if ((cleanupCommands) != null) {
                        cleanupCommands.add(new CLITestCmdErasureCoding(charString, new CLICommandErasureCodingCli()));
                    }

            } else {
                super.endElement(uri, localName, qName);
            }
        }
    }

    @Test
    @Override
    public void testAll() {
        super.testAll();
    }
}

