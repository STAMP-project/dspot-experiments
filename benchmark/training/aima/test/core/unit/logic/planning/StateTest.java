package aima.test.core.unit.logic.planning;


import aima.core.logic.fol.kb.data.Literal;
import aima.core.logic.planning.ActionSchema;
import aima.core.logic.planning.State;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author samagra
 */
public class StateTest {
    private Literal testFluentOne;

    private Literal testFluentTwo;

    private Literal testFluentThree;

    private Literal testFluentFour;

    private State testState;

    private ActionSchema flyActionOne;

    private ActionSchema flyActionTwo;

    @Test
    public void constructorTest() {
        Assert.assertTrue(testState.getFluents().contains(testFluentOne));
        Assert.assertTrue(testState.getFluents().contains(testFluentTwo));
        Assert.assertTrue(testState.getFluents().contains(testFluentThree));
        Assert.assertFalse(testState.getFluents().contains(testFluentFour));
    }

    @Test
    public void isApplicableTest() {
        Assert.assertTrue(testState.isApplicable(flyActionOne));
        Assert.assertFalse(testState.isApplicable(flyActionTwo));
    }

    @Test
    public void resultTest() {
        State initState = new State(("At(C1,SFO)^At(C2,JFK)^At(P1,SFO)^At(P2,JFK)" + "^Cargo(C1)^Cargo(C2)^Plane(P1)^Plane(P2)^Airport(JFK)^Airport(SFO)"));
        State finalState = new State(("At(C1,SFO)^At(C2,JFK)^At(P1,JFK)^At(P2,JFK)" + "^Cargo(C1)^Cargo(C2)^Plane(P1)^Plane(P2)^Airport(JFK)^Airport(SFO)"));
        State newState = testState.result(flyActionTwo);
        Assert.assertNotEquals(finalState, newState);
        Assert.assertEquals(initState, newState);
        newState = testState.result(flyActionOne);
        Assert.assertEquals(finalState, newState);
        Assert.assertNotEquals(initState, newState);
        newState = testState.result(flyActionTwo);
        Assert.assertEquals(initState, newState);
    }
}

