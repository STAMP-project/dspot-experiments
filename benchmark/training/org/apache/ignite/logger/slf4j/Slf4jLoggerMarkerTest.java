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
package org.apache.ignite.logger.slf4j;


import java.io.File;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.LoggerFactory;


/**
 * Testing that markers are supported by log4j2 implementation.
 */
public class Slf4jLoggerMarkerTest {
    /**
     * Path to full log.
     */
    private static final String LOG_ALL = "work/log/all.log";

    /**
     * Path to filtered log.
     */
    private static final String LOG_FILTERED = "work/log/filtered.log";

    /**
     *
     */
    @Test
    public void testMarkerFiltering() throws Exception {
        // create log
        Slf4jLogger log = new Slf4jLogger(LoggerFactory.getLogger(Slf4jLoggerMarkerTest.class));
        // populate log with messages
        log.error("IGNORE_ME", "Ignored error", null);
        log.warning("IGNORE_ME", "Ignored warning", null);
        log.info("IGNORE_ME", "Ignored info");
        log.debug("IGNORE_ME", "Ignored debug");
        log.trace("IGNORE_ME", "Ignored trace");
        log.error("ACCEPT_ME", "Accepted error", null);
        log.warning("ACCEPT_ME", "Accepted warning", null);
        log.info("ACCEPT_ME", "Accepted info");
        log.debug("ACCEPT_ME", "Accepted debug");
        log.trace("ACCEPT_ME", "Accepted trace");
        // check file with all messages
        File allFile = U.resolveIgnitePath(Slf4jLoggerMarkerTest.LOG_ALL);
        Assert.assertNotNull(allFile);
        String all = U.readFileToString(allFile.getPath(), "UTF-8");
        Assert.assertTrue(all.contains("[IGNORE_ME] Ignored error"));
        Assert.assertTrue(all.contains("[IGNORE_ME] Ignored warning"));
        Assert.assertTrue(all.contains("[IGNORE_ME] Ignored info"));
        Assert.assertTrue(all.contains("[IGNORE_ME] Ignored debug"));
        Assert.assertTrue(all.contains("[IGNORE_ME] Ignored trace"));
        Assert.assertTrue(all.contains("[ACCEPT_ME] Accepted error"));
        Assert.assertTrue(all.contains("[ACCEPT_ME] Accepted warning"));
        Assert.assertTrue(all.contains("[ACCEPT_ME] Accepted info"));
        Assert.assertTrue(all.contains("[ACCEPT_ME] Accepted debug"));
        Assert.assertTrue(all.contains("[ACCEPT_ME] Accepted trace"));
        // check file with one marker filtered out
        File filteredFile = U.resolveIgnitePath(Slf4jLoggerMarkerTest.LOG_FILTERED);
        Assert.assertNotNull(filteredFile);
        String filtered = U.readFileToString(filteredFile.getPath(), "UTF-8");
        Assert.assertFalse(filtered.contains("[IGNORE_ME] Ignored error"));
        Assert.assertFalse(filtered.contains("[IGNORE_ME] Ignored warning"));
        Assert.assertFalse(filtered.contains("[IGNORE_ME] Ignored info"));
        Assert.assertFalse(filtered.contains("[IGNORE_ME] Ignored debug"));
        Assert.assertFalse(filtered.contains("[IGNORE_ME] Ignored trace"));
        Assert.assertTrue(filtered.contains("[ACCEPT_ME] Accepted error"));
        Assert.assertTrue(filtered.contains("[ACCEPT_ME] Accepted warning"));
        Assert.assertTrue(filtered.contains("[ACCEPT_ME] Accepted info"));
        Assert.assertTrue(filtered.contains("[ACCEPT_ME] Accepted debug"));
        Assert.assertTrue(filtered.contains("[ACCEPT_ME] Accepted trace"));
    }
}

