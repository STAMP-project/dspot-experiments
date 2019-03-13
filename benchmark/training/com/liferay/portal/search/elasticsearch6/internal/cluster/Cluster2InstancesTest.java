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
package com.liferay.portal.search.elasticsearch6.internal.cluster;


import com.liferay.portal.search.elasticsearch6.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.elasticsearch6.internal.connection.Index;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


/**
 *
 *
 * @author Andr? de Oliveira
 */
@Ignore
public class Cluster2InstancesTest {
    @Test
    public void test2Nodes1PrimaryShard() throws Exception {
        ElasticsearchFixture elasticsearchFixture0 = _testCluster.getNode(0);
        createIndex(elasticsearchFixture0);
        ClusterAssert.assert1PrimaryShardAnd2Nodes(elasticsearchFixture0);
        ElasticsearchFixture elasticsearchFixture1 = _testCluster.getNode(1);
        createIndex(elasticsearchFixture1);
        ClusterAssert.assert1PrimaryShardAnd2Nodes(elasticsearchFixture1);
    }

    @Test
    public void testExpandAndShrink() throws Exception {
        ElasticsearchFixture elasticsearchFixture0 = _testCluster.getNode(0);
        Index index0 = createIndex(elasticsearchFixture0);
        ElasticsearchFixture elasticsearchFixture1 = _testCluster.getNode(1);
        Index index1 = createIndex(elasticsearchFixture1);
        updateNumberOfReplicas(1, index1, elasticsearchFixture1);
        ClusterAssert.assert1ReplicaShard(elasticsearchFixture0);
        ClusterAssert.assert1ReplicaShard(elasticsearchFixture1);
        updateNumberOfReplicas(0, index0, elasticsearchFixture0);
        ClusterAssert.assert1PrimaryShardAnd2Nodes(elasticsearchFixture0);
        ClusterAssert.assert1PrimaryShardAnd2Nodes(elasticsearchFixture1);
    }

    @Rule
    public TestName testName = new TestName();

    private final TestCluster _testCluster = new TestCluster(2, this);
}

