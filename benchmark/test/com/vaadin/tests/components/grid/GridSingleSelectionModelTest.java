package com.vaadin.tests.components.grid;


import SelectionMode.MULTI;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.provider.bov.Person;
import com.vaadin.event.selection.SingleSelectionListener;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.GridSelectionModel;
import com.vaadin.ui.components.grid.SingleSelectionModelImpl;
import elemental.json.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class GridSingleSelectionModelTest {
    public static final Person PERSON_C = new Person("c", 3);

    public static final Person PERSON_B = new Person("b", 2);

    public static final Person PERSON_A = new Person("a", 1);

    public static class CustomSingleSelectionModel extends SingleSelectionModelImpl<String> {
        public final Map<String, Boolean> generatedData = new LinkedHashMap<>();

        @Override
        public void generateData(String item, JsonObject jsonObject) {
            super.generateData(item, jsonObject);
            // capture updated row
            generatedData.put(item, isSelected(item));
        }
    }

    private static class TestSingleSelectionModel extends SingleSelectionModelImpl<Object> {
        public TestSingleSelectionModel() {
            getState(false).selectionAllowed = false;
        }

        @Override
        protected void setSelectedFromClient(String key) {
            super.setSelectedFromClient(key);
        }
    }

    private List<Person> selectionChanges;

    private Grid<Person> grid;

    private SingleSelectionModelImpl<Person> selectionModel;

    @Test(expected = IllegalStateException.class)
    public void throwExceptionWhenSelectionIsDisallowed() {
        GridSingleSelectionModelTest.TestSingleSelectionModel model = new GridSingleSelectionModelTest.TestSingleSelectionModel();
        model.setSelectedFromClient("foo");
    }

    @Test(expected = IllegalStateException.class)
    public void selectionModelChanged_usingPreviousSelectionModel_throws() {
        grid.setSelectionMode(MULTI);
        selectionModel.select(GridSingleSelectionModelTest.PERSON_A);
    }

    @Test
    public void gridChangingSelectionModel_firesSelectionChangeEvent() {
        Grid<String> customGrid = new Grid();
        customGrid.setItems("Foo", "Bar", "Baz");
        List<String> selectionChanges = new ArrayList<>();
        List<String> oldSelectionValues = new ArrayList<>();
        ((SingleSelectionModelImpl<String>) (customGrid.getSelectionModel())).addSingleSelectionListener(( event) -> {
            selectionChanges.add(event.getValue());
            oldSelectionValues.add(event.getOldValue());
        });
        customGrid.getSelectionModel().select("Foo");
        Assert.assertEquals("Foo", customGrid.getSelectionModel().getFirstSelectedItem().get());
        Assert.assertEquals(Arrays.asList("Foo"), selectionChanges);
        Assert.assertEquals(Arrays.asList(((String) (null))), oldSelectionValues);
        customGrid.setSelectionMode(MULTI);
        Assert.assertEquals(Arrays.asList("Foo", null), selectionChanges);
        Assert.assertEquals(Arrays.asList(null, "Foo"), oldSelectionValues);
    }

    @Test
    public void serverSideSelection_GridChangingSelectionModel_sendsUpdatedRowsToClient() {
        GridSingleSelectionModelTest.CustomSingleSelectionModel customModel = new GridSingleSelectionModelTest.CustomSingleSelectionModel();
        Grid<String> customGrid = new Grid<String>() {
            {
                setSelectionModel(customModel);
            }
        };
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
        // switch to another selection model to cause event
        customModel.generatedData.clear();
        customGrid.setSelectionMode(MULTI);
        customGrid.getDataCommunicator().beforeClientResponse(false);
        // since the selection model has been removed, it is no longer a data
        // generator for the data communicator, would need to verify somehow
        // that row is not marked as selected anymore ? (done in UI tests)
        // at least removed selection model is not triggered
        Assert.assertTrue(customModel.generatedData.isEmpty());
    }

    @Test
    public void testGridWithSingleSelection() {
        Grid<String> gridWithStrings = new Grid();
        gridWithStrings.setItems("Foo", "Bar", "Baz");
        GridSelectionModel<String> model = gridWithStrings.getSelectionModel();
        Assert.assertFalse(model.isSelected("Foo"));
        model.select("Foo");
        Assert.assertTrue(model.isSelected("Foo"));
        Assert.assertEquals(Optional.of("Foo"), model.getFirstSelectedItem());
        model.select("Bar");
        Assert.assertFalse(model.isSelected("Foo"));
        Assert.assertTrue(model.isSelected("Bar"));
        model.deselect("Bar");
        Assert.assertFalse(model.isSelected("Bar"));
        Assert.assertFalse(model.getFirstSelectedItem().isPresent());
    }

    @Test
    public void select_isSelected() {
        selectionModel.select(GridSingleSelectionModelTest.PERSON_B);
        Assert.assertTrue(selectionModel.getSelectedItem().isPresent());
        Assert.assertEquals(GridSingleSelectionModelTest.PERSON_B, selectionModel.getSelectedItem().orElse(null));
        Assert.assertFalse(selectionModel.isSelected(GridSingleSelectionModelTest.PERSON_A));
        Assert.assertTrue(selectionModel.isSelected(GridSingleSelectionModelTest.PERSON_B));
        Assert.assertFalse(selectionModel.isSelected(GridSingleSelectionModelTest.PERSON_C));
        Assert.assertEquals(Optional.of(GridSingleSelectionModelTest.PERSON_B), selectionModel.getSelectedItem());
        Assert.assertEquals(Arrays.asList(GridSingleSelectionModelTest.PERSON_B), selectionChanges);
    }

    @Test
    public void selectDeselect() {
        selectionModel.select(GridSingleSelectionModelTest.PERSON_B);
        selectionModel.deselect(GridSingleSelectionModelTest.PERSON_B);
        Assert.assertFalse(selectionModel.getSelectedItem().isPresent());
        Assert.assertFalse(selectionModel.isSelected(GridSingleSelectionModelTest.PERSON_A));
        Assert.assertFalse(selectionModel.isSelected(GridSingleSelectionModelTest.PERSON_B));
        Assert.assertFalse(selectionModel.isSelected(GridSingleSelectionModelTest.PERSON_C));
        Assert.assertFalse(selectionModel.getSelectedItem().isPresent());
        Assert.assertEquals(Arrays.asList(GridSingleSelectionModelTest.PERSON_B, null), selectionChanges);
    }

    @Test
    public void reselect() {
        selectionModel.select(GridSingleSelectionModelTest.PERSON_B);
        selectionModel.select(GridSingleSelectionModelTest.PERSON_C);
        Assert.assertEquals(GridSingleSelectionModelTest.PERSON_C, selectionModel.getSelectedItem().orElse(null));
        Assert.assertFalse(selectionModel.isSelected(GridSingleSelectionModelTest.PERSON_A));
        Assert.assertFalse(selectionModel.isSelected(GridSingleSelectionModelTest.PERSON_B));
        Assert.assertTrue(selectionModel.isSelected(GridSingleSelectionModelTest.PERSON_C));
        Assert.assertEquals(Optional.of(GridSingleSelectionModelTest.PERSON_C), selectionModel.getSelectedItem());
        Assert.assertEquals(Arrays.asList(GridSingleSelectionModelTest.PERSON_B, GridSingleSelectionModelTest.PERSON_C), selectionChanges);
    }

    @Test
    public void selectTwice() {
        selectionModel.select(GridSingleSelectionModelTest.PERSON_C);
        selectionModel.select(GridSingleSelectionModelTest.PERSON_C);
        Assert.assertEquals(GridSingleSelectionModelTest.PERSON_C, selectionModel.getSelectedItem().orElse(null));
        Assert.assertFalse(selectionModel.isSelected(GridSingleSelectionModelTest.PERSON_A));
        Assert.assertFalse(selectionModel.isSelected(GridSingleSelectionModelTest.PERSON_B));
        Assert.assertTrue(selectionModel.isSelected(GridSingleSelectionModelTest.PERSON_C));
        Assert.assertEquals(Optional.of(GridSingleSelectionModelTest.PERSON_C), selectionModel.getSelectedItem());
        Assert.assertEquals(Arrays.asList(GridSingleSelectionModelTest.PERSON_C), selectionChanges);
    }

    @Test
    public void deselectTwice() {
        selectionModel.select(GridSingleSelectionModelTest.PERSON_C);
        selectionModel.deselect(GridSingleSelectionModelTest.PERSON_C);
        selectionModel.deselect(GridSingleSelectionModelTest.PERSON_C);
        Assert.assertFalse(selectionModel.getSelectedItem().isPresent());
        Assert.assertFalse(selectionModel.isSelected(GridSingleSelectionModelTest.PERSON_A));
        Assert.assertFalse(selectionModel.isSelected(GridSingleSelectionModelTest.PERSON_B));
        Assert.assertFalse(selectionModel.isSelected(GridSingleSelectionModelTest.PERSON_C));
        Assert.assertFalse(selectionModel.getSelectedItem().isPresent());
        Assert.assertEquals(Arrays.asList(GridSingleSelectionModelTest.PERSON_C, null), selectionChanges);
    }

    @Test
    public void getSelectedItem() {
        selectionModel.setSelectedItem(GridSingleSelectionModelTest.PERSON_B);
        Assert.assertEquals(GridSingleSelectionModelTest.PERSON_B, selectionModel.getSelectedItem().get());
        selectionModel.deselect(GridSingleSelectionModelTest.PERSON_B);
        Assert.assertFalse(selectionModel.getSelectedItem().isPresent());
    }

    @Test
    public void select_deselect_getSelectedItem() {
        selectionModel.select(GridSingleSelectionModelTest.PERSON_C);
        Assert.assertEquals(GridSingleSelectionModelTest.PERSON_C, selectionModel.getSelectedItem().get());
        selectionModel.deselect(GridSingleSelectionModelTest.PERSON_C);
        Assert.assertFalse(selectionModel.getSelectedItem().isPresent());
    }

    @SuppressWarnings({ "serial" })
    @Test
    public void addValueChangeListener() {
        AtomicReference<SingleSelectionListener<String>> selectionListener = new AtomicReference<>();
        Registration registration = Mockito.mock(Registration.class);
        Grid<String> grid = new Grid();
        grid.setItems("foo", "bar");
        String value = "foo";
        SingleSelectionModelImpl<String> select = new SingleSelectionModelImpl<String>() {
            @Override
            public Registration addSingleSelectionListener(SingleSelectionListener<String> listener) {
                selectionListener.set(listener);
                return registration;
            }

            @Override
            public Optional<String> getSelectedItem() {
                return Optional.of(value);
            }
        };
        AtomicReference<ValueChangeEvent<?>> event = new AtomicReference<>();
        Registration actualRegistration = select.addSingleSelectionListener(( evt) -> {
            assertNull(event.get());
            event.set(evt);
        });
        Assert.assertSame(registration, actualRegistration);
        selectionListener.get().selectionChange(new com.vaadin.event.selection.SingleSelectionEvent(grid, select.asSingleSelect(), null, true));
        Assert.assertEquals(grid, event.get().getComponent());
        Assert.assertEquals(value, event.get().getValue());
        Assert.assertEquals(null, event.get().getOldValue());
        Assert.assertTrue(event.get().isUserOriginated());
    }
}

