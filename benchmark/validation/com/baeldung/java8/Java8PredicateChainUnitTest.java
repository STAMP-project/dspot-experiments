package com.baeldung.java8;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class Java8PredicateChainUnitTest {
    private List<String> names = Arrays.asList("Adam", "Alexander", "John", "Tom");

    @Test
    public void whenFilterList_thenSuccess() {
        List<String> result = names.stream().filter(( name) -> name.startsWith("A")).collect(Collectors.toList());
        Assert.assertEquals(2, result.size());
        MatcherAssert.assertThat(result, Matchers.contains("Adam", "Alexander"));
    }

    @Test
    public void whenFilterListWithMultipleFilters_thenSuccess() {
        List<String> result = names.stream().filter(( name) -> name.startsWith("A")).filter(( name) -> (name.length()) < 5).collect(Collectors.toList());
        Assert.assertEquals(1, result.size());
        MatcherAssert.assertThat(result, Matchers.contains("Adam"));
    }

    @Test
    public void whenFilterListWithComplexPredicate_thenSuccess() {
        List<String> result = names.stream().filter(( name) -> (name.startsWith("A")) && ((name.length()) < 5)).collect(Collectors.toList());
        Assert.assertEquals(1, result.size());
        MatcherAssert.assertThat(result, Matchers.contains("Adam"));
    }

    @Test
    public void whenFilterListWithCombinedPredicatesInline_thenSuccess() {
        List<String> result = names.stream().filter(((Predicate<String>) (( name) -> name.startsWith("A"))).and(( name) -> (name.length()) < 5)).collect(Collectors.toList());
        Assert.assertEquals(1, result.size());
        MatcherAssert.assertThat(result, Matchers.contains("Adam"));
    }

    @Test
    public void whenFilterListWithCombinedPredicatesUsingAnd_thenSuccess() {
        Predicate<String> predicate1 = ( str) -> str.startsWith("A");
        Predicate<String> predicate2 = ( str) -> (str.length()) < 5;
        List<String> result = names.stream().filter(predicate1.and(predicate2)).collect(Collectors.toList());
        Assert.assertEquals(1, result.size());
        MatcherAssert.assertThat(result, Matchers.contains("Adam"));
    }

    @Test
    public void whenFilterListWithCombinedPredicatesUsingOr_thenSuccess() {
        Predicate<String> predicate1 = ( str) -> str.startsWith("J");
        Predicate<String> predicate2 = ( str) -> (str.length()) < 4;
        List<String> result = names.stream().filter(predicate1.or(predicate2)).collect(Collectors.toList());
        Assert.assertEquals(2, result.size());
        MatcherAssert.assertThat(result, Matchers.contains("John", "Tom"));
    }

    @Test
    public void whenFilterListWithCombinedPredicatesUsingOrAndNegate_thenSuccess() {
        Predicate<String> predicate1 = ( str) -> str.startsWith("J");
        Predicate<String> predicate2 = ( str) -> (str.length()) < 4;
        List<String> result = names.stream().filter(predicate1.or(predicate2.negate())).collect(Collectors.toList());
        Assert.assertEquals(3, result.size());
        MatcherAssert.assertThat(result, Matchers.contains("Adam", "Alexander", "John"));
    }

    @Test
    public void whenFilterListWithCollectionOfPredicatesUsingAnd_thenSuccess() {
        List<Predicate<String>> allPredicates = new ArrayList<Predicate<String>>();
        allPredicates.add(( str) -> str.startsWith("A"));
        allPredicates.add(( str) -> str.contains("d"));
        allPredicates.add(( str) -> (str.length()) > 4);
        List<String> result = names.stream().filter(allPredicates.stream().reduce(( x) -> true, Predicate::and)).collect(Collectors.toList());
        Assert.assertEquals(1, result.size());
        MatcherAssert.assertThat(result, Matchers.contains("Alexander"));
    }

    @Test
    public void whenFilterListWithCollectionOfPredicatesUsingOr_thenSuccess() {
        List<Predicate<String>> allPredicates = new ArrayList<Predicate<String>>();
        allPredicates.add(( str) -> str.startsWith("A"));
        allPredicates.add(( str) -> str.contains("d"));
        allPredicates.add(( str) -> (str.length()) > 4);
        List<String> result = names.stream().filter(allPredicates.stream().reduce(( x) -> false, Predicate::or)).collect(Collectors.toList());
        Assert.assertEquals(2, result.size());
        MatcherAssert.assertThat(result, Matchers.contains("Adam", "Alexander"));
    }
}

