package aima.test.core.unit.agent.impl.aprog;


import NoOpAction.NO_OP;
import aima.core.agent.Action;
import aima.core.agent.impl.AbstractAgent;
import aima.core.agent.impl.DynamicAction;
import aima.core.agent.impl.DynamicPercept;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ciaran O'Reilly
 */
public class TableDrivenAgentProgramTest {
    private static final Action ACTION_1 = new DynamicAction("action1");

    private static final Action ACTION_2 = new DynamicAction("action2");

    private static final Action ACTION_3 = new DynamicAction("action3");

    private AbstractAgent agent;

    @Test
    public void testExistingSequences() {
        Assert.assertEquals(TableDrivenAgentProgramTest.ACTION_1, agent.execute(new DynamicPercept("key1", "value1")));
        Assert.assertEquals(TableDrivenAgentProgramTest.ACTION_2, agent.execute(new DynamicPercept("key1", "value2")));
        Assert.assertEquals(TableDrivenAgentProgramTest.ACTION_3, agent.execute(new DynamicPercept("key1", "value3")));
    }

    @Test
    public void testNonExistingSequence() {
        Assert.assertEquals(TableDrivenAgentProgramTest.ACTION_1, agent.execute(new DynamicPercept("key1", "value1")));
        Assert.assertEquals(NO_OP, agent.execute(new DynamicPercept("key1", "value3")));
    }
}

