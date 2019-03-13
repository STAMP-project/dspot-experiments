/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zeppelin.server;


import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * Basic CORS REST API tests.
 */
public class CorsFilterTest {
    public static String[] headers = new String[8];

    public static Integer count = 0;

    @Test
    @SuppressWarnings("rawtypes")
    public void validCorsFilterTest() throws IOException, ServletException {
        CorsFilter filter = new CorsFilter();
        HttpServletResponse mockResponse = Mockito.mock(HttpServletResponse.class);
        FilterChain mockedFilterChain = Mockito.mock(FilterChain.class);
        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getHeader("Origin")).thenReturn("http://localhost:8080");
        Mockito.when(mockRequest.getMethod()).thenReturn("Empty");
        Mockito.when(mockRequest.getServerName()).thenReturn("localhost");
        CorsFilterTest.count = 0;
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                CorsFilterTest.headers[CorsFilterTest.count] = invocationOnMock.getArguments()[1].toString();
                (CorsFilterTest.count)++;
                return null;
            }
        }).when(mockResponse).setHeader(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        filter.doFilter(mockRequest, mockResponse, mockedFilterChain);
        Assert.assertTrue(CorsFilterTest.headers[0].equals("http://localhost:8080"));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void invalidCorsFilterTest() throws IOException, ServletException {
        CorsFilter filter = new CorsFilter();
        HttpServletResponse mockResponse = Mockito.mock(HttpServletResponse.class);
        FilterChain mockedFilterChain = Mockito.mock(FilterChain.class);
        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getHeader("Origin")).thenReturn("http://evillocalhost:8080");
        Mockito.when(mockRequest.getMethod()).thenReturn("Empty");
        Mockito.when(mockRequest.getServerName()).thenReturn("evillocalhost");
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                CorsFilterTest.headers[CorsFilterTest.count] = invocationOnMock.getArguments()[1].toString();
                (CorsFilterTest.count)++;
                return null;
            }
        }).when(mockResponse).setHeader(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        filter.doFilter(mockRequest, mockResponse, mockedFilterChain);
        Assert.assertTrue(CorsFilterTest.headers[0].equals(""));
    }
}

