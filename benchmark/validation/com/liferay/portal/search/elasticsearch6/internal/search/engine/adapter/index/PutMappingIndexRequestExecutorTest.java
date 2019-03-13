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
import com.liferay.portal.search.engine.adapter.index.PutMappingIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Dylan Rebelak
 */
public class PutMappingIndexRequestExecutorTest {
    @Test
    public void testIndexRequestTranslation() {
        PutMappingIndexRequest putMappingIndexRequest = new PutMappingIndexRequest(new String[]{ PutMappingIndexRequestExecutorTest._INDEX_NAME }, PutMappingIndexRequestExecutorTest._MAPPING_NAME, PutMappingIndexRequestExecutorTest._FIELD_NAME);
        PutMappingIndexRequestExecutorImpl putMappingIndexRequestExecutorImpl = new PutMappingIndexRequestExecutorImpl() {
            {
                setElasticsearchClientResolver(_elasticsearchFixture);
            }
        };
        PutMappingRequestBuilder putMappingRequestBuilder = putMappingIndexRequestExecutorImpl.createPutMappingRequestBuilder(putMappingIndexRequest);
        PutMappingRequest putMappingRequest = putMappingRequestBuilder.request();
        Assert.assertArrayEquals(new String[]{ PutMappingIndexRequestExecutorTest._INDEX_NAME }, putMappingRequest.indices());
        Assert.assertEquals(PutMappingIndexRequestExecutorTest._FIELD_NAME, putMappingRequest.source());
        Assert.assertEquals(PutMappingIndexRequestExecutorTest._MAPPING_NAME, putMappingRequest.type());
    }

    private static final String _FIELD_NAME = "testField";

    private static final String _INDEX_NAME = "test_request_index";

    private static final String _MAPPING_NAME = "testMapping";

    private ElasticsearchFixture _elasticsearchFixture;
}

