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


import java.io.File;
import org.apache.hadoop.cli.util.CLICommandCryptoAdmin;
import org.apache.hadoop.cli.util.CLICommandTypes;
import org.apache.hadoop.cli.util.CLITestCmd;
import org.apache.hadoop.cli.util.CommandExecutor;
import org.apache.hadoop.cli.util.CryptoAdminCmdExecutor;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.Test;
import org.xml.sax.SAXException;


public class TestCryptoAdminCLI extends CLITestHelperDFS {
    protected MiniDFSCluster dfsCluster = null;

    protected FileSystem fs = null;

    protected String namenode = null;

    private static File tmpDir;

    private class TestConfigFileParserCryptoAdmin extends CLITestHelperDFS.TestConfigFileParserDFS {
        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (qName.equals("crypto-admin-command")) {
                if ((testCommands) != null) {
                    testCommands.add(new TestCryptoAdminCLI.CLITestCmdCryptoAdmin(charString, new CLICommandCryptoAdmin()));
                } else
                    if ((cleanupCommands) != null) {
                        cleanupCommands.add(new TestCryptoAdminCLI.CLITestCmdCryptoAdmin(charString, new CLICommandCryptoAdmin()));
                    }

            } else {
                super.endElement(uri, localName, qName);
            }
        }
    }

    private class CLITestCmdCryptoAdmin extends CLITestCmd {
        public CLITestCmdCryptoAdmin(String str, CLICommandTypes type) {
            super(str, type);
        }

        @Override
        public CommandExecutor getExecutor(String tag, Configuration conf) throws IllegalArgumentException {
            if ((getType()) instanceof CLICommandCryptoAdmin) {
                return new CryptoAdminCmdExecutor(tag, new org.apache.hadoop.hdfs.tools.CryptoAdmin(conf));
            }
            return super.getExecutor(tag, conf);
        }
    }

    @Test
    @Override
    public void testAll() {
        super.testAll();
    }
}

