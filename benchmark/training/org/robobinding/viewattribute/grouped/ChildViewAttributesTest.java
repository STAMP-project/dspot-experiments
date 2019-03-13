package org.robobinding.viewattribute.grouped;


import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
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
@RunWith(MockitoJUnitRunner.class)
public class ChildViewAttributesTest {
    @Mock
    private BindingContext bindingContext;

    @Mock
    private ViewAttributeBinder viewAttribute1;

    @Mock
    private ViewAttributeBinder viewAttribute2;

    private String viewAttributeName1 = "viewAttribute1";

    private String viewAttributeName2 = "viewAttribute2";

    @Rule
    public ExpectedException thrownException = ExpectedException.none();

    @Test
    public void whenBindTo_thenTheMethodInEachChildViewAttributeIsCalled() {
        ChildViewAttributes childViewAttributes = childViewAttributes();
        childViewAttributes.bindTo(bindingContext);
        Mockito.verify(viewAttribute1).bindTo(bindingContext);
        Mockito.verify(viewAttribute2).bindTo(bindingContext);
    }

    @Test
    public void whenPreInitializeView_thenTheMethodOfChildPropertyViewAttributeIsCalled() {
        ChildViewAttributes childViewAttributes = childViewAttributes();
        childViewAttributes.preInitializeView(bindingContext);
        Mockito.verify(viewAttribute1).preInitializeView(bindingContext);
        Mockito.verify(viewAttribute2).preInitializeView(bindingContext);
    }

    @Test
    public void whenErrorsOccurDuringBindingWithReportAllErrors_thenAllErrorsAreReported() {
        throwAttributeBindingExceptionWhenBinding(viewAttribute1);
        throwAttributeBindingExceptionWhenBinding(viewAttribute2);
        thrownException.expect(AttributeGroupBindingException.class);
        thrownException.expect(ChildViewAttributesTest.hasChildAttributeError(viewAttributeName1));
        thrownException.expect(ChildViewAttributesTest.hasChildAttributeError(viewAttributeName2));
        ChildViewAttributes childViewAttributes = childViewAttributesWithReportAllErrors();
        childViewAttributes.bindTo(bindingContext);
    }

    @Test
    public void whenErrorsOccurDuringBindingWithFailOnFirstBindingError_thenOnlyTheFirstErrorIsReported() {
        throwAttributeBindingExceptionWhenBinding(viewAttribute1);
        throwAttributeBindingExceptionWhenBinding(viewAttribute2);
        thrownException.expect(AttributeGroupBindingException.class);
        thrownException.expect(ChildViewAttributesTest.hasChildAttributeError(viewAttributeName1));
        thrownException.expect(CoreMatchers.not(ChildViewAttributesTest.hasChildAttributeError(viewAttributeName2)));
        ChildViewAttributes childViewAttributes = childViewAttributesWithFailOnFirstError();
        childViewAttributes.bindTo(bindingContext);
    }

    @Test(expected = AttributeGroupBindingException.class)
    public void whenErrorsOccurDuringBindingWithFailOnFirstBindingError_thenOnlyTheMethodInTheFirstChildViewAttributeIsCalled() {
        throwAttributeBindingExceptionWhenBinding(viewAttribute1);
        ChildViewAttributes childViewAttributes = childViewAttributesWithFailOnFirstError();
        childViewAttributes.bindTo(bindingContext);
        Mockito.verify(viewAttribute1).bindTo(bindingContext);
        Mockito.verify(viewAttribute2, Mockito.never()).bindTo(bindingContext);
    }

    @Test(expected = AttributeGroupBindingException.class)
    public void whenErrorsOccurDuringBindingWithReportAllErrors_thenAllTheMethodInEachChildViewAttributeIsCalled() {
        throwAttributeBindingExceptionWhenBinding(viewAttribute1);
        ChildViewAttributes childViewAttributes = childViewAttributesWithReportAllErrors();
        childViewAttributes.bindTo(bindingContext);
        Mockito.verify(viewAttribute1).bindTo(bindingContext);
        Mockito.verify(viewAttribute2).bindTo(bindingContext);
    }

    @SuppressWarnings("serial")
    private static class ProgrammingError extends RuntimeException {}
}

