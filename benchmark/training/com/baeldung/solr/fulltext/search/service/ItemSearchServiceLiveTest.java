package com.baeldung.solr.fulltext.search.service;


import com.baeldung.solr.fulltext.search.model.Item;
import java.util.List;
import java.util.Map;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.RangeFacet;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse.Suggestion;
import org.apache.solr.client.solrj.response.SuggesterResponse;
import org.apache.solr.common.SolrDocument;
import org.junit.Assert;
import org.junit.Test;


public class ItemSearchServiceLiveTest {
    private static SolrClient solrClient;

    private static ItemSearchService itemSearchService;

    private static final String solrUrl = "http://localhost:8983/solr/item";

    @Test
    public void whenIndexing_thenAvailableOnRetrieval() throws Exception {
        ItemSearchServiceLiveTest.itemSearchService.index("hm0001", "Washing Machine", "Home Appliances", 450.0F);
        final SolrDocument indexedDoc = ItemSearchServiceLiveTest.solrClient.getById("hm0001");
        Assert.assertEquals("hm0001", indexedDoc.get("id"));
    }

    @Test
    public void whenIndexingBean_thenAvailableOnRetrieval() throws Exception {
        Item item = new Item();
        item.setId("hm0002");
        item.setCategory("Televisions");
        item.setDescription("LED TV 32");
        item.setPrice(500);
        ItemSearchServiceLiveTest.itemSearchService.indexBean(item);
    }

    @Test
    public void whenSearchingByBasicQuery_thenAllMatchingItemsShouldAvialble() throws Exception {
        ItemSearchServiceLiveTest.itemSearchService.index("hm0001", "Brand1 Washing Machine", "Home Appliances", 450.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("hm0002", "Brand1 Refrigerator", "Home Appliances", 450.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("hm0003", "LED TV 32", "Brand1 Home Appliances", 450.0F);
        SolrQuery query = new SolrQuery();
        query.setQuery("brand1");
        query.setStart(0);
        query.setRows(10);
        QueryResponse response = ItemSearchServiceLiveTest.solrClient.query(query);
        List<Item> items = response.getBeans(Item.class);
        Assert.assertEquals(3, items.size());
    }

    @Test
    public void whenSearchingWithWildCard_thenAllMatchingItemsShouldAvialble() throws Exception {
        ItemSearchServiceLiveTest.itemSearchService.index("hm0001", "Brand1 Washing Machine", "Home Appliances", 450.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("hm0002", "Brand1 Refrigerator", "Home Appliances", 450.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("hm0003", "LED TV 32", "Brand1 Home Appliances", 450.0F);
        SolrQuery query = new SolrQuery();
        query.setQuery("*rand?");
        QueryResponse response = ItemSearchServiceLiveTest.solrClient.query(query);
        List<Item> items = response.getBeans(Item.class);
        Assert.assertEquals(3, items.size());
    }

    @Test
    public void whenSearchingWithLogicalOperators_thenAllMatchingItemsShouldAvialble() throws Exception {
        ItemSearchServiceLiveTest.itemSearchService.index("hm0001", "Brand1 Washing Machine", "Home Appliances", 450.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("hm0002", "Brand1 Refrigerator", "Home Appliances", 450.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("hm0003", "Brand2 LED TV 32", "Washing Appliances", 450.0F);
        SolrQuery query = new SolrQuery();
        query.setQuery("brand1 AND (Washing OR Refrigerator)");
        QueryResponse response = ItemSearchServiceLiveTest.solrClient.query(query);
        List<Item> items = response.getBeans(Item.class);
        Assert.assertEquals(2, items.size());
    }

    @Test
    public void whenSearchingWithFields_thenAllMatchingItemsShouldAvialble() throws Exception {
        ItemSearchServiceLiveTest.itemSearchService.index("0001", "Brand1 Washing Machine", "Home Appliances", 450.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("0002", "Brand1 Refrigerator", "Home Appliances", 450.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("0003", "Brand2 LED TV 32", "Brand1 Washing Home Appliances", 450.0F);
        SolrQuery query = new SolrQuery();
        query.setQuery("description:Brand* AND category:*Washing*");
        QueryResponse response = ItemSearchServiceLiveTest.solrClient.query(query);
        List<Item> items = response.getBeans(Item.class);
        Assert.assertEquals(1, items.size());
    }

    @Test
    public void whenSearchingWithPhrase_thenAllMatchingItemsShouldAvialble() throws Exception {
        ItemSearchServiceLiveTest.itemSearchService.index("hm0001", "Brand1 Washing Machine", "Home Appliances", 450.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("hm0002", "Brand1 Refrigerator", "Home Appliances", 450.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("hm0003", "Brand2 Dishwasher", "Washing tools and equipment ", 450.0F);
        SolrQuery query = new SolrQuery();
        query.setQuery("washing MachIne");
        QueryResponse response = ItemSearchServiceLiveTest.solrClient.query(query);
        List<Item> items = response.getBeans(Item.class);
        Assert.assertEquals(2, items.size());
    }

    @Test
    public void whenSearchingWithRealPhrase_thenAllMatchingItemsShouldAvialble() throws Exception {
        ItemSearchServiceLiveTest.itemSearchService.index("hm0001", "Brand1 Washing Machine", "Home Appliances", 450.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("hm0002", "Brand1 Refrigerator", "Home Appliances", 450.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("hm0003", "Brand2 Dishwasher", "Washing tools and equipment ", 450.0F);
        SolrQuery query = new SolrQuery();
        query.setQuery("\"washing machine\"");
        QueryResponse response = ItemSearchServiceLiveTest.solrClient.query(query);
        List<Item> items = response.getBeans(Item.class);
        Assert.assertEquals(1, items.size());
    }

    @Test
    public void whenSearchingPhraseWithProximity_thenAllMatchingItemsShouldAvialble() throws Exception {
        ItemSearchServiceLiveTest.itemSearchService.index("hm0001", "Brand1 Washing Machine", "Home Appliances", 450.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("hm0002", "Brand1 Refrigerator", "Home Appliances", 450.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("hm0003", "Brand2 Dishwasher", "Washing tools and equipment ", 450.0F);
        SolrQuery query = new SolrQuery();
        query.setQuery("\"Washing equipment\"~2");
        QueryResponse response = ItemSearchServiceLiveTest.solrClient.query(query);
        List<Item> items = response.getBeans(Item.class);
        Assert.assertEquals(1, items.size());
    }

    @Test
    public void whenSearchingWithPriceRange_thenAllMatchingItemsShouldAvialble() throws Exception {
        ItemSearchServiceLiveTest.itemSearchService.index("hm0001", "Brand1 Washing Machine", "Home Appliances", 100.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("hm0002", "Brand1 Refrigerator", "Home Appliances", 300.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("hm0003", "Brand2 Dishwasher", "Home Appliances", 200.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("hm0004", "Brand2 Dishwasher", "Washing tools and equipment ", 450.0F);
        SolrQuery query = new SolrQuery();
        query.setQuery("price:[100 TO 300]");
        QueryResponse response = ItemSearchServiceLiveTest.solrClient.query(query);
        List<Item> items = response.getBeans(Item.class);
        Assert.assertEquals(3, items.size());
    }

    @Test
    public void whenSearchingWithPriceRangeInclusiveExclusive_thenAllMatchingItemsShouldAvialble() throws Exception {
        ItemSearchServiceLiveTest.itemSearchService.index("hm0001", "Brand1 Washing Machine", "Home Appliances", 100.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("hm0002", "Brand1 Refrigerator", "Home Appliances", 300.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("hm0003", "Brand2 Dishwasher", "Home Appliances", 200.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("hm0004", "Brand2 Dishwasher", "Washing tools and equipment ", 450.0F);
        SolrQuery query = new SolrQuery();
        query.setQuery("price:{100 TO 300]");
        QueryResponse response = ItemSearchServiceLiveTest.solrClient.query(query);
        List<Item> items = response.getBeans(Item.class);
        Assert.assertEquals(2, items.size());
    }

    @Test
    public void whenSearchingWithFilterQuery_thenAllMatchingItemsShouldAvialble() throws Exception {
        ItemSearchServiceLiveTest.itemSearchService.index("hm0001", "Brand1 Washing Machine", "Home Appliances", 100.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("hm0002", "Brand1 Refrigerator", "Home Appliances", 300.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("hm0003", "Brand2 Ceiling Fan", "Home Appliances", 200.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("hm0004", "Brand2 Dishwasher", "Washing tools and equipment ", 250.0F);
        SolrQuery query = new SolrQuery();
        query.setQuery("price:[100 TO 300]");
        query.addFilterQuery("description:Brand1", "category:Home Appliances");
        QueryResponse response = ItemSearchServiceLiveTest.solrClient.query(query);
        List<Item> items = response.getBeans(Item.class);
        Assert.assertEquals(2, items.size());
    }

    @Test
    public void whenSearchingWithFacetFields_thenAllMatchingFacetsShouldAvialble() throws Exception {
        ItemSearchServiceLiveTest.itemSearchService.index("hm0001", "Brand1 Washing Machine", "CategoryA", 100.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("hm0002", "Brand1 Refrigerator", "CategoryA", 300.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("hm0003", "Brand2 Ceiling Fan", "CategoryB", 200.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("hm0004", "Brand2 Dishwasher", "CategoryB", 250.0F);
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        query.addFacetField("category");
        QueryResponse response = ItemSearchServiceLiveTest.solrClient.query(query);
        List<Count> facetResults = response.getFacetField("category").getValues();
        Assert.assertEquals(2, facetResults.size());
        for (Count count : facetResults) {
            if ("categorya".equalsIgnoreCase(count.getName())) {
                Assert.assertEquals(2, count.getCount());
            } else
                if ("categoryb".equalsIgnoreCase(count.getName())) {
                    Assert.assertEquals(2, count.getCount());
                } else {
                    Assert.fail("unexpected category");
                }

        }
    }

    @Test
    public void whenSearchingWithFacetQuery_thenAllMatchingFacetsShouldAvialble() throws Exception {
        ItemSearchServiceLiveTest.itemSearchService.index("hm0001", "Brand1 Washing Machine", "CategoryA", 100.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("hm0002", "Brand1 Refrigerator", "CategoryA", 300.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("hm0003", "Brand2 Ceiling Fan", "CategoryB", 200.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("hm0004", "Brand2 Dishwasher", "CategoryB", 250.0F);
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        query.addFacetQuery("Washing OR Refrigerator");
        query.addFacetQuery("Brand2");
        QueryResponse response = ItemSearchServiceLiveTest.solrClient.query(query);
        Map<String, Integer> facetQueryMap = response.getFacetQuery();
        Assert.assertEquals(2, facetQueryMap.size());
        for (Map.Entry<String, Integer> entry : facetQueryMap.entrySet()) {
            String facetQuery = entry.getKey();
            if ("Washing OR Refrigerator".equals(facetQuery)) {
                Assert.assertEquals(Integer.valueOf(2), entry.getValue());
            } else
                if ("Brand2".equals(facetQuery)) {
                    Assert.assertEquals(Integer.valueOf(2), entry.getValue());
                } else {
                    Assert.fail("unexpected query");
                }

        }
    }

    @Test
    public void whenSearchingWithFacetRange_thenAllMatchingFacetsShouldAvialble() throws Exception {
        ItemSearchServiceLiveTest.itemSearchService.index("hm0001", "Brand1 Washing Machine", "CategoryA", 100.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("hm0002", "Brand1 Refrigerator", "CategoryA", 125.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("hm0003", "Brand2 Ceiling Fan", "CategoryB", 150.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("hm0004", "Brand2 Dishwasher", "CategoryB", 250.0F);
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        query.addNumericRangeFacet("price", 100, 275, 25);
        QueryResponse response = ItemSearchServiceLiveTest.solrClient.query(query);
        List<RangeFacet> rangeFacets = response.getFacetRanges().get(0).getCounts();
        Assert.assertEquals(7, rangeFacets.size());
    }

    @Test
    public void whenSearchingWithHitHighlighting_thenKeywordsShouldBeHighlighted() throws Exception {
        ItemSearchServiceLiveTest.itemSearchService.index("hm0001", "Brand1 Washing Machine", "Home Appliances", 100.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("hm0002", "Brand1 Refrigerator", "Home Appliances", 300.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("hm0003", "Brand2 Ceiling Fan", "Home Appliances", 200.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("hm0004", "Brand2 Dishwasher", "Washing equipments", 250.0F);
        SolrQuery query = new SolrQuery();
        query.setQuery("Appliances");
        query.setHighlight(true);
        query.addHighlightField("category");
        query.setHighlightSimplePre("<strong>");
        query.setHighlightSimplePost("</strong>");
        QueryResponse response = ItemSearchServiceLiveTest.solrClient.query(query);
        Map<String, Map<String, List<String>>> hitHighlightedMap = response.getHighlighting();
        Map<String, List<String>> highlightedFieldMap = hitHighlightedMap.get("hm0001");
        List<String> highlightedList = highlightedFieldMap.get("category");
        String highLightedText = highlightedList.get(0);
        Assert.assertEquals("Home <strong>Appliances</strong>", highLightedText);
    }

    @Test
    public void whenSearchingWithKeywordWithMistake_thenSpellingSuggestionsShouldBeReturned() throws Exception {
        ItemSearchServiceLiveTest.itemSearchService.index("hm0001", "Brand1 Washing Machine", "Home Appliances", 100.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("hm0002", "Brand1 Refrigerator", "Home Appliances", 300.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("hm0003", "Brand2 Ceiling Fan", "Home Appliances", 200.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("hm0004", "Brand2 Dishwasher", "Washing equipments", 250.0F);
        SolrQuery query = new SolrQuery();
        query.setQuery("hme");
        query.set("spellcheck", "on");
        QueryResponse response = ItemSearchServiceLiveTest.solrClient.query(query);
        SpellCheckResponse spellCheckResponse = response.getSpellCheckResponse();
        Assert.assertEquals(false, spellCheckResponse.isCorrectlySpelled());
        Suggestion suggestion = spellCheckResponse.getSuggestions().get(0);
        Assert.assertEquals("hme", suggestion.getToken());
        List<String> alternatives = suggestion.getAlternatives();
        String alternative = alternatives.get(0);
        Assert.assertEquals("home", alternative);
    }

    @Test
    public void whenSearchingWithIncompleteKeyword_thenKeywordSuggestionsShouldBeReturned() throws Exception {
        ItemSearchServiceLiveTest.itemSearchService.index("hm0001", "Brand1 Washing Machine", "Home Appliances", 100.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("hm0002", "Brand1 Refrigerator", "Home Appliances", 300.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("hm0003", "Brand2 Ceiling Fan", "Home Appliances", 200.0F);
        ItemSearchServiceLiveTest.itemSearchService.index("hm0004", "Brand2 Dishwasher", "Home washing equipments", 250.0F);
        SolrQuery query = new SolrQuery();
        query.setRequestHandler("/suggest");
        query.set("suggest", "true");
        query.set("suggest.build", "true");
        query.set("suggest.dictionary", "mySuggester");
        query.set("suggest.q", "Hom");
        QueryResponse response = ItemSearchServiceLiveTest.solrClient.query(query);
        SuggesterResponse suggesterResponse = response.getSuggesterResponse();
        Map<String, List<String>> suggestedTerms = suggesterResponse.getSuggestedTerms();
        List<String> suggestions = suggestedTerms.get("mySuggester");
        Assert.assertEquals(2, suggestions.size());
        for (String term : suggestions) {
            if ((!("Home Appliances".equals(term))) && (!("Home washing equipments".equals(term)))) {
                Assert.fail("Unexpected suggestions");
            }
        }
    }
}

