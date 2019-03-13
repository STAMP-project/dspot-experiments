package aima.test.core.unit.search.framework;


import SimplifiedRoadMapOfPartOfRomania.BUCHAREST;
import SimplifiedRoadMapOfPartOfRomania.HIRSOVA;
import aima.core.environment.map.Map;
import aima.core.environment.map.MapFunctions;
import aima.core.environment.map.MoveToAction;
import aima.core.environment.map.SimplifiedRoadMapOfPartOfRomania;
import aima.core.search.agent.SearchAgent;
import aima.core.search.framework.Node;
import aima.core.search.framework.SearchForActions;
import aima.core.search.framework.problem.GoalTest;
import aima.core.search.framework.problem.Problem;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author RuedigerLunde
 */
public class SolutionTesterTest {
    @Test
    public void testMultiGoalProblem() throws Exception {
        Map romaniaMap = new SimplifiedRoadMapOfPartOfRomania();
        Problem<String, MoveToAction> problem = new aima.core.search.framework.problem.GeneralProblem<String, MoveToAction>(SimplifiedRoadMapOfPartOfRomania.ARAD, MapFunctions.createActionsFunction(romaniaMap), MapFunctions.createResultFunction(), GoalTest.<String>forState(BUCHAREST).or(GoalTest.forState(HIRSOVA)), MapFunctions.createDistanceStepCostFunction(romaniaMap)) {
            @Override
            public boolean testSolution(Node<String, MoveToAction> node) {
                return (testGoal(node.getState())) && ((node.getPathCost()) > 550);
                // accept paths to goal only if their costs are above 550
            }
        };
        SearchForActions<String, MoveToAction> search = new aima.core.search.uninformed.UniformCostSearch(new aima.core.search.framework.qsearch.GraphSearch());
        SearchAgent<String, MoveToAction> agent = new SearchAgent(problem, search);
        Assert.assertEquals("[Action[name=moveTo, location=Sibiu], Action[name=moveTo, location=RimnicuVilcea], Action[name=moveTo, location=Pitesti], Action[name=moveTo, location=Bucharest], Action[name=moveTo, location=Urziceni], Action[name=moveTo, location=Hirsova]]", agent.getActions().toString());
        Assert.assertEquals(6, agent.getActions().size());
        Assert.assertEquals("15", agent.getInstrumentation().getProperty("nodesExpanded"));
        Assert.assertEquals("1", agent.getInstrumentation().getProperty("queueSize"));
        Assert.assertEquals("5", agent.getInstrumentation().getProperty("maxQueueSize"));
    }
}

