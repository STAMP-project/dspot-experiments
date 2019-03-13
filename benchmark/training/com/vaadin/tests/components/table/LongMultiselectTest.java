package com.vaadin.tests.components.table;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class LongMultiselectTest extends MultiBrowserTest {
    private int ROWCOUNT = 100;

    private int FIRSTSELECTEDROW = 4;

    private int LASTSELECTEDROW = 97;

    @Test
    public void selectedRowsAreUpdated() throws InterruptedException {
        openTestURL();
        selectRows();
        $(ButtonElement.class).first().click();
        TableElement table = getTable();
        MatcherAssert.assertThat(table.getCell(LASTSELECTEDROW, 1).getText(), CoreMatchers.is("updated"));
        MatcherAssert.assertThat(table.getCell(((LASTSELECTEDROW) - 1), 1).getText(), CoreMatchers.is("updated"));
    }
}

