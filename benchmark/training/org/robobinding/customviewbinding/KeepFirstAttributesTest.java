package org.robobinding.customviewbinding;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.robobinding.viewattribute.property.OneWayPropertyViewAttribute;
import org.robobinding.viewattribute.property.OneWayPropertyViewAttributeFactory;
import org.robobinding.viewbinding.BindingAttributeMappings;


/**
 *
 *
 * @since 1.0
 * @author Cheng Wei
 */
@RunWith(MockitoJUnitRunner.class)
public class KeepFirstAttributesTest {
    private String attributeName = "attributeName";

    @Mock
    private BindingAttributeMappings<KeepFirstAttributesTest.ViewType> bindingAttributeMappings;

    private KeepFirstAttributes<KeepFirstAttributesTest.ViewType> keepFirstAttributes;

    @Test
    public void whenMapSameOneWayPropertyAgain_thenMappedOnceOnly() {
        Class<KeepFirstAttributesTest.OneWayPropertyViewAttribute1> viewAttributeClass = null;
        keepFirstAttributes.mapOneWayProperty(viewAttributeClass, attributeName);
        keepFirstAttributes.mapOneWayProperty(viewAttributeClass, attributeName);
        Mockito.verify(bindingAttributeMappings, Mockito.times(1)).mapOneWayProperty(viewAttributeClass, attributeName);
    }

    @Test
    public void whenMapSameOneWayPropertyByFactoryAgain_thenMappedOnceOnly() {
        Class<KeepFirstAttributesTest.OneWayPropertyViewAttribute1> viewAttributeClass = null;
        OneWayPropertyViewAttributeFactory<KeepFirstAttributesTest.ViewType> factory = null;
        keepFirstAttributes.mapOneWayProperty(viewAttributeClass, attributeName);
        keepFirstAttributes.mapOneWayProperty(factory, attributeName);
        Mockito.verify(bindingAttributeMappings, Mockito.times(1)).mapOneWayProperty(viewAttributeClass, attributeName);
    }

    public static interface ViewType {}

    public static interface OneWayPropertyViewAttribute1 extends OneWayPropertyViewAttribute<KeepFirstAttributesTest.ViewType, Object> {}
}

