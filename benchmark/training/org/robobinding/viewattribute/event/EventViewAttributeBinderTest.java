package org.robobinding.viewattribute.event;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.robobinding.BindingContext;
import org.robobinding.attribute.Command;
import org.robobinding.attribute.EventAttribute;
import org.robobinding.viewattribute.ViewAttributeContractTest;
import org.robobinding.widgetaddon.ViewAddOn;


/**
 *
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Robert Taylor
 */
@RunWith(MockitoJUnitRunner.class)
public final class EventViewAttributeBinderTest extends ViewAttributeContractTest<EventViewAttributeBinder> {
    @Mock
    private BindingContext bindingContext;

    @Mock
    private Object view;

    @Mock
    private ViewAddOn viewAddOn;

    @Mock
    private EventViewAttribute<Object, ViewAddOn> viewAttribute;

    @Mock
    private EventAttribute attribute;

    private EventViewAttributeBinder viewAttributeBinder;

    @Test
    public void givenAMatchingCommandWithArgs_whenBinding_thenBindWithTheCommand() {
        Command commandWithArgs = Mockito.mock(Command.class);
        Mockito.<Class<?>>when(viewAttribute.getEventType()).thenReturn(EventViewAttributeBinderTest.EventType.class);
        Mockito.when(attribute.findCommand(bindingContext, EventViewAttributeBinderTest.EventType.class)).thenReturn(commandWithArgs);
        viewAttributeBinder.bindTo(bindingContext);
        Mockito.verify(viewAttribute).bind(viewAddOn, commandWithArgs, view);
    }

    @Test
    public void givenAMatchingCommandWithNoArgs_whenBinding_thenBindWithTheCommand() {
        Command commandWithNoArgs = Mockito.mock(Command.class);
        Mockito.when(attribute.findCommand(bindingContext)).thenReturn(commandWithNoArgs);
        viewAttributeBinder.bindTo(bindingContext);
        Mockito.verify(viewAttribute).bind(viewAddOn, commandWithNoArgs, view);
    }

    private static class ExceptionDuringBinding extends EventViewAttributeBinder {
        public ExceptionDuringBinding(EventAttribute attribute) {
            super(null, null, null, attribute);
        }

        @Override
        void performBind(BindingContext context) {
            throw new RuntimeException();
        }
    }

    public static class EventType {}
}

