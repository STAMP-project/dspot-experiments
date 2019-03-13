package com.baeldung.java.doublebrace;


import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


public class DoubleBraceUnitTest {
    @Test
    public void whenInitializeSetWithoutDoubleBraces_containsElements() {
        final Set<String> countries = new HashSet<>();
        countries.add("India");
        countries.add("USSR");
        countries.add("USA");
        Assert.assertTrue(countries.contains("India"));
    }

    @Test
    public void whenInitializeSetWithDoubleBraces_containsElements() {
        final Set<String> countries = new HashSet<String>() {
            {
                add("India");
                add("USSR");
                add("USA");
            }
        };
        Assert.assertTrue(countries.contains("India"));
    }

    @Test
    public void whenInitializeUnmodifiableSetWithDoubleBrace_containsElements() {
        Set<String> countries = Stream.of("India", "USSR", "USA").collect(Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet));
        Assert.assertTrue(countries.contains("India"));
    }
}

