package aima.test.core.unit.search.online;


import aima.core.environment.map.ExtendableMap;
import aima.core.environment.map.MapEnvironment;
import aima.core.environment.map.MapFunctions;
import aima.core.environment.map.MoveToAction;
import aima.core.search.framework.problem.GoalTest;
import aima.core.search.framework.problem.OnlineSearchProblem;
import aima.core.search.online.LRTAStarAgent;
import java.util.function.ToDoubleFunction;
import org.junit.Assert;
import org.junit.Test;


public class LRTAStarAgentTest {
    private ExtendableMap aMap;

    private StringBuffer envChanges;

    private ToDoubleFunction<String> h;

    @Test
    public void testAlreadyAtGoal() {
        MapEnvironment me = new MapEnvironment(aMap);
        OnlineSearchProblem<String, MoveToAction> problem = new aima.core.search.framework.problem.GeneralProblem(null, MapFunctions.createActionsFunction(aMap), null, GoalTest.forState("A"), MapFunctions.createDistanceStepCostFunction(aMap));
        LRTAStarAgent<String, MoveToAction> agent = new LRTAStarAgent(problem, MapFunctions.createPerceptToStateFunction(), h);
        me.addAgent(agent, "A");
        me.addEnvironmentView(new LRTAStarAgentTest.TestEnvironmentView());
        me.stepUntilDone();
        Assert.assertEquals("Action[name=NoOp]->", envChanges.toString());
    }

    @Test
    public void testNormalSearch() {
        MapEnvironment me = new MapEnvironment(aMap);
        OnlineSearchProblem<String, MoveToAction> problem = new aima.core.search.framework.problem.GeneralProblem(null, MapFunctions.createActionsFunction(aMap), null, GoalTest.forState("F"), MapFunctions.createDistanceStepCostFunction(aMap));
        LRTAStarAgent<String, MoveToAction> agent = new LRTAStarAgent(problem, MapFunctions.createPerceptToStateFunction(), h);
        me.addAgent(agent, "A");
        me.addEnvironmentView(new LRTAStarAgentTest.TestEnvironmentView());
        me.stepUntilDone();
        Assert.assertEquals("Action[name=moveTo, location=B]->Action[name=moveTo, location=A]->Action[name=moveTo, location=B]->Action[name=moveTo, location=C]->Action[name=moveTo, location=B]->Action[name=moveTo, location=C]->Action[name=moveTo, location=D]->Action[name=moveTo, location=C]->Action[name=moveTo, location=D]->Action[name=moveTo, location=E]->Action[name=moveTo, location=D]->Action[name=moveTo, location=E]->Action[name=moveTo, location=F]->Action[name=NoOp]->", envChanges.toString());
    }

    @Test
    public void testNoPath() {
        MapEnvironment me = new MapEnvironment(aMap);
        OnlineSearchProblem<String, MoveToAction> problem = new aima.core.search.framework.problem.GeneralProblem(null, MapFunctions.createActionsFunction(aMap), null, GoalTest.forState("G"), MapFunctions.createDistanceStepCostFunction(aMap));
        LRTAStarAgent<String, MoveToAction> agent = new LRTAStarAgent(problem, MapFunctions.createPerceptToStateFunction(), h);
        me.addAgent(agent, "A");
        me.addEnvironmentView(new LRTAStarAgentTest.TestEnvironmentView());
        // Note: Will search forever if no path is possible,
        // Therefore restrict the number of steps to something
        // reasonablbe, against which to test.
        me.step(14);
        Assert.assertEquals("Action[name=moveTo, location=B]->Action[name=moveTo, location=A]->Action[name=moveTo, location=B]->Action[name=moveTo, location=C]->Action[name=moveTo, location=B]->Action[name=moveTo, location=C]->Action[name=moveTo, location=D]->Action[name=moveTo, location=C]->Action[name=moveTo, location=D]->Action[name=moveTo, location=E]->Action[name=moveTo, location=D]->Action[name=moveTo, location=E]->Action[name=moveTo, location=F]->Action[name=moveTo, location=E]->", envChanges.toString());
    }

    private class TestEnvironmentView implements EnvironmentView {
        public void notify(String msg) {
            envChanges.append(msg).append("->");
        }

        public void agentAdded(Agent agent, Environment source) {
            // Nothing.
        }

        public void agentActed(Agent agent, Percept percept, Action action, Environment source) {
            envChanges.append(action).append("->");
        }
    }
}

