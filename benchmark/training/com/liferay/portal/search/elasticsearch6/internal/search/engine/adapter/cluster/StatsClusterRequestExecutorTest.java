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
import com.liferay.portal.search.engine.adapter.cluster.StatsClusterRequest;
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsRequest;
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsRequestBuilder;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Dylan Rebelak
 */
public class StatsClusterRequestExecutorTest {
    @Test
    public void testClusterRequestTranslation() {
        StatsClusterRequest statsClusterRequest = new StatsClusterRequest(new String[]{ StatsClusterRequestExecutorTest._INDEX_NAME });
        StatsClusterRequestExecutorImpl statsClusterRequestExecutorImpl = new StatsClusterRequestExecutorImpl() {
            {
                setElasticsearchClientResolver(_elasticsearchFixture);
            }
        };
        ClusterStatsRequestBuilder clusterStatsRequestBuilder = statsClusterRequestExecutorImpl.createClusterStatsRequestBuilder(statsClusterRequest);
        ClusterStatsRequest clusterStatsRequest = clusterStatsRequestBuilder.request();
        Assert.assertNotNull(clusterStatsRequest);
    }

    private static final String _INDEX_NAME = "test_request_index";

    private ElasticsearchFixture _elasticsearchFixture;
}

