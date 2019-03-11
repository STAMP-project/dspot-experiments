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
package org.apache.geode.management.internal.cli.functions;


import UserFunctionExecution.ID;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.ResultCollector;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.geode.cache.query.RegionNotFoundException;
import org.apache.geode.internal.cache.InternalCacheForClientAccess;
import org.apache.geode.internal.security.SecurityService;
import org.apache.geode.security.AuthenticationRequiredException;
import org.apache.shiro.subject.Subject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class UserFunctionExecutionTest {
    private Object[] arguments;

    private Execution execution;

    private Function userFunction;

    private UserFunctionExecution function;

    private SecurityService securityService;

    private ResultCollector resultCollector;

    private FunctionContext<Object[]> context;

    private ResultSender<Object> resultSender;

    private InternalCacheForClientAccess filterCache;

    private ArgumentCaptor<CliFunctionResult> resultCaptor;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testDefaultAttributes() {
        assertThat(function.isHA()).isFalse();
        assertThat(function.getId()).isEqualTo(ID);
        assertThat(function.getRequiredPermissions(ArgumentMatchers.anyString())).isEmpty();
    }

    @Test
    public void parseArgumentsTest() {
        assertThat(function.parseArguments(null)).isNull();
        assertThat(function.parseArguments("")).isNull();
        assertThat(function.parseArguments("arg1,arg2")).isNotNull().isEqualTo(new String[]{ "arg1", "arg2" });
    }

    @Test
    public void parseFiltersTest() {
        assertThat(function.parseFilters(null)).isNotNull().isEmpty();
        assertThat(function.parseFilters("")).isNotNull().isEmpty();
        assertThat(function.parseFilters("key1,key2")).isNotNull().containsOnly("key1", "key2");
    }

    @Test
    public void buildExecutionShouldThrowExceptionWhenRegionIsRequiredButItDoesNotExist() throws Exception {
        Mockito.when(filterCache.getRegion("region")).thenReturn(null);
        Mockito.when(function.buildExecution(ArgumentMatchers.any(), ArgumentMatchers.any())).thenCallRealMethod();
        assertThatThrownBy(() -> function.buildExecution(filterCache, "region")).isInstanceOf(RegionNotFoundException.class);
    }

    @Test
    public void loginRequiredShouldReturnTrueWhenSubjectIsNull() {
        Mockito.when(securityService.getSubject()).thenReturn(null);
        Mockito.when(function.loginRequired(securityService)).thenCallRealMethod();
        assertThat(function.loginRequired(securityService)).isTrue();
    }

    @Test
    public void loginRequiredShouldReturnTrueWhenSubjectIsNotAuthenticated() {
        Subject subject = Mockito.mock(Subject.class);
        Mockito.when(subject.isAuthenticated()).thenReturn(false);
        Mockito.when(securityService.getSubject()).thenReturn(subject);
        Mockito.when(function.loginRequired(securityService)).thenCallRealMethod();
        assertThat(function.loginRequired(securityService)).isTrue();
    }

    @Test
    public void loginRequiredShouldReturnTrueWhenSecurityServiceFailsToLoadSubject() {
        Mockito.when(function.loginRequired(securityService)).thenCallRealMethod();
        Mockito.doThrow(new AuthenticationRequiredException("Dummy Exception")).when(securityService).getSubject();
        assertThat(function.loginRequired(securityService)).isTrue();
    }

    @Test
    public void loginRequiredShouldReturnFalseWhenSubjectIsAuthenticated() {
        Subject subject = Mockito.mock(Subject.class);
        Mockito.when(subject.isAuthenticated()).thenReturn(true);
        Mockito.when(securityService.getSubject()).thenReturn(subject);
        Mockito.when(function.loginRequired(securityService)).thenCallRealMethod();
        assertThat(function.loginRequired(securityService)).isFalse();
    }

    @Test
    public void executeShouldFailWhenNoArgumentsAreProvided() {
        Mockito.when(context.getArguments()).thenReturn(null);
        function.execute(context);
        Mockito.verify(resultSender, Mockito.times(1)).lastResult(resultCaptor.capture());
        CliFunctionResult result = resultCaptor.getValue();
        assertThat(result.isSuccessful()).isFalse();
        assertThat(result.getStatusMessage()).isEqualTo("Could not retrieve arguments");
    }

    @Test
    public void executeShouldFailWhenTargetFunctionCanNotBeLoaded() {
        Mockito.doReturn(null).when(function).loadFunction(ArgumentMatchers.anyString());
        function.execute(context);
        Mockito.verify(resultSender, Mockito.times(1)).lastResult(resultCaptor.capture());
        CliFunctionResult result = resultCaptor.getValue();
        assertThat(result.isSuccessful()).isFalse();
        assertThat(result.getStatusMessage()).isEqualTo("Function : TestFunction is not registered on member.");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void executeShouldFailWhenResultCollectorCanNotBeInstantiated() throws Exception {
        CliFunctionResult result;
        Mockito.doThrow(new ClassNotFoundException("ClassNotFoundException")).when(function).parseResultCollector(ArgumentMatchers.anyString());
        function.execute(context);
        Mockito.verify(resultSender, Mockito.times(1)).lastResult(resultCaptor.capture());
        result = resultCaptor.getValue();
        assertThat(result.isSuccessful()).isFalse();
        assertThat(result.getStatusMessage()).isEqualTo("ResultCollector : TestResultCollector not found. Error : ClassNotFoundException");
        Mockito.reset(resultSender);
        Mockito.doThrow(new IllegalAccessException("IllegalAccessException")).when(function).parseResultCollector(ArgumentMatchers.anyString());
        function.execute(context);
        Mockito.verify(resultSender, Mockito.times(1)).lastResult(resultCaptor.capture());
        result = resultCaptor.getValue();
        assertThat(result.isSuccessful()).isFalse();
        assertThat(result.getStatusMessage()).isEqualTo("ResultCollector : TestResultCollector not found. Error : IllegalAccessException");
        Mockito.reset(resultSender);
        Mockito.doThrow(new InstantiationException("InstantiationException")).when(function).parseResultCollector(ArgumentMatchers.anyString());
        function.execute(context);
        Mockito.verify(resultSender, Mockito.times(1)).lastResult(resultCaptor.capture());
        result = resultCaptor.getValue();
        assertThat(result.isSuccessful()).isFalse();
        assertThat(result.getStatusMessage()).isEqualTo("ResultCollector : TestResultCollector not found. Error : InstantiationException");
        Mockito.reset(resultSender);
    }

    @Test
    public void executeShouldFailWhenRegionIsSetAsArgumentButItDoesNotExist() throws Exception {
        Mockito.when(function.buildExecution(ArgumentMatchers.any(), ArgumentMatchers.any())).thenCallRealMethod();
        function.execute(context);
        Mockito.verify(resultSender, Mockito.times(1)).lastResult(resultCaptor.capture());
        CliFunctionResult result = resultCaptor.getValue();
        assertThat(result.isSuccessful()).isFalse();
        assertThat(result.getStatusMessage()).isEqualTo("/TestRegion does not exist");
    }

    @Test
    public void executeShouldFailWhenExecutorCanNotBeLoaded() throws Exception {
        Mockito.doReturn(null).when(function).buildExecution(ArgumentMatchers.any(), ArgumentMatchers.any());
        function.execute(context);
        Mockito.verify(resultSender, Mockito.times(1)).lastResult(resultCaptor.capture());
        CliFunctionResult result = resultCaptor.getValue();
        assertThat(result.isSuccessful()).isFalse();
        assertThat(result.getStatusMessage()).isEqualTo("While executing function : TestFunction on member : MockMemberId one region : /TestRegion error occurred : Could not retrieve executor");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void executeShouldProperlyConfigureExecutionContext() {
        Set<String> filter = new HashSet<>();
        filter.add("key1");
        filter.add("key2");
        arguments = new Object[]{ "TestFunction", "key1,key2", "TestResultCollector", "arg1,arg2", "/TestRegion", new Properties() };
        Mockito.when(context.getArguments()).thenReturn(arguments);
        function.execute(context);
        Mockito.verify(execution, Mockito.times(1)).withFilter(filter);
        Mockito.verify(execution, Mockito.times(1)).withCollector(resultCollector);
        Mockito.verify(execution, Mockito.times(1)).setArguments(new String[]{ "arg1", "arg2" });
        Mockito.verify(resultSender, Mockito.times(1)).lastResult(resultCaptor.capture());
        CliFunctionResult resultFullArguments = resultCaptor.getValue();
        assertThat(resultFullArguments.isSuccessful()).isTrue();
        Mockito.reset(resultSender);
        Mockito.reset(execution);
        arguments = new Object[]{ "TestFunction", "", "", "", "", new Properties() };
        Mockito.when(context.getArguments()).thenReturn(arguments);
        function.execute(context);
        Mockito.verify(execution, Mockito.never()).withFilter(ArgumentMatchers.any());
        Mockito.verify(execution, Mockito.never()).setArguments(ArgumentMatchers.any());
        Mockito.verify(execution, Mockito.never()).withCollector(ArgumentMatchers.any());
        Mockito.verify(resultSender, Mockito.times(1)).lastResult(resultCaptor.capture());
        CliFunctionResult resultNoArguments = resultCaptor.getValue();
        assertThat(resultNoArguments.isSuccessful()).isTrue();
    }

    @Test
    public void executeShouldWorkProperlyForFunctionsWithResults() {
        Mockito.when(userFunction.hasResult()).thenReturn(true);
        Mockito.doReturn(true).when(function).loginRequired(ArgumentMatchers.any());
        Mockito.when(resultCollector.getResult()).thenReturn(Arrays.asList("result1", "result2"));
        function.execute(context);
        Mockito.verify(resultSender, Mockito.times(1)).lastResult(resultCaptor.capture());
        CliFunctionResult result = resultCaptor.getValue();
        assertThat(result.isSuccessful()).isTrue();
        assertThat(result.getStatusMessage()).isEqualTo("[result1, result2]");
        Mockito.verify(securityService, Mockito.times(1)).login(ArgumentMatchers.any());
        Mockito.verify(securityService, Mockito.times(1)).logout();
    }

    @Test
    public void executeShouldWorkProperlyForFunctionsWithoutResults() {
        Mockito.when(userFunction.hasResult()).thenReturn(false);
        Mockito.doReturn(true).when(function).loginRequired(ArgumentMatchers.any());
        function.execute(context);
        Mockito.verify(resultSender, Mockito.times(1)).lastResult(resultCaptor.capture());
        CliFunctionResult result = resultCaptor.getValue();
        assertThat(result.isSuccessful()).isTrue();
        assertThat(result.getStatusMessage()).isEqualTo("[]");
        Mockito.verify(securityService, Mockito.times(1)).login(ArgumentMatchers.any());
        Mockito.verify(securityService, Mockito.times(1)).logout();
    }
}

