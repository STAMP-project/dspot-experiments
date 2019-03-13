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
package org.apache.camel.component.salesforce;


import ComponentVerifierExtension.Result;
import ComponentVerifierExtension.Result.Status.ERROR;
import ComponentVerifierExtension.Result.Status.OK;
import ComponentVerifierExtension.Scope.CONNECTIVITY;
import ComponentVerifierExtension.VerificationError.ExceptionAttribute.EXCEPTION_INSTANCE;
import ComponentVerifierExtension.VerificationError.HttpAttribute.HTTP_CODE;
import ComponentVerifierExtension.VerificationError.StandardCode.EXCEPTION;
import java.util.Map;
import org.apache.camel.component.extension.ComponentVerifierExtension;
import org.apache.camel.component.salesforce.api.SalesforceException;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class SalesforceComponentVerifierExtensionIntegrationTest extends CamelTestSupport {
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
    public void testConnectivityWithWrongUserName() {
        Map<String, Object> parameters = getParameters();
        parameters.put("userName", "not-a-salesforce-user");
        ComponentVerifierExtension.Result result = getExtension().verify(CONNECTIVITY, parameters);
        Assert.assertEquals(ERROR, result.getStatus());
        Assert.assertEquals(2, result.getErrors().size());
        // Exception
        Assert.assertEquals(EXCEPTION, result.getErrors().get(0).getCode());
        Assert.assertNotNull(result.getErrors().get(0).getDetails().get(EXCEPTION_INSTANCE));
        Assert.assertTrue(((result.getErrors().get(0).getDetails().get(EXCEPTION_INSTANCE)) instanceof SalesforceException));
        Assert.assertEquals(400, result.getErrors().get(0).getDetails().get(HTTP_CODE));
        // Salesforce Error
        Assert.assertEquals("invalid_grant", result.getErrors().get(1).getDetail("salesforce_code"));
    }

    @Test
    public void testConnectivityWithWrongSecrets() {
        Map<String, Object> parameters = getParameters();
        parameters.put("clientId", "wrong-client-id");
        parameters.put("clientSecret", "wrong-client-secret");
        ComponentVerifierExtension.Result result = getExtension().verify(CONNECTIVITY, parameters);
        Assert.assertEquals(ERROR, result.getStatus());
        Assert.assertEquals(ERROR, result.getStatus());
        Assert.assertEquals(2, result.getErrors().size());
        // Exception
        Assert.assertEquals(EXCEPTION, result.getErrors().get(0).getCode());
        Assert.assertNotNull(result.getErrors().get(0).getDetails().get(EXCEPTION_INSTANCE));
        Assert.assertTrue(((result.getErrors().get(0).getDetails().get(EXCEPTION_INSTANCE)) instanceof SalesforceException));
        Assert.assertEquals(400, result.getErrors().get(0).getDetails().get(HTTP_CODE));
        // Salesforce Error
        Assert.assertEquals("invalid_client_id", result.getErrors().get(1).getDetail("salesforce_code"));
    }
}

