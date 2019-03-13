package org.opentripplanner.routing.flex;


import BoardAlightType.DEFAULT;
import BoardAlightType.DEVIATED;
import BoardAlightType.FLAG_STOP;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.opentripplanner.ConstantsForTests;
import org.opentripplanner.routing.algorithm.AStar;
import org.opentripplanner.routing.core.RoutingRequest;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.spt.GraphPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class VermontFlexRoutingTest {
    private static final Logger LOG = LoggerFactory.getLogger(VermontFlexRoutingTest.class);

    private Graph graph = ConstantsForTests.getInstance().getVermontGraph();

    private static final int MAX_WALK_DISTANCE = 804;

    private static final double CALL_AND_RIDE_RELUCTANCE = 3.0;

    private static final double WALK_RELUCTANCE = 3.0;

    private static final double WAIT_AT_BEGINNING_FACTOR = 0;

    private static final int TRANSFER_PENALTY = 600;

    private static final boolean IGNORE_DRT_ADVANCE_MIN_BOOKING = true;

    // Flag stop on both sides, on Jay-Lyn (route 1382)
    @Test
    public void testFlagStop() {
        RoutingRequest options = buildRequest("44.4214596,-72.019371", "44.4277732,-72.01203514", "2018-05-23", "1:37pm");
        GraphPath path = getPathToDestination(options);
        List<Ride> rides = Ride.createRides(path);
        Assert.assertEquals(1, rides.size());
        Ride ride = rides.get(0);
        Assert.assertEquals("1382", ride.getRoute().getId());
        Assert.assertEquals(FLAG_STOP, ride.getBoardType());
        Assert.assertEquals(FLAG_STOP, ride.getAlightType());
        checkFare(path);
        options.rctx.destroy();
    }

    // Deviated Route on both ends
    @Test
    public void testCallAndRide() {
        RoutingRequest options = buildRequest("44.38485134435363,-72.05881118774415", "44.422379116722084,-72.0198440551758", "2018-05-23", "1:37pm");
        GraphPath path = getPathToDestination(options);
        List<Ride> rides = Ride.createRides(path);
        Assert.assertEquals(1, rides.size());
        Ride ride = rides.get(0);
        Assert.assertEquals("7415", ride.getRoute().getId());
        Assert.assertEquals(DEVIATED, ride.getBoardType());
        Assert.assertEquals(DEVIATED, ride.getBoardType());
        checkFare(path);
        // Check that times are respected. DAR is available 1pm-3pm
        // arriveBy=false Check start time respected. If request is for before 1pm, we should get a trip starting at 1pm.
        path = getPathToDestination(buildRequest("44.38485134435363,-72.05881118774415", "44.422379116722084,-72.0198440551758", "2018-05-23", "12:50pm"));
        rides = Ride.createRides(path);
        Assert.assertEquals(1, rides.size());
        ride = rides.get(0);
        Assert.assertEquals("7415", ride.getRoute().getId());
        assertDateEquals(ride.getStartTime(), "2018-05-23", "1:00pm", graph.getTimeZone());
        // arriveBy=false Check end time respected.
        path = getPathToDestination(buildRequest("44.38485134435363,-72.05881118774415", "44.422379116722084,-72.0198440551758", "2018-05-23", "2:56pm"));
        rides = Ride.createRides(path);
        Assert.assertEquals(1, rides.size());
        ride = rides.get(0);
        Assert.assertEquals("7415", ride.getRoute().getId());
        assertDateEquals(ride.getStartTime(), "2018-05-24", "1:00pm", graph.getTimeZone());
        // arriveBy=true Check start time respected.
        path = getPathToDestination(buildRequest("44.38485134435363,-72.05881118774415", "44.422379116722084,-72.0198440551758", "2018-05-24", "1:05pm", true));
        rides = Ride.createRides(path);
        Assert.assertEquals(1, rides.size());
        ride = rides.get(0);
        Assert.assertEquals("7415", ride.getRoute().getId());
        assertDateEquals(ride.getEndTime(), "2018-05-23", "3:00pm", graph.getTimeZone());
        // arriveBy=true Check end time respected
        path = getPathToDestination(buildRequest("44.38485134435363,-72.05881118774415", "44.422379116722084,-72.0198440551758", "2018-05-24", "3:05pm", true));
        rides = Ride.createRides(path);
        Assert.assertEquals(1, rides.size());
        ride = rides.get(0);
        Assert.assertEquals("7415", ride.getRoute().getId());
        assertDateEquals(ride.getEndTime(), "2018-05-24", "3:00pm", graph.getTimeZone());
        options.rctx.destroy();
    }

    // Deviated Fixed Route at both ends
    @Test
    public void testDeviatedFixedRoute() {
        RoutingRequest options = buildRequest("44.950950106914135,-72.20008850097658", "44.94985671536269,-72.13708877563478", "2018-05-23", "4:00pm");
        GraphPath path = getPathToDestination(options);
        List<Ride> rides = Ride.createRides(path);
        Assert.assertEquals(1, rides.size());
        Ride ride = rides.get(0);
        Assert.assertEquals("1383", ride.getRoute().getId());
        Assert.assertEquals(DEVIATED, ride.getBoardType());
        Assert.assertEquals(DEVIATED, ride.getAlightType());
        checkFare(path);
        options.rctx.destroy();
    }

    // Flag stop to a deviated fixed route that starts as a regular route and ends deviated
    @Test
    public void testFlagStopToRegularStopEndingInDeviatedFixedRoute() {
        RoutingRequest options = buildRequest("44.8091683,-72.20580269999999", "44.94985671536269,-72.13708877563478", "2018-06-13", "9:30am");
        GraphPath path = getPathToDestination(options);
        List<Ride> rides = Ride.createRides(path);
        Assert.assertEquals(2, rides.size());
        Ride ride = rides.get(0);
        Assert.assertEquals("3116", ride.getRoute().getId());
        Assert.assertEquals(FLAG_STOP, ride.getBoardType());
        Assert.assertEquals(DEFAULT, ride.getAlightType());
        Ride ride2 = rides.get(1);
        Assert.assertEquals("1383", ride2.getRoute().getId());
        Assert.assertEquals(DEFAULT, ride2.getBoardType());
        Assert.assertEquals(DEVIATED, ride2.getAlightType());
        checkFare(path);
        options.rctx.destroy();
    }

    // Deviated Route on both ends
    @Test
    public void testMultipleThreads() throws InterruptedException, ExecutionException {
        AtomicBoolean thread1SetupComplete = new AtomicBoolean(false);
        AtomicBoolean thread2SearchComplete = new AtomicBoolean(false);
        FutureTask<Boolean> task1 = new FutureTask<>(() -> {
            boolean success = true;
            VermontFlexRoutingTest.LOG.info("Thread 1 starting");
            RoutingRequest options = buildRequest("44.4214596,-72.019371", "44.4277732,-72.01203514", "2018-05-23", "1:37pm");
            GraphPath path = getPathToDestination(options);
            thread1SetupComplete.set(true);
            List<Ride> rides = Ride.createRides(path);
            Assert.assertEquals(1, rides.size());
            Ride ride = rides.get(0);
            success &= "1382".equals(ride.getRoute().getId());
            success &= FLAG_STOP.equals(ride.getBoardType());
            success &= FLAG_STOP.equals(ride.getAlightType());
            while (!(thread2SearchComplete.get()));
            VermontFlexRoutingTest.LOG.info("Thread 1 cleaning up...");
            options.rctx.destroy();
            VermontFlexRoutingTest.LOG.info("Thread 1 complete.");
            return success;
        });
        FutureTask<Boolean> task2 = new FutureTask<>(() -> {
            boolean success = true;
            while (!(thread1SetupComplete.get()));
            VermontFlexRoutingTest.LOG.info("Thread 2 starting");
            RoutingRequest options = buildRequest("44.4214596,-72.019371", "44.4277732,-72.01203514", "2018-05-23", "1:37pm");
            // Don't make temporary edges
            AStar astar = new AStar();
            astar.getShortestPathTree(options);
            GraphPath path = astar.getPathsToTarget().iterator().next();
            List<Ride> rides = Ride.createRides(path);
            // possibly no rides if just walking.
            if (!(rides.isEmpty())) {
                Ride ride = rides.get(0);
                success &= !(FLAG_STOP.equals(ride.getBoardType()));
                success &= !(FLAG_STOP.equals(ride.getAlightType()));
            }
            VermontFlexRoutingTest.LOG.info("Thread 2 cleaning up...");
            options.rctx.destroy();
            VermontFlexRoutingTest.LOG.info("Thread 2 complete");
            thread2SearchComplete.set(true);
            return success;
        });
        new Thread(task1).start();
        new Thread(task2).start();
        Assert.assertTrue(task1.get());
        Assert.assertTrue(task2.get());
    }
}

