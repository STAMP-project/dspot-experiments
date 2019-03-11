package org.opentripplanner.api.resource;


import FareType.regular;
import FareType.senior;
import FareType.special;
import FareType.student;
import FareType.tram;
import java.util.Locale;
import java.util.Set;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Test;
import org.opentripplanner.api.model.Itinerary;
import org.opentripplanner.model.FeedScopedId;
import org.opentripplanner.model.calendar.CalendarServiceData;
import org.opentripplanner.model.calendar.ServiceDate;
import org.opentripplanner.routing.core.Fare;
import org.opentripplanner.routing.core.RoutingRequest;
import org.opentripplanner.routing.core.State;
import org.opentripplanner.routing.core.WrappedCurrency;
import org.opentripplanner.routing.edgetype.FreeEdge;
import org.opentripplanner.routing.edgetype.LegSwitchingEdge;
import org.opentripplanner.routing.error.TrivialPathException;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.services.FareService;
import org.opentripplanner.routing.spt.GraphPath;
import org.opentripplanner.routing.vertextype.ExitVertex;


public class GraphPathToTripPlanConverterTest {
    private static final double[] F_DISTANCE = new double[]{ 3, 9996806.8, 3539050.5, 7, 2478638.8, 4, 2, 1, 0 };

    private static final double O_DISTANCE = 7286193.2;

    private static final double OCTANT = (Math.PI) / 4;

    private static final double NORTH = (GraphPathToTripPlanConverterTest.OCTANT) * 0;

    private static final double NORTHEAST = (GraphPathToTripPlanConverterTest.OCTANT) * 1;

    private static final double EAST = (GraphPathToTripPlanConverterTest.OCTANT) * 2;

    private static final double NORTHWEST = (GraphPathToTripPlanConverterTest.OCTANT) * (-1);

    private static final double SOUTH = (GraphPathToTripPlanConverterTest.OCTANT) * 4;

    private static final double EPSILON = 0.1;

    private static final SimpleTimeZone timeZone = new SimpleTimeZone(2, "CEST");

    private static final String alertsExample = "Mine is the last voice that you will ever hear. Do not be alarmed.";

    private static final Locale locale = new Locale("en");

    /**
     * Test the generateItinerary() method. This test is intended to be comprehensive but fast.
     * Any future changes to the generateItinerary() method should be accompanied by changes in this
     * test, to ensure continued maximum coverage.
     */
    @Test
    public void testGenerateItinerary() {
        GraphPath[] graphPaths = buildPaths();
        compare(GraphPathToTripPlanConverter.generateItinerary(graphPaths[0], true, false, GraphPathToTripPlanConverterTest.locale), GraphPathToTripPlanConverterTest.Type.FORWARD);
        compare(GraphPathToTripPlanConverter.generateItinerary(graphPaths[1], true, false, GraphPathToTripPlanConverterTest.locale), GraphPathToTripPlanConverterTest.Type.BACKWARD);
        compare(GraphPathToTripPlanConverter.generateItinerary(graphPaths[2], true, false, GraphPathToTripPlanConverterTest.locale), GraphPathToTripPlanConverterTest.Type.ONBOARD);
    }

    /**
     * Test that a LEG_SWITCH mode at the end of a graph path does not generate an extra leg.
     * Also test that such a LEG_SWITCH mode does not show up as part of the itinerary.
     */
    @Test
    public void testEndWithLegSwitch() {
        // Reuse testGenerateItinerary()'s graph path, but shorten it
        GraphPath graphPath = new GraphPath(buildPaths()[0].states.get(3), false);
        Itinerary itinerary = GraphPathToTripPlanConverter.generateItinerary(graphPath, false, false, GraphPathToTripPlanConverterTest.locale);
        Assert.assertEquals(1, itinerary.legs.size());
        Assert.assertEquals("WALK", itinerary.legs.get(0).mode);
    }

    /**
     * Test that empty graph paths throw a TrivialPathException
     */
    @Test(expected = TrivialPathException.class)
    public void testEmptyGraphPath() {
        RoutingRequest options = new RoutingRequest();
        Graph graph = new Graph();
        ExitVertex vertex = new ExitVertex(graph, "Vertex", 0, 0, 0);
        options.rctx = new org.opentripplanner.routing.core.RoutingContext(options, graph, vertex, vertex);
        GraphPath graphPath = new GraphPath(new State(options), false);
        GraphPathToTripPlanConverter.generateItinerary(graphPath, false, false, GraphPathToTripPlanConverterTest.locale);
    }

    /**
     * Test that graph paths with only null and LEG_SWITCH modes throw a TrivialPathException
     */
    @Test(expected = TrivialPathException.class)
    public void testLegSwitchOnlyGraphPath() {
        RoutingRequest options = new RoutingRequest();
        Graph graph = new Graph();
        ExitVertex start = new ExitVertex(graph, "Start", 0, (-90), 0);
        ExitVertex middle = new ExitVertex(graph, "Middle", 0, 0, 0);
        ExitVertex end = new ExitVertex(graph, "End", 0, 90, 0);
        FreeEdge depart = new FreeEdge(start, middle);
        LegSwitchingEdge arrive = new LegSwitchingEdge(middle, end);
        options.rctx = new org.opentripplanner.routing.core.RoutingContext(options, graph, start, end);
        State intermediate = depart.traverse(new State(options));
        GraphPath graphPath = new GraphPath(arrive.traverse(intermediate), false);
        GraphPathToTripPlanConverter.generateItinerary(graphPath, false, false, GraphPathToTripPlanConverterTest.locale);
    }

    /**
     * This class extends the {@link CalendarServiceData} class to allow for easier testing.
     * It includes methods to return both the set of service ids and the time zone used for testing.
     */
    private static final class CalendarServiceDataStub extends CalendarServiceData {
        private static final long serialVersionUID = 1L;

        final Set<FeedScopedId> serviceIds;

        CalendarServiceDataStub(Set<FeedScopedId> serviceIds) {
            this.serviceIds = serviceIds;
        }

        @Override
        public Set<FeedScopedId> getServiceIds() {
            return serviceIds;
        }

        @Override
        public Set<FeedScopedId> getServiceIdsForDate(ServiceDate date) {
            return serviceIds;
        }

        @Override
        public TimeZone getTimeZoneForAgencyId(String agencyId) {
            return GraphPathToTripPlanConverterTest.timeZone;
        }
    }

    /**
     * This class implements the {@link FareService} interface to allow for testing.
     * It will return the same fare at every invocation.
     */
    private static final class FareServiceStub implements FareService {
        @Override
        public Fare getCost(GraphPath path) {
            Fare fare = new Fare();
            fare.addFare(regular, new WrappedCurrency(), 0);
            fare.addFare(student, new WrappedCurrency(), 1);
            fare.addFare(senior, new WrappedCurrency(), 2);
            fare.addFare(tram, new WrappedCurrency(), 4);
            fare.addFare(special, new WrappedCurrency(), 8);
            return fare;
        }
    }

    /**
     * This enum is used to distinguish between the different graph paths and resulting itineraries.
     * Its three values correspond to the forward, backward and onboard graph paths and itineraries.
     * When future values are added, the test should not be assumed to fail upon encountering those.
     */
    private static enum Type {

        FORWARD,
        BACKWARD,
        ONBOARD;}
}

