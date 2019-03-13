package aima.test.core.unit.search.uninformed;


import aima.core.environment.nqueens.NQueensBoard;
import aima.core.environment.nqueens.QueenAction;
import aima.core.search.framework.SearchForActions;
import aima.core.search.framework.problem.Problem;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;


public class IterativeDeepeningSearchTest {
    @Test
    public void testIterativeDeepeningSearch() {
        try {
            Problem<NQueensBoard, QueenAction> problem = new aima.core.search.framework.problem.GeneralProblem(new NQueensBoard(8), NQueensFunctions::getIFActions, NQueensFunctions::getResult, NQueensFunctions::testGoal);
            SearchForActions<NQueensBoard, QueenAction> search = new aima.core.search.uninformed.IterativeDeepeningSearch();
            Optional<List<QueenAction>> actions = search.findActions(problem);
            Assert.assertTrue(actions.isPresent());
            assertCorrectPlacement(actions.get());
            Assert.assertEquals("3656", search.getMetrics().get("nodesExpanded"));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Exception should not occur");
        }
    }
}

