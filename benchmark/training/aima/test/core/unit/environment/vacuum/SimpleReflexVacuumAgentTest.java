package aima.test.core.unit.environment.vacuum;


import VacuumEnvironment.LOCATION_A;
import VacuumEnvironment.LocationState;
import aima.core.agent.impl.SimpleActionTracker;
import aima.core.environment.vacuum.SimpleReflexVacuumAgent;
import aima.core.environment.vacuum.VacuumEnvironment;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ciaran O'Reilly
 * @author Ruediger Lunde
 */
public class SimpleReflexVacuumAgentTest {
    private SimpleReflexVacuumAgent agent;

    private SimpleActionTracker actionTracker;

    @Test
    public void testCleanClean() {
        VacuumEnvironment tve = new VacuumEnvironment(LocationState.Clean, LocationState.Clean);
        tve.addAgent(agent, LOCATION_A);
        tve.addEnvironmentView(actionTracker);
        tve.step(8);
        Assert.assertEquals("Action[name=Right], Action[name=Left], Action[name=Right], Action[name=Left], Action[name=Right], Action[name=Left], Action[name=Right], Action[name=Left]", actionTracker.getActions());
    }

    @Test
    public void testCleanDirty() {
        VacuumEnvironment tve = new VacuumEnvironment(LocationState.Clean, LocationState.Dirty);
        tve.addAgent(agent, LOCATION_A);
        tve.addEnvironmentView(actionTracker);
        tve.step(8);
        Assert.assertEquals("Action[name=Right], Action[name=Suck], Action[name=Left], Action[name=Right], Action[name=Left], Action[name=Right], Action[name=Left], Action[name=Right]", actionTracker.getActions());
    }

    @Test
    public void testDirtyClean() {
        VacuumEnvironment tve = new VacuumEnvironment(LocationState.Dirty, LocationState.Clean);
        tve.addAgent(agent, LOCATION_A);
        tve.addEnvironmentView(actionTracker);
        tve.step(8);
        Assert.assertEquals("Action[name=Suck], Action[name=Right], Action[name=Left], Action[name=Right], Action[name=Left], Action[name=Right], Action[name=Left], Action[name=Right]", actionTracker.getActions());
    }

    @Test
    public void testDirtyDirty() {
        VacuumEnvironment tve = new VacuumEnvironment(LocationState.Dirty, LocationState.Dirty);
        tve.addAgent(agent, LOCATION_A);
        tve.addEnvironmentView(actionTracker);
        tve.step(8);
        Assert.assertEquals("Action[name=Suck], Action[name=Right], Action[name=Suck], Action[name=Left], Action[name=Right], Action[name=Left], Action[name=Right], Action[name=Left]", actionTracker.getActions());
    }
}

