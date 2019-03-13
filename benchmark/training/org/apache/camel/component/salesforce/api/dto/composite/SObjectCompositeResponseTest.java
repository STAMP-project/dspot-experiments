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
package org.apache.camel.component.salesforce.api.dto.composite;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.camel.component.salesforce.api.utils.JsonUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;


public class SObjectCompositeResponseTest {
    @Test
    public void shouldDeserializeFailedJsonResponse() throws IOException {
        final String json = IOUtils.toString(this.getClass().getResourceAsStream("/org/apache/camel/component/salesforce/api/dto/composite_response_example_failure.json"), Charset.forName("UTF-8"));
        final ObjectMapper mapper = JsonUtils.createObjectMapper();
        final SObjectCompositeResponse response = mapper.readerFor(SObjectCompositeResponse.class).readValue(json);
        SObjectCompositeResponseTest.assertFailedResponse(response);
    }

    @Test
    public void shouldDeserializeSuccessfulJsonResponse() throws IOException {
        final String json = IOUtils.toString(this.getClass().getResourceAsStream("/org/apache/camel/component/salesforce/api/dto/composite_response_example_success.json"), Charset.forName("UTF-8"));
        final ObjectMapper mapper = JsonUtils.createObjectMapper();
        final SObjectCompositeResponse response = mapper.readerFor(SObjectCompositeResponse.class).readValue(json);
        SObjectCompositeResponseTest.assertSuccessfulResponse(response);
    }
}

