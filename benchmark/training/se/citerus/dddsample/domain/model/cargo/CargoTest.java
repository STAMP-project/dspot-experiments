package se.citerus.dddsample.domain.model.cargo;


import HandlingEvent.Type;
import Location.UNKNOWN;
import Voyage.NONE;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import se.citerus.dddsample.application.util.DateTestUtil;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.location.SampleLocations;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;


public class CargoTest {
    private List<HandlingEvent> events;

    private Voyage voyage;

    @Test
    public void testConstruction() {
        final TrackingId trackingId = new TrackingId("XYZ");
        final Date arrivalDeadline = DateTestUtil.toDate("2009-03-13");
        final RouteSpecification routeSpecification = new RouteSpecification(SampleLocations.STOCKHOLM, SampleLocations.MELBOURNE, arrivalDeadline);
        final Cargo cargo = new Cargo(trackingId, routeSpecification);
        assertThat(cargo.delivery().routingStatus()).isEqualTo(RoutingStatus.NOT_ROUTED);
        assertThat(cargo.delivery().transportStatus()).isEqualTo(TransportStatus.NOT_RECEIVED);
        assertThat(cargo.delivery().lastKnownLocation()).isEqualTo(UNKNOWN);
        assertThat(cargo.delivery().currentVoyage()).isEqualTo(NONE);
    }

    @Test
    public void testRoutingStatus() {
        final Cargo cargo = new Cargo(new TrackingId("XYZ"), new RouteSpecification(SampleLocations.STOCKHOLM, SampleLocations.MELBOURNE, new Date()));
        final Itinerary good = new Itinerary();
        final Itinerary bad = new Itinerary();
        final RouteSpecification acceptOnlyGood = new RouteSpecification(cargo.origin(), cargo.routeSpecification().destination(), new Date()) {
            @Override
            public boolean isSatisfiedBy(Itinerary itinerary) {
                return itinerary == good;
            }
        };
        cargo.specifyNewRoute(acceptOnlyGood);
        assertThat(cargo.delivery().routingStatus()).isEqualTo(RoutingStatus.NOT_ROUTED);
        cargo.assignToRoute(bad);
        assertThat(cargo.delivery().routingStatus()).isEqualTo(RoutingStatus.MISROUTED);
        cargo.assignToRoute(good);
        assertThat(cargo.delivery().routingStatus()).isEqualTo(RoutingStatus.ROUTED);
    }

    @Test
    public void testlastKnownLocationUnknownWhenNoEvents() {
        Cargo cargo = new Cargo(new TrackingId("XYZ"), new RouteSpecification(SampleLocations.STOCKHOLM, SampleLocations.MELBOURNE, new Date()));
        assertThat(cargo.delivery().lastKnownLocation()).isEqualTo(UNKNOWN);
    }

    @Test
    public void testlastKnownLocationReceived() throws Exception {
        Cargo cargo = populateCargoReceivedStockholm();
        assertThat(cargo.delivery().lastKnownLocation()).isEqualTo(SampleLocations.STOCKHOLM);
    }

    @Test
    public void testlastKnownLocationClaimed() throws Exception {
        Cargo cargo = populateCargoClaimedMelbourne();
        assertThat(cargo.delivery().lastKnownLocation()).isEqualTo(SampleLocations.MELBOURNE);
    }

    @Test
    public void testlastKnownLocationUnloaded() throws Exception {
        Cargo cargo = populateCargoOffHongKong();
        assertThat(cargo.delivery().lastKnownLocation()).isEqualTo(SampleLocations.HONGKONG);
    }

    @Test
    public void testlastKnownLocationloaded() throws Exception {
        Cargo cargo = populateCargoOnHamburg();
        assertThat(cargo.delivery().lastKnownLocation()).isEqualTo(SampleLocations.HAMBURG);
    }

    @Test
    public void testEquality() {
        RouteSpecification spec1 = new RouteSpecification(SampleLocations.STOCKHOLM, SampleLocations.HONGKONG, new Date());
        RouteSpecification spec2 = new RouteSpecification(SampleLocations.STOCKHOLM, SampleLocations.MELBOURNE, new Date());
        Cargo c1 = new Cargo(new TrackingId("ABC"), spec1);
        Cargo c2 = new Cargo(new TrackingId("CBA"), spec1);
        Cargo c3 = new Cargo(new TrackingId("ABC"), spec2);
        Cargo c4 = new Cargo(new TrackingId("ABC"), spec1);
        assertThat(c1.equals(c4)).as("Cargos should be equal when TrackingIDs are equal").isTrue();
        assertThat(c1.equals(c3)).as("Cargos should be equal when TrackingIDs are equal").isTrue();
        assertThat(c3.equals(c4)).as("Cargos should be equal when TrackingIDs are equal").isTrue();
        assertThat(c1.equals(c2)).as("Cargos are not equal when TrackingID differ").isFalse();
    }

    @Test
    public void testIsUnloadedAtFinalDestination() {
        Cargo cargo = setUpCargoWithItinerary(SampleLocations.HANGZOU, SampleLocations.TOKYO, SampleLocations.NEWYORK);
        assertThat(cargo.delivery().isUnloadedAtDestination()).isFalse();
        // Adding an event unrelated to unloading at final destination
        events.add(new HandlingEvent(cargo, new Date(10), new Date(), Type.RECEIVE, SampleLocations.HANGZOU));
        cargo.deriveDeliveryProgress(new se.citerus.dddsample.domain.model.handling.HandlingHistory(events));
        assertThat(cargo.delivery().isUnloadedAtDestination()).isFalse();
        Voyage voyage = new Voyage.Builder(new VoyageNumber("0123"), SampleLocations.HANGZOU).addMovement(SampleLocations.NEWYORK, new Date(), new Date()).build();
        // Adding an unload event, but not at the final destination
        events.add(new HandlingEvent(cargo, new Date(20), new Date(), Type.UNLOAD, SampleLocations.TOKYO, voyage));
        cargo.deriveDeliveryProgress(new se.citerus.dddsample.domain.model.handling.HandlingHistory(events));
        assertThat(cargo.delivery().isUnloadedAtDestination()).isFalse();
        // Adding an event in the final destination, but not unload
        events.add(new HandlingEvent(cargo, new Date(30), new Date(), Type.CUSTOMS, SampleLocations.NEWYORK));
        cargo.deriveDeliveryProgress(new se.citerus.dddsample.domain.model.handling.HandlingHistory(events));
        assertThat(cargo.delivery().isUnloadedAtDestination()).isFalse();
        // Finally, cargo is unloaded at final destination
        events.add(new HandlingEvent(cargo, new Date(40), new Date(), Type.UNLOAD, SampleLocations.NEWYORK, voyage));
        cargo.deriveDeliveryProgress(new se.citerus.dddsample.domain.model.handling.HandlingHistory(events));
        assertThat(cargo.delivery().isUnloadedAtDestination()).isTrue();
    }

    @Test
    public void testIsMisdirected() {
        // A cargo with no itinerary is not misdirected
        Cargo cargo = new Cargo(new TrackingId("TRKID"), new RouteSpecification(SampleLocations.SHANGHAI, SampleLocations.GOTHENBURG, new Date()));
        assertThat(cargo.delivery().isMisdirected()).isFalse();
        cargo = setUpCargoWithItinerary(SampleLocations.SHANGHAI, SampleLocations.ROTTERDAM, SampleLocations.GOTHENBURG);
        // A cargo with no handling events is not misdirected
        assertThat(cargo.delivery().isMisdirected()).isFalse();
        Collection<HandlingEvent> handlingEvents = new ArrayList<HandlingEvent>();
        // Happy path
        handlingEvents.add(new HandlingEvent(cargo, new Date(10), new Date(20), Type.RECEIVE, SampleLocations.SHANGHAI));
        handlingEvents.add(new HandlingEvent(cargo, new Date(30), new Date(40), Type.LOAD, SampleLocations.SHANGHAI, voyage));
        handlingEvents.add(new HandlingEvent(cargo, new Date(50), new Date(60), Type.UNLOAD, SampleLocations.ROTTERDAM, voyage));
        handlingEvents.add(new HandlingEvent(cargo, new Date(70), new Date(80), Type.LOAD, SampleLocations.ROTTERDAM, voyage));
        handlingEvents.add(new HandlingEvent(cargo, new Date(90), new Date(100), Type.UNLOAD, SampleLocations.GOTHENBURG, voyage));
        handlingEvents.add(new HandlingEvent(cargo, new Date(110), new Date(120), Type.CLAIM, SampleLocations.GOTHENBURG));
        handlingEvents.add(new HandlingEvent(cargo, new Date(130), new Date(140), Type.CUSTOMS, SampleLocations.GOTHENBURG));
        events.addAll(handlingEvents);
        cargo.deriveDeliveryProgress(new se.citerus.dddsample.domain.model.handling.HandlingHistory(events));
        assertThat(cargo.delivery().isMisdirected()).isFalse();
        // Try a couple of failing ones
        cargo = setUpCargoWithItinerary(SampleLocations.SHANGHAI, SampleLocations.ROTTERDAM, SampleLocations.GOTHENBURG);
        handlingEvents = new ArrayList<HandlingEvent>();
        handlingEvents.add(new HandlingEvent(cargo, new Date(), new Date(), Type.RECEIVE, SampleLocations.HANGZOU));
        events.addAll(handlingEvents);
        cargo.deriveDeliveryProgress(new se.citerus.dddsample.domain.model.handling.HandlingHistory(events));
        assertThat(cargo.delivery().isMisdirected()).isTrue();
        cargo = setUpCargoWithItinerary(SampleLocations.SHANGHAI, SampleLocations.ROTTERDAM, SampleLocations.GOTHENBURG);
        handlingEvents = new ArrayList<HandlingEvent>();
        handlingEvents.add(new HandlingEvent(cargo, new Date(10), new Date(20), Type.RECEIVE, SampleLocations.SHANGHAI));
        handlingEvents.add(new HandlingEvent(cargo, new Date(30), new Date(40), Type.LOAD, SampleLocations.SHANGHAI, voyage));
        handlingEvents.add(new HandlingEvent(cargo, new Date(50), new Date(60), Type.UNLOAD, SampleLocations.ROTTERDAM, voyage));
        handlingEvents.add(new HandlingEvent(cargo, new Date(70), new Date(80), Type.LOAD, SampleLocations.ROTTERDAM, voyage));
        events.addAll(handlingEvents);
        cargo.deriveDeliveryProgress(new se.citerus.dddsample.domain.model.handling.HandlingHistory(events));
        assertThat(cargo.delivery().isMisdirected()).isTrue();
        cargo = setUpCargoWithItinerary(SampleLocations.SHANGHAI, SampleLocations.ROTTERDAM, SampleLocations.GOTHENBURG);
        handlingEvents = new ArrayList<HandlingEvent>();
        handlingEvents.add(new HandlingEvent(cargo, new Date(10), new Date(20), Type.RECEIVE, SampleLocations.SHANGHAI));
        handlingEvents.add(new HandlingEvent(cargo, new Date(30), new Date(40), Type.LOAD, SampleLocations.SHANGHAI, voyage));
        handlingEvents.add(new HandlingEvent(cargo, new Date(50), new Date(60), Type.UNLOAD, SampleLocations.ROTTERDAM, voyage));
        handlingEvents.add(new HandlingEvent(cargo, new Date(), new Date(), Type.CLAIM, SampleLocations.ROTTERDAM));
        events.addAll(handlingEvents);
        cargo.deriveDeliveryProgress(new se.citerus.dddsample.domain.model.handling.HandlingHistory(events));
        assertThat(cargo.delivery().isMisdirected()).isTrue();
    }
}

