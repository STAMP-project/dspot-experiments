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
package com.liferay.portal.search.elasticsearch6.internal.search.engine.adapter.cluster;


import com.liferay.portal.search.elasticsearch6.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.engine.adapter.cluster.StateClusterRequest;
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequest;
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequestBuilder;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Dylan Rebelak
 */
public class StateClusterRequestExecutorTest {
    @Test
    public void testClusterRequestTranslation() {
        StateClusterRequest stateClusterRequest = new StateClusterRequest(new String[]{ StateClusterRequestExecutorTest._INDEX_NAME });
        StateClusterRequestExecutorImpl stateClusterRequestExecutorImpl = new StateClusterRequestExecutorImpl() {
            {
                setElasticsearchClientResolver(_elasticsearchFixture);
            }
        };
        ClusterStateRequestBuilder clusterStateRequestBuilder = stateClusterRequestExecutorImpl.createClusterStateRequestBuilder(stateClusterRequest);
        ClusterStateRequest clusterStateRequest = clusterStateRequestBuilder.request();
        String[] indices = clusterStateRequest.indices();
        Assert.assertArrayEquals(new String[]{ StateClusterRequestExecutorTest._INDEX_NAME }, indices);
    }

    private static final String _INDEX_NAME = "test_request_index";

    private ElasticsearchFixture _elasticsearchFixture;
}

