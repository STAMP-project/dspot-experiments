package aima.test.core.unit.search.uninformed;


import aima.core.agent.Action;
import aima.core.environment.nqueens.NQueensBoard;
import aima.core.environment.nqueens.QueenAction;
import aima.core.search.agent.SearchAgent;
import aima.core.search.framework.SearchForActions;
import aima.core.search.framework.problem.Problem;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;


public class DepthFirstSearchTest {
    @Test
    public void testDepthFirstSuccesfulSearch() throws Exception {
        Problem<NQueensBoard, QueenAction> problem = new aima.core.search.framework.problem.GeneralProblem(new NQueensBoard(8), NQueensFunctions::getIFActions, NQueensFunctions::getResult, NQueensFunctions::testGoal);
        SearchForActions<NQueensBoard, QueenAction> search = new aima.core.search.uninformed.DepthFirstSearch(new aima.core.search.framework.qsearch.GraphSearch());
        Optional<List<QueenAction>> actions = search.findActions(problem);
        Assert.assertTrue(actions.isPresent());
        assertCorrectPlacement(actions.get());
        Assert.assertEquals("113", search.getMetrics().get("nodesExpanded"));
    }

    @Test
    public void testDepthFirstUnSuccessfulSearch() throws Exception {
        Problem<NQueensBoard, QueenAction> problem = new aima.core.search.framework.problem.GeneralProblem(new NQueensBoard(3), NQueensFunctions::getIFActions, NQueensFunctions::getResult, NQueensFunctions::testGoal);
        SearchForActions<NQueensBoard, QueenAction> search = new aima.core.search.uninformed.DepthFirstSearch(new aima.core.search.framework.qsearch.GraphSearch());
        SearchAgent<NQueensBoard, QueenAction> agent = new SearchAgent(problem, search);
        List<Action> actions = agent.getActions();
        Assert.assertEquals(0, actions.size());
        Assert.assertEquals("6", agent.getInstrumentation().getProperty("nodesExpanded"));
    }
}

