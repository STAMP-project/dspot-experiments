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
package org.apache.hive.jdbc;


import java.sql.Connection;
import org.apache.hadoop.fs.Path;
import org.apache.hive.jdbc.miniHS2.MiniHS2;
import org.junit.Test;


public class TestXSRFFilter {
    private static MiniHS2 miniHS2 = null;

    private static String dataFileDir;

    private static Path kvDataFilePath;

    private static final String tmpDir = System.getProperty("test.tmp.dir");

    private Connection hs2Conn = null;

    @Test
    public void testFilterDisabledNoInjection() throws Exception {
        // filter disabled, injection disabled, exception not expected
        runTest(false, false);
    }

    @Test
    public void testFilterDisabledWithInjection() throws Exception {
        // filter disabled, injection enabled, exception not expected
        runTest(false, true);
    }

    @Test
    public void testFilterEnabledWithInjection() throws Exception {
        // filter enabled, injection enabled, exception not expected
        runTest(true, true);
    }

    @Test
    public void testFilterEnabledNoInjection() throws Exception {
        // filter enabled, injection disabled, exception expected
        runTest(true, false);
    }
}

