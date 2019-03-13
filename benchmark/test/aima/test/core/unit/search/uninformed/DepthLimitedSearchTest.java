package aima.test.core.unit.search.uninformed;


import aima.core.environment.nqueens.NQueensBoard;
import aima.core.environment.nqueens.QueenAction;
import aima.core.search.framework.Node;
import aima.core.search.framework.SearchForActions;
import aima.core.search.framework.problem.Problem;
import aima.core.search.uninformed.DepthLimitedSearch;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests depth-limited search.
 *
 * @author Ruediger Lunde
 */
public class DepthLimitedSearchTest {
    @Test
    public void testSuccessfulDepthLimitedSearch() throws Exception {
        Problem<NQueensBoard, QueenAction> problem = new aima.core.search.framework.problem.GeneralProblem(new NQueensBoard(8), NQueensFunctions::getIFActions, NQueensFunctions::getResult, NQueensFunctions::testGoal);
        SearchForActions<NQueensBoard, QueenAction> search = new DepthLimitedSearch(8);
        Optional<List<QueenAction>> actions = search.findActions(problem);
        Assert.assertTrue(actions.isPresent());
        assertCorrectPlacement(actions.get());
        Assert.assertEquals("113", search.getMetrics().get("nodesExpanded"));
    }

    @Test
    public void testCutOff() throws Exception {
        Problem<NQueensBoard, QueenAction> problem = new aima.core.search.framework.problem.GeneralProblem(new NQueensBoard(8), NQueensFunctions::getIFActions, NQueensFunctions::getResult, NQueensFunctions::testGoal);
        DepthLimitedSearch<NQueensBoard, QueenAction> search = new DepthLimitedSearch(1);
        Optional<Node<NQueensBoard, QueenAction>> result = search.findNode(problem);
        Assert.assertEquals(true, search.isCutoffResult(result));
    }

    @Test
    public void testFailure() throws Exception {
        Problem<NQueensBoard, QueenAction> problem = new aima.core.search.framework.problem.GeneralProblem(new NQueensBoard(3), NQueensFunctions::getIFActions, NQueensFunctions::getResult, NQueensFunctions::testGoal);
        DepthLimitedSearch<NQueensBoard, QueenAction> search = new DepthLimitedSearch(5);
        Optional<List<QueenAction>> actions = search.findActions(problem);
        Assert.assertFalse(actions.isPresent());// failure

    }
}

