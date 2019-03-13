package com.baeldung.distinct;


import java.util.List;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;


public class DistinctWithJavaFunctionUnitTest {
    List<Person> personList;

    @Test
    public void whenFilterListByName_thenSizeShouldBe4() {
        List<Person> personListFiltered = personList.stream().filter(DistinctWithJavaFunction.distinctByKey(( p) -> p.getName())).collect(Collectors.toList());
        Assert.assertTrue(((personListFiltered.size()) == 4));
    }

    @Test
    public void whenFilterListByAge_thenSizeShouldBe2() {
        List<Person> personListFiltered = personList.stream().filter(DistinctWithJavaFunction.distinctByKey(( p) -> p.getAge())).collect(Collectors.toList());
        Assert.assertTrue(((personListFiltered.size()) == 2));
    }

    @Test
    public void whenFilterListWithDefaultDistinct_thenSizeShouldBe5() {
        List<Person> personListFiltered = personList.stream().distinct().collect(Collectors.toList());
        Assert.assertTrue(((personListFiltered.size()) == 5));
    }
}

