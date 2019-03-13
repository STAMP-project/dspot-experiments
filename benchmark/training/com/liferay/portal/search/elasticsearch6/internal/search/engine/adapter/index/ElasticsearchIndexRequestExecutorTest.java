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


import com.liferay.portal.search.engine.adapter.index.GetFieldMappingIndexRequest;
import com.liferay.portal.search.engine.adapter.index.GetMappingIndexRequest;
import com.liferay.portal.search.engine.adapter.index.PutMappingIndexRequest;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author Dylan Rebelak
 */
public class ElasticsearchIndexRequestExecutorTest {
    @Test
    public void testExecuteGetFieldMappingIndexRequest() {
        GetFieldMappingIndexRequest getFieldMappingIndexRequest = new GetFieldMappingIndexRequest(null, null, null);
        _elasticsearchIndexRequestExecutor.executeIndexRequest(getFieldMappingIndexRequest);
        Mockito.verify(_getFieldMappingIndexRequestExecutor).execute(getFieldMappingIndexRequest);
    }

    @Test
    public void testExecuteGetMappingIndexRequest() {
        GetMappingIndexRequest getMappingIndexRequest = new GetMappingIndexRequest(null, null);
        _elasticsearchIndexRequestExecutor.executeIndexRequest(getMappingIndexRequest);
        Mockito.verify(_getMappingIndexRequestExecutor).execute(getMappingIndexRequest);
    }

    @Test
    public void testExecutePutMappingIndexRequest() {
        PutMappingIndexRequest putMappingIndexRequest = new PutMappingIndexRequest(null, null, null);
        _elasticsearchIndexRequestExecutor.executeIndexRequest(putMappingIndexRequest);
        Mockito.verify(_putMappingIndexRequestExecutor).execute(putMappingIndexRequest);
    }

    private ElasticsearchIndexRequestExecutor _elasticsearchIndexRequestExecutor;

    @Mock
    private GetFieldMappingIndexRequestExecutor _getFieldMappingIndexRequestExecutor;

    @Mock
    private GetMappingIndexRequestExecutor _getMappingIndexRequestExecutor;

    @Mock
    private PutMappingIndexRequestExecutor _putMappingIndexRequestExecutor;
}

