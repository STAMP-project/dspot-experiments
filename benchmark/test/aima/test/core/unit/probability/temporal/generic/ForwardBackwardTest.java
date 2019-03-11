package aima.test.core.unit.probability.temporal.generic;


import aima.core.probability.temporal.generic.ForwardBackward;
import aima.test.core.unit.probability.temporal.CommonForwardBackwardTest;
import org.junit.Test;


/**
 *
 *
 * @author Ciaran O'Reilly
 */
public class ForwardBackwardTest extends CommonForwardBackwardTest {
    // 
    private ForwardBackward uw = null;

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

