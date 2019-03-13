/**
 * Licensed to Crate.IO GmbH ("Crate") under one or more contributor
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
package io.crate.expression.reference.sys.check.cluster;


import GatewayService.RECOVER_AFTER_MASTER_NODES_SETTING;
import Settings.EMPTY;
import Severity.HIGH;
import Severity.MEDIUM;
import com.google.common.collect.ImmutableList;
import io.crate.metadata.ClusterReferenceResolver;
import io.crate.metadata.PartitionName;
import io.crate.metadata.Schemas;
import io.crate.metadata.doc.DocSchemaInfo;
import io.crate.metadata.doc.DocTableInfo;
import io.crate.metadata.table.SchemaInfo;
import io.crate.test.integration.CrateUnitTest;
import java.util.List;
import org.elasticsearch.cluster.service.ClusterService;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockito.Mockito;


public class SysChecksTest extends CrateUnitTest {
    private final ClusterService clusterService = Mockito.mock(ClusterService.class);

    private final ClusterReferenceResolver referenceResolver = Mockito.mock(ClusterReferenceResolver.class);

    private final SchemaInfo docSchemaInfo = Mockito.mock(DocSchemaInfo.class);

    private final DocTableInfo docTableInfo = Mockito.mock(DocTableInfo.class);

    @Test
    public void testMaxMasterNodesCheckWithEmptySetting() {
        MinMasterNodesSysCheck minMasterNodesCheck = new MinMasterNodesSysCheck(clusterService, referenceResolver);
        assertThat(minMasterNodesCheck.id(), Is.is(1));
        assertThat(minMasterNodesCheck.severity(), Is.is(HIGH));
        assertThat(minMasterNodesCheck.validate(2, RECOVER_AFTER_MASTER_NODES_SETTING.getDefault(EMPTY)), Is.is(false));
    }

    @Test
    public void testMaxMasterNodesCheckWithCorrectSetting() {
        MinMasterNodesSysCheck minMasterNodesCheck = new MinMasterNodesSysCheck(clusterService, referenceResolver);
        assertThat(minMasterNodesCheck.id(), Is.is(1));
        assertThat(minMasterNodesCheck.severity(), Is.is(HIGH));
        assertThat(minMasterNodesCheck.validate(8, 5), Is.is(true));
    }

    @Test
    public void testMaxMasterNodesCheckWithLessThanQuorum() {
        MinMasterNodesSysCheck minMasterNodesCheck = new MinMasterNodesSysCheck(clusterService, referenceResolver);
        assertThat(minMasterNodesCheck.id(), Is.is(1));
        assertThat(minMasterNodesCheck.severity(), Is.is(HIGH));
        assertThat(minMasterNodesCheck.validate(6, 3), Is.is(false));
    }

    @Test
    public void testMaxMasterNodesCheckWithGreaterThanNodes() {
        MinMasterNodesSysCheck minMasterNodesCheck = new MinMasterNodesSysCheck(clusterService, referenceResolver);
        assertThat(minMasterNodesCheck.id(), Is.is(1));
        assertThat(minMasterNodesCheck.severity(), Is.is(HIGH));
        assertThat(minMasterNodesCheck.validate(6, 7), Is.is(false));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testNumberOfPartitionCorrectPartitioning() {
        NumberOfPartitionsSysCheck numberOfPartitionsSysCheck = new NumberOfPartitionsSysCheck(Mockito.mock(Schemas.class));
        Mockito.when(docSchemaInfo.getTables()).thenReturn(ImmutableList.of(docTableInfo, docTableInfo));
        Mockito.when(docTableInfo.isPartitioned()).thenReturn(true);
        List<PartitionName> partitionsFirst = buildPartitions(500);
        List<PartitionName> partitionsSecond = buildPartitions(100);
        Mockito.when(docTableInfo.partitions()).thenReturn(partitionsFirst, partitionsSecond);
        assertThat(numberOfPartitionsSysCheck.id(), Is.is(2));
        assertThat(numberOfPartitionsSysCheck.severity(), Is.is(MEDIUM));
        assertThat(numberOfPartitionsSysCheck.validateDocTablesPartitioning(docSchemaInfo), Is.is(true));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testNumberOfPartitionsWrongPartitioning() {
        NumberOfPartitionsSysCheck numberOfPartitionsSysCheck = new NumberOfPartitionsSysCheck(Mockito.mock(Schemas.class));
        List<PartitionName> partitions = buildPartitions(1001);
        Mockito.when(docSchemaInfo.getTables()).thenReturn(ImmutableList.of(docTableInfo));
        Mockito.when(docTableInfo.isPartitioned()).thenReturn(true);
        Mockito.when(docTableInfo.partitions()).thenReturn(partitions);
        assertThat(numberOfPartitionsSysCheck.id(), Is.is(2));
        assertThat(numberOfPartitionsSysCheck.severity(), Is.is(MEDIUM));
        assertThat(numberOfPartitionsSysCheck.validateDocTablesPartitioning(docSchemaInfo), Is.is(false));
    }
}

