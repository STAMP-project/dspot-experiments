package com.vaadin.v7.data.fieldgroup;


import FieldGroup.BindException;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.Property.Transactional;
import com.vaadin.v7.data.util.TransactionalPropertyWrapper;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class FieldGroupTest {
    private FieldGroup sut;

    private Field field;

    @Test
    public void fieldIsBound() {
        sut.bind(field, "foobar");
        MatcherAssert.assertThat(sut.getField("foobar"), Is.is(field));
    }

    @Test(expected = BindException.class)
    public void cannotBindToAlreadyBoundProperty() {
        sut.bind(field, "foobar");
        sut.bind(Mockito.mock(Field.class), "foobar");
    }

    @Test(expected = BindException.class)
    public void cannotBindNullField() {
        sut.bind(null, "foobar");
    }

    @Test
    public void wrapInTransactionalProperty_provideCustomImpl_customTransactionalWrapperIsUsed() {
        FieldGroupTest.Bean bean = new FieldGroupTest.Bean();
        FieldGroup group = new FieldGroup() {
            @Override
            protected <T> Transactional<T> wrapInTransactionalProperty(Property<T> itemProperty) {
                return new FieldGroupTest.TransactionalPropertyImpl(itemProperty);
            }
        };
        group.setItemDataSource(new com.vaadin.v7.data.util.BeanItem<FieldGroupTest.Bean>(bean));
        TextField field = new TextField();
        group.bind(field, "name");
        Property propertyDataSource = field.getPropertyDataSource();
        Assert.assertTrue(("Custom implementation of transactional property " + "has not been used"), (propertyDataSource instanceof FieldGroupTest.TransactionalPropertyImpl));
    }

    public static class TransactionalPropertyImpl<T> extends TransactionalPropertyWrapper<T> {
        public TransactionalPropertyImpl(Property<T> wrappedProperty) {
            super(wrappedProperty);
        }
    }

    public static class Bean {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

