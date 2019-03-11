package com.insightfullogic.java8.exercises.chapter3;


import SampleData.johnColtrane;
import SampleData.theBeatles;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class Question2Test {
    @Test
    public void internal() {
        Assert.assertEquals(4, Question2.countBandMembersInternal(Arrays.asList(johnColtrane, theBeatles)));
    }
}

