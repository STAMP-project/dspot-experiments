package org.robobinding.binder;


import com.google.common.collect.Sets;
import java.util.Collection;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.robobinding.PendingAttributesForView;
import org.robobinding.ViewResolutionErrors;
import org.robobinding.viewattribute.ViewAttributeBinder;
import org.robobinding.viewattribute.event.EventViewAttributeBinder;
import org.robobinding.viewattribute.grouped.GroupedViewAttributeBinder;
import org.robobinding.viewattribute.property.MultiTypePropertyViewAttributeBinder;
import org.robobinding.viewattribute.property.PropertyViewAttributeBinder;


/**
 *
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Cheng Wei
 */
@RunWith(MockitoJUnitRunner.class)
public class ByBindingAttributeMappingsResolverTest {
    private PendingAttributesForView pendingAttributesForView;

    @Test
    public void givenAPropertyAttribute_whenResolve_thenAResolvedPropertyViewAttributeShouldBeReturned() {
        PropertyViewAttributeBinder viewAttributeBinder = Mockito.mock(PropertyViewAttributeBinder.class);
        ByBindingAttributeMappingsResolver byBindingAttributeMappingsResolver = newByBindingAttributeMappingsResolver(MockInitializedBindingAttributeMappingsBuilder.aBindingAttributeMappings().withPropertyAttribute("propertyAttribute", viewAttributeBinder).build());
        Collection<ViewAttributeBinder> viewAttributes = byBindingAttributeMappingsResolver.resolve(pendingAttributesForView);
        Assert.assertThat(Sets.newHashSet(viewAttributes), CoreMatchers.equalTo(Sets.<ViewAttributeBinder>newHashSet(viewAttributeBinder)));
    }

    @Test
    public void givenAMultiTypePropertyAttribute_whenResolve_thenAResolvedMultiTypePropertyViewAttributeShouldBeReturned() {
        MultiTypePropertyViewAttributeBinder viewAttributeBinder = Mockito.mock(MultiTypePropertyViewAttributeBinder.class);
        ByBindingAttributeMappingsResolver byBindingAttributeMappingsResolver = newByBindingAttributeMappingsResolver(MockInitializedBindingAttributeMappingsBuilder.aBindingAttributeMappings().withMultiTypePropertyAttribute("multiTypePropertyAttribute", viewAttributeBinder).build());
        Collection<ViewAttributeBinder> viewAttributes = byBindingAttributeMappingsResolver.resolve(pendingAttributesForView);
        Assert.assertThat(Sets.newHashSet(viewAttributes), CoreMatchers.equalTo(Sets.<ViewAttributeBinder>newHashSet(viewAttributeBinder)));
    }

    @Test
    public void givenAnEventAttribute_whenResolve_thenAResolvedEventViewAttributeShouldBeReturned() {
        EventViewAttributeBinder viewAttributeBinder = Mockito.mock(EventViewAttributeBinder.class);
        ByBindingAttributeMappingsResolver byBindingAttributeMappingsResolver = newByBindingAttributeMappingsResolver(MockInitializedBindingAttributeMappingsBuilder.aBindingAttributeMappings().withEventAttribute("eventAttribute", viewAttributeBinder).build());
        Collection<ViewAttributeBinder> viewAttributes = byBindingAttributeMappingsResolver.resolve(pendingAttributesForView);
        Assert.assertThat(Sets.newHashSet(viewAttributes), CoreMatchers.equalTo(Sets.<ViewAttributeBinder>newHashSet(viewAttributeBinder)));
    }

    @Test
    public void givenAnAttributeGroup_whenResolve_thenAResolvedGroupedViewAttributeShouldBeReturned() {
        String[] attributeGroup = new String[]{ "group_attribute1", "group_attribute2" };
        GroupedViewAttributeBinder viewAttributeBinder = Mockito.mock(GroupedViewAttributeBinder.class);
        ByBindingAttributeMappingsResolver byBindingAttributeMappingsResolver = newByBindingAttributeMappingsResolver(MockInitializedBindingAttributeMappingsBuilder.aBindingAttributeMappings().withAttributeGroup(attributeGroup, viewAttributeBinder).build());
        Collection<ViewAttributeBinder> viewAttributes = byBindingAttributeMappingsResolver.resolve(pendingAttributesForView);
        Assert.assertThat(Sets.newHashSet(viewAttributes), CoreMatchers.equalTo(Sets.<ViewAttributeBinder>newHashSet(viewAttributeBinder)));
    }

    private static class MockPendingAttributesForView implements PendingAttributesForView {
        @Override
        public void resolveAttributeGroupIfExists(String[] attributeGroup, AttributeGroupResolver attributeGroupResolver) {
            attributeGroupResolver.resolve(null, attributeGroup, null);
        }

        @Override
        public void resolveAttributeIfExists(String attribute, AttributeResolver attributeResolver) {
            attributeResolver.resolve(null, attribute, null);
        }

        @Override
        public Object getView() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ViewResolutionErrors getResolutionErrors() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isEmpty() {
            throw new UnsupportedOperationException();
        }
    }
}

