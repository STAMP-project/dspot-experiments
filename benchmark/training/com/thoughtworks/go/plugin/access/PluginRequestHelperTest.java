/**
 * Copyright 2018 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.plugin.access;


import DefaultGoApiResponse.INTERNAL_ERROR;
import DefaultGoApiResponse.SUCCESS_RESPONSE_CODE;
import DefaultGoApiResponse.VALIDATION_ERROR;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.thoughtworks.go.plugin.infra.PluginManager;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class PluginRequestHelperTest {
    private PluginManager pluginManager;

    private PluginRequestHelper helper;

    private boolean[] isSuccessInvoked;

    private String pluginId = "pid";

    private GoPluginApiResponse response;

    private final String requestName = "req";

    private final String extensionName = "some-extension";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldNotInvokeSuccessBlockOnFailureResponse() {
        Mockito.when(response.responseCode()).thenReturn(INTERNAL_ERROR);
        Mockito.when(response.responseBody()).thenReturn("junk");
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(pluginId), ArgumentMatchers.eq(extensionName), ArgumentMatchers.any(GoPluginApiRequest.class))).thenReturn(response);
        try {
            helper.submitRequest(pluginId, requestName, new DefaultPluginInteractionCallback<Object>() {
                @Override
                public Object onSuccess(String responseBody, Map<String, String> responseHeaders, String resolvedExtensionVersion) {
                    isSuccessInvoked[0] = true;
                    return null;
                }
            });
            Assert.fail("should throw exception");
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), Matchers.is("Interaction with plugin with id 'pid' implementing 'some-extension' extension failed while requesting for 'req'. Reason: [The plugin sent a response that could not be understood by Go. Plugin returned with code '500' and the following response: 'junk']"));
            Assert.assertThat(e.getCause().getMessage(), Matchers.is("The plugin sent a response that could not be understood by Go. Plugin returned with code '500' and the following response: 'junk'"));
            Assert.assertFalse(isSuccessInvoked[0]);
            Mockito.verify(pluginManager).submitTo(ArgumentMatchers.eq(pluginId), ArgumentMatchers.eq(extensionName), ArgumentMatchers.any(GoPluginApiRequest.class));
        }
    }

    @Test
    public void shouldInvokeSuccessBlockOnSuccessfulResponse() {
        Mockito.when(response.responseCode()).thenReturn(SUCCESS_RESPONSE_CODE);
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(pluginId), ArgumentMatchers.eq(extensionName), ArgumentMatchers.any(GoPluginApiRequest.class))).thenReturn(response);
        helper.submitRequest(pluginId, requestName, new DefaultPluginInteractionCallback<Object>() {
            @Override
            public Object onSuccess(String responseBody, Map<String, String> responseHeaders, String resolvedExtensionVersion) {
                isSuccessInvoked[0] = true;
                return null;
            }
        });
        Assert.assertTrue(isSuccessInvoked[0]);
        Mockito.verify(pluginManager).submitTo(ArgumentMatchers.eq(pluginId), ArgumentMatchers.eq(extensionName), ArgumentMatchers.any(GoPluginApiRequest.class));
    }

    @Test
    public void shouldErrorOutOnValidationFailure() {
        Mockito.when(response.responseCode()).thenReturn(VALIDATION_ERROR);
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(pluginId), ArgumentMatchers.eq(extensionName), ArgumentMatchers.any(GoPluginApiRequest.class))).thenReturn(response);
        thrown.expect(RuntimeException.class);
        helper.submitRequest(pluginId, requestName, new DefaultPluginInteractionCallback<Object>() {
            @Override
            public Object onSuccess(String responseBody, Map<String, String> responseHeaders, String resolvedExtensionVersion) {
                isSuccessInvoked[0] = true;
                return null;
            }
        });
    }

    @Test
    public void shouldConstructTheRequest() {
        final String requestBody = "request_body";
        Mockito.when(response.responseCode()).thenReturn(SUCCESS_RESPONSE_CODE);
        final GoPluginApiRequest[] generatedRequest = new GoPluginApiRequest[]{ null };
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                generatedRequest[0] = ((GoPluginApiRequest) (invocationOnMock.getArguments()[2]));
                return response;
            }
        }).when(pluginManager).submitTo(ArgumentMatchers.eq(pluginId), ArgumentMatchers.eq(extensionName), ArgumentMatchers.any(GoPluginApiRequest.class));
        helper.submitRequest(pluginId, requestName, new DefaultPluginInteractionCallback<Object>() {
            @Override
            public String requestBody(String resolvedExtensionVersion) {
                return requestBody;
            }
        });
        Assert.assertThat(generatedRequest[0].requestBody(), Matchers.is(requestBody));
        Assert.assertThat(generatedRequest[0].extension(), Matchers.is(extensionName));
        Assert.assertThat(generatedRequest[0].requestName(), Matchers.is(requestName));
        Assert.assertTrue(generatedRequest[0].requestParameters().isEmpty());
    }

    @Test
    public void shouldConstructTheRequestWithRequestParams() {
        final String requestBody = "request_body";
        Mockito.when(response.responseCode()).thenReturn(SUCCESS_RESPONSE_CODE);
        final GoPluginApiRequest[] generatedRequest = new GoPluginApiRequest[]{ null };
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                generatedRequest[0] = ((GoPluginApiRequest) (invocationOnMock.getArguments()[2]));
                return response;
            }
        }).when(pluginManager).submitTo(ArgumentMatchers.eq(pluginId), ArgumentMatchers.eq(extensionName), ArgumentMatchers.any(GoPluginApiRequest.class));
        helper.submitRequest(pluginId, requestName, new PluginInteractionCallback<Object>() {
            @Override
            public String requestBody(String resolvedExtensionVersion) {
                return requestBody;
            }

            @Override
            public Map<String, String> requestParams(String resolvedExtensionVersion) {
                final HashMap params = new HashMap();
                params.put("p1", "v1");
                params.put("p2", "v2");
                return params;
            }

            @Override
            public Map<String, String> requestHeaders(String resolvedExtensionVersion) {
                return null;
            }

            @Override
            public Object onSuccess(String responseBody, Map<String, String> responseHeaders, String resolvedExtensionVersion) {
                return null;
            }
        });
        Assert.assertThat(generatedRequest[0].requestBody(), Matchers.is(requestBody));
        Assert.assertThat(generatedRequest[0].extension(), Matchers.is(extensionName));
        Assert.assertThat(generatedRequest[0].requestName(), Matchers.is(requestName));
        Assert.assertThat(generatedRequest[0].requestParameters().size(), Matchers.is(2));
        Assert.assertThat(generatedRequest[0].requestParameters().get("p1"), Matchers.is("v1"));
        Assert.assertThat(generatedRequest[0].requestParameters().get("p2"), Matchers.is("v2"));
    }

    @Test
    public void shouldConstructTheRequestWithRequestHeaders() {
        final String requestBody = "request_body";
        Mockito.when(response.responseCode()).thenReturn(SUCCESS_RESPONSE_CODE);
        final GoPluginApiRequest[] generatedRequest = new GoPluginApiRequest[]{ null };
        Mockito.doAnswer(( invocationOnMock) -> {
            generatedRequest[0] = ((GoPluginApiRequest) (invocationOnMock.getArguments()[2]));
            return response;
        }).when(pluginManager).submitTo(ArgumentMatchers.eq(pluginId), ArgumentMatchers.eq(extensionName), ArgumentMatchers.any(GoPluginApiRequest.class));
        helper.submitRequest(pluginId, requestName, new PluginInteractionCallback<Object>() {
            @Override
            public String requestBody(String resolvedExtensionVersion) {
                return requestBody;
            }

            @Override
            public Map<String, String> requestParams(String resolvedExtensionVersion) {
                return null;
            }

            @Override
            public Map<String, String> requestHeaders(String resolvedExtensionVersion) {
                final Map<String, String> headers = new HashMap();
                headers.put("HEADER-1", "HEADER-VALUE-1");
                headers.put("HEADER-2", "HEADER-VALUE-2");
                return headers;
            }

            @Override
            public Object onSuccess(String responseBody, Map<String, String> responseHeaders, String resolvedExtensionVersion) {
                return null;
            }
        });
        Assert.assertThat(generatedRequest[0].requestBody(), Matchers.is(requestBody));
        Assert.assertThat(generatedRequest[0].extension(), Matchers.is(extensionName));
        Assert.assertThat(generatedRequest[0].requestName(), Matchers.is(requestName));
        Assert.assertThat(generatedRequest[0].requestHeaders().size(), Matchers.is(2));
        Assert.assertThat(generatedRequest[0].requestHeaders().get("HEADER-1"), Matchers.is("HEADER-VALUE-1"));
        Assert.assertThat(generatedRequest[0].requestHeaders().get("HEADER-2"), Matchers.is("HEADER-VALUE-2"));
    }
}

