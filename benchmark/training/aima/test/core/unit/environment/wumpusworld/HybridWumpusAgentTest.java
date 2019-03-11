package aima.test.core.unit.environment.wumpusworld;


import AgentPosition.Orientation;
import WumpusAction.CLIMB;
import WumpusAction.FORWARD;
import WumpusAction.GRAB;
import WumpusAction.SHOOT;
import WumpusAction.TURN_LEFT;
import aima.core.agent.Action;
import aima.core.agent.impl.SimpleActionTracker;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ciaran O'Reilly
 * @author Ruediger Lunde
 */
public class HybridWumpusAgentTest {
    @SuppressWarnings("serial")
    @Test
    public void testPlanRoute() {
        HybridWumpusAgent hwa;
        hwa = new HybridWumpusAgent(4, 4, new AgentPosition(1, 1, Orientation.FACING_EAST));
        // Should be a NoOp plan as we are already at the goal.
        Assert.assertEquals(Collections.emptyList(), hwa.planRouteToRooms(new LinkedHashSet<Room>() {
            {
                add(new Room(1, 1));
            }
        }, HybridWumpusAgentTest.allRooms(4)));
        hwa = new HybridWumpusAgent(4, 4, new AgentPosition(2, 1, Orientation.FACING_EAST));
        Assert.assertEquals(Arrays.asList(TURN_LEFT, TURN_LEFT, FORWARD), hwa.planRouteToRooms(new LinkedHashSet<Room>() {
            {
                add(new Room(1, 1));
            }
        }, HybridWumpusAgentTest.allRooms(4)));
        hwa = new HybridWumpusAgent(4, 4, new AgentPosition(3, 1, Orientation.FACING_EAST));
        Assert.assertEquals(Arrays.asList(TURN_LEFT, FORWARD, FORWARD, TURN_LEFT, FORWARD, FORWARD, TURN_LEFT, FORWARD, FORWARD), hwa.planRouteToRooms(new LinkedHashSet<Room>() {
            {
                add(new Room(1, 1));
            }
        }, new LinkedHashSet<Room>() {
            {
                addAll(HybridWumpusAgentTest.allRooms(4));
                remove(new Room(2, 1));
                remove(new Room(2, 2));
            }
        }));
    }

    @SuppressWarnings("serial")
    @Test
    public void testPlanShot() {
        HybridWumpusAgent hwa;
        hwa = new HybridWumpusAgent(4, 4, new AgentPosition(1, 1, Orientation.FACING_EAST));
        // Should be just shoot as are facing the Wumpus
        Assert.assertEquals(Collections.singletonList(SHOOT), hwa.planShot(new LinkedHashSet<Room>() {
            {
                add(new Room(3, 1));
            }
        }, HybridWumpusAgentTest.allRooms(4)));
        hwa = new HybridWumpusAgent(4, 4, new AgentPosition(1, 1, Orientation.FACING_EAST));
        Assert.assertEquals(Arrays.asList(TURN_LEFT, SHOOT), hwa.planShot(new LinkedHashSet<Room>() {
            {
                add(new Room(1, 2));
            }
        }, HybridWumpusAgentTest.allRooms(4)));
        hwa = new HybridWumpusAgent(4, 4, new AgentPosition(1, 1, Orientation.FACING_EAST));
        Assert.assertEquals(Arrays.asList(FORWARD, TURN_LEFT, SHOOT), hwa.planShot(new LinkedHashSet<Room>() {
            {
                add(new Room(2, 2));
            }
        }, HybridWumpusAgentTest.allRooms(4)));
    }

    @Test
    public void testGrabAndClimb() {
        HybridWumpusAgent hwa = new HybridWumpusAgent(2, 2, new AgentPosition(1, 1, Orientation.FACING_NORTH));
        // The gold is in the first square
        Action a = hwa.execute(new WumpusPercept().setStench().setBreeze().setGlitter());
        Assert.assertEquals(a, GRAB);
        a = hwa.execute(new WumpusPercept().setStench().setBreeze().setGlitter());
        Assert.assertEquals(a, CLIMB);
    }

    @Test
    public void testSimulation2x2Cave() {
        WumpusCave cave = new WumpusCave(2, 2, ("" + ("W . " + "S G ")));
        WumpusEnvironment env = new WumpusEnvironment(cave);
        SimpleActionTracker view = new SimpleActionTracker();
        env.addEnvironmentView(view);
        HybridWumpusAgent a = new HybridWumpusAgent(cave.getCaveXDimension(), cave.getCaveYDimension(), cave.getStart());
        env.addAgent(a);
        env.stepUntilDone();
        Assert.assertEquals(view.getActions(), "SHOOT, FORWARD, TURN_RIGHT, FORWARD, TURN_RIGHT, FORWARD, GRAB, TURN_RIGHT, FORWARD, CLIMB");
    }
}

