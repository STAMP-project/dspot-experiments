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


import MetaDataExtension.MetaData.CONTENT_TYPE;
import MetaDataExtension.MetaData.JAVA_TYPE;
import SalesforceEndpointConfig.SOBJECT_NAME;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.types.ObjectSchema;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.camel.component.extension.MetaDataExtension;
import org.apache.camel.component.extension.MetaDataExtension.MetaData;
import org.apache.camel.component.salesforce.internal.client.RestClient;
import org.apache.camel.component.salesforce.internal.client.RestClient.ResponseCallback;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static SalesforceClientTemplate.restClientSupplier;


public class SalesforceMetaDataExtensionTest {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static final Class<Map<String, List<String>>> HEADERS_TYPE = ((Class) (Map.class));

    final SalesforceComponent component = new SalesforceComponent();

    final MetaDataExtension metadata;

    final RestClient restClient = Mockito.mock(RestClient.class);

    public SalesforceMetaDataExtensionTest() {
        component.setCamelContext(new DefaultCamelContext());
        restClientSupplier = ( c, p) -> restClient;
        metadata = component.getExtension(MetaDataExtension.class).get();
    }

    @Test
    public void componentShouldProvideMetadataExtension() {
        assertThat(component.getExtension(MetaDataExtension.class)).isPresent();
    }

    @Test
    public void shouldProvideSalesforceObjectFields() throws IOException {
        final Optional<MetaData> maybeMeta;
        try (InputStream stream = SalesforceMetaDataExtensionTest.resource("/objectDescription.json")) {
            Mockito.doAnswer(SalesforceMetaDataExtensionTest.provideStreamToCallback(stream)).when(restClient).getDescription(ArgumentMatchers.eq("Account"), ArgumentMatchers.any(SalesforceMetaDataExtensionTest.HEADERS_TYPE), ArgumentMatchers.any(ResponseCallback.class));
            maybeMeta = metadata.meta(Collections.singletonMap(SOBJECT_NAME, "Account"));
        }
        assertThat(maybeMeta).isPresent();
        final MetaData meta = maybeMeta.get();
        assertThat(meta.getAttribute(JAVA_TYPE)).isEqualTo(JsonNode.class);
        assertThat(meta.getAttribute(CONTENT_TYPE)).isEqualTo("application/schema+json");
        final ObjectSchema payload = meta.getPayload(ObjectSchema.class);
        assertThat(payload).isNotNull();
        assertThat(SalesforceMetaDataExtensionTest.schemaFor(payload, "Merchandise__c")).isPresent();
        assertThat(SalesforceMetaDataExtensionTest.schemaFor(payload, "QueryRecordsMerchandise__c")).isPresent();
    }

    @Test
    public void shouldProvideSalesforceObjectTypes() throws IOException {
        final Optional<MetaData> maybeMeta;
        try (InputStream stream = SalesforceMetaDataExtensionTest.resource("/globalObjects.json")) {
            Mockito.doAnswer(SalesforceMetaDataExtensionTest.provideStreamToCallback(stream)).when(restClient).getGlobalObjects(ArgumentMatchers.any(SalesforceMetaDataExtensionTest.HEADERS_TYPE), ArgumentMatchers.any(ResponseCallback.class));
            maybeMeta = metadata.meta(Collections.emptyMap());
        }
        assertThat(maybeMeta).isPresent();
        final MetaData meta = maybeMeta.get();
        assertThat(meta.getAttribute(JAVA_TYPE)).isEqualTo(JsonNode.class);
        assertThat(meta.getAttribute(CONTENT_TYPE)).isEqualTo("application/schema+json");
        final ObjectSchema payload = meta.getPayload(ObjectSchema.class);
        assertThat(payload).isNotNull();
        @SuppressWarnings({ "unchecked", "rawtypes" })
        final Set<JsonSchema> oneOf = ((Set) (payload.getOneOf()));
        assertThat(oneOf).hasSize(4);
        assertThat(SalesforceMetaDataExtensionTest.schemaFor(payload, "AcceptedEventRelation")).isPresent().hasValueSatisfying(( schema) -> assertThat(schema.getTitle()).isEqualTo("Accepted Event Relation"));
        assertThat(SalesforceMetaDataExtensionTest.schemaFor(payload, "Account")).isPresent().hasValueSatisfying(( schema) -> assertThat(schema.getTitle()).isEqualTo("Account"));
        assertThat(SalesforceMetaDataExtensionTest.schemaFor(payload, "AccountCleanInfo")).isPresent().hasValueSatisfying(( schema) -> assertThat(schema.getTitle()).isEqualTo("Account Clean Info"));
        assertThat(SalesforceMetaDataExtensionTest.schemaFor(payload, "AccountContactRole")).isPresent().hasValueSatisfying(( schema) -> assertThat(schema.getTitle()).isEqualTo("Account Contact Role"));
    }
}

