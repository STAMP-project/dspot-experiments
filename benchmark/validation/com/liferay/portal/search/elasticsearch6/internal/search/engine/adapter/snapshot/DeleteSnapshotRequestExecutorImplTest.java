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
package com.liferay.portal.search.elasticsearch6.internal.search.engine.adapter.snapshot;


import com.liferay.portal.search.elasticsearch6.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.engine.adapter.snapshot.DeleteSnapshotRequest;
import org.elasticsearch.action.admin.cluster.snapshots.delete.DeleteSnapshotRequestBuilder;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Michael C. Han
 */
public class DeleteSnapshotRequestExecutorImplTest {
    @Test
    public void testCreateDeleteSnapshotRequestBuilder() {
        DeleteSnapshotRequest deleteSnapshotRequest = new DeleteSnapshotRequest("repository", "snapshot");
        DeleteSnapshotRequestExecutorImpl deleteSnapshotRequestExecutorImpl = new DeleteSnapshotRequestExecutorImpl() {
            {
                setElasticsearchClientResolver(_elasticsearchFixture);
            }
        };
        DeleteSnapshotRequestBuilder deleteSnapshotRequestBuilder = deleteSnapshotRequestExecutorImpl.createDeleteSnapshotRequestBuilder(deleteSnapshotRequest);
        org.elasticsearch.action.admin.cluster.snapshots.delete.DeleteSnapshotRequest elasticDeleteSnapshotRequest = deleteSnapshotRequestBuilder.request();
        Assert.assertEquals(deleteSnapshotRequest.getRepositoryName(), elasticDeleteSnapshotRequest.repository());
        Assert.assertEquals(deleteSnapshotRequest.getSnapshotName(), elasticDeleteSnapshotRequest.snapshot());
    }

    private ElasticsearchFixture _elasticsearchFixture;
}

