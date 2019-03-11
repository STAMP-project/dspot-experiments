/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.management.internal.cli.commands;


import java.util.ArrayList;
import java.util.List;
import org.apache.geode.management.cli.Result;
import org.apache.geode.management.internal.cli.AbstractCliAroundInterceptor;
import org.apache.geode.management.internal.cli.GfshParseResult;
import org.apache.geode.test.junit.categories.GfshTest;
import org.apache.geode.test.junit.categories.LoggingTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;


@Category({ GfshTest.class, LoggingTest.class })
public class LogLevelInterceptorTest {
    private final List<AbstractCliAroundInterceptor> interceptors = new ArrayList<>();

    private GfshParseResult parseResult;

    private Result result;

    @Test
    public void testInvalidLogLevel() {
        Mockito.when(parseResult.getParamValueAsString("log-level")).thenReturn("test");
        Mockito.when(parseResult.getParamValueAsString("loglevel")).thenReturn("test");
        for (AbstractCliAroundInterceptor interceptor : interceptors) {
            result = ((Result) (interceptor.preExecution(parseResult)));
            assertThat(result.nextLine()).contains("Invalid log level: test");
        }
    }

    @Test
    public void testGeodeLogLevel() {
        Mockito.when(parseResult.getParamValueAsString("log-level")).thenReturn("fine");
        Mockito.when(parseResult.getParamValueAsString("loglevel")).thenReturn("fine");
        for (AbstractCliAroundInterceptor interceptor : interceptors) {
            result = ((Result) (interceptor.preExecution(parseResult)));
            assertThat(result.nextLine()).isEmpty();
        }
    }

    @Test
    public void testLog4JLevel() {
        Mockito.when(parseResult.getParamValueAsString("log-level")).thenReturn("trace");
        Mockito.when(parseResult.getParamValueAsString("loglevel")).thenReturn("trace");
        for (AbstractCliAroundInterceptor interceptor : interceptors) {
            result = ((Result) (interceptor.preExecution(parseResult)));
            assertThat(result.nextLine()).isEmpty();
        }
    }
}

