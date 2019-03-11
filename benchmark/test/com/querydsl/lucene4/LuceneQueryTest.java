/**
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.lucene4;


import LuceneSerializer.DEFAULT;
import com.google.common.collect.Sets;
import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.QueryException;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ParamNotSetException;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.Param;
import com.querydsl.core.types.dsl.StringPath;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.sandbox.queries.DuplicateFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for LuceneQuery
 *
 * @author vema
 */
public class LuceneQueryTest {
    private LuceneQuery query;

    private StringPath title;

    private NumberPath<Integer> year;

    private NumberPath<Double> gross;

    private final StringPath sort = Expressions.stringPath("sort");

    private RAMDirectory idx;

    private IndexWriter writer;

    private IndexSearcher searcher;

    @Test
    public void between() {
        Assert.assertEquals(3, query.where(year.between(1950, 1990)).fetchCount());
    }

    @Test
    public void count_empty_where_clause() {
        Assert.assertEquals(4, query.fetchCount());
    }

    @Test
    public void exists() {
        Assert.assertTrue(((query.where(title.eq("Jurassic Park")).fetchCount()) > 0));
        Assert.assertFalse(((query.where(title.eq("Jurassic Park X")).fetchCount()) > 0));
    }

    @Test
    public void notExists() {
        Assert.assertFalse(((query.where(title.eq("Jurassic Park")).fetchCount()) == 0));
        Assert.assertTrue(((query.where(title.eq("Jurassic Park X")).fetchCount()) == 0));
    }

    @Test
    public void count() {
        query.where(title.eq("Jurassic Park"));
        Assert.assertEquals(1, query.fetchCount());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void countDistinct() {
        query.where(year.between(1900, 3000));
        Assert.assertEquals(3, query.distinct().fetchCount());
    }

    @Test
    public void in() {
        Assert.assertEquals(2, query.where(title.in("Jurassic Park", "Nummisuutarit")).fetchCount());
    }

    @Test
    public void in2() {
        Assert.assertEquals(3, query.where(year.in(1990, 1864)).fetchCount());
    }

    @Test
    public void in_toString() {
        Assert.assertEquals("year:`____F year:`____H", query.where(year.in(1990, 1864)).toString());
    }

    @Test
    public void list_sorted_by_year_ascending() {
        query.where(year.between(1800, 2000));
        query.orderBy(year.asc());
        final List<Document> documents = query.fetch();
        Assert.assertFalse(documents.isEmpty());
        Assert.assertEquals(4, documents.size());
    }

    @Test
    public void list_not_sorted() {
        query.where(year.between(1800, 2000));
        final List<Document> documents = query.fetch();
        Assert.assertFalse(documents.isEmpty());
        Assert.assertEquals(4, documents.size());
    }

    @Test
    public void list_not_sorted_limit_2() {
        query.where(year.between(1800, 2000));
        query.limit(2);
        final List<Document> documents = query.fetch();
        Assert.assertFalse(documents.isEmpty());
        Assert.assertEquals(2, documents.size());
    }

    @Test
    public void list_sorted_by_year_limit_1() {
        query.where(year.between(1800, 2000));
        query.limit(1);
        query.orderBy(year.asc());
        final List<Document> documents = query.fetch();
        Assert.assertFalse(documents.isEmpty());
        Assert.assertEquals(1, documents.size());
    }

    @Test
    public void list_not_sorted_offset_2() {
        query.where(year.between(1800, 2000));
        query.offset(2);
        final List<Document> documents = query.fetch();
        Assert.assertFalse(documents.isEmpty());
        Assert.assertEquals(2, documents.size());
    }

    @Test
    public void list_sorted_ascending_by_year_offset_2() {
        query.where(year.between(1800, 2000));
        query.offset(2);
        query.orderBy(year.asc());
        final List<Document> documents = query.fetch();
        Assert.assertFalse(documents.isEmpty());
        Assert.assertEquals(2, documents.size());
        Assert.assertEquals("1990", documents.get(0).get("year"));
        Assert.assertEquals("1990", documents.get(1).get("year"));
    }

    @Test
    public void list_sorted_ascending_by_year_restrict_limit_2_offset_1() {
        query.where(year.between(1800, 2000));
        query.restrict(new QueryModifiers(2L, 1L));
        query.orderBy(year.asc());
        final List<Document> documents = query.fetch();
        Assert.assertFalse(documents.isEmpty());
        Assert.assertEquals(2, documents.size());
        Assert.assertEquals("1954", documents.get(0).get("year"));
        Assert.assertEquals("1990", documents.get(1).get("year"));
    }

    @Test
    public void list_sorted_ascending_by_year() {
        query.where(year.between(1800, 2000));
        query.orderBy(year.asc());
        final List<Document> documents = query.fetch();
        Assert.assertFalse(documents.isEmpty());
        Assert.assertEquals(4, documents.size());
        Assert.assertEquals("1864", documents.get(0).get("year"));
        Assert.assertEquals("1954", documents.get(1).get("year"));
        Assert.assertEquals("1990", documents.get(2).get("year"));
        Assert.assertEquals("1990", documents.get(3).get("year"));
    }

    @Test
    public void list_sort() {
        Sort sort = DEFAULT.toSort(Collections.singletonList(year.asc()));
        query.where(year.between(1800, 2000));
        // query.orderBy(year.asc());
        query.sort(sort);
        final List<Document> documents = query.fetch();
        Assert.assertFalse(documents.isEmpty());
        Assert.assertEquals(4, documents.size());
        Assert.assertEquals("1864", documents.get(0).get("year"));
        Assert.assertEquals("1954", documents.get(1).get("year"));
        Assert.assertEquals("1990", documents.get(2).get("year"));
        Assert.assertEquals("1990", documents.get(3).get("year"));
    }

    @Test
    public void list_distinct_property() {
        Assert.assertEquals(4, query.fetch().size());
        Assert.assertEquals(3, query.distinct(year).fetch().size());
    }

    @Test
    public void list_with_filter() {
        Filter filter = new DuplicateFilter("year");
        Assert.assertEquals(4, query.fetch().size());
        Assert.assertEquals(3, query.filter(filter).fetch().size());
    }

    @Test
    public void count_distinct_property() {
        Assert.assertEquals(4L, query.fetchCount());
        Assert.assertEquals(3L, query.distinct(year).fetchCount());
    }

    @Test
    public void list_sorted_descending_by_year() {
        query.where(year.between(1800, 2000));
        query.orderBy(year.desc());
        final List<Document> documents = query.fetch();
        Assert.assertFalse(documents.isEmpty());
        Assert.assertEquals(4, documents.size());
        Assert.assertEquals("1990", documents.get(0).get("year"));
        Assert.assertEquals("1990", documents.get(1).get("year"));
        Assert.assertEquals("1954", documents.get(2).get("year"));
        Assert.assertEquals("1864", documents.get(3).get("year"));
    }

    @Test
    public void list_sorted_descending_by_gross() {
        query.where(gross.between(0.0, 1000.0));
        query.orderBy(gross.desc());
        final List<Document> documents = query.fetch();
        Assert.assertFalse(documents.isEmpty());
        Assert.assertEquals(4, documents.size());
        Assert.assertEquals("90.0", documents.get(0).get("gross"));
        Assert.assertEquals("89.0", documents.get(1).get("gross"));
        Assert.assertEquals("30.5", documents.get(2).get("gross"));
        Assert.assertEquals("10.0", documents.get(3).get("gross"));
    }

    @Test
    public void list_sorted_descending_by_year_and_ascending_by_title() {
        query.where(year.between(1800, 2000));
        query.orderBy(year.desc());
        query.orderBy(title.asc());
        final List<Document> documents = query.fetch();
        Assert.assertFalse(documents.isEmpty());
        Assert.assertEquals(4, documents.size());
        Assert.assertEquals("1990", documents.get(0).get("year"));
        Assert.assertEquals("1990", documents.get(1).get("year"));
        Assert.assertEquals("Introduction to Algorithms", documents.get(0).get("title"));
        Assert.assertEquals("Jurassic Park", documents.get(1).get("title"));
    }

    @Test
    public void list_sorted_descending_by_year_and_descending_by_title() {
        query.where(year.between(1800, 2000));
        query.orderBy(year.desc());
        query.orderBy(title.desc());
        final List<Document> documents = query.fetch();
        Assert.assertFalse(documents.isEmpty());
        Assert.assertEquals(4, documents.size());
        Assert.assertEquals("1990", documents.get(0).get("year"));
        Assert.assertEquals("1990", documents.get(1).get("year"));
        Assert.assertEquals("Jurassic Park", documents.get(0).get("title"));
        Assert.assertEquals("Introduction to Algorithms", documents.get(1).get("title"));
    }

    @Test
    public void offset() {
        Assert.assertTrue(query.where(title.eq("Jurassic Park")).offset(30).fetch().isEmpty());
    }

    @Test
    public void load_list() {
        Document document = query.where(title.ne("")).load(title).fetch().get(0);
        Assert.assertNotNull(document.get("title"));
        Assert.assertNull(document.get("year"));
    }

    @Test
    public void load_list_fieldSelector() {
        Document document = query.where(title.ne("")).load(Sets.newHashSet("title")).fetch().get(0);
        Assert.assertNotNull(document.get("title"));
        Assert.assertNull(document.get("year"));
    }

    @Test
    public void load_singleResult() {
        Document document = query.where(title.ne("")).load(title).fetchFirst();
        Assert.assertNotNull(document.get("title"));
        Assert.assertNull(document.get("year"));
    }

    @Test
    public void load_singleResult_fieldSelector() {
        Document document = query.where(title.ne("")).load(Sets.newHashSet("title")).fetchFirst();
        Assert.assertNotNull(document.get("title"));
        Assert.assertNull(document.get("year"));
    }

    @Test
    public void singleResult() {
        Assert.assertNotNull(query.where(title.ne("")).fetchFirst());
    }

    @Test
    public void single_result_takes_limit() {
        Assert.assertEquals("Jurassic Park", query.where(title.ne("")).limit(1).fetchFirst().get("title"));
    }

    @Test
    public void single_result_considers_limit_and_actual_result_size() {
        query.where(title.startsWith("Nummi"));
        final Document document = query.limit(3).fetchFirst();
        Assert.assertEquals("Nummisuutarit", document.get("title"));
    }

    @Test
    public void single_result_returns_null_if_nothing_is_in_range() {
        query.where(title.startsWith("Nummi"));
        Assert.assertNull(query.offset(10).fetchFirst());
    }

    @Test
    public void single_result_considers_offset() {
        Assert.assertEquals("Introduction to Algorithms", query.where(title.ne("")).offset(3).fetchFirst().get("title"));
    }

    @Test
    public void single_result_considers_limit_and_offset() {
        Assert.assertEquals("The Lord of the Rings", query.where(title.ne("")).limit(1).offset(2).fetchFirst().get("title"));
    }

    @Test(expected = NonUniqueResultException.class)
    public void uniqueResult_contract() {
        query.where(title.ne("")).fetchOne();
    }

    @Test
    public void unique_result_takes_limit() {
        Assert.assertEquals("Jurassic Park", query.where(title.ne("")).limit(1).fetchOne().get("title"));
    }

    @Test
    public void unique_result_considers_limit_and_actual_result_size() {
        query.where(title.startsWith("Nummi"));
        final Document document = query.limit(3).fetchOne();
        Assert.assertEquals("Nummisuutarit", document.get("title"));
    }

    @Test
    public void unique_result_returns_null_if_nothing_is_in_range() {
        query.where(title.startsWith("Nummi"));
        Assert.assertNull(query.offset(10).fetchOne());
    }

    @Test
    public void unique_result_considers_offset() {
        Assert.assertEquals("Introduction to Algorithms", query.where(title.ne("")).offset(3).fetchOne().get("title"));
    }

    @Test
    public void unique_result_considers_limit_and_offset() {
        Assert.assertEquals("The Lord of the Rings", query.where(title.ne("")).limit(1).offset(2).fetchOne().get("title"));
    }

    @Test
    public void uniqueResult() {
        query.where(title.startsWith("Nummi"));
        final Document document = query.fetchOne();
        Assert.assertEquals("Nummisuutarit", document.get("title"));
    }

    @Test
    public void uniqueResult_with_param() {
        final Param<String> param = new Param<String>(String.class, "title");
        query.set(param, "Nummi");
        query.where(title.startsWith(param));
        final Document document = query.fetchOne();
        Assert.assertEquals("Nummisuutarit", document.get("title"));
    }

    @Test(expected = ParamNotSetException.class)
    public void uniqueResult_param_not_set() {
        final Param<String> param = new Param<String>(String.class, "title");
        query.where(title.startsWith(param));
        query.fetchOne();
    }

    @Test(expected = QueryException.class)
    public void uniqueResult_finds_more_than_one_result() {
        query.where(year.eq(1990));
        query.fetchOne();
    }

    @Test
    public void uniqueResult_finds_no_results() {
        query.where(year.eq(2200));
        Assert.assertNull(query.fetchOne());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void listDistinct() {
        query.where(year.between(1900, 2000).or(title.startsWith("Jura")));
        query.orderBy(year.asc());
        final List<Document> documents = query.distinct().fetch();
        Assert.assertFalse(documents.isEmpty());
        Assert.assertEquals(3, documents.size());
    }

    @Test
    public void listResults() {
        query.where(year.between(1800, 2000));
        query.restrict(new QueryModifiers(2L, 1L));
        query.orderBy(year.asc());
        final QueryResults<Document> results = query.fetchResults();
        Assert.assertFalse(results.isEmpty());
        Assert.assertEquals("1954", results.getResults().get(0).get("year"));
        Assert.assertEquals("1990", results.getResults().get(1).get("year"));
        Assert.assertEquals(2, results.getLimit());
        Assert.assertEquals(1, results.getOffset());
        Assert.assertEquals(4, results.getTotal());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void listDistinctResults() {
        query.where(year.between(1800, 2000).or(title.eq("The Lord of the Rings")));
        query.restrict(new QueryModifiers(1L, 1L));
        query.orderBy(year.asc());
        final QueryResults<Document> results = query.distinct().fetchResults();
        Assert.assertFalse(results.isEmpty());
        Assert.assertEquals("1954", results.getResults().get(0).get("year"));
        Assert.assertEquals(1, results.getLimit());
        Assert.assertEquals(1, results.getOffset());
        Assert.assertEquals(4, results.getTotal());
    }

    @Test
    public void list_all() {
        final List<Document> results = query.where(title.like("*")).orderBy(title.asc(), year.desc()).fetch();
        Assert.assertEquals(4, results.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void list_sorted_ascending_limit_negative() {
        query.where(year.between(1800, 2000));
        query.limit((-1));
        query.orderBy(year.asc());
        query.fetch();
    }

    @Test(expected = IllegalArgumentException.class)
    public void list_not_sorted_limit_negative() {
        query.where(year.between(1800, 2000));
        query.limit((-1));
        query.fetch();
    }

    @Test(expected = IllegalArgumentException.class)
    public void list_sorted_ascending_limit_0() {
        query.where(year.between(1800, 2000));
        query.limit(0);
        query.orderBy(year.asc());
        query.fetch();
    }

    @Test(expected = IllegalArgumentException.class)
    public void list_not_sorted_limit_0() {
        query.where(year.between(1800, 2000));
        query.limit(0);
        query.fetch();
    }

    @Test(expected = IllegalArgumentException.class)
    public void list_sorted_ascending_offset_negative() {
        query.where(year.between(1800, 2000));
        query.offset((-1));
        query.orderBy(year.asc());
        query.fetch();
    }

    @Test(expected = IllegalArgumentException.class)
    public void list_not_sorted_offset_negative() {
        query.where(year.between(1800, 2000));
        query.offset((-1));
        query.fetch();
    }

    @Test
    public void list_sorted_ascending_offset_0() {
        query.where(year.between(1800, 2000));
        query.offset(0);
        query.orderBy(year.asc());
        final List<Document> documents = query.fetch();
        Assert.assertFalse(documents.isEmpty());
        Assert.assertEquals(4, documents.size());
    }

    @Test
    public void list_not_sorted_offset_0() {
        query.where(year.between(1800, 2000));
        query.offset(0);
        final List<Document> documents = query.fetch();
        Assert.assertFalse(documents.isEmpty());
        Assert.assertEquals(4, documents.size());
    }

    @Test
    public void iterate() {
        query.where(year.between(1800, 2000));
        final Iterator<Document> iterator = query.iterate();
        int count = 0;
        while (iterator.hasNext()) {
            iterator.next();
            ++count;
        } 
        Assert.assertEquals(4, count);
    }

    @Test
    public void all_by_excluding_where() {
        Assert.assertEquals(4, query.fetch().size());
    }

    @Test
    public void empty_index_should_return_empty_list() throws Exception {
        idx = new RAMDirectory();
        writer = createWriter(idx);
        writer.close();
        IndexReader reader = IndexReader.open(idx);
        searcher = new IndexSearcher(reader);
        query = new LuceneQuery(new LuceneSerializer(true, true), searcher);
        Assert.assertTrue(query.fetch().isEmpty());
    }

    @Test(expected = QueryException.class)
    public void list_results_throws_an_illegal_argument_exception_when_sum_of_limit_and_offset_is_negative() {
        query.limit(1).offset(Integer.MAX_VALUE).fetchResults();
    }

    @Test
    public void limit_max_value() {
        Assert.assertEquals(4, query.limit(Long.MAX_VALUE).fetch().size());
    }
}

