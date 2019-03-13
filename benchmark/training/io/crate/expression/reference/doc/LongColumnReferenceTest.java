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
package io.crate.expression.reference.doc;


import io.crate.expression.reference.doc.lucene.LongColumnReference;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.hamcrest.core.Is;
import org.junit.Test;


public class LongColumnReferenceTest extends DocLevelExpressionsTest {
    private String column = "l";

    public LongColumnReferenceTest() {
        super("create table t (l long)");
    }

    @Test
    public void testLongExpression() throws Exception {
        LongColumnReference longColumn = new LongColumnReference(column);
        longColumn.startCollect(ctx);
        longColumn.setNextReader(readerContext);
        IndexSearcher searcher = new IndexSearcher(readerContext.reader());
        TopDocs topDocs = searcher.search(new MatchAllDocsQuery(), 20);
        long l = Long.MIN_VALUE;
        for (ScoreDoc doc : topDocs.scoreDocs) {
            longColumn.setNextDocId(doc.doc);
            assertThat(longColumn.value(), Is.is(l));
            l++;
        }
    }
}

