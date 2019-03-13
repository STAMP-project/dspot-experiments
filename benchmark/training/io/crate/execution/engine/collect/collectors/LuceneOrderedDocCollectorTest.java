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


import Fuzziness.AUTO;
import KeywordFieldMapper.KeywordFieldType;
import NumberFieldMapper.NumberFieldType;
import NumberFieldMapper.NumberType;
import SortField.Type.LONG;
import com.carrotsearch.randomizedtesting.RandomizedTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import io.crate.analyze.OrderBy;
import io.crate.data.Row;
import io.crate.expression.reference.doc.lucene.LuceneCollectorExpression;
import io.crate.expression.reference.doc.lucene.LuceneMissingValue;
import io.crate.expression.reference.doc.lucene.ScoreCollectorExpression;
import io.crate.metadata.Reference;
import io.crate.metadata.ReferenceIdent;
import io.crate.metadata.RowGranularity;
import io.crate.metadata.Schemas;
import io.crate.metadata.doc.DocSysColumns;
import io.crate.types.DataTypes;
import java.util.Collections;
import java.util.List;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.FieldDoc;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.elasticsearch.index.mapper.KeywordFieldMapper;
import org.elasticsearch.index.query.QueryShardContext;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class LuceneOrderedDocCollectorTest extends RandomizedTest {
    private static final Reference REFERENCE = new Reference(new ReferenceIdent(new io.crate.metadata.RelationName(Schemas.DOC_SCHEMA_NAME, "table"), "value"), RowGranularity.DOC, DataTypes.LONG);

    private final NumberType fieldType = NumberType.LONG;

    private NumberFieldType valueFieldType;

    @Test
    public void testNextPageQueryWithLastCollectedNullValue() {
        FieldDoc fieldDoc = new FieldDoc(1, 0, new Object[]{ null });
        OrderBy orderBy = new OrderBy(Collections.singletonList(LuceneOrderedDocCollectorTest.REFERENCE), new boolean[]{ false }, new Boolean[]{ null });
        OptimizeQueryForSearchAfter queryForSearchAfter = new OptimizeQueryForSearchAfter(orderBy, Mockito.mock(QueryShardContext.class), ( name) -> valueFieldType);
        queryForSearchAfter.apply(fieldDoc);
    }

    // search after queries
    @Test
    public void testSearchAfterQueriesNullsLast() throws Exception {
        Directory index = createLuceneIndex();
        IndexReader reader = DirectoryReader.open(index);
        // reverseOrdering = false, nulls First = false
        // 1  2  null null
        // ^  (lastCollected = 2)
        FieldDoc afterDoc = new FieldDoc(0, 0, new Object[]{ 2L });
        Long[] result = nextPageQuery(reader, afterDoc, false, null);
        MatcherAssert.assertThat(result, Is.is(new Long[]{ 2L, null, null }));
        // reverseOrdering = false, nulls First = false
        // 1  2  null null
        // ^
        afterDoc = new FieldDoc(0, 0, new Object[]{ LuceneMissingValue.missingValue(false, null, LONG) });
        result = nextPageQuery(reader, afterDoc, false, null);
        MatcherAssert.assertThat(result, Is.is(new Long[]{ null, null }));
        // reverseOrdering = true, nulls First = false
        // 2  1  null null
        // ^
        afterDoc = new FieldDoc(0, 0, new Object[]{ 1L });
        result = nextPageQuery(reader, afterDoc, true, false);
        MatcherAssert.assertThat(result, Is.is(new Long[]{ 1L, null, null }));
        // reverseOrdering = true, nulls First = false
        // 2  1  null null
        // ^
        afterDoc = new FieldDoc(0, 0, new Object[]{ LuceneMissingValue.missingValue(true, false, LONG) });
        result = nextPageQuery(reader, afterDoc, true, false);
        MatcherAssert.assertThat(result, Is.is(new Long[]{ null, null }));
        reader.close();
    }

    @Test
    public void testSearchAfterQueriesNullsFirst() throws Exception {
        Directory index = createLuceneIndex();
        IndexReader reader = DirectoryReader.open(index);
        // reverseOrdering = false, nulls First = true
        // null, null, 1, 2
        // ^  (lastCollected = 2L)
        FieldDoc afterDoc = new FieldDoc(0, 0, new Object[]{ 2L });
        Long[] result = nextPageQuery(reader, afterDoc, false, true);
        MatcherAssert.assertThat(result, Is.is(new Long[]{ 2L }));
        // reverseOrdering = false, nulls First = true
        // null, null, 1, 2
        // ^
        afterDoc = new FieldDoc(0, 0, new Object[]{ LuceneMissingValue.missingValue(false, true, LONG) });
        result = nextPageQuery(reader, afterDoc, false, true);
        MatcherAssert.assertThat(result, Is.is(new Long[]{ null, null, 1L, 2L }));
        // reverseOrdering = true, nulls First = true
        // null, null, 2, 1
        // ^
        afterDoc = new FieldDoc(0, 0, new Object[]{ 1L });
        result = nextPageQuery(reader, afterDoc, true, true);
        MatcherAssert.assertThat(result, Is.is(new Long[]{ 1L }));
        // reverseOrdering = true, nulls First = true
        // null, null, 2, 1
        // ^
        afterDoc = new FieldDoc(0, 0, new Object[]{ LuceneMissingValue.missingValue(true, true, LONG) });
        result = nextPageQuery(reader, afterDoc, true, true);
        MatcherAssert.assertThat(result, Is.is(new Long[]{ null, null, 2L, 1L }));
        reader.close();
    }

    @Test
    public void testSearchAfterWithSystemColumn() {
        Reference sysColReference = new Reference(new ReferenceIdent(new io.crate.metadata.RelationName(Schemas.DOC_SCHEMA_NAME, "table"), DocSysColumns.SCORE), RowGranularity.DOC, DataTypes.FLOAT);
        OrderBy orderBy = new OrderBy(ImmutableList.of(sysColReference, LuceneOrderedDocCollectorTest.REFERENCE), new boolean[]{ false, false }, new Boolean[]{ false, false });
        FieldDoc lastCollected = new FieldDoc(0, 0, new Object[]{ 2L });
        OptimizeQueryForSearchAfter queryForSearchAfter = new OptimizeQueryForSearchAfter(orderBy, Mockito.mock(QueryShardContext.class), ( name) -> valueFieldType);
        Query nextPageQuery = queryForSearchAfter.apply(lastCollected);
        // returns null which leads to reuse of old query without paging optimization
        Assert.assertNull(nextPageQuery);
    }

    @Test
    public void testSearchMoreAppliesMinScoreFilter() throws Exception {
        IndexWriter w = new IndexWriter(new RAMDirectory(), new org.apache.lucene.index.IndexWriterConfig(new KeywordAnalyzer()));
        KeywordFieldMapper.KeywordFieldType fieldType = new KeywordFieldMapper.KeywordFieldType();
        fieldType.setName("x");
        fieldType.freeze();
        for (int i = 0; i < 3; i++) {
            LuceneOrderedDocCollectorTest.addDoc(w, fieldType, "Arthur");
        }
        LuceneOrderedDocCollectorTest.addDoc(w, fieldType, "Arthurr");// not "Arthur" to lower score

        w.commit();
        IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(w, true, true));
        List<LuceneCollectorExpression<?>> columnReferences = Collections.singletonList(new ScoreCollectorExpression());
        Query query = fieldType.fuzzyQuery("Arthur", AUTO, 2, 3, true);
        LuceneOrderedDocCollector collector;
        // without minScore filter we get 2 and 2 docs - this is not necessary for the test but is here
        // to make sure the "FuzzyQuery" matches the right documents
        collector = collectorWithMinScore(searcher, columnReferences, query, null);
        MatcherAssert.assertThat(Iterables.size(collector.collect()), Is.is(2));
        MatcherAssert.assertThat(Iterables.size(collector.collect()), Is.is(2));
        collector = collectorWithMinScore(searcher, columnReferences, query, 0.3F);
        int count = 0;
        // initialSearch -> 2 rows
        for (Row row : collector.collect()) {
            MatcherAssert.assertThat(((float) (row.get(0))), Matchers.greaterThanOrEqualTo(0.3F));
            count++;
        }
        MatcherAssert.assertThat(count, Is.is(2));
        count = 0;
        // searchMore -> 1 row is below minScore
        for (Row row : collector.collect()) {
            MatcherAssert.assertThat(((float) (row.get(0))), Matchers.greaterThanOrEqualTo(0.3F));
            count++;
        }
        MatcherAssert.assertThat(count, Is.is(1));
    }
}

