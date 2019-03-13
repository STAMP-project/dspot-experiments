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
package io.crate.planner;


import WhereClauseOptimizer.DetailedQuery;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.testing.SQLExecutor;
import io.crate.testing.SymbolMatchers;
import io.crate.testing.TestingHelpers;
import org.hamcrest.Matchers;
import org.junit.Test;


public class WhereClauseOptimizerTest extends CrateDummyClusterServiceUnitTest {
    private SQLExecutor e;

    @Test
    public void testFilterOn_IdDoesNotProduceDocKeysIfTableHasOnlyAClusteredByDefinition() {
        WhereClauseOptimizer.DetailedQuery query = optimize("select * from clustered_by_only where _id = '1'");
        assertThat(query.docKeys().isPresent(), Matchers.is(false));
    }

    @Test
    public void testFilterOn_IdOnPartitionedTableDoesNotResultInDocKeys() {
        WhereClauseOptimizer.DetailedQuery query = optimize("select * from parted where _id = '1'");
        assertThat(query.docKeys().isPresent(), Matchers.is(false));
    }

    @Test
    public void testFilterOnPartitionColumnAndPrimaryKeyResultsInDocKeys() {
        WhereClauseOptimizer.DetailedQuery query = optimize("select * from parted_pk where id = 1 and date = 1395874800000");
        assertThat(query.docKeys().toString(), Matchers.is("Optional[DocKeys{1, 1395874800000}]"));
        assertThat(query.partitions(), Matchers.empty());
    }

    @Test
    public void testClusteredByValueContainsComma() throws Exception {
        WhereClauseOptimizer.DetailedQuery query = optimize("select * from bystring where name = 'a,b,c'");
        assertThat(query.clusteredBy(), Matchers.contains(SymbolMatchers.isLiteral("a,b,c")));
        assertThat(query.docKeys().get().size(), Matchers.is(1));
        assertThat(query.docKeys().get().getOnlyKey(), TestingHelpers.isDocKey("a,b,c"));
    }

    @Test
    public void testEmptyClusteredByValue() throws Exception {
        WhereClauseOptimizer.DetailedQuery query = optimize("select * from bystring where name = ''");
        assertThat(query.clusteredBy(), Matchers.contains(SymbolMatchers.isLiteral("")));
        assertThat(query.docKeys().get().getOnlyKey(), TestingHelpers.isDocKey(""));
    }

    @Test
    public void testFilterOnClusteredByColumnDoesNotResultInDocKeysSimpleEq() {
        WhereClauseOptimizer.DetailedQuery query = optimize("select * from clustered_by_only where x = 10");
        assertThat(query.clusteredBy(), Matchers.contains(SymbolMatchers.isLiteral(10)));
        assertThat(query.docKeys().isPresent(), Matchers.is(false));
    }

    @Test
    public void testFilterOnClusteredByColumnDoesNotResultInDocKeysSimpleEqOr() {
        WhereClauseOptimizer.DetailedQuery query = optimize("select * from clustered_by_only where x = 10 or x = 20");
        assertThat(query.clusteredBy(), Matchers.containsInAnyOrder(SymbolMatchers.isLiteral(10), SymbolMatchers.isLiteral(20)));
        assertThat(query.docKeys().isPresent(), Matchers.is(false));
    }

    @Test
    public void testFilterOnClusteredByColumnDoesNotResultInDocKeysIn() {
        WhereClauseOptimizer.DetailedQuery query = optimize("select * from clustered_by_only where x in (10, 20)");
        assertThat(query.clusteredBy(), Matchers.containsInAnyOrder(SymbolMatchers.isLiteral(10), SymbolMatchers.isLiteral(20)));
        assertThat(query.docKeys().isPresent(), Matchers.is(false));
    }

    @Test
    public void testFilterOnPKAndVersionResultsInDocKeys() {
        WhereClauseOptimizer.DetailedQuery query = optimize("select * from bystring where name = 'foo' and _version = 2");
        assertThat(query.docKeys().toString(), Matchers.is("Optional[DocKeys{foo, 2}]"));
    }
}

