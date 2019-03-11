/**
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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
package io.crate.execution.engine.collect;


import ESIntegTestCase.ClusterScope;
import EqOperator.NAME;
import RoutingProvider.ShardSelection.ANY;
import WhereClause.MATCH_ALL;
import io.crate.action.sql.SessionContext;
import io.crate.analyze.WhereClause;
import io.crate.data.Bucket;
import io.crate.data.Row;
import io.crate.execution.dsl.phases.RoutedCollectPhase;
import io.crate.expression.operator.EqOperator;
import io.crate.expression.symbol.Literal;
import io.crate.expression.symbol.Symbol;
import io.crate.integrationtests.SQLTransportIntegrationTest;
import io.crate.metadata.Functions;
import io.crate.metadata.Reference;
import io.crate.metadata.ReferenceIdent;
import io.crate.metadata.RelationName;
import io.crate.metadata.Routing;
import io.crate.metadata.RowGranularity;
import io.crate.metadata.Schemas;
import io.crate.metadata.SearchPath;
import io.crate.testing.TestingHelpers;
import io.crate.testing.UseRandomizedSchema;
import io.crate.types.DataTypes;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.elasticsearch.common.Randomness;
import org.hamcrest.Matchers;
import org.junit.Test;


@ClusterScope(numDataNodes = 1)
@UseRandomizedSchema(random = false)
public class DocLevelCollectTest extends SQLTransportIntegrationTest {
    private static final String TEST_TABLE_NAME = "test_table";

    private static final Reference testDocLevelReference = new Reference(new ReferenceIdent(new RelationName(Schemas.DOC_SCHEMA_NAME, DocLevelCollectTest.TEST_TABLE_NAME), "doc"), RowGranularity.DOC, DataTypes.INTEGER);

    private static final Reference underscoreIdReference = new Reference(new ReferenceIdent(new RelationName(Schemas.DOC_SCHEMA_NAME, DocLevelCollectTest.TEST_TABLE_NAME), "_id"), RowGranularity.DOC, DataTypes.STRING);

    private static final Reference underscoreRawReference = new Reference(new ReferenceIdent(new RelationName(Schemas.DOC_SCHEMA_NAME, DocLevelCollectTest.TEST_TABLE_NAME), "_raw"), RowGranularity.DOC, DataTypes.STRING);

    private static final String PARTITIONED_TABLE_NAME = "parted_table";

    private Functions functions;

    private Schemas schemas;

    @Test
    public void testCollectDocLevel() throws Throwable {
        List<Symbol> toCollect = Arrays.asList(DocLevelCollectTest.testDocLevelReference, DocLevelCollectTest.underscoreIdReference);
        RoutedCollectPhase collectNode = getCollectNode(toCollect, MATCH_ALL);
        Bucket result = collect(collectNode);
        assertThat(result, Matchers.containsInAnyOrder(TestingHelpers.isRow(2, "1"), TestingHelpers.isRow(4, "3")));
    }

    @Test
    public void testCollectDocLevelWhereClause() throws Throwable {
        List<Symbol> arguments = Arrays.asList(DocLevelCollectTest.testDocLevelReference, Literal.of(2));
        EqOperator op = ((EqOperator) (functions.get(null, NAME, arguments, SearchPath.pathWithPGCatalogAndDoc())));
        List<Symbol> toCollect = Collections.singletonList(DocLevelCollectTest.testDocLevelReference);
        WhereClause whereClause = new WhereClause(new io.crate.expression.symbol.Function(op.info(), arguments));
        RoutedCollectPhase collectNode = getCollectNode(toCollect, whereClause);
        Bucket result = collect(collectNode);
        assertThat(result, Matchers.contains(TestingHelpers.isRow(2)));
    }

    @Test
    public void testCollectWithPartitionedColumns() throws Throwable {
        RelationName relationName = new RelationName(Schemas.DOC_SCHEMA_NAME, DocLevelCollectTest.PARTITIONED_TABLE_NAME);
        Routing routing = schemas.getTableInfo(relationName).getRouting(clusterService().state(), new io.crate.metadata.RoutingProvider(Randomness.get().nextInt(), Collections.emptyList()), MATCH_ALL, ANY, SessionContext.systemSessionContext());
        RoutedCollectPhase collectNode = getCollectNode(Arrays.asList(new Reference(new ReferenceIdent(relationName, "id"), RowGranularity.DOC, DataTypes.INTEGER), new Reference(new ReferenceIdent(relationName, "date"), RowGranularity.SHARD, DataTypes.TIMESTAMP)), routing, MATCH_ALL);
        Bucket result = collect(collectNode);
        for (Row row : result) {
            System.out.println(("Row:" + (Arrays.toString(row.materialize()))));
        }
        assertThat(result, Matchers.containsInAnyOrder(TestingHelpers.isRow(1, 0L), TestingHelpers.isRow(2, 1L)));
    }
}

