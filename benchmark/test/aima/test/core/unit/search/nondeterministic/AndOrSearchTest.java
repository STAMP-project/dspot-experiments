package aima.test.core.unit.search.nondeterministic;


import LocationState.Clean;
import LocationState.Dirty;
import aima.core.agent.Action;
import aima.core.search.agent.NondeterministicSearchAgent;
import aima.core.search.nondeterministic.Path;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the AND-OR search algorithm using the erratic vacuum world of page 133,
 * AIMA3e. In essence, a two-square office is cleaned by a vacuum that randomly
 * (1) cleans the square, (2) cleans both squares, or (3) dirties the square it
 * meant to clean.
 *
 * @author Andrew Brown
 * @author Ruediger Lunde
 */
public class AndOrSearchTest {
    private NondeterministicSearchAgent<VacuumEnvironmentState, Action> agent;

    private NondeterministicVacuumEnvironment world;

    /**
     * Test whether two identically-initialized states will equals() each other.
     */
    @Test
    public void testStateEquality() {
        // create state 1
        VacuumEnvironmentState s1 = new VacuumEnvironmentState();
        s1.setLocationState(LOCATION_A, Dirty);
        s1.setLocationState(LOCATION_B, Dirty);
        s1.setAgentLocation(agent, LOCATION_A);
        // create state 2
        VacuumEnvironmentState s2 = new VacuumEnvironmentState();
        s2.setLocationState(LOCATION_A, Dirty);
        s2.setLocationState(LOCATION_B, Dirty);
        s2.setAgentLocation(agent, LOCATION_A);
        // test
        boolean actual = s1.equals(s2);
        Assert.assertEquals(true, actual);
    }

    /**
     * Test whether a Path contains() a state; uses state enumeration from page
     * 134, AIMA3e.
     */
    @Test
    public void testPathContains() {
        // create state 1
        VacuumEnvironmentState s1 = new VacuumEnvironmentState();
        s1.setLocationState(LOCATION_A, Dirty);
        s1.setLocationState(LOCATION_B, Dirty);
        s1.setAgentLocation(agent, LOCATION_A);
        // create state 2
        VacuumEnvironmentState s2 = new VacuumEnvironmentState();
        s2.setLocationState(LOCATION_A, Dirty);
        s2.setLocationState(LOCATION_B, Dirty);
        s2.setAgentLocation(agent, LOCATION_B);
        // create state 3
        VacuumEnvironmentState s3 = new VacuumEnvironmentState();
        s3.setLocationState(LOCATION_A, Dirty);
        s3.setLocationState(LOCATION_B, Clean);
        s3.setAgentLocation(agent, LOCATION_A);
        // create state 4
        VacuumEnvironmentState s4 = new VacuumEnvironmentState();
        s4.setLocationState(LOCATION_A, Dirty);
        s4.setLocationState(LOCATION_B, Clean);
        s4.setAgentLocation(agent, LOCATION_B);
        // create test state 1
        VacuumEnvironmentState test1 = new VacuumEnvironmentState();
        test1.setLocationState(LOCATION_A, Dirty);
        test1.setLocationState(LOCATION_B, Clean);
        test1.setAgentLocation(agent, LOCATION_A);
        // create test state 2
        VacuumEnvironmentState test2 = new VacuumEnvironmentState();
        test2.setLocationState(LOCATION_A, Clean);
        test2.setLocationState(LOCATION_B, Clean);
        test2.setAgentLocation(agent, LOCATION_B);
        // add to path
        Path<VacuumEnvironmentState> path = new Path();
        path.addAll(Arrays.asList(s1, s2, s3, s4));
        // test
        Assert.assertEquals(true, path.contains(test1));
        Assert.assertEquals(false, path.contains(test2));
    }

    /**
     * Use AND-OR search to create a contingency plan; execute the plan and
     * verify that it successfully cleans the NondeterministicVacuumWorld.
     */
    @Test
    public void testSearchExecutesSuccessfully() {
        // execute plan
        world.stepUntilDone();
        // test
        VacuumEnvironmentState endState = ((VacuumEnvironmentState) (world.getCurrentState()));
        LocationState a = endState.getLocationState(LOCATION_A);
        LocationState b = endState.getLocationState(LOCATION_B);
        Assert.assertEquals(Clean, a);
        Assert.assertEquals(Clean, b);
    }
}

