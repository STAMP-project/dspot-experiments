package aima.test.core.unit.environment.nqueens;


import aima.core.environment.nqueens.NQueensBoard;
import aima.core.environment.nqueens.QueenAction;
import aima.core.search.framework.problem.ActionsFunction;
import aima.core.search.framework.problem.GoalTest;
import aima.core.search.framework.problem.ResultFunction;
import aima.core.util.datastructure.XYLocation;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ravi Mohan
 * @author Ciaran O'Reilly
 * @author Ruediger Lunde
 */
public class NQueensFunctionsTest {
    private ActionsFunction<NQueensBoard, QueenAction> actionsFn;

    private ResultFunction<NQueensBoard, QueenAction> resultFn;

    private GoalTest<NQueensBoard> goalTest;

    private NQueensBoard oneBoard;

    private NQueensBoard eightBoard;

    private NQueensBoard board;

    @Test
    public void testSimpleBoardSuccessorGenerator() {
        List<QueenAction> actions = new java.util.ArrayList(actionsFn.apply(oneBoard));
        Assert.assertEquals(1, actions.size());
        NQueensBoard next = resultFn.apply(oneBoard, actions.get(0));
        Assert.assertEquals(1, next.getNumberOfQueensOnBoard());
    }

    @Test
    public void testComplexBoardSuccessorGenerator() {
        List<QueenAction> actions = new java.util.ArrayList(actionsFn.apply(eightBoard));
        Assert.assertEquals(8, actions.size());
        NQueensBoard next = resultFn.apply(eightBoard, actions.get(0));
        Assert.assertEquals(1, next.getNumberOfQueensOnBoard());
        actions = new java.util.ArrayList(actionsFn.apply(next));
        Assert.assertEquals(6, actions.size());
    }

    @Test
    public void testEmptyBoard() {
        Assert.assertFalse(goalTest.test(board));
    }

    @Test
    public void testSingleSquareBoard() {
        board = new NQueensBoard(1);
        Assert.assertFalse(goalTest.test(board));
        board.addQueenAt(new XYLocation(0, 0));
        Assert.assertTrue(goalTest.test(board));
    }

    @Test
    public void testInCorrectPlacement() {
        Assert.assertFalse(goalTest.test(board));
        // This is the configuration of Figure 3.5 (b) in AIMA 2nd Edition
        board.addQueenAt(new XYLocation(0, 0));
        board.addQueenAt(new XYLocation(1, 2));
        board.addQueenAt(new XYLocation(2, 4));
        board.addQueenAt(new XYLocation(3, 6));
        board.addQueenAt(new XYLocation(4, 1));
        board.addQueenAt(new XYLocation(5, 3));
        board.addQueenAt(new XYLocation(6, 5));
        board.addQueenAt(new XYLocation(7, 7));
        Assert.assertFalse(goalTest.test(board));
    }

    @Test
    public void testCorrectPlacement() {
        Assert.assertFalse(goalTest.test(board));
        // This is the configuration of Figure 5.9 (c) in AIMA 2nd Edition
        board.addQueenAt(new XYLocation(0, 1));
        board.addQueenAt(new XYLocation(1, 4));
        board.addQueenAt(new XYLocation(2, 6));
        board.addQueenAt(new XYLocation(3, 3));
        board.addQueenAt(new XYLocation(4, 0));
        board.addQueenAt(new XYLocation(5, 7));
        board.addQueenAt(new XYLocation(6, 5));
        board.addQueenAt(new XYLocation(7, 2));
        Assert.assertTrue(goalTest.test(board));
    }
}

