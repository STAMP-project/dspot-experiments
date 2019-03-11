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


import ClusterHealthStatus.GREEN;
import ClusterHealthStatus.YELLOW;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.search.elasticsearch6.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.cluster.ClusterHealthStatus;
import com.liferay.portal.search.engine.adapter.cluster.HealthClusterRequest;
import com.liferay.portal.search.engine.adapter.cluster.HealthClusterResponse;
import com.liferay.portal.search.engine.adapter.cluster.StateClusterRequest;
import com.liferay.portal.search.engine.adapter.cluster.StateClusterResponse;
import com.liferay.portal.search.engine.adapter.cluster.StatsClusterRequest;
import com.liferay.portal.search.engine.adapter.cluster.StatsClusterResponse;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Dylan Rebelak
 */
public class ElasticsearchSearchEngineAdapterClusterRequestTest {
    @Test
    public void testExecuteHealthClusterRequest() throws JSONException {
        HealthClusterRequest healthClusterRequest = new HealthClusterRequest(new String[]{ ElasticsearchSearchEngineAdapterClusterRequestTest._INDEX_NAME });
        HealthClusterResponse healthClusterResponse = _searchEngineAdapter.execute(healthClusterRequest);
        ClusterHealthStatus clusterHealthStatus = healthClusterResponse.getClusterHealthStatus();
        Assert.assertTrue(((clusterHealthStatus.equals(GREEN)) || (clusterHealthStatus.equals(YELLOW))));
        String healthStatusMessage = healthClusterResponse.getHealthStatusMessage();
        JSONFactory jsonFactory = new JSONFactoryImpl();
        JSONObject jsonObject = jsonFactory.createJSONObject(healthStatusMessage);
        Assert.assertEquals("LiferayElasticsearchCluster", jsonObject.getString("cluster_name"));
        Assert.assertEquals("5", jsonObject.getString("active_shards"));
    }

    @Test
    public void testExecuteStateClusterRequest() throws JSONException {
        StateClusterRequest stateClusterRequest = new StateClusterRequest(new String[]{ ElasticsearchSearchEngineAdapterClusterRequestTest._INDEX_NAME });
        StateClusterResponse stateClusterResponse = _searchEngineAdapter.execute(stateClusterRequest);
        String stateMessage = stateClusterResponse.getStateMessage();
        JSONFactory jsonFactory = new JSONFactoryImpl();
        JSONObject jsonObject = jsonFactory.createJSONObject(stateMessage);
        String nodesString = jsonObject.getString("nodes");
        Assert.assertTrue(nodesString.contains("127.0.0.1"));
    }

    @Test
    public void testExecuteStatsClusterRequest() throws JSONException {
        StatsClusterRequest statsClusterRequest = new StatsClusterRequest(new String[]{ ElasticsearchSearchEngineAdapterClusterRequestTest._INDEX_NAME });
        StatsClusterResponse statsClusterResponse = _searchEngineAdapter.execute(statsClusterRequest);
        ClusterHealthStatus clusterHealthStatus = statsClusterResponse.getClusterHealthStatus();
        Assert.assertTrue(((clusterHealthStatus.equals(GREEN)) || (clusterHealthStatus.equals(YELLOW))));
        String statusMessage = statsClusterResponse.getStatsMessage();
        JSONFactory jsonFactory = new JSONFactoryImpl();
        JSONObject jsonObject = jsonFactory.createJSONObject(statusMessage);
        JSONObject indicesJSONObject = jsonObject.getJSONObject("indices");
        Assert.assertEquals("1", indicesJSONObject.getString("count"));
    }

    private static final String _INDEX_NAME = "test_request_index";

    private ElasticsearchFixture _elasticsearchFixture;

    private SearchEngineAdapter _searchEngineAdapter;
}

