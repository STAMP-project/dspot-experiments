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
package org.apache.nifi.processor;


import LogLevel.ERROR;
import LogLevel.INFO;
import LogLevel.TRACE;
import LogLevel.WARN;
import org.apache.nifi.reporting.ReportingTask;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.internal.matchers.VarargMatcher;
import org.slf4j.Logger;


public class TestSimpleProcessLogger {
    private final Exception e = new RuntimeException("intentional");

    private ReportingTask task;

    private SimpleProcessLogger componentLog;

    private Logger logger;

    @Test
    public void validateDelegateLoggerReceivesThrowableToStringOnError() {
        componentLog.error("Hello {}", e);
        Mockito.verify(logger, Mockito.times(1)).error(ArgumentMatchers.anyString(), ArgumentMatchers.argThat(new TestSimpleProcessLogger.MyVarargMatcher()));
    }

    @Test
    public void validateDelegateLoggerReceivesThrowableToStringOnInfo() {
        componentLog.info("Hello {}", e);
        Mockito.verify(logger, Mockito.times(1)).info(ArgumentMatchers.anyString(), ArgumentMatchers.argThat(new TestSimpleProcessLogger.MyVarargMatcher()));
    }

    @Test
    public void validateDelegateLoggerReceivesThrowableToStringOnTrace() {
        componentLog.trace("Hello {}", e);
        Mockito.verify(logger, Mockito.times(1)).trace(ArgumentMatchers.anyString(), ArgumentMatchers.argThat(new TestSimpleProcessLogger.MyVarargMatcher()));
    }

    @Test
    public void validateDelegateLoggerReceivesThrowableToStringOnWarn() {
        componentLog.warn("Hello {}", e);
        Mockito.verify(logger, Mockito.times(1)).warn(ArgumentMatchers.anyString(), ArgumentMatchers.argThat(new TestSimpleProcessLogger.MyVarargMatcher()));
    }

    @Test
    public void validateDelegateLoggerReceivesThrowableToStringOnLogWithLevel() {
        componentLog.log(WARN, "Hello {}", e);
        Mockito.verify(logger, Mockito.times(1)).warn(ArgumentMatchers.anyString(), ArgumentMatchers.argThat(new TestSimpleProcessLogger.MyVarargMatcher()));
        componentLog.log(ERROR, "Hello {}", e);
        Mockito.verify(logger, Mockito.times(1)).error(ArgumentMatchers.anyString(), ArgumentMatchers.argThat(new TestSimpleProcessLogger.MyVarargMatcher()));
        componentLog.log(INFO, "Hello {}", e);
        Mockito.verify(logger, Mockito.times(1)).info(ArgumentMatchers.anyString(), ArgumentMatchers.argThat(new TestSimpleProcessLogger.MyVarargMatcher()));
        componentLog.log(TRACE, "Hello {}", e);
        Mockito.verify(logger, Mockito.times(1)).trace(ArgumentMatchers.anyString(), ArgumentMatchers.argThat(new TestSimpleProcessLogger.MyVarargMatcher()));
    }

    /**
     *
     */
    private class MyVarargMatcher extends ArgumentMatcher<Object[]> implements VarargMatcher {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean matches(Object argument) {
            Object[] args = ((Object[]) (argument));
            Assert.assertEquals(task, args[0]);
            Assert.assertEquals(e.toString(), args[1]);
            return true;
        }
    }
}

