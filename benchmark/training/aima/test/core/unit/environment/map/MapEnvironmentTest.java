package aima.test.core.unit.environment.map;


import DynAttributeNames.PERCEPT_IN;
import aima.core.agent.impl.DynamicPercept;
import aima.core.environment.map.MapEnvironment;
import aima.core.environment.map.MoveToAction;
import aima.core.environment.map.SimpleMapAgent;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ciaran O'Reilly
 */
public class MapEnvironmentTest {
    MapEnvironment me;

    SimpleMapAgent ma;

    @Test
    public void testAddAgent() {
        me.addAgent(ma, "E");
        Assert.assertEquals(me.getAgentLocation(ma), "E");
    }

    @Test
    public void testExecuteAction() {
        me.addAgent(ma, "D");
        me.executeAction(ma, new MoveToAction("C"));
        Assert.assertEquals(me.getAgentLocation(ma), "C");
    }

    @Test
    public void testPerceptSeenBy() {
        me.addAgent(ma, "D");
        DynamicPercept p = ((DynamicPercept) (me.getPerceptSeenBy(ma)));
        Assert.assertEquals(p.getAttribute(PERCEPT_IN), "D");
    }

    @Test
    public void testTwoAgentsSupported() {
        SimpleMapAgent ma1 = new SimpleMapAgent(me.getMap(), me, new aima.core.search.uninformed.UniformCostSearch(), new String[]{ "A" });
        SimpleMapAgent ma2 = new SimpleMapAgent(me.getMap(), me, new aima.core.search.uninformed.UniformCostSearch(), new String[]{ "A" });
        me.addAgent(ma1, "A");
        me.addAgent(ma2, "A");
        me.executeAction(ma1, new MoveToAction("B"));
        me.executeAction(ma2, new MoveToAction("C"));
        Assert.assertEquals(me.getAgentLocation(ma1), "B");
        Assert.assertEquals(me.getAgentLocation(ma2), "C");
    }
}

