package com.baeldung.spring.data.es;


import Fuzziness.ONE;
import MultiMatchQueryBuilder.Type.BEST_FIELDS;
import Terms.Order;
import com.baeldung.spring.data.es.config.Config;
import com.baeldung.spring.data.es.model.Article;
import com.baeldung.spring.data.es.model.Author;
import com.baeldung.spring.data.es.service.ArticleService;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
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
 * This Manual test requires:
 * * Elasticsearch instance running on host
 * * with cluster name = elasticsearch
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Config.class)
public class ElasticSearchQueryManualTest {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private Client client;

    private final Author johnSmith = new Author("John Smith");

    private final Author johnDoe = new Author("John Doe");

    @Test
    public void givenFullTitle_whenRunMatchQuery_thenDocIsFound() {
        final SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchQuery("title", "Search engines").operator(AND)).build();
        final List<Article> articles = elasticsearchTemplate.queryForList(searchQuery, Article.class);
        Assert.assertEquals(1, articles.size());
    }

    @Test
    public void givenOneTermFromTitle_whenRunMatchQuery_thenDocIsFound() {
        final SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchQuery("title", "Engines Solutions")).build();
        final List<Article> articles = elasticsearchTemplate.queryForList(searchQuery, Article.class);
        Assert.assertEquals(1, articles.size());
        Assert.assertEquals("Search engines", articles.get(0).getTitle());
    }

    @Test
    public void givenPartTitle_whenRunMatchQuery_thenDocIsFound() {
        final SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchQuery("title", "elasticsearch data")).build();
        final List<Article> articles = elasticsearchTemplate.queryForList(searchQuery, Article.class);
        Assert.assertEquals(3, articles.size());
    }

    @Test
    public void givenFullTitle_whenRunMatchQueryOnVerbatimField_thenDocIsFound() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchQuery("title.verbatim", "Second Article About Elasticsearch")).build();
        List<Article> articles = elasticsearchTemplate.queryForList(searchQuery, Article.class);
        Assert.assertEquals(1, articles.size());
        searchQuery = new NativeSearchQueryBuilder().withQuery(matchQuery("title.verbatim", "Second Article About")).build();
        articles = elasticsearchTemplate.queryForList(searchQuery, Article.class);
        Assert.assertEquals(0, articles.size());
    }

    @Test
    public void givenNestedObject_whenQueryByAuthorsName_thenFoundArticlesByThatAuthor() {
        final QueryBuilder builder = nestedQuery("authors", boolQuery().must(termQuery("authors.name", "smith")), ScoreMode.None);
        final SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(builder).build();
        final List<Article> articles = elasticsearchTemplate.queryForList(searchQuery, Article.class);
        Assert.assertEquals(2, articles.size());
    }

    @Test
    public void givenAnalyzedQuery_whenMakeAggregationOnTermCount_thenEachTokenCountsSeparately() {
        final TermsAggregationBuilder aggregation = AggregationBuilders.terms("top_tags").field("title");
        final SearchResponse response = client.prepareSearch("blog").setTypes("article").addAggregation(aggregation).execute().actionGet();
        final Map<String, Aggregation> results = response.getAggregations().asMap();
        final StringTerms topTags = ((StringTerms) (results.get("top_tags")));
        final List<String> keys = topTags.getBuckets().stream().map(MultiBucketsAggregation.Bucket::getKeyAsString).sorted().collect(Collectors.toList());
        Assert.assertEquals(Arrays.asList("about", "article", "data", "elasticsearch", "engines", "search", "second", "spring", "tutorial"), keys);
    }

    @Test
    public void givenNotAnalyzedQuery_whenMakeAggregationOnTermCount_thenEachTermCountsIndividually() {
        final TermsAggregationBuilder aggregation = AggregationBuilders.terms("top_tags").field("tags").order(Order.count(false));
        final SearchResponse response = client.prepareSearch("blog").setTypes("article").addAggregation(aggregation).execute().actionGet();
        final Map<String, Aggregation> results = response.getAggregations().asMap();
        final StringTerms topTags = ((StringTerms) (results.get("top_tags")));
        final List<String> keys = topTags.getBuckets().stream().map(MultiBucketsAggregation.Bucket::getKeyAsString).collect(Collectors.toList());
        Assert.assertEquals(Arrays.asList("elasticsearch", "spring data", "search engines", "tutorial"), keys);
    }

    @Test
    public void givenNotExactPhrase_whenUseSlop_thenQueryMatches() {
        final SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchPhraseQuery("title", "spring elasticsearch").slop(1)).build();
        final List<Article> articles = elasticsearchTemplate.queryForList(searchQuery, Article.class);
        Assert.assertEquals(1, articles.size());
    }

    @Test
    public void givenPhraseWithType_whenUseFuzziness_thenQueryMatches() {
        final SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchQuery("title", "spring date elasticserch").operator(AND).fuzziness(ONE).prefixLength(3)).build();
        final List<Article> articles = elasticsearchTemplate.queryForList(searchQuery, Article.class);
        Assert.assertEquals(1, articles.size());
    }

    @Test
    public void givenMultimatchQuery_whenDoSearch_thenAllProvidedFieldsMatch() {
        final SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(multiMatchQuery("tutorial").field("title").field("tags").type(BEST_FIELDS)).build();
        final List<Article> articles = elasticsearchTemplate.queryForList(searchQuery, Article.class);
        Assert.assertEquals(2, articles.size());
    }

    @Test
    public void givenBoolQuery_whenQueryByAuthorsName_thenFoundArticlesByThatAuthorAndFilteredTag() {
        final QueryBuilder builder = boolQuery().must(nestedQuery("authors", boolQuery().must(termQuery("authors.name", "doe")), ScoreMode.None)).filter(termQuery("tags", "elasticsearch"));
        final SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(builder).build();
        final List<Article> articles = elasticsearchTemplate.queryForList(searchQuery, Article.class);
        Assert.assertEquals(2, articles.size());
    }
}

