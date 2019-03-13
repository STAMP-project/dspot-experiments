package com.vaadin.tests.server.component.grid;


import com.vaadin.data.Binder;
import com.vaadin.data.PropertyDefinition;
import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.shared.ui.grid.editor.EditorServerRpc;
import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.EditorCancelEvent;
import com.vaadin.ui.components.grid.EditorCancelListener;
import com.vaadin.ui.components.grid.EditorImpl;
import com.vaadin.ui.components.grid.EditorSaveEvent;
import com.vaadin.ui.components.grid.EditorSaveListener;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Vaadin Ltd
 */
public class EditorImplTest {
    public static class TestEditorImpl extends EditorImpl<Object> implements EditorCancelListener<Object> , EditorSaveListener<Object> {
        @Override
        public void doEdit(Object bean) {
            super.doEdit(bean);
        }

        public TestEditorImpl() {
            super(new com.vaadin.data.PropertySet<Object>() {
                @Override
                public Stream<PropertyDefinition<Object, ?>> getProperties() {
                    return null;
                }

                @Override
                public Optional<PropertyDefinition<Object, ?>> getProperty(String name) {
                    return null;
                }
            });
        }

        @Override
        public Grid<Object> getParent() {
            return new Grid();
        }

        EditorCancelEvent<Object> cancelEvent;

        EditorSaveEvent<Object> saveEvent;

        EditorServerRpc rpc;

        @Override
        public boolean isBuffered() {
            return true;
        }

        @Override
        protected void refresh(Object item) {
        }

        @Override
        public void onEditorCancel(EditorCancelEvent<Object> event) {
            Assert.assertNull(cancelEvent);
            cancelEvent = event;
        }

        @Override
        public void onEditorSave(EditorSaveEvent<Object> event) {
            Assert.assertNull(saveEvent);
            saveEvent = event;
        }

        @Override
        protected <T extends ServerRpc> void registerRpc(T implementation) {
            if (implementation instanceof EditorServerRpc) {
                rpc = ((EditorServerRpc) (implementation));
            }
            super.registerRpc(implementation);
        }
    }

    private Object beanToEdit = new Object();

    private EditorImplTest.TestEditorImpl editor;

    private Binder<Object> binder;

    @Test
    public void save_eventIsFired() {
        Mockito.when(binder.writeBeanIfValid(Mockito.any())).thenReturn(true);
        save();
        Assert.assertNotNull(editor.saveEvent);
        Assert.assertNull(editor.cancelEvent);
        Assert.assertEquals(editor, editor.saveEvent.getSource());
        Assert.assertEquals(beanToEdit, editor.saveEvent.getBean());
    }

    @Test
    public void cancel_eventIsFired() {
        cancel();
        Assert.assertNull(editor.saveEvent);
        Assert.assertNotNull(editor.cancelEvent);
        Assert.assertEquals(editor, editor.cancelEvent.getSource());
        Assert.assertEquals(beanToEdit, editor.cancelEvent.getBean());
    }

    @Test
    public void saveFromClient_eventIsFired() {
        Mockito.when(binder.writeBeanIfValid(Mockito.any())).thenReturn(true);
        editor.rpc.save();
        Assert.assertNotNull(editor.saveEvent);
        Assert.assertNull(editor.cancelEvent);
        Assert.assertEquals(editor, editor.saveEvent.getSource());
        Assert.assertEquals(beanToEdit, editor.saveEvent.getBean());
    }

    @Test
    public void cancelFromClient_eventIsFired() {
        editor.rpc.cancel(false);
        Assert.assertNull(editor.saveEvent);
        Assert.assertNotNull(editor.cancelEvent);
        Assert.assertEquals(editor, editor.cancelEvent.getSource());
        Assert.assertEquals(beanToEdit, editor.cancelEvent.getBean());
    }

    @Test
    public void cancelAfterSaveFromClient_eventIsNotFired() {
        editor.rpc.cancel(true);
        Assert.assertNull(editor.saveEvent);
        Assert.assertNull(editor.cancelEvent);
    }
}

