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
package io.crate.execution.engine.collect.collectors;


import io.crate.breaker.RamAccountingContext;
import io.crate.expression.reference.doc.lucene.CollectorContext;
import io.crate.expression.reference.doc.lucene.LongColumnReference;
import io.crate.test.integration.CrateUnitTest;
import io.crate.testing.BatchIteratorTester;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.elasticsearch.common.breaker.NoopCircuitBreaker;
import org.junit.Test;


public class LuceneBatchIteratorTest extends CrateUnitTest {
    private List<LongColumnReference> columnRefs;

    private IndexSearcher indexSearcher;

    private ArrayList<Object[]> expectedResult;

    @Test
    public void testLuceneBatchIterator() throws Exception {
        BatchIteratorTester tester = new BatchIteratorTester(() -> new LuceneBatchIterator(indexSearcher, new MatchAllDocsQuery(), null, false, new CollectorContext(( mappedFieldType) -> null), new RamAccountingContext("dummy", new NoopCircuitBreaker("dummy")), columnRefs, columnRefs));
        tester.verifyResultAndEdgeCaseBehaviour(expectedResult);
    }
}

