package com.vaadin.tests.themes.valo;


import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Test;


public class TableSortIndicatorTest extends MultiBrowserTest {
    @Test
    public void ascendingIndicatorIsShown() throws IOException {
        openTestURL();
        waitUntilLoadingIndicatorNotVisible();
        clickOnCellHeader();
        compareScreen("ascending");
    }

    @Test
    public void descendingIndicatorIsShown() throws IOException {
        openTestURL();
        waitUntilLoadingIndicatorNotVisible();
        clickOnCellHeader();
        clickOnSortIndicator();
        compareScreen("descending");
    }
}

