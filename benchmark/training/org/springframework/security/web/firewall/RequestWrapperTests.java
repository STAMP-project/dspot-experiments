/**
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.web.firewall;


import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;


/**
 *
 *
 * @author Luke Taylor
 */
public class RequestWrapperTests {
    private static Map<String, String> testPaths = new LinkedHashMap<>();

    @Test
    public void pathParametersAreRemovedFromServletPath() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        for (Map.Entry<String, String> entry : RequestWrapperTests.testPaths.entrySet()) {
            String path = entry.getKey();
            String expectedResult = entry.getValue();
            request.setServletPath(path);
            RequestWrapper wrapper = new RequestWrapper(request);
            assertThat(wrapper.getServletPath()).isEqualTo(expectedResult);
            wrapper.reset();
            assertThat(wrapper.getServletPath()).isEqualTo(path);
        }
    }

    @Test
    public void pathParametersAreRemovedFromPathInfo() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        for (Map.Entry<String, String> entry : RequestWrapperTests.testPaths.entrySet()) {
            String path = entry.getKey();
            String expectedResult = entry.getValue();
            // Should be null when stripped value is empty
            if ((expectedResult.length()) == 0) {
                expectedResult = null;
            }
            request.setPathInfo(path);
            RequestWrapper wrapper = new RequestWrapper(request);
            assertThat(wrapper.getPathInfo()).isEqualTo(expectedResult);
            wrapper.reset();
            assertThat(wrapper.getPathInfo()).isEqualTo(path);
        }
    }

    @Test
    public void resetWhenForward() throws Exception {
        String denormalizedPath = RequestWrapperTests.testPaths.keySet().iterator().next();
        String forwardPath = "/forward/path";
        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = Mockito.mock(HttpServletResponse.class);
        RequestDispatcher mockDispatcher = Mockito.mock(RequestDispatcher.class);
        Mockito.when(mockRequest.getServletPath()).thenReturn("");
        Mockito.when(mockRequest.getPathInfo()).thenReturn(denormalizedPath);
        Mockito.when(mockRequest.getRequestDispatcher(forwardPath)).thenReturn(mockDispatcher);
        RequestWrapper wrapper = new RequestWrapper(mockRequest);
        RequestDispatcher dispatcher = wrapper.getRequestDispatcher(forwardPath);
        dispatcher.forward(mockRequest, mockResponse);
        Mockito.verify(mockRequest).getRequestDispatcher(forwardPath);
        Mockito.verify(mockDispatcher).forward(mockRequest, mockResponse);
        assertThat(wrapper.getPathInfo()).isEqualTo(denormalizedPath);
        Mockito.verify(mockRequest, Mockito.times(2)).getPathInfo();
        // validate wrapper.getServletPath() delegates to the mock
        wrapper.getServletPath();
        Mockito.verify(mockRequest, Mockito.times(2)).getServletPath();
        Mockito.verifyNoMoreInteractions(mockRequest, mockResponse, mockDispatcher);
    }

    @Test
    public void requestDispatcherNotWrappedAfterReset() {
        String path = "/forward/path";
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        RequestDispatcher dispatcher = Mockito.mock(RequestDispatcher.class);
        Mockito.when(request.getRequestDispatcher(path)).thenReturn(dispatcher);
        RequestWrapper wrapper = new RequestWrapper(request);
        wrapper.reset();
        assertThat(wrapper.getRequestDispatcher(path)).isSameAs(dispatcher);
    }
}

