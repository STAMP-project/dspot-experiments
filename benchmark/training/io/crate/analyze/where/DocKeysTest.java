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
package io.crate.analyze.where;


import DocKeys.DocKey;
import Row.EMPTY;
import com.google.common.collect.ImmutableList;
import io.crate.expression.symbol.Literal;
import io.crate.expression.symbol.Symbol;
import io.crate.metadata.CoordinatorTxnCtx;
import io.crate.test.integration.CrateUnitTest;
import io.crate.testing.TestingHelpers;
import java.util.List;
import org.hamcrest.core.Is;
import org.junit.Test;


public class DocKeysTest extends CrateUnitTest {
    @Test
    public void testClusteredIsFirstInId() throws Exception {
        // if a the table is clustered and has a pk, the clustering value is put in front in the id computation
        List<List<Symbol>> pks = ImmutableList.<List<Symbol>>of(ImmutableList.<Symbol>of(Literal.of(1), Literal.of("Ford")));
        DocKeys docKeys = new DocKeys(pks, false, 1, null);
        DocKeys.DocKey key = docKeys.getOnlyKey();
        CoordinatorTxnCtx txnCtx = CoordinatorTxnCtx.systemTransactionContext();
        assertThat(key.getRouting(txnCtx, TestingHelpers.getFunctions(), EMPTY, SubQueryResults.EMPTY), Is.is("Ford"));
        assertThat(key.getId(txnCtx, TestingHelpers.getFunctions(), EMPTY, SubQueryResults.EMPTY), Is.is("AgRGb3JkATE="));
    }
}

