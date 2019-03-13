/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.portal.search.elasticsearch6.internal.document;


import Field.GEO_LOCATION;
import LiferayTypeMappingsConstants.LIFERAY_DOCUMENT_TYPE;
import PutMappingAction.INSTANCE;
import XContentType.JSON;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.search.elasticsearch6.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.elasticsearch6.internal.connection.IndexCreationHelper;
import com.liferay.portal.search.test.util.indexing.BaseIndexingTestCase;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.elasticsearch.common.settings.Settings;
import org.junit.Test;


/**
 *
 *
 * @author Andr? de Oliveira
 */
public class GeoLocationPointFieldTest extends BaseIndexingTestCase {
    @Test
    public void testCustomField() throws Exception {
        assertGeoLocationPointField(GeoLocationPointFieldTest._CUSTOM_FIELD);
    }

    @Test
    public void testDefaultField() throws Exception {
        assertGeoLocationPointField(GEO_LOCATION);
    }

    @Test
    public void testDefaultTemplate() throws Exception {
        assertGeoLocationPointField(GeoLocationPointFieldTest._CUSTOM_FIELD.concat("_geolocation"));
    }

    private static final String _CUSTOM_FIELD = "customField";

    private static class CustomFieldLiferayIndexCreationHelper implements IndexCreationHelper {
        public CustomFieldLiferayIndexCreationHelper(ElasticsearchClientResolver elasticsearchClientResolver) {
            _elasticsearchClientResolver = elasticsearchClientResolver;
        }

        @Override
        public void contribute(CreateIndexRequestBuilder createIndexRequestBuilder) {
        }

        @Override
        public void contributeIndexSettings(Settings.Builder builder) {
        }

        @Override
        public void whenIndexCreated(String indexName) {
            PutMappingRequestBuilder putMappingRequestBuilder = INSTANCE.newRequestBuilder(_elasticsearchClientResolver.getClient());
            String source = StringBundler.concat("{ \"properties\": { \"", GeoLocationPointFieldTest._CUSTOM_FIELD, "\" : { \"fields\": ", "{ \"geopoint\" : { \"store\": true, \"type\": \"keyword\" } ", "}, \"store\": true, \"type\": \"geo_point\" } } }");
            putMappingRequestBuilder.setIndices(indexName).setSource(source, JSON).setType(LIFERAY_DOCUMENT_TYPE);
            putMappingRequestBuilder.get();
        }

        private final ElasticsearchClientResolver _elasticsearchClientResolver;
    }
}

