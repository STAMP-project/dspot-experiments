package org.robobinding.viewattribute.grouped;


import org.junit.Assert;
import org.junit.Test;
import org.robobinding.BindingContext;
import org.robobinding.viewattribute.ViewAttributeBinder;


/**
 *
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Robert Taylor
 * @author Cheng Wei
 */
public class FromClassViewAttributeFactoriesTest {
    @Test
    public void shouldCreateNewInstanceFromClass() {
        ViewAttributeBinder viewAttributeBinder = FromClassViewAttributeFactories.newViewAttribute(FromClassViewAttributeFactoriesTest.ViewAttributeBinderImpl.class);
        Assert.assertNotNull(viewAttributeBinder);
    }

    @Test(expected = RuntimeException.class)
    public void givenSuppliedClassIsAbstract_shouldThrowRuntimeException() {
        FromClassViewAttributeFactories.newViewAttribute(FromClassViewAttributeFactoriesTest.AbstractViewAttribute.class);
    }

    @Test(expected = RuntimeException.class)
    public void givenSuppliedClassIsNotVisible_shouldThrowRuntimeException() {
        FromClassViewAttributeFactories.newViewAttribute(FromClassViewAttributeFactoriesTest.NonVisibleViewAttribute.class);
    }

    public static class ViewAttributeBinderImpl implements ViewAttributeBinder {
        @Override
        public void bindTo(BindingContext bindingContext) {
        }

        @Override
        public void preInitializeView(BindingContext bindingContext) {
        }
    }

    public abstract static class AbstractViewAttribute implements ViewAttributeBinder {}

    private static class NonVisibleViewAttribute implements ViewAttributeBinder {
        @Override
        public void bindTo(BindingContext bindingContext) {
        }

        @Override
        public void preInitializeView(BindingContext bindingContext) {
        }
    }
}

