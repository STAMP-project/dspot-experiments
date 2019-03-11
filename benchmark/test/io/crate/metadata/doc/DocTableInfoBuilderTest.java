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
package io.crate.metadata.doc;


import Constants.DEFAULT_MAPPING_TYPE;
import IndexMetaData.Builder;
import Version.CURRENT;
import io.crate.exceptions.RelationUnknown;
import io.crate.metadata.Functions;
import io.crate.metadata.PartitionName;
import io.crate.metadata.RelationName;
import io.crate.test.integration.CrateUnitTest;
import io.crate.testing.TestingHelpers;
import java.util.Collections;
import java.util.Locale;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MetaData;
import org.elasticsearch.common.settings.Settings;
import org.junit.Test;


public class DocTableInfoBuilderTest extends CrateUnitTest {
    private Functions functions = TestingHelpers.getFunctions();

    @Test
    public void testNoTableInfoFromOrphanedPartition() throws Exception {
        String schemaName = randomSchema();
        PartitionName partitionName = new PartitionName(new RelationName(schemaName, "test"), Collections.singletonList("boo"));
        IndexMetaData.Builder indexMetaDataBuilder = IndexMetaData.builder(partitionName.asIndexName()).settings(Settings.builder().put("index.version.created", CURRENT).build()).numberOfReplicas(0).numberOfShards(5).putMapping(DEFAULT_MAPPING_TYPE, ("{" + (((((((("  \"default\": {" + "    \"properties\":{") + "      \"id\": {") + "         \"type\": \"integer\",") + "         \"index\": \"not_analyzed\"") + "      }") + "    }") + "  }") + "}")));
        MetaData metaData = MetaData.builder().put(indexMetaDataBuilder).build();
        ClusterState state = ClusterState.builder(ClusterName.DEFAULT).metaData(metaData).build();
        DocTableInfoBuilder builder = new DocTableInfoBuilder(functions, new RelationName(schemaName, "test"), state, new org.elasticsearch.cluster.metadata.IndexNameExpressionResolver(Settings.EMPTY));
        expectedException.expect(RelationUnknown.class);
        expectedException.expectMessage(String.format(Locale.ENGLISH, "Relation '%s.test' unknown", schemaName));
        builder.build();
    }
}

