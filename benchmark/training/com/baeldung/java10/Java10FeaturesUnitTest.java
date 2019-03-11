package com.baeldung.java10;


import java.util.List;
import java.util.function.Predicate;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class Java10FeaturesUnitTest {
    private List<Integer> someIntList;

    @Test
    public void whenVarInitWithString_thenGetStringTypeVar() {
        var message = "Hello, Java 10";
        Assert.assertTrue((message instanceof String));
    }

    @Test
    public void whenVarInitWithAnonymous_thenGetAnonymousType() {
        var obj = new Object() {};
        Assert.assertFalse(obj.getClass().equals(Object.class));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void whenModifyCopyOfList_thenThrowsException() {
        List<Integer> copyList = List.copyOf(List, someIntList);
        copyList.add(4);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void whenModifyToUnmodifiableList_thenThrowsException() {
        List<Integer> evenList = someIntList.stream().filter(( i) -> (i % 2) == 0).collect(toUnmodifiableList());
        evenList.add(4);
    }

    @Test
    public void whenListContainsInteger_OrElseThrowReturnsInteger() {
        Integer firstEven = someIntList.stream().filter(( i) -> (i % 2) == 0).findFirst().orElseThrow();
        CoreMatchers.is(firstEven).equals(Integer.valueOf(2));
    }
}

