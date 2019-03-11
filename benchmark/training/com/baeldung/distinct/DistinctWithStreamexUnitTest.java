package com.baeldung.distinct;


import java.util.List;
import one.util.streamex.StreamEx;
import org.junit.Assert;
import org.junit.Test;


public class DistinctWithStreamexUnitTest {
    List<Person> personList;

    @Test
    public void whenFilterListByName_thenSizeShouldBe4() {
        List<Person> personListFiltered = StreamEx.of(personList).distinct(Person::getName).toList();
        Assert.assertTrue(((personListFiltered.size()) == 4));
    }

    @Test
    public void whenFilterListByAge_thenSizeShouldBe2() {
        List<Person> personListFiltered = StreamEx.of(personList).distinct(Person::getAge).toList();
        Assert.assertTrue(((personListFiltered.size()) == 2));
    }
}

