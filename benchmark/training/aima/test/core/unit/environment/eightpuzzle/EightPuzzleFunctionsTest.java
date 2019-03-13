package aima.test.core.unit.environment.eightpuzzle;


import EightPuzzleBoard.DOWN;
import EightPuzzleBoard.LEFT;
import EightPuzzleBoard.RIGHT;
import EightPuzzleBoard.UP;
import aima.core.agent.Action;
import aima.core.environment.eightpuzzle.EightPuzzleBoard;
import aima.core.environment.eightpuzzle.EightPuzzleFunctions;
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
public class EightPuzzleFunctionsTest {
    EightPuzzleBoard board;

    @Test
    public void testGenerateCorrect3Successors() {
        List<Action> actions = new java.util.ArrayList(EightPuzzleFunctions.getActions(board));
        Assert.assertEquals(3, actions.size());
        // test first successor
        EightPuzzleBoard expectedFirst = new EightPuzzleBoard(new int[]{ 1, 2, 0, 3, 4, 5, 6, 7, 8 });
        EightPuzzleBoard actualFirst = ((EightPuzzleBoard) (EightPuzzleFunctions.getResult(board, actions.get(0))));
        Assert.assertEquals(expectedFirst, actualFirst);
        Assert.assertEquals(UP, actions.get(0));
        // test second successor
        EightPuzzleBoard expectedSecond = new EightPuzzleBoard(new int[]{ 1, 2, 5, 3, 4, 8, 6, 7, 0 });
        EightPuzzleBoard actualSecond = ((EightPuzzleBoard) (EightPuzzleFunctions.getResult(board, actions.get(1))));
        Assert.assertEquals(expectedSecond, actualSecond);
        Assert.assertEquals(DOWN, actions.get(1));
        // test third successor
        EightPuzzleBoard expectedThird = new EightPuzzleBoard(new int[]{ 1, 2, 5, 3, 0, 4, 6, 7, 8 });
        EightPuzzleBoard actualThird = ((EightPuzzleBoard) (EightPuzzleFunctions.getResult(board, actions.get(2))));
        Assert.assertEquals(expectedThird, actualThird);
        Assert.assertEquals(LEFT, actions.get(2));
    }

    @Test
    public void testGenerateCorrectWhenGapMovedRightward() {
        board.moveGapLeft();// gives { 1, 2, 5, 3, 0, 4, 6, 7, 8 }

        Assert.assertEquals(new EightPuzzleBoard(new int[]{ 1, 2, 5, 3, 0, 4, 6, 7, 8 }), board);
        List<Action> actions = new java.util.ArrayList(EightPuzzleFunctions.getActions(board));
        Assert.assertEquals(4, actions.size());
        EightPuzzleBoard expectedFourth = new EightPuzzleBoard(new int[]{ 1, 2, 5, 3, 4, 0, 6, 7, 8 });
        EightPuzzleBoard actualFourth = ((EightPuzzleBoard) (EightPuzzleFunctions.getResult(board, actions.get(3))));
        Assert.assertEquals(expectedFourth, actualFourth);
        Assert.assertEquals(RIGHT, actions.get(3));
    }
}

