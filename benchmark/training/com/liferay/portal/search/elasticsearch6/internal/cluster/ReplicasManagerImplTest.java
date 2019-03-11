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


import CompanyConstants.SYSTEM;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.search.elasticsearch6.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.elasticsearch6.internal.connection.IndexCreator;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.mockito.Mock;


/**
 *
 *
 * @author Artur Aquino
 */
@Ignore
public class ReplicasManagerImplTest {
    @Test
    public void testSystemCompanyIndexIsReplicatedAndMigrated() throws Exception {
        long companyId = RandomTestUtil.randomLong();
        setUpCompanyLocalService(companyId);
        ElasticsearchFixture elasticsearchFixture0 = createNode(0);
        IndexCreator indexCreator0 = new IndexCreator() {
            {
                setElasticsearchClientResolver(elasticsearchFixture0);
            }
        };
        indexCreator0.createIndex(getTestIndexName(SYSTEM));
        ElasticsearchFixture elasticsearchFixture1 = createNode(1);
        ClusterAssert.assert1PrimaryShardAnd2Nodes(elasticsearchFixture0);
        IndexCreator indexCreator1 = new IndexCreator() {
            {
                setElasticsearchClientResolver(elasticsearchFixture1);
            }
        };
        indexCreator1.createIndex(getTestIndexName(companyId));
        ClusterAssert.assert2PrimaryShardsAnd2Nodes(elasticsearchFixture1);
        ReplicasManager replicasManager = new ReplicasManagerImpl(elasticsearchFixture0.getIndicesAdminClient());
        replicasManager.updateNumberOfReplicas(1, _replicasClusterContext.getTargetIndexNames());
        ClusterAssert.assert2PrimaryShards1ReplicaAnd2Nodes(elasticsearchFixture0);
        _testCluster.destroyNode(0);
        ClusterAssert.assert2Primary2UnassignedShardsAnd1Node(elasticsearchFixture1);
    }

    @Rule
    public TestName testName = new TestName();

    @Mock
    private CompanyLocalService _companyLocalService;

    private ReplicasClusterContext _replicasClusterContext;

    private final TestCluster _testCluster = new TestCluster(2, this);
}

