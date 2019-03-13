package com.vaadin.tests.server.component.grid;


import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.components.grid.FooterCell;
import com.vaadin.ui.components.grid.HeaderCell;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;


public class GridChildrenTest {
    private Grid<Person> grid;

    @Test
    public void iteratorFindsComponentsInMergedHeader() {
        HeaderCell merged = grid.getDefaultHeaderRow().join("foo", "bar", "baz");
        Label label = new Label();
        merged.setComponent(label);
        Iterator<Component> i = grid.iterator();
        Assert.assertEquals(label, i.next());
        Assert.assertFalse(i.hasNext());
    }

    @Test
    public void removeComponentInMergedHeaderCell() {
        HeaderCell merged = grid.getDefaultHeaderRow().join("foo", "bar", "baz");
        Label label = new Label();
        merged.setComponent(label);
        Assert.assertEquals(grid, label.getParent());
        merged.setText("foo");
        Assert.assertNull(label.getParent());
    }

    @Test
    public void removeHeaderWithComponentInMergedHeaderCell() {
        HeaderCell merged = grid.getDefaultHeaderRow().join("foo", "bar", "baz");
        Label label = new Label();
        merged.setComponent(label);
        Assert.assertEquals(grid, label.getParent());
        grid.removeHeaderRow(0);
        Assert.assertNull(label.getParent());
    }

    @Test
    public void removeComponentInMergedFooterCell() {
        FooterCell merged = grid.addFooterRowAt(0).join("foo", "bar", "baz");
        Label label = new Label();
        merged.setComponent(label);
        Assert.assertEquals(grid, label.getParent());
        merged.setText("foo");
        Assert.assertNull(label.getParent());
    }

    @Test
    public void removeFooterWithComponentInMergedFooterCell() {
        FooterCell merged = grid.addFooterRowAt(0).join("foo", "bar", "baz");
        Label label = new Label();
        merged.setComponent(label);
        Assert.assertEquals(grid, label.getParent());
        grid.removeFooterRow(0);
        Assert.assertNull(label.getParent());
    }

    @Test
    public void componentsInMergedFooter() {
        FooterCell merged = grid.addFooterRowAt(0).join("foo", "bar", "baz");
        Label label = new Label();
        merged.setComponent(label);
        Iterator<Component> i = grid.iterator();
        Assert.assertEquals(label, i.next());
        Assert.assertFalse(i.hasNext());
        Assert.assertEquals(grid, label.getParent());
    }
}

