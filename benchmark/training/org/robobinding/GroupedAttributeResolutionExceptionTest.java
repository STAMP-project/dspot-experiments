package org.robobinding;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Robert Taylor
 */
public class GroupedAttributeResolutionExceptionTest {
    @Test
    public void byDefaultShouldNotHaveErrors() {
        GroupedAttributeResolutionException exception = new GroupedAttributeResolutionException();
        exception.assertNoErrors();
        Assert.assertThat(exception.getAttributeResolutionExceptions(), Matchers.hasSize(0));
    }

    @Test
    public void givenErrorsHaveBeenAddedShouldThrowExceptionOnAssert() {
        GroupedAttributeResolutionException exception = new GroupedAttributeResolutionException();
        exception.add(new AttributeResolutionException("attribute"));
        try {
            exception.assertNoErrors();
            Assert.fail();
        } catch (GroupedAttributeResolutionException e) {
            Assert.assertThat(e.getAttributeResolutionExceptions(), Matchers.hasSize(1));
        }
    }
}

