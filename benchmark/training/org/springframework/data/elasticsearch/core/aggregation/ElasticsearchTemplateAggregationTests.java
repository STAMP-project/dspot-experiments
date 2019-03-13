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
package org.springframework.data.elasticsearch.core.aggregation;


import SearchType.DEFAULT;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.Aggregations;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
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
public class ElasticsearchTemplateAggregationTests {
    public static final String RIZWAN_IDREES = "Rizwan Idrees";

    public static final String MOHSIN_HUSEN = "Mohsin Husen";

    public static final String JONATHAN_YAN = "Jonathan Yan";

    public static final String ARTUR_KONCZAK = "Artur Konczak";

    public static final int YEAR_2002 = 2002;

    public static final int YEAR_2001 = 2001;

    public static final int YEAR_2000 = 2000;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void shouldReturnAggregatedResponseForGivenSearchQuery() {
        // given
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchAllQuery()).withSearchType(DEFAULT).withIndices("test-index-articles").withTypes("article").addAggregation(terms("subjects").field("subject")).build();
        // when
        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new org.springframework.data.elasticsearch.core.ResultsExtractor<Aggregations>() {
            @Override
            public Aggregations extract(SearchResponse response) {
                return response.getAggregations();
            }
        });
        // then
        Assert.assertThat(aggregations, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(aggregations.asMap().get("subjects"), Matchers.is(Matchers.notNullValue()));
    }
}

