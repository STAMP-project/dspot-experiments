package aima.test.core.unit.environment.cellworld;


import CellWorldAction.Down;
import CellWorldAction.Left;
import CellWorldAction.Right;
import CellWorldAction.Up;
import aima.core.environment.cellworld.Cell;
import aima.core.environment.cellworld.CellWorld;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ravi Mohan
 * @author Ciaran O'Reilly
 */
public class CellWorldTest {
    private CellWorld<Double> cw;

    @Test
    public void testNumberOfCells() {
        Assert.assertEquals(11, cw.getCells().size());
    }

    @Test
    public void testMoveUpIntoAdjacentCellChangesPositionCorrectly() {
        Cell<Double> sDelta = cw.result(cw.getCellAt(1, 1), Up);
        Assert.assertEquals(1, sDelta.getX());
        Assert.assertEquals(2, sDelta.getY());
    }

    @Test
    public void testMoveUpIntoWallLeavesPositionUnchanged() {
        Cell<Double> sDelta = cw.result(cw.getCellAt(1, 3), Up);
        Assert.assertEquals(1, sDelta.getX());
        Assert.assertEquals(3, sDelta.getY());
    }

    @Test
    public void testMoveUpIntoRemovedCellLeavesPositionUnchanged() {
        Cell<Double> sDelta = cw.result(cw.getCellAt(2, 1), Up);
        Assert.assertEquals(2, sDelta.getX());
        Assert.assertEquals(1, sDelta.getY());
    }

    @Test
    public void testMoveDownIntoAdjacentCellChangesPositionCorrectly() {
        Cell<Double> sDelta = cw.result(cw.getCellAt(1, 2), Down);
        Assert.assertEquals(1, sDelta.getX());
        Assert.assertEquals(1, sDelta.getY());
    }

    @Test
    public void testMoveDownIntoWallLeavesPositionUnchanged() {
        Cell<Double> sDelta = cw.result(cw.getCellAt(1, 1), Down);
        Assert.assertEquals(1, sDelta.getX());
        Assert.assertEquals(1, sDelta.getY());
    }

    @Test
    public void testMoveDownIntoRemovedCellLeavesPositionUnchanged() {
        Cell<Double> sDelta = cw.result(cw.getCellAt(2, 3), Down);
        Assert.assertEquals(2, sDelta.getX());
        Assert.assertEquals(3, sDelta.getY());
    }

    @Test
    public void testMoveLeftIntoAdjacentCellChangesPositionCorrectly() {
        Cell<Double> sDelta = cw.result(cw.getCellAt(2, 1), Left);
        Assert.assertEquals(1, sDelta.getX());
        Assert.assertEquals(1, sDelta.getY());
    }

    @Test
    public void testMoveLeftIntoWallLeavesPositionUnchanged() {
        Cell<Double> sDelta = cw.result(cw.getCellAt(1, 1), Left);
        Assert.assertEquals(1, sDelta.getX());
        Assert.assertEquals(1, sDelta.getY());
    }

    @Test
    public void testMoveLeftIntoRemovedCellLeavesPositionUnchanged() {
        Cell<Double> sDelta = cw.result(cw.getCellAt(3, 2), Left);
        Assert.assertEquals(3, sDelta.getX());
        Assert.assertEquals(2, sDelta.getY());
    }

    @Test
    public void testMoveRightIntoAdjacentCellChangesPositionCorrectly() {
        Cell<Double> sDelta = cw.result(cw.getCellAt(1, 1), Right);
        Assert.assertEquals(2, sDelta.getX());
        Assert.assertEquals(1, sDelta.getY());
    }

    @Test
    public void testMoveRightIntoWallLeavesPositionUnchanged() {
        Cell<Double> sDelta = cw.result(cw.getCellAt(4, 1), Right);
        Assert.assertEquals(4, sDelta.getX());
        Assert.assertEquals(1, sDelta.getY());
    }

    @Test
    public void testMoveRightIntoRemovedCellLeavesPositionUnchanged() {
        Cell<Double> sDelta = cw.result(cw.getCellAt(1, 2), Right);
        Assert.assertEquals(1, sDelta.getX());
        Assert.assertEquals(2, sDelta.getY());
    }
}

