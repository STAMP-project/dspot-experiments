package com.vaadin.tests.components.grid;


import SelectAllCheckBoxVisibility.DEFAULT;
import SelectAllCheckBoxVisibility.HIDDEN;
import SelectAllCheckBoxVisibility.VISIBLE;
import SelectionMode.MULTI;
import SelectionMode.SINGLE;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.bov.Person;
import com.vaadin.event.selection.MultiSelectionEvent;
import com.vaadin.event.selection.MultiSelectionListener;
import com.vaadin.shared.Registration;
import com.vaadin.tests.util.MockUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;
import com.vaadin.ui.components.grid.GridSelectionModel;
import com.vaadin.ui.components.grid.MultiSelectionModel;
import com.vaadin.ui.components.grid.MultiSelectionModelImpl;
import elemental.json.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;
import org.easymock.Capture;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class GridMultiSelectionModelTest {
    public static final Person PERSON_C = new Person("c", 3);

    public static final Person PERSON_B = new Person("b", 2);

    public static final Person PERSON_A = new Person("a", 1);

    private Grid<Person> grid;

    private MultiSelectionModelImpl<Person> selectionModel;

    private Capture<List<Person>> currentSelectionCapture;

    private Capture<List<Person>> oldSelectionCapture;

    private AtomicInteger events;

    public static class CustomMultiSelectionModel extends MultiSelectionModelImpl<String> {
        public final Map<String, Boolean> generatedData = new LinkedHashMap<>();

        @Override
        public void generateData(String item, JsonObject jsonObject) {
            super.generateData(item, jsonObject);
            // capture updated row
            generatedData.put(item, isSelected(item));
        }
    }

    public static class CustomSelectionModelGrid extends Grid<String> {
        public CustomSelectionModelGrid() {
            this(new GridMultiSelectionModelTest.CustomMultiSelectionModel());
        }

        public CustomSelectionModelGrid(GridSelectionModel<String> selectionModel) {
            super();
            setSelectionModel(selectionModel);
        }
    }

    private static class TestMultiSelectionModel extends MultiSelectionModelImpl<Object> {
        public TestMultiSelectionModel() {
            getState(false).selectionAllowed = false;
        }

        @Override
        protected void updateSelection(Set<Object> addedItems, Set<Object> removedItems, boolean userOriginated) {
            super.updateSelection(addedItems, removedItems, userOriginated);
        }
    }

    @Test(expected = IllegalStateException.class)
    public void throwExcpetionWhenSelectionIsDisallowed() {
        GridMultiSelectionModelTest.TestMultiSelectionModel model = new GridMultiSelectionModelTest.TestMultiSelectionModel();
        model.updateSelection(Collections.emptySet(), Collections.emptySet(), true);
    }

    @Test(expected = IllegalStateException.class)
    public void selectionModelChanged_usingPreviousSelectionModel_throws() {
        grid.setSelectionMode(SINGLE);
        selectionModel.select(GridMultiSelectionModelTest.PERSON_A);
    }

    @Test
    public void changingSelectionModel_firesSelectionEvent() {
        Grid<String> customGrid = new Grid();
        customGrid.setSelectionMode(MULTI);
        customGrid.setItems("Foo", "Bar", "Baz");
        List<String> selectionChanges = new ArrayList<>();
        Capture<List<String>> oldSelectionCapture = new Capture();
        ((MultiSelectionModelImpl<String>) (customGrid.getSelectionModel())).addMultiSelectionListener(( event) -> {
            selectionChanges.addAll(event.getValue());
            oldSelectionCapture.setValue(new ArrayList<>(event.getOldSelection()));
        });
        customGrid.getSelectionModel().select("Foo");
        Assert.assertEquals(Arrays.asList("Foo"), selectionChanges);
        selectionChanges.clear();
        customGrid.getSelectionModel().select("Bar");
        Assert.assertEquals("Foo", customGrid.getSelectionModel().getFirstSelectedItem().get());
        Assert.assertEquals(Arrays.asList("Foo", "Bar"), selectionChanges);
        selectionChanges.clear();
        customGrid.setSelectionMode(SINGLE);
        Assert.assertFalse(customGrid.getSelectionModel().getFirstSelectedItem().isPresent());
        Assert.assertEquals(Arrays.asList(), selectionChanges);
        Assert.assertEquals(Arrays.asList("Foo", "Bar"), oldSelectionCapture.getValue());
    }

    @Test
    public void serverSideSelection_GridChangingSelectionModel_sendsUpdatedRowsToClient() {
        Grid<String> customGrid = new GridMultiSelectionModelTest.CustomSelectionModelGrid();
        GridMultiSelectionModelTest.CustomMultiSelectionModel customModel = ((GridMultiSelectionModelTest.CustomMultiSelectionModel) (customGrid.getSelectionModel()));
        customGrid.setItems("Foo", "Bar", "Baz");
        customGrid.getDataCommunicator().beforeClientResponse(true);
        Assert.assertFalse("Item should have been updated as selected", customModel.generatedData.get("Foo"));
        Assert.assertFalse("Item should have been updated as NOT selected", customModel.generatedData.get("Bar"));
        Assert.assertFalse("Item should have been updated as NOT selected", customModel.generatedData.get("Baz"));
        customModel.generatedData.clear();
        customGrid.getSelectionModel().select("Foo");
        customGrid.getDataCommunicator().beforeClientResponse(false);
        Assert.assertTrue("Item should have been updated as selected", customModel.generatedData.get("Foo"));
        Assert.assertFalse("Item should have NOT been updated", customModel.generatedData.containsKey("Bar"));
        Assert.assertFalse("Item should have NOT been updated", customModel.generatedData.containsKey("Baz"));
        customModel.generatedData.clear();
        customModel.updateSelection(asSet("Bar"), asSet("Foo"));
        customGrid.getDataCommunicator().beforeClientResponse(false);
        Assert.assertFalse("Item should have been updated as NOT selected", customModel.generatedData.get("Foo"));
        Assert.assertTrue("Item should have been updated as selected", customModel.generatedData.get("Bar"));
        Assert.assertFalse("Item should have NOT been updated", customModel.generatedData.containsKey("Baz"));
        // switch to single to cause event
        customModel.generatedData.clear();
        customGrid.setSelectionMode(SINGLE);
        customGrid.getDataCommunicator().beforeClientResponse(false);
        // changing selection model should trigger row updates, but the old
        // selection model is not triggered as it has been removed
        Assert.assertTrue(customModel.generatedData.isEmpty());// not triggered

    }

    @Test
    public void select_gridWithStrings() {
        Grid<String> gridWithStrings = new Grid();
        gridWithStrings.setSelectionMode(MULTI);
        gridWithStrings.setItems("Foo", "Bar", "Baz");
        GridSelectionModel<String> model = gridWithStrings.getSelectionModel();
        Assert.assertFalse(model.isSelected("Foo"));
        model.select("Foo");
        Assert.assertTrue(model.isSelected("Foo"));
        Assert.assertEquals(Optional.of("Foo"), model.getFirstSelectedItem());
        model.select("Bar");
        Assert.assertTrue(model.isSelected("Foo"));
        Assert.assertTrue(model.isSelected("Bar"));
        Assert.assertEquals(Arrays.asList("Foo", "Bar"), new ArrayList(model.getSelectedItems()));
        model.deselect("Bar");
        Assert.assertFalse(model.isSelected("Bar"));
        Assert.assertTrue(model.getFirstSelectedItem().isPresent());
        Assert.assertEquals(Arrays.asList("Foo"), new ArrayList(model.getSelectedItems()));
    }

    @Test
    public void select() {
        selectionModel.select(GridMultiSelectionModelTest.PERSON_B);
        Assert.assertEquals(GridMultiSelectionModelTest.PERSON_B, selectionModel.getFirstSelectedItem().orElse(null));
        Assert.assertEquals(Optional.of(GridMultiSelectionModelTest.PERSON_B), selectionModel.getFirstSelectedItem());
        Assert.assertFalse(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_A));
        Assert.assertTrue(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_B));
        Assert.assertFalse(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_C));
        Assert.assertEquals(Arrays.asList(GridMultiSelectionModelTest.PERSON_B), currentSelectionCapture.getValue());
        selectionModel.select(GridMultiSelectionModelTest.PERSON_A);
        Assert.assertEquals(GridMultiSelectionModelTest.PERSON_B, selectionModel.getFirstSelectedItem().orElse(null));
        Assert.assertTrue(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_A));
        Assert.assertTrue(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_B));
        Assert.assertFalse(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_C));
        Assert.assertEquals(Arrays.asList(GridMultiSelectionModelTest.PERSON_B, GridMultiSelectionModelTest.PERSON_A), currentSelectionCapture.getValue());
        Assert.assertEquals(2, events.get());
    }

    @Test
    public void deselect() {
        selectionModel.select(GridMultiSelectionModelTest.PERSON_B);
        selectionModel.deselect(GridMultiSelectionModelTest.PERSON_B);
        Assert.assertFalse(selectionModel.getFirstSelectedItem().isPresent());
        Assert.assertFalse(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_A));
        Assert.assertFalse(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_B));
        Assert.assertFalse(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_C));
        Assert.assertEquals(Arrays.asList(), currentSelectionCapture.getValue());
        Assert.assertEquals(2, events.get());
    }

    @Test
    public void selectItems() {
        selectionModel.selectItems(GridMultiSelectionModelTest.PERSON_C, GridMultiSelectionModelTest.PERSON_B);
        Assert.assertEquals(GridMultiSelectionModelTest.PERSON_C, selectionModel.getFirstSelectedItem().orElse(null));
        Assert.assertEquals(Optional.of(GridMultiSelectionModelTest.PERSON_C), selectionModel.getFirstSelectedItem());
        Assert.assertFalse(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_A));
        Assert.assertTrue(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_B));
        Assert.assertTrue(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_C));
        Assert.assertEquals(Arrays.asList(GridMultiSelectionModelTest.PERSON_C, GridMultiSelectionModelTest.PERSON_B), currentSelectionCapture.getValue());
        selectionModel.selectItems(GridMultiSelectionModelTest.PERSON_A, GridMultiSelectionModelTest.PERSON_C);// partly NOOP

        Assert.assertTrue(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_A));
        Assert.assertTrue(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_B));
        Assert.assertTrue(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_C));
        Assert.assertEquals(Arrays.asList(GridMultiSelectionModelTest.PERSON_C, GridMultiSelectionModelTest.PERSON_B, GridMultiSelectionModelTest.PERSON_A), currentSelectionCapture.getValue());
        Assert.assertEquals(2, events.get());
    }

    @Test
    public void deselectItems() {
        selectionModel.selectItems(GridMultiSelectionModelTest.PERSON_C, GridMultiSelectionModelTest.PERSON_A, GridMultiSelectionModelTest.PERSON_B);
        selectionModel.deselectItems(GridMultiSelectionModelTest.PERSON_A);
        Assert.assertEquals(GridMultiSelectionModelTest.PERSON_C, selectionModel.getFirstSelectedItem().orElse(null));
        Assert.assertEquals(Optional.of(GridMultiSelectionModelTest.PERSON_C), selectionModel.getFirstSelectedItem());
        Assert.assertFalse(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_A));
        Assert.assertTrue(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_B));
        Assert.assertTrue(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_C));
        Assert.assertEquals(Arrays.asList(GridMultiSelectionModelTest.PERSON_C, GridMultiSelectionModelTest.PERSON_B), currentSelectionCapture.getValue());
        selectionModel.deselectItems(GridMultiSelectionModelTest.PERSON_A, GridMultiSelectionModelTest.PERSON_B, GridMultiSelectionModelTest.PERSON_C);
        Assert.assertNull(selectionModel.getFirstSelectedItem().orElse(null));
        Assert.assertEquals(Optional.empty(), selectionModel.getFirstSelectedItem());
        Assert.assertFalse(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_A));
        Assert.assertFalse(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_B));
        Assert.assertFalse(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_C));
        Assert.assertEquals(Arrays.asList(), currentSelectionCapture.getValue());
        Assert.assertEquals(3, events.get());
    }

    @Test
    public void selectionEvent_newSelection_oldSelection() {
        selectionModel.selectItems(GridMultiSelectionModelTest.PERSON_C, GridMultiSelectionModelTest.PERSON_A, GridMultiSelectionModelTest.PERSON_B);
        Assert.assertEquals(Arrays.asList(GridMultiSelectionModelTest.PERSON_C, GridMultiSelectionModelTest.PERSON_A, GridMultiSelectionModelTest.PERSON_B), currentSelectionCapture.getValue());
        Assert.assertEquals(Arrays.asList(), oldSelectionCapture.getValue());
        selectionModel.deselect(GridMultiSelectionModelTest.PERSON_A);
        Assert.assertEquals(Arrays.asList(GridMultiSelectionModelTest.PERSON_C, GridMultiSelectionModelTest.PERSON_B), currentSelectionCapture.getValue());
        Assert.assertEquals(Arrays.asList(GridMultiSelectionModelTest.PERSON_C, GridMultiSelectionModelTest.PERSON_A, GridMultiSelectionModelTest.PERSON_B), oldSelectionCapture.getValue());
        selectionModel.deselectItems(GridMultiSelectionModelTest.PERSON_A, GridMultiSelectionModelTest.PERSON_B, GridMultiSelectionModelTest.PERSON_C);
        Assert.assertEquals(Arrays.asList(), currentSelectionCapture.getValue());
        Assert.assertEquals(Arrays.asList(GridMultiSelectionModelTest.PERSON_C, GridMultiSelectionModelTest.PERSON_B), oldSelectionCapture.getValue());
        selectionModel.selectItems(GridMultiSelectionModelTest.PERSON_A);
        Assert.assertEquals(Arrays.asList(GridMultiSelectionModelTest.PERSON_A), currentSelectionCapture.getValue());
        Assert.assertEquals(Arrays.asList(), oldSelectionCapture.getValue());
        selectionModel.updateSelection(new LinkedHashSet(Arrays.asList(GridMultiSelectionModelTest.PERSON_B, GridMultiSelectionModelTest.PERSON_C)), new LinkedHashSet(Arrays.asList(GridMultiSelectionModelTest.PERSON_A)));
        Assert.assertEquals(Arrays.asList(GridMultiSelectionModelTest.PERSON_B, GridMultiSelectionModelTest.PERSON_C), currentSelectionCapture.getValue());
        Assert.assertEquals(Arrays.asList(GridMultiSelectionModelTest.PERSON_A), oldSelectionCapture.getValue());
        selectionModel.deselectAll();
        Assert.assertEquals(Arrays.asList(), currentSelectionCapture.getValue());
        Assert.assertEquals(Arrays.asList(GridMultiSelectionModelTest.PERSON_B, GridMultiSelectionModelTest.PERSON_C), oldSelectionCapture.getValue());
        selectionModel.select(GridMultiSelectionModelTest.PERSON_C);
        Assert.assertEquals(Arrays.asList(GridMultiSelectionModelTest.PERSON_C), currentSelectionCapture.getValue());
        Assert.assertEquals(Arrays.asList(), oldSelectionCapture.getValue());
        selectionModel.deselect(GridMultiSelectionModelTest.PERSON_C);
        Assert.assertEquals(Arrays.asList(), currentSelectionCapture.getValue());
        Assert.assertEquals(Arrays.asList(GridMultiSelectionModelTest.PERSON_C), oldSelectionCapture.getValue());
    }

    @Test
    public void deselectAll() {
        selectionModel.selectItems(GridMultiSelectionModelTest.PERSON_A, GridMultiSelectionModelTest.PERSON_C, GridMultiSelectionModelTest.PERSON_B);
        Assert.assertTrue(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_A));
        Assert.assertTrue(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_B));
        Assert.assertTrue(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_C));
        Assert.assertEquals(Arrays.asList(GridMultiSelectionModelTest.PERSON_A, GridMultiSelectionModelTest.PERSON_C, GridMultiSelectionModelTest.PERSON_B), currentSelectionCapture.getValue());
        Assert.assertEquals(1, events.get());
        selectionModel.deselectAll();
        Assert.assertFalse(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_A));
        Assert.assertFalse(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_B));
        Assert.assertFalse(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_C));
        Assert.assertEquals(Arrays.asList(), currentSelectionCapture.getValue());
        Assert.assertEquals(Arrays.asList(GridMultiSelectionModelTest.PERSON_A, GridMultiSelectionModelTest.PERSON_C, GridMultiSelectionModelTest.PERSON_B), oldSelectionCapture.getValue());
        Assert.assertEquals(2, events.get());
        selectionModel.select(GridMultiSelectionModelTest.PERSON_C);
        Assert.assertFalse(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_A));
        Assert.assertFalse(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_B));
        Assert.assertTrue(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_C));
        Assert.assertEquals(Arrays.asList(GridMultiSelectionModelTest.PERSON_C), currentSelectionCapture.getValue());
        Assert.assertEquals(Arrays.asList(), oldSelectionCapture.getValue());
        Assert.assertEquals(3, events.get());
        selectionModel.deselectAll();
        Assert.assertFalse(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_A));
        Assert.assertFalse(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_B));
        Assert.assertFalse(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_C));
        Assert.assertEquals(Arrays.asList(), currentSelectionCapture.getValue());
        Assert.assertEquals(Arrays.asList(GridMultiSelectionModelTest.PERSON_C), oldSelectionCapture.getValue());
        Assert.assertEquals(4, events.get());
        selectionModel.deselectAll();
        Assert.assertEquals(4, events.get());
    }

    @Test
    public void selectAll() {
        selectionModel.selectAll();
        Assert.assertTrue(selectionModel.isAllSelected());
        Assert.assertTrue(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_A));
        Assert.assertTrue(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_B));
        Assert.assertTrue(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_C));
        Assert.assertEquals(Arrays.asList(GridMultiSelectionModelTest.PERSON_A, GridMultiSelectionModelTest.PERSON_B, GridMultiSelectionModelTest.PERSON_C), currentSelectionCapture.getValue());
        Assert.assertEquals(1, events.get());
        selectionModel.deselectItems(GridMultiSelectionModelTest.PERSON_A, GridMultiSelectionModelTest.PERSON_C);
        Assert.assertFalse(selectionModel.isAllSelected());
        Assert.assertFalse(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_A));
        Assert.assertTrue(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_B));
        Assert.assertFalse(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_C));
        Assert.assertEquals(Arrays.asList(GridMultiSelectionModelTest.PERSON_A, GridMultiSelectionModelTest.PERSON_B, GridMultiSelectionModelTest.PERSON_C), oldSelectionCapture.getValue());
        selectionModel.selectAll();
        Assert.assertTrue(selectionModel.isAllSelected());
        Assert.assertTrue(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_A));
        Assert.assertTrue(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_B));
        Assert.assertTrue(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_C));
        Assert.assertEquals(Arrays.asList(GridMultiSelectionModelTest.PERSON_B, GridMultiSelectionModelTest.PERSON_A, GridMultiSelectionModelTest.PERSON_C), currentSelectionCapture.getValue());
        Assert.assertEquals(Arrays.asList(GridMultiSelectionModelTest.PERSON_B), oldSelectionCapture.getValue());
        Assert.assertEquals(3, events.get());
    }

    @Test
    public void updateSelection() {
        selectionModel.updateSelection(asSet(GridMultiSelectionModelTest.PERSON_A), Collections.emptySet());
        Assert.assertTrue(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_A));
        Assert.assertFalse(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_B));
        Assert.assertFalse(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_C));
        Assert.assertEquals(Arrays.asList(GridMultiSelectionModelTest.PERSON_A), currentSelectionCapture.getValue());
        Assert.assertEquals(1, events.get());
        selectionModel.updateSelection(asSet(GridMultiSelectionModelTest.PERSON_B), asSet(GridMultiSelectionModelTest.PERSON_A));
        Assert.assertFalse(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_A));
        Assert.assertTrue(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_B));
        Assert.assertFalse(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_C));
        Assert.assertEquals(Arrays.asList(GridMultiSelectionModelTest.PERSON_B), currentSelectionCapture.getValue());
        Assert.assertEquals(Arrays.asList(GridMultiSelectionModelTest.PERSON_A), oldSelectionCapture.getValue());
        Assert.assertEquals(2, events.get());
        selectionModel.updateSelection(asSet(GridMultiSelectionModelTest.PERSON_B), asSet(GridMultiSelectionModelTest.PERSON_A));// NOOP

        Assert.assertFalse(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_A));
        Assert.assertTrue(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_B));
        Assert.assertFalse(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_C));
        Assert.assertEquals(Arrays.asList(GridMultiSelectionModelTest.PERSON_B), currentSelectionCapture.getValue());
        Assert.assertEquals(Arrays.asList(GridMultiSelectionModelTest.PERSON_A), oldSelectionCapture.getValue());
        Assert.assertEquals(2, events.get());
        selectionModel.updateSelection(asSet(GridMultiSelectionModelTest.PERSON_A, GridMultiSelectionModelTest.PERSON_C), asSet(GridMultiSelectionModelTest.PERSON_A));// partly NOOP

        Assert.assertFalse(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_A));
        Assert.assertTrue(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_B));
        Assert.assertTrue(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_C));
        Assert.assertEquals(Arrays.asList(GridMultiSelectionModelTest.PERSON_B, GridMultiSelectionModelTest.PERSON_C), currentSelectionCapture.getValue());
        Assert.assertEquals(Arrays.asList(GridMultiSelectionModelTest.PERSON_B), oldSelectionCapture.getValue());
        Assert.assertEquals(3, events.get());
        selectionModel.updateSelection(asSet(GridMultiSelectionModelTest.PERSON_B, GridMultiSelectionModelTest.PERSON_A), asSet(GridMultiSelectionModelTest.PERSON_B));// partly NOOP

        Assert.assertTrue(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_A));
        Assert.assertTrue(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_B));
        Assert.assertTrue(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_C));
        Assert.assertEquals(Arrays.asList(GridMultiSelectionModelTest.PERSON_B, GridMultiSelectionModelTest.PERSON_C, GridMultiSelectionModelTest.PERSON_A), currentSelectionCapture.getValue());
        Assert.assertEquals(Arrays.asList(GridMultiSelectionModelTest.PERSON_B, GridMultiSelectionModelTest.PERSON_C), oldSelectionCapture.getValue());
        Assert.assertEquals(4, events.get());
        selectionModel.updateSelection(asSet(), asSet(GridMultiSelectionModelTest.PERSON_B, GridMultiSelectionModelTest.PERSON_A, GridMultiSelectionModelTest.PERSON_C));
        Assert.assertFalse(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_A));
        Assert.assertFalse(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_B));
        Assert.assertFalse(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_C));
        Assert.assertEquals(Arrays.asList(), currentSelectionCapture.getValue());
        Assert.assertEquals(Arrays.asList(GridMultiSelectionModelTest.PERSON_B, GridMultiSelectionModelTest.PERSON_C, GridMultiSelectionModelTest.PERSON_A), oldSelectionCapture.getValue());
        Assert.assertEquals(5, events.get());
    }

    @Test
    public void selectTwice() {
        selectionModel.select(GridMultiSelectionModelTest.PERSON_C);
        selectionModel.select(GridMultiSelectionModelTest.PERSON_C);
        Assert.assertEquals(GridMultiSelectionModelTest.PERSON_C, selectionModel.getFirstSelectedItem().orElse(null));
        Assert.assertFalse(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_A));
        Assert.assertFalse(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_B));
        Assert.assertTrue(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_C));
        Assert.assertEquals(Optional.of(GridMultiSelectionModelTest.PERSON_C), selectionModel.getFirstSelectedItem());
        Assert.assertEquals(Arrays.asList(GridMultiSelectionModelTest.PERSON_C), currentSelectionCapture.getValue());
        Assert.assertEquals(1, events.get());
    }

    @Test
    public void deselectTwice() {
        selectionModel.select(GridMultiSelectionModelTest.PERSON_C);
        Assert.assertEquals(Arrays.asList(GridMultiSelectionModelTest.PERSON_C), currentSelectionCapture.getValue());
        Assert.assertEquals(1, events.get());
        selectionModel.deselect(GridMultiSelectionModelTest.PERSON_C);
        Assert.assertFalse(selectionModel.getFirstSelectedItem().isPresent());
        Assert.assertFalse(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_C));
        Assert.assertEquals(Arrays.asList(), currentSelectionCapture.getValue());
        Assert.assertEquals(2, events.get());
        selectionModel.deselect(GridMultiSelectionModelTest.PERSON_C);
        Assert.assertFalse(selectionModel.getFirstSelectedItem().isPresent());
        Assert.assertFalse(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_A));
        Assert.assertFalse(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_B));
        Assert.assertFalse(selectionModel.isSelected(GridMultiSelectionModelTest.PERSON_C));
        Assert.assertEquals(Arrays.asList(), currentSelectionCapture.getValue());
        Assert.assertEquals(2, events.get());
    }

    @SuppressWarnings({ "serial" })
    @Test
    public void addValueChangeListener() {
        String value = "foo";
        AtomicReference<MultiSelectionListener<String>> selectionListener = new AtomicReference<>();
        Registration registration = Mockito.mock(Registration.class);
        MultiSelectionModelImpl<String> model = new MultiSelectionModelImpl<String>() {
            @Override
            public Registration addMultiSelectionListener(MultiSelectionListener<String> listener) {
                selectionListener.set(listener);
                return registration;
            }

            @Override
            public Set<String> getSelectedItems() {
                return new LinkedHashSet<>(Arrays.asList(value));
            }
        };
        Grid<String> grid = new GridMultiSelectionModelTest.CustomSelectionModelGrid(model);
        grid.setItems("foo", "bar");
        AtomicReference<MultiSelectionEvent<String>> event = new AtomicReference<>();
        Registration actualRegistration = model.addMultiSelectionListener(( evt) -> {
            assertNull(event.get());
            event.set(evt);
        });
        Assert.assertSame(registration, actualRegistration);
        selectionListener.get().selectionChange(new MultiSelectionEvent(grid, model.asMultiSelect(), Collections.emptySet(), true));
        Assert.assertEquals(grid, event.get().getComponent());
        Assert.assertEquals(new LinkedHashSet(Arrays.asList(value)), event.get().getValue());
        Assert.assertTrue(event.get().isUserOriginated());
    }

    @Test
    public void selectAllCheckboxVisible__inMemoryDataProvider() {
        UI ui = new MockUI();
        Grid<String> grid = new Grid();
        MultiSelectionModel<String> model = ((MultiSelectionModel<String>) (grid.setSelectionMode(MULTI)));
        ui.setContent(grid);
        // no items yet, default data provider is empty not in memory one
        Assert.assertFalse(model.isSelectAllCheckBoxVisible());
        Assert.assertEquals(DEFAULT, model.getSelectAllCheckBoxVisibility());
        grid.setItems("Foo", "Bar", "Baz");
        // in-memory container keeps default
        Assert.assertTrue(model.isSelectAllCheckBoxVisible());
        Assert.assertEquals(DEFAULT, model.getSelectAllCheckBoxVisibility());
        // change to explicit NO
        model.setSelectAllCheckBoxVisibility(HIDDEN);
        Assert.assertEquals(HIDDEN, model.getSelectAllCheckBoxVisibility());
        Assert.assertFalse(model.isSelectAllCheckBoxVisible());
        // change to explicit YES
        model.setSelectAllCheckBoxVisibility(VISIBLE);
        Assert.assertEquals(VISIBLE, model.getSelectAllCheckBoxVisibility());
        Assert.assertTrue(model.isSelectAllCheckBoxVisible());
    }

    @Test
    public void selectAllCheckboxVisible__lazyDataProvider() {
        Grid<String> grid = new Grid();
        UI ui = new MockUI();
        ui.setContent(grid);
        MultiSelectionModel<String> model = ((MultiSelectionModel<String>) (grid.setSelectionMode(MULTI)));
        // no items yet, default data provider is empty not in memory one
        Assert.assertFalse(model.isSelectAllCheckBoxVisible());
        Assert.assertEquals(DEFAULT, model.getSelectAllCheckBoxVisibility());
        grid.setDataProvider(DataProvider.fromCallbacks(( query) -> IntStream.range(query.getOffset(), Math.max((((query.getOffset()) + (query.getLimit())) + 1), 1000)).mapToObj(( i) -> "Item " + i), ( query) -> 1000));
        // not in-memory -> checkbox is hidden
        Assert.assertFalse(model.isSelectAllCheckBoxVisible());
        Assert.assertEquals(DEFAULT, model.getSelectAllCheckBoxVisibility());
        // change to explicit YES
        model.setSelectAllCheckBoxVisibility(VISIBLE);
        Assert.assertEquals(VISIBLE, model.getSelectAllCheckBoxVisibility());
        Assert.assertTrue(model.isSelectAllCheckBoxVisible());
        // change to explicit NO
        model.setSelectAllCheckBoxVisibility(HIDDEN);
        Assert.assertEquals(HIDDEN, model.getSelectAllCheckBoxVisibility());
        Assert.assertFalse(model.isSelectAllCheckBoxVisible());
        // change back to depends on data provider
        model.setSelectAllCheckBoxVisibility(DEFAULT);
        Assert.assertFalse(model.isSelectAllCheckBoxVisible());
        Assert.assertEquals(DEFAULT, model.getSelectAllCheckBoxVisibility());
    }
}

