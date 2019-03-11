package com.baeldung;


import Outer.Inner;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class OuterUnitTest {
    private static final String NEST_HOST_NAME = "com.baeldung.Outer";

    @Test
    public void whenGetNestHostFromOuter_thenGetNestHost() {
        CoreMatchers.is(Outer.class.getNestHost().getName()).equals(OuterUnitTest.NEST_HOST_NAME);
    }

    @Test
    public void whenGetNestHostFromInner_thenGetNestHost() {
        CoreMatchers.is(Inner.class.getNestHost().getName()).equals(OuterUnitTest.NEST_HOST_NAME);
    }

    @Test
    public void whenCheckNestmatesForNestedClasses_thenGetTrue() {
        CoreMatchers.is(Inner.class.isNestmateOf(Outer.class)).equals(true);
    }

    @Test
    public void whenCheckNestmatesForUnrelatedClasses_thenGetFalse() {
        CoreMatchers.is(Inner.class.isNestmateOf(Outer.class)).equals(false);
    }

    @Test
    public void whenGetNestMembersForNestedClasses_thenGetAllNestedClasses() {
        Set<String> nestMembers = Arrays.stream(Inner.class.getNestMembers()).map(Class::getName).collect(Collectors.toSet());
        CoreMatchers.is(nestMembers.size()).equals(2);
        Assert.assertTrue(nestMembers.contains("com.baeldung.Outer"));
        Assert.assertTrue(nestMembers.contains("com.baeldung.Outer$Inner"));
    }
}

