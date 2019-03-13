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
package org.apache.dubbo.common.utils;


import org.apache.dubbo.common.logger.Logger;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class LogHelperTest {
    @Test
    public void testTrace() throws Exception {
        Logger logger = Mockito.mock(Logger.class);
        Mockito.when(logger.isTraceEnabled()).thenReturn(true);
        LogHelper.trace(logger, "trace");
        Mockito.verify(logger).trace("trace");
        Throwable t = new RuntimeException();
        LogHelper.trace(logger, t);
        Mockito.verify(logger).trace(t);
        LogHelper.trace(logger, "trace", t);
        Mockito.verify(logger).trace("trace", t);
    }

    @Test
    public void testDebug() throws Exception {
        Logger logger = Mockito.mock(Logger.class);
        Mockito.when(logger.isDebugEnabled()).thenReturn(true);
        LogHelper.debug(logger, "debug");
        Mockito.verify(logger).debug("debug");
        Throwable t = new RuntimeException();
        LogHelper.debug(logger, t);
        Mockito.verify(logger).debug(t);
        LogHelper.debug(logger, "debug", t);
        Mockito.verify(logger).debug("debug", t);
    }

    @Test
    public void testInfo() throws Exception {
        Logger logger = Mockito.mock(Logger.class);
        Mockito.when(logger.isInfoEnabled()).thenReturn(true);
        LogHelper.info(logger, "info");
        Mockito.verify(logger).info("info");
        Throwable t = new RuntimeException();
        LogHelper.info(logger, t);
        Mockito.verify(logger).info(t);
        LogHelper.info(logger, "info", t);
        Mockito.verify(logger).info("info", t);
    }

    @Test
    public void testWarn() throws Exception {
        Logger logger = Mockito.mock(Logger.class);
        Mockito.when(logger.isWarnEnabled()).thenReturn(true);
        LogHelper.warn(logger, "warn");
        Mockito.verify(logger).warn("warn");
        Throwable t = new RuntimeException();
        LogHelper.warn(logger, t);
        Mockito.verify(logger).warn(t);
        LogHelper.warn(logger, "warn", t);
        Mockito.verify(logger).warn("warn", t);
    }

    @Test
    public void testError() throws Exception {
        Logger logger = Mockito.mock(Logger.class);
        Mockito.when(logger.isErrorEnabled()).thenReturn(true);
        LogHelper.error(logger, "error");
        Mockito.verify(logger).error("error");
        Throwable t = new RuntimeException();
        LogHelper.error(logger, t);
        Mockito.verify(logger).error(t);
        LogHelper.error(logger, "error", t);
        Mockito.verify(logger).error("error", t);
    }
}

