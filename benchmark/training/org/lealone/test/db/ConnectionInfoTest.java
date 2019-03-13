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
package org.lealone.test.db;


import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;
import org.lealone.db.ConnectionInfo;
import org.lealone.test.TestBase;
import org.lealone.test.UnitTestBase;


public class ConnectionInfoTest extends UnitTestBase {
    @Test
    public void run() {
        setEmbedded(true);
        ConnectionInfo ci = new ConnectionInfo(getURL());
        Assert.assertTrue(ci.isEmbedded());
        Assert.assertTrue(ci.isPersistent());
        Assert.assertFalse(ci.isRemote());
        Assert.assertTrue((((ci.getDatabaseShortName()) != null) && (ci.getDatabaseShortName().endsWith(TestBase.DEFAULT_DB_NAME))));
        Assert.assertNull(ci.getServers());
        setEmbedded(false);
        ci = new ConnectionInfo(getURL());
        Assert.assertFalse(ci.isEmbedded());
        Assert.assertFalse(ci.isPersistent());// TCP???URL?Client?????????????Persistent????false

        Assert.assertTrue(ci.isRemote());
        Assert.assertEquals(TestBase.DEFAULT_DB_NAME, ci.getDatabaseShortName());
        Assert.assertEquals(getHostAndPort(), ci.getServers());
        try {
            new ConnectionInfo("invalid url");
            Assert.fail();
        } catch (Exception e) {
        }
        setMysqlUrlStyle(true);
        try {
            new ConnectionInfo(((getURL()) + ";a=b"));// MySQL???URL?????';'

            Assert.fail();
        } catch (Exception e) {
        }
        setMysqlUrlStyle(false);
        try {
            new ConnectionInfo(((getURL()) + "&a=b"));// ?????URL?????'&'

            Assert.fail();
        } catch (Exception e) {
        }
        setEmbedded(true);
        try {
            new ConnectionInfo(getURL(), "mydb");// ???Server???ConnectionInfo?URL???????

            Assert.fail();
        } catch (Exception e) {
        }
        try {
            new ConnectionInfo(((getURL()) + ";a=b"));// a?????????

            Assert.fail();
        } catch (Exception e) {
        }
        try {
            Properties prop = new Properties();
            prop.setProperty("IS_LOCAL", "true");
            new ConnectionInfo(((getURL()) + ";IS_LOCAL=true"), prop);// url????????Properties??????????????????????

            new ConnectionInfo(((getURL()) + ";IS_LOCAL=false"), prop);// ????????????

            Assert.fail();
        } catch (Exception e) {
        }
    }
}

