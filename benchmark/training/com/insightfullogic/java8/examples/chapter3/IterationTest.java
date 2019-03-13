package com.insightfullogic.java8.examples.chapter3;


import SampleData.membersOfTheBeatles;
import org.junit.Test;


public class IterationTest {
    @Test
    public void lazyPrintOuts() {
        Iteration iteration = new Iteration();
        iteration.filterArtistsFromLondonPrinted(membersOfTheBeatles);
    }

    @Test
    public void evaluatedPrintOuts() {
        Iteration iteration = new Iteration();
        iteration.internalCountArtistsFromLondonPrinted(membersOfTheBeatles);
    }
}

