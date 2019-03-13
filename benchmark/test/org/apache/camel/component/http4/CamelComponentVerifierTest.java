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
package org.apache.camel.component.http4;


import ComponentVerifierExtension.Result;
import ComponentVerifierExtension.Result.Status.ERROR;
import ComponentVerifierExtension.Result.Status.OK;
import ComponentVerifierExtension.Scope.CONNECTIVITY;
import ComponentVerifierExtension.Scope.PARAMETERS;
import ComponentVerifierExtension.VerificationError;
import ComponentVerifierExtension.VerificationError.HttpAttribute.HTTP_CODE;
import ComponentVerifierExtension.VerificationError.HttpAttribute.HTTP_REDIRECT;
import ComponentVerifierExtension.VerificationError.StandardCode.AUTHENTICATION;
import ComponentVerifierExtension.VerificationError.StandardCode.EXCEPTION;
import ComponentVerifierExtension.VerificationError.StandardCode.GENERIC;
import ComponentVerifierExtension.VerificationError.StandardCode.MISSING_PARAMETER;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.component.extension.ComponentVerifierExtension;
import org.apache.http.impl.bootstrap.HttpServer;
import org.junit.Assert;
import org.junit.Test;


public class CamelComponentVerifierTest extends BaseHttpTest {
    private static final String AUTH_USERNAME = "camel";

    private static final String AUTH_PASSWORD = "password";

    private HttpServer localServer;

    // *************************************************
    // Tests (parameters)
    // *************************************************
    @Test
    public void testParameters() throws Exception {
        HttpComponent component = context().getComponent("http4", HttpComponent.class);
        ComponentVerifierExtension verifier = component.getVerifier();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("httpUri", getLocalServerUri("/basic"));
        ComponentVerifierExtension.Result result = verifier.verify(PARAMETERS, parameters);
        Assert.assertEquals(OK, result.getStatus());
    }

    @Test
    public void testMissingMandatoryParameters() throws Exception {
        HttpComponent component = context().getComponent("http4", HttpComponent.class);
        ComponentVerifierExtension verifier = component.getVerifier();
        Map<String, Object> parameters = new HashMap<>();
        ComponentVerifierExtension.Result result = verifier.verify(PARAMETERS, parameters);
        Assert.assertEquals(ERROR, result.getStatus());
        Assert.assertEquals(1, result.getErrors().size());
        ComponentVerifierExtension.VerificationError error = result.getErrors().get(0);
        Assert.assertEquals(MISSING_PARAMETER, error.getCode());
        Assert.assertTrue(error.getParameterKeys().contains("httpUri"));
    }

    // *************************************************
    // Tests (connectivity)
    // *************************************************
    @Test
    public void testConnectivity() throws Exception {
        HttpComponent component = context().getComponent("http4", HttpComponent.class);
        ComponentVerifierExtension verifier = component.getVerifier();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("httpUri", getLocalServerUri("/basic"));
        ComponentVerifierExtension.Result result = verifier.verify(CONNECTIVITY, parameters);
        Assert.assertEquals(OK, result.getStatus());
    }

    @Test
    public void testConnectivityWithWrongUri() throws Exception {
        HttpComponent component = context().getComponent("http4", HttpComponent.class);
        ComponentVerifierExtension verifier = component.getVerifier();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("httpUri", "http://www.not-existing-uri.unknown");
        ComponentVerifierExtension.Result result = verifier.verify(CONNECTIVITY, parameters);
        Assert.assertEquals(ERROR, result.getStatus());
        Assert.assertEquals(1, result.getErrors().size());
        ComponentVerifierExtension.VerificationError error = result.getErrors().get(0);
        Assert.assertEquals(EXCEPTION, error.getCode());
        Assert.assertTrue(error.getParameterKeys().contains("httpUri"));
    }

    @Test
    public void testConnectivityWithAuthentication() throws Exception {
        HttpComponent component = context().getComponent("http4", HttpComponent.class);
        ComponentVerifierExtension verifier = component.getVerifier();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("httpUri", getLocalServerUri("/auth"));
        parameters.put("authUsername", CamelComponentVerifierTest.AUTH_USERNAME);
        parameters.put("authPassword", CamelComponentVerifierTest.AUTH_PASSWORD);
        ComponentVerifierExtension.Result result = verifier.verify(CONNECTIVITY, parameters);
        Assert.assertEquals(OK, result.getStatus());
    }

    @Test
    public void testConnectivityWithWrongAuthenticationData() throws Exception {
        HttpComponent component = context().getComponent("http4", HttpComponent.class);
        ComponentVerifierExtension verifier = component.getVerifier();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("httpUri", getLocalServerUri("/auth"));
        parameters.put("authUsername", "unknown");
        parameters.put("authPassword", CamelComponentVerifierTest.AUTH_PASSWORD);
        ComponentVerifierExtension.Result result = verifier.verify(CONNECTIVITY, parameters);
        Assert.assertEquals(ERROR, result.getStatus());
        Assert.assertEquals(1, result.getErrors().size());
        ComponentVerifierExtension.VerificationError error = result.getErrors().get(0);
        Assert.assertEquals(AUTHENTICATION, error.getCode());
        Assert.assertEquals(401, error.getDetails().get(HTTP_CODE));
        Assert.assertTrue(error.getParameterKeys().contains("authUsername"));
        Assert.assertTrue(error.getParameterKeys().contains("authPassword"));
    }

    @Test
    public void testConnectivityWithRedirect() throws Exception {
        HttpComponent component = context().getComponent("http4", HttpComponent.class);
        ComponentVerifierExtension verifier = component.getVerifier();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("httpUri", getLocalServerUri("/redirect"));
        ComponentVerifierExtension.Result result = verifier.verify(CONNECTIVITY, parameters);
        Assert.assertEquals(OK, result.getStatus());
    }

    @Test
    public void testConnectivityWithRedirectDisabled() throws Exception {
        HttpComponent component = context().getComponent("http4", HttpComponent.class);
        ComponentVerifierExtension verifier = component.getVerifier();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("httpUri", getLocalServerUri("/redirect"));
        parameters.put("httpClient.redirectsEnabled", "false");
        ComponentVerifierExtension.Result result = verifier.verify(CONNECTIVITY, parameters);
        Assert.assertEquals(ERROR, result.getStatus());
        Assert.assertEquals(1, result.getErrors().size());
        ComponentVerifierExtension.VerificationError error = result.getErrors().get(0);
        Assert.assertEquals(GENERIC, error.getCode());
        Assert.assertEquals(getLocalServerUri("/redirected"), error.getDetails().get(HTTP_REDIRECT));
        Assert.assertTrue(error.getParameterKeys().contains("httpUri"));
    }
}

