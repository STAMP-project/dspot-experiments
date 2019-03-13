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
package org.apache.ignite.logger.log4j;


import java.io.File;
import org.apache.log4j.helpers.FileWatchdog;
import org.junit.Test;


/**
 * Checking that Log4j configuration is updated when its source file is changed.
 */
public class GridLog4jConfigUpdateTest {
    /**
     * Path to log4j configuration with INFO enabled.
     */
    private static final String LOG_CONFIG_INFO = "modules/log4j/src/test/config/log4j-info.xml";

    /**
     * Path to log4j configuration with DEBUG enabled.
     */
    private static final String LOG_CONFIG_DEBUG = "modules/log4j/src/test/config/log4j-debug.xml";

    /**
     * Path to log4j configuration with DEBUG enabled.
     */
    private static final String LOG_CONFIG_MAIN = "work/log/log4j-GridLog4jConfigUpdateTest.xml";

    /**
     * Path to log file.
     */
    private static final String LOG_DEST = "work/log/GridLog4jConfigUpdateTest.log";

    /**
     * Time to wait before updating the configuration file.
     * Should be large enough to be recorded on different file systems.
     */
    private static final int DELAY_BEFORE_MODIFICATION = 5000;

    /**
     * Check that changing log4j config file causes the logger configuration to be updated.
     * String-accepting constructor is used.
     */
    @Test
    public void testConfigChangeStringConstructor() throws Exception {
        checkConfigUpdate(new GridLog4jConfigUpdateTest.Log4JLoggerSupplier() {
            @Override
            public Log4JLogger get(File cfgFile) throws Exception {
                return new Log4JLogger(cfgFile.getPath(), 10);
            }
        }, 5000);// use larger delay to avoid relying on exact timing

    }

    /**
     * Check that changing log4j config file causes the logger configuration to be updated.
     * String-accepting constructor is used.
     */
    @Test
    public void testConfigChangeStringConstructorDefaultDelay() throws Exception {
        checkConfigUpdate(new GridLog4jConfigUpdateTest.Log4JLoggerSupplier() {
            @Override
            public Log4JLogger get(File cfgFile) throws Exception {
                return new Log4JLogger(cfgFile.getPath());
            }
        }, ((FileWatchdog.DEFAULT_DELAY) + 5000));// use larger delay to avoid relying on exact timing

    }

    /**
     * Check that changing log4j config file causes the logger configuration to be updated.
     * File-accepting constructor is used.
     */
    @Test
    public void testConfigChangeFileConstructor() throws Exception {
        checkConfigUpdate(new GridLog4jConfigUpdateTest.Log4JLoggerSupplier() {
            @Override
            public Log4JLogger get(File cfgFile) throws Exception {
                return new Log4JLogger(cfgFile, 10);
            }
        }, 5000);// use larger delay to avoid relying on exact timing

    }

    /**
     * Check that changing log4j config file causes the logger configuration to be updated.
     * File-accepting constructor is used.
     */
    @Test
    public void testConfigChangeUrlConstructor() throws Exception {
        checkConfigUpdate(new GridLog4jConfigUpdateTest.Log4JLoggerSupplier() {
            @Override
            public Log4JLogger get(File cfgFile) throws Exception {
                return new Log4JLogger(cfgFile.toURI().toURL(), 10);
            }
        }, 5000);// use larger delay to avoid relying on exact timing

    }

    /**
     * Creates Log4JLogger instance for testing.
     */
    private interface Log4JLoggerSupplier {
        /**
         * Creates Log4JLogger instance for testing.
         */
        Log4JLogger get(File cfgFile) throws Exception;
    }
}

