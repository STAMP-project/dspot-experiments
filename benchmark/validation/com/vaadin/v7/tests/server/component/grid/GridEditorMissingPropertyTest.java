package com.vaadin.v7.tests.server.component.grid;


import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.util.AbstractInMemoryContainer;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.PasswordField;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

import static com.vaadin.v7.ui.Grid.<init>;


public class GridEditorMissingPropertyTest {
    private static final String PROPERTY_NAME = "name";

    private static final String PROPERTY_SIZE = "size";

    private static final String FOLDER_NAME_BEFORE = "Folder name";

    private static final String FOLDER_NAME_AFTER = "Modified folder name";

    private static final String FILE_NAME_BEFORE = "File name";

    private static final String FILE_NAME_AFTER = "Modified file name";

    private static final String FILE_SIZE_BEFORE = "10kB";

    private static final String FILE_SIZE_AFTER = "20MB";

    private final GridEditorMissingPropertyTest.Grid grid = new GridEditorMissingPropertyTest.Grid();

    // Test items
    private final GridEditorMissingPropertyTest.Folder folder = new GridEditorMissingPropertyTest.Folder(GridEditorMissingPropertyTest.FOLDER_NAME_BEFORE);

    private final GridEditorMissingPropertyTest.File file = new GridEditorMissingPropertyTest.File(GridEditorMissingPropertyTest.FILE_NAME_BEFORE, GridEditorMissingPropertyTest.FILE_SIZE_BEFORE);

    @Test
    public void testBindFields() {
        FieldGroup fieldGroup = getEditorFieldGroup();
        // Item with incomplete property set
        fieldGroup.setItemDataSource(getContainerDataSource().getItem(folder));
        // called in grid.doEditItem
        getColumn(GridEditorMissingPropertyTest.PROPERTY_NAME).getEditorField();
        Assert.assertTrue("Properties in item should be bound", fieldGroup.getBoundPropertyIds().contains(GridEditorMissingPropertyTest.PROPERTY_NAME));
        Assert.assertFalse("Properties not present in item should not be bound", fieldGroup.getBoundPropertyIds().contains(GridEditorMissingPropertyTest.PROPERTY_SIZE));
        Assert.assertTrue("All of item's properties should be bound", fieldGroup.getUnboundPropertyIds().isEmpty());
        // Unbind all fields
        fieldGroup.setItemDataSource(null);
        Assert.assertTrue("No properties should be bound", fieldGroup.getBoundPropertyIds().isEmpty());
        Assert.assertTrue("No unbound properties should exist", fieldGroup.getUnboundPropertyIds().isEmpty());
        // Item with complete property set
        fieldGroup.setItemDataSource(getContainerDataSource().getItem(file));
        getColumn(GridEditorMissingPropertyTest.PROPERTY_NAME).getEditorField();
        getColumn(GridEditorMissingPropertyTest.PROPERTY_SIZE).getEditorField();
        Assert.assertTrue("Properties in item should be bound", fieldGroup.getBoundPropertyIds().contains(GridEditorMissingPropertyTest.PROPERTY_NAME));
        Assert.assertTrue("Properties in item should be bound", fieldGroup.getBoundPropertyIds().contains(GridEditorMissingPropertyTest.PROPERTY_SIZE));
        Assert.assertTrue("All of item's properties should be bound", fieldGroup.getUnboundPropertyIds().isEmpty());
        // Unbind all fields
        fieldGroup.setItemDataSource(null);
        Assert.assertTrue("No properties should be bound", fieldGroup.getBoundPropertyIds().isEmpty());
        Assert.assertTrue("No unbound properties should exist", fieldGroup.getUnboundPropertyIds().isEmpty());
    }

    @Test
    public void testSetEditorField() {
        FieldGroup fieldGroup = getEditorFieldGroup();
        Field editorField = new PasswordField();
        // Explicitly set editor field
        fieldGroup.setItemDataSource(getContainerDataSource().getItem(folder));
        getColumn(GridEditorMissingPropertyTest.PROPERTY_NAME).setEditorField(editorField);
        Assert.assertTrue("Editor field should be the one that was previously set", ((getColumn(GridEditorMissingPropertyTest.PROPERTY_NAME).getEditorField()) == editorField));
        // Reset item
        fieldGroup.setItemDataSource(null);
        fieldGroup.setItemDataSource(getContainerDataSource().getItem(file));
        Assert.assertTrue("Editor field should be the one that was previously set", ((getColumn(GridEditorMissingPropertyTest.PROPERTY_NAME).getEditorField()) == editorField));
    }

    @Test
    public void testEditCell() {
        // Row with missing property
        startEdit(folder);
        Assert.assertEquals(folder, getEditedItemId());
        Assert.assertEquals(getEditedItem(), grid.getEditorFieldGroup().getItemDataSource());
        Assert.assertEquals(GridEditorMissingPropertyTest.FOLDER_NAME_BEFORE, getColumn(GridEditorMissingPropertyTest.PROPERTY_NAME).getEditorField().getValue());
        try {
            getColumn(GridEditorMissingPropertyTest.PROPERTY_SIZE).getEditorField();
            Assert.fail("Grid.editorFieldGroup should throw BindException by default");
        } catch (FieldGroup e) {
            // BindException is thrown using the default FieldGroup
        }
        cancelEditor();
        // Row with all properties
        startEdit(file);
        Assert.assertEquals(file, getEditedItemId());
        Assert.assertEquals(getEditedItem(), grid.getEditorFieldGroup().getItemDataSource());
        Assert.assertEquals(GridEditorMissingPropertyTest.FILE_NAME_BEFORE, getColumn(GridEditorMissingPropertyTest.PROPERTY_NAME).getEditorField().getValue());
        Assert.assertEquals(GridEditorMissingPropertyTest.FILE_SIZE_BEFORE, getColumn(GridEditorMissingPropertyTest.PROPERTY_SIZE).getEditorField().getValue());
        cancelEditor();
    }

    @Test
    public void testCancelEditor() {
        // Row with all properties
        testCancel(file, GridEditorMissingPropertyTest.PROPERTY_NAME, GridEditorMissingPropertyTest.FILE_NAME_BEFORE, GridEditorMissingPropertyTest.FILE_NAME_AFTER);
        testCancel(file, GridEditorMissingPropertyTest.PROPERTY_SIZE, GridEditorMissingPropertyTest.FILE_SIZE_BEFORE, GridEditorMissingPropertyTest.FILE_SIZE_AFTER);
        // Row with missing property
        testCancel(folder, GridEditorMissingPropertyTest.PROPERTY_NAME, GridEditorMissingPropertyTest.FOLDER_NAME_BEFORE, GridEditorMissingPropertyTest.FOLDER_NAME_AFTER);
    }

    @Test
    public void testSaveEditor() throws Exception {
        // Row with all properties
        testSave(file, GridEditorMissingPropertyTest.PROPERTY_SIZE, GridEditorMissingPropertyTest.FILE_SIZE_BEFORE, GridEditorMissingPropertyTest.FILE_SIZE_AFTER);
        // Row with missing property
        testSave(folder, GridEditorMissingPropertyTest.PROPERTY_NAME, GridEditorMissingPropertyTest.FOLDER_NAME_BEFORE, GridEditorMissingPropertyTest.FOLDER_NAME_AFTER);
    }

    private class TestContainer extends AbstractInMemoryContainer<Object, String, BeanItem> {
        private final List<BeanItem<GridEditorMissingPropertyTest.Entry>> items;

        private final List<String> pids;

        public TestContainer(List<BeanItem<GridEditorMissingPropertyTest.Entry>> items, List<String> pids) {
            this.items = items;
            this.pids = pids;
        }

        @Override
        protected List<Object> getAllItemIds() {
            List<Object> ids = new ArrayList<Object>();
            for (BeanItem<GridEditorMissingPropertyTest.Entry> item : items) {
                ids.add(item.getBean());
            }
            return ids;
        }

        @Override
        protected BeanItem<GridEditorMissingPropertyTest.Entry> getUnfilteredItem(Object itemId) {
            for (BeanItem<GridEditorMissingPropertyTest.Entry> item : items) {
                if (item.getBean().equals(itemId)) {
                    return item;
                }
            }
            return null;
        }

        @Override
        public Collection<?> getContainerPropertyIds() {
            return pids;
        }

        @Override
        public Property getContainerProperty(Object itemId, Object propertyId) {
            return getItem(itemId).getItemProperty(propertyId);
        }

        @Override
        public Class<?> getType(Object propertyId) {
            return String.class;
        }
    }

    public class Entry {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Entry(String name) {
            this.name = name;
        }
    }

    public class Folder extends GridEditorMissingPropertyTest.Entry {
        public Folder(String name) {
            super(name);
        }
    }

    public class File extends GridEditorMissingPropertyTest.Entry {
        private String size;

        public File(String name, String size) {
            super(name);
            this.size = size;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }
    }

    private class Grid extends com.vaadin.v7.ui.Grid {
        @Override
        protected void doEditItem() {
            super.doEditItem();
        }
    }
}

