package com.vaadin.v7.tests.server.component.abstractfield;


import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.ui.AbstractField;
import org.easymock.EasyMock;
import org.junit.Test;


/**
 * Base class for tests for checking that value change listeners for fields are
 * not called exactly once when they should be, and not at other times.
 *
 * Does not check all cases (e.g. properties that do not implement
 * {@link ValueChangeNotifier}).
 *
 * Subclasses should implement {@link #setValue()} and call
 * <code>super.setValue(LegacyAbstractField)</code>. Also, subclasses should
 * typically override {@link #setValue(AbstractField)} to set the field value
 * via <code>changeVariables()</code>.
 */
public abstract class AbstractFieldValueChangeTestBase<T> {
    private AbstractField<T> field;

    private ValueChangeListener listener;

    /**
     * Test that listeners are not called when they have been unregistered.
     */
    @Test
    public void testRemoveListener() {
        getField().setPropertyDataSource(new com.vaadin.v7.data.util.ObjectProperty<String>(""));
        getField().setBuffered(false);
        // Expectations and start test
        listener.valueChange(EasyMock.isA(ValueChangeEvent.class));
        EasyMock.replay(listener);
        // Add listener and set the value -> should end up in listener once
        getField().addListener(listener);
        setValue(getField());
        // Ensure listener was called once
        EasyMock.verify(listener);
        // Remove the listener and set the value -> should not end up in
        // listener
        getField().removeListener(listener);
        setValue(getField());
        // Ensure listener still has been called only once
        EasyMock.verify(listener);
    }

    /**
     * Common unbuffered case: both writeThrough (auto-commit) and readThrough
     * are on. Calling commit() should not cause notifications.
     *
     * Using the readThrough mode allows changes made to the property value to
     * be seen in some cases also when there is no notification of value change
     * from the property.
     *
     * LegacyField value change notifications closely mirror value changes of
     * the data source behind the field.
     */
    @Test
    public void testNonBuffered() {
        getField().setPropertyDataSource(new com.vaadin.v7.data.util.ObjectProperty<String>(""));
        getField().setBuffered(false);
        expectValueChangeFromSetValueNotCommit();
    }
}

