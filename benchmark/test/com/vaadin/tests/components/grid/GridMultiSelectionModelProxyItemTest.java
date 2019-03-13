package com.vaadin.tests.components.grid;


import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.MultiSelectionModel;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


public class GridMultiSelectionModelProxyItemTest {
    private List<String> data = IntStream.range(0, 100).boxed().map(( i) -> "String " + i).collect(Collectors.toList());

    private Grid<AtomicReference<String>> proxyGrid = new Grid();

    private MultiSelectionModel<AtomicReference<String>> model;

    private AtomicReference<Set<AtomicReference<String>>> selectionEvent = new AtomicReference<>();

    @Test
    public void testSelectAllWithProxyDataProvider() {
        model.selectAll();
        Assert.assertEquals("Item count mismatch on first select all", 100, getSelectionEvent().size());
        model.deselect(model.getFirstSelectedItem().orElseThrow(() -> new IllegalStateException("Items should be selected")));
        Assert.assertEquals("Item count mismatch on deselect", 99, getSelectionEvent().size());
        model.selectAll();
        Assert.assertEquals("Item count mismatch on second select all", 100, getSelectionEvent().size());
    }

    @Test
    public void testUpdateSelectionWithDuplicateEntries() {
        List<String> selection = data.stream().filter(( s) -> s.contains("1")).collect(Collectors.toList());
        model.updateSelection(selection.stream().map(AtomicReference::new).collect(Collectors.toSet()), Collections.emptySet());
        Assert.assertEquals("Failure in initial selection", selection.size(), getSelectionEvent().size());
        String toRemove = model.getFirstSelectedItem().map(AtomicReference::get).orElseThrow(() -> new IllegalStateException("Items should be selected"));
        model.updateSelection(Stream.of(toRemove).map(AtomicReference::new).collect(Collectors.toSet()), Stream.of(toRemove).map(AtomicReference::new).collect(Collectors.toSet()));
        Assert.assertNull("Selection should not change when selecting and deselecting once", selectionEvent.get());
        Set<AtomicReference<String>> added = new LinkedHashSet<>();
        Set<AtomicReference<String>> removed = new LinkedHashSet<>();
        for (int i = 0; i < 20; ++i) {
            added.add(new AtomicReference<>(toRemove));
            removed.add(new AtomicReference<>(toRemove));
        }
        model.updateSelection(added, removed);
        Assert.assertNull("Selection should not change when selecting and deselecting 20 times", selectionEvent.get());
        removed.add(new AtomicReference<>(toRemove));
        model.updateSelection(added, removed);
        Assert.assertEquals("Item should have been deselected", ((selection.size()) - 1), getSelectionEvent().size());
    }
}

