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
package org.apache.hive.beeline.hs2connection;


import org.junit.Assert;
import org.junit.Test;


public class TestBeelineConnectionUsingHiveSite extends BeelineWithHS2ConnectionFileTestBase {
    @Test
    public void testBeelineConnectionHttp() throws Exception {
        setupHs2();
        String path = createDefaultHs2ConnectionFile();
        assertBeelineOutputContains(path, new String[]{ "-e", "show tables;" }, tableName);
    }

    @Test
    public void testBeelineConnectionSSL() throws Exception {
        setupSSLHs2();
        String path = createDefaultHs2ConnectionFile();
        assertBeelineOutputContains(path, new String[]{ "-e", "show tables;" }, tableName);
    }

    @Test
    public void testBeelineConnectionNoAuth() throws Exception {
        setupNoAuthHs2();
        String path = createDefaultHs2ConnectionFile();
        assertBeelineOutputContains(path, new String[]{ "-e", "show tables;" }, tableName);
    }

    @Test
    public void testBeelineDoesntUseDefaultIfU() throws Exception {
        setupNoAuthHs2();
        String path = createDefaultHs2ConnectionFile();
        BeelineWithHS2ConnectionFileTestBase.BeelineResult res = getBeelineOutput(path, new String[]{ "-u", "invalidUrl", "-e", "show tables;" });
        Assert.assertEquals(1, res.exitCode);
        Assert.assertFalse(((tableName) + " should not appear"), res.output.toLowerCase().contains(tableName));
    }

    /* tests if the beeline behaves like default mode if there is no user-specific connection
    configuration file
     */
    @Test
    public void testBeelineWithNoConnectionFile() throws Exception {
        setupNoAuthHs2();
        BeelineWithHS2ConnectionFileTestBase.BeelineResult res = getBeelineOutput(null, new String[]{ "-e", "show tables;" });
        Assert.assertEquals(1, res.exitCode);
        Assert.assertTrue(res.output.toLowerCase().contains("no current connection"));
    }

    @Test
    public void testBeelineUsingArgs() throws Exception {
        setupNoAuthHs2();
        String url = (miniHS2.getBaseJdbcURL()) + "default";
        String[] args = new String[]{ "-u", url, "-n", System.getProperty("user.name"), "-p", "foo", "-e", "show tables;" };
        assertBeelineOutputContains(null, args, tableName);
    }
}

