/**
 * Copyright 2013-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.elasticsearch.core.facet;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.FacetedPage;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.facet.request.HistogramFacetRequestBuilder;
import org.springframework.data.elasticsearch.core.facet.request.NativeFacetRequest;
import org.springframework.data.elasticsearch.core.facet.request.StatisticalFacetRequestBuilder;
import org.springframework.data.elasticsearch.core.facet.request.TermFacetRequestBuilder;
import org.springframework.data.elasticsearch.core.facet.result.HistogramResult;
import org.springframework.data.elasticsearch.core.facet.result.IntervalUnit;
import org.springframework.data.elasticsearch.core.facet.result.Range;
import org.springframework.data.elasticsearch.core.facet.result.RangeResult;
import org.springframework.data.elasticsearch.core.facet.result.StatisticalResult;
import org.springframework.data.elasticsearch.core.facet.result.Term;
import org.springframework.data.elasticsearch.core.facet.result.TermResult;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 *
 *
 * @author Rizwan Idrees
 * @author Mohsin Husen
 * @author Jonathan Yan
 * @author Artur Konczak
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:elasticsearch-template-test.xml")
public class ElasticsearchTemplateFacetTests {
    private static final String RIZWAN_IDREES = "Rizwan Idrees";

    private static final String MOHSIN_HUSEN = "Mohsin Husen";

    private static final String JONATHAN_YAN = "Jonathan Yan";

    private static final String ARTUR_KONCZAK = "Artur Konczak";

    private static final int YEAR_2002 = 2002;

    private static final int YEAR_2001 = 2001;

    private static final int YEAR_2000 = 2000;

    private static final String PUBLISHED_YEARS = "publishedYears";

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void shouldReturnFacetedAuthorsForGivenQueryWithDefaultOrder() {
        // given
        String facetName = "fauthors";
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withFacet(new TermFacetRequestBuilder(facetName).fields("authors.untouched").build()).build();
        // when
        FacetedPage<ArticleEntity> result = elasticsearchTemplate.queryForPage(searchQuery, ArticleEntity.class);
        // then
        Assert.assertThat(result.getNumberOfElements(), is(equalTo(4)));
        TermResult facet = ((TermResult) (result.getFacet(facetName)));
        Term term = facet.getTerms().get(0);
        Assert.assertThat(term.getTerm(), is(ElasticsearchTemplateFacetTests.RIZWAN_IDREES));
        Assert.assertThat(term.getCount(), is(4L));
        term = facet.getTerms().get(1);
        Assert.assertThat(term.getTerm(), is(ElasticsearchTemplateFacetTests.ARTUR_KONCZAK));
        Assert.assertThat(term.getCount(), is(3L));
        term = facet.getTerms().get(2);
        Assert.assertThat(term.getTerm(), is(ElasticsearchTemplateFacetTests.MOHSIN_HUSEN));
        Assert.assertThat(term.getCount(), is(2L));
        term = facet.getTerms().get(3);
        Assert.assertThat(term.getTerm(), is(ElasticsearchTemplateFacetTests.JONATHAN_YAN));
        Assert.assertThat(term.getCount(), is(1L));
        Assert.assertThat(facet.getTotal(), is(4L));
        Assert.assertThat(facet.getOther(), is(0L));
        Assert.assertThat(facet.getMissing(), is(0L));
    }

    @Test
    public void shouldReturnFacetedAuthorsForGivenFilteredQuery() {
        // given
        String facetName = "fauthors";
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withFacet(new TermFacetRequestBuilder(facetName).applyQueryFilter().fields("authors.untouched").build()).build();
        // when
        FacetedPage<ArticleEntity> result = elasticsearchTemplate.queryForPage(searchQuery, ArticleEntity.class);
        // then
        Assert.assertThat(result.getNumberOfElements(), is(equalTo(4)));
        TermResult facet = ((TermResult) (result.getFacet(facetName)));
        Term term = facet.getTerms().get(0);
        Assert.assertThat(term.getTerm(), is(ElasticsearchTemplateFacetTests.RIZWAN_IDREES));
        Assert.assertThat(term.getCount(), is(4L));
        term = facet.getTerms().get(1);
        Assert.assertThat(term.getTerm(), is(ElasticsearchTemplateFacetTests.ARTUR_KONCZAK));
        Assert.assertThat(term.getCount(), is(3L));
        term = facet.getTerms().get(2);
        Assert.assertThat(term.getTerm(), is(ElasticsearchTemplateFacetTests.MOHSIN_HUSEN));
        Assert.assertThat(term.getCount(), is(2L));
        Assert.assertThat(facet.getTotal(), is(4L));
        Assert.assertThat(facet.getOther(), is(0L));
        Assert.assertThat(facet.getMissing(), is(0L));
    }

    @Test
    public void shouldExcludeTermsFromFacetedAuthorsForGivenQuery() {
        // given
        String facetName = "fauthors";
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withFacet(new TermFacetRequestBuilder(facetName).applyQueryFilter().fields("authors.untouched").excludeTerms(ElasticsearchTemplateFacetTests.RIZWAN_IDREES, ElasticsearchTemplateFacetTests.ARTUR_KONCZAK).build()).build();
        // when
        FacetedPage<ArticleEntity> result = elasticsearchTemplate.queryForPage(searchQuery, ArticleEntity.class);
        // then
        Assert.assertThat(result.getNumberOfElements(), is(equalTo(4)));
        TermResult facet = ((TermResult) (result.getFacet(facetName)));
        Assert.assertThat(facet.getTerms().size(), is(2));
        Term term = facet.getTerms().get(0);
        Assert.assertThat(term.getTerm(), is(ElasticsearchTemplateFacetTests.MOHSIN_HUSEN));
        Assert.assertThat(term.getCount(), is(2L));
        Term term1 = facet.getTerms().get(1);
        Assert.assertThat(term1.getTerm(), is(ElasticsearchTemplateFacetTests.JONATHAN_YAN));
        Assert.assertThat(term1.getCount(), is(1L));
        Assert.assertThat(facet.getTotal(), is(2L));
        Assert.assertThat(facet.getOther(), is(0L));
        Assert.assertThat(facet.getMissing(), is(0L));
    }

    @Test
    public void shouldReturnFacetedAuthorsForGivenQueryOrderedByTerm() {
        // given
        String facetName = "fauthors";
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withFacet(new TermFacetRequestBuilder(facetName).fields("authors.untouched").ascTerm().build()).build();
        // when
        FacetedPage<ArticleEntity> result = elasticsearchTemplate.queryForPage(searchQuery, ArticleEntity.class);
        // then
        Assert.assertThat(result.getNumberOfElements(), is(equalTo(4)));
        TermResult facet = ((TermResult) (result.getFacet(facetName)));
        Term term = facet.getTerms().get(0);
        Assert.assertThat(term.getTerm(), is(ElasticsearchTemplateFacetTests.ARTUR_KONCZAK));
        Assert.assertThat(term.getCount(), is(3L));
        term = facet.getTerms().get(1);
        Assert.assertThat(term.getTerm(), is(ElasticsearchTemplateFacetTests.JONATHAN_YAN));
        Assert.assertThat(term.getCount(), is(1L));
        term = facet.getTerms().get(2);
        Assert.assertThat(term.getTerm(), is(ElasticsearchTemplateFacetTests.MOHSIN_HUSEN));
        Assert.assertThat(term.getCount(), is(2L));
        term = facet.getTerms().get(3);
        Assert.assertThat(term.getTerm(), is(ElasticsearchTemplateFacetTests.RIZWAN_IDREES));
        Assert.assertThat(term.getCount(), is(4L));
        Assert.assertThat(facet.getTotal(), is(4L));
        Assert.assertThat(facet.getOther(), is(0L));
        Assert.assertThat(facet.getMissing(), is(0L));
    }

    @Test
    public void shouldReturnFacetedAuthorsForGivenQueryOrderedByCountAsc() {
        // given
        String facetName = "fauthors";
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withFacet(new TermFacetRequestBuilder(facetName).fields("authors.untouched").ascCount().build()).build();
        // when
        FacetedPage<ArticleEntity> result = elasticsearchTemplate.queryForPage(searchQuery, ArticleEntity.class);
        // then
        Assert.assertThat(result.getNumberOfElements(), is(equalTo(4)));
        TermResult facet = ((TermResult) (result.getFacet(facetName)));
        Term term = facet.getTerms().get(0);
        Assert.assertThat(term.getTerm(), is(ElasticsearchTemplateFacetTests.JONATHAN_YAN));
        Assert.assertThat(term.getCount(), is(1L));
        term = facet.getTerms().get(1);
        Assert.assertThat(term.getTerm(), is(ElasticsearchTemplateFacetTests.MOHSIN_HUSEN));
        Assert.assertThat(term.getCount(), is(2L));
        term = facet.getTerms().get(2);
        Assert.assertThat(term.getTerm(), is(ElasticsearchTemplateFacetTests.ARTUR_KONCZAK));
        Assert.assertThat(term.getCount(), is(3L));
        term = facet.getTerms().get(3);
        Assert.assertThat(term.getTerm(), is(ElasticsearchTemplateFacetTests.RIZWAN_IDREES));
        Assert.assertThat(term.getCount(), is(4L));
        Assert.assertThat(facet.getTotal(), is(4L));
        Assert.assertThat(facet.getOther(), is(0L));
        Assert.assertThat(facet.getMissing(), is(0L));
    }

    @Test
    public void shouldReturnFacetedYearsForGivenQuery() {
        // given
        String facetName = "fyears";
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withFacet(new TermFacetRequestBuilder(facetName).fields("publishedYears").descCount().build()).build();
        // when
        FacetedPage<ArticleEntity> result = elasticsearchTemplate.queryForPage(searchQuery, ArticleEntity.class);
        // then
        Assert.assertThat(result.getNumberOfElements(), is(equalTo(4)));
        TermResult facet = ((TermResult) (result.getFacet(facetName)));
        Assert.assertThat(facet.getTerms().size(), is(equalTo(3)));
        Term term = facet.getTerms().get(0);
        Assert.assertThat(term.getTerm(), is(Long.toString(ElasticsearchTemplateFacetTests.YEAR_2000)));
        Assert.assertThat(term.getCount(), is(3L));
        term = facet.getTerms().get(1);
        Assert.assertThat(term.getTerm(), is(Long.toString(ElasticsearchTemplateFacetTests.YEAR_2001)));
        Assert.assertThat(term.getCount(), is(2L));
        term = facet.getTerms().get(2);
        Assert.assertThat(term.getTerm(), is(Long.toString(ElasticsearchTemplateFacetTests.YEAR_2002)));
        Assert.assertThat(term.getCount(), is(1L));
        Assert.assertThat(facet.getTotal(), is(3L));
        Assert.assertThat(facet.getOther(), is(0L));
        Assert.assertThat(facet.getMissing(), is(0L));
    }

    @Test
    public void shouldReturnExistingFacetedYearsForGivenQuery() {
        // given
        String facetName = "fyears";
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withFacet(new TermFacetRequestBuilder(facetName).applyQueryFilter().fields("publishedYears").descCount().build()).build();
        // when
        FacetedPage<ArticleEntity> result = elasticsearchTemplate.queryForPage(searchQuery, ArticleEntity.class);
        // then
        Assert.assertThat(result.getNumberOfElements(), is(equalTo(4)));
        TermResult facet = ((TermResult) (result.getFacet(facetName)));
        Assert.assertThat(facet.getTerms().size(), is(equalTo(3)));
        Term term = facet.getTerms().get(0);
        Assert.assertThat(term.getTerm(), is(Long.toString(ElasticsearchTemplateFacetTests.YEAR_2000)));
        Assert.assertThat(term.getCount(), is(3L));
        term = facet.getTerms().get(1);
        Assert.assertThat(term.getTerm(), is(Long.toString(ElasticsearchTemplateFacetTests.YEAR_2001)));
        Assert.assertThat(term.getCount(), is(2L));
        term = facet.getTerms().get(2);
        Assert.assertThat(term.getTerm(), is(Long.toString(ElasticsearchTemplateFacetTests.YEAR_2002)));
        Assert.assertThat(term.getCount(), is(1L));
        Assert.assertThat(facet.getTotal(), is(3L));
        Assert.assertThat(facet.getOther(), is(0L));
        Assert.assertThat(facet.getMissing(), is(0L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExeptionsForMultiFieldFacet() {
        // given
        String facetName = "fyears";
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withFacet(new TermFacetRequestBuilder(facetName).fields("publishedYears", "authors.untouched").ascTerm().build()).build();
        // when
        FacetedPage<ArticleEntity> result = elasticsearchTemplate.queryForPage(searchQuery, ArticleEntity.class);
        // then
        Assert.assertThat(result.getNumberOfElements(), is(equalTo(4)));
        TermResult facet = ((TermResult) (result.getFacet(facetName)));
        Assert.assertThat(facet.getTerms().size(), is(equalTo(7)));
        Term term = facet.getTerms().get(0);
        Assert.assertThat(term.getTerm(), is(Long.toString(ElasticsearchTemplateFacetTests.YEAR_2000)));
        Assert.assertThat(term.getCount(), is(3L));
        term = facet.getTerms().get(1);
        Assert.assertThat(term.getTerm(), is(Long.toString(ElasticsearchTemplateFacetTests.YEAR_2001)));
        Assert.assertThat(term.getCount(), is(2L));
        term = facet.getTerms().get(2);
        Assert.assertThat(term.getTerm(), is(Long.toString(ElasticsearchTemplateFacetTests.YEAR_2002)));
        Assert.assertThat(term.getCount(), is(1L));
        term = facet.getTerms().get(3);
        Assert.assertThat(term.getTerm(), is(ElasticsearchTemplateFacetTests.ARTUR_KONCZAK));
        Assert.assertThat(term.getCount(), is(3L));
        term = facet.getTerms().get(4);
        Assert.assertThat(term.getTerm(), is(ElasticsearchTemplateFacetTests.JONATHAN_YAN));
        Assert.assertThat(term.getCount(), is(1L));
        term = facet.getTerms().get(5);
        Assert.assertThat(term.getTerm(), is(ElasticsearchTemplateFacetTests.MOHSIN_HUSEN));
        Assert.assertThat(term.getCount(), is(2L));
        term = facet.getTerms().get(6);
        Assert.assertThat(term.getTerm(), is(ElasticsearchTemplateFacetTests.RIZWAN_IDREES));
        Assert.assertThat(term.getCount(), is(4L));
        Assert.assertThat(facet.getTotal(), is(16L));
        Assert.assertThat(facet.getOther(), is(0L));
        Assert.assertThat(facet.getMissing(), is(1L));
    }

    @Test
    public void shouldReturnFacetedYearsAndFacetedAuthorsForGivenQuery() {
        // given
        String numberFacetName = "fAuthors";
        String stringFacetName = "fyears";
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withFacet(new TermFacetRequestBuilder(numberFacetName).fields("publishedYears").ascTerm().build()).withFacet(new TermFacetRequestBuilder(stringFacetName).fields("authors.untouched").ascTerm().build()).build();
        // when
        FacetedPage<ArticleEntity> result = elasticsearchTemplate.queryForPage(searchQuery, ArticleEntity.class);
        // then
        Assert.assertThat(result.getNumberOfElements(), is(equalTo(4)));
        TermResult numberFacet = ((TermResult) (result.getFacet(numberFacetName)));
        Assert.assertThat(numberFacet.getTerms().size(), is(equalTo(3)));
        Term numberTerm = numberFacet.getTerms().get(0);
        Assert.assertThat(numberTerm.getTerm(), is(Long.toString(ElasticsearchTemplateFacetTests.YEAR_2000)));
        Assert.assertThat(numberTerm.getCount(), is(3L));
        numberTerm = numberFacet.getTerms().get(1);
        Assert.assertThat(numberTerm.getTerm(), is(Long.toString(ElasticsearchTemplateFacetTests.YEAR_2001)));
        Assert.assertThat(numberTerm.getCount(), is(2L));
        numberTerm = numberFacet.getTerms().get(2);
        Assert.assertThat(numberTerm.getTerm(), is(Long.toString(ElasticsearchTemplateFacetTests.YEAR_2002)));
        Assert.assertThat(numberTerm.getCount(), is(1L));
        TermResult stringFacet = ((TermResult) (result.getFacet(stringFacetName)));
        Term stringTerm = stringFacet.getTerms().get(0);
        Assert.assertThat(stringTerm.getTerm(), is(ElasticsearchTemplateFacetTests.ARTUR_KONCZAK));
        Assert.assertThat(stringTerm.getCount(), is(3L));
        stringTerm = stringFacet.getTerms().get(1);
        Assert.assertThat(stringTerm.getTerm(), is(ElasticsearchTemplateFacetTests.JONATHAN_YAN));
        Assert.assertThat(stringTerm.getCount(), is(1L));
        stringTerm = stringFacet.getTerms().get(2);
        Assert.assertThat(stringTerm.getTerm(), is(ElasticsearchTemplateFacetTests.MOHSIN_HUSEN));
        Assert.assertThat(stringTerm.getCount(), is(2L));
        stringTerm = stringFacet.getTerms().get(3);
        Assert.assertThat(stringTerm.getTerm(), is(ElasticsearchTemplateFacetTests.RIZWAN_IDREES));
        Assert.assertThat(stringTerm.getCount(), is(4L));
        Assert.assertThat(stringFacet.getTotal(), is(4L));
        Assert.assertThat(stringFacet.getOther(), is(0L));
        Assert.assertThat(stringFacet.getMissing(), is(0L));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowExceptionForNativeFacets() {
        // given
        String facetName = "fyears";
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withFacet(new NativeFacetRequest()).build();
        // when
        FacetedPage<ArticleEntity> result = elasticsearchTemplate.queryForPage(searchQuery, ArticleEntity.class);
        // then
        Assert.assertThat(result.getNumberOfElements(), is(equalTo(4)));
        TermResult facet = ((TermResult) (result.getFacet(facetName)));
        Assert.assertThat(facet.getTerms().size(), is(equalTo(3)));
        Term term = facet.getTerms().get(0);
        Assert.assertThat(term.getTerm(), is(Long.toString(ElasticsearchTemplateFacetTests.YEAR_2000)));
        Assert.assertThat(term.getCount(), is(3L));
        term = facet.getTerms().get(1);
        Assert.assertThat(term.getTerm(), is(Long.toString(ElasticsearchTemplateFacetTests.YEAR_2001)));
        Assert.assertThat(term.getCount(), is(2L));
        term = facet.getTerms().get(2);
        Assert.assertThat(term.getTerm(), is(Long.toString(ElasticsearchTemplateFacetTests.YEAR_2002)));
        Assert.assertThat(term.getCount(), is(1L));
        Assert.assertThat(facet.getTotal(), is(6L));
        Assert.assertThat(facet.getOther(), is(0L));
        Assert.assertThat(facet.getMissing(), is(1L));
    }

    @Test
    public void shouldFilterResultByRegexForGivenQuery() {
        // given
        String facetName = "regex_authors";
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withFacet(new TermFacetRequestBuilder(facetName).applyQueryFilter().fields("authors.untouched").regex("Art.*").build()).build();
        // when
        FacetedPage<ArticleEntity> result = elasticsearchTemplate.queryForPage(searchQuery, ArticleEntity.class);
        // then
        Assert.assertThat(result.getNumberOfElements(), is(equalTo(4)));
        TermResult facet = ((TermResult) (result.getFacet(facetName)));
        Assert.assertThat(facet.getTerms().size(), is(1));
        Term term = facet.getTerms().get(0);
        Assert.assertThat(term.getTerm(), is(ElasticsearchTemplateFacetTests.ARTUR_KONCZAK));
        Assert.assertThat(term.getCount(), is(3L));
        Assert.assertThat(facet.getTotal(), is(1L));
        Assert.assertThat(facet.getOther(), is(0L));
        Assert.assertThat(facet.getMissing(), is(0L));
    }

    @Test
    public void shouldReturnAllTermsForGivenQuery() {
        // given
        String facetName = "all_authors";
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withFacet(new TermFacetRequestBuilder(facetName).applyQueryFilter().fields("authors.untouched").allTerms().build()).build();
        // when
        FacetedPage<ArticleEntity> result = elasticsearchTemplate.queryForPage(searchQuery, ArticleEntity.class);
        // then
        Assert.assertThat(result.getNumberOfElements(), is(equalTo(4)));
        TermResult facet = ((TermResult) (result.getFacet(facetName)));
        Assert.assertThat(facet.getTerms().size(), is(4));
        Assert.assertThat(facet.getTotal(), is(4L));
        Assert.assertThat(facet.getOther(), is(0L));
        Assert.assertThat(facet.getMissing(), is(0L));
    }

    @Test
    public void shouldReturnRangeFacetForGivenQuery() {
        // given
        String facetName = "rangeYears";
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withFacet(new org.springframework.data.elasticsearch.core.facet.request.RangeFacetRequestBuilder(facetName).field(ElasticsearchTemplateFacetTests.PUBLISHED_YEARS).to(ElasticsearchTemplateFacetTests.YEAR_2000).range(ElasticsearchTemplateFacetTests.YEAR_2000, ElasticsearchTemplateFacetTests.YEAR_2002).from(ElasticsearchTemplateFacetTests.YEAR_2002).build()).build();
        // when
        FacetedPage<ArticleEntity> result = elasticsearchTemplate.queryForPage(searchQuery, ArticleEntity.class);
        // then
        Assert.assertThat(result.getNumberOfElements(), is(equalTo(4)));
        RangeResult facet = ((RangeResult) (result.getFacet(facetName)));
        Assert.assertThat(facet.getRanges().size(), is(equalTo(3)));
        Range range = facet.getRanges().get(0);
        Assert.assertThat(range.getFrom(), is(Double.NEGATIVE_INFINITY));
        Assert.assertThat(range.getTo(), is(((double) (ElasticsearchTemplateFacetTests.YEAR_2000))));
        Assert.assertThat(range.getCount(), is(0L));
        Assert.assertThat(range.getTotal(), is(0.0));
        range = facet.getRanges().get(1);
        Assert.assertThat(range.getFrom(), is(((double) (ElasticsearchTemplateFacetTests.YEAR_2000))));
        Assert.assertThat(range.getTo(), is(((double) (ElasticsearchTemplateFacetTests.YEAR_2002))));
        Assert.assertThat(range.getCount(), is(3L));
        Assert.assertThat(range.getTotal(), is(12004.0));
        range = facet.getRanges().get(2);
        Assert.assertThat(range.getFrom(), is(((double) (ElasticsearchTemplateFacetTests.YEAR_2002))));
        Assert.assertThat(range.getTo(), is(Double.POSITIVE_INFINITY));
        Assert.assertThat(range.getCount(), is(1L));
        Assert.assertThat(range.getTotal(), is(6003.0));
    }

    @Test
    public void shouldReturnKeyValueRangeFacetForStringValuesInGivenQuery() {
        // given
        String facetName = "rangeScoreOverYears";
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withFacet(new org.springframework.data.elasticsearch.core.facet.request.RangeFacetRequestBuilder(facetName).fields(ElasticsearchTemplateFacetTests.PUBLISHED_YEARS, "score").to(ElasticsearchTemplateFacetTests.YEAR_2000).range(ElasticsearchTemplateFacetTests.YEAR_2000, ElasticsearchTemplateFacetTests.YEAR_2002).from(ElasticsearchTemplateFacetTests.YEAR_2002).build()).build();
        // when
        FacetedPage<ArticleEntity> result = elasticsearchTemplate.queryForPage(searchQuery, ArticleEntity.class);
        // then
        Assert.assertThat(result.getNumberOfElements(), is(equalTo(4)));
        RangeResult facet = ((RangeResult) (result.getFacet(facetName)));
        Assert.assertThat(facet.getRanges().size(), is(equalTo(3)));
        Range range = facet.getRanges().get(0);
        Assert.assertThat(range.getFrom(), is(Double.NEGATIVE_INFINITY));
        Assert.assertThat(range.getTo(), is(((double) (ElasticsearchTemplateFacetTests.YEAR_2000))));
        Assert.assertThat(range.getCount(), is(0L));
        Assert.assertThat(range.getTotal(), is(0.0));
        range = facet.getRanges().get(1);
        Assert.assertThat(range.getFrom(), is(((double) (ElasticsearchTemplateFacetTests.YEAR_2000))));
        Assert.assertThat(range.getTo(), is(((double) (ElasticsearchTemplateFacetTests.YEAR_2002))));
        Assert.assertThat(range.getCount(), is(3L));
        Assert.assertThat(range.getTotal(), is(90.0));
        range = facet.getRanges().get(2);
        Assert.assertThat(range.getFrom(), is(((double) (ElasticsearchTemplateFacetTests.YEAR_2002))));
        Assert.assertThat(range.getTo(), is(Double.POSITIVE_INFINITY));
        Assert.assertThat(range.getCount(), is(1L));
        Assert.assertThat(range.getTotal(), is(40.0));
    }

    @Test
    public void shouldReturnStatisticalFacetForGivenQuery() {
        // given
        String facetName = "statPublishedYear";
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withFacet(new StatisticalFacetRequestBuilder(facetName).field(ElasticsearchTemplateFacetTests.PUBLISHED_YEARS).build()).build();
        // when
        FacetedPage<ArticleEntity> result = elasticsearchTemplate.queryForPage(searchQuery, ArticleEntity.class);
        // then
        Assert.assertThat(result.getNumberOfElements(), is(equalTo(4)));
        StatisticalResult facet = ((StatisticalResult) (result.getFacet(facetName)));
        Assert.assertThat(facet.getCount(), is(equalTo(6L)));
        Assert.assertThat(facet.getMax(), is(equalTo(2002.0)));
        Assert.assertThat(facet.getMin(), is(equalTo(2000.0)));
    }

    @Test
    public void shouldReturnHistogramFacetForGivenQuery() {
        // given
        String facetName = "numberPublicationPerYear";
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withFacet(new HistogramFacetRequestBuilder(facetName).field(ElasticsearchTemplateFacetTests.PUBLISHED_YEARS).interval(1).build()).build();
        // when
        FacetedPage<ArticleEntity> result = elasticsearchTemplate.queryForPage(searchQuery, ArticleEntity.class);
        // then
        Assert.assertThat(result.getNumberOfElements(), is(equalTo(4)));
        HistogramResult facet = ((HistogramResult) (result.getFacet(facetName)));
        Assert.assertThat(facet.getIntervalUnit().size(), is(equalTo(3)));
        IntervalUnit unit = facet.getIntervalUnit().get(0);
        Assert.assertThat(unit.getKey(), is(Long.valueOf(ElasticsearchTemplateFacetTests.YEAR_2000)));
        Assert.assertThat(unit.getCount(), is(3L));
        unit = facet.getIntervalUnit().get(1);
        Assert.assertThat(unit.getKey(), is(Long.valueOf(ElasticsearchTemplateFacetTests.YEAR_2001)));
        Assert.assertThat(unit.getCount(), is(2L));
        unit = facet.getIntervalUnit().get(2);
        Assert.assertThat(unit.getKey(), is(Long.valueOf(ElasticsearchTemplateFacetTests.YEAR_2002)));
        Assert.assertThat(unit.getCount(), is(1L));
    }

    @Test
    public void shouldNotThrowExceptionForNoFacets() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).build();
        AggregatedPage<ArticleEntity> result = elasticsearchTemplate.queryForPage(searchQuery, ArticleEntity.class);
        Assert.assertThat(result.hasFacets(), is(false));
    }
}

