package se.citerus.dddsample.domain.model.handling;


import java.util.Arrays;
import java.util.Date;
import org.junit.Test;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.location.SampleLocations;
import se.citerus.dddsample.domain.model.voyage.SampleVoyages;

import static HandlingEvent.Type.CLAIM;
import static Type.LOAD;
import static Type.RECEIVE;
import static Type.UNLOAD;
import static Type.valueOf;


public class HandlingEventTest {
    private Cargo cargo;

    @Test
    public void testNewWithCarrierMovement() {
        HandlingEvent e1 = new HandlingEvent(cargo, new Date(), new Date(), LOAD, SampleLocations.HONGKONG, SampleVoyages.CM003);
        assertThat(e1.location()).isEqualTo(SampleLocations.HONGKONG);
        HandlingEvent e2 = new HandlingEvent(cargo, new Date(), new Date(), UNLOAD, SampleLocations.NEWYORK, SampleVoyages.CM003);
        assertThat(e2.location()).isEqualTo(SampleLocations.NEWYORK);
        // These event types prohibit a carrier movement association
        for (HandlingEvent.Type type : Arrays.asList(Type.CLAIM, Type.RECEIVE, Type.CUSTOMS)) {
            try {
                new HandlingEvent(cargo, new Date(), new Date(), type, SampleLocations.HONGKONG, SampleVoyages.CM003);
                fail((("Handling event type " + type) + " prohibits carrier movement"));
            } catch (IllegalArgumentException expected) {
            }
        }
        // These event types requires a carrier movement association
        for (HandlingEvent.Type type : Arrays.asList(Type.LOAD, Type.UNLOAD)) {
            try {
                new HandlingEvent(cargo, new Date(), new Date(), type, SampleLocations.HONGKONG, null);
                fail((("Handling event type " + type) + " requires carrier movement"));
            } catch (IllegalArgumentException expected) {
            }
        }
    }

    @Test
    public void testNewWithLocation() {
        HandlingEvent e1 = new HandlingEvent(cargo, new Date(), new Date(), CLAIM, SampleLocations.HELSINKI);
        assertThat(e1.location()).isEqualTo(SampleLocations.HELSINKI);
    }

    @Test
    public void testCurrentLocationLoadEvent() {
        HandlingEvent ev = new HandlingEvent(cargo, new Date(), new Date(), LOAD, SampleLocations.CHICAGO, SampleVoyages.CM004);
        assertThat(ev.location()).isEqualTo(SampleLocations.CHICAGO);
    }

    @Test
    public void testCurrentLocationReceivedEvent() {
        HandlingEvent ev = new HandlingEvent(cargo, new Date(), new Date(), RECEIVE, SampleLocations.CHICAGO);
        assertThat(ev.location()).isEqualTo(SampleLocations.CHICAGO);
    }

    @Test
    public void testCurrentLocationClaimedEvent() {
        HandlingEvent ev = new HandlingEvent(cargo, new Date(), new Date(), Type.CLAIM, SampleLocations.CHICAGO);
        assertThat(ev.location()).isEqualTo(SampleLocations.CHICAGO);
    }

    @Test
    public void testParseType() {
        assertThat(valueOf("CLAIM")).isEqualTo(Type.CLAIM);
        assertThat(valueOf("LOAD")).isEqualTo(Type.LOAD);
        assertThat(valueOf("UNLOAD")).isEqualTo(Type.UNLOAD);
        assertThat(valueOf("RECEIVE")).isEqualTo(Type.RECEIVE);
    }

    @Test
    public void testParseTypeIllegal() {
        try {
            valueOf("NOT_A_HANDLING_EVENT_TYPE");
            fail("Expected IllegaArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            // All's well
        }
    }

    @Test
    public void testEqualsAndSameAs() {
        Date timeOccured = new Date();
        Date timeRegistered = new Date();
        HandlingEvent ev1 = new HandlingEvent(cargo, timeOccured, timeRegistered, LOAD, SampleLocations.CHICAGO, SampleVoyages.CM005);
        HandlingEvent ev2 = new HandlingEvent(cargo, timeOccured, timeRegistered, LOAD, SampleLocations.CHICAGO, SampleVoyages.CM005);
        // Two handling events are not equal() even if all non-uuid fields are identical
        assertThat(ev1.equals(ev2)).isTrue();
        assertThat(ev2.equals(ev1)).isTrue();
        assertThat(ev1.equals(ev1)).isTrue();
        assertThat(ev2.equals(null)).isFalse();
        assertThat(ev2.equals(new Object())).isFalse();
    }
}

