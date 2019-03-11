package com.baeldung.hibernatesearch;


import com.baeldung.hibernatesearch.model.Product;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { HibernateSearchConfig.class }, loader = AnnotationConfigContextLoader.class)
@Transactional
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HibernateSearchIntegrationTest {
    @Autowired
    ProductSearchDao dao;

    @PersistenceContext
    private EntityManager entityManager;

    private List<Product> products;

    @Commit
    @Test
    public void testA_whenInitialTestDataInserted_thenSuccess() {
        for (int i = 0; i < ((products.size()) - 1); i++) {
            entityManager.persist(products.get(i));
        }
    }

    @Test
    public void testB_whenIndexInitialized_thenCorrectIndexSize() throws InterruptedException {
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        fullTextEntityManager.createIndexer().startAndWait();
        int indexSize = fullTextEntityManager.getSearchFactory().getStatistics().getNumberOfIndexedEntities(Product.class.getName());
        Assert.assertEquals(((products.size()) - 1), indexSize);
    }

    @Commit
    @Test
    public void testC_whenAdditionalTestDataInserted_thenSuccess() {
        entityManager.persist(products.get(((products.size()) - 1)));
    }

    @Test
    public void testD_whenAdditionalTestDataInserted_thenIndexUpdatedAutomatically() {
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        int indexSize = fullTextEntityManager.getSearchFactory().getStatistics().getNumberOfIndexedEntities(Product.class.getName());
        Assert.assertEquals(products.size(), indexSize);
    }

    @Test
    public void testE_whenKeywordSearchOnName_thenCorrectMatches() {
        List<Product> expected = Arrays.asList(products.get(0), products.get(1), products.get(2));
        List<Product> results = dao.searchProductNameByKeywordQuery("iphone");
        Assert.assertThat(results, containsInAnyOrder(expected.toArray()));
    }

    @Test
    public void testF_whenFuzzySearch_thenCorrectMatches() {
        List<Product> expected = Arrays.asList(products.get(0), products.get(1), products.get(2));
        List<Product> results = dao.searchProductNameByFuzzyQuery("iPhaen");
        Assert.assertThat(results, containsInAnyOrder(expected.toArray()));
    }

    @Test
    public void testG_whenWildcardSearch_thenCorrectMatches() {
        List<Product> expected = Arrays.asList(products.get(4), products.get(5), products.get(6));
        List<Product> results = dao.searchProductNameByWildcardQuery("6*");
        Assert.assertThat(results, containsInAnyOrder(expected.toArray()));
    }

    @Test
    public void testH_whenPhraseSearch_thenCorrectMatches() {
        List<Product> expected = Arrays.asList(products.get(2));
        List<Product> results = dao.searchProductDescriptionByPhraseQuery("with wireless charging");
        Assert.assertThat(results, containsInAnyOrder(expected.toArray()));
    }

    @Test
    public void testI_whenSimpleQueryStringSearch_thenCorrectMatches() {
        List<Product> expected = Arrays.asList(products.get(0), products.get(1));
        List<Product> results = dao.searchProductNameAndDescriptionBySimpleQueryStringQuery("Aple~2 + \"iPhone X\" + (256 | 128)");
        Assert.assertThat(results, containsInAnyOrder(expected.toArray()));
    }

    @Test
    public void testJ_whenRangeSearch_thenCorrectMatches() {
        List<Product> expected = Arrays.asList(products.get(0), products.get(1), products.get(2), products.get(3));
        List<Product> results = dao.searchProductNameByRangeQuery(64, 256);
        Assert.assertThat(results, containsInAnyOrder(expected.toArray()));
    }

    @Test
    public void testK_whenMoreLikeThisSearch_thenCorrectMatchesInOrder() {
        List<Product> expected = products;
        List<Object[]> resultsWithScore = dao.searchProductNameByMoreLikeThisQuery(products.get(0));
        List<Product> results = new LinkedList<Product>();
        for (Object[] resultWithScore : resultsWithScore) {
            results.add(((Product) (resultWithScore[0])));
        }
        Assert.assertThat(results, contains(expected.toArray()));
    }

    @Test
    public void testL_whenKeywordSearchOnNameAndDescription_thenCorrectMatches() {
        List<Product> expected = Arrays.asList(products.get(0), products.get(1), products.get(2));
        List<Product> results = dao.searchProductNameAndDescriptionByKeywordQuery("iphone");
        Assert.assertThat(results, containsInAnyOrder(expected.toArray()));
    }

    @Test
    public void testM_whenMoreLikeThisSearchOnProductNameAndDescription_thenCorrectMatchesInOrder() {
        List<Product> expected = products;
        List<Object[]> resultsWithScore = dao.searchProductNameAndDescriptionByMoreLikeThisQuery(products.get(0));
        List<Product> results = new LinkedList<Product>();
        for (Object[] resultWithScore : resultsWithScore) {
            results.add(((Product) (resultWithScore[0])));
        }
        Assert.assertThat(results, contains(expected.toArray()));
    }

    @Test
    public void testN_whenCombinedSearch_thenCorrectMatches() {
        List<Product> expected = Arrays.asList(products.get(1), products.get(2));
        List<Product> results = dao.searchProductNameAndDescriptionByCombinedQuery("apple", 64, 128, "face id", "samsung");
        Assert.assertThat(results, containsInAnyOrder(expected.toArray()));
    }
}

