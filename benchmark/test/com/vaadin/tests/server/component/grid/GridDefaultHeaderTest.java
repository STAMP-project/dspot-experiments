package com.vaadin.tests.server.component.grid;


import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;
import org.junit.Assert;
import org.junit.Test;


public class GridDefaultHeaderTest {
    private Grid<String> grid;

    private Column<?, ?> column1;

    private Column<?, ?> column2;

    @Test
    public void initialState_hasDefaultHeader() {
        HeaderRow defaultHeader = grid.getDefaultHeaderRow();
        Assert.assertEquals(1, grid.getHeaderRowCount());
        Assert.assertSame(grid.getHeaderRow(0), defaultHeader);
        Assert.assertEquals("First", defaultHeader.getCell(column1).getText());
        Assert.assertEquals("Second", defaultHeader.getCell(column2).getText());
    }

    @Test
    public void initialState_defaultHeaderRemovable() {
        grid.removeHeaderRow(0);
        Assert.assertEquals(0, grid.getHeaderRowCount());
        Assert.assertNull(grid.getDefaultHeaderRow());
    }

    @Test
    public void initialState_updateColumnCaption_defaultHeaderUpdated() {
        column1.setCaption("1st");
        Assert.assertEquals("1st", grid.getDefaultHeaderRow().getCell(column1).getText());
    }

    @Test
    public void customDefaultHeader_updateColumnCaption_defaultHeaderUpdated() {
        grid.setDefaultHeaderRow(grid.appendHeaderRow());
        column1.setCaption("1st");
        Assert.assertEquals("1st", grid.getDefaultHeaderRow().getCell(column1).getText());
        Assert.assertEquals("First", grid.getHeaderRow(0).getCell(column1).getText());
    }

    @Test
    public void noDefaultRow_updateColumnCaption_headerNotUpdated() {
        grid.setDefaultHeaderRow(null);
        column1.setCaption("1st");
        Assert.assertEquals("First", grid.getHeaderRow(0).getCell(column1).getText());
    }

    @Test
    public void updateDefaultRow_columnCaptionUpdated() {
        grid.getDefaultHeaderRow().getCell(column1).setText("new");
        Assert.assertEquals("new", column1.getCaption());
        Assert.assertEquals("Second", column2.getCaption());
    }

    @Test
    public void updateDefaultRowWithMergedCell_columnCaptionNotUpdated() {
        HeaderCell merged = grid.getDefaultHeaderRow().join(column1, column2);
        merged.setText("new");
        Assert.assertEquals("First", column1.getCaption());
        Assert.assertEquals("Second", column2.getCaption());
    }

    @Test
    public void updateColumnCaption_defaultRowWithMergedCellNotUpdated() {
        HeaderCell merged = grid.getDefaultHeaderRow().join(column1, column2);
        merged.setText("new");
        column1.setCaption("foo");
        column2.setCaption("bar");
        Assert.assertEquals("new", merged.getText());
    }
}

