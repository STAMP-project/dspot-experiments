package com.vaadin.data;


import SelectionMode.MULTI;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.ui.Grid;
import com.vaadin.ui.SingleSelect;
import com.vaadin.ui.components.grid.SingleSelectionModelImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class GridAsSingleSelectInBinderTest extends BinderTestBase<Binder<Person>, Person> {
    private class GridWithCustomSingleSelectionModel extends Grid<Sex> {
        @Override
        public void setSelectionModel(com.vaadin.ui.components.grid.GridSelectionModel<Sex> model) {
            super.setSelectionModel(model);
        }
    }

    private class CustomSingleSelectModel extends SingleSelectionModelImpl<Sex> {
        public void setSelectedFromClient(Sex item) {
            setSelectedFromClient(getGrid().getDataCommunicator().getKeyMapper().key(item));
        }
    }

    private Grid<Sex> grid;

    private SingleSelect<Sex> select;

    @Test(expected = IllegalStateException.class)
    public void boundGridInBinder_selectionModelChanged_throws() {
        grid.setSelectionMode(MULTI);
        select.setValue(Sex.MALE);
    }

    @Test
    public void personBound_bindSelectByShortcut_selectionUpdated() {
        item.setSex(Sex.FEMALE);
        binder.setBean(item);
        binder.bind(select, Person::getSex, Person::setSex);
        Assert.assertSame(Sex.FEMALE, select.getValue());
    }

    @Test
    public void personBound_bindSelect_selectionUpdated() {
        item.setSex(Sex.MALE);
        binder.setBean(item);
        binder.forField(select).bind(Person::getSex, Person::setSex);
        Assert.assertSame(Sex.MALE, select.getValue());
    }

    @Test
    public void selectBound_bindPersonWithNullSex_selectedItemNotPresent() {
        bindSex();
        Assert.assertFalse(((select.getValue()) != null));
    }

    @Test
    public void selectBound_bindPerson_selectionUpdated() {
        item.setSex(Sex.FEMALE);
        bindSex();
        Assert.assertSame(Sex.FEMALE, select.getValue());
    }

    @Test
    public void bound_setSelection_beanValueUpdated() {
        bindSex();
        select.setValue(Sex.MALE);
        Assert.assertSame(Sex.MALE, item.getSex());
    }

    @Test
    public void bound_deselect_beanValueUpdatedToNull() {
        item.setSex(Sex.MALE);
        bindSex();
        select.setValue(null);
        Assert.assertNull(item.getSex());
    }

    @Test
    public void unbound_changeSelection_beanValueNotUpdated() {
        item.setSex(Sex.UNKNOWN);
        bindSex();
        binder.removeBean();
        select.setValue(Sex.FEMALE);
        Assert.assertSame(Sex.UNKNOWN, item.getSex());
    }

    @Test
    public void addValueChangeListener_selectionUpdated_eventTriggeredForSelect() {
        GridAsSingleSelectInBinderTest.GridWithCustomSingleSelectionModel grid = new GridAsSingleSelectInBinderTest.GridWithCustomSingleSelectionModel();
        GridAsSingleSelectInBinderTest.CustomSingleSelectModel model = new GridAsSingleSelectInBinderTest.CustomSingleSelectModel();
        setSelectionModel(model);
        setItems(Sex.values());
        select = asSingleSelect();
        List<Sex> selected = new ArrayList<>();
        List<Sex> oldSelected = new ArrayList<>();
        List<Boolean> userOriginated = new ArrayList<>();
        select.addValueChangeListener(( event) -> {
            selected.add(event.getValue());
            oldSelected.add(event.getOldValue());
            userOriginated.add(event.isUserOriginated());
            assertSame(grid, event.getComponent());
            // cannot compare that the event source is the select since a new
            // SingleSelect wrapper object has been created for the event
            assertSame(select.getValue(), event.getValue());
        });
        getSelectionModel().select(Sex.UNKNOWN);
        // simulates client side selection
        model.setSelectedFromClient(Sex.MALE);
        getSelectionModel().select(Sex.MALE);// NOOP

        getSelectionModel().deselect(Sex.UNKNOWN);// NOOP

        // simulates deselect from client side
        model.setSelectedFromClient(null);
        getSelectionModel().select(Sex.FEMALE);
        Assert.assertEquals(Arrays.asList(Sex.UNKNOWN, Sex.MALE, null, Sex.FEMALE), selected);
        Assert.assertEquals(Arrays.asList(null, Sex.UNKNOWN, Sex.MALE, null), oldSelected);
        Assert.assertEquals(Arrays.asList(false, true, true, false), userOriginated);
    }
}

