package org.robobinding.attribute;


import org.junit.Test;


/**
 *
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Robert Taylor
 */
public class PendingGroupAttributesTest {
    private PendingGroupAttributes pendingGroupAttributes;

    private String[] attributeNames;

    @Test
    public void givenAllAttributesArePresent_whenAssertingAllAttributesArePresent_thenDoNothing() {
        allAttributesArePresent();
        pendingGroupAttributes.assertAttributesArePresent(attributeNames);
    }

    @Test(expected = MissingRequiredAttributesException.class)
    public void givenNoAttributesArePresent_whenAssertingAllAttributesArePresent_thenThrowException() {
        noAttributeIsPresent();
        pendingGroupAttributes.assertAttributesArePresent(attributeNames);
    }
}

