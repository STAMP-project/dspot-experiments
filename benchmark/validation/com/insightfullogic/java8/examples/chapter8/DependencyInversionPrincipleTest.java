package com.insightfullogic.java8.examples.chapter8;


import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class DependencyInversionPrincipleTest {
    private final DependencyInversionPrinciple.HeadingFinder finder;

    public DependencyInversionPrincipleTest(DependencyInversionPrinciple.HeadingFinder finder) {
        this.finder = finder;
    }

    @Test
    public void correctHeadings() {
        InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream("test_file"));
        List<String> headings = finder.findHeadings(reader);
        Assert.assertEquals(Arrays.asList("Improve Content", "Cleanup", "Add Content", "Add to Streams Chapter"), headings);
    }
}

