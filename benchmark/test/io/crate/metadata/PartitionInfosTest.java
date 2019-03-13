/**
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial agreement.
 */
package io.crate.metadata;


import Constants.DEFAULT_MAPPING_TYPE;
import IndexMetaData.Builder;
import com.google.common.collect.ImmutableList;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import java.util.Iterator;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.hamcrest.Matchers;
import org.junit.Test;


public class PartitionInfosTest extends CrateDummyClusterServiceUnitTest {
    @Test
    public void testIgnoreNoPartitions() throws Exception {
        addIndexMetaDataToClusterState(IndexMetaData.builder("test1").settings(PartitionInfosTest.defaultSettings()).numberOfShards(10).numberOfReplicas(4));
        Iterable<PartitionInfo> partitioninfos = new PartitionInfos(clusterService);
        assertThat(partitioninfos.iterator().hasNext(), Matchers.is(false));
    }

    @Test
    public void testPartitionWithoutMapping() throws Exception {
        PartitionName partitionName = new PartitionName(new RelationName("doc", "test1"), ImmutableList.of("foo"));
        addIndexMetaDataToClusterState(IndexMetaData.builder(partitionName.asIndexName()).settings(PartitionInfosTest.defaultSettings()).numberOfShards(10).numberOfReplicas(4));
        Iterable<PartitionInfo> partitioninfos = new PartitionInfos(clusterService);
        assertThat(partitioninfos.iterator().hasNext(), Matchers.is(false));
    }

    @Test
    public void testPartitionWithMeta() throws Exception {
        PartitionName partitionName = new PartitionName(new RelationName("doc", "test1"), ImmutableList.of("foo"));
        IndexMetaData.Builder indexMetaData = IndexMetaData.builder(partitionName.asIndexName()).settings(PartitionInfosTest.defaultSettings()).putMapping(DEFAULT_MAPPING_TYPE, "{\"_meta\":{\"partitioned_by\":[[\"col\", \"string\"]]}}").numberOfShards(10).numberOfReplicas(4);
        addIndexMetaDataToClusterState(indexMetaData);
        Iterable<PartitionInfo> partitioninfos = new PartitionInfos(clusterService);
        Iterator<PartitionInfo> iter = partitioninfos.iterator();
        PartitionInfo partitioninfo = iter.next();
        assertThat(partitioninfo.name().asIndexName(), Matchers.is(partitionName.asIndexName()));
        assertThat(partitioninfo.numberOfShards(), Matchers.is(10));
        assertThat(partitioninfo.numberOfReplicas(), Matchers.is("4"));
        assertThat(partitioninfo.values(), Matchers.hasEntry("col", "foo"));
        assertThat(iter.hasNext(), Matchers.is(false));
    }

    @Test
    public void testPartitionWithMetaMultiCol() throws Exception {
        PartitionName partitionName = new PartitionName(new RelationName("doc", "test1"), ImmutableList.of("foo", "1"));
        IndexMetaData.Builder indexMetaData = IndexMetaData.builder(partitionName.asIndexName()).settings(PartitionInfosTest.defaultSettings()).putMapping(DEFAULT_MAPPING_TYPE, "{\"_meta\":{\"partitioned_by\":[[\"col\", \"string\"], [\"col2\", \"integer\"]]}}").numberOfShards(10).numberOfReplicas(4);
        addIndexMetaDataToClusterState(indexMetaData);
        Iterable<PartitionInfo> partitioninfos = new PartitionInfos(clusterService);
        Iterator<PartitionInfo> iter = partitioninfos.iterator();
        PartitionInfo partitioninfo = iter.next();
        assertThat(partitioninfo.name().asIndexName(), Matchers.is(partitionName.asIndexName()));
        assertThat(partitioninfo.numberOfShards(), Matchers.is(10));
        assertThat(partitioninfo.numberOfReplicas(), Matchers.is("4"));
        assertThat(partitioninfo.values(), Matchers.hasEntry("col", "foo"));
        assertThat(partitioninfo.values(), Matchers.hasEntry("col2", 1));
        assertThat(iter.hasNext(), Matchers.is(false));
    }
}

