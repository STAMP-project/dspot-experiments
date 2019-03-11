package se.citerus.dddsample.domain.model.handling;


import java.util.Arrays;
import org.junit.Test;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.voyage.Voyage;


public class HandlingHistoryTest {
    Cargo cargo;

    Voyage voyage;

    HandlingEvent event1;

    HandlingEvent event1duplicate;

    HandlingEvent event2;

    HandlingHistory handlingHistory;

    @Test
    public void testDistinctEventsByCompletionTime() {
        assertThat(handlingHistory.distinctEventsByCompletionTime()).isEqualTo(Arrays.asList(event1, event2));
    }

    @Test
    public void testMostRecentlyCompletedEvent() {
        assertThat(handlingHistory.mostRecentlyCompletedEvent()).isEqualTo(event2);
    }
}

