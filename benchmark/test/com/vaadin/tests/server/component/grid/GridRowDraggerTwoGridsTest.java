package com.vaadin.tests.server.component.grid;


import DropLocation.ABOVE;
import DropLocation.BELOW;
import DropLocation.EMPTY;
import SourceDataProviderUpdater.NOOP;
import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.DropIndexCalculator;
import com.vaadin.ui.components.grid.GridDropEvent;
import com.vaadin.ui.components.grid.GridRowDragger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


public class GridRowDraggerTwoGridsTest {
    public class TestGridRowDragger extends GridRowDragger<String> {
        public TestGridRowDragger(Grid<String> source, Grid<String> target) {
            super(source, target);
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

    private Grid<String> target;

    private GridRowDraggerTwoGridsTest.TestGridRowDragger dragger;

    private List<String> draggedItems;

    @Test
    public void listDataProviders_basicOperation() {
        source.setItems("0", "1", "2");
        drop(null, null, "0");
        verifySourceDataProvider("1", "2");
        verifyTargetDataProvider("0");
        drop("0", BELOW, "1");
        verifySourceDataProvider("2");
        verifyTargetDataProvider("0", "1");
        drop("1", ABOVE, "2");
        verifySourceDataProvider();
        verifyTargetDataProvider("0", "2", "1");
    }

    @Test
    public void listDataProvider_dropAboveFirst() {
        source.setItems("0");
        target.setItems("1");
        drop("1", ABOVE, "0");
        verifySourceDataProvider();
        verifyTargetDataProvider("0", "1");
    }

    @Test
    public void listDataProvider_customCalculator() {
        source.setItems("0");
        target.setItems("1");
        AtomicInteger trigger = new AtomicInteger();
        setDropIndexCalculator(( event) -> {
            trigger.incrementAndGet();
            return 0;
        });
        drop("1", BELOW, "0");
        Assert.assertEquals("Custom calculator should be invoked", 1, trigger.get());
        verifySourceDataProvider();
        verifyTargetDataProvider("0", "1");
    }

    @Test
    public void listDataProvider_customCalculatorReturnsMax_droppedToEnd() {
        source.setItems("0");
        target.setItems("1", "2");
        setDropIndexCalculator(( event) -> {
            return Integer.MAX_VALUE;
        });
        drop("1", ABOVE, "0");
        verifySourceDataProvider();
        verifyTargetDataProvider("1", "2", "0");
    }

    @Test
    public void customSourceDataProvider_isInvoked() {
        GridRowDraggerTwoGridsTest.setCustomDataProvider(source, "0", "1");
        target.setItems("2");
        AtomicInteger updaterTrigger = new AtomicInteger();
        List<String> droppedItems = new ArrayList<>();
        setSourceDataProviderUpdater(( event, dp, items) -> {
            updaterTrigger.incrementAndGet();
            droppedItems.addAll(items);
        });
        drop("2", BELOW, "0", "1");
        Assert.assertEquals("source updater not triggered", 1, updaterTrigger.get());
        Assert.assertArrayEquals(droppedItems.toArray(), new Object[]{ "0", "1" });
        verifyTargetDataProvider("2", "0", "1");
    }

    @Test
    public void noopSourceUpdater() {
        source.setItems("0", "1");
        target.setItems("2");
        dragger.setSourceDataProviderUpdater(NOOP);
        drop("2", ABOVE, "0", "1");
        verifySourceDataProvider("0", "1");
        verifyTargetDataProvider("0", "1", "2");
    }

    @Test
    public void alwaysDropToEndCalculator() {
        source.setItems("0");
        target.setItems("1", "2");
        dragger.setDropIndexCalculator(DropIndexCalculator.alwaysDropToEnd());
        drop("1", ABOVE, "0");
        verifySourceDataProvider();
        verifyTargetDataProvider("1", "2", "0");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void customSourceDataProvider_noCustomSourceUpdater_unsupportedOperationExceptionThrown() {
        GridRowDraggerTwoGridsTest.setCustomDataProvider(source);
        drop(null, BELOW, "0");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void customTargetDataProvider_noCustomCalculatorAndNoCustomTargetUpdater_unsupportedOperationExceptionThrown() {
        GridRowDraggerTwoGridsTest.setCustomDataProvider(target);
        drop(null, BELOW, "0");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void customTargetDataProvider_customCalculatorAndNoCustomTargetUpdater_unsupportedOperationExceptionThrown() {
        GridRowDraggerTwoGridsTest.setCustomDataProvider(target);
        setDropIndexCalculator(( event) -> 0);
        drop(null, BELOW, "0");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void customTargetDataProvider_noCustomCalculatorAndCustomTargetUpdater_unsupportedOperationExceptionThrown() {
        source.setItems("0");
        GridRowDraggerTwoGridsTest.setCustomDataProvider(target);
        setTargetDataProviderUpdater(( event, dp, index, items) -> {
        });
        drop(null, BELOW, "0");
    }

    @Test
    public void customTargetDataProvider_customCalculatorAndCustomTargetUpdater_triggeredWithMaxIndex() {
        source.setItems("0");
        GridRowDraggerTwoGridsTest.setCustomDataProvider(target, "1", "2", "3");
        AtomicInteger updaterTrigger = new AtomicInteger((-1));
        setTargetDataProviderUpdater(( event, dp, index, items) -> updaterTrigger.set(index));
        AtomicInteger calculatorTrigger = new AtomicInteger();
        setDropIndexCalculator(( event) -> {
            calculatorTrigger.incrementAndGet();
            return 2;
        });
        drop("1", ABOVE, "2");
        Assert.assertEquals("custom calculator not triggered", 1, calculatorTrigger.get());
        // getting value from custom calculator
        Assert.assertEquals("given drop index to target updater is wrong", 2, updaterTrigger.get());
    }

    @Test
    public void dropOnSortedGrid_byDefault_dropsToTheEnd() {
        Assert.assertFalse("Default drops on sorted grid rows should not be allowed", getGridDropTarget().isDropAllowedOnRowsWhenSorted());
        source.setItems("0", "1", "2");
        target.setItems("4", "5");
        target.sort("1");
        drop(null, EMPTY, "0");
        verifySourceDataProvider("1", "2");
        verifyTargetDataProvider("4", "5", "0");
    }
}

