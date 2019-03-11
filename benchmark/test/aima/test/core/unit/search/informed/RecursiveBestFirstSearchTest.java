package aima.test.core.unit.search.informed;


import SimplifiedRoadMapOfPartOfRomania.ARAD;
import SimplifiedRoadMapOfPartOfRomania.BUCHAREST;
import aima.core.search.informed.RecursiveBestFirstSearch;
import org.junit.Assert;
import org.junit.Test;

import static SimplifiedRoadMapOfPartOfRomania.BUCHAREST;
import static SimplifiedRoadMapOfPartOfRomania.NEAMT;


/**
 *
 *
 * @author Ciaran O'Reilly
 * @author Ruediger Lunde
 */
public class RecursiveBestFirstSearchTest {
    private StringBuffer envChanges;

    private Map aMap;

    private RecursiveBestFirstSearch<String, MoveToAction> recursiveBestFirstSearch;

    private RecursiveBestFirstSearch<String, MoveToAction> recursiveBestFirstSearchAvoidingLoops;

    @Test
    public void testStartingAtGoal() {
        MapEnvironment me = new MapEnvironment(aMap);
        SimpleMapAgent ma = new SimpleMapAgent(me.getMap(), me, recursiveBestFirstSearch, new String[]{ BUCHAREST });
        me.addAgent(ma, BUCHAREST);
        me.addEnvironmentView(new RecursiveBestFirstSearchTest.TestEnvironmentView());
        me.stepUntilDone();
        Assert.assertEquals("CurrentLocation=In(Bucharest), Goal=In(Bucharest):Action[name=NoOp]:METRIC[pathCost]=0.0:METRIC[maxRecursiveDepth]=0:METRIC[nodesExpanded]=0:Action[name=NoOp]:", envChanges.toString());
    }

    @Test
    public void testAIMA3eFigure3_27() {
        MapEnvironment me = new MapEnvironment(aMap);
        SimpleMapAgent ma = new SimpleMapAgent(me.getMap(), me, recursiveBestFirstSearch, new String[]{ BUCHAREST });
        me.addAgent(ma, ARAD);
        me.addEnvironmentView(new RecursiveBestFirstSearchTest.TestEnvironmentView());
        me.stepUntilDone();
        Assert.assertEquals("CurrentLocation=In(Arad), Goal=In(Bucharest):Action[name=moveTo, location=Sibiu]:Action[name=moveTo, location=RimnicuVilcea]:Action[name=moveTo, location=Pitesti]:Action[name=moveTo, location=Bucharest]:METRIC[pathCost]=418.0:METRIC[maxRecursiveDepth]=4:METRIC[nodesExpanded]=6:Action[name=NoOp]:", envChanges.toString());
    }

    @Test
    public void testAIMA3eAradNeamtA() {
        MapEnvironment me = new MapEnvironment(aMap);
        SimpleMapAgent ma = new SimpleMapAgent(me.getMap(), me, recursiveBestFirstSearch, new String[]{ NEAMT });
        me.addAgent(ma, ARAD);
        me.addEnvironmentView(new RecursiveBestFirstSearchTest.TestEnvironmentView());
        me.stepUntilDone();
        Assert.assertEquals("CurrentLocation=In(Arad), Goal=In(Neamt):Action[name=moveTo, location=Sibiu]:Action[name=moveTo, location=RimnicuVilcea]:Action[name=moveTo, location=Pitesti]:Action[name=moveTo, location=Bucharest]:Action[name=moveTo, location=Urziceni]:Action[name=moveTo, location=Vaslui]:Action[name=moveTo, location=Iasi]:Action[name=moveTo, location=Neamt]:METRIC[pathCost]=824.0:METRIC[maxRecursiveDepth]=12:METRIC[nodesExpanded]=340208:Action[name=NoOp]:", envChanges.toString());
    }

    @Test
    public void testAIMA3eAradNeamtB() {
        MapEnvironment me = new MapEnvironment(aMap);
        SimpleMapAgent ma = new SimpleMapAgent(me.getMap(), me, recursiveBestFirstSearchAvoidingLoops, new String[]{ NEAMT });
        me.addAgent(ma, ARAD);
        me.addEnvironmentView(new RecursiveBestFirstSearchTest.TestEnvironmentView());
        me.stepUntilDone();
        // loops avoided, now much less number of expanded nodes ...
        Assert.assertEquals("CurrentLocation=In(Arad), Goal=In(Neamt):Action[name=moveTo, location=Sibiu]:Action[name=moveTo, location=RimnicuVilcea]:Action[name=moveTo, location=Pitesti]:Action[name=moveTo, location=Bucharest]:Action[name=moveTo, location=Urziceni]:Action[name=moveTo, location=Vaslui]:Action[name=moveTo, location=Iasi]:Action[name=moveTo, location=Neamt]:METRIC[pathCost]=824.0:METRIC[maxRecursiveDepth]=9:METRIC[nodesExpanded]=1367:Action[name=NoOp]:", envChanges.toString());
    }

    private class TestEnvironmentView implements EnvironmentView {
        public void notify(String msg) {
            envChanges.append(msg).append(":");
        }

        public void agentAdded(Agent agent, Environment source) {
            // Nothing.
        }

        public void agentActed(Agent agent, Percept percept, Action action, Environment source) {
            envChanges.append(action).append(":");
        }
    }
}

