package com.vaadin.tests.server.component.grid;


import DropLocation.ABOVE;
import DropLocation.BELOW;
import DropLocation.EMPTY;
import SourceDataProviderUpdater.NOOP;
import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.DropIndexCalculator;
import com.vaadin.ui.components.grid.GridDropEvent;
import com.vaadin.ui.components.grid.GridRowDragger;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


public class GridRowDraggerOneGridTest {
    public class TestGridRowDragger extends GridRowDragger<String> {
        public TestGridRowDragger(Grid<String> grid) {
            super(grid);
        }

        @Override
        public void handleDrop(GridDropEvent<String> event) {
            super.handleDrop(event);
        }

        @Override
        public List<String> getDraggedItems() {
            return draggedItems;
        }
    }

    private Grid<String> source;

    private GridRowDraggerOneGridTest.TestGridRowDragger dragger;

    private List<String> draggedItems;

    @Test
    public void listDataProviders_basicOperation() {
        source.setItems("0", "1", "2");
        drop(null, null, "0");
        verifyDataProvider("1", "2", "0");
        drop("0", BELOW, "1");
        verifyDataProvider("2", "0", "1");
        drop("1", ABOVE, "2");
        verifyDataProvider("0", "2", "1");
    }

    @Test
    public void listDataProvider_dropAboveFirst() {
        source.setItems("0", "1");
        drop("0", ABOVE, "1");
        verifyDataProvider("1", "0");
    }

    @Test
    public void listDataProvider_customCalculator() {
        source.setItems("0", "1");
        AtomicInteger trigger = new AtomicInteger();
        setDropIndexCalculator(( event) -> {
            trigger.incrementAndGet();
            return 0;
        });
        drop("1", BELOW, "0");
        Assert.assertEquals("Custom calculator should be invoked", 1, trigger.get());
        verifyDataProvider("0", "1");
    }

    @Test
    public void listDataProvider_customCalculatorReturnsMax_droppedToEnd() {
        source.setItems("0", "1", "2");
        setDropIndexCalculator(( event) -> {
            return Integer.MAX_VALUE;
        });
        drop("1", ABOVE, "0");
        verifyDataProvider("1", "2", "0");
    }

    @Test
    public void noopSourceUpdater() {
        source.setItems("0", "1", "2");
        dragger.setSourceDataProviderUpdater(NOOP);
        drop("2", ABOVE, "0", "1");
        verifyDataProvider("0", "1", "0", "1", "2");
    }

    @Test
    public void alwaysDropToEndCalculator() {
        source.setItems("0", "1", "2");
        dragger.setDropIndexCalculator(DropIndexCalculator.alwaysDropToEnd());
        drop("1", ABOVE, "0");
        verifyDataProvider("1", "2", "0");
    }

    @Test
    public void dropTwoFromEnd_beginning() {
        source.setItems("0", "1", "2", "3");
        drop("0", ABOVE, "2", "3");
        verifyDataProvider("2", "3", "0", "1");
    }

    @Test
    public void dropTwoFromEnd_middle() {
        source.setItems("0", "1", "2", "3");
        drop("1", ABOVE, "2", "3");
        verifyDataProvider("0", "2", "3", "1");
    }

    @Test
    public void dropTwoFromEnd_aboveOneThatIsDragged_doesntExplode() {
        source.setItems("0", "1", "2", "3");
        drop("2", ABOVE, "2", "3");
        verifyDataProvider("0", "1", "2", "3");
    }

    @Test
    public void dragAndAboveFirst_thatIsAlsoDragged_doesntExplode() {
        source.setItems("0", "1", "2", "3");
        drop("2", ABOVE, "2", "3");
        verifyDataProvider("0", "1", "2", "3");
    }

    @Test
    public void dropFromBeginning_afterOneDragged_doesntExplode() {
        source.setItems("0", "1", "2", "3", "4");
        drop("3", BELOW, "0", "1", "3");
        verifyDataProvider("2", "0", "1", "3", "4");
    }

    @Test
    public void dropMixedSet_onOneOfTheDragged_doesntExplode() {
        source.setItems("0", "1", "2", "3", "4");
        drop("2", BELOW, "0", "2", "4");
        verifyDataProvider("1", "0", "2", "4", "3");
    }

    @Test
    public void dropOnSortedGrid_byDefault_dropsToTheEnd() {
        Assert.assertFalse("Default drops on sorted grid rows should not be allowed", getGridDropTarget().isDropAllowedOnRowsWhenSorted());
        source.setItems("0", "1", "2", "3", "4");
        drop("3", BELOW, "1");
        verifyDataProvider("0", "2", "3", "1", "4");
        source.sort("1");
        drop(null, EMPTY, "0");
        verifyDataProvider("2", "3", "1", "4", "0");
    }
}

