package org.jabref.gui;


import java.awt.event.KeyEvent;
import java.util.regex.Pattern;
import org.assertj.swing.fixture.JTableCellFixture;
import org.assertj.swing.fixture.JTableFixture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


/**
 * Specific Use-Case: I import a database. Then I doubleclick on the first entry in the table to open the entry editor.
 * Then I click on the first entry again, and scroll through all of the lists entries, without having to click on the
 * table again.
 */
@Tag("GUITest")
public class EntryTableTest extends AbstractUITest {
    private static final int SCROLL_ACTION_EXECUTION = 5;

    private static final String TEST_FILE_NAME = "testbib/testjabref.bib";

    private static final int DOWN = KeyEvent.VK_DOWN;

    private static final int UP = KeyEvent.VK_UP;

    private static final int TITLE_COLUMN_INDEX = 5;

    @Test
    public void scrollThroughEntryList() throws Exception {
        String path = getAbsolutePath(EntryTableTest.TEST_FILE_NAME);
        importBibIntoNewDatabase(path);
        JTableFixture entryTable = mainFrame.table();
        // use a pattern from the first row to select it since it seems to be the best way to get the cell object
        Pattern pattern = Pattern.compile("256.*");
        JTableCellFixture firstCell = entryTable.cell(pattern);
        entryTable.selectRows(0).doubleClick();
        // delay has to be shortened so that double click is recognized
        robot().settings().delayBetweenEvents(0);
        firstCell.doubleClick();
        robot().settings().delayBetweenEvents(AbstractUITest.SPEED_NORMAL);
        firstCell.click();
        // is the first table entry selected?
        assertColumnValue(entryTable, 0, EntryTableTest.TITLE_COLUMN_INDEX, entryTable.selectionValue());
        // go throught the table and check if the entry with the correct index is selected
        for (int i = 0; i < (EntryTableTest.SCROLL_ACTION_EXECUTION); i++) {
            robot().pressAndReleaseKey(EntryTableTest.DOWN);
            Assertions.assertNotNull(entryTable.selectionValue());
            assertColumnValue(entryTable, (i + 1), EntryTableTest.TITLE_COLUMN_INDEX, entryTable.selectionValue());
        }
        // do the same going up again
        for (int i = EntryTableTest.SCROLL_ACTION_EXECUTION; i > 0; i--) {
            robot().pressAndReleaseKey(EntryTableTest.UP);
            Assertions.assertNotNull(entryTable.selectionValue());
            assertColumnValue(entryTable, (i - 1), EntryTableTest.TITLE_COLUMN_INDEX, entryTable.selectionValue());
        }
        closeDatabase();
        exitJabRef();
    }
}

