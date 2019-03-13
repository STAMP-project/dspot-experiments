package org.robobinding.binder;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.robobinding.binder.ViewHierarchyInflationErrorsException.ErrorFormatter;


/**
 *
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Cheng Wei
 */
public class ErrorFormatterWithFirstErrorStackTraceTest {
    @Test
    public void whenFormatAGivenInflationError_thenReceivedAnExpectedMessage() {
        ErrorFormatter errorFormatter = new ErrorFormatterWithFirstErrorStackTrace();
        String message = errorFormatter.format(ViewInflationErrorsBuilder.aViewInflationErrors("CustomView").withAttributeResolutionErrorOf("text", "invalid syntax ${prop1").withMissingRequiredAttributesResolutionErrorOf("source", "dropdownLayout").withAttributeBindingErrorOf("visibility", "unmatch presentationModel.prop2 type").build());
        Assert.assertThat(message, Matchers.containsString("CustomView(3 errors)"));
        Assert.assertThat(message, Matchers.containsString("text: invalid syntax ${prop1"));
        Assert.assertThat(message, Matchers.containsString("Missing attributes: source, dropdownLayout"));
        Assert.assertThat(message, Matchers.containsString("visibility: unmatch presentationModel.prop2 type"));
        Assert.assertThat(message, Matchers.containsString("The first error stack trace"));
    }
}

