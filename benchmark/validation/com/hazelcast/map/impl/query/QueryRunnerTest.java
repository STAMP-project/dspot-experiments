/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.map.impl.query;


import IterationType.ENTRY;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;
import com.hazelcast.query.impl.QueryContext;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.query.impl.predicates.EqualPredicate;
import com.hazelcast.spi.partition.MigrationEndpoint;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class })
public class QueryRunnerTest extends HazelcastTestSupport {
    private HazelcastInstance instance;

    private IMap<String, String> map;

    private QueryRunner queryRunner;

    private MapService mapService;

    private int partitionId;

    private String key;

    private String value;

    @Test
    public void assertSequentialQueryRunner() {
        Assert.assertEquals(QueryRunner.class, queryRunner.getClass());
    }

    @Test
    public void runFullQuery() {
        Predicate predicate = Predicates.equal("this", value);
        Query query = Query.of().mapName(map.getName()).predicate(predicate).iterationType(ENTRY).build();
        QueryResult result = ((QueryResult) (queryRunner.runIndexOrPartitionScanQueryOnOwnedPartitions(query)));
        Assert.assertEquals(1, result.getRows().size());
        Assert.assertEquals(map.get(key), toObject(result.getRows().iterator().next().getValue()));
        Assert.assertArrayEquals(result.getPartitionIds().toArray(), mapService.getMapServiceContext().getOwnedPartitions().toArray());
    }

    @Test
    public void runPartitionScanQueryOnSinglePartition() {
        Predicate predicate = Predicates.equal("this", value);
        Query query = Query.of().mapName(map.getName()).predicate(predicate).iterationType(ENTRY).build();
        QueryResult result = ((QueryResult) (queryRunner.runPartitionScanQueryOnGivenOwnedPartition(query, partitionId)));
        Assert.assertEquals(1, result.getRows().size());
        Assert.assertEquals(map.get(key), toObject(result.getRows().iterator().next().getValue()));
        Assert.assertArrayEquals(result.getPartitionIds().toArray(), new Object[]{ partitionId });
    }

    @Test
    public void verifyIndexedQueryFailureWhileMigrating() {
        map.addIndex("this", false);
        Predicate predicate = new EqualPredicate("this", value);
        mapService.beforeMigration(new com.hazelcast.spi.PartitionMigrationEvent(MigrationEndpoint.SOURCE, partitionId, 0, 1));
        Query query = Query.of().mapName(map.getName()).predicate(predicate).iterationType(ENTRY).build();
        QueryResult result = ((QueryResult) (queryRunner.runIndexOrPartitionScanQueryOnOwnedPartitions(query)));
        Assert.assertNull(result.getPartitionIds());
    }

    @Test
    public void verifyIndexedQueryFailureWhileMigratingInFlight() {
        map.addIndex("this", false);
        Predicate predicate = new EqualPredicate("this", value) {
            @Override
            public Set<QueryableEntry> filter(QueryContext queryContext) {
                // start a new migration while executing an indexed query
                mapService.beforeMigration(new com.hazelcast.spi.PartitionMigrationEvent(MigrationEndpoint.SOURCE, partitionId, 0, 1));
                return super.filter(queryContext);
            }
        };
        Query query = Query.of().mapName(map.getName()).predicate(predicate).iterationType(ENTRY).build();
        QueryResult result = ((QueryResult) (queryRunner.runIndexOrPartitionScanQueryOnOwnedPartitions(query)));
        Assert.assertNull(result.getPartitionIds());
    }

    @Test
    public void verifyFullScanFailureWhileMigrating() {
        Predicate predicate = new EqualPredicate("this", value);
        mapService.beforeMigration(new com.hazelcast.spi.PartitionMigrationEvent(MigrationEndpoint.SOURCE, partitionId, 0, 1));
        Query query = Query.of().mapName(map.getName()).predicate(predicate).iterationType(ENTRY).build();
        QueryResult result = ((QueryResult) (queryRunner.runIndexOrPartitionScanQueryOnOwnedPartitions(query)));
        Assert.assertNull(result.getPartitionIds());
    }

    @Test
    public void verifyFullScanFailureWhileMigratingInFlight() {
        Predicate predicate = new EqualPredicate("this", value) {
            @Override
            protected boolean applyForSingleAttributeValue(Comparable attributeValue) {
                // start a new migration while executing a full scan
                mapService.beforeMigration(new com.hazelcast.spi.PartitionMigrationEvent(MigrationEndpoint.SOURCE, partitionId, 0, 1));
                return super.applyForSingleAttributeValue(attributeValue);
            }
        };
        Query query = Query.of().mapName(map.getName()).predicate(predicate).iterationType(ENTRY).build();
        QueryResult result = ((QueryResult) (queryRunner.runIndexOrPartitionScanQueryOnOwnedPartitions(query)));
        Assert.assertNull(result.getPartitionIds());
    }
}

