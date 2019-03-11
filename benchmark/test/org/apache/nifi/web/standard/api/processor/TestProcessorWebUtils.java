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
package org.apache.nifi.web.standard.api.processor;


import Response.ResponseBuilder;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;
import org.apache.nifi.web.ComponentDetails;
import org.apache.nifi.web.HttpServletConfigurationRequestContext;
import org.apache.nifi.web.HttpServletRequestContext;
import org.apache.nifi.web.NiFiWebConfigurationContext;
import org.apache.nifi.web.NiFiWebRequestContext;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestProcessorWebUtils {
    @Test
    public void testGetComponentDetailsForProcessor() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        NiFiWebConfigurationContext configurationContext = Mockito.mock(NiFiWebConfigurationContext.class);
        Mockito.when(configurationContext.getComponentDetails(ArgumentMatchers.any(HttpServletRequestContext.class))).thenReturn(new ComponentDetails.Builder().build());
        ComponentDetails componentDetails = ProcessorWebUtils.getComponentDetails(configurationContext, "1", request);
        Assert.assertNotNull(componentDetails);
    }

    @Test
    public void testGetComponentDetailsForProcessorWithSpecificClientRevision() {
        NiFiWebConfigurationContext configurationContext = Mockito.mock(NiFiWebConfigurationContext.class);
        Mockito.when(configurationContext.getComponentDetails(ArgumentMatchers.any(HttpServletConfigurationRequestContext.class))).thenReturn(new ComponentDetails.Builder().build());
        ComponentDetails componentDetails = ProcessorWebUtils.getComponentDetails(configurationContext, "1", Mockito.mock(HttpServletRequest.class));
        Assert.assertNotNull(componentDetails);
    }

    @Test
    public void testApplyCacheControl() {
        Response.ResponseBuilder response = Mockito.mock(ResponseBuilder.class);
        ProcessorWebUtils.applyCacheControl(response);
        Mockito.verify(response).cacheControl(ArgumentMatchers.any(CacheControl.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetRequestContextForProcessor() throws IOException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Method method = ProcessorWebUtils.class.getDeclaredMethod("getRequestContext", String.class, HttpServletRequest.class);
        method.setAccessible(true);
        NiFiWebRequestContext requestContext = ((NiFiWebRequestContext) (method.invoke(null, "1", Mockito.mock(HttpServletRequest.class))));
        Assert.assertTrue((requestContext instanceof HttpServletRequestContext));
        Assert.assertTrue(requestContext.getId().equals("1"));
    }
}

