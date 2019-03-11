package aima.test.core.unit.probability.mdp;


import aima.core.environment.cellworld.Cell;
import aima.core.environment.cellworld.CellWorld;
import aima.core.environment.cellworld.CellWorldAction;
import aima.core.probability.mdp.MarkovDecisionProcess;
import aima.core.probability.mdp.search.ValueIteration;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ravi Mohan
 * @author Ciaran O'Reilly
 */
public class ValueIterationTest {
    public static final double DELTA_THRESHOLD = 0.001;

    private CellWorld<Double> cw = null;

    private MarkovDecisionProcess<Cell<Double>, CellWorldAction> mdp = null;

    private ValueIteration<Cell<Double>, CellWorldAction> vi = null;

    @Test
    public void testValueIterationForFig17_3() {
        Map<Cell<Double>, Double> U = vi.valueIteration(mdp, 1.0E-4);
        Assert.assertEquals(0.705, U.get(cw.getCellAt(1, 1)), ValueIterationTest.DELTA_THRESHOLD);
        Assert.assertEquals(0.762, U.get(cw.getCellAt(1, 2)), ValueIterationTest.DELTA_THRESHOLD);
        Assert.assertEquals(0.812, U.get(cw.getCellAt(1, 3)), ValueIterationTest.DELTA_THRESHOLD);
        Assert.assertEquals(0.655, U.get(cw.getCellAt(2, 1)), ValueIterationTest.DELTA_THRESHOLD);
        Assert.assertEquals(0.868, U.get(cw.getCellAt(2, 3)), ValueIterationTest.DELTA_THRESHOLD);
        Assert.assertEquals(0.611, U.get(cw.getCellAt(3, 1)), ValueIterationTest.DELTA_THRESHOLD);
        Assert.assertEquals(0.66, U.get(cw.getCellAt(3, 2)), ValueIterationTest.DELTA_THRESHOLD);
        Assert.assertEquals(0.918, U.get(cw.getCellAt(3, 3)), ValueIterationTest.DELTA_THRESHOLD);
        Assert.assertEquals(0.388, U.get(cw.getCellAt(4, 1)), ValueIterationTest.DELTA_THRESHOLD);
        Assert.assertEquals((-1.0), U.get(cw.getCellAt(4, 2)), ValueIterationTest.DELTA_THRESHOLD);
        Assert.assertEquals(1.0, U.get(cw.getCellAt(4, 3)), ValueIterationTest.DELTA_THRESHOLD);
    }
}

