package org.opentripplanner.routing.impl;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.CoordinateSequenceFactory;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.opentripplanner.common.geometry.GeometryUtils;
import org.opentripplanner.common.model.GenericLocation;
import org.opentripplanner.model.Agency;
import org.opentripplanner.model.FeedScopedId;
import org.opentripplanner.model.Route;
import org.opentripplanner.model.Stop;
import org.opentripplanner.model.StopPattern;
import org.opentripplanner.model.StopTime;
import org.opentripplanner.model.Trip;
import org.opentripplanner.routing.core.RoutingContext;
import org.opentripplanner.routing.core.RoutingRequest;
import org.opentripplanner.routing.core.ServiceDay;
import org.opentripplanner.routing.core.TraverseModeSet;
import org.opentripplanner.routing.edgetype.PatternHop;
import org.opentripplanner.routing.edgetype.TripPattern;
import org.opentripplanner.routing.graph.Edge;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.graph.Vertex;
import org.opentripplanner.routing.trippattern.Deduplicator;
import org.opentripplanner.routing.trippattern.TripTimes;
import org.opentripplanner.routing.vertextype.PatternArriveVertex;
import org.opentripplanner.routing.vertextype.PatternDepartVertex;
import org.opentripplanner.routing.vertextype.TransitStop;


/* FIXME: This test has become seriously ugly after recent changes to OTP. Using mocks, which seemed
like a good idea at the time, became more of a liability when it turned out lots of mocks were no
longer valid. The idea of a decoupled unit test has certainly not worked out the way it should've
worked out in theory. It would be very wise to rewrite this test to be simpler and not use mocks.
FIXME too: Even worse, it didn't even fail when OnBoardDepartServiceImpl turned out to be broken.
 */
public class OnBoardDepartServiceImplTest {
    OnBoardDepartServiceImpl onBoardDepartServiceImpl = new OnBoardDepartServiceImpl();

    @Test
    public final void testOnBoardDepartureTime() {
        Coordinate[] coordinates = new Coordinate[5];
        coordinates[0] = new Coordinate(0.0, 0.0);
        coordinates[1] = new Coordinate(0.0, 1.0);
        coordinates[2] = new Coordinate(2.0, 1.0);
        coordinates[3] = new Coordinate(5.0, 1.0);
        coordinates[4] = new Coordinate(5.0, 5.0);
        PatternDepartVertex depart = Mockito.mock(PatternDepartVertex.class);
        PatternArriveVertex dwell = Mockito.mock(PatternArriveVertex.class);
        PatternArriveVertex arrive = Mockito.mock(PatternArriveVertex.class);
        Graph graph = Mockito.mock(Graph.class);
        RoutingRequest routingRequest = Mockito.mock(RoutingRequest.class);
        ServiceDay serviceDay = Mockito.mock(ServiceDay.class);
        // You're probably not supposed to do this to mocks (access their fields directly)
        // But I know of no other way to do this since the mock object has only action-free stub methods.
        routingRequest.modes = new TraverseModeSet("WALK,TRANSIT");
        Mockito.when(graph.getTimeZone()).thenReturn(TimeZone.getTimeZone("GMT"));
        GeometryFactory geometryFactory = GeometryUtils.getGeometryFactory();
        CoordinateSequenceFactory coordinateSequenceFactory = geometryFactory.getCoordinateSequenceFactory();
        CoordinateSequence coordinateSequence = coordinateSequenceFactory.create(coordinates);
        LineString geometry = new LineString(coordinateSequence, geometryFactory);
        ArrayList<Edge> hops = new ArrayList<Edge>(2);
        RoutingContext routingContext = new RoutingContext(routingRequest, graph, null, arrive);
        FeedScopedId id = new FeedScopedId("Agency", "ID");
        Agency agency = new Agency();
        Route route = new Route();
        ArrayList<StopTime> stopTimes = new ArrayList<StopTime>(3);
        StopTime stopDepartTime = new StopTime();
        StopTime stopDwellTime = new StopTime();
        StopTime stopArriveTime = new StopTime();
        Stop stopDepart = new Stop();
        Stop stopDwell = new Stop();
        Stop stopArrive = new Stop();
        Trip trip = new Trip();
        routingContext.serviceDays = new ArrayList<ServiceDay>(Collections.singletonList(serviceDay));
        agency.setId(id.getAgencyId());
        route.setId(id);
        route.setAgency(agency);
        stopDepart.setId(id);
        stopDwell.setId(id);
        stopArrive.setId(id);
        stopDepartTime.setStop(stopDepart);
        stopDepartTime.setDepartureTime(0);
        stopDwellTime.setArrivalTime(20);
        stopDwellTime.setStop(stopDwell);
        stopDwellTime.setDepartureTime(40);
        stopArriveTime.setArrivalTime(60);
        stopArriveTime.setStop(stopArrive);
        stopTimes.add(stopDepartTime);
        stopTimes.add(stopDwellTime);
        stopTimes.add(stopArriveTime);
        trip.setId(id);
        trip.setTripHeadsign("The right");
        trip.setRoute(route);
        TripTimes tripTimes = new TripTimes(trip, stopTimes, new Deduplicator());
        StopPattern stopPattern = new StopPattern(stopTimes);
        TripPattern tripPattern = new TripPattern(route, stopPattern);
        TripPattern.generateUniqueIds(Arrays.asList(tripPattern));
        Mockito.when(depart.getTripPattern()).thenReturn(tripPattern);
        Mockito.when(dwell.getTripPattern()).thenReturn(tripPattern);
        PatternHop patternHop0 = new PatternHop(depart, dwell, stopDepart, stopDwell, 0);
        PatternHop patternHop1 = new PatternHop(dwell, arrive, stopDwell, stopArrive, 1);
        hops.add(patternHop0);
        hops.add(patternHop1);
        Mockito.when(graph.getEdges()).thenReturn(hops);
        Mockito.when(depart.getCoordinate()).thenReturn(new Coordinate(0, 0));
        Mockito.when(dwell.getCoordinate()).thenReturn(new Coordinate(0, 0));
        Mockito.when(arrive.getCoordinate()).thenReturn(new Coordinate(0, 0));
        routingRequest.from = new GenericLocation();
        routingRequest.startingTransitTripId = id;
        Mockito.when(serviceDay.secondsSinceMidnight(ArgumentMatchers.anyInt())).thenReturn(9);
        patternHop0.setGeometry(geometry);
        tripPattern.add(tripTimes);
        graph.index = new org.opentripplanner.routing.graph.GraphIndex(graph);
        coordinates = new Coordinate[3];
        coordinates[0] = new Coordinate(3.5, 1.0);
        coordinates[1] = new Coordinate(5.0, 1.0);
        coordinates[2] = new Coordinate(5.0, 5.0);
        coordinateSequence = coordinateSequenceFactory.create(coordinates);
        geometry = new LineString(coordinateSequence, geometryFactory);
        Vertex vertex = onBoardDepartServiceImpl.setupDepartOnBoard(routingContext);
        Edge edge = vertex.getOutgoing().toArray(new Edge[1])[0];
        Assert.assertEquals(vertex, edge.getFromVertex());
        Assert.assertEquals(dwell, edge.getToVertex());
        Assert.assertEquals("The right", edge.getDirection());
        Assert.assertEquals(geometry, edge.getGeometry());
        Assert.assertEquals(coordinates[0].x, vertex.getX(), 0.0);
        Assert.assertEquals(coordinates[0].y, vertex.getY(), 0.0);
    }

    @Test
    public final void testOnBoardDepartureAtArrivalTime() {
        Coordinate[] coordinates = new Coordinate[2];
        coordinates[0] = new Coordinate(0.0, 0.0);
        coordinates[1] = new Coordinate(0.0, 1.0);
        TransitStop station0 = Mockito.mock(TransitStop.class);
        TransitStop station1 = Mockito.mock(TransitStop.class);
        PatternDepartVertex depart = Mockito.mock(PatternDepartVertex.class);
        PatternArriveVertex arrive = Mockito.mock(PatternArriveVertex.class);
        Graph graph = Mockito.mock(Graph.class);
        RoutingRequest routingRequest = Mockito.mock(RoutingRequest.class);
        ServiceDay serviceDay = Mockito.mock(ServiceDay.class);
        // You're probably not supposed to do this to mocks (access their fields directly)
        // But I know of no other way to do this since the mock object has only action-free stub methods.
        routingRequest.modes = new TraverseModeSet("WALK,TRANSIT");
        Mockito.when(graph.getTimeZone()).thenReturn(TimeZone.getTimeZone("GMT"));
        Mockito.when(station0.getX()).thenReturn(coordinates[0].x);
        Mockito.when(station0.getY()).thenReturn(coordinates[0].y);
        Mockito.when(station1.getX()).thenReturn(coordinates[1].x);
        Mockito.when(station1.getY()).thenReturn(coordinates[1].y);
        RoutingContext routingContext = new RoutingContext(routingRequest, graph, null, arrive);
        FeedScopedId id = new FeedScopedId("Agency", "ID");
        Agency agency = new Agency();
        Route route = new Route();
        ArrayList<StopTime> stopTimes = new ArrayList<StopTime>(2);
        StopTime stopDepartTime = new StopTime();
        StopTime stopArriveTime = new StopTime();
        Stop stopDepart = new Stop();
        Stop stopArrive = new Stop();
        Trip trip = new Trip();
        routingContext.serviceDays = new ArrayList<ServiceDay>(Collections.singletonList(serviceDay));
        agency.setId(id.getAgencyId());
        route.setId(id);
        route.setAgency(agency);
        stopDepart.setId(new FeedScopedId("Station", "0"));
        stopArrive.setId(new FeedScopedId("Station", "1"));
        stopDepartTime.setStop(stopDepart);
        stopDepartTime.setDepartureTime(0);
        stopArriveTime.setArrivalTime(10);
        stopArriveTime.setStop(stopArrive);
        stopTimes.add(stopDepartTime);
        stopTimes.add(stopArriveTime);
        trip.setId(id);
        trip.setRoute(route);
        TripTimes tripTimes = new TripTimes(trip, stopTimes, new Deduplicator());
        StopPattern stopPattern = new StopPattern(stopTimes);
        TripPattern tripPattern = new TripPattern(route, stopPattern);
        TripPattern.generateUniqueIds(Arrays.asList(tripPattern));
        Mockito.when(depart.getTripPattern()).thenReturn(tripPattern);
        PatternHop patternHop = new PatternHop(depart, arrive, stopDepart, stopArrive, 0);
        Mockito.when(graph.getEdges()).thenReturn(Collections.<Edge>singletonList(patternHop));
        Mockito.when(depart.getCoordinate()).thenReturn(new Coordinate(0, 0));
        Mockito.when(arrive.getCoordinate()).thenReturn(new Coordinate(0, 0));
        routingRequest.from = new GenericLocation();
        routingRequest.startingTransitTripId = id;
        Mockito.when(serviceDay.secondsSinceMidnight(ArgumentMatchers.anyInt())).thenReturn(10);
        Mockito.when(graph.getVertex("Station_0")).thenReturn(station0);
        Mockito.when(graph.getVertex("Station_1")).thenReturn(station1);
        tripPattern.add(tripTimes);
        graph.index = new org.opentripplanner.routing.graph.GraphIndex(graph);
        Vertex vertex = onBoardDepartServiceImpl.setupDepartOnBoard(routingContext);
        Assert.assertEquals(coordinates[1].x, vertex.getX(), 0.0);
        Assert.assertEquals(coordinates[1].y, vertex.getY(), 0.0);
    }

    @Test
    public final void testOnBoardAtStation() {
        TransitStop station0 = Mockito.mock(TransitStop.class);
        TransitStop station1 = Mockito.mock(TransitStop.class);
        TransitStop station2 = Mockito.mock(TransitStop.class);
        PatternDepartVertex depart = Mockito.mock(PatternDepartVertex.class);
        PatternArriveVertex dwell = Mockito.mock(PatternArriveVertex.class);
        PatternArriveVertex arrive = Mockito.mock(PatternArriveVertex.class);
        Graph graph = Mockito.mock(Graph.class);
        RoutingRequest routingRequest = Mockito.mock(RoutingRequest.class);
        ServiceDay serviceDay = Mockito.mock(ServiceDay.class);
        // You're probably not supposed to do this to mocks (access their fields directly)
        // But I know of no other way to do this since the mock object has only action-free stub methods.
        routingRequest.modes = new TraverseModeSet("WALK,TRANSIT");
        Mockito.when(graph.getTimeZone()).thenReturn(TimeZone.getTimeZone("GMT"));
        ArrayList<Edge> hops = new ArrayList<Edge>(2);
        RoutingContext routingContext = new RoutingContext(routingRequest, graph, null, arrive);
        Agency agency = new Agency();
        FeedScopedId id = new FeedScopedId("Agency", "ID");
        Route route = new Route();
        ArrayList<StopTime> stopTimes = new ArrayList<StopTime>(2);
        StopTime stopDepartTime = new StopTime();
        StopTime stopDwellTime = new StopTime();
        StopTime stopArriveTime = new StopTime();
        Stop stopDepart = new Stop();
        Stop stopDwell = new Stop();
        Stop stopArrive = new Stop();
        Trip trip = new Trip();
        routingContext.serviceDays = new ArrayList<ServiceDay>(Collections.singletonList(serviceDay));
        agency.setId(id.getAgencyId());
        route.setId(id);
        route.setAgency(agency);
        stopDepart.setId(new FeedScopedId("Station", "0"));
        stopDwell.setId(new FeedScopedId("Station", "1"));
        stopArrive.setId(new FeedScopedId("Station", "2"));
        stopDepartTime.setStop(stopDepart);
        stopDepartTime.setDepartureTime(0);
        stopDwellTime.setArrivalTime(20);
        stopDwellTime.setStop(stopDwell);
        stopDwellTime.setDepartureTime(40);
        stopArriveTime.setArrivalTime(60);
        stopArriveTime.setStop(stopArrive);
        stopTimes.add(stopDepartTime);
        stopTimes.add(stopDwellTime);
        stopTimes.add(stopArriveTime);
        trip.setId(id);
        trip.setRoute(route);
        TripTimes tripTimes = new TripTimes(trip, stopTimes, new Deduplicator());
        StopPattern stopPattern = new StopPattern(stopTimes);
        TripPattern tripPattern = new TripPattern(route, stopPattern);
        TripPattern.generateUniqueIds(Arrays.asList(tripPattern));
        Mockito.when(depart.getTripPattern()).thenReturn(tripPattern);
        Mockito.when(dwell.getTripPattern()).thenReturn(tripPattern);
        PatternHop patternHop0 = new PatternHop(depart, dwell, stopDepart, stopDwell, 0);
        PatternHop patternHop1 = new PatternHop(dwell, arrive, stopDwell, stopArrive, 1);
        hops.add(patternHop0);
        hops.add(patternHop1);
        Mockito.when(graph.getEdges()).thenReturn(hops);
        Mockito.when(depart.getCoordinate()).thenReturn(new Coordinate(0, 0));
        Mockito.when(dwell.getCoordinate()).thenReturn(new Coordinate(0, 0));
        Mockito.when(arrive.getCoordinate()).thenReturn(new Coordinate(0, 0));
        routingRequest.from = new GenericLocation();
        routingRequest.startingTransitTripId = id;
        Mockito.when(graph.getVertex("Station_0")).thenReturn(station0);
        Mockito.when(graph.getVertex("Station_1")).thenReturn(station1);
        Mockito.when(graph.getVertex("Station_2")).thenReturn(station2);
        tripPattern.add(tripTimes);
        graph.index = new org.opentripplanner.routing.graph.GraphIndex(graph);
        Mockito.when(serviceDay.secondsSinceMidnight(ArgumentMatchers.anyInt())).thenReturn(0);
        Assert.assertEquals(station0, onBoardDepartServiceImpl.setupDepartOnBoard(routingContext));
        Mockito.when(serviceDay.secondsSinceMidnight(ArgumentMatchers.anyInt())).thenReturn(20);
        Assert.assertEquals(station1, onBoardDepartServiceImpl.setupDepartOnBoard(routingContext));
        Mockito.when(serviceDay.secondsSinceMidnight(ArgumentMatchers.anyInt())).thenReturn(30);
        Assert.assertEquals(station1, onBoardDepartServiceImpl.setupDepartOnBoard(routingContext));
        Mockito.when(serviceDay.secondsSinceMidnight(ArgumentMatchers.anyInt())).thenReturn(40);
        Assert.assertEquals(station1, onBoardDepartServiceImpl.setupDepartOnBoard(routingContext));
        Mockito.when(serviceDay.secondsSinceMidnight(ArgumentMatchers.anyInt())).thenReturn(60);
        Assert.assertEquals(station2, onBoardDepartServiceImpl.setupDepartOnBoard(routingContext));
    }
}

