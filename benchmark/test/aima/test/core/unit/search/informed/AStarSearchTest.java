package aima.test.core.unit.search.informed;


import QueueSearch.METRIC_PATH_COST;
import SimplifiedRoadMapOfPartOfRomania.BUCHAREST;
import aima.core.agent.Action;
import aima.core.environment.eightpuzzle.EightPuzzleBoard;
import aima.core.environment.eightpuzzle.EightPuzzleFunctions;
import aima.core.search.agent.SearchAgent;
import aima.core.search.framework.Node;
import aima.core.search.framework.SearchForActions;
import aima.core.search.framework.problem.GoalTest;
import aima.core.search.framework.problem.Problem;
import java.util.List;
import java.util.function.ToDoubleFunction;
import org.junit.Assert;
import org.junit.Test;

import static SimplifiedRoadMapOfPartOfRomania.ARAD;
import static SimplifiedRoadMapOfPartOfRomania.SIBIU;


public class AStarSearchTest {
    @Test
    public void testAStarSearch() {
        // added to narrow down bug report filed by L.N.Sudarshan of
        // Thoughtworks and Xin Lu of UCI
        try {
            // EightPuzzleBoard extreme = new EightPuzzleBoard(new int[]
            // {2,0,5,6,4,8,3,7,1});
            // EightPuzzleBoard extreme = new EightPuzzleBoard(new int[]
            // {0,8,7,6,5,4,3,2,1});
            EightPuzzleBoard board = new EightPuzzleBoard(new int[]{ 7, 1, 8, 0, 4, 6, 2, 3, 5 });
            Problem<EightPuzzleBoard, Action> problem = new aima.core.environment.eightpuzzle.BidirectionalEightPuzzleProblem(board);
            SearchForActions<EightPuzzleBoard, Action> search = new aima.core.search.informed.AStarSearch(new aima.core.search.framework.qsearch.GraphSearch(), EightPuzzleFunctions.createManhattanHeuristicFunction());
            SearchAgent<EightPuzzleBoard, Action> agent = new SearchAgent(problem, search);
            Assert.assertEquals(23, agent.getActions().size());
            // "926" GraphSearchReduced Frontier
            Assert.assertEquals("1133", agent.getInstrumentation().getProperty("nodesExpanded"));
            // "534" GraphSearchReduced Frontier
            Assert.assertEquals("676", agent.getInstrumentation().getProperty("queueSize"));
            // "535" GraphSearchReduced Frontier
            Assert.assertEquals("677", agent.getInstrumentation().getProperty("maxQueueSize"));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Exception thrown");
        }
    }

    @Test
    public void testAIMA3eFigure3_15() throws Exception {
        Map romaniaMap = new SimplifiedRoadMapOfPartOfRomania();
        Problem<String, MoveToAction> problem = new aima.core.search.framework.problem.GeneralProblem(SIBIU, MapFunctions.createActionsFunction(romaniaMap), MapFunctions.createResultFunction(), GoalTest.forState(BUCHAREST), MapFunctions.createDistanceStepCostFunction(romaniaMap));
        SearchForActions<String, MoveToAction> search = new aima.core.search.informed.AStarSearch(new aima.core.search.framework.qsearch.GraphSearch(), MapFunctions.createSLDHeuristicFunction(BUCHAREST, romaniaMap));
        SearchAgent<String, MoveToAction> agent = new SearchAgent(problem, search);
        List<Action> actions = agent.getActions();
        Assert.assertEquals("[Action[name=moveTo, location=RimnicuVilcea], Action[name=moveTo, location=Pitesti], Action[name=moveTo, location=Bucharest]]", actions.toString());
        Assert.assertEquals("278.0", search.getMetrics().get(METRIC_PATH_COST));
    }

    @Test
    public void testAIMA3eFigure3_24() throws Exception {
        Map romaniaMap = new SimplifiedRoadMapOfPartOfRomania();
        Problem<String, MoveToAction> problem = new aima.core.search.framework.problem.GeneralProblem(ARAD, MapFunctions.createActionsFunction(romaniaMap), MapFunctions.createResultFunction(), GoalTest.forState(BUCHAREST), MapFunctions.createDistanceStepCostFunction(romaniaMap));
        SearchForActions<String, MoveToAction> search = new aima.core.search.informed.AStarSearch(new aima.core.search.framework.qsearch.TreeSearch(), MapFunctions.createSLDHeuristicFunction(BUCHAREST, romaniaMap));
        SearchAgent<String, MoveToAction> agent = new SearchAgent(problem, search);
        Assert.assertEquals("[Action[name=moveTo, location=Sibiu], Action[name=moveTo, location=RimnicuVilcea], Action[name=moveTo, location=Pitesti], Action[name=moveTo, location=Bucharest]]", agent.getActions().toString());
        Assert.assertEquals(4, agent.getActions().size());
        Assert.assertEquals("5", agent.getInstrumentation().getProperty("nodesExpanded"));
        Assert.assertEquals("10", agent.getInstrumentation().getProperty("queueSize"));
        Assert.assertEquals("11", agent.getInstrumentation().getProperty("maxQueueSize"));
    }

    @Test
    public void testAIMA3eFigure3_24_using_GraphSearch() throws Exception {
        Map romaniaMap = new SimplifiedRoadMapOfPartOfRomania();
        Problem<String, MoveToAction> problem = new aima.core.search.framework.problem.GeneralProblem(ARAD, MapFunctions.createActionsFunction(romaniaMap), MapFunctions.createResultFunction(), GoalTest.forState(BUCHAREST), MapFunctions.createDistanceStepCostFunction(romaniaMap));
        SearchForActions<String, MoveToAction> search = new aima.core.search.informed.AStarSearch(new aima.core.search.framework.qsearch.GraphSearch(), MapFunctions.createSLDHeuristicFunction(BUCHAREST, romaniaMap));
        SearchAgent<String, MoveToAction> agent = new SearchAgent(problem, search);
        Assert.assertEquals("[Action[name=moveTo, location=Sibiu], Action[name=moveTo, location=RimnicuVilcea], Action[name=moveTo, location=Pitesti], Action[name=moveTo, location=Bucharest]]", agent.getActions().toString());
        Assert.assertEquals(4, agent.getActions().size());
        Assert.assertEquals("5", agent.getInstrumentation().getProperty("nodesExpanded"));
        Assert.assertEquals("6", agent.getInstrumentation().getProperty("queueSize"));
        Assert.assertEquals("7", agent.getInstrumentation().getProperty("maxQueueSize"));
    }

    @Test
    public void testCheckFrontierPathCost() throws Exception {
        ExtendableMap map = new ExtendableMap();
        map.addBidirectionalLink("start", "b", 2.5);
        map.addBidirectionalLink("start", "c", 1.0);
        map.addBidirectionalLink("b", "d", 2.0);
        map.addBidirectionalLink("c", "d", 4.0);
        map.addBidirectionalLink("c", "e", 1.0);
        map.addBidirectionalLink("d", "goal", 1.0);
        map.addBidirectionalLink("e", "goal", 5.0);
        Problem<String, MoveToAction> problem = new aima.core.search.framework.problem.GeneralProblem("start", MapFunctions.createActionsFunction(map), MapFunctions.createResultFunction(), GoalTest.forState("goal"), MapFunctions.createDistanceStepCostFunction(map));
        ToDoubleFunction<Node<String, MoveToAction>> h = ( node) -> 0.0;// Don't have one for this test

        SearchForActions<String, MoveToAction> search = new aima.core.search.informed.AStarSearch(new aima.core.search.framework.qsearch.GraphSearch(), h);
        SearchAgent<String, MoveToAction> agent = new SearchAgent(problem, search);
        List<Action> actions = agent.getActions();
        Assert.assertEquals("[Action[name=moveTo, location=b], Action[name=moveTo, location=d], Action[name=moveTo, location=goal]]", actions.toString());
        Assert.assertEquals("5.5", search.getMetrics().get(METRIC_PATH_COST));
    }
}

