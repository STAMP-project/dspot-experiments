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


import DeleteSnapshotAction.INSTANCE;
import SnapshotRepositoryDetails.FS_REPOSITORY_TYPE;
import SnapshotState.SUCCESS;
import com.liferay.portal.search.elasticsearch6.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.snapshot.CreateSnapshotRepositoryRequest;
import com.liferay.portal.search.engine.adapter.snapshot.CreateSnapshotRepositoryResponse;
import com.liferay.portal.search.engine.adapter.snapshot.CreateSnapshotRequest;
import com.liferay.portal.search.engine.adapter.snapshot.CreateSnapshotResponse;
import com.liferay.portal.search.engine.adapter.snapshot.DeleteSnapshotRequest;
import com.liferay.portal.search.engine.adapter.snapshot.DeleteSnapshotResponse;
import com.liferay.portal.search.engine.adapter.snapshot.GetSnapshotRepositoriesRequest;
import com.liferay.portal.search.engine.adapter.snapshot.GetSnapshotRepositoriesResponse;
import com.liferay.portal.search.engine.adapter.snapshot.GetSnapshotsRequest;
import com.liferay.portal.search.engine.adapter.snapshot.GetSnapshotsResponse;
import com.liferay.portal.search.engine.adapter.snapshot.RestoreSnapshotRequest;
import com.liferay.portal.search.engine.adapter.snapshot.SnapshotDetails;
import com.liferay.portal.search.engine.adapter.snapshot.SnapshotRepositoryDetails;
import com.liferay.portal.search.test.util.IdempotentRetryAssert;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.elasticsearch.action.admin.cluster.repositories.get.GetRepositoriesRequestBuilder;
import org.elasticsearch.action.admin.cluster.repositories.get.GetRepositoriesResponse;
import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotRequestBuilder;
import org.elasticsearch.action.admin.cluster.snapshots.delete.DeleteSnapshotRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.cluster.metadata.RepositoryMetaData;
import org.elasticsearch.snapshots.SnapshotInfo;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Michael C. Han
 */
public class ElasticsearchSearchEngineAdapterSnapshotRequestTest {
    @Test
    public void testCreateSnapshot() {
        CreateSnapshotRequest createSnapshotRequest = new CreateSnapshotRequest(ElasticsearchSearchEngineAdapterSnapshotRequestTest._TEST_REPOSITORY_NAME, "test_create_snapshot");
        createSnapshotRequest.setIndexNames(ElasticsearchSearchEngineAdapterSnapshotRequestTest._INDEX_NAME);
        CreateSnapshotResponse createSnapshotResponse = _searchEngineAdapter.execute(createSnapshotRequest);
        SnapshotDetails snapshotDetails = createSnapshotResponse.getSnapshotDetails();
        Assert.assertArrayEquals(createSnapshotRequest.getIndexNames(), snapshotDetails.getIndexNames());
        Assert.assertEquals(SUCCESS, snapshotDetails.getSnapshotState());
        Assert.assertTrue(((snapshotDetails.getSuccessfulShards()) > 1));
        List<SnapshotInfo> snapshotInfos = getSnapshotInfo("test_create_snapshot");
        Assert.assertEquals("Expected 1 SnapshotInfo", 1, snapshotInfos.size());
        SnapshotInfo snapshotInfo = snapshotInfos.get(0);
        List<String> indices = snapshotInfo.indices();
        Assert.assertArrayEquals(createSnapshotRequest.getIndexNames(), indices.toArray());
        Assert.assertEquals("test_create_snapshot", createSnapshotRequest.getSnapshotName());
        Assert.assertEquals(createSnapshotRequest.getRepositoryName(), ElasticsearchSearchEngineAdapterSnapshotRequestTest._TEST_REPOSITORY_NAME);
        DeleteSnapshotRequestBuilder deleteSnapshotRequestBuilder = INSTANCE.newRequestBuilder(_elasticsearchFixture.getClient());
        deleteSnapshotRequestBuilder.setRepository(ElasticsearchSearchEngineAdapterSnapshotRequestTest._TEST_REPOSITORY_NAME);
        deleteSnapshotRequestBuilder.setSnapshot("test_create_snapshot");
        deleteSnapshotRequestBuilder.get();
    }

    @Test
    public void testCreateSnapshotRepository() {
        CreateSnapshotRepositoryRequest createSnapshotRepositoryRequest = new CreateSnapshotRepositoryRequest("testCreateSnapshotRepository", "testCreateSnapshotRepository");
        CreateSnapshotRepositoryResponse createSnapshotRepositoryResponse = _searchEngineAdapter.execute(createSnapshotRepositoryRequest);
        Assert.assertTrue(createSnapshotRepositoryResponse.isAcknowledged());
        GetRepositoriesRequestBuilder getRepositoriesRequestBuilder = GetRepositoriesAction.INSTANCE.newRequestBuilder(_elasticsearchFixture.getClient());
        getRepositoriesRequestBuilder.addRepositories("testCreateSnapshotRepository");
        GetRepositoriesResponse getRepositoriesResponse = getRepositoriesRequestBuilder.get();
        List<RepositoryMetaData> repositoryMetaDatas = getRepositoriesResponse.repositories();
        Assert.assertEquals("Expected 1 RepositoryMetaData", 1, repositoryMetaDatas.size());
        RepositoryMetaData repositoryMetaData = repositoryMetaDatas.get(0);
        Assert.assertEquals("testCreateSnapshotRepository", repositoryMetaData.name());
        Assert.assertEquals(FS_REPOSITORY_TYPE, repositoryMetaData.type());
        deleteRepository("testCreateSnapshotRepository");
    }

    @Test
    public void testDeleteSnapshot() throws Exception {
        CreateSnapshotRequestBuilder createSnapshotRequestBuilder = CreateSnapshotAction.INSTANCE.newRequestBuilder(_elasticsearchFixture.getClient());
        createSnapshotRequestBuilder.setIndices(ElasticsearchSearchEngineAdapterSnapshotRequestTest._INDEX_NAME);
        createSnapshotRequestBuilder.setRepository(ElasticsearchSearchEngineAdapterSnapshotRequestTest._TEST_REPOSITORY_NAME);
        createSnapshotRequestBuilder.setSnapshot("test_delete_snapshot");
        createSnapshotRequestBuilder.setWaitForCompletion(true);
        createSnapshotRequestBuilder.get();
        IdempotentRetryAssert.retryAssert(10, TimeUnit.SECONDS, () -> {
            List<SnapshotInfo> snapshotInfos = getSnapshotInfo("test_delete_snapshot");
            Assert.assertEquals("Expected 1 SnapshotInfo", 1, snapshotInfos.size());
            DeleteSnapshotRequest deleteSnapshotRequest = new DeleteSnapshotRequest(_TEST_REPOSITORY_NAME, "test_delete_snapshot");
            DeleteSnapshotResponse deleteSnapshotResponse = _searchEngineAdapter.execute(deleteSnapshotRequest);
            Assert.assertTrue(deleteSnapshotResponse.isAcknowledged());
            snapshotInfos = getSnapshotInfo("test_delete_snapshot");
            Assert.assertTrue(snapshotInfos.isEmpty());
            return null;
        });
    }

    @Test
    public void testGetSnapshotRepositories() {
        GetSnapshotRepositoriesRequest getSnapshotRepositoriesRequest = new GetSnapshotRepositoriesRequest(ElasticsearchSearchEngineAdapterSnapshotRequestTest._TEST_REPOSITORY_NAME);
        GetSnapshotRepositoriesResponse getSnapshotRepositoriesResponse = _searchEngineAdapter.execute(getSnapshotRepositoriesRequest);
        List<SnapshotRepositoryDetails> snapshotRepositoryDetailsList = getSnapshotRepositoriesResponse.getSnapshotRepositoryDetails();
        Assert.assertEquals("Expected 1 SnapshotRepositoryDetails", 1, snapshotRepositoryDetailsList.size());
        SnapshotRepositoryDetails snapshotRepositoryDetails = snapshotRepositoryDetailsList.get(0);
        Assert.assertEquals(ElasticsearchSearchEngineAdapterSnapshotRequestTest._TEST_REPOSITORY_NAME, snapshotRepositoryDetails.getName());
        Assert.assertEquals(FS_REPOSITORY_TYPE, snapshotRepositoryDetails.getType());
    }

    @Test
    public void testGetSnapshots() {
        CreateSnapshotRequestBuilder createSnapshotRequestBuilder = CreateSnapshotAction.INSTANCE.newRequestBuilder(_elasticsearchFixture.getClient());
        createSnapshotRequestBuilder.setIndices(ElasticsearchSearchEngineAdapterSnapshotRequestTest._INDEX_NAME);
        createSnapshotRequestBuilder.setRepository(ElasticsearchSearchEngineAdapterSnapshotRequestTest._TEST_REPOSITORY_NAME);
        createSnapshotRequestBuilder.setSnapshot("test_get_snapshots");
        createSnapshotRequestBuilder.setWaitForCompletion(true);
        createSnapshotRequestBuilder.get();
        GetSnapshotsRequest getSnapshotsRequest = new GetSnapshotsRequest(ElasticsearchSearchEngineAdapterSnapshotRequestTest._TEST_REPOSITORY_NAME);
        getSnapshotsRequest.setSnapshotNames("test_get_snapshots");
        GetSnapshotsResponse getSnapshotsResponse = _searchEngineAdapter.execute(getSnapshotsRequest);
        List<SnapshotDetails> snapshotDetailsList = getSnapshotsResponse.getSnapshotDetails();
        Assert.assertEquals("Expected 1 SnapshotDetails", 1, snapshotDetailsList.size());
        SnapshotDetails snapshotDetails = snapshotDetailsList.get(0);
        Assert.assertArrayEquals(new String[]{ ElasticsearchSearchEngineAdapterSnapshotRequestTest._INDEX_NAME }, snapshotDetails.getIndexNames());
        Assert.assertEquals(SUCCESS, snapshotDetails.getSnapshotState());
        DeleteSnapshotRequestBuilder deleteSnapshotRequestBuilder = INSTANCE.newRequestBuilder(_elasticsearchFixture.getClient());
        deleteSnapshotRequestBuilder.setRepository(ElasticsearchSearchEngineAdapterSnapshotRequestTest._TEST_REPOSITORY_NAME);
        deleteSnapshotRequestBuilder.setSnapshot("test_get_snapshots");
        deleteSnapshotRequestBuilder.get();
    }

    @Test
    public void testRestoreSnapshot() {
        CreateSnapshotRequestBuilder createSnapshotRequestBuilder = CreateSnapshotAction.INSTANCE.newRequestBuilder(_elasticsearchFixture.getClient());
        createSnapshotRequestBuilder.setIndices(ElasticsearchSearchEngineAdapterSnapshotRequestTest._INDEX_NAME);
        createSnapshotRequestBuilder.setRepository(ElasticsearchSearchEngineAdapterSnapshotRequestTest._TEST_REPOSITORY_NAME);
        createSnapshotRequestBuilder.setSnapshot("test_restore_snapshot");
        createSnapshotRequestBuilder.setWaitForCompletion(true);
        createSnapshotRequestBuilder.get();
        deleteIndex();
        RestoreSnapshotRequest restoreSnapshotRequest = new RestoreSnapshotRequest(ElasticsearchSearchEngineAdapterSnapshotRequestTest._TEST_REPOSITORY_NAME, "test_restore_snapshot");
        restoreSnapshotRequest.setIndexNames(ElasticsearchSearchEngineAdapterSnapshotRequestTest._INDEX_NAME);
        _searchEngineAdapter.execute(restoreSnapshotRequest);
        IndicesExistsRequestBuilder indicesExistsRequestBuilder = IndicesExistsAction.INSTANCE.newRequestBuilder(_elasticsearchFixture.getClient());
        indicesExistsRequestBuilder.setIndices(ElasticsearchSearchEngineAdapterSnapshotRequestTest._INDEX_NAME);
        IndicesExistsResponse indicesExistsResponse = indicesExistsRequestBuilder.get();
        Assert.assertTrue("Indices not restored", indicesExistsResponse.isExists());
        DeleteSnapshotRequestBuilder deleteSnapshotRequestBuilder = INSTANCE.newRequestBuilder(_elasticsearchFixture.getClient());
        deleteSnapshotRequestBuilder.setRepository(ElasticsearchSearchEngineAdapterSnapshotRequestTest._TEST_REPOSITORY_NAME);
        deleteSnapshotRequestBuilder.setSnapshot("test_restore_snapshot");
        deleteSnapshotRequestBuilder.get();
    }

    private static final String _INDEX_NAME = "test_request_index";

    private static final String _TEST_REPOSITORY_NAME = "testRepositoryOperations";

    private ElasticsearchFixture _elasticsearchFixture;

    private IndicesAdminClient _indicesAdminClient;

    private SearchEngineAdapter _searchEngineAdapter;
}

