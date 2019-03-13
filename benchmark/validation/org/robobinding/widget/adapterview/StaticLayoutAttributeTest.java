package org.robobinding.widget.adapterview;


import android.content.Context;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.robobinding.BindingContext;
import org.robobinding.attribute.StaticResourceAttribute;
import org.robobinding.util.RandomValues;


/**
 *
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Robert Taylor
 */
@RunWith(MockitoJUnitRunner.class)
public class StaticLayoutAttributeTest {
    @Mock
    StaticResourceAttribute staticResourceAttribute;

    @Mock
    RowLayoutUpdater rowLayoutUpdater;

    @InjectMocks
    StaticLayoutAttribute staticLayoutAttribute;

    @Mock
    Context context;

    @Mock
    BindingContext bindingContext;

    @Test
    public void whenBinding_thenSetRowLayout() {
        int resourceId = RandomValues.anyInteger();
        Mockito.when(bindingContext.getContext()).thenReturn(context);
        Mockito.when(staticResourceAttribute.getResourceId(context)).thenReturn(resourceId);
        staticLayoutAttribute.bindTo(bindingContext);
        Mockito.verify(rowLayoutUpdater).updateRowLayout(resourceId);
    }
}

