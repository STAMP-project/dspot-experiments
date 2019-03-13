package aima.test.core.unit.probability.mdp;


import CellWorldAction.Left;
import CellWorldAction.Right;
import CellWorldAction.Up;
import aima.core.environment.cellworld.Cell;
import aima.core.environment.cellworld.CellWorld;
import aima.core.environment.cellworld.CellWorldAction;
import aima.core.probability.mdp.MarkovDecisionProcess;
import aima.core.probability.mdp.Policy;
import aima.core.probability.mdp.search.PolicyIteration;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ravi Mohan
 * @author Ciaran O'Reilly
 */
public class PolicyIterationTest {
    private CellWorld<Double> cw = null;

    private MarkovDecisionProcess<Cell<Double>, CellWorldAction> mdp = null;

    private PolicyIteration<Cell<Double>, CellWorldAction> pi = null;

    @Test
    public void testPolicyIterationForFig17_2() {
        // AIMA3e check with Figure 17.2 (a)
        Policy<Cell<Double>, CellWorldAction> policy = pi.policyIteration(mdp);
        Assert.assertEquals(Up, policy.action(cw.getCellAt(1, 1)));
        Assert.assertEquals(Up, policy.action(cw.getCellAt(1, 2)));
        Assert.assertEquals(Right, policy.action(cw.getCellAt(1, 3)));
        Assert.assertEquals(Left, policy.action(cw.getCellAt(2, 1)));
        Assert.assertEquals(Right, policy.action(cw.getCellAt(2, 3)));
        Assert.assertEquals(Left, policy.action(cw.getCellAt(3, 1)));
        Assert.assertEquals(Up, policy.action(cw.getCellAt(3, 2)));
        Assert.assertEquals(Right, policy.action(cw.getCellAt(3, 3)));
        Assert.assertEquals(Left, policy.action(cw.getCellAt(4, 1)));
        Assert.assertNull(policy.action(cw.getCellAt(4, 2)));
        Assert.assertNull(policy.action(cw.getCellAt(4, 3)));
    }
}

