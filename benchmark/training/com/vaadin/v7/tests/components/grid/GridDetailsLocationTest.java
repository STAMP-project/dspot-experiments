package com.vaadin.v7.tests.components.grid;


import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;


@TestCategory("grid")
public class GridDetailsLocationTest extends MultiBrowserTest {
    private static final int detailsDefaultHeight = 51;

    private static final int detailsDefinedHeight = 33;

    private static class Param {
        private final int rowIndex;

        private final boolean useGenerator;

        private final boolean scrollFirstToBottom;

        public Param(int rowIndex, boolean useGenerator, boolean scrollFirstToBottom) {
            this.rowIndex = rowIndex;
            this.useGenerator = useGenerator;
            this.scrollFirstToBottom = scrollFirstToBottom;
        }

        public int getRowIndex() {
            return rowIndex;
        }

        public boolean useGenerator() {
            return useGenerator;
        }

        public boolean scrollFirstToBottom() {
            return scrollFirstToBottom;
        }

        @Override
        public String toString() {
            return ((((("Param [rowIndex=" + (getRowIndex())) + ", useGenerator=") + (useGenerator())) + ", scrollFirstToBottom=") + (scrollFirstToBottom())) + "]";
        }
    }

    @Test
    public void toggleAndScroll() throws Throwable {
        for (GridDetailsLocationTest.Param param : GridDetailsLocationTest.parameters()) {
            try {
                openTestURL();
                useGenerator(param.useGenerator());
                scrollToBottom(param.scrollFirstToBottom());
                // the tested method
                toggleAndScroll(param.getRowIndex());
                verifyLocation(param);
            } catch (Throwable t) {
                throw new Throwable(("" + param), t);
            }
        }
    }

    @Test
    public void scrollAndToggle() throws Throwable {
        for (GridDetailsLocationTest.Param param : GridDetailsLocationTest.parameters()) {
            try {
                openTestURL();
                useGenerator(param.useGenerator());
                scrollToBottom(param.scrollFirstToBottom());
                // the tested method
                scrollAndToggle(param.getRowIndex());
                verifyLocation(param);
            } catch (Throwable t) {
                throw new Throwable(("" + param), t);
            }
        }
    }

    @Test
    public void testDetailsHeightWithGenerator() {
        openTestURL();
        useGenerator(true);
        toggleAndScroll(5);
        verifyDetailsRowHeight(5, GridDetailsLocationTest.detailsDefinedHeight, 0);
        verifyDetailsDecoratorLocation(5, 0, 0);
        toggleAndScroll(0);
        verifyDetailsRowHeight(0, GridDetailsLocationTest.detailsDefinedHeight, 0);
        // decorator elements are in DOM in the order they have been added
        verifyDetailsDecoratorLocation(0, 0, 1);
        verifyDetailsRowHeight(5, GridDetailsLocationTest.detailsDefinedHeight, 1);
        verifyDetailsDecoratorLocation(5, 1, 0);
    }

    private final By locator = By.className("v-grid-spacer");
}

