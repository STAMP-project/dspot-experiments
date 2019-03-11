package aima.test.core.unit.environment.map;


import aima.core.environment.map.MoveToAction;
import aima.core.search.framework.problem.ActionsFunction;
import aima.core.search.framework.problem.ResultFunction;
import aima.core.search.framework.problem.StepCostFunction;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ciaran O'Reilly
 * @author Ruediger Lunde
 */
public class MapFunctionsTest {
    private ActionsFunction<String, MoveToAction> actionsFn;

    private ResultFunction<String, MoveToAction> resultFn;

    private StepCostFunction<String, MoveToAction> stepCostFn;

    @Test
    public void testSuccessors() {
        ArrayList<String> locations = new ArrayList<>();
        // A
        locations.clear();
        locations.add("B");
        locations.add("C");
        for (MoveToAction a : actionsFn.apply("A")) {
            Assert.assertTrue(locations.contains(a.getToLocation()));
            Assert.assertTrue(locations.contains(resultFn.apply("A", a)));
        }
        // B
        locations.clear();
        locations.add("A");
        locations.add("C");
        locations.add("E");
        for (MoveToAction a : actionsFn.apply("B")) {
            Assert.assertTrue(locations.contains(a.getToLocation()));
            Assert.assertTrue(locations.contains(resultFn.apply("B", a)));
        }
        // C
        locations.clear();
        locations.add("A");
        locations.add("B");
        locations.add("D");
        for (MoveToAction a : actionsFn.apply("C")) {
            Assert.assertTrue(locations.contains(a.getToLocation()));
            Assert.assertTrue(locations.contains(resultFn.apply("C", a)));
        }
        // D
        locations.clear();
        locations.add("C");
        for (MoveToAction a : actionsFn.apply("D")) {
            Assert.assertTrue(locations.contains(a.getToLocation()));
            Assert.assertTrue(locations.contains(resultFn.apply("D", a)));
        }
        // E
        locations.clear();
        Assert.assertTrue((0 == (actionsFn.apply("E").size())));
    }

    @Test
    public void testCosts() {
        Assert.assertEquals(5.0, stepCostFn.applyAsDouble("A", new MoveToAction("B"), "B"), 0.001);
        Assert.assertEquals(6.0, stepCostFn.applyAsDouble("A", new MoveToAction("C"), "C"), 0.001);
        Assert.assertEquals(4.0, stepCostFn.applyAsDouble("B", new MoveToAction("C"), "C"), 0.001);
        Assert.assertEquals(7.0, stepCostFn.applyAsDouble("C", new MoveToAction("D"), "D"), 0.001);
        Assert.assertEquals(14.0, stepCostFn.applyAsDouble("B", new MoveToAction("E"), "E"), 0.001);
        // 
        Assert.assertEquals(5.0, stepCostFn.applyAsDouble("B", new MoveToAction("A"), "A"), 0.001);
        Assert.assertEquals(6.0, stepCostFn.applyAsDouble("C", new MoveToAction("A"), "A"), 0.001);
        Assert.assertEquals(4.0, stepCostFn.applyAsDouble("C", new MoveToAction("B"), "B"), 0.001);
        Assert.assertEquals(7.0, stepCostFn.applyAsDouble("D", new MoveToAction("C"), "C"), 0.001);
        // 
        Assert.assertEquals(1.0, stepCostFn.applyAsDouble("X", new MoveToAction("Z"), "Z"), 0.001);
        Assert.assertEquals(1.0, stepCostFn.applyAsDouble("A", new MoveToAction("Z"), "Z"), 0.001);
        Assert.assertEquals(1.0, stepCostFn.applyAsDouble("A", new MoveToAction("D"), "D"), 0.001);
        Assert.assertEquals(1.0, stepCostFn.applyAsDouble("A", new MoveToAction("B"), "E"), 0.001);
    }
}

