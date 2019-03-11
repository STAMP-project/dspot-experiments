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


import org.apache.geode.management.cli.Result;
import org.apache.geode.management.internal.cli.GfshParseResult;
import org.apache.geode.test.junit.categories.GfshTest;
import org.apache.geode.test.junit.categories.LoggingTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;


@Category({ GfshTest.class, LoggingTest.class })
public class ExportLogsInterceptorTest {
    private ExportLogsInterceptor interceptor;

    private GfshParseResult parseResult;

    private Result result;

    @Test
    public void testGroupAndMember() {
        Mockito.when(parseResult.getParamValueAsString("group")).thenReturn("group");
        Mockito.when(parseResult.getParamValueAsString("member")).thenReturn("group");
        result = interceptor.preExecution(parseResult);
        assertThat(result.nextLine()).contains("Can't specify both group and member");
    }

    @Test
    public void testStartEnd() {
        Mockito.when(parseResult.getParamValueAsString("start-time")).thenReturn("2000/01/01");
        Mockito.when(parseResult.getParamValueAsString("end-time")).thenReturn("2000/01/02");
        result = interceptor.preExecution(parseResult);
        assertThat(result.nextLine()).isEmpty();
        Mockito.when(parseResult.getParamValueAsString("start-time")).thenReturn("2000/01/02");
        Mockito.when(parseResult.getParamValueAsString("end-time")).thenReturn("2000/01/01");
        result = interceptor.preExecution(parseResult);
        assertThat(result.nextLine()).contains("start-time has to be earlier than end-time");
    }

    @Test
    public void testInclideStats() {
        Mockito.when(parseResult.getParamValue("logs-only")).thenReturn(true);
        Mockito.when(parseResult.getParamValue("stats-only")).thenReturn(false);
        result = interceptor.preExecution(parseResult);
        assertThat(result.nextLine()).isEmpty();
        Mockito.when(parseResult.getParamValue("logs-only")).thenReturn(true);
        Mockito.when(parseResult.getParamValue("stats-only")).thenReturn(true);
        result = interceptor.preExecution(parseResult);
        assertThat(result.nextLine()).contains("logs-only and stats-only can't both be true");
    }
}

