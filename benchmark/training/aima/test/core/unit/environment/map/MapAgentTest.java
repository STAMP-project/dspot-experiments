package aima.test.core.unit.environment.map;


import aima.core.environment.map.ExtendableMap;
import aima.core.environment.map.MapEnvironment;
import aima.core.environment.map.MoveToAction;
import aima.core.environment.map.SimpleMapAgent;
import aima.core.search.uninformed.UniformCostSearch;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ciaran O'Reilly
 * @author Ruediger Lunde
 */
public class MapAgentTest {
    private ExtendableMap aMap;

    private StringBuffer envChanges;

    @Test
    public void testAlreadyAtGoal() {
        MapEnvironment me = new MapEnvironment(aMap);
        SimpleMapAgent ma = new SimpleMapAgent(me.getMap(), me, new UniformCostSearch(), new String[]{ "A" });
        me.addAgent(ma, "A");
        me.addEnvironmentView(new MapAgentTest.TestEnvironmentView());
        me.stepUntilDone();
        Assert.assertEquals("CurrentLocation=In(A), Goal=In(A):Action[name=NoOp]:METRIC[pathCost]=0.0:METRIC[maxQueueSize]=1:METRIC[queueSize]=0:METRIC[nodesExpanded]=0:Action[name=NoOp]:", envChanges.toString());
    }

    @Test
    public void testNormalSearch() {
        MapEnvironment me = new MapEnvironment(aMap);
        SimpleMapAgent ma = new SimpleMapAgent(me.getMap(), me, new UniformCostSearch(), new String[]{ "D" });
        me.addAgent(ma, "A");
        me.addEnvironmentView(new MapAgentTest.TestEnvironmentView());
        me.stepUntilDone();
        Assert.assertEquals("CurrentLocation=In(A), Goal=In(D):Action[name=moveTo, location=C]:Action[name=moveTo, location=D]:METRIC[pathCost]=13.0:METRIC[maxQueueSize]=3:METRIC[queueSize]=1:METRIC[nodesExpanded]=3:Action[name=NoOp]:", envChanges.toString());
    }

    @Test
    public void testNormalSearchGraphSearchMinFrontier() {
        MapEnvironment me = new MapEnvironment(aMap);
        UniformCostSearch<String, MoveToAction> ucSearch = new UniformCostSearch(new aima.core.search.framework.qsearch.GraphSearchReducedFrontier());
        SimpleMapAgent ma = new SimpleMapAgent(me.getMap(), me, ucSearch, new String[]{ "D" });
        me.addAgent(ma, "A");
        me.addEnvironmentView(new MapAgentTest.TestEnvironmentView());
        me.stepUntilDone();
        Assert.assertEquals("CurrentLocation=In(A), Goal=In(D):Action[name=moveTo, location=C]:Action[name=moveTo, location=D]:METRIC[pathCost]=13.0:METRIC[maxQueueSize]=2:METRIC[queueSize]=1:METRIC[nodesExpanded]=3:Action[name=NoOp]:", envChanges.toString());
    }

    @Test
    public void testNoPath() {
        MapEnvironment me = new MapEnvironment(aMap);
        SimpleMapAgent ma = new SimpleMapAgent(me.getMap(), me, new UniformCostSearch(), new String[]{ "A" });
        me.addAgent(ma, "E");
        me.addEnvironmentView(new MapAgentTest.TestEnvironmentView());
        me.stepUntilDone();
        Assert.assertEquals("CurrentLocation=In(E), Goal=In(A):Action[name=NoOp]:METRIC[pathCost]=0:METRIC[maxQueueSize]=1:METRIC[queueSize]=0:METRIC[nodesExpanded]=1:Action[name=NoOp]:", envChanges.toString());
    }

    private class TestEnvironmentView implements EnvironmentView {
        public void notify(String msg) {
            envChanges.append(msg).append(":");
        }

        public void agentAdded(Agent agent, Environment source) {
            // Nothing
        }

        public void agentActed(Agent agent, Percept percept, Action action, Environment source) {
            envChanges.append(action).append(":");
        }
    }
}

