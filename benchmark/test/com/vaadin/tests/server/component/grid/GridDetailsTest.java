package com.vaadin.tests.server.component.grid;


import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;


public class GridDetailsTest {
    private final class DummyLabel extends Label {
        private DummyLabel(String content) {
            super(content);
        }

        @Override
        public String getConnectorId() {
            return "";
        }
    }

    public static class TestGrid extends Grid<String> {
        /**
         * Used to execute data generation
         */
        public void runDataGeneration() {
            super.getDataCommunicator().beforeClientResponse(true);
        }
    }

    private GridDetailsTest.TestGrid grid;

    private List<String> data;

    @Test
    public void testGridComponentIteratorContainsDetailsComponents() {
        for (Component c : grid) {
            if (c instanceof Label) {
                String value = getValue();
                Assert.assertTrue(("Unexpected label in component iterator with value " + value), data.remove(value));
            } else {
                Assert.fail("Iterator contained a component that is not a label.");
            }
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGridComponentIteratorNotModifiable() {
        Iterator<Component> iterator = iterator();
        iterator.next();
        // This should fail
        iterator.remove();
    }

    @Test
    public void testGridComponentIteratorIsEmptyAfterHidingDetails() {
        Assert.assertTrue("Component iterator should have components.", grid.iterator().hasNext());
        data.forEach(( s) -> setDetailsVisible(s, false));
        Assert.assertFalse("Component iterator should not have components.", grid.iterator().hasNext());
    }
}

