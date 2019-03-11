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
package org.apache.hadoop.fs;


import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test Job History Log Analyzer.
 *
 * @see JHLogAnalyzer
 */
public class TestJHLA {
    private static final Logger LOG = LoggerFactory.getLogger(JHLogAnalyzer.class);

    private String historyLog = (System.getProperty("test.build.data", "build/test/data")) + "/history/test.log";

    /**
     * Run log analyzer in test mode for file test.log.
     */
    @Test
    public void testJHLA() {
        String[] args = new String[]{ "-test", historyLog, "-jobDelimiter", ".!!FILE=.*!!" };
        JHLogAnalyzer.main(args);
        args = new String[]{ "-test", historyLog, "-jobDelimiter", ".!!FILE=.*!!", "-usersIncluded", "hadoop,hadoop2" };
        JHLogAnalyzer.main(args);
        args = new String[]{ "-test", historyLog, "-jobDelimiter", ".!!FILE=.*!!", "-usersExcluded", "hadoop,hadoop3" };
        JHLogAnalyzer.main(args);
    }
}

