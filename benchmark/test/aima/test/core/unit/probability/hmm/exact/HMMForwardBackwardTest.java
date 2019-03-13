package aima.test.core.unit.probability.hmm.exact;


import aima.core.probability.hmm.exact.HMMForwardBackward;
import aima.test.core.unit.probability.temporal.CommonForwardBackwardTest;
import org.junit.Test;


/**
 *
 *
 * @author Ciaran O'Reilly
 */
public class HMMForwardBackwardTest extends CommonForwardBackwardTest {
    // 
    private HMMForwardBackward uw = null;

    @Test
    public void testForwardStep_UmbrellaWorld() {
        super.testForwardStep_UmbrellaWorld(uw);
    }

    @Test
    public void testBackwardStep_UmbrellaWorld() {
        super.testBackwardStep_UmbrellaWorld(uw);
    }

    @Test
    public void testForwardBackward_UmbrellaWorld() {
        super.testForwardBackward_UmbrellaWorld(uw);
    }
}

