package aima.test.core.unit.learning.reinforcement.agent;


import aima.core.environment.cellworld.Cell;
import aima.core.environment.cellworld.CellWorld;
import aima.core.environment.cellworld.CellWorldAction;
import aima.core.learning.reinforcement.agent.PassiveTDAgent;
import aima.core.learning.reinforcement.example.CellWorldEnvironment;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class PassiveTDAgentTest extends ReinforcementLearningAgentTest {
    // 
    private CellWorld<Double> cw = null;

    private CellWorldEnvironment cwe = null;

    private PassiveTDAgent<Cell<Double>, CellWorldAction> ptda = null;

    @Test
    public void test_TD_learning_fig21_1() {
        ptda.reset();
        cwe.executeTrials(10000);
        Map<Cell<Double>, Double> U = ptda.getUtility();
        Assert.assertNotNull(U.get(cw.getCellAt(1, 1)));
        // Note:
        // These are not reachable when starting at 1,1 using
        // the policy and default transition model
        // (i.e. 80% intended, 10% each right angle from intended).
        Assert.assertNull(U.get(cw.getCellAt(3, 1)));
        Assert.assertNull(U.get(cw.getCellAt(4, 1)));
        Assert.assertEquals(9, U.size());
        // Note: Due to stochastic nature of environment,
        // will not test the individual utilities calculated
        // as this will take a fair amount of time.
        // Instead we will check if the RMS error in utility
        // for 1,1 is below a reasonable threshold.
        ReinforcementLearningAgentTest.test_RMSeiu_for_1_1(ptda, 20, 1000, 0.07);
    }
}

