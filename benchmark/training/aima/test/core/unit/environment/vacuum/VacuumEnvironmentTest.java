package aima.test.core.unit.environment.vacuum;


import VacuumEnvironment.LOCATION_A;
import VacuumEnvironment.LOCATION_B;
import VacuumEnvironment.LocationState.Clean;
import VacuumEnvironment.LocationState.Dirty;
import aima.core.environment.vacuum.ModelBasedReflexVacuumAgent;
import aima.core.environment.vacuum.VacuumEnvironment;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ravi Mohan
 */
public class VacuumEnvironmentTest {
    VacuumEnvironment tve;

    VacuumEnvironment tve2;

    VacuumEnvironment tve3;

    VacuumEnvironment tve4;

    ModelBasedReflexVacuumAgent a;

    @Test
    public void testTVEConstruction() {
        Assert.assertEquals(Dirty, tve.getLocationState(LOCATION_A));
        Assert.assertEquals(Dirty, tve.getLocationState(LOCATION_B));
        Assert.assertEquals(Clean, tve2.getLocationState(LOCATION_A));
        Assert.assertEquals(Clean, tve2.getLocationState(LOCATION_B));
    }

    @Test
    public void testAgentAdd() {
        tve.addAgent(a, LOCATION_A);
        Assert.assertEquals(LOCATION_A, tve.getAgentLocation(a));
        Assert.assertEquals(1, tve.getAgents().size());
    }
}

