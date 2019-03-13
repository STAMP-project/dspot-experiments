/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.servicenow;


import ComponentVerifierExtension.Result;
import ComponentVerifierExtension.Result.Status.ERROR;
import ComponentVerifierExtension.Result.Status.OK;
import ComponentVerifierExtension.Scope.CONNECTIVITY;
import ComponentVerifierExtension.Scope.PARAMETERS;
import ComponentVerifierExtension.VerificationError.ExceptionAttribute.EXCEPTION_INSTANCE;
import ComponentVerifierExtension.VerificationError.HttpAttribute.HTTP_CODE;
import ComponentVerifierExtension.VerificationError.StandardCode.AUTHENTICATION;
import ComponentVerifierExtension.VerificationError.StandardCode.EXCEPTION;
import ComponentVerifierExtension.VerificationError.StandardCode.MISSING_PARAMETER;
import java.util.Map;
import javax.ws.rs.ProcessingException;
import org.apache.camel.component.extension.ComponentVerifierExtension;
import org.junit.Assert;
import org.junit.Test;


public class ServiceNowComponentVerifierExtensionTest extends ServiceNowTestSupport {
    public ServiceNowComponentVerifierExtensionTest() {
        super(false);
    }

    // *********************************
    // Parameters validation
    // *********************************
    @Test
    public void testParameter() {
        Map<String, Object> parameters = getParameters();
        ComponentVerifierExtension.Result result = getExtension().verify(PARAMETERS, parameters);
        Assert.assertEquals(OK, result.getStatus());
    }

    @Test
    public void testMissingMandatoryParameter() {
        Map<String, Object> parameters = getParameters();
        parameters.remove("instanceName");
        ComponentVerifierExtension.Result result = getExtension().verify(PARAMETERS, parameters);
        Assert.assertEquals(ERROR, result.getStatus());
        Assert.assertEquals(1, result.getErrors().size());
        Assert.assertEquals(MISSING_PARAMETER, result.getErrors().get(0).getCode());
        Assert.assertEquals("instanceName", result.getErrors().get(0).getParameterKeys().iterator().next());
    }

    @Test
    public void testMissingMandatoryAuthenticationParameter() {
        Map<String, Object> parameters = getParameters();
        parameters.remove("userName");
        ComponentVerifierExtension.Result result = getExtension().verify(PARAMETERS, parameters);
        Assert.assertEquals(ERROR, result.getStatus());
        Assert.assertEquals(1, result.getErrors().size());
        Assert.assertEquals(MISSING_PARAMETER, result.getErrors().get(0).getCode());
        Assert.assertEquals("userName", result.getErrors().get(0).getParameterKeys().iterator().next());
    }

    // *********************************
    // Connectivity validation
    // *********************************
    @Test
    public void testConnectivity() {
        Map<String, Object> parameters = getParameters();
        ComponentVerifierExtension.Result result = getExtension().verify(CONNECTIVITY, parameters);
        Assert.assertEquals(OK, result.getStatus());
    }

    @Test
    public void testConnectivityOnCustomTable() {
        Map<String, Object> parameters = getParameters();
        parameters.put("table", "ticket");
        ComponentVerifierExtension.Result result = getExtension().verify(CONNECTIVITY, parameters);
        Assert.assertEquals(OK, result.getStatus());
    }

    @Test
    public void testConnectivityWithWrongInstance() {
        Map<String, Object> parameters = getParameters();
        parameters.put("instanceName", "unknown-instance");
        ComponentVerifierExtension.Result result = getExtension().verify(CONNECTIVITY, parameters);
        Assert.assertEquals(ERROR, result.getStatus());
        Assert.assertEquals(1, result.getErrors().size());
        Assert.assertEquals(EXCEPTION, result.getErrors().get(0).getCode());
        Assert.assertNotNull(result.getErrors().get(0).getDetails().get(EXCEPTION_INSTANCE));
        Assert.assertTrue(((result.getErrors().get(0).getDetails().get(EXCEPTION_INSTANCE)) instanceof ProcessingException));
    }

    @Test
    public void testConnectivityWithWrongTable() {
        Map<String, Object> parameters = getParameters();
        parameters.put("table", "unknown");
        ComponentVerifierExtension.Result result = getExtension().verify(CONNECTIVITY, parameters);
        Assert.assertEquals(ERROR, result.getStatus());
        Assert.assertEquals(1, result.getErrors().size());
        Assert.assertEquals(EXCEPTION, result.getErrors().get(0).getCode());
        Assert.assertNotNull(result.getErrors().get(0).getDetails().get(EXCEPTION_INSTANCE));
        Assert.assertEquals(400, result.getErrors().get(0).getDetails().get(HTTP_CODE));
        Assert.assertTrue(((result.getErrors().get(0).getDetails().get(EXCEPTION_INSTANCE)) instanceof ServiceNowException));
    }

    @Test
    public void testConnectivityWithWrongAuthentication() {
        Map<String, Object> parameters = getParameters();
        parameters.put("userName", "unknown-user");
        parameters.remove("oauthClientId");
        parameters.remove("oauthClientSecret");
        ComponentVerifierExtension.Result result = getExtension().verify(CONNECTIVITY, parameters);
        Assert.assertEquals(ERROR, result.getStatus());
        Assert.assertEquals(1, result.getErrors().size());
        Assert.assertEquals(AUTHENTICATION, result.getErrors().get(0).getCode());
        Assert.assertNotNull(result.getErrors().get(0).getDetails().get(EXCEPTION_INSTANCE));
        Assert.assertEquals(401, result.getErrors().get(0).getDetails().get(HTTP_CODE));
        Assert.assertTrue(((result.getErrors().get(0).getDetails().get(EXCEPTION_INSTANCE)) instanceof ServiceNowException));
        Assert.assertTrue(result.getErrors().get(0).getParameterKeys().contains("userName"));
        Assert.assertTrue(result.getErrors().get(0).getParameterKeys().contains("password"));
        Assert.assertTrue(result.getErrors().get(0).getParameterKeys().contains("oauthClientId"));
        Assert.assertTrue(result.getErrors().get(0).getParameterKeys().contains("oauthClientSecret"));
    }
}

