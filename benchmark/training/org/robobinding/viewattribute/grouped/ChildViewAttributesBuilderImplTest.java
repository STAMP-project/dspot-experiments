package org.robobinding.viewattribute.grouped;


import android.view.View;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.robobinding.attribute.AbstractAttribute;
import org.robobinding.viewattribute.event.EventViewAttribute;
import org.robobinding.viewattribute.event.EventViewAttributeFactory;
import org.robobinding.viewattribute.property.OneWayMultiTypePropertyViewAttribute;
import org.robobinding.viewattribute.property.OneWayMultiTypePropertyViewAttributeFactory;
import org.robobinding.viewattribute.property.OneWayPropertyViewAttribute;
import org.robobinding.viewattribute.property.OneWayPropertyViewAttributeFactory;
import org.robobinding.widgetaddon.ViewAddOn;


/**
 *
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Cheng Wei
 */
@RunWith(MockitoJUnitRunner.class)
public class ChildViewAttributesBuilderImplTest {
    private static final int NUM_KINDS_OF_CHILD_VIEW_ATTRIBUTES = 8;

    private ChildViewAttributesBuilderImpl<View> childViewAttributesBuilder;

    @Mock
    private ChildViewAttribute childViewAttribute;

    @Mock
    private OneWayPropertyViewAttribute<View, ?> propertyViewAttribute;

    @Mock
    private ChildViewAttributeFactory childViewAttributeFactory;

    @Mock
    private OneWayPropertyViewAttributeFactory<View> propertyViewAttributeFactory;

    @Mock
    private OneWayMultiTypePropertyViewAttribute<View> multiTypePropertyViewAttribute;

    @Mock
    private OneWayMultiTypePropertyViewAttributeFactory<View> multiTypePropertyViewAttributeFactory;

    @Mock
    private EventViewAttribute<View, ViewAddOn> eventViewAttribute;

    @Mock
    private EventViewAttributeFactory<View> eventViewAttributeFactory;

    @Test
    public void whenAddAChildViewAttribute_thenTheAttributeIsAdded() {
        String attributeName = "attributeName";
        addAChildViewAttribute(attributeName);
        Assert.assertThat(numOfChildViewAttributes(), Matchers.is(1));
        Assert.assertTrue(hasChildViewAttribute(attributeName));
    }

    @Test
    public void whenAddDifferentChildViewAttributes_thenTheAllAttributesAreAdded() {
        String attribute1 = "attribute1";
        String attribute2 = "attribute2";
        String attribute3 = "attribute3";
        String attribute4 = "attribute4";
        String attribute5 = "attribute5";
        String attribute6 = "attribute6";
        String attribute7 = "attribute7";
        String attribute8 = "attribute8";
        childViewAttributesBuilder.add(attribute1, childViewAttribute);
        childViewAttributesBuilder.add(attribute2, childViewAttributeFactory);
        childViewAttributesBuilder.add(attribute3, propertyViewAttribute);
        childViewAttributesBuilder.add(attribute4, propertyViewAttributeFactory);
        childViewAttributesBuilder.add(attribute5, multiTypePropertyViewAttribute);
        childViewAttributesBuilder.add(attribute6, multiTypePropertyViewAttributeFactory);
        childViewAttributesBuilder.add(attribute7, eventViewAttribute);
        childViewAttributesBuilder.add(attribute8, eventViewAttributeFactory);
        Assert.assertThat(numOfChildViewAttributes(), Matchers.is(ChildViewAttributesBuilderImplTest.NUM_KINDS_OF_CHILD_VIEW_ATTRIBUTES));
        Assert.assertTrue(hasChildViewAttribute(attribute1));
        Assert.assertTrue(hasChildViewAttribute(attribute2));
        Assert.assertTrue(hasChildViewAttribute(attribute3));
        Assert.assertTrue(hasChildViewAttribute(attribute4));
        Assert.assertTrue(hasChildViewAttribute(attribute5));
        Assert.assertTrue(hasChildViewAttribute(attribute6));
        Assert.assertTrue(hasChildViewAttribute(attribute7));
        Assert.assertTrue(hasChildViewAttribute(attribute8));
    }

    @Test
    public void whenAddChildViewAttributeWithAttribute_thenTheAttributeIsSet() {
        @SuppressWarnings("unchecked")
        ChildViewAttributeWithAttribute<AbstractAttribute> childViewAttribute = Mockito.mock(ChildViewAttributeWithAttribute.class);
        childViewAttributesBuilder.add("attributeName", childViewAttribute);
        Mockito.verify(childViewAttribute).setAttribute(ArgumentMatchers.any(AbstractAttribute.class));
    }
}

