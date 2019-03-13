package aima.test.core.unit.environment.tictactoe;


import MinimaxSearch.METRICS_NODES_EXPANDED;
import TicTacToeState.EMPTY;
import TicTacToeState.O;
import TicTacToeState.X;
import aima.core.environment.tictactoe.TicTacToeGame;
import aima.core.environment.tictactoe.TicTacToeState;
import aima.core.search.adversarial.AlphaBetaSearch;
import aima.core.search.adversarial.IterativeDeepeningAlphaBetaSearch;
import aima.core.search.adversarial.MinimaxSearch;
import aima.core.util.datastructure.XYLocation;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ruediger Lunde
 */
public class TicTacToeTest {
    private TicTacToeGame game;

    private TicTacToeState state;

    private double epsilon = 1.0E-4;

    @Test
    public void testCreation() {
        game = new TicTacToeGame();
        state = game.getInitialState();
        Assert.assertEquals(9, game.getActions(state).size());
        Assert.assertEquals(X, game.getPlayer(state));
    }

    @Test
    public void testHashCode() {
        TicTacToeState initialState1 = game.getInitialState();
        TicTacToeState initialState2 = game.getInitialState();
        Assert.assertEquals(initialState1.hashCode(), initialState2.hashCode());
        TicTacToeState state1 = game.getResult(initialState1, new XYLocation(0, 0));
        Assert.assertNotSame(state1.hashCode(), initialState2.hashCode());
        TicTacToeState state2 = game.getResult(initialState2, new XYLocation(0, 0));
        Assert.assertEquals(state1.hashCode(), state2.hashCode());
    }

    @Test
    public void testOnCreationBoardIsEmpty() {
        Assert.assertEquals(EMPTY, state.getValue(0, 0));
        Assert.assertEquals(EMPTY, state.getValue(0, 2));
        Assert.assertEquals(EMPTY, state.getValue(2, 0));
        Assert.assertEquals(EMPTY, state.getValue(2, 2));
        Assert.assertEquals(true, state.isEmpty(0, 0));
        Assert.assertEquals(true, state.isEmpty(2, 2));
    }

    @Test
    public void testMakingOneMoveChangesState() {
        state = game.getResult(state, new XYLocation(0, 0));
        Assert.assertEquals(X, state.getValue(0, 0));
        Assert.assertEquals(false, state.isEmpty(0, 0));
        Assert.assertEquals(8, game.getActions(state).size());
        Assert.assertEquals(O, game.getPlayer(state));
    }

    @Test
    public void testMakingTwoMovesChangesState() {
        state = game.getResult(state, new XYLocation(0, 0));
        state = game.getResult(state, new XYLocation(0, 1));
        Assert.assertEquals(O, state.getValue(0, 1));
        Assert.assertEquals(false, state.isEmpty(0, 1));
        Assert.assertEquals(true, state.isEmpty(1, 0));
        Assert.assertEquals(7, game.getActions(state).size());
        Assert.assertEquals(X, game.getPlayer(state));
    }

    @Test
    public void testVerticalLineThroughBoard() {
        state.mark(0, 0);
        state.mark(1, 0);
        state.mark(0, 1);
        state.mark(1, 1);
        Assert.assertEquals(false, state.lineThroughBoard());
        state.mark(new XYLocation(0, 2));
        Assert.assertEquals(true, state.lineThroughBoard());
    }

    @Test
    public void testHorizontalLineThroughBoard() {
        state.mark(0, 0);
        state.mark(0, 1);
        state.mark(1, 0);
        state.mark(1, 1);
        Assert.assertEquals(false, state.lineThroughBoard());
        state.mark(2, 0);
        Assert.assertEquals(true, state.lineThroughBoard());
    }

    @Test
    public void testDiagonalLineThroughBoard() {
        state.mark(0, 0);
        state.mark(0, 1);
        state.mark(1, 1);
        state.mark(0, 2);
        Assert.assertEquals(false, state.lineThroughBoard());
        state.mark(2, 2);
        Assert.assertEquals(true, state.lineThroughBoard());
    }

    @Test
    public void testMinmaxValueCalculation() {
        MinimaxSearch<TicTacToeState, XYLocation, String> search = MinimaxSearch.createFor(game);
        Assert.assertTrue(((epsilon) > (Math.abs(((search.maxValue(state, X)) - 0.5)))));
        Assert.assertTrue(((epsilon) > (Math.abs(((search.minValue(state, O)) - 0.5)))));
        // x o x
        // o o x
        // - - -
        // next move: x
        state.mark(0, 0);// x

        state.mark(1, 0);// o

        state.mark(2, 0);// x

        state.mark(0, 1);// o

        state.mark(2, 1);// x

        state.mark(1, 1);// o

        Assert.assertTrue(((epsilon) > (Math.abs(((search.maxValue(state, X)) - 1)))));
        Assert.assertTrue(((epsilon) > (Math.abs(search.minValue(state, O)))));
        XYLocation action = search.makeDecision(state);
        Assert.assertEquals(new XYLocation(2, 2), action);
    }

    @Test
    public void testMinmaxDecision() {
        MinimaxSearch<TicTacToeState, XYLocation, String> search = MinimaxSearch.createFor(game);
        search.makeDecision(state);
        int expandedNodes = search.getMetrics().getInt(METRICS_NODES_EXPANDED);
        Assert.assertEquals(549945, expandedNodes);
    }

    @Test
    public void testAlphaBetaDecision() {
        AlphaBetaSearch<TicTacToeState, XYLocation, String> search = AlphaBetaSearch.createFor(game);
        search.makeDecision(state);
        int expandedNodes = search.getMetrics().getInt(METRICS_NODES_EXPANDED);
        Assert.assertEquals(30709, expandedNodes);
    }

    @Test
    public void testIterativeDeepeningAlphaBetaDecision() {
        IterativeDeepeningAlphaBetaSearch<TicTacToeState, XYLocation, String> search = IterativeDeepeningAlphaBetaSearch.createFor(game, 0.0, 1.0, 100);
        search.makeDecision(state);
        int expandedNodes = search.getMetrics().getInt(METRICS_NODES_EXPANDED);
        Assert.assertEquals(76035, expandedNodes);
    }
}

