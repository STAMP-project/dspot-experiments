package com.querydsl.collections;


import QCat.cat.name;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import org.junit.Assert;
import org.junit.Test;


public class GuavaHelpersTest {
    @Test
    public void predicate() {
        Predicate<Cat> predicate = GuavaHelpers.wrap(name.startsWith("Ann"));
        Assert.assertTrue(predicate.apply(new Cat("Ann")));
        Assert.assertFalse(predicate.apply(new Cat("Bob")));
    }

    @Test
    public void function() {
        Function<Cat, String> function = GuavaHelpers.wrap(name);
        Assert.assertEquals("Ann", function.apply(new Cat("Ann")));
        Assert.assertEquals("Bob", function.apply(new Cat("Bob")));
    }
}

