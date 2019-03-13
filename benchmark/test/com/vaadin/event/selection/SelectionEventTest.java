package com.vaadin.event.selection;


import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Vaadin Ltd
 */
public class SelectionEventTest {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void getFirstSelected_mutliSelectEvent() {
        MultiSelectionEvent<?> event = Mockito.mock(MultiSelectionEvent.class);
        Mockito.doCallRealMethod().when(event).getFirstSelectedItem();
        Mockito.when(event.getValue()).thenReturn(new LinkedHashSet(Arrays.asList("foo", "bar")));
        Optional<?> selected = event.getFirstSelectedItem();
        Mockito.verify(event).getValue();
        Assert.assertEquals("foo", selected.get());
        Mockito.when(event.getValue()).thenReturn(Collections.emptySet());
        Assert.assertFalse(event.getFirstSelectedItem().isPresent());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void getFirstSelected_singleSelectEvent() {
        SingleSelectionEvent event = Mockito.mock(SingleSelectionEvent.class);
        Mockito.doCallRealMethod().when(event).getFirstSelectedItem();
        Mockito.when(event.getSelectedItem()).thenReturn(Optional.of("foo"));
        Optional<?> selected = event.getSelectedItem();
        Mockito.verify(event).getSelectedItem();
        Assert.assertEquals("foo", selected.get());
        Mockito.when(event.getSelectedItem()).thenReturn(Optional.empty());
        Assert.assertFalse(event.getFirstSelectedItem().isPresent());
    }
}

