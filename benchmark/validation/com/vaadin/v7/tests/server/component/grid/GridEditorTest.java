package com.vaadin.v7.tests.server.component.grid;


import com.vaadin.server.VaadinSession;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.TextField;
import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;


public class GridEditorTest {
    private static final Object PROPERTY_NAME = "name";

    private static final Object PROPERTY_AGE = "age";

    private static final String DEFAULT_NAME = "Some Valid Name";

    private static final Integer DEFAULT_AGE = 25;

    private static final Object ITEM_ID = new Object();

    // Explicit field for the test session to save it from GC
    private VaadinSession session;

    private final Grid grid = new Grid();

    private Method doEditMethod;

    @Test
    public void testInitAssumptions() throws Exception {
        Assert.assertFalse(grid.isEditorEnabled());
        Assert.assertNull(grid.getEditedItemId());
        Assert.assertNotNull(grid.getEditorFieldGroup());
    }

    @Test
    public void testSetEnabled() throws Exception {
        Assert.assertFalse(grid.isEditorEnabled());
        grid.setEditorEnabled(true);
        Assert.assertTrue(grid.isEditorEnabled());
    }

    @Test
    public void testSetDisabled() throws Exception {
        Assert.assertFalse(grid.isEditorEnabled());
        grid.setEditorEnabled(true);
        grid.setEditorEnabled(false);
        Assert.assertFalse(grid.isEditorEnabled());
    }

    @Test
    public void testSetReEnabled() throws Exception {
        Assert.assertFalse(grid.isEditorEnabled());
        grid.setEditorEnabled(true);
        grid.setEditorEnabled(false);
        grid.setEditorEnabled(true);
        Assert.assertTrue(grid.isEditorEnabled());
    }

    @Test
    public void testDetached() throws Exception {
        FieldGroup oldFieldGroup = grid.getEditorFieldGroup();
        grid.removeAllColumns();
        grid.setContainerDataSource(new IndexedContainer());
        Assert.assertFalse((oldFieldGroup == (grid.getEditorFieldGroup())));
    }

    @Test(expected = IllegalStateException.class)
    public void testDisabledEditItem() throws Exception {
        grid.editItem(GridEditorTest.ITEM_ID);
    }

    @Test
    public void testEditItem() throws Exception {
        startEdit();
        Assert.assertEquals(GridEditorTest.ITEM_ID, grid.getEditedItemId());
        Assert.assertEquals(getEditedItem(), grid.getEditorFieldGroup().getItemDataSource());
        Assert.assertEquals(GridEditorTest.DEFAULT_NAME, grid.getColumn(GridEditorTest.PROPERTY_NAME).getEditorField().getValue());
        Assert.assertEquals(String.valueOf(GridEditorTest.DEFAULT_AGE), grid.getColumn(GridEditorTest.PROPERTY_AGE).getEditorField().getValue());
    }

    @Test
    public void testSaveEditor() throws Exception {
        startEdit();
        TextField field = ((TextField) (grid.getColumn(GridEditorTest.PROPERTY_NAME).getEditorField()));
        field.setValue("New Name");
        Assert.assertEquals(GridEditorTest.DEFAULT_NAME, field.getPropertyDataSource().getValue());
        grid.saveEditor();
        Assert.assertTrue(grid.isEditorActive());
        Assert.assertFalse(field.isModified());
        Assert.assertEquals("New Name", field.getValue());
        Assert.assertEquals("New Name", getEditedProperty(GridEditorTest.PROPERTY_NAME).getValue());
    }

    @Test
    public void testSaveEditorCommitFail() throws Exception {
        startEdit();
        setValue("Invalid");
        try {
            // Manual fail instead of @Test(expected=...) to check it is
            // saveEditor that fails and not setValue
            grid.saveEditor();
            Assert.fail("CommitException expected when saving an invalid field value");
        } catch (CommitException e) {
            // expected
        }
    }

    @Test
    public void testCancelEditor() throws Exception {
        startEdit();
        TextField field = ((TextField) (grid.getColumn(GridEditorTest.PROPERTY_NAME).getEditorField()));
        field.setValue("New Name");
        Property<?> datasource = field.getPropertyDataSource();
        grid.cancelEditor();
        Assert.assertFalse(grid.isEditorActive());
        Assert.assertNull(grid.getEditedItemId());
        Assert.assertFalse(field.isModified());
        Assert.assertEquals("", field.getValue());
        Assert.assertEquals(GridEditorTest.DEFAULT_NAME, datasource.getValue());
        Assert.assertNull(field.getPropertyDataSource());
        Assert.assertNull(grid.getEditorFieldGroup().getItemDataSource());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonexistentEditItem() throws Exception {
        grid.setEditorEnabled(true);
        grid.editItem(new Object());
    }

    @Test
    public void testGetField() throws Exception {
        startEdit();
        Assert.assertNotNull(grid.getColumn(GridEditorTest.PROPERTY_NAME).getEditorField());
    }

    @Test
    public void testGetFieldWithoutItem() throws Exception {
        grid.setEditorEnabled(true);
        Assert.assertNotNull(grid.getColumn(GridEditorTest.PROPERTY_NAME).getEditorField());
    }

    @Test
    public void testCustomBinding() {
        TextField textField = new TextField();
        grid.getColumn(GridEditorTest.PROPERTY_NAME).setEditorField(textField);
        startEdit();
        Assert.assertSame(textField, grid.getColumn(GridEditorTest.PROPERTY_NAME).getEditorField());
    }

    @Test(expected = IllegalStateException.class)
    public void testDisableWhileEditing() {
        startEdit();
        grid.setEditorEnabled(false);
    }

    @Test
    public void testFieldIsNotReadonly() {
        startEdit();
        Field<?> field = grid.getColumn(GridEditorTest.PROPERTY_NAME).getEditorField();
        Assert.assertFalse(field.isReadOnly());
    }

    @Test
    public void testFieldIsReadonlyWhenFieldGroupIsReadonly() {
        startEdit();
        grid.getEditorFieldGroup().setReadOnly(true);
        Field<?> field = grid.getColumn(GridEditorTest.PROPERTY_NAME).getEditorField();
        Assert.assertTrue(field.isReadOnly());
    }

    @Test
    public void testColumnRemoved() {
        Field<?> field = grid.getColumn(GridEditorTest.PROPERTY_NAME).getEditorField();
        Assert.assertSame("field should be attached to ", grid, field.getParent());
        grid.removeColumn(GridEditorTest.PROPERTY_NAME);
        Assert.assertNull("field should be detached from ", field.getParent());
    }

    @Test
    public void testSetFieldAgain() {
        TextField field = new TextField();
        grid.getColumn(GridEditorTest.PROPERTY_NAME).setEditorField(field);
        field = new TextField();
        grid.getColumn(GridEditorTest.PROPERTY_NAME).setEditorField(field);
        Assert.assertSame("new field should be used.", field, grid.getColumn(GridEditorTest.PROPERTY_NAME).getEditorField());
    }
}

