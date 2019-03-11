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


import io.crate.expression.reference.doc.lucene.BooleanColumnReference;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.hamcrest.core.Is;
import org.junit.Test;


public class BooleanColumnReferenceTest extends DocLevelExpressionsTest {
    private String column = "b";

    public BooleanColumnReferenceTest() {
        super("create table t (b boolean)");
    }

    @Test
    public void testBooleanExpression() throws Exception {
        BooleanColumnReference booleanColumn = new BooleanColumnReference(column);
        booleanColumn.startCollect(ctx);
        booleanColumn.setNextReader(readerContext);
        IndexSearcher searcher = new IndexSearcher(readerContext.reader());
        TopDocs topDocs = searcher.search(new MatchAllDocsQuery(), 20);
        int i = 0;
        for (ScoreDoc doc : topDocs.scoreDocs) {
            booleanColumn.setNextDocId(doc.doc);
            assertThat(booleanColumn.value(), Is.is(((i % 2) == 0)));
            i++;
        }
    }
}

