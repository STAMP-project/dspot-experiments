/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.common.logger.support;


import org.apache.dubbo.common.logger.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class FailsafeLoggerTest {
    @Test
    public void testFailSafeForLoggingMethod() {
        Logger failLogger = Mockito.mock(Logger.class);
        FailsafeLogger failsafeLogger = new FailsafeLogger(failLogger);
        Mockito.doThrow(new RuntimeException()).when(failLogger).error(ArgumentMatchers.anyString());
        Mockito.doThrow(new RuntimeException()).when(failLogger).warn(ArgumentMatchers.anyString());
        Mockito.doThrow(new RuntimeException()).when(failLogger).info(ArgumentMatchers.anyString());
        Mockito.doThrow(new RuntimeException()).when(failLogger).debug(ArgumentMatchers.anyString());
        Mockito.doThrow(new RuntimeException()).when(failLogger).trace(ArgumentMatchers.anyString());
        failsafeLogger.error("error");
        failsafeLogger.warn("warn");
        failsafeLogger.info("info");
        failsafeLogger.debug("debug");
        failsafeLogger.trace("info");
        Mockito.doThrow(new RuntimeException()).when(failLogger).error(ArgumentMatchers.any(Throwable.class));
        Mockito.doThrow(new RuntimeException()).when(failLogger).warn(ArgumentMatchers.any(Throwable.class));
        Mockito.doThrow(new RuntimeException()).when(failLogger).info(ArgumentMatchers.any(Throwable.class));
        Mockito.doThrow(new RuntimeException()).when(failLogger).debug(ArgumentMatchers.any(Throwable.class));
        Mockito.doThrow(new RuntimeException()).when(failLogger).trace(ArgumentMatchers.any(Throwable.class));
        failsafeLogger.error(new Exception("error"));
        failsafeLogger.warn(new Exception("warn"));
        failsafeLogger.info(new Exception("info"));
        failsafeLogger.debug(new Exception("debug"));
        failsafeLogger.trace(new Exception("trace"));
        failsafeLogger.error("error", new Exception("error"));
        failsafeLogger.warn("warn", new Exception("warn"));
        failsafeLogger.info("info", new Exception("info"));
        failsafeLogger.debug("debug", new Exception("debug"));
        failsafeLogger.trace("trace", new Exception("trace"));
    }

    @Test
    public void testSuccessLogger() {
        Logger successLogger = Mockito.mock(Logger.class);
        FailsafeLogger failsafeLogger = new FailsafeLogger(successLogger);
        failsafeLogger.error("error");
        failsafeLogger.warn("warn");
        failsafeLogger.info("info");
        failsafeLogger.debug("debug");
        failsafeLogger.trace("info");
        Mockito.verify(successLogger).error(ArgumentMatchers.anyString());
        Mockito.verify(successLogger).warn(ArgumentMatchers.anyString());
        Mockito.verify(successLogger).info(ArgumentMatchers.anyString());
        Mockito.verify(successLogger).debug(ArgumentMatchers.anyString());
        Mockito.verify(successLogger).trace(ArgumentMatchers.anyString());
        failsafeLogger.error(new Exception("error"));
        failsafeLogger.warn(new Exception("warn"));
        failsafeLogger.info(new Exception("info"));
        failsafeLogger.debug(new Exception("debug"));
        failsafeLogger.trace(new Exception("trace"));
        failsafeLogger.error("error", new Exception("error"));
        failsafeLogger.warn("warn", new Exception("warn"));
        failsafeLogger.info("info", new Exception("info"));
        failsafeLogger.debug("debug", new Exception("debug"));
        failsafeLogger.trace("trace", new Exception("trace"));
    }

    @Test
    public void testGetLogger() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            Logger failLogger = Mockito.mock(Logger.class);
            FailsafeLogger failsafeLogger = new FailsafeLogger(failLogger);
            Mockito.doThrow(new RuntimeException()).when(failLogger).error(ArgumentMatchers.anyString());
            failsafeLogger.getLogger().error("should get error");
        });
    }
}

