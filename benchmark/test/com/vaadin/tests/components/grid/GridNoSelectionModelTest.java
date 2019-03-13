package com.vaadin.tests.components.grid;


import SelectionMode.MULTI;
import SelectionMode.SINGLE;
import com.vaadin.data.provider.bov.Person;
import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.GridSelectionModel;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;


public class GridNoSelectionModelTest {
    public static final Person PERSON_C = new Person("c", 3);

    public static final Person PERSON_B = new Person("b", 2);

    public static final Person PERSON_A = new Person("a", 1);

    private Grid<Person> grid;

    private GridSelectionModel<Person> model;

    @Test
    public void select() {
        model.select(GridNoSelectionModelTest.PERSON_A);
        Assert.assertFalse(model.isSelected(GridNoSelectionModelTest.PERSON_A));
        Assert.assertEquals(0, model.getSelectedItems().size());
        Assert.assertEquals(Optional.empty(), model.getFirstSelectedItem());
        model.select(GridNoSelectionModelTest.PERSON_B);
        Assert.assertFalse(model.isSelected(GridNoSelectionModelTest.PERSON_B));
        Assert.assertEquals(0, model.getSelectedItems().size());
        Assert.assertEquals(Optional.empty(), model.getFirstSelectedItem());
    }

    @Test
    public void changingToSingleSelectionModel() {
        grid.setSelectionMode(SINGLE);
        grid.getSelectionModel().select(GridNoSelectionModelTest.PERSON_B);
        Assert.assertEquals(GridNoSelectionModelTest.PERSON_B, grid.getSelectionModel().getFirstSelectedItem().get());
    }

    @Test
    public void changingToMultiSelectionModel() {
        grid.setSelectionMode(MULTI);
        grid.getSelectionModel().select(GridNoSelectionModelTest.PERSON_B);
        Assert.assertEquals(new LinkedHashSet(Arrays.asList(GridNoSelectionModelTest.PERSON_B)), grid.getSelectionModel().getSelectedItems());
    }
}

