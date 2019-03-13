package com.vaadin.v7.tests.server.component.grid;


import com.vaadin.server.ServerRpcManager.RpcInvocationException;
import com.vaadin.tests.util.MockUI;
import com.vaadin.ui.UI;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Grid;
import org.junit.Assert;
import org.junit.Test;


public class ItemSetChangeDuringEditorCommit {
    private static class IndexedContainerImpl extends IndexedContainer {
        public IndexedContainerImpl() {
        }

        @Override
        public void fireItemSetChange() {
            super.fireItemSetChange();
        }
    }

    @Test
    public void itemSetChangeDoesNotInterruptCommit() throws RpcInvocationException, CommitException {
        UI ui = new MockUI();
        final ItemSetChangeDuringEditorCommit.IndexedContainerImpl indexedContainer = new ItemSetChangeDuringEditorCommit.IndexedContainerImpl();
        addContainerProperty("firstName", String.class, "first");
        addContainerProperty("lastName", String.class, "last");
        addItem();
        addItem();
        Grid grid = new Grid();
        ui.setContent(grid);
        grid.setContainerDataSource(indexedContainer);
        grid.setEditorEnabled(true);
        grid.getEditorFieldGroup().addCommitHandler(new FieldGroup.CommitHandler() {
            @Override
            public void preCommit(FieldGroup.CommitEvent commitEvent) throws CommitException {
            }

            @Override
            public void postCommit(FieldGroup.CommitEvent commitEvent) throws CommitException {
                indexedContainer.fireItemSetChange();
            }
        });
        editItem(grid, 0);
        setValue("New first");
        setValue("New last");
        grid.saveEditor();
        Assert.assertEquals("New first", getValue());
        Assert.assertEquals("New last", getValue());
        grid.cancelEditor();
        Assert.assertFalse(grid.isEditorActive());
        editItem(grid, 0);
        Assert.assertEquals("New first", getValue());
        Assert.assertEquals("New last", getValue());
        saveEditor(grid, 0);
    }
}

