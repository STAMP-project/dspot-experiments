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


import Bundle.LINK_NEXT;
import Bundle.LINK_PREV;
import EncodingEnum.XML;
import ExtraParameters.ENCODING_ENUM;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.component.fhir.internal.FhirApiCollection;
import org.apache.camel.component.fhir.internal.FhirLoadPageApiMethod;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test class for {@link org.apache.camel.component.fhir.api.FhirLoadPage} APIs.
 * The class source won't be generated again if the generator MOJO finds it under src/test/java.
 */
public class FhirLoadPageIT extends AbstractFhirTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(FhirLoadPageIT.class);

    private static final String PATH_PREFIX = FhirApiCollection.getCollection().getApiName(FhirLoadPageApiMethod.class).getName();

    @Test
    public void testByUrl() throws Exception {
        String url = "Patient?_count=2";
        Bundle bundle = this.fhirClient.search().byUrl(url).returnBundle(Bundle.class).execute();
        assertNotNull(bundle.getLink(LINK_NEXT));
        String nextPageLink = bundle.getLink("next").getUrl();
        final Map<String, Object> headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelFhir.url", nextPageLink);
        // parameter type is Class
        headers.put("CamelFhir.returnType", Bundle.class);
        IBaseBundle result = requestBodyAndHeaders("direct://BY_URL", null, headers);
        FhirLoadPageIT.LOG.debug(("byUrl: " + result));
        assertNotNull("byUrl result", result);
    }

    @Test
    public void testNext() throws Exception {
        String url = "Patient?_count=2";
        Bundle bundle = this.fhirClient.search().byUrl(url).returnBundle(Bundle.class).execute();
        assertNotNull(bundle.getLink(LINK_NEXT));
        // using org.hl7.fhir.instance.model.api.IBaseBundle message body for single parameter "bundle"
        Bundle result = requestBody("direct://NEXT", bundle);
        assertNotNull("next result", result);
        FhirLoadPageIT.LOG.debug(("next: " + result));
    }

    @Test
    public void testPrevious() throws Exception {
        String url = "Patient?_count=2";
        Bundle bundle = this.fhirClient.search().byUrl(url).returnBundle(Bundle.class).execute();
        assertNotNull(bundle.getLink(LINK_NEXT));
        String nextPageLink = bundle.getLink("next").getUrl();
        bundle = this.fhirClient.loadPage().byUrl(nextPageLink).andReturnBundle(Bundle.class).execute();
        assertNotNull(bundle.getLink(LINK_PREV));
        // using org.hl7.fhir.instance.model.api.IBaseBundle message body for single parameter "bundle"
        Bundle result = requestBody("direct://PREVIOUS", bundle);
        FhirLoadPageIT.LOG.debug(("previous: " + result));
        assertNotNull("previous result", result);
    }

    @Test
    public void testPreviousWithEncodingEnum() throws Exception {
        String url = "Patient?_count=2";
        Bundle bundle = this.fhirClient.search().byUrl(url).returnBundle(Bundle.class).execute();
        assertNotNull(bundle.getLink(LINK_NEXT));
        String nextPageLink = bundle.getLink("next").getUrl();
        bundle = this.fhirClient.loadPage().byUrl(nextPageLink).andReturnBundle(Bundle.class).execute();
        assertNotNull(bundle.getLink(LINK_PREV));
        Map<String, Object> headers = new HashMap<>();
        headers.put(ENCODING_ENUM.getHeaderName(), XML);
        // using org.hl7.fhir.instance.model.api.IBaseBundle message body for single parameter "bundle"
        Bundle result = requestBodyAndHeaders("direct://PREVIOUS", bundle, headers);
        FhirLoadPageIT.LOG.debug(("previous: " + result));
        assertNotNull("previous result", result);
    }
}

