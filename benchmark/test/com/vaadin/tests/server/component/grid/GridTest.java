package com.vaadin.tests.server.component.grid;


import HeightMode.CSS;
import HeightMode.ROW;
import SelectionMode.MULTI;
import SelectionMode.NONE;
import SortDirection.ASCENDING;
import SortDirection.DESCENDING;
import com.vaadin.data.Binder.Binding;
import com.vaadin.data.ValidationException;
import com.vaadin.data.ValueProvider;
import com.vaadin.data.provider.DataCommunicator;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.GridSortOrder;
import com.vaadin.data.provider.bov.Person;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.grid.GridState;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.TextField;
import com.vaadin.ui.renderers.NumberRenderer;
import elemental.json.JsonObject;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.easymock.Capture;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class GridTest {
    private Grid<String> grid;

    private Column<String, String> fooColumn;

    private Column<String, Integer> lengthColumn;

    private Column<String, Object> objectColumn;

    private Column<String, String> randomColumn;

    private GridState state;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testGridAssistiveCaption() {
        Assert.assertEquals(null, fooColumn.getAssistiveCaption());
        fooColumn.setAssistiveCaption("Press Enter to sort.");
        Assert.assertEquals("Press Enter to sort.", fooColumn.getAssistiveCaption());
    }

    @Test
    public void testCreateGridWithDataCommunicator() {
        DataCommunicator<String> specificDataCommunicator = new DataCommunicator();
        TestGrid<String> grid = new TestGrid(String.class, specificDataCommunicator);
        Assert.assertEquals(specificDataCommunicator, grid.getDataCommunicator());
    }

    @Test
    public void testGridHeightModeChange() {
        Assert.assertEquals("Initial height mode was not CSS", CSS, grid.getHeightMode());
        grid.setHeightByRows(13.24);
        Assert.assertEquals("Setting height by rows did not change height mode", ROW, grid.getHeightMode());
        grid.setHeight("100px");
        Assert.assertEquals("Setting height did not change height mode.", CSS, grid.getHeightMode());
    }

    @Test
    public void testFrozenColumnCountTooBig() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("count must be between -1 and the current number of columns (4): 5");
        grid.setFrozenColumnCount(5);
    }

    @Test
    public void testFrozenColumnCountTooSmall() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("count must be between -1 and the current number of columns (4): -2");
        grid.setFrozenColumnCount((-2));
    }

    @Test
    public void testSetFrozenColumnCount() {
        for (int i = -1; i < 2; ++i) {
            grid.setFrozenColumnCount(i);
            Assert.assertEquals("Frozen column count not updated", i, grid.getFrozenColumnCount());
        }
    }

    @Test
    public void testGridColumnIdentifier() {
        grid.getColumn("foo").setCaption("Bar");
        Assert.assertEquals("Column header not updated correctly", "Bar", grid.getHeaderRow(0).getCell("foo").getText());
    }

    @Test
    public void testGridMultipleColumnsWithSameIdentifier() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Duplicate ID for columns");
        grid.addColumn(( t) -> t).setId("foo");
    }

    @Test
    public void testAddSelectionListener_singleSelectMode() {
        grid.setItems("foo", "bar", "baz");
        Capture<SelectionEvent<String>> eventCapture = new Capture();
        grid.addSelectionListener(( event) -> eventCapture.setValue(event));
        grid.getSelectionModel().select("foo");
        SelectionEvent<String> event = eventCapture.getValue();
        Assert.assertNotNull(event);
        Assert.assertFalse(event.isUserOriginated());
        Assert.assertEquals("foo", event.getFirstSelectedItem().get());
        Assert.assertEquals("foo", event.getAllSelectedItems().stream().findFirst().get());
        grid.getSelectionModel().select("bar");
        event = eventCapture.getValue();
        Assert.assertNotNull(event);
        Assert.assertFalse(event.isUserOriginated());
        Assert.assertEquals("bar", event.getFirstSelectedItem().get());
        Assert.assertEquals("bar", event.getAllSelectedItems().stream().findFirst().get());
        grid.getSelectionModel().deselect("bar");
        event = eventCapture.getValue();
        Assert.assertNotNull(event);
        Assert.assertFalse(event.isUserOriginated());
        Assert.assertEquals(Optional.empty(), event.getFirstSelectedItem());
        Assert.assertEquals(0, event.getAllSelectedItems().size());
    }

    @Test
    public void testAddSelectionListener_multiSelectMode() {
        grid.setItems("foo", "bar", "baz");
        grid.setSelectionMode(MULTI);
        Capture<SelectionEvent<String>> eventCapture = new Capture();
        grid.addSelectionListener(( event) -> eventCapture.setValue(event));
        grid.getSelectionModel().select("foo");
        SelectionEvent<String> event = eventCapture.getValue();
        Assert.assertNotNull(event);
        Assert.assertFalse(event.isUserOriginated());
        Assert.assertEquals("foo", event.getFirstSelectedItem().get());
        Assert.assertEquals("foo", event.getAllSelectedItems().stream().findFirst().get());
        grid.getSelectionModel().select("bar");
        event = eventCapture.getValue();
        Assert.assertNotNull(event);
        Assert.assertFalse(event.isUserOriginated());
        Assert.assertEquals("foo", event.getFirstSelectedItem().get());
        Assert.assertEquals("foo", event.getAllSelectedItems().stream().findFirst().get());
        Assert.assertArrayEquals(new String[]{ "foo", "bar" }, event.getAllSelectedItems().toArray(new String[2]));
        grid.getSelectionModel().deselect("foo");
        event = eventCapture.getValue();
        Assert.assertNotNull(event);
        Assert.assertFalse(event.isUserOriginated());
        Assert.assertEquals("bar", event.getFirstSelectedItem().get());
        Assert.assertEquals("bar", event.getAllSelectedItems().stream().findFirst().get());
        Assert.assertArrayEquals(new String[]{ "bar" }, event.getAllSelectedItems().toArray(new String[1]));
        grid.getSelectionModel().deselectAll();
        event = eventCapture.getValue();
        Assert.assertNotNull(event);
        Assert.assertFalse(event.isUserOriginated());
        Assert.assertEquals(Optional.empty(), event.getFirstSelectedItem());
        Assert.assertEquals(0, event.getAllSelectedItems().size());
    }

    @Test
    public void testAddSelectionListener_noSelectionMode() {
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("This selection model doesn't allow selection, cannot add selection listeners to it");
        grid.setSelectionMode(NONE);
        grid.addSelectionListener(( event) -> fail("never ever happens (tm)"));
    }

    @Test
    public void sortByColumn_sortOrderIsAscendingOneColumn() {
        Column<String, ?> column = grid.getColumns().get(1);
        grid.sort(column);
        GridSortOrder<String> sortOrder = grid.getSortOrder().get(0);
        Assert.assertEquals(column, sortOrder.getSorted());
        Assert.assertEquals(ASCENDING, sortOrder.getDirection());
    }

    @Test
    public void sortByColumnDesc_sortOrderIsDescendingOneColumn() {
        Column<String, ?> column = grid.getColumns().get(1);
        grid.sort(column, DESCENDING);
        GridSortOrder<String> sortOrder = grid.getSortOrder().get(0);
        Assert.assertEquals(column, sortOrder.getSorted());
        Assert.assertEquals(DESCENDING, sortOrder.getDirection());
    }

    @Test
    public void setSortOrder() {
        Column<String, ?> column1 = grid.getColumns().get(1);
        Column<String, ?> column2 = grid.getColumns().get(2);
        List<GridSortOrder<String>> order = Arrays.asList(new GridSortOrder(column2, SortDirection.DESCENDING), new GridSortOrder(column1, SortDirection.ASCENDING));
        grid.setSortOrder(order);
        List<GridSortOrder<String>> sortOrder = grid.getSortOrder();
        Assert.assertEquals(column2, sortOrder.get(0).getSorted());
        Assert.assertEquals(DESCENDING, sortOrder.get(0).getDirection());
        Assert.assertEquals(column1, sortOrder.get(1).getSorted());
        Assert.assertEquals(ASCENDING, sortOrder.get(1).getDirection());
    }

    @Test
    public void clearSortOrder() throws Exception {
        Column<String, ?> column = grid.getColumns().get(1);
        grid.sort(column);
        grid.clearSortOrder();
        Assert.assertEquals(0, grid.getSortOrder().size());
        // Make sure state is updated.
        Assert.assertEquals(0, state.sortColumns.length);
        Assert.assertEquals(0, state.sortDirs.length);
    }

    @Test
    public void sortOrderDoesnotContainRemovedColumns() {
        Column<String, ?> sortColumn = grid.getColumns().get(1);
        grid.sort(sortColumn);
        // Get id of column and check it's sorted.
        String id = state.columnOrder.get(1);
        Assert.assertEquals(id, state.sortColumns[0]);
        // Remove column and make sure it's cleared correctly
        grid.removeColumn(sortColumn);
        Assert.assertFalse("Column not removed", state.columnOrder.contains(id));
        Assert.assertEquals(0, state.sortColumns.length);
        Assert.assertEquals(0, state.sortDirs.length);
    }

    @Test
    public void sortListener_eventIsFired() {
        Column<String, ?> column1 = grid.getColumns().get(1);
        Column<String, ?> column2 = grid.getColumns().get(2);
        List<GridSortOrder<String>> list = new ArrayList<>();
        AtomicReference<Boolean> fired = new AtomicReference<>();
        grid.addSortListener(( event) -> {
            assertTrue(list.isEmpty());
            fired.set(true);
            list.addAll(event.getSortOrder());
        });
        grid.sort(column1, DESCENDING);
        Assert.assertEquals(column1, list.get(0).getSorted());
        Assert.assertEquals(DESCENDING, list.get(0).getDirection());
        List<GridSortOrder<String>> order = Arrays.asList(new GridSortOrder(column2, SortDirection.DESCENDING), new GridSortOrder(column1, SortDirection.ASCENDING));
        list.clear();
        grid.setSortOrder(order);
        Assert.assertEquals(column2, list.get(0).getSorted());
        Assert.assertEquals(DESCENDING, list.get(0).getDirection());
        Assert.assertEquals(column1, list.get(1).getSorted());
        Assert.assertEquals(ASCENDING, list.get(1).getDirection());
        list.clear();
        fired.set(false);
        grid.clearSortOrder();
        Assert.assertEquals(0, list.size());
        Assert.assertTrue(fired.get());
    }

    @Test
    public void beanGrid() {
        Grid<Person> grid = new Grid(Person.class);
        Column<Person, ?> nameColumn = grid.getColumn("name");
        Column<Person, ?> bornColumn = grid.getColumn("born");
        Assert.assertNotNull(nameColumn);
        Assert.assertNotNull(bornColumn);
        Assert.assertEquals("Name", nameColumn.getCaption());
        Assert.assertEquals("Born", bornColumn.getCaption());
        JsonObject json = GridTest.getRowData(grid, new Person("Lorem", 2000));
        Set<String> values = Stream.of(json.keys()).map(json::getString).collect(Collectors.toSet());
        Assert.assertEquals(new HashSet<>(Arrays.asList("Lorem", "2000")), values);
        GridTest.assertSingleSortProperty(nameColumn, "name");
        GridTest.assertSingleSortProperty(bornColumn, "born");
    }

    @Test
    public void beanGrid_editor() throws ValidationException {
        Grid<Person> grid = new Grid(Person.class);
        Column<Person, ?> nameColumn = grid.getColumn("name");
        TextField nameField = new TextField();
        nameColumn.setEditorComponent(nameField);
        Optional<Binding<Person, ?>> maybeBinding = grid.getEditor().getBinder().getBinding("name");
        Assert.assertTrue(maybeBinding.isPresent());
        Binding<Person, ?> binding = maybeBinding.get();
        Assert.assertSame(nameField, binding.getField());
        Person person = new Person("Lorem", 2000);
        grid.getEditor().getBinder().setBean(person);
        Assert.assertEquals("Lorem", nameField.getValue());
        nameField.setValue("Ipsum");
        Assert.assertEquals("Ipsum", person.getName());
    }

    @Test
    public void oneArgSetEditor_nonBeanGrid() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(("A Grid created without a bean type class literal or a custom property set" + " doesn't support finding properties by name."));
        Grid<Person> grid = new Grid();
        Column<Person, String> nameCol = grid.addColumn(Person::getName).setId("name");
        nameCol.setEditorComponent(new TextField());
    }

    @Test
    public void addExistingColumnById_throws() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("There is already a column for name");
        Grid<Person> grid = new Grid(Person.class);
        grid.addColumn("name");
    }

    @Test
    public void removeByColumn_readdById() {
        Grid<Person> grid = new Grid(Person.class);
        grid.removeColumn(grid.getColumn("name"));
        grid.addColumn("name");
        List<Column<Person, ?>> columns = grid.getColumns();
        Assert.assertEquals(2, columns.size());
        Assert.assertEquals("born", columns.get(0).getId());
        Assert.assertEquals("name", columns.get(1).getId());
    }

    @Test
    public void removeColumnByColumn() {
        grid.removeColumn(fooColumn);
        Assert.assertEquals(Arrays.asList(lengthColumn, objectColumn, randomColumn), grid.getColumns());
    }

    @Test
    public void removeColumnByColumn_alreadyRemoved() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Column with id foo cannot be removed from the grid");
        grid.removeColumn(fooColumn);
        grid.removeColumn(fooColumn);
        Assert.assertEquals(Arrays.asList(lengthColumn, objectColumn, randomColumn), grid.getColumns());
    }

    @Test
    public void removeColumnById_alreadyRemoved() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("There is no column with the id foo");
        grid.removeColumn("foo");
        grid.removeColumn("foo");
    }

    @Test
    public void removeColumnById() {
        grid.removeColumn("foo");
        Assert.assertEquals(Arrays.asList(lengthColumn, objectColumn, randomColumn), grid.getColumns());
    }

    @Test
    public void removeAllColumns() {
        grid.removeAllColumns();
        Assert.assertEquals(Collections.emptyList(), grid.getColumns());
    }

    @Test
    public void removeAllColumnsInGridWithoutColumns() {
        grid.removeAllColumns();
        grid.removeAllColumns();
        Assert.assertEquals(Collections.emptyList(), grid.getColumns());
    }

    @Test
    public void removeFrozenColumn() {
        grid.setFrozenColumnCount(3);
        grid.removeColumn(fooColumn);
        Assert.assertEquals(2, grid.getFrozenColumnCount());
    }

    @Test
    public void removeHiddenFrozenColumn() {
        lengthColumn.setHidden(true);
        grid.setFrozenColumnCount(3);
        grid.removeColumn(lengthColumn);
        Assert.assertEquals(2, grid.getFrozenColumnCount());
    }

    @Test
    public void removeNonFrozenColumn() {
        grid.setFrozenColumnCount(3);
        grid.removeColumn(randomColumn);
        Assert.assertEquals(3, grid.getFrozenColumnCount());
    }

    @Test
    public void testFrozenColumnRemoveColumn() {
        Assert.assertEquals("Grid should not start with a frozen column", 0, grid.getFrozenColumnCount());
        int columnCount = grid.getColumns().size();
        grid.setFrozenColumnCount(columnCount);
        grid.removeColumn(grid.getColumns().get(0));
        Assert.assertEquals("Frozen column count should be updated when removing a frozen column", (columnCount - 1), grid.getFrozenColumnCount());
    }

    @Test
    public void setColumns_reorder() {
        // Will remove other columns
        grid.setColumns("length", "foo");
        List<Column<String, ?>> columns = grid.getColumns();
        Assert.assertEquals(2, columns.size());
        Assert.assertEquals("length", columns.get(0).getId());
        Assert.assertEquals("foo", columns.get(1).getId());
    }

    @Test
    public void setColumns_addColumn_notBeangrid() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(("A Grid created without a bean type class literal or a custom property set" + " doesn't support finding properties by name."));
        // Not possible to add a column in a grid that cannot add columns based
        // on a string
        grid.setColumns("notHere");
    }

    @Test
    public void setColumns_addColumns_beangrid() {
        Grid<Person> grid = new Grid(Person.class);
        // Remove so we can add it back
        grid.removeColumn("name");
        grid.setColumns("born", "name");
        List<Column<Person, ?>> columns = grid.getColumns();
        Assert.assertEquals(2, columns.size());
        Assert.assertEquals("born", columns.get(0).getId());
        Assert.assertEquals("name", columns.get(1).getId());
    }

    @Test
    public void setColumnOrder_byColumn() {
        grid.setColumnOrder(randomColumn, lengthColumn);
        Assert.assertEquals(Arrays.asList(randomColumn, lengthColumn, fooColumn, objectColumn), grid.getColumns());
    }

    @Test
    public void setColumnOrder_byColumn_removedColumn() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(("setColumnOrder should not be called " + "with columns that are not in the grid."));
        grid.removeColumn(randomColumn);
        grid.setColumnOrder(randomColumn, lengthColumn);
    }

    @Test
    public void setColumnOrder_byString() {
        grid.setColumnOrder("randomColumnId", "length");
        Assert.assertEquals(Arrays.asList(randomColumn, lengthColumn, fooColumn, objectColumn), grid.getColumns());
    }

    @Test
    public void setColumnOrder_byString_removedColumn() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("There is no column with the id randomColumnId");
        grid.removeColumn("randomColumnId");
        grid.setColumnOrder("randomColumnId", "length");
    }

    @Test
    public void defaultSorting_comparableTypes() {
        GridTest.testValueProviderSorting(1, 2, 3);
    }

    @Test
    public void defaultSorting_strings() {
        GridTest.testValueProviderSorting("a", "b", "c");
    }

    @Test
    public void defaultSorting_notComparable() {
        assert !(Comparable.class.isAssignableFrom(AtomicInteger.class));
        GridTest.testValueProviderSorting(new AtomicInteger(10), new AtomicInteger(8), new AtomicInteger(9));
    }

    @Test
    public void defaultSorting_differentComparables() {
        GridTest.testValueProviderSorting(10.1, 200, 3000.1, 4000);
    }

    @Test
    public void defaultSorting_mutuallyComparableTypes() {
        GridTest.testValueProviderSorting(new Date(10), new java.sql.Date(1000000), new Date(100000000));
    }

    @Test
    public void addBeanColumn_validRenderer() {
        Grid<Person> grid = new Grid(Person.class);
        grid.removeColumn("born");
        grid.addColumn("born", new NumberRenderer(new DecimalFormat("#,###", DecimalFormatSymbols.getInstance(Locale.US))));
        Person person = new Person("Name", 2017);
        JsonObject rowData = GridTest.getRowData(grid, person);
        String formattedValue = Stream.of(rowData.keys()).map(rowData::getString).filter(( value) -> !(value.equals("Name"))).findFirst().orElse(null);
        Assert.assertEquals(formattedValue, "2,017");
    }

    @Test
    public void addBeanColumn_invalidRenderer() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("NumberRenderer");
        thrown.expectMessage(" cannot be used with a property of type java.lang.String");
        Grid<Person> grid = new Grid(Person.class);
        grid.removeColumn("name");
        grid.addColumn("name", new NumberRenderer());
    }

    @Test
    public void columnId_sortProperty() {
        GridTest.assertSingleSortProperty(lengthColumn, "length");
    }

    @Test
    public void columnId_sortProperty_noId() {
        Assert.assertEquals(0, objectColumn.getSortOrder(ASCENDING).count());
    }

    @Test
    public void sortProperty_setId_doesntOverride() {
        objectColumn.setSortProperty("foo");
        objectColumn.setId("bar");
        GridTest.assertSingleSortProperty(objectColumn, "foo");
    }

    @Test
    public void removeColumnToThrowForInvalidColumn() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Column with id null cannot be removed from the grid");
        Grid<Person> grid1 = new Grid();
        Grid<Person> grid2 = new Grid();
        Column<Person, ?> column1 = grid1.addColumn(ValueProvider.identity());
        grid2.removeColumn(column1);
    }

    @Test
    public void testColumnSortable() {
        Column<String, String> column = grid.addColumn(String::toString);
        // Use in-memory data provider
        grid.setItems(Collections.emptyList());
        Assert.assertTrue("Column should be initially sortable", column.isSortable());
        Assert.assertTrue("User should be able to sort the column", column.isSortableByUser());
        column.setSortable(false);
        Assert.assertFalse("Column should not be sortable", column.isSortable());
        Assert.assertFalse("User should not be able to sort the column with in-memory data", column.isSortableByUser());
        // Use CallBackDataProvider
        grid.setDataProvider(DataProvider.fromCallbacks(( q) -> Stream.of(), ( q) -> 0));
        Assert.assertFalse("Column should not be sortable", column.isSortable());
        Assert.assertFalse("User should not be able to sort the column", column.isSortableByUser());
        column.setSortable(true);
        Assert.assertTrue("Column should be marked sortable", column.isSortable());
        Assert.assertFalse("User should not be able to sort the column since no sort order is provided", column.isSortableByUser());
        column.setSortProperty("toString");
        Assert.assertTrue("Column should be marked sortable", column.isSortable());
        Assert.assertTrue("User should be able to sort the column with the sort order", column.isSortableByUser());
    }
}

