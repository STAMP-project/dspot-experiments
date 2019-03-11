package com.baeldung.aggregation;


import com.baeldung.aggregation.model.StatePopulation;
import com.baeldung.config.MongoConfig;
import com.mongodb.MongoClient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.bson.Document;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * This test requires:
 * * mongodb instance running on the environment
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MongoConfig.class)
public class ZipsAggregationLiveTest {
    private static MongoClient client;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void whenStatesHavePopGrtrThan10MillionAndSorted_thenSuccess() {
        GroupOperation groupByStateAndSumPop = group("state").sum("pop").as("statePop");
        MatchOperation filterStates = match(new Criteria("statePop").gt(10000000));
        SortOperation sortByPopDesc = sort(new org.springframework.data.domain.Sort(Direction.DESC, "statePop"));
        Aggregation aggregation = newAggregation(groupByStateAndSumPop, filterStates, sortByPopDesc);
        AggregationResults<StatePopulation> result = mongoTemplate.aggregate(aggregation, "zips", StatePopulation.class);
        /* Assert that all states have population
        greater than 10000000
         */
        result.forEach(( statePop) -> {
            assertTrue(((statePop.getStatePop()) > 10000000));
        });
        /* Assert that states fetched are in sorted by
        decreasing population
         */
        List<StatePopulation> actualList = StreamSupport.stream(result.spliterator(), false).collect(Collectors.toList());
        List<StatePopulation> expectedList = new ArrayList<>(actualList);
        Collections.sort(expectedList, ( sp1, sp2) -> (sp2.getStatePop()) - (sp1.getStatePop()));
        Assert.assertEquals(expectedList, actualList);
    }

    @Test
    public void whenStateWithLowestAvgCityPopIsND_theSuccess() {
        GroupOperation sumTotalCityPop = group("state", "city").sum("pop").as("cityPop");
        GroupOperation averageStatePop = group("_id.state").avg("cityPop").as("avgCityPop");
        SortOperation sortByAvgPopAsc = sort(new org.springframework.data.domain.Sort(Direction.ASC, "avgCityPop"));
        ProjectionOperation projectToMatchModel = project().andExpression("_id").as("state").andExpression("avgCityPop").as("statePop");
        LimitOperation limitToOnlyFirstDoc = limit(1);
        Aggregation aggregation = newAggregation(sumTotalCityPop, averageStatePop, sortByAvgPopAsc, limitToOnlyFirstDoc, projectToMatchModel);
        AggregationResults<StatePopulation> result = mongoTemplate.aggregate(aggregation, "zips", StatePopulation.class);
        StatePopulation smallestState = result.getUniqueMappedResult();
        Assert.assertEquals("ND", smallestState.getState());
        Assert.assertTrue(smallestState.getStatePop().equals(1645));
    }

    @Test
    public void whenMaxTXAndMinDC_theSuccess() {
        GroupOperation sumZips = group("state").count().as("zipCount");
        SortOperation sortByCount = sort(Direction.ASC, "zipCount");
        GroupOperation groupFirstAndLast = group().first("_id").as("minZipState").first("zipCount").as("minZipCount").last("_id").as("maxZipState").last("zipCount").as("maxZipCount");
        Aggregation aggregation = newAggregation(sumZips, sortByCount, groupFirstAndLast);
        AggregationResults<Document> result = mongoTemplate.aggregate(aggregation, "zips", Document.class);
        Document document = result.getUniqueMappedResult();
        Assert.assertEquals("DC", document.get("minZipState"));
        Assert.assertEquals(24, document.get("minZipCount"));
        Assert.assertEquals("TX", document.get("maxZipState"));
        Assert.assertEquals(1671, document.get("maxZipCount"));
    }
}

