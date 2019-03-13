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
import com.liferay.portal.search.engine.adapter.snapshot.RestoreSnapshotRequest;
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotRequestBuilder;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Michael C. Han
 */
public class RestoreSnapshotRequestExecutorImplTest {
    @Test
    public void testCreateRestoreSnapshotRequestBuilder() {
        RestoreSnapshotRequest restoreSnapshotRequest = new RestoreSnapshotRequest("repositoryName", "snapshotName");
        restoreSnapshotRequest.setIncludeAliases(true);
        restoreSnapshotRequest.setPartialRestore(true);
        restoreSnapshotRequest.setRestoreGlobalState(true);
        restoreSnapshotRequest.setWaitForCompletion(true);
        restoreSnapshotRequest.setIndexNames("index1", "index2");
        RestoreSnapshotRequestExecutorImpl restoreSnapshotRequestExecutorImpl = new RestoreSnapshotRequestExecutorImpl() {
            {
                setElasticsearchClientResolver(_elasticsearchFixture);
            }
        };
        RestoreSnapshotRequestBuilder restoreSnapshotRequestBuilder = restoreSnapshotRequestExecutorImpl.createRestoreSnapshotRequestBuilder(restoreSnapshotRequest);
        org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotRequest elasticsearchRestoreSnapshotsRequest = restoreSnapshotRequestBuilder.request();
        Assert.assertArrayEquals(restoreSnapshotRequest.getIndexNames(), elasticsearchRestoreSnapshotsRequest.indices());
        Assert.assertEquals(restoreSnapshotRequest.isIncludeAliases(), elasticsearchRestoreSnapshotsRequest.includeAliases());
        Assert.assertEquals(restoreSnapshotRequest.isPartialRestore(), elasticsearchRestoreSnapshotsRequest.partial());
        Assert.assertEquals(restoreSnapshotRequest.getRepositoryName(), elasticsearchRestoreSnapshotsRequest.repository());
        Assert.assertEquals(restoreSnapshotRequest.isRestoreGlobalState(), elasticsearchRestoreSnapshotsRequest.includeGlobalState());
        Assert.assertEquals(restoreSnapshotRequest.getSnapshotName(), elasticsearchRestoreSnapshotsRequest.snapshot());
        Assert.assertEquals(restoreSnapshotRequest.isWaitForCompletion(), elasticsearchRestoreSnapshotsRequest.waitForCompletion());
    }

    private ElasticsearchFixture _elasticsearchFixture;
}

