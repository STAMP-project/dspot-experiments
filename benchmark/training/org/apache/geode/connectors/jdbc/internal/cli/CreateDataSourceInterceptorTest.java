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
package org.apache.geode.connectors.jdbc.internal.cli;


import CreateDataSourceCommand.POOLED;
import CreateDataSourceCommand.POOLED_DATA_SOURCE_FACTORY_CLASS;
import CreateDataSourceCommand.POOL_PROPERTIES;
import CreateDataSourceInterceptor.POOLED_DATA_SOURCE_FACTORY_CLASS_ONLY_VALID_ON_POOLED_DATA_SOURCE;
import CreateDataSourceInterceptor.POOL_PROPERTIES_ONLY_VALID_ON_POOLED_DATA_SOURCE;
import Status.ERROR;
import Status.OK;
import org.apache.geode.management.internal.cli.GfshParseResult;
import org.apache.geode.management.internal.cli.result.model.ResultModel;
import org.apache.geode.management.internal.cli.shell.Gfsh;
import org.junit.Test;
import org.mockito.Mockito;


public class CreateDataSourceInterceptorTest {
    private Gfsh gfsh;

    private GfshParseResult parseResult;

    private CreateDataSourceInterceptor interceptor;

    @Test
    public void defaultsAreOk() {
        ResultModel result = interceptor.preExecution(parseResult);
        assertThat(result.getStatus()).isEqualTo(OK);
    }

    @Test
    public void nonPooledIsOk() {
        Mockito.when(parseResult.getParamValueAsString(POOLED)).thenReturn("false");
        ResultModel result = interceptor.preExecution(parseResult);
        assertThat(result.getStatus()).isEqualTo(OK);
    }

    @Test
    public void nonPooledWithEmptyPoolPropertiesIsOk() {
        Mockito.when(parseResult.getParamValueAsString(POOLED)).thenReturn("false");
        Mockito.when(parseResult.getParamValueAsString(POOL_PROPERTIES)).thenReturn("");
        ResultModel result = interceptor.preExecution(parseResult);
        assertThat(result.getStatus()).isEqualTo(OK);
    }

    @Test
    public void nonPooledWithEmptyPoolFactoryIsOk() {
        Mockito.when(parseResult.getParamValueAsString(POOLED)).thenReturn("false");
        Mockito.when(parseResult.getParamValueAsString(POOLED_DATA_SOURCE_FACTORY_CLASS)).thenReturn("");
        ResultModel result = interceptor.preExecution(parseResult);
        assertThat(result.getStatus()).isEqualTo(OK);
    }

    @Test
    public void pooledIsOk() {
        Mockito.when(parseResult.getParamValueAsString(POOLED)).thenReturn("true");
        ResultModel result = interceptor.preExecution(parseResult);
        assertThat(result.getStatus()).isEqualTo(OK);
    }

    @Test
    public void pooledWithPoolPropertiesIsOk() {
        Mockito.when(parseResult.getParamValueAsString(POOLED)).thenReturn("true");
        Mockito.when(parseResult.getParamValueAsString(POOL_PROPERTIES)).thenReturn("pool properties value");
        ResultModel result = interceptor.preExecution(parseResult);
        assertThat(result.getStatus()).isEqualTo(OK);
    }

    @Test
    public void pooledWithPoolFactoryIsOk() {
        Mockito.when(parseResult.getParamValueAsString(POOLED)).thenReturn("true");
        Mockito.when(parseResult.getParamValueAsString(POOLED_DATA_SOURCE_FACTORY_CLASS)).thenReturn("pool factory value");
        ResultModel result = interceptor.preExecution(parseResult);
        assertThat(result.getStatus()).isEqualTo(OK);
    }

    @Test
    public void nonPooledWithPoolPropertiesIsError() {
        Mockito.when(parseResult.getParamValueAsString(POOLED)).thenReturn("false");
        Mockito.when(parseResult.getParamValueAsString(POOL_PROPERTIES)).thenReturn("pool properties value");
        ResultModel result = interceptor.preExecution(parseResult);
        assertThat(result.getStatus()).isEqualTo(ERROR);
        assertThat(result.getInfoSection("info").getContent().get(0)).contains(POOL_PROPERTIES_ONLY_VALID_ON_POOLED_DATA_SOURCE);
    }

    @Test
    public void nonPooledWithPoolFactoryIsError() {
        Mockito.when(parseResult.getParamValueAsString(POOLED)).thenReturn("false");
        Mockito.when(parseResult.getParamValueAsString(POOLED_DATA_SOURCE_FACTORY_CLASS)).thenReturn("pool factory value");
        ResultModel result = interceptor.preExecution(parseResult);
        assertThat(result.getStatus()).isEqualTo(ERROR);
        assertThat(result.getInfoSection("info").getContent().get(0)).contains(POOLED_DATA_SOURCE_FACTORY_CLASS_ONLY_VALID_ON_POOLED_DATA_SOURCE);
    }
}

