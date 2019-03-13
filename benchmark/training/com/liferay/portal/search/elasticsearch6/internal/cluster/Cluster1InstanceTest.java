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
public class Cluster1InstanceTest {
    @Test
    public void test1PrimaryShardByDefault() throws Exception {
        ElasticsearchFixture elasticsearchFixture = _testCluster.getNode(0);
        createIndex(elasticsearchFixture);
        ClusterAssert.assert1PrimaryShardOnly(elasticsearchFixture);
    }

    @Rule
    public TestName testName = new TestName();

    private final TestCluster _testCluster = new TestCluster(1, this);
}

