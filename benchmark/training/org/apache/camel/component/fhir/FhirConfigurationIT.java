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
package org.apache.camel.component.fhir;


import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.impl.GenericClient;
import java.util.List;
import org.apache.camel.component.fhir.internal.FhirApiCollection;
import org.apache.camel.component.fhir.internal.FhirCreateApiMethod;
import org.junit.Test;


/**
 * Test class for {@link FhirConfiguration} APIs.
 */
public class FhirConfigurationIT extends AbstractFhirTestSupport {
    private static final String PATH_PREFIX = FhirApiCollection.getCollection().getApiName(FhirCreateApiMethod.class).getName();

    private static final String TEST_URI = ((("fhir://" + (FhirConfigurationIT.PATH_PREFIX)) + "/resource?inBody=resourceAsString&log=true&") + "encoding=JSON&summary=TEXT&compress=true&username=art&password=tatum&sessionCookie=mycookie%3DChips%20Ahoy") + "&accessToken=token&serverUrl=http://localhost:8080/hapi-fhir-jpaserver-example/baseDstu3&fhirVersion=DSTU3";

    private FhirConfiguration componentConfiguration;

    @Test
    public void testConfiguration() throws Exception {
        FhirEndpoint endpoint = getMandatoryEndpoint(FhirConfigurationIT.TEST_URI, FhirEndpoint.class);
        GenericClient client = ((GenericClient) (endpoint.getClient()));
        FhirConfiguration configuration = endpoint.getConfiguration();
        assertEquals(this.componentConfiguration, configuration);
        assertEquals("http://localhost:8080/hapi-fhir-jpaserver-example/baseDstu3", client.getUrlBase());
        assertEquals(EncodingEnum.JSON, client.getEncoding());
        assertEquals(SummaryEnum.TEXT, client.getSummary());
        List<IClientInterceptor> interceptors = client.getInterceptors();
        assertEquals(5, interceptors.size());
    }
}

