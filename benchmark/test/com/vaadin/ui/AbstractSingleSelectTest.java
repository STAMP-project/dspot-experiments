package com.vaadin.ui;


import com.vaadin.data.HasDataProvider;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.bov.Person;
import com.vaadin.event.selection.SingleSelectionListener;
import com.vaadin.shared.Registration;
import com.vaadin.shared.data.DataCommunicatorClientRpc;
import com.vaadin.ui.declarative.DesignContext;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;


/**
 * Test for {@link AbstractSingleSelect} and {@link AbstractSingleSelection}
 *
 * @author Vaadin Ltd
 */
public class AbstractSingleSelectTest {
    private List<Person> selectionChanges;

    private List<Person> oldSelections;

    private static class PersonListing extends AbstractSingleSelect<Person> implements HasDataProvider<Person> {
        @Override
        protected Element writeItem(Element design, Person item, DesignContext context) {
            return null;
        }

        @Override
        protected void readItems(Element design, DesignContext context) {
        }

        @Override
        public DataProvider<Person, ?> getDataProvider() {
            return internalGetDataProvider();
        }

        @Override
        public void setDataProvider(DataProvider<Person, ?> dataProvider) {
            internalSetDataProvider(dataProvider);
        }
    }

    public static final Person PERSON_C = new Person("c", 3);

    public static final Person PERSON_B = new Person("b", 2);

    public static final Person PERSON_A = new Person("a", 1);

    public static final String RPC_INTERFACE = DataCommunicatorClientRpc.class.getName();

    private AbstractSingleSelectTest.PersonListing listing;

    @Test
    public void select() {
        listing.setValue(AbstractSingleSelectTest.PERSON_B);
        Assert.assertTrue(getSelectedItem().isPresent());
        Assert.assertEquals(AbstractSingleSelectTest.PERSON_B, getSelectedItem().orElse(null));
        Assert.assertFalse(isSelected(AbstractSingleSelectTest.PERSON_A));
        Assert.assertTrue(isSelected(AbstractSingleSelectTest.PERSON_B));
        Assert.assertFalse(isSelected(AbstractSingleSelectTest.PERSON_C));
        Assert.assertEquals(Optional.of(AbstractSingleSelectTest.PERSON_B), getSelectedItem());
        Assert.assertEquals(Arrays.asList(AbstractSingleSelectTest.PERSON_B), selectionChanges);
        verifyValueChanges();
    }

    @Test
    public void selectDeselect() {
        listing.setValue(AbstractSingleSelectTest.PERSON_B);
        listing.setValue(null);
        Assert.assertFalse(getSelectedItem().isPresent());
        Assert.assertFalse(isSelected(AbstractSingleSelectTest.PERSON_A));
        Assert.assertFalse(isSelected(AbstractSingleSelectTest.PERSON_B));
        Assert.assertFalse(isSelected(AbstractSingleSelectTest.PERSON_C));
        Assert.assertFalse(getSelectedItem().isPresent());
        Assert.assertEquals(Arrays.asList(AbstractSingleSelectTest.PERSON_B, null), selectionChanges);
        verifyValueChanges();
    }

    @Test
    public void reselect() {
        listing.setValue(AbstractSingleSelectTest.PERSON_B);
        listing.setValue(AbstractSingleSelectTest.PERSON_C);
        Assert.assertEquals(AbstractSingleSelectTest.PERSON_C, getSelectedItem().orElse(null));
        Assert.assertFalse(isSelected(AbstractSingleSelectTest.PERSON_A));
        Assert.assertFalse(isSelected(AbstractSingleSelectTest.PERSON_B));
        Assert.assertTrue(isSelected(AbstractSingleSelectTest.PERSON_C));
        Assert.assertEquals(Optional.of(AbstractSingleSelectTest.PERSON_C), getSelectedItem());
        Assert.assertEquals(Arrays.asList(AbstractSingleSelectTest.PERSON_B, AbstractSingleSelectTest.PERSON_C), selectionChanges);
        verifyValueChanges();
    }

    @Test
    public void selectTwice() {
        listing.setValue(AbstractSingleSelectTest.PERSON_C);
        listing.setValue(AbstractSingleSelectTest.PERSON_C);
        Assert.assertEquals(AbstractSingleSelectTest.PERSON_C, getSelectedItem().orElse(null));
        Assert.assertFalse(isSelected(AbstractSingleSelectTest.PERSON_A));
        Assert.assertFalse(isSelected(AbstractSingleSelectTest.PERSON_B));
        Assert.assertTrue(isSelected(AbstractSingleSelectTest.PERSON_C));
        Assert.assertEquals(Optional.of(AbstractSingleSelectTest.PERSON_C), getSelectedItem());
        Assert.assertEquals(Arrays.asList(AbstractSingleSelectTest.PERSON_C), selectionChanges);
        verifyValueChanges();
    }

    @Test
    public void deselectTwice() {
        listing.setValue(AbstractSingleSelectTest.PERSON_C);
        listing.setValue(null);
        listing.setValue(null);
        Assert.assertFalse(getSelectedItem().isPresent());
        Assert.assertFalse(isSelected(AbstractSingleSelectTest.PERSON_A));
        Assert.assertFalse(isSelected(AbstractSingleSelectTest.PERSON_B));
        Assert.assertFalse(isSelected(AbstractSingleSelectTest.PERSON_C));
        Assert.assertFalse(getSelectedItem().isPresent());
        Assert.assertEquals(Arrays.asList(AbstractSingleSelectTest.PERSON_C, null), selectionChanges);
        verifyValueChanges();
    }

    @Test
    public void getValue() {
        setSelectedItem(AbstractSingleSelectTest.PERSON_B);
        Assert.assertEquals(AbstractSingleSelectTest.PERSON_B, listing.getValue());
        listing.setValue(null);
        Assert.assertNull(listing.getValue());
        verifyValueChanges();
    }

    @Test
    @SuppressWarnings({ "rawtypes" })
    public void getValue_isDelegatedTo_getSelectedItem() {
        AbstractSingleSelect select = Mockito.mock(AbstractSingleSelect.class);
        Optional selected = Optional.of(new Object());
        Mockito.when(select.getSelectedItem()).thenReturn(selected);
        Mockito.doCallRealMethod().when(select).getValue();
        Assert.assertSame(selected.get(), select.getValue());
        selected = Optional.empty();
        Mockito.when(select.getSelectedItem()).thenReturn(selected);
        Assert.assertNull(select.getValue());
    }

    @Test
    public void setValue() {
        listing.setValue(AbstractSingleSelectTest.PERSON_C);
        Assert.assertEquals(AbstractSingleSelectTest.PERSON_C, getSelectedItem().get());
        listing.setValue(null);
        Assert.assertFalse(getSelectedItem().isPresent());
        verifyValueChanges();
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void setValue_isDelegatedTo_setSelectedItem() {
        AbstractSingleSelect select = Mockito.mock(AbstractSingleSelect.class);
        Mockito.doCallRealMethod().when(select).setValue(any());
        Object value = new Object();
        select.setValue(value);
        Mockito.verify(select).setSelectedItem(value);
        select.setValue(null);
        Mockito.verify(select).setSelectedItem(null);
    }

    @SuppressWarnings("serial")
    @Test
    public void addValueChangeListener() {
        AtomicReference<SingleSelectionListener<String>> selectionListener = new AtomicReference<>();
        Registration registration = Mockito.mock(Registration.class);
        String value = "foo";
        AbstractSingleSelect<String> select = new AbstractSingleSelect<String>() {
            @Override
            public Registration addSelectionListener(SingleSelectionListener<String> listener) {
                selectionListener.set(listener);
                return registration;
            }

            @Override
            public String getValue() {
                return value;
            }

            @Override
            protected Element writeItem(Element design, String item, DesignContext context) {
                return null;
            }

            @Override
            protected void readItems(Element design, DesignContext context) {
            }

            @Override
            public void setItems(Collection<String> items) {
                throw new UnsupportedOperationException("Not needed in this test");
            }

            @Override
            public DataProvider<String, ?> getDataProvider() {
                return null;
            }
        };
        AtomicReference<ValueChangeEvent<?>> event = new AtomicReference<>();
        Registration actualRegistration = select.addValueChangeListener(( evt) -> {
            assertNull(event.get());
            event.set(evt);
        });
        Assert.assertSame(registration, actualRegistration);
        selectionListener.get().selectionChange(new com.vaadin.event.selection.SingleSelectionEvent(select, value, true));
        Assert.assertEquals(select, event.get().getComponent());
        Assert.assertEquals(value, event.get().getOldValue());
        Assert.assertEquals(value, event.get().getValue());
        Assert.assertTrue(event.get().isUserOriginated());
    }
}

