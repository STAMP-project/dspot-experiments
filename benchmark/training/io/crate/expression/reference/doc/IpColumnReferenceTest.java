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
package io.crate.expression.reference.doc;


import io.crate.exceptions.GroupByOnArrayUnsupportedException;
import io.crate.expression.reference.doc.lucene.IpColumnReference;
import java.io.IOException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class IpColumnReferenceTest extends DocLevelExpressionsTest {
    private static final String IP_COLUMN = "i";

    private static final String IP_ARRAY_COLUMN = "ia";

    public IpColumnReferenceTest() {
        super("create table t (i ip, ia array(ip))");
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testIpExpression() throws Exception {
        IpColumnReference columnReference = new IpColumnReference(IpColumnReferenceTest.IP_COLUMN);
        columnReference.startCollect(ctx);
        columnReference.setNextReader(readerContext);
        IndexSearcher searcher = new IndexSearcher(readerContext.reader());
        TopDocs topDocs = searcher.search(new MatchAllDocsQuery(), 21);
        assertThat(topDocs.scoreDocs.length, Is.is(21));
        int i = 0;
        for (ScoreDoc doc : topDocs.scoreDocs) {
            columnReference.setNextDocId(doc.doc);
            if (i == 20) {
                assertThat(columnReference.value(), Is.is(Matchers.nullValue()));
            } else
                if (i < 10) {
                    assertThat(columnReference.value(), Is.is(("192.168.0." + i)));
                } else {
                    assertThat(columnReference.value(), Is.is(("7bd0:8082:2df8:487e:e0df:e7b5:9362:" + (Integer.toHexString(i)))));
                }

            i++;
        }
    }

    @Test
    public void testIpExpressionOnArrayThrowsException() throws Exception {
        IpColumnReference columnReference = new IpColumnReference(IpColumnReferenceTest.IP_ARRAY_COLUMN);
        columnReference.startCollect(ctx);
        columnReference.setNextReader(readerContext);
        IndexSearcher searcher = new IndexSearcher(readerContext.reader());
        TopDocs topDocs = searcher.search(new MatchAllDocsQuery(), 10);
        ScoreDoc doc = topDocs.scoreDocs[0];
        expectedException.expect(GroupByOnArrayUnsupportedException.class);
        expectedException.expectMessage("Column \"ia\" has a value that is an array. Group by doesn\'t work on Arrays");
        columnReference.setNextDocId(doc.doc);
    }

    @Test
    public void testNullDocValuesDoNotResultInNPE() throws IOException {
        IpColumnReference ref = new IpColumnReference("missing_column");
        ref.startCollect(ctx);
        ref.setNextReader(readerContext);
        ref.setNextDocId(0);
        assertThat(ref.value(), Matchers.nullValue());
    }
}

