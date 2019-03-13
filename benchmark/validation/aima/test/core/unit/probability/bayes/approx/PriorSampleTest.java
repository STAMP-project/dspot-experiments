package aima.test.core.unit.probability.bayes.approx;


import ExampleRV.CLOUDY_RV;
import ExampleRV.RAIN_RV;
import ExampleRV.SPRINKLER_RV;
import ExampleRV.WET_GRASS_RV;
import aima.core.probability.RandomVariable;
import aima.core.probability.bayes.BayesianNetwork;
import aima.core.probability.bayes.approx.PriorSample;
import aima.core.probability.example.BayesNetExampleFactory;
import aima.core.util.MockRandomizer;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ciaran O'Reilly
 * @author Ravi Mohan
 */
public class PriorSampleTest {
    @Test
    public void testPriorSample_basic() {
        // AIMA3e pg. 530
        BayesianNetwork bn = BayesNetExampleFactory.constructCloudySprinklerRainWetGrassNetwork();
        MockRandomizer r = new MockRandomizer(new double[]{ 0.5, 0.5, 0.5, 0.5 });
        PriorSample ps = new PriorSample(r);
        Map<RandomVariable, Object> event = ps.priorSample(bn);
        Assert.assertEquals(4, event.keySet().size());
        Assert.assertEquals(Boolean.TRUE, event.get(CLOUDY_RV));
        Assert.assertEquals(Boolean.FALSE, event.get(SPRINKLER_RV));
        Assert.assertEquals(Boolean.TRUE, event.get(RAIN_RV));
        Assert.assertEquals(Boolean.TRUE, event.get(WET_GRASS_RV));
    }
}

