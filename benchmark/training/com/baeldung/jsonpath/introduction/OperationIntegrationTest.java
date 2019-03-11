package com.baeldung.jsonpath.introduction;


import com.jayway.jsonpath.Criteria;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Predicate;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class OperationIntegrationTest {
    private InputStream jsonInputStream = this.getClass().getClassLoader().getResourceAsStream("intro_api.json");

    private String jsonDataSourceString = new Scanner(jsonInputStream, "UTF-8").useDelimiter("\\Z").next();

    @Test
    public void givenJsonPathWithoutPredicates_whenReading_thenCorrect() {
        String jsonpathCreatorNamePath = "$['tool']['jsonpath']['creator']['name']";
        String jsonpathCreatorLocationPath = "$['tool']['jsonpath']['creator']['location'][*]";
        DocumentContext jsonContext = JsonPath.parse(jsonDataSourceString);
        String jsonpathCreatorName = jsonContext.read(jsonpathCreatorNamePath);
        List<String> jsonpathCreatorLocation = jsonContext.read(jsonpathCreatorLocationPath);
        Assert.assertEquals("Jayway Inc.", jsonpathCreatorName);
        Assert.assertThat(jsonpathCreatorLocation.toString(), CoreMatchers.containsString("Malmo"));
        Assert.assertThat(jsonpathCreatorLocation.toString(), CoreMatchers.containsString("San Francisco"));
        Assert.assertThat(jsonpathCreatorLocation.toString(), CoreMatchers.containsString("Helsingborg"));
    }

    @Test
    public void givenJsonPathWithFilterPredicate_whenReading_thenCorrect() {
        Filter expensiveFilter = Filter.filter(Criteria.where("price").gt(20.0));
        List<Map<String, Object>> expensive = JsonPath.parse(jsonDataSourceString).read("$['book'][?]", expensiveFilter);
        predicateUsageAssertionHelper(expensive);
    }

    @Test
    public void givenJsonPathWithCustomizedPredicate_whenReading_thenCorrect() {
        Predicate expensivePredicate = ( context) -> (Float.valueOf(context.item(.class).get("price").toString())) > 20.0;
        List<Map<String, Object>> expensive = JsonPath.parse(jsonDataSourceString).read("$['book'][?]", expensivePredicate);
        predicateUsageAssertionHelper(expensive);
    }

    @Test
    public void givenJsonPathWithInlinePredicate_whenReading_thenCorrect() {
        List<Map<String, Object>> expensive = JsonPath.parse(jsonDataSourceString).read("$['book'][?(@['price'] > $['price range']['medium'])]");
        predicateUsageAssertionHelper(expensive);
    }
}

