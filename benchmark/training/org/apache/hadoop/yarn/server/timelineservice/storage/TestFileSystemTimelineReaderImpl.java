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
package org.apache.hadoop.yarn.server.timelineservice.storage;


import Field.ALL;
import Field.CONFIGS;
import Field.INFO;
import Field.METRICS;
import java.io.File;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import org.apache.hadoop.yarn.api.records.timelineservice.TimelineEntity;
import org.apache.hadoop.yarn.server.timelineservice.reader.TimelineDataToRetrieve;
import org.apache.hadoop.yarn.server.timelineservice.reader.TimelineEntityFilters;
import org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext;
import org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareOp;
import org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineFilterList;
import org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineFilterList.Operator;
import org.junit.Assert;
import org.junit.Test;


public class TestFileSystemTimelineReaderImpl {
    private static final String ROOT_DIR = new File("target", TestFileSystemTimelineReaderImpl.class.getSimpleName()).getAbsolutePath();

    private FileSystemTimelineReaderImpl reader;

    @Test
    public void testGetEntityDefaultView() throws Exception {
        // If no fields are specified, entity is returned with default view i.e.
        // only the id, type and created time.
        TimelineEntity result = reader.getEntity(new TimelineReaderContext("cluster1", "user1", "flow1", 1L, "app1", "app", "id_1"), new TimelineDataToRetrieve(null, null, null, null, null, null));
        Assert.assertEquals(new TimelineEntity.Identifier("app", "id_1").toString(), result.getIdentifier().toString());
        Assert.assertEquals(((Long) (1425016502000L)), result.getCreatedTime());
        Assert.assertEquals(0, result.getConfigs().size());
        Assert.assertEquals(0, result.getMetrics().size());
    }

    @Test
    public void testGetEntityByClusterAndApp() throws Exception {
        // Cluster and AppId should be enough to get an entity.
        TimelineEntity result = reader.getEntity(new TimelineReaderContext("cluster1", null, null, null, "app1", "app", "id_1"), new TimelineDataToRetrieve(null, null, null, null, null, null));
        Assert.assertEquals(new TimelineEntity.Identifier("app", "id_1").toString(), result.getIdentifier().toString());
        Assert.assertEquals(((Long) (1425016502000L)), result.getCreatedTime());
        Assert.assertEquals(0, result.getConfigs().size());
        Assert.assertEquals(0, result.getMetrics().size());
    }

    /**
     * This test checks whether we can handle commas in app flow mapping csv.
     */
    @Test
    public void testAppFlowMappingCsv() throws Exception {
        // Test getting an entity by cluster and app where flow entry
        // in app flow mapping csv has commas.
        TimelineEntity result = reader.getEntity(new TimelineReaderContext("cluster1", null, null, null, "app2", "app", "id_5"), new TimelineDataToRetrieve(null, null, null, null, null, null));
        Assert.assertEquals(new TimelineEntity.Identifier("app", "id_5").toString(), result.getIdentifier().toString());
        Assert.assertEquals(((Long) (1425016502050L)), result.getCreatedTime());
    }

    @Test
    public void testGetEntityCustomFields() throws Exception {
        // Specified fields in addition to default view will be returned.
        TimelineEntity result = reader.getEntity(new TimelineReaderContext("cluster1", "user1", "flow1", 1L, "app1", "app", "id_1"), new TimelineDataToRetrieve(null, null, EnumSet.of(INFO, CONFIGS, METRICS), null, null, null));
        Assert.assertEquals(new TimelineEntity.Identifier("app", "id_1").toString(), result.getIdentifier().toString());
        Assert.assertEquals(((Long) (1425016502000L)), result.getCreatedTime());
        Assert.assertEquals(3, result.getConfigs().size());
        Assert.assertEquals(3, result.getMetrics().size());
        Assert.assertEquals(2, result.getInfo().size());
        // No events will be returned
        Assert.assertEquals(0, result.getEvents().size());
    }

    @Test
    public void testGetEntityAllFields() throws Exception {
        // All fields of TimelineEntity will be returned.
        TimelineEntity result = reader.getEntity(new TimelineReaderContext("cluster1", "user1", "flow1", 1L, "app1", "app", "id_1"), new TimelineDataToRetrieve(null, null, EnumSet.of(ALL), null, null, null));
        Assert.assertEquals(new TimelineEntity.Identifier("app", "id_1").toString(), result.getIdentifier().toString());
        Assert.assertEquals(((Long) (1425016502000L)), result.getCreatedTime());
        Assert.assertEquals(3, result.getConfigs().size());
        Assert.assertEquals(3, result.getMetrics().size());
        // All fields including events will be returned.
        Assert.assertEquals(2, result.getEvents().size());
    }

    @Test
    public void testGetAllEntities() throws Exception {
        Set<TimelineEntity> result = reader.getEntities(new TimelineReaderContext("cluster1", "user1", "flow1", 1L, "app1", "app", null), new TimelineEntityFilters.Builder().build(), new TimelineDataToRetrieve(null, null, EnumSet.of(ALL), null, null, null));
        // All 4 entities will be returned
        Assert.assertEquals(4, result.size());
    }

    @Test
    public void testGetEntitiesWithLimit() throws Exception {
        Set<TimelineEntity> result = reader.getEntities(new TimelineReaderContext("cluster1", "user1", "flow1", 1L, "app1", "app", null), new TimelineEntityFilters.Builder().entityLimit(2L).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(2, result.size());
        // Needs to be rewritten once hashcode and equals for
        // TimelineEntity is implemented
        // Entities with id_1 and id_4 should be returned,
        // based on created time, descending.
        for (TimelineEntity entity : result) {
            if ((!(entity.getId().equals("id_1"))) && (!(entity.getId().equals("id_4")))) {
                Assert.fail("Entity not sorted by created time");
            }
        }
        result = reader.getEntities(new TimelineReaderContext("cluster1", "user1", "flow1", 1L, "app1", "app", null), new TimelineEntityFilters.Builder().entityLimit(3L).build(), new TimelineDataToRetrieve());
        // Even though 2 entities out of 4 have same created time, one entity
        // is left out due to limit
        Assert.assertEquals(3, result.size());
    }

    @Test
    public void testGetEntitiesByTimeWindows() throws Exception {
        // Get entities based on created time start and end time range.
        Set<TimelineEntity> result = reader.getEntities(new TimelineReaderContext("cluster1", "user1", "flow1", 1L, "app1", "app", null), new TimelineEntityFilters.Builder().createdTimeBegin(1425016502030L).createTimeEnd(1425016502060L).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(1, result.size());
        // Only one entity with ID id_4 should be returned.
        for (TimelineEntity entity : result) {
            if (!(entity.getId().equals("id_4"))) {
                Assert.fail("Incorrect filtering based on created time range");
            }
        }
        // Get entities if only created time end is specified.
        result = reader.getEntities(new TimelineReaderContext("cluster1", "user1", "flow1", 1L, "app1", "app", null), new TimelineEntityFilters.Builder().createTimeEnd(1425016502010L).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(3, result.size());
        for (TimelineEntity entity : result) {
            if (entity.getId().equals("id_4")) {
                Assert.fail("Incorrect filtering based on created time range");
            }
        }
        // Get entities if only created time start is specified.
        result = reader.getEntities(new TimelineReaderContext("cluster1", "user1", "flow1", 1L, "app1", "app", null), new TimelineEntityFilters.Builder().createdTimeBegin(1425016502010L).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(1, result.size());
        for (TimelineEntity entity : result) {
            if (!(entity.getId().equals("id_4"))) {
                Assert.fail("Incorrect filtering based on created time range");
            }
        }
    }

    @Test
    public void testGetFilteredEntities() throws Exception {
        // Get entities based on info filters.
        TimelineFilterList infoFilterList = new TimelineFilterList();
        infoFilterList.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "info2", 3.5));
        Set<TimelineEntity> result = reader.getEntities(new TimelineReaderContext("cluster1", "user1", "flow1", 1L, "app1", "app", null), new TimelineEntityFilters.Builder().infoFilters(infoFilterList).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(1, result.size());
        // Only one entity with ID id_3 should be returned.
        for (TimelineEntity entity : result) {
            if (!(entity.getId().equals("id_3"))) {
                Assert.fail("Incorrect filtering based on info filters");
            }
        }
        // Get entities based on config filters.
        TimelineFilterList confFilterList = new TimelineFilterList();
        confFilterList.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "config_1", "123"));
        confFilterList.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "config_3", "abc"));
        result = reader.getEntities(new TimelineReaderContext("cluster1", "user1", "flow1", 1L, "app1", "app", null), new TimelineEntityFilters.Builder().configFilters(confFilterList).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(1, result.size());
        for (TimelineEntity entity : result) {
            if (!(entity.getId().equals("id_3"))) {
                Assert.fail("Incorrect filtering based on config filters");
            }
        }
        // Get entities based on event filters.
        TimelineFilterList eventFilters = new TimelineFilterList();
        eventFilters.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineExistsFilter(TimelineCompareOp.EQUAL, "event_2"));
        eventFilters.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineExistsFilter(TimelineCompareOp.EQUAL, "event_4"));
        result = reader.getEntities(new TimelineReaderContext("cluster1", "user1", "flow1", 1L, "app1", "app", null), new TimelineEntityFilters.Builder().eventFilters(eventFilters).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(1, result.size());
        for (TimelineEntity entity : result) {
            if (!(entity.getId().equals("id_3"))) {
                Assert.fail("Incorrect filtering based on event filters");
            }
        }
        // Get entities based on metric filters.
        TimelineFilterList metricFilterList = new TimelineFilterList();
        metricFilterList.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.GREATER_OR_EQUAL, "metric3", 0L));
        result = reader.getEntities(new TimelineReaderContext("cluster1", "user1", "flow1", 1L, "app1", "app", null), new TimelineEntityFilters.Builder().metricFilters(metricFilterList).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(2, result.size());
        // Two entities with IDs' id_1 and id_2 should be returned.
        for (TimelineEntity entity : result) {
            if ((!(entity.getId().equals("id_1"))) && (!(entity.getId().equals("id_2")))) {
                Assert.fail("Incorrect filtering based on metric filters");
            }
        }
        // Get entities based on complex config filters.
        TimelineFilterList list1 = new TimelineFilterList();
        list1.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "config_1", "129"));
        list1.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "config_3", "def"));
        TimelineFilterList list2 = new TimelineFilterList();
        list2.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "config_2", "23"));
        list2.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "config_3", "abc"));
        TimelineFilterList confFilterList1 = new TimelineFilterList(Operator.OR, list1, list2);
        result = reader.getEntities(new TimelineReaderContext("cluster1", "user1", "flow1", 1L, "app1", "app", null), new TimelineEntityFilters.Builder().configFilters(confFilterList1).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(2, result.size());
        for (TimelineEntity entity : result) {
            if ((!(entity.getId().equals("id_1"))) && (!(entity.getId().equals("id_2")))) {
                Assert.fail("Incorrect filtering based on config filters");
            }
        }
        TimelineFilterList list3 = new TimelineFilterList();
        list3.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.NOT_EQUAL, "config_1", "123"));
        list3.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.NOT_EQUAL, "config_3", "abc"));
        TimelineFilterList list4 = new TimelineFilterList();
        list4.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "config_2", "23"));
        TimelineFilterList confFilterList2 = new TimelineFilterList(Operator.OR, list3, list4);
        result = reader.getEntities(new TimelineReaderContext("cluster1", "user1", "flow1", 1L, "app1", "app", null), new TimelineEntityFilters.Builder().configFilters(confFilterList2).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(2, result.size());
        for (TimelineEntity entity : result) {
            if ((!(entity.getId().equals("id_1"))) && (!(entity.getId().equals("id_2")))) {
                Assert.fail("Incorrect filtering based on config filters");
            }
        }
        TimelineFilterList confFilterList3 = new TimelineFilterList();
        confFilterList3.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.NOT_EQUAL, "config_1", "127"));
        confFilterList3.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.NOT_EQUAL, "config_3", "abc"));
        result = reader.getEntities(new TimelineReaderContext("cluster1", "user1", "flow1", 1L, "app1", "app", null), new TimelineEntityFilters.Builder().configFilters(confFilterList3).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(1, result.size());
        for (TimelineEntity entity : result) {
            if (!(entity.getId().equals("id_2"))) {
                Assert.fail("Incorrect filtering based on config filters");
            }
        }
        TimelineFilterList confFilterList4 = new TimelineFilterList();
        confFilterList4.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "config_dummy", "dummy"));
        confFilterList4.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "config_3", "def"));
        result = reader.getEntities(new TimelineReaderContext("cluster1", "user1", "flow1", 1L, "app1", "app", null), new TimelineEntityFilters.Builder().configFilters(confFilterList4).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(0, result.size());
        TimelineFilterList confFilterList5 = new TimelineFilterList(Operator.OR);
        confFilterList5.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "config_dummy", "dummy"));
        confFilterList5.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "config_3", "def"));
        result = reader.getEntities(new TimelineReaderContext("cluster1", "user1", "flow1", 1L, "app1", "app", null), new TimelineEntityFilters.Builder().configFilters(confFilterList5).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(1, result.size());
        for (TimelineEntity entity : result) {
            if (!(entity.getId().equals("id_2"))) {
                Assert.fail("Incorrect filtering based on config filters");
            }
        }
        // Get entities based on complex metric filters.
        TimelineFilterList list6 = new TimelineFilterList();
        list6.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.GREATER_THAN, "metric1", 200));
        list6.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.EQUAL, "metric3", 23));
        TimelineFilterList list7 = new TimelineFilterList();
        list7.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.GREATER_OR_EQUAL, "metric2", 74));
        TimelineFilterList metricFilterList1 = new TimelineFilterList(Operator.OR, list6, list7);
        result = reader.getEntities(new TimelineReaderContext("cluster1", "user1", "flow1", 1L, "app1", "app", null), new TimelineEntityFilters.Builder().metricFilters(metricFilterList1).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(2, result.size());
        // Two entities with IDs' id_2 and id_3 should be returned.
        for (TimelineEntity entity : result) {
            if ((!(entity.getId().equals("id_2"))) && (!(entity.getId().equals("id_3")))) {
                Assert.fail("Incorrect filtering based on metric filters");
            }
        }
        TimelineFilterList metricFilterList2 = new TimelineFilterList();
        metricFilterList2.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.LESS_THAN, "metric2", 70));
        metricFilterList2.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.LESS_OR_EQUAL, "metric3", 23));
        result = reader.getEntities(new TimelineReaderContext("cluster1", "user1", "flow1", 1L, "app1", "app", null), new TimelineEntityFilters.Builder().metricFilters(metricFilterList2).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(1, result.size());
        for (TimelineEntity entity : result) {
            if (!(entity.getId().equals("id_1"))) {
                Assert.fail("Incorrect filtering based on metric filters");
            }
        }
        TimelineFilterList metricFilterList3 = new TimelineFilterList();
        metricFilterList3.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.LESS_THAN, "dummy_metric", 30));
        metricFilterList3.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.LESS_OR_EQUAL, "metric3", 23));
        result = reader.getEntities(new TimelineReaderContext("cluster1", "user1", "flow1", 1L, "app1", "app", null), new TimelineEntityFilters.Builder().metricFilters(metricFilterList3).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(0, result.size());
        TimelineFilterList metricFilterList4 = new TimelineFilterList(Operator.OR);
        metricFilterList4.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.LESS_THAN, "dummy_metric", 30));
        metricFilterList4.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.LESS_OR_EQUAL, "metric3", 23));
        result = reader.getEntities(new TimelineReaderContext("cluster1", "user1", "flow1", 1L, "app1", "app", null), new TimelineEntityFilters.Builder().metricFilters(metricFilterList4).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(2, result.size());
        for (TimelineEntity entity : result) {
            if ((!(entity.getId().equals("id_1"))) && (!(entity.getId().equals("id_2")))) {
                Assert.fail("Incorrect filtering based on metric filters");
            }
        }
        TimelineFilterList metricFilterList5 = new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.NOT_EQUAL, "metric2", 74));
        result = reader.getEntities(new TimelineReaderContext("cluster1", "user1", "flow1", 1L, "app1", "app", null), new TimelineEntityFilters.Builder().metricFilters(metricFilterList5).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(2, result.size());
        for (TimelineEntity entity : result) {
            if ((!(entity.getId().equals("id_1"))) && (!(entity.getId().equals("id_2")))) {
                Assert.fail("Incorrect filtering based on metric filters");
            }
        }
        TimelineFilterList infoFilterList1 = new TimelineFilterList();
        infoFilterList1.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "info2", 3.5));
        infoFilterList1.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.NOT_EQUAL, "info4", 20));
        result = reader.getEntities(new TimelineReaderContext("cluster1", "user1", "flow1", 1L, "app1", "app", null), new TimelineEntityFilters.Builder().infoFilters(infoFilterList1).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(0, result.size());
        TimelineFilterList infoFilterList2 = new TimelineFilterList(Operator.OR);
        infoFilterList2.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "info2", 3.5));
        infoFilterList2.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "info1", "val1"));
        result = reader.getEntities(new TimelineReaderContext("cluster1", "user1", "flow1", 1L, "app1", "app", null), new TimelineEntityFilters.Builder().infoFilters(infoFilterList2).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(2, result.size());
        for (TimelineEntity entity : result) {
            if ((!(entity.getId().equals("id_1"))) && (!(entity.getId().equals("id_3")))) {
                Assert.fail("Incorrect filtering based on info filters");
            }
        }
        TimelineFilterList infoFilterList3 = new TimelineFilterList();
        infoFilterList3.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "dummy_info", 1));
        infoFilterList3.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "info2", "val5"));
        result = reader.getEntities(new TimelineReaderContext("cluster1", "user1", "flow1", 1L, "app1", "app", null), new TimelineEntityFilters.Builder().infoFilters(infoFilterList3).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(0, result.size());
        TimelineFilterList infoFilterList4 = new TimelineFilterList(Operator.OR);
        infoFilterList4.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "dummy_info", 1));
        infoFilterList4.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "info2", "val5"));
        result = reader.getEntities(new TimelineReaderContext("cluster1", "user1", "flow1", 1L, "app1", "app", null), new TimelineEntityFilters.Builder().infoFilters(infoFilterList4).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(1, result.size());
        for (TimelineEntity entity : result) {
            if (!(entity.getId().equals("id_1"))) {
                Assert.fail("Incorrect filtering based on info filters");
            }
        }
    }

    @Test
    public void testGetEntitiesByRelations() throws Exception {
        // Get entities based on relatesTo.
        TimelineFilterList relatesTo = new TimelineFilterList(Operator.OR);
        Set<Object> relatesToIds = new HashSet<Object>(Arrays.asList(((Object) ("flow1"))));
        relatesTo.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "flow", relatesToIds));
        Set<TimelineEntity> result = reader.getEntities(new TimelineReaderContext("cluster1", "user1", "flow1", 1L, "app1", "app", null), new TimelineEntityFilters.Builder().relatesTo(relatesTo).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(1, result.size());
        // Only one entity with ID id_1 should be returned.
        for (TimelineEntity entity : result) {
            if (!(entity.getId().equals("id_1"))) {
                Assert.fail("Incorrect filtering based on relatesTo");
            }
        }
        // Get entities based on isRelatedTo.
        TimelineFilterList isRelatedTo = new TimelineFilterList(Operator.OR);
        Set<Object> isRelatedToIds = new HashSet<Object>(Arrays.asList(((Object) ("tid1_2"))));
        isRelatedTo.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "type1", isRelatedToIds));
        result = reader.getEntities(new TimelineReaderContext("cluster1", "user1", "flow1", 1L, "app1", "app", null), new TimelineEntityFilters.Builder().isRelatedTo(isRelatedTo).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(2, result.size());
        // Two entities with IDs' id_1 and id_3 should be returned.
        for (TimelineEntity entity : result) {
            if ((!(entity.getId().equals("id_1"))) && (!(entity.getId().equals("id_3")))) {
                Assert.fail("Incorrect filtering based on isRelatedTo");
            }
        }
    }
}

