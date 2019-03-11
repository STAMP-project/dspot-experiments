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
package com.liferay.portal.search.elasticsearch6.internal.search.engine.adapter;


import CloseIndexAction.INSTANCE;
import IndexMetaData.State.CLOSE;
import IndexMetaData.State.OPEN;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.search.elasticsearch6.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.index.CloseIndexRequest;
import com.liferay.portal.search.engine.adapter.index.CloseIndexResponse;
import com.liferay.portal.search.engine.adapter.index.CreateIndexRequest;
import com.liferay.portal.search.engine.adapter.index.CreateIndexResponse;
import com.liferay.portal.search.engine.adapter.index.DeleteIndexRequest;
import com.liferay.portal.search.engine.adapter.index.DeleteIndexResponse;
import com.liferay.portal.search.engine.adapter.index.FlushIndexRequest;
import com.liferay.portal.search.engine.adapter.index.FlushIndexResponse;
import com.liferay.portal.search.engine.adapter.index.GetIndexIndexRequest;
import com.liferay.portal.search.engine.adapter.index.GetIndexIndexResponse;
import com.liferay.portal.search.engine.adapter.index.GetMappingIndexRequest;
import com.liferay.portal.search.engine.adapter.index.GetMappingIndexResponse;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexRequest;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexResponse;
import com.liferay.portal.search.engine.adapter.index.IndicesOptions;
import com.liferay.portal.search.engine.adapter.index.OpenIndexRequest;
import com.liferay.portal.search.engine.adapter.index.OpenIndexResponse;
import com.liferay.portal.search.engine.adapter.index.PutMappingIndexRequest;
import com.liferay.portal.search.engine.adapter.index.PutMappingIndexResponse;
import com.liferay.portal.search.engine.adapter.index.RefreshIndexRequest;
import com.liferay.portal.search.engine.adapter.index.RefreshIndexResponse;
import java.util.Arrays;
import java.util.Map;
import org.elasticsearch.action.admin.indices.close.CloseIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequestBuilder;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Dylan Rebelak
 */
public class ElasticsearchSearchEngineAdapterIndexRequestTest {
    @Test
    public void testExecuteCloseIndexRequest() {
        CloseIndexRequest closeIndexRequest = new CloseIndexRequest(ElasticsearchSearchEngineAdapterIndexRequestTest._INDEX_NAME);
        IndicesOptions indicesOptions = new IndicesOptions();
        indicesOptions.setIgnoreUnavailable(true);
        closeIndexRequest.setIndicesOptions(indicesOptions);
        CloseIndexResponse closeIndexResponse = _searchEngineAdapter.execute(closeIndexRequest);
        Assert.assertTrue("Close request not acknowledged", closeIndexResponse.isAcknowledged());
        assertIndexMetaDataState(ElasticsearchSearchEngineAdapterIndexRequestTest._INDEX_NAME, CLOSE);
    }

    @Test
    public void testExecuteCreateIndexRequest() {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("test_index_2");
        StringBundler sb = new StringBundler(14);
        sb.append("{\n");
        sb.append("    \"settings\": {\n");
        sb.append("        \"number_of_shards\": 1\n");
        sb.append("    },\n");
        sb.append("    \"mappings\": {\n");
        sb.append("        \"type1\": {\n");
        sb.append("            \"properties\": {\n");
        sb.append("                \"field1\": {\n");
        sb.append("                    \"type\": \"text\"\n");
        sb.append("                }\n");
        sb.append("            }\n");
        sb.append("        }\n");
        sb.append("    }\n");
        sb.append("}");
        createIndexRequest.setSource(sb.toString());
        CreateIndexResponse createIndexResponse = _searchEngineAdapter.execute(createIndexRequest);
        Assert.assertTrue(createIndexResponse.isAcknowledged());
        IndicesExistsRequestBuilder indicesExistsRequestBuilder = _indicesAdminClient.prepareExists("test_index_2");
        IndicesExistsResponse indicesExistsResponse = indicesExistsRequestBuilder.get();
        Assert.assertTrue(indicesExistsResponse.isExists());
        DeleteIndexRequestBuilder deleteIndexRequestBuilder = _indicesAdminClient.prepareDelete("test_index_2");
        deleteIndexRequestBuilder.get();
    }

    @Test
    public void testExecuteDeleteIndexRequest() {
        CreateIndexRequestBuilder createIndexRequestBuilder = _indicesAdminClient.prepareCreate("test_index_2");
        createIndexRequestBuilder.get();
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("test_index_2");
        DeleteIndexResponse deleteIndexResponse = _searchEngineAdapter.execute(deleteIndexRequest);
        Assert.assertTrue(deleteIndexResponse.isAcknowledged());
        IndicesExistsRequestBuilder indicesExistsRequestBuilder = _indicesAdminClient.prepareExists("test_index_2");
        IndicesExistsResponse indicesExistsResponse = indicesExistsRequestBuilder.get();
        Assert.assertFalse(indicesExistsResponse.isExists());
    }

    @Test
    public void testExecuteFlushIndexRequest() {
        FlushIndexRequest flushIndexRequest = new FlushIndexRequest(ElasticsearchSearchEngineAdapterIndexRequestTest._INDEX_NAME);
        FlushIndexResponse flushIndexResponse = _searchEngineAdapter.execute(flushIndexRequest);
        Assert.assertEquals(0, flushIndexResponse.getFailedShards());
    }

    @Test
    public void testExecuteGetIndexIndexRequest() {
        GetIndexIndexRequest getIndexIndexRequest = new GetIndexIndexRequest(ElasticsearchSearchEngineAdapterIndexRequestTest._INDEX_NAME);
        GetIndexIndexResponse getIndexIndexResponse = _searchEngineAdapter.execute(getIndexIndexRequest);
        String[] indexNames = getIndexIndexResponse.getIndexNames();
        Assert.assertEquals(Arrays.toString(indexNames), 1, indexNames.length);
        Assert.assertEquals(ElasticsearchSearchEngineAdapterIndexRequestTest._INDEX_NAME, indexNames[0]);
    }

    @Test
    public void testExecuteGetMappingIndexRequest() throws JSONException {
        String mappingName = "testGetMapping";
        String mappingSource = "{\"properties\":{\"testField\":{\"type\":\"keyword\"}}}";
        _putMapping(mappingName, mappingSource);
        GetMappingIndexRequest getMappingIndexRequest = new GetMappingIndexRequest(new String[]{ ElasticsearchSearchEngineAdapterIndexRequestTest._INDEX_NAME }, mappingName);
        GetMappingIndexResponse getMappingIndexResponse = _searchEngineAdapter.execute(getMappingIndexRequest);
        Map<String, String> indexMappings = getMappingIndexResponse.getIndexMappings();
        String string = indexMappings.toString();
        Assert.assertTrue(string.contains(mappingSource));
    }

    @Test
    public void testExecuteIndicesExistsIndexRequest() {
        IndicesExistsIndexRequest indicesExistsIndexRequest = new IndicesExistsIndexRequest(ElasticsearchSearchEngineAdapterIndexRequestTest._INDEX_NAME);
        IndicesExistsIndexResponse indicesExistsIndexResponse = _searchEngineAdapter.execute(indicesExistsIndexRequest);
        Assert.assertTrue(indicesExistsIndexResponse.isExists());
        IndicesExistsIndexRequest indicesExistsIndexRequest2 = new IndicesExistsIndexRequest("test_index_2");
        IndicesExistsIndexResponse indicesExistsIndexResponse2 = _searchEngineAdapter.execute(indicesExistsIndexRequest2);
        Assert.assertFalse(indicesExistsIndexResponse2.isExists());
    }

    @Test
    public void testExecutePutMappingIndexRequest() {
        String mappingName = "testPutMapping";
        String mappingSource = "{\"properties\":{\"testField\":{\"type\":\"keyword\"}}}";
        PutMappingIndexRequest putMappingIndexRequest = new PutMappingIndexRequest(new String[]{ ElasticsearchSearchEngineAdapterIndexRequestTest._INDEX_NAME }, mappingName, mappingSource);
        PutMappingIndexResponse putMappingIndexResponse = _searchEngineAdapter.execute(putMappingIndexRequest);
        Assert.assertTrue(putMappingIndexResponse.isAcknowledged());
        GetMappingsRequestBuilder getMappingsRequestBuilder = _indicesAdminClient.prepareGetMappings(ElasticsearchSearchEngineAdapterIndexRequestTest._INDEX_NAME);
        getMappingsRequestBuilder.setTypes(mappingName);
        GetMappingsResponse getMappingsResponse = getMappingsRequestBuilder.get();
        ImmutableOpenMap<String, ImmutableOpenMap<String, MappingMetaData>> immutableOpenMap1 = getMappingsResponse.getMappings();
        ImmutableOpenMap<String, MappingMetaData> immutableOpenMap2 = immutableOpenMap1.get(ElasticsearchSearchEngineAdapterIndexRequestTest._INDEX_NAME);
        MappingMetaData mappingMetaData = immutableOpenMap2.get(mappingName);
        String mappingMetaDataSource = String.valueOf(mappingMetaData.source());
        Assert.assertTrue(mappingMetaDataSource.contains(mappingSource));
    }

    @Test
    public void testExecuteRefreshIndexRequest() {
        RefreshIndexRequest refreshIndexRequest = new RefreshIndexRequest(ElasticsearchSearchEngineAdapterIndexRequestTest._INDEX_NAME);
        RefreshIndexResponse refreshIndexResponse = _searchEngineAdapter.execute(refreshIndexRequest);
        Assert.assertEquals(0, refreshIndexResponse.getFailedShards());
    }

    @Test
    public void testOpenIndexRequest() {
        CloseIndexRequestBuilder closeIndexRequestBuilder = INSTANCE.newRequestBuilder(_elasticsearchFixture.getClient());
        closeIndexRequestBuilder.setIndices(ElasticsearchSearchEngineAdapterIndexRequestTest._INDEX_NAME);
        closeIndexRequestBuilder.get();
        assertIndexMetaDataState(ElasticsearchSearchEngineAdapterIndexRequestTest._INDEX_NAME, CLOSE);
        OpenIndexRequest openIndexRequest = new OpenIndexRequest(ElasticsearchSearchEngineAdapterIndexRequestTest._INDEX_NAME);
        IndicesOptions indicesOptions = new IndicesOptions();
        indicesOptions.setIgnoreUnavailable(true);
        openIndexRequest.setIndicesOptions(indicesOptions);
        OpenIndexResponse openIndexResponse = _searchEngineAdapter.execute(openIndexRequest);
        Assert.assertTrue("Open request not acknowledged", openIndexResponse.isAcknowledged());
        assertIndexMetaDataState(ElasticsearchSearchEngineAdapterIndexRequestTest._INDEX_NAME, OPEN);
    }

    private static final String _INDEX_NAME = "test_request_index";

    private ElasticsearchFixture _elasticsearchFixture;

    private IndicesAdminClient _indicesAdminClient;

    private SearchEngineAdapter _searchEngineAdapter;
}

