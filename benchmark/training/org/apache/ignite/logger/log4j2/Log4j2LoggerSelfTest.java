/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.logger.log4j2;


import java.io.File;
import java.net.URL;
import java.util.UUID;
import org.apache.ignite.IgniteLogger;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.testframework.GridTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Grid Log4j2 SPI test.
 */
public class Log4j2LoggerSelfTest {
    /**
     *
     */
    private static final String LOG_PATH_TEST = "modules/core/src/test/config/log4j2-test.xml";

    /**
     *
     */
    private static final String LOG_PATH_MAIN = "config/ignite-log4j2.xml";

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testFileConstructor() throws Exception {
        File xml = GridTestUtils.resolveIgnitePath(Log4j2LoggerSelfTest.LOG_PATH_TEST);
        assert xml != null;
        assert xml.exists();
        IgniteLogger log = new Log4J2Logger(xml).getLogger(getClass());
        System.out.println(log.toString());
        Assert.assertTrue(log.toString().contains("Log4J2Logger"));
        Assert.assertTrue(log.toString().contains(xml.getPath()));
        setNodeId(UUID.randomUUID());
        checkLog(log);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testUrlConstructor() throws Exception {
        File xml = GridTestUtils.resolveIgnitePath(Log4j2LoggerSelfTest.LOG_PATH_TEST);
        assert xml != null;
        assert xml.exists();
        URL url = xml.toURI().toURL();
        IgniteLogger log = new Log4J2Logger(url).getLogger(getClass());
        System.out.println(log.toString());
        Assert.assertTrue(log.toString().contains("Log4J2Logger"));
        Assert.assertTrue(log.toString().contains(url.getPath()));
        setNodeId(UUID.randomUUID());
        checkLog(log);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPathConstructor() throws Exception {
        IgniteLogger log = new Log4J2Logger(Log4j2LoggerSelfTest.LOG_PATH_TEST).getLogger(getClass());
        System.out.println(log.toString());
        Assert.assertTrue(log.toString().contains("Log4J2Logger"));
        Assert.assertTrue(log.toString().contains(Log4j2LoggerSelfTest.LOG_PATH_TEST));
        setNodeId(UUID.randomUUID());
        checkLog(log);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSystemNodeId() throws Exception {
        UUID id = UUID.randomUUID();
        new Log4J2Logger(Log4j2LoggerSelfTest.LOG_PATH_TEST).setNodeId(id);
        Assert.assertEquals(U.id8(id), System.getProperty("nodeId"));
    }

    /**
     * Tests correct behaviour in case 2 local nodes are started.
     *
     * @throws Exception
     * 		If error occurs.
     */
    @Test
    public void testLogFilesTwoNodes() throws Exception {
        checkOneNode(0);
        checkOneNode(1);
    }
}

