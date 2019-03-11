package com.insightfullogic.java8.answers.chapter4;


import SampleData.membersOfTheBeatles;
import SampleData.theBeatles;
import com.insightfullogic.java8.examples.chapter1.Artist;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class PerformanceTest {
    @Test
    public void findsAllTheBeatles() {
        com.insightfullogic.java8.answers.chapter4.PerformanceFixed stub = new PerformanceFixed() {
            @Override
            public String getName() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Stream<Artist> getMusicians() {
                return Stream.of(theBeatles);
            }
        };
        List<Artist> allMusicians = stub.getAllMusicians().collect(Collectors.toList());
        Assert.assertThat(allMusicians, Matchers.hasItem(theBeatles));
        // There really must be a better way than this
        Assert.assertThat(allMusicians, Matchers.hasItems(membersOfTheBeatles.toArray(new Artist[0])));
    }
}

