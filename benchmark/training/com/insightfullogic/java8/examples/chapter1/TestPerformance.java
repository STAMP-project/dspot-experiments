package com.insightfullogic.java8.examples.chapter1;


import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import junit.framework.Assert;
import org.junit.Test;

import static SampleData.membersOfTheBeatles;


public class TestPerformance {
    @Test
    public void allMembers() {
        Album album = new Album("foo", Collections.<Track>emptyList(), Collections.singletonList(SampleData.theBeatles));
        Set<Artist> musicians = album.getAllMusicians().collect(Collectors.toSet());
        Set<Artist> expectedMusicians = new java.util.HashSet(membersOfTheBeatles);
        expectedMusicians.add(SampleData.theBeatles);
        Assert.assertEquals(expectedMusicians, musicians);
    }
}

