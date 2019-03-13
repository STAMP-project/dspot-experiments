package com.baeldung.removal;


import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class RemovalUnitTest {
    Collection<String> names;

    Collection<String> expected;

    Collection<String> removed;

    @Test
    public void givenCollectionOfNames_whenUsingIteratorToRemoveAllNamesStartingWithLetterA_finalListShouldContainNoNamesStartingWithLetterA() {
        Iterator<String> i = names.iterator();
        while (i.hasNext()) {
            String e = i.next();
            if (e.startsWith("A")) {
                i.remove();
            }
        } 
        Assert.assertThat(names, Matchers.is(expected));
    }

    @Test
    public void givenCollectionOfNames_whenUsingRemoveIfToRemoveAllNamesStartingWithLetterA_finalListShouldContainNoNamesStartingWithLetterA() {
        names.removeIf(( e) -> e.startsWith("A"));
        Assert.assertThat(names, Matchers.is(expected));
    }

    @Test
    public void givenCollectionOfNames_whenUsingStreamToFilterAllNamesStartingWithLetterA_finalListShouldContainNoNamesStartingWithLetterA() {
        Collection<String> filteredCollection = names.stream().filter(( e) -> !(e.startsWith("A"))).collect(Collectors.toList());
        Assert.assertThat(filteredCollection, Matchers.is(expected));
    }

    @Test
    public void givenCollectionOfNames_whenUsingStreamAndPartitioningByToFindNamesThatStartWithLetterA_shouldFind3MatchingAnd2NonMatching() {
        Map<Boolean, List<String>> classifiedElements = names.stream().collect(Collectors.partitioningBy((String e) -> !(e.startsWith("A"))));
        Assert.assertThat(classifiedElements.get(Boolean.TRUE), Matchers.is(expected));
        Assert.assertThat(classifiedElements.get(Boolean.FALSE), Matchers.is(removed));
    }
}

