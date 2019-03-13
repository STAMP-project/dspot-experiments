package org.robobinding.attribute;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Cheng Wei
 */
public class ChildAttributeResolverMappingsTest {
    private static final String ATTRIBUTE = "attribute";

    private ChildAttributeResolverMappings mappings;

    @Test
    public void givenResolverForAttribute_whenAskResolverForAttribute_thenReturnExpectedResolver() {
        ChildAttributeResolver expectedResolver = givenResolverForAttribute();
        ChildAttributeResolver actualResolver = mappings.resolverFor(ChildAttributeResolverMappingsTest.ATTRIBUTE);
        Assert.assertThat(actualResolver, CoreMatchers.sameInstance(expectedResolver));
    }

    @Test(expected = RuntimeException.class)
    public void givenNoResolverForAttribute_whenAskResolverForAttribute_thenThrowException() {
        mappings.resolverFor(ChildAttributeResolverMappingsTest.ATTRIBUTE);
    }
}

