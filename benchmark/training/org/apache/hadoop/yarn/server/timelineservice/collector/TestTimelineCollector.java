/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.server.timelineservice.collector;


import TimelineEntityType.YARN_APPLICATION;
import TimelineMetricOperation.NOP;
import TimelineMetricOperation.SUM;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.timelineservice.TimelineDomain;
import org.apache.hadoop.yarn.api.records.timelineservice.TimelineEntities;
import org.apache.hadoop.yarn.api.records.timelineservice.TimelineEntity;
import org.apache.hadoop.yarn.api.records.timelineservice.TimelineMetric;
import org.apache.hadoop.yarn.server.timelineservice.collector.TimelineCollector.AggregationStatusTable;
import org.apache.hadoop.yarn.server.timelineservice.storage.TimelineWriter;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestTimelineCollector {
    @Test
    public void testAggregation() throws Exception {
        // Test aggregation with multiple groups.
        int groups = 3;
        int n = 50;
        TimelineEntities testEntities = generateTestEntities(groups, n);
        TimelineEntity resultEntity = TimelineCollector.aggregateEntities(testEntities, "test_result", "TEST_AGGR", true);
        Assert.assertEquals(resultEntity.getMetrics().size(), (groups * 3));
        for (int i = 0; i < groups; i++) {
            Set<TimelineMetric> metrics = resultEntity.getMetrics();
            for (TimelineMetric m : metrics) {
                if (m.getId().startsWith("HDFS_BYTES_WRITE")) {
                    Assert.assertEquals((100 * n), m.getSingleDataValue().intValue());
                } else
                    if (m.getId().startsWith("VCORES_USED")) {
                        Assert.assertEquals((3 * n), m.getSingleDataValue().intValue());
                    } else
                        if (m.getId().startsWith("TXN_FINISH_TIME")) {
                            Assert.assertEquals((n - 1), m.getSingleDataValue());
                        } else {
                            Assert.fail(("Unrecognized metric! " + (m.getId())));
                        }


            }
        }
        // Test aggregation with a single group.
        TimelineEntities testEntities1 = generateTestEntities(1, n);
        TimelineEntity resultEntity1 = TimelineCollector.aggregateEntities(testEntities1, "test_result", "TEST_AGGR", false);
        Assert.assertEquals(resultEntity1.getMetrics().size(), 3);
        Set<TimelineMetric> metrics = resultEntity1.getMetrics();
        for (TimelineMetric m : metrics) {
            if (m.getId().equals("HDFS_BYTES_WRITE")) {
                Assert.assertEquals((100 * n), m.getSingleDataValue().intValue());
            } else
                if (m.getId().equals("VCORES_USED")) {
                    Assert.assertEquals((3 * n), m.getSingleDataValue().intValue());
                } else
                    if (m.getId().equals("TXN_FINISH_TIME")) {
                        Assert.assertEquals((n - 1), m.getSingleDataValue());
                    } else {
                        Assert.fail(("Unrecognized metric! " + (m.getId())));
                    }


        }
    }

    /**
     * Test TimelineCollector's interaction with TimelineWriter upon
     * putEntity() calls.
     */
    @Test
    public void testPutEntity() throws IOException {
        TimelineWriter writer = Mockito.mock(TimelineWriter.class);
        TimelineCollector collector = new TestTimelineCollector.TimelineCollectorForTest(writer);
        TimelineEntities entities = generateTestEntities(1, 1);
        collector.putEntities(entities, UserGroupInformation.createRemoteUser("test-user"));
        Mockito.verify(writer, Mockito.times(1)).write(ArgumentMatchers.any(TimelineCollectorContext.class), ArgumentMatchers.any(TimelineEntities.class), ArgumentMatchers.any(UserGroupInformation.class));
        Mockito.verify(writer, Mockito.times(1)).flush();
    }

    /**
     * Test TimelineCollector's interaction with TimelineWriter upon
     * putEntityAsync() calls.
     */
    @Test
    public void testPutEntityAsync() throws IOException {
        TimelineWriter writer = Mockito.mock(TimelineWriter.class);
        TimelineCollector collector = new TestTimelineCollector.TimelineCollectorForTest(writer);
        TimelineEntities entities = generateTestEntities(1, 1);
        collector.putEntitiesAsync(entities, UserGroupInformation.createRemoteUser("test-user"));
        Mockito.verify(writer, Mockito.times(1)).write(ArgumentMatchers.any(TimelineCollectorContext.class), ArgumentMatchers.any(TimelineEntities.class), ArgumentMatchers.any(UserGroupInformation.class));
        Mockito.verify(writer, Mockito.never()).flush();
    }

    /**
     * Test TimelineCollector's interaction with TimelineWriter upon
     * putDomain() calls.
     */
    @Test
    public void testPutDomain() throws IOException {
        TimelineWriter writer = Mockito.mock(TimelineWriter.class);
        TimelineCollector collector = new TestTimelineCollector.TimelineCollectorForTest(writer);
        TimelineDomain domain = TestTimelineCollector.generateDomain("id", "desc", "owner", "reader1,reader2", "writer", 0L, 1L);
        collector.putDomain(domain, UserGroupInformation.createRemoteUser("owner"));
        Mockito.verify(writer, Mockito.times(1)).write(ArgumentMatchers.any(TimelineCollectorContext.class), ArgumentMatchers.any(TimelineDomain.class));
        Mockito.verify(writer, Mockito.times(1)).flush();
    }

    private static class TimelineCollectorForTest extends TimelineCollector {
        private final TimelineCollectorContext context = new TimelineCollectorContext();

        TimelineCollectorForTest(TimelineWriter writer) {
            super("TimelineCollectorForTest");
            setWriter(writer);
        }

        @Override
        public TimelineCollectorContext getTimelineEntityContext() {
            return context;
        }
    }

    @Test
    public void testClearPreviousEntitiesOnAggregation() throws Exception {
        final long ts = System.currentTimeMillis();
        TimelineCollector collector = new TimelineCollector("") {
            @Override
            public TimelineCollectorContext getTimelineEntityContext() {
                return new TimelineCollectorContext("cluster", "user", "flow", "1", 1L, ApplicationId.newInstance(ts, 1).toString());
            }
        };
        collector.init(new Configuration());
        collector.setWriter(Mockito.mock(TimelineWriter.class));
        // Put 5 entities with different metric values.
        TimelineEntities entities = new TimelineEntities();
        for (int i = 1; i <= 5; i++) {
            TimelineEntity entity = TestTimelineCollector.createEntity(("e" + i), "type");
            entity.addMetric(TestTimelineCollector.createDummyMetric((ts + i), Long.valueOf((i * 50))));
            entities.addEntity(entity);
        }
        collector.putEntities(entities, UserGroupInformation.getCurrentUser());
        TimelineCollectorContext currContext = collector.getTimelineEntityContext();
        // Aggregate the entities.
        Map<String, AggregationStatusTable> aggregationGroups = collector.getAggregationGroups();
        Assert.assertEquals(Sets.newHashSet("type"), aggregationGroups.keySet());
        TimelineEntity aggregatedEntity = TimelineCollector.aggregateWithoutGroupId(aggregationGroups, currContext.getAppId(), YARN_APPLICATION.toString());
        TimelineMetric aggregatedMetric = aggregatedEntity.getMetrics().iterator().next();
        Assert.assertEquals(750L, aggregatedMetric.getValues().values().iterator().next());
        Assert.assertEquals(SUM, aggregatedMetric.getRealtimeAggregationOp());
        // Aggregate entities.
        aggregatedEntity = TimelineCollector.aggregateWithoutGroupId(aggregationGroups, currContext.getAppId(), YARN_APPLICATION.toString());
        aggregatedMetric = aggregatedEntity.getMetrics().iterator().next();
        // No values aggregated as no metrics put for an entity between this
        // aggregation and the previous one.
        Assert.assertTrue(aggregatedMetric.getValues().isEmpty());
        Assert.assertEquals(NOP, aggregatedMetric.getRealtimeAggregationOp());
        // Put 3 entities.
        entities = new TimelineEntities();
        for (int i = 1; i <= 3; i++) {
            TimelineEntity entity = TestTimelineCollector.createEntity(("e" + i), "type");
            entity.addMetric(TestTimelineCollector.createDummyMetric(((System.currentTimeMillis()) + i), 50L));
            entities.addEntity(entity);
        }
        aggregationGroups = collector.getAggregationGroups();
        collector.putEntities(entities, UserGroupInformation.getCurrentUser());
        // Aggregate entities.
        aggregatedEntity = TimelineCollector.aggregateWithoutGroupId(aggregationGroups, currContext.getAppId(), YARN_APPLICATION.toString());
        // Last 3 entities picked up for aggregation.
        aggregatedMetric = aggregatedEntity.getMetrics().iterator().next();
        Assert.assertEquals(150L, aggregatedMetric.getValues().values().iterator().next());
        Assert.assertEquals(SUM, aggregatedMetric.getRealtimeAggregationOp());
        collector.close();
    }
}

