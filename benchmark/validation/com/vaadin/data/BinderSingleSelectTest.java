package com.vaadin.data;


import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.ui.NativeSelect;
import org.junit.Assert;
import org.junit.Test;


public class BinderSingleSelectTest extends BinderTestBase<Binder<Person>, Person> {
    private NativeSelect<Sex> select;

    @Test
    public void personBound_bindSelectByShortcut_selectionUpdated() {
        item.setSex(Sex.FEMALE);
        binder.setBean(item);
        binder.bind(select, Person::getSex, Person::setSex);
        Assert.assertSame(Sex.FEMALE, select.getSelectedItem().orElse(null));
    }

    @Test
    public void personBound_bindSelect_selectionUpdated() {
        item.setSex(Sex.MALE);
        binder.setBean(item);
        binder.forField(select).bind(Person::getSex, Person::setSex);
        Assert.assertSame(Sex.MALE, select.getSelectedItem().orElse(null));
    }

    @Test
    public void selectBound_bindPersonWithNullSex_selectedItemNotPresent() {
        bindSex();
        Assert.assertFalse(select.getSelectedItem().isPresent());
    }

    @Test
    public void selectBound_bindPerson_selectionUpdated() {
        item.setSex(Sex.FEMALE);
        bindSex();
        Assert.assertSame(Sex.FEMALE, select.getSelectedItem().orElse(null));
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
}

