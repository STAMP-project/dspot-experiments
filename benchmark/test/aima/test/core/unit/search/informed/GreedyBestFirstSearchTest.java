package aima.test.core.unit.search.informed;


import SimplifiedRoadMapOfPartOfRomania.BUCHAREST;
import aima.core.agent.Action;
import aima.core.environment.eightpuzzle.EightPuzzleBoard;
import aima.core.environment.eightpuzzle.EightPuzzleFunctions;
import aima.core.environment.map.Map;
import aima.core.environment.map.MapFunctions;
import aima.core.environment.map.MoveToAction;
import aima.core.environment.map.SimplifiedRoadMapOfPartOfRomania;
import aima.core.search.agent.SearchAgent;
import aima.core.search.framework.QueueBasedSearch;
import aima.core.search.framework.SearchForActions;
import aima.core.search.framework.problem.GoalTest;
import aima.core.search.framework.problem.Problem;
import org.junit.Assert;
import org.junit.Test;


public class GreedyBestFirstSearchTest {
    @Test
    public void testGreedyBestFirstSearch() {
        try {
            // EightPuzzleBoard extreme = new EightPuzzleBoard(new int[]
            // {2,0,5,6,4,8,3,7,1});
            // EightPuzzleBoard extreme = new EightPuzzleBoard(new int[]
            // {0,8,7,6,5,4,3,2,1});
            EightPuzzleBoard board = new EightPuzzleBoard(new int[]{ 7, 1, 8, 0, 4, 6, 2, 3, 5 });
            Problem<EightPuzzleBoard, Action> problem = new aima.core.environment.eightpuzzle.BidirectionalEightPuzzleProblem(board);
            SearchForActions<EightPuzzleBoard, Action> search = new aima.core.search.informed.GreedyBestFirstSearch(new aima.core.search.framework.qsearch.GraphSearch(), EightPuzzleFunctions.createManhattanHeuristicFunction());
            SearchAgent<EightPuzzleBoard, Action> agent = new SearchAgent(problem, search);
            Assert.assertEquals(49, agent.getActions().size());// GraphSearchReducedFrontier: "49"

            // GraphSearchReducedFrontier: "197"
            Assert.assertEquals("332", agent.getInstrumentation().getProperty("nodesExpanded"));
            // GraphSearchReducedFrontier: "140"
            Assert.assertEquals("241", agent.getInstrumentation().getProperty("queueSize"));
            // GraphSearchReducedFrontier: "141"
            Assert.assertEquals("242", agent.getInstrumentation().getProperty("maxQueueSize"));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Exception thrown.");
        }
    }

    @Test
    public void testGreedyBestFirstSearchReducedFrontier() {
        try {
            // EightPuzzleBoard extreme = new EightPuzzleBoard(new int[]
            // {2,0,5,6,4,8,3,7,1});
            // EightPuzzleBoard extreme = new EightPuzzleBoard(new int[]
            // {0,8,7,6,5,4,3,2,1});
            EightPuzzleBoard board = new EightPuzzleBoard(new int[]{ 7, 1, 8, 0, 4, 6, 2, 3, 5 });
            Problem<EightPuzzleBoard, Action> problem = new aima.core.environment.eightpuzzle.BidirectionalEightPuzzleProblem(board);
            QueueBasedSearch<EightPuzzleBoard, Action> search = new aima.core.search.informed.GreedyBestFirstSearch(new aima.core.search.framework.qsearch.GraphSearchReducedFrontier(), EightPuzzleFunctions.createManhattanHeuristicFunction());
            SearchAgent<EightPuzzleBoard, Action> agent = new SearchAgent(problem, search);
            Assert.assertEquals(49, agent.getActions().size());
            Assert.assertEquals("197", agent.getInstrumentation().getProperty("nodesExpanded"));
            Assert.assertEquals("140", agent.getInstrumentation().getProperty("queueSize"));
            Assert.assertEquals("141", agent.getInstrumentation().getProperty("maxQueueSize"));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Exception thrown.");
        }
    }

    @Test
    public void testAIMA3eFigure3_23() throws Exception {
        Map romaniaMap = new SimplifiedRoadMapOfPartOfRomania();
        Problem<String, MoveToAction> problem = new aima.core.search.framework.problem.GeneralProblem(SimplifiedRoadMapOfPartOfRomania.ARAD, MapFunctions.createActionsFunction(romaniaMap), MapFunctions.createResultFunction(), GoalTest.forState(BUCHAREST), MapFunctions.createDistanceStepCostFunction(romaniaMap));
        SearchForActions<String, MoveToAction> search = new aima.core.search.informed.GreedyBestFirstSearch(new aima.core.search.framework.qsearch.TreeSearch(), MapFunctions.createSLDHeuristicFunction(BUCHAREST, romaniaMap));
        SearchAgent<String, MoveToAction> agent = new SearchAgent(problem, search);
        Assert.assertEquals("[Action[name=moveTo, location=Sibiu], Action[name=moveTo, location=Fagaras], Action[name=moveTo, location=Bucharest]]", agent.getActions().toString());
        Assert.assertEquals(3, agent.getActions().size());
        Assert.assertEquals("3", agent.getInstrumentation().getProperty("nodesExpanded"));
        Assert.assertEquals("6", agent.getInstrumentation().getProperty("queueSize"));
        Assert.assertEquals("7", agent.getInstrumentation().getProperty("maxQueueSize"));
    }

    @Test
    public void testAIMA3eFigure3_23_using_GraphSearch() throws Exception {
        Map romaniaMap = new SimplifiedRoadMapOfPartOfRomania();
        Problem<String, MoveToAction> problem = new aima.core.search.framework.problem.GeneralProblem(SimplifiedRoadMapOfPartOfRomania.ARAD, MapFunctions.createActionsFunction(romaniaMap), MapFunctions.createResultFunction(), GoalTest.forState(BUCHAREST), MapFunctions.createDistanceStepCostFunction(romaniaMap));
        SearchForActions<String, MoveToAction> search = new aima.core.search.informed.GreedyBestFirstSearch(new aima.core.search.framework.qsearch.GraphSearch(), MapFunctions.createSLDHeuristicFunction(BUCHAREST, romaniaMap));
        SearchAgent<String, MoveToAction> agent = new SearchAgent(problem, search);
        Assert.assertEquals("[Action[name=moveTo, location=Sibiu], Action[name=moveTo, location=Fagaras], Action[name=moveTo, location=Bucharest]]", agent.getActions().toString());
        Assert.assertEquals(3, agent.getActions().size());
        Assert.assertEquals("3", agent.getInstrumentation().getProperty("nodesExpanded"));
        Assert.assertEquals("4", agent.getInstrumentation().getProperty("queueSize"));
        Assert.assertEquals("5", agent.getInstrumentation().getProperty("maxQueueSize"));
    }
}

