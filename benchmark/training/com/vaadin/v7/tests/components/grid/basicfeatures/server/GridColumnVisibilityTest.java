package com.vaadin.v7.tests.components.grid.basicfeatures.server;


import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicFeaturesTest;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


@TestCategory("grid")
public class GridColumnVisibilityTest extends GridBasicFeaturesTest {
    private static final String[] TOGGLE_LISTENER = new String[]{ "Component", "State", "ColumnVisibilityChangeListener" };

    private static final String[] TOGGLE_HIDE_COLUMN_0 = new String[]{ "Component", "Columns", "Column 0", "Hidden" };

    private static final String COLUMN_0_BECAME_HIDDEN_MSG = "Visibility " + "changed: propertyId: Column 0, isHidden: true";

    private static final String COLUMN_0_BECAME_UNHIDDEN_MSG = "Visibility " + "changed: propertyId: Column 0, isHidden: false";

    private static final String USER_ORIGINATED_TRUE = "userOriginated: true";

    private static final String USER_ORIGINATED_FALSE = "userOriginated: false";

    @Test
    public void columnIsNotShownWhenHidden() {
        Assert.assertEquals("column 0", getGridElement().getHeaderCell(0, 0).getText().toLowerCase(Locale.ROOT));
        selectMenuPath(GridColumnVisibilityTest.TOGGLE_HIDE_COLUMN_0);
        Assert.assertEquals("column 1", getGridElement().getHeaderCell(0, 0).getText().toLowerCase(Locale.ROOT));
    }

    @Test
    public void columnIsShownWhenUnhidden() {
        selectMenuPath(GridColumnVisibilityTest.TOGGLE_HIDE_COLUMN_0);
        selectMenuPath(GridColumnVisibilityTest.TOGGLE_HIDE_COLUMN_0);
        Assert.assertEquals("column 0", getGridElement().getHeaderCell(0, 0).getText().toLowerCase(Locale.ROOT));
    }

    @Test
    public void registeringListener() {
        Assert.assertFalse(logContainsText(GridColumnVisibilityTest.COLUMN_0_BECAME_HIDDEN_MSG));
        selectMenuPath(GridColumnVisibilityTest.TOGGLE_LISTENER);
        Assert.assertFalse(logContainsText(GridColumnVisibilityTest.COLUMN_0_BECAME_HIDDEN_MSG));
        selectMenuPath(GridColumnVisibilityTest.TOGGLE_HIDE_COLUMN_0);
        Assert.assertTrue(logContainsText(GridColumnVisibilityTest.COLUMN_0_BECAME_HIDDEN_MSG));
        Assert.assertTrue(logContainsText(GridColumnVisibilityTest.USER_ORIGINATED_FALSE));
        selectMenuPath(GridColumnVisibilityTest.TOGGLE_HIDE_COLUMN_0);
        Assert.assertTrue(logContainsText(GridColumnVisibilityTest.COLUMN_0_BECAME_UNHIDDEN_MSG));
        Assert.assertTrue(logContainsText(GridColumnVisibilityTest.USER_ORIGINATED_FALSE));
    }

    @Test
    public void deregisteringListener() {
        selectMenuPath(GridColumnVisibilityTest.TOGGLE_LISTENER);
        selectMenuPath(GridColumnVisibilityTest.TOGGLE_HIDE_COLUMN_0);
        selectMenuPath(GridColumnVisibilityTest.TOGGLE_LISTENER);
        selectMenuPath(GridColumnVisibilityTest.TOGGLE_HIDE_COLUMN_0);
        Assert.assertFalse(logContainsText(GridColumnVisibilityTest.COLUMN_0_BECAME_UNHIDDEN_MSG));
    }

    @Test
    public void testColumnHiding_userOriginated_correctParams() {
        selectMenuPath(GridColumnVisibilityTest.TOGGLE_LISTENER);
        toggleColumnHidable(0);
        assertColumnHeaderOrder(0, 1, 2, 3);
        getSidebarOpenButton().click();
        getColumnHidingToggle(0).click();
        getSidebarOpenButton().click();
        assertColumnHeaderOrder(1, 2, 3);
        Assert.assertTrue(logContainsText(GridColumnVisibilityTest.COLUMN_0_BECAME_HIDDEN_MSG));
        Assert.assertTrue(logContainsText(GridColumnVisibilityTest.USER_ORIGINATED_TRUE));
        getSidebarOpenButton().click();
        getColumnHidingToggle(0).click();
        getSidebarOpenButton().click();
        assertColumnHeaderOrder(0, 1, 2, 3);
        Assert.assertTrue(logContainsText(GridColumnVisibilityTest.COLUMN_0_BECAME_UNHIDDEN_MSG));
        Assert.assertTrue(logContainsText(GridColumnVisibilityTest.USER_ORIGINATED_TRUE));
        getSidebarOpenButton().click();
        getColumnHidingToggle(0).click();
        getSidebarOpenButton().click();
        assertColumnHeaderOrder(1, 2, 3);
        Assert.assertTrue(logContainsText(GridColumnVisibilityTest.COLUMN_0_BECAME_HIDDEN_MSG));
        Assert.assertTrue(logContainsText(GridColumnVisibilityTest.USER_ORIGINATED_TRUE));
    }

    @Test
    public void testColumnHiding_whenHidableColumnRemoved_toggleRemoved() {
        toggleColumnHidable(0);
        toggleColumnHidable(1);
        getSidebarOpenButton().click();
        Assert.assertNotNull(getColumnHidingToggle(0));
        addRemoveColumn(0);
        Assert.assertNull(getColumnHidingToggle(0));
    }

    @Test
    public void testColumnHiding_whenHidableColumnAdded_toggleWithCorrectCaptionAdded() {
        selectMenuPath("Component", "Size", "Width", "100%");
        toggleColumnHidable(0);
        toggleColumnHidable(1);
        toggleColumnHidingToggleCaptionChange(0);
        getSidebarOpenButton().click();
        Assert.assertEquals("Column 0 caption 0", getColumnHidingToggle(0).getText());
        getSidebarOpenButton().click();
        addRemoveColumn(0);
        addRemoveColumn(4);
        addRemoveColumn(5);
        addRemoveColumn(6);
        addRemoveColumn(7);
        addRemoveColumn(8);
        addRemoveColumn(9);
        addRemoveColumn(10);
        assertColumnHeaderOrder(1, 2, 3, 11);
        getSidebarOpenButton().click();
        Assert.assertNull(getColumnHidingToggle(0));
        getSidebarOpenButton().click();
        addRemoveColumn(0);
        assertColumnHeaderOrder(1, 2, 3, 11, 0);
        getSidebarOpenButton().click();
        Assert.assertEquals("Column 0 caption 0", getColumnHidingToggle(0).getText());
    }

    @Test
    public void testColumnHidingToggleCaption_settingToggleCaption_updatesToggle() {
        toggleColumnHidable(1);
        getSidebarOpenButton().click();
        Assert.assertEquals("column 1", getGridElement().getHeaderCell(0, 1).getText().toLowerCase(Locale.ROOT));
        Assert.assertEquals("Column 1", getColumnHidingToggle(1).getText());
        toggleColumnHidingToggleCaptionChange(1);
        getSidebarOpenButton().click();
        Assert.assertEquals("column 1", getGridElement().getHeaderCell(0, 1).getText().toLowerCase(Locale.ROOT));
        Assert.assertEquals("Column 1 caption 0", getColumnHidingToggle(1).getText());
        toggleColumnHidingToggleCaptionChange(1);
        getSidebarOpenButton().click();
        Assert.assertEquals("Column 1 caption 1", getColumnHidingToggle(1).getText());
    }

    @Test
    public void testColumnHidingToggleCaption_settingWidgetToHeader_toggleCaptionStays() {
        toggleColumnHidable(1);
        getSidebarOpenButton().click();
        Assert.assertEquals("column 1", getGridElement().getHeaderCell(0, 1).getText().toLowerCase(Locale.ROOT));
        Assert.assertEquals("Column 1", getColumnHidingToggle(1).getText());
        selectMenuPath("Component", "Columns", "Column 1", "Header Type", "Widget Header");
        getSidebarOpenButton().click();
        Assert.assertEquals("Column 1", getColumnHidingToggle(1).getText());
    }

    @Test
    public void testColumnHidingToggleCaption_settingColumnHeaderCaption_toggleCaptionIsEqual() {
        toggleColumnHidable(1);
        selectMenuPath("Component", "Columns", "Column 1", "Change header caption");
        getSidebarOpenButton().click();
        Assert.assertEquals("column 1 header 0", getGridElement().getHeaderCell(0, 1).getText().toLowerCase(Locale.ROOT));
        Assert.assertEquals("Column 1 header 0", getColumnHidingToggle(1).getText());
    }

    @Test
    public void testColumnHidingToggleCaption_explicitlySet_toggleCaptionIsNotOverridden() {
        toggleColumnHidable(1);
        selectMenuPath("Component", "Columns", "Column 1", "Change hiding toggle caption");
        selectMenuPath("Component", "Columns", "Column 1", "Change header caption");
        getSidebarOpenButton().click();
        Assert.assertEquals("column 1 header 0", getGridElement().getHeaderCell(0, 1).getText().toLowerCase(Locale.ROOT));
        Assert.assertEquals("Column 1 caption 0", getColumnHidingToggle(1).getText());
    }

    @Test
    public void testFrozenColumnHiding_hiddenColumnMadeFrozen_frozenWhenMadeVisible() {
        selectMenuPath("Component", "Size", "Width", "100%");
        toggleColumnHidable(0);
        toggleColumnHidable(1);
        getSidebarOpenButton().click();
        getColumnHidingToggle(0).click();
        getColumnHidingToggle(1).click();
        assertColumnHeaderOrder(2, 3, 4, 5);
        setFrozenColumns(2);
        verifyColumnNotFrozen(0);
        verifyColumnNotFrozen(1);
        getSidebarOpenButton().click();
        getColumnHidingToggle(0).click();
        assertColumnHeaderOrder(0, 2, 3, 4, 5);
        verifyColumnFrozen(0);
        verifyColumnNotFrozen(1);
        getColumnHidingToggle(1).click();
        assertColumnHeaderOrder(0, 1, 2, 3, 4, 5);
        verifyColumnFrozen(0);
        verifyColumnFrozen(1);
        verifyColumnNotFrozen(2);
    }

    @Test
    public void testFrozenColumnHiding_hiddenFrozenColumnUnfrozen_notFrozenWhenMadeVisible() {
        selectMenuPath("Component", "Size", "Width", "100%");
        toggleColumnHidable(0);
        toggleColumnHidable(1);
        setFrozenColumns(2);
        verifyColumnFrozen(0);
        verifyColumnFrozen(1);
        verifyColumnNotFrozen(2);
        verifyColumnNotFrozen(3);
        getSidebarOpenButton().click();
        getColumnHidingToggle(0).click();
        getColumnHidingToggle(1).click();
        assertColumnHeaderOrder(2, 3, 4, 5);
        verifyColumnNotFrozen(0);
        verifyColumnNotFrozen(1);
        setFrozenColumns(0);
        verifyColumnNotFrozen(0);
        verifyColumnNotFrozen(1);
        getSidebarOpenButton().click();
        getColumnHidingToggle(0).click();
        assertColumnHeaderOrder(0, 2, 3, 4, 5);
        verifyColumnNotFrozen(0);
        verifyColumnNotFrozen(1);
        getColumnHidingToggle(1).click();
        assertColumnHeaderOrder(0, 1, 2, 3, 4, 5);
        verifyColumnNotFrozen(0);
        verifyColumnNotFrozen(1);
        verifyColumnNotFrozen(2);
    }

    @Test
    public void showColumnAndScrollbarWhenScrolledDownAndVisibleRowsChange() throws Exception {
        // Set a (un)suitable height
        selectMenuPath("Component", "Size", "HeightMode Row");
        selectMenuPath("Component", "Size", "Height by Rows", "4.33 rows");
        toggleAllColumnsHidable();
        // Hide all but the first 3
        getSidebarOpenButton().click();
        for (int i = 3; i < 12; i++) {
            getColumnHidingToggle(i).click();
        }
        getSidebarOpenButton().click();
        // Scroll all the way to the end
        $(GridElement.class).first().scrollToRow(999);
        // Show the fourth column
        getSidebarOpenButton().click();
        getColumnHidingToggle(3).click();
        // Make sure that the new column contains the data it should
        Assert.assertEquals("(999, 3)", getGridElement().getCell(999, 3).getText());
    }
}

