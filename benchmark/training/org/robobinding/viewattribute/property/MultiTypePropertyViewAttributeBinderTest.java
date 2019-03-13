package org.robobinding.viewattribute.property;


import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robobinding.BindingContext;
import org.robobinding.attribute.ValueModelAttribute;
import org.robobinding.viewattribute.AttributeBindingException;
import org.robobinding.viewattribute.ViewAttributeContractTest;


/**
 *
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Cheng Wei
 */
public class MultiTypePropertyViewAttributeBinderTest extends ViewAttributeContractTest<MultiTypePropertyViewAttributeBinder> {
    @Mock
    PropertyViewAttributeBinderProvider viewAttributeBinderProvider;

    @Mock
    ValueModelAttribute attribute;

    @Mock
    PropertyViewAttributeBinder viewAttributeBinder;

    @Mock
    BindingContext bindingContext;

    @Test
    public void givenAMatchingViewAttributeBinder_whenBind_thenTheViewAttributeBinderIsBoundToContext() {
        Mockito.when(viewAttributeBinderProvider.create(ArgumentMatchers.any(Class.class))).thenReturn(viewAttributeBinder);
        MultiTypePropertyViewAttributeBinder multiTypeViewAttributeBinder = new MultiTypePropertyViewAttributeBinder(viewAttributeBinderProvider, attribute);
        multiTypeViewAttributeBinder.bindTo(bindingContext);
        Mockito.verify(viewAttributeBinder).bindTo(bindingContext);
    }

    @Test(expected = AttributeBindingException.class)
    public void givenNoMatchingViewAttributeBinder_whenBind_thenExceptionIsThrown() {
        Mockito.when(viewAttributeBinderProvider.create(ArgumentMatchers.any(Class.class))).thenReturn(null);
        MultiTypePropertyViewAttributeBinder multiTypeViewAttributeBinder = new MultiTypePropertyViewAttributeBinder(viewAttributeBinderProvider, attribute);
        multiTypeViewAttributeBinder.bindTo(bindingContext);
    }
}

