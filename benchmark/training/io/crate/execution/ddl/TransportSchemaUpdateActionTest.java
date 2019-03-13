/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.execution.ddl;


import Constants.DEFAULT_MAPPING_TYPE;
import IndexMappings.VERSION_STRING;
import NamedXContentRegistry.EMPTY;
import io.crate.analyze.AddColumnAnalyzedStatement;
import io.crate.metadata.PartitionName;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.testing.SQLExecutor;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.metadata.IndexTemplateMetaData;
import org.elasticsearch.cluster.metadata.MetaData;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;


public class TransportSchemaUpdateActionTest extends CrateDummyClusterServiceUnitTest {
    @Test
    public void testTemplateMappingUpdateFailsIfTypeIsDifferent() throws Exception {
        SQLExecutor e = SQLExecutor.builder(clusterService).addPartitionedTable("create table t (p int) partitioned by (p)").build();
        ClusterState currentState = clusterService.state();
        AddColumnAnalyzedStatement addXLong = e.analyze("alter table t add column x long");
        AddColumnAnalyzedStatement addXString = e.analyze("alter table t add column x string");
        String templateName = PartitionName.templateName("doc", "t");
        IndexTemplateMetaData template = currentState.metaData().templates().get(templateName);
        ClusterState stateWithXLong = ClusterState.builder(currentState).metaData(MetaData.builder(currentState.metaData()).put(IndexTemplateMetaData.builder(templateName).patterns(template.patterns()).putMapping(DEFAULT_MAPPING_TYPE, Strings.toString(JsonXContent.contentBuilder().map(addXLong.analyzedTableElements().toMapping())))).build()).build();
        expectedException.expect(IllegalArgumentException.class);
        TransportSchemaUpdateAction.updateTemplate(EMPTY, stateWithXLong, templateName, addXString.analyzedTableElements().toMapping());
    }

    @Test
    public void testDynamicTrueCanBeChangedFromBooleanToStringValue() {
        HashMap<String, Object> source = new HashMap<>();
        source.put("dynamic", true);
        TransportSchemaUpdateAction.mergeIntoSource(source, Collections.singletonMap("dynamic", "true"));
        assertThat(source.get("dynamic"), Matchers.is("true"));
    }

    @Test
    public void testVersionChangesAreIgnored() {
        HashMap<String, Object> source = new HashMap<>();
        String versionKey = "cratedb";
        source.put(versionKey, 100);
        TransportSchemaUpdateAction.mergeIntoSource(source, Collections.singletonMap(versionKey, 200), Arrays.asList("default", "_meta", VERSION_STRING, versionKey));
        assertThat(source.get(versionKey), Is.is(100));
    }
}

