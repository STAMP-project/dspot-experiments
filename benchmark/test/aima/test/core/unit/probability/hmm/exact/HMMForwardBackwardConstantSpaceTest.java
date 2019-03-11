package aima.test.core.unit.probability.hmm.exact;


import aima.core.probability.hmm.exact.HMMForwardBackwardConstantSpace;
import aima.test.core.unit.probability.temporal.CommonForwardBackwardTest;
import org.junit.Test;


/**
 *
 *
 * @author Ciaran O'Reilly
 */
public class HMMForwardBackwardConstantSpaceTest extends CommonForwardBackwardTest {
    // 
    private HMMForwardBackwardConstantSpace uw = null;

    @Test
    public void testForwardBackward_UmbrellaWorld() {
        super.testForwardBackward_UmbrellaWorld(uw);
    }
}

