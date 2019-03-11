package se.citerus.dddsample.domain.model.cargo;


import java.util.Arrays;
import org.junit.Test;
import se.citerus.dddsample.domain.model.location.SampleLocations;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;


public class RouteSpecificationTest {
    final Voyage hongKongTokyoNewYork = new Voyage.Builder(new VoyageNumber("V001"), SampleLocations.HONGKONG).addMovement(SampleLocations.TOKYO, toDate("2009-02-01"), toDate("2009-02-05")).addMovement(SampleLocations.NEWYORK, toDate("2009-02-06"), toDate("2009-02-10")).addMovement(SampleLocations.HONGKONG, toDate("2009-02-11"), toDate("2009-02-14")).build();

    final Voyage dallasNewYorkChicago = new Voyage.Builder(new VoyageNumber("V002"), SampleLocations.DALLAS).addMovement(SampleLocations.NEWYORK, toDate("2009-02-06"), toDate("2009-02-07")).addMovement(SampleLocations.CHICAGO, toDate("2009-02-12"), toDate("2009-02-20")).build();

    // TODO:
    // it shouldn't be possible to create Legs that have load/unload locations
    // and/or dates that don't match the voyage's carrier movements.
    final Itinerary itinerary = new Itinerary(Arrays.asList(new Leg(hongKongTokyoNewYork, SampleLocations.HONGKONG, SampleLocations.NEWYORK, toDate("2009-02-01"), toDate("2009-02-10")), new Leg(dallasNewYorkChicago, SampleLocations.NEWYORK, SampleLocations.CHICAGO, toDate("2009-02-12"), toDate("2009-02-20"))));

    @Test
    public void testIsSatisfiedBy_Success() {
        RouteSpecification routeSpecification = new RouteSpecification(SampleLocations.HONGKONG, SampleLocations.CHICAGO, toDate("2009-03-01"));
        assertThat(routeSpecification.isSatisfiedBy(itinerary)).isTrue();
    }

    @Test
    public void testIsSatisfiedBy_WrongOrigin() {
        RouteSpecification routeSpecification = new RouteSpecification(SampleLocations.HANGZOU, SampleLocations.CHICAGO, toDate("2009-03-01"));
        assertThat(routeSpecification.isSatisfiedBy(itinerary)).isFalse();
    }

    @Test
    public void testIsSatisfiedBy_WrongDestination() {
        RouteSpecification routeSpecification = new RouteSpecification(SampleLocations.HONGKONG, SampleLocations.DALLAS, toDate("2009-03-01"));
        assertThat(routeSpecification.isSatisfiedBy(itinerary)).isFalse();
    }

    @Test
    public void testIsSatisfiedBy_MissedDeadline() {
        RouteSpecification routeSpecification = new RouteSpecification(SampleLocations.HONGKONG, SampleLocations.CHICAGO, toDate("2009-02-15"));
        assertThat(routeSpecification.isSatisfiedBy(itinerary)).isFalse();
    }
}

