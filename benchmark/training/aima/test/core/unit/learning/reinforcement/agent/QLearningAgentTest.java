package aima.test.core.unit.learning.reinforcement.agent;


import aima.core.environment.cellworld.Cell;
import aima.core.environment.cellworld.CellWorld;
import aima.core.environment.cellworld.CellWorldAction;
import aima.core.learning.reinforcement.agent.QLearningAgent;
import aima.core.learning.reinforcement.example.CellWorldEnvironment;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class QLearningAgentTest extends ReinforcementLearningAgentTest {
    // 
    private CellWorld<Double> cw = null;

    private CellWorldEnvironment cwe = null;

    private QLearningAgent<Cell<Double>, CellWorldAction> qla = null;

    @Test
    public void test_Q_learning() {
        qla.reset();
        cwe.executeTrials(100000);
        Map<Cell<Double>, Double> U = qla.getUtility();
        Assert.assertNotNull(U.get(cw.getCellAt(1, 1)));
        // Note:
        // As the Q-Learning Agent is not using a fixed
        // policy it should with a reasonable number
        // of iterations observe and calculate an
        // approximate utility for all of the states.
        Assert.assertEquals(11, U.size());
        // Note: Due to stochastic nature of environment,
        // will not test the individual utilities calculated
        // as this will take a fair amount of time.
        // Instead we will check if the RMS error in utility
        // for 1,1 is below a reasonable threshold.
        ReinforcementLearningAgentTest.test_RMSeiu_for_1_1(qla, 20, 10000, 0.2);
    }
}

