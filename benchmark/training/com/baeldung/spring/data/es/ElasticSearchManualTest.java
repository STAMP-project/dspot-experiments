package com.baeldung.spring.data.es;


import com.baeldung.spring.data.es.config.Config;
import com.baeldung.spring.data.es.model.Article;
import com.baeldung.spring.data.es.model.Author;
import com.baeldung.spring.data.es.service.ArticleService;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
public class ElasticSearchManualTest {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private ArticleService articleService;

    private final Author johnSmith = new Author("John Smith");

    private final Author johnDoe = new Author("John Doe");

    @Test
    public void givenArticleService_whenSaveArticle_thenIdIsAssigned() {
        final List<Author> authors = Arrays.asList(new Author("John Smith"), johnDoe);
        Article article = new Article("Making Search Elastic");
        article.setAuthors(authors);
        article = articleService.save(article);
        Assert.assertNotNull(article.getId());
    }

    @Test
    public void givenPersistedArticles_whenSearchByAuthorsName_thenRightFound() {
        final Page<Article> articleByAuthorName = articleService.findByAuthorName(johnSmith.getName(), PageRequest.of(0, 10));
        Assert.assertEquals(2L, articleByAuthorName.getTotalElements());
    }

    @Test
    public void givenCustomQuery_whenSearchByAuthorsName_thenArticleIsFound() {
        final Page<Article> articleByAuthorName = articleService.findByAuthorNameUsingCustomQuery("Smith", PageRequest.of(0, 10));
        Assert.assertEquals(2L, articleByAuthorName.getTotalElements());
    }

    @Test
    public void givenTagFilterQuery_whenSearchByTag_thenArticleIsFound() {
        final Page<Article> articleByAuthorName = articleService.findByFilteredTagQuery("elasticsearch", PageRequest.of(0, 10));
        Assert.assertEquals(3L, articleByAuthorName.getTotalElements());
    }

    @Test
    public void givenTagFilterQuery_whenSearchByAuthorsName_thenArticleIsFound() {
        final Page<Article> articleByAuthorName = articleService.findByAuthorsNameAndFilteredTagQuery("Doe", "elasticsearch", PageRequest.of(0, 10));
        Assert.assertEquals(2L, articleByAuthorName.getTotalElements());
    }

    @Test
    public void givenPersistedArticles_whenUseRegexQuery_thenRightArticlesFound() {
        final SearchQuery searchQuery = new NativeSearchQueryBuilder().withFilter(regexpQuery("title", ".*data.*")).build();
        final List<Article> articles = elasticsearchTemplate.queryForList(searchQuery, Article.class);
        Assert.assertEquals(1, articles.size());
    }

    @Test
    public void givenSavedDoc_whenTitleUpdated_thenCouldFindByUpdatedTitle() {
        final SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(fuzzyQuery("title", "serch")).build();
        final List<Article> articles = elasticsearchTemplate.queryForList(searchQuery, Article.class);
        Assert.assertEquals(1, articles.size());
        final Article article = articles.get(0);
        final String newTitle = "Getting started with Search Engines";
        article.setTitle(newTitle);
        articleService.save(article);
        Assert.assertEquals(newTitle, articleService.findOne(article.getId()).get().getTitle());
    }

    @Test
    public void givenSavedDoc_whenDelete_thenRemovedFromIndex() {
        final String articleTitle = "Spring Data Elasticsearch";
        final SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchQuery("title", articleTitle).minimumShouldMatch("75%")).build();
        final List<Article> articles = elasticsearchTemplate.queryForList(searchQuery, Article.class);
        Assert.assertEquals(1, articles.size());
        final long count = articleService.count();
        articleService.delete(articles.get(0));
        Assert.assertEquals((count - 1), articleService.count());
    }

    @Test
    public void givenSavedDoc_whenOneTermMatches_thenFindByTitle() {
        final SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchQuery("title", "Search engines").operator(AND)).build();
        final List<Article> articles = elasticsearchTemplate.queryForList(searchQuery, Article.class);
        Assert.assertEquals(1, articles.size());
    }
}

