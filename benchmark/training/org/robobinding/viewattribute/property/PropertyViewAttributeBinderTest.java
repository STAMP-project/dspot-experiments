package org.robobinding.viewattribute.property;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.robobinding.BindingContext;
import org.robobinding.viewattribute.ViewAttributeContractTest;


/**
 *
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Robert Taylor
 */
@RunWith(MockitoJUnitRunner.class)
public final class PropertyViewAttributeBinderTest extends ViewAttributeContractTest<PropertyViewAttributeBinder> {
    @Mock
    BindingContext bindingContext;

    @Mock
    AbstractBindingProperty bindingProperty;

    private PropertyViewAttributeBinder viewAttributeBinder;

    @Test
    public void givenAlwaysPreInitializingView_whenBindTo_thenPreInitializeTheViewToReflectTheValueModel() {
        Mockito.when(bindingProperty.isAlwaysPreInitializingView()).thenReturn(true);
        viewAttributeBinder = new PropertyViewAttributeBinder(bindingProperty, null);
        viewAttributeBinder.bindTo(bindingContext);
        Mockito.verify(bindingProperty).preInitializeView(bindingContext);
    }

    @Test
    public void givenAlwaysPreInitializingView_whenPreInitializeViewAfterBindTo_thenPreInitializingViewHappensOnceOnly() {
        Mockito.when(bindingProperty.isAlwaysPreInitializingView()).thenReturn(true);
        viewAttributeBinder = new PropertyViewAttributeBinder(bindingProperty, null);
        viewAttributeBinder.bindTo(bindingContext);
        viewAttributeBinder.preInitializeView(bindingContext);
        Mockito.verify(bindingProperty, Mockito.times(1)).preInitializeView(bindingContext);
    }
}

