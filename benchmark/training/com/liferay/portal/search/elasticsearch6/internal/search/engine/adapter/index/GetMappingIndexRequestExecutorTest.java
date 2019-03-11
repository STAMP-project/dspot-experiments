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
import com.liferay.portal.search.engine.adapter.index.GetMappingIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequestBuilder;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Dylan Rebelak
 */
public class GetMappingIndexRequestExecutorTest {
    @Test
    public void testIndexRequestTranslation() {
        GetMappingIndexRequest getMappingIndexRequest = new GetMappingIndexRequest(new String[]{ GetMappingIndexRequestExecutorTest._INDEX_NAME }, GetMappingIndexRequestExecutorTest._MAPPING_NAME);
        GetMappingIndexRequestExecutorImpl getMappingIndexRequestExecutorImpl = new GetMappingIndexRequestExecutorImpl() {
            {
                setElasticsearchClientResolver(_elasticsearchFixture);
            }
        };
        GetMappingsRequestBuilder getMappingsRequestBuilder = getMappingIndexRequestExecutorImpl.createGetMappingsRequestBuilder(getMappingIndexRequest);
        GetMappingsRequest getMappingsRequest = getMappingsRequestBuilder.request();
        Assert.assertArrayEquals(new String[]{ GetMappingIndexRequestExecutorTest._INDEX_NAME }, getMappingsRequest.indices());
        Assert.assertArrayEquals(new String[]{ GetMappingIndexRequestExecutorTest._MAPPING_NAME }, getMappingsRequest.types());
    }

    private static final String _INDEX_NAME = "test_request_index";

    private static final String _MAPPING_NAME = "testMapping";

    private ElasticsearchFixture _elasticsearchFixture;
}

