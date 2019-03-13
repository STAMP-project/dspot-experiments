package com.vaadin.ui;


import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.event.selection.MultiSelectionListener;
import com.vaadin.shared.Registration;
import com.vaadin.shared.data.selection.MultiSelectServerRpc;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.anySet;


@RunWith(Parameterized.class)
public class AbstractMultiSelectTest<S extends AbstractMultiSelect<String>> {
    @Parameterized.Parameter
    public S selectToTest;

    private MultiSelectServerRpc rpc;

    private Registration registration;

    private List<Set<String>> values;

    private List<Set<String>> oldValues;

    @Test
    public void stableSelectionOrder() {
        select("1");
        select("2");
        select("3");
        assertSelectionOrder("1", "2", "3");
        selectToTest.deselect("1");
        assertSelectionOrder("2", "3");
        select("1");
        assertSelectionOrder("2", "3", "1");
        selectToTest.select("7", "8", "4");
        assertSelectionOrder("2", "3", "1", "7", "8", "4");
        selectToTest.deselect("2", "1", "4", "5");
        assertSelectionOrder("3", "7", "8");
        updateSelection(new LinkedHashSet(Arrays.asList("5", "2")), new LinkedHashSet(Arrays.asList("3", "8")));
        assertSelectionOrder("7", "5", "2");
        verifyValueChangeEvents();
    }

    @Test
    public void apiSelectionChange_notUserOriginated() {
        AtomicInteger listenerCount = new AtomicInteger(0);
        listenerCount.set(0);
        registration = addSelectionListener(( event) -> {
            listenerCount.incrementAndGet();
            assertFalse(event.isUserOriginated());
        });
        select("1");
        select("2");
        selectToTest.deselect("2");
        deselectAll();
        selectToTest.select("2", "3", "4");
        deselect("1", "4");
        Assert.assertEquals(6, listenerCount.get());
        // select partly selected
        selectToTest.select("2", "3", "4");
        Assert.assertEquals(7, listenerCount.get());
        // select completely selected
        selectToTest.select("2", "3", "4");
        Assert.assertEquals(7, listenerCount.get());
        // deselect partly not selected
        selectToTest.select("1", "4");
        Assert.assertEquals(8, listenerCount.get());
        // deselect completely not selected
        selectToTest.select("1", "4");
        Assert.assertEquals(8, listenerCount.get());
        verifyValueChangeEvents();
    }

    @Test
    public void rpcSelectionChange_userOriginated() {
        AtomicInteger listenerCount = new AtomicInteger(0);
        registration = addSelectionListener(( event) -> {
            listenerCount.incrementAndGet();
            assertTrue(event.isUserOriginated());
        });
        rpcSelect("1");
        assertSelectionOrder("1");
        rpcSelect("2");
        assertSelectionOrder("1", "2");
        rpcDeselectItems("2");
        assertSelectionOrder("1");
        rpcSelect("3", "6");
        assertSelectionOrder("1", "3", "6");
        rpcDeselectItems("1", "3");
        assertSelectionOrder("6");
        Assert.assertEquals(5, listenerCount.get());
        // select partly selected
        rpcSelect("2", "3", "4");
        Assert.assertEquals(6, listenerCount.get());
        assertSelectionOrder("6", "2", "3", "4");
        // select completely selected
        rpcSelect("2", "3", "4");
        Assert.assertEquals(6, listenerCount.get());
        assertSelectionOrder("6", "2", "3", "4");
        // deselect partly not selected
        rpcDeselectItems("1", "4");
        Assert.assertEquals(7, listenerCount.get());
        assertSelectionOrder("6", "2", "3");
        // deselect completely not selected
        rpcDeselectItems("1", "4");
        Assert.assertEquals(7, listenerCount.get());
        assertSelectionOrder("6", "2", "3");
        // select completely selected and deselect completely not selected
        rpcUpdateSelection(new String[]{ "3" }, new String[]{ "1", "4" });
        Assert.assertEquals(7, listenerCount.get());
        assertSelectionOrder("6", "2", "3");
        // select partly selected and deselect completely not selected
        rpcUpdateSelection(new String[]{ "4", "2" }, new String[]{ "1", "8" });
        Assert.assertEquals(8, listenerCount.get());
        assertSelectionOrder("6", "2", "3", "4");
        // select completely selected and deselect partly not selected
        rpcUpdateSelection(new String[]{ "4", "3" }, new String[]{ "1", "2" });
        Assert.assertEquals(9, listenerCount.get());
        assertSelectionOrder("6", "3", "4");
        // duplicate case - ignored
        rpcUpdateSelection(new String[]{ "2" }, new String[]{ "2" });
        Assert.assertEquals(9, listenerCount.get());
        assertSelectionOrder("6", "3", "4");
        // duplicate case - duplicate removed
        rpcUpdateSelection(new String[]{ "2" }, new String[]{ "2", "3" });
        Assert.assertEquals(10, listenerCount.get());
        assertSelectionOrder("6", "4");
        // duplicate case - duplicate removed
        rpcUpdateSelection(new String[]{ "6", "8" }, new String[]{ "6" });
        Assert.assertEquals(11, listenerCount.get());
        assertSelectionOrder("6", "4", "8");
        verifyValueChangeEvents();
    }

    @Test
    public void getValue() {
        select("1");
        Assert.assertEquals(Collections.singleton("1"), selectToTest.getValue());
        deselectAll();
        LinkedHashSet<String> set = new LinkedHashSet<>();
        set.add("1");
        set.add("5");
        selectToTest.select(set.toArray(new String[2]));
        Assert.assertEquals(set, selectToTest.getValue());
        set.add("3");
        select("3");
        Assert.assertEquals(set, selectToTest.getValue());
        verifyValueChangeEvents();
    }

    @Test
    @SuppressWarnings({ "serial", "unchecked" })
    public void getValue_isDelegatedTo_getSelectedItems() {
        Set<String> set = Mockito.mock(Set.class);
        AbstractMultiSelect<String> select = new AbstractMultiSelect<String>() {
            @Override
            public Set<String> getSelectedItems() {
                return set;
            }

            @Override
            public void setItems(Collection<String> items) {
                throw new UnsupportedOperationException("Not implemented for this test");
            }

            @Override
            public DataProvider<String, ?> getDataProvider() {
                return null;
            }
        };
        Assert.assertSame(set, select.getValue());
        verifyValueChangeEvents();
    }

    @Test
    public void setValue() {
        selectToTest.setValue(Collections.singleton("1"));
        Assert.assertEquals(Collections.singleton("1"), getSelectedItems());
        Set<String> set = new LinkedHashSet<>();
        set.add("4");
        set.add("3");
        selectToTest.setValue(set);
        Assert.assertEquals(set, getSelectedItems());
        verifyValueChangeEvents();
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes", "serial" })
    public void setValue_isDelegatedToDeselectAndUpdateSelection() {
        AbstractMultiSelect<String> select = Mockito.mock(AbstractMultiSelect.class);
        Set set = new LinkedHashSet<>();
        set.add("foo1");
        set.add("foo");
        Set selected = new LinkedHashSet<>();
        selected.add("bar1");
        selected.add("bar");
        selected.add("bar2");
        Mockito.when(select.getSelectedItems()).thenReturn(selected);
        Mockito.doCallRealMethod().when(select).setValue(anySet());
        select.setValue(set);
        Mockito.verify(select).updateSelection(set, selected);
    }

    @SuppressWarnings({ "unchecked", "serial" })
    @Test
    public void addValueChangeListener() {
        AtomicReference<MultiSelectionListener<String>> selectionListener = new AtomicReference<>();
        Registration registration = Mockito.mock(Registration.class);
        Set<String> set = new HashSet<>();
        set.add("foo");
        set.add("bar");
        AbstractMultiSelect<String> select = new AbstractMultiSelect<String>() {
            @Override
            public Registration addSelectionListener(MultiSelectionListener<String> listener) {
                selectionListener.set(listener);
                return registration;
            }

            @Override
            public Set<String> getValue() {
                return set;
            }

            @Override
            public void setItems(Collection<String> items) {
                throw new UnsupportedOperationException("Not implemented for this test");
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
        selectionListener.get().selectionChange(new com.vaadin.event.selection.MultiSelectionEvent(select, Mockito.mock(Set.class), true));
        Assert.assertEquals(select, event.get().getComponent());
        Assert.assertEquals(set, event.get().getValue());
        Assert.assertTrue(event.get().isUserOriginated());
    }
}

