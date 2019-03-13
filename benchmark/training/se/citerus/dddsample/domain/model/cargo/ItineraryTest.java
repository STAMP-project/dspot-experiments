package se.citerus.dddsample.domain.model.cargo;


import HandlingEvent.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.location.SampleLocations;
import se.citerus.dddsample.domain.model.voyage.CarrierMovement;
import se.citerus.dddsample.domain.model.voyage.Voyage;


public class ItineraryTest {
    private final CarrierMovement abc = new CarrierMovement(SampleLocations.SHANGHAI, SampleLocations.ROTTERDAM, new Date(), new Date());

    private final CarrierMovement def = new CarrierMovement(SampleLocations.ROTTERDAM, SampleLocations.GOTHENBURG, new Date(), new Date());

    private final CarrierMovement ghi = new CarrierMovement(SampleLocations.ROTTERDAM, SampleLocations.NEWYORK, new Date(), new Date());

    private final CarrierMovement jkl = new CarrierMovement(SampleLocations.SHANGHAI, SampleLocations.HELSINKI, new Date(), new Date());

    Voyage voyage;

    Voyage wrongVoyage;

    @Test
    public void testCargoOnTrack() {
        TrackingId trackingId = new TrackingId("CARGO1");
        RouteSpecification routeSpecification = new RouteSpecification(SampleLocations.SHANGHAI, SampleLocations.GOTHENBURG, new Date());
        Cargo cargo = new Cargo(trackingId, routeSpecification);
        Itinerary itinerary = new Itinerary(Arrays.asList(new Leg(voyage, SampleLocations.SHANGHAI, SampleLocations.ROTTERDAM, new Date(), new Date()), new Leg(voyage, SampleLocations.ROTTERDAM, SampleLocations.GOTHENBURG, new Date(), new Date())));
        // Happy path
        HandlingEvent event = new HandlingEvent(cargo, new Date(), new Date(), Type.RECEIVE, SampleLocations.SHANGHAI);
        assertThat(itinerary.isExpected(event)).isTrue();
        event = new HandlingEvent(cargo, new Date(), new Date(), Type.LOAD, SampleLocations.SHANGHAI, voyage);
        assertThat(itinerary.isExpected(event)).isTrue();
        event = new HandlingEvent(cargo, new Date(), new Date(), Type.UNLOAD, SampleLocations.ROTTERDAM, voyage);
        assertThat(itinerary.isExpected(event)).isTrue();
        event = new HandlingEvent(cargo, new Date(), new Date(), Type.LOAD, SampleLocations.ROTTERDAM, voyage);
        assertThat(itinerary.isExpected(event)).isTrue();
        event = new HandlingEvent(cargo, new Date(), new Date(), Type.UNLOAD, SampleLocations.GOTHENBURG, voyage);
        assertThat(itinerary.isExpected(event)).isTrue();
        event = new HandlingEvent(cargo, new Date(), new Date(), Type.CLAIM, SampleLocations.GOTHENBURG);
        assertThat(itinerary.isExpected(event)).isTrue();
        // Customs event changes nothing
        event = new HandlingEvent(cargo, new Date(), new Date(), Type.CUSTOMS, SampleLocations.GOTHENBURG);
        assertThat(itinerary.isExpected(event)).isTrue();
        // Received at the wrong location
        event = new HandlingEvent(cargo, new Date(), new Date(), Type.RECEIVE, SampleLocations.HANGZOU);
        assertThat(itinerary.isExpected(event)).isFalse();
        // Loaded to onto the wrong ship, correct location
        event = new HandlingEvent(cargo, new Date(), new Date(), Type.LOAD, SampleLocations.ROTTERDAM, wrongVoyage);
        assertThat(itinerary.isExpected(event)).isFalse();
        // Unloaded from the wrong ship in the wrong location
        event = new HandlingEvent(cargo, new Date(), new Date(), Type.UNLOAD, SampleLocations.HELSINKI, wrongVoyage);
        assertThat(itinerary.isExpected(event)).isFalse();
        event = new HandlingEvent(cargo, new Date(), new Date(), Type.CLAIM, SampleLocations.ROTTERDAM);
        assertThat(itinerary.isExpected(event)).isFalse();
    }

    @Test
    public void testCreateItinerary() {
        try {
            new Itinerary(new ArrayList());
            fail("An empty itinerary is not OK");
        } catch (IllegalArgumentException iae) {
            // Expected
        }
        try {
            List<Leg> legs = null;
            new Itinerary(legs);
            fail("Null itinerary is not OK");
        } catch (IllegalArgumentException iae) {
            // Expected
        }
    }
}

