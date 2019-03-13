package org.robobinding;


import android.view.View;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robobinding.PendingAttributesForView.AttributeGroupResolver;
import org.robobinding.PendingAttributesForView.AttributeResolver;
import org.robobinding.attribute.MalformedAttributeException;


/**
 *
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Cheng Wei
 */
public class PendingAttributesForViewImplTest {
    private PendingAttributesForViewImplTest.AttributeResolverImpl attributeResolver;

    @Test
    public void givenAttributeInPendingList_whenResolveAttributeIfExists_thenAttributeShouldBeResolved() {
        String attribute = "existingAttribute";
        PendingAttributesForView pendingAttributesForView = createWithPendingList(attribute);
        pendingAttributesForView.resolveAttributeIfExists(attribute, attributeResolver);
        Assert.assertTrue(attributeResolver.isAttributeResolved(attribute));
    }

    @Test
    public void givenAttributeNotInPendingList_whenResolveAttributeIfExists_thenAttributeShouldBeIgnored() {
        String attribute = "nonExistingAttribute";
        PendingAttributesForView pendingAttributesForView = createWithPendingList();
        pendingAttributesForView.resolveAttributeIfExists(attribute, attributeResolver);
        Assert.assertFalse(attributeResolver.isAttributeResolved(attribute));
    }

    @Test
    public void whenResolveAttributeSuccessfully_thenAttributeShouldBeRemovedOffPendingList() {
        String attribute = "existingAttribute";
        PendingAttributesForView pendingAttributesForView = createWithPendingList(attribute);
        pendingAttributesForView.resolveAttributeIfExists(attribute, Mockito.mock(AttributeResolver.class));
        Assert.assertTrue(pendingAttributesForView.isEmpty());
    }

    @Test
    public void whenResolveAttributeFailed_thenAttributeShouldBeRemovedOffPendingList() {
        String attribute = "existingAttribute";
        PendingAttributesForView pendingAttributesForView = createWithPendingList(attribute);
        AttributeResolver attributeResolver = Mockito.mock(AttributeResolver.class);
        Mockito.doThrow(new MalformedAttributeException(attribute, "error message")).when(attributeResolver).resolve(ArgumentMatchers.any(View.class), ArgumentMatchers.eq(attribute), ArgumentMatchers.anyString());
        pendingAttributesForView.resolveAttributeIfExists(attribute, attributeResolver);
        Assert.assertTrue(pendingAttributesForView.isEmpty());
    }

    @Test
    public void whenAttributeGroupResolvedSuccessfully_thenPresentAttributesShouldBeRemovedOffThePendingList() {
        String[] presentAttributes = new String[]{ "group_attribute1", "group_attribute2" };
        String[] attributeGroup = ArrayUtils.add(presentAttributes, "group_attribute3");
        PendingAttributesForView pendingAttributesForView = createWithPendingList(presentAttributes);
        pendingAttributesForView.resolveAttributeGroupIfExists(attributeGroup, Mockito.mock(AttributeGroupResolver.class));
        Assert.assertTrue(pendingAttributesForView.isEmpty());
    }

    private class AttributeResolverImpl implements AttributeResolver {
        private boolean resolved;

        private String resolvedAttribute;

        @Override
        public void resolve(Object view, String attribute, String attributeValue) {
            resolved = true;
            resolvedAttribute = attribute;
        }

        public boolean isAttributeResolved(String attribute) {
            if (resolved) {
                return StringUtils.equals(resolvedAttribute, attribute);
            } else {
                return false;
            }
        }
    }
}

