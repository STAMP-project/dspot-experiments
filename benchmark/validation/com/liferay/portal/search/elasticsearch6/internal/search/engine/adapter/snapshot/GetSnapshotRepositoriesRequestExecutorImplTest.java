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
import com.liferay.portal.search.engine.adapter.snapshot.GetSnapshotRepositoriesRequest;
import org.elasticsearch.action.admin.cluster.repositories.get.GetRepositoriesRequest;
import org.elasticsearch.action.admin.cluster.repositories.get.GetRepositoriesRequestBuilder;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Michael C. Han
 */
public class GetSnapshotRepositoriesRequestExecutorImplTest {
    @Test
    public void testGetSnapshotRepositoriesRequestBuilder() {
        GetSnapshotRepositoriesRequest getSnapshotRepositoriesRequest = new GetSnapshotRepositoriesRequest("repository1", "repository2");
        GetSnapshotRepositoriesRequestExecutorImpl getSnapshotRepositoriesRequestExecutorImpl = new GetSnapshotRepositoriesRequestExecutorImpl() {
            {
                setElasticsearchClientResolver(_elasticsearchFixture);
            }
        };
        GetRepositoriesRequestBuilder getRepositoriesRequestBuilder = getSnapshotRepositoriesRequestExecutorImpl.createGetRepositoriesRequestBuilder(getSnapshotRepositoriesRequest);
        GetRepositoriesRequest getRepositoriesRequest = getRepositoriesRequestBuilder.request();
        Assert.assertArrayEquals(getSnapshotRepositoriesRequest.getRepositoryNames(), getRepositoriesRequest.repositories());
    }

    private ElasticsearchFixture _elasticsearchFixture;
}

