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
package com.liferay.portal.search.elasticsearch6.internal.search.engine.adapter.index;


import com.liferay.portal.search.elasticsearch6.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.engine.adapter.index.GetFieldMappingIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetFieldMappingsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetFieldMappingsRequestBuilder;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Dylan Rebelak
 */
public class GetFieldMappingIndexRequestExecutorTest {
    @Test
    public void testIndexRequestTranslation() {
        GetFieldMappingIndexRequest getFieldMappingIndexRequest = new GetFieldMappingIndexRequest(new String[]{ GetFieldMappingIndexRequestExecutorTest._INDEX_NAME }, GetFieldMappingIndexRequestExecutorTest._MAPPING_NAME, new String[]{ GetFieldMappingIndexRequestExecutorTest._FIELD_NAME });
        GetFieldMappingIndexRequestExecutorImpl getFieldMappingIndexRequestExecutorImpl = new GetFieldMappingIndexRequestExecutorImpl() {
            {
                setElasticsearchClientResolver(_elasticsearchFixture);
            }
        };
        GetFieldMappingsRequestBuilder getFieldMappingsRequestBuilder = getFieldMappingIndexRequestExecutorImpl.createGetFieldMappingsRequestBuilder(getFieldMappingIndexRequest);
        GetFieldMappingsRequest getFieldMappingsRequest = getFieldMappingsRequestBuilder.request();
        Assert.assertArrayEquals(new String[]{ GetFieldMappingIndexRequestExecutorTest._INDEX_NAME }, getFieldMappingsRequest.indices());
        Assert.assertArrayEquals(new String[]{ GetFieldMappingIndexRequestExecutorTest._MAPPING_NAME }, getFieldMappingsRequest.types());
        Assert.assertArrayEquals(new String[]{ GetFieldMappingIndexRequestExecutorTest._FIELD_NAME }, getFieldMappingsRequest.fields());
    }

    private static final String _FIELD_NAME = "testField";

    private static final String _INDEX_NAME = "test_request_index";

    private static final String _MAPPING_NAME = "testMapping";

    private ElasticsearchFixture _elasticsearchFixture;
}

